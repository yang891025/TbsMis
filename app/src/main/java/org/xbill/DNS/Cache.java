// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * A cache of DNS records.  The cache obeys TTLs, so items are purged after
 * their validity period is complete.  Negative answers are cached, to
 * avoid repeated failed DNS queries.  The credibility of each RRset is
 * maintained, so that more credible records replace less credible records,
 * and lookups can specify the minimum credibility of data they are requesting.
 * @see RRset
 * @see Credibility
 *
 * @author Brian Wellington
 */

public class Cache {

private interface Element {
	boolean expired();
	int compareCredibility(int cred);
	int getType();
}

private static int
limitExpire(long ttl, long maxttl) {
	if (maxttl >= 0 && maxttl < ttl)
		ttl = maxttl;
	long expire = System.currentTimeMillis() / 1000 + ttl;
	if (expire < 0 || expire > Integer.MAX_VALUE)
		return Integer.MAX_VALUE;
	return (int)expire;
}

private static class CacheRRset extends RRset implements Cache.Element {
	private static final long serialVersionUID = 5971755205903597024L;

	int credibility;
	int expire;

	public
	CacheRRset(Record rec, int cred, long maxttl) {
        credibility = cred;
        expire = Cache.limitExpire(rec.getTTL(), maxttl);
        this.addRR(rec);
	}

	public
	CacheRRset(RRset rrset, int cred, long maxttl) {
		super(rrset);
        credibility = cred;
        expire = Cache.limitExpire(rrset.getTTL(), maxttl);
	}

	@Override
	public final boolean
	expired() {
		int now = (int)(System.currentTimeMillis() / 1000);
		return now >= this.expire;
	}

	@Override
	public final int
	compareCredibility(int cred) {
		return this.credibility - cred;
	}

	@Override
	public String
	toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(super.toString());
		sb.append(" cl = ");
		sb.append(this.credibility);
		return sb.toString();
	}
}

private static class NegativeElement implements Cache.Element {
	int type;
	Name name;
	int credibility;
	int expire;

	public
	NegativeElement(Name name, int type, SOARecord soa, int cred,
			long maxttl)
	{
		this.name = name;
		this.type = type;
		long cttl = 0;
		if (soa != null)
			cttl = soa.getMinimum();
        credibility = cred;
        expire = Cache.limitExpire(cttl, maxttl);
	}

	@Override
	public int
	getType() {
		return this.type;
	}

	@Override
	public final boolean
	expired() {
		int now = (int)(System.currentTimeMillis() / 1000);
		return now >= this.expire;
	}

	@Override
	public final int
	compareCredibility(int cred) {
		return this.credibility - cred;
	}

	@Override
	public String
	toString() {
		StringBuffer sb = new StringBuffer();
		if (this.type == 0)
			sb.append("NXDOMAIN " + this.name);
		else
			sb.append("NXRRSET " + this.name + " " + Type.string(this.type));
		sb.append(" cl = ");
		sb.append(this.credibility);
		return sb.toString();
	}
}

private static class CacheMap extends LinkedHashMap {
	private int maxsize = -1;

	CacheMap(int maxsize) {
		super(16, (float) 0.75, true);
		this.maxsize = maxsize;
	}

	int
	getMaxSize() {
		return this.maxsize;
	}

	void
	setMaxSize(int maxsize) {
		/*
		 * Note that this doesn't shrink the size of the map if
		 * the maximum size is lowered, but it should shrink as
		 * entries expire.
		 */
		this.maxsize = maxsize;
	}

	@Override
	protected boolean removeEldestEntry(Entry eldest) {
		return this.maxsize >= 0 && this.size() > this.maxsize;
	}
}

private final Cache.CacheMap data;
private int maxncache = -1;
private int maxcache = -1;
private int dclass;

private static final int defaultMaxEntries = 50000;

/**
 * Creates an empty Cache
 *
 * @param dclass The DNS class of this cache
 * @see DClass
 */
public
Cache(int dclass) {
	this.dclass = dclass;
    this.data = new Cache.CacheMap(Cache.defaultMaxEntries);
}

/**
 * Creates an empty Cache for class IN.
 * @see DClass
 */
public
Cache() {
	this(DClass.IN);
}

/**
 * Creates a Cache which initially contains all records in the specified file.
 */
public
Cache(String file) throws IOException {
    this.data = new Cache.CacheMap(Cache.defaultMaxEntries);
	Master m = new Master(file);
	Record record;
	while ((record = m.nextRecord()) != null)
        this.addRecord(record, Credibility.HINT, m);
}

