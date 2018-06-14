// Copyright (c) 2005 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.*;
import java.net.*;
import java.security.SecureRandom;
import java.nio.*;
import java.nio.channels.*;

final class UDPClient extends Client {

private static final int EPHEMERAL_START = 1024;
private static final int EPHEMERAL_STOP  = 65535;
private static final int EPHEMERAL_RANGE  = UDPClient.EPHEMERAL_STOP - UDPClient.EPHEMERAL_START;

private static final SecureRandom prng = new SecureRandom();
private static volatile boolean prng_initializing = true;

/*
 * On some platforms (Windows), the SecureRandom module initialization involves
 * a call to InetAddress.getLocalHost(), which can end up here if using a
 * dnsjava name service provider.
 *
 * This can cause problems in multiple ways.
 *   - If the SecureRandom seed generation process calls into here, and this
 *     module attempts to seed the local SecureRandom object, the thread hangs.
 *   - If something else calls InetAddress.getLocalHost(), and that causes this
 *     module to seed the local SecureRandom object, the thread hangs.
 *
 * To avoid both of these, check at initialization time to see if InetAddress
 * is in the call chain.  If so, initialize the SecureRandom object in a new
 * thread, and disable port randomization until it completes.
 */
static {
	new Thread(new Runnable() {
			   @Override
			public void run() {
			   int n = UDPClient.prng.nextInt();
                   UDPClient.prng_initializing = false;
		   }}).start();
}

private boolean bound;

public
UDPClient(long endTime) throws IOException {
	super(DatagramChannel.open(), endTime);
}

private void
bind_random(InetSocketAddress addr) throws IOException
{
	if (UDPClient.prng_initializing) {
		try {
			Thread.sleep(2);
		}
		catch (InterruptedException e) {
		}
		if (UDPClient.prng_initializing)
			return;
	}

	DatagramChannel channel = (DatagramChannel) this.key.channel();
	InetSocketAddress temp;

	for (int i = 0; i < 1024; i++) {
		try {
			int port = UDPClient.prng.nextInt(UDPClient.EPHEMERAL_RANGE) +
                    UDPClient.EPHEMERAL_START;
			if (addr != null)
				temp = new InetSocketAddress(addr.getAddress(),
							     port);
			else
				temp = new InetSocketAddress(port);
			channel.socket().bind(temp);
            this.bound = true;
			return;
		}
		catch (SocketException e) {
		}
	}
}

void
bind(SocketAddress addr) throws IOException {
	if (addr == null ||
            addr instanceof InetSocketAddress &&
             ((InetSocketAddress)addr).getPort() == 0)
	{
        this.bind_random((InetSocketAddress) addr);
		if (this.bound)
			return;
	}

	if (addr != null) {
		DatagramChannel channel = (DatagramChannel) this.key.channel();
		channel.socket().bind(addr);
        this.bound = true;
	}
}

void
connect(SocketAddress addr) throws IOException {
	if (!this.bound)
        this.bind(null);
	DatagramChannel channel = (DatagramChannel) this.key.channel();
	channel.connect(addr);
}

void
send(byte [] data) throws IOException {
	DatagramChannel channel = (DatagramChannel) this.key.channel();
    Client.verboseLog("UDP write", data);
	channel.write(ByteBuffer.wrap(data));
}

byte []
recv(int max) throws IOException {
	DatagramChannel channel = (DatagramChannel) this.key.channel();
	byte [] temp = new byte[max];
    this.key.interestOps(SelectionKey.OP_READ);
	try {
		while (!this.key.isReadable())
            Client.blockUntil(this.key, this.endTime);
	}
	finally {
		if (this.key.isValid())
            this.key.interestOps(0);
	}
	long ret = channel.read(ByteBuffer.wrap(temp));
	if (ret <= 0)
		throw new EOFException();
	int len = (int) ret;
	byte [] data = new byte[len];
	System.arraycopy(temp, 0, data, 0, len);
    Client.verboseLog("UDP read", data);
	return data;
}

static byte []
sendrecv(SocketAddress local, SocketAddress remote, byte [] data, int max,
	 long endTime)
throws IOException
{
	UDPClient client = new UDPClient(endTime);
	try {
		client.bind(local);
		client.connect(remote);
		client.send(data);
		return client.recv(max);
	}
	finally {
		client.cleanup();
	}
}

static byte []
sendrecv(SocketAddress addr, byte [] data, int max, long endTime)
throws IOException
{
	return UDPClient.sendrecv(null, addr, data, max, endTime);
}

}
