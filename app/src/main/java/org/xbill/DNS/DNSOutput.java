// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

/**
 * A class for rendering DNS messages.
 *
 * @author Brian Wellington
 */


public class DNSOutput {

private byte [] array;
private int pos;
private int saved_pos;

/**
 * Create a new DNSOutput with a specified size.
 * @param size The initial size
 */
public
DNSOutput(int size) {
    this.array = new byte[size];
    this.pos = 0;
    this.saved_pos = -1;
}

/**
 * Create a new DNSOutput
 */
public
DNSOutput() {
	this(32);
}

/**
 * Returns the current position.
 */
public int
current() {
	return this.pos;
}

private void
check(long val, int bits) {
	long max = 1;
	max <<= bits;
	if (val < 0 || val > max) {
		throw new IllegalArgumentException(val + " out of range for " +
						   bits + " bit value");
	}
}

private void
need(int n) {
	if (this.array.length - this.pos >= n) {
		return;
	}
	int newsize = this.array.length * 2;
	if (newsize < this.pos + n) {
		newsize = this.pos + n;
	}
	byte [] newarray = new byte[newsize];
	System.arraycopy(this.array, 0, newarray, 0, this.pos);
    this.array = newarray;
}

/**
 * Resets the current position of the output stream to the specified index.
 * @param index The new current position.
 * @throws IllegalArgumentException The index is not within the output.
 */
public void
jump(int index) {
	if (index > this.pos) {
		throw new IllegalArgumentException("cannot jump past " +
						   "end of data");
	}
    this.pos = index;
}

/**
 * Saves the current state of the output stream.
 * @throws IllegalArgumentException The index is not within the output.
 */
public void
save() {
    this.saved_pos = this.pos;
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
    this.saved_pos = -1;
}

/**
 * Writes an unsigned 8 bit value to the stream.
 * @param val The value to be written
 */
public void
writeU8(int val) {
    this.check(val, 8);
    this.need(1);
    this.array[this.pos++] = (byte)(val & 0xFF);
}

/**
 * Writes an unsigned 16 bit value to the stream.
 * @param val The value to be written
 */
public void
writeU16(int val) {
    this.check(val, 16);
    this.need(2);
    this.array[this.pos++] = (byte)(val >>> 8 & 0xFF);
    this.array[this.pos++] = (byte)(val & 0xFF);
}

/**
 * Writes an unsigned 32 bit value to the stream.
 * @param val The value to be written
 */
public void
writeU32(long val) {
    this.check(val, 32);
    this.need(4);
    this.array[this.pos++] = (byte)(val >>> 24 & 0xFF);
    this.array[this.pos++] = (byte)(val >>> 16 & 0xFF);
    this.array[this.pos++] = (byte)(val >>> 8 & 0xFF);
    this.array[this.pos++] = (byte)(val & 0xFF);
}

/**
 * Writes a byte array to the stream.
 * @param b The array to write.
 * @param off The offset of the array to start copying data from.
 * @param len The number of bytes to write.
 */
public void
writeByteArray(byte [] b, int off, int len) {
    this.need(len);
	System.arraycopy(b, off, this.array, this.pos, len);
    this.pos += len;
}

/**
 * Writes a byte array to the stream.
 * @param b The array to write.
 */
public void
writeByteArray(byte [] b) {
    this.writeByteArray(b, 0, b.length);
}

/**
 * Writes a counted string from the stream.  A counted string is a one byte
 * value indicating string length, followed by bytes of data.
 * @param s The string to write.
 */
public void
writeCountedString(byte [] s) {
	if (s.length > 0xFF) {
		throw new IllegalArgumentException("Invalid counted string");
	}
    this.need(1 + s.length);
    this.array[this.pos++] = (byte)(s.length & 0xFF);
    this.writeByteArray(s, 0, s.length);
}

/**
 * Returns a byte array containing the current contents of the stream.
 */
public byte []
toByteArray() {
	byte [] out = new byte[this.pos];
	System.arraycopy(this.array, 0, out, 0, this.pos);
	return out;
}

}