private synchronized Object
exactName(Name name) {
	return this.data.get(name);
}

private synchronized void
removeName(Name name) {
    this.data.remove(name);
}

private synchronized Cache.Element []
allElements(Object types) {
	if (types instanceof List) {
		List typelist = (List) types;
		int size = typelist.size();
		return (Cache.Element []) typelist.toArray(new Cache.Element[size]);
	} else {
		Cache.Element set = (Cache.Element) types;
		return new Cache.Element[] {set};
	}
}

private synchronized Cache.Element
oneElement(Name name, Object types, int type, int minCred) {
	Cache.Element found = null;

	if (type == Type.ANY)
		throw new IllegalArgumentException("oneElement(ANY)");
	if (types instanceof List) {
		List list = (List) types;
		for (int i = 0; i < list.size(); i++) {
			Cache.Element set = (Cache.Element) list.get(i);
			if (set.getType() == type) {
				found = set;
				break;
			}
		}
	} else {
		Cache.Element set = (Cache.Element) types;
		if (set.getType() == type)
			found = set;
	}
	if (found == null)
		return null;
	if (found.expired()) {
        this.removeElement(name, type);
		return null;
	}
	if (found.compareCredibility(minCred) < 0)
		return null;
	return found;
}

private synchronized Cache.Element
findElement(Name name, int type, int minCred) {
	Object types = this.exactName(name);
	if (types == null)
		return null;
	return this.oneElement(name, types, type, minCred);
}

private synchronized void
addElement(Name name, Cache.Element element) {
	Object types = this.data.get(name);
	if (types == null) {
        this.data.put(name, element);
		return;
	}
	int type = element.getType();
	if (types instanceof List) {
		List list = (List) types;
		for (int i = 0; i < list.size(); i++) {
			Cache.Element elt = (Cache.Element) list.get(i);
			if (elt.getType() == type) {
				list.set(i, element);
				return;
			}
		}
		list.add(element);
	} else {
		Cache.Element elt = (Cache.Element) types;
		if (elt.getType() == type)
            this.data.put(name, element);
		else {
			LinkedList list = new LinkedList();
			list.add(elt);
			list.add(element);
            this.data.put(name, list);
		}
	}
}

private synchronized void
removeElement(Name name, int type) {
	Object types = this.data.get(name);
	if (types == null) {
		return;
	}
	if (types instanceof List) {
		List list = (List) types;
		for (int i = 0; i < list.size(); i++) {
			Cache.Element elt = (Cache.Element) list.get(i);
			if (elt.getType() == type) {
				list.remove(i);
				if (list.size() == 0)
                    this.data.remove(name);
				return;
			}
		}
	} else {
		Cache.Element elt = (Cache.Element) types;
		if (elt.getType() != type)
			return;
        this.data.remove(name);
	}
}

/** Empties the Cache. */
public synchronized void
clearCache() {
    this.data.clear();
}

/**
 * Adds a record to the Cache.
 * @param r The record to be added
 * @param cred The credibility of the record
 * @param o The source of the record (this could be a Message, for example)
 * @see Record
 */
public synchronized void
addRecord(Record r, int cred, Object o) {
	Name name = r.getName();
	int type = r.getRRsetType();
	if (!Type.isRR(type))
		return;
	Cache.Element element = this.findElement(name, type, cred);
	if (element == null) {
		Cache.CacheRRset crrset = new Cache.CacheRRset(r, cred, this.maxcache);
        this.addRRset(crrset, cred);
	} else if (element.compareCredibility(cred) == 0) {
		if (element instanceof Cache.CacheRRset) {
			Cache.CacheRRset crrset = (Cache.CacheRRset) element;
			crrset.addRR(r);
		}
	}
}

/**
 * Adds an RRset to the Cache.
 * @param rrset The RRset to be added
 * @param cred The credibility of these records
 * @see RRset
 */
public synchronized void
addRRset(RRset rrset, int cred) {
	long ttl = rrset.getTTL();
	Name name = rrset.getName();
	int type = rrset.getType();
	Cache.Element element = this.findElement(name, type, 0);
	if (ttl == 0) {
		if (element != null && element.compareCredibility(cred) <= 0)
            this.removeElement(name, type);
	} else {
		if (element != null && element.compareCredibility(cred) <= 0)
			element = null;
		if (element == null) {
			Cache.CacheRRset crrset;
			if (rrset instanceof Cache.CacheRRset)
				crrset = (Cache.CacheRRset) rrset;
			else
				crrset = new Cache.CacheRRset(rrset, cred, this.maxcache);
            this.addElement(name, crrset);
		}
	}
}

