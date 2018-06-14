/**
 * $RCSfile$
 * $Revision$
 * $Date$
 *
 * Copyright 2009 Jive Software.
 *
 * All rights reserved. Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jivesoftware.smack;

import java.net.URI;
import java.net.URISyntaxException;

import org.jivesoftware.smack.proxy.ProxyInfo;
import org.jivesoftware.smack.proxy.ProxyInfo.ProxyType;

/**
 * Configuration to use while establishing the connection to the XMPP server via
 * HTTP binding.
 * 
 * @see BOSHConnection
 * @author Guenther Niess
 */
public class BOSHConfiguration extends ConnectionConfiguration {

    private final boolean ssl;
    private String file;

    public BOSHConfiguration(String xmppDomain) {
        super(xmppDomain, 7070);
        this.setSASLAuthenticationEnabled(true);
        this.ssl = false;
        this.file = "/http-bind/";
    }

    public BOSHConfiguration(String xmppDomain, int port) {
        super(xmppDomain, port);
        this.setSASLAuthenticationEnabled(true);
        this.ssl = false;
        this.file = "/http-bind/";
    }

    /**
     * Create a HTTP Binding configuration.
     * 
     * @param https true if you want to use SSL
     *             (e.g. false for http://domain.lt:7070/http-bind).
     * @param host the hostname or IP address of the connection manager
     *             (e.g. domain.lt for http://domain.lt:7070/http-bind).
     * @param port the port of the connection manager
     *             (e.g. 7070 for http://domain.lt:7070/http-bind).
     * @param filePath the file which is described by the URL
     *             (e.g. /http-bind for http://domain.lt:7070/http-bind).
     * @param xmppDomain the XMPP service name
     *             (e.g. domain.lt for the user alice@domain.lt)
     */
    public BOSHConfiguration(boolean https, String host, int port, String filePath, String xmppDomain) {
        super(host, port, xmppDomain);
        this.setSASLAuthenticationEnabled(true);
        this.ssl = https;
        this.file = filePath != null ? filePath : "/";
    }

    /**
     * Create a HTTP Binding configuration.
     * 
     * @param https true if you want to use SSL
     *             (e.g. false for http://domain.lt:7070/http-bind).
     * @param host the hostname or IP address of the connection manager
     *             (e.g. domain.lt for http://domain.lt:7070/http-bind).
     * @param port the port of the connection manager
     *             (e.g. 7070 for http://domain.lt:7070/http-bind).
     * @param filePath the file which is described by the URL
     *             (e.g. /http-bind for http://domain.lt:7070/http-bind).
     * @param proxy the configuration of a proxy server.
     * @param xmppDomain the XMPP service name
     *             (e.g. domain.lt for the user alice@domain.lt)
     */
    public BOSHConfiguration(boolean https, String host, int port, String filePath, ProxyInfo proxy, String xmppDomain) {
        super(host, port, xmppDomain, proxy);
        this.setSASLAuthenticationEnabled(true);
        this.ssl = https;
        this.file = filePath != null ? filePath : "/";
    }

    public boolean isProxyEnabled() {
        return this.proxy != null && this.proxy.getProxyType() != ProxyType.NONE;
    }

    public ProxyInfo getProxyInfo() {
        return this.proxy;
    }

    public String getProxyAddress() {
        return this.proxy != null ? this.proxy.getProxyAddress() : null;
    }

    public int getProxyPort() {
        return this.proxy != null ? this.proxy.getProxyPort() : 8080;
    }

    public boolean isUsingSSL() {
        return this.ssl;
    }

    public URI getURI() throws URISyntaxException {
        if (this.file.charAt(0) != '/') {
            this.file = '/' + this.file;
        }
        return new URI((this.ssl ? "https://" : "http://") + this.getHost() + ":" + this.getPort() + this.file);
    }
}
