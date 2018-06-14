// Copyright (c) 2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.util.HashMap;

/**
 * A utility class for converting between numeric codes and mnemonics
 * for those codes.  Mnemonics are case insensitive.
 *
 * @author Brian Wellington
 */

class Mnemonic {

private static final Integer[] cachedInts = new Integer[64];

static {
	for (int i = 0; i < Mnemonic.cachedInts.length; i++) {
        Mnemonic.cachedInts[i] = new Integer(i);
	}
}

/* Strings are case-sensitive. */
static final int CASE_SENSITIVE = 1;

/* Strings will be stored/searched for in uppercase. */
static final int CASE_UPPER = 2;

/* Strings will be stored/searched for in lowercase. */
static final int CASE_LOWER = 3;

private final HashMap strings;
private final HashMap values;
private final String description;
private final int wordcase;
private String prefix;
private int max;
private boolean numericok;

/**
 * Creates a new Mnemonic table.
 * @param description A short description of the mnemonic to use when
 * @param wordcase Whether to convert strings into uppercase, lowercase,
 * or leave them unchanged.
 * throwing exceptions.
 */
public
Mnemonic(String description, int wordcase) {
	this.description = description;
	this.wordcase = wordcase;
    this.strings = new HashMap();
    this.values = new HashMap();
    this.max = Integer.MAX_VALUE;
}

/** Sets the maximum numeric value */
public void
setMaximum(int max) {
	this.max = max;
}

/**
 * Sets the prefix to use when converting to and from values that don't
 * have mnemonics.
 */
public void
setPrefix(String prefix) {
	this.prefix = this.sanitize(prefix);
}

/**
 * Sets whether numeric values stored in strings are acceptable.
 */
public void
setNumericAllowed(boolean numeric) {
    numericok = numeric;
}

/**
 * Converts an int into a possibly cached Integer.
 */
public static Integer
toInteger(int val) {
	if (val >= 0 && val < Mnemonic.cachedInts.length)
		return Mnemonic.cachedInts[val];
	return new Integer(val);
}       

/**
 * Checks that a numeric value is within the range [0..max]
 */
public void
check(int val) {
	if (val < 0 || val > this.max) {
		throw new IllegalArgumentException(this.description + " " + val +
						   "is out of range");
	}
}

/* Converts a String to the correct case. */
private String
sanitize(String str) {
	if (this.wordcase == Mnemonic.CASE_UPPER)
		return str.toUpperCase();
	else if (this.wordcase == Mnemonic.CASE_LOWER)
		return str.toLowerCase();
	return str;
}

private int
parseNumeric(String s) {
	try {
		int val = Integer.parseInt(s);
		if (val >= 0 && val <= this.max)
			return val;
	}
	catch (NumberFormatException e) {
	}
	return -1;
}

/**
 * Defines the text representation of a numeric value.
 * @param val The numeric value
 * @param string The text string
 */
public void
add(int val, String str) {
    this.check(val);
	Integer value = Mnemonic.toInteger(val);
	str = this.sanitize(str);
    this.strings.put(str, value);
    this.values.put(value, str);
}

/**
 * Defines an additional text representation of a numeric value.  This will
 * be used by getValue(), but not getText().
 * @param val The numeric value
 * @param string The text string
 */
public void
addAlias(int val, String str) {
    this.check(val);
	Integer value = Mnemonic.toInteger(val);
	str = this.sanitize(str);
    this.strings.put(str, value);
}

/**
 * Copies all mnemonics from one table into another.
 * @param val The numeric value
 * @param string The text string
 * @throws IllegalArgumentException The wordcases of the Mnemonics do not
 * match.
 */
public void
addAll(Mnemonic source) {
	if (this.wordcase != source.wordcase)
		throw new IllegalArgumentException(source.description +
						   ": wordcases do not match");
    this.strings.putAll(source.strings);
    this.values.putAll(source.values);
}

/**
 * Gets the text mnemonic corresponding to a numeric value.
 * @param val The numeric value
 * @return The corresponding text mnemonic.
 */
public String
getText(int val) {
    this.check(val);
	String str = (String) this.values.get(Mnemonic.toInteger(val));
	if (str != null)
		return str;
	str = Integer.toString(val);
	if (this.prefix != null)
		return this.prefix + str;
	return str;
}

/**
 * Gets the numeric value corresponding to a text mnemonic.
 * @param str The text mnemonic
 * @return The corresponding numeric value, or -1 if there is none
 */
public int
getValue(String str) {
	str = this.sanitize(str);
	Integer value = (Integer) this.strings.get(str);
	if (value != null) {
		return value.intValue();
	}
	if (this.prefix != null) {
		if (str.startsWith(this.prefix)) {
			int val = this.parseNumeric(str.substring(this.prefix.length()));
			if (val >= 0) {
				return val;
			}
		}
	}
	if (this.numericok) {
		return this.parseNumeric(str);
	}
	return -1;
}

}
