/* **************************************************************************
 * $OpenLDAP: /com/novell/sasl/client/DigestMD5SaslClient.java,v 1.4 2005/01/17 15:00:54 sunilk Exp $
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

import org.apache.harmony.javax.security.sasl.*;
import org.apache.harmony.javax.security.auth.callback.*;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.util.*;

/**
 * Implements the Client portion of DigestMD5 Sasl mechanism.
 */
public class DigestMD5SaslClient implements SaslClient
{
    private String           m_authorizationId = "";
    private String           m_protocol = "";
    private String           m_serverName = "";
    private final Map              m_props;
    private final CallbackHandler  m_cbh;
    private int              m_state;
    private String           m_qopValue = "";
    private char[]              m_HA1;
    private String           m_digestURI;
    private DigestChallenge  m_dc;
    private String           m_clientNonce = "";
    private String           m_realm = "";
    private String           m_name = "";

    private static final int   STATE_INITIAL = 0;
    private static final int   STATE_DIGEST_RESPONSE_SENT = 1;
    private static final int   STATE_VALID_SERVER_RESPONSE = 2;
    private static final int   STATE_INVALID_SERVER_RESPONSE = 3;
    private static final int   STATE_DISPOSED = 4;

    private static final int   NONCE_BYTE_COUNT = 32;
    private static final int   NONCE_HEX_COUNT = 2* DigestMD5SaslClient.NONCE_BYTE_COUNT;

    private static final String DIGEST_METHOD = "AUTHENTICATE";

    /**
     * Creates an DigestMD5SaslClient object using the parameters supplied.
     * Assumes that the QOP, STRENGTH, and SERVER_AUTH properties are
     * contained in props
     *
     * @param authorizationId  The possibly null protocol-dependent
     *                     identification to be used for authorization. If
     *                     null or empty, the server derives an authorization
     *                     ID from the client's authentication credentials.
     *                     When the SASL authentication completes
     *                     successfully, the specified entity is granted
     *                     access.
     *
     * @param protocol     The non-null string name of the protocol for which
     *                     the authentication is being performed (e.g. "ldap")
     *
     * @param serverName   The non-null fully qualified host name of the server
     *                     to authenticate to
     *
     * @param props        The possibly null set of properties used to select
     *                     the SASL mechanism and to configure the
     *                     authentication exchange of the selected mechanism.
     *                     See the Sasl class for a list of standard properties.
     *                     Other, possibly mechanism-specific, properties can
     *                     be included. Properties not relevant to the selected
     *                     mechanism are ignored.
     *
     * @param cbh          The possibly null callback handler to used by the
     *                     SASL mechanisms to get further information from the
     *                     application/library to complete the authentication.
     *                     For example, a SASL mechanism might require the
     *                     authentication ID, password and realm from the
     *                     caller. The authentication ID is requested by using
     *                     a NameCallback. The password is requested by using
     *                     a PasswordCallback. The realm is requested by using
     *                     a RealmChoiceCallback if there is a list of realms
     *                     to choose from, and by using a RealmCallback if the
     *                     realm must be entered.
     *
     * @return            A possibly null SaslClient created using the
     *                     parameters supplied. If null, this factory cannot
     *                     produce a SaslClient using the parameters supplied.
     *
     * @exception SaslException  If a SaslClient instance cannot be created
     *                     because of an error
     */
    public static SaslClient getClient(
        String          authorizationId,
        String          protocol,
        String          serverName,
        Map             props,
        CallbackHandler cbh)
    {
        String desiredQOP = (String)props.get(Sasl.QOP);
        String desiredStrength = (String)props.get(Sasl.STRENGTH);
        String serverAuth = (String)props.get(Sasl.SERVER_AUTH);

        //only support qop equal to auth
        if (desiredQOP != null && !"auth".equals(desiredQOP))
            return null;

        //doesn't support server authentication
        if (serverAuth != null && !"false".equals(serverAuth))
            return null;

        //need a callback handler to get the password
        if (cbh == null)
            return null;

        return new DigestMD5SaslClient(authorizationId, protocol,
                                       serverName, props, cbh);
    }

