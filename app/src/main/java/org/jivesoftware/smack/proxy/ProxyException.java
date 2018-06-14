package org.jivesoftware.smack.proxy;

import org.jivesoftware.smack.proxy.ProxyInfo.ProxyType;

import java.io.IOException;

/**
 * An exception class to handle exceptions caused by proxy.
 * 
 * @author Atul Aggarwal
 */
public class ProxyException 
    extends IOException
{
    public ProxyException(ProxyType type, String ex, Throwable cause)
    {
        super("Proxy Exception " + type + " : "+ex+", "+cause);
    }
    
    public ProxyException(ProxyType type, String ex)
    {
        super("Proxy Exception " + type + " : "+ex);
    }
    
    public ProxyException(ProxyType type)
    {
        super("Proxy Exception " + type + " : " + "Unknown Error");
    }
}
