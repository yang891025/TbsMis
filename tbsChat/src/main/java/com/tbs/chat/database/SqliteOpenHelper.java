/**
 * Copyright (c) 1994-2012 北京金信桥信息技术有限公司
 * Field.java
 * @author liyimig
 * @version 2012-5-3
 * @time 上午09:58:10
 * @function 
 */
package com.tbs.chat.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tbs.chat.database.table.COUNTEYCODE_TABLE;
import com.tbs.chat.database.table.FRIENDS_TABLE;
import com.tbs.chat.database.table.LOGINSTATE_TABLE;
import com.tbs.chat.database.table.LOGIN_TABLE;
import com.tbs.chat.database.table.MESSAGE_TABLE;
import com.tbs.chat.database.table.TREE_TABLE;
import com.tbs.chat.database.table.USER_TABLE;
import com.tbs.chat.database.table.ebs.FIRENDINFO_TABLE;
import com.tbs.chat.database.table.ebs.USERHEAD_TABLE;
import com.tbs.chat.database.table.ebs.USERINFO_TABLE;
import com.tbs.chat.database.table.ebs.USERLOGIN_TABLE;

public class SqliteOpenHelper extends SQLiteOpenHelper {

	private static final String TAG = "SqliteOpenHelper";
	private static SqliteOpenHelper dbUtil;
	public static final String DB_NAME = "TBSIMS.db";

	static Context ctx;

	public SqliteOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		ctx = context;
	}

	public static SqliteOpenHelper getInstance(Context context) {
		return getInstance(context, DB_NAME, 1);
	}

	public static SqliteOpenHelper getInstance(Context context, String name,
			int version) {
		if (dbUtil == null) {
			dbUtil = new SqliteOpenHelper(context, name, null, version);
		}
		return dbUtil;
	}

	public static boolean deleteDatabase(String dbName) {
		return ctx.deleteDatabase(dbName);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "create database in first time ..initial...");
		db.execSQL(FRIENDS_TABLE.CREATE_TABLE);
		db.execSQL(USER_TABLE.CREATE_TABLE);
		db.execSQL(MESSAGE_TABLE.CREATE_TABLE);
		db.execSQL(COUNTEYCODE_TABLE.CREATE_TABLE);
		db.execSQL(LOGINSTATE_TABLE.CREATE_TABLE);
		db.execSQL(LOGIN_TABLE.CREATE_TABLE);
		db.execSQL(TREE_TABLE.CREATE_TABLE);
		//新增表，与ebs结合
		db.execSQL(USERHEAD_TABLE.CREATE_TABLE);
		db.execSQL(USERINFO_TABLE.CREATE_TABLE);
		db.execSQL(USERLOGIN_TABLE.CREATE_TABLE);
		db.execSQL(FIRENDINFO_TABLE.CREATE_TABLE);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		Log.d(TAG, "open database in first time ..initial...");
		ContentValues values = new ContentValues();
		values.put(COUNTEYCODE_TABLE.COUNTRY, "中国");
		values.put(COUNTEYCODE_TABLE.COUNTRY_CODE, "86");
		values.put(COUNTEYCODE_TABLE.COUNTRY_INITIAL, "chs");
		db.insert(COUNTEYCODE_TABLE.TABLE_NAME, null, values);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
