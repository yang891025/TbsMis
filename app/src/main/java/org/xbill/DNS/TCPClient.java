// Copyright (c) 2005 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;

final class TCPClient extends Client {

public
TCPClient(long endTime) throws IOException {
	super(SocketChannel.open(), endTime);
}

void
bind(SocketAddress addr) throws IOException {
	SocketChannel channel = (SocketChannel) this.key.channel();
	channel.socket().bind(addr);
}

void
connect(SocketAddress addr) throws IOException {
	SocketChannel channel = (SocketChannel) this.key.channel();
	if (channel.connect(addr))
		return;
    this.key.interestOps(SelectionKey.OP_CONNECT);
	try {
		while (!channel.finishConnect()) {
			if (!this.key.isConnectable())
                Client.blockUntil(this.key, this.endTime);
		}
	}
	finally {
		if (this.key.isValid())
            this.key.interestOps(0);
	}
}

void
send(byte [] data) throws IOException {
	SocketChannel channel = (SocketChannel) this.key.channel();
    Client.verboseLog("TCP write", data);
	byte [] lengthArray = new byte[2];
	lengthArray[0] = (byte)(data.length >>> 8);
	lengthArray[1] = (byte)(data.length & 0xFF);
	ByteBuffer [] buffers = new ByteBuffer[2];
	buffers[0] = ByteBuffer.wrap(lengthArray);
	buffers[1] = ByteBuffer.wrap(data);
	int nsent = 0;
    this.key.interestOps(SelectionKey.OP_WRITE);
	try {
		while (nsent < data.length + 2) {
			if (this.key.isWritable()) {
				long n = channel.write(buffers);
				if (n < 0)
					throw new EOFException();
				nsent += (int) n;
				if (nsent < data.length + 2 &&
				    System.currentTimeMillis() > this.endTime)
					throw new SocketTimeoutException();
			} else
                Client.blockUntil(this.key, this.endTime);
		}
	}
	finally {
		if (this.key.isValid())
            this.key.interestOps(0);
	}
}

private byte []
_recv(int length) throws IOException {
	SocketChannel channel = (SocketChannel) this.key.channel();
	int nrecvd = 0;
	byte [] data = new byte[length];
	ByteBuffer buffer = ByteBuffer.wrap(data);
    this.key.interestOps(SelectionKey.OP_READ);
	try {
		while (nrecvd < length) {
			if (this.key.isReadable()) {
				long n = channel.read(buffer);
				if (n < 0)
					throw new EOFException();
				nrecvd += (int) n;
				if (nrecvd < length &&
				    System.currentTimeMillis() > this.endTime)
					throw new SocketTimeoutException();
			} else
                Client.blockUntil(this.key, this.endTime);
		}
	}
	finally {
		if (this.key.isValid())
            this.key.interestOps(0);
	}
	return data;
}

byte []
recv() throws IOException {
	byte [] buf = this._recv(2);
	int length = ((buf[0] & 0xFF) << 8) + (buf[1] & 0xFF);
	byte [] data = this._recv(length);
    Client.verboseLog("TCP read", data);
	return data;
}

static byte []
sendrecv(SocketAddress local, SocketAddress remote, byte [] data, long endTime)
throws IOException
{
	TCPClient client = new TCPClient(endTime);
	try {
		if (local != null)
			client.bind(local);
		client.connect(remote);
		client.send(data);
		return client.recv();
	}
	finally {
		client.cleanup();
	}
}

static byte []
sendrecv(SocketAddress addr, byte [] data, long endTime) throws IOException {
	return TCPClient.sendrecv(null, addr, data, endTime);
}

}
