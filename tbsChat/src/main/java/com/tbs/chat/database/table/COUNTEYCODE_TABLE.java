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

public interface COUNTEYCODE_TABLE extends BaseColumns {

	String TABLE_NAME = "countryCodeTable";// 表名
	String COUNTRY = "country";// 国家名称
	String COUNTRY_INITIAL = "country_initial";// 国家首字母
	String COUNTRY_CODE = "country_code";// 收信人名称
	String DATA1 = "data1";// 其他资源
	String DATA2 = "data2";// 其他资源
	String DATA3 = "data3";// 其他资源
	String DATA4 = "data4";// 其他资源
	String DATA5 = "data5";// 其他资源

	String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
			+ " integer primary key autoincrement," + COUNTRY + " text,"
			+ COUNTRY_INITIAL + " text," + COUNTRY_CODE + " text," + DATA1
			+ " text," + DATA2 + " text," + DATA3 + " text," + DATA4 + " text,"
			+ DATA5 + " text" + ")";
}
