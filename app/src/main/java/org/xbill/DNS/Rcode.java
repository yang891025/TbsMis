// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

/**
 * Constants and functions relating to DNS rcodes (error values)
 *
 * @author Brian Wellington
 */

public final class Rcode {

private static final Mnemonic rcodes = new Mnemonic("DNS Rcode",
					      Mnemonic.CASE_UPPER);

private static final Mnemonic tsigrcodes = new Mnemonic("TSIG rcode",
						  Mnemonic.CASE_UPPER);

/** No error */
public static final int NOERROR		= 0;

/** Format error */
public static final int FORMERR		= 1;

/** Server failure */
public static final int SERVFAIL	= 2;

/** The name does not exist */
public static final int NXDOMAIN	= 3;

/** The operation requested is not implemented */
public static final int NOTIMP		= 4;

/** Deprecated synonym for NOTIMP. */
public static final int NOTIMPL		= 4;

/** The operation was refused by the server */
public static final int REFUSED		= 5;

/** The name exists */
public static final int YXDOMAIN	= 6;

/** The RRset (name, type) exists */
public static final int YXRRSET		= 7;

/** The RRset (name, type) does not exist */
public static final int NXRRSET		= 8;

/** The requestor is not authorized to perform this operation */
public static final int NOTAUTH		= 9;

/** The zone specified is not a zone */
public static final int NOTZONE		= 10;

/* EDNS extended rcodes */
/** Unsupported EDNS level */
public static final int BADVERS		= 16;

/* TSIG/TKEY only rcodes */
/** The signature is invalid (TSIG/TKEY extended error) */
public static final int BADSIG		= 16;

/** The key is invalid (TSIG/TKEY extended error) */
public static final int BADKEY		= 17;

/** The time is out of range (TSIG/TKEY extended error) */
public static final int BADTIME		= 18;

/** The mode is invalid (TKEY extended error) */
public static final int BADMODE		= 19;

static {
    Rcode.rcodes.setMaximum(0xFFF);
    Rcode.rcodes.setPrefix("RESERVED");
    Rcode.rcodes.setNumericAllowed(true);

    Rcode.rcodes.add(Rcode.NOERROR, "NOERROR");
    Rcode.rcodes.add(Rcode.FORMERR, "FORMERR");
    Rcode.rcodes.add(Rcode.SERVFAIL, "SERVFAIL");
    Rcode.rcodes.add(Rcode.NXDOMAIN, "NXDOMAIN");
    Rcode.rcodes.add(Rcode.NOTIMP, "NOTIMP");
    Rcode.rcodes.addAlias(Rcode.NOTIMP, "NOTIMPL");
    Rcode.rcodes.add(Rcode.REFUSED, "REFUSED");
    Rcode.rcodes.add(Rcode.YXDOMAIN, "YXDOMAIN");
    Rcode.rcodes.add(Rcode.YXRRSET, "YXRRSET");
    Rcode.rcodes.add(Rcode.NXRRSET, "NXRRSET");
    Rcode.rcodes.add(Rcode.NOTAUTH, "NOTAUTH");
    Rcode.rcodes.add(Rcode.NOTZONE, "NOTZONE");
    Rcode.rcodes.add(Rcode.BADVERS, "BADVERS");

    Rcode.tsigrcodes.setMaximum(0xFFFF);
    Rcode.tsigrcodes.setPrefix("RESERVED");
    Rcode.tsigrcodes.setNumericAllowed(true);
    Rcode.tsigrcodes.addAll(Rcode.rcodes);

    Rcode.tsigrcodes.add(Rcode.BADSIG, "BADSIG");
    Rcode.tsigrcodes.add(Rcode.BADKEY, "BADKEY");
    Rcode.tsigrcodes.add(Rcode.BADTIME, "BADTIME");
    Rcode.tsigrcodes.add(Rcode.BADMODE, "BADMODE");
}

private
Rcode() {}

/** Converts a numeric Rcode into a String */
public static String
string(int i) {
	return Rcode.rcodes.getText(i);
}

/** Converts a numeric TSIG extended Rcode into a String */
public static String
TSIGstring(int i) {
	return Rcode.tsigrcodes.getText(i);
}

/** Converts a String representation of an Rcode into its numeric value */
public static int
value(String s) {
	return Rcode.rcodes.getValue(s);
}

}
