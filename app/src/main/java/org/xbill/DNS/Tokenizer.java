// Copyright (c) 2003-2004 Brian Wellington (bwelling@xbill.org)
//
// Copyright (C) 2003-2004 Nominum, Inc.
// 
// Permission to use, copy, modify, and distribute this software for any
// purpose with or without fee is hereby granted, provided that the above
// copyright notice and this permission notice appear in all copies.
//
// THE SOFTWARE IS PROVIDED "AS IS" AND NOMINUM DISCLAIMS ALL WARRANTIES
// WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL NOMINUM BE LIABLE FOR ANY
// SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
// WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
// ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT
// OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
//

package org.xbill.DNS;

import java.io.*;
import java.net.*;

import org.xbill.DNS.utils.*;

/**
 * Tokenizer is used to parse DNS records and zones from text format,
 *
 * @author Brian Wellington
 * @author Bob Halley
 */

public class Tokenizer {

private static final String delim = " \t\n;()\"";
private static final String quotes = "\"";

/** End of file */
public static final int EOF		= 0;

/** End of line */
public static final int EOL		= 1;

/** Whitespace; only returned when wantWhitespace is set */
public static final int WHITESPACE	= 2;

/** An identifier (unquoted string) */
public static final int IDENTIFIER	= 3;

/** A quoted string */
public static final int QUOTED_STRING	= 4;

/** A comment; only returned when wantComment is set */
public static final int COMMENT		= 5;

private final PushbackInputStream is;
private boolean ungottenToken;
private int multiline;
private boolean quoting;
private String delimiters;
private final Tokenizer.Token current;
private final StringBuffer sb;
private boolean wantClose;

private String filename;
private int line;

public static class Token {
	/** The type of token. */
	public int type;

	/** The value of the token, or null for tokens without values. */
	public String value;

	private
	Token() {
        this.type = -1;
        this.value = null;
	}

	private Tokenizer.Token
	set(int type, StringBuffer value) {
		if (type < 0)
			throw new IllegalArgumentException();
		this.type = type;
		this.value = value == null ? null : value.toString();
		return this;
	}

	/**
	 * Converts the token to a string containing a representation useful
	 * for debugging.
	 */
	@Override
	public String
	toString() {
		switch (this.type) {
		case Tokenizer.EOF:
			return "<eof>";
		case Tokenizer.EOL:
			return "<eol>";
		case Tokenizer.WHITESPACE:
			return "<whitespace>";
		case Tokenizer.IDENTIFIER:
			return "<identifier: " + this.value + ">";
		case Tokenizer.QUOTED_STRING:
			return "<quoted_string: " + this.value + ">";
		case Tokenizer.COMMENT:
			return "<comment: " + this.value + ">";
		default:
			return "<unknown>";
		}
	}

	/** Indicates whether this token contains a string. */
	public boolean
	isString() {
		return this.type == Tokenizer.IDENTIFIER || this.type == Tokenizer.QUOTED_STRING;
	}

	/** Indicates whether this token contains an EOL or EOF. */
	public boolean
	isEOL() {
		return this.type == Tokenizer.EOL || this.type == Tokenizer.EOF;
	}
}

static class TokenizerException extends TextParseException {
	String message;

	public
	TokenizerException(String filename, int line, String message) {
		super(filename + ":" + line + ": " + message);
		this.message = message;
	}

