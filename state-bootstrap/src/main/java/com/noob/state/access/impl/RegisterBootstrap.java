package com.noob.state.access.impl;

import java.util.List;

import com.google.common.base.Strings;
import com.noob.state.access.AbstractBootstrap;
import com.noob.state.constants.Symbol;
import com.noob.state.entity.Api;
import com.noob.state.entity.Provider;
import com.noob.state.entity.adapter.Adapter;
import com.noob.state.listener.SyncListener;
import com.noob.state.monitor.Monitor;
import com.noob.state.node.impl.ApiNode;
import com.noob.state.node.impl.LogNode;
import com.noob.state.node.impl.MetaNode;
import com.noob.state.node.impl.ProviderNode;
import com.noob.state.node.impl.ServerNode;
import com.noob.state.register.impl.ZookeeperConfiguration;
import com.noob.state.register.storage.ServerInstance;
import com.noob.state.storage.BusinessStorage;
import com.noob.state.util.CommonUtil;
import com.noob.state.util.SateUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 注册节点信息并同步节点变动状态
 */

@Slf4j
public class RegisterBootstrap extends AbstractBootstrap {

    @Setter
    private List<Provider> providerList;
    @Getter
    private ServerInstance serverInstance;

    public RegisterBootstrap(ZookeeperConfiguration zkConfig, String root, List<Provider> providerList) {
        super(zkConfig, root);
        this.providerList = providerList;
    }

    /**
     * 注册服务器： 1、创建或同步zk上的节点和信息 2、创建同步监控listener
     */
    @Override
    protected void beforeListen() {
        registerNode(storage);
    }

    @Override
    protected void initCache() {
        addTreeCache(treePath);
    }

    @Override
    protected void startListen() {
        startTreeCacheListen(treePath, new SyncListener(serviceManager));
    }

    /**
     * 注册节点
     */
    private void registerNode(BusinessStorage storage) {

        if (CommonUtil.notEmpty(providerList)) {
            LogNode logNode = new LogNode();
            MetaNode metaNode = new MetaNode();

            storage.createNodeIfNeeded(logNode.getRoot());
            storage.createNodeIfNeeded(ProviderNode.ROOT);

            String rootState = Strings.nullToEmpty(storage.getData(ProviderNode.ROOT)); // providers节点状态
            for (Provider provider : providerList) {

                String providerNode = provider.getNode();
                String providerInstancePath = ProviderNode.getInstancePath(providerNode);
                log.info("register provider_instance begin. path: 【{}】", providerInstancePath);

                serviceManager.getProviderService().initNode(providerInstancePath, rootState);
                storage.createNodeIfNeeded(logNode.getProviderInstancePath(providerNode));

                storage.fillNode(metaNode.getProviderInstancePath(providerNode), provider.toJson());

                Adapter<Provider> providerAdapter = providerMapping(storage, provider);

                List<Api> apiList = provider.getApiList();
                if (CommonUtil.notEmpty(apiList)) {
                    for (Api api : apiList) {
                        String apiNode = api.getNode();
                        String apiInstancePath = ApiNode.getInstancePath(providerNode, apiNode);
                        log.info("register api_instance begin. path: 【{}】", apiInstancePath);

                        serviceManager.getApiService().initNode(apiInstancePath,
                                SateUtil.join(providerAdapter.getMonitorList())); // 带上级监控状态创建实例

                        storage.createNodeIfNeeded(logNode.getApiInstancePath(providerNode, apiNode));
                        storage.fillNode(metaNode.getApiInstancePath(providerNode, apiNode), api.toJson());

                        apiMapping(storage, api, providerInstancePath, apiInstancePath);

                    }
                }

            }
            serverInstance = new ServerInstance();
            storage.fillEphemeralNode(ServerNode.getInstancePath(new ServerInstance().getServerId()), Symbol.EMPTY);
        }
    }

    /**
     * @param storage
     * @param providerInstancePath 提供者实例节点地址
     * @param api                  服务实例对象
     * @param apiInstancePath      服务实例节点地址
     */
    private void apiMapping(BusinessStorage storage, Api api, String providerInstancePath, String apiInstancePath) {
        Adapter<Api> apiAdapter = new Adapter<Api>(api);
        List<Monitor> monitorList = generatorMonitor(storage, apiInstancePath); // "/providers/${providerInstance}/apis/${apiInstance}提供者实例"
        if (monitorList != null) {
            apiAdapter.getMonitorList().addAll(monitorList);
        }

        storage.getApiMap().put(getFullPath(apiInstancePath), apiAdapter);
        storage.getRelationMap().put(getFullPath(providerInstancePath), getFullPath(apiInstancePath));
    }

    /**
     * 本地映射服务提供者
     */
    private Adapter<Provider> providerMapping(BusinessStorage storage, Provider provider) {

        String instancePath = ProviderNode.getInstancePath(provider.getNode());

        Adapter<Provider> providerAdapter = new Adapter<Provider>(provider);

        List<Monitor> instanceMonitors = generatorMonitor(storage, instancePath); // "/providers/${providerInstance}提供者实例"
        if (instanceMonitors != null)
            providerAdapter.getMonitorList().addAll(instanceMonitors);

        storage.getProviderMap().put(getFullPath(instancePath), providerAdapter);
        return providerAdapter;
    }

    private List<Monitor> generatorMonitor(BusinessStorage storage, String node) {
        return SateUtil.splitToMonitorList(storage.getData(node));
    }
}
