package com.tbs.chat.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.Context;


public class PropertyReader {
	
	private static final String TAG = "PropertyReader";
	
	private Properties properties;
	private InputStream in;

	/**
	 * 读取文件
	 * @param filename 文件名
	 * @throws IOException
	 */
	public PropertyReader(Context context, String name) throws IOException {
		try {
			File file = new File(name);
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.getStackTrace();
		}
	}

	public Properties loadConfig() {
		properties = new Properties();
		try {
			properties.load(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return properties;
	}
}