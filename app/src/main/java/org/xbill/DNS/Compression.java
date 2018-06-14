// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

/**
 * DNS Name Compression object.
 * @see Message
 * @see Name
 *
 * @author Brian Wellington
 */

public class Compression {

private static class Entry {
	Name name;
	int pos;
	Compression.Entry next;
}

private static final int TABLE_SIZE = 17;
private static final int MAX_POINTER = 0x3FFF;
private final Compression.Entry [] table;
private final boolean verbose = Options.check("verbosecompression");

/**
 * Creates a new Compression object.
 */
public
Compression() {
    this.table = new Compression.Entry[Compression.TABLE_SIZE];
}

/**
 * Adds a compression entry mapping a name to a position in a message.
 * @param pos The position at which the name is added.
 * @param name The name being added to the message.
 */
public void
add(int pos, Name name) {
	if (pos > Compression.MAX_POINTER)
		return;
	int row = (name.hashCode() & 0x7FFFFFFF) % Compression.TABLE_SIZE;
	Compression.Entry entry = new Compression.Entry();
	entry.name = name;
	entry.pos = pos;
	entry.next = this.table[row];
    this.table[row] = entry;
	if (this.verbose)
		System.err.println("Adding " + name + " at " + pos);
}

/**
 * Retrieves the position of the given name, if it has been previously
 * included in the message.
 * @param name The name to find in the compression table.
 * @return The position of the name, or -1 if not found.
 */
public int
get(Name name) {
	int row = (name.hashCode() & 0x7FFFFFFF) % Compression.TABLE_SIZE;
	int pos = -1;
	for (Compression.Entry entry = this.table[row]; entry != null; entry = entry.next) {
		if (entry.name.equals(name))
			pos = entry.pos;
	}
	if (this.verbose)
		System.err.println("Looking for " + name + ", found " + pos);
	return pos;
}

}
