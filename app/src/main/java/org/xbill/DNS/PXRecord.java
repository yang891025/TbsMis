// Copyright (c) 2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.*;

/**
 * X.400 mail mapping record.
 *
 * @author Brian Wellington
 */

public class PXRecord extends Record {

private static final long serialVersionUID = 1811540008806660667L;

private int preference;
private Name map822;
private Name mapX400;

PXRecord() {}

@Override
Record
getObject() {
	return new PXRecord();
}

/**
 * Creates an PX Record from the given data
 * @param preference The preference of this mail address.
 * @param map822 The RFC 822 component of the mail address.
 * @param mapX400 The X.400 component of the mail address.
 */
public
PXRecord(Name name, int dclass, long ttl, int preference,
	 Name map822, Name mapX400)
{
	super(name, Type.PX, dclass, ttl);

	this.preference = Record.checkU16("preference", preference);
	this.map822 = Record.checkName("map822", map822);
	this.mapX400 = Record.checkName("mapX400", mapX400);
}

@Override
void
rrFromWire(DNSInput in) throws IOException {
    this.preference = in.readU16();
    this.map822 = new Name(in);
    this.mapX400 = new Name(in);
}

@Override
void
rdataFromString(Tokenizer st, Name origin) throws IOException {
    this.preference = st.getUInt16();
    this.map822 = st.getName(origin);
    this.mapX400 = st.getName(origin);
}

/** Converts the PX Record to a String */
@Override
String
rrToString() {
	StringBuffer sb = new StringBuffer();
	sb.append(this.preference);
	sb.append(" ");
	sb.append(this.map822);
	sb.append(" ");
	sb.append(this.mapX400);
	return sb.toString();
}

@Override
void
rrToWire(DNSOutput out, Compression c, boolean canonical) {
	out.writeU16(this.preference);
    this.map822.toWire(out, null, canonical);
    this.mapX400.toWire(out, null, canonical);
}

/** Gets the preference of the route. */
public int
getPreference() {
	return this.preference;
}

/** Gets the RFC 822 component of the mail address. */
public Name
getMap822() {
	return this.map822;
}

/** Gets the X.400 component of the mail address. */
public Name
getMapX400() {
	return this.mapX400;
}

}
