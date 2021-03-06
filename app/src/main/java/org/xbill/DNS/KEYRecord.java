// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import org.xbill.DNS.DNSSEC.Algorithm;
import org.xbill.DNS.DNSSEC.DNSSECException;

import java.io.*;
import java.security.PublicKey;
import java.util.*;

/**
 * Key - contains a cryptographic public key.  The data can be converted
 * to objects implementing java.security.interfaces.PublicKey
 * @see DNSSEC
 *
 * @author Brian Wellington
 */

public class KEYRecord extends KEYBase {

private static final long serialVersionUID = 6385613447571488906L;

public static class Protocol {
	/**
	 * KEY protocol identifiers.
	 */

	private Protocol() {}

	/** No defined protocol. */
	public static final int NONE = 0;

	/** Transaction Level Security */
	public static final int TLS = 1;

	/** Email */
	public static final int EMAIL = 2;

	/** DNSSEC */
	public static final int DNSSEC = 3;

	/** IPSEC Control */
	public static final int IPSEC = 4;

	/** Any protocol */
	public static final int ANY = 255;

	private static final Mnemonic protocols = new Mnemonic("KEY protocol",
							 Mnemonic.CASE_UPPER);

	static {
        Protocol.protocols.setMaximum(0xFF);
        Protocol.protocols.setNumericAllowed(true);

        Protocol.protocols.add(Protocol.NONE, "NONE");
        Protocol.protocols.add(Protocol.TLS, "TLS");
        Protocol.protocols.add(Protocol.EMAIL, "EMAIL");
        Protocol.protocols.add(Protocol.DNSSEC, "DNSSEC");
        Protocol.protocols.add(Protocol.IPSEC, "IPSEC");
        Protocol.protocols.add(Protocol.ANY, "ANY");
	}

	/**
	 * Converts an KEY protocol value into its textual representation
	 */
	public static String
	string(int type) {
		return Protocol.protocols.getText(type);
	}

	/**
	 * Converts a textual representation of a KEY protocol into its
	 * numeric code.  Integers in the range 0..255 are also accepted.
	 * @param s The textual representation of the protocol
	 * @return The protocol code, or -1 on error.
	 */
	public static int
	value(String s) {
		return Protocol.protocols.getValue(s);
	}
}
	
public static class Flags {
	/**
	 * KEY flags identifiers.
	 */

	private Flags() {}

	/** KEY cannot be used for confidentiality */
	public static final int NOCONF = 0x4000;

	/** KEY cannot be used for authentication */
	public static final int NOAUTH = 0x8000;

	/** No key present */
	public static final int NOKEY = 0xC000;

	/** Bitmask of the use fields */
	public static final int USE_MASK = 0xC000;

	/** Flag 2 (unused) */
	public static final int FLAG2 = 0x2000;

	/** Flags extension */
	public static final int EXTEND = 0x1000;

	/** Flag 4 (unused) */
	public static final int FLAG4 = 0x0800;

	/** Flag 5 (unused) */
	public static final int FLAG5 = 0x0400;

	/** Key is owned by a user. */
	public static final int USER = 0x0000;

	/** Key is owned by a zone. */
	public static final int ZONE = 0x0100;

	/** Key is owned by a host. */
	public static final int HOST = 0x0200;

	/** Key owner type 3 (reserved). */
	public static final int NTYP3 = 0x0300;

	/** Key owner bitmask. */
	public static final int OWNER_MASK = 0x0300;

	/** Flag 8 (unused) */
	public static final int FLAG8 = 0x0080;

	/** Flag 9 (unused) */
	public static final int FLAG9 = 0x0040;

	/** Flag 10 (unused) */
	public static final int FLAG10 = 0x0020;

	/** Flag 11 (unused) */
	public static final int FLAG11 = 0x0010;

	/** Signatory value 0 */
	public static final int SIG0 = 0;

	/** Signatory value 1 */
	public static final int SIG1 = 1;

	/** Signatory value 2 */
	public static final int SIG2 = 2;

	/** Signatory value 3 */
	public static final int SIG3 = 3;

	/** Signatory value 4 */
	public static final int SIG4 = 4;

	/** Signatory value 5 */
	public static final int SIG5 = 5;

	/** Signatory value 6 */
	public static final int SIG6 = 6;

	/** Signatory value 7 */
	public static final int SIG7 = 7;

	/** Signatory value 8 */
	public static final int SIG8 = 8;

	/** Signatory value 9 */
	public static final int SIG9 = 9;

	/** Signatory value 10 */
	public static final int SIG10 = 10;

	/** Signatory value 11 */
	public static final int SIG11 = 11;

	/** Signatory value 12 */
	public static final int SIG12 = 12;

	/** Signatory value 13 */
	public static final int SIG13 = 13;

	/** Signatory value 14 */
	public static final int SIG14 = 14;

	/** Signatory value 15 */
	public static final int SIG15 = 15;

	private static final Mnemonic flags = new Mnemonic("KEY flags",
						      Mnemonic.CASE_UPPER);

