package com.noob.state.listener;

import java.time.Instant;
import java.util.Date;

import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type;

import com.noob.state.listener.AbstractChildrenCacheListener;
import com.noob.state.service.impl.LogService;
import com.noob.state.service.impl.ProviderService;
import com.noob.state.service.impl.ServerService;
import com.noob.state.service.manager.ServiceManager;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 服务运行实例上下线监听
 */
@Slf4j
public class ServerChangeListener extends AbstractChildrenCacheListener {

	private ServerService serverService;

	private ProviderService providerService;

	public ServerChangeListener(ServiceManager serviceManager) {
		this.serverService = serviceManager.getServerService();
		this.providerService = serviceManager.getProviderService();
	}

	/**
	 * 当服务实例上下线时判定各节点业务状态的变化
	 */
	@Override
	public void dataChanged(String path, Type type, String data) {
		log.info("eventPath:{}, eventType:{}, data:{}", path, type, data);

		if (serverService.isServerPath(path)) {

			LogService.LogInfo info = new LogService.LogInfo(Date.from(Instant.now()), path, type.toString());
			if (Type.CHILD_REMOVED.equals(type) && !serverService.hasOnlineServer()) {
				providerService.turnOffline(info);
				return;
			}

			if (Type.CHILD_ADDED.equals(type) && providerService.isOffline()) {
				providerService.turnOnline(info);
			}

		}

	}

}
