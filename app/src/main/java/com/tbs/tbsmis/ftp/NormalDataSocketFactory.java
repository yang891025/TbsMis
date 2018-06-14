/*
Copyright 2009 David Revell

This file is part of SwiFTP.

SwiFTP is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SwiFTP is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.tbs.tbsmis.ftp;


import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.tbs.tbsmis.file.FTPServerService;

import android.util.Log;

public class NormalDataSocketFactory extends DataSocketFactory {
	/**
	 * This class implements normal, traditional opening and closing of data sockets
	 * used for transmitting directory listings and file contents. PORT and PASV
	 * work according to the FTP specs. This is in contrast to a 
	 * ProxyDataSocketFactory, which performs contortions to allow data sockets
	 * to be proxied through a server out in the cloud.
	 * 
	 */
	
	// Listener socket used for PASV mode
	ServerSocket server;
	// Remote IP & port information used for PORT mode
	InetAddress remoteAddr;
	int remotePort;
	boolean isPasvMode = true;
	
	public NormalDataSocketFactory() {
        this.clearState();
	}
		
	
	private void clearState() {
		/**
		 * Clears the state of this object, as if no pasv() or port() had occurred.
		 * All sockets are closed.
		 */
		if(this.server != null) {
			try {
                this.server.close();
			} catch (IOException e) {}
		}
        this.server = null;
        this.remoteAddr = null;
        this.remotePort = 0;
        this.myLog.l(Log.DEBUG, "NormalDataSocketFactory state cleared");
	}
	
	@Override
	public int onPasv() {
        this.clearState();
		try {
			// Listen on any port (port parameter 0)
            this.server = new ServerSocket(0, Defaults.tcpConnectionBacklog);
            this.myLog.l(Log.DEBUG, "Data socket pasv() listen successful");
			return this.server.getLocalPort();
		} catch(IOException e) {
            this.myLog.l(Log.ERROR, "Data socket creation error");
            this.clearState();
			return 0;
		}
	}

	@Override
	public boolean onPort(InetAddress remoteAddr, int remotePort) {
        this.clearState();
		this.remoteAddr = remoteAddr;
		this.remotePort = remotePort;
		return true;
	}
	
	@Override
	public Socket onTransfer() {
		if(this.server == null) {
			// We're in PORT mode (not PASV)
			if(this.remoteAddr == null || this.remotePort == 0) {
                this.myLog.l(Log.INFO, "PORT mode but not initialized correctly");
                this.clearState();
				return null;
			}
			Socket socket;
			try {
				socket = new Socket(this.remoteAddr, this.remotePort);
			} catch (IOException e) {
                this.myLog.l(Log.INFO,
						"Couldn't open PORT data socket to: " +
                                this.remoteAddr + ":" + this.remotePort);
                this.clearState();
				return null;
			}
			
			// Kill the socket if nothing happens for X milliseconds
			try {
				socket.setSoTimeout(Defaults.SO_TIMEOUT_MS);
			} catch (Exception e) {
                this.myLog.l(Log.ERROR, "Couldn't set SO_TIMEOUT");
                this.clearState();
				return null;
			}
			
			return socket;
		} else {
			// We're in PASV mode (not PORT)
			Socket socket = null;
			try {
				socket = this.server.accept();
                this.myLog.l(Log.DEBUG, "onTransfer pasv accept successful");
			} catch (Exception e) {
                this.myLog.l(Log.INFO, "Exception accepting PASV socket");
				socket = null;
			}
            this.clearState();
			return socket;  // will be null if error occurred
		}
	}
	
	/**
	 * Return the port number that the remote client should be informed of (in the body
	 * of the PASV response).
	 * @return The port number, or -1 if error.
	 */
	public int getPortNumber() {
		if(this.server != null) {
			return this.server.getLocalPort(); // returns -1 if serversocket is unbound
		} else {
			return -1;
		}
	}
	
	@Override
	public InetAddress getPasvIp() {
		//String retVal = server.getInetAddress().getHostAddress();
		return FTPServerService.getWifiIp();
	}
	
	@Override
	public void reportTraffic(long bytes) {
		// ignore, we don't care about how much traffic goes over wifi.
	}
}