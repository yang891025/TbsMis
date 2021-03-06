// Copyright (c) 2000-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.*;

/**
 * Name Authority Pointer Record  - specifies rewrite rule, that when applied
 * to an existing string will produce a new domain.
 *
 * @author Chuck Santos
 */

public class NAPTRRecord extends Record {

private static final long serialVersionUID = 5191232392044947002L;

private int order, preference;
private byte [] flags, service, regexp;
private Name replacement;

NAPTRRecord() {}

@Override
Record
getObject() {
	return new NAPTRRecord();
}

/**
 * Creates an NAPTR Record from the given data
 * @param order The order of this NAPTR.  Records with lower order are
 * preferred.
 * @param preference The preference, used to select between records at the
 * same order.
 * @param flags The control aspects of the NAPTRRecord.
 * @param service The service or protocol available down the rewrite path.
 * @param regexp The regular/substitution expression.
 * @param replacement The domain-name to query for the next DNS resource
 * record, depending on the value of the flags field.
 * @throws IllegalArgumentException One of the strings has invalid escapes
 */
public
NAPTRRecord(Name name, int dclass, long ttl, int order, int preference,
	    String flags, String service, String regexp, Name replacement)
{
	super(name, Type.NAPTR, dclass, ttl);
	this.order = Record.checkU16("order", order);
	this.preference = Record.checkU16("preference", preference);
	try {
		this.flags = Record.byteArrayFromString(flags);
		this.service = Record.byteArrayFromString(service);
		this.regexp = Record.byteArrayFromString(regexp);
	}
	catch (TextParseException e) {
		throw new IllegalArgumentException(e.getMessage());
	}
	this.replacement = Record.checkName("replacement", replacement);
}

@Override
void
rrFromWire(DNSInput in) throws IOException {
    this.order = in.readU16();
    this.preference = in.readU16();
    this.flags = in.readCountedString();
    this.service = in.readCountedString();
    this.regexp = in.readCountedString();
    this.replacement = new Name(in);
}

@Override
void
rdataFromString(Tokenizer st, Name origin) throws IOException {
    this.order = st.getUInt16();
    this.preference = st.getUInt16();
	try {
        this.flags = Record.byteArrayFromString(st.getString());
        this.service = Record.byteArrayFromString(st.getString());
        this.regexp = Record.byteArrayFromString(st.getString());
	}
	catch (TextParseException e) {
		throw st.exception(e.getMessage());
	}
    this.replacement = st.getName(origin);
}

/** Converts rdata to a String */
@Override
String
rrToString() {
	StringBuffer sb = new StringBuffer();
	sb.append(this.order);
	sb.append(" ");
	sb.append(this.preference);
	sb.append(" ");
	sb.append(Record.byteArrayToString(this.flags, true));
	sb.append(" ");
	sb.append(Record.byteArrayToString(this.service, true));
	sb.append(" ");
	sb.append(Record.byteArrayToString(this.regexp, true));
	sb.append(" ");
	sb.append(this.replacement);
	return sb.toString();
}

/** Returns the order */
public int
getOrder() {
	return this.order;
}

/** Returns the preference */
public int
getPreference() {
	return this.preference;
}

/** Returns flags */
public String
getFlags() {
	return Record.byteArrayToString(this.flags, false);
}

/** Returns service */
public String
getService() {
	return Record.byteArrayToString(this.service, false);
}

/** Returns regexp */
public String
getRegexp() {
	return Record.byteArrayToString(this.regexp, false);
}

/** Returns the replacement domain-name */
public Name
getReplacement() {
	return this.replacement;
}

@Override
void
rrToWire(DNSOutput out, Compression c, boolean canonical) {
	out.writeU16(this.order);
	out.writeU16(this.preference);
	out.writeCountedString(this.flags);
	out.writeCountedString(this.service);
	out.writeCountedString(this.regexp);
    this.replacement.toWire(out, null, canonical);
}

@Override
public Name
getAdditionalName() {
	return this.replacement;
}

}
