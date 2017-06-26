package com.noob.state.node.impl;

import org.apache.curator.utils.ZKPaths;

import com.noob.state.constants.Symbol;

/**
 * 服务实例节点
 */
public class ServerNode {

	public static final String ROOT = "servers";
	private static final String SERVER = String.join(ZKPaths.PATH_SEPARATOR, ROOT, Symbol.PLACEHOLDER);

	/** "servers/${node}" **/
	public static String getInstancePath(String node) {
		return String.format(ServerNode.SERVER, node);
	}

}
