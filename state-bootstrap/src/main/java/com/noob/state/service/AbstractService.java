package com.noob.state.service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;

import com.google.common.base.Strings;
import com.noob.state.constants.Symbol;
import com.noob.state.entity.Meta;
import com.noob.state.entity.adapter.Adapter;
import com.noob.state.monitor.Monitor;
import com.noob.state.monitor.MonitorFactory.EventSource;
import com.noob.state.node.impl.MetaNode;
import com.noob.state.service.impl.LogService;
import com.noob.state.storage.BusinessStorage;
import com.noob.state.util.CommonUtil;
import com.noob.state.util.GsonUtil;
import com.noob.state.util.MonitorUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractService {

	protected final BusinessStorage storage;
	protected final LogService logService;
	protected final MetaNode metaNode;

	public AbstractService(LogService logService, MetaNode metaNode) {
		this.logService = logService;
		this.metaNode = metaNode;
		this.storage = logService.getStorage();
	}

	/**
	 * 更新节点数据
	 */
	public void updateNode(String path, String value) {
		storage.updateNode(path, false, value);
	}

	/**
	 * 更新节点数据
	 */
	public void updateNodeWithFullPath(String path, String value, LogService.LogInfo logInfo) {
		if (storage.updateNode(path, true, value)) updateLogData(path, logInfo);
	}

	/**
	 * 初始化节点. 若节点不存在则用指定数据创建新节点,当指定数据不为空，还需要创建日志节点
	 */
	public void initNode(String path, String data) {
		if (!storage.isNodeExisted(path)) {
			storage.createNodeIfNeeded(path, data, new BackgroundCallback() {

				@Override
				public void processResult(CuratorFramework client, CuratorEvent event)
						throws Exception {
					// 创建成功
					if (CuratorEventType.CREATE.equals(event.getType())
							&& event.getResultCode() == 0) {
						log.info("register node success.  path:{}, data:{}", path, data);
						if (!Strings.isNullOrEmpty(data)) {
							updateLogData(storage.getFullPath(path), new LogService.LogInfo(
									Date.from(Instant.now()), path,
									TreeCacheEvent.Type.INITIALIZED.toString(),
									String.format(Symbol.LOG_TEMPLETE, Symbol.EMPTY, data)));
						}
					}
				}
			});

		} else {

		}
	}

	private void updateLogData(String path, LogService.LogInfo info) {
		logService.merge(path, info);
	}

	/**
	 * 增加新实例的本地缓存
	 *
	 * @param path       实例节点全路径
	 * @param metaConfig 配置节点数据
	 * @param map        节点本地缓存映射
	 * @param cls        指定类型
	 */
	protected <T extends Meta> void register(String path, String metaConfig,
			Map<String, Adapter<T>> map, Class<T> cls) {
		if (Strings.isNullOrEmpty(metaConfig))
			log.error("get node's meta data return null. path:{}", path);
		else {
			Object object = GsonUtil.fromJson(metaConfig, cls);
			Adapter<T> adapter = new Adapter<T>((T) object);
			List<Monitor> monitors =
					MonitorUtil.splitToMonitorList(storage.getDataForFullPath(path));
			if (CommonUtil.notEmpty(monitors)) adapter.setMonitorList(monitors);
			map.putIfAbsent(path, adapter);
		}

	}

	/**
	 * 依据上级节点的事件响应,更新下级节点实例的状态
	 *
	 * @param transferData 上级节点传递下来的状态信息
	 * @param path         需要处理的节点地址
	 * @param source       待处理的EventSource
	 * @param logInfo      日志信息
	 */
	public void toggle(String transferData, String path, EventSource source,
			LogService.LogInfo logInfo) {

		String localInfo = storage.getDataForFullPath(path);
		String updateInfo = MonitorUtil.exchange(transferData, localInfo, source);
		log.info("exchange data. node:{} localInfo:{}, transferInfo: {}, finalInfo: {}", path,
				localInfo, transferData, updateInfo);
		if (!localInfo.equals(updateInfo)) {
			logInfo.setRemark(String.format(Symbol.LOG_TEMPLETE, localInfo, updateInfo));
			updateNodeWithFullPath(path, updateInfo, logInfo);
		}

	}

}
