package com.noob.state.access.impl;

import com.noob.state.access.AbstractBootstrap;
import com.noob.state.listener.SyncListener;
import com.noob.state.register.impl.ZookeeperConfiguration;

/**
 * 
 * 仅同步节点数据. TODO 当zk节点变化中时,新上线observer将导致本地cache不稳定。
 */
public class ObserverBootstrap extends AbstractBootstrap {

	public ObserverBootstrap(ZookeeperConfiguration zkConfig, String root) {
		super(zkConfig, root);
	}

	@Override
	protected void initCache() {
		addTreeCache(treePath);
	}

	/**
	 * 注册监听
	 */
	protected void startListen() {
		startTreeCacheListen(treePath, new SyncListener(serviceManager));
	}

	@Override
	protected void beforeListen() {
		syncRemoteData();
	}

}
