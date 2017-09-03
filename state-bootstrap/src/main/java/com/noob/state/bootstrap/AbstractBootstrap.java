package com.noob.state.bootstrap;

import java.util.List;
import java.util.stream.Collectors;

import com.noob.state.entity.Provider;
import com.noob.state.entity.adapter.Adapter;
import com.noob.state.listener.AbstractChildrenCacheListener;
import com.noob.state.listener.AbstractTreeCacheListener;
import com.noob.state.node.impl.ProviderNode;
import com.noob.state.node.impl.ServiceNode;
import com.noob.state.register.impl.ZookeeperConfiguration;
import com.noob.state.register.impl.ZookeeperRegistryCenter;
import com.noob.state.service.impl.ProviderManager;
import com.noob.state.service.impl.ServerManager;
import com.noob.state.service.impl.ServiceManager;
import com.noob.state.service.manager.ManagerController;
import com.noob.state.storage.BusinessStorage;
import com.noob.state.util.CommonUtil;
import com.noob.state.util.GsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 节点管理
 *
 */
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractBootstrap {

	private final ZookeeperConfiguration zkConfig;
	private final String root; // 主题

	protected final String treePath = ProviderNode.ROOT; // 需要监控树的根节点

	protected BusinessStorage storage;
	protected ManagerController managerController;

	/**
	 * 初始化
	 */
	public void init() {
		storage = new BusinessStorage(initZookeeper(), root);
		managerController = new ManagerController(storage);
		initCache();
		beforeListen();
		startListen();
		afterListen();

		// log.info("init finish. storage:{}", GsonUtil.toJson(storage));

		Thread infoThread = new Thread(()->{
			while (true) {
				log.info("init finish. storage:{}", GsonUtil.toJson(storage));
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		infoThread.setDaemon(true);
		infoThread.start();


	}

	/**
	 * 初始化cache
	 */
	protected abstract void initCache();

	/**
	 * 注册监听
	 */
	protected abstract void startListen();

	/**
	 * 监听之前初始化
	 */
	protected void beforeListen() {

	}

	/**
	 * 监听之后初始化
	 */
	private void afterListen() {

	}

	/**
	 * 连接zk
	 */
	private ZookeeperRegistryCenter initZookeeper() {
		ZookeeperRegistryCenter regCenter = new ZookeeperRegistryCenter(zkConfig);
		regCenter.init();
		return regCenter;
	}

	/**
	 * 同步远端节点数据
	 */
	protected void syncRemoteData() {
		List<String> providerNodeList = storage.getNodeChildrenKeys(treePath); // 提供者
		if (CommonUtil.notEmpty(providerNodeList)) {
			for (String providerNode : providerNodeList) {
				managerController.getProviderManager().register(providerNode);
				List<String> serviceNodeList =
						storage.getNodeChildrenKeys(ServiceNode.getRootPath(providerNode)); // service
				if (CommonUtil.notEmpty(serviceNodeList)) {
					for (String serviceNode : serviceNodeList) {
						managerController.getServiceManager().register(providerNode, serviceNode);
					}
				}

			}
		}
	}

	protected void addTreeCache(String path) {
		storage.addTreeCache(path);
	}

	protected void startTreeCacheListen(String path, AbstractTreeCacheListener listener) {
		storage.addTreeListener(listener, path);
	}

	protected void startChildrenListen(String path, AbstractChildrenCacheListener listener) {
		storage.addChildrenListener(listener, path);

	}

	protected void addPathChildrenCache(String path) {
		storage.addChildrenCache(path);
	}

	public ProviderManager getProviderManager() {
		return managerController.getProviderManager();
	}

	public ServiceManager getServiceManager() {
		return managerController.getServiceManager();
	}

	public ServerManager getServerManager() {
		return managerController.getServerManager();
	}

	/**
	 * 获取从namespace下的全路径
	 * 
	 * @param path
	 * @return
	 */
	public String getFullPath(String path) {
		return storage.getFullPath(path);
	}

	/**
	 * 获取所有通道的元配置
	 */
	public List<Provider> getChannelInfo() {
		return storage.getProviderMap().values().stream().map(Adapter::getT)
				.collect(Collectors.toList());
	}

}
