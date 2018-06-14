package com.tbs.chat.entity.ebs;

import java.io.Serializable;

public class UserHeadEbsEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private String userCode;// 好友账号
	private String fileName;// 好友名称
	private String savePath;// 好友性别
	private long fileSize;// 好友联系人电话
	private String modifyTime;// 好友电子邮件
	private String httpUrl;// 好友电子邮件

	public UserHeadEbsEntity() {

	}

	public UserHeadEbsEntity(String userCode, String fileName, String savePath, long fileSize, String modifyTime, String httpUrl) {
		super();
		this.userCode = userCode;
		this.fileName = fileName;
		this.savePath = savePath;
		this.fileSize = fileSize;
		this.modifyTime = modifyTime;
		this.httpUrl = httpUrl;
	}

	@Override
	public String toString() {
		return "UserHeadEbsEntity [userCode=" + userCode + "fileName=" + fileName + ", savePath=" + savePath + ", fileSize=" + fileSize + ", modifyTime=" + modifyTime + ", httpUrl=" + httpUrl + "]";
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getHttpUrl() {
		return httpUrl;
	}

	public void setHttpUrl(String httpUrl) {
		this.httpUrl = httpUrl;
	}
}
