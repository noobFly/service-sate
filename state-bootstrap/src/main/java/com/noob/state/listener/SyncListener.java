package com.noob.state.listener;

import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;

import com.noob.state.listener.AbstractTreeCacheListener;
import com.noob.state.service.manager.ServiceManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 同步节点数据 TODO 当提供者实例和服务实例 节点删除时的处理
 *
 */
@Slf4j
@RequiredArgsConstructor
public class SyncListener extends AbstractTreeCacheListener {

	private final ServiceManager serviceManager;

	@Override
	public void dataChanged(String path, Type type, String data) {
		log.info("eventPath:{}, eventType:{}, data:{}.", path, type, data);
		if (Type.NODE_ADDED.equals(type)) {
			serviceManager.registerNew(path, data); // 针对meta节点
			return;
		}

		if (Type.NODE_UPDATED.equals(type)) {
			serviceManager.updateCache(path, data); // 针对实例节点
		}
	}

}
