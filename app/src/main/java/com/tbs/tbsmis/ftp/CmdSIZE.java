package com.tbs.tbsmis.ftp;

import java.io.File;
import java.io.IOException;

public class CmdSIZE extends FtpCmd {
	protected String input;
	
	public CmdSIZE(SessionThread sessionThread, String input) {
		super(sessionThread, CmdSIZE.class.toString());
		this.input = input;
	}
	
	@Override
	public void run() {
        this.myLog.d("SIZE executing");
		
		String errString = null;
		String param = FtpCmd.getParameter(this.input);
		long size = 0;
		mainblock: {
			File currentDir = this.sessionThread.getWorkingDir();
			if(param.contains(File.separator)) {
				errString = "550 No directory traversal allowed in SIZE param\r\n";
				break mainblock;
			}
			File target = new File(currentDir, param);

			// We should have caught any invalid location access before now, but
			// here we check again, just to be explicitly sure.
			if(this.violatesChroot(target)) {
				errString = "550 SIZE target violates chroot\r\n";
				break mainblock;
			}
			if(!target.exists()) {
				errString = "550 Cannot get the SIZE of nonexistent object\r\n";
				try {
                    this.myLog.i("Failed getting size of: " + target.getCanonicalPath());
				} catch (IOException e) {}
				break mainblock;
			}
			if(!target.isFile()) {
				errString = "550 Cannot get the size of a non-file\r\n";
				break mainblock;
			}
			size = target.length(); 
		}
		if(errString != null) {
            this.sessionThread.writeString(errString);
		} else {
            this.sessionThread.writeString("213 " + size + "\r\n");
		}
        this.myLog.d("SIZE complete");
	}

}
