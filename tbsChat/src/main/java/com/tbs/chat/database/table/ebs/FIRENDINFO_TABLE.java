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

public interface FIRENDINFO_TABLE extends BaseColumns {

	String TABLE_NAME = "FriendInfoTable";// 表名
	String MYUSERCODE = "myUserCode";// 当前登录的用户账号
	String USERCODE = "userCode";// 好友账号
	String USERNAME = "userName";// 好友名称
	String SEX = "sex";// 好友性别
	String MOBILE = "mobile";// 好友联系人电话
	String EMAIL = "email";// 好友电子邮件
	String NEWEMAIL = "newEMail";// 好友新邮箱
	String IDIOGRAPH = "idiograph";// 类型
	String CITY = "city";// 资源2
	String MYURL = "myURL";// 资源3
	String COUNTRYCODE = "countryCode";// 资源4
	String MODIFYTIME = "modifyTime";// 资源5

	String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
			+ " integer primary key autoincrement," + MYUSERCODE + " text,"
			+ USERCODE + " text," + USERNAME + " text," + SEX + " text,"
			+ MOBILE + " text," + EMAIL + " text," + NEWEMAIL + " text,"
			+ IDIOGRAPH + " text," + CITY + " text," + MYURL + " text,"
			+ COUNTRYCODE + " text," + MODIFYTIME + " text" + ")";
}
