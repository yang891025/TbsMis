// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

/**
 * Constants and functions relating to DNS classes.  This is called DClass
 * to avoid confusion with Class.
 *
 * @author Brian Wellington
 */

public final class DClass {

/** Internet */
public static final int IN		= 1;

/** Chaos network (MIT) */
public static final int CH		= 3;

/** Chaos network (MIT, alternate name) */
public static final int CHAOS		= 3;

/** Hesiod name server (MIT) */
public static final int HS		= 4;

/** Hesiod name server (MIT, alternate name) */
public static final int HESIOD		= 4;

/** Special value used in dynamic update messages */
public static final int NONE		= 254;

/** Matches any class */
public static final int ANY		= 255;

private static class DClassMnemonic extends Mnemonic {
	public
	DClassMnemonic() {
		super("DClass", Mnemonic.CASE_UPPER);
        this.setPrefix("CLASS");
	}

	@Override
	public void
	check(int val) {
		DClass.check(val);
	}
}

private static final Mnemonic classes = new DClass.DClassMnemonic();

static {
    DClass.classes.add(DClass.IN, "IN");
    DClass.classes.add(DClass.CH, "CH");
    DClass.classes.addAlias(DClass.CH, "CHAOS");
    DClass.classes.add(DClass.HS, "HS");
    DClass.classes.addAlias(DClass.HS, "HESIOD");
    DClass.classes.add(DClass.NONE, "NONE");
    DClass.classes.add(DClass.ANY, "ANY");
}

private
DClass() {}

/**
 * Checks that a numeric DClass is valid.
 * @throws InvalidDClassException The class is out of range.
 */
public static void
check(int i) {
	if (i < 0 || i > 0xFFFF)
		throw new InvalidDClassException(i);
}

/**
 * Converts a numeric DClass into a String
 * @return The canonical string representation of the class
 * @throws InvalidDClassException The class is out of range.
 */
public static String
string(int i) {
	return DClass.classes.getText(i);
}

/**
 * Converts a String representation of a DClass into its numeric value
 * @return The class code, or -1 on error.
 */
public static int
value(String s) {
	return DClass.classes.getValue(s);
}

}
