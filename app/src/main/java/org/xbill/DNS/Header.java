// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.*;
import java.util.*;

/**
 * A DNS message header
 * @see Message
 *
 * @author Brian Wellington
 */

public class Header implements Cloneable {

private int id; 
private int flags;
private int [] counts;

private static final Random random = new Random();

/** The length of a DNS Header in wire format. */
public static final int LENGTH = 12;

private void
init() {
    this.counts = new int[4];
    this.flags = 0;
    this.id = -1;
}

/**
 * Create a new empty header.
 * @param id The message id
 */
public
Header(int id) {
    this.init();
    this.setID(id);
}

/**
 * Create a new empty header with a random message id
 */
public
Header() {
    this.init();
}

/**
 * Parses a Header from a stream containing DNS wire format.
 */
Header(DNSInput in) throws IOException {
	this(in.readU16());
    this.flags = in.readU16();
	for (int i = 0; i < this.counts.length; i++)
        this.counts[i] = in.readU16();
}

/**
 * Creates a new Header from its DNS wire format representation
 * @param b A byte array containing the DNS Header.
 */
public
Header(byte [] b) throws IOException {
	this(new DNSInput(b));
}

void
toWire(DNSOutput out) {
	out.writeU16(this.getID());
	out.writeU16(this.flags);
	for (int i = 0; i < this.counts.length; i++)
		out.writeU16(this.counts[i]);
}

public byte []
toWire() {
	DNSOutput out = new DNSOutput();
    this.toWire(out);
	return out.toByteArray();
}

private static boolean
validFlag(int bit) {
	return bit >= 0 && bit <= 0xF && Flags.isFlag(bit);
}

private static void
checkFlag(int bit) {
	if (!Header.validFlag(bit))
		throw new IllegalArgumentException("invalid flag bit " + bit);
}

/**
 * Sets a flag to the supplied value
 * @see Flags
 */
public void
setFlag(int bit) {
    Header.checkFlag(bit);
	// bits are indexed from left to right
    this.flags |= 1 << 15 - bit;
}

/**
 * Sets a flag to the supplied value
 * @see Flags
 */
public void
unsetFlag(int bit) {
    Header.checkFlag(bit);
	// bits are indexed from left to right
    this.flags &= ~(1 << 15 - bit);
}

/**
 * Retrieves a flag
 * @see Flags
 */
public boolean
getFlag(int bit) {
    Header.checkFlag(bit);
	// bits are indexed from left to right
	return (this.flags & 1 << 15 - bit) != 0;
}

boolean []
getFlags() {
	boolean [] array = new boolean[16];
	for (int i = 0; i < array.length; i++)
		if (Header.validFlag(i))
			array[i] = this.getFlag(i);
	return array;
}

/**
 * Retrieves the message ID
 */
public int
getID() {
	if (this.id >= 0)
		return this.id;
	synchronized (this) {
		if (this.id < 0)
            this.id = Header.random.nextInt(0xffff);
		return this.id;
	}
}

/**
 * Sets the message ID
 */
public void
setID(int id) {
	if (id < 0 || id > 0xffff)
		throw new IllegalArgumentException("DNS message ID " + id +
						   " is out of range");
	this.id = id;
}

/**
 * Sets the message's rcode
 * @see Rcode
 */
public void
setRcode(int value) {
	if (value < 0 || value > 0xF)
		throw new IllegalArgumentException("DNS Rcode " + value +
						   " is out of range");
    this.flags &= ~0xF;
    this.flags |= value;
}

/**
 * Retrieves the mesasge's rcode
 * @see Rcode
 */
public int
getRcode() {
	return this.flags & 0xF;
}

/**
 * Sets the message's opcode
 * @see Opcode
 */
public void
setOpcode(int value) {
	if (value < 0 || value > 0xF)
		throw new IllegalArgumentException("DNS Opcode " + value +
						   "is out of range");
    this.flags &= 0x87FF;
    this.flags |= value << 11;
}

/**
 * Retrieves the mesasge's opcode
 * @see Opcode
 */
public int
getOpcode() {
	return this.flags >> 11 & 0xF;
}

void
setCount(int field, int value) {
	if (value < 0 || value > 0xFFFF)
		throw new IllegalArgumentException("DNS section count " +
						   value + " is out of range");
    this.counts[field] = value;
}

void
incCount(int field) {
	if (this.counts[field] == 0xFFFF)
		throw new IllegalStateException("DNS section count cannot " +
						"be incremented");
    this.counts[field]++;
}

void
decCount(int field) {
	if (this.counts[field] == 0)
		throw new IllegalStateException("DNS section count cannot " +
						"be decremented");
    this.counts[field]--;
}

/**
 * Retrieves the record count for the given section
 * @see Section
 */
public int
getCount(int field) {
	return this.counts[field];
}

/** Converts the header's flags into a String */
public String
printFlags() {
	StringBuffer sb = new StringBuffer();

	for (int i = 0; i < 16; i++)
		if (Header.validFlag(i) && this.getFlag(i)) {
			sb.append(Flags.string(i));
			sb.append(" ");
		}
	return sb.toString();
}

String
toStringWithRcode(int newrcode) {
	StringBuffer sb = new StringBuffer();

	sb.append(";; ->>HEADER<<- "); 
	sb.append("opcode: " + Opcode.string(this.getOpcode()));
	sb.append(", status: " + Rcode.string(newrcode));
	sb.append(", id: " + this.getID());
	sb.append("\n");

	sb.append(";; flags: " + this.printFlags());
	sb.append("; ");
	for (int i = 0; i < 4; i++)
		sb.append(Section.string(i) + ": " + this.getCount(i) + " ");
	return sb.toString();
}

/** Converts the header into a String */
@Override
public String
toString() {
	return this.toStringWithRcode(this.getRcode());
}

/* Creates a new Header identical to the current one */
@Override
public Object
clone() {
	Header h = new Header();
	h.id = this.id;
	h.flags = this.flags;
	System.arraycopy(this.counts, 0, h.counts, 0, this.counts.length);
	return h;
}

}
