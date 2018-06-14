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

package org.jivesoftware.smack;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.packet.XMPPError.Condition;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * Listens for XML traffic from the XMPP server and parses it into packet objects.
 * The packet reader also invokes all packet listeners and collectors.<p>
 *
 * @see Connection#createPacketCollector
 * @see Connection#addPacketListener
 * @author Matt Tucker
 */
class PacketReader {

    private Thread readerThread;
    private ExecutorService listenerExecutor;

    private final XMPPConnection connection;
    private XmlPullParser parser;
    private boolean done;

    private String connectionID;
    private Semaphore connectionSemaphore;
    
    protected PacketReader(XMPPConnection connection) {
        this.connection = connection;
        init();
    }

    /**
     * Initializes the reader in order to be used. The reader is initialized during the
     * first connection and when reconnecting due to an abruptly disconnection.
     */
    protected void init() {
        this.done = false;
        this.connectionID = null;

        this.readerThread = new Thread() {
            @Override
			public void run() {
                PacketReader.this.parsePackets(this);
            }
        };
        this.readerThread.setName("Smack Packet Reader (" + this.connection.connectionCounterValue + ")");
        this.readerThread.setDaemon(true);

        this.listenerExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {

            @Override
			public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable,
                        "Smack Listener Processor (" + PacketReader.this.connection.connectionCounterValue + ")");
                thread.setDaemon(true);
                return thread;
            }
        });

        this.resetParser();
    }
    
