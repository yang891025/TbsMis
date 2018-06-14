package com.tbs.tbsmis.update;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.tbs.cbs.CBSInterpret;
import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R.string;
import com.tbs.tbsmis.app.AppException;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.ApiClient;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

/**
 * 生成该类的对象，并调用execute方法之后 首先执行的是onProExecute方法 其次执行doInBackgroup方法
 * 
 */
public class CbsDownloadAsyncTask extends AsyncTask<Integer, Integer, String> {

	private final String category;
	private final Context context;
	private String rootPath;
	private String WebIniFile;
	private IniFile m_iniFileIO;
	private String appNewsFile;
	private int count;

	public CbsDownloadAsyncTask(Context context, String category) {
        this.category = category;
		this.context = context;
	}

	public CbsDownloadAsyncTask(Context context, String category, int count) {
        this.category = category;
		this.context = context;
		this.count = count;
	}

	/**
	 * 这里的Integer参数对应AsyncTask中的第一个参数 这里的String返回值对应AsyncTask的第三个参数
	 * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
	 * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
	 */
	@Override
	protected String doInBackground(Integer... params) {
        this.initPath();
		// String CheckIndex = m_iniFileIO.getIniString(appNewsFile,
		// "DOWNLOADURL", "CheckIndex", "0", (byte) 0);
		// String OfflineDownloadpage = m_iniFileIO.getIniString(appNewsFile,
		// "DOWNLOADURL", "OfflineDownloadpage", "", (byte) 0);
		// String OfflineDownloadpic = m_iniFileIO.getIniString(appNewsFile,
		// "DOWNLOADURL", "OfflineDownloadpic", "", (byte) 0);
		// String OfflineDownloadpart = m_iniFileIO.getIniString(appNewsFile,
		// "DOWNLOADURL", "OfflineDownloadpart", "", (byte) 0);
		// int imageflag =
		// Integer.parseInt(m_iniFileIO.getIniString(appNewsFile,
		// "OFFLINESETTING", "setup_image", "0", (byte) 0));
		// String onlinePort = m_iniFileIO.getIniString(appNewsFile, "NETWORK",
		// "offlinePort", constants.DefaultServerPort, (byte) 0);
		// String onlineIp = m_iniFileIO.getIniString(appNewsFile, "NETWORK",
		// "offlineAddress", constants.DefaultServerIp, (byte) 0);
		// CBSInterpret mInterpret = new CBSInterpret();
		// mInterpret.initGlobal(WebIniFile, rootPath);
		// String baseUrl1 = OfflineDownloadpage + "&webserverip=" + onlineIp
		// + "&webserverport=" + onlinePort + "&resname=" + category;
		// String interpretFile = mInterpret.Interpret(baseUrl1, "GET", "",
		// null,
		// 0);
		// FileUtils.deleteFileWithPath(interpretFile);
		// baseUrl1 = OfflineDownloadpart + "&webserverip=" + onlineIp
		// + "&webserverport=" + onlinePort + "&resname=" + category;
		// interpretFile = mInterpret.Interpret(baseUrl1, "GET", "", null, 0);
		// FileUtils.deleteFileWithPath(interpretFile);
		// if (imageflag == 1) {
		// baseUrl1 = OfflineDownloadpic + "&webserverip=" + onlineIp
		// + "&webserverport=" + onlinePort + "&resname=" + category;
		// interpretFile = mInterpret.Interpret(baseUrl1, "GET", "", null, 0);
		// FileUtils.deleteFileWithPath(interpretFile);
		// }
		// if (Integer.parseInt(m_iniFileIO.getIniString(appNewsFile,
		// "OFFLINESETTING", "setup_index", "0", (byte) 0)) == 1) {
		// interpretFile = mInterpret.Interpret(CheckIndex + category, "GET",
		// "", null, 0);
		// FileUtils.deleteFileWithPath(interpretFile);
		// }
        String userIni = this.appNewsFile;
        if(Integer.parseInt(this.m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1){
            String dataPath = this.context.getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
		String OfflineDownloadpage = this.m_iniFileIO.getIniString(this.appNewsFile,
				"subscribe", "CheckPath", "", (byte) 0);
		String OfflineDownloadpart = this.m_iniFileIO.getIniString(this.appNewsFile,
				"subscribe", "DownPath", "", (byte) 0);
		String onlinePort = this.m_iniFileIO.getIniString(userIni, "Offline",
				"offlinePort", constants.DefaultServerPort, (byte) 0);
		String onlineIp = this.m_iniFileIO.getIniString(userIni, "Offline",
				"offlineAddress", constants.DefaultServerIp, (byte) 0);
		String deviceID = this.m_iniFileIO.getIniString(this.appNewsFile, "DEVICE",
				"device_md5", UIHelper.DeviceMD5ID(this.context), (byte) 0);
		int CurrentServer = Integer.parseInt(this.m_iniFileIO.getIniString(
                userIni, "SERVICE", "serverMarks", "4", (byte) 0));

		String LoginId = this.m_iniFileIO.getIniString(userIni, "Login",
				"LoginId", "", (byte) 0);
		String Account = this.m_iniFileIO.getIniString(userIni, "Login",
				"Account", "", (byte) 0);
		String ipUrl = "";
		String portUrl = "";
		String baseUrl = "";
		if (CurrentServer == 2) {
			ipUrl = this.m_iniFileIO.getIniString(userIni, "SERVICE",
					"currentAddress", constants.DefaultLocalIp, (byte) 0);
			portUrl = this.m_iniFileIO.getIniString(userIni, "SERVICE",
					"currentPort", constants.DefaultLocalPort, (byte) 0);
			baseUrl = "http://" + ipUrl + ":" + portUrl;
			ApiClient ApiClient = new ApiClient();
			if (this.count == 0) {
				String baseUrl1 = baseUrl + OfflineDownloadpage
						+ "&webserverip=" + onlineIp + "&webserverport="
						+ onlinePort + "&resname=" + this.category + "&deviceID="
						+ deviceID + "&LoginId=" + LoginId + "&Account="
						+ Account;
                //System.out.println(baseUrl1);
				try {
					ApiClient.DownloadData(baseUrl1);
					baseUrl1 = baseUrl + OfflineDownloadpart + "&webserverip="
							+ onlineIp + "&webserverport=" + onlinePort
							+ "&resname=" + this.category + "&downFlag=download"
							+ "&deviceID=" + deviceID + "&LoginId=" + LoginId
							+ "&Account=" + Account;
					ApiClient.DownloadData(baseUrl1);
				} catch (AppException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				String baseUrl1 = baseUrl + OfflineDownloadpart
						+ "&webserverip=" + onlineIp + "&webserverport="
						+ onlinePort + "&resname=" + this.category + "&deviceID="
						+ deviceID + "&LoginId=" + LoginId + "&Account="
						+ Account;
               // System.out.println(baseUrl1);
				try {
					ApiClient.DownloadData(baseUrl1);
				} catch (AppException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			CBSInterpret mInterpret = new CBSInterpret();
			mInterpret.initGlobal(this.WebIniFile, this.rootPath);
			String baseUrl1;
			String interpretFile;
			if (this.count == 0) {
				baseUrl1 = OfflineDownloadpage + "&webserverip=" + onlineIp
						+ "&webserverport=" + onlinePort + "&resname="
						+ this.category + "&deviceID=" + deviceID + "&LoginId="
						+ LoginId + "&Account=" + Account;
                //System.out.println(baseUrl1);
				interpretFile = mInterpret.Interpret(baseUrl1, "GET", "", null,
						0);
				FileUtils.deleteFileWithPath(interpretFile);
				baseUrl1 = OfflineDownloadpart + "&webserverip=" + onlineIp
						+ "&webserverport=" + onlinePort + "&resname="
						+ this.category + "&downFlag=download" + "&deviceID="
						+ deviceID + "&LoginId=" + LoginId + "&Account="
						+ Account;
				interpretFile = mInterpret.Interpret(baseUrl1, "GET", "", null,
						0);
				FileUtils.deleteFileWithPath(interpretFile);
			} else {
				baseUrl1 = OfflineDownloadpart + "&webserverip=" + onlineIp
						+ "&webserverport=" + onlinePort + "&resname="
						+ this.category + "&downFlag=push" + "&deviceID=" + deviceID
						+ "&LoginId=" + LoginId + "&Account=" + Account;
                //System.out.println(baseUrl1);
				interpretFile = mInterpret.Interpret(baseUrl1, "GET", "", null,
						0);
				FileUtils.deleteFileWithPath(interpretFile);
			}
		}

		return null;
	}

	/**
	 * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
	 * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
	 */
	@SuppressLint("ShowToast")
	@Override
	protected void onPostExecute(String result) {

	}

	// 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
	@Override
	protected void onPreExecute() {

	}

	/**
	 * 这里的Intege参数对应AsyncTask中的第二个参数
	 * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行
	 * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作
	 */
	@Override
	protected void onProgressUpdate(Integer... values) {

	}

	private void initPath() {
		// TODO Auto-generated method stub
        this.m_iniFileIO = new IniFile();
		String webRoot = UIHelper.getSoftPath(context);
		if (webRoot.endsWith("/") == false) {
			webRoot += "/";
		}
		webRoot += this.context.getString(string.SD_CARD_TBSAPP_PATH2);
		webRoot = UIHelper.getShareperference(this.context,
				constants.SAVE_INFORMATION, "Path", webRoot);
		if (webRoot.endsWith("/") == false) {
			webRoot += "/";
		}
        this.rootPath = webRoot;
        this.WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        this.appNewsFile = webRoot
				+ this.m_iniFileIO.getIniString(this.WebIniFile, "TBSWeb", "IniName",
						constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
		String devicemd5 = this.m_iniFileIO.getIniString(this.appNewsFile, "DEVICE",
				"device_md5", "", (byte) 0);
		if (StringUtils.isEmpty(devicemd5)) {
			devicemd5 = UIHelper.DeviceMD5ID(this.context);
            this.m_iniFileIO.writeIniString(this.appNewsFile, "DEVICE", "device_md5",
					devicemd5);
		}
	}
}