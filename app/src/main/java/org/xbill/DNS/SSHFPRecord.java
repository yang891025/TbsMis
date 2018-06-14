// Copyright (c) 2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.*;
import org.xbill.DNS.utils.*;

/**
 * SSH Fingerprint - stores the fingerprint of an SSH host key.
 *
 * @author Brian Wellington
 */

public class SSHFPRecord extends Record {

private static final long serialVersionUID = -8104701402654687025L;

public static class Algorithm {
	private Algorithm() {}

	public static final int RSA = 1;
	public static final int DSS = 2;
}

public static class Digest {
	private Digest() {}

	public static final int SHA1 = 1;
}

private int alg;
private int digestType;
private byte [] fingerprint;

SSHFPRecord() {} 

@Override
Record
getObject() {
	return new SSHFPRecord();
}

/**
 * Creates an SSHFP Record from the given data.
 * @param alg The public key's algorithm.
 * @param digestType The public key's digest type.
 * @param fingerprint The public key's fingerprint.
 */
public
SSHFPRecord(Name name, int dclass, long ttl, int alg, int digestType,
	    byte [] fingerprint)
{
	super(name, Type.SSHFP, dclass, ttl);
	this.alg = Record.checkU8("alg", alg);
	this.digestType = Record.checkU8("digestType", digestType);
	this.fingerprint = fingerprint;
}

@Override
void
rrFromWire(DNSInput in) throws IOException {
    this.alg = in.readU8();
    this.digestType = in.readU8();
    this.fingerprint = in.readByteArray();
}

@Override
void
rdataFromString(Tokenizer st, Name origin) throws IOException {
    this.alg = st.getUInt8();
    this.digestType = st.getUInt8();
    this.fingerprint = st.getHex(true);
}

@Override
String
rrToString() {
	StringBuffer sb = new StringBuffer();
	sb.append(this.alg);
	sb.append(" ");
	sb.append(this.digestType);
	sb.append(" ");
	sb.append(base16.toString(this.fingerprint));
	return sb.toString();
}

/** Returns the public key's algorithm. */
public int
getAlgorithm() {
	return this.alg;
}

/** Returns the public key's digest type. */
public int
getDigestType() {
	return this.digestType;
}

/** Returns the fingerprint */
public byte []
getFingerPrint() {
	return this.fingerprint;
}

@Override
void
rrToWire(DNSOutput out, Compression c, boolean canonical) {
	out.writeU8(this.alg);
	out.writeU8(this.digestType);
	out.writeByteArray(this.fingerprint);
}

}