/**
 * Adds a negative entry to the Cache.
 * @param name The name of the negative entry
 * @param type The type of the negative entry
 * @param soa The SOA record to add to the negative cache entry, or null.
 * The negative cache ttl is derived from the SOA.
 * @param cred The credibility of the negative entry
 */
public synchronized void
addNegative(Name name, int type, SOARecord soa, int cred) {
	long ttl = 0;
	if (soa != null)
		ttl = soa.getTTL();
	Cache.Element element = this.findElement(name, type, 0);
	if (ttl == 0) {
		if (element != null && element.compareCredibility(cred) <= 0)
            this.removeElement(name, type);
	} else {
		if (element != null && element.compareCredibility(cred) <= 0)
			element = null;
		if (element == null)
            this.addElement(name, new Cache.NegativeElement(name, type,
							     soa, cred,
                    this.maxncache));
	}
}

/**
 * Finds all matching sets or something that causes the lookup to stop.
 */
protected synchronized SetResponse
lookup(Name name, int type, int minCred) {
	int labels;
	int tlabels;
	Cache.Element element;
	Name tname;
	Object types;
	SetResponse sr;

	labels = name.labels();

	for (tlabels = labels; tlabels >= 1; tlabels--) {
		boolean isRoot = tlabels == 1;
		boolean isExact = tlabels == labels;

		if (isRoot)
			tname = Name.root;
		else if (isExact)
			tname = name;
		else
			tname = new Name(name, labels - tlabels);

		types = this.data.get(tname);
		if (types == null)
			continue;

		/* If this is an ANY lookup, return everything. */
		if (isExact && type == Type.ANY) {
			sr = new SetResponse(SetResponse.SUCCESSFUL);
			Cache.Element [] elements = this.allElements(types);
			int added = 0;
			for (int i = 0; i < elements.length; i++) {
				element = elements[i];
				if (element.expired()) {
                    this.removeElement(tname, element.getType());
					continue;
				}
				if (!(element instanceof Cache.CacheRRset))
					continue;
				if (element.compareCredibility(minCred) < 0)
					continue;
				sr.addRRset((Cache.CacheRRset)element);
				added++;
			}
			/* There were positive entries */
			if (added > 0)
				return sr;
		}

		/*
		 * If this is the name, look for the actual type or a CNAME.
		 * Otherwise, look for a DNAME.
		 */
		if (isExact) {
			element = this.oneElement(tname, types, type, minCred);
			if (element != null &&
			    element instanceof Cache.CacheRRset)
			{
				sr = new SetResponse(SetResponse.SUCCESSFUL);
				sr.addRRset((Cache.CacheRRset) element);
				return sr;
			} else if (element != null) {
				sr = new SetResponse(SetResponse.NXRRSET);
				return sr;
			}

			element = this.oneElement(tname, types, Type.CNAME, minCred);
			if (element != null &&
			    element instanceof Cache.CacheRRset)
			{
				return new SetResponse(SetResponse.CNAME,
						       (Cache.CacheRRset) element);
			}
		} else {
			element = this.oneElement(tname, types, Type.DNAME, minCred);
			if (element != null &&
			    element instanceof Cache.CacheRRset)
			{
				return new SetResponse(SetResponse.DNAME,
						       (Cache.CacheRRset) element);
			}
		}

		/* Look for an NS */
		element = this.oneElement(tname, types, Type.NS, minCred);
		if (element != null && element instanceof Cache.CacheRRset)
			return new SetResponse(SetResponse.DELEGATION,
					       (Cache.CacheRRset) element);

		/* Check for the special NXDOMAIN element. */
		if (isExact) {
			element = this.oneElement(tname, types, 0, minCred);
			if (element != null)
				return SetResponse.ofType(SetResponse.NXDOMAIN);
		}

	}
	return SetResponse.ofType(SetResponse.UNKNOWN);
}

/**
 * Looks up Records in the Cache.  This follows CNAMEs and handles negatively
 * cached data.
 * @param name The name to look up
 * @param type The type to look up
 * @param minCred The minimum acceptable credibility
 * @return A SetResponse object
 * @see SetResponse
 * @see Credibility
 */
public SetResponse
lookupRecords(Name name, int type, int minCred) {
	return this.lookup(name, type, minCred);
}

private RRset []
findRecords(Name name, int type, int minCred) {
	SetResponse cr = this.lookupRecords(name, type, minCred);
	if (cr.isSuccessful())
		return cr.answers();
	else
		return null;
}

