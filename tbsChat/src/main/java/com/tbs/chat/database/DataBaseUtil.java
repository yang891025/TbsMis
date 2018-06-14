/**
 * Copyright (c) 1994-2012 北京金信桥信息技术有限公司
 * Field.java
 * @author liyimig
 * @version 2012-5-3
 * @time 上午09:58:10
 * @function 
 */
package com.tbs.chat.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DataBaseUtil
{

    SqliteOpenHelper helper;
    SQLiteDatabase db;
    Context c;

    public DataBaseUtil(Context context)
    {
        this.c = context;
    }

    public SQLiteDatabase getDataBase()
    {
        helper = SqliteOpenHelper.getInstance(c);
        db = helper.getWritableDatabase();
        return db;
    }
}
