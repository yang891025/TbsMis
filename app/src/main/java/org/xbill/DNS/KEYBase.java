// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.*;
import java.security.PublicKey;

import org.xbill.DNS.DNSSEC.Algorithm;
import org.xbill.DNS.DNSSEC.DNSSECException;
import org.xbill.DNS.utils.*;

/**
 * The base class for KEY/DNSKEY records, which have identical formats 
 *
 * @author Brian Wellington
 */

abstract class KEYBase extends Record {

private static final long serialVersionUID = 3469321722693285454L;

protected int flags, proto, alg;
protected byte [] key;
protected int footprint = -1;
protected PublicKey publicKey;

protected
KEYBase() {}

public
KEYBase(Name name, int type, int dclass, long ttl, int flags, int proto,
	int alg, byte [] key)
{
	super(name, type, dclass, ttl);
	this.flags = Record.checkU16("flags", flags);
	this.proto = Record.checkU8("proto", proto);
	this.alg = Record.checkU8("alg", alg);
	this.key = key;
}

@Override
void
rrFromWire(DNSInput in) throws IOException {
    this.flags = in.readU16();
    this.proto = in.readU8();
    this.alg = in.readU8();
	if (in.remaining() > 0)
        this.key = in.readByteArray();
}

/** Converts the DNSKEY/KEY Record to a String */
@Override
String
rrToString() {
	StringBuffer sb = new StringBuffer();
	sb.append(this.flags);
	sb.append(" ");
	sb.append(this.proto);
	sb.append(" ");
	sb.append(this.alg);
	if (this.key != null) {
		if (Options.check("multiline")) {
			sb.append(" (\n");
			sb.append(base64.formatString(this.key, 64, "\t", true));
			sb.append(" ; key_tag = ");
			sb.append(this.getFootprint());
		} else {
			sb.append(" ");
			sb.append(base64.toString(this.key));
		}
	}
	return sb.toString();
}

/**
 * Returns the flags describing the key's properties
 */
public int
getFlags() {
	return this.flags;
}

/**
 * Returns the protocol that the key was created for
 */
public int
getProtocol() {
	return this.proto;
}

/**
 * Returns the key's algorithm
 */
public int
getAlgorithm() {
	return this.alg;
}

/**
 * Returns the binary data representing the key
 */
public byte []
getKey() {
	return this.key;
}

/**
 * Returns the key's footprint (after computing it)
 */
public int
getFootprint() {
	if (this.footprint >= 0)
		return this.footprint;

	int foot = 0;

	DNSOutput out = new DNSOutput();
    this.rrToWire(out, null, false);
	byte [] rdata = out.toByteArray();

	if (this.alg == Algorithm.RSAMD5) {
		int d1 = rdata[rdata.length - 3] & 0xFF;
		int d2 = rdata[rdata.length - 2] & 0xFF;
		foot = (d1 << 8) + d2;
	}
	else {
		int i; 
		for (i = 0; i < rdata.length - 1; i += 2) {
			int d1 = rdata[i] & 0xFF;
			int d2 = rdata[i + 1] & 0xFF;
			foot += (d1 << 8) + d2;
		}
		if (i < rdata.length) {
			int d1 = rdata[i] & 0xFF;
			foot += d1 << 8;
		}
		foot += foot >> 16 & 0xFFFF;
	}
    this.footprint = foot & 0xFFFF;
	return this.footprint;
}

/**
 * Returns a PublicKey corresponding to the data in this key.
 * @throws DNSSECException The key could not be converted.
 */
public PublicKey
getPublicKey() throws DNSSECException {
	if (this.publicKey != null)
		return this.publicKey;

    this.publicKey = DNSSEC.toPublicKey(this);
	return this.publicKey;
}

@Override
void
rrToWire(DNSOutput out, Compression c, boolean canonical) {
	out.writeU16(this.flags);
	out.writeU8(this.proto);
	out.writeU8(this.alg);
	if (this.key != null)
		out.writeByteArray(this.key);
}

}
