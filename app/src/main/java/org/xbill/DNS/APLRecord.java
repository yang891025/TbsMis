// Copyright (c) 2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.*;
import java.net.*;
import java.util.*;

import org.xbill.DNS.Tokenizer.Token;
import org.xbill.DNS.utils.*;

/**
 * APL - Address Prefix List.  See RFC 3123.
 *
 * @author Brian Wellington
 */

/*
 * Note: this currently uses the same constants as the Address class;
 * this could change if more constants are defined for APL records.
 */

public class APLRecord extends Record {

public static class Element {
	public final int family;
	public final boolean negative;
	public final int prefixLength;
	public final Object address;

	private
	Element(int family, boolean negative, Object address, int prefixLength)
	{
		this.family = family;
		this.negative = negative;
		this.address = address;
		this.prefixLength = prefixLength;
		if (!APLRecord.validatePrefixLength(family, prefixLength)) {
			throw new IllegalArgumentException("invalid prefix " +
							   "length");
		}
	}

	/**
	 * Creates an APL element corresponding to an IPv4 or IPv6 prefix.
	 * @param negative Indicates if this prefix is a negation.
	 * @param address The IPv4 or IPv6 address.
	 * @param prefixLength The length of this prefix, in bits.
	 * @throws IllegalArgumentException The prefix length is invalid.
	 */
	public
	Element(boolean negative, InetAddress address, int prefixLength) {
		this(Address.familyOf(address), negative, address,
		     prefixLength);
	}

	@Override
	public String
	toString() {
		StringBuffer sb = new StringBuffer();
		if (this.negative)
			sb.append("!");
		sb.append(this.family);
		sb.append(":");
		if (this.family == Address.IPv4 || this.family == Address.IPv6)
			sb.append(((InetAddress) this.address).getHostAddress());
		else
			sb.append(base16.toString((byte []) this.address));
		sb.append("/");
		sb.append(this.prefixLength);
		return sb.toString();
	}

	@Override
	public boolean
	equals(Object arg) {
		if (arg == null || !(arg instanceof APLRecord.Element))
			return false;
		APLRecord.Element elt = (APLRecord.Element) arg;
		return this.family == elt.family &&
                this.negative == elt.negative &&
                this.prefixLength == elt.prefixLength &&
                this.address.equals(elt.address);
	}

