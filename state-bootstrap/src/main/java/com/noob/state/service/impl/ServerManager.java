package com.noob.state.service.impl;

import com.noob.state.node.impl.ServerNode;
import com.noob.state.storage.BusinessStorage;

import lombok.RequiredArgsConstructor;

/**
 * 运行服务实例管理
 */
@RequiredArgsConstructor
public class ServerManager {

    private final BusinessStorage storage;

    /**
     * 判定 服务实例节点
     */
    public boolean isServerPath(String path) {
        return path.startsWith(storage.getFullPath(ServerNode.ROOT));
    }

    /**
     * 判定存在 在线的服务实例
     */
    public boolean hasOnlineServer() {
        return storage.getNodeChildrenKeys(ServerNode.ROOT).size() > 0;
    }

}
