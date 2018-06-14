package com.tbs.tbsmis.util;

import com.tbs.tbsmis.app.AppException;
import com.tbs.tbsmis.update.Updateapk;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class ApiClient {
	private static final int TIMEOUT_CONNECTION = 20000;
	private static final int TIMEOUT_SOCKET = 150000;
	private static final int LOCAL_TIMEOUT_SOCKET = 20000;
	private static final int RETRY_TIME = 2;

	public ApiClient() {
	}

	// app客户标示
	// private static String getUserAgent(Context appContext) {
	// if (appUserAgent == null || appUserAgent == "") {
	// StringBuilder ua = new StringBuilder("OSChina.NET");
	// ua.append('/' + appContext.getPackageInfo().versionName + '_'
	// + appContext.getPackageInfo().versionCode);// App版本
	// ua.append("/Android");// 手机系统平台
	// ua.append("/" + android.os.Build.VERSION.RELEASE);// 手机系统版本
	// ua.append("/" + android.os.Build.MODEL); // 手机型号
	// ua.append("/" + appContext.getAppId());// 客户端唯一标识
	// appUserAgent = ua.toString();
	// }
	// return appUserAgent;
	// }
	private static GetMethod local_getHttpGet(String url) {
		GetMethod httpGet = new GetMethod(url);
		// 设置 请求超时时间
		httpGet.getParams().setSoTimeout(ApiClient.LOCAL_TIMEOUT_SOCKET);
		// httpGet.setRequestHeader("Host", URLs.HOST);
		httpGet.setRequestHeader("Connection", "Keep-Alive");
		// httpGet.setRequestHeader("Cookie", cookie);
		// httpGet.setRequestHeader("User-Agent", userAgent);
		return httpGet;
	}

	private static HttpClient local_getHttpClient() {
		HttpClient httpClient = new HttpClient();
		// 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
		// httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		// 设置 默认的超时重试处理策略
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		// 设置 连接超时时间
		httpClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(ApiClient.TIMEOUT_CONNECTION);
		// 设置 读数据超时时间
		httpClient.getHttpConnectionManager().getParams()
				.setSoTimeout(ApiClient.LOCAL_TIMEOUT_SOCKET);
		// 设置 字符集
		httpClient.getParams().setContentCharset("utf-8");
		return httpClient;
	}

	private static GetMethod getHttpGet(String url) {
		GetMethod httpGet = new GetMethod(url);
		// 设置 请求超时时间
		httpGet.getParams().setSoTimeout(ApiClient.TIMEOUT_SOCKET);
		// httpGet.setRequestHeader("Host", URLs.HOST);
		httpGet.setRequestHeader("Connection", "Keep-Alive");
		// httpGet.setRequestHeader("Cookie", cookie);
		// httpGet.setRequestHeader("User-Agent", userAgent);
		return httpGet;
	}

	private static HttpClient getHttpClient() {
		HttpClient httpClient = new HttpClient();
		// 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
		// httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		// 设置 默认的超时重试处理策略
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		// 设置 连接超时时间
		httpClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(ApiClient.TIMEOUT_CONNECTION);
		// 设置 读数据超时时间
		httpClient.getHttpConnectionManager().getParams()
				.setSoTimeout(ApiClient.TIMEOUT_SOCKET);
		// 设置 字符集
		httpClient.getParams().setContentCharset("utf-8");
		return httpClient;
	}

	/**
	 * 本地get请求URL
	 * 
	 * @param url
	 * @throws AppException
	 */
	public String local_http_get(String url) throws AppException {
		// System.out.println("get_url==> "+url);
		// String cookie = getCookie(appContext);
		// String userAgent = getUserAgent(appContext);

		HttpClient httpClient = null;
		GetMethod httpGet = null;
		String responseBody = "";
		int time = 0;
		do {
			try {
				httpClient = ApiClient.local_getHttpClient();
				httpGet = ApiClient.local_getHttpGet(url);
				int statusCode = httpClient.executeMethod(httpGet);
				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				}
				responseBody = httpGet.getResponseBodyAsString();
//				BufferedReader br = new BufferedReader(new InputStreamReader(
//						inputStream, "UTF-8"));
//				StringBuffer stringBuffer = new StringBuffer();
//				String str = "";
//				while ((str = br.readLine()) != null) {
//					stringBuffer.append(str);
//				}
//				responseBody = stringBuffer.toString();
				break;
			} catch (HttpException e) {
				time++;
				if (time < ApiClient.RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if (time < ApiClient.RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 释放连接
				httpGet.releaseConnection();
				httpClient = null;
			}
		} while (time < ApiClient.RETRY_TIME);

		return responseBody;
	}

	/**
	 * get请求URL
	 * 
	 * @param url
	 * @throws AppException
	 */
	public String http_get(String url) throws AppException {
		// System.out.println("get_url==> "+url);
		// String cookie = getCookie(appContext);
		// String userAgent = getUserAgent(appContext);

		HttpClient httpClient = null;
		GetMethod httpGet = null;
		String responseBody = "";
		int time = 0;
		do {
			try {
				httpClient = ApiClient.getHttpClient();
				httpGet = ApiClient.getHttpGet(url);
				int statusCode = httpClient.executeMethod(httpGet);
				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				}
				InputStream inputStream = httpGet.getResponseBodyAsStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						inputStream, "UTF-8"));
				StringBuffer stringBuffer = new StringBuffer();
				String str = "";
                int i = 0;
                while ((str = br.readLine()) != null) {
                    ++i;
                    if (i > 1) {
                        stringBuffer.append("\r\n");
                        stringBuffer.append(str);
                    } else {
                        stringBuffer.append(str);
                    }
                }
				responseBody = stringBuffer.toString();

				break;
			} catch (HttpException e) {
				time++;
				if (time < ApiClient.RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if (time < ApiClient.RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 释放连接
				httpGet.releaseConnection();
				httpClient = null;
			}
		} while (time < ApiClient.RETRY_TIME);

		return responseBody;
	}

	/**
	 * get请求URL
	 * 
	 * @param url
	 * @throws AppException
	 */
	public static InputStream http_get_InputStream(String url)
			throws AppException {
		// System.out.println("get_url==> "+url);
		// String cookie = getCookie(appContext);
		// String userAgent = getUserAgent(appContext);

		HttpClient httpClient = null;
		GetMethod httpGet = null;
		String responseBody = "";
		int time = 0;
		do {
			try {
				httpClient = ApiClient.local_getHttpClient();
				httpGet = ApiClient.local_getHttpGet(url);
				int statusCode = httpClient.executeMethod(httpGet);
				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				}
				InputStream inputStream = httpGet.getResponseBodyAsStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						inputStream, "UTF-8"));
				StringBuffer stringBuffer = new StringBuffer();
				String str = "";
                int i = 0;
                while ((str = br.readLine()) != null) {
                    ++i;
                    if (i > 1) {
                        stringBuffer.append("\r\n");
                        stringBuffer.append(str);
                    } else {
                        stringBuffer.append(str);
                    }
                }
				responseBody = stringBuffer.toString();
				 System.out.println(responseBody);
				break;
			} catch (HttpException e) {
				time++;
				if (time < ApiClient.RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if (time < ApiClient.RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 释放连接
				httpGet.releaseConnection();
				httpClient = null;
			}
		} while (time < ApiClient.RETRY_TIME);

		return new ByteArrayInputStream(responseBody.getBytes());
	}

	/**
	 * 检查版本更新
	 * 
	 * @param Url
	 * @return
	 */
	public static Updateapk checkVersion(String Url) throws AppException {
		try {
			return Updateapk.parse(ApiClient.http_get_InputStream(Url));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	public String[] navigationmsg(String Url) throws AppException {
		String[] menus = null;
		String result = null;
		try {
			result = this.local_http_get(Url);
            //System.out.println("result = "+result);
			if (!result.isEmpty()) {
				if (result.indexOf(",") > 0) {
					menus = result.split(",");
					return menus;
				}
			}
		} catch (Exception e) {
			result = null;
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
		return null;
	}

	public String[] DownloadData(String Url) throws AppException {
		String[] menus = null;
		String result = null;
		try {
			result = this.http_get(Url);
			if (!StringUtils.isEmpty(result)) {
				// System.out.println(result);
				if (result.indexOf(":") > 0) {
					if (result.substring(0, result.indexOf(":") + 1)
							.equalsIgnoreCase("offline:")) {
						result = result.substring(8);
						// System.out.println(result);
						menus = result.split(";");
						return menus;
					}
				}
				return null;
			}
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
		return null;
	}

	public String[][] DbMsg(String Url) throws AppException {
		String result = null;
		String[][] dbmsg = null;
		String[] menus = null;
		try {
			result = this.local_http_get(Url);
//            System.out.println("Url = " + Url);
//			System.out.println("result = " + result);
			if (!result.isEmpty()) {
				if (result.indexOf(";") > 0) {
					menus = result.split(";");
					dbmsg = new String[menus.length][];
					for (int i = 0; i < menus.length; i++) {
						String[] media = menus[i].split(",");
						dbmsg[i] = new String[media.length];
						for (int j = 0; j < media.length; j++) {
							dbmsg[i][j] = media[j];
						}
					}
				}
				return dbmsg;
			}
		} catch (Exception e) {
			result = null;
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
		return null;
	}

	public static boolean SrvCheck(String SrvUrl) {

		URL url = null;
		try {
			url = new URL(SrvUrl);
			InputStream in = url.openStream();
			in.close();
			return true;
		} catch (IOException e) {
			return false;
		}

	}

	public boolean NetCheck(String NetUrl) {
		URL url = null;
		try {
			url = new URL(NetUrl);
			InputStream in = url.openStream();
			in.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	// public void htmlGet(String Url) {
	// try {
	// result = sendGet(Url);
	// } catch (ClientProtocolException e) {
	// // TODO Auto-generated catch block
	// // System.err.println(e);
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// // System.err.println(e);
	// e.printStackTrace();
	// }
	// FileIO fileIO = new FileIO();
	// result = fileIO.HtmlToTextGb2312(result);
	// System.out.println(result);
	// String[] HtmlMsg = null;
	// HtmlMsg = result.split(" ");
	// // HtmlMsg = HtmlMsg1[1].split(" ");
	// for (int i = 0; i < HtmlMsg.length; i++) {
	// System.out.println(HtmlMsg[i]);
	// }
	// }
}