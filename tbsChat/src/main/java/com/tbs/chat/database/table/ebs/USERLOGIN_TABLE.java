/**
 * Copyright (c) 1994-2012 北京金信桥信息技术有限公司
 * Field.java
 * @author 任雪涛
 * @version 2012-12-21
 * @time 下午12:21:00
 * @function 
 */
package com.tbs.chat.database.table.ebs;

import android.provider.BaseColumns;

public interface USERLOGIN_TABLE extends BaseColumns {

	String TABLE_NAME = "UserLoginTable";// 表名
	String ACCOUNT = "account";// 用户外部账号
	String USERCODE = "userCode";// 用户外部账号
	String PASSWORD = "password";// 用户密码
	String MODIFYTIME = "modifyTime";// 登录时间
	String LOGINID = "loginID";// 登录号
	String LOGINSTATE = "loginState";// 登录状态 0登录 1未登录

	String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
			+ " integer primary key autoincrement," + ACCOUNT + " text,"
			+ USERCODE + " text," + PASSWORD + " text," + MODIFYTIME + " text,"
			+ LOGINID + " text," + LOGINSTATE + " text" + ")";
}
