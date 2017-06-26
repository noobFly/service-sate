package com.noob.state.service.manager;

import java.time.Instant;
import java.util.Date;

import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;
import org.apache.curator.utils.ZKPaths;
import org.apache.curator.utils.ZKPaths.PathAndNode;

import com.noob.state.node.impl.LogNode;
import com.noob.state.node.impl.MetaNode;
import com.noob.state.service.impl.ApiService;
import com.noob.state.service.impl.LogService;
import com.noob.state.service.impl.ProviderService;
import com.noob.state.service.impl.ServerService;
import com.noob.state.storage.BusinessStorage;

import lombok.Getter;

/**
 * 
 * 服务管理
 */
@Getter
public class ServiceManager {

	private final ProviderService providerService;
	private final ApiService apiService;
	private final ServerService serverService;
	private final LogService logService;
	private final MetaNode metaNode;

	public ServiceManager(BusinessStorage storage) {
		this.metaNode = new MetaNode();
		this.logService = new LogService(new LogNode(), storage);
		this.providerService = new ProviderService(logService, metaNode);
		this.apiService = new ApiService(logService, metaNode);
		this.serverService = new ServerService(storage);
	}

	/**
	 * 仅仅更新本地数据更新
	 */
	public void updateCache(String path, String data) {

		if (providerService.isRootPath(path)) return;
		if (providerService.hasRegister(path)) {
			updateProviderCache(path, data);
			return;
		}
		if (apiService.hasRegister(path)) {
			updateServiceCache(path, data);
		}

	}

	/**
	 * 逐级向下切换远端节点数据,并同步本级的本地缓存
	 */
	public void toggleStep(String path, Type type, String data) {
		LogService.LogInfo info =
				new LogService.LogInfo(Date.from(Instant.now()), path, type.toString());
		if (providerService.isRootPath(path)) {
			toggleProvider(data, info);
			return;
		}
		if (providerService.hasRegister(path)) {
			toggleService(path, data, info);
			return;
		}
		if (apiService.hasRegister(path)) {
			updateServiceCache(path, data);
		}
	}

	/**
	 * 新注册 带meta节点
	 */
	public void registerNew(String metaPath, String metaData) {
		PathAndNode pathAndNode = ZKPaths.getPathAndNode(metaPath);
		if (metaNode.getRoot().equals(pathAndNode.getNode())) {
			String path = pathAndNode.getPath();

			// "providers/${node}/meta"
			if (providerService.isProviderPath(path) && !providerService.hasRegister(path)) {
				providerService.registerWithConfig(path, metaData);
				return;
			}
			// "providers/${providerNode}/apis/${apiNode}/meta"
			if (apiService.isServicePath(path) && !apiService.hasRegister(path)) {
				apiService.registerWithConfig(path, metaData);
			}
		}
	}

	/**
	 * 当"/providers" 数据变化时, 更新每个提供者实例的状态
	 */
	private void toggleProvider(String data, LogService.LogInfo logInfo) {
		providerService.toggle(data, logInfo);
	}

	/**
	 * 
	 * 当"/providers/${instance}" 数据变化时, 更新本地提供者实例状态缓存 && 更新每个服务实例的节点状态
	 */
	private void toggleService(String path, String data, LogService.LogInfo info) {
		updateProviderCache(path, data);
		apiService.toggle(path, data, info);
	}

	private void updateProviderCache(String path, String data) {
		providerService.updateCache(path, data);
	}

	/**
	 * 当"/providers/${providerInstance}/apis/${serviceInstance}" 数据变化时,
	 * 更新每个服务实例的本地状态
	 */
	private void updateServiceCache(String path, String data) {
		apiService.updateCache(path, data);
	}

}
