/* **************************************************************************
 * $OpenLDAP: /com/novell/sasl/client/TokenParser.java,v 1.3 2005/01/17 15:00:54 sunilk Exp $
 *
 * Copyright (C) 2002 Novell, Inc. All Rights Reserved.
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

import org.apache.harmony.javax.security.sasl.*;
/**
 * The TokenParser class will parse individual tokens from a list of tokens that
 * are a directive value for a DigestMD5 authentication.The tokens are separated
 * commas.
 */
class TokenParser extends Object
{
    private static final int STATE_LOOKING_FOR_FIRST_TOKEN = 1;
    private static final int STATE_LOOKING_FOR_TOKEN       = 2;
    private static final int STATE_SCANNING_TOKEN          = 3;
    private static final int STATE_LOOKING_FOR_COMMA       = 4;
    private static final int STATE_PARSING_ERROR           = 5;
    private static final int STATE_DONE                    = 6;

    private int        m_curPos;
    private int     m_scanStart;
    private int     m_state;
    private final String  m_tokens;


    TokenParser(
        String tokens)
    {
        this.m_tokens = tokens;
        this.m_curPos = 0;
        this.m_scanStart = 0;
        this.m_state = TokenParser.STATE_LOOKING_FOR_FIRST_TOKEN;
    }

    /**
     * This function parses the next token from the tokens string and returns
     * it as a string. If there are no more tokens a null reference is returned.
     *
     * @return  the parsed token or a null reference if there are no more
     * tokens
     *
     * @exception  SASLException if an error occurs while parsing
     */
    String parseToken() throws SaslException
    {
        char    currChar;
        String  token = null;


        if (this.m_state == TokenParser.STATE_DONE)
            return null;

        while (this.m_curPos < this.m_tokens.length() && token == null)
        {
            currChar = this.m_tokens.charAt(this.m_curPos);
            switch (this.m_state)
            {
            case TokenParser.STATE_LOOKING_FOR_FIRST_TOKEN:
            case TokenParser.STATE_LOOKING_FOR_TOKEN:
                if (this.isWhiteSpace(currChar))
                {
                    break;
                }
                else if (this.isValidTokenChar(currChar))
                {
                    this.m_scanStart = this.m_curPos;
                    this.m_state = TokenParser.STATE_SCANNING_TOKEN;
                }
                else
                {
                    this.m_state = TokenParser.STATE_PARSING_ERROR;
                    throw new SaslException("Invalid token character at position " + this.m_curPos);
                }
                break;

            case TokenParser.STATE_SCANNING_TOKEN:
                if (this.isValidTokenChar(currChar))
                {
                    break;
                }
                else if (this.isWhiteSpace(currChar))
                {
                    token = this.m_tokens.substring(this.m_scanStart, this.m_curPos);
                    this.m_state = TokenParser.STATE_LOOKING_FOR_COMMA;
                }
                else if (',' == currChar)
                {
                    token = this.m_tokens.substring(this.m_scanStart, this.m_curPos);
                    this.m_state = TokenParser.STATE_LOOKING_FOR_TOKEN;
                }
                else
                {
                    this.m_state = TokenParser.STATE_PARSING_ERROR;
                    throw new SaslException("Invalid token character at position " + this.m_curPos);
                }
                break;


            case TokenParser.STATE_LOOKING_FOR_COMMA:
                if (this.isWhiteSpace(currChar))
                    break;
                else if (currChar == ',')
                    this.m_state = TokenParser.STATE_LOOKING_FOR_TOKEN;
                else
                {
                    this.m_state = TokenParser.STATE_PARSING_ERROR;
                    throw new SaslException("Expected a comma, found '" +
                                            currChar + "' at postion " +
                            this.m_curPos);
                }
                break;
            }
            this.m_curPos++;
        } /* end while loop */

        if (token == null)
        {    /* check the ending state */
            switch (this.m_state)
            {
            case TokenParser.STATE_SCANNING_TOKEN:
                token = this.m_tokens.substring(this.m_scanStart);
                this.m_state = TokenParser.STATE_DONE;
                break;

            case TokenParser.STATE_LOOKING_FOR_FIRST_TOKEN:
            case TokenParser.STATE_LOOKING_FOR_COMMA:
                break;

            case TokenParser.STATE_LOOKING_FOR_TOKEN:
                throw new SaslException("Trialing comma");
            }
        }

        return token;
    }

    /**
     * This function returns TRUE if the character is a valid token character.
     *
     *     token          = 1*<any CHAR except CTLs or separators>
     *
     *      separators     = "(" | ")" | "<" | ">" | "@"
     *                     | "," | ";" | ":" | "\" | <">
     *                     | "/" | "[" | "]" | "?" | "="
     *                     | "{" | "}" | SP | HT
     *
     *      CTL            = <any US-ASCII control character
     *                       (octets 0 - 31) and DEL (127)>
     *
     *      CHAR           = <any US-ASCII character (octets 0 - 127)>
     *
     * @param c  character to be validated
     *
     * @return True if character is valid Token character else it returns 
     * false
     */
    boolean isValidTokenChar(
        char c)
    {
        return !(c >= '\u0000' && c <= '\u0020' ||
                c >= '\u003a' && c <= '\u0040' ||
                c >= '\u005b' && c <= '\u005d' ||
                '\u002c' == c ||
                '\u0025' == c ||
                '\u0028' == c ||
                '\u0029' == c ||
                '\u007b' == c ||
                '\u007d' == c ||
                '\u007f' == c);

    }

    /**
     * This function returns TRUE if the character is linear white space (LWS).
     *         LWS = [CRLF] 1*( SP | HT )
     *
     * @param c  character to be validated
     *
     * @return True if character is liner whitespace else it returns false
     */
    boolean isWhiteSpace(
        char c)
    {
        return '\t' == c || // HORIZONTAL TABULATION.
                '\n' == c || // LINE FEED.
                '\r' == c || // CARRIAGE RETURN.
                '\u0020' == c;

    }

}

