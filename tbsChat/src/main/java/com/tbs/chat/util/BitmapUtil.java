package com.tbs.chat.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class BitmapUtil {
	

	private static final String TAG = "BitmapUtil";

	/**
	 * 根据资源文件获取Bitmap
	 */
	public static Bitmap ReadBitmapById(Context context, String path, int screenWidth, int screenHight) {
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Config.ARGB_8888;
		options.inInputShareable = true;
		options.inPurgeable = true;
		options.inTempStorage = new byte[12 * 1024]; 
		
		//Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		
		Log.d(TAG, "head path:"+path);
		
		File file = new File(path);
		FileInputStream fs = null;
		try {
			fs = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Bitmap bmp = null;
		if (fs != null){
			try {
				bmp = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, options);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fs != null) {
					try {
						fs.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return getBitmap(bmp, screenWidth, screenHight);
	}

	/**
	 * 等比例压缩图片
	 */
	public static Bitmap getBitmap(Bitmap bitmap, int screenWidth, int screenHight) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		
		Matrix matrix = new Matrix();
		float scale = (float) screenWidth / w;
		float scale2 = (float) screenHight / h;

		// 保证图片不变形.
		matrix.postScale(scale, scale2);
		// w,h是原图的属性.
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
	}

	/**
	 * 保存图片至SD卡
	 */
	private static int FREE_SD_SPACE_NEEDED_TO_CACHE = 1;
	private static int MB = 1024 * 1024;
	public final static String DIR = "/sdcard/hypers";

	public static void saveBmpToSd(Bitmap bm, String url, int quantity) {
		// 判断sdcard上的空间
		if (FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
			return;
		}
		if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
			return;
		}
		String filename = url;
		// 目录不存在就创建
		File dirPath = new File(DIR);
		if (!dirPath.exists()) {
			dirPath.mkdirs();
		}
		File file = new File(DIR + "/" + filename);
		try {
			file.createNewFile();
			OutputStream outStream = new FileOutputStream(file);
			bm.compress(Bitmap.CompressFormat.PNG, quantity, outStream);
			outStream.flush();
			outStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/***
	 * 获取SD卡图片
	 */
	public static Bitmap GetBitmap(String url, int quantity) {
		InputStream inputStream = null;
		String filename = "";
		Bitmap map = null;
		URL url_Image = null;
		String LOCALURL = "";
		if (url == null)
			return null;
		try {
			filename = url;
		} catch (Exception err) {
		}

		LOCALURL = URLEncoder.encode(filename);
		if (Exist(DIR + "/" + LOCALURL)) {
			map = BitmapFactory.decodeFile(DIR + "/" + LOCALURL);
		} else {
			try {
				url_Image = new URL(url);
				inputStream = url_Image.openStream();
				map = BitmapFactory.decodeStream(inputStream);
				// url = URLEncoder.encode(url, "UTF-8");
				if (map != null) {
					saveBmpToSd(map, LOCALURL, quantity);
				}
				inputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return map;
	}

	/***
	 * 判断图片是存在
	 * @param url
	 * @return
	 */
	public static boolean Exist(String url) {
		File file = new File(DIR + url);
		return file.exists();
	}

	/** * 计算sdcard上的剩余空间 * @return */
	private static int freeSpaceOnSd() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat.getBlockSize()) / MB;
		return (int) sdFreeMB;
	}

}
