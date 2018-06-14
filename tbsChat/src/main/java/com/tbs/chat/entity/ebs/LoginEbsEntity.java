package com.tbs.chat.entity.ebs;

import java.io.Serializable;

public class LoginEbsEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private String account;// 用户账号
	private String userCode;// 用户账号
	private String password;// 用户密码
	private String modifyTime;// 登录时间
	private String loginID;// loginID
	private String loginState;// 用户登录状态 0是已登录 1是已登出

	public LoginEbsEntity() {

	}

	public LoginEbsEntity(String account, String userCode, String password,
			String modifyTime, String loginID, String loginState) {
		super();
		this.account = account;
		this.userCode = userCode;
		this.password = password;
		this.loginID = loginID;
		this.loginState = loginState;
		this.modifyTime = modifyTime;
	}

	@Override
	public String toString() {
		return "LoginEbsEntity [account=" + account + "userCode=" + userCode
				+ "password=" + password + ", loginID=" + loginID
				+ ", loginState=" + loginState + ", modifyTime=" + modifyTime
				+ "]";
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getLoginID() {
		return loginID;
	}

	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}

	public String getLoginState() {
		return loginState;
	}

	public void setLoginState(String loginState) {
		this.loginState = loginState;
	}
}
