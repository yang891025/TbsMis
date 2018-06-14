// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * A DNS Zone.  This encapsulates all data related to a Zone, and provides
 * convenient lookup methods.
 *
 * @author Brian Wellington
 */

public class Zone implements Serializable {

private static final long serialVersionUID = -9220510891189510942L;

/** A primary zone */
public static final int PRIMARY = 1;

/** A secondary zone */
public static final int SECONDARY = 2;

private Map data;
private Name origin;
private Object originNode;
private final int dclass = DClass.IN;
private RRset NS;
private SOARecord SOA;
private boolean hasWild;

class ZoneIterator implements Iterator {
	private final Iterator zentries;
	private RRset [] current;
	private int count;
	private boolean wantLastSOA;

	ZoneIterator(boolean axfr) {
		synchronized (Zone.this) {
            this.zentries = Zone.this.data.entrySet().iterator();
		}
        this.wantLastSOA = axfr;
		RRset [] sets = Zone.this.allRRsets(Zone.this.originNode);
        this.current = new RRset[sets.length];
		for (int i = 0, j = 2; i < sets.length; i++) {
			int type = sets[i].getType();
			if (type == Type.SOA)
                this.current[0] = sets[i];
			else if (type == Type.NS)
                this.current[1] = sets[i];
			else
                this.current[j++] = sets[i];
		}
	}

	@Override
	public boolean
	hasNext() {
		return this.current != null || this.wantLastSOA;
	}

	@Override
	public Object
	next() {
		if (!this.hasNext()) {
			throw new NoSuchElementException();
		}
		if (this.current == null) {
            this.wantLastSOA = false;
			return Zone.this.oneRRset(Zone.this.originNode, Type.SOA);
		}
		Object set = this.current[this.count++];
		if (this.count == this.current.length) {
            this.current = null;
			while (this.zentries.hasNext()) {
				Entry entry = (Entry) this.zentries.next();
				if (entry.getKey().equals(Zone.this.origin))
					continue;
				RRset [] sets = Zone.this.allRRsets(entry.getValue());
				if (sets.length == 0)
					continue;
                this.current = sets;
                this.count = 0;
				break;
			}
		}
		return set;
	}

