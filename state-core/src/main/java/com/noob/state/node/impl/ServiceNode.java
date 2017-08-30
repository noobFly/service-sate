package com.noob.state.node.impl;

import org.apache.curator.utils.ZKPaths;

import com.noob.state.constants.Symbol;

/**
 * 服务组节点
 *
 */
public class ServiceNode {
	public final static String ROOT = "services";
	public final static String SERVICE = String.join(ZKPaths.PATH_SEPARATOR, ProviderNode.PROVIDER, ROOT,
			Symbol.PLACEHOLDER);

	/**
	 * "providers/${node}/services"
	 */
	public static String getRootPath(String node) {
		return String.join(ZKPaths.PATH_SEPARATOR, ProviderNode.getInstancePath(node), ROOT);
	}

	/**
	 * "providers/${providerNode}/services/${serviceNode}"
	 */
	public static String getInstancePath(String providerNode, String serviceNode) {
		return String.format(ServiceNode.SERVICE, providerNode, serviceNode);
	}

	/**
	 * service实例节点路径推导提供者节点路径
	 */
	public static String getProviderPath(String path) {
		return ZKPaths.getPathAndNode(ZKPaths.getPathAndNode(path).getPath()).getPath();
	}

}