	public String
	getBaseMessage() {
		return this.message;
	}
}

/**
 * Creates a Tokenizer from an arbitrary input stream.
 * @param is The InputStream to tokenize.
 */
public
Tokenizer(InputStream is) {
	if (!(is instanceof BufferedInputStream))
		is = new BufferedInputStream(is);
	this.is = new PushbackInputStream(is, 2);
    this.ungottenToken = false;
    this.multiline = 0;
    this.quoting = false;
    this.delimiters = Tokenizer.delim;
    this.current = new Tokenizer.Token();
    this.sb = new StringBuffer();
    this.filename = "<none>";
    this.line = 1;
}

/**
 * Creates a Tokenizer from a string.
 * @param s The String to tokenize.
 */
public
Tokenizer(String s) {
	this(new ByteArrayInputStream(s.getBytes()));
}

/**
 * Creates a Tokenizer from a file.
 * @param f The File to tokenize.
 */
public
Tokenizer(File f) throws FileNotFoundException {
	this(new FileInputStream(f));
    this.wantClose = true;
    this.filename = f.getName();
}

private int
getChar() throws IOException {
	int c = this.is.read();
	if (c == '\r') {
		int next = this.is.read();
		if (next != '\n')
            this.is.unread(next);
		c = '\n';
	}
	if (c == '\n')
        this.line++;
	return c;
}

private void
ungetChar(int c) throws IOException {
	if (c == -1)
		return;
    this.is.unread(c);
	if (c == '\n')
        this.line--;
}

private int
skipWhitespace() throws IOException {
	int skipped = 0;
	while (true) {
		int c = this.getChar();
		if (c != ' ' && c != '\t') {
	                if (!(c == '\n' && this.multiline > 0)) {
                        this.ungetChar(c);
				return skipped;
			}
		}
		skipped++;
	}
}

private void
checkUnbalancedParens() throws TextParseException {
	if (this.multiline > 0)
		throw this.exception("unbalanced parentheses");
}

/**
 * Gets the next token from a tokenizer.
 * @param wantWhitespace If true, leading whitespace will be returned as a
 * token.
 * @param wantComment If true, comments are returned as tokens.
 * @return The next token in the stream.
 * @throws TextParseException The input was invalid.
 * @throws IOException An I/O error occurred.
 */
public Tokenizer.Token
get(boolean wantWhitespace, boolean wantComment) throws IOException {
	int type;
	int c;

	if (this.ungottenToken) {
        this.ungottenToken = false;
		if (this.current.type == Tokenizer.WHITESPACE) {
			if (wantWhitespace)
				return this.current;
		} else if (this.current.type == Tokenizer.COMMENT) {
			if (wantComment)
				return this.current;
		} else {
			if (this.current.type == Tokenizer.EOL)
                this.line++;
			return this.current;
		}
	}
	int skipped = this.skipWhitespace();
	if (skipped > 0 && wantWhitespace)
		return this.current.set(Tokenizer.WHITESPACE, null);
	type = Tokenizer.IDENTIFIER;
    this.sb.setLength(0);
	while (true) {
		c = this.getChar();
		if (c == -1 || this.delimiters.indexOf(c) != -1) {
			if (c == -1) {
				if (this.quoting)
					throw this.exception("EOF in " +
							"quoted string");
				else if (this.sb.length() == 0)
					return this.current.set(Tokenizer.EOF, null);
				else
					return this.current.set(type, this.sb);
			}
			if (this.sb.length() == 0 && type != Tokenizer.QUOTED_STRING) {
				if (c == '(') {
                    this.multiline++;
                    this.skipWhitespace();
					continue;
				} else if (c == ')') {
					if (this.multiline <= 0)
						throw this.exception("invalid " +
								"close " +
								"parenthesis");
                    this.multiline--;
                    this.skipWhitespace();
					continue;
				} else if (c == '"') {
					if (!this.quoting) {
                        this.quoting = true;
                        this.delimiters = Tokenizer.quotes;
						type = Tokenizer.QUOTED_STRING;
					} else {
                        this.quoting = false;
                        this.delimiters = Tokenizer.delim;
                        this.skipWhitespace();
					}
					continue;
				} else if (c == '\n') {
					return this.current.set(Tokenizer.EOL, null);
				} else if (c == ';') {
					while (true) {
						c = this.getChar();
						if (c == '\n' || c == -1)
							break;
                        this.sb.append((char)c);
					}
					if (wantComment) {
                        this.ungetChar(c);
						return this.current.set(Tokenizer.COMMENT, this.sb);
					} else if (c == -1 &&
						   type != Tokenizer.QUOTED_STRING)
					{
                        this.checkUnbalancedParens();
						return this.current.set(Tokenizer.EOF, null);
					} else if (this.multiline > 0) {
                        this.skipWhitespace();
                        this.sb.setLength(0);
						continue;
					} else
						return this.current.set(Tokenizer.EOL, null);
				} else
					throw new IllegalStateException();
			} else
                this.ungetChar(c);
			break;
		} else if (c == '\\') {
			c = this.getChar();
			if (c == -1)
				throw this.exception("unterminated escape sequence");
            this.sb.append('\\');
		} else if (this.quoting && c == '\n') {
			throw this.exception("newline in quoted string");
		}
        this.sb.append((char)c);
	}
	if (this.sb.length() == 0 && type != Tokenizer.QUOTED_STRING) {
        this.checkUnbalancedParens();
		return this.current.set(Tokenizer.EOF, null);
	}
	return this.current.set(type, this.sb);
}

/**
 * Gets the next token from a tokenizer, ignoring whitespace and comments.
 * @return The next token in the stream.
 * @throws TextParseException The input was invalid.
 * @throws IOException An I/O error occurred.
 */
public Tokenizer.Token
get() throws IOException {
	return this.get(false, false);
}

/**
 * Returns a token to the stream, so that it will be returned by the next call
 * to get().
 * @throws IllegalStateException There are already ungotten tokens.
 */
public void
unget() {
	if (this.ungottenToken)
		throw new IllegalStateException
				("Cannot unget multiple tokens");
	if (this.current.type == Tokenizer.EOL)
        this.line--;
    this.ungottenToken = true;
}

/**
 * Gets the next token from a tokenizer and converts it to a string.
 * @return The next token in the stream, as a string.
 * @throws TextParseException The input was invalid or not a string.
 * @throws IOException An I/O error occurred.
 */
public String
getString() throws IOException {
	Tokenizer.Token next = this.get();
	if (!next.isString()) {
		throw this.exception("expected a string");
	}
	return next.value;
}

private String
_getIdentifier(String expected) throws IOException {
	Tokenizer.Token next = this.get();
	if (next.type != Tokenizer.IDENTIFIER)
		throw this.exception("expected " + expected);
	return next.value;
}

/**
 * Gets the next token from a tokenizer, ensures it is an unquoted string,
 * and converts it to a string.
 * @return The next token in the stream, as a string.
 * @throws TextParseException The input was invalid or not an unquoted string.
 * @throws IOException An I/O error occurred.
 */
public String
getIdentifier() throws IOException {
	return this._getIdentifier("an identifier");
}

/**
 * Gets the next token from a tokenizer and converts it to a long.
 * @return The next token in the stream, as a long.
 * @throws TextParseException The input was invalid or not a long.
 * @throws IOException An I/O error occurred.
 */
public long
getLong() throws IOException {
	String next = this._getIdentifier("an integer");
	if (!Character.isDigit(next.charAt(0)))
		throw this.exception("expected an integer");
	try {
		return Long.parseLong(next);
	} catch (NumberFormatException e) {
		throw this.exception("expected an integer");
	}
}

/**
 * Gets the next token from a tokenizer and converts it to an unsigned 32 bit
 * integer.
 * @return The next token in the stream, as an unsigned 32 bit integer.
 * @throws TextParseException The input was invalid or not an unsigned 32
 * bit integer.
 * @throws IOException An I/O error occurred.
 */
public long
getUInt32() throws IOException {
	long l = this.getLong();
	if (l < 0 || l > 0xFFFFFFFFL)
		throw this.exception("expected an 32 bit unsigned integer");
	return l;
}

/**
 * Gets the next token from a tokenizer and converts it to an unsigned 16 bit
 * integer.
 * @return The next token in the stream, as an unsigned 16 bit integer.
 * @throws TextParseException The input was invalid or not an unsigned 16
 * bit integer.
 * @throws IOException An I/O error occurred.
 */
public int
getUInt16() throws IOException {
	long l = this.getLong();
	if (l < 0 || l > 0xFFFFL)
		throw this.exception("expected an 16 bit unsigned integer");
	return (int) l;
}

/**
 * Gets the next token from a tokenizer and converts it to an unsigned 8 bit
 * integer.
 * @return The next token in the stream, as an unsigned 8 bit integer.
 * @throws TextParseException The input was invalid or not an unsigned 8
 * bit integer.
 * @throws IOException An I/O error occurred.
 */
public int
getUInt8() throws IOException {
	long l = this.getLong();
	if (l < 0 || l > 0xFFL)
		throw this.exception("expected an 8 bit unsigned integer");
	return (int) l;
}

/**
 * Gets the next token from a tokenizer and parses it as a TTL.
 * @return The next token in the stream, as an unsigned 32 bit integer.
 * @throws TextParseException The input was not valid.
 * @throws IOException An I/O error occurred.
 * @see TTL
 */
public long
getTTL() throws IOException {
	String next = this._getIdentifier("a TTL value");
	try {
		return TTL.parseTTL(next);
	}
	catch (NumberFormatException e) {
		throw this.exception("expected a TTL value");
	}
}

/**
 * Gets the next token from a tokenizer and parses it as if it were a TTL.
 * @return The next token in the stream, as an unsigned 32 bit integer.
 * @throws TextParseException The input was not valid.
 * @throws IOException An I/O error occurred.
 * @see TTL
 */
public long
getTTLLike() throws IOException {
	String next = this._getIdentifier("a TTL-like value");
	try {
		return TTL.parse(next, false);
	}
	catch (NumberFormatException e) {
		throw this.exception("expected a TTL-like value");
	}
}

/**
 * Gets the next token from a tokenizer and converts it to a name.
 * @param origin The origin to append to relative names.
 * @return The next token in the stream, as a name.
 * @throws TextParseException The input was invalid or not a valid name.
 * @throws IOException An I/O error occurred.
 * @throws RelativeNameException The parsed name was relative, even with the
 * origin.
 * @see Name
 */
public Name
getName(Name origin) throws IOException {
	String next = this._getIdentifier("a name");
	try {
		Name name = Name.fromString(next, origin);
		if (!name.isAbsolute())
			throw new RelativeNameException(name);
		return name;
	}
	catch (TextParseException e) {
		throw this.exception(e.getMessage());
	}
}

/**
 * Gets the next token from a tokenizer and converts it to an IP Address.
 * @param family The address family.
 * @return The next token in the stream, as an InetAddress
 * @throws TextParseException The input was invalid or not a valid address.
 * @throws IOException An I/O error occurred.
 * @see Address
 */
public InetAddress
getAddress(int family) throws IOException {
	String next = this._getIdentifier("an address");
	try {
		return Address.getByAddress(next, family);
	}
	catch (UnknownHostException e) {
		throw this.exception(e.getMessage());
	}
}

/**
 * Gets the next token from a tokenizer, which must be an EOL or EOF.
 * @throws TextParseException The input was invalid or not an EOL or EOF token.
 * @throws IOException An I/O error occurred.
 */
public void
getEOL() throws IOException {
	Tokenizer.Token next = this.get();
	if (next.type != Tokenizer.EOL && next.type != Tokenizer.EOF) {
		throw this.exception("expected EOL or EOF");
	}
}

/**
 * Returns a concatenation of the remaining strings from a Tokenizer.
 */
private String
remainingStrings() throws IOException {
        StringBuffer buffer = null;
        while (true) {
                Tokenizer.Token t = this.get();
                if (!t.isString())
                        break;
                if (buffer == null)
                        buffer = new StringBuffer();
                buffer.append(t.value);
        }
    this.unget();
        if (buffer == null)
                return null;
        return buffer.toString();
}

/**
 * Gets the remaining string tokens until an EOL/EOF is seen, concatenates
 * them together, and converts the base64 encoded data to a byte array.
 * @param required If true, an exception will be thrown if no strings remain;
 * otherwise null be be returned.
 * @return The byte array containing the decoded strings, or null if there
 * were no strings to decode.
 * @throws TextParseException The input was invalid.
 * @throws IOException An I/O error occurred.
 */
public byte []
getBase64(boolean required) throws IOException {
	String s = this.remainingStrings();
	if (s == null) {
		if (required)
			throw this.exception("expected base64 encoded string");
		else
			return null;
	}
	byte [] array = base64.fromString(s);
	if (array == null)
		throw this.exception("invalid base64 encoding");
	return array;
}

/**
 * Gets the remaining string tokens until an EOL/EOF is seen, concatenates
 * them together, and converts the base64 encoded data to a byte array.
 * @return The byte array containing the decoded strings, or null if there
 * were no strings to decode.
 * @throws TextParseException The input was invalid.
 * @throws IOException An I/O error occurred.
 */
public byte []
getBase64() throws IOException {
	return this.getBase64(false);
}

/**
 * Gets the remaining string tokens until an EOL/EOF is seen, concatenates
 * them together, and converts the hex encoded data to a byte array.
 * @param required If true, an exception will be thrown if no strings remain;
 * otherwise null be be returned.
 * @return The byte array containing the decoded strings, or null if there
 * were no strings to decode.
 * @throws TextParseException The input was invalid.
 * @throws IOException An I/O error occurred.
 */
public byte []
getHex(boolean required) throws IOException {
	String s = this.remainingStrings();
	if (s == null) {
		if (required)
			throw this.exception("expected hex encoded string");
		else
			return null;
	}
	byte [] array = base16.fromString(s);
	if (array == null)
		throw this.exception("invalid hex encoding");
	return array;
}

/**
 * Gets the remaining string tokens until an EOL/EOF is seen, concatenates
 * them together, and converts the hex encoded data to a byte array.
 * @return The byte array containing the decoded strings, or null if there
 * were no strings to decode.
 * @throws TextParseException The input was invalid.
 * @throws IOException An I/O error occurred.
 */
public byte []
getHex() throws IOException {
	return this.getHex(false);
}

/**
 * Gets the next token from a tokenizer and decodes it as hex.
 * @return The byte array containing the decoded string.
 * @throws TextParseException The input was invalid.
 * @throws IOException An I/O error occurred.
 */
public byte []
getHexString() throws IOException {
	String next = this._getIdentifier("a hex string");
	byte [] array = base16.fromString(next);
	if (array == null)
		throw this.exception("invalid hex encoding");
	return array;
}

/**
 * Gets the next token from a tokenizer and decodes it as base32.
 * @param b32 The base32 context to decode with.
 * @return The byte array containing the decoded string.
 * @throws TextParseException The input was invalid.
 * @throws IOException An I/O error occurred.
 */
public byte []
getBase32String(base32 b32) throws IOException {
	String next = this._getIdentifier("a base32 string");
	byte [] array = b32.fromString(next);
	if (array == null)
		throw this.exception("invalid base32 encoding");
	return array;
}

/**
 * Creates an exception which includes the current state in the error message
 * @param s The error message to include.
 * @return The exception to be thrown
 */
public TextParseException
exception(String s) {
	return new Tokenizer.TokenizerException(this.filename, this.line, s);
}

/**
 * Closes any files opened by this tokenizer.
 */
public void
close() {
	if (this.wantClose) {
		try {
            this.is.close();
		}
		catch (IOException e) {
		}
	}
}

@Override
protected void
finalize() {
    this.close();
}

}
