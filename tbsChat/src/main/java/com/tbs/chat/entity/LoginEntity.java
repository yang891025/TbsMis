package com.tbs.chat.entity;

import java.io.Serializable;

public class LoginEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	public LoginEntity() {

	}

	public LoginEntity(String userID, String password, String loginID) {

		super();
		this.userID = userID;
		this.password = password;
		this.loginID = loginID;
	}

	private String userID;
	private String password;
	private String loginID;

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
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
}
