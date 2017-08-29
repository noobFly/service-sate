package com.noob.state.access;

import java.util.List;

import com.google.common.base.Splitter;
import com.noob.state.bootstrap.AbstractBootstrap;
import com.noob.state.constants.Symbol;
import com.noob.state.entity.Meta;
import com.noob.state.entity.Provider;
import com.noob.state.entity.adapter.Adapter;
import com.noob.state.node.impl.ApiNode;
import com.noob.state.node.impl.ProviderNode;
import com.noob.state.util.CommonUtil;

import lombok.RequiredArgsConstructor;

/**
 * 判定状态是否可用
 * 
 *
 */
@RequiredArgsConstructor
public class StatePortal {

	private final AbstractBootstrap bootstrap;

	/**
	 * 获取所有通道的元配置
	 */
	public List<Provider> getChannelInfo() {
		return bootstrap.getChannelInfo();
	}

	/**
	 * 禁用通道
	 */
	public void disabledProvider(String code, String logRemark) {
		bootstrap.getProviderService().disabledProvider(code, logRemark);
	}

	/**
	 * 启用通道
	 */
	public void enabledProvider(String code, String logRemark) {
		bootstrap.getProviderService().enabledProvider(code, logRemark);
	}

	/**
	 * 禁用服务
	 */
	public void disabledApi(String providerCode, String apiCode, String logRemark) {
		bootstrap.getApiService().disabledApi(providerCode, apiCode, logRemark);
	}

	/**
	 * 启用服务
	 */
	public void enabledApi(String providerCode, String apiCode, String logRemark) {
		bootstrap.getApiService().enabledApi(providerCode, apiCode, logRemark);
	}

	/**
	 * 判定提供者状态可用providerCode
	 */
	public boolean providerAlive(String code) {
		return predicate(bootstrap.getProviderService().getProviderAdapters().get(ProviderNode.getInstancePath(code)));
	}

	/**
	 * 判定Api状态可用 (providerCode@-@apiCode)
	 */
	public boolean apiPredicate(String code) {
		List<String> list = Splitter.on(Symbol.DELIMITER).splitToList(code);
		return list != null && list.size() == 2 && apiPredicate(list.get(0), list.get(1));
	}

	/**
	 * 判定Api状态可用
	 */
	public boolean apiPredicate(String providerCode, String apiCode) {
		return predicate(
				bootstrap.getApiService().getServiceAdapters().get(ApiNode.getInstancePath(providerCode, apiCode)));
	}

	private <T extends Meta> boolean predicate(Adapter<T> adapter) {
		return !CommonUtil.notEmpty(adapter.getMonitorList());
	}

}