	@Override
	public int
	hashCode() {
		return this.address.hashCode() + this.prefixLength + (this.negative ? 1 : 0);
	}
}

private static final long serialVersionUID = -1348173791712935864L;

private List elements;

APLRecord() {}

@Override
Record
getObject() {
	return new APLRecord();
}

private static boolean
validatePrefixLength(int family, int prefixLength) {
	if (prefixLength < 0 || prefixLength >= 256)
		return false;
    return !(family == Address.IPv4 && prefixLength > 32 ||
            family == Address.IPv6 && prefixLength > 128);
}

/**
 * Creates an APL Record from the given data.
 * @param elements The list of APL elements.
 */
public
APLRecord(Name name, int dclass, long ttl, List elements) {
	super(name, Type.APL, dclass, ttl);
	this.elements = new ArrayList(elements.size());
	for (Iterator it = elements.iterator(); it.hasNext(); ) {
		Object o = it.next();
		if (!(o instanceof APLRecord.Element)) {
			throw new IllegalArgumentException("illegal element");
		}
		APLRecord.Element element = (APLRecord.Element) o;
		if (element.family != Address.IPv4 &&
		    element.family != Address.IPv6)
		{
			throw new IllegalArgumentException("unknown family");
		}
		this.elements.add(element);

	}
}

private static byte []
parseAddress(byte [] in, int length) throws WireParseException {
	if (in.length > length)
		throw new WireParseException("invalid address length");
	if (in.length == length)
		return in;
	byte [] out = new byte[length];
	System.arraycopy(in, 0, out, 0, in.length);
	return out;
}

@Override
void
rrFromWire(DNSInput in) throws IOException {
    this.elements = new ArrayList(1);
	while (in.remaining() != 0) {
		int family = in.readU16();
		int prefix = in.readU8();
		int length = in.readU8();
		boolean negative = (length & 0x80) != 0;
		length &= ~0x80;

		byte [] data = in.readByteArray(length);
		APLRecord.Element element;
		if (!APLRecord.validatePrefixLength(family, prefix)) {
			throw new WireParseException("invalid prefix length");
		}

		if (family == Address.IPv4 || family == Address.IPv6) {
			data = APLRecord.parseAddress(data,
					    Address.addressLength(family));
			InetAddress addr = InetAddress.getByAddress(data);
			element = new APLRecord.Element(negative, addr, prefix);
		} else {
			element = new APLRecord.Element(family, negative, data, prefix);
		}
        this.elements.add(element);

	}
}

@Override
void
rdataFromString(Tokenizer st, Name origin) throws IOException {
    this.elements = new ArrayList(1);
	while (true) {
		Token t = st.get();
		if (!t.isString())
			break;

		boolean negative = false;
		int family = 0;
		int prefix = 0;

		String s = t.value;
		int start = 0;
		if (s.startsWith("!")) {
			negative = true;
			start = 1;
		}
		int colon = s.indexOf(':', start);
		if (colon < 0)
			throw st.exception("invalid address prefix element");
		int slash = s.indexOf('/', colon);
		if (slash < 0)
			throw st.exception("invalid address prefix element");

		String familyString = s.substring(start, colon);
		String addressString = s.substring(colon + 1, slash);
		String prefixString = s.substring(slash + 1);

		try {
			family = Integer.parseInt(familyString);
		}
		catch (NumberFormatException e) {
			throw st.exception("invalid family");
		}
		if (family != Address.IPv4 && family != Address.IPv6)
			throw st.exception("unknown family");

		try {
			prefix = Integer.parseInt(prefixString);
		}
		catch (NumberFormatException e) {
			throw st.exception("invalid prefix length");
		}

		if (!APLRecord.validatePrefixLength(family, prefix)) {
			throw st.exception("invalid prefix length");
		}

		byte [] bytes = Address.toByteArray(addressString, family);
		if (bytes == null)
			throw st.exception("invalid IP address " +
					   addressString);

		InetAddress address = InetAddress.getByAddress(bytes);
        this.elements.add(new APLRecord.Element(negative, address, prefix));
	}
	st.unget();
}

@Override
String
rrToString() {
	StringBuffer sb = new StringBuffer();
	for (Iterator it = this.elements.iterator(); it.hasNext(); ) {
		APLRecord.Element element = (APLRecord.Element) it.next();
		sb.append(element);
		if (it.hasNext())
			sb.append(" ");
	}
	return sb.toString();
}

/** Returns the list of APL elements. */
public List
getElements() {
	return this.elements;
}

private static int
addressLength(byte [] addr) {
	for (int i = addr.length - 1; i >= 0; i--) {
		if (addr[i] != 0)
			return i + 1;
	}
	return 0;
}

@Override
void
rrToWire(DNSOutput out, Compression c, boolean canonical) {
	for (Iterator it = this.elements.iterator(); it.hasNext(); ) {
		APLRecord.Element element = (APLRecord.Element) it.next();
		int length = 0;
		byte [] data;
		if (element.family == Address.IPv4 ||
		    element.family == Address.IPv6)
		{
			InetAddress addr = (InetAddress) element.address;
			data = addr.getAddress();
			length = APLRecord.addressLength(data);
		} else {
			data = (byte []) element.address;
			length = data.length;
		}
		int wlength = length;
		if (element.negative) {
			wlength |= 0x80;
		}
		out.writeU16(element.family);
		out.writeU8(element.prefixLength);
		out.writeU8(wlength);
		out.writeByteArray(data, 0, length);
	}
}

}