    /**
     * Creates an DigestMD5SaslClient object using the parameters supplied.
     * Assumes that the QOP, STRENGTH, and SERVER_AUTH properties are
     * contained in props
     *
     * @param authorizationId  The possibly null protocol-dependent
     *                     identification to be used for authorization. If
     *                     null or empty, the server derives an authorization
     *                     ID from the client's authentication credentials.
     *                     When the SASL authentication completes
     *                     successfully, the specified entity is granted
     *                     access.
     *
     * @param protocol     The non-null string name of the protocol for which
     *                     the authentication is being performed (e.g. "ldap")
     *
     * @param serverName   The non-null fully qualified host name of the server
     *                     to authenticate to
     *
     * @param props        The possibly null set of properties used to select
     *                     the SASL mechanism and to configure the
     *                     authentication exchange of the selected mechanism.
     *                     See the Sasl class for a list of standard properties.
     *                     Other, possibly mechanism-specific, properties can
     *                     be included. Properties not relevant to the selected
     *                     mechanism are ignored.
     *
     * @param cbh          The possibly null callback handler to used by the
     *                     SASL mechanisms to get further information from the
     *                     application/library to complete the authentication.
     *                     For example, a SASL mechanism might require the
     *                     authentication ID, password and realm from the
     *                     caller. The authentication ID is requested by using
     *                     a NameCallback. The password is requested by using
     *                     a PasswordCallback. The realm is requested by using
     *                     a RealmChoiceCallback if there is a list of realms
     *                     to choose from, and by using a RealmCallback if the
     *                     realm must be entered.
     *
     */
    private  DigestMD5SaslClient(
        String          authorizationId,
        String          protocol,
        String          serverName,
        Map             props,
        CallbackHandler cbh)
    {
        this.m_authorizationId = authorizationId;
        this.m_protocol = protocol;
        this.m_serverName = serverName;
        this.m_props = props;
        this.m_cbh = cbh;

        this.m_state = DigestMD5SaslClient.STATE_INITIAL;
    }

    /**
     * Determines if this mechanism has an optional initial response. If true,
     * caller should call evaluateChallenge() with an empty array to get the
     * initial response.
     *
     * @return  true if this mechanism has an initial response
     */
    @Override
	public boolean hasInitialResponse()
    {
        return false;
    }

    /**
     * Determines if the authentication exchange has completed. This method
     * may be called at any time, but typically, it will not be called until
     * the caller has received indication from the server (in a protocol-
     * specific manner) that the exchange has completed.
     *
     * @return  true if the authentication exchange has completed;
     *           false otherwise.
     */
    @Override
	public boolean isComplete()
    {
        return this.m_state == DigestMD5SaslClient.STATE_VALID_SERVER_RESPONSE ||
                this.m_state == DigestMD5SaslClient.STATE_INVALID_SERVER_RESPONSE ||
                this.m_state == DigestMD5SaslClient.STATE_DISPOSED;
    }

    /**
     * Unwraps a byte array received from the server. This method can be called
     * only after the authentication exchange has completed (i.e., when
     * isComplete() returns true) and only if the authentication exchange has
     * negotiated integrity and/or privacy as the quality of protection;
     * otherwise, an IllegalStateException is thrown.
     *
     * incoming is the contents of the SASL buffer as defined in RFC 2222
     * without the leading four octet field that represents the length.
     * offset and len specify the portion of incoming to use.
     *
     * @param incoming   A non-null byte array containing the encoded bytes
     *                   from the server
     * @param offset     The starting position at incoming of the bytes to use
     *
     * @param len        The number of bytes from incoming to use
     *
     * @return           A non-null byte array containing the decoded bytes
     *
     */
    @Override
	public byte[] unwrap(
        byte[] incoming,
        int    offset,
        int    len)
            throws SaslException
    {
        throw new IllegalStateException(
         "unwrap: QOP has neither integrity nor privacy>");
    }

