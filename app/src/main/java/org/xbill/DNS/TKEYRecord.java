// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.*;
import java.util.*;
import org.xbill.DNS.utils.*;

/**
 * Transaction Key - used to compute and/or securely transport a shared
 * secret to be used with TSIG.
 * @see TSIG
 *
 * @author Brian Wellington
 */

public class TKEYRecord extends Record {

private static final long serialVersionUID = 8828458121926391756L;

private Name alg;
private Date timeInception;
private Date timeExpire;
private int mode, error;
private byte [] key;
private byte [] other;

/** The key is assigned by the server (unimplemented) */
public static final int SERVERASSIGNED		= 1;

/** The key is computed using a Diffie-Hellman key exchange */
public static final int DIFFIEHELLMAN		= 2;

/** The key is computed using GSS_API (unimplemented) */
public static final int GSSAPI			= 3;

/** The key is assigned by the resolver (unimplemented) */
public static final int RESOLVERASSIGNED	= 4;

/** The key should be deleted */
public static final int DELETE			= 5;

TKEYRecord() {}

@Override
Record
getObject() {
	return new TKEYRecord();
}

/**
 * Creates a TKEY Record from the given data.
 * @param alg The shared key's algorithm
 * @param timeInception The beginning of the validity period of the shared
 * secret or keying material
 * @param timeExpire The end of the validity period of the shared
 * secret or keying material
 * @param mode The mode of key agreement
 * @param error The extended error field.  Should be 0 in queries
 * @param key The shared secret
 * @param other The other data field.  Currently unused
 * responses.
 */
public
TKEYRecord(Name name, int dclass, long ttl, Name alg,
	   Date timeInception, Date timeExpire, int mode, int error,
	   byte [] key, byte other[])
{
	super(name, Type.TKEY, dclass, ttl);
	this.alg = Record.checkName("alg", alg);
	this.timeInception = timeInception;
	this.timeExpire = timeExpire;
	this.mode = Record.checkU16("mode", mode);
	this.error = Record.checkU16("error", error);
	this.key = key;
	this.other = other;
}

@Override
void
rrFromWire(DNSInput in) throws IOException {
    this.alg = new Name(in);
    this.timeInception = new Date(1000 * in.readU32());
    this.timeExpire = new Date(1000 * in.readU32());
    this.mode = in.readU16();
    this.error = in.readU16();

	int keylen = in.readU16();
	if (keylen > 0)
        this.key = in.readByteArray(keylen);
	else
        this.key = null;

	int otherlen = in.readU16();
	if (otherlen > 0)
        this.other = in.readByteArray(otherlen);
	else
        this.other = null;
}

@Override
void
rdataFromString(Tokenizer st, Name origin) throws IOException {
	throw st.exception("no text format defined for TKEY");
}

protected String
modeString() {
	switch (this.mode) {
		case TKEYRecord.SERVERASSIGNED:	return "SERVERASSIGNED";
		case TKEYRecord.DIFFIEHELLMAN:	return "DIFFIEHELLMAN";
		case TKEYRecord.GSSAPI:		return "GSSAPI";
		case TKEYRecord.RESOLVERASSIGNED:	return "RESOLVERASSIGNED";
		case TKEYRecord.DELETE:		return "DELETE";
		default:		return Integer.toString(this.mode);
	}
}

/** Converts rdata to a String */
@Override
String
rrToString() {
	StringBuffer sb = new StringBuffer();
	sb.append(this.alg);
	sb.append(" ");
	if (Options.check("multiline"))
		sb.append("(\n\t");
	sb.append(FormattedTime.format(this.timeInception));
	sb.append(" ");
	sb.append(FormattedTime.format(this.timeExpire));
	sb.append(" ");
	sb.append(this.modeString());
	sb.append(" ");
	sb.append(Rcode.TSIGstring(this.error));
	if (Options.check("multiline")) {
		sb.append("\n");
		if (this.key != null) {
			sb.append(base64.formatString(this.key, 64, "\t", false));
			sb.append("\n");
		}
		if (this.other != null)
			sb.append(base64.formatString(this.other, 64, "\t", false));
		sb.append(" )");
	} else {
		sb.append(" ");
		if (this.key != null) {
			sb.append(base64.toString(this.key));
			sb.append(" ");
		}
		if (this.other != null)
			sb.append(base64.toString(this.other));
	}
	return sb.toString();
}

/** Returns the shared key's algorithm */
public Name
getAlgorithm() {
	return this.alg;
}

/**
 * Returns the beginning of the validity period of the shared secret or
 * keying material
 */
public Date
getTimeInception() {
	return this.timeInception;
}

/**
 * Returns the end of the validity period of the shared secret or
 * keying material
 */
public Date
getTimeExpire() {
	return this.timeExpire;
}

/** Returns the key agreement mode */
public int
getMode() {
	return this.mode;
}

/** Returns the extended error */
public int
getError() {
	return this.error;
}

/** Returns the shared secret or keying material */
public byte []
getKey() {
	return this.key;
}

/** Returns the other data */
public byte []
getOther() {
	return this.other;
}

@Override
void
rrToWire(DNSOutput out, Compression c, boolean canonical) {
    this.alg.toWire(out, null, canonical);

	out.writeU32(this.timeInception.getTime() / 1000);
	out.writeU32(this.timeExpire.getTime() / 1000);

	out.writeU16(this.mode);
	out.writeU16(this.error);

	if (this.key != null) {
		out.writeU16(this.key.length);
		out.writeByteArray(this.key);
	}
	else
		out.writeU16(0);

	if (this.other != null) {
		out.writeU16(this.other.length);
		out.writeByteArray(this.other);
	}
	else
		out.writeU16(0);
}

}
