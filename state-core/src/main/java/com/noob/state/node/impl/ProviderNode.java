package com.noob.state.node.impl;

import org.apache.curator.utils.ZKPaths;

import com.noob.state.constants.Symbol;

/**
 * 服务提供方节点
 */
public class ProviderNode {

	public static final String ROOT = "providers";
	public static final String PROVIDER = String.join(ZKPaths.PATH_SEPARATOR, ROOT, Symbol.PLACEHOLDER);

	/**
	 * "providers/${node}"
	 */
	public static String getInstancePath(String node) {
		return String.format(ProviderNode.PROVIDER, node);
	}

}
