// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.util.HashMap;

/**
 * Constants and functions relating to DNS Types
 *
 * @author Brian Wellington
 */

public final class Type {

/** Address */
public static final int A		= 1;

/** Name server */
public static final int NS		= 2;

/** Mail destination */
public static final int MD		= 3;

/** Mail forwarder */
public static final int MF		= 4;

/** Canonical name (alias) */
public static final int CNAME		= 5;

/** Start of authority */
public static final int SOA		= 6;

/** Mailbox domain name */
public static final int MB		= 7;

/** Mail group member */
public static final int MG		= 8;

/** Mail rename name */
public static final int MR		= 9;

/** Null record */
public static final int NULL		= 10;

/** Well known services */
public static final int WKS		= 11;

/** Domain name pointer */
public static final int PTR		= 12;

/** Host information */
public static final int HINFO		= 13;

/** Mailbox information */
public static final int MINFO		= 14;

/** Mail routing information */
public static final int MX		= 15;

/** Text strings */
public static final int TXT		= 16;

/** Responsible person */
public static final int RP		= 17;

/** AFS cell database */
public static final int AFSDB		= 18;

/** X.25 calling address */
public static final int X25		= 19;

/** ISDN calling address */
public static final int ISDN		= 20;

/** Router */
public static final int RT		= 21;

/** NSAP address */
public static final int NSAP		= 22;

/** Reverse NSAP address (deprecated) */
public static final int NSAP_PTR	= 23;

/** Signature */
public static final int SIG		= 24;

/** Key */
public static final int KEY		= 25;

/** X.400 mail mapping */
public static final int PX		= 26;

/** Geographical position (withdrawn) */
public static final int GPOS		= 27;

/** IPv6 address */
public static final int AAAA		= 28;

/** Location */
public static final int LOC		= 29;

/** Next valid name in zone */
public static final int NXT		= 30;

/** Endpoint identifier */
public static final int EID		= 31;

/** Nimrod locator */
public static final int NIMLOC		= 32;

/** Server selection */
public static final int SRV		= 33;

/** ATM address */
public static final int ATMA		= 34;

/** Naming authority pointer */
public static final int NAPTR		= 35;

/** Key exchange */
public static final int KX		= 36;

/** Certificate */
public static final int CERT		= 37;

/** IPv6 address (experimental) */
public static final int A6		= 38;

/** Non-terminal name redirection */
public static final int DNAME		= 39;

/** Options - contains EDNS metadata */
public static final int OPT		= 41;

/** Address Prefix List */
public static final int APL		= 42;

/** Delegation Signer */
public static final int DS		= 43;

/** SSH Key Fingerprint */
public static final int SSHFP		= 44;

/** IPSEC key */
public static final int IPSECKEY	= 45;

/** Resource Record Signature */
public static final int RRSIG		= 46;

/** Next Secure Name */
public static final int NSEC		= 47;

/** DNSSEC Key */
public static final int DNSKEY		= 48;

/** Dynamic Host Configuration Protocol (DHCP) ID */
public static final int DHCID		= 49;

/** Next SECure, 3rd edition, RFC 5155 */
public static final int NSEC3		= 50;

public static final int NSEC3PARAM	= 51;

/** Sender Policy Framework (experimental) */
public static final int SPF		= 99;

/** Transaction key - used to compute a shared secret or exchange a key */
public static final int TKEY		= 249;

/** Transaction signature */
public static final int TSIG		= 250;

/** Incremental zone transfer */
public static final int IXFR		= 251;

/** Zone transfer */
public static final int AXFR		= 252;

/** Transfer mailbox records */
public static final int MAILB		= 253;

/** Transfer mail agent records */
public static final int MAILA		= 254;

/** Matches any type */
public static final int ANY		= 255;

/** DNSSEC Lookaside Validation, RFC 4431 . */
public static final int DLV		= 32769;


private static class TypeMnemonic extends Mnemonic {
	private final HashMap objects;

	public
	TypeMnemonic() {
		super("Type", Mnemonic.CASE_UPPER);
        this.setPrefix("TYPE");
        this.objects = new HashMap();
	}

	public void
	add(int val, String str, Record proto) {
		add(val, str);
        this.objects.put(Mnemonic.toInteger(val), proto);
	}
	
	@Override
	public void
	check(int val) {
		Type.check(val);
	}

