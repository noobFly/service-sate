package com.noob.state.register.storage;

import org.apache.curator.utils.ZKPaths;

import lombok.RequiredArgsConstructor;

/**
 * 主题节点工具
 */
@RequiredArgsConstructor
public class NodePath {

	private final String root;

	public String getRoot() {
		return this.root;
	}

	/**
	 * 获取节点全路径.
	 * 
	 * @param node
	 *            节点名称
	 * @return 节点全路径
	 */
	public String getFullPath(final String node) {
		return ZKPaths.makePath(root, node);
	}

}
