package com.noob.state.access;

import com.google.common.base.Splitter;
import com.noob.state.bootstrap.AbstractBootstrap;
import com.noob.state.constants.Symbol;
import com.noob.state.entity.Meta;
import com.noob.state.entity.Provider;
import com.noob.state.entity.Service;
import com.noob.state.entity.adapter.Adapter;
import com.noob.state.node.impl.ProviderNode;
import com.noob.state.node.impl.ServiceNode;
import com.noob.state.util.CommonUtil;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 判定状态是否可用
 */
@RequiredArgsConstructor
public class StatePortal {

    private final AbstractBootstrap bootstrap;

    /**
     * service状态集合缓存
     *
     * @return
     */
    public Map<String, Adapter<Service>> getServiceAdapters() {
        return bootstrap.getServiceManager().getServiceAdapters();
    }

    /**
     * provider状态集合缓存
     *
     * @return
     */
    public Map<String, Adapter<Provider>> getProviderAdapters() {
        return bootstrap.getProviderManager().getProviderAdapters();
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
        return predicate(getProvider(code));
    }

    /**
     * 判定Service状态可用 (providerCode@-@serviceCode)
     */
    public boolean serviceAlive(String code) {
        List<String> list = Splitter.on(Symbol.DELIMITER).splitToList(code);
        return list != null && list.size() == 2 && serviceAlive(list.get(0), list.get(1));
    }

    /**
     * 判定Service状态可用
     */
    public boolean serviceAlive(String providerCode, String serviceCode) {
        return predicate(getService(providerCode, serviceCode));
    }

    /**
     * 获取缓存中的指定提供者实例
     *
     * @param code 提供者编码
     * @return
     */
    public Adapter<Provider> getProvider(String code) {
        return getProviderAdapters().get(bootstrap.getFullPath(ProviderNode.getInstancePath(code)));
    }

    /**
     * 获取缓存中的指定service实例
     *
     * @param providerCode 提供者编码
     * @param serviceCode  service编码
     * @return
     */
    public Adapter<Service> getService(String providerCode, String serviceCode) {
        return getServiceAdapters().get(bootstrap.getFullPath(ServiceNode.getInstancePath(providerCode, serviceCode)));
    }

    public <T extends Meta> boolean predicate(Adapter<T> adapter) {
        return !CommonUtil.notEmpty(adapter.getMonitorList());
    }

}
