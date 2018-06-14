// Copyright (c) 2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.*;
import java.net.*;
import org.xbill.DNS.utils.*;

/**
 * IPsec Keying Material (RFC 4025)
 *
 * @author Brian Wellington
 */

public class IPSECKEYRecord extends Record {

private static final long serialVersionUID = 3050449702765909687L;

public static class Algorithm {
	private Algorithm() {}

	public static final int DSA = 1;
	public static final int RSA = 2;
}

public static class Gateway {
	private Gateway() {}

	public static final int None = 0;
	public static final int IPv4 = 1;
	public static final int IPv6 = 2;
	public static final int Name = 3;
}

private int precedence;
private int gatewayType;
private int algorithmType;
private Object gateway;
private byte [] key;

IPSECKEYRecord() {} 

@Override
Record
getObject() {
	return new IPSECKEYRecord();
}

/**
 * Creates an IPSECKEY Record from the given data.
 * @param precedence The record's precedence.
 * @param gatewayType The record's gateway type.
 * @param algorithmType The record's algorithm type.
 * @param gateway The record's gateway.
 * @param key The record's public key.
 */
public
IPSECKEYRecord(Name name, int dclass, long ttl, int precedence,
	       int gatewayType, int algorithmType, Object gateway,
	       byte [] key)
{
	super(name, Type.IPSECKEY, dclass, ttl);
	this.precedence = Record.checkU8("precedence", precedence);
	this.gatewayType = Record.checkU8("gatewayType", gatewayType);
	this.algorithmType = Record.checkU8("algorithmType", algorithmType);
	switch (gatewayType) {
	case IPSECKEYRecord.Gateway.None:
		this.gateway = null;
		break;
	case IPSECKEYRecord.Gateway.IPv4:
		if (!(gateway instanceof InetAddress))
			throw new IllegalArgumentException("\"gateway\" " +
							   "must be an IPv4 " +
							   "address");
		this.gateway = gateway;
		break;
	case IPSECKEYRecord.Gateway.IPv6:
		if (!(gateway instanceof Inet6Address))
			throw new IllegalArgumentException("\"gateway\" " +
							   "must be an IPv6 " +
							   "address");
		this.gateway = gateway;
		break;
	case IPSECKEYRecord.Gateway.Name:
		if (!(gateway instanceof Name))
			throw new IllegalArgumentException("\"gateway\" " +
							   "must be a DNS " +
							   "name");
		this.gateway = Record.checkName("gateway", (Name) gateway);
		break;
	default:
		throw new IllegalArgumentException("\"gatewayType\" " +
						   "must be between 0 and 3");
	}

	this.key = key;
}

@Override
void
rrFromWire(DNSInput in) throws IOException {
    this.precedence = in.readU8();
    this.gatewayType = in.readU8();
    this.algorithmType = in.readU8();
	switch (this.gatewayType) {
	case IPSECKEYRecord.Gateway.None:
        this.gateway = null;
		break;
	case IPSECKEYRecord.Gateway.IPv4:
        this.gateway = InetAddress.getByAddress(in.readByteArray(4));
		break;
	case IPSECKEYRecord.Gateway.IPv6:
        this.gateway = InetAddress.getByAddress(in.readByteArray(16));
		break;
	case IPSECKEYRecord.Gateway.Name:
        this.gateway = new Name(in);
		break;
	default:
		throw new WireParseException("invalid gateway type");
	}
	if (in.remaining() > 0)
        this.key = in.readByteArray();
}

@Override
void
rdataFromString(Tokenizer st, Name origin) throws IOException {
    this.precedence = st.getUInt8();
    this.gatewayType = st.getUInt8();
    this.algorithmType = st.getUInt8();
	switch (this.gatewayType) {
	case IPSECKEYRecord.Gateway.None:
		String s = st.getString();
		if (!s.equals("."))
			throw new TextParseException("invalid gateway format");
        this.gateway = null;
		break;
	case IPSECKEYRecord.Gateway.IPv4:
        this.gateway = st.getAddress(Address.IPv4);
		break;
	case IPSECKEYRecord.Gateway.IPv6:
        this.gateway = st.getAddress(Address.IPv6);
		break;
	case IPSECKEYRecord.Gateway.Name:
        this.gateway = st.getName(origin);
		break;
	default:
		throw new WireParseException("invalid gateway type");
	}
    this.key = st.getBase64(false);
}

@Override
String
rrToString() {
	StringBuffer sb = new StringBuffer();
	sb.append(this.precedence);
	sb.append(" ");
	sb.append(this.gatewayType);
	sb.append(" ");
	sb.append(this.algorithmType);
	sb.append(" ");
	switch (this.gatewayType) {
	case IPSECKEYRecord.Gateway.None:
		sb.append(".");
		break;
	case IPSECKEYRecord.Gateway.IPv4:
	case IPSECKEYRecord.Gateway.IPv6:
		InetAddress gatewayAddr = (InetAddress) this.gateway;
		sb.append(gatewayAddr.getHostAddress());
		break;
	case IPSECKEYRecord.Gateway.Name:
		sb.append(this.gateway);
		break;
	}
	if (this.key != null) {
		sb.append(" ");
		sb.append(base64.toString(this.key));
	}
	return sb.toString();
}

/** Returns the record's precedence. */
public int
getPrecedence() {
	return this.precedence;
}

/** Returns the record's gateway type. */
public int
getGatewayType() {
	return this.gatewayType;
}

/** Returns the record's algorithm type. */
public int
getAlgorithmType() {
	return this.algorithmType;
}

/** Returns the record's gateway. */
public Object
getGateway() {
	return this.gateway;
}

/** Returns the record's public key */
public byte []
getKey() {
	return this.key;
}

@Override
void
rrToWire(DNSOutput out, Compression c, boolean canonical) {
	out.writeU8(this.precedence);
	out.writeU8(this.gatewayType);
	out.writeU8(this.algorithmType);
	switch (this.gatewayType) {
	case IPSECKEYRecord.Gateway.None:
		break;
	case IPSECKEYRecord.Gateway.IPv4:
	case IPSECKEYRecord.Gateway.IPv6:
		InetAddress gatewayAddr = (InetAddress) this.gateway;
		out.writeByteArray(gatewayAddr.getAddress());
		break;
	case IPSECKEYRecord.Gateway.Name:
		Name gatewayName = (Name) this.gateway;
		gatewayName.toWire(out, null, canonical);
		break;
	}
	if (this.key != null)
		out.writeByteArray(this.key);
}

}
