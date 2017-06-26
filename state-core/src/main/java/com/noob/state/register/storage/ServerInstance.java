package com.noob.state.register.storage;

import com.noob.state.constants.Symbol;
import com.noob.state.utils.IpUtils;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.lang.management.ManagementFactory;

@Getter
@EqualsAndHashCode(of = "serverId")
public class ServerInstance {
    /**
     * 作业实例主键.
     */
    private final String serverId;

    public ServerInstance() {
        serverId = String.join(Symbol.DELIMITER, IpUtils.getIp(), ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
    }

}