	public Record
	getProto(int val) {
        this.check(val);
		return (Record) this.objects.get(Mnemonic.toInteger(val));
	}
}

private static final Type.TypeMnemonic types = new Type.TypeMnemonic();

static {
    Type.types.add(Type.A, "A", new ARecord());
    Type.types.add(Type.NS, "NS", new NSRecord());
    Type.types.add(Type.MD, "MD", new MDRecord());
    Type.types.add(Type.MF, "MF", new MFRecord());
    Type.types.add(Type.CNAME, "CNAME", new CNAMERecord());
    Type.types.add(Type.SOA, "SOA", new SOARecord());
    Type.types.add(Type.MB, "MB", new MBRecord());
    Type.types.add(Type.MG, "MG", new MGRecord());
    Type.types.add(Type.MR, "MR", new MRRecord());
    Type.types.add(Type.NULL, "NULL", new NULLRecord());
    Type.types.add(Type.WKS, "WKS", new WKSRecord());
    Type.types.add(Type.PTR, "PTR", new PTRRecord());
    Type.types.add(Type.HINFO, "HINFO", new HINFORecord());
    Type.types.add(Type.MINFO, "MINFO", new MINFORecord());
    Type.types.add(Type.MX, "MX", new MXRecord());
    Type.types.add(Type.TXT, "TXT", new TXTRecord());
    Type.types.add(Type.RP, "RP", new RPRecord());
    Type.types.add(Type.AFSDB, "AFSDB", new AFSDBRecord());
    Type.types.add(Type.X25, "X25", new X25Record());
    Type.types.add(Type.ISDN, "ISDN", new ISDNRecord());
    Type.types.add(Type.RT, "RT", new RTRecord());
    Type.types.add(Type.NSAP, "NSAP", new NSAPRecord());
    Type.types.add(Type.NSAP_PTR, "NSAP-PTR", new NSAP_PTRRecord());
    Type.types.add(Type.SIG, "SIG", new SIGRecord());
    Type.types.add(Type.KEY, "KEY", new KEYRecord());
    Type.types.add(Type.PX, "PX", new PXRecord());
    Type.types.add(Type.GPOS, "GPOS", new GPOSRecord());
    Type.types.add(Type.AAAA, "AAAA", new AAAARecord());
    Type.types.add(Type.LOC, "LOC", new LOCRecord());
    Type.types.add(Type.NXT, "NXT", new NXTRecord());
    Type.types.add(Type.EID, "EID");
    Type.types.add(Type.NIMLOC, "NIMLOC");
    Type.types.add(Type.SRV, "SRV", new SRVRecord());
    Type.types.add(Type.ATMA, "ATMA");
    Type.types.add(Type.NAPTR, "NAPTR", new NAPTRRecord());
    Type.types.add(Type.KX, "KX", new KXRecord());
    Type.types.add(Type.CERT, "CERT", new CERTRecord());
    Type.types.add(Type.A6, "A6", new A6Record());
    Type.types.add(Type.DNAME, "DNAME", new DNAMERecord());
    Type.types.add(Type.OPT, "OPT", new OPTRecord());
    Type.types.add(Type.APL, "APL", new APLRecord());
    Type.types.add(Type.DS, "DS", new DSRecord());
    Type.types.add(Type.SSHFP, "SSHFP", new SSHFPRecord());
    Type.types.add(Type.IPSECKEY, "IPSECKEY", new IPSECKEYRecord());
    Type.types.add(Type.RRSIG, "RRSIG", new RRSIGRecord());
    Type.types.add(Type.NSEC, "NSEC", new NSECRecord());
    Type.types.add(Type.DNSKEY, "DNSKEY", new DNSKEYRecord());
    Type.types.add(Type.DHCID, "DHCID", new DHCIDRecord());
    Type.types.add(Type.NSEC3, "NSEC3", new NSEC3Record());
    Type.types.add(Type.NSEC3PARAM, "NSEC3PARAM", new NSEC3PARAMRecord());
    Type.types.add(Type.SPF, "SPF", new SPFRecord());
    Type.types.add(Type.TKEY, "TKEY", new TKEYRecord());
    Type.types.add(Type.TSIG, "TSIG", new TSIGRecord());
    Type.types.add(Type.IXFR, "IXFR");
    Type.types.add(Type.AXFR, "AXFR");
    Type.types.add(Type.MAILB, "MAILB");
    Type.types.add(Type.MAILA, "MAILA");
    Type.types.add(Type.ANY, "ANY");
    Type.types.add(Type.DLV, "DLV", new DLVRecord());
}

private
Type() {
}

/**
 * Checks that a numeric Type is valid.
 * @throws InvalidTypeException The type is out of range.
 */
public static void
check(int val) {
	if (val < 0 || val > 0xFFFF)
		throw new InvalidTypeException(val);
}

/**
 * Converts a numeric Type into a String
 * @param val The type value.
 * @return The canonical string representation of the type
 * @throws InvalidTypeException The type is out of range.
 */
public static String
string(int val) {
	return Type.types.getText(val);
}

/**
 * Converts a String representation of an Type into its numeric value.
 * @param s The string representation of the type
 * @param numberok Whether a number will be accepted or not.
 * @return The type code, or -1 on error.
 */
public static int
value(String s, boolean numberok) {
	int val = Type.types.getValue(s);
	if (val == -1 && numberok) {
		val = Type.types.getValue("TYPE" + s);
	}
	return val;
}

/**
 * Converts a String representation of an Type into its numeric value
 * @return The type code, or -1 on error.
 */
public static int
value(String s) {
	return Type.value(s, false);
}

static Record
getProto(int val) {
	return Type.types.getProto(val);
}

/** Is this type valid for a record (a non-meta type)? */
public static boolean
isRR(int type) {
	switch (type) {
		case Type.OPT:
		case Type.TKEY:
		case Type.TSIG:
		case Type.IXFR:
		case Type.AXFR:
		case Type.MAILB:
		case Type.MAILA:
		case Type.ANY:
			return false;
		default:
			return true;
	}
}

}
