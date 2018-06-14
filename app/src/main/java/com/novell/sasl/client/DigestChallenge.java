/* **************************************************************************
 * $OpenLDAP: /com/novell/sasl/client/DigestChallenge.java,v 1.3 2005/01/17 15:00:54 sunilk Exp $
 *
 * Copyright (C) 2003 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/
package com.novell.sasl.client;

import java.util.*;
import org.apache.harmony.javax.security.sasl.*;

/**
 * Implements the DigestChallenge class which will be used by the
 * DigestMD5SaslClient class
 */
class DigestChallenge extends Object {
	public static final int QOP_AUTH = 0x01;
	public static final int QOP_AUTH_INT = 0x02;
	public static final int QOP_AUTH_CONF = 0x04;
	public static final int QOP_UNRECOGNIZED = 0x08;

	private static final int CIPHER_3DES = 0x01;
	private static final int CIPHER_DES = 0x02;
	private static final int CIPHER_RC4_40 = 0x04;
	private static final int CIPHER_RC4 = 0x08;
	private static final int CIPHER_RC4_56 = 0x10;
	private static final int CIPHER_UNRECOGNIZED = 0x20;
	private static final int CIPHER_RECOGNIZED_MASK = DigestChallenge.CIPHER_3DES | DigestChallenge.CIPHER_DES
			| DigestChallenge.CIPHER_RC4_40 | DigestChallenge.CIPHER_RC4 | DigestChallenge.CIPHER_RC4_56;

	@SuppressWarnings("rawtypes")
	private final ArrayList m_realms;
	private String m_nonce;
	private int m_qop;
	private boolean m_staleFlag;
	private int m_maxBuf;
	private String m_characterSet;
	private String m_algorithm;
	private int m_cipherOptions;

	@SuppressWarnings("rawtypes")
	DigestChallenge(byte[] challenge) throws SaslException {
        this.m_realms = new ArrayList(5);
        this.m_nonce = null;
        this.m_qop = 0;
        this.m_staleFlag = false;
        this.m_maxBuf = -1;
        this.m_characterSet = null;
        this.m_algorithm = null;
        this.m_cipherOptions = 0;

		DirectiveList dirList = new DirectiveList(challenge);
		try {
			dirList.parseDirectives();
            this.checkSemantics(dirList);
		} catch (SaslException e) {
		}
	}

	/**
	 * Checks the semantics of the directives in the directive list as parsed
	 * from the digest challenge byte array.
	 * 
	 * @param dirList
	 *            the list of directives parsed from the digest challenge
	 * 
	 * @exception SaslException
	 *                If a semantic error occurs
	 */
	void checkSemantics(DirectiveList dirList) throws SaslException {
		Iterator directives = dirList.getIterator();
		ParsedDirective directive;
		String name;

		while (directives.hasNext()) {
			directive = (ParsedDirective) directives.next();
			name = directive.getName();
			if (name.equals("realm"))
                this.handleRealm(directive);
			else if (name.equals("nonce"))
                this.handleNonce(directive);
			else if (name.equals("qop"))
                this.handleQop(directive);
			else if (name.equals("maxbuf"))
                this.handleMaxbuf(directive);
			else if (name.equals("charset"))
                this.handleCharset(directive);
			else if (name.equals("algorithm"))
                this.handleAlgorithm(directive);
			else if (name.equals("cipher"))
                this.handleCipher(directive);
			else if (name.equals("stale"))
                this.handleStale(directive);
		}

		/* post semantic check */
		if (-1 == this.m_maxBuf)
            this.m_maxBuf = 65536;

		if (this.m_qop == 0)
            this.m_qop = DigestChallenge.QOP_AUTH;
		else if ((this.m_qop & DigestChallenge.QOP_AUTH) != DigestChallenge.QOP_AUTH)
			throw new SaslException("Only qop-auth is supported by client");
		else if ((this.m_qop & DigestChallenge.QOP_AUTH_CONF) == DigestChallenge.QOP_AUTH_CONF
				&& 0 == (this.m_cipherOptions & DigestChallenge.CIPHER_RECOGNIZED_MASK))
			throw new SaslException("Invalid cipher options");
		else if (null == this.m_nonce)
			throw new SaslException("Missing nonce directive");
		else if (this.m_staleFlag)
			throw new SaslException("Unexpected stale flag");
		else if (null == this.m_algorithm)
			throw new SaslException("Missing algorithm directive");
	}

	/**
	 * This function implements the semenatics of the nonce directive.
	 * 
	 * @param pd
	 *            ParsedDirective
	 * 
	 * @exception SaslException
	 *                If an error occurs due to too many nonce values
	 */
	void handleNonce(ParsedDirective pd) throws SaslException {
		if (null != this.m_nonce)
			throw new SaslException("Too many nonce values.");

        this.m_nonce = pd.getValue();
	}

	/**
	 * This function implements the semenatics of the realm directive.
	 * 
	 * @param pd
	 *            ParsedDirective
	 */
	void handleRealm(ParsedDirective pd) {
        this.m_realms.add(pd.getValue());
	}

/**
     * This function implements the semenatics of the qop (quality of protection)
     * directive. The value of the qop directive is as defined below:
     *      qop-options =     "qop" "=" <"> qop-list <">
     *      qop-list    =     1#qop-value
     *      qop-value    =     "auth" | "auth-int"  | "auth-conf" | token
     *
     * @param      pd   ParsedDirective
     *
     * @exception  SaslException   If an error occurs due to too many qop
     *                             directives
     */
	void handleQop(ParsedDirective pd) throws SaslException {
		String token;
		TokenParser parser;

		if (this.m_qop != 0)
			throw new SaslException("Too many qop directives.");

		parser = new TokenParser(pd.getValue());
		for (token = parser.parseToken(); token != null; token = parser
				.parseToken()) {
			if (token.equals("auth"))
                this.m_qop |= DigestChallenge.QOP_AUTH;
			else if (token.equals("auth-int"))
                this.m_qop |= DigestChallenge.QOP_AUTH_INT;
			else if (token.equals("auth-conf"))
                this.m_qop |= DigestChallenge.QOP_AUTH_CONF;
			else
                this.m_qop |= DigestChallenge.QOP_UNRECOGNIZED;
		}
	}

