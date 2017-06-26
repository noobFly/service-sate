package com.noob.state.register.storage;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.utils.ZKPaths;

import com.noob.state.constants.Symbol;
import com.noob.state.register.ICoordinatorRegistryCenter;
import com.noob.state.register.ITransactionExecutionCallback;
import com.noob.state.register.exception.RegExceptionHandler;

import lombok.Getter;

/**
 * 节点数据访问类.
 * 
 * 
 */
public class NodeStorage {
	@Getter
	private transient final ICoordinatorRegistryCenter regCenter;

	@Getter
	private transient final NodePath nodePath;

	public NodeStorage(final ICoordinatorRegistryCenter regCenter, String root) {
		this.regCenter = regCenter;
		this.nodePath = new NodePath(root);
		try {
			getClient().createContainers(root);
		} catch (Exception e) {
			RegExceptionHandler.handleException(e);
		}
	}

	/**
	 * 获取从namespace路径下的全路径
	 * 
	 * @param path
	 * @return
	 */
	public String getFullPath(String path) {
		return this.getNodePath().getFullPath(path);
	}

	/**
	 * 先从treeCache中获取,获取不到从从注册中心获取
	 * 
	 * @param node
	 *            节点名称
	 * @return 节点数据值
	 */
	public String getData(final String node) {
		return getDataForFullPath(getFullPath(node));
	}

	public String getDataForFullPath(final String node) {
		return regCenter.getDirectly(node);
	}

	/**
	 * 获取子节点名称列表.
	 * 
	 * @param node
	 *            节点名称
	 * @return 节点子节点名称列表
	 */
	public List<String> getNodeChildrenKeys(final String node) {
		return regCenter.getChildrenKeys(getFullPath(node));
	}

	/**
	 * 如果存在根节点且不存在指定节点 则创建指定节点.
	 * 
	 * @param node
	 *            节点名称
	 */
	public void createNodeIfNeeded(final String node) {
		if (canCreate(node))
			regCenter.persist(getFullPath(node), Symbol.EMPTY, false);

	}

	/**
	 * 如果存在根节点且不存在指定节点 则创建指定节点. 并执行回调
	 * 
	 * @param node
	 *            指定节点
	 * @param value
	 *            指定填充数据
	 * @param BackgroundCallback
	 *            指定回调
	 */
	public void createNodeIfNeeded(final String node, final String value, final BackgroundCallback callback) {
		if (canCreate(node))
			regCenter.persist(getFullPath(node), value, callback);

	}

	/**
	 * 判定 节点能否创建
	 */
	private boolean canCreate(final String node) {
		return isRootNodeExisted() && !isNodeExisted(node);
	}

	/**
	 * 根节点是否存在
	 */
	private boolean isRootNodeExisted() {
		return regCenter.isExisted(ZKPaths.PATH_SEPARATOR + nodePath.getRoot());
	}

	/**
	 * 删除节点.
	 * 
	 * @param node
	 *            节点名称
	 */
	public void removeNodeIfExisted(final String node) {
		if (isNodeExisted(node)) {
			regCenter.remove(getFullPath(node));
		}
	}

	/**
	 * 判断节点是否存在.
	 * 
	 * @param node
	 *            节点名称
	 * @return 节点是否存在
	 */
	public boolean isNodeExisted(final String node) {

		return regCenter.isExisted(getFullPath(node));
	}

	/**
	 * 判断节点是否存在.
	 * 
	 * @param node
	 *            节点名称
	 * @return 节点是否存在
	 */
	public boolean isNodeExistedForFullPath(final String node) {

		return regCenter.isExisted(node);
	}

	/**
	 * 填充节点数据. 不存在则创建；存在则更新
	 *
	 * @param node
	 *            节点名称
	 * @param value
	 *            节点数据值
	 */
	public void fillNode(final String node, final Object value) {
		fillNodeForFullPath(getFullPath(node), value.toString());
	}

	/**
	 * 填充节点数据. 不存在则创建；存在则更新
	 *
	 * @param node
	 *            节点名称
	 * @param value
	 *            节点数据值
	 */
	public void fillNodeForFullPath(final String node, final Object value) {
		regCenter.persist(node, value.toString(), true);
	}

	/**
	 * 填充临时节点数据.
	 * 
	 * @param node
	 *            节点名称
	 * @param value
	 *            节点数据值
	 */
	public void fillEphemeralNode(final String node, final Object value) {
		regCenter.persistEphemeral(getFullPath(node), value.toString());
	}

	/**
	 * 更新节点数据.
	 * 
	 * @param node
	 *            节点名称
	 * @param isfull
	 *            节点名称是否全路径
	 * @param value
	 *            节点数据值
	 */
	public boolean updateNode(final String node, boolean isfull, final Object value) {
		return regCenter.update(isfull ? node : getFullPath(node), value.toString());
	}

	/**
	 * 在事务中执行操作.
	 * 
	 * @param callback
	 *            执行操作的回调
	 */
	public void executeInTransaction(final ITransactionExecutionCallback callback) {
		try {
			CuratorTransactionFinal curatorTransactionFinal = getClient().inTransaction().check()
					.forPath(ZKPaths.PATH_SEPARATOR).and();
			callback.execute(curatorTransactionFinal);
			curatorTransactionFinal.commit();
			// CHECKSTYLE:OFF
		} catch (final Exception ex) {
			// CHECKSTYLE:ON
			RegExceptionHandler.handleException(ex);
		}
	}

	/**
	 * 注册连接状态监听器.
	 * 
	 * @param listener
	 *            连接状态监听器
	 */
	public void addConnectionStateListener(final ConnectionStateListener listener) {
		getClient().getConnectionStateListenable().addListener(listener);
	}

	private CuratorFramework getClient() {
		return (CuratorFramework) regCenter.getRawClient();
	}

	/**
	 * 初始化节点树缓存
	 * 
	 * @param cachePath
	 *            节点树root路径
	 */
	public void addTreeCache(String cachePath) {
		regCenter.addTreeCache(getFullPath(cachePath));
	}

	/**
	 * 注册节点树监听
	 * 
	 * @param listener
	 *            监听器
	 * @param cachePath
	 *            节点树root路径
	 */
	public void addTreeListener(final TreeCacheListener listener, String cachePath) {
		regCenter.getTreeCache(getFullPath(cachePath)).getListenable().addListener(listener);
	}

	/**
	 * 初始化子节点列表缓存
	 * 
	 * @param cachePath
	 *            节点树root路径
	 */
	public void addChildrenCache(String cachePath) {
		regCenter.addChildrenCache(getFullPath(cachePath));
	}

	/**
	 * 注册子节点列表监听器.
	 * 
	 * @param listener
	 *            数据监听器
	 */
	public void addChildrenListener(final PathChildrenCacheListener listener, String cachePath) {
		regCenter.getChildrenCache(getFullPath(cachePath)).getListenable().addListener(listener);
	}

}
