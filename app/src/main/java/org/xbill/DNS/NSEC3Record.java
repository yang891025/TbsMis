// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.*;
import java.security.*;

import org.xbill.DNS.utils.*;
import org.xbill.DNS.utils.base32.Alphabet;

/**
 * Next SECure name 3 - this record contains the next hashed name in an
 * ordered list of hashed names in the zone, and a set of types for which
 * records exist for this name. The presence of this record in a response
 * signifies a negative response from a DNSSEC-signed zone.
 * 
 * This replaces the NSEC and NXT records, when used.
 * 
 * @author Brian Wellington
 * @author David Blacka
 */

public class NSEC3Record extends Record {

public static class Flags {
	/**
	 * NSEC3 flags identifiers.
	 */

	private Flags() {}

	/** Unsigned delegation are not included in the NSEC3 chain.
	 *
	 */
	public static final int OPT_OUT = 0x01;
}

public static class Digest {
	private Digest() {}

	/** SHA-1 */
	public static final int SHA1 = 1;
}

public static final int SHA1_DIGEST_ID = NSEC3Record.Digest.SHA1;

private static final long serialVersionUID = -7123504635968932855L;

private int hashAlg;
private int flags;
private int iterations;
private byte [] salt;
private byte [] next;
private TypeBitmap types;

private static final base32 b32 = new base32(Alphabet.BASE32HEX,
					     false, false);

NSEC3Record() {}

@Override
Record getObject() {
	return new NSEC3Record();
}

/**
 * Creates an NSEC3 record from the given data.
 *
 * @param name The ownername of the NSEC3 record (base32'd hash plus zonename).
 * @param dclass The class.
 * @param ttl The TTL.
 * @param hashAlg The hash algorithm.
 * @param flags The value of the flags field.
 * @param iterations The number of hash iterations.
 * @param salt The salt to use (may be null).
 * @param next The next hash (may not be null).
 * @param types The types present at the original ownername.
 */
public NSEC3Record(Name name, int dclass, long ttl, int hashAlg,
		   int flags, int iterations, byte [] salt, byte [] next,
		   int [] types)
{
	super(name, Type.NSEC3, dclass, ttl);
	this.hashAlg = Record.checkU8("hashAlg", hashAlg);
	this.flags = Record.checkU8("flags", flags);
	this.iterations = Record.checkU16("iterations", iterations);

	if (salt != null) {
		if (salt.length > 255)
			throw new IllegalArgumentException("Invalid salt");
		if (salt.length > 0) {
			this.salt = new byte[salt.length];
			System.arraycopy(salt, 0, this.salt, 0, salt.length);
		}
	}

	if (next.length > 255) {
		throw new IllegalArgumentException("Invalid next hash");
	}
	this.next = new byte[next.length];
	System.arraycopy(next, 0, this.next, 0, next.length);
	this.types = new TypeBitmap(types);
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

	int next_length = in.readU8();
    this.next = in.readByteArray(next_length);
    this.types = new TypeBitmap(in);
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

	out.writeU8(this.next.length);
	out.writeByteArray(this.next);
    this.types.toWire(out);
}

@Override
void
rdataFromString(Tokenizer st, Name origin) throws IOException {
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

    this.next = st.getBase32String(NSEC3Record.b32);
    this.types = new TypeBitmap(st);
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
	sb.append(' ');
	sb.append(NSEC3Record.b32.toString(this.next));

	if (!this.types.empty()) {
		sb.append(' ');
		sb.append(types);
	}

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

/** Returns the next hash */
public byte []
getNext() {
	return this.next;
}

  /** Returns the set of types defined for this name */
public int []
getTypes() {
	return this.types.toArray();
}

/** Returns whether a specific type is in the set of types. */
public boolean
hasType(int type)
{
	return this.types.contains(type);
}

static byte []
hashName(Name name, int hashAlg, int iterations, byte [] salt)
throws NoSuchAlgorithmException
{
	MessageDigest digest;
	switch (hashAlg) {
	case NSEC3Record.Digest.SHA1:
		digest = MessageDigest.getInstance("sha-1");
		break;
	default:
		throw new NoSuchAlgorithmException("Unknown NSEC3 algorithm" +
						   "identifier: " +
						   hashAlg);
	}
	byte [] hash = null;
	for (int i = 0; i <= iterations; i++) {
		digest.reset();
		if (i == 0)
			digest.update(name.toWireCanonical());
		else
			digest.update(hash);
		if (salt != null)
			digest.update(salt);
		hash = digest.digest();
	}
	return hash;
}

/**
 * Hashes a name with the parameters of this NSEC3 record.
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
