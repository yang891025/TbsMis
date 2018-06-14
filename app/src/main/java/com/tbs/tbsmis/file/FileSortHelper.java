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

import java.util.Comparator;
import java.util.HashMap;

public class FileSortHelper {

    public enum SortMethod {
        name, size, date, type
    }

    private FileSortHelper.SortMethod mSort;

    private boolean mFileFirst;

    @SuppressWarnings("rawtypes")
	private final HashMap<FileSortHelper.SortMethod, Comparator> mComparatorList = new HashMap<FileSortHelper.SortMethod, Comparator>();

    public FileSortHelper() {
        this.mSort = FileSortHelper.SortMethod.name;
        this.mComparatorList.put(FileSortHelper.SortMethod.name, this.cmpName);
        this.mComparatorList.put(FileSortHelper.SortMethod.size, this.cmpSize);
        this.mComparatorList.put(FileSortHelper.SortMethod.date, this.cmpDate);
        this.mComparatorList.put(FileSortHelper.SortMethod.type, this.cmpType);
    }

    public void setSortMethog(FileSortHelper.SortMethod s) {
        this.mSort = s;
    }

    public FileSortHelper.SortMethod getSortMethod() {
        return this.mSort;
    }

    public void setFileFirst(boolean f) {
        this.mFileFirst = f;
    }

    @SuppressWarnings("rawtypes")
	public Comparator getComparator() {
        return this.mComparatorList.get(this.mSort);
    }

    private abstract class FileComparator implements Comparator<FileInfo> {

        @Override
        public int compare(FileInfo object1, FileInfo object2) {
            if (object1.IsDir == object2.IsDir) {
                return this.doCompare(object1, object2);
            }

            if (FileSortHelper.this.mFileFirst) {
                // the files are listed before the dirs
                return object1.IsDir ? 1 : -1;
            } else {
                // the dir-s are listed before the files
                return object1.IsDir ? -1 : 1;
            }
        }

        protected abstract int doCompare(FileInfo object1, FileInfo object2);
    }

    @SuppressWarnings("rawtypes")
	private final Comparator cmpName = new FileSortHelper.FileComparator() {
        @Override
        public int doCompare(FileInfo object1, FileInfo object2) {
            return object1.fileName.compareToIgnoreCase(object2.fileName);
        }
    };
    @SuppressWarnings("rawtypes")
    private final Comparator cmpSize = new FileSortHelper.FileComparator() {
        @Override
        public int doCompare(FileInfo object1, FileInfo object2) {
            return FileSortHelper.this.longToCompareInt(object1.fileSize - object2.fileSize);
        }
    };
    @SuppressWarnings("rawtypes")
    private final Comparator cmpDate = new FileSortHelper.FileComparator() {
        @Override
        public int doCompare(FileInfo object1, FileInfo object2) {
            return FileSortHelper.this.longToCompareInt(object2.ModifiedDate - object1.ModifiedDate);
        }
    };

    private int longToCompareInt(long result) {
        return result > 0 ? 1 : result < 0 ? -1 : 0;
    }
    @SuppressWarnings("rawtypes")
    private final Comparator cmpType = new FileSortHelper.FileComparator() {
        @Override
        public int doCompare(FileInfo object1, FileInfo object2) {
            int result = Util.getExtFromFilename(object1.fileName).compareToIgnoreCase(
                    Util.getExtFromFilename(object2.fileName));
            if (result != 0)
                return result;

            return Util.getNameFromFilename(object1.fileName).compareToIgnoreCase(
                    Util.getNameFromFilename(object2.fileName));
        }
    };
}
