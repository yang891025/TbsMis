package org.jivesoftware.smack.proxy;

import javax.net.SocketFactory;

/**
 * Class which stores proxy information such as proxy type, host, port,
 * authentication etc.
 * 
 * @author Atul Aggarwal
 */

public class ProxyInfo {
	public enum ProxyType {
		NONE, HTTP, SOCKS4, SOCKS5
	}

	private final String proxyAddress;
	private final int proxyPort;
	private final String proxyUsername;
	private final String proxyPassword;
	private final ProxyInfo.ProxyType proxyType;

	public ProxyInfo(ProxyInfo.ProxyType pType, String pHost, int pPort, String pUser,
			String pPass) {
        proxyType = pType;
        proxyAddress = pHost;
        proxyPort = pPort;
        proxyUsername = pUser;
        proxyPassword = pPass;
	}

	public static ProxyInfo forHttpProxy(String pHost, int pPort, String pUser,
			String pPass) {
		return new ProxyInfo(ProxyInfo.ProxyType.HTTP, pHost, pPort, pUser, pPass);
	}

	public static ProxyInfo forSocks4Proxy(String pHost, int pPort,
			String pUser, String pPass) {
		return new ProxyInfo(ProxyInfo.ProxyType.SOCKS4, pHost, pPort, pUser, pPass);
	}

	public static ProxyInfo forSocks5Proxy(String pHost, int pPort,
			String pUser, String pPass) {
		return new ProxyInfo(ProxyInfo.ProxyType.SOCKS5, pHost, pPort, pUser, pPass);
	}

	public static ProxyInfo forNoProxy() {
		return new ProxyInfo(ProxyInfo.ProxyType.NONE, null, 0, null, null);
	}

	public static ProxyInfo forDefaultProxy() {
		return new ProxyInfo(ProxyInfo.ProxyType.NONE, null, 0, null, null);
	}

	public ProxyInfo.ProxyType getProxyType() {
		return this.proxyType;
	}

	public String getProxyAddress() {
		return this.proxyAddress;
	}

	public int getProxyPort() {
		return this.proxyPort;
	}

	public String getProxyUsername() {
		return this.proxyUsername;
	}

	public String getProxyPassword() {
		return this.proxyPassword;
	}

	public SocketFactory getSocketFactory() {
		if (this.proxyType == ProxyInfo.ProxyType.NONE) {
			return new DirectSocketFactory();
		} else if (this.proxyType == ProxyInfo.ProxyType.HTTP) {
			return new HTTPProxySocketFactory(this);
		} else if (this.proxyType == ProxyInfo.ProxyType.SOCKS4) {
			return new Socks4ProxySocketFactory(this);
		} else if (this.proxyType == ProxyInfo.ProxyType.SOCKS5) {
			return new Socks5ProxySocketFactory(this);
		} else {
			return null;
		}
	}
}
