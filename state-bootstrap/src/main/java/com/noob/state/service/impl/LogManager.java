package com.noob.state.service.impl;

import java.text.DateFormat;
import java.util.Date;

import com.noob.state.constants.Symbol;
import com.noob.state.node.impl.LogNode;
import com.noob.state.storage.BusinessStorage;
import com.noob.state.util.GsonUtil;
import com.noob.state.util.IpUtils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 日志节点管理
 */
@RequiredArgsConstructor
@Slf4j
public class LogManager {
	private final LogNode logNode;
	@Getter
	private final BusinessStorage storage;

	private String TOPIC = "merge node info begin. path:{}, append-data:{}.";

	public void merge(String node, LogInfo logInfo) {

		String path = logNode.getPath(node, DateFormat.getDateInstance(DateFormat.DEFAULT).format(logInfo.getTime()));
		String data = GsonUtil.toJson(logInfo);
		log.info(TOPIC, path, data);
		if (!storage.isNodeExistedForFullPath(path)) {
			storage.fillNodeForFullPath(path, data);
		} else {
			storage.updateNode(path, true, String.join(Symbol.SEMICOLON, storage.getDataForFullPath(path), data));
		}

	}

	@Setter
	@Getter
	public static class LogInfo {
		private Date time;
		private String eventPath;
		private String eventType;
		private String operator;
		private String remark;

		public LogInfo(Date time, String eventPath, String eventType) {
			this.time = time;
			this.eventPath = eventPath;
			this.eventType = eventType;
			this.operator = String.join(Symbol.DELIMITER_PART, IpUtils.getHostName(), IpUtils.getIp());
		}

		public LogInfo(Date time, String eventPath, String eventType, String remark) {
			this.time = time;
			this.eventPath = eventPath;
			this.eventType = eventType;
			this.remark = remark;
			this.operator = String.join(Symbol.DELIMITER_PART, IpUtils.getHostName(), IpUtils.getIp());
		}

	}

}
