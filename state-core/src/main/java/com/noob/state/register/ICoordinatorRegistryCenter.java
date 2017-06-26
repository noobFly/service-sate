package com.noob.state.register;

import java.util.List;

import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.TreeCache;

/**
 * 用于协调分布式服务的注册中心.
 * 
 */
public interface ICoordinatorRegistryCenter extends IRegistryCenter {

	/**
     * 直接从注册中心而非本地缓存获取数据.
     * 
     * @param key 键
     * @return 值
     */
    String getDirectly(String key);

	/**
     * 获取子节点名称集合.
     * 
     * @param key 键
     * @return 子节点名称集合
     */
    List<String> getChildrenKeys(String key);

	/**
     * 获取子节点数量.
     *
     * @param key 键
     * @return 子节点数量
     */
    int getNumChildren(String key);

	/**
     * 持久化临时注册数据.
     * 
     * @param key 键
     * @param value 值
     */
    void persistEphemeral(String key, String value);

	/**
     * 持久化顺序注册数据.
     *
     * @param key 键
     * @param value 值
     * @return 包含10位顺序数字的znode名称
     */
    String persistSequential(String key, String value);

	/**
     * 持久化临时顺序注册数据.
     * 
     * @param key 键
     */
    void persistEphemeralSequential(String key);

	/**
     * 添加本地Tree缓存.
     * 
     * @param cachePath 需加入缓存的路径
     */
    void addTreeCache(String cachePath);

	/**
     * 释放本地缓存.
     *
     * @param cachePath 需释放缓存的路径
     */
    void evictTreeCacheData(String cachePath);

	/**
     * 获取注册中心节点树数据缓存对象.
     * 
     * @param cachePath 缓存的节点路径
     * @return 注册中心数据缓存对象
     */
    TreeCache getTreeCache(String cachePath);

	/**
    **
    * 获取注册中心子节点列表数据缓存对象.
    * 
    * @param cachePath 缓存的节点路径
    * @return 注册中心数据缓存对象
    */
	PathChildrenCache getChildrenCache(String cachePath);

	/**
     * 添加本地指定节点的子节点缓存.
     * 
     * @param cachePath 需加入缓存的路径
     */
	void addChildrenCache(final String cachePath);

}