    /**
     * Wraps a byte array to be sent to the server. This method can be called
     * only after the authentication exchange has completed (i.e., when
     * isComplete() returns true) and only if the authentication exchange has
     * negotiated integrity and/or privacy as the quality of protection;
     * otherwise, an IllegalStateException is thrown.
     *
     * The result of this method will make up the contents of the SASL buffer as
     * defined in RFC 2222 without the leading four octet field that represents
     * the length. offset and len specify the portion of outgoing to use.
     *
     * @param outgoing   A non-null byte array containing the bytes to encode
     * @param offset     The starting position at outgoing of the bytes to use
     * @param len        The number of bytes from outgoing to use
     *
     * @return A non-null byte array containing the encoded bytes
     *
     * @exception SaslException  if incoming cannot be successfully unwrapped.
     *
     * @exception IllegalStateException   if the authentication exchange has
     *                   not completed, or if the negotiated quality of
     *                   protection has neither integrity nor privacy.
     */
    @Override
	public byte[] wrap(
        byte[]  outgoing,
        int     offset,
        int     len)
            throws SaslException
    {
        throw new IllegalStateException(
         "wrap: QOP has neither integrity nor privacy>");
    }

    /**
     * Retrieves the negotiated property. This method can be called only after
     * the authentication exchange has completed (i.e., when isComplete()
     * returns true); otherwise, an IllegalStateException is thrown.
     *
     * @param propName   The non-null property name
     *
     * @return  The value of the negotiated property. If null, the property was
     *          not negotiated or is not applicable to this mechanism.
     *
     * @exception IllegalStateException   if this authentication exchange has
     *                                    not completed
     */
    @Override
	public Object getNegotiatedProperty(
        String propName)
    {
        if (this.m_state != DigestMD5SaslClient.STATE_VALID_SERVER_RESPONSE)
            throw new IllegalStateException(
             "getNegotiatedProperty: authentication exchange not complete.");

        if (Sasl.QOP.equals(propName))
            return "auth";
        else
            return null;
    }

    /**
     * Disposes of any system resources or security-sensitive information the
     * SaslClient might be using. Invoking this method invalidates the
     * SaslClient instance. This method is idempotent.
     *
     * @exception SaslException  if a problem was encountered while disposing
     *                           of the resources
     */
    @Override
	public void dispose()
            throws SaslException
    {
        if (this.m_state != DigestMD5SaslClient.STATE_DISPOSED)
        {
            this.m_state = DigestMD5SaslClient.STATE_DISPOSED;
        }
    }

    /**
     * Evaluates the challenge data and generates a response. If a challenge
     * is received from the server during the authentication process, this
     * method is called to prepare an appropriate next response to submit to
     * the server.
     *
     * @param challenge  The non-null challenge sent from the server. The
     *                   challenge array may have zero length.
     *
     * @return    The possibly null reponse to send to the server. It is null
     *            if the challenge accompanied a "SUCCESS" status and the
     *            challenge only contains data for the client to update its
     *            state and no response needs to be sent to the server.
     *            The response is a zero-length byte array if the client is to
     *            send a response with no data.
     *
     * @exception SaslException   If an error occurred while processing the
     *                            challenge or generating a response.
     */
    @Override
	public byte[] evaluateChallenge(
        byte[] challenge)
            throws SaslException
    {
        byte[] response = null;

        //printState();
        switch (this.m_state)
        {
        case DigestMD5SaslClient.STATE_INITIAL:
            if (challenge.length == 0)
                throw new SaslException("response = byte[0]");
            else
                try
                {
                    response = this.createDigestResponse(challenge).
                                                           getBytes("UTF-8");
                    this.m_state = DigestMD5SaslClient.STATE_DIGEST_RESPONSE_SENT;
                }
                catch (UnsupportedEncodingException e)
                {
                    throw new SaslException(
                     "UTF-8 encoding not suppported by platform", e);
                }
            break;
        case DigestMD5SaslClient.STATE_DIGEST_RESPONSE_SENT:
            if (this.checkServerResponseAuth(challenge))
                this.m_state = DigestMD5SaslClient.STATE_VALID_SERVER_RESPONSE;
            else
            {
                this.m_state = DigestMD5SaslClient.STATE_INVALID_SERVER_RESPONSE;
                throw new SaslException("Could not validate response-auth " +
                                        "value from server");
            }
            break;
        case DigestMD5SaslClient.STATE_VALID_SERVER_RESPONSE:
        case DigestMD5SaslClient.STATE_INVALID_SERVER_RESPONSE:
            throw new SaslException("Authentication sequence is complete");
        case DigestMD5SaslClient.STATE_DISPOSED:
            throw new SaslException("Client has been disposed");
        default:
            throw new SaslException("Unknown client state.");
        }

        return response;
    }

