/**
 * Copyright (c) 1994-2012 北京金信桥信息技术有限公司
 * Field.java
 * @author 任雪涛
 * @version 2012-12-21
 * @time 下午12:21:00
 * @function 
 */
package com.tbs.chat.database.table;

import android.provider.BaseColumns;

public interface FRIENDS_TABLE extends BaseColumns {

	String TABLE_NAME = "FriendsTable";// 表名
	String SELF_ID = "self_id";// 联系电话
	String FRIEND_ID = "friend_id";// 联系人电话
	String USERNUM = "userNum";// 用户号
	String NICK = "nick_Name";// 联系电话
	String SEX = "sex";// 性别
	String HEAD = "head";// 头像
	String PHONE = "phone";// 联系人电话
	String COUNTRY = "country";// 国家名称
	String COUNTRY_INITIAL = "country_initial";// 国家首字母
	String COUNTRY_CODE = "country_code";// 收信人名称
	String MODIFY_TIME = "modify_time";// 地址
	String TYPE = "type";// 地址
	String CONTENT = "content";// 地址
	String TIME = "time";// 地址
	String DATA1 = "data1";// 资源1
	String DATA2 = "data2";// 资源2
	String DATA3 = "data3";// 资源3
	String DATA4 = "data4";// 资源4
	String DATA5 = "data5";// 资源5

	String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
			+ " integer primary key autoincrement," + SELF_ID + " text,"
			+ FRIEND_ID + " text," + NICK + " text," + SEX + " text," + HEAD
			+ " text," + PHONE + " text," + COUNTRY + " text," + COUNTRY_INITIAL
			+ " text," + COUNTRY_CODE + " text," + CONTENT + " text,"
			+ MODIFY_TIME + " text," + TIME + " text," + TYPE + " text,"
			+ DATA1 + " text," + DATA2 + " text," + DATA3 + " text," + DATA4
			+ " text," + DATA5 + " text" + ")";
}
