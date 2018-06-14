package com.tbs.chat.entity.ebs;

import java.io.Serializable;
import java.util.ArrayList;

public class JsonEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private String msg = null;
	private String loginID = null;// 好友账号
	private ArrayList<Object> objArray = null;// 好友名称
	private UserEbsEntity userInfo = null;

	public JsonEntity() {

	}

	public JsonEntity(String msg, String loginID, ArrayList<Object> objArray,
			UserEbsEntity userInfo) {
		super();
		this.msg = msg;
		this.loginID = loginID;
		this.objArray = objArray;
		this.userInfo = userInfo;
	}

	@Override
	public String toString() {
		return "JsonEntity [msg=" + msg + "loginID=" + loginID + ", userInfo="
				+ userInfo + ", objArray=" + objArray + "]";
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getLoginID() {
		return loginID;
	}

	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}

	public ArrayList<Object> getObjArray() {
		return objArray;
	}

	public void setObjArray(ArrayList<Object> objArray) {
		this.objArray = objArray;
	}

	public UserEbsEntity getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserEbsEntity userInfo) {
		this.userInfo = userInfo;
	}
}
