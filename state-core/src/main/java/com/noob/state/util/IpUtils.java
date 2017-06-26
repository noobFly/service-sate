package com.noob.state.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import com.noob.state.constants.Symbol;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 获取真实本机网络的服务.
 * 
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IpUtils {

	/**
	 * IP地址的正则表达式.
	 */
	public static final String IP_REGEX = "((\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})";

	private static volatile String cachedIpAddress;

	/**
	 * 获取本机IP地址.
	 * 
	 * <p>
	 * 有限获取外网IP地址. 也有可能是链接着路由器的最终IP地址.
	 * </p>
	 * 
	 * @return 本机IP地址
	 */
	public static String getIp() {
		if (null != cachedIpAddress) {
			return cachedIpAddress;
		}
		Enumeration<NetworkInterface> netInterfaces;
		try {

			netInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (final SocketException ex) {
			throw new HostException(ex);
		}
		String localIpAddress = null;
		while (netInterfaces.hasMoreElements()) {
			NetworkInterface netInterface = netInterfaces.nextElement();
			Enumeration<InetAddress> ipAddresses = netInterface.getInetAddresses();
			while (ipAddresses.hasMoreElements()) {
				InetAddress ipAddress = ipAddresses.nextElement();
				if (isPublicIpAddress(ipAddress)) {
					String publicIpAddress = ipAddress.getHostAddress();
					cachedIpAddress = publicIpAddress;
					return publicIpAddress;
				}
				if (isLocalIpAddress(ipAddress)) {
					localIpAddress = ipAddress.getHostAddress();
				}
			}
		}
		cachedIpAddress = localIpAddress;
		return localIpAddress;
	}

	private static boolean isPublicIpAddress(final InetAddress ipAddress) {
		return !ipAddress.isSiteLocalAddress() && !ipAddress.isLoopbackAddress() && !isV6IpAddress(ipAddress);
	}

	private static boolean isLocalIpAddress(final InetAddress ipAddress) {
		return ipAddress.isSiteLocalAddress() && !ipAddress.isLoopbackAddress() && !isV6IpAddress(ipAddress);
	}

	private static boolean isV6IpAddress(final InetAddress ipAddress) {
		return ipAddress.getHostAddress().contains(Symbol.COLON);
	}

	/**
	 * 获取本机Host名称.
	 * 
	 * @return 本机Host名称
	 */
	public static String getHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (final UnknownHostException ex) {
			throw new HostException(ex);
		}
	}

}

/**
 * 网络主机异常.
 * 
 */
class HostException extends RuntimeException {

	private static final long serialVersionUID = 3589264847881174997L;

	public HostException(final IOException cause) {
		super(cause);
	}
}