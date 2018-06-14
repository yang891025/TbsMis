// Copyright (c) 2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.*;

/**
 * Mailbox information Record - lists the address responsible for a mailing
 * list/mailbox and the address to receive error messages relating to the
 * mailing list/mailbox.
 *
 * @author Brian Wellington
 */

public class MINFORecord extends Record {

private static final long serialVersionUID = -3962147172340353796L;

private Name responsibleAddress;
private Name errorAddress;

MINFORecord() {}

@Override
Record
getObject() {
	return new MINFORecord();
}

/**
 * Creates an MINFO Record from the given data
 * @param responsibleAddress The address responsible for the
 * mailing list/mailbox.
 * @param errorAddress The address to receive error messages relating to the
 * mailing list/mailbox.
 */
public
MINFORecord(Name name, int dclass, long ttl,
	    Name responsibleAddress, Name errorAddress)
{
	super(name, Type.MINFO, dclass, ttl);

	this.responsibleAddress = Record.checkName("responsibleAddress",
					    responsibleAddress);
	this.errorAddress = Record.checkName("errorAddress", errorAddress);
}

@Override
void
rrFromWire(DNSInput in) throws IOException {
    this.responsibleAddress = new Name(in);
    this.errorAddress = new Name(in);
}

@Override
void
rdataFromString(Tokenizer st, Name origin) throws IOException {
    this.responsibleAddress = st.getName(origin);
    this.errorAddress = st.getName(origin);
}

/** Converts the MINFO Record to a String */
@Override
String
rrToString() {
	StringBuffer sb = new StringBuffer();
	sb.append(this.responsibleAddress);
	sb.append(" ");
	sb.append(this.errorAddress);
	return sb.toString();
}

/** Gets the address responsible for the mailing list/mailbox. */
public Name
getResponsibleAddress() {
	return this.responsibleAddress;
}

/**
 * Gets the address to receive error messages relating to the mailing
 * list/mailbox.
 */
public Name
getErrorAddress() {
	return this.errorAddress;
}

@Override
void
rrToWire(DNSOutput out, Compression c, boolean canonical) {
    this.responsibleAddress.toWire(out, null, canonical);
    this.errorAddress.toWire(out, null, canonical);
}

}
