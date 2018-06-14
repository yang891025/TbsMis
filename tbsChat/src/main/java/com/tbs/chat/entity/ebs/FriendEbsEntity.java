package com.tbs.chat.entity.ebs;

import java.io.Serializable;

public class FriendEbsEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String myUserCode;
	private String userCode;// 好友账号
	private String userName;// 好友名称
	private String sex;// 好友性别
	private String mobile;// 好友联系人电话
	private String email;// 好友电子邮件
	private String newEMail;// 好友新邮箱
	private String idiograph;// 类型
	private String city;// 资源2
	private String myURL;// 资源3
	private String countryCode;// 资源4
	private String modifyTime;// 资源5

	public FriendEbsEntity() {

	}

	public FriendEbsEntity(String myUserCode, String userCode, String userName,
			String sex, String mobile, String email, String newEMail,
			String idiograph, String city, String myURL, String countryCode,
			String modifyTime) {

		super();
		this.myUserCode = myUserCode;
		this.userCode = userCode;
		this.userName = userName;
		this.sex = sex;
		this.mobile = mobile;
		this.email = email;
		this.newEMail = newEMail;
		this.idiograph = idiograph;
		this.city = city;
		this.countryCode = countryCode;
		this.modifyTime = modifyTime;
		this.myURL = myURL;
	}

	@Override
	public String toString() {
		return "FriendEbsEntity [myUserCode=" + myUserCode + "userCode="
				+ userCode + ", userName=" + userName + ", sex=" + sex
				+ ", mobile=" + mobile + ", email=" + email + ", newEMail="
				+ newEMail + ", idiograph=" + idiograph + ", city=" + mobile
				+ ", countryCode=" + countryCode + ", modifyTime=" + modifyTime
				+ ", myURL=" + myURL + "]";
	}

	public String getMyUserCode() {
		return myUserCode;
	}

	public void setMyUserCode(String myUserCode) {
		this.myUserCode = myUserCode;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNewEMail() {
		return newEMail;
	}

	public void setNewEMail(String newEMail) {
		this.newEMail = newEMail;
	}

	public String getIdiograph() {
		return idiograph;
	}

	public void setIdiograph(String idiograph) {
		this.idiograph = idiograph;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getMyURL() {
		return myURL;
	}

	public void setMyURL(String myURL) {
		this.myURL = myURL;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}
}
