// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.*;

/**
 * Responsible Person Record - lists the mail address of a responsible person
 * and a domain where TXT records are available.
 *
 * @author Tom Scola <tscola@research.att.com>
 * @author Brian Wellington
 */

public class RPRecord extends Record {

private static final long serialVersionUID = 8124584364211337460L;

private Name mailbox;
private Name textDomain;

RPRecord() {}

@Override
Record
getObject() {
	return new RPRecord();
}

/**
 * Creates an RP Record from the given data
 * @param mailbox The responsible person
 * @param textDomain The address where TXT records can be found
 */
public
RPRecord(Name name, int dclass, long ttl, Name mailbox, Name textDomain) {
	super(name, Type.RP, dclass, ttl);

	this.mailbox = Record.checkName("mailbox", mailbox);
	this.textDomain = Record.checkName("textDomain", textDomain);
}

@Override
void
rrFromWire(DNSInput in) throws IOException {
    this.mailbox = new Name(in);
    this.textDomain = new Name(in);
}

@Override
void
rdataFromString(Tokenizer st, Name origin) throws IOException {
    this.mailbox = st.getName(origin);
    this.textDomain = st.getName(origin);
}

/** Converts the RP Record to a String */
@Override
String
rrToString() {
	StringBuffer sb = new StringBuffer();
	sb.append(this.mailbox);
	sb.append(" ");
	sb.append(this.textDomain);
	return sb.toString();
}

/** Gets the mailbox address of the RP Record */
public Name
getMailbox() {
	return this.mailbox;
}

/** Gets the text domain info of the RP Record */
public Name
getTextDomain() {
	return this.textDomain;
}

@Override
void
rrToWire(DNSOutput out, Compression c, boolean canonical) {
    this.mailbox.toWire(out, null, canonical);
    this.textDomain.toWire(out, null, canonical);
}

}
