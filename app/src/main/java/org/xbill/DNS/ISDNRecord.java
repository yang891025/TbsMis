// Copyright (c) 2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import org.xbill.DNS.Tokenizer.Token;

import java.io.*;

/**
 * ISDN - identifies the ISDN number and subaddress associated with a name.
 *
 * @author Brian Wellington
 */

public class ISDNRecord extends Record {

private static final long serialVersionUID = -8730801385178968798L;

private byte [] address;
private byte [] subAddress;

ISDNRecord() {}

@Override
Record
getObject() {
	return new ISDNRecord();
}

/**
 * Creates an ISDN Record from the given data
 * @param address The ISDN number associated with the domain.
 * @param subAddress The subaddress, if any.
 * @throws IllegalArgumentException One of the strings is invalid.
 */
public
ISDNRecord(Name name, int dclass, long ttl, String address, String subAddress) {
	super(name, Type.ISDN, dclass, ttl);
	try {
		this.address = Record.byteArrayFromString(address);
		if (subAddress != null)
			this.subAddress = Record.byteArrayFromString(subAddress);
	}
	catch (TextParseException e) {
		throw new IllegalArgumentException(e.getMessage());
	}
}

@Override
void
rrFromWire(DNSInput in) throws IOException {
    this.address = in.readCountedString();
	if (in.remaining() > 0)
        this.subAddress = in.readCountedString();
}

@Override
void
rdataFromString(Tokenizer st, Name origin) throws IOException {
	try {
        this.address = Record.byteArrayFromString(st.getString());
		Token t = st.get();
		if (t.isString()) {
            this.subAddress = Record.byteArrayFromString(t.value);
		} else {
			st.unget();
		}
	}
	catch (TextParseException e) {
		throw st.exception(e.getMessage());
	}
}

/**
 * Returns the ISDN number associated with the domain.
 */
public String
getAddress() {
	return Record.byteArrayToString(this.address, false);
}

/**
 * Returns the ISDN subaddress, or null if there is none.
 */
public String
getSubAddress() {
	if (this.subAddress == null)
		return null;
	return Record.byteArrayToString(this.subAddress, false);
}

@Override
void
rrToWire(DNSOutput out, Compression c, boolean canonical) {
	out.writeCountedString(this.address);
	if (this.subAddress != null)
		out.writeCountedString(this.subAddress);
}

@Override
String
rrToString() {
	StringBuffer sb = new StringBuffer();
	sb.append(Record.byteArrayToString(this.address, true));
	if (this.subAddress != null) {
		sb.append(" ");
		sb.append(Record.byteArrayToString(this.subAddress, true));
	}
	return sb.toString();
}

}
