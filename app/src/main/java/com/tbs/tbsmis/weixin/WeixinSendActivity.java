package com.tbs.tbsmis.weixin;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;
import com.tbs.tbsmis.util.httpRequestUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 信息推送
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class WeixinSendActivity extends Activity implements View.OnClickListener {
	private EditText send_content;
	private Button send_btn, cancle_btn;
	private IniFile m_iniFileIO;
	private String userIni;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_fullscreen);
		// 添加Activity到堆栈
		MyActivity.getInstance().addActivity(this);
        this.initView();
	}

	// 初始化视图控件
	private void initView() {
        this.send_content = (EditText) this.findViewById(R.id.send_content);
        this.send_btn = (Button) this.findViewById(R.id.send_btn);
        this.cancle_btn = (Button) this.findViewById(R.id.cancle_btn);
        this.send_btn.setOnClickListener(this);
        this.cancle_btn.setOnClickListener(this);
	}

	private void initPath() {
        this.m_iniFileIO = new IniFile();
		String webRoot = UIHelper.getSoftPath(this);
		if (webRoot.endsWith("/") == false) {
			webRoot += "/";
		}
		webRoot += getString(R.string.SD_CARD_TBSAPP_PATH2);
		webRoot = UIHelper.getShareperference(this, constants.SAVE_INFORMATION,
				"Path", webRoot);
		if (webRoot.endsWith("/") == false) {
			webRoot += "/";
		}
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String appIniFile = webRoot
                + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        String userIni = appIniFile;
        if(Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1){
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 结束Activity&从堆栈中移除
		MyActivity.getInstance().finishActivity(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.onBackPressed();
		}
		return true;
	}

	class HttpAsyncTask extends AsyncTask<Integer, Integer, String> {

		private final Context context;
		private final String sendTxt;

		public HttpAsyncTask(Context context, String sendTxt) {
			this.context = context;
			this.sendTxt = sendTxt;
		}

		/**
		 * 这里的Integer参数对应AsyncTask中的第一个参数 这里的String返回值对应AsyncTask的第三个参数
		 * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
		 * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
		 * 
		 * @return
		 */
		@Override
		protected String doInBackground(Integer... params) {
            WeixinSendActivity.this.initPath();
			String verifyURL = WeixinSendActivity.this.m_iniFileIO.getIniString(WeixinSendActivity.this.userIni, "WeiXin",
					"WeToken", "http://e.tbs.com.cn/wechat.do", (byte) 0)
					+ "?action=sendsTxt";
			JSONObject jsonObject = httpRequestUtil.httpRequest(verifyURL,
					"GET", this.sendTxt);
			if (null != jsonObject) {
				try {
					if (jsonObject.getInt("errcode") == 0) {
						return "发送成功";
					} else {
						return jsonObject.getInt("errcode") + ":"
								+ jsonObject.getString("errmsg");
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}

		/**
		 * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
		 * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
		 */
		@Override
		protected void onPostExecute(String result) {
			if (StringUtils.isEmpty(result)) {
				Toast.makeText(this.context, "错误(E0027)：无法连接到群发服务!",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this.context, result, Toast.LENGTH_SHORT).show();
                WeixinSendActivity.this.finish();
			}

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
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.cancle_btn:
            this.finish();
			break;
		case R.id.send_btn:
			if (StringUtils.isEmpty(this.send_content.getText().toString())) {
				Toast.makeText(this, "信息不可为空",
						Toast.LENGTH_SHORT).show();
			} else {
				WeixinSendActivity.HttpAsyncTask task = new WeixinSendActivity.HttpAsyncTask(this, this.send_content
						.getText().toString());
				task.execute();
                this.finish();
			}
			break;
		}
	}
}
