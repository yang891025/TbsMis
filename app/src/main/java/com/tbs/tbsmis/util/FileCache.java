package com.tbs.tbsmis.util;

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class FileCache {

	private final File cacheDir;

	public FileCache(Context context) {
		// 如果有SD卡则在SD卡中建一个LazyList的目录存放缓存的图片
		// 没有SD卡就放在系统的缓存目录中
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
            this.cacheDir = new File(
					Environment.getExternalStorageDirectory(),
					"LazyList");
		else
            this.cacheDir = context.getCacheDir();
		if (!this.cacheDir.exists())
            this.cacheDir.mkdirs();
	}

	public File getFile(String url) {
		// 将url的hashCode作为缓存的文件名
		String filename = String.valueOf(url.hashCode());
		// Another possible solution
		// String filename = URLEncoder.encode(url);
		File f = new File(this.cacheDir, filename);
		return f;

	}

	public void clear() {
		File[] files = this.cacheDir.listFiles();
		if (files == null)
			return;
		for (File f : files)
			f.delete();
	}

}