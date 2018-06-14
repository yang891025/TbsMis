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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.file.FileListItem.FileItemOnClickListener;

import java.util.List;

public class FileListAdapter extends ArrayAdapter<FileInfo> {
    private final LayoutInflater mInflater;

    private final FileViewInteractionHub mFileViewInteractionHub;

    private final FileIconHelper mFileIcon;

    private final Context mContext;

    public FileListAdapter(Context context, int resource,
            List<FileInfo> objects, FileViewInteractionHub f,
            FileIconHelper fileIcon) {
        super(context, resource, objects);
        this.mInflater = LayoutInflater.from(context);
        this.mFileViewInteractionHub = f;
        this.mFileIcon = fileIcon;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView != null) {
            view = convertView;
        } else {
            view = this.mInflater.inflate(R.layout.file_browser_item, parent, false);
        }

        FileInfo lFileInfo = this.mFileViewInteractionHub.getItem(position);
        FileListItem.setupFileListItemInfo(this.mContext, view, lFileInfo,
                this.mFileIcon, this.mFileViewInteractionHub);
        view.findViewById(R.id.file_checkbox_area).setOnClickListener(
                new FileItemOnClickListener(this.mContext,
                        this.mFileViewInteractionHub));
        return view;
    }
}
