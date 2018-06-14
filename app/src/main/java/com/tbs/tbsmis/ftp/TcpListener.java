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


import java.net.ServerSocket;
import java.net.Socket;

import com.tbs.tbsmis.file.FTPServerService;
import com.tbs.tbsmis.ftp.SessionThread.Source;

import android.util.Log;

public class TcpListener extends Thread {
	ServerSocket listenSocket;
	FTPServerService ftpServerService;
	MyLog myLog = new MyLog(this.getClass().getName());
	
	public TcpListener(ServerSocket listenSocket, FTPServerService ftpServerService) {
		this.listenSocket = listenSocket;
		this.ftpServerService = ftpServerService;
	}
	
	public void quit() {
		try {
            this.listenSocket.close(); // if the TcpListener thread is blocked on accept,
			                      // closing the socket will raise an exception
		} catch (Exception e) {
            this.myLog.l(Log.DEBUG, "Exception closing TcpListener listenSocket");
		}
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				
				Socket clientSocket = this.listenSocket.accept();
                this.myLog.l(Log.INFO, "New connection, spawned thread");
				SessionThread newSession = new SessionThread(clientSocket,
						new NormalDataSocketFactory(), 
						Source.LOCAL);
				newSession.start();
                this.ftpServerService.registerSessionThread(newSession);
			}
		} catch (Exception e) {
            this.myLog.l(Log.DEBUG, "Exception in TcpListener");
		}
	}
}
	
