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

public class CmdRMD extends FtpCmd
{
	public static final String message = "TEMPLATE!!";
	protected String input;
	
	public CmdRMD(SessionThread sessionThread, String input) {
		super(sessionThread, CmdRMD.class.toString());
		this.input = input;
	}
	
	@Override
	public void run() {
        this.myLog.l(Log.INFO, "RMD executing");
		String param = FtpCmd.getParameter(this.input);
		File toRemove;
		String errString = null;
		mainblock: {
			if(param.length() < 1) {
				errString = "550 Invalid argument\r\n";
				break mainblock;
			}
			toRemove = FtpCmd.inputPathToChrootedFile(this.sessionThread.getWorkingDir(), param);
			if(this.violatesChroot(toRemove)) {
				errString = "550 Invalid name or chroot violation\r\n";
				break mainblock;
			}
			if(!toRemove.isDirectory()) {
				errString = "550 Can't RMD a non-directory\r\n";
				break mainblock;
			}
			if(toRemove.equals(new File("/"))) {
				errString = "550 Won't RMD the root directory\r\n";
				break mainblock;
			}
			if(!this.recursiveDelete(toRemove)) {
				errString = "550 Deletion error, possibly incomplete\r\n";
				break mainblock;
			}
		}
		if(errString != null) {
            this.sessionThread.writeString(errString);
            this.myLog.l(Log.INFO, "RMD failed: " + errString.trim());
		} else {
            this.sessionThread.writeString("250 Removed directory\r\n");
		}
        this.myLog.l(Log.DEBUG, "RMD finished");
	}
	
	/**
	 * Accepts a file or directory name, and recursively deletes the contents
	 * of that directory and all subdirectories.
	 * @param toDelete
	 * @return Whether the operation completed successfully
	 */
	protected boolean recursiveDelete(File toDelete) {
		if(!toDelete.exists()) {
			return false;
		}
		if(toDelete.isDirectory()) {
			// If any of the recursive operations fail, then we return false
			boolean success = true;
			for(File entry : toDelete.listFiles()) {
				success &= this.recursiveDelete(entry);
			}
            this.myLog.l(Log.DEBUG, "Recursively deleted: " + toDelete);
			return success && toDelete.delete();
		} else {
            this.myLog.l(Log.DEBUG, "RMD deleting file: " + toDelete);
			return toDelete.delete();
		} 
	}
}