	/**
	 * This function implements the semenatics of the Maxbuf directive. the
	 * value is defined as: 1*DIGIT
	 * 
	 * @param pd
	 *            ParsedDirective
	 * 
	 * @exception SaslException
	 *                If an error occur
	 */
	void handleMaxbuf(ParsedDirective pd) throws SaslException {
		if (-1 != this.m_maxBuf) /* it's initialized to -1 */
			throw new SaslException("Too many maxBuf directives.");

        this.m_maxBuf = Integer.parseInt(pd.getValue());

		if (0 == this.m_maxBuf)
			throw new SaslException("Max buf value must be greater than zero.");
	}

	/**
	 * This function implements the semenatics of the charset directive. the
	 * value is defined as: 1*DIGIT
	 * 
	 * @param pd
	 *            ParsedDirective
	 * 
	 * @exception SaslException
	 *                If an error occurs dur to too many charset directives or
	 *                Invalid character encoding directive
	 */
	void handleCharset(ParsedDirective pd) throws SaslException {
		if (null != this.m_characterSet)
			throw new SaslException("Too many charset directives.");

        this.m_characterSet = pd.getValue();

		if (!this.m_characterSet.equals("utf-8"))
			throw new SaslException("Invalid character encoding directive");
	}

	/**
	 * This function implements the semenatics of the charset directive. the
	 * value is defined as: 1*DIGIT
	 * 
	 * @param pd
	 *            ParsedDirective
	 * 
	 * @exception SaslException
	 *                If an error occurs due to too many algorith directive or
	 *                Invalid algorithm directive value
	 */
	void handleAlgorithm(ParsedDirective pd) throws SaslException {
		if (null != this.m_algorithm)
			throw new SaslException("Too many algorithm directives.");

        this.m_algorithm = pd.getValue();

		if (!"md5-sess".equals(this.m_algorithm))
			throw new SaslException("Invalid algorithm directive value: "
					+ this.m_algorithm);
	}

/**
     * This function implements the semenatics of the cipher-opts directive
     * directive. The value of the qop directive is as defined below:
     *      qop-options =     "qop" "=" <"> qop-list <">
     *      qop-list    =     1#qop-value
     *      qop-value    =     "auth" | "auth-int"  | "auth-conf" | token
     *
     * @param      pd   ParsedDirective
     *
     * @exception  SaslException If an error occurs due to Too many cipher
     *                           directives 
     */
	void handleCipher(ParsedDirective pd) throws SaslException {
		String token;
		TokenParser parser;

		if (0 != this.m_cipherOptions)
			throw new SaslException("Too many cipher directives.");

		parser = new TokenParser(pd.getValue());
		token = parser.parseToken();
		for (token = parser.parseToken(); token != null; token = parser
				.parseToken()) {
			if ("3des".equals(token))
                this.m_cipherOptions |= DigestChallenge.CIPHER_3DES;
			else if ("des".equals(token))
                this.m_cipherOptions |= DigestChallenge.CIPHER_DES;
			else if ("rc4-40".equals(token))
                this.m_cipherOptions |= DigestChallenge.CIPHER_RC4_40;
			else if ("rc4".equals(token))
                this.m_cipherOptions |= DigestChallenge.CIPHER_RC4;
			else if ("rc4-56".equals(token))
                this.m_cipherOptions |= DigestChallenge.CIPHER_RC4_56;
			else
                this.m_cipherOptions |= DigestChallenge.CIPHER_UNRECOGNIZED;
		}

		if (this.m_cipherOptions == 0)
            this.m_cipherOptions = DigestChallenge.CIPHER_UNRECOGNIZED;
	}

	/**
	 * This function implements the semenatics of the stale directive.
	 * 
	 * @param pd
	 *            ParsedDirective
	 * 
	 * @exception SaslException
	 *                If an error occurs due to Too many stale directives or
	 *                Invalid stale directive value
	 */
	void handleStale(ParsedDirective pd) throws SaslException {
		if (false != this.m_staleFlag)
			throw new SaslException("Too many stale directives.");

		if ("true".equals(pd.getValue()))
            this.m_staleFlag = true;
		else
			throw new SaslException("Invalid stale directive value: "
					+ pd.getValue());
	}

	/**
	 * Return the list of the All the Realms
	 * 
	 * @return List of all the realms
	 */
	public ArrayList getRealms() {
		return this.m_realms;
	}

	/**
	 * @return Returns the Nonce
	 */
	public String getNonce() {
		return this.m_nonce;
	}

	/**
	 * Return the quality-of-protection
	 * 
	 * @return The quality-of-protection
	 */
	public int getQop() {
		return this.m_qop;
	}

	/**
	 * @return The state of the Staleflag
	 */
	public boolean getStaleFlag() {
		return this.m_staleFlag;
	}

	/**
	 * @return The Maximum Buffer value
	 */
	public int getMaxBuf() {
		return this.m_maxBuf;
	}

	/**
	 * @return character set values as string
	 */
	public String getCharacterSet() {
		return this.m_characterSet;
	}

	/**
	 * @return The String value of the algorithm
	 */
	public String getAlgorithm() {
		return this.m_algorithm;
	}

	/**
	 * @return The cipher options
	 */
	public int getCipherOptions() {
		return this.m_cipherOptions;
	}
}
