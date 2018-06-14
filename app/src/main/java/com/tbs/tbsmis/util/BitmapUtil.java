package com.tbs.tbsmis.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.os.StatFs;

@SuppressLint("SdCardPath")
public class BitmapUtil {

//	/**
//	 * �����Դ�ļ���ȡBitmap
//	 */
//	public static Bitmap ReadBitmapById(Context context, String path,
//			int screenWidth, int screenHight) {
//
//		BitmapFactory.Options options = new BitmapFactory.Options();
//		options.inPreferredConfig = Config.ARGB_8888;
//		options.inInputShareable = true;
//		options.inPurgeable = true;
//		options.inTempStorage = new byte[12 * 1024];
//
//		// Bitmap bitmap = BitmapFactory.decodeFile(path, options);
//
//		File file = new File(path);
//		FileInputStream fs = null;
//		try {
//			fs = new FileInputStream(file);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		Bitmap bmp = null;
//		if (fs != null) {
//			try {
//				bmp = BitmapFactory.decodeFileDescriptor(fs.getFD(), null,
//						options);
//			} catch (IOException e) {
//				e.printStackTrace();
//			} finally {
//				if (fs != null) {
//					try {
//						fs.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//		return toRoundBitmap(bmp);
//	}

	/**
	 * �ȱ���ѹ��ͼƬ
	 */
	public static Bitmap getBitmap(Bitmap bitmap, int screenWidth,
			int screenHight) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		Matrix matrix = new Matrix();
		float scale = (float) screenWidth / w;
		float scale2 = (float) screenHight / h;

		// ��֤ͼƬ������.
		matrix.postScale(scale, scale2);
		// w,h��ԭͼ������.
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
	}

	/**
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}
		Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		int color = 0xff424242;
		Paint paint = new Paint();
		Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		RectF rectF = new RectF(dst);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}

	/**
	 * ����ͼƬ��SD��
	 */
	private static final int FREE_SD_SPACE_NEEDED_TO_CACHE = 1;
	private static final int MB = 1024 * 1024;
	public static final String DIR = "/sdcard/hypers";

	public static void saveBmpToSd(Bitmap bm, String url, int quantity) {
		// �ж�sdcard�ϵĿռ�
		if (BitmapUtil.FREE_SD_SPACE_NEEDED_TO_CACHE > BitmapUtil.freeSpaceOnSd()) {
			return;
		}
		if (!Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			return;
		}
		String filename = url;
		// Ŀ¼�����ھʹ���
		File dirPath = new File(BitmapUtil.DIR);
		if (!dirPath.exists()) {
			dirPath.mkdirs();
		}
		File file = new File(BitmapUtil.DIR + "/" + filename);
		try {
			file.createNewFile();
			OutputStream outStream = new FileOutputStream(file);
			bm.compress(CompressFormat.PNG, quantity, outStream);
			outStream.flush();
			outStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/***
	 * ��ȡSD��ͼƬ
	 */
	@SuppressWarnings("deprecation")
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
		if (BitmapUtil.Exist(BitmapUtil.DIR + "/" + LOCALURL)) {
			map = BitmapFactory.decodeFile(BitmapUtil.DIR + "/" + LOCALURL);
		} else {
			try {
				url_Image = new URL(url);
				inputStream = url_Image.openStream();
				map = BitmapFactory.decodeStream(inputStream);
				// url = URLEncoder.encode(url, "UTF-8");
				if (map != null) {
                    BitmapUtil.saveBmpToSd(map, LOCALURL, quantity);
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
	 * �ж�ͼƬ�Ǵ���
	 * 
	 * @param url
	 * @return
	 */
	public static boolean Exist(String url) {
		File file = new File(BitmapUtil.DIR + url);
		return file.exists();
	}

	/** * ����sdcard�ϵ�ʣ��ռ� * @return */
	private static int freeSpaceOnSd() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());
		double sdFreeMB = (double) stat.getAvailableBlocks() * (double) stat
				.getBlockSize() / BitmapUtil.MB;
		return (int) sdFreeMB;
	}

}
