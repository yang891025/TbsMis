// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.*;

import org.xbill.DNS.DNSSEC.Algorithm;
import org.xbill.DNS.utils.*;

/**
 * Certificate Record  - Stores a certificate associated with a name.  The
 * certificate might also be associated with a KEYRecord.
 * @see KEYRecord
 *
 * @author Brian Wellington
 */

public class CERTRecord extends Record {

public static class CertificateType {
	/** Certificate type identifiers.  See RFC 4398 for more detail. */

	private CertificateType() {}

	/** PKIX (X.509v3) */
	public static final int PKIX = 1;

	/** Simple Public Key Infrastructure */
	public static final int SPKI = 2;

	/** Pretty Good Privacy */
	public static final int PGP = 3;

	/** URL of an X.509 data object */
	public static final int IPKIX = 4;

	/** URL of an SPKI certificate */
	public static final int ISPKI = 5;

	/** Fingerprint and URL of an OpenPGP packet */
	public static final int IPGP = 6;

	/** Attribute Certificate */
	public static final int ACPKIX = 7;

	/** URL of an Attribute Certificate */
	public static final int IACPKIX = 8;

	/** Certificate format defined by URI */
	public static final int URI = 253;

	/** Certificate format defined by OID */
	public static final int OID = 254;

	private static final Mnemonic types = new Mnemonic("Certificate type",
						     Mnemonic.CASE_UPPER);

	static {
        CertificateType.types.setMaximum(0xFFFF);
        CertificateType.types.setNumericAllowed(true);

        CertificateType.types.add(CertificateType.PKIX, "PKIX");
        CertificateType.types.add(CertificateType.SPKI, "SPKI");
        CertificateType.types.add(CertificateType.PGP, "PGP");
        CertificateType.types.add(CertificateType.PKIX, "IPKIX");
        CertificateType.types.add(CertificateType.SPKI, "ISPKI");
        CertificateType.types.add(CertificateType.PGP, "IPGP");
        CertificateType.types.add(CertificateType.PGP, "ACPKIX");
        CertificateType.types.add(CertificateType.PGP, "IACPKIX");
        CertificateType.types.add(CertificateType.URI, "URI");
        CertificateType.types.add(CertificateType.OID, "OID");
	}

	/**
	 * Converts a certificate type into its textual representation
	 */
	public static String
	string(int type) {
		return CertificateType.types.getText(type);
	}

	/**
	 * Converts a textual representation of an certificate type into its
	 * numeric code.  Integers in the range 0..65535 are also accepted.
	 * @param s The textual representation of the algorithm
	 * @return The algorithm code, or -1 on error.
	 */
	public static int
	value(String s) {
		return CertificateType.types.getValue(s);
	}
}

/** PKIX (X.509v3) */
public static final int PKIX = CERTRecord.CertificateType.PKIX;

/** Simple Public Key Infrastructure  */
public static final int SPKI = CERTRecord.CertificateType.SPKI;

/** Pretty Good Privacy */
public static final int PGP = CERTRecord.CertificateType.PGP;

/** Certificate format defined by URI */
public static final int URI = CERTRecord.CertificateType.URI;

/** Certificate format defined by IOD */
public static final int OID = CERTRecord.CertificateType.OID;

private static final long serialVersionUID = 4763014646517016835L;

private int certType, keyTag;
private int alg;
private byte [] cert;

CERTRecord() {}

@Override
Record
getObject() {
	return new CERTRecord();
}

/**
 * Creates a CERT Record from the given data
 * @param certType The type of certificate (see constants)
 * @param keyTag The ID of the associated KEYRecord, if present
 * @param alg The algorithm of the associated KEYRecord, if present
 * @param cert Binary data representing the certificate
 */
public
CERTRecord(Name name, int dclass, long ttl, int certType, int keyTag,
	   int alg, byte []  cert)
{
	super(name, Type.CERT, dclass, ttl);
	this.certType = Record.checkU16("certType", certType);
	this.keyTag = Record.checkU16("keyTag", keyTag);
	this.alg = Record.checkU8("alg", alg);
	this.cert = cert;
}

@Override
void
rrFromWire(DNSInput in) throws IOException {
    this.certType = in.readU16();
    this.keyTag = in.readU16();
    this.alg = in.readU8();
    this.cert = in.readByteArray();
}

@Override
void
rdataFromString(Tokenizer st, Name origin) throws IOException {
	String certTypeString = st.getString();
    this.certType = CERTRecord.CertificateType.value(certTypeString);
	if (this.certType < 0)
		throw st.exception("Invalid certificate type: " +
				   certTypeString);
    this.keyTag = st.getUInt16();
	String algString = st.getString();
    this.alg = Algorithm.value(algString);
	if (this.alg < 0)
		throw st.exception("Invalid algorithm: " + algString);
    this.cert = st.getBase64();
}

/**
 * Converts rdata to a String
 */
@Override
String
rrToString() {
	StringBuffer sb = new StringBuffer();
	sb.append (this.certType);
	sb.append (" ");
	sb.append (this.keyTag);
	sb.append (" ");
	sb.append (this.alg);
	if (this.cert != null) {
		if (Options.check("multiline")) {
			sb.append(" (\n");
			sb.append(base64.formatString(this.cert, 64, "\t", true));
		} else {
			sb.append(" ");
			sb.append(base64.toString(this.cert));
		}
	}
	return sb.toString();
}

/**
 * Returns the type of certificate
 */
public int
getCertType() {
	return this.certType;
}

/**
 * Returns the ID of the associated KEYRecord, if present
 */
public int
getKeyTag() {
	return this.keyTag;
}

/**
 * Returns the algorithm of the associated KEYRecord, if present
 */
public int
getAlgorithm() {
	return this.alg;
}

/**
 * Returns the binary representation of the certificate
 */
public byte []
getCert() {
	return this.cert;
}

@Override
void
rrToWire(DNSOutput out, Compression c, boolean canonical) {
	out.writeU16(this.certType);
	out.writeU16(this.keyTag);
	out.writeU8(this.alg);
	out.writeByteArray(this.cert);
}

}