/**
 * Looks up credible Records in the Cache (a wrapper around lookupRecords).
 * Unlike lookupRecords, this given no indication of why failure occurred.
 * @param name The name to look up
 * @param type The type to look up
 * @return An array of RRsets, or null
 * @see Credibility
 */
public RRset []
findRecords(Name name, int type) {
	return this.findRecords(name, type, Credibility.NORMAL);
}

/**
 * Looks up Records in the Cache (a wrapper around lookupRecords).  Unlike
 * lookupRecords, this given no indication of why failure occurred.
 * @param name The name to look up
 * @param type The type to look up
 * @return An array of RRsets, or null
 * @see Credibility
 */
public RRset []
findAnyRecords(Name name, int type) {
	return this.findRecords(name, type, Credibility.GLUE);
}

private final int
getCred(int section, boolean isAuth) {
	if (section == Section.ANSWER) {
		if (isAuth)
			return Credibility.AUTH_ANSWER;
		else
			return Credibility.NONAUTH_ANSWER;
	} else if (section == Section.AUTHORITY) {
		if (isAuth)
			return Credibility.AUTH_AUTHORITY;
		else
			return Credibility.NONAUTH_AUTHORITY;
	} else if (section == Section.ADDITIONAL) {
		return Credibility.ADDITIONAL;
	} else
		throw new IllegalArgumentException("getCred: invalid section");
}

private static void
markAdditional(RRset rrset, Set names) {
	Record first = rrset.first();
	if (first.getAdditionalName() == null)
		return;

	Iterator it = rrset.rrs();
	while (it.hasNext()) {
		Record r = (Record) it.next();
		Name name = r.getAdditionalName();
		if (name != null)
			names.add(name);
	}
}

/**
 * Adds all data from a Message into the Cache.  Each record is added with
 * the appropriate credibility, and negative answers are cached as such.
 * @param in The Message to be added
 * @return A SetResponse that reflects what would be returned from a cache
 * lookup, or null if nothing useful could be cached from the message.
 * @see Message
 */
public SetResponse
addMessage(Message in) {
	boolean isAuth = in.getHeader().getFlag(Flags.AA);
	Record question = in.getQuestion();
	Name qname;
	Name curname;
	int qtype;
	int qclass;
	int cred;
	int rcode = in.getHeader().getRcode();
	boolean completed = false;
	RRset [] answers, auth, addl;
	SetResponse response = null;
	boolean verbose = Options.check("verbosecache");
	HashSet additionalNames;

	if (rcode != Rcode.NOERROR && rcode != Rcode.NXDOMAIN ||
	    question == null)
		return null;

	qname = question.getName();
	qtype = question.getType();
	qclass = question.getDClass();

	curname = qname;

	additionalNames = new HashSet();

	answers = in.getSectionRRsets(Section.ANSWER);
	for (int i = 0; i < answers.length; i++) {
		if (answers[i].getDClass() != qclass)
			continue;
		int type = answers[i].getType();
		Name name = answers[i].getName();
		cred = this.getCred(Section.ANSWER, isAuth);
		if ((type == qtype || qtype == Type.ANY) &&
		    name.equals(curname))
		{
            this.addRRset(answers[i], cred);
			completed = true;
			if (curname == qname) {
				if (response == null)
					response = new SetResponse(
							SetResponse.SUCCESSFUL);
				response.addRRset(answers[i]);
			}
            Cache.markAdditional(answers[i], additionalNames);
		} else if (type == Type.CNAME && name.equals(curname)) {
			CNAMERecord cname;
            this.addRRset(answers[i], cred);
			if (curname == qname)
				response = new SetResponse(SetResponse.CNAME,
							   answers[i]);
			cname = (CNAMERecord) answers[i].first();
			curname = cname.getTarget();
		} else if (type == Type.DNAME && curname.subdomain(name)) {
			DNAMERecord dname;
            this.addRRset(answers[i], cred);
			if (curname == qname)
				response = new SetResponse(SetResponse.DNAME,
							   answers[i]);
			dname = (DNAMERecord) answers[i].first();
			try {
				curname = curname.fromDNAME(dname);
			}
			catch (NameTooLongException e) {
				break;
			}
		}
	}

	auth = in.getSectionRRsets(Section.AUTHORITY);
	RRset soa = null, ns = null;
	for (int i = 0; i < auth.length; i++) {
		if (auth[i].getType() == Type.SOA &&
		    curname.subdomain(auth[i].getName()))
			soa = auth[i];
		else if (auth[i].getType() == Type.NS &&
			 curname.subdomain(auth[i].getName()))
			ns = auth[i];
	}
	if (!completed) {
		/* This is a negative response or a referral. */
		int cachetype = rcode == Rcode.NXDOMAIN ? 0 : qtype;
		if (rcode == Rcode.NXDOMAIN || soa != null || ns == null) {
			/* Negative response */
			cred = this.getCred(Section.AUTHORITY, isAuth);
			SOARecord soarec = null;
			if (soa != null)
				soarec = (SOARecord) soa.first();
            this.addNegative(curname, cachetype, soarec, cred);
			if (response == null) {
				int responseType;
				if (rcode == Rcode.NXDOMAIN)
					responseType = SetResponse.NXDOMAIN;
				else
					responseType = SetResponse.NXRRSET;
				response = SetResponse.ofType(responseType);
			}
			/* DNSSEC records are not cached. */
		} else {
			/* Referral response */
			cred = this.getCred(Section.AUTHORITY, isAuth);
            this.addRRset(ns, cred);
            Cache.markAdditional(ns, additionalNames);
			if (response == null)
				response = new SetResponse(
							SetResponse.DELEGATION,
							ns);
		}
	} else if (rcode == Rcode.NOERROR && ns != null) {
		/* Cache the NS set from a positive response. */
		cred = this.getCred(Section.AUTHORITY, isAuth);
        this.addRRset(ns, cred);
        Cache.markAdditional(ns, additionalNames);
	}

	addl = in.getSectionRRsets(Section.ADDITIONAL);
	for (int i = 0; i < addl.length; i++) {
		int type = addl[i].getType();
		if (type != Type.A && type != Type.AAAA && type != Type.A6)
			continue;
		Name name = addl[i].getName();
		if (!additionalNames.contains(name))
			continue;
		cred = this.getCred(Section.ADDITIONAL, isAuth);
        this.addRRset(addl[i], cred);
	}
	if (verbose)
		System.out.println("addMessage: " + response);
	return response;
}

