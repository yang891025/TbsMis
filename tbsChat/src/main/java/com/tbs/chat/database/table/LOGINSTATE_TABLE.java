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

public interface LOGINSTATE_TABLE extends BaseColumns {

	String TABLE_NAME = "LoginStateTable";// 表名
	String USERID = "userID";// 用户账号
	String LOGINID = "loginID";// 用户账号

	String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID + " integer primary key autoincrement," + USERID + " text," + LOGINID + " text" + ")";
}
