package com.tbs.chat.test;

import android.test.AndroidTestCase;

import com.tbs.chat.socket.Communication;

public class AndroidTest extends AndroidTestCase {


	private static final String TAG = "AndroidTest";

	
	public void connectionSocket(){
		Communication con = Communication.newInstance(getContext());
		con.register("1", "张无忌", "13488717690", "086", "/storage/emulated/0/TbsIms/temp.jpg");
	}
	
}
