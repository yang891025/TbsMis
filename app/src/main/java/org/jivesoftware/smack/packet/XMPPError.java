/**
 * $RCSfile$
 * $Revision$
 * $Date$
 *
 * Copyright 2003-2007 Jive Software.
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

package org.jivesoftware.smack.packet;

import java.util.*;

/**
 * Represents a XMPP error sub-packet. Typically, a server responds to a request that has
 * problems by sending the packet back and including an error packet. Each error has a code, type, 
 * error condition as well as as an optional text explanation. Typical errors are:<p>
 *
 * <table border=1>
 *      <hr><td><b>Code</b></td><td><b>XMPP Error</b></td><td><b>Type</b></td></hr>
 *      <tr><td>500</td><td>interna-server-error</td><td>WAIT</td></tr>
 *      <tr><td>403</td><td>forbidden</td><td>AUTH</td></tr>
 *      <tr><td>400</td<td>bad-request</td><td>MODIFY</td>></tr>
 *      <tr><td>404</td><td>item-not-found</td><td>CANCEL</td></tr>
 *      <tr><td>409</td><td>conflict</td><td>CANCEL</td></tr>
 *      <tr><td>501</td><td>feature-not-implemented</td><td>CANCEL</td></tr>
 *      <tr><td>302</td><td>gone</td><td>MODIFY</td></tr>
 *      <tr><td>400</td><td>jid-malformed</td><td>MODIFY</td></tr>
 *      <tr><td>406</td><td>no-acceptable</td><td> MODIFY</td></tr>
 *      <tr><td>405</td><td>not-allowed</td><td>CANCEL</td></tr>
 *      <tr><td>401</td><td>not-authorized</td><td>AUTH</td></tr>
 *      <tr><td>402</td><td>payment-required</td><td>AUTH</td></tr>
 *      <tr><td>404</td><td>recipient-unavailable</td><td>WAIT</td></tr>
 *      <tr><td>302</td><td>redirect</td><td>MODIFY</td></tr>
 *      <tr><td>407</td><td>registration-required</td><td>AUTH</td></tr>
 *      <tr><td>404</td><td>remote-server-not-found</td><td>CANCEL</td></tr>
 *      <tr><td>504</td><td>remote-server-timeout</td><td>WAIT</td></tr>
 *      <tr><td>502</td><td>remote-server-error</td><td>CANCEL</td></tr>
 *      <tr><td>500</td><td>resource-constraint</td><td>WAIT</td></tr>
 *      <tr><td>503</td><td>service-unavailable</td><td>CANCEL</td></tr>
 *      <tr><td>407</td><td>subscription-required</td><td>AUTH</td></tr>
 *      <tr><td>500</td><td>undefined-condition</td><td>WAIT</td></tr>
 *      <tr><td>400</td><td>unexpected-condition</td><td>WAIT</td></tr>
 *      <tr><td>408</td><td>request-timeout</td><td>CANCEL</td></tr>
 * </table>
 *
 * @author Matt Tucker
 */
public class XMPPError {

    private int code;
    private XMPPError.Type type;
    private String condition;
    private final String message;
    private List<PacketExtension> applicationExtensions;


    /**
     * Creates a new error with the specified condition infering the type and code.
     * If the Condition is predefined, client code should be like:
     *     new XMPPError(XMPPError.Condition.remote_server_timeout);
     * If the Condition is not predefined, invocations should be like
     *     new XMPPError(new XMPPError.Condition("my_own_error"));
     *
     * @param condition the error condition.
     */
    public XMPPError(XMPPError.Condition condition) {
        init(condition);
        message = null;
    }

    /**
     * Creates a new error with the specified condition and message infering the type and code.
     * If the Condition is predefined, client code should be like:
     *     new XMPPError(XMPPError.Condition.remote_server_timeout, "Error Explanation");
     * If the Condition is not predefined, invocations should be like
     *     new XMPPError(new XMPPError.Condition("my_own_error"), "Error Explanation");
     *
     * @param condition the error condition.
     * @param messageText a message describing the error.
     */
    public XMPPError(XMPPError.Condition condition, String messageText) {
        init(condition);
        message = messageText;
    }

    /**
     * Creates a new  error with the specified code and no message.
     *
     * @param code the error code.
     * @deprecated new errors should be created using the constructor XMPPError(condition)
     */
    @Deprecated
	public XMPPError(int code) {
        this.code = code;
        message = null;
    }

