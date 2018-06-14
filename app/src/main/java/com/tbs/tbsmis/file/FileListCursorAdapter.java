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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.file.FileListItem.FileItemOnClickListener;

import java.util.Collection;
import java.util.HashMap;

public class FileListCursorAdapter extends CursorAdapter {

    private final LayoutInflater mFactory;

    private final FileViewInteractionHub mFileViewInteractionHub;

    private final FileIconHelper mFileIcon;

    private final HashMap<Integer, FileInfo> mFileNameList = new HashMap<Integer, FileInfo>();

    private final Context mContext;

    public FileListCursorAdapter(Context context, Cursor cursor,
            FileViewInteractionHub f, FileIconHelper fileIcon) {
        super(context, cursor, false /* auto-requery */);
        this.mFactory = LayoutInflater.from(context);
        this.mFileViewInteractionHub = f;
        this.mFileIcon = fileIcon;
        this.mContext = context;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        FileInfo fileInfo = this.getFileItem(cursor.getPosition());
        if (fileInfo == null) {
            // file is not existing, create a fake info
            fileInfo = new FileInfo();
            fileInfo.dbId = cursor.getLong(FileCategoryHelper.COLUMN_ID);
            fileInfo.filePath = cursor.getString(FileCategoryHelper.COLUMN_PATH);
            fileInfo.fileName = Util.getNameFromFilepath(fileInfo.filePath);
            fileInfo.fileSize = cursor.getLong(FileCategoryHelper.COLUMN_SIZE);
            fileInfo.ModifiedDate = cursor.getLong(FileCategoryHelper.COLUMN_DATE);
        }
        FileListItem.setupFileListItemInfo(this.mContext, view, fileInfo, this.mFileIcon,
                this.mFileViewInteractionHub);
        view.findViewById(R.id.category_file_checkbox_area).setOnClickListener(
                new FileItemOnClickListener(this.mContext, this.mFileViewInteractionHub));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return this.mFactory.inflate(R.layout.category_file_browser_item, parent, false);
    }

    @Override
    public void changeCursor(Cursor cursor) {
        this.mFileNameList.clear();
        super.changeCursor(cursor);
    }

    public Collection<FileInfo> getAllFiles() {
        if (this.mFileNameList.size() == this.getCount())
            return this.mFileNameList.values();

        Cursor cursor = this.getCursor();
        if (cursor.moveToFirst()) {
            do {
                Integer position = Integer.valueOf(cursor.getPosition());
                if (this.mFileNameList.containsKey(position))
                    continue;
                FileInfo fileInfo = this.getFileInfo(cursor);
                if (fileInfo != null) {
                    this.mFileNameList.put(position, fileInfo);
                }
            } while (cursor.moveToNext());
        }

        return this.mFileNameList.values();
    }

    public FileInfo getFileItem(int pos) {
        Integer position = Integer.valueOf(pos);
        if (this.mFileNameList.containsKey(position))
            return this.mFileNameList.get(position);

        Cursor cursor = (Cursor) this.getItem(pos);
        FileInfo fileInfo = this.getFileInfo(cursor);
        if (fileInfo == null)
            return null;

        fileInfo.dbId = cursor.getLong(FileCategoryHelper.COLUMN_ID);
        this.mFileNameList.put(position, fileInfo);
        return fileInfo;
    }

    private FileInfo getFileInfo(Cursor cursor) {
        return cursor == null || cursor.getCount() == 0 ? null : Util
                .GetFileInfo(cursor.getString(FileCategoryHelper.COLUMN_PATH));
    }
}
