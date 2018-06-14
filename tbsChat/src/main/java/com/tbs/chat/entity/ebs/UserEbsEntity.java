package com.tbs.chat.entity.ebs;

import java.io.Serializable;

public class UserEbsEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private String userCode ="";// 好友账号
	private String userName ="";// 好友名称
	private String userId ="";// 好友账号
	private String password ="";
	private String loginID ="";
	private String  userNum ="";
	private String  head ="";
	private String sex ="";// 好友性别
	private String mobile ="";// 好友联系人电话
	private String email ="";// 好友电子邮件
	private String newEMail ="";// 好友新邮箱
	private String idiograph ="";// 类型
	private String city ="";// 资源2
	private String myURL ="";// 资源3
	private String countryCode ="";// 资源4
	private String modifyTime ="";// 资源5
	private String data1 ="";
	private String data2 ="";
	private String data3 ="";
	private String data4 ="";
	private String data5 ="";

	public UserEbsEntity() {

	}

	public UserEbsEntity(String userCode, String userName, String sex,
			String mobile, String email, String newEMail, String idiograph,
			String city, String myURL, String countryCode, String modifyTime) {

		super();
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
		return "UserEbsEntity [userCode=" + userCode + ", userName=" + userName
				+ ", sex=" + sex + ", mobile=" + mobile + ", email=" + email
				+ ", newEMail=" + newEMail + ", idiograph=" + idiograph
				+ ", city=" + mobile + ", countryCode=" + countryCode
				+ ", modifyTime=" + modifyTime + ", myURL=" + myURL + "]";
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLoginID() {
		return loginID;
	}

	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}

	public String getData1() {
		return data1;
	}

	public void setData1(String data1) {
		this.data1 = data1;
	}

	public String getData2() {
		return data2;
	}

	public void setData2(String data2) {
		this.data2 = data2;
	}

	public String getData3() {
		return data3;
	}

	public void setData3(String data3) {
		this.data3 = data3;
	}

	public String getData4() {
		return data4;
	}

	public void setData4(String data4) {
		this.data4 = data4;
	}

	public String getData5() {
		return data5;
	}

	public void setData5(String data5) {
		this.data5 = data5;
	}

	public String getUserNum() {
		return userNum;
	}

	public void setUserNum(String userNum) {
		this.userNum = userNum;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
