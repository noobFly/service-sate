package com.noob.state.node.impl;

import org.apache.curator.utils.ZKPaths;

import com.noob.state.node.AbstractLeafNode;

/**
 * 日志节点
 */
public class LogNode extends AbstractLeafNode {

	@Override
	public String getRoot() {
		return "log";
	}

	/** "${parentNode}/log/${node}" **/
	public String getPath(String parentNode, String node) {
		return String.join(ZKPaths.PATH_SEPARATOR, parentNode, getRoot(), node);
	}

}