    /**
     * This function takes a 16 byte binary md5-hash value and creates a 32
     * character (plus    a terminating null character) hex-digit 
     * representation of binary data.
     *
     * @param hash  16 byte binary md5-hash value in bytes
     * 
     * @return   32 character (plus    a terminating null character) hex-digit
     *           representation of binary data.
     */
    char[] convertToHex(
        byte[] hash)
    {
        int          i;
        byte         j;
        byte         fifteen = 15;
        char[]      hex = new char[32];

        for (i = 0; i < 16; i++)
        {
            //convert value of top 4 bits to hex char
            hex[i*2] = DigestMD5SaslClient.getHexChar((byte)((hash[i] & 0xf0) >> 4));
            //convert value of bottom 4 bits to hex char
            hex[i*2 +1] = DigestMD5SaslClient.getHexChar((byte)(hash[i] & 0x0f));
        }

        return hex;
    }

    /**
     * Calculates the HA1 portion of the response
     *
     * @param  algorithm   Algorith to use.
     * @param  userName    User being authenticated
     * @param  realm       realm information
     * @param  password    password of teh user
     * @param  nonce       nonce value
     * @param  clientNonce Clients Nonce value
     *
     * @return  HA1 portion of the response in a character array
     *
     * @exception SaslException  If an error occurs
     */
    char[] DigestCalcHA1(
        String   algorithm,
        String   userName,
        String   realm,
        String   password,
        String   nonce,
        String   clientNonce) throws SaslException
    {
        byte[]        hash;

        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(userName.getBytes("UTF-8"));
            md.update(":".getBytes("UTF-8"));
            md.update(realm.getBytes("UTF-8"));
            md.update(":".getBytes("UTF-8"));
            md.update(password.getBytes("UTF-8"));
            hash = md.digest();

            if ("md5-sess".equals(algorithm))
            {
                md.update(hash);
                md.update(":".getBytes("UTF-8"));
                md.update(nonce.getBytes("UTF-8"));
                md.update(":".getBytes("UTF-8"));
                md.update(clientNonce.getBytes("UTF-8"));
                hash = md.digest();
            }
        }
        catch(NoSuchAlgorithmException e)
        {
            throw new SaslException("No provider found for MD5 hash", e);
        }
        catch(UnsupportedEncodingException e)
        {
            throw new SaslException(
             "UTF-8 encoding not supported by platform.", e);
        }

