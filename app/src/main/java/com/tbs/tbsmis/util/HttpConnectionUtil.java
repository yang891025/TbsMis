package com.tbs.tbsmis.util;

import android.content.Context;
import android.util.Log;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpConnectionUtil
{

    public enum HttpMethod
    {
        GET, POST
    }

    private static final String TAG = "HttpConnectionUtil";
    private static boolean unupdata;

    public static boolean isUnupdata() {
        return HttpConnectionUtil.unupdata;
    }

    public static void setUnupdata(boolean Unupdata) {
        HttpConnectionUtil.unupdata = Unupdata;
    }

    public void asyncConnect(String url, HttpConnectionUtil.HttpMethod method,
                             Context context) throws Exception {
        this.asyncConnect(url, null, method, context);

    }

    public void asyncConnect(String url,
                             String filePath) throws Exception {
        this.downFile(url, filePath);
    }

    public String syncConnect(String url, HttpConnectionUtil.HttpMethod method,
                              Context context, String coding) {
        return this.syncConnect(url, null, method, context, coding);
    }

    // �첽����
    public String asyncConnect(String url,
                               Map<String, String> params, HttpConnectionUtil.HttpMethod method,
                               Context context) {
        return this.syncConnect(url, params, method, context);
    }

    // �첽����
    public String asyncConnect(String url,
                               Map<String, String> params, String file, Context context) {
        HttpConnectionUtil.setUnupdata(true);
        return this.syncConnect(url, params, file, context);
    }

    // ͬ������
    public String syncConnect(String url,
                              Map<String, String> params, HttpConnectionUtil.HttpMethod method,
                              Context context) {

        String json = null;
        BufferedReader reader = null;
        String sessionId = "";
        try {
            HttpClient client = new DefaultHttpClient();
            HttpUriRequest request = this.getRequest(url, params, method, context);
            HttpResponse response = client.execute(request);
            List<Cookie> cookie = ((AbstractHttpClient) client)
                    .getCookieStore().getCookies();
            for (int i = 0; i < cookie.size(); i++) {
                if ("JSESSIONID".equalsIgnoreCase(cookie.get(i).getName())
                        || "CGISESSIONID".equalsIgnoreCase(cookie.get(i)
                        .getName())) {
                    sessionId = cookie.get(i).getValue();
                }
                Log.d(HttpConnectionUtil.TAG, "SessionId:" + sessionId);
            }
            //
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                reader = new BufferedReader(new InputStreamReader(response
                        .getEntity().getContent(), "utf-8"));
                StringBuilder sb = new StringBuilder();
                String s = null;
                int i = 0;
                while ((s = reader.readLine()) != null) {

                    ++i;
                    //Log.d(HttpConnectionUtil.TAG, "readLine:" + i);
                    if (i > 1) {
                        sb.append("\r\n");
                        sb.append(s);
                    } else {
                        sb.append(s);
                    }
                }
                json = sb.toString();
            }
        } catch (ClientProtocolException e) {
            Log.e("HttpConnectionUtil", e.getMessage(), e);
            return null;
        } catch (IOException e) {
            Log.e("HttpConnectionUtil", e.getMessage(), e);
            return null;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                // ignore me
            }
        }
        // constants.SessionId = sessionId;
        if (!StringUtils.isEmpty(sessionId)) {
            UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM,
                    "SessionId", sessionId);
        }
        return json;// callback.execute(json, sessionId, context);
    }

    // 指定编码类型
    public String syncConnect(String url,
                              Map<String, String> params, HttpConnectionUtil.HttpMethod method,
                              Context context, String coding) {

        String json = null;
        BufferedReader reader = null;
        String sessionId = "";
        try {
            HttpClient client = new DefaultHttpClient();
            HttpUriRequest request = this.getRequest(url, params, method, context);
            HttpResponse response = client.execute(request);
            // ���sessionId
            List<Cookie> cookie = ((AbstractHttpClient) client)
                    .getCookieStore().getCookies();
            for (int i = 0; i < cookie.size(); i++) {
                if ("JSESSIONID".equalsIgnoreCase(cookie.get(i).getName())
                        || "CGISESSIONID".equalsIgnoreCase(cookie.get(i)
                        .getName())) {
                    sessionId = cookie.get(i).getValue();
                }
                Log.d(HttpConnectionUtil.TAG, "SessionId:" + sessionId);
            }
            // ��÷���ֵ
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                reader = new BufferedReader(new InputStreamReader(response
                        .getEntity().getContent(), coding));
                StringBuilder sb = new StringBuilder();
                String s = null;
                int i = 0;
                while ((s = reader.readLine()) != null) {
                    ++i;
                    if (i > 1) {
                        sb.append("\r\n");
                        sb.append(s);
                    } else {
                        sb.append(s);
                    }
                }
                json = sb.toString();
            }
        } catch (ClientProtocolException e) {
            Log.e("HttpConnectionUtil", e.getMessage(), e);
            return null;
        } catch (IOException e) {
            Log.e("HttpConnectionUtil", e.getMessage(), e);
            return null;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                // ignore me
            }
        }
        // constants.SessionId = sessionId;
        if (!StringUtils.isEmpty(sessionId)) {
            UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM,
                    "SessionId", sessionId);
        }
        return json;// callback.execute(json, sessionId, context);
    }

    @SuppressWarnings("deprecation")
    private HttpUriRequest getRequest(String url, Map<String, String> params,
                                      HttpConnectionUtil.HttpMethod method, Context context) {

        if (method.equals(HttpConnectionUtil.HttpMethod.POST)) {// post�ύ
            List<NameValuePair> listParams = new ArrayList<NameValuePair>();
            if (params != null) {
                for (String name : params.keySet()) {
                    listParams.add(new BasicNameValuePair(name, params
                            .get(name)));
                }
            }
            try {
                Log.d(HttpConnectionUtil.TAG,
                        "getRequest SessionId:"
                                + UIHelper.getShareperference(context,
                                constants.SAVE_LOCALMSGNUM,
                                "SessionId", ""));
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
                        listParams, HTTP.UTF_8);
                HttpPost request = new HttpPost(url);
                request.setHeader(
                        "Cookie",
                        "JSESSIONID="
                                + UIHelper.getShareperference(context,
                                constants.SAVE_LOCALMSGNUM,
                                "SessionId", "")
                                + ";CGISESSIONID="
                                + UIHelper.getShareperference(context,
                                constants.SAVE_LOCALMSGNUM,
                                "SessionId", ""));
                request.setEntity(entity);
                return request;
            } catch (UnsupportedEncodingException e) {
                // Should not come here, ignore me.
                //throw new java.lang.RuntimeException(e.getMessage(), e);
                return null;
            }
        } else {// get�ύ
            if (url.indexOf("?") < 0) {
                url += "?";
            }
            if (params != null) {
                for (String name : params.keySet()) {
                    url += "&" + name + "="
                            + URLEncoder.encode(params.get(name));
                }
            }
            if(url.contains("?&")){
              url = url.substring(0,url.indexOf("&")) + url.substring(url.indexOf("&")+1);
            }
//             System.out.println("constants.verifyURL ="
//             + url);
            HttpGet request = new HttpGet(url);
            request.setHeader(
                    "Cookie",
                    "JSESSIONID="
                            + UIHelper
                            .getShareperference(context,
                                    constants.SAVE_LOCALMSGNUM,
                                    "SessionId", "")
                            + ";CGISESSIONID="
                            + UIHelper
                            .getShareperference(context,
                                    constants.SAVE_LOCALMSGNUM,
                                    "SessionId", ""));
            return request;
        }
    }

    // 增加文件传输属性
    @SuppressWarnings("deprecation")
    public String syncConnect(String urlstr, Map<String, String> params,
                              String localfile, Context context) {

        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "******";
        String result = null;

        FileInputStream fis = null;
        InputStream is = null;
        DataOutputStream dos = null;
        InputStreamReader isr = null;
        long startPos = 0;// 开始点
        long endPos = 0;// 结束点
        long compeleteSize = 0;// 完成度
        File uploadFile = new File(localfile);
        if (uploadFile.exists()) {
            endPos = uploadFile.length();
        }
        if (urlstr.indexOf("?") < 0) {
            urlstr += "?";
        }
        if (params != null) {
            for (String name : params.keySet()) {
               // System.out.println(name + "=" + params.get(name));
                urlstr += "&" + name + "="
                        + URLEncoder.encode(params.get(name));
            }
        }
        if(urlstr.contains("?&")){
            urlstr = urlstr.substring(0,urlstr.indexOf("&")) + urlstr.substring(urlstr.indexOf("&")+1);
        }
        //System.out.println("urlstr=" + urlstr);
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlstr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(constants.REQUEST_UPDATE_HEAD_MENU);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            connection.setRequestProperty("Range", "bytes="
                    + (startPos + compeleteSize) + "-" + endPos);
            // connection.setChunkedStreamingMode(0);
            dos = new DataOutputStream(connection.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + end);
            String fileName = URLEncoder.encode(
                    localfile.substring(localfile.lastIndexOf("/") + 1),
                    "utf-8");
            dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""
                    + fileName + "\"" + end);
            dos.writeBytes(end);

            fis = new FileInputStream(localfile);
            byte[] buffer = new byte[constants.UPLOAD_BUFFER]; // 8k
            int count = 0;
            while ((count = fis.read(buffer)) != -1) {
                // System.out.println(isUnupdata());
                if (HttpConnectionUtil.isUnupdata()) {
                    dos.write(buffer, 0, count);
                    compeleteSize += count;
                } else {
                    try {
                        if (null != fis) {
                            fis.close();
                        }
                        if (null != dos) {
                            dos.close();
                        }
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                    result = context.getResources().getString(
                            R.string.toast_upload_cancel);
                }
            }
            if (HttpConnectionUtil.isUnupdata()) {
                dos.writeBytes(end);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
                dos.flush();
                is = connection.getInputStream();
                isr = new InputStreamReader(is, "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                result = br.readLine();
            }

        } catch (Exception e) {
            e.printStackTrace();// 打印错误信息
            result = context.getResources().getString(
                    R.string.toast_uploaderro_cancel);
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                }
                if (null != dos) {
                    dos.close();
                }
                if (null != is) {
                    is.close();
                }
                if (null != isr) {
                    isr.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return result;
    }

    //下载文件接口

    /**
     * @param urlStr
     * @param path
     * @return -1:文件下载出错
     * 0:文件下载成功
     * 1:文件已经存在
     */
    public int downFile(String urlStr, String path) {
        InputStream inputStream = null;
        File file = null;
        OutputStream output = null;
        try {
            HttpURLConnection urlConn = null;
            URL url = new URL(urlStr);
            urlConn = (HttpURLConnection) url.openConnection();

            //urlConn.setRequestMethod("POST");
            urlConn.setConnectTimeout(5000);
            if (urlConn.getResponseCode() == 200) {
                inputStream = urlConn.getInputStream();
                file = FileIO.CreateNewFile(path);
                output = new FileOutputStream(file);
                byte[] buffer = new byte[4 * 1024];
                int length = 0;
                while ((length = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, length);
                }
                output.flush();
                if (file == null) {
                    return -1;
                }
            } else {
                return -1;
            }
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
            return -1;
        } catch (IOException e1) {
            e1.printStackTrace();
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
}
