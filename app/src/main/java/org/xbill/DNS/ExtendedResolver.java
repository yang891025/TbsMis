// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.util.*;
import java.io.*;
import java.net.*;

/**
 * An implementation of Resolver that can send queries to multiple servers,
 * sending the queries multiple times if necessary.
 * @see Resolver
 *
 * @author Brian Wellington
 */

public class ExtendedResolver implements Resolver {

private static class Resolution implements ResolverListener {
	Resolver [] resolvers;
	int [] sent;
	Object [] inprogress;
	int retries;
	int outstanding;
	boolean done;
	Message query;
	Message response;
	Throwable thrown;
	ResolverListener listener;

	public
	Resolution(ExtendedResolver eres, Message query) {
		List l = eres.resolvers;
        this.resolvers = (Resolver []) l.toArray (new Resolver[l.size()]);
		if (eres.loadBalance) {
			int nresolvers = this.resolvers.length;
			/*
			 * Note: this is not synchronized, since the
			 * worst thing that can happen is a random
			 * ordering, which is ok.
			 */
			int start = eres.lbStart++ % nresolvers;
			if (eres.lbStart > nresolvers)
				eres.lbStart %= nresolvers;
			if (start > 0) {
				Resolver [] shuffle = new Resolver[nresolvers];
				for (int i = 0; i < nresolvers; i++) {
					int pos = (i + start) % nresolvers;
					shuffle[i] = this.resolvers[pos];
				}
                this.resolvers = shuffle;
			}
		}
        this.sent = new int[this.resolvers.length];
        this.inprogress = new Object[this.resolvers.length];
        this.retries = eres.retries;
		this.query = query;
	}

	/* Asynchronously sends a message. */
	public void
	send(int n) {
        this.sent[n]++;
        this.outstanding++;
		try {
            this.inprogress[n] = this.resolvers[n].sendAsync(this.query, this);
		}
		catch (Throwable t) {
			synchronized (this) {
                this.thrown = t;
                this.done = true;
				if (this.listener == null) {
                    this.notifyAll();
					return;
				}
			}
		}
	}

	/* Start a synchronous resolution */
	public Message
	start() throws IOException {
		try {
			/*
			 * First, try sending synchronously.  If this works,
			 * we're done.  Otherwise, we'll get an exception
			 * and continue.  It would be easier to call send(0),
			 * but this avoids a thread creation.  If and when
			 * SimpleResolver.sendAsync() can be made to not
			 * create a thread, this could be changed.
			 */
            this.sent[0]++;
            this.outstanding++;
            this.inprogress[0] = new Object();
			return this.resolvers[0].send(this.query);
		}
		catch (Exception e) {
			/*
			 * This will either cause more queries to be sent
			 * asynchronously or will set the 'done' flag.
			 */
            this.handleException(this.inprogress[0], e);
		}
		/*
		 * Wait for a successful response or for each
		 * subresolver to fail.
		 */
		synchronized (this) {
			while (!this.done) {
				try {
                    this.wait();
				}
				catch (InterruptedException e) {
				}
			}
		}
		/* Return the response or throw an exception */
		if (this.response != null)
			return this.response;
		else if (this.thrown instanceof IOException)
			throw (IOException) this.thrown;
		else if (this.thrown instanceof RuntimeException)
			throw (RuntimeException) this.thrown;
		else if (this.thrown instanceof Error)
			throw (Error) this.thrown;
		else
			throw new IllegalStateException
				("ExtendedResolver failure");
	}

	/* Start an asynchronous resolution */
	public void
	startAsync(ResolverListener listener) {
		this.listener = listener;
        this.send(0);
	}

