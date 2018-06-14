package com.tbs.tbsmis.util;

import android.annotation.SuppressLint;
import android.content.Context;

import com.tbs.cbs.CBSInterpret;
import com.tbs.ini.IniFile;
import com.tbs.tbsmis.app.AppContext;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.file.FileInfo;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串操作工具包
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class StringUtils {

	@SuppressLint("SimpleDateFormat")
	public static String getDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.format(new Date());
	}

	@SuppressLint("SimpleDateFormat")
	public static String getDateTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		return formatter.format(new Date());
	}

	@SuppressLint("SimpleDateFormat")
	public static String getTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
		return formatter.format(new Date());
	}

	private static final Pattern emailer = Pattern
			.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

	private static final ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
		@SuppressLint("SimpleDateFormat")
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	private static final ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
		@SuppressLint("SimpleDateFormat")
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};

	/**
	 * 将字符串转位日期类型
	 * 
	 * @param sdate
	 * @return
	 */
	public static Date toDate(String sdate) {
		try {
			return StringUtils.dateFormater.get().parse(sdate);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取13位当前时间 时间戳
	 */
	public static int getNowTimeStamp() {
		Date date = new Date();
		long time = date.getTime();
		// mysq 时间戳只有10位 要做处理
		String dateline = time + "";
		dateline = dateline.substring(0, 10);
		return Integer.parseInt(dateline);
	}

	/**
	 * 将时间戳日期类型
	 * 
	 * @param sdate
	 * @return
	 */
	public static String LongtoDate(String sdate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sd = sdf.format(new Date(Long.parseLong(sdate)));
		return sd;
	}

	/**
	 * 以友好的方式显示时间
	 * 
	 * @param sdate
	 * @return
	 */
	public static String friendly_time(String sdate) {
		Date time = StringUtils.toDate(sdate);
		if (time == null) {
			return "Unknown";
		}
		String ftime = "";
		Calendar cal = Calendar.getInstance();

		// 判断是否是同一天
		String curDate = StringUtils.dateFormater2.get().format(cal.getTime());
		String paramDate = StringUtils.dateFormater2.get().format(time);
		if (curDate.equals(paramDate)) {
			int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
			if (hour == 0)
				ftime = Math.max(
						(cal.getTimeInMillis() - time.getTime()) / 60000, 1)
						+ "分钟前";
			else
				ftime = hour + "小时前";
			return ftime;
		}

		long lt = time.getTime() / 86400000;
		long ct = cal.getTimeInMillis() / 86400000;
		int days = (int) (ct - lt);
		if (days == 0) {
			int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
			if (hour == 0)
				ftime = Math.max(
						(cal.getTimeInMillis() - time.getTime()) / 60000, 1)
						+ "分钟前";
			else
				ftime = hour + "小时前";
		} else if (days == 1) {
			ftime = "昨天";
		} else if (days == 2) {
			ftime = "前天";
		} else if (days > 2 && days <= 3) {
			ftime = days + "天前";
		} else if (days > 3) {
			ftime = StringUtils.dateFormater2.get().format(time);
		}
		return ftime;
	}

	public static boolean compareToStrIng(String str1, String str2, int tag) {
		char str1char = str1.charAt(tag);
		char str2char = str2.charAt(tag);
		if ('A' < str1char && str1char < 'Z')
			str1char += 32;
		if ('A' < str2char && str2char < 'Z')
			str2char += 32;
		if (str1char > str2char)
			return true;
		else {
			if (str1char < str2char)
				return false;
			else {
				if (tag + 1 < str1.length())
					return true;
				if (tag + 1 < str2.length())
					return false;
				return StringUtils.compareToStr(str1.substring(tag + 1),
						str2.substring(tag + 1), tag + 1);

			}

		}

	}

	public static boolean compareToStr(String str1, String str2, int tag) {
		// int max = (str1.length() < str2.length() ? str1.length() : str2
		// .length());// 取两个字符串相对较小的长
		if (str1.charAt(tag) > str2.charAt(tag))
			return true;
		else {
			if (str1.charAt(tag) < str2.charAt(tag))
				return false;
			else {
				if (tag + 1 < str1.length())
					return true;
				if (tag + 1 < str2.length())
					return false;
				return StringUtils.compareToStr(str1.substring(tag + 1),
						str2.substring(tag + 1), tag + 1);
			}

		}
	}

	/***
	 * 区分大小写的字符串排序
	 * 
	 * @param str
	 */
	public void strOrder(String[] str) {
		for (int i = 0; i < str.length; i++) {
			for (int t = i; t < str.length; t++) {
				if (StringUtils.compareToStr(str[i], str[t], 0)) {
					String temp = str[i];
					str[i] = str[t];
					str[t] = temp;
				}
			}
		}
	}

	/***
	 * 不区分大小写的字符串排序
	 * 
	 * @param str
	 */
	public static ArrayList<FileInfo> strOrderIng(ArrayList<FileInfo> str) {
		for (int i = 0; i < str.size(); i++) {
			for (int t = i; t < str.size(); t++) {
				if (StringUtils.compareToStrIng(str.get(t).fileName, str.get(i).fileName, 0)) {
					FileInfo temp = new FileInfo();
					temp.IsDir = str.get(i).IsDir;
					temp.fileName = str.get(i).fileName;
					str.get(i).fileName = str.get(t).fileName;
					str.get(i).IsDir = str.get(t).IsDir;
					str.get(t).IsDir = temp.IsDir;
					str.get(t).fileName = temp.fileName;
				}
			}
		}
		return str;
	}

	/**
	 * 判断给定字符串时间是否为今日
	 * 
	 * @param sdate
	 * @return boolean
	 */
	public static boolean isToday(String sdate) {
		boolean b = false;
		Date time = StringUtils.toDate(sdate);
		Date today = new Date();
		if (time != null) {
			String nowDate = StringUtils.dateFormater2.get().format(today);
			String timeDate = StringUtils.dateFormater2.get().format(time);
			if (nowDate.equals(timeDate)) {
				b = true;
			}
		}
		return b;
	}

	/**
	 * 判断当前是否在指定时间段内
	 * 
	 * @param nowTime
	 * @return boolean
	 */
	@SuppressLint("SimpleDateFormat")
	public static boolean isInTimer(String nowTime, String starTime,
			String endTime) {
		boolean flag = false;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			Date dt1 = df.parse(nowTime);
			Date dt2 = df.parse(starTime);
			Date dt3 = df.parse(endTime);
            flag = dt1.getTime() >= dt2.getTime()
                    && dt1.getTime() <= dt3.getTime();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return flag;
	}
    /**
     * 判断当前是否在指定时间段内
     *
     * @param starTime
     * @return boolean
     */
    @SuppressLint("SimpleDateFormat")
    public static int isInTimerFormat(String starTime, String endTime) {
        int flag = 0;
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date dt2 = df1.parse(starTime);
            Date dt3 = null;
            if(endTime.contains(":"))
                dt3 = df1.parse(endTime);
            else
                dt3 = df2.parse(endTime);
            if (dt2.getTime() > dt3.getTime()) {
                flag = -1;
            } else if (dt2.getTime() == dt3.getTime()) {
                flag = 1;
            } else {
                flag = 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return flag;
    }
	/**
	 * 判断当前是否在指定时间段内
	 * 
	 * @param starTime
	 * @return boolean
	 */
	@SuppressLint("SimpleDateFormat")
	public static int isInTimer(String starTime, String endTime) {
		int flag = 0;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
		try {
			Date dt2 = df.parse(starTime);
			Date dt3 = df.parse(endTime);
			if (dt2.getTime() > dt3.getTime()) {
				flag = -1;
			} else if (dt2.getTime() == dt3.getTime()) {
				flag = 1;
			} else {
				flag = 0;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return flag;
	}
    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = StringUtils.unitFormat(minute) + ":" + StringUtils.unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = StringUtils.unitFormat(hour) + ":" + StringUtils.unitFormat(minute) + ":" + StringUtils.unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }
    /**
	 * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
	 * 
	 * @param input
	 * @return boolean
	 */
	public static boolean isEmpty(String input) {
		if (input == null || "".equals(input) || input.equals("null"))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是不是一个合法的电子邮件地址
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		if (email == null || email.trim().length() == 0)
			return false;
		return StringUtils.emailer.matcher(email).matches();
	}

	// 2
	public static boolean checkEmail(String email) {

		Pattern pattern = Pattern
				.compile("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
		Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

	/*
	 * 判断验证码是否为六位数字
	 */
	public static boolean checkVerify(String verifyNum) {

		Pattern pattern = Pattern.compile("\\d{6}");
		Matcher matcher = pattern.matcher(verifyNum);
        return matcher.matches();
    }

	/*
	 * 判断密码格式 6-14位 区分大小写
	 */
	public static boolean checkPassWord(String password) {

//		Pattern pattern = Pattern
//				.compile("^[\\@A-Za-z0-9\\!\\#\\$\\%\\^\\&\\*\\.\\~]{6,14}$");
        Pattern pattern = Pattern
				.compile("^([A-Z]|[a-z]|[0-9]|[`~!@#$%^&*()+=|{}':;',\\\\\\\\[\\\\\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]){6,20}$");
		Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

	/*
	 * 判断是否为中英文，数字，下划线或者邮箱
	 */
	public static boolean checkUserName(String username) {

		Pattern pattern = Pattern.compile("^[\\w\\u4e00-\\u9fa5]+$");
		Matcher matcher = pattern.matcher(username);
		if (matcher.matches()) {
			return true;
		} else if (StringUtils.checkEmail(username)) {
			return true;
		}
		return false;
	}

	/*
	 * 判断是否是一个合法的电话号码
	 */
	public static boolean checkPhone(String phone) {
//		Pattern pattern = Pattern
//				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,0-9]))\\d{8}$");
        Pattern pattern = Pattern
				.compile("^1[3|4|5|7|8][0-9]{9}$");
		Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

	// 去掉IP字符串前后所有的空格
	public static String trimSpaces(String IP) {
		while (IP.startsWith(" ")) {
			IP = IP.substring(1, IP.length()).trim();
		}
		while (IP.endsWith(" ")) {
			IP = IP.substring(0, IP.length() - 1).trim();
		}
		return IP;
	}

	/**
	 * 判断是不是一个合法的IP地址
	 * 
	 * @author
	 * @param IP
	 * @return
	 */

	public static boolean isIp(String IP) {// 判断是否是一个IP
		boolean b = false;
		IP = StringUtils.trimSpaces(IP);
		if (IP.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
			String s[] = IP.split("\\.");
			if (Integer.parseInt(s[0]) < 255)
				if (Integer.parseInt(s[1]) < 255)
					if (Integer.parseInt(s[2]) < 255)
						if (Integer.parseInt(s[3]) < 255)
							b = true;
		} else if (IP
				.matches("[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\\.?")) {
			b = true;
		}
		return b;
	}

	/**
	 * 判断是不是一个合法的url地址(主要是开头问题 专用)
	 *
      ;uy  1e56
	 * @param url
	 * @return
	 */
	public static String isUrl(String url, String baseUrl, String resName) {
        if(isEmpty(url)){
            return "";
        }else {
            if (url.lastIndexOf(":") > 0) {
                if (url.substring(0, url.indexOf(":")).toLowerCase().contains ("http")) {
                    if (url.substring(url.lastIndexOf("?") + 1).equalsIgnoreCase(
                            "resname=")
                            || url.substring(url.lastIndexOf("&") + 1)
                            .equalsIgnoreCase("resname=")) {
                        return url + resName;
                    } else {
                        return url;
                    }
                } else if (url.substring(0, url.indexOf(":"))
                        .equalsIgnoreCase("js")) {
                    return "javascript" + url.substring(url.indexOf(":"));
                } else if (url.substring(0, url.indexOf(":")).equalsIgnoreCase(
                        "tbs")) {
                    return url.substring(url.indexOf(":") + 1);
                } else if (url.substring(0, url.indexOf(":")).equalsIgnoreCase(
                        "file")) {
                    return url;
                } else {
                    if (url.substring(url.lastIndexOf("?") + 1).equalsIgnoreCase(
                            "resname=")
                            || url.substring(url.lastIndexOf("&") + 1)
                            .equalsIgnoreCase("resname=")) {
                        return baseUrl + url + resName;
                    } else {
                        return baseUrl + url;
                    }
                }
            } else {
                Context c = AppContext.getInstance();
                String webRoot = UIHelper.getShareperference(c, constants.SAVE_INFORMATION,
                        "Path", "");
                if (webRoot.endsWith("/") == false) {
                    webRoot += "/";
                }
                IniFile m_iniFileIO = new IniFile();
                String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
                String appTestFile = webRoot
                        + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                        constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
                String userIni = appTestFile;
                if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                        "LoginType", "0", (byte) 0)) == 1) {
                    String dataPath = c.getFilesDir().getParentFile()
                            .getAbsolutePath();
                    if (dataPath.endsWith("/") == false) {
                        dataPath = dataPath + "/";
                    }
                    userIni = dataPath + "TbsApp.ini";
                }
                if (Integer.parseInt(m_iniFileIO.getIniString(userIni,
                        "SERVICE", "serverMarks", "4", (byte) 0)) == 4) {
                    baseUrl = "file://" + webRoot + "Web";
                    if (url.substring(url.lastIndexOf("?") + 1).equalsIgnoreCase(
                            "resname=")
                            || url.substring(url.lastIndexOf("&") + 1)
                            .equalsIgnoreCase("resname=")) {
                        url = baseUrl + url + resName;
                    } else {
                        url = baseUrl + url;
                    }
                    url = url.substring(7);
                    String relatPath = webRoot + "Web/";
                    CBSInterpret interpret = new CBSInterpret();
                    interpret.initGlobal(webRoot + "TbsWeb.ini", webRoot);
                    String interpretFile = interpret.Interpret(url.substring(relatPath.length() - 1), "GET", "", null, 0);

                    //System.out.println("url.substring(relatPath.length()-1) = "+url.substring(relatPath.length()-1));
                    String FileName = FileUtils.getFileName(interpretFile);
                    String FilePath = FileUtils.getPath(url);
                    File file = new File(FilePath + FileName);
                    try {
                        file.createNewFile();
                        File srcfile = new File(interpretFile);
                        FileUtils.copyFileTo(srcfile, file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    FileUtils.deleteFile(interpretFile);
                    int tmpCount = UIHelper.getShareperference(c, "tmp", "tmpCount", 0);
                    UIHelper.setSharePerference(c, "tmp", "tmpFile" + tmpCount, FilePath + FileName);
                    UIHelper.setSharePerference(c, "tmp", "tmpCount", tmpCount + 1);
                    return "file://" + FilePath + FileName;
                }
                if (url.substring(url.lastIndexOf("?") + 1).equalsIgnoreCase(
                        "resname=")
                        || url.substring(url.lastIndexOf("&") + 1)
                        .equalsIgnoreCase("resname=")) {

                    return baseUrl + url + resName;
                } else {
                    return baseUrl + url;
                }
            }
        }
	}

	/**
	 * 字符串转整数
	 * 
	 * @param str
	 * @param defValue
	 * @return
	 */
	public static int toInt(String str, int defValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
		}
		return defValue;
	}

	/**
	 * 对象转整数
	 * 
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static int toInt(Object obj) {
		if (obj == null)
			return 0;
		return StringUtils.toInt(obj.toString(), 0);
	}

	/**
	 * 对象转整数
	 * 
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static long toLong(String obj) {
		try {
			return Long.parseLong(obj);
		} catch (Exception e) {
		}
		return 0;
	}

	/**
	 * 字符串转布尔值
	 * 
	 * @param b
	 * @return 转换异常返回 false
	 */
	public static boolean toBool(String b) {
		try {
			return Boolean.parseBoolean(b);
		} catch (Exception e) {
		}
		return false;
	}

	public static String getMD5(String val) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(val.getBytes());
		byte[] m = md5.digest();// 加密
		String sb = new String();
		for (int i = 0; i < m.length; i++) {
			// sb.append(m[i]);
			int b = 0xFF & m[i];
			// if it is a single digit, make sure it have 0 in front (proper
			// padding)
			if (b <= 0xF)
				sb += "0";
			// add number to string
			sb += Integer.toHexString(b);
		}
		return sb.toUpperCase();
	}

    public static String getHost(String url) {
        //使用正则表达式过滤，
        String re = "((http|ftp|https)://)(([a-zA-Z0-9._-]+)|([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}))(([a-zA-Z]{2,6})|(:[0-9]{1,4})?)";
        String str = "";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(re);
        // 忽略大小写的写法
        // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);
        //若url==http://127.0.0.1:9040或www.baidu.com的，正则表达式表示匹配
        if (matcher.matches()) {
            str = url;
        } else {
            String[] split2 = url.split(re);
            if (split2.length > 1) {
                String substring = url.substring(0, url.length() - split2[1].length());
                str = substring;
            }
        }
        return str;
    }
}
