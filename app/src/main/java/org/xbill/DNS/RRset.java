// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.Serializable;
import java.util.*;

/**
 * A set of Records with the same name, type, and class.  Also included
 * are all RRSIG records signing the data records.
 * @see Record
 * @see RRSIGRecord 
 *
 * @author Brian Wellington
 */

public class RRset implements Serializable {

private static final long serialVersionUID = -3270249290171239695L;

/*
 * rrs contains both normal and RRSIG records, with the RRSIG records
 * at the end.
 */
private final List rrs;
private short nsigs;
private short position;

/** Creates an empty RRset */
public
RRset() {
    this.rrs = new ArrayList(1);
    this.nsigs = 0;
    this.position = 0;
}

/** Creates an RRset and sets its contents to the specified record */
public
RRset(Record record) {
	this();
    this.safeAddRR(record);
}

/** Creates an RRset with the contents of an existing RRset */
public
RRset(RRset rrset) {
	synchronized (rrset) {
        this.rrs = (List) ((ArrayList)rrset.rrs).clone();
        this.nsigs = rrset.nsigs;
        this.position = rrset.position;
	}
}

private void
safeAddRR(Record r) {
	if (!(r instanceof RRSIGRecord)) {
		if (this.nsigs == 0)
            this.rrs.add(r);
		else
            this.rrs.add(this.rrs.size() - this.nsigs, r);
	} else {
        this.rrs.add(r);
        this.nsigs++;
	}
}

/** Adds a Record to an RRset */
public synchronized void
addRR(Record r) {
	if (this.rrs.size() == 0) {
        this.safeAddRR(r);
		return;
	}
	Record first = this.first();
	if (!r.sameRRset(first))
		throw new IllegalArgumentException("record does not match " +
						   "rrset");

	if (r.getTTL() != first.getTTL()) {
		if (r.getTTL() > first.getTTL()) {
			r = r.cloneRecord();
			r.setTTL(first.getTTL());
		} else {
			for (int i = 0; i < this.rrs.size(); i++) {
				Record tmp = (Record) this.rrs.get(i);
				tmp = tmp.cloneRecord();
				tmp.setTTL(r.getTTL());
                this.rrs.set(i, tmp);
			}
		}
	}

	if (!this.rrs.contains(r))
        this.safeAddRR(r);
}

/** Deletes a Record from an RRset */
public synchronized void
deleteRR(Record r) {
	if (this.rrs.remove(r) && r instanceof RRSIGRecord)
        this.nsigs--;
}

/** Deletes all Records from an RRset */
public synchronized void
clear() {
    this.rrs.clear();
    this.position = 0;
    this.nsigs = 0;
}

private synchronized Iterator
iterator(boolean data, boolean cycle) {
	int size, start, total;

	total = this.rrs.size();

	if (data)
		size = total - this.nsigs;
	else
		size = this.nsigs;
	if (size == 0)
		return Collections.EMPTY_LIST.iterator();

	if (data) {
		if (!cycle)
			start = 0;
		else {
			if (this.position >= size)
                this.position = 0;
			start = this.position++;
		}
	} else {
		start = total - this.nsigs;
	}

	List list = new ArrayList(size);
	if (data) {
		list.addAll(this.rrs.subList(start, size));
		if (start != 0)
			list.addAll(this.rrs.subList(0, start));
	} else {
		list.addAll(this.rrs.subList(start, total));
	}

	return list.iterator();
}

/**
 * Returns an Iterator listing all (data) records.
 * @param cycle If true, cycle through the records so that each Iterator will
 * start with a different record.
 */
public synchronized Iterator
rrs(boolean cycle) {
	return this.iterator(true, cycle);
}

/**
 * Returns an Iterator listing all (data) records.  This cycles through
 * the records, so each Iterator will start with a different record.
 */
public synchronized Iterator
rrs() {
	return this.iterator(true, true);
}

/** Returns an Iterator listing all signature records */
public synchronized Iterator
sigs() {
	return this.iterator(false, false);
}

/** Returns the number of (data) records */
public synchronized int
size() {
	return this.rrs.size() - this.nsigs;
}

/**
 * Returns the name of the records
 * @see Name
 */
public Name
getName() {
	return this.first().getName();
}

/**
 * Returns the type of the records
 * @see Type
 */
public int
getType() {
	return this.first().getRRsetType();
}

/**
 * Returns the class of the records
 * @see DClass
 */
public int
getDClass() {
	return this.first().getDClass();
}

/** Returns the ttl of the records */
public synchronized long
getTTL() {
	return this.first().getTTL();
}

/**
 * Returns the first record
 * @throws IllegalStateException if the rrset is empty
 */
public synchronized Record
first() {
	if (this.rrs.size() == 0)
		throw new IllegalStateException("rrset is empty");
	return (Record) this.rrs.get(0);
}

private String
iteratorToString(Iterator it) {
	StringBuffer sb = new StringBuffer();
	while (it.hasNext()) {
		Record rr = (Record) it.next();
		sb.append("[");
		sb.append(rr.rdataToString());
		sb.append("]");
		if (it.hasNext())
			sb.append(" ");
	}
	return sb.toString();
}

/** Converts the RRset to a String */
@Override
public String
toString() {
	if (this.rrs == null)
		return "{empty}";
	StringBuffer sb = new StringBuffer();
	sb.append("{ ");
	sb.append(this.getName() + " ");
	sb.append(this.getTTL() + " ");
	sb.append(DClass.string(this.getDClass()) + " ");
	sb.append(Type.string(this.getType()) + " ");
	sb.append(this.iteratorToString(this.iterator(true, false)));
	if (this.nsigs > 0) {
		sb.append(" sigs: ");
		sb.append(this.iteratorToString(this.iterator(false, false)));
	}
	sb.append(" }");
	return sb.toString();
}

}