	/*
	 * Receive a response.  If the resolution hasn't been completed,
	 * either wake up the blocking thread or call the callback.
	 */
	@Override
	public void
	receiveMessage(Object id, Message m) {
		if (Options.check("verbose"))
			System.err.println("ExtendedResolver: " +
					   "received message");
		synchronized (this) {
			if (this.done)
				return;
            this.response = m;
            this.done = true;
			if (this.listener == null) {
                this.notifyAll();
				return;
			}
		}
        this.listener.receiveMessage(this, this.response);
	}

	/*
	 * Receive an exception.  If the resolution has been completed,
	 * do nothing.  Otherwise make progress.
	 */
	@Override
	public void
	handleException(Object id, Exception e) {
		if (Options.check("verbose"))
			System.err.println("ExtendedResolver: got " + e);
		synchronized (this) {
            this.outstanding--;
			if (this.done)
				return;
			int n;
			for (n = 0; n < this.inprogress.length; n++)
				if (this.inprogress[n] == id)
					break;
			/* If we don't know what this is, do nothing. */
			if (n == this.inprogress.length)
				return;
			boolean startnext = false;
			/*
			 * If this is the first response from server n, 
			 * we should start sending queries to server n + 1.
			 */
			if (this.sent[n] == 1 && n < this.resolvers.length - 1)
				startnext = true;
			if (e instanceof InterruptedIOException) {
				/* Got a timeout; resend */
				if (this.sent[n] < this.retries)
                    this.send(n);
				if (this.thrown == null)
                    this.thrown = e;
			} else if (e instanceof SocketException) {
				/*
				 * Problem with the socket; don't resend
				 * on it
				 */
				if (this.thrown == null ||
                        this.thrown instanceof InterruptedIOException)
                    this.thrown = e;
			} else {
				/*
				 * Problem with the response; don't resend
				 * on the same socket.
				 */
                this.thrown = e;
			}
			if (this.done)
				return;
			if (startnext)
                this.send(n + 1);
			if (this.done)
				return;
			if (this.outstanding == 0) {
				/*
				 * If we're done and this is synchronous,
				 * wake up the blocking thread.
				 */
                this.done = true;
				if (this.listener == null) {
                    this.notifyAll();
					return;
				}
			}
			if (!this.done)
				return;
		}
		/* If we're done and this is asynchronous, call the callback. */
		if (!(this.thrown instanceof Exception))
            this.thrown = new RuntimeException(this.thrown.getMessage());
        this.listener.handleException(this, (Exception) this.thrown);
	}
}

private static final int quantum = 5;

private List resolvers;
private boolean loadBalance;
private int lbStart;
private int retries = 3;

private void
init() {
    this.resolvers = new ArrayList();
}

/**
 * Creates a new Extended Resolver.  The default ResolverConfig is used to
 * determine the servers for which SimpleResolver contexts should be
 * initialized.
 * @see SimpleResolver
 * @see ResolverConfig
 * @exception UnknownHostException Failure occured initializing SimpleResolvers
 */
public
ExtendedResolver() throws UnknownHostException {
    this.init();
	String [] servers = ResolverConfig.getCurrentConfig().servers();
	if (servers != null) {
		for (int i = 0; i < servers.length; i++) {
			Resolver r = new SimpleResolver(servers[i]);
			r.setTimeout(ExtendedResolver.quantum);
            this.resolvers.add(r);
		}
	}
	else
        this.resolvers.add(new SimpleResolver());
}

/**
 * Creates a new Extended Resolver
 * @param servers An array of server names for which SimpleResolver
 * contexts should be initialized.
 * @see SimpleResolver
 * @exception UnknownHostException Failure occured initializing SimpleResolvers
 */
public
ExtendedResolver(String [] servers) throws UnknownHostException {
    this.init();
	for (int i = 0; i < servers.length; i++) {
		Resolver r = new SimpleResolver(servers[i]);
		r.setTimeout(ExtendedResolver.quantum);
        this.resolvers.add(r);
	}
}

/**
 * Creates a new Extended Resolver
 * @param res An array of pre-initialized Resolvers is provided.
 * @see SimpleResolver
 * @exception UnknownHostException Failure occured initializing SimpleResolvers
 */
public
ExtendedResolver(Resolver [] res) throws UnknownHostException {
    this.init();
	for (int i = 0; i < res.length; i++)
        this.resolvers.add(res[i]);
}

@Override
public void
setPort(int port) {
	for (int i = 0; i < this.resolvers.size(); i++)
		((Resolver) this.resolvers.get(i)).setPort(port);
}

@Override
public void
setTCP(boolean flag) {
	for (int i = 0; i < this.resolvers.size(); i++)
		((Resolver) this.resolvers.get(i)).setTCP(flag);
}

@Override
public void
setIgnoreTruncation(boolean flag) {
	for (int i = 0; i < this.resolvers.size(); i++)
		((Resolver) this.resolvers.get(i)).setIgnoreTruncation(flag);
}

@Override
public void
setEDNS(int level) {
	for (int i = 0; i < this.resolvers.size(); i++)
		((Resolver) this.resolvers.get(i)).setEDNS(level);
}

@Override
public void
setEDNS(int level, int payloadSize, int flags, List options) {
	for (int i = 0; i < this.resolvers.size(); i++)
		((Resolver) this.resolvers.get(i)).setEDNS(level, payloadSize,
						     flags, options);
}

@Override
public void
setTSIGKey(TSIG key) {
	for (int i = 0; i < this.resolvers.size(); i++)
		((Resolver) this.resolvers.get(i)).setTSIGKey(key);
}

@Override
public void
setTimeout(int secs, int msecs) {
	for (int i = 0; i < this.resolvers.size(); i++)
		((Resolver) this.resolvers.get(i)).setTimeout(secs, msecs);
}

@Override
public void
setTimeout(int secs) {
    this.setTimeout(secs, 0);
}

/**
 * Sends a message and waits for a response.  Multiple servers are queried,
 * and queries are sent multiple times until either a successful response
 * is received, or it is clear that there is no successful response.
 * @param query The query to send.
 * @return The response.
 * @throws IOException An error occurred while sending or receiving.
 */
@Override
public Message
send(Message query) throws IOException {
	ExtendedResolver.Resolution res = new ExtendedResolver.Resolution(this, query);
	return res.start();
}

/**
 * Asynchronously sends a message to multiple servers, potentially multiple
 * times, registering a listener to receive a callback on success or exception.
 * Multiple asynchronous lookups can be performed in parallel.  Since the
 * callback may be invoked before the function returns, external
 * synchronization is necessary.
 * @param query The query to send
 * @param listener The object containing the callbacks.
 * @return An identifier, which is also a parameter in the callback
 */
@Override
public Object
sendAsync(Message query, ResolverListener listener) {
	ExtendedResolver.Resolution res = new ExtendedResolver.Resolution(this, query);
	res.startAsync(listener);
	return res;
}

/** Returns the nth resolver used by this ExtendedResolver */
public Resolver
getResolver(int n) {
	if (n < this.resolvers.size())
		return (Resolver) this.resolvers.get(n);
	return null;
}

/** Returns all resolvers used by this ExtendedResolver */
public Resolver []
getResolvers() {
	return (Resolver []) this.resolvers.toArray(new Resolver[this.resolvers.size()]);
}

/** Adds a new resolver to be used by this ExtendedResolver */
public void
addResolver(Resolver r) {
    this.resolvers.add(r);
}

/** Deletes a resolver used by this ExtendedResolver */
public void
deleteResolver(Resolver r) {
    this.resolvers.remove(r);
}

/** Sets whether the servers should be load balanced.
 * @param flag If true, servers will be tried in round-robin order.  If false,
 * servers will always be queried in the same order.
 */
public void
setLoadBalance(boolean flag) {
    this.loadBalance = flag;
}

/** Sets the number of retries sent to each server per query */
public void
setRetries(int retries) {
	this.retries = retries;
}

}
