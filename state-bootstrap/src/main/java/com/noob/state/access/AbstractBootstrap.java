package com.noob.state.access;

import java.util.List;

import com.noob.state.listener.AbstractChildrenCacheListener;
import com.noob.state.listener.AbstractTreeCacheListener;
import com.noob.state.node.impl.ApiNode;
import com.noob.state.node.impl.ProviderNode;
import com.noob.state.register.impl.ZookeeperConfiguration;
import com.noob.state.register.impl.ZookeeperRegistryCenter;
import com.noob.state.service.impl.ApiService;
import com.noob.state.service.impl.ProviderService;
import com.noob.state.service.impl.ServerService;
import com.noob.state.service.manager.ServiceManager;
import com.noob.state.storage.BusinessStorage;
import com.noob.state.utils.CommonUtil;
import com.noob.state.utils.GsonUtil;

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
	protected ServiceManager serviceManager;

	/**
	 * 初始化
	 */
	public void init() {
		storage = new BusinessStorage(initZookeeper(), root);
		serviceManager = new ServiceManager(storage);
		initCache();
		beforeListen();
		startListen();
		afterListen();

		log.info("init finish. storage:{}", GsonUtil.toJson(storage));

		/*while (true) {
			log.info("init finish. storage:{}", GsonUtil.toJson(storage));
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}*/
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
				serviceManager.getProviderService().register(providerNode);
				List<String> apiNodeList = storage.getNodeChildrenKeys(ApiNode.getRootPath(providerNode)); // api
				if (CommonUtil.notEmpty(apiNodeList)) {
					for (String apiNode : apiNodeList) {
						serviceManager.getApiService().register(providerNode, apiNode);
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

	public ProviderService getProviderService() {
		return serviceManager.getProviderService();
	}

	public ApiService getApiService() {
		return serviceManager.getApiService();
	}

	public ServerService getServerService() {
		return serviceManager.getServerService();
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

}
