package com.tbs.tbsmis.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Java utils 实现的Zip工具
 * 
 * @author once
 */
public class ZipUtils {
	private static final int BUFF_SIZE = 1024 * 1024; // 1M Byte

	/**
	 * 批量压缩文件（夹�? *
	 * 
	 * @param resFileList
	 *            要压缩的文件（夹）列�? * @param zipFile 生成的压缩文�? * @throws
	 *            IOException 当压缩过程出错时抛出
	 */
	public static void zipFiles(Collection<File> resFileList, File zipFile)
			throws IOException {
		ZipOutputStream zipout = new ZipOutputStream(new BufferedOutputStream(
				new FileOutputStream(zipFile), ZipUtils.BUFF_SIZE));
		for (File resFile : resFileList) {
            ZipUtils.zipFile(resFile, zipout, "");
		}
		zipout.close();
	}

	/**
	 * 批量压缩文件（夹�? *
	 * 
	 * @param resFileList
	 *            要压缩的文件（夹）列�? * @param zipFile 生成的压缩文�? * @param
	 *            comment 压缩文件的注�? * @throws IOException 当压缩过程出错时抛出
	 */
	public static void zipFiles(Collection<File> resFileList, File zipFile,
			String comment) throws IOException {
		ZipOutputStream zipout = new ZipOutputStream(new BufferedOutputStream(
				new FileOutputStream(zipFile), ZipUtils.BUFF_SIZE));
		for (File resFile : resFileList) {
            ZipUtils.zipFile(resFile, zipout, "");
		}
		zipout.setComment(comment);
		zipout.close();
	}

