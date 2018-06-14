package com.tbs.ini;

public class IniFile {
	public String getIniString(String iniFile, String section, String key,
			String defaultValue, byte delimiter) {
		return CGetIniString(iniFile, section, key, defaultValue, delimiter);
	}

	public boolean writeIniString(String iniFile, String section, String key,
			String value) {
		return CWriteIniString(iniFile, section, key, value);
	}

	public String getMemIniString(String iniStr, String section, String key,
			String defaultValue, byte delimiter) {
		return CGetMemIniString(iniStr, section, key, defaultValue, delimiter);
	}

	public String writeMemIniString(String iniStr, String section, String key,
			String value) {
		return CWriteMemIniString(iniStr, section, key, value);
	}

	public boolean deleteIniString(String iniFile, String section, String key) {
		return CDeleteIniString(iniFile, section, key);
	}

	public boolean writeIniSection(String iniFile, String section, String data) {
		return CWriteIniSection(iniFile, section, data);
	}

	public String getIniSection(String iniFile, String section) {
		return CGetIniSection(iniFile, section);
	}

	public boolean deleteIniSection(String iniFile, String section) {
		return CDeleteIniSection(iniFile, section);
	}

	/*
	 * summary: lighttpd.conf read function
	 * 
	 * confFile: lighttpd.conf file path key: the key we will read, such as
	 * "server.port" assignSymbol: assign symbol, like "=", "==", "=~", "+=",
	 * "=>"
	 */
	public String getConfString(String confFile, String key, String assignSymbol) {
		return CGetConfString(confFile, key, assignSymbol);
	}

	/*
	 * summary: lighttpd.conf read function
	 * 
	 * confFile: lighttpd.conf file path section: the section we will read, real
	 * key is "server.port", section is "server" key: the key we will read, real
	 * key is "server.port", key is "port"
	 */
	public String getConfSectionString(String confFile, String section,
			String key) {
		return CGetConfSectionString(confFile, section, key);
	}

	/*
	 * summary: lighttpd.conf write function
	 * 
	 * confFile: lighttpd.conf file path key: the key we will write, such as
	 * "server.port" value: the value we will write assignSymbol: assign symbol,
	 * like "=", "==", "=~", "+=", "=>"
	 */
	public boolean writeConfString(String confFile, String key, String value,
			String assignSymbol) {
		return CWriteConfString(confFile, key, value, assignSymbol);
	}

	/*
	 * summary: lighttpd.conf write function
	 * 
	 * confFile: lighttpd.conf file path section: the section we will write key:
	 * the key we will write, value: the value we will write
	 */
	public boolean writeConfSectionString(String confFile, String section,
			String key, String value) {
		return CWriteConfSectionString(confFile, section, key, value);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////
	// native functions
	private native String CGetIniString(String iniFile, String section,
			String key, String defaultValue, byte delimiter);

	private native boolean CWriteIniString(String iniFile, String section,
			String key, String value);

	private native String CGetMemIniString(String iniStr, String section,
			String key, String defaultValue, byte delimiter);

	private native String CWriteMemIniString(String iniStr, String section,
			String key, String value);

	private native boolean CDeleteIniString(String iniFile, String section,
			String key);

	private native boolean CWriteIniSection(String iniFile, String section,
			String data);

	private native String CGetIniSection(String iniFile, String section);

	private native boolean CDeleteIniSection(String iniFile, String section);

	// lighttpd.conf read/wirte function
	private native String CGetConfString(String confFile, String key,
			String assignSymbol);

	private native boolean CWriteConfString(String confFile, String key,
			String value, String assignSymbol);

	private native String CGetConfSectionString(String confFile,
			String section, String key);

	private native boolean CWriteConfSectionString(String confFile,
			String section, String key, String value);

	// //////////////////////////////////////////////////////////////////////////////////////////////////////
	// variables
	private static Boolean loaded = null;

	// //////////////////////////////////////////////////////////////////////////////////////////////////////
	// preload
	static {
		try {
			System.loadLibrary("gnustl_shared");// for android
		} catch (UnsatisfiedLinkError e) {
		}

		try {
			System.loadLibrary("TBSLib");
			loaded = Boolean.TRUE;
		} catch (UnsatisfiedLinkError e) {
			loaded = Boolean.FALSE;
		}
	}
}
