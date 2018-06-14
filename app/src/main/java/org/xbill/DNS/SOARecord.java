// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.*;

/**
 * Start of Authority - describes properties of a zone.
 *
 * @author Brian Wellington
 */

public class SOARecord extends Record {

private static final long serialVersionUID = 1049740098229303931L;

private Name host, admin;
private long serial, refresh, retry, expire, minimum;

SOARecord() {}

@Override
Record
getObject() {
	return new SOARecord();
}

/**
 * Creates an SOA Record from the given data
 * @param host The primary name server for the zone
 * @param admin The zone administrator's address
 * @param serial The zone's serial number
 * @param refresh The amount of time until a secondary checks for a new serial
 * number
 * @param retry The amount of time between a secondary's checks for a new
 * serial number
 * @param expire The amount of time until a secondary expires a zone
 * @param minimum The minimum TTL for records in the zone
*/
public
SOARecord(Name name, int dclass, long ttl, Name host, Name admin,
	  long serial, long refresh, long retry, long expire, long minimum)
{
	super(name, Type.SOA, dclass, ttl);
	this.host = Record.checkName("host", host);
	this.admin = Record.checkName("admin", admin);
	this.serial = Record.checkU32("serial", serial);
	this.refresh = Record.checkU32("refresh", refresh);
	this.retry = Record.checkU32("retry", retry);
	this.expire = Record.checkU32("expire", expire);
	this.minimum = Record.checkU32("minimum", minimum);
}

@Override
void
rrFromWire(DNSInput in) throws IOException {
    this.host = new Name(in);
    this.admin = new Name(in);
    this.serial = in.readU32();
    this.refresh = in.readU32();
    this.retry = in.readU32();
    this.expire = in.readU32();
    this.minimum = in.readU32();
}

@Override
void
rdataFromString(Tokenizer st, Name origin) throws IOException {
    this.host = st.getName(origin);
    this.admin = st.getName(origin);
    this.serial = st.getUInt32();
    this.refresh = st.getTTLLike();
    this.retry = st.getTTLLike();
    this.expire = st.getTTLLike();
    this.minimum = st.getTTLLike();
}

/** Convert to a String */
@Override
String
rrToString() {
	StringBuffer sb = new StringBuffer();
	sb.append(this.host);
	sb.append(" ");
	sb.append(this.admin);
	if (Options.check("multiline")) {
		sb.append(" (\n\t\t\t\t\t");
		sb.append(this.serial);
		sb.append("\t; serial\n\t\t\t\t\t");
		sb.append(this.refresh);
		sb.append("\t; refresh\n\t\t\t\t\t");
		sb.append(this.retry);
		sb.append("\t; retry\n\t\t\t\t\t");
		sb.append(this.expire);
		sb.append("\t; expire\n\t\t\t\t\t");
		sb.append(this.minimum);
		sb.append(" )\t; minimum");
	} else {
		sb.append(" ");
		sb.append(this.serial);
		sb.append(" ");
		sb.append(this.refresh);
		sb.append(" ");
		sb.append(this.retry);
		sb.append(" ");
		sb.append(this.expire);
		sb.append(" ");
		sb.append(this.minimum);
	}
	return sb.toString();
}

/** Returns the primary name server */
public Name
getHost() {  
	return this.host;
}       

/** Returns the zone administrator's address */
public Name
getAdmin() {  
	return this.admin;
}       

/** Returns the zone's serial number */
public long
getSerial() {  
	return this.serial;
}       

/** Returns the zone refresh interval */
public long
getRefresh() {  
	return this.refresh;
}       

/** Returns the zone retry interval */
public long
getRetry() {  
	return this.retry;
}       

/** Returns the time until a secondary expires a zone */
public long
getExpire() {  
	return this.expire;
}       

/** Returns the minimum TTL for records in the zone */
public long
getMinimum() {  
	return this.minimum;
}       

@Override
void
rrToWire(DNSOutput out, Compression c, boolean canonical) {
    this.host.toWire(out, c, canonical);
    this.admin.toWire(out, c, canonical);
	out.writeU32(this.serial);
	out.writeU32(this.refresh);
	out.writeU32(this.retry);
	out.writeU32(this.expire);
	out.writeU32(this.minimum);
}

}
