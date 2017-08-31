package com.noob.state.access;

import java.util.List;

import com.google.common.base.Splitter;
import com.noob.state.bootstrap.AbstractBootstrap;
import com.noob.state.constants.Symbol;
import com.noob.state.entity.Meta;
import com.noob.state.entity.Provider;
import com.noob.state.entity.adapter.Adapter;
import com.noob.state.node.impl.ServiceNode;
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
		bootstrap.getProviderManager().disabledProvider(code, logRemark);
	}

	/**
	 * 启用通道
	 */
	public void enabledProvider(String code, String logRemark) {
		bootstrap.getProviderManager().enabledProvider(code, logRemark);
	}

	/**
	 * 禁用服务
	 */
	public void disabledService(String providerCode, String serviceCode, String logRemark) {
		bootstrap.getServiceManager().disabledService(providerCode, serviceCode, logRemark);
	}

	/**
	 * 启用服务
	 */
	public void enabledService(String providerCode, String serviceCode, String logRemark) {
		bootstrap.getServiceManager().enabledService(providerCode, serviceCode, logRemark);
	}

	/**
	 * 判定提供者状态可用providerCode
	 */
	public boolean providerAlive(String code) {
		return predicate(bootstrap.getProviderManager().getProviderAdapters()
				.get(bootstrap.getFullPath(ProviderNode.getInstancePath(code))));
	}

	/**
	 * 判定Service状态可用 (providerCode@-@serviceCode)
	 */
	public boolean servicePredicate(String code) {
		List<String> list = Splitter.on(Symbol.DELIMITER).splitToList(code);
		return list != null && list.size() == 2 && servicePredicate(list.get(0), list.get(1));
	}

	/**
	 * 判定Service状态可用
	 */
	public boolean servicePredicate(String providerCode, String serviceCode) {
		return predicate(bootstrap.getServiceManager().getServiceAdapters().get(
				bootstrap.getFullPath(ServiceNode.getInstancePath(providerCode, serviceCode))));
	}

	private <T extends Meta> boolean predicate(Adapter<T> adapter) {
		return !CommonUtil.notEmpty(adapter.getMonitorList());
	}

}
