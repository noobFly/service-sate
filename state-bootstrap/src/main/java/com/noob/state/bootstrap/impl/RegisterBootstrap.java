package com.noob.state.bootstrap.impl;

import java.util.List;

import com.google.common.base.Strings;
import com.noob.state.bootstrap.AbstractBootstrap;
import com.noob.state.constants.Symbol;
import com.noob.state.entity.Service;
import com.noob.state.entity.Provider;
import com.noob.state.entity.adapter.Adapter;
import com.noob.state.listener.SyncListener;
import com.noob.state.monitor.Monitor;
import com.noob.state.node.impl.ServiceNode;
import com.noob.state.node.impl.LogNode;
import com.noob.state.node.impl.MetaNode;
import com.noob.state.node.impl.ProviderNode;
import com.noob.state.node.impl.ServerNode;
import com.noob.state.register.impl.ZookeeperConfiguration;
import com.noob.state.register.storage.ServerInstance;
import com.noob.state.storage.BusinessStorage;
import com.noob.state.util.CommonUtil;
import com.noob.state.util.StateUtil;

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
        startTreeCacheListen(treePath, new SyncListener(managerController));
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

                managerController.getProviderManager().initNode(providerInstancePath, rootState);
                storage.createNodeIfNeeded(logNode.getProviderInstancePath(providerNode));

                storage.fillNode(metaNode.getProviderInstancePath(providerNode), provider.toJson());

                Adapter<Provider> providerAdapter = providerMapping(storage, provider);

                List<Service> serviceList = provider.getServiceList();
                if (CommonUtil.notEmpty(serviceList)) {
                    for (Service service : serviceList) {
                        String serviceNode = service.getNode();
                        String serviceInstancePath = ServiceNode.getInstancePath(providerNode, serviceNode);
                        log.info("register service_instance begin. path: 【{}】", serviceInstancePath);

                        managerController.getServiceManager().initNode(serviceInstancePath,
                                StateUtil.join(providerAdapter.getMonitorList())); // 带上级监控状态创建实例

                        storage.createNodeIfNeeded(logNode.getServiceInstancePath(providerNode, serviceNode));
                        storage.fillNode(metaNode.getServiceInstancePath(providerNode, serviceNode), service.toJson());

                        serviceMapping(storage, service, providerInstancePath, serviceInstancePath);

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
     * @param service                  服务实例对象
     * @param serviceInstancePath      服务实例节点地址
     */
    private void serviceMapping(BusinessStorage storage, Service service, String providerInstancePath, String serviceInstancePath) {
        Adapter<Service> serviceAdapter = new Adapter<Service>(service);
        List<Monitor> monitorList = generatorMonitor(storage, serviceInstancePath); // "/providers/${providerInstance}/services/${serviceInstance}提供者实例"
        if (monitorList != null) {
            serviceAdapter.getMonitorList().addAll(monitorList);
        }

        storage.getServiceMap().put(getFullPath(serviceInstancePath), serviceAdapter);
        storage.getRelationMap().put(getFullPath(providerInstancePath), getFullPath(serviceInstancePath));
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
        return StateUtil.splitToMonitorList(storage.getData(node));
    }
}
