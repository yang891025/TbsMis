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
import android.content.Intent;
import android.view.View;

import java.util.Collection;

public interface IFileInteractionListener {

    View getViewById(int id);

    Context getContext();

    void startActivity(Intent intent);

    void onDataChanged();

    void onPick(FileInfo f);

    boolean shouldShowOperationPane();

    /**
     * Handle operation listener.
     * @param id
     * @return true: indicate have operated it; false: otherwise.
     */
    boolean onOperation(int id);

    String getDisplayPath(String path);

    String getRealPath(String displayPath);

    void runOnUiThread(Runnable r);

    // return true indicates the navigation has been handled
    boolean onNavigation(String path);

    boolean shouldHideMenu(int menu);

    FileIconHelper getFileIconHelper();

    FileInfo getItem(int pos);

    void sortCurrentList(FileSortHelper sort);

    Collection<FileInfo> getAllFiles();

    void addSingleFile(FileInfo file);

    boolean onRefreshFileList(String path, FileSortHelper sort);

    int getItemCount();
}
