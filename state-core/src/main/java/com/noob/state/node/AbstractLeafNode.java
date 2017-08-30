package com.noob.state.node;

import org.apache.curator.utils.ZKPaths;

import com.noob.state.node.impl.ServiceNode;
import com.noob.state.node.impl.ProviderNode;

/**
 * 叶子节点抽象
 */
public abstract class AbstractLeafNode {

	public String PROVIDER = String.join(ZKPaths.PATH_SEPARATOR, ProviderNode.PROVIDER, getRoot());
	public String SERVICE = String.join(ZKPaths.PATH_SEPARATOR, ServiceNode.SERVICE, getRoot());

	/**
	 * "providers/${node}/${getRoot()}"
	 */
	public String getProviderInstancePath(String node) {
		return String.format(PROVIDER, node);
	}

	/**
	 * "providers/${providerNode}/services/${serviceNode}/${getRoot()}"
	 */
	public String getServiceInstancePath(String providerNode, String serviceNode) {
		return String.format(SERVICE, providerNode, serviceNode);
	}

	public abstract String getRoot() ;

}
