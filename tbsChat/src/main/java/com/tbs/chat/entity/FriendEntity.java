package com.tbs.chat.entity;

import java.io.Serializable;

public class FriendEntity implements Serializable {

	public FriendEntity() {

	}

	public FriendEntity(String userID, String friendID, String userNum,
			String nickName, String sex, String head, String phone,
			String country, String countryInitial, String countryCode,
			String modifyTime, String content, String type, String time,
			String data1, String data2, String data3, String data4, String data5) {
		super();
		this.userID = userID;
		this.friendID = friendID;
		this.nickName = nickName;
		this.sex = sex;
		this.head = head;
		this.phone = phone;
		this.country = country;
		this.countryInitial = countryInitial;
		this.countryCode = countryCode;
		this.modifyTime = modifyTime;
		this.type = type;
		this.content = content;
		this.time = time;
		this.data1 = data1;
		this.data2 = data2;
		this.data3 = data3;
		this.data4 = data4;
		this.data5 = data5;

	}

	private String userID;
	private String friendID;
	private String userNum;
	private String nickName;
	private String sex;
	private String head;
	private String phone;
	private String country;
	private String countryInitial;
	private String countryCode;
	private String modifyTime;
	private String type;
	private String content;
	private String time;
	private String data1;
	private String data2;
	private String data3;
	private String data4;
	private String data5;
	
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getFriendID() {
		return friendID;
	}

	public void setFriendID(String friendID) {
		this.friendID = friendID;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getCountryInitial() {
		return countryInitial;
	}

	public void setCountryInitial(String countryInitial) {
		this.countryInitial = countryInitial;
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

	public String getUserNum() {
		return userNum;
	}

	public void setUserNum(String userNum) {
		this.userNum = userNum;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
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
}
