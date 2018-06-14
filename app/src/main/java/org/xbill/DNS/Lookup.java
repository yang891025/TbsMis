// Copyright (c) 2002-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.util.*;
import java.io.*;
import java.net.*;

/**
 * The Lookup object issues queries to caching DNS servers.  The input consists
 * of a name, an optional type, and an optional class.  Caching is enabled
 * by default and used when possible to reduce the number of DNS requests.
 * A Resolver, which defaults to an ExtendedResolver initialized with the
 * resolvers located by the ResolverConfig class, performs the queries.  A
 * search path of domain suffixes is used to resolve relative names, and is
 * also determined by the ResolverConfig class.
 *
 * A Lookup object may be reused, but should not be used by multiple threads.
 *
 * @see Cache
 * @see Resolver
 * @see ResolverConfig
 *
 * @author Brian Wellington
 */

public final class Lookup {

private static Resolver defaultResolver;
private static Name [] defaultSearchPath;
private static Map defaultCaches;

private Resolver resolver;
private Name [] searchPath;
private Cache cache;
private boolean temporary_cache;
private int credibility;
private final Name name;
private final int type;
private final int dclass;
private final boolean verbose;
private int iterations;
private boolean foundAlias;
private boolean done;
private boolean doneCurrent;
private List aliases;
private Record [] answers;
private int result;
private String error;
private boolean nxdomain;
private boolean badresponse;
private String badresponse_error;
private boolean networkerror;
private boolean timedout;
private boolean nametoolong;
private boolean referral;

private static final Name [] noAliases = new Name[0];

/** The lookup was successful. */
public static final int SUCCESSFUL = 0;

/**
 * The lookup failed due to a data or server error. Repeating the lookup
 * would not be helpful.
 */
public static final int UNRECOVERABLE = 1;

/**
 * The lookup failed due to a network error. Repeating the lookup may be
 * helpful.
 */
public static final int TRY_AGAIN = 2;

/** The host does not exist. */
public static final int HOST_NOT_FOUND = 3;

/** The host exists, but has no records associated with the queried type. */
public static final int TYPE_NOT_FOUND = 4;

public static synchronized void
refreshDefault() {

	try {
        Lookup.defaultResolver = new ExtendedResolver();
	}
	catch (UnknownHostException e) {
		throw new RuntimeException("Failed to initialize resolver");
	}
    Lookup.defaultSearchPath = ResolverConfig.getCurrentConfig().searchPath();
    Lookup.defaultCaches = new HashMap();
}

static {
    Lookup.refreshDefault();
}

/**
 * Gets the Resolver that will be used as the default by future Lookups.
 * @return The default resolver.
 */
public static synchronized Resolver
getDefaultResolver() {
	return Lookup.defaultResolver;
}

/**
 * Sets the default Resolver to be used as the default by future Lookups.
 * @param resolver The default resolver.
 */
public static synchronized void
setDefaultResolver(Resolver resolver) {
    Lookup.defaultResolver = resolver;
}

/**
 * Gets the Cache that will be used as the default for the specified
 * class by future Lookups.
 * @param dclass The class whose cache is being retrieved.
 * @return The default cache for the specified class.
 */
public static synchronized Cache
getDefaultCache(int dclass) {
	DClass.check(dclass);
	Cache c = (Cache) Lookup.defaultCaches.get(Mnemonic.toInteger(dclass));
	if (c == null) {
		c = new Cache(dclass);
        Lookup.defaultCaches.put(Mnemonic.toInteger(dclass), c);
	}
	return c;
}

/**
 * Sets the Cache to be used as the default for the specified class by future
 * Lookups.
 * @param cache The default cache for the specified class.
 * @param dclass The class whose cache is being set.
 */
public static synchronized void
setDefaultCache(Cache cache, int dclass) {
	DClass.check(dclass);
    Lookup.defaultCaches.put(Mnemonic.toInteger(dclass), cache);
}

/**
 * Gets the search path that will be used as the default by future Lookups.
 * @return The default search path.
 */
public static synchronized Name []
getDefaultSearchPath() {
	return Lookup.defaultSearchPath;
}

/**
 * Sets the search path to be used as the default by future Lookups.
 * @param domains The default search path.
 */
public static synchronized void
setDefaultSearchPath(Name [] domains) {
    Lookup.defaultSearchPath = domains;
}

/**
 * Sets the search path that will be used as the default by future Lookups.
 * @param domains The default search path.
 * @throws TextParseException A name in the array is not a valid DNS name.
 */
public static synchronized void
setDefaultSearchPath(String [] domains) throws TextParseException {
	if (domains == null) {
        Lookup.defaultSearchPath = null;
		return;
	}
	Name [] newdomains = new Name[domains.length];
	for (int i = 0; i < domains.length; i++)
		newdomains[i] = Name.fromString(domains[i], Name.root);
    Lookup.defaultSearchPath = newdomains;
}

private final void
reset() {
    this.iterations = 0;
    this.foundAlias = false;
    this.done = false;
    this.doneCurrent = false;
    this.aliases = null;
    this.answers = null;
    this.result = -1;
    this.error = null;
    this.nxdomain = false;
    this.badresponse = false;
    this.badresponse_error = null;
    this.networkerror = false;
    this.timedout = false;
    this.nametoolong = false;
    this.referral = false;
	if (this.temporary_cache)
        this.cache.clearCache();
}

/**
 * Create a Lookup object that will find records of the given name, type,
 * and class.  The lookup will use the default cache, resolver, and search
 * path, and look for records that are reasonably credible.
 * @param name The name of the desired records
 * @param type The type of the desired records
 * @param dclass The class of the desired records
 * @throws IllegalArgumentException The type is a meta type other than ANY.
 * @see Cache
 * @see Resolver
 * @see Credibility
 * @see Name
 * @see Type
 * @see DClass
 */
public
Lookup(Name name, int type, int dclass) {
	Type.check(type);
	DClass.check(dclass);
	if (!Type.isRR(type) && type != Type.ANY)
		throw new IllegalArgumentException("Cannot query for " +
						   "meta-types other than ANY");
	this.name = name;
	this.type = type;
	this.dclass = dclass;
	synchronized (Lookup.class) {
        resolver = Lookup.getDefaultResolver();
        searchPath = Lookup.getDefaultSearchPath();
        cache = Lookup.getDefaultCache(dclass);
	}
    credibility = Credibility.NORMAL;
    verbose = Options.check("verbose");
    result = -1;
}

/**
 * Create a Lookup object that will find records of the given name and type
 * in the IN class.
 * @param name The name of the desired records
 * @param type The type of the desired records
 * @throws IllegalArgumentException The type is a meta type other than ANY.
 * @see #Lookup(Name,int,int)
 */
public
Lookup(Name name, int type) {
	this(name, type, DClass.IN);
}

/**
 * Create a Lookup object that will find records of type A at the given name
 * in the IN class.
 * @param name The name of the desired records
 * @see #Lookup(Name,int,int)
 */
public
Lookup(Name name) {
	this(name, Type.A, DClass.IN);
}

/**
 * Create a Lookup object that will find records of the given name, type,
 * and class.
 * @param name The name of the desired records
 * @param type The type of the desired records
 * @param dclass The class of the desired records
 * @throws TextParseException The name is not a valid DNS name
 * @throws IllegalArgumentException The type is a meta type other than ANY.
 * @see #Lookup(Name,int,int)
 */
public
Lookup(String name, int type, int dclass) throws TextParseException {
	this(Name.fromString(name), type, dclass);
}

/**
 * Create a Lookup object that will find records of the given name and type
 * in the IN class.
 * @param name The name of the desired records
 * @param type The type of the desired records
 * @throws TextParseException The name is not a valid DNS name
 * @throws IllegalArgumentException The type is a meta type other than ANY.
 * @see #Lookup(Name,int,int)
 */
public
Lookup(String name, int type) throws TextParseException {
	this(Name.fromString(name), type, DClass.IN);
}

/**
 * Create a Lookup object that will find records of type A at the given name
 * in the IN class.
 * @param name The name of the desired records
 * @throws TextParseException The name is not a valid DNS name
 * @see #Lookup(Name,int,int)
 */
public
Lookup(String name) throws TextParseException {
	this(Name.fromString(name), Type.A, DClass.IN);
}

/**
 * Sets the resolver to use when performing this lookup.  This overrides the
 * default value.
 * @param resolver The resolver to use.
 */
public void
setResolver(Resolver resolver) {
	this.resolver = resolver;
}

/**
 * Sets the search path to use when performing this lookup.  This overrides the
 * default value.
 * @param domains An array of names containing the search path.
 */
public void
setSearchPath(Name [] domains) {
    searchPath = domains;
}

/**
 * Sets the search path to use when performing this lookup. This overrides the
 * default value.
 * @param domains An array of names containing the search path.
 * @throws TextParseException A name in the array is not a valid DNS name.
 */
public void
setSearchPath(String [] domains) throws TextParseException {
	if (domains == null) {
        searchPath = null;
		return;
	}
	Name [] newdomains = new Name[domains.length];
	for (int i = 0; i < domains.length; i++)
		newdomains[i] = Name.fromString(domains[i], Name.root);
    searchPath = newdomains;
}

/**
 * Sets the cache to use when performing this lookup.  This overrides the
 * default value.  If the results of this lookup should not be permanently
 * cached, null can be provided here.
 * @param cache The cache to use.
 */
public void
setCache(Cache cache) {
	if (cache == null) {
		this.cache = new Cache(this.dclass);
        temporary_cache = true;
	} else {
		this.cache = cache;
        temporary_cache = false;
	}
}

/**
 * Sets the minimum credibility level that will be accepted when performing
 * the lookup.  This defaults to Credibility.NORMAL.
 * @param credibility The minimum credibility level.
 */
public void
setCredibility(int credibility) {
	this.credibility = credibility;
}

private void
follow(Name name, Name oldname) {
    this.foundAlias = true;
    this.badresponse = false;
    this.networkerror = false;
    this.timedout = false;
    this.nxdomain = false;
    this.referral = false;
    this.iterations++;
	if (this.iterations >= 6 || name.equals(oldname)) {
        this.result = Lookup.UNRECOVERABLE;
        this.error = "CNAME loop";
        this.done = true;
		return;
	}
	if (this.aliases == null)
        this.aliases = new ArrayList();
    this.aliases.add(oldname);
    this.lookup(name);
}

private void
processResponse(Name name, SetResponse response) {
	if (response.isSuccessful()) {
		RRset [] rrsets = response.answers();
		List l = new ArrayList();
		Iterator it;
		int i;

		for (i = 0; i < rrsets.length; i++) {
			it = rrsets[i].rrs();
			while (it.hasNext())
				l.add(it.next());
		}

        this.result = Lookup.SUCCESSFUL;
        this.answers = (Record []) l.toArray(new Record[l.size()]);
        this.done = true;
	} else if (response.isNXDOMAIN()) {
        this.nxdomain = true;
        this.doneCurrent = true;
		if (this.iterations > 0) {
            this.result = Lookup.HOST_NOT_FOUND;
            this.done = true;
		}
	} else if (response.isNXRRSET()) {
        this.result = Lookup.TYPE_NOT_FOUND;
        this.answers = null;
        this.done = true;
	} else if (response.isCNAME()) {
		CNAMERecord cname = response.getCNAME();
        this.follow(cname.getTarget(), name);
	} else if (response.isDNAME()) {
		DNAMERecord dname = response.getDNAME();
		try {
            this.follow(name.fromDNAME(dname), name);
		} catch (NameTooLongException e) {
            this.result = Lookup.UNRECOVERABLE;
            this.error = "Invalid DNAME target";
            this.done = true;
		}
	} else if (response.isDelegation()) {
		// We shouldn't get a referral.  Ignore it.
        this.referral = true;
	}
}

private void
lookup(Name current) {
	SetResponse sr = this.cache.lookupRecords(current, this.type, this.credibility);
	if (this.verbose) {
		System.err.println("lookup " + current + " " +
				   Type.string(this.type));
		System.err.println(sr);
	}
    this.processResponse(current, sr);
	if (this.done || this.doneCurrent)
		return;

	Record question = Record.newRecord(current, this.type, this.dclass);
	Message query = Message.newQuery(question);
	Message response = null;
	try {
		response = this.resolver.send(query);
	}
	catch (IOException e) {
		// A network error occurred.  Press on.
		if (e instanceof InterruptedIOException)
            this.timedout = true;
		else
            this.networkerror = true;
		return;
	}
	int rcode = response.getHeader().getRcode();
	if (rcode != Rcode.NOERROR && rcode != Rcode.NXDOMAIN) {
		// The server we contacted is broken or otherwise unhelpful.
		// Press on.
        this.badresponse = true;
        this.badresponse_error = Rcode.string(rcode);
		return;
	}

	if (!query.getQuestion().equals(response.getQuestion())) {
		// The answer doesn't match the question.  That's not good.
        this.badresponse = true;
        this.badresponse_error = "response does not match query";
		return;
	}

	sr = this.cache.addMessage(response);
	if (sr == null)
		sr = this.cache.lookupRecords(current, this.type, this.credibility);
	if (this.verbose) {
		System.err.println("queried " + current + " " +
				   Type.string(this.type));
		System.err.println(sr);
	}
    this.processResponse(current, sr);
}

private void
resolve(Name current, Name suffix) {
    this.doneCurrent = false;
	Name tname = null;
	if (suffix == null)
		tname = current;
	else {
		try {
			tname = Name.concatenate(current, suffix);
		}
		catch (NameTooLongException e) {
            this.nametoolong = true;
			return;
		}
	}
    this.lookup(tname);
}

/**
 * Performs the lookup, using the specified Cache, Resolver, and search path.
 * @return The answers, or null if none are found.
 */
public Record []
run() {
	if (this.done)
        this.reset();
	if (this.name.isAbsolute())
        this.resolve(this.name, null);
	else if (this.searchPath == null)
        this.resolve(this.name, Name.root);
	else {
		if (this.name.labels() > 1)
            this.resolve(this.name, Name.root);
		if (this.done)
			return this.answers;

		for (int i = 0; i < this.searchPath.length; i++) {
            this.resolve(this.name, this.searchPath[i]);
			if (this.done)
				return this.answers;
			else if (this.foundAlias)
				break;
		}
	}
	if (!this.done) {
		if (this.badresponse) {
            this.result = Lookup.TRY_AGAIN;
            this.error = this.badresponse_error;
            this.done = true;
		} else if (this.timedout) {
            this.result = Lookup.TRY_AGAIN;
            this.error = "timed out";
            this.done = true;
		} else if (this.networkerror) {
            this.result = Lookup.TRY_AGAIN;
            this.error = "network error";
            this.done = true;
		} else if (this.nxdomain) {
            this.result = Lookup.HOST_NOT_FOUND;
            this.done = true;
		} else if (this.referral) {
            this.result = Lookup.UNRECOVERABLE;
            this.error = "referral";
            this.done = true;
		} else if (this.nametoolong) {
            this.result = Lookup.UNRECOVERABLE;
            this.error = "name too long";
            this.done = true;
		}
	}
	return this.answers;
}

private void
checkDone() {
	if (this.done && this.result != -1)
		return;
	StringBuffer sb = new StringBuffer("Lookup of " + this.name + " ");
	if (this.dclass != DClass.IN)
		sb.append(DClass.string(this.dclass) + " ");
	sb.append(Type.string(this.type) + " isn't done");
	throw new IllegalStateException(sb.toString());
}

/**
 * Returns the answers from the lookup.
 * @return The answers, or null if none are found.
 * @throws IllegalStateException The lookup has not completed.
 */
public Record []
getAnswers() {
    this.checkDone();
	return this.answers;
}

/**
 * Returns all known aliases for this name.  Whenever a CNAME/DNAME is
 * followed, an alias is added to this array.  The last element in this
 * array will be the owner name for records in the answer, if there are any.
 * @return The aliases.
 * @throws IllegalStateException The lookup has not completed.
 */
public Name []
getAliases() {
    this.checkDone();
	if (this.aliases == null)
		return Lookup.noAliases;
	return (Name []) this.aliases.toArray(new Name[this.aliases.size()]);
}

/**
 * Returns the result code of the lookup.
 * @return The result code, which can be SUCCESSFUL, UNRECOVERABLE, TRY_AGAIN,
 * HOST_NOT_FOUND, or TYPE_NOT_FOUND.
 * @throws IllegalStateException The lookup has not completed.
 */
public int
getResult() {
    this.checkDone();
	return this.result;
}

/**
 * Returns an error string describing the result code of this lookup.
 * @return A string, which may either directly correspond the result code
 * or be more specific.
 * @throws IllegalStateException The lookup has not completed.
 */
public String
getErrorString() {
    this.checkDone();
	if (this.error != null)
		return this.error;
	switch (this.result) {
		case Lookup.SUCCESSFUL:	return "successful";
		case Lookup.UNRECOVERABLE:	return "unrecoverable error";
		case Lookup.TRY_AGAIN:		return "try again";
		case Lookup.HOST_NOT_FOUND:	return "host not found";
		case Lookup.TYPE_NOT_FOUND:	return "type not found";
	}
	throw new IllegalStateException("unknown result");
}

}
