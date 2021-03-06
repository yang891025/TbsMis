// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.*;
import java.util.*;
import org.xbill.DNS.utils.*;

/**
 * Transaction Signature - this record is automatically generated by the
 * resolver.  TSIG records provide transaction security between the
 * sender and receiver of a message, using a shared key.
 * @see Resolver
 * @see TSIG
 *
 * @author Brian Wellington
 */

public class TSIGRecord extends Record {

private static final long serialVersionUID = -88820909016649306L;

private Name alg;
private Date timeSigned;
private int fudge;
private byte [] signature;
private int originalID;
private int error;
private byte [] other;

TSIGRecord() {} 

@Override
Record
getObject() {
	return new TSIGRecord();
}

/**
 * Creates a TSIG Record from the given data.  This is normally called by
 * the TSIG class
 * @param alg The shared key's algorithm
 * @param timeSigned The time that this record was generated
 * @param fudge The fudge factor for time - if the time that the message is
 * received is not in the range [now - fudge, now + fudge], the signature
 * fails
 * @param signature The signature
 * @param originalID The message ID at the time of its generation
 * @param error The extended error field.  Should be 0 in queries.
 * @param other The other data field.  Currently used only in BADTIME
 * responses.
 * @see TSIG
 */
public
TSIGRecord(Name name, int dclass, long ttl, Name alg, Date timeSigned,
	   int fudge, byte [] signature, int originalID, int error,
	   byte other[])
{
	super(name, Type.TSIG, dclass, ttl);
	this.alg = Record.checkName("alg", alg);
	this.timeSigned = timeSigned;
	this.fudge = Record.checkU16("fudge", fudge);
	this.signature = signature;
	this.originalID = Record.checkU16("originalID", originalID);
	this.error = Record.checkU16("error", error);
	this.other = other;
}

@Override
void
rrFromWire(DNSInput in) throws IOException {
    this.alg = new Name(in);

	long timeHigh = in.readU16();
	long timeLow = in.readU32();
	long time = (timeHigh << 32) + timeLow;
    this.timeSigned = new Date(time * 1000);
    this.fudge = in.readU16();

	int sigLen = in.readU16();
    this.signature = in.readByteArray(sigLen);

    this.originalID = in.readU16();
    this.error = in.readU16();

	int otherLen = in.readU16();
	if (otherLen > 0)
        this.other = in.readByteArray(otherLen);
	else
        this.other = null;
}

@Override
void
rdataFromString(Tokenizer st, Name origin) throws IOException {
	throw st.exception("no text format defined for TSIG");
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

	sb.append (this.timeSigned.getTime() / 1000);
	sb.append (" ");
	sb.append (this.fudge);
	sb.append (" ");
	sb.append (this.signature.length);
	if (Options.check("multiline")) {
		sb.append ("\n");
		sb.append (base64.formatString(this.signature, 64, "\t", false));
	} else {
		sb.append (" ");
		sb.append (base64.toString(this.signature));
	}
	sb.append (" ");
	sb.append (Rcode.TSIGstring(this.error));
	sb.append (" ");
	if (this.other == null)
		sb.append (0);
	else {
		sb.append (this.other.length);
		if (Options.check("multiline"))
			sb.append("\n\n\n\t");
		else
			sb.append(" ");
		if (this.error == Rcode.BADTIME) {
			if (this.other.length != 6) {
				sb.append("<invalid BADTIME other data>");
			} else {
				long time = ((long)(this.other[0] & 0xFF) << 40) +
					    ((long)(this.other[1] & 0xFF) << 32) +
					    ((this.other[2] & 0xFF) << 24) +
					    ((this.other[3] & 0xFF) << 16) +
					    ((this.other[4] & 0xFF) << 8) +
                        (this.other[5] & 0xFF);
				sb.append("<server time: ");
				sb.append(new Date(time * 1000));
				sb.append(">");
			}
		} else {
			sb.append("<");
			sb.append(base64.toString(this.other));
			sb.append(">");
		}
	}
	if (Options.check("multiline"))
		sb.append(" )");
	return sb.toString();
}

/** Returns the shared key's algorithm */
public Name
getAlgorithm() {
	return this.alg;
}

/** Returns the time that this record was generated */
public Date
getTimeSigned() {
	return this.timeSigned;
}

/** Returns the time fudge factor */
public int
getFudge() {
	return this.fudge;
}

/** Returns the signature */
public byte []
getSignature() {
	return this.signature;
}

/** Returns the original message ID */
public int
getOriginalID() {
	return this.originalID;
}

/** Returns the extended error */
public int
getError() {
	return this.error;
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

	long time = this.timeSigned.getTime() / 1000;
	int timeHigh = (int) (time >> 32);
	long timeLow = time & 0xFFFFFFFFL;
	out.writeU16(timeHigh);
	out.writeU32(timeLow);
	out.writeU16(this.fudge);

	out.writeU16(this.signature.length);
	out.writeByteArray(this.signature);

	out.writeU16(this.originalID);
	out.writeU16(this.error);

	if (this.other != null) {
		out.writeU16(this.other.length);
		out.writeByteArray(this.other);
	}
	else
		out.writeU16(0);
}

}
