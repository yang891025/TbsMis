// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import org.xbill.DNS.Tokenizer.Token;
import org.xbill.DNS.Tokenizer.TokenizerException;

import java.io.*;
import java.util.*;

/**
 * A DNS master file parser.  This incrementally parses the file, returning
 * one record at a time.  When directives are seen, they are added to the
 * state and used when parsing future records.
 *
 * @author Brian Wellington
 */

public class Master {

private Name origin;
private File file;
private Record last;
private long defaultTTL;
private Master included;
private final Tokenizer st;
private int currentType;
private int currentDClass;
private long currentTTL;
private boolean needSOATTL;

private Generator generator;
private List generators;
private boolean noExpandGenerate;

Master(File file, Name origin, long initialTTL) throws IOException {
	if (origin != null && !origin.isAbsolute()) {
		throw new RelativeNameException(origin);
	}
	this.file = file;
    this.st = new Tokenizer(file);
	this.origin = origin;
    this.defaultTTL = initialTTL;
}

/**
 * Initializes the master file reader and opens the specified master file.
 * @param filename The master file.
 * @param origin The initial origin to append to relative names.
 * @param ttl The initial default TTL.
 * @throws IOException The master file could not be opened.
 */
public
Master(String filename, Name origin, long ttl) throws IOException {
	this(new File(filename), origin, ttl);
}

/**
 * Initializes the master file reader and opens the specified master file.
 * @param filename The master file.
 * @param origin The initial origin to append to relative names.
 * @throws IOException The master file could not be opened.
 */
public
Master(String filename, Name origin) throws IOException {
	this(new File(filename), origin, -1);
}

/**
 * Initializes the master file reader and opens the specified master file.
 * @param filename The master file.
 * @throws IOException The master file could not be opened.
 */
public
Master(String filename) throws IOException {
	this(new File(filename), null, -1);
}

/**
 * Initializes the master file reader.
 * @param in The input stream containing a master file.
 * @param origin The initial origin to append to relative names.
 * @param ttl The initial default TTL.
 */
public
Master(InputStream in, Name origin, long ttl) {
	if (origin != null && !origin.isAbsolute()) {
		throw new RelativeNameException(origin);
	}
    this.st = new Tokenizer(in);
	this.origin = origin;
    this.defaultTTL = ttl;
}

/**
 * Initializes the master file reader.
 * @param in The input stream containing a master file.
 * @param origin The initial origin to append to relative names.
 */
public
Master(InputStream in, Name origin) {
	this(in, origin, -1);
}

/**
 * Initializes the master file reader.
 * @param in The input stream containing a master file.
 */
public
Master(InputStream in) {
	this(in, null, -1);
}

private Name
parseName(String s, Name origin) throws TextParseException {
	try {
		return Name.fromString(s, origin);
	}
	catch (TextParseException e) {
		throw this.st.exception(e.getMessage());
	}
}

private void
parseTTLClassAndType() throws IOException {
	String s;
	boolean seen_class = false;


	// This is a bit messy, since any of the following are legal:
	//   class ttl type
	//   ttl class type
	//   class type
	//   ttl type
	//   type
	seen_class = false;
	s = this.st.getString();
	if ((this.currentDClass = DClass.value(s)) >= 0) {
		s = this.st.getString();
		seen_class = true;
	}

    this.currentTTL = -1;
	try {
        this.currentTTL = TTL.parseTTL(s);
		s = this.st.getString();
	}
	catch (NumberFormatException e) {
		if (this.defaultTTL >= 0)
            this.currentTTL = this.defaultTTL;
		else if (this.last != null)
            this.currentTTL = this.last.getTTL();
	}

	if (!seen_class) {
		if ((this.currentDClass = DClass.value(s)) >= 0) {
			s = this.st.getString();
		} else {
            this.currentDClass = DClass.IN;
		}
	}

	if ((this.currentType = Type.value(s)) < 0)
		throw this.st.exception("Invalid type '" + s + "'");

	// BIND allows a missing TTL for the initial SOA record, and uses
	// the SOA minimum value.  If the SOA is not the first record,
	// this is an error.
	if (this.currentTTL < 0) {
		if (this.currentType != Type.SOA)
			throw this.st.exception("missing TTL");
        this.needSOATTL = true;
        this.currentTTL = 0;
	}
}

private long
parseUInt32(String s) {
	if (!Character.isDigit(s.charAt(0)))
		return -1;
	try {
		long l = Long.parseLong(s);
		if (l < 0 || l > 0xFFFFFFFFL)
			return -1;
		return l;
	}
	catch (NumberFormatException e) {
		return -1;
	}
}

private void
startGenerate() throws IOException {
	String s;
	int n;

	// The first field is of the form start-end[/step]
	// Regexes would be useful here.
	s = this.st.getIdentifier();
	n = s.indexOf("-");
	if (n < 0)
		throw this.st.exception("Invalid $GENERATE range specifier: " + s);
	String startstr = s.substring(0, n);
	String endstr = s.substring(n + 1);
	String stepstr = null;
	n = endstr.indexOf("/");
	if (n >= 0) {
		stepstr = endstr.substring(n + 1);
		endstr = endstr.substring(0, n);
	}
	long start = this.parseUInt32(startstr);
	long end = this.parseUInt32(endstr);
	long step;
	if (stepstr != null)
		step = this.parseUInt32(stepstr);
	else
		step = 1;
	if (start < 0 || end < 0 || start > end || step <= 0)
		throw this.st.exception("Invalid $GENERATE range specifier: " + s);

	// The next field is the name specification.
	String nameSpec = this.st.getIdentifier();

	// Then the ttl/class/type, in the same form as a normal record.
	// Only some types are supported.
    this.parseTTLClassAndType();
	if (!Generator.supportedType(this.currentType))
		throw this.st.exception("$GENERATE does not support " +
				   Type.string(this.currentType) + " records");

	// Next comes the rdata specification.
	String rdataSpec = this.st.getIdentifier();

	// That should be the end.  However, we don't want to move past the
	// line yet, so put back the EOL after reading it.
    this.st.getEOL();
    this.st.unget();

    this.generator = new Generator(start, end, step, nameSpec,
            this.currentType, this.currentDClass, this.currentTTL,
				  rdataSpec, this.origin);
	if (this.generators == null)
        this.generators = new ArrayList(1);
    this.generators.add(this.generator);
}

private void
endGenerate() throws IOException {
	// Read the EOL that we put back before.
    this.st.getEOL();

    this.generator = null;
}

private Record
nextGenerated() throws IOException {
	try {
		return this.generator.nextRecord();
	}
	catch (TokenizerException e) {
		throw this.st.exception("Parsing $GENERATE: " + e.getBaseMessage());
	}
	catch (TextParseException e) {
		throw this.st.exception("Parsing $GENERATE: " + e.getMessage());
	}
}

/**
 * Returns the next record in the master file.  This will process any
 * directives before the next record.
 * @return The next record.
 * @throws IOException The master file could not be read, or was syntactically
 * invalid.
 */
public Record
_nextRecord() throws IOException {
	Token token;
	String s;

	if (this.included != null) {
		Record rec = this.included.nextRecord();
		if (rec != null)
			return rec;
        this.included = null;
	}
	if (this.generator != null) {
		Record rec = this.nextGenerated();
		if (rec != null)
			return rec;
        this.endGenerate();
	}
	while (true) {
		Name name;

		token = this.st.get(true, false);
		if (token.type == Tokenizer.WHITESPACE) {
			Token next = this.st.get();
			if (next.type == Tokenizer.EOL)
				continue;
			else if (next.type == Tokenizer.EOF)
				return null;
			else
                this.st.unget();
			if (this.last == null)
				throw this.st.exception("no owner");
			name = this.last.getName();
		}
		else if (token.type == Tokenizer.EOL)
			continue;
		else if (token.type == Tokenizer.EOF)
			return null;
		else if (token.value.charAt(0) == '$') {
			s = token.value;

			if (s.equalsIgnoreCase("$ORIGIN")) {
                this.origin = this.st.getName(Name.root);
                this.st.getEOL();
				continue;
			} else if (s.equalsIgnoreCase("$TTL")) {
                this.defaultTTL = this.st.getTTL();
                this.st.getEOL();
				continue;
			} else  if (s.equalsIgnoreCase("$INCLUDE")) {
				String filename = this.st.getString();
				File newfile;
				if (this.file != null) {
					String parent = this.file.getParent();
					newfile = new File(parent, filename);
				} else {
					newfile = new File(filename);
				}
				Name incorigin = this.origin;
				token = this.st.get();
				if (token.isString()) {
					incorigin = this.parseName(token.value,
							      Name.root);
                    this.st.getEOL();
				}
                this.included = new Master(newfile, incorigin,
                        this.defaultTTL);
				/*
				 * If we continued, we wouldn't be looking in
				 * the new file.  Recursing works better.
				 */
				return this.nextRecord();
			} else  if (s.equalsIgnoreCase("$GENERATE")) {
				if (this.generator != null)
					throw new IllegalStateException
						("cannot nest $GENERATE");
                this.startGenerate();
				if (this.noExpandGenerate) {
                    this.endGenerate();
					continue;
				}
				return this.nextGenerated();
			} else {
				throw this.st.exception("Invalid directive: " + s);
			}
		} else {
			s = token.value;
			name = this.parseName(s, this.origin);
			if (this.last != null && name.equals(this.last.getName())) {
				name = this.last.getName();
			}
		}

        this.parseTTLClassAndType();
        this.last = Record.fromString(name, this.currentType, this.currentDClass,
                this.currentTTL, this.st, this.origin);
		if (this.needSOATTL) {
			long ttl = ((SOARecord) this.last).getMinimum();
            this.last.setTTL(ttl);
            this.defaultTTL = ttl;
            this.needSOATTL = false;
		}
		return this.last;
	}
}

/**
 * Returns the next record in the master file.  This will process any
 * directives before the next record.
 * @return The next record.
 * @throws IOException The master file could not be read, or was syntactically
 * invalid.
 */
public Record
nextRecord() throws IOException {
	Record rec = null;
	try {
		rec = this._nextRecord();
	}
	finally {
		if (rec == null) {
            this.st.close();
		}
	}
	return rec;
}

/**
 * Specifies whether $GENERATE statements should be expanded.  Whether
 * expanded or not, the specifications for generated records are available
 * by calling {@link #generators}.  This must be called before a $GENERATE
 * statement is seen during iteration to have an effect.
 */
public void
expandGenerate(boolean wantExpand) {
    this.noExpandGenerate = !wantExpand;
}

/**
 * Returns an iterator over the generators specified in the master file; that
 * is, the parsed contents of $GENERATE statements.
 * @see Generator
 */
public Iterator
generators() {
	if (this.generators != null)
		return Collections.unmodifiableList(this.generators).iterator();
	else
		return Collections.EMPTY_LIST.iterator();
}

@Override
protected void
finalize() {
    this.st.close();
}

}
