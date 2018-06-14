package com.tbs.tbsmis.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileFilter;

public class FileIO {

	private static FileIO instance;

	public FileIO() {

	}

	public static FileIO getInstance() {
		if (FileIO.instance == null) {
            FileIO.instance = new FileIO();
		}
		return FileIO.instance;
	}

	public static SmbFile[] smbTraversal(String remoteUrl) {
		System.out.println(remoteUrl);
		SmbFile[] data = null;
		try {
			SmbFile file = new SmbFile(remoteUrl);
			data = FileIO.findSharefiles(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static SmbFile[] findSharefiles(SmbFile dir) {
		Stack curPath = new Stack();
		curPath.push(dir);
		return FileIO.findFiles(curPath);
	}

	@SuppressWarnings("rawtypes")
	public static SmbFile[] findFiles(Stack curPath) {
		/** ���Ŀ¼ */
		class MyDirFilter implements SmbFileFilter {
			@Override
			public boolean accept(SmbFile pathname) {
				try {
					return pathname != null
							&& (pathname.isFile() || pathname.isDirectory())
							&& pathname.canRead();
				} catch (Exception e) {
					return false;
				}
			}
		}

		MyDirFilter dirFilter = new MyDirFilter();
		SmbFile dir = (SmbFile) curPath.pop();
		SmbFile[] subDirs = null;

		try {
			subDirs = dir.listFiles(dirFilter);
		} catch (SmbException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		}
		return subDirs;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean checkUser(String curUrl) {
		SmbFile file = null;
		Stack curPath = null;
		try {
			file = new SmbFile(curUrl);
			curPath = new Stack();
			curPath.push(file);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("error  here");
		}

		/** ���Ŀ¼ */
		class MyDirFilter implements SmbFileFilter {
			@Override
			public boolean accept(SmbFile pathname) {
				try {
					return pathname != null
							&& (pathname.isFile() || pathname.isDirectory())
							&& pathname.canRead();
				} catch (SmbException e) {
					// TODO Auto-generated catch block
					return false;
				}
			}
		}

		MyDirFilter dirFilter = new MyDirFilter();
		SmbFile dir = (SmbFile) curPath.pop();
		try {
			@SuppressWarnings("unused")
			SmbFile[] subDirs = dir.listFiles(dirFilter);
		} catch (SmbException e) {
			// TODO Auto-generated catch block
			return false;
		}
		return true;

	}

	public String HtmlToTextGb2312(String inputString) {
		String htmlStr = inputString; // ��html��ǩ���ַ�
		String textStr = "";
		Pattern p_script;
		Matcher m_script;
		Pattern p_style;
		Matcher m_style;
		Pattern p_html;
		Matcher m_html;
		Pattern p_houhtml;
		Matcher m_houhtml;
		Pattern p_spe;
		Matcher m_spe;
		Pattern p_blank;
		Matcher m_blank;
		Pattern p_table;
		Matcher m_table;
		Pattern p_enter;
		Matcher m_enter;

		try {
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
			// ����script��������ʽ.
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
			// ����style��������ʽ.
			String regEx_html = "<[^>]+>";
			// ����HTML��ǩ��������ʽ
			String regEx_houhtml = "/[^>]+>";
			// ����HTML��ǩ��������ʽ
			String regEx_spe = "\\&[^;]+;";
			// ���������ŵ�������ʽ
			String regEx_blank = " +";
			// �������ո��������ʽ
			String regEx_table = "\t+";
			// �������Ʊ���������ʽ
			String regEx_enter = "\n+";
			// �������س���������ʽ

			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll(""); // ����script��ǩ

			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); // ����style��ǩ

			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // ����html��ǩ

			p_houhtml = Pattern
					.compile(regEx_houhtml, Pattern.CASE_INSENSITIVE);
			m_houhtml = p_houhtml.matcher(htmlStr);
			htmlStr = m_houhtml.replaceAll(""); // ����html��ǩ

			p_spe = Pattern.compile(regEx_spe, Pattern.CASE_INSENSITIVE);
			m_spe = p_spe.matcher(htmlStr);
			htmlStr = m_spe.replaceAll(""); // ����������

			p_blank = Pattern.compile(regEx_blank, Pattern.CASE_INSENSITIVE);
			m_blank = p_blank.matcher(htmlStr);
			htmlStr = m_blank.replaceAll(" "); // ���˹��Ŀո�

			p_table = Pattern.compile(regEx_table, Pattern.CASE_INSENSITIVE);
			m_table = p_table.matcher(htmlStr);
			htmlStr = m_table.replaceAll(" "); // ���˹����Ʊ��

			p_enter = Pattern.compile(regEx_enter, Pattern.CASE_INSENSITIVE);
			m_enter = p_enter.matcher(htmlStr);
			htmlStr = m_enter.replaceAll(" "); // ���˹����Ʊ��

			textStr = htmlStr;

		} catch (Exception e) {
			System.err.println("Html2Text: " + e.getMessage());
		}

		return textStr;
	}

	public static void CreateTxt(String rootPath, String content, String name) {
		try {
			File file = new File(rootPath);
			if (!file.exists()) {
				file.mkdirs();
			}
			File f = new File(rootPath + "/" + name);
			f.createNewFile();// 不存在则创建
            FileIO.writeTxtFile(content, f);
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public static boolean CreateFile(String rootPath, String content) {
		boolean flag = false;
		String path = rootPath.substring(0, rootPath.lastIndexOf("/"));
		File pathf = new File(path);
		pathf.mkdirs();
		try {
			File f = new File(rootPath);
			f.createNewFile();// 不存在则创建
			FileOutputStream fos = new FileOutputStream(rootPath);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
			BufferedWriter bw = new BufferedWriter(osw);
			bw.write(content);
			bw.flush();
			bw.close();
			osw.close();
			fos.close();
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
    public static File CreateNewFile(String rootPath) {
        String path = rootPath.substring(0, rootPath.lastIndexOf("/"));
        File pathf = new File(path);
        pathf.mkdirs();
        try {
            File f = new File(rootPath);
            f.createNewFile();// 不存在则创建
            return f;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	public static boolean writeTxtFile(String content, File fileName)
			throws Exception {
		boolean flag = false;
		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			fw = new FileWriter(fileName, true);
			pw = new PrintWriter(fw);
			pw.println(content);
			pw.close();
			fw.close();
			flag = true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {

		}
		return flag;
	}

}