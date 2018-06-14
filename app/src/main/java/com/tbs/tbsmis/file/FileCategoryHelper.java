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

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;

import com.tbs.tbsmis.R;

import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Iterator;

public class FileCategoryHelper {
    public static final int COLUMN_ID = 0;

    public static final int COLUMN_PATH = 1;

    public static final int COLUMN_SIZE = 2;

    public static final int COLUMN_DATE = 3;

    private static final String LOG_TAG = "FileCategoryHelper";

    public enum FileCategory {
        All, Music, Video, Picture, Theme, Doc, Zip, Apk, Custom, Other, Favorite
    }

    private static final String APK_EXT = "apk";
    private static final String THEME_EXT = "mtz";
    private static final String[] ZIP_EXTS  = {
            "zip", "rar"
    };

    public static HashMap<FileCategoryHelper.FileCategory, FilenameExtFilter> filters = new HashMap<FileCategoryHelper.FileCategory, FilenameExtFilter>();

    public static HashMap<FileCategoryHelper.FileCategory, Integer> categoryNames = new HashMap<FileCategoryHelper.FileCategory, Integer>();

    static {
        FileCategoryHelper.categoryNames.put(FileCategoryHelper.FileCategory.All, R.string.category_all);
        FileCategoryHelper.categoryNames.put(FileCategoryHelper.FileCategory.Music, R.string.category_music);
        FileCategoryHelper.categoryNames.put(FileCategoryHelper.FileCategory.Video, R.string.category_video);
        FileCategoryHelper.categoryNames.put(FileCategoryHelper.FileCategory.Picture, R.string.category_picture);
        FileCategoryHelper.categoryNames.put(FileCategoryHelper.FileCategory.Theme, R.string.category_theme);
        FileCategoryHelper.categoryNames.put(FileCategoryHelper.FileCategory.Doc, R.string.category_document);
        FileCategoryHelper.categoryNames.put(FileCategoryHelper.FileCategory.Zip, R.string.category_zip);
        FileCategoryHelper.categoryNames.put(FileCategoryHelper.FileCategory.Apk, R.string.category_apk);
        FileCategoryHelper.categoryNames.put(FileCategoryHelper.FileCategory.Other, R.string.category_other);
        FileCategoryHelper.categoryNames.put(FileCategoryHelper.FileCategory.Favorite, R.string.category_favorite);
    }

    public static FileCategoryHelper.FileCategory[] sCategories = {
            FileCategoryHelper.FileCategory.Music, FileCategoryHelper.FileCategory.Video, FileCategoryHelper.FileCategory.Picture, FileCategoryHelper.FileCategory.Theme,
            FileCategoryHelper.FileCategory.Doc, FileCategoryHelper.FileCategory.Zip, FileCategoryHelper.FileCategory.Apk, FileCategoryHelper.FileCategory.Other
    };

    private FileCategoryHelper.FileCategory mCategory;

    private final Context mContext;

    public FileCategoryHelper(Context context) {
        this.mContext = context;

        this.mCategory = FileCategoryHelper.FileCategory.All;
    }

    public FileCategoryHelper.FileCategory getCurCategory() {
        return this.mCategory;
    }

    public void setCurCategory(FileCategoryHelper.FileCategory c) {
        this.mCategory = c;
    }

    public int getCurCategoryNameResId() {
        return FileCategoryHelper.categoryNames.get(this.mCategory);
    }

    public void setCustomCategory(String[] exts) {
        this.mCategory = FileCategoryHelper.FileCategory.Custom;
        if (FileCategoryHelper.filters.containsKey(FileCategoryHelper.FileCategory.Custom)) {
            FileCategoryHelper.filters.remove(FileCategoryHelper.FileCategory.Custom);
        }

        FileCategoryHelper.filters.put(FileCategoryHelper.FileCategory.Custom, new FilenameExtFilter(exts));
    }

    public FilenameFilter getFilter() {
        return FileCategoryHelper.filters.get(this.mCategory);
    }

