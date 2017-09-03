package com.noob.state;

import com.noob.state.access.StatePortal;
import com.noob.state.bootstrap.impl.ControllerBootstrap;
import com.noob.state.bootstrap.impl.RegisterBootstrap;
import com.noob.state.entity.Provider;
import com.noob.state.entity.Service;
import com.noob.state.node.impl.*;
import com.noob.state.register.impl.ZookeeperConfiguration;
import org.junit.Test;

import java.util.Arrays;

public class CuratorTest {

    public void testNodePath() {

        System.out.println(ProviderNode.ROOT);
        System.out.println(ProviderNode.getInstancePath("provider1"));

        System.out.println(ServerNode.ROOT);
        System.out.println(ServerNode.getInstancePath("server1"));

        System.out.println(ServiceNode.getRootPath("provider1"));
        System.out.println(ServiceNode.getInstancePath("provider1", "service1"));

        LogNode logNode = new LogNode();
        System.out.println(logNode.getProviderInstancePath("provider1"));
        System.out.println(logNode.getServiceInstancePath("provider1", "service1"));

        MetaNode metaNode = new MetaNode();
        System.out.println(metaNode.getProviderInstancePath("provider1"));
        System.out.println(metaNode.getServiceInstancePath("provider1", "service1"));
    }

    @Test
    public void curatorFrameWorkTest() throws Exception {

        Service s1 = new Service("S0001", "学历查询", "C0001");
        Service s2 = new Service("S0002", "手机号实名认证", "C0001");
        Service s3 = new Service("S0003", "身份证实名认证", "C0001");
        Service s4 = new Service("S0001", "学历查询", "C0002");
        Service s5 = new Service("S0002", "手机号实名认证", "C0002");
        Service s6 = new Service("S0003", "身份证实名认证", "C0002");


        RegisterBootstrap manager1 = new RegisterBootstrap(new ZookeeperConfiguration("127.0.0.1:2181", "mirco/route"),
                "gateWay",
                Arrays.asList(new Provider("C0001", "同盾", Arrays.asList(s1, s2, s3)),
                        new Provider("C0002", "亿鑫", Arrays.asList(s4, s5, s6))));

        ControllerBootstrap manager2 = new ControllerBootstrap(
                new ZookeeperConfiguration("127.0.0.1:2181", "mirco/route"), "gateWay");
        manager2.init();
        StatePortal portal = new StatePortal(manager2);
        portal.disabledProvider("C0001","xwj");
        portal.enabledService("C0001","S0003","xwj");
        while (true) {

        }
    }

}
