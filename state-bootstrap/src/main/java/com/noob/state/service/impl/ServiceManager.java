package com.noob.state.service.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.curator.utils.ZKPaths;

import com.google.common.collect.Multimap;
import com.noob.state.entity.Service;
import com.noob.state.entity.adapter.Adapter;
import com.noob.state.monitor.MonitorFactory.EventSource;
import com.noob.state.monitor.MonitorFactory.MonitorContainer;
import com.noob.state.node.impl.ServiceNode;
import com.noob.state.node.impl.MetaNode;
import com.noob.state.service.AbstractManager;
import com.noob.state.util.CommonUtil;
import com.noob.state.util.StateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 对应管理每一个通道提供的SERVICE节点
 */
@Slf4j
public class ServiceManager extends AbstractManager {

	private final String SERVICE_CACHE_TOPIC = "更新每个服务实例本地缓存状态";
	private final String SERVICE_TOPIC = "更新每个服务实例的状态";

	public ServiceManager(LogManager logService, MetaNode metaNode) {
		super(logService, metaNode);
	}

	public Multimap<String, String> getRelationMap() {
		return storage.getRelationMap();
	}

	public Map<String, Adapter<Service>> getServiceAdapters() {
		return storage.getServiceMap();
	}

	/**
	 * 判定服务实例节点已注册
	 */
	public boolean hasRegister(String path) {
		return getServiceAdapters().containsKey(path);
	}

	/**
	 * 判定 "/providers/${providerInstance}/services/${serviceInstance}"
	 */
	public boolean isServicePath(String path) {

		return ServiceNode.ROOT
				.equals(ZKPaths.getPathAndNode(ZKPaths.getPathAndNode(path).getPath()).getNode());
	}

	/**
	 * 更新每个实例的缓存状态
	 */
	public void updateCache(String path, String data) {
		log.info("{} begin. path:{}, data:{}.", SERVICE_CACHE_TOPIC, path, data);
		StateUtil.updateLocalCache(data, getServiceAdapters().get(path));

	}

	/**
	 * 更新服务实例节点的信息
	 * 
	 * @param path
	 *            事件源节点地址
	 * @param data
	 *            事件源数据
	 * @param info
	 *            日志信息
	 */
	public void toggle(String path, String data, LogManager.LogInfo info) {
		log.info("{} begin.", SERVICE_TOPIC);
		Entry<String, Collection<String>> entry = CommonUtil.getObjFromOptional(getRelationMap()
				.asMap().entrySet().stream().filter(t -> t.getKey().equals(path)).findFirst());
		if (entry != null) {
			for (String relationPath : entry.getValue()) {
				toggle(data, relationPath, EventSource.SERVICE_INSTANCE, info);
			}
		}
	}

	/**
	 * 注册
	 * 
	 * @param path
	 *            实例节点全路径
	 * @param metaConfig
	 *            配置
	 */
	public void registerWithConfig(String path, String metaConfig) {
		super.register(path, metaConfig, getServiceAdapters(), Service.class);
		String providerPath = ServiceNode.getProviderPath(path);
		if (storage.getProviderMap().get(providerPath) != null) {
			getRelationMap().put(providerPath, path); // 当服务实例节点的上级提供者实例节点已经被注册时，保存两者的关系
		}
	}

	/**
	 * 注册
	 * 
	 * @param providerNode
	 *            提供者节点
	 * @param serviceNode
	 *            服务节点
	 */
	public void register(String providerNode, String serviceNode) {
		registerWithConfig(storage.getFullPath(ServiceNode.getInstancePath(providerNode, serviceNode)),
				storage.getData(metaNode.getServiceInstancePath(providerNode, serviceNode)));

	}

	/**
	 * 禁用通道
	 */
	public void disabledService(String providerCode, String serviceCode, String logRemark) {
		addSingleMonitor(ServiceNode.getInstancePath(providerCode, serviceCode),
				MonitorContainer.DIS_SERVICE_INSTANCE, logRemark);
	}

	/**
	 * 启用通道
	 */
	public void enabledService(String providerCode, String serviceCode, String logRemark) {
		removeSingleMonitor(ServiceNode.getInstancePath(providerCode, serviceCode),
				MonitorContainer.DIS_SERVICE_INSTANCE, logRemark);
	}

}