/*    public void startKeepHeartBeat(XmppManager xmppManager){
    	beginTime = System.currentTimeMillis();
    	this.xmppManager = xmppManager;
    	keepAlive.start();
    }*/

    /**
     * Starts the packet reader thread and returns once a connection to the server
     * has been established. A connection will be attempted for a maximum of five
     * seconds. An XMPPException will be thrown if the connection fails.
     *
     * @throws XMPPException if the server fails to send an opening stream back
     *      for more than five seconds.
     */
    public void startup() throws XMPPException {
        this.connectionSemaphore = new Semaphore(1);

        this.readerThread.start();
        // Wait for stream tag before returing. We'll wait a couple of seconds before
        // giving up and throwing an error.
        try {
            this.connectionSemaphore.acquire();

            // A waiting thread may be woken up before the wait time or a notify
            // (although this is a rare thing). Therefore, we continue waiting
            // until either a connectionID has been set (and hence a notify was
            // made) or the total wait time has elapsed.
            int waitTime = SmackConfiguration.getPacketReplyTimeout();
            this.connectionSemaphore.tryAcquire(waitTime, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException ie) {
            // Ignore.
        }
        if (this.connectionID == null) {
            throw new XMPPException("Connection failed. No response from server.");
        }
        else {
            this.connection.connectionID = this.connectionID;
        }
    }

    /**
     * Shuts the packet reader down.
     */
    public void shutdown() {
        // Notify connection listeners of the connection closing if done hasn't already been set.
        if (!this.done) {
            for (ConnectionListener listener : this.connection.getConnectionListeners()) {
                try {
                    listener.connectionClosed();
                }
                catch (Exception e) {
                    // Cath and print any exception so we can recover
                    // from a faulty listener and finish the shutdown process
                    e.printStackTrace();
                }
            }
        }
        this.done = true;

        // Shut down the listener executor.
        this.listenerExecutor.shutdown();
    }

    /**
     * Cleans up all resources used by the packet reader.
     */
    void cleanup() {
        this.connection.recvListeners.clear();
        this.connection.collectors.clear();
    }

    /**
     * Sends out a notification that there was an error with the connection
     * and closes the connection.
     *
     * @param e the exception that causes the connection close event.
     */
    void notifyConnectionError(Exception e) {
        this.done = true;
        // Closes the connection temporary. A reconnection is possible
        this.connection.shutdown(new Presence(Type.unavailable));
        // Print the stack trace to help catch the problem
        e.printStackTrace();
        // Notify connection listeners of the error.
        for (ConnectionListener listener : this.connection.getConnectionListeners()) {
            try {
                listener.connectionClosedOnError(e);
            }
            catch (Exception e2) {
                // Catch and print any exception so we can recover
                // from a faulty listener
                e2.printStackTrace();
            }
        }
    }

    /**
     * Sends a notification indicating that the connection was reconnected successfully.
     */
    protected void notifyReconnection() {
        // Notify connection listeners of the reconnection.
        for (ConnectionListener listener : this.connection.getConnectionListeners()) {
            try {
                listener.reconnectionSuccessful();
            }
            catch (Exception e) {
                // Catch and print any exception so we can recover
                // from a faulty listener
                e.printStackTrace();
            }
        }
    }

    /**
     * Resets the parser using the latest connection's reader. Reseting the parser is necessary
     * when the plain connection has been secured or when a new opening stream element is going
     * to be sent by the server.
     */
    private void resetParser() {
        try {
            this.parser = XmlPullParserFactory.newInstance().newPullParser();
            this.parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            this.parser.setInput(this.connection.reader);
        }
        catch (XmlPullParserException xppe) {
            xppe.printStackTrace();
        }
    }

    /**
     * Parse top-level packets in order to process them further.
     *
     * @param thread the thread that is being used by the reader to parse incoming packets.
     */
    private void parsePackets(Thread thread) {
        try {
            int eventType = this.parser.getEventType();
            do {
                if (eventType == XmlPullParser.START_TAG) {
                    if (this.parser.getName().equals("message")) {
                        this.processPacket(PacketParserUtils.parseMessage(this.parser));
                    }
                    else if (this.parser.getName().equals("iq")) {
                        this.processPacket(PacketParserUtils.parseIQ(this.parser, this.connection));
                    }
                    else if (this.parser.getName().equals("presence")) {
                        this.processPacket(PacketParserUtils.parsePresence(this.parser));
                    }
                    // We found an opening stream. Record information about it, then notify
                    // the connectionID lock so that the packet reader startup can finish.
                    else if (this.parser.getName().equals("stream")) {
                        // Ensure the correct jabber:client namespace is being used.
                        if ("jabber:client".equals(this.parser.getNamespace(null))) {
                            // Get the connection id.
                            for (int i = 0; i< this.parser.getAttributeCount(); i++) {
                                if (this.parser.getAttributeName(i).equals("id")) {
                                    // Save the connectionID
                                    this.connectionID = this.parser.getAttributeValue(i);
                                    if (!"1.0".equals(this.parser.getAttributeValue("", "version"))) {
                                        // Notify that a stream has been opened if the
                                        // server is not XMPP 1.0 compliant otherwise make the
                                        // notification after TLS has been negotiated or if TLS
                                        // is not supported
                                        this.releaseConnectionIDLock();
                                    }
                                }
                                else if (this.parser.getAttributeName(i).equals("from")) {
                                    // Use the server name that the server says that it is.
                                    this.connection.config.setServiceName(this.parser.getAttributeValue(i));
                                }
                            }
                        }
                    }
                    else if (this.parser.getName().equals("error")) {
                        throw new XMPPException(PacketParserUtils.parseStreamError(this.parser));
                    }
                    else if (this.parser.getName().equals("features")) {
                        this.parseFeatures(this.parser);
                    }
                    else if (this.parser.getName().equals("proceed")) {
                        // Secure the connection by negotiating TLS
                        this.connection.proceedTLSReceived();
                        // Reset the state of the parser since a new stream element is going
                        // to be sent by the server
                        this.resetParser();
                    }
                    else if (this.parser.getName().equals("failure")) {
                        String namespace = this.parser.getNamespace(null);
                        if ("urn:ietf:params:xml:ns:xmpp-tls".equals(namespace)) {
                            // TLS negotiation has failed. The server will close the connection
                            throw new Exception("TLS negotiation has failed");
                        }
                        else if ("http://jabber.org/protocol/compress".equals(namespace)) {
                            // Stream compression has been denied. This is a recoverable
                            // situation. It is still possible to authenticate and
                            // use the connection but using an uncompressed connection
                            this.connection.streamCompressionDenied();
                        }
                        else {
                            // SASL authentication has failed. The server may close the connection
                            // depending on the number of retries
                            SASLMechanism.Failure failure = PacketParserUtils.parseSASLFailure(this.parser);
                            this.processPacket(failure);
                            this.connection.getSASLAuthentication().authenticationFailed();
                        }
                    }
                    else if (this.parser.getName().equals("challenge")) {
                        // The server is challenging the SASL authentication made by the client
                        String challengeData = this.parser.nextText();
                        this.processPacket(new SASLMechanism.Challenge(challengeData));
                        this.connection.getSASLAuthentication().challengeReceived(challengeData);
                    }
                    else if (this.parser.getName().equals("success")) {
                        this.processPacket(new SASLMechanism.Success(this.parser.nextText()));
                        // We now need to bind a resource for the connection
                        // Open a new stream and wait for the response
                        this.connection.packetWriter.openStream();
                        // Reset the state of the parser since a new stream element is going
                        // to be sent by the server
                        this.resetParser();
                        // The SASL authentication with the server was successful. The next step
                        // will be to bind the resource
                        this.connection.getSASLAuthentication().authenticated();
                    }
                    else if (this.parser.getName().equals("compressed")) {
                        // Server confirmed that it's possible to use stream compression. Start
                        // stream compression
                        this.connection.startStreamCompression();
                        // Reset the state of the parser since a new stream element is going
                        // to be sent by the server
                        this.resetParser();
                    }
                }
                else if (eventType == XmlPullParser.END_TAG) {
                    if (this.parser.getName().equals("stream")) {
                        // Disconnect the connection
                        this.connection.disconnect();
                    }
                }
                eventType = this.parser.next();
            } while (!this.done && eventType != XmlPullParser.END_DOCUMENT && thread == this.readerThread);
        }
        catch (Exception e) {
            if (!this.done) {
                // Close the connection and notify connection listeners of the
                // error.
                this.notifyConnectionError(e);
            }
        }
    }

	/**
     * Releases the connection ID lock so that the thread that was waiting can resume. The
     * lock will be released when one of the following three conditions is met:<p>
     *
     * 1) An opening stream was sent from a non XMPP 1.0 compliant server
     * 2) Stream features were received from an XMPP 1.0 compliant server that does not support TLS
     * 3) TLS negotiation was successful
     *
     */
    private void releaseConnectionIDLock() {
        this.connectionSemaphore.release();
    }

    /**
     * Processes a packet after it's been fully parsed by looping through the installed
     * packet collectors and listeners and letting them examine the packet to see if
     * they are a match with the filter.
     *
     * @param packet the packet to process.
     */
    private void processPacket(Packet packet) {
        if (packet == null) {
            return;
        }

        // Loop through all collectors and notify the appropriate ones.
        for (PacketCollector collector: this.connection.getPacketCollectors()) {
            collector.processPacket(packet);
        }

        // Deliver the incoming packet to listeners.
        this.listenerExecutor.submit(new ListenerNotification(packet));
    }

    private void parseFeatures(XmlPullParser parser) throws Exception {
        boolean startTLSReceived = false;
        boolean startTLSRequired = false;
        boolean done = false;
        while (!done) {
            int eventType = parser.next();

            if (eventType == XmlPullParser.START_TAG) {
                if (parser.getName().equals("starttls")) {
                    startTLSReceived = true;
                }
                else if (parser.getName().equals("mechanisms")) {
                    // The server is reporting available SASL mechanisms. Store this information
                    // which will be used later while logging (i.e. authenticating) into
                    // the server
                    this.connection.getSASLAuthentication()
                            .setAvailableSASLMethods(PacketParserUtils.parseMechanisms(parser));
                }
                else if (parser.getName().equals("bind")) {
                    // The server requires the client to bind a resource to the stream
                    this.connection.getSASLAuthentication().bindingRequired();
                }
                else if(parser.getName().equals("ver")){
                    this.connection.getConfiguration().setRosterVersioningAvailable(true);
                }
                else if(parser.getName().equals("c")){
                	String node = parser.getAttributeValue(null, "node");
                	String ver = parser.getAttributeValue(null, "ver");
                	String capsNode = node+"#"+ver;
                    this.connection.getConfiguration().setCapsNode(capsNode);
                }
                else if (parser.getName().equals("session")) {
                    // The server supports sessions
                    this.connection.getSASLAuthentication().sessionsSupported();
                }
                else if (parser.getName().equals("compression")) {
                    // The server supports stream compression
                    this.connection.setAvailableCompressionMethods(PacketParserUtils.parseCompressionMethods(parser));
                }
                else if (parser.getName().equals("register")) {
                    this.connection.getAccountManager().setSupportsAccountCreation(true);
                }
            }
            else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals("starttls")) {
                    // Confirm the server that we want to use TLS
                    this.connection.startTLSReceived(startTLSRequired);
                }
                else if (parser.getName().equals("required") && startTLSReceived) {
                    startTLSRequired = true;
                }
                else if (parser.getName().equals("features")) {
                    done = true;
                }
            }
        }

        // If TLS is required but the server doesn't offer it, disconnect
        // from the server and throw an error. First check if we've already negotiated TLS
        // and are secure, however (features get parsed a second time after TLS is established).
        if (!this.connection.isSecureConnection()) {
            if (!startTLSReceived && this.connection.getConfiguration().getSecurityMode() ==
                    SecurityMode.required)
            {
                throw new XMPPException("Server does not support security (TLS), " +
                        "but security required by connection configuration.",
                        new XMPPError(Condition.forbidden));
            }
        }
        
        // Release the lock after TLS has been negotiated or we are not insterested in TLS
        if (!startTLSReceived || this.connection.getConfiguration().getSecurityMode() ==
                SecurityMode.disabled)
        {
            this.releaseConnectionIDLock();
        }
    }

    /**
     * A runnable to notify all listeners of a packet.
     */
    private class ListenerNotification implements Runnable {

        private final Packet packet;

        public ListenerNotification(Packet packet) {
            this.packet = packet;
        }

        @Override
		public void run() {
            for (Connection.ListenerWrapper listenerWrapper : PacketReader.this.connection.recvListeners.values()) {
                listenerWrapper.notifyListener(this.packet);
            }
        }
    }
}
