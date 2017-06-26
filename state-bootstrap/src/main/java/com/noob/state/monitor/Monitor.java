package com.noob.state.monitor;

import com.noob.state.constants.Symbol;
import com.noob.state.monitor.MonitorFactory.EventSource;
import com.noob.state.monitor.MonitorFactory.EventType;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 状态的监控
 */
@Getter
@AllArgsConstructor
public class Monitor {
	/**
	 * {@link EventType}
	 */
	private String type;
	/**
	 * {@link EventSource}
	 */
	private String source;

	public String toString() {
		return String.join(Symbol.DELIMITER, type, source);
	}

	public boolean equals(Monitor t) {
		return this.toString().equals(t.toString());

	}
}
