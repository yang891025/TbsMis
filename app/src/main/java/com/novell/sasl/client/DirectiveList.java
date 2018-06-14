/* **************************************************************************
 * $OpenLDAP: /com/novell/sasl/client/DirectiveList.java,v 1.4 2005/01/17 15:00:54 sunilk Exp $
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

import java.util.*;
import org.apache.harmony.javax.security.sasl.*;
import java.io.UnsupportedEncodingException;

/**
 * Implements the DirectiveList class whihc will be used by the 
 * DigestMD5SaslClient class
 */
class DirectiveList extends Object
{
    private static final int STATE_LOOKING_FOR_FIRST_DIRECTIVE  = 1;
    private static final int STATE_LOOKING_FOR_DIRECTIVE        = 2;
    private static final int STATE_SCANNING_NAME                = 3;
    private static final int STATE_LOOKING_FOR_EQUALS            = 4;
    private static final int STATE_LOOKING_FOR_VALUE            = 5;
    private static final int STATE_LOOKING_FOR_COMMA            = 6;
    private static final int STATE_SCANNING_QUOTED_STRING_VALUE    = 7;
    private static final int STATE_SCANNING_TOKEN_VALUE            = 8;
    private static final int STATE_NO_UTF8_SUPPORT              = 9;

    private int        m_curPos;
    private int        m_errorPos;
    private String     m_directives;
    private int        m_state;
    private final ArrayList  m_directiveList;
    private String     m_curName;
    private int        m_scanStart;

    /**
     *  Constructs a new DirectiveList.
     */
     DirectiveList(
        byte[] directives)
    {
        this.m_curPos = 0;
        this.m_state = DirectiveList.STATE_LOOKING_FOR_FIRST_DIRECTIVE;
        this.m_directiveList = new ArrayList(10);
        this.m_scanStart = 0;
        this.m_errorPos = -1;
        try
        {
            this.m_directives = new String(directives, "UTF-8");
        }
        catch(UnsupportedEncodingException e)
        {
            this.m_state = DirectiveList.STATE_NO_UTF8_SUPPORT;
        }
    }

