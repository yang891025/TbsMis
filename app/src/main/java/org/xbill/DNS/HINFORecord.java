// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.*;

/**
 * Host Information - describes the CPU and OS of a host
 *
 * @author Brian Wellington
 */

public class HINFORecord extends Record {

private static final long serialVersionUID = -4732870630947452112L;
	
private byte [] cpu, os;

HINFORecord() {}

@Override
Record
getObject() {
	return new HINFORecord();
}

/**
 * Creates an HINFO Record from the given data
 * @param cpu A string describing the host's CPU
 * @param os A string describing the host's OS
 * @throws IllegalArgumentException One of the strings has invalid escapes
 */
public
HINFORecord(Name name, int dclass, long ttl, String cpu, String os) {
	super(name, Type.HINFO, dclass, ttl);
	try {
		this.cpu = Record.byteArrayFromString(cpu);
		this.os = Record.byteArrayFromString(os);
	}
	catch (TextParseException e) {
		throw new IllegalArgumentException(e.getMessage());
	}
}

@Override
void
rrFromWire(DNSInput in) throws IOException {
    this.cpu = in.readCountedString();
    this.os = in.readCountedString();
}

@Override
void
rdataFromString(Tokenizer st, Name origin) throws IOException {
	try {
        this.cpu = Record.byteArrayFromString(st.getString());
        this.os = Record.byteArrayFromString(st.getString());
	}
	catch (TextParseException e) {
		throw st.exception(e.getMessage());
	}
}

/**
 * Returns the host's CPU
 */
public String
getCPU() {
	return Record.byteArrayToString(this.cpu, false);
}

/**
 * Returns the host's OS
 */
public String
getOS() {
	return Record.byteArrayToString(this.os, false);
}

@Override
void
rrToWire(DNSOutput out, Compression c, boolean canonical) {
	out.writeCountedString(this.cpu);
	out.writeCountedString(this.os);
}

/**
 * Converts to a string
 */
@Override
String
rrToString() {
	StringBuffer sb = new StringBuffer();
	sb.append(Record.byteArrayToString(this.cpu, true));
	sb.append(" ");
	sb.append(Record.byteArrayToString(this.os, true));
	return sb.toString();
}

}
