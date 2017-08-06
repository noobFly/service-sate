# service-sate

#入口：<br>
    1. RegisterBootstrap:    节点树初始化; 同步节点状态.   注册SyncListener. <br>
    2. ObserverBootstrap:    同步节点状态.    注册SyncListener. <br>
    3. ControllerBootstrap:  同步节点状态, 响应各层节点的变化, 调控下层节点的状态.  注册ToggleListener和ServerChangeListener <br>

#节点： <br>
level下实例节点state有优先级 #{MonitorFactory.EventSource} <br>
     1、servers: 服务运行实例集合 <br>
     2、providers: 提供者标识集合 <br>
                    state: OFF@-@SERVER; DISABLED@-@PROVIDER_ALL; LIMIT@-@PROVIDER_INSTANCE; DISABLED@-@PROVIDER_INSTANCE <br>
     3、apis: 服务借口标识集合 <br>
                    state: LIMIT@-@API_INSTANCE;  DISABLED@-@API_INSTANCE <br>
     4、log: 日志集合, 每天产生一个日期格式<2017-06-26>的子日志节点记录变动过程 <br>

#监听： <br>
 TreeCacheListener:   监控TreeCache下节点树的变动 <br>
            SyncListener 与 ToggleListener:   新增事件 -> 新增状态标识至本地缓存;    更新事件 -> 更新本地缓存(ToggleListener同时处理变更状态向下传导).<br>
 PathChildrenCacheListener： 监听指定节点下子节点列表的变动 <br>
            ServerChangeListener:  监听服务运行实例的存亡, 判定是否标记offline. <br>
