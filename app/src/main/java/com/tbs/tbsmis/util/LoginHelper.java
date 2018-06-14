package com.tbs.tbsmis.util;

import android.content.Context;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.update.SynLandingAsyncTask;

/**
 * Created by TBS on 2017/4/26.
 */

public class LoginHelper
{
    private static IniFile m_iniFileIO;
    private static String userIni;

    public static void WebLogin(Context context, String account, String loginId) {
        String webRoot = UIHelper.getSoftPath(context);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        webRoot += context.getString(R.string.SD_CARD_TBSAPP_PATH2);
        webRoot = UIHelper.getShareperference(context, constants.SAVE_INFORMATION,
                "Path", webRoot);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        m_iniFileIO = new IniFile();
        String WebIniFile = webRoot
                + constants.WEB_CONFIG_FILE_NAME;
        String rcIni = m_iniFileIO.getIniString(WebIniFile, "TBSWeb",
                "IniName", constants.NEWS_CONFIG_FILE_NAME,
                (byte) 0);
        userIni = webRoot + rcIni;
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = context.getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginFlag", "0", (byte) 0)) == 0) {
            SynLandingAsyncTask task = new SynLandingAsyncTask(context, loginId,
                    account);
            task.execute();
        }else{
            if(!loginId.equalsIgnoreCase(m_iniFileIO.getIniString(userIni, "Login",
                    "LoginId", "", (byte) 0))){
                SynLandingAsyncTask task = new SynLandingAsyncTask(context, loginId,
                        account);
                task.execute();
            }
        }

    }
}
