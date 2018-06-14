package com.tbs.tbsmis.entity;

import java.io.Serializable;

public class WXUserEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	private String openid;
	private String nickname;
	private String city;
	private String province;
	private int sex;
	private String language;
	private String headimgurl;
	private int subscribe_time;
	private String remark;
	private int groupid;
	private int subscribe;
	private String country;
	private String groupname;

	public WXUserEntity() {

	}

	public WXUserEntity(String openid, String nickname, String city,
			String province, String country, int sex, String language,
			String headimgurl, int subscribe_time, String remark, int groupid,
			int subscribe) {

        this.openid = openid;
		this.nickname = nickname;
		this.city = city;
		this.province = province;
		this.sex = sex;
		this.country = country;
		this.language = language;
		this.headimgurl = headimgurl;
		this.subscribe_time = subscribe_time;
		this.remark = remark;
		this.groupid = groupid;
		this.subscribe = subscribe;
	}

	public String getOpenid() {
		return this.openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getNickname() {
		return this.nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getProvince() {
		return this.province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public int getSex() {
		return this.sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getLanguage() {
		return this.language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getHeadimgurl() {
		return this.headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public int getSubscribe_time() {
		return this.subscribe_time;
	}

	public void setSubscribe_time(int subscribe_time) {
		this.subscribe_time = subscribe_time;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getGroupid() {
		return this.groupid;
	}

	public void setGroupid(int groupid) {
		this.groupid = groupid;
	}

	public int getSubscribe() {
		return this.subscribe;
	}

	public void setSubscribe(int subscribe) {
		this.subscribe = subscribe;
	}

	public String getGroupname() {
		return this.groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

}