/**
 * Flushes an RRset from the cache
 * @param name The name of the records to be flushed
 * @param type The type of the records to be flushed
 * @see RRset
 */
public void
flushSet(Name name, int type) {
    this.removeElement(name, type);
}

/**
 * Flushes all RRsets with a given name from the cache
 * @param name The name of the records to be flushed
 * @see RRset
 */
public void
flushName(Name name) {
    this.removeName(name);
}

/**
 * Sets the maximum length of time that a negative response will be stored
 * in this Cache.  A negative value disables this feature (that is, sets
 * no limit).
 */
public void
setMaxNCache(int seconds) {
    this.maxncache = seconds;
}

/**
 * Gets the maximum length of time that a negative response will be stored
 * in this Cache.  A negative value indicates no limit.
 */
public int
getMaxNCache() {
	return this.maxncache;
}

/**
 * Sets the maximum length of time that records will be stored in this
 * Cache.  A negative value disables this feature (that is, sets no limit).
 */
public void
setMaxCache(int seconds) {
    this.maxcache = seconds;
}

/**
 * Gets the maximum length of time that records will be stored
 * in this Cache.  A negative value indicates no limit.
 */
public int
getMaxCache() {
	return this.maxcache;
}

/**
 * Gets the current number of entries in the Cache, where an entry consists
 * of all records with a specific Name.
 */
public int
getSize() {
	return this.data.size();
}

/**
 * Gets the maximum number of entries in the Cache, where an entry consists
 * of all records with a specific Name.  A negative value is treated as an
 * infinite limit.
 */
public int
getMaxEntries() {
	return this.data.getMaxSize();
}

/**
 * Sets the maximum number of entries in the Cache, where an entry consists
 * of all records with a specific Name.  A negative value is treated as an
 * infinite limit.
 *
 * Note that setting this to a value lower than the current number
 * of entries will not cause the Cache to shrink immediately.
 *
 * The default maximum number of entries is 50000.
 *
 * @param entries The maximum number of entries in the Cache.
 */
public void
setMaxEntries(int entries) {
    this.data.setMaxSize(entries);
}

/**
 * Returns the DNS class of this cache.
 */
public int
getDClass() {
	return this.dclass;
}

/**
 * Returns the contents of the Cache as a string.
 */
@Override
public String
toString() {
	StringBuffer sb = new StringBuffer();
	synchronized (this) {
		Iterator it = this.data.values().iterator();
		while (it.hasNext()) {
			Cache.Element [] elements = this.allElements(it.next());
			for (int i = 0; i < elements.length; i++) {
				sb.append(elements[i]);
				sb.append("\n");
			}
		}
	}
	return sb.toString();
}

}
