// Copyright (c) 2003-2004 Brian Wellington (bwelling@xbill.org)
// Parts of this are derived from lib/dns/xfrin.c from BIND 9; its copyright
// notice follows.

/*
 * Copyright (C) 1999-2001  Internet Software Consortium.
 *
 * Permission to use, copy, modify, and distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND INTERNET SOFTWARE CONSORTIUM
 * DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL
 * INTERNET SOFTWARE CONSORTIUM BE LIABLE FOR ANY SPECIAL, DIRECT,
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING
 * FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT,
 * NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION
 * WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package org.xbill.DNS;

import org.xbill.DNS.TSIG.StreamVerifier;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * An incoming DNS Zone Transfer.  To use this class, first initialize an
 * object, then call the run() method.  If run() doesn't throw an exception
 * the result will either be an IXFR-style response, an AXFR-style response,
 * or an indication that the zone is up to date.
 *
 * @author Brian Wellington
 */

public class ZoneTransferIn {

private static final int INITIALSOA	= 0;
private static final int FIRSTDATA	= 1;
private static final int IXFR_DELSOA	= 2;
private static final int IXFR_DEL	= 3;
private static final int IXFR_ADDSOA	= 4;
private static final int IXFR_ADD	= 5;
private static final int AXFR		= 6;
private static final int END		= 7;

private Name zname;
private int qtype;
private int dclass;
private long ixfr_serial;
private boolean want_fallback;

private SocketAddress localAddress;
private SocketAddress address;
private TCPClient client;
private TSIG tsig;
private StreamVerifier verifier;
private long timeout = 900 * 1000;

private int state;
private long end_serial;
private long current_serial;
private Record initialsoa;

private int rtype;

private List axfr;
private List ixfr;

public static class Delta {
	/**
	 * All changes between two versions of a zone in an IXFR response.
	 */

	/** The starting serial number of this delta. */
	public long start;

	/** The ending serial number of this delta. */
	public long end;

	/** A list of records added between the start and end versions */
	public List adds;

	/** A list of records deleted between the start and end versions */
	public List deletes;