    private final HashMap<FileCategoryHelper.FileCategory, FileCategoryHelper.CategoryInfo> mCategoryInfo = new HashMap<FileCategoryHelper.FileCategory, FileCategoryHelper.CategoryInfo>();

    public HashMap<FileCategoryHelper.FileCategory, FileCategoryHelper.CategoryInfo> getCategoryInfos() {
        return this.mCategoryInfo;
    }

    public FileCategoryHelper.CategoryInfo getCategoryInfo(FileCategoryHelper.FileCategory fc) {
        if (this.mCategoryInfo.containsKey(fc)) {
            return this.mCategoryInfo.get(fc);
        } else {
            FileCategoryHelper.CategoryInfo info = new FileCategoryHelper.CategoryInfo();
            this.mCategoryInfo.put(fc, info);
            return info;
        }
    }

    public class CategoryInfo {
        public long count;

        public long size;
    }

    private void setCategoryInfo(FileCategoryHelper.FileCategory fc, long count, long size) {
        FileCategoryHelper.CategoryInfo info = this.mCategoryInfo.get(fc);
        if (info == null) {
            info = new FileCategoryHelper.CategoryInfo();
            this.mCategoryInfo.put(fc, info);
        }
        info.count = count;
        info.size = size;
    }


    private String buildDocSelection() {
        StringBuilder selection = new StringBuilder();
        Iterator<String> iter = Util.sDocMimeTypesSet.iterator();
        while(iter.hasNext()) {
            selection.append("(" + MediaStore.Files.FileColumns.MIME_TYPE + "=='" + iter.next() + "') OR ");
        }
        return  selection.substring(0, selection.lastIndexOf(")") + 1);
    }

    private String buildSelectionByCategory(FileCategoryHelper.FileCategory cat) {
        String selection = null;
        switch (cat) {
            case Theme:
                selection = MediaStore.MediaColumns.DATA + " LIKE '%.mtz'";
                break;
            case Doc:
                selection = this.buildDocSelection();
                break;
            case Zip:
                selection = "(" + MediaStore.Files.FileColumns.MIME_TYPE + " == '" + Util.sZipFileMimeType + "')";
                break;
            case Apk:
                selection = MediaStore.MediaColumns.DATA + " LIKE '%.apk'";
                break;
            default:
                selection = null;
        }
        return selection;
    }

    private Uri getContentUriByCategory(FileCategoryHelper.FileCategory cat) {
        Uri uri;
        String volumeName = "external";
        switch(cat) {
            case Theme:
            case Doc:
            case Zip:
            case Apk:
                uri = MediaStore.Files.getContentUri(volumeName);
                break;
            case Music:
                uri = MediaStore.Audio.Media.getContentUri(volumeName);
                break;
            case Video:
                uri = MediaStore.Video.Media.getContentUri(volumeName);
                break;
            case Picture:
                uri = Media.getContentUri(volumeName);
                break;
           default:
               uri = null;
        }
        return uri;
    }

    private String buildSortOrder(FileSortHelper.SortMethod sort) {
        String sortOrder = null;
        switch (sort) {
            case name:
                sortOrder = MediaStore.Files.FileColumns.TITLE + " asc";
                break;
            case size:
                sortOrder = MediaStore.MediaColumns.SIZE + " asc";
                break;
            case date:
                sortOrder = MediaStore.MediaColumns.DATE_MODIFIED + " desc";
                break;
            case type:
                sortOrder = MediaStore.Files.FileColumns.MIME_TYPE + " asc, " + MediaStore.Files.FileColumns.TITLE + " asc";
                break;
        }
        return sortOrder;
    }

