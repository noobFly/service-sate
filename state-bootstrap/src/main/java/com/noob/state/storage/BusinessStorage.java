package com.noob.state.storage;

import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.noob.state.entity.Api;
import com.noob.state.entity.Provider;
import com.noob.state.entity.adapter.Adapter;
import com.noob.state.monitor.MonitorFactory.EventType;
import com.noob.state.register.ICoordinatorRegistryCenter;
import com.noob.state.register.storage.NodeStorage;

import lombok.Getter;

/**
 * 带业务属性扩展
 */
public class BusinessStorage extends NodeStorage {
	/**
	 * <providerFullPath, adapter> "/${root}/providers/${providerInstance}"
	 */
	@Getter
	private final Map<String, Adapter<Provider>> providerMap = Maps.newHashMap();
	/**
	 * <apiFullPath, adapter>
	 * "/${root}/providers/${providerInstance}/apis/${apiInstance}"
	 */
	@Getter
	private final Map<String, Adapter<Api>> apiMap = Maps.newHashMap();
	/**
	 * <providerFullPath, apiFullPath>
	 */
	@Getter
	private final Multimap<String, String> relationMap = HashMultimap.create();

	public BusinessStorage(ICoordinatorRegistryCenter regCenter, String root) {
		super(regCenter, root);
	}

	/**
	 * 判定可用状态
	 */
	public boolean isAlive(String status) {
		return !Strings.isNullOrEmpty(status) && !(EventType.OFF.toString().equals(status)
				&& EventType.DISABLED.toString().equals(status) && EventType.LIMIT.toString().equals(status));
	}

}
