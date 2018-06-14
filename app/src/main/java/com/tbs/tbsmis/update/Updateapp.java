package com.tbs.tbsmis.update;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class Updateapp
{

    public Updateapp() {
    }

    public static int CheckVersion(Context context, String fileName,
                                   String timer) {
        // TODO Auto-generated method stub
        //System.out.println("timer =" + timer);
        //System.out.println("fileName=" + fileName);
        if (fileName.contains("app:")) {
            fileName = fileName.substring(fileName.indexOf(":") + 1);
            try {
                //包管理操作管理类
                PackageManager pm = context.getPackageManager();
                //获取到应用信息
                PackageInfo info = pm.getPackageInfo(fileName, 0);
                if (timer.compareToIgnoreCase(info.versionName) > 0) {
                    //System.out.println("return=-1");
                    return -1;
                } else {
                    //System.out.println("return=1");
                    return 1;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return 0;
            }
        } else {
            String webRoot = UIHelper.getStoragePath(context);
            webRoot += constants.SD_CARD_TBSSOFT_PATH3;
            File filePath = new File(webRoot + "/" + fileName);
            //System.out.println("webRoot + \"/\" + fileName=" + webRoot + "/" + fileName);
            //System.out.println("filePath.exists()=" + filePath.exists());
            if (filePath.exists()) {
                IniFile IniFile = new IniFile();
                String configPath = context.getFilesDir().getParentFile()
                        .getAbsolutePath();
                if (configPath.endsWith("/") == false) {
                    configPath = configPath + "/";
                }
                String appIniFile = configPath + constants.USER_CONFIG_FILE_NAME;
                String endTime = IniFile.getIniString(appIniFile, "data", fileName,
                        "", (byte) 0);
                //
                if (StringUtils.isEmpty(endTime)) {
                    return -1;
                } else {
                    return StringUtils.isInTimer(timer, endTime);
                }
            } else {
                return 0;
            }
        }
    }

    public static int CheckFileVersion(Context context, String fileName,
                                       String timer) {
        // TODO Auto-generated method stub
        //System.out.println("fileName=" + fileName);
        String webRoot = UIHelper.getStoragePath(context);
        webRoot += constants.SD_CARD_TBSFILE_PATH6;
        File filePath = new File(webRoot + "/" + fileName);
        if (filePath.exists() && filePath.isFile()) {
            IniFile IniFile = new IniFile();
            String configPath = context.getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (configPath.endsWith("/") == false) {
                configPath = configPath + "/";
            }
            String appIniFile = configPath + constants.USER_CONFIG_FILE_NAME;
            String endTime = IniFile.getIniString(appIniFile, "data", fileName,
                    "", (byte) 0);
            //
            if (StringUtils.isEmpty(endTime)) {
                return -1;
            } else {
                return StringUtils.isInTimer(timer, endTime);
            }
        } else {
            return 0;
        }
    }

    public static void FileStoreInfo(Context context, String dirPath, String fileinfo) {
        // TODO Auto-generated method stub
        String webRoot = UIHelper.getStoragePath(context);
        webRoot += constants.SD_CARD_TBSFILE_PATH6 + "/" + dirPath;
        File filePath = new File(webRoot);
        if (!filePath.exists()) {
            IniFile IniFile = new IniFile();
            String path = webRoot.substring(0, webRoot.lastIndexOf("/"));
            String inipath = path.substring(path.lastIndexOf("/") + 1);
            inipath = path + "/" + inipath + ".ini";
            File file = new File(path);
            if (!file.exists())
                file.mkdirs();
            int count = Integer.parseInt(IniFile.getIniString(inipath, "file", "count", "0", (byte) 0));
            try {
                JSONObject jsonObject = new JSONObject(fileinfo);
                String fileCode = jsonObject.getString("fileCode");
                String name = jsonObject.getString("name");
                String title = jsonObject.getString("title");
                String filepath = jsonObject.getString("path");
                String pic = jsonObject.getString("pic");
                String description = jsonObject.getString("description");
                String content = jsonObject.getString("content");
                String play = jsonObject.getString("play");
                String category = jsonObject.getString("category");
                String fileType = jsonObject.getString("fileType");
                String shareUrl = jsonObject.getString("shareUrl");
                String chapter = jsonObject.getString("chapter");
                String relateExam = jsonObject.getString("relateExam");
                String relateExamUrl = jsonObject.getString("relateExamUrl");
                String relateKnowled = jsonObject.getString("relateKnowled");
                String relateKnowledUrl = jsonObject.getString("relateKnowledUrl");
                int filecount = 1;
                boolean ishave = false;
                for (int i = 1; i <= count; i++) {
                    String countCode = IniFile.getIniString(inipath, "file" + i, "code", "", (byte) 0);
                    if (countCode.equalsIgnoreCase(fileCode)) {
                        filecount = i;
                        ishave = true;
                        break;
                    }
                }
                if (!ishave) {
                    filecount = count + 1;
                    IniFile.writeIniString(inipath, "file", "count", filecount + "");
                }
                IniFile.writeIniString(inipath, "file" + filecount, "time", StringUtils.getDate
                        ());
                IniFile.writeIniString(inipath, "file" + filecount, "title", title);
                IniFile.writeIniString(inipath, "file" + filecount, "description", description);
                IniFile.writeIniString(inipath, "file" + filecount, "content", content);
                IniFile.writeIniString(inipath, "file" + filecount, "name", name);
                IniFile.writeIniString(inipath, "file" + filecount, "code", fileCode);
                IniFile.writeIniString(inipath, "file" + filecount, "path", filepath);
                IniFile.writeIniString(inipath, "file" + filecount, "pic", pic);
                IniFile.writeIniString(inipath, "file" + filecount, "category", category);
                IniFile.writeIniString(inipath, "file" + filecount, "type", fileType);
                IniFile.writeIniString(inipath, "file" + filecount, "play", play);
                IniFile.writeIniString(inipath, "file" + filecount, "shareUrl", shareUrl);
                IniFile.writeIniString(inipath, "file" + filecount, "chapter", chapter);
                IniFile.writeIniString(inipath, "file" + filecount, "relateExam", relateExam);
                IniFile.writeIniString(inipath, "file" + filecount, "relateExamUrl", relateExamUrl);
                IniFile.writeIniString(inipath, "file" + filecount, "relateKnowled", relateKnowled);
                IniFile.writeIniString(inipath, "file" + filecount, "relateKnowledUrl", relateKnowledUrl);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}