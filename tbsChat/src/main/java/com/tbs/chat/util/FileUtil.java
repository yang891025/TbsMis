package com.tbs.chat.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.tbs.chat.constants.Config;
import com.tbs.chat.constants.Constants;

public class FileUtil {

	private static final String TAG = "FileUtil";
	public static final String DOWNLOAD_APK = Constants.BASE_APP_PATH
			+ "/TbsChat/download";
	public static final String PICTURE_PATH = Constants.BASE_APP_PATH
			+ "/TbsChat/picture";
	public static final String VOICE_PATH = Constants.BASE_APP_PATH
			+ "/TbsChat/voice";
	public static final String HEAD_PATH = Constants.BASE_APP_PATH
			+ "/TbsChat/head";
	public static final String TEMP_PATH = Constants.BASE_APP_PATH
			+ "/TbsChat/temp";
	public static final int BUFFER = 1024;

	/**
	 * 创建一个以userId为文件名的文件，保存用户头像。文件路径为"/sdcard/woliao/selfId/friendId.jpg"
	 * 
	 * @param selfId
	 * @param friendId
	 * @return
	 */
	public static File creatApk() {
		File fileParent = new File(DOWNLOAD_APK);
		if (!fileParent.exists()) {
			fileParent.mkdirs();
		}
		File file = new File(DOWNLOAD_APK + "/" + UUID.randomUUID() + ".apk");
		if (!file.exists()) {
			try {
				boolean flag = file.createNewFile();
				if (flag) {
					Log.d(TAG, "head file is creat:" + flag);
				} else {
					Log.d(TAG, "head file isn't creat:" + flag);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * 创建一个以userId为文件名的文件，保存用户头像。文件路径为"/sdcard/woliao/selfId/friendId.jpg"
	 * 
	 * @param selfId
	 * @param friendId
	 * @return
	 */
	public static File createHeadFile(String ID) {
		File fileParent = new File(HEAD_PATH);
		if (!fileParent.exists()) {
			fileParent.mkdirs();
		}
		File file = new File(HEAD_PATH + "/" + ID + ".jpg");
		if (!file.exists()) {
			try {
				boolean flag = file.createNewFile();
				if (flag) {
					Log.d(TAG, "head file is creat:" + flag);
				} else {
					Log.d(TAG, "head file isn't creat:" + flag);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	// 根据用户ID,创建一个以该ID为文件名的jpg图片
	public static File createTempHeadFile(String userId) {
		File fileParent = new File(TEMP_PATH);
		if (!fileParent.exists()) {
			fileParent.mkdirs();
		}
		File file = new File(TEMP_PATH + "/" + userId + ".jpg");
		if (!file.exists()) {
			try {
				boolean flag = file.createNewFile();
				if (flag) {
					Log.d(TAG, "head file is creat:" + flag);
				} else {
					Log.d(TAG, "head file isn't creat:" + flag);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	public static boolean createFolder(String selfId) {
		boolean flag = false;
		String filePath = Constants.BASE_SOFT_PATH + selfId;
		File fileParent = new File(filePath);
		if (fileParent.exists() == false) {
			flag = fileParent.mkdirs();
		} else {
			flag = false;
		}
		return flag;
	}

	/**
	 * 根据fileType,创建普通的jpg或3gp文件来保存图片或语音
	 * 
	 * @param selfId
	 * @param fileType
	 * @return
	 */
	public static File createFile(String selfId, int fileType) {
		String filePath = "";
		String nowTime = TimeUtil.getAbsoluteTime();
		if (fileType == Config.MESSAGE_TYPE_IMG) {
			// filePath = Environment.getExternalStorageDirectory() +
			// "/TbsChat/" + selfId + "/picture";
			filePath = PICTURE_PATH;
		} else if (fileType == Config.MESSAGE_TYPE_AUDIO) {
			// filePath = Environment.getExternalStorageDirectory() +
			// "/TbsChat/" + selfId + "/voice";
			filePath = VOICE_PATH;
		}
		File fileParent = new File(filePath);
		if (fileParent.exists() == false) {
			fileParent.mkdirs();
		}
		File file = null;
		if (fileType == Config.MESSAGE_TYPE_IMG) {
			file = new File(filePath + "/" + nowTime + ".jpg");
		} else if (fileType == Config.MESSAGE_TYPE_AUDIO) {
			file = new File(filePath + "/" + nowTime + ".3gp");
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d("FileUtil", "createFile file:" + file);
		return file;
	}

	public static boolean writeFile(ContentResolver cr, File file, Uri uri) {
		Log.d("FileUtil", "cr:" + cr + ", file:" + file + ", uri:" + uri);
		boolean result = true;
		try {
			FileOutputStream fos = new FileOutputStream(file);
			Log.d("FileUtil", "fout:" + fos);
			Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
			Log.d("FileUtil", "bitmap:" + bitmap);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			try {
				fos.flush();
				fos.close();
			} catch (IOException e) {
				result = false;
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			result = false;
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static boolean headFileExist(String headPath) {
		File file = new File(headPath);
		return file.exists();
	}

	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf.txt
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 */
	public static String copyFile(String oldPath, String newPath) {
		String newFileName = null;
		FileOutputStream fos = null;
		InputStream is = null;
		File newFileAb = null;
		try {
			int bytesum = 0;
			File newFile = new File(newPath);
			if (newFile.exists() == false) {
				newFile.mkdirs();
			}
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				newFileName = oldfile.getName();
			}
			newFileAb = new File(newPath + "/" + newFileName);
			if (newFileAb.exists() == false) {
				try {
					newFileAb.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (oldfile.exists()) { // 文件存在时
				is = new FileInputStream(oldfile); // 读入原文件
				fos = new FileOutputStream(newFileAb);
				byte[] buffer = new byte[BUFFER];
				int length = 0;
				while ((length = is.read(buffer)) != -1) {
					bytesum += length; // 字节数 文件大小
					System.out.println(bytesum);
					fos.write(buffer, 0, length);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return newFileAb.getAbsolutePath();
	}

	/**
	 * 复制整个文件夹内容
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf/ff
	 * @return boolean
	 */
	public static void copyFolder(String oldPath, String newPath) {

		try {
			(new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}
				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath
							+ "/" + (temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {// 如果是子文件夹
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
		} catch (Exception e) {
			System.out.println("复制整个文件夹内容操作出错");
			e.printStackTrace();

		}
	}

	/***
	 * 判断文件存在
	 * 
	 * @param url
	 * @return
	 */
	public static boolean Exist(String url) {
		File file = new File(url);
		return file.exists();
	}
}
