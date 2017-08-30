package com.noob.state;

import java.util.Arrays;

import org.junit.Test;

import com.noob.state.bootstrap.impl.ControllerBootstrap;
import com.noob.state.bootstrap.impl.RegisterBootstrap;
import com.noob.state.entity.Service;
import com.noob.state.entity.Provider;
import com.noob.state.node.impl.ServiceNode;
import com.noob.state.node.impl.LogNode;
import com.noob.state.node.impl.MetaNode;
import com.noob.state.node.impl.ProviderNode;
import com.noob.state.node.impl.ServerNode;
import com.noob.state.register.impl.ZookeeperConfiguration;

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

		Service s1 = new Service("S0001", "GRXXCX1", "1", "个人信息查询", "个人信息查询提供维度1,2,3", "PY");
		Service s2 = new Service("S0002", "XLCX2", "2", "学历查询", "学历查询提供维度4,5", "PY");

		Service s3 = new Service("S0003", "GRXXCX3", "1", "个人信息查询", "个人信息查询提供维度1,2,3", "TD");
		Service s4 = new Service("S0004", "XLCX4", "2", "学历查询", "学历查询提供维度4,5", "TD");

		Service s5 = new Service("S0005", "GRXXCX3", "1", "x个人信息查询", "x个人信息查询提供维度1,2,3", "TD");
		Service s6 = new Service("S0006", "XLCX4", "2", "x学历查询", "x学历查询提供维度4,5", "TD");
		RegisterBootstrap manager1 = new RegisterBootstrap(new ZookeeperConfiguration("127.0.0.1:2181", "mirco/route"),
				"gateWay",
				Arrays.asList(new Provider("C0002", "TD", "tongdun", "同盾通道服务", Arrays.asList(s3, s4)),
						new Provider("C0001", "PY", "pengyuan", "鹏远通道服务", Arrays.asList(s1, s2)),
						new Provider("C0003", "PYx", "pengxxyuan", "鹏xx远通道服务", Arrays.asList(s1, s2))));

		ControllerBootstrap manager2 = new ControllerBootstrap(
				new ZookeeperConfiguration("127.0.0.1:2181", "mirco/route"), "gateWay");
		manager1.init();
		while (true) {

		}
	}

}
