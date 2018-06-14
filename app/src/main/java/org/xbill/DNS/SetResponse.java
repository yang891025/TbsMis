// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.util.*;

/**
 * The Response from a query to Cache.lookupRecords() or Zone.findRecords()
 * @see Cache
 * @see Zone
 *
 * @author Brian Wellington
 */

public class SetResponse {

/**
 * The Cache contains no information about the requested name/type
 */
static final int UNKNOWN	= 0;

/**
 * The Zone does not contain the requested name, or the Cache has
 * determined that the name does not exist.
 */
static final int NXDOMAIN	= 1;

/**
 * The Zone contains the name, but no data of the requested type,
 * or the Cache has determined that the name exists and has no data
 * of the requested type.
 */
static final int NXRRSET	= 2;

/**
 * A delegation enclosing the requested name was found.
 */
static final int DELEGATION	= 3;

/**
 * The Cache/Zone found a CNAME when looking for the name.
 * @see CNAMERecord
 */
static final int CNAME		= 4;

/**
 * The Cache/Zone found a DNAME when looking for the name.
 * @see DNAMERecord
 */
static final int DNAME		= 5;

/**
 * The Cache/Zone has successfully answered the question for the
 * requested name/type/class.
 */
static final int SUCCESSFUL	= 6;

private static final SetResponse unknown = new SetResponse(SetResponse.UNKNOWN);
private static final SetResponse nxdomain = new SetResponse(SetResponse.NXDOMAIN);
private static final SetResponse nxrrset = new SetResponse(SetResponse.NXRRSET);

private int type;
private Object data;

private
SetResponse() {}

SetResponse(int type, RRset rrset) {
	if (type < 0 || type > 6)
		throw new IllegalArgumentException("invalid type");
	this.type = type;
    data = rrset;
}

SetResponse(int type) {
	if (type < 0 || type > 6)
		throw new IllegalArgumentException("invalid type");
	this.type = type;
    data = null;
}

static SetResponse
ofType(int type) {
	switch (type) {
		case SetResponse.UNKNOWN:
			return SetResponse.unknown;
		case SetResponse.NXDOMAIN:
			return SetResponse.nxdomain;
		case SetResponse.NXRRSET:
			return SetResponse.nxrrset;
		case SetResponse.DELEGATION:
		case SetResponse.CNAME:
		case SetResponse.DNAME:
		case SetResponse.SUCCESSFUL:
			SetResponse sr = new SetResponse();
			sr.type = type;
			sr.data = null;
			return sr;
		default:
			throw new IllegalArgumentException("invalid type");
	}
}

void
addRRset(RRset rrset) {
	if (this.data == null)
        this.data = new ArrayList();
	List l = (List) this.data;
	l.add(rrset);
}

/** Is the answer to the query unknown? */
public boolean
isUnknown() {
	return this.type == SetResponse.UNKNOWN;
}

/** Is the answer to the query that the name does not exist? */
public boolean
isNXDOMAIN() {
	return this.type == SetResponse.NXDOMAIN;
}

/** Is the answer to the query that the name exists, but the type does not? */
public boolean
isNXRRSET() {
	return this.type == SetResponse.NXRRSET;
}

/** Is the result of the lookup that the name is below a delegation? */
public boolean
isDelegation() {
	return this.type == SetResponse.DELEGATION;
}

/** Is the result of the lookup a CNAME? */
public boolean
isCNAME() {
	return this.type == SetResponse.CNAME;
}

/** Is the result of the lookup a DNAME? */
public boolean
isDNAME() {
	return this.type == SetResponse.DNAME;
}

/** Was the query successful? */
public boolean
isSuccessful() {
	return this.type == SetResponse.SUCCESSFUL;
}

/** If the query was successful, return the answers */
public RRset []
answers() {
	if (this.type != SetResponse.SUCCESSFUL)
		return null;
	List l = (List) this.data;
	return (RRset []) l.toArray(new RRset[l.size()]);
}

/**
 * If the query encountered a CNAME, return it.
 */
public CNAMERecord
getCNAME() {
	return (CNAMERecord)((RRset) this.data).first();
}

/**
 * If the query encountered a DNAME, return it.
 */
public DNAMERecord
getDNAME() {
	return (DNAMERecord)((RRset) this.data).first();
}

/**
 * If the query hit a delegation point, return the NS set.
 */
public RRset
getNS() {
	return (RRset) this.data;
}

/** Prints the value of the SetResponse */
@Override
public String
toString() {
	switch (this.type) {
		case SetResponse.UNKNOWN:		return "unknown";
		case SetResponse.NXDOMAIN:		return "NXDOMAIN";
		case SetResponse.NXRRSET:		return "NXRRSET";
		case SetResponse.DELEGATION:	return "delegation: " + this.data;
		case SetResponse.CNAME:		return "CNAME: " + this.data;
		case SetResponse.DNAME:		return "DNAME: " + this.data;
		case SetResponse.SUCCESSFUL:	return "successful";
		default:		throw new IllegalStateException();
	}
}

}
