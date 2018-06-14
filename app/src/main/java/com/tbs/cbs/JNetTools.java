package com.tbs.cbs;

public class JNetTools {
	public static native int GetNewFile(String paramString);

	static {
		System.loadLibrary("gnustl_shared");
		System.loadLibrary("libTbszlibDll");
		System.loadLibrary("JNetTools");
	}

	public static void main(String args[]) {

	}
}
