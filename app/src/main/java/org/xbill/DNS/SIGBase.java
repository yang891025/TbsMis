// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.*;
import java.util.*;

import org.xbill.DNS.DNSSEC.Algorithm;
import org.xbill.DNS.utils.*;

/**
 * The base class for SIG/RRSIG records, which have identical formats 
 *
 * @author Brian Wellington
 */

abstract class SIGBase extends Record {

private static final long serialVersionUID = -3738444391533812369L;

protected int covered;
protected int alg, labels;
protected long origttl;
protected Date expire, timeSigned;
protected int footprint;
protected Name signer;
protected byte [] signature;

protected
SIGBase() {}

public
SIGBase(Name name, int type, int dclass, long ttl, int covered, int alg,
	long origttl, Date expire, Date timeSigned, int footprint, Name signer,
	byte [] signature)
{
	super(name, type, dclass, ttl);
	Type.check(covered);
	TTL.check(origttl);
	this.covered = covered;
	this.alg = Record.checkU8("alg", alg);
    labels = name.labels() - 1;
	if (name.isWild())
        labels--;
	this.origttl = origttl;
	this.expire = expire;
	this.timeSigned = timeSigned;
	this.footprint = Record.checkU16("footprint", footprint);
	this.signer = Record.checkName("signer", signer);
	this.signature = signature;
}

@Override
void
rrFromWire(DNSInput in) throws IOException {
    this.covered = in.readU16();
    this.alg = in.readU8();
    this.labels = in.readU8();
    this.origttl = in.readU32();
    this.expire = new Date(1000 * in.readU32());
    this.timeSigned = new Date(1000 * in.readU32());
    this.footprint = in.readU16();
    this.signer = new Name(in);
    this.signature = in.readByteArray();
}

@Override
void
rdataFromString(Tokenizer st, Name origin) throws IOException {
	String typeString = st.getString();
    this.covered = Type.value(typeString);
	if (this.covered < 0)
		throw st.exception("Invalid type: " + typeString);
	String algString = st.getString();
    this.alg = Algorithm.value(algString);
	if (this.alg < 0)
		throw st.exception("Invalid algorithm: " + algString);
    this.labels = st.getUInt8();
    this.origttl = st.getTTL();
    this.expire = FormattedTime.parse(st.getString());
    this.timeSigned = FormattedTime.parse(st.getString());
    this.footprint = st.getUInt16();
    this.signer = st.getName(origin);
    this.signature = st.getBase64();
}

/** Converts the RRSIG/SIG Record to a String */
@Override
String
rrToString() {
	StringBuffer sb = new StringBuffer();
	sb.append (Type.string(this.covered));
	sb.append (" ");
	sb.append (this.alg);
	sb.append (" ");
	sb.append (this.labels);
	sb.append (" ");
	sb.append (this.origttl);
	sb.append (" ");
	if (Options.check("multiline"))
		sb.append ("(\n\t");
	sb.append (FormattedTime.format(this.expire));
	sb.append (" ");
	sb.append (FormattedTime.format(this.timeSigned));
	sb.append (" ");
	sb.append (this.footprint);
	sb.append (" ");
	sb.append (this.signer);
	if (Options.check("multiline")) {
		sb.append("\n");
		sb.append(base64.formatString(this.signature, 64, "\t",
					      true));
	} else {
		sb.append (" ");
		sb.append(base64.toString(this.signature));
	}
	return sb.toString();
}

/** Returns the RRset type covered by this signature */
public int
getTypeCovered() {
	return this.covered;
}

/**
 * Returns the cryptographic algorithm of the key that generated the signature
 */
public int
getAlgorithm() {
	return this.alg;
}

/**
 * Returns the number of labels in the signed domain name.  This may be
 * different than the record's domain name if the record is a wildcard
 * record.
 */
public int
getLabels() {
	return this.labels;
}

/** Returns the original TTL of the RRset */
public long
getOrigTTL() {
	return this.origttl;
}

/** Returns the time at which the signature expires */
public Date
getExpire() {
	return this.expire;
}

/** Returns the time at which this signature was generated */
public Date
getTimeSigned() {
	return this.timeSigned;
}

/** Returns The footprint/key id of the signing key.  */
public int
getFootprint() {
	return this.footprint;
}

/** Returns the owner of the signing key */
public Name
getSigner() {
	return this.signer;
}

/** Returns the binary data representing the signature */
public byte []
getSignature() {
	return this.signature;
}

void
setSignature(byte [] signature) {
	this.signature = signature;
}

@Override
void
rrToWire(DNSOutput out, Compression c, boolean canonical) {
	out.writeU16(this.covered);
	out.writeU8(this.alg);
	out.writeU8(this.labels);
	out.writeU32(this.origttl);
	out.writeU32(this.expire.getTime() / 1000);
	out.writeU32(this.timeSigned.getTime() / 1000);
	out.writeU16(this.footprint);
    this.signer.toWire(out, null, canonical);
	out.writeByteArray(this.signature);
}

}
