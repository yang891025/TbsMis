// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.*;

/**
 * Next SECure name - this record contains the following name in an
 * ordered list of names in the zone, and a set of types for which
 * records exist for this name.  The presence of this record in a response
 * signifies a negative response from a DNSSEC-signed zone.
 *
 * This replaces the NXT record.
 *
 * @author Brian Wellington
 * @author David Blacka
 */

public class NSECRecord extends Record {

private static final long serialVersionUID = -5165065768816265385L;

private Name next;
private TypeBitmap types;

NSECRecord() {}

@Override
Record
getObject() {
	return new NSECRecord();
}

/**
 * Creates an NSEC Record from the given data.
 * @param next The following name in an ordered list of the zone
 * @param types An array containing the types present.
 */
public
NSECRecord(Name name, int dclass, long ttl, Name next, int [] types) {
	super(name, Type.NSEC, dclass, ttl);
	this.next = Record.checkName("next", next);
	for (int i = 0; i < types.length; i++) {
		Type.check(types[i]);
	}
	this.types = new TypeBitmap(types);
}

@Override
void
rrFromWire(DNSInput in) throws IOException {
    this.next = new Name(in);
    this.types = new TypeBitmap(in);
}

@Override
void
rrToWire(DNSOutput out, Compression c, boolean canonical) {
	// Note: The next name is not lowercased.
    this.next.toWire(out, null, false);
    this.types.toWire(out);
}

@Override
void
rdataFromString(Tokenizer st, Name origin) throws IOException {
    this.next = st.getName(origin);
    this.types = new TypeBitmap(st);
}

/** Converts rdata to a String */
@Override
String
rrToString()
{
	StringBuffer sb = new StringBuffer();
	sb.append(this.next);
	if (!this.types.empty()) {
		sb.append(' ');
		sb.append(this.types);
	}
	return sb.toString();
}

/** Returns the next name */
public Name
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
hasType(int type) {
	return this.types.contains(type);
}

}
