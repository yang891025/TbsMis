// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.*;
import java.net.*;

/**
 * A6 Record - maps a domain name to an IPv6 address (experimental)
 *
 * @author Brian Wellington
 */

public class A6Record extends Record {

private static final long serialVersionUID = -8815026887337346789L;

private int prefixBits;
private InetAddress suffix;
private Name prefix;

A6Record() {}

@Override
Record
getObject() {
	return new A6Record();
}

/**
 * Creates an A6 Record from the given data
 * @param prefixBits The number of bits in the address prefix
 * @param suffix The address suffix
 * @param prefix The name of the prefix
 */
public
A6Record(Name name, int dclass, long ttl, int prefixBits,
	 InetAddress suffix, Name prefix)
{
	super(name, Type.A6, dclass, ttl);
	this.prefixBits = Record.checkU8("prefixBits", prefixBits);
	if (suffix != null && Address.familyOf(suffix) != Address.IPv6)
		throw new IllegalArgumentException("invalid IPv6 address");
	this.suffix = suffix;
	if (prefix != null)
		this.prefix = Record.checkName("prefix", prefix);
}

@Override
void
rrFromWire(DNSInput in) throws IOException {
    this.prefixBits = in.readU8();
	int suffixbits = 128 - this.prefixBits;
	int suffixbytes = (suffixbits + 7) / 8;
	if (this.prefixBits < 128) {
		byte [] bytes = new byte[16];
		in.readByteArray(bytes, 16 - suffixbytes, suffixbytes);
        this.suffix = InetAddress.getByAddress(bytes);
	}
	if (this.prefixBits > 0)
        this.prefix = new Name(in);
}

@Override
void
rdataFromString(Tokenizer st, Name origin) throws IOException {
    this.prefixBits = st.getUInt8();
	if (this.prefixBits > 128) {
		throw st.exception("prefix bits must be [0..128]");
	} else if (this.prefixBits < 128) {
		String s = st.getString();
		try {
            this.suffix = Address.getByAddress(s, Address.IPv6);
		}
		catch (UnknownHostException e) {
			throw st.exception("invalid IPv6 address: " + s);
		}
	}
	if (this.prefixBits > 0)
        this.prefix = st.getName(origin);
}

/** Converts rdata to a String */
@Override
String
rrToString() {
	StringBuffer sb = new StringBuffer();
	sb.append(this.prefixBits);
	if (this.suffix != null) {
		sb.append(" ");
		sb.append(this.suffix.getHostAddress());
	}
	if (this.prefix != null) {
		sb.append(" ");
		sb.append(this.prefix);
	}
	return sb.toString();
}

/** Returns the number of bits in the prefix */
public int
getPrefixBits() {
	return this.prefixBits;
}

/** Returns the address suffix */
public InetAddress
getSuffix() {
	return this.suffix;
}

/** Returns the address prefix */
public Name
getPrefix() {
	return this.prefix;
}

@Override
void
rrToWire(DNSOutput out, Compression c, boolean canonical) {
	out.writeU8(this.prefixBits);
	if (this.suffix != null) {
		int suffixbits = 128 - this.prefixBits;
		int suffixbytes = (suffixbits + 7) / 8;
		byte [] data = this.suffix.getAddress();
		out.writeByteArray(data, 16 - suffixbytes, suffixbytes);
	}
	if (this.prefix != null)
        this.prefix.toWire(out, null, canonical);
}

}
