package com.tbs.chat.entity;

import java.io.Serializable;

public class LoginStateEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	public LoginStateEntity() {

	}

	public LoginStateEntity(String userID, String loginID) {

		super();
		this.userID = userID;
		this.loginID = loginID;
	}

	private String userID;
	private String loginID;

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getLoginID() {
		return loginID;
	}

	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}
	
	
}
