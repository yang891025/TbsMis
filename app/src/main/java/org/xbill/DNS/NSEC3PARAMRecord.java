// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.xbill.DNS.utils.base16;

/**
 * Next SECure name 3 Parameters - this record contains the parameters (hash
 * algorithm, salt, iterations) used for a valid, complete NSEC3 chain present
 * in a zone. Zones signed using NSEC3 must include this record at the zone apex
 * to inform authoritative servers that NSEC3 is being used with the given
 * parameters.
 * 
 * @author Brian Wellington
 * @author David Blacka
 */

public class NSEC3PARAMRecord extends Record {

private static final long serialVersionUID = -8689038598776316533L;

private int hashAlg;
private int flags;
private int iterations;
private byte salt[];

NSEC3PARAMRecord() {}

@Override
Record getObject() {
	return new NSEC3PARAMRecord();
}

/**
 * Creates an NSEC3PARAM record from the given data.
 * 
 * @param name The ownername of the NSEC3PARAM record (generally the zone name).
 * @param dclass The class.
 * @param ttl The TTL.
 * @param hashAlg The hash algorithm.
 * @param flags The value of the flags field.
 * @param iterations The number of hash iterations.
 * @param salt The salt to use (may be null).
 */
public NSEC3PARAMRecord(Name name, int dclass, long ttl, int hashAlg, 
			int flags, int iterations, byte [] salt)
{
	super(name, Type.NSEC3PARAM, dclass, ttl);
	this.hashAlg = Record.checkU8("hashAlg", hashAlg);
	this.flags = Record.checkU8("flags", flags);
	this.iterations = Record.checkU16("iterations", iterations);

	if (salt != null) {
		if (salt.length > 255)
			throw new IllegalArgumentException("Invalid salt " +
							   "length");
		if (salt.length > 0) {
			this.salt = new byte[salt.length];
			System.arraycopy(salt, 0, this.salt, 0, salt.length);
		}
	}
}

@Override
void
rrFromWire(DNSInput in) throws IOException {
    this.hashAlg = in.readU8();
    this.flags = in.readU8();
    this.iterations = in.readU16();

	int salt_length = in.readU8();
	if (salt_length > 0)
        this.salt = in.readByteArray(salt_length);
	else
        this.salt = null;
}

@Override
void
rrToWire(DNSOutput out, Compression c, boolean canonical) {
	out.writeU8(this.hashAlg);
	out.writeU8(this.flags);
	out.writeU16(this.iterations);

	if (this.salt != null) {
		out.writeU8(this.salt.length);
		out.writeByteArray(this.salt);
	} else
		out.writeU8(0);
}

@Override
void
rdataFromString(Tokenizer st, Name origin) throws IOException
{
    this.hashAlg = st.getUInt8();
    this.flags = st.getUInt8();
    this.iterations = st.getUInt16();

	String s = st.getString();
	if (s.equals("-"))
        this.salt = null;
	else {
		st.unget();
        this.salt = st.getHexString();
		if (this.salt.length > 255)
			throw st.exception("salt value too long");
	}
}

/** Converts rdata to a String */
@Override
String
rrToString() {
	StringBuffer sb = new StringBuffer();
	sb.append(this.hashAlg);
	sb.append(' ');
	sb.append(this.flags);
	sb.append(' ');
	sb.append(this.iterations);
	sb.append(' ');
	if (this.salt == null)
		sb.append('-');
	else
		sb.append(base16.toString(this.salt));

	return sb.toString();
}

/** Returns the hash algorithm */
public int
getHashAlgorithm() {
	return this.hashAlg;
}

/** Returns the flags */
public int
getFlags() {
	return this.flags;
}
  
/** Returns the number of iterations */
public int
getIterations() {
	return this.iterations;
}

/** Returns the salt */
public byte []
getSalt()
{
	return this.salt;
}

/**
 * Hashes a name with the parameters of this NSEC3PARAM record.
 * @param name The name to hash
 * @return The hashed version of the name
 * @throws NoSuchAlgorithmException The hash algorithm is unknown.
 */
public byte []
hashName(Name name) throws NoSuchAlgorithmException
{
	return NSEC3Record.hashName(name, this.hashAlg, this.iterations, this.salt);
}

}
