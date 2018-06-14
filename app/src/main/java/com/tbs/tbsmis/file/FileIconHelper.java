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

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.tbs.tbsmis.R;

import java.util.HashMap;

@SuppressLint("DefaultLocale")
public class FileIconHelper implements FileIconLoader.IconLoadFinishListener {

	// private static final String LOG_TAG = "FileIconHelper";

	private static final HashMap<ImageView, ImageView> imageFrames = new HashMap<ImageView, ImageView>();

	private static final HashMap<String, Integer> fileExtToIcons = new HashMap<String, Integer>();

	private final FileIconLoader mIconLoader;

	static {
        FileIconHelper.addItem(new String[] { "mp3" }, R.drawable.format_music);
        FileIconHelper.addItem(new String[] { "ppt", "pptx" }, R.drawable.format_ppt);
        FileIconHelper.addItem(new String[] { "doc"  ,"docx"}, R.drawable.format_word);
        FileIconHelper.addItem(new String[] { "chm" }, R.drawable.format_chm);
        FileIconHelper.addItem(new String[] { "mp4", "mov", "wmv", "mpeg", "m4v", "3gp", "3gpp",
				"3g2", "3gpp2", "asf","mid", "wav","mid"}, R.drawable.format_media);
        FileIconHelper.addItem(new String[] { "jpg", "jpeg", "gif", "png", "bmp", "wbmp" },
				R.drawable.format_picture);
        FileIconHelper.addItem(new String[] { "html" }, R.drawable.format_html);
        FileIconHelper.addItem(new String[] { "apk" }, R.drawable.format_app);
        FileIconHelper.addItem(new String[] { "torrent" }, R.drawable.format_torrent);
        FileIconHelper.addItem(new String[] { "txt", "log", "ini", "lrc","cbs" },
				R.drawable.format_text);
        FileIconHelper.addItem(new String[] {"xsl", "xslx" },
				R.drawable.format_excel);
        FileIconHelper.addItem(new String[] { "pdf" }, R.drawable.format_pdf);
        FileIconHelper.addItem(new String[] { "zip","rar","gz" }, R.drawable.format_zip);
        FileIconHelper.addItem(new String[] { "mtz" }, R.drawable.clean_category_thumbnails);
        FileIconHelper.addItem(new String[] { "swf" }, R.drawable.format_flash);
	}

	public FileIconHelper(Context context) {
        this.mIconLoader = new FileIconLoader(context, this);
	}

	private static void addItem(String[] exts, int resId) {
		if (exts != null) {
			for (String ext : exts) {
                FileIconHelper.fileExtToIcons.put(ext.toLowerCase(), resId);
			}
		}
	}

	public static int getFileIcon(String ext) {
		Integer i = FileIconHelper.fileExtToIcons.get(ext.toLowerCase());
		if (i != null) {
			return i.intValue();
		} else {
			return R.drawable.format_unkown;
		}

	}

	public void setIcon(FileInfo fileInfo, ImageView fileImage,
			ImageView fileImageFrame) {
		String filePath = fileInfo.filePath;
		long fileId = fileInfo.dbId;
		String extFromFilename = Util.getExtFromFilename(filePath);
		FileCategoryHelper.FileCategory fc = FileCategoryHelper.getCategoryFromPath(filePath);
		fileImageFrame.setVisibility(View.GONE);
		boolean set = false;
		int id = FileIconHelper.getFileIcon(extFromFilename);
		fileImage.setImageResource(id);

        this.mIconLoader.cancelRequest(fileImage);
		switch (fc) {
		case Apk:
			set = this.mIconLoader.loadIcon(fileImage, filePath, fileId, fc);
			break;
		case Picture:
		case Video:
			set = this.mIconLoader.loadIcon(fileImage, filePath, fileId, fc);
			if (set)
				fileImageFrame.setVisibility(View.VISIBLE);
			else {
				fileImage
						.setImageResource(fc == FileCategoryHelper.FileCategory.Picture ? R.drawable.format_picture
								: R.drawable.format_media);
                FileIconHelper.imageFrames.put(fileImage, fileImageFrame);
				set = true;
			}
			break;
		default:
			set = true;
			break;
		}

		if (!set)
			fileImage.setImageResource(R.drawable.format_picture);
	}
    public void setIcon(String filePath, ImageView fileImage) {
        String extFromFilename = Util.getExtFromFilename(filePath);
        int id = FileIconHelper.getFileIcon(extFromFilename);
        fileImage.setImageResource(id);
    }
	@Override
	public void onIconLoadFinished(ImageView view) {
		ImageView frame = FileIconHelper.imageFrames.get(view);
		if (frame != null) {
			frame.setVisibility(View.VISIBLE);
            FileIconHelper.imageFrames.remove(view);
		}
	}

}
