package com.noob.state.node.impl;

import org.apache.curator.utils.ZKPaths;

import com.noob.state.constants.Symbol;

/**
 * 服务组节点
 *
 */
public class ApiNode {
	public final static String ROOT = "apis";
	public final static String API = String.join(ZKPaths.PATH_SEPARATOR, ProviderNode.PROVIDER, ROOT,
			Symbol.PLACEHOLDER);

	/**
	 * "providers/${node}/apis"
	 */
	public static String getRootPath(String node) {
		return String.join(ZKPaths.PATH_SEPARATOR, ProviderNode.getInstancePath(node), ROOT);
	}

	/**
	 * "providers/${providerNode}/apis/${apiNode}"
	 */
	public static String getInstancePath(String providerNode, String apiNode) {
		return String.format(ApiNode.API, providerNode, apiNode);
	}

	/**
	 * api实例节点路径推导提供者节点路径
	 */
	public static String getProviderPath(String path) {
		return ZKPaths.getPathAndNode(ZKPaths.getPathAndNode(path).getPath()).getPath();
	}

}