    /**
     * This function takes a US-ASCII character string containing a list of comma
     * separated directives, and parses the string into the individual directives
     * and their values. A directive consists of a token specifying the directive
     * name followed by an equal sign (=) and the directive value. The value is
     * either a token or a quoted string
     *
     * @exception SaslException  If an error Occurs
     */
    void parseDirectives() throws SaslException
    {
        char        prevChar;
        char        currChar;
        int            rc = 0;
        boolean        haveQuotedPair = false;
        String      currentName = "<no name>";

        if (this.m_state == DirectiveList.STATE_NO_UTF8_SUPPORT)
            throw new SaslException("No UTF-8 support on platform");

        prevChar = 0;

        while (this.m_curPos < this.m_directives.length())
        {
            currChar = this.m_directives.charAt(this.m_curPos);
            switch (this.m_state)
            {
            case DirectiveList.STATE_LOOKING_FOR_FIRST_DIRECTIVE:
            case DirectiveList.STATE_LOOKING_FOR_DIRECTIVE:
                if (this.isWhiteSpace(currChar))
                {
                    break;
                }
                else if (this.isValidTokenChar(currChar))
                {
                    this.m_scanStart = this.m_curPos;
                    this.m_state = DirectiveList.STATE_SCANNING_NAME;
                }
                else
                {
                    this.m_errorPos = this.m_curPos;
                    throw new SaslException("Parse error: Invalid name character");
                }
                break;

            case DirectiveList.STATE_SCANNING_NAME:
                if (this.isValidTokenChar(currChar))
                {
                    break;
                }
                else if (this.isWhiteSpace(currChar))
                {
                    currentName = this.m_directives.substring(this.m_scanStart, this.m_curPos);
                    this.m_state = DirectiveList.STATE_LOOKING_FOR_EQUALS;
                }
                else if ('=' == currChar)
                {
                    currentName = this.m_directives.substring(this.m_scanStart, this.m_curPos);
                    this.m_state = DirectiveList.STATE_LOOKING_FOR_VALUE;
                }
                else
                {
                    this.m_errorPos = this.m_curPos;
                    throw new SaslException("Parse error: Invalid name character");
                }
                break;

            case DirectiveList.STATE_LOOKING_FOR_EQUALS:
                if (this.isWhiteSpace(currChar))
                {
                    break;
                }
                else if ('=' == currChar)
                {
                    this.m_state = DirectiveList.STATE_LOOKING_FOR_VALUE;
                }
                else
                {
                    this.m_errorPos = this.m_curPos;
                    throw new SaslException("Parse error: Expected equals sign '='.");
                }
                break;

            case DirectiveList.STATE_LOOKING_FOR_VALUE:
                if (this.isWhiteSpace(currChar))
                {
                    break;
                }
                else if ('"' == currChar)
                {
                    this.m_scanStart = this.m_curPos +1; /* don't include the quote */
                    this.m_state = DirectiveList.STATE_SCANNING_QUOTED_STRING_VALUE;
                }
                else if (this.isValidTokenChar(currChar))
                {
                    this.m_scanStart = this.m_curPos;
                    this.m_state = DirectiveList.STATE_SCANNING_TOKEN_VALUE;
                }
                else
                {
                    this.m_errorPos = this.m_curPos;
                    throw new SaslException("Parse error: Unexpected character");
                }
                break;

            case DirectiveList.STATE_SCANNING_TOKEN_VALUE:
                if (this.isValidTokenChar(currChar))
                {
                    break;
                }
                else if (this.isWhiteSpace(currChar))
                {
                    this.addDirective(currentName, false);
                    this.m_state = DirectiveList.STATE_LOOKING_FOR_COMMA;
                }
                else if (',' == currChar)
                {
                    this.addDirective(currentName, false);
                    this.m_state = DirectiveList.STATE_LOOKING_FOR_DIRECTIVE;
                }
                else
                {
                    this.m_errorPos = this.m_curPos;
                    throw new SaslException("Parse error: Invalid value character");
                }
                break;

            case DirectiveList.STATE_SCANNING_QUOTED_STRING_VALUE:
                if ('\\' == currChar)
                    haveQuotedPair = true;
                if ( '"' == currChar &&
                        '\\' != prevChar)
                {
                    this.addDirective(currentName, haveQuotedPair);
                    haveQuotedPair = false;
                    this.m_state = DirectiveList.STATE_LOOKING_FOR_COMMA;
                }
                break;

            case DirectiveList.STATE_LOOKING_FOR_COMMA:
                if (this.isWhiteSpace(currChar))
                    break;
                else if (currChar == ',')
                    this.m_state = DirectiveList.STATE_LOOKING_FOR_DIRECTIVE;
                else
                {
                    this.m_errorPos = this.m_curPos;
                    throw new SaslException("Parse error: Expected a comma.");
                }
                break;
            }
            if (0 != rc)
                break;
            prevChar = currChar;
            this.m_curPos++;
        } /* end while loop */


        if (rc == 0)
        {
            /* check the ending state */
            switch (this.m_state)
            {
            case DirectiveList.STATE_SCANNING_TOKEN_VALUE:
                this.addDirective(currentName, false);
                break;

            case DirectiveList.STATE_LOOKING_FOR_FIRST_DIRECTIVE:
            case DirectiveList.STATE_LOOKING_FOR_COMMA:
                break;

            case DirectiveList.STATE_LOOKING_FOR_DIRECTIVE:
                    throw new SaslException("Parse error: Trailing comma.");

            case DirectiveList.STATE_SCANNING_NAME:
            case DirectiveList.STATE_LOOKING_FOR_EQUALS:
            case DirectiveList.STATE_LOOKING_FOR_VALUE:
                    throw new SaslException("Parse error: Missing value.");

            case DirectiveList.STATE_SCANNING_QUOTED_STRING_VALUE:
                    throw new SaslException("Parse error: Missing closing quote.");
            }
        }

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
     * @param c  character to be tested
     *
     * @return Returns TRUE if the character is a valid token character.
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
     * @param c  Input charcter to be tested
     *
     * @return Returns TRUE if the character is linear white space (LWS)
     */
    boolean isWhiteSpace(
        char c)
    {
        return '\t' == c ||  // HORIZONTAL TABULATION.
                '\n' == c ||  // LINE FEED.
                '\r' == c ||  // CARRIAGE RETURN.
                '\u0020' == c;

    }

    /**
     * This function creates a directive record and adds it to the list, the
     * value will be added later after it is parsed.
     *
     * @param name  Name
     * @param haveQuotedPair true if quoted pair is there else false
     */
    void addDirective(
        String    name,
        boolean   haveQuotedPair)
    {
        String value;
        int    inputIndex;
        int    valueIndex;
        char   valueChar;
        int    type;

        if (!haveQuotedPair)
        {
            value = this.m_directives.substring(this.m_scanStart, this.m_curPos);
        }
        else
        { //copy one character at a time skipping backslash excapes.
            StringBuffer valueBuf = new StringBuffer(this.m_curPos - this.m_scanStart);
            valueIndex = 0;
            inputIndex = this.m_scanStart;
            while (inputIndex < this.m_curPos)
            {
                if ('\\' == (valueChar = this.m_directives.charAt(inputIndex)))
                    inputIndex++;
                valueBuf.setCharAt(valueIndex, this.m_directives.charAt(inputIndex));
                valueIndex++;
                inputIndex++;
            }
            value = new String(valueBuf);
        }

        if (this.m_state == DirectiveList.STATE_SCANNING_QUOTED_STRING_VALUE)
            type = ParsedDirective.QUOTED_STRING_VALUE;
        else
            type = ParsedDirective.TOKEN_VALUE;
        this.m_directiveList.add(new ParsedDirective(name, value, type));
    }


    /**
     * Returns the List iterator.
     *
     * @return     Returns the Iterator Object for the List.
     */
    Iterator getIterator()
    {
        return this.m_directiveList.iterator();
    }
}

