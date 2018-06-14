/**
 * Copyright (c) 1994-2012 北京金信桥信息技术有限公司
 * Field.java
 * @author liyimig
 * @version 2012-5-3
 * @time 上午09:58:10
 * @function 
 */
package com.tbs.tbsmis.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tbs.tbsmis.database.table.WX_GROUP_TABLE;
import com.tbs.tbsmis.database.table.WX_MESSAGE_TABLE;
import com.tbs.tbsmis.database.table.WX_USER_TABLE;

public class SqliteOpenHelper extends SQLiteOpenHelper {

	private static final String TAG = "SqliteOpenHelper";
	private static SqliteOpenHelper dbUtil;
	private static final String DBName = "TbsMis.db";
	static Context ctx;

	public SqliteOpenHelper(Context context, String name,
			SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
        SqliteOpenHelper.ctx = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(SqliteOpenHelper.TAG, "create database in first time ..initial...");

		db.execSQL(WX_GROUP_TABLE.CREATE_TABLE);
		db.execSQL(WX_USER_TABLE.CREATE_TABLE);
		db.execSQL(WX_MESSAGE_TABLE.CREATE_TABLE);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		Log.d(SqliteOpenHelper.TAG, "open database in first time ..initial...");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public static SqliteOpenHelper getInstance(Context context) {
		// String configPath = context.getFilesDir().getParentFile()
		// .getAbsolutePath();
		// if (configPath.endsWith("/") == false) {
		// configPath = configPath + "/";
		// }
		// configPath = configPath + constants.APP_CONFIG_FILE_NAME;
		// IniFile IniFile = new IniFile();
		// String dbName = IniFile.getIniString(configPath, "WeiXin", "iniPath",
		// "wechat", (byte) 0) + ".db";
		//	DBName = dbName;
		return SqliteOpenHelper.getInstance(context, SqliteOpenHelper.DBName, 1);
	}

	public static SqliteOpenHelper getInstance(Context context, String name,
			int version) {
		if (SqliteOpenHelper.dbUtil == null) {
            SqliteOpenHelper.dbUtil = new SqliteOpenHelper(context, name, null, version);
		}
		return SqliteOpenHelper.dbUtil;
	}

	public static boolean deleteDatabase(String dbName) {
		return SqliteOpenHelper.ctx.deleteDatabase(dbName);

	}

}