	static {
        Flags.flags.setMaximum(0xFFFF);
        Flags.flags.setNumericAllowed(false);

        Flags.flags.add(Flags.NOCONF, "NOCONF");
        Flags.flags.add(Flags.NOAUTH, "NOAUTH");
        Flags.flags.add(Flags.NOKEY, "NOKEY");
        Flags.flags.add(Flags.FLAG2, "FLAG2");
        Flags.flags.add(Flags.EXTEND, "EXTEND");
        Flags.flags.add(Flags.FLAG4, "FLAG4");
        Flags.flags.add(Flags.FLAG5, "FLAG5");
        Flags.flags.add(Flags.USER, "USER");
        Flags.flags.add(Flags.ZONE, "ZONE");
        Flags.flags.add(Flags.HOST, "HOST");
        Flags.flags.add(Flags.NTYP3, "NTYP3");
        Flags.flags.add(Flags.FLAG8, "FLAG8");
        Flags.flags.add(Flags.FLAG9, "FLAG9");
        Flags.flags.add(Flags.FLAG10, "FLAG10");
        Flags.flags.add(Flags.FLAG11, "FLAG11");
        Flags.flags.add(Flags.SIG0, "SIG0");
        Flags.flags.add(Flags.SIG1, "SIG1");
        Flags.flags.add(Flags.SIG2, "SIG2");
        Flags.flags.add(Flags.SIG3, "SIG3");
        Flags.flags.add(Flags.SIG4, "SIG4");
        Flags.flags.add(Flags.SIG5, "SIG5");
        Flags.flags.add(Flags.SIG6, "SIG6");
        Flags.flags.add(Flags.SIG7, "SIG7");
        Flags.flags.add(Flags.SIG8, "SIG8");
        Flags.flags.add(Flags.SIG9, "SIG9");
        Flags.flags.add(Flags.SIG10, "SIG10");
        Flags.flags.add(Flags.SIG11, "SIG11");
        Flags.flags.add(Flags.SIG12, "SIG12");
        Flags.flags.add(Flags.SIG13, "SIG13");
        Flags.flags.add(Flags.SIG14, "SIG14");
        Flags.flags.add(Flags.SIG15, "SIG15");
	}

	/**
	 * Converts a textual representation of KEY flags into its
	 * numeric code.  Integers in the range 0..65535 are also accepted.
	 * @param s The textual representation of the protocol
	 * @return The protocol code, or -1 on error.
	 */
	public static int
	value(String s) {
		int value;
		try {
			value = Integer.parseInt(s);
			if (value >= 0 && value <= 0xFFFF) {
				return value;
			}
			return -1;
		} catch (NumberFormatException e) {
		}
		StringTokenizer st = new StringTokenizer(s, "|");
		value = 0;
		while (st.hasMoreTokens()) {
			int val = Flags.flags.getValue(st.nextToken());
			if (val < 0) {
				return -1;
			}
			value |= val;
		}
		return value;
	}
}

/* flags */
/** This key cannot be used for confidentiality (encryption) */
public static final int FLAG_NOCONF = KEYRecord.Flags.NOCONF;

/** This key cannot be used for authentication */
public static final int FLAG_NOAUTH = KEYRecord.Flags.NOAUTH;

/** This key cannot be used for authentication or confidentiality */
public static final int FLAG_NOKEY = KEYRecord.Flags.NOKEY;

/** A zone key */
public static final int OWNER_ZONE = KEYRecord.Flags.ZONE;

/** A host/end entity key */
public static final int OWNER_HOST = KEYRecord.Flags.HOST;

/** A user key */
public static final int OWNER_USER = KEYRecord.Flags.USER;

/* protocols */
/** Key was created for use with transaction level security */
public static final int PROTOCOL_TLS = KEYRecord.Protocol.TLS;

/** Key was created for use with email */
public static final int PROTOCOL_EMAIL = KEYRecord.Protocol.EMAIL;

/** Key was created for use with DNSSEC */
public static final int PROTOCOL_DNSSEC = KEYRecord.Protocol.DNSSEC;

/** Key was created for use with IPSEC */
public static final int PROTOCOL_IPSEC = KEYRecord.Protocol.IPSEC;

/** Key was created for use with any protocol */
public static final int PROTOCOL_ANY = KEYRecord.Protocol.ANY;

KEYRecord() {}

@Override
Record
getObject() {
	return new KEYRecord();
}

/**
 * Creates a KEY Record from the given data
 * @param flags Flags describing the key's properties
 * @param proto The protocol that the key was created for
 * @param alg The key's algorithm
 * @param key Binary data representing the key
 */
public
KEYRecord(Name name, int dclass, long ttl, int flags, int proto, int alg,
	  byte [] key)
{
	super(name, Type.KEY, dclass, ttl, flags, proto, alg, key);
}

/**
 * Creates a KEY Record from the given data
 * @param flags Flags describing the key's properties
 * @param proto The protocol that the key was created for
 * @param alg The key's algorithm
 * @param key The key as a PublicKey
 * @throws DNSSECException The PublicKey could not be converted into DNS
 * format.
 */
public
KEYRecord(Name name, int dclass, long ttl, int flags, int proto, int alg,
	  PublicKey key) throws DNSSECException
{
	super(name, Type.KEY, dclass, ttl, flags, proto, alg,
	      DNSSEC.fromPublicKey(key, alg));
    this.publicKey = key;
}

@Override
void
rdataFromString(Tokenizer st, Name origin) throws IOException {
	String flagString = st.getIdentifier();
    this.flags = KEYRecord.Flags.value(flagString);
	if (this.flags < 0)
		throw st.exception("Invalid flags: " + flagString);
	String protoString = st.getIdentifier();
    this.proto = KEYRecord.Protocol.value(protoString);
	if (this.proto < 0)
		throw st.exception("Invalid protocol: " + protoString);
	String algString = st.getIdentifier();
    this.alg = Algorithm.value(algString);
	if (this.alg < 0)
		throw st.exception("Invalid algorithm: " + algString);
	/* If this is a null KEY, there's no key data */
	if ((this.flags & KEYRecord.Flags.USE_MASK) == KEYRecord.Flags.NOKEY)
        this.key = null;
	else
        this.key = st.getBase64();
}

}
