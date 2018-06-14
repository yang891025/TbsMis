// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS.utils;

import java.util.Arrays;
import java.security.*;

/**
 * An implementation of the HMAC message authentication code.
 *
 * @author Brian Wellington
 */

public class HMAC {

MessageDigest digest;
private byte [] ipad, opad;

private static final byte IPAD = 0x36;
private static final byte OPAD = 0x5c;
private static final byte PADLEN = 64;

private void
init(byte [] key) {
	int i;

	if (key.length > HMAC.PADLEN) {
		key = this.digest.digest(key);
        this.digest.reset();
	}
    this.ipad = new byte[HMAC.PADLEN];
    this.opad = new byte[HMAC.PADLEN];
	for (i = 0; i < key.length; i++) {
        this.ipad[i] = (byte) (key[i] ^ HMAC.IPAD);
        this.opad[i] = (byte) (key[i] ^ HMAC.OPAD);
	}
	for (; i < HMAC.PADLEN; i++) {
        this.ipad[i] = HMAC.IPAD;
        this.opad[i] = HMAC.OPAD;
	}
    this.digest.update(this.ipad);
}

/**
 * Creates a new HMAC instance
 * @param digest The message digest object.
 * @param key The secret key
 */
public
HMAC(MessageDigest digest, byte [] key) {
	digest.reset();
	this.digest = digest;
    this.init(key);
}

/**
 * Creates a new HMAC instance
 * @param digestName The name of the message digest function.
 * @param key The secret key.
 */
public
HMAC(String digestName, byte [] key) {
	try {
        this.digest = MessageDigest.getInstance(digestName);
	} catch (NoSuchAlgorithmException e) {
		throw new IllegalArgumentException("unknown digest algorithm "
						   + digestName);
	}
    this.init(key);
}

/**
 * Adds data to the current hash
 * @param b The data
 * @param offset The index at which to start adding to the hash
 * @param length The number of bytes to hash
 */
public void
update(byte [] b, int offset, int length) {
    this.digest.update(b, offset, length);
}

/**
 * Adds data to the current hash
 * @param b The data
 */
public void
update(byte [] b) {
    this.digest.update(b);
}

/**
 * Signs the data (computes the secure hash)
 * @return An array with the signature
 */
public byte []
sign() {
	byte [] output = this.digest.digest();
    this.digest.reset();
    this.digest.update(this.opad);
	return this.digest.digest(output);
}

/**
 * Verifies the data (computes the secure hash and compares it to the input)
 * @param signature The signature to compare against
 * @return true if the signature matched, false otherwise
 */
public boolean
verify(byte [] signature) {
	return Arrays.equals(signature, this.sign());
}

/**
 * Resets the HMAC object for further use
 */
public void
clear() {
    this.digest.reset();
    this.digest.update(this.ipad);
}

}
