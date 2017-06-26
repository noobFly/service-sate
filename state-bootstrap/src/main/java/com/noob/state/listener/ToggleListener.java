package com.noob.state.listener;

import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;

import com.noob.state.listener.AbstractTreeCacheListener;
import com.noob.state.service.manager.ServiceManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 当上级控制节点发生状态变化时，更新当前节点的变化状态，并同步到本地缓存中
 */
@Slf4j
@RequiredArgsConstructor
public class ToggleListener extends AbstractTreeCacheListener {
	private final ServiceManager serviceManager;

	/**
	 * 上层的状态变动影响下级状态
	 */
	@Override
	public void dataChanged(String path, Type type, String data) {
		log.info("eventPath:{}, eventType:{}, data:{}", path, type, data);
		if (Type.NODE_ADDED.equals(type)) {
			serviceManager.registerNew(path, data);
			return;
		}

		if (Type.NODE_UPDATED.equals(type)) {
			serviceManager.toggleStep(path, type, data);
		}
	}

}
