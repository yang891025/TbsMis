/* **************************************************************************
 * $OpenLDAP: /com/novell/sasl/client/ParsedDirective.java,v 1.1 2003/08/21 10:06:26 kkanil Exp $
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

/**
 * Implements the ParsedDirective class which will be used in the
 * DigestMD5SaslClient mechanism.
 */
class ParsedDirective
{
    public static final int  QUOTED_STRING_VALUE = 1;
    public static final int  TOKEN_VALUE         = 2;

    private final int     m_valueType;
    private final String  m_name;
    private final String  m_value;

    ParsedDirective(
        String  name,
        String  value,
        int     type)
    {
        this.m_name = name;
        this.m_value = value;
        this.m_valueType = type;
    }

    String getValue()
    {
        return this.m_value;
    }

    String getName()
    {
        return this.m_name;
    }

    int getValueType()
    {
        return this.m_valueType;
    }

}

