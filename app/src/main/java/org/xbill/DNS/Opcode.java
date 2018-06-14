// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

/**
 * Constants and functions relating to DNS opcodes
 *
 * @author Brian Wellington
 */

public final class Opcode {

/** A standard query */
public static final int QUERY		= 0;

/** An inverse query (deprecated) */
public static final int IQUERY		= 1;

/** A server status request (not used) */
public static final int STATUS		= 2;

/**
 * A message from a primary to a secondary server to initiate a zone transfer
 */
public static final int NOTIFY		= 4;

/** A dynamic update message */
public static final int UPDATE		= 5;

private static final Mnemonic opcodes = new Mnemonic("DNS Opcode",
					       Mnemonic.CASE_UPPER);

static {
    Opcode.opcodes.setMaximum(0xF);
    Opcode.opcodes.setPrefix("RESERVED");
    Opcode.opcodes.setNumericAllowed(true);

    Opcode.opcodes.add(Opcode.QUERY, "QUERY");
    Opcode.opcodes.add(Opcode.IQUERY, "IQUERY");
    Opcode.opcodes.add(Opcode.STATUS, "STATUS");
    Opcode.opcodes.add(Opcode.NOTIFY, "NOTIFY");
    Opcode.opcodes.add(Opcode.UPDATE, "UPDATE");
}

private
Opcode() {}

/** Converts a numeric Opcode into a String */
public static String
string(int i) {
	return Opcode.opcodes.getText(i);
}

/** Converts a String representation of an Opcode into its numeric value */
public static int
value(String s) {
	return Opcode.opcodes.getValue(s);
}

}
