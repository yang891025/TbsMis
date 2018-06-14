// Copyright (c) 2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

/**
 * An class for parsing DNS messages.
 *
 * @author Brian Wellington
 */

public class DNSInput {

private final byte [] array;
private int pos;
private int end;
private int saved_pos;
private int saved_end;

/**
 * Creates a new DNSInput
 * @param input The byte array to read from
 */
public
DNSInput(byte [] input) {
    this.array = input;
    this.pos = 0;
    this.end = this.array.length;
    this.saved_pos = -1;
    this.saved_end = -1;
}

/**
 * Returns the current position.
 */
public int
current() {
	return this.pos;
}

/**
 * Returns the number of bytes that can be read from this stream before
 * reaching the end.
 */
public int
remaining() {
	return this.end - this.pos;
}

private void
require(int n) throws WireParseException{
	if (n > this.remaining()) {
		throw new WireParseException("end of input");
	}
}

/**
 * Marks the following bytes in the stream as active.
 * @param len The number of bytes in the active region.
 * @throws IllegalArgumentException The number of bytes in the active region
 * is longer than the remainder of the input.
 */
public void
setActive(int len) {
	if (len > this.array.length - this.pos) {
		throw new IllegalArgumentException("cannot set active " +
						   "region past end of input");
	}
    this.end = this.pos + len;
}

/**
 * Clears the active region of the string.  Further operations are not
 * restricted to part of the input.
 */
public void
clearActive() {
    this.end = this.array.length;
}

/**
 * Resets the current position of the input stream to the specified index,
 * and clears the active region.
 * @param index The position to continue parsing at.
 * @throws IllegalArgumentException The index is not within the input.
 */
public void
jump(int index) {
	if (index >= this.array.length) {
		throw new IllegalArgumentException("cannot jump past " +
						   "end of input");
	}
    this.pos = index;
    this.end = this.array.length;
}

/**
 * Saves the current state of the input stream.  Both the current position and
 * the end of the active region are saved.
 * @throws IllegalArgumentException The index is not within the input.
 */
public void
save() {
    this.saved_pos = this.pos;
    this.saved_end = this.end;
}

/**
 * Restores the input stream to its state before the call to {@link #save}.
 */
public void
restore() {
	if (this.saved_pos < 0) {
		throw new IllegalStateException("no previous state");
	}
    this.pos = this.saved_pos;
    this.end = this.saved_end;
    this.saved_pos = -1;
    this.saved_end = -1;
}

/**
 * Reads an unsigned 8 bit value from the stream, as an int.
 * @return An unsigned 8 bit value.
 * @throws WireParseException The end of the stream was reached.
 */
public int
readU8() throws WireParseException {
    this.require(1);
	return this.array[this.pos++] & 0xFF;
}

/**
 * Reads an unsigned 16 bit value from the stream, as an int.
 * @return An unsigned 16 bit value.
 * @throws WireParseException The end of the stream was reached.
 */
public int
readU16() throws WireParseException {
    this.require(2);
	int b1 = this.array[this.pos++] & 0xFF;
	int b2 = this.array[this.pos++] & 0xFF;
	return (b1 << 8) + b2;
}

/**
 * Reads an unsigned 32 bit value from the stream, as a long.
 * @return An unsigned 32 bit value.
 * @throws WireParseException The end of the stream was reached.
 */
public long
readU32() throws WireParseException {
    this.require(4);
	int b1 = this.array[this.pos++] & 0xFF;
	int b2 = this.array[this.pos++] & 0xFF;
	int b3 = this.array[this.pos++] & 0xFF;
	int b4 = this.array[this.pos++] & 0xFF;
	return ((long)b1 << 24) + (b2 << 16) + (b3 << 8) + b4;
}

/**
 * Reads a byte array of a specified length from the stream into an existing
 * array.
 * @param b The array to read into.
 * @param off The offset of the array to start copying data into.
 * @param len The number of bytes to copy.
 * @throws WireParseException The end of the stream was reached.
 */
public void
readByteArray(byte [] b, int off, int len) throws WireParseException {
    this.require(len);
	System.arraycopy(this.array, this.pos, b, off, len);
    this.pos += len;
}

/**
 * Reads a byte array of a specified length from the stream.
 * @return The byte array.
 * @throws WireParseException The end of the stream was reached.
 */
public byte []
readByteArray(int len) throws WireParseException {
    this.require(len);
	byte [] out = new byte[len];
	System.arraycopy(this.array, this.pos, out, 0, len);
    this.pos += len;
	return out;
}

/**
 * Reads a byte array consisting of the remainder of the stream (or the
 * active region, if one is set.
 * @return The byte array.
 */
public byte []
readByteArray() {
	int len = this.remaining();
	byte [] out = new byte[len];
	System.arraycopy(this.array, this.pos, out, 0, len);
    this.pos += len;
	return out;
}

/**
 * Reads a counted string from the stream.  A counted string is a one byte
 * value indicating string length, followed by bytes of data.
 * @return A byte array containing the string.
 * @throws WireParseException The end of the stream was reached.
 */
public byte []
readCountedString() throws WireParseException {
    this.require(1);
	int len = this.array[this.pos++] & 0xFF;
	return this.readByteArray(len);
}

}
