package com.noob.state.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.curator.utils.ZKPaths;

import com.noob.state.constants.Symbol;
import com.noob.state.entity.Provider;
import com.noob.state.entity.adapter.Adapter;
import com.noob.state.monitor.MonitorFactory.EventSource;
import com.noob.state.monitor.MonitorFactory.MonitorContainer;
import com.noob.state.node.impl.MetaNode;
import com.noob.state.node.impl.ProviderNode;
import com.noob.state.service.AbstractService;
import com.noob.state.util.MonitorUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 提供者节点管理服务
 */
@Slf4j
public class ProviderService extends AbstractService {

	private final String PROVIDER_CACHE_TOPIC = "更新每个提供者实例本地缓存状态";
	private final String PROVIDER_TOPIC = "更新每个服务实例的状态";

	public ProviderService(LogService logService, MetaNode metaNode) {
		super(logService, metaNode);
	}

	public Map<String, Adapter<Provider>> getProviderAdapters() {
		return storage.getProviderMap();
	}

	/**
	 * 更新每个实例的缓存状态
	 */
	public void updateCache(String path, String data) {
		log.info("{} begin. path:{}, data:{}.", PROVIDER_CACHE_TOPIC, path, data);
		MonitorUtil.updateLocalCache(data, getProviderAdapters().get(path));

	}

	/**
	 * 更新提供者实例节点的信息
	 *
	 * @param data    事件源数据
	 * @param logInfo 日志信息
	 */
	public void toggle(String data, LogService.LogInfo logInfo) {
		log.info("{} begin.", PROVIDER_TOPIC);
		for (String path : getProviderAdapters().keySet()) {
			toggle(data, path, EventSource.PROVIDER_INSTANCE, logInfo);

		}
	}

	/**
	 * 判定 提供者实例节点已被注册
	 */
	public boolean hasRegister(String path) {
		return getProviderAdapters().get(path) != null;
	}

	/**
	 * 判定 "/providers"
	 */
	public boolean isRootPath(String path) {
		return ProviderNode.ROOT.equals(ZKPaths.getNodeFromPath(path));
	}

	/**
	 * 判定 "/providers/${providerInstance}"
	 */
	public boolean isProviderPath(String path) {
		return storage.getFullPath(ProviderNode.ROOT)
				.equals(ZKPaths.getPathAndNode(path).getPath());
	}

	/**
	 * 判定 下线
	 */
	public boolean isOffline() {
		String data = storage.getData(ProviderNode.ROOT);
		List<String> list = MonitorUtil.split(data);
		return list != null && list.stream()
				.anyMatch(t -> t.equals(MonitorContainer.OFF_SERVER.getMonitor().toString()));
	}

	/**
	 * 下线
	 */
	public void turnOffline(LogService.LogInfo log) {
		String remoteData = getRootRemoteData();
		updateNodeData(remoteData, MonitorUtil.addSingleMonitor(remoteData,
				MonitorContainer.OFF_SERVER.getMonitor().toString()), log);

	}

	/**
	 * 上线
	 */
	public void turnOnline(LogService.LogInfo log) {
		String remoteData = getRootRemoteData();
		updateNodeData(remoteData, MonitorUtil.removeMonitor(remoteData,
				MonitorUtil.greaterLevel(EventSource.PROVIDER_ALL)), log);

	}

	/**
	 * 获取zk节点数据
	 */
	private String getRootRemoteData() {
		return storage.getData(ProviderNode.ROOT);
	}

	/**
	 * 更新节点数据并记录日志
	 *
	 * @param originData 原来的数据
	 * @param updateData 需要更新的数据
	 * @param log        日志记录
	 */
	private void updateNodeData(String originData, String updateData, LogService.LogInfo log) {
		if (updateData != null && storage.updateNode(ProviderNode.ROOT, false, updateData)) {
			log.setRemark(String.format(Symbol.LOG_TEMPLETE, originData, updateData));
			logService.merge(storage.getFullPath(Symbol.EMPTY), log);
		}

	}

	/**
	 * 注册
	 *
	 * @param node 节点
	 */
	public void register(String node) {
		registerWithConfig(storage.getFullPath(ProviderNode.getInstancePath(node)),
				storage.getData(metaNode.getProviderInstancePath(node)));

	}

	/**
	 * 注册
	 *
	 * @param path       实例节点全路径
	 * @param metaConfig 配置
	 */
	public void registerWithConfig(String path, String metaConfig) {
		register(path, metaConfig, getProviderAdapters(), Provider.class);
	}
}
