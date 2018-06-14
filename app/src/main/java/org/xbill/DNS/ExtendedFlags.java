// Copyright (c) 2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

/**
 * Constants and functions relating to EDNS flags.
 *
 * @author Brian Wellington
 */

public final class ExtendedFlags {

private static final Mnemonic extflags = new Mnemonic("EDNS Flag",
						Mnemonic.CASE_LOWER);

/** dnssec ok */
public static final int DO		= 0x8000;

static {
    ExtendedFlags.extflags.setMaximum(0xFFFF);
    ExtendedFlags.extflags.setPrefix("FLAG");
    ExtendedFlags.extflags.setNumericAllowed(true);

    ExtendedFlags.extflags.add(ExtendedFlags.DO, "do");
}

private
ExtendedFlags() {}

/** Converts a numeric extended flag into a String */
public static String
string(int i) {
	return ExtendedFlags.extflags.getText(i);
}

/**
 * Converts a textual representation of an extended flag into its numeric
 * value
 */
public static int
value(String s) {
	return ExtendedFlags.extflags.getValue(s);
}

}
