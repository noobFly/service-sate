package com.noob.state.monitor;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

public interface MonitorFactory {

	public enum EventType {
		/**
		 * 离线
		 */
		OFF,
		/**
		 * 上线
		 */
		ON,
		/**
		 * 不可用
		 */
		DISABLED,
		/**
		 * 可用
		 */
		ENABLED,
		/**
		 * 受限制
		 */
		LIMIT,
		/**
		 * 解除限制
		 */
		RELEASE;
	}

	/**
	 * 事件源头
	 */
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	public enum EventSource {
		/**
		 * 服务实例
		 */
		SERVER(1),
		/**
		 * 所有服务提供方
		 */
		PROVIDER_ALL(2),
		/**
		 * 单个服务提供方实例
		 */
		PROVIDER_INSTANCE(3),
		/**
		 * 单个提供方下的所有服务
		 */
		API_INSTANCE(4);

		private int level;

	}

	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	public enum MonitorContainer {
		/**
		 * 服务下线
		 */
		OFF_SERVER(bulider(EventType.OFF, EventSource.SERVER), true),
		/**
		 * 服务上线
		 */
		ON_SERVER(bulider(EventType.ON, EventSource.SERVER), false),

		/**
		 * 停用 所有提供者
		 */
		DIS_PROVIDER_ALL(bulider(EventType.DISABLED, EventSource.PROVIDER_ALL), true),
		/**
		 * 启用 所有提供者
		 */
		EN_PROVIDER_ALL(bulider(EventType.ENABLED, EventSource.PROVIDER_ALL), false),

		/************************* 提供者实例 **********************************/

		/**
		 * 限制单个提供者下所有服务
		 */
		LIMIT_PROVIDER_INSTANCE(bulider(EventType.LIMIT, EventSource.PROVIDER_INSTANCE), true),
		/**
		 * 解除限制单个提供者实例
		 */
		RELEASE_PROVIDER_INSTANCE(bulider(EventType.RELEASE, EventSource.PROVIDER_INSTANCE), false),
		/**
		 * 启用单个提供者
		 */
		EN_PROVIDER_INSTANCE(bulider(EventType.ENABLED, EventSource.PROVIDER_INSTANCE), false),
		/**
		 * 停用单个提供者
		 */
		DIS_PROVIDER_INSTANCE(bulider(EventType.DISABLED, EventSource.PROVIDER_INSTANCE), true),
		

		/************************* 服务实例 **********************************/
		/**
		 * 限制单个服务实例
		 */
		LIMIT_API_INSTANCE(bulider(EventType.LIMIT, EventSource.API_INSTANCE), true),
		/**
		 * 解除限制单个服务实例
		 */
		RELEASE_API_INSTANCE(bulider(EventType.RELEASE, EventSource.API_INSTANCE), false),
		/**
		 * 启用单个服务实例
		 */
		EN_API_INSTANCE(bulider(EventType.ENABLED, EventSource.API_INSTANCE), false),
		/**
		 * 停用单个服务实例
		 */
		DIS_API_INSTANCE(bulider(EventType.DISABLED, EventSource.API_INSTANCE), true);

		private Monitor monitor;
		private boolean display;

		private static Monitor bulider(EventType type, EventSource source) {
			return new Monitor(type.toString(), source.toString());
		}

		public static List<Monitor> getAllMonitor() {
			List<Monitor> list = Lists.newArrayList();
			for (MonitorContainer enums : MonitorContainer.values()) {
				list.add(enums.getMonitor());
			}
			return list;
		}

		/**
		 * 只有display 为 true 的状态才会更新到节点上
		 */
		public static List<String> getDisplayMonitor(EventSource source) {
			List<String> result = null;
			for (MonitorContainer each : MonitorContainer.values()) {
				Monitor monitor = each.getMonitor();
				if (each.display && monitor.getSource().equals(source.toString())) {
					if (result == null)
						result = Lists.newArrayList();
					result.add(monitor.toString());
				}
			}
			return result;
		}

	}

	Map<Monitor, Monitor> ANTONYM_MAP = ImmutableMap.of(MonitorContainer.OFF_SERVER.getMonitor(),
			MonitorContainer.ON_SERVER.getMonitor(), MonitorContainer.DIS_PROVIDER_ALL.getMonitor(),
			MonitorContainer.EN_PROVIDER_ALL.getMonitor()); // 相对的两组
}
