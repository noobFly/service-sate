package com.noob.state.service.manager;

import java.time.Instant;
import java.util.Date;

import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;
import org.apache.curator.utils.ZKPaths;
import org.apache.curator.utils.ZKPaths.PathAndNode;

import com.noob.state.node.impl.LogNode;
import com.noob.state.node.impl.MetaNode;
import com.noob.state.service.impl.ServiceManager;
import com.noob.state.service.impl.LogManager;
import com.noob.state.service.impl.ProviderManager;
import com.noob.state.service.impl.ServerManager;
import com.noob.state.storage.BusinessStorage;

import lombok.Getter;

/**
 * 
 * 服务管理
 */
@Getter
public class ManagerController {

	private final ProviderManager providerManager;
	private final ServiceManager serviceManager;
	private final ServerManager serverManager;
	private final LogManager logManager;
	private final MetaNode metaNode;

	public ManagerController(BusinessStorage storage) {
		this.metaNode = new MetaNode();
		this.logManager = new LogManager(new LogNode(), storage);
		this.providerManager = new ProviderManager(logManager, metaNode);
		this.serviceManager = new ServiceManager(logManager, metaNode);
		this.serverManager = new ServerManager(storage);
	}

	/**
	 * 仅仅更新本地数据更新
	 */
	public void updateCache(String path, String data) {

		if (providerManager.isRootPath(path)) return;
		if (providerManager.hasRegister(path)) {
			updateProviderCache(path, data);
			return;
		}
		if (serviceManager.hasRegister(path)) {
			updateServiceCache(path, data);
		}

	}

	/**
	 * 逐级向下切换远端节点数据,并同步本级的本地缓存
	 */
	public void toggleStep(String path, Type type, String data) {
		LogManager.LogInfo info =
				new LogManager.LogInfo(Date.from(Instant.now()), path, type.toString());
		if (providerManager.isRootPath(path)) {
			toggleProvider(data, info);
			return;
		}
		if (providerManager.hasRegister(path)) {
			toggleService(path, data, info);
			return;
		}
		if (serviceManager.hasRegister(path)) {
			updateServiceCache(path, data);
		}
	}

	/**
	 * 新注册 带meta节点 （因为在注册时先注册了log节点再注册meta节点，保证在注册新节点时日志一定能写入log data）
	 */
	public void registerNew(String metaPath, String metaData) {
		PathAndNode pathAndNode = ZKPaths.getPathAndNode(metaPath);
		if (metaNode.getRoot().equals(pathAndNode.getNode())) {
			String path = pathAndNode.getPath();

			// "providers/${node}/meta"
			if (providerManager.isProviderPath(path) && !providerManager.hasRegister(path)) {
				providerManager.registerWithConfig(path, metaData);
				return;
			}
			// "providers/${providerNode}/services/${serviceNode}/meta"
			if (serviceManager.isServicePath(path) && !serviceManager.hasRegister(path)) {
				serviceManager.registerWithConfig(path, metaData);
			}
		}
	}

	/**
	 * 当"/providers" 数据变化时, 更新每个提供者实例的状态
	 */
	private void toggleProvider(String data, LogManager.LogInfo logInfo) {
		providerManager.toggle(data, logInfo);
	}

	/**
	 * 
	 * 当"/providers/${instance}" 数据变化时, 更新本地提供者实例状态缓存 && 更新每个服务实例的节点状态
	 */
	private void toggleService(String path, String data, LogManager.LogInfo info) {
		updateProviderCache(path, data);
		serviceManager.toggle(path, data, info);
	}

	private void updateProviderCache(String path, String data) {
		providerManager.updateCache(path, data);
	}

	/**
	 * 当"/providers/${providerInstance}/services/${serviceInstance}" 数据变化时,
	 * 更新每个服务实例的本地状态
	 */
	private void updateServiceCache(String path, String data) {
		serviceManager.updateCache(path, data);
	}

}
