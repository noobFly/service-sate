package com.noob.state.register.exception;

import org.apache.zookeeper.KeeperException.ConnectionLossException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 注册中心异常处理类.
 * 
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RegExceptionHandler {

	/**
	 * 处理异常.
	 * 
	 * <p>
	 * 处理掉中断和连接失效异常并继续抛注册中心.
	 * </p>
	 * 
	 * @param cause
	 *            待处理异常.
	 */
	public static void handleException(final Exception cause) {
		if (isIgnoredException(cause) || null != cause.getCause() && isIgnoredException(cause.getCause())) {
			log.debug("ignored exception for: {}", cause.getMessage());
		} else if (cause instanceof InterruptedException) {
			Thread.currentThread().interrupt();
		} else {
			throw new RegException(cause);
		}
	}

	private static boolean isIgnoredException(final Throwable cause) {
		return null != cause && (cause instanceof ConnectionLossException || cause instanceof NoNodeException
				|| cause instanceof NodeExistsException);
	}
}
