// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.util.*;
import java.io.*;
import java.net.*;

/**
 * An implementation of Resolver that sends one query to one server.
 * SimpleResolver handles TCP retries, transaction security (TSIG), and
 * EDNS 0.
 * @see Resolver
 * @see TSIG
 * @see OPTRecord
 *
 * @author Brian Wellington
 */


public class SimpleResolver implements Resolver {

/** The default port to send queries to */
public static final int DEFAULT_PORT = 53;

/** The default EDNS payload size */
public static final int DEFAULT_EDNS_PAYLOADSIZE = 1280;

private InetSocketAddress address;
private InetSocketAddress localAddress;
private boolean useTCP, ignoreTruncation;
private OPTRecord queryOPT;
private TSIG tsig;
private long timeoutValue = 10 * 1000;

private static final short DEFAULT_UDPSIZE = 512;

private static String defaultResolver = "localhost";
private static int uniqueID;

/**
 * Creates a SimpleResolver that will query the specified host 
 * @exception UnknownHostException Failure occurred while finding the host
 */
public
SimpleResolver(String hostname) throws UnknownHostException {
	if (hostname == null) {
		hostname = ResolverConfig.getCurrentConfig().server();
		if (hostname == null)
			hostname = SimpleResolver.defaultResolver;
	}
	InetAddress addr;
	if (hostname.equals("0"))
		addr = InetAddress.getLocalHost();
	else
		addr = InetAddress.getByName(hostname);
    this.address = new InetSocketAddress(addr, SimpleResolver.DEFAULT_PORT);
}

/**
 * Creates a SimpleResolver.  The host to query is either found by using
 * ResolverConfig, or the default host is used.
 * @see ResolverConfig
 * @exception UnknownHostException Failure occurred while finding the host
 */
public
SimpleResolver() throws UnknownHostException {
	this(null);
}

InetSocketAddress
getAddress() {
	return this.address;
}

/** Sets the default host (initially localhost) to query */
public static void
setDefaultResolver(String hostname) {
    SimpleResolver.defaultResolver = hostname;
}

@Override
public void
setPort(int port) {
    this.address = new InetSocketAddress(this.address.getAddress(), port);
}

/**
 * Sets the address of the server to communicate with.
 * @param addr The address of the DNS server
 */
public void
setAddress(InetSocketAddress addr) {
    this.address = addr;
}

/**
 * Sets the address of the server to communicate with (on the default
 * DNS port)
 * @param addr The address of the DNS server
 */
public void
setAddress(InetAddress addr) {
    this.address = new InetSocketAddress(addr, this.address.getPort());
}

/**
 * Sets the local address to bind to when sending messages.
 * @param addr The local address to send messages from.
 */
public void
setLocalAddress(InetSocketAddress addr) {
    this.localAddress = addr;
}

/**
 * Sets the local address to bind to when sending messages.  A random port
 * will be used.
 * @param addr The local address to send messages from.
 */
public void
setLocalAddress(InetAddress addr) {
    this.localAddress = new InetSocketAddress(addr, 0);
}

@Override
public void
setTCP(boolean flag) {
    useTCP = flag;
}

@Override
public void
setIgnoreTruncation(boolean flag) {
    ignoreTruncation = flag;
}

@Override
public void
setEDNS(int level, int payloadSize, int flags, List options) {
	if (level != 0 && level != -1)
		throw new IllegalArgumentException("invalid EDNS level - " +
						   "must be 0 or -1");
	if (payloadSize == 0)
		payloadSize = SimpleResolver.DEFAULT_EDNS_PAYLOADSIZE;
    this.queryOPT = new OPTRecord(payloadSize, 0, level, flags, options);
}

@Override
public void
setEDNS(int level) {
    this.setEDNS(level, 0, 0, null);
}

@Override
public void
setTSIGKey(TSIG key) {
    this.tsig = key;
}

TSIG
getTSIGKey() {
	return this.tsig;
}

@Override
public void
setTimeout(int secs, int msecs) {
    this.timeoutValue = (long)secs * 1000 + msecs;
}

@Override
public void
setTimeout(int secs) {
    this.setTimeout(secs, 0);
}

long
getTimeout() {
	return this.timeoutValue;
}

private Message
parseMessage(byte [] b) throws WireParseException {
	try {
		return new Message(b);
	}
	catch (IOException e) {
		if (Options.check("verbose"))
			e.printStackTrace();
		if (!(e instanceof WireParseException))
			e = new WireParseException("Error parsing message");
		throw (WireParseException) e;
	}
}

private void
verifyTSIG(Message query, Message response, byte [] b, TSIG tsig) {
	if (tsig == null)
		return;
	int error = tsig.verify(response, b, query.getTSIG());
	if (Options.check("verbose"))
		System.err.println("TSIG verify: " + Rcode.string(error));
}

private void
applyEDNS(Message query) {
	if (this.queryOPT == null || query.getOPT() != null)
		return;
	query.addRecord(this.queryOPT, Section.ADDITIONAL);
}

private int
maxUDPSize(Message query) {
	OPTRecord opt = query.getOPT();
	if (opt == null)
		return SimpleResolver.DEFAULT_UDPSIZE;
	else
		return opt.getPayloadSize();
}

/**
 * Sends a message to a single server and waits for a response.  No checking
 * is done to ensure that the response is associated with the query.
 * @param query The query to send.
 * @return The response.
 * @throws IOException An error occurred while sending or receiving.
 */
@Override
public Message
send(Message query) throws IOException {
	if (Options.check("verbose"))
		System.err.println("Sending to " +
                this.address.getAddress().getHostAddress() +
				   ":" + this.address.getPort());

	if (query.getHeader().getOpcode() == Opcode.QUERY) {
		Record question = query.getQuestion();
		if (question != null && question.getType() == Type.AXFR)
			return this.sendAXFR(query);
	}

	query = (Message) query.clone();
    this.applyEDNS(query);
	if (this.tsig != null)
        this.tsig.apply(query, null);

	byte [] out = query.toWire(Message.MAXLENGTH);
	int udpSize = this.maxUDPSize(query);
	boolean tcp = false;
	long endTime = System.currentTimeMillis() + this.timeoutValue;
	do {
		byte [] in;

		if (this.useTCP || out.length > udpSize)
			tcp = true;
		if (tcp)
			in = TCPClient.sendrecv(this.localAddress, this.address, out,
						endTime);
		else
			in = UDPClient.sendrecv(this.localAddress, this.address, out,
						udpSize, endTime);

		/*
		 * Check that the response is long enough.
		 */
		if (in.length < Header.LENGTH) {
			throw new WireParseException("invalid DNS header - " +
						     "too short");
		}
		/*
		 * Check that the response ID matches the query ID.  We want
		 * to check this before actually parsing the message, so that
		 * if there's a malformed response that's not ours, it
		 * doesn't confuse us.
		 */
		int id = ((in[0] & 0xFF) << 8) + (in[1] & 0xFF);
		int qid = query.getHeader().getID();
		if (id != qid) {
			String error = "invalid message id: expected " + qid +
				       "; got id " + id;
			if (tcp) {
				throw new WireParseException(error);
			} else {
				if (Options.check("verbose")) {
					System.err.println(error);
				}
				continue;
			}
		}
		Message response = this.parseMessage(in);
        this.verifyTSIG(query, response, in, this.tsig);
		if (!tcp && !this.ignoreTruncation &&
		    response.getHeader().getFlag(Flags.TC))
		{
			tcp = true;
			continue;
		}
		return response;
	} while (true);
}

/**
 * Asynchronously sends a message to a single server, registering a listener
 * to receive a callback on success or exception.  Multiple asynchronous
 * lookups can be performed in parallel.  Since the callback may be invoked
 * before the function returns, external synchronization is necessary.
 * @param query The query to send
 * @param listener The object containing the callbacks.
 * @return An identifier, which is also a parameter in the callback
 */
@Override
public Object
sendAsync(Message query, ResolverListener listener) {
	Object id;
	synchronized (this) {
		id = new Integer(SimpleResolver.uniqueID++);
	}
	Record question = query.getQuestion();
	String qname;
	if (question != null)
		qname = question.getName().toString();
	else
		qname = "(none)";
	String name = getClass() + ": " + qname;
	Thread thread = new ResolveThread(this, query, id, listener);
	thread.setName(name);
	thread.setDaemon(true);
	thread.start();
	return id;
}

private Message
sendAXFR(Message query) throws IOException {
	Name qname = query.getQuestion().getName();
	ZoneTransferIn xfrin = ZoneTransferIn.newAXFR(qname, this.address, this.tsig);
	xfrin.setTimeout((int)(this.getTimeout() / 1000));
	xfrin.setLocalAddress(this.localAddress);
	try {
		xfrin.run();
	}
	catch (ZoneTransferException e) {
		throw new WireParseException(e.getMessage());
	}
	List records = xfrin.getAXFR();
	Message response = new Message(query.getHeader().getID());
	response.getHeader().setFlag(Flags.AA);
	response.getHeader().setFlag(Flags.QR);
	response.addRecord(query.getQuestion(), Section.QUESTION);
	Iterator it = records.iterator();
	while (it.hasNext())
		response.addRecord((Record)it.next(), Section.ANSWER);
	return response;
}

}
