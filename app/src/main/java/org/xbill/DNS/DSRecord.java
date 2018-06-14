// Copyright (c) 2002-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.*;
import org.xbill.DNS.utils.*;

/**
 * DS - contains a Delegation Signer record, which acts as a
 * placeholder for KEY records in the parent zone.
 * @see DNSSEC
 *
 * @author David Blacka
 * @author Brian Wellington
 */

public class DSRecord extends Record {

public static class Digest {
	private Digest() {}

	/** SHA-1 */
	public static final int SHA1 = 1;

	/** SHA-256 */
	public static final int SHA256 = 2;
}

public static final int SHA1_DIGEST_ID = DSRecord.Digest.SHA1;
public static final int SHA256_DIGEST_ID = DSRecord.Digest.SHA256;

private static final long serialVersionUID = -9001819329700081493L;

private int footprint;
private int alg;
private int digestid;
private byte [] digest;

DSRecord() {}

@Override
Record
getObject() {
	return new DSRecord();
}

/**
 * Creates a DS Record from the given data
 * @param footprint The original KEY record's footprint (keyid).
 * @param alg The original key algorithm.
 * @param digestid The digest id code.
 * @param digest A hash of the original key.
 */
public
DSRecord(Name name, int dclass, long ttl, int footprint, int alg,
	 int digestid, byte [] digest)
{
	super(name, Type.DS, dclass, ttl);
	this.footprint = Record.checkU16("footprint", footprint);
	this.alg = Record.checkU8("alg", alg);
	this.digestid = Record.checkU8("digestid", digestid);
	this.digest = digest;
}

/**
 * Creates a DS Record from the given data
 * @param footprint The original KEY record's footprint (keyid).
 * @param digestid The digest id code.
 * @param key The key to digest
 */
public
DSRecord(Name name, int dclass, long ttl, int footprint, int digestid,
	 DNSKEYRecord key)
{
	this(name, dclass, ttl, footprint, key.getAlgorithm(), digestid,
	     DNSSEC.generateDS(key, digestid));
}

@Override
void
rrFromWire(DNSInput in) throws IOException {
    this.footprint = in.readU16();
    this.alg = in.readU8();
    this.digestid = in.readU8();
    this.digest = in.readByteArray();
}

@Override
void
rdataFromString(Tokenizer st, Name origin) throws IOException {
    this.footprint = st.getUInt16();
    this.alg = st.getUInt8();
    this.digestid = st.getUInt8();
    this.digest = st.getHex();
}

/**
 * Converts rdata to a String
 */
@Override
String
rrToString() {
	StringBuffer sb = new StringBuffer();
	sb.append(this.footprint);
	sb.append(" ");
	sb.append(this.alg);
	sb.append(" ");
	sb.append(this.digestid);
	if (this.digest != null) {
		sb.append(" ");
		sb.append(base16.toString(this.digest));
	}

	return sb.toString();
}

/**
 * Returns the key's algorithm.
 */
public int
getAlgorithm() {
	return this.alg;
}

/**
 *  Returns the key's Digest ID.
 */
public int
getDigestID()
{
	return this.digestid;
}
  
/**
 * Returns the binary hash of the key.
 */
public byte []
getDigest() {
	return this.digest;
}

/**
 * Returns the key's footprint.
 */
public int
getFootprint() {
	return this.footprint;
}

@Override
void
rrToWire(DNSOutput out, Compression c, boolean canonical) {
	out.writeU16(this.footprint);
	out.writeU8(this.alg);
	out.writeU8(this.digestid);
	if (this.digest != null)
		out.writeByteArray(this.digest);
}

}
