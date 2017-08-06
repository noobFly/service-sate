package com.noob.state.service.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.curator.utils.ZKPaths;

import com.google.common.collect.Multimap;
import com.noob.state.entity.Api;
import com.noob.state.entity.adapter.Adapter;
import com.noob.state.monitor.MonitorFactory.EventSource;
import com.noob.state.node.impl.ApiNode;
import com.noob.state.node.impl.MetaNode;
import com.noob.state.service.AbstractService;
import com.noob.state.util.CommonUtil;
import com.noob.state.util.SateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 对应管理每一个通道提供的API节点
 */
@Slf4j
public class ApiService extends AbstractService {

	private final String SERVICE_CACHE_TOPIC = "更新每个服务实例本地缓存状态";
	private final String SERVICE_TOPIC = "更新每个服务实例的状态";

	public ApiService(LogService logService, MetaNode metaNode) {
		super(logService, metaNode);
	}

	public Multimap<String, String> getRelationMap() {
		return storage.getRelationMap();
	}

	public Map<String, Adapter<Api>> getServiceAdapters() {
		return storage.getApiMap();
	}

	/**
	 * 判定服务实例节点已注册
	 */
	public boolean hasRegister(String path) {
		return getServiceAdapters().containsKey(path);
	}

	/**
	 * 判定 "/providers/${providerInstance}/apis/${serviceInstance}"
	 */
	public boolean isServicePath(String path) {

		return ApiNode.ROOT
				.equals(ZKPaths.getPathAndNode(ZKPaths.getPathAndNode(path).getPath()).getNode());
	}

	/**
	 * 更新每个实例的缓存状态
	 */
	public void updateCache(String path, String data) {
		log.info("{} begin. path:{}, data:{}.", SERVICE_CACHE_TOPIC, path, data);
		SateUtil.updateLocalCache(data, getServiceAdapters().get(path));

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
	public void toggle(String path, String data, LogService.LogInfo info) {
		log.info("{} begin.", SERVICE_TOPIC);
		Entry<String, Collection<String>> entry = CommonUtil.getObjFromOptional(getRelationMap()
				.asMap().entrySet().stream().filter(t -> t.getKey().equals(path)).findFirst());
		if (entry != null) {
			for (String relationPath : entry.getValue()) {
				toggle(data, relationPath, EventSource.API_INSTANCE, info);
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
		super.register(path, metaConfig, getServiceAdapters(), Api.class);
		String providerPath = ApiNode.getProviderPath(path);
		if (storage.getProviderMap().get(providerPath) != null) {
			getRelationMap().put(providerPath, path); // 当服务实例节点的上级提供者实例节点已经被注册时，保存两者的关系
		}
	}

	/**
	 * 注册
	 * 
	 * @param providerNode
	 *            提供者节点
	 * @param apiNode
	 *            服务节点
	 */
	public void register(String providerNode, String apiNode) {
		registerWithConfig(storage.getFullPath(ApiNode.getInstancePath(providerNode, apiNode)),
				storage.getData(metaNode.getApiInstancePath(providerNode, apiNode)));

	}

}
