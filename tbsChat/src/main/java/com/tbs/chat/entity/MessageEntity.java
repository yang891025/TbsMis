package com.tbs.chat.entity;

import java.io.Serializable;

/**
 * 聊天信息的格式
 * @author hu
 *
 */
public class MessageEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String self;
	private String friend;
	private int direction;
	private int type;		//消息类型：纯文本、表情+文本、图片、语音
	private String content;
	private String time;
	private int read_type;
	private String data1;
	private String data2;
	private String data3;
	private String data4;
	private String data5;
	
	public MessageEntity(String self, String friend,int direction, int type, String time, String content , int read_type, String data1, String data2, String data3, String data4, String data5) {
		this.self = self;
		this.friend = friend;
		this.direction = direction;
		this.type = type;
		this.time = time;
		this.content = content;
		this.read_type = read_type;
		this.data1 = data1;
		this.data2 = data2;
		this.data3 = data3;
		this.data4 = data4;
		this.data5 = data5;
	}

	public MessageEntity(){
		
	}
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getSelf() {
		return self;
	}

	public void setSelf(String self) {
		this.self = self;
	}

	public String getFriend() {
		return friend;
	}

	public void setFriend(String friend) {
		this.friend = friend;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public int getRead_type() {
		return read_type;
	}

	public void setRead_type(int read_type) {
		this.read_type = read_type;
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
