package com.noob.state.bootstrap.impl;

import com.noob.state.bootstrap.AbstractBootstrap;
import com.noob.state.listener.ServerChangeListener;
import com.noob.state.listener.ToggleListener;
import com.noob.state.node.impl.ServerNode;
import com.noob.state.register.impl.ZookeeperConfiguration;

/**
 * 
 * 同步节点信息，响应服务节点的变化、 控制节点的信息状态
 */
public class ControllerBootstrap extends AbstractBootstrap {
	final String childrenPath = ServerNode.ROOT; // 需要监控服务上下线节点

	public ControllerBootstrap(ZookeeperConfiguration zkConfig, String root) {
		super(zkConfig, root);
	}

	@Override
	protected void initCache() {
		addTreeCache(treePath);
		addPathChildrenCache(childrenPath);

	}

	@Override
	protected void beforeListen() {
		syncRemoteData();
	}

	@Override
	protected void startListen() {
		startTreeCacheListen(treePath, new ToggleListener(managerController));
		startChildrenListen(childrenPath, new ServerChangeListener(managerController));
	}

}
