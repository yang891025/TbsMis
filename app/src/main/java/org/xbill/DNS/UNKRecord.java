// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.*;

/**
 * A class implementing Records of unknown and/or unimplemented types.  This
 * class can only be initialized using static Record initializers.
 *
 * @author Brian Wellington
 */

public class UNKRecord extends Record {

private static final long serialVersionUID = -4193583311594626915L;

private byte [] data;

UNKRecord() {}

@Override
Record
getObject() {
	return new UNKRecord();
}

@Override
void
rrFromWire(DNSInput in) throws IOException {
    this.data = in.readByteArray();
}

@Override
void
rdataFromString(Tokenizer st, Name origin) throws IOException {
	throw st.exception("invalid unknown RR encoding");
}

/** Converts this Record to the String "unknown format" */
@Override
String
rrToString() {
	return Record.unknownToString(this.data);
}

/** Returns the contents of this record. */
public byte []
getData() { 
	return this.data;
}

@Override
void
rrToWire(DNSOutput out, Compression c, boolean canonical) {
	out.writeByteArray(this.data);
}

}