    /**
     * Creates a new error with the specified code and message.
     * deprecated
     *
     * @param code the error code.
     * @param message a message describing the error.
     * @deprecated new errors should be created using the constructor XMPPError(condition, message)
     */
    @Deprecated
	public XMPPError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Creates a new error with the specified code, type, condition and message.
     * This constructor is used when the condition is not recognized automatically by XMPPError
     * i.e. there is not a defined instance of ErrorCondition or it does not applies the default
     * specification.
     *
     * @param code the error code.
     * @param type the error type.
     * @param condition the error condition.
     * @param message a message describing the error.
     * @param extension list of packet extensions
     */
    public XMPPError(int code, XMPPError.Type type, String condition, String message,
            List<PacketExtension> extension) {
        this.code = code;
        this.type = type;
        this.condition = condition;
        this.message = message;
        applicationExtensions = extension;
    }

    /**
     * Initialize the error infering the type and code for the received condition.
     *
     * @param condition the error condition.
     */
    private void init(XMPPError.Condition condition) {
        // Look for the condition and its default code and type
        XMPPError.ErrorSpecification defaultErrorSpecification = XMPPError.ErrorSpecification.specFor(condition);
        this.condition = condition.value;
        if (defaultErrorSpecification != null) {
            // If there is a default error specification for the received condition,
            // it get configured with the infered type and code.
            type = defaultErrorSpecification.getType();
            code = defaultErrorSpecification.getCode();
        }
    }
    /**
     * Returns the error condition.
     *
     * @return the error condition.
     */
    public String getCondition() {
        return this.condition;
    }

    /**
     * Returns the error type.
     *
     * @return the error type.
     */
    public XMPPError.Type getType() {
        return this.type;
    }

    /**
     * Returns the error code.
     *
     * @return the error code.
     */
    public int getCode() {
        return this.code;
    }

    /**
     * Returns the message describing the error, or null if there is no message.
     *
     * @return the message describing the error, or null if there is no message.
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Returns the error as XML.
     *
     * @return the error as XML.
     */
    public String toXML() {
        StringBuilder buf = new StringBuilder();
        buf.append("<error code=\"").append(this.code).append("\"");
        if (this.type != null) {
            buf.append(" type=\"");
            buf.append(this.type.name());
            buf.append("\"");
        }
        buf.append(">");
        if (this.condition != null) {
            buf.append("<").append(this.condition);
            buf.append(" xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\"/>");
        }
        if (this.message != null) {
            buf.append("<text xml:lang=\"en\" xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\">");
            buf.append(this.message);
            buf.append("</text>");
        }
        for (PacketExtension element : getExtensions()) {
            buf.append(element.toXML());
        }
        buf.append("</error>");
        return buf.toString();
    }

    @Override
	public String toString() {
        StringBuilder txt = new StringBuilder();
        if (this.condition != null) {
            txt.append(this.condition);
        }
        txt.append("(").append(this.code).append(")");
        if (this.message != null) {
            txt.append(" ").append(this.message);
        }
        return txt.toString();
    }

