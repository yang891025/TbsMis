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
import java.io.IOException;

import android.util.Log;

public class CmdCWD extends FtpCmd
{
	protected String input;
	
	public CmdCWD(SessionThread sessionThread, String input) {
		super(sessionThread, CmdCWD.class.toString());
		this.input = input;
	}
	
	@Override
	public void run() {
        this.myLog.l(Log.DEBUG, "CWD executing");
		String param = FtpCmd.getParameter(this.input);
		File newDir;
		String errString = null;
		mainblock: {
			newDir = FtpCmd.inputPathToChrootedFile(this.sessionThread.getWorkingDir(), param);

			// Ensure the new path does not violate the chroot restriction
			if(this.violatesChroot(newDir)) {
				errString = "550 Invalid name or chroot violation\r\n";
                this.sessionThread.writeString(errString);
                this.myLog.l(Log.INFO, errString);
				break mainblock;
			}

			try {
				newDir = newDir.getCanonicalFile();
				if(!newDir.isDirectory()) {
                    this.sessionThread.writeString("550 Can't CWD to invalid directory\r\n");
				} else if(newDir.canRead()) {
                    this.sessionThread.setWorkingDir(newDir);
                    this.sessionThread.writeString("250 CWD successful\r\n");
				} else {
                    this.sessionThread.writeString("550 That path is inaccessible\r\n");
				}
			} catch(IOException e) {
                this.sessionThread.writeString("550 Invalid path\r\n");
				break mainblock;
			}
		}
        this.myLog.l(Log.DEBUG, "CWD complete");
	}
}
