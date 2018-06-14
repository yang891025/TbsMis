/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * This file is part of FileExplorer.
 *
 * FileExplorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FileExplorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.tbs.tbsmis.file;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavoriteDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "file_explorer";

    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "favorite";

    public static final String FIELD_ID = "_id";

    public static final String FIELD_TITLE = "title";

    public static final String FIELD_LOCATION = "location";

    private boolean firstCreate;

    private final FavoriteDatabaseHelper.FavoriteDatabaseListener mListener;

    private static FavoriteDatabaseHelper instance;

    public interface FavoriteDatabaseListener {
        void onFavoriteDatabaseChanged();
    }

    public FavoriteDatabaseHelper(Context context, FavoriteDatabaseHelper.FavoriteDatabaseListener listener) {
        super(context, FavoriteDatabaseHelper.DATABASE_NAME, null, FavoriteDatabaseHelper.DATABASE_VERSION);
        FavoriteDatabaseHelper.instance = this;
        this.mListener = listener;
    }

    public static FavoriteDatabaseHelper getInstance() {
        return FavoriteDatabaseHelper.instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "Create table " + FavoriteDatabaseHelper.TABLE_NAME + "(" + FavoriteDatabaseHelper.FIELD_ID + " integer primary key autoincrement,"
                + FavoriteDatabaseHelper.FIELD_TITLE + " text, " + FavoriteDatabaseHelper.FIELD_LOCATION + " text );";
        db.execSQL(sql);
        this.firstCreate = true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = " DROP TABLE IF EXISTS " + FavoriteDatabaseHelper.TABLE_NAME;
        db.execSQL(sql);
        this.onCreate(db);
    }

    public boolean isFirstCreate() {
        return this.firstCreate;
    }

    public boolean isFavorite(String path) {
        String selection = FavoriteDatabaseHelper.FIELD_LOCATION + "=?";
        String[] selectionArgs = {
            path
        };
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(FavoriteDatabaseHelper.TABLE_NAME, null, selection, selectionArgs, null, null, null);
        if (cursor == null)
            return false;
        boolean ret = cursor.getCount() > 0;
        cursor.close();
        return ret;
    }

    public Cursor query() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(FavoriteDatabaseHelper.TABLE_NAME, null, null, null, null, null, null);
        return cursor;
    }

    public long insert(String title, String location) {
        if (this.isFavorite(location))
            return -1;

        SQLiteDatabase db = getWritableDatabase();
        long ret = db.insert(FavoriteDatabaseHelper.TABLE_NAME, null, this.createValues(title, location));
        this.mListener.onFavoriteDatabaseChanged();
        return ret;
    }

    public void delete(long id, boolean notify) {
        SQLiteDatabase db = getWritableDatabase();
        String where = FavoriteDatabaseHelper.FIELD_ID + "=?";
        String[] whereValue = {
            Long.toString(id)
        };
        db.delete(FavoriteDatabaseHelper.TABLE_NAME, where, whereValue);

        if (notify)
            this.mListener.onFavoriteDatabaseChanged();
    }

    public void delete(String location) {
        SQLiteDatabase db = getWritableDatabase();
        String where = FavoriteDatabaseHelper.FIELD_LOCATION + "=?";
        String[] whereValue = {
            location
        };
        db.delete(FavoriteDatabaseHelper.TABLE_NAME, where, whereValue);
        this.mListener.onFavoriteDatabaseChanged();
    }

    public void update(int id, String title, String location) {
        SQLiteDatabase db = getWritableDatabase();
        String where = FavoriteDatabaseHelper.FIELD_ID + "=?";
        String[] whereValue = {
            Integer.toString(id)
        };
        db.update(FavoriteDatabaseHelper.TABLE_NAME, this.createValues(title, location), where, whereValue);
        this.mListener.onFavoriteDatabaseChanged();
    }

    private ContentValues createValues(String title, String location) {
        ContentValues cv = new ContentValues();
        cv.put(FavoriteDatabaseHelper.FIELD_TITLE, title);
        cv.put(FavoriteDatabaseHelper.FIELD_LOCATION, location);
        return cv;
    }
}