	/**
	 * 解压缩一个文�? *
	 * 
	 * @param zipFile
	 *            压缩文件
	 * @param folderPath
	 *            解压缩的目标目录
	 * @throws IOException
	 *             当解压缩过程出错时抛�?
	 */
	public static void upZipFile(File zipFile, String folderPath)
			throws IOException {
		File desDir = new File(folderPath);
		if (!desDir.exists()) {
			desDir.mkdirs();
		}
		ZipFile zf = new ZipFile(zipFile);
		for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
			ZipEntry entry = (ZipEntry) entries.nextElement();
			InputStream in = zf.getInputStream(entry);
			String str = folderPath + File.separator + entry.getName();
			str = new String(str.getBytes("8859_1"), "GB2312");
			File desFile = new File(str);
			if (!desFile.exists()) {
				File fileParentDir = desFile.getParentFile();
				if (!fileParentDir.exists()) {
					fileParentDir.mkdirs();
				}
			}

			if (entry.isDirectory()) {
				desFile.mkdir();
			} else {
				desFile.createNewFile();
				OutputStream out = new FileOutputStream(desFile);
				byte buffer[] = new byte[ZipUtils.BUFF_SIZE];
				int realLength;
				while ((realLength = in.read(buffer)) > 0) {
					out.write(buffer, 0, realLength);
				}

				out.close();
			}

			in.close();

		}
	}

	/**
	 * 解压缩一个文�? *
	 * 
	 * @param zipStream
	 *            压缩文件
	 * @param folderPath
	 *            解压缩的目标目录
	 * @throws IOException
	 *             当解压缩过程出错时抛�?
	 */
	public static void upZipFile(InputStream zipInputStream, String folderPath)
			throws IOException {
		File desDir = new File(folderPath);
		if (!desDir.exists()) {
			desDir.mkdirs();
		}
		ZipInputStream zipInput = new ZipInputStream(new BufferedInputStream(
				zipInputStream));
		ZipEntry entry = null;
		while ((entry = zipInput.getNextEntry()) != null) {
			String str = folderPath + File.separator + entry.getName();
			str = new String(str.getBytes("8859_1"), "GB2312");
			File desFile = new File(str);
			// if (!desFile.exists()) {
			File fileParentDir = desFile.getParentFile();
			// if (!fileParentDir.exists()) {
			fileParentDir.mkdirs();
			// }
			// }

			if (entry.isDirectory()) {
				desFile.mkdir();
			} else {
				desFile.createNewFile();
				OutputStream out = new FileOutputStream(desFile);
				byte buffer[] = new byte[ZipUtils.BUFF_SIZE];
				int realLength;
				while ((realLength = zipInput.read(buffer)) > 0) {
					out.write(buffer, 0, realLength);
				}

				out.close();
			}

		}

		zipInput.close();
	}

	/**
	 * 解压文件名包含传入文字的文件
	 * 
	 * @param zipFile
	 *            压缩文件
	 * @param folderPath
	 *            目标文件�? * @param nameContains 传入的文件匹配名
	 * @throws ZipException
	 *             压缩格式有误时抛�? * @throws IOException IO错误时抛�?
	 */
	public static ArrayList<File> upZipSelectedFile(File zipFile,
			String folderPath, String nameContains) throws
            IOException {
		ArrayList<File> fileList = new ArrayList<File>();

		File desDir = new File(folderPath);
		if (!desDir.exists()) {
			desDir.mkdir();
		}

		ZipFile zf = new ZipFile(zipFile);
		for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
			ZipEntry entry = (ZipEntry) entries.nextElement();
			if (entry.getName().contains(nameContains)) {
				InputStream in = zf.getInputStream(entry);
				String str = folderPath + File.separator + entry.getName();
				str = new String(str.getBytes("8859_1"), "GB2312");
				// str.getBytes("GB2312"),"8859_1" 输出
				// str.getBytes("8859_1"),"GB2312" 输入
				File desFile = new File(str);
				if (!desFile.exists()) {
					File fileParentDir = desFile.getParentFile();
					if (!fileParentDir.exists()) {
						fileParentDir.mkdirs();
					}
					desFile.createNewFile();
				}
				OutputStream out = new FileOutputStream(desFile);
				byte buffer[] = new byte[ZipUtils.BUFF_SIZE];
				int realLength;
				while ((realLength = in.read(buffer)) > 0) {
					out.write(buffer, 0, realLength);
				}
				in.close();
				out.close();
				fileList.add(desFile);
			}
		}
		return fileList;
	}

	/**
	 * 获得压缩文件内文件列�? *
	 * 
	 * @param zipFile
	 *            压缩文件
	 * @return 压缩文件内文件名�? * @throws ZipException 压缩文件格式有误时抛�? * @throws
	 *         IOException 当解压缩过程出错时抛�?
	 */
	public static ArrayList<String> getEntriesNames(File zipFile)
			throws IOException {
		ArrayList<String> entryNames = new ArrayList<String>();
		Enumeration<?> entries = ZipUtils.getEntriesEnumeration(zipFile);
		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();
			entryNames.add(new String(ZipUtils.getEntryName(entry).getBytes("GB2312"),
					"8859_1"));
		}
		return entryNames;
	}

	/**
	 * 获得压缩文件内压缩文件对象以取得其属�? *
	 * 
	 * @param zipFile
	 *            压缩文件
	 * @return 返回�?��压缩文件列表
	 * @throws ZipException
	 *             压缩文件格式有误时抛�? * @throws IOException IO操作有误时抛�?
	 */
	public static Enumeration<?> getEntriesEnumeration(File zipFile)
			throws IOException {
		ZipFile zf = new ZipFile(zipFile);
		return zf.entries();

	}

	/**
	 * 取得压缩文件对象的注�? *
	 * 
	 * @param entry
	 *            压缩文件对象
	 * @return 压缩文件对象的注�? * @throws UnsupportedEncodingException
	 */
	public static String getEntryComment(ZipEntry entry)
			throws UnsupportedEncodingException {
		return new String(entry.getComment().getBytes("GB2312"), "8859_1");
	}

	/**
	 * 取得压缩文件对象的名�? *
	 * 
	 * @param entry
	 *            压缩文件对象
	 * @return 压缩文件对象的名�? * @throws UnsupportedEncodingException
	 */
	public static String getEntryName(ZipEntry entry)
			throws UnsupportedEncodingException {
		return new String(entry.getName().getBytes("GB2312"), "8859_1");
	}

	/**
	 * 压缩文件
	 * 
	 * @param resFile
	 *            �?��压缩的文件（夹）
	 * @param zipout
	 *            压缩的目的文�? * @param rootpath 压缩的文件路�? * @throws
	 *            FileNotFoundException 找不到文件时抛出
	 * @throws IOException
	 *             当压缩过程出错时抛出
	 */
	private static void zipFile(File resFile, ZipOutputStream zipout,
			String rootpath) throws IOException {
		rootpath = rootpath
				+ (rootpath.trim().length() == 0 ? "" : File.separator)
				+ resFile.getName();
		rootpath = new String(rootpath.getBytes("8859_1"), "GB2312");
		if (resFile.isDirectory()) {
			File[] fileList = resFile.listFiles();
			for (File file : fileList) {
                ZipUtils.zipFile(file, zipout, rootpath);
			}
		} else {
			byte buffer[] = new byte[ZipUtils.BUFF_SIZE];
			BufferedInputStream in = new BufferedInputStream(
					new FileInputStream(resFile), ZipUtils.BUFF_SIZE);
			zipout.putNextEntry(new ZipEntry(rootpath));
			int realLength;
			while ((realLength = in.read(buffer)) != -1) {
				zipout.write(buffer, 0, realLength);
			}
			in.close();
			zipout.flush();
			zipout.closeEntry();
		}
	}
}
