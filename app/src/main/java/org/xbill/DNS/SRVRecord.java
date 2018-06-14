// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.*;

/**
 * Server Selection Record  - finds hosts running services in a domain.  An
 * SRV record will normally be named <service>.<protocol>.domain - an
 * example would be http.tcp.example.com (if HTTP used SRV records)
 *
 * @author Brian Wellington
 */

public class SRVRecord extends Record {

private static final long serialVersionUID = -3886460132387522052L;

private int priority, weight, port;
private Name target;

SRVRecord() {}

@Override
Record
getObject() {
	return new SRVRecord();
}

/**
 * Creates an SRV Record from the given data
 * @param priority The priority of this SRV.  Records with lower priority
 * are preferred.
 * @param weight The weight, used to select between records at the same
 * priority.
 * @param port The TCP/UDP port that the service uses
 * @param target The host running the service
 */
public
SRVRecord(Name name, int dclass, long ttl, int priority,
	  int weight, int port, Name target)
{
	super(name, Type.SRV, dclass, ttl);
	this.priority = Record.checkU16("priority", priority);
	this.weight = Record.checkU16("weight", weight);
	this.port = Record.checkU16("port", port);
	this.target = Record.checkName("target", target);
}

@Override
void
rrFromWire(DNSInput in) throws IOException {
    this.priority = in.readU16();
    this.weight = in.readU16();
    this.port = in.readU16();
    this.target = new Name(in);
}

@Override
void
rdataFromString(Tokenizer st, Name origin) throws IOException {
    this.priority = st.getUInt16();
    this.weight = st.getUInt16();
    this.port = st.getUInt16();
    this.target = st.getName(origin);
}

/** Converts rdata to a String */
@Override
String
rrToString() {
	StringBuffer sb = new StringBuffer();
	sb.append(this.priority + " ");
	sb.append(this.weight + " ");
	sb.append(this.port + " ");
	sb.append(this.target);
	return sb.toString();
}

/** Returns the priority */
public int
getPriority() {
	return this.priority;
}

/** Returns the weight */
public int
getWeight() {
	return this.weight;
}

/** Returns the port that the service runs on */
public int
getPort() {
	return this.port;
}

/** Returns the host running that the service */
public Name
getTarget() {
	return this.target;
}

@Override
void
rrToWire(DNSOutput out, Compression c, boolean canonical) {
	out.writeU16(this.priority);
	out.writeU16(this.weight);
	out.writeU16(this.port);
    this.target.toWire(out, null, canonical);
}

@Override
public Name
getAdditionalName() {
	return this.target;
}

}