        return this.convertToHex(hash);
    }


    /**
     * This function calculates the response-value of the response directive of
     * the digest-response as documented in RFC 2831
     *
     * @param  HA1           H(A1)
     * @param  serverNonce   nonce from server
     * @param  nonceCount    8 hex digits
     * @param  clientNonce   client nonce 
     * @param  qop           qop-value: "", "auth", "auth-int"
     * @param  method        method from the request
     * @param  digestUri     requested URL
     * @param  clientResponseFlag request-digest or response-digest
     *
     * @return Response-value of the response directive of the digest-response
     *
     * @exception SaslException  If an error occurs
     */
    char[] DigestCalcResponse(
        char[]      HA1,            /* H(A1) */
        String      serverNonce,    /* nonce from server */
        String      nonceCount,     /* 8 hex digits */
        String      clientNonce,    /* client nonce */
        String      qop,            /* qop-value: "", "auth", "auth-int" */
        String      method,         /* method from the request */
        String      digestUri,      /* requested URL */
        boolean     clientResponseFlag) /* request-digest or response-digest */
            throws SaslException
    {
        byte[]             HA2;
        byte[]             respHash;
        char[]             HA2Hex;

        // calculate H(A2)
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (clientResponseFlag)
                  md.update(method.getBytes("UTF-8"));
            md.update(":".getBytes("UTF-8"));
            md.update(digestUri.getBytes("UTF-8"));
            if ("auth-int".equals(qop))
            {
                md.update(":".getBytes("UTF-8"));
                md.update("00000000000000000000000000000000".getBytes("UTF-8"));
            }
            HA2 = md.digest();
            HA2Hex = this.convertToHex(HA2);

            // calculate response
            md.update(new String(HA1).getBytes("UTF-8"));
            md.update(":".getBytes("UTF-8"));
            md.update(serverNonce.getBytes("UTF-8"));
            md.update(":".getBytes("UTF-8"));
            if (qop.length() > 0)
            {
                md.update(nonceCount.getBytes("UTF-8"));
                md.update(":".getBytes("UTF-8"));
                md.update(clientNonce.getBytes("UTF-8"));
                md.update(":".getBytes("UTF-8"));
                md.update(qop.getBytes("UTF-8"));
                md.update(":".getBytes("UTF-8"));
            }
            md.update(new String(HA2Hex).getBytes("UTF-8"));
            respHash = md.digest();
        }
        catch(NoSuchAlgorithmException e)
        {
            throw new SaslException("No provider found for MD5 hash", e);
        }
        catch(UnsupportedEncodingException e)
        {
            throw new SaslException(
             "UTF-8 encoding not supported by platform.", e);
        }

        return this.convertToHex(respHash);
    }


    /**
     * Creates the intial response to be sent to the server.
     *
     * @param challenge  Challenge in bytes recived form the Server
     *
     * @return Initial response to be sent to the server
     */
    private String createDigestResponse(
        byte[] challenge)
            throws SaslException
    {
        char[]            response;
        StringBuffer    digestResponse = new StringBuffer(512);
        int             realmSize;

        this.m_dc = new DigestChallenge(challenge);

        this.m_digestURI = this.m_protocol + "/" + this.m_serverName;

        if ((this.m_dc.getQop() & DigestChallenge.QOP_AUTH)
            == DigestChallenge.QOP_AUTH )
            this.m_qopValue = "auth";
        else
            throw new SaslException("Client only supports qop of 'auth'");

        //get call back information
        Callback[] callbacks = new Callback[3];
        ArrayList realms = this.m_dc.getRealms();
        realmSize = realms.size();
        if (realmSize == 0)
        {
            callbacks[0] = new RealmCallback("Realm");
        }
        else if (realmSize == 1)
        {
            callbacks[0] = new RealmCallback("Realm", (String)realms.get(0));
        }
        else
        {
            callbacks[0] =
             new RealmChoiceCallback(
                         "Realm",
                         (String[])realms.toArray(new String[realmSize]),
                          0,      //the default choice index
                          false); //no multiple selections
        }

        callbacks[1] = new PasswordCallback("Password", false); 
        //false = no echo

        if (this.m_authorizationId == null || this.m_authorizationId.length() == 0)
            callbacks[2] = new NameCallback("Name");
        else
            callbacks[2] = new NameCallback("Name", this.m_authorizationId);

        try
        {
            this.m_cbh.handle(callbacks);
        }
        catch(UnsupportedCallbackException e)
        {
            throw new SaslException("Handler does not support" +
                                          " necessary callbacks",e);
        }
        catch(IOException e)
        {
            throw new SaslException("IO exception in CallbackHandler.", e);
        }

        if (realmSize > 1)
        {
            int[] selections =
             ((RealmChoiceCallback)callbacks[0]).getSelectedIndexes();

            if (selections.length > 0)
                this.m_realm =
                ((RealmChoiceCallback)callbacks[0]).getChoices()[selections[0]];
            else
                this.m_realm = ((RealmChoiceCallback)callbacks[0]).getChoices()[0];
        }
        else
            this.m_realm = ((RealmCallback)callbacks[0]).getText();

        this.m_clientNonce = this.getClientNonce();

        this.m_name = ((NameCallback)callbacks[2]).getName();
        if (this.m_name == null)
            this.m_name = ((NameCallback)callbacks[2]).getDefaultName();
        if (this.m_name == null)
            throw new SaslException("No user name was specified.");

        this.m_HA1 = this.DigestCalcHA1(
                this.m_dc.getAlgorithm(),
                this.m_name,
                this.m_realm,
                      new String(((PasswordCallback)callbacks[1]).getPassword()),
                this.m_dc.getNonce(),
                this.m_clientNonce);

        response = this.DigestCalcResponse(this.m_HA1,
                this.m_dc.getNonce(),
                                      "00000001",
                this.m_clientNonce,
                this.m_qopValue,
                                      "AUTHENTICATE",
                this.m_digestURI,
                                      true);

        digestResponse.append("username=\"");
        digestResponse.append(this.m_authorizationId);
        if (0 != this.m_realm.length())
        {
            digestResponse.append("\",realm=\"");
            digestResponse.append(this.m_realm);
        }
        digestResponse.append("\",cnonce=\"");
        digestResponse.append(this.m_clientNonce);
        digestResponse.append("\",nc=");
        digestResponse.append("00000001"); //nounce count
        digestResponse.append(",qop=");
        digestResponse.append(this.m_qopValue);
        digestResponse.append(",digest-uri=\"");
	digestResponse.append(this.m_digestURI);
        digestResponse.append("\",response=");
        digestResponse.append(response);
        digestResponse.append(",charset=utf-8,nonce=\"");
        digestResponse.append(this.m_dc.getNonce());
        digestResponse.append("\"");

        return digestResponse.toString();
     }
     
     
    /**
     * This function validates the server response. This step performs a 
     * modicum of mutual authentication by verifying that the server knows
     * the user's password
     *
     * @param  serverResponse  Response recived form Server
     *
     * @return  true if the mutual authentication succeeds;
     *          else return false
     *
     * @exception SaslException  If an error occurs
     */
    boolean checkServerResponseAuth(
            byte[]  serverResponse) throws SaslException
    {
        char[]           response;
        ResponseAuth  responseAuth = null;
        String        responseStr;

        responseAuth = new ResponseAuth(serverResponse);

        response = this.DigestCalcResponse(this.m_HA1,
                this.m_dc.getNonce(),
                                  "00000001",
                this.m_clientNonce,
                this.m_qopValue,
                DigestMD5SaslClient.DIGEST_METHOD,
                this.m_digestURI,
                                  false);

        responseStr = new String(response);

        return responseStr.equals(responseAuth.getResponseValue());
    }


    /**
     * This function returns hex character representing the value of the input
     * 
     * @param value Input value in byte
     *
     * @return Hex value of the Input byte value
     */
    private static char getHexChar(
        byte    value)
    {
        switch (value)
        {
        case 0:
            return '0';
        case 1:
            return '1';
        case 2:
            return '2';
        case 3:
            return '3';
        case 4:
            return '4';
        case 5:
            return '5';
        case 6:
            return '6';
        case 7:
            return '7';
        case 8:
            return '8';
        case 9:
            return '9';
        case 10:
            return 'a';
        case 11:
            return 'b';
        case 12:
            return 'c';
        case 13:
            return 'd';
        case 14:
            return 'e';
        case 15:
            return 'f';
        default:
            return 'Z';
        }
    }

    /**
     * Calculates the Nonce value of the Client
     * 
     * @return   Nonce value of the client
     *
     * @exception   SaslException If an error Occurs
     */
    String getClientNonce() throws SaslException
    {
        byte[]          nonceBytes = new byte[DigestMD5SaslClient.NONCE_BYTE_COUNT];
        SecureRandom    prng;
        byte            nonceByte;
        char[]          hexNonce = new char[DigestMD5SaslClient.NONCE_HEX_COUNT];

        try
        {
            prng = SecureRandom.getInstance("SHA1PRNG");
            prng.nextBytes(nonceBytes);
            for(int i = 0; i< DigestMD5SaslClient.NONCE_BYTE_COUNT; i++)
            {
                //low nibble
                hexNonce[i*2] = DigestMD5SaslClient.getHexChar((byte)(nonceBytes[i] & 0x0f));
                //high nibble
                hexNonce[i*2 +1] = DigestMD5SaslClient.getHexChar((byte)((nonceBytes[i] & 0xf0)
                                                                      >> 4));
            }
            return new String(hexNonce);
        }
        catch(NoSuchAlgorithmException e)
        {
            throw new SaslException("No random number generator available", e);
        }
    }

    /**
     * Returns the IANA-registered mechanism name of this SASL client.
     *  (e.g. "CRAM-MD5", "GSSAPI")
     *
     * @return  "DIGEST-MD5"the IANA-registered mechanism name of this SASL
     *          client.
     */
    @Override
	public String getMechanismName()
    {
        return "DIGEST-MD5";
    }

} //end class DigestMD5SaslClient

