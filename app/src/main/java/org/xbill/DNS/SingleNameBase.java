// Copyright (c) 2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.*;

/**
 * Implements common functionality for the many record types whose format
 * is a single name.
 *
 * @author Brian Wellington
 */

abstract class SingleNameBase extends Record {

private static final long serialVersionUID = -18595042501413L;

protected Name singleName;

protected
SingleNameBase() {}

protected
SingleNameBase(Name name, int type, int dclass, long ttl) {
	super(name, type, dclass, ttl);
}

protected
SingleNameBase(Name name, int type, int dclass, long ttl, Name singleName,
	    String description)
{
	super(name, type, dclass, ttl);
	this.singleName = Record.checkName(description, singleName);
}

@Override
void
rrFromWire(DNSInput in) throws IOException {
    this.singleName = new Name(in);
}

@Override
void
rdataFromString(Tokenizer st, Name origin) throws IOException {
    this.singleName = st.getName(origin);
}

@Override
String
rrToString() {
	return this.singleName.toString();
}

protected Name
getSingleName() {
	return this.singleName;
}

@Override
void
rrToWire(DNSOutput out, Compression c, boolean canonical) {
    this.singleName.toWire(out, null, canonical);
}

}