	@Override
	public void
	remove() {
		throw new UnsupportedOperationException();
	}
}

private void
validate() throws IOException {
    this.originNode = this.exactName(this.origin);
	if (this.originNode == null)
		throw new IOException(this.origin + ": no data specified");

	RRset rrset = this.oneRRset(this.originNode, Type.SOA);
	if (rrset == null || rrset.size() != 1)
		throw new IOException(this.origin +
				      ": exactly 1 SOA must be specified");
	Iterator it = rrset.rrs();
    this.SOA = (SOARecord) it.next();

    this.NS = this.oneRRset(this.originNode, Type.NS);
	if (this.NS == null)
		throw new IOException(this.origin + ": no NS set specified");
}

private final void
maybeAddRecord(Record record) throws IOException {
	int rtype = record.getType();
	Name name = record.getName();

	if (rtype == Type.SOA && !name.equals(this.origin)) {
		throw new IOException("SOA owner " + name +
				      " does not match zone origin " +
                this.origin);
	}
	if (name.subdomain(this.origin))
        this.addRecord(record);
}

/**
 * Creates a Zone from the records in the specified master file.
 * @param zone The name of the zone.
 * @param file The master file to read from.
 * @see Master
 */
public
Zone(Name zone, String file) throws IOException {
    this.data = new TreeMap();

	if (zone == null)
		throw new IllegalArgumentException("no zone name specified");
	Master m = new Master(file, zone);
	Record record;

    this.origin = zone;
	while ((record = m.nextRecord()) != null)
        this.maybeAddRecord(record);
    this.validate();
}

/**
 * Creates a Zone from an array of records.
 * @param zone The name of the zone.
 * @param records The records to add to the zone.
 * @see Master
 */
public
Zone(Name zone, Record [] records) throws IOException {
    this.data = new TreeMap();

	if (zone == null)
		throw new IllegalArgumentException("no zone name specified");
    this.origin = zone;
	for (int i = 0; i < records.length; i++)
        this.maybeAddRecord(records[i]);
    this.validate();
}

private void
fromXFR(ZoneTransferIn xfrin) throws IOException, ZoneTransferException {
    this.data = new TreeMap();

    this.origin = xfrin.getName();
	List records = xfrin.run();
	for (Iterator it = records.iterator(); it.hasNext(); ) {
		Record record = (Record) it.next();
        this.maybeAddRecord(record);
	}
	if (!xfrin.isAXFR())
		throw new IllegalArgumentException("zones can only be " +
						   "created from AXFRs");
    this.validate();
}

/**
 * Creates a Zone by doing the specified zone transfer.
 * @param xfrin The incoming zone transfer to execute.
 * @see ZoneTransferIn
 */
public
Zone(ZoneTransferIn xfrin) throws IOException, ZoneTransferException {
    this.fromXFR(xfrin);
}

/**
 * Creates a Zone by performing a zone transfer to the specified host.
 * @see ZoneTransferIn
 */
public
Zone(Name zone, int dclass, String remote)
throws IOException, ZoneTransferException
{
	ZoneTransferIn xfrin = ZoneTransferIn.newAXFR(zone, remote, null);
	xfrin.setDClass(dclass);
    this.fromXFR(xfrin);
}

/** Returns the Zone's origin */
public Name
getOrigin() {
	return this.origin;
}

/** Returns the Zone origin's NS records */
public RRset
getNS() {
	return this.NS;
}

/** Returns the Zone's SOA record */
public SOARecord
getSOA() {
	return this.SOA;
}

/** Returns the Zone's class */
public int
getDClass() {
	return this.dclass;
}

private synchronized Object
exactName(Name name) {
	return this.data.get(name);
}

private synchronized RRset []
allRRsets(Object types) {
	if (types instanceof List) {
		List typelist = (List) types;
		return (RRset []) typelist.toArray(new RRset[typelist.size()]);
	} else {
		RRset set = (RRset) types;
		return new RRset [] {set};
	}
}

private synchronized RRset
oneRRset(Object types, int type) {
	if (type == Type.ANY)
		throw new IllegalArgumentException("oneRRset(ANY)");
	if (types instanceof List) {
		List list = (List) types;
		for (int i = 0; i < list.size(); i++) {
			RRset set = (RRset) list.get(i);
			if (set.getType() == type)
				return set;
		}
	} else {
		RRset set = (RRset) types;
		if (set.getType() == type)
			return set;
	}
	return null;
}

private synchronized RRset
findRRset(Name name, int type) {
	Object types = this.exactName(name);
	if (types == null)
		return null;
	return this.oneRRset(types, type);
}

private synchronized void
addRRset(Name name, RRset rrset) {
	if (!this.hasWild && name.isWild())
        this.hasWild = true;
	Object types = this.data.get(name);
	if (types == null) {
        this.data.put(name, rrset);
		return;
	}
	int rtype = rrset.getType();
	if (types instanceof List) {
		List list = (List) types;
		for (int i = 0; i < list.size(); i++) {
			RRset set = (RRset) list.get(i);
			if (set.getType() == rtype) {
				list.set(i, rrset);
				return;
			}
		}
		list.add(rrset);
	} else {
		RRset set = (RRset) types;
		if (set.getType() == rtype)
            this.data.put(name, rrset);
		else {
			LinkedList list = new LinkedList();
			list.add(set);
			list.add(rrset);
            this.data.put(name, list);
		}
	}
}

private synchronized void
removeRRset(Name name, int type) {
	Object types = this.data.get(name);
	if (types == null) {
		return;
	}
	if (types instanceof List) {
		List list = (List) types;
		for (int i = 0; i < list.size(); i++) {
			RRset set = (RRset) list.get(i);
			if (set.getType() == type) {
				list.remove(i);
				if (list.size() == 0)
                    this.data.remove(name);
				return;
			}
		}
	} else {
		RRset set = (RRset) types;
		if (set.getType() != type)
			return;
        this.data.remove(name);
	}
}

private synchronized SetResponse
lookup(Name name, int type) {
	int labels;
	int olabels;
	int tlabels;
	RRset rrset;
	Name tname;
	Object types;
	SetResponse sr;

	if (!name.subdomain(this.origin))
		return SetResponse.ofType(SetResponse.NXDOMAIN);

	labels = name.labels();
	olabels = this.origin.labels();

	for (tlabels = olabels; tlabels <= labels; tlabels++) {
		boolean isOrigin = tlabels == olabels;
		boolean isExact = tlabels == labels;

		if (isOrigin)
			tname = this.origin;
		else if (isExact)
			tname = name;
		else
			tname = new Name(name, labels - tlabels);

		types = this.exactName(tname);
		if (types == null)
			continue;

		/* If this is a delegation, return that. */
		if (!isOrigin) {
			RRset ns = this.oneRRset(types, Type.NS);
			if (ns != null)
				return new SetResponse(SetResponse.DELEGATION,
						       ns);
		}

		/* If this is an ANY lookup, return everything. */
		if (isExact && type == Type.ANY) {
			sr = new SetResponse(SetResponse.SUCCESSFUL);
			RRset [] sets = this.allRRsets(types);
			for (int i = 0; i < sets.length; i++)
				sr.addRRset(sets[i]);
			return sr;
		}

		/*
		 * If this is the name, look for the actual type or a CNAME.
		 * Otherwise, look for a DNAME.
		 */
		if (isExact) {
			rrset = this.oneRRset(types, type);
			if (rrset != null) {
				sr = new SetResponse(SetResponse.SUCCESSFUL);
				sr.addRRset(rrset);
				return sr;
			}
			rrset = this.oneRRset(types, Type.CNAME);
			if (rrset != null)
				return new SetResponse(SetResponse.CNAME,
						       rrset);
		} else {
			rrset = this.oneRRset(types, Type.DNAME);
			if (rrset != null)
				return new SetResponse(SetResponse.DNAME,
						       rrset);
		}

		/* We found the name, but not the type. */
		if (isExact)
			return SetResponse.ofType(SetResponse.NXRRSET);
	}

	if (this.hasWild) {
		for (int i = 0; i < labels - olabels; i++) {
			tname = name.wild(i + 1);

			types = this.exactName(tname);
			if (types == null)
				continue;

			rrset = this.oneRRset(types, type);
			if (rrset != null) {
				sr = new SetResponse(SetResponse.SUCCESSFUL);
				sr.addRRset(rrset);
				return sr;
			}
		}
	}

	return SetResponse.ofType(SetResponse.NXDOMAIN);
}

/**     
 * Looks up Records in the Zone.  This follows CNAMEs and wildcards.
 * @param name The name to look up
 * @param type The type to look up
 * @return A SetResponse object
 * @see SetResponse
 */ 
public SetResponse
findRecords(Name name, int type) {
	return this.lookup(name, type);
}

/**
 * Looks up Records in the zone, finding exact matches only.
 * @param name The name to look up
 * @param type The type to look up
 * @return The matching RRset
 * @see RRset
 */ 
public RRset
findExactMatch(Name name, int type) {
	Object types = this.exactName(name);
	if (types == null)
		return null;
	return this.oneRRset(types, type);
}

/**
 * Adds an RRset to the Zone
 * @param rrset The RRset to be added
 * @see RRset
 */
public void
addRRset(RRset rrset) {
	Name name = rrset.getName();
    this.addRRset(name, rrset);
}

/**
 * Adds a Record to the Zone
 * @param r The record to be added
 * @see Record
 */
public void
addRecord(Record r) {
	Name name = r.getName();
	int rtype = r.getRRsetType();
	synchronized (this) {
		RRset rrset = this.findRRset(name, rtype);
		if (rrset == null) {
			rrset = new RRset(r);
            this.addRRset(name, rrset);
		} else {
			rrset.addRR(r);
		}
	}
}

/**
 * Removes a record from the Zone
 * @param r The record to be removed
 * @see Record
 */
public void
removeRecord(Record r) {
	Name name = r.getName();
	int rtype = r.getRRsetType();
	synchronized (this) {
		RRset rrset = this.findRRset(name, rtype);
		if (rrset == null)
			return;
		if (rrset.size() == 1 && rrset.first().equals(r))
            this.removeRRset(name, rtype);
		else
			rrset.deleteRR(r);
	}
}

/**
 * Returns an Iterator over the RRsets in the zone.
 */
public Iterator
iterator() {
	return new ZoneIterator(false);
}

/**
 * Returns an Iterator over the RRsets in the zone that can be used to
 * construct an AXFR response.  This is identical to {@link #iterator} except
 * that the SOA is returned at the end as well as the beginning.
 */
public Iterator
AXFR() {
	return new ZoneIterator(true);
}

private void
nodeToString(StringBuffer sb, Object node) {
	RRset [] sets = this.allRRsets(node);
	for (int i = 0; i < sets.length; i++) {
		RRset rrset = sets[i];
		Iterator it = rrset.rrs();
		while (it.hasNext())
			sb.append(it.next() + "\n");
		it = rrset.sigs();
		while (it.hasNext())
			sb.append(it.next() + "\n");
	}
}

/**
 * Returns the contents of the Zone in master file format.
 */
public synchronized String
toMasterFile() {
	Iterator zentries = this.data.entrySet().iterator();
	StringBuffer sb = new StringBuffer();
    this.nodeToString(sb, this.originNode);
	while (zentries.hasNext()) {
		Entry entry = (Entry) zentries.next();
		if (!this.origin.equals(entry.getKey()))
            this.nodeToString(sb, entry.getValue());
	}
	return sb.toString();
}

/**
 * Returns the contents of the Zone as a string (in master file format).
 */
@Override
public String
toString() {
	return this.toMasterFile();
}

}