    public Cursor query(FileCategoryHelper.FileCategory fc, FileSortHelper.SortMethod sort) {
        Uri uri = this.getContentUriByCategory(fc);
        String selection = this.buildSelectionByCategory(fc);
        String sortOrder = this.buildSortOrder(sort);

        if (uri == null) {
            Log.e(FileCategoryHelper.LOG_TAG, "invalid uri, category:" + fc.name());
            return null;
        }

        String[] columns = {
                BaseColumns._ID, MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.SIZE, MediaStore.MediaColumns.DATE_MODIFIED
        };

        return this.mContext.getContentResolver().query(uri, columns, selection, null, sortOrder);
    }

    public void refreshCategoryInfo() {
        // clear
        for (FileCategoryHelper.FileCategory fc : FileCategoryHelper.sCategories) {
            this.setCategoryInfo(fc, 0, 0);
        }

        // query database
        String volumeName = "external";

        Uri uri = MediaStore.Audio.Media.getContentUri(volumeName);
        this.refreshMediaCategory(FileCategoryHelper.FileCategory.Music, uri);

        uri = MediaStore.Video.Media.getContentUri(volumeName);
        this.refreshMediaCategory(FileCategoryHelper.FileCategory.Video, uri);

        uri = Media.getContentUri(volumeName);
        this.refreshMediaCategory(FileCategoryHelper.FileCategory.Picture, uri);

        uri = MediaStore.Files.getContentUri(volumeName);
        this.refreshMediaCategory(FileCategoryHelper.FileCategory.Theme, uri);
        this.refreshMediaCategory(FileCategoryHelper.FileCategory.Doc, uri);
        this.refreshMediaCategory(FileCategoryHelper.FileCategory.Zip, uri);
        this.refreshMediaCategory(FileCategoryHelper.FileCategory.Apk, uri);
    }

    private boolean refreshMediaCategory(FileCategoryHelper.FileCategory fc, Uri uri) {
        String[] columns = {
                "COUNT(*)", "SUM(_size)"
        };
        Cursor c = this.mContext.getContentResolver().query(uri, columns, this.buildSelectionByCategory(fc), null, null);
        if (c == null) {
            Log.e(FileCategoryHelper.LOG_TAG, "fail to query uri:" + uri);
            return false;
        }

        if (c.moveToNext()) {
            this.setCategoryInfo(fc, c.getLong(0), c.getLong(1));
            Log.v(FileCategoryHelper.LOG_TAG, "Retrieved " + fc.name() + " info >>> count:" + c.getLong(0) + " size:" + c.getLong(1));
            c.close();
            return true;
        }

        return false;
    }

    public static FileCategoryHelper.FileCategory getCategoryFromPath(String path) {
        MediaFile.MediaFileType type = MediaFile.getFileType(path);
        if (type != null) {
            if (MediaFile.isAudioFileType(type.fileType)) return FileCategoryHelper.FileCategory.Music;
            if (MediaFile.isVideoFileType(type.fileType)) return FileCategoryHelper.FileCategory.Video;
            if (MediaFile.isImageFileType(type.fileType)) return FileCategoryHelper.FileCategory.Picture;
            if (Util.sDocMimeTypesSet.contains(type.mimeType)) return FileCategoryHelper.FileCategory.Doc;
        }

        int dotPosition = path.lastIndexOf('.');
        if (dotPosition < 0) {
            return FileCategoryHelper.FileCategory.Other;
        }

        String ext = path.substring(dotPosition + 1);
        if (ext.equalsIgnoreCase(FileCategoryHelper.APK_EXT)) {
            return FileCategoryHelper.FileCategory.Apk;
        }
        if (ext.equalsIgnoreCase(FileCategoryHelper.THEME_EXT)) {
            return FileCategoryHelper.FileCategory.Theme;
        }

        if (FileCategoryHelper.matchExts(ext, FileCategoryHelper.ZIP_EXTS)) {
            return FileCategoryHelper.FileCategory.Zip;
        }

        return FileCategoryHelper.FileCategory.Other;
    }

    private static boolean matchExts(String ext, String[] exts) {
        for (String ex : exts) {
            if (ex.equalsIgnoreCase(ext))
                return true;
        }
        return false;
    }
}
