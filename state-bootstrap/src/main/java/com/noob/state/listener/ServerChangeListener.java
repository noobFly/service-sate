package com.noob.state.listener;

import java.time.Instant;
import java.util.Date;

import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type;

import com.noob.state.listener.AbstractChildrenCacheListener;
import com.noob.state.service.impl.LogManager;
import com.noob.state.service.impl.ProviderManager;
import com.noob.state.service.impl.ServerManager;
import com.noob.state.service.manager.ManagerController;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 服务运行实例上下线监听
 */
@Slf4j
public class ServerChangeListener extends AbstractChildrenCacheListener {

	private ServerManager serverService;

	private ProviderManager providerService;

	public ServerChangeListener(ManagerController managerController) {
		this.serverService = managerController.getServerManager();
		this.providerService = managerController.getProviderManager();
	}

	/**
	 * 当服务实例上下线时判定各节点业务状态的变化
	 */
	@Override
	public void dataChanged(String path, Type type, String data) {
		log.info("eventPath:{}, eventType:{}, data:{}", path, type, data);

		if (serverService.isServerPath(path)) {

			LogManager.LogInfo info = new LogManager.LogInfo(Date.from(Instant.now()), path, type.toString());
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
