package com.noob.state.node;

import org.apache.curator.utils.ZKPaths;

import com.noob.state.node.impl.ApiNode;
import com.noob.state.node.impl.ProviderNode;

/**
 * 叶子节点抽象
 */
public abstract class AbstractLeafNode {

	public String PROVIDER = String.join(ZKPaths.PATH_SEPARATOR, ProviderNode.PROVIDER, getRoot());
	public String API = String.join(ZKPaths.PATH_SEPARATOR, ApiNode.API, getRoot());

	/**
	 * "providers/${node}/${getRoot()}"
	 */
	public String getProviderInstancePath(String node) {
		return String.format(PROVIDER, node);
	}

	/**
	 * "providers/${providerNode}/apis/${apiNode}/${getRoot()}"
	 */
	public String getApiInstancePath(String providerNode, String apiNode) {
		return String.format(API, providerNode, apiNode);
	}

	public abstract String getRoot() ;

}