    /**
     * Returns an Iterator for the error extensions attached to the xmppError.
     * An application MAY provide application-specific error information by including a
     * properly-namespaced child in the error element.
     *
     * @return an Iterator for the error extensions.
     */
    public synchronized List<PacketExtension> getExtensions() {
        if (this.applicationExtensions == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(this.applicationExtensions);
    }

    /**
     * Returns the first patcket extension that matches the specified element name and
     * namespace, or <tt>null</tt> if it doesn't exist.
     *
     * @param elementName the XML element name of the packet extension.
     * @param namespace the XML element namespace of the packet extension.
     * @return the extension, or <tt>null</tt> if it doesn't exist.
     */
    public synchronized PacketExtension getExtension(String elementName, String namespace) {
        if (this.applicationExtensions == null || elementName == null || namespace == null) {
            return null;
        }
        for (PacketExtension ext : this.applicationExtensions) {
            if (elementName.equals(ext.getElementName()) && namespace.equals(ext.getNamespace())) {
                return ext;
            }
        }
        return null;
    }

    /**
     * Adds a packet extension to the error.
     *
     * @param extension a packet extension.
     */
    public synchronized void addExtension(PacketExtension extension) {
        if (this.applicationExtensions == null) {
            this.applicationExtensions = new ArrayList<PacketExtension>();
        }
        this.applicationExtensions.add(extension);
    }

    /**
     * Set the packet extension to the error.
     *
     * @param extension a packet extension.
     */
    public synchronized void setExtension(List<PacketExtension> extension) {
        this.applicationExtensions = extension;
    }

    /**
     * A class to represent the type of the Error. The types are:
     *
     * <ul>
     *      <li>XMPPError.Type.WAIT - retry after waiting (the error is temporary)
     *      <li>XMPPError.Type.CANCEL - do not retry (the error is unrecoverable)
     *      <li>XMPPError.Type.MODIFY - retry after changing the data sent
     *      <li>XMPPError.Type.AUTH - retry after providing credentials
     *      <li>XMPPError.Type.CONTINUE - proceed (the condition was only a warning)
     * </ul>
     */
    public enum Type {
        WAIT,
        CANCEL,
        MODIFY,
        AUTH,
        CONTINUE
    }

    /**
     * A class to represent predefined error conditions.
     */
    public static class Condition {

        public static final XMPPError.Condition interna_server_error = new XMPPError.Condition("internal-server-error");
        public static final XMPPError.Condition forbidden = new XMPPError.Condition("forbidden");
        public static final XMPPError.Condition bad_request = new XMPPError.Condition("bad-request");
        public static final XMPPError.Condition conflict = new XMPPError.Condition("conflict");
        public static final XMPPError.Condition feature_not_implemented = new XMPPError.Condition("feature-not-implemented");
        public static final XMPPError.Condition gone = new XMPPError.Condition("gone");
        public static final XMPPError.Condition item_not_found = new XMPPError.Condition("item-not-found");
        public static final XMPPError.Condition jid_malformed = new XMPPError.Condition("jid-malformed");
        public static final XMPPError.Condition no_acceptable = new XMPPError.Condition("not-acceptable");
        public static final XMPPError.Condition not_allowed = new XMPPError.Condition("not-allowed");
        public static final XMPPError.Condition not_authorized = new XMPPError.Condition("not-authorized");
        public static final XMPPError.Condition payment_required = new XMPPError.Condition("payment-required");
        public static final XMPPError.Condition recipient_unavailable = new XMPPError.Condition("recipient-unavailable");
        public static final XMPPError.Condition redirect = new XMPPError.Condition("redirect");
        public static final XMPPError.Condition registration_required = new XMPPError.Condition("registration-required");
        public static final XMPPError.Condition remote_server_error = new XMPPError.Condition("remote-server-error");
        public static final XMPPError.Condition remote_server_not_found = new XMPPError.Condition("remote-server-not-found");
        public static final XMPPError.Condition remote_server_timeout = new XMPPError.Condition("remote-server-timeout");
        public static final XMPPError.Condition resource_constraint = new XMPPError.Condition("resource-constraint");
        public static final XMPPError.Condition service_unavailable = new XMPPError.Condition("service-unavailable");
        public static final XMPPError.Condition subscription_required = new XMPPError.Condition("subscription-required");
        public static final XMPPError.Condition undefined_condition = new XMPPError.Condition("undefined-condition");
        public static final XMPPError.Condition unexpected_request = new XMPPError.Condition("unexpected-request");
        public static final XMPPError.Condition request_timeout = new XMPPError.Condition("request-timeout");

        private final String value;

        public Condition(String value) {
            this.value = value;
        }

        @Override
		public String toString() {
            return this.value;
        }
    }


    /**
     * A class to represent the error specification used to infer common usage.
     */
    private static class ErrorSpecification {
        private final int code;
        private final XMPPError.Type type;
        private XMPPError.Condition condition;
        private static final Map<XMPPError.Condition, XMPPError.ErrorSpecification> instances = XMPPError.ErrorSpecification
                .errorSpecifications();

        private ErrorSpecification(XMPPError.Condition condition, XMPPError.Type type, int code) {
            this.code = code;
            this.type = type;
            this.condition = condition;
        }

        private static Map<XMPPError.Condition, XMPPError.ErrorSpecification> errorSpecifications() {
            Map<XMPPError.Condition, XMPPError.ErrorSpecification> instances = new HashMap<XMPPError.Condition, XMPPError.ErrorSpecification>(22);
            instances.put(XMPPError.Condition.interna_server_error, new XMPPError.ErrorSpecification(
                    XMPPError.Condition.interna_server_error, XMPPError.Type.WAIT, 500));
            instances.put(XMPPError.Condition.forbidden, new XMPPError.ErrorSpecification(XMPPError.Condition.forbidden,
                    XMPPError.Type.AUTH, 403));
            instances.put(XMPPError.Condition.bad_request, new XMPPError.ErrorSpecification(
                    XMPPError.Condition.bad_request, XMPPError.Type.MODIFY, 400));
            instances.put(XMPPError.Condition.item_not_found, new XMPPError.ErrorSpecification(
                    XMPPError.Condition.item_not_found, XMPPError.Type.CANCEL, 404));
            instances.put(XMPPError.Condition.conflict, new XMPPError.ErrorSpecification(
                    XMPPError.Condition.conflict, XMPPError.Type.CANCEL, 409));
            instances.put(XMPPError.Condition.feature_not_implemented, new XMPPError.ErrorSpecification(
                    XMPPError.Condition.feature_not_implemented, XMPPError.Type.CANCEL, 501));
            instances.put(XMPPError.Condition.gone, new XMPPError.ErrorSpecification(
                    XMPPError.Condition.gone, XMPPError.Type.MODIFY, 302));
            instances.put(XMPPError.Condition.jid_malformed, new XMPPError.ErrorSpecification(
                    XMPPError.Condition.jid_malformed, XMPPError.Type.MODIFY, 400));
            instances.put(XMPPError.Condition.no_acceptable, new XMPPError.ErrorSpecification(
                    XMPPError.Condition.no_acceptable, XMPPError.Type.MODIFY, 406));
            instances.put(XMPPError.Condition.not_allowed, new XMPPError.ErrorSpecification(
                    XMPPError.Condition.not_allowed, XMPPError.Type.CANCEL, 405));
            instances.put(XMPPError.Condition.not_authorized, new XMPPError.ErrorSpecification(
                    XMPPError.Condition.not_authorized, XMPPError.Type.AUTH, 401));
            instances.put(XMPPError.Condition.payment_required, new XMPPError.ErrorSpecification(
                    XMPPError.Condition.payment_required, XMPPError.Type.AUTH, 402));
            instances.put(XMPPError.Condition.recipient_unavailable, new XMPPError.ErrorSpecification(
                    XMPPError.Condition.recipient_unavailable, XMPPError.Type.WAIT, 404));
            instances.put(XMPPError.Condition.redirect, new XMPPError.ErrorSpecification(
                    XMPPError.Condition.redirect, XMPPError.Type.MODIFY, 302));
            instances.put(XMPPError.Condition.registration_required, new XMPPError.ErrorSpecification(
                    XMPPError.Condition.registration_required, XMPPError.Type.AUTH, 407));
            instances.put(XMPPError.Condition.remote_server_not_found, new XMPPError.ErrorSpecification(
                    XMPPError.Condition.remote_server_not_found, XMPPError.Type.CANCEL, 404));
            instances.put(XMPPError.Condition.remote_server_timeout, new XMPPError.ErrorSpecification(
                    XMPPError.Condition.remote_server_timeout, XMPPError.Type.WAIT, 504));
            instances.put(XMPPError.Condition.remote_server_error, new XMPPError.ErrorSpecification(
                    XMPPError.Condition.remote_server_error, XMPPError.Type.CANCEL, 502));
            instances.put(XMPPError.Condition.resource_constraint, new XMPPError.ErrorSpecification(
                    XMPPError.Condition.resource_constraint, XMPPError.Type.WAIT, 500));
            instances.put(XMPPError.Condition.service_unavailable, new XMPPError.ErrorSpecification(
                    XMPPError.Condition.service_unavailable, XMPPError.Type.CANCEL, 503));
            instances.put(XMPPError.Condition.subscription_required, new XMPPError.ErrorSpecification(
                    XMPPError.Condition.subscription_required, XMPPError.Type.AUTH, 407));
            instances.put(XMPPError.Condition.undefined_condition, new XMPPError.ErrorSpecification(
                    XMPPError.Condition.undefined_condition, XMPPError.Type.WAIT, 500));
            instances.put(XMPPError.Condition.unexpected_request, new XMPPError.ErrorSpecification(
                    XMPPError.Condition.unexpected_request, XMPPError.Type.WAIT, 400));
            instances.put(XMPPError.Condition.request_timeout, new XMPPError.ErrorSpecification(
                    XMPPError.Condition.request_timeout, XMPPError.Type.CANCEL, 408));

            return instances;
        }

        protected static XMPPError.ErrorSpecification specFor(XMPPError.Condition condition) {
            return XMPPError.ErrorSpecification.instances.get(condition);
        }

        /**
         * Returns the error condition.
         *
         * @return the error condition.
         */
        protected XMPPError.Condition getCondition() {
            return this.condition;
        }

        /**
         * Returns the error type.
         *
         * @return the error type.
         */
        protected XMPPError.Type getType() {
            return this.type;
        }

        /**
         * Returns the error code.
         *
         * @return the error code.
         */
        protected int getCode() {
            return this.code;
        }
    }
}