	private
	Delta() {
        this.adds = new ArrayList();
        this.deletes = new ArrayList();
	}
}

private
ZoneTransferIn() {}

private
ZoneTransferIn(Name zone, int xfrtype, long serial, boolean fallback,
	       SocketAddress address, TSIG key)
{
	this.address = address;
    tsig = key;
	if (zone.isAbsolute())
        this.zname = zone;
	else {
		try {
            this.zname = Name.concatenate(zone, Name.root);
		}
		catch (NameTooLongException e) {
			throw new IllegalArgumentException("ZoneTransferIn: " +
							   "name too long");
		}
	}
    this.qtype = xfrtype;
    this.dclass = DClass.IN;
    this.ixfr_serial = serial;
    this.want_fallback = fallback;
    this.state = ZoneTransferIn.INITIALSOA;
}

/**
 * Instantiates a ZoneTransferIn object to do an AXFR (full zone transfer).
 * @param zone The zone to transfer.
 * @param address The host/port from which to transfer the zone.
 * @param key The TSIG key used to authenticate the transfer, or null.
 * @return The ZoneTransferIn object.
 * @throws UnknownHostException The host does not exist.
 */
public static ZoneTransferIn
newAXFR(Name zone, SocketAddress address, TSIG key) {
	return new ZoneTransferIn(zone, Type.AXFR, 0, false, address, key);
}

/**
 * Instantiates a ZoneTransferIn object to do an AXFR (full zone transfer).
 * @param zone The zone to transfer.
 * @param host The host from which to transfer the zone.
 * @param port The port to connect to on the server, or 0 for the default.
 * @param key The TSIG key used to authenticate the transfer, or null.
 * @return The ZoneTransferIn object.
 * @throws UnknownHostException The host does not exist.
 */
public static ZoneTransferIn
newAXFR(Name zone, String host, int port, TSIG key)
throws UnknownHostException
{
	if (port == 0)
		port = SimpleResolver.DEFAULT_PORT;
	return ZoneTransferIn.newAXFR(zone, new InetSocketAddress(host, port), key);
}

/**
 * Instantiates a ZoneTransferIn object to do an AXFR (full zone transfer).
 * @param zone The zone to transfer.
 * @param host The host from which to transfer the zone.
 * @param key The TSIG key used to authenticate the transfer, or null.
 * @return The ZoneTransferIn object.
 * @throws UnknownHostException The host does not exist.
 */
public static ZoneTransferIn
newAXFR(Name zone, String host, TSIG key)
throws UnknownHostException
{
	return ZoneTransferIn.newAXFR(zone, host, 0, key);
}

/**
 * Instantiates a ZoneTransferIn object to do an IXFR (incremental zone
 * transfer).
 * @param zone The zone to transfer.
 * @param serial The existing serial number.
 * @param fallback If true, fall back to AXFR if IXFR is not supported.
 * @param address The host/port from which to transfer the zone.
 * @param key The TSIG key used to authenticate the transfer, or null.
 * @return The ZoneTransferIn object.
 * @throws UnknownHostException The host does not exist.
 */
public static ZoneTransferIn
newIXFR(Name zone, long serial, boolean fallback, SocketAddress address,
	TSIG key)
{
	return new ZoneTransferIn(zone, Type.IXFR, serial, fallback, address,
				  key);
}

/**
 * Instantiates a ZoneTransferIn object to do an IXFR (incremental zone
 * transfer).
 * @param zone The zone to transfer.
 * @param serial The existing serial number.
 * @param fallback If true, fall back to AXFR if IXFR is not supported.
 * @param host The host from which to transfer the zone.
 * @param port The port to connect to on the server, or 0 for the default.
 * @param key The TSIG key used to authenticate the transfer, or null.
 * @return The ZoneTransferIn object.
 * @throws UnknownHostException The host does not exist.
 */
public static ZoneTransferIn
newIXFR(Name zone, long serial, boolean fallback, String host, int port,
	TSIG key)
throws UnknownHostException
{
	if (port == 0)
		port = SimpleResolver.DEFAULT_PORT;
	return ZoneTransferIn.newIXFR(zone, serial, fallback,
		       new InetSocketAddress(host, port), key);
}

/**
 * Instantiates a ZoneTransferIn object to do an IXFR (incremental zone
 * transfer).
 * @param zone The zone to transfer.
 * @param serial The existing serial number.
 * @param fallback If true, fall back to AXFR if IXFR is not supported.
 * @param host The host from which to transfer the zone.
 * @param key The TSIG key used to authenticate the transfer, or null.
 * @return The ZoneTransferIn object.
 * @throws UnknownHostException The host does not exist.
 */
public static ZoneTransferIn
newIXFR(Name zone, long serial, boolean fallback, String host, TSIG key)
throws UnknownHostException
{
	return ZoneTransferIn.newIXFR(zone, serial, fallback, host, 0, key);
}

/**
 * Gets the name of the zone being transferred.
 */
public Name
getName() {
	return this.zname;
}

/**
 * Gets the type of zone transfer (either AXFR or IXFR).
 */
public int
getType() {
	return this.qtype;
}

/**
 * Sets a timeout on this zone transfer.  The default is 900 seconds (15
 * minutes).
 * @param secs The maximum amount of time that this zone transfer can take.
 */
public void
setTimeout(int secs) {
	if (secs < 0)
		throw new IllegalArgumentException("timeout cannot be " +
						   "negative");
    this.timeout = 1000L * secs;
}

/**
 * Sets an alternate DNS class for this zone transfer.
 * @param dclass The class to use instead of class IN.
 */
public void
setDClass(int dclass) {
	DClass.check(dclass);
	this.dclass = dclass;
}

/**
 * Sets the local address to bind to when sending messages.
 * @param addr The local address to send messages from.
 */
public void
setLocalAddress(SocketAddress addr) {
    localAddress = addr;
}

private void
openConnection() throws IOException {
	long endTime = System.currentTimeMillis() + this.timeout;
    this.client = new TCPClient(endTime);
	if (this.localAddress != null)
        this.client.bind(this.localAddress);
    this.client.connect(this.address);
}

private void
sendQuery() throws IOException {
	Record question = Record.newRecord(this.zname, this.qtype, this.dclass);

	Message query = new Message();
	query.getHeader().setOpcode(Opcode.QUERY);
	query.addRecord(question, Section.QUESTION);
	if (this.qtype == Type.IXFR) {
		Record soa = new SOARecord(this.zname, this.dclass, 0, Name.root,
					   Name.root, this.ixfr_serial,
					   0, 0, 0, 0);
		query.addRecord(soa, Section.AUTHORITY);
	}
	if (this.tsig != null) {
        this.tsig.apply(query, null);
        this.verifier = new StreamVerifier(this.tsig, query.getTSIG());
	}
	byte [] out = query.toWire(Message.MAXLENGTH);
    this.client.send(out);
}

private long
getSOASerial(Record rec) {
	SOARecord soa = (SOARecord) rec;
	return soa.getSerial();
}

private void
logxfr(String s) {
	if (Options.check("verbose"))
		System.out.println(this.zname + ": " + s);
}

private void
fail(String s) throws ZoneTransferException {
	throw new ZoneTransferException(s);
}

private void
fallback() throws ZoneTransferException {
	if (!this.want_fallback)
        this.fail("server doesn't support IXFR");

    this.logxfr("falling back to AXFR");
    this.qtype = Type.AXFR;
    this.state = ZoneTransferIn.INITIALSOA;
}

private void
parseRR(Record rec) throws ZoneTransferException {
	int type = rec.getType();
	ZoneTransferIn.Delta delta;

	switch (this.state) {
	case ZoneTransferIn.INITIALSOA:
		if (type != Type.SOA)
            this.fail("missing initial SOA");
        this.initialsoa = rec;
		// Remember the serial number in the initial SOA; we need it
		// to recognize the end of an IXFR.
        this.end_serial = this.getSOASerial(rec);
		if (this.qtype == Type.IXFR && this.end_serial <= this.ixfr_serial) {
            this.logxfr("up to date");
            this.state = ZoneTransferIn.END;
			break;
		}
        this.state = ZoneTransferIn.FIRSTDATA;
		break;

	case ZoneTransferIn.FIRSTDATA:
		// If the transfer begins with 1 SOA, it's an AXFR.
		// If it begins with 2 SOAs, it's an IXFR.
		if (this.qtype == Type.IXFR && type == Type.SOA &&
                this.getSOASerial(rec) == this.ixfr_serial)
		{
            this.rtype = Type.IXFR;
            this.ixfr = new ArrayList();
            this.logxfr("got incremental response");
            this.state = ZoneTransferIn.IXFR_DELSOA;
		} else {
            this.rtype = Type.AXFR;
            this.axfr = new ArrayList();
            this.axfr.add(this.initialsoa);
            this.logxfr("got nonincremental response");
            this.state = ZoneTransferIn.AXFR;
		}
        this.parseRR(rec); // Restart...
		return;

	case ZoneTransferIn.IXFR_DELSOA:
		delta = new ZoneTransferIn.Delta();
        this.ixfr.add(delta);
		delta.start = this.getSOASerial(rec);
		delta.deletes.add(rec);
        this.state = ZoneTransferIn.IXFR_DEL;
		break;

	case ZoneTransferIn.IXFR_DEL:
		if (type == Type.SOA) {
            this.current_serial = this.getSOASerial(rec);
            this.state = ZoneTransferIn.IXFR_ADDSOA;
            this.parseRR(rec); // Restart...
			return;
		}
		delta = (ZoneTransferIn.Delta) this.ixfr.get(this.ixfr.size() - 1);
		delta.deletes.add(rec);
		break;

	case ZoneTransferIn.IXFR_ADDSOA:
		delta = (ZoneTransferIn.Delta) this.ixfr.get(this.ixfr.size() - 1);
		delta.end = this.getSOASerial(rec);
		delta.adds.add(rec);
        this.state = ZoneTransferIn.IXFR_ADD;
		break;

	case ZoneTransferIn.IXFR_ADD:
		if (type == Type.SOA) {
			long soa_serial = this.getSOASerial(rec);
			if (soa_serial == this.end_serial) {
                this.state = ZoneTransferIn.END;
				break;
			} else if (soa_serial != this.current_serial) {
                this.fail("IXFR out of sync: expected serial " +
                        this.current_serial + " , got " + soa_serial);
			} else {
                this.state = ZoneTransferIn.IXFR_DELSOA;
                this.parseRR(rec); // Restart...
				return;
			}
		}
		delta = (ZoneTransferIn.Delta) this.ixfr.get(this.ixfr.size() - 1);
		delta.adds.add(rec);
		break;

	case ZoneTransferIn.AXFR:
		// Old BINDs sent cross class A records for non IN classes.
		if (type == Type.A && rec.getDClass() != this.dclass)
			break;
        this.axfr.add(rec);
		if (type == Type.SOA) {
            this.state = ZoneTransferIn.END;
		}
		break;

	case ZoneTransferIn.END:
        this.fail("extra data");
		break;

	default:
        this.fail("invalid state");
		break;
	}
}

private void
closeConnection() {
	try {
		if (this.client != null)
            this.client.cleanup();
	}
	catch (IOException e) {
	}
}

private Message
parseMessage(byte [] b) throws WireParseException {
	try {
		return new Message(b);
	}
	catch (IOException e) {
		if (e instanceof WireParseException)
			throw (WireParseException) e;
		throw new WireParseException("Error parsing message");
	}
}

private void
doxfr() throws IOException, ZoneTransferException {
    this.sendQuery();
	while (this.state != ZoneTransferIn.END) {
		byte [] in = this.client.recv();
		Message response = this.parseMessage(in);
		if (response.getHeader().getRcode() == Rcode.NOERROR &&
                this.verifier != null)
		{
			TSIGRecord tsigrec = response.getTSIG();

			int error = this.verifier.verify(response, in);
			if (error != Rcode.NOERROR)
                this.fail("TSIG failure");
		}

		Record [] answers = response.getSectionArray(Section.ANSWER);

		if (this.state == ZoneTransferIn.INITIALSOA) {
			int rcode = response.getRcode();
			if (rcode != Rcode.NOERROR) {
				if (this.qtype == Type.IXFR &&
				    rcode == Rcode.NOTIMP)
				{
                    this.fallback();
                    this.doxfr();
					return;
				}
                this.fail(Rcode.string(rcode));
			}

			Record question = response.getQuestion();
			if (question != null && question.getType() != this.qtype) {
                this.fail("invalid question section");
			}

			if (answers.length == 0 && this.qtype == Type.IXFR) {
                this.fallback();
                this.doxfr();
				return;
			}
		}

		for (int i = 0; i < answers.length; i++) {
            this.parseRR(answers[i]);
		}

		if (this.state == ZoneTransferIn.END && this.verifier != null &&
		    !response.isVerified())
            this.fail("last message must be signed");
	}
}

/**
 * Does the zone transfer.
 * @return A list, which is either an AXFR-style response (List of Records),
 * and IXFR-style response (List of Deltas), or null, which indicates that
 * an IXFR was performed and the zone is up to date.
 * @throws IOException The zone transfer failed to due an IO problem.
 * @throws ZoneTransferException The zone transfer failed to due a problem
 * with the zone transfer itself.
 */
public List
run() throws IOException, ZoneTransferException {
	try {
        this.openConnection();
        this.doxfr();
	}
	finally {
        this.closeConnection();
	}
	if (this.axfr != null)
		return this.axfr;
	return this.ixfr;
}

/**
 * Returns true if the response is an AXFR-style response (List of Records).
 * This will be true if either an IXFR was performed, an IXFR was performed
 * and the server provided a full zone transfer, or an IXFR failed and
 * fallback to AXFR occurred.
 */
public boolean
isAXFR() {
	return this.rtype == Type.AXFR;
}

/**
 * Gets the AXFR-style response.
 */
public List
getAXFR() {
	return this.axfr;
}

/**
 * Returns true if the response is an IXFR-style response (List of Deltas).
 * This will be true only if an IXFR was performed and the server provided
 * an incremental zone transfer.
 */
public boolean
isIXFR() {
	return this.rtype == Type.IXFR;
}

/**
 * Gets the IXFR-style response.
 */
public List
getIXFR() {
	return this.ixfr;
}

/**
 * Returns true if the response indicates that the zone is up to date.
 * This will be true only if an IXFR was performed.
 */
public boolean
isCurrent() {
	return this.axfr == null && this.ixfr == null;
}

}
