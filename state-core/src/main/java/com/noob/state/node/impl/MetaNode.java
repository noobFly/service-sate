package com.noob.state.node.impl;

import org.apache.curator.utils.ZKPaths;

import com.noob.state.node.AbstractLeafNode;

/**
 * 元数据配置节点
 */
public class MetaNode extends AbstractLeafNode {

	@Override
	public String getRoot() {
		return "meta";
	}

	/** "${path}/meta/" **/
	public String getPath(String path) {
		return String.join(ZKPaths.PATH_SEPARATOR, path, getRoot());
	}

}
