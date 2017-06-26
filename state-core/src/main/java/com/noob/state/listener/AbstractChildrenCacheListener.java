package com.noob.state.listener;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

import com.google.common.base.Charsets;
import com.noob.state.constants.Symbol;

/**
 * 指定节点的子节点变化监听
 */
public abstract class AbstractChildrenCacheListener implements PathChildrenCacheListener {
	@Override
	public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
		ChildData childData = event.getData();
		if (null == childData) {
			return;
		}

		String path = childData.getPath();
		if (path.isEmpty()) {
			return;
		}

		dataChanged(path, event.getType(),
				null == childData.getData() ? Symbol.EMPTY : new String(childData.getData(), Charsets.UTF_8));
	}

	public abstract void dataChanged(String path, Type type, String object);

}
