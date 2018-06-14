package com.tbs.tbsmis.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.source.FileSourceActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 15-11-9.
 */
public class JsoupExam
{

    public static Elements DoJsoup(String url) throws IOException {

        Document doc = Jsoup.connect(url).get();
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");
        for (Element link : imports) {
            media.add(link);
        }
        return media;
    }

    public static Elements DoJsoup(File file) throws IOException {

        Document doc = Jsoup.parse(file, "gbk");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");
        for (Element link : imports) {
            media.add(link);
        }
        return media;
    }

    public static String getTitle(String url) throws IOException {
        URL Url = new URL(url);
        Document doc = Jsoup.parse(Url, 6 * 1000);
        return doc.title();
    }

    public static String getHtml(String url) throws IOException {
        URL Url = new URL(url);
        Document doc = Jsoup.parse(Url, 6 * 1000);
        return doc.html();
    }

    //    public static String  getCharset(String url) throws IOException {
//
//        URL Url = new URL(url);
//        Document doc = Jsoup.parse(Url,6*1000);
//        Elements elem = doc.select("meta[http-equiv=Content-Type]");
//        if(elem.isEmpty()){
//            elem = doc.select("meta[http-equiv=content-type]");
//        }
//        Iterator<Element> ito = elem.iterator();
//        while (ito.hasNext()){
//            Pattern p = Pattern.compile("(?<=charset=)(.+)(?=\")");
//            Matcher m = p.matcher(ito.next().toString());
//            if (m.find())
//                return m.group();
//        }
//        return "UTF-8";
//    }
    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width - 1) + ".";
        else
            return s;
    }

    /**
     * 获取html信息
     * 保存到本地
     * 并保存
     *
     * @param urlPath
     */
    public static void getHtmlSource(final String category, final String title, final String urlPath, final Context
            ac) {
        final int mId = urlPath.hashCode();
        final NotificationManager mNotificationManager = (NotificationManager) ac
                .getSystemService(Context.NOTIFICATION_SERVICE);
         final Notification.Builder builder = initNotifiction(title,ac);
        final Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    builder.setSmallIcon(android.R.drawable.stat_sys_download_done);
                    builder.setTicker(ac.getString(R.string.download_finished));
                    builder.setContentText(ac.getString(R.string.download_finished)); //消息内容
                    Intent intent = new Intent(ac, FileSourceActivity.class);
                    PendingIntent contentIntent = PendingIntent.getActivity(ac, 0, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(contentIntent);
//        mNotification.setLatestEventInfo(mContext, title, mContext.getString(R.string.download_finished),
//                mNotification.contentIntent);
                    mNotificationManager.notify(mId, builder.build());
                    Toast.makeText(ac, "下载成功", Toast.LENGTH_SHORT).show();
                } else if (msg.what == 2) {
                    Toast.makeText(ac, "已下载", Toast.LENGTH_SHORT).show();
                } else {
                    builder.setSmallIcon(android.R.drawable.stat_sys_download_done);
                    builder.setTicker(ac.getString(R.string.download_failed));
                    builder.setContentText(ac.getString(R.string.download_failed)); //消息内容
                    //mNotification.contentView.setProgressBar(R.id.notify_processbar, 100, 0, true);
                    mNotificationManager.notify(mId, builder.build());
                    mNotificationManager.cancel(mId);
                    Toast.makeText(ac, "下载失败", Toast.LENGTH_SHORT).show();
                }
            }
        };
        new Thread()
        {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    if (isHtmlSaven(category, urlPath, ac))
                        msg.what = 2;
                    else {
                        mNotificationManager.notify(mId, builder.build());
                        getHtml(category, title, urlPath, ac);
                        msg.what = 1;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = -1;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    private static void getHtml(String fileInfo, String title, String urlPath, Context context) {
        try {
            //System.out.println("fileInfo = "+fileInfo);
            JSONObject jsonObject = new JSONObject(fileInfo);
            String category = jsonObject.getString("category");
            String pic = jsonObject.getString("pic");
            String range = jsonObject.getString("range");
            String type = jsonObject.getString("type");
            Elements FileMents = DoJsoup(urlPath);
            HttpConnectionUtil connection = new HttpConnectionUtil();
            String webRoot = UIHelper.getStoragePath(context);
            String name = StringUtils.getTime();
            if (type.equalsIgnoreCase("考试"))
                webRoot += constants.SD_CARD_TBSFILE_PATH6 + "/" + type + "/" + range + "/" + name + "/";
            else
                webRoot += constants.SD_CARD_TBSFILE_PATH6 + "/" + type + "/" + range + "/" + category + "/" + name +
                        "/";
            //webRoot += constants.SD_CARD_TBSFILE_PATH6 + "/" + category + "/" + name + "/";
            for (int i = 0; i < FileMents.size(); i++) {
                String srcPath = FileMents.get(i).attr("abs:href");
                if (StringUtils.isEmpty(srcPath)) {
                    srcPath = FileMents.get(i).attr("abs:src");
                }
                String fileName = FileUtils.getFileName(srcPath);
                if (fileName.contains("?")) {
                    fileName = fileName.substring(0, fileName.indexOf("?"));
                }
                if (fileName.isEmpty()) {
                    continue;
                }
                //String relatePath = getRelatePath(srcPath);
                String savePath = webRoot + "src/" + fileName;
                FileIO.CreateNewFile(savePath);
                //System.out.println("srcPath"+ i +" = "+ srcPath);
                connection.downFile(srcPath, savePath);
            }
            //System.out.println("urlPath = "+ urlPath);
//            String relatePath = getRelatePath(urlPath);

            String savePath = name + ".html";
//            Document doc = Jsoup.connect(urlPath).timeout(30000).get();
//            String title = doc.title();
//            String html = getHtml(doc);
//            html = htmlJsoupUtil.modifyLink(html, "", getWebPath(urlPath) + "/" + getRelatePath
//                    (urlPath) + "/");
            FileIO.CreateFile(webRoot + savePath, htmlJsoupUtil.modifyLink(JsoupExam.getHtml(urlPath), "", getWebPath
                    (urlPath) + "/" + getRelatePath(urlPath) + "/"));
            savePath(range, type,pic,name, category, savePath, urlPath, title, context);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static boolean isHtmlSaven(String category, String urlPath, Context context) {
        String dirPath = category;
        if (category.contains("/")) {
            dirPath = category.substring(category.lastIndexOf("/") + 1);
        }
        String webRoot = UIHelper.getStoragePath(context);
        webRoot = webRoot + constants.SD_CARD_TBSFILE_PATH6 + "/";
        String inipath = webRoot + category + "/" + dirPath + ".ini";
        IniFile IniFile = new IniFile();
        try {
            String noCode = StringUtils.getMD5(urlPath);
            //System.out.println("noCode = "+ noCode);
            int count = Integer.parseInt(IniFile.getIniString(inipath, "file", "count", "0", (byte) 0));
            for (int i = 1; i <= count; i++) {
                String code = IniFile.getIniString(inipath, "file" + i, "MD5code", "", (byte) 0);
                //System.out.println("code = "+ code);
                if (code.equalsIgnoreCase(noCode)) {
                    String name = IniFile.getIniString(inipath, "file" + i, "name", "", (byte) 0);
                    //System.out.println(webRoot + name);
                    File file = new File(webRoot + category + "/" + name);
                    return file.exists() && file.isFile();
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void savePath(String range, String type,String pic,String filedir, String category, String htmlpath, String urlPath, String title,
                                 Context context) {
        String dirPath = category;
        if (category.contains("/")) {
            dirPath = category.substring(category.lastIndexOf("/") + 1);
        }
        String webRoot = UIHelper.getStoragePath(context);
        webRoot = webRoot + constants.SD_CARD_TBSFILE_PATH6 + "/";
        String inipath = webRoot + type + "/" + range + "/" + category + "/" + dirPath + ".ini";
        if (type.equalsIgnoreCase("考试"))
            inipath = webRoot + category + "/" + range + "/" + range + ".ini";
        IniFile IniFile = new IniFile();
        int count = Integer.parseInt(IniFile.getIniString(inipath, "file", "count", "0", (byte) 0));
        String fileCode = urlPath.substring(urlPath.lastIndexOf("=") + 1);
        int filecount = count + 1;
        for (int i = 1; i <= count; i++) {
            if (fileCode.equalsIgnoreCase(IniFile.getIniString(inipath, "file" + i, "code", "", (byte) 0))) {
                String filePath = IniFile.getIniString(inipath, "file" + i, "path", "",
                        (byte) 0);
                FileUtils.deleteDirectory(webRoot+filePath);
                filecount = i;
                break;
            }
        }
        String MD5code = "";
        try {
            MD5code = StringUtils.getMD5(urlPath);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        IniFile.writeIniString(inipath, "file" + filecount, "time", StringUtils.getDate
                ());
        IniFile.writeIniString(inipath, "file" + filecount, "pic", pic);
        IniFile.writeIniString(inipath, "file" + filecount, "title", title);
        IniFile.writeIniString(inipath, "file" + filecount, "description", "");
        IniFile.writeIniString(inipath, "file" + filecount, "name", filedir + "/" + htmlpath);
        IniFile.writeIniString(inipath, "file" + filecount, "code", urlPath.substring(urlPath.lastIndexOf("=") + 1));
        IniFile.writeIniString(inipath, "file" + filecount, "MD5code", MD5code);
        if (type.equalsIgnoreCase("考试")) {
            IniFile.writeIniString(inipath, "file" + filecount, "path", category + "/" + range + "/" + filedir);
            IniFile.writeIniString(inipath, "file" + filecount, "category", category);
        } else {
            IniFile.writeIniString(inipath, "file" + filecount, "path", type + "/" + range + "/" + category + "/" +
                    filedir);
            IniFile.writeIniString(inipath, "file" + filecount, "category", category);
        }
        IniFile.writeIniString(inipath, "file" + filecount, "range", range);
        IniFile.writeIniString(inipath, "file" + filecount, "type", "file");
        IniFile.writeIniString(inipath, "file" + filecount, "shareUrl", urlPath);
        IniFile.writeIniString(inipath, "file" + filecount, "play", "0");
        if(filecount > count)
        IniFile.writeIniString(inipath, "file", "count", filecount + "");
    }


    private static String getRelatePath(String srcPath) {
        if (srcPath.contains("?"))
            srcPath = srcPath.substring(0, srcPath.indexOf("?"));
        if (srcPath.contains("//"))
            srcPath = srcPath.substring(srcPath.indexOf("//") + 2);
        if (srcPath.contains("/"))
            srcPath = srcPath.substring(srcPath.indexOf("/") + 1);
        else
            srcPath = "";
        if (srcPath.contains("/"))
            srcPath = srcPath.substring(0, srcPath.lastIndexOf("/"));
        return srcPath;
    }

    private static String getWebPath(String srcPath) {
        if (srcPath.contains("?"))
            srcPath = srcPath.substring(0, srcPath.indexOf("?"));
        if (srcPath.contains("//"))
            srcPath = srcPath.substring(srcPath.indexOf("//") + 2);
        if (srcPath.contains("/"))
            srcPath = srcPath.substring(0, srcPath.indexOf("/"));
        return "http://" + srcPath;
    }


    /**
     * 获取html信息
     * 保存到本地
     * 并保存
     *
     * @param urlPath
     */
    public static void getSearchEngine(final int category, final String urlPath, final Context ac) {
        final Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    //Toast.makeText(ac, "测试成功", Toast.LENGTH_SHORT).show();
                } else if (msg.what == 2) {
                    //Toast.makeText(ac, "已下载", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ac, "获取信息失败", Toast.LENGTH_SHORT).show();
                }
            }
        };
        new Thread()
        {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    try {
                        Document doc = Jsoup.connect(urlPath).timeout(30000).get();
                        String title = doc.title();
                        String description = "";
                        Elements metas = doc.head().select("meta");
                        for (Element meta : metas) {
                            String content = meta.attr("content");
                            if ("keywords".equalsIgnoreCase(meta.attr("name"))) {
                                //System.out.println("关键字："+content);
                            }
                            if ("description".equalsIgnoreCase(meta.attr("name"))) {
                                description = content;
                            }
                        }
                        if (description == "") {
                            description = title;
                        }
                        if (title.contains("-"))
                            title = title.substring(0, title.indexOf("-")) + "-" + ac.getString(R.string.app_name);
                        if (category == 1) {
                            UIHelper.Collect(ac, urlPath, title.trim(), "", description, "");
                            msg.what = 1;
                        } else if(category == 2){
                            UIHelper.Share(ac, urlPath, title.trim(), description, "");
                            msg.what = 1;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = -1;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    private static Notification.Builder initNotifiction(String title, Context mContext) {
        Notification.Builder builder1 = new Notification.Builder(mContext);
        builder1.setSmallIcon(android.R.drawable.stat_sys_download); //设置图标
        builder1.setTicker(mContext.getString(R.string.downloading_msg));
        builder1.setContentTitle(title); //设置标题
        builder1.setContentText(mContext.getString(R.string.downloading_msg)); //消息内容
        builder1.setWhen(System.currentTimeMillis()); //发送时间
        builder1.setDefaults(Notification.DEFAULT_ALL); //设置默认的提示音，振动方式，灯光
        builder1.setAutoCancel(true);//打开程序后图标消失
        Intent intent = new Intent(mContext, FileSourceActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        builder1.setContentIntent(pendingIntent);
        return builder1;
    }
}

