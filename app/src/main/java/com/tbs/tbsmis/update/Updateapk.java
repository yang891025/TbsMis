package com.tbs.tbsmis.update;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.tbs.tbsmis.app.AppException;
import com.tbs.tbsmis.util.StringUtils;


import android.util.Xml;

/**
 * 应用程序更新实体类
 * 
 * @author liux 
 * @version 1.0
 * @created 2013-8-21
 */
@SuppressWarnings("serial")
public class Updateapk implements Serializable {

	public static final String UTF8 = "UTF-8";
	//public final static String NODE_ROOT = "oschina";

	private int versionCode;
	private String versionName;
	private String downloadUrl;
	private String updateLog;

	public int getVersionCode() {
		return this.versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionName() {
		return this.versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getDownloadUrl() {
		return this.downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getUpdateLog() {
		return this.updateLog;
	}

	public void setUpdateLog(String updateLog) {
		this.updateLog = updateLog;
	}

	public static Updateapk parse(InputStream inputStream) throws IOException,
			AppException {
		Updateapk update = null;
		// 获得XmlPullParser解析器
		XmlPullParser xmlParser = Xml.newPullParser();
		try {
			xmlParser.setInput(inputStream, Updateapk.UTF8);
			// 获得解析到的事件类别，这里有开始文档，结束文档，开始标签，结束标签，文本等等事件。
			int evtType = xmlParser.getEventType();
			// 一直循环，直到文档结束
			while (evtType != XmlPullParser.END_DOCUMENT) {
				String tag = xmlParser.getName();
				switch (evtType) {
				case XmlPullParser.START_TAG:
					// 通知信息
					if (tag.equalsIgnoreCase("android")) {
						update = new Updateapk();
					} else if (update != null) {
						if (tag.equalsIgnoreCase("versionCode")) {
							update.setVersionCode(StringUtils.toInt(
									xmlParser.nextText(), 0));
						} else if (tag.equalsIgnoreCase("versionName")) {
							update.setVersionName(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase("downloadUrl")) {
							update.setDownloadUrl(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase("updateLog")) {
							update.setUpdateLog(xmlParser.nextText());
						}
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				// 如果xml没有结束，则导航到下一个节点
				evtType = xmlParser.next();
			}
		} catch (XmlPullParserException e) {
			throw AppException.xml(e);
		} finally {
			inputStream.close();
		}
		return update;
	}
}
