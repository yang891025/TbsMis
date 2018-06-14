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

import java.io.File;

import android.util.Log;

public class CmdRNTO extends FtpCmd
{
	protected String input;

	public CmdRNTO(SessionThread sessionThread, String input) {
		super(sessionThread, CmdRNTO.class.toString());
		this.input = input;
	}
	
	@Override
	public void run() {
		String param = FtpCmd.getParameter(this.input);
		String errString = null;
		File toFile = null;
        this.myLog.l(Log.DEBUG, "RNTO executing\r\n");
		mainblock: {
            this.myLog.l(Log.INFO, "param: " + param);
			toFile = FtpCmd.inputPathToChrootedFile(this.sessionThread.getWorkingDir(), param);
            this.myLog.l(Log.INFO, "RNTO parsed: " + toFile.getPath());
			if(this.violatesChroot(toFile)) {
				errString = "550 Invalid name or chroot violation\r\n";
				break mainblock;
			}
			File fromFile = this.sessionThread.getRenameFrom();
			if(fromFile == null) {
				errString = "550 Rename error, maybe RNFR not sent\r\n";
				break mainblock;
			}
			if(!fromFile.renameTo(toFile)) {
				errString = "550 Error during rename operation\r\n";
				break mainblock;
			}
		}
		if(errString != null) {
            this.sessionThread.writeString(errString);
            this.myLog.l(Log.INFO, "RNFR failed: " + errString.trim());
		} else {
            this.sessionThread.writeString("250 rename successful\r\n");
		}
        this.sessionThread.setRenameFrom(null);
        this.myLog.l(Log.DEBUG, "RNTO finished");
	}
}
