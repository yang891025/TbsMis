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

public class CmdRNFR extends FtpCmd
{
	protected String input;

	public CmdRNFR(SessionThread sessionThread, String input) {
		super(sessionThread, CmdRNFR.class.toString());
		this.input = input;
	}
	
	@Override
	public void run() {
		String param = FtpCmd.getParameter(this.input);
		String errString = null;
		File file = null;
		mainblock: {
			file = FtpCmd.inputPathToChrootedFile(this.sessionThread.getWorkingDir(), param);
			if(this.violatesChroot(file)) {
				errString = "550 Invalid name or chroot violation\r\n";
				break mainblock;
			}
			if(!file.exists()) {
				errString = "450 Cannot rename nonexistent file\r\n";
			}
		}
		if(errString != null) {
            this.sessionThread.writeString(errString);
            this.myLog.l(Log.INFO, "RNFR failed: " + errString.trim());
            this.sessionThread.setRenameFrom(null);
		} else {
            this.sessionThread.writeString("350 Filename noted, now send RNTO\r\n");
            this.sessionThread.setRenameFrom(file);
		}
	}
}
