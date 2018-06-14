package com.tbs.cbs;

public class CBSInterpret {

	public native int initGlobal(String iniFile, String webRoot);
	public native String Interpret(String url, String method, String contentType, byte[] content, int contentLength);
	public native long startAsyncInterpret(String url, String method, String contentType, byte[] content, int contentLength);
	public native int stopAsyncInterpret(long objHandle );

	static
	{
		//common modules
		System.loadLibrary("gnustl_shared");
		System.loadLibrary("TBSLib");

		//interpret modules
		System.loadLibrary("CBSInterpret");

		//CBS modules
		System.loadLibrary("TBSBase");
		System.loadLibrary("TBSDBMan");
		System.loadLibrary("EBSClient24");
		System.loadLibrary("FTClient32");
		System.loadLibrary("NewsTools");
		System.loadLibrary("CBScript70");
	}
}
