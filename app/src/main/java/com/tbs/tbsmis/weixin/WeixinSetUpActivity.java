package com.tbs.tbsmis.weixin;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.UIHelper;

import java.util.HashMap;
import java.util.Map;

public class WeixinSetUpActivity extends Activity implements View.OnClickListener {
	private ImageView finishBtn, downBtn;
	private EditText Token;
	private String userIni;
	private IniFile IniFile;
	private TextView title;
	private EditText Home;
	private RelativeLayout api_layout;
	protected ProgressDialog Prodialog;
	private TextView Api;
	// private EditText syn_menu_view;
	private TextView show_name;
	private RelativeLayout account_layout;
	private RelativeLayout receive_layout;
	private TextView receive;
	private EditText weixin_news_data;

    private RelativeLayout weixin_show_layout;
    private CheckBox weixin_show_box;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.weixin_setup);
        this.weixin_news_data = (EditText) this.findViewById(R.id.weixin_news_data);
        this.Token = (EditText) this.findViewById(R.id.token_view);
        this.Home = (EditText) this.findViewById(R.id.weixin_home);
		// syn_menu_view = (EditText) findViewById(R.id.syn_menu_view);
        this.Api = (TextView) this.findViewById(R.id.api_view);
        this.receive = (TextView) this.findViewById(R.id.receive_view);
        this.show_name = (TextView) this.findViewById(R.id.show_name);
        this.title = (TextView) this.findViewById(R.id.textView1);
        this.finishBtn = (ImageView) this.findViewById(R.id.more_btn);
        this.downBtn = (ImageView) this.findViewById(R.id.finish_btn);

        this.api_layout = (RelativeLayout) this.findViewById(R.id.api_layout);
        this.receive_layout = (RelativeLayout) this.findViewById(R.id.receive_layout);
        this.account_layout = (RelativeLayout) this.findViewById(R.id.weixin_account_layout);
        this.account_layout.setOnClickListener(this);
        this.receive_layout.setOnClickListener(this);
        this.api_layout.setOnClickListener(this);
        this.finishBtn.setOnClickListener(this);
        this.downBtn.setOnClickListener(this);
        this.title.setText(R.string.weixin_manager);
        this.IniFile = new IniFile();
		initPath();

        weixin_show_layout = (RelativeLayout) findViewById(R.id.weixin_show_layout);
        weixin_show_box = (CheckBox) findViewById(R.id.weixin_show_box);
        int nVal = Integer.parseInt(IniFile.getIniString(userIni, "WeiXin",
                "weixin_show_in_set", "0", (byte) 0));
        if (nVal == 1) {
            weixin_show_box.setChecked(true);
        }
        weixin_show_layout.setOnClickListener(this);

        this.Token.setText(this.IniFile.getIniString(this.userIni, "WeiXin", "WeToken",
				"http://e.tbs.com.cn/wechat.do", (byte) 0));
        this.receive.setText(this.IniFile.getIniString(this.userIni, "WeiXin", "newsUrl",
				"http://e.tbs.com.cn:8003/IPS/search_wp.cbs?resname=yqxx",
				(byte) 0));
        this.weixin_news_data.setText(this.IniFile.getIniString(this.userIni, "WeiXin",
				"dataCount", "3", (byte) 0));
        this.show_name.setText(this.IniFile.getIniString(this.userIni, "WeiXin", "Account",
				"TBS软件", (byte) 0));
        this.Home.setText(this.IniFile.getIniString(this.userIni, "WeiXin", "WeHome",
				"http://e.tbs.com.cn:2011/tbsnews/home/introduce.cbs", (byte) 0));
        this.Prodialog = new ProgressDialog(this);
        this.Prodialog.setTitle("获取信息");
        this.Prodialog.setMessage("正在请求，请稍候...");
        this.Prodialog.setIndeterminate(false);
        this.Prodialog.setCanceledOnTouchOutside(false);
        this.Prodialog.setButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method
				dialog.dismiss();
			}
		});
        this.connect(0);
	}
    private void initPath(){
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
                + IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        userIni = appIniFile;
        if(Integer.parseInt(IniFile.getIniString(userIni, "Login",
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.more_btn:
            this.finish();
            this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
			break;
		case R.id.weixin_account_layout:
			Intent intent = new Intent();
			intent.setClass(this, WeixinActivity.class);
            this.startActivity(intent);
            this.finish();
			break;
		case R.id.api_layout:
            this.showNickDialog();
			break;
		case R.id.receive_layout:
            this.showReceiveDialog();
			break;
		case R.id.finish_btn:
            this.IniFile.writeIniString(this.userIni, "WeiXin", "WeToken", this.Token
					.getText().toString());
            this.IniFile.writeIniString(this.userIni, "WeiXin", "WeHome", this.Home
					.getText().toString());
            this.IniFile.writeIniString(this.userIni, "WeiXin", "dataCount",
                    this.weixin_news_data.getText().toString());
			// IniFile.writeIniString(userIni, "WeiXin", "CreatMenu",
			// syn_menu_view.getText().toString());
            this.finish();
            this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
			break;
            case R.id.weixin_show_layout:
                if (weixin_show_box.isChecked()) {
                    weixin_show_box.setChecked(false);
                    IniFile.writeIniString(userIni, "WeiXin", "weixin_show_in_set", "0");
                } else {
                    weixin_show_box.setChecked(true);
                    IniFile.writeIniString(userIni, "WeiXin", "weixin_show_in_set", "1");
                }
                break;
		}
	}

	private void showReceiveDialog() {
		// TODO Auto-generated method stub

		LayoutInflater factory = LayoutInflater.from(this);// 提示框
		View view = factory.inflate(R.layout.set_account_username, null);// 这里必须是final的
		final EditText edit = (EditText) view.findViewById(R.id.editText);// 获得输入框对象
		edit.setText(this.receive.getText().toString());
		edit.setHint("接收信息链接");
		edit.setSelection(edit.getText().toString().length());// 光标位置
		// edit.setCursorVisible(false);
		edit.setSelectAllOnFocus(true);// 全选文本
		new Builder(this).setTitle("修改")// 提示框标题
				.setView(view).setPositiveButton("提交",// 提示框的两个按钮
						new DialogInterface.OnClickListener() {
							@SuppressWarnings("deprecation")
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Toast.makeText(WeixinSetUpActivity.this,
										edit.getText().toString(),
										Toast.LENGTH_SHORT).show();
                                WeixinSetUpActivity.this.connect(2, edit.getText().toString());
                                WeixinSetUpActivity.this.Prodialog = new ProgressDialog(
										WeixinSetUpActivity.this);
                                WeixinSetUpActivity.this.Prodialog.setTitle("信息保存");
                                WeixinSetUpActivity.this.Prodialog.setMessage("正在保存，请稍候...");
                                WeixinSetUpActivity.this.Prodialog.setIndeterminate(false);
                                WeixinSetUpActivity.this.Prodialog.setCanceledOnTouchOutside(false);
                                WeixinSetUpActivity.this.Prodialog.setButton("取消",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												dialog.dismiss();
											}
										});
                                WeixinSetUpActivity.this.Prodialog.show();
							}
						}).setNegativeButton("取消", null).create().show();

	}

	private void showNickDialog() {
		LayoutInflater factory = LayoutInflater.from(this);// 提示框
		View view = factory.inflate(R.layout.set_account_username, null);// 这里必须是final的
		final EditText edit = (EditText) view.findViewById(R.id.editText);// 获得输入框对象
		edit.setText(this.Api.getText().toString());
		edit.setHint("微信回调api链接");
		edit.setSelection(edit.getText().toString().length());// 光标位置
		// edit.setCursorVisible(false);
		edit.setSelectAllOnFocus(true);// 全选文本
		new Builder(this).setTitle("修改api")// 提示框标题
				.setView(view).setPositiveButton("提交",// 提示框的两个按钮
						new DialogInterface.OnClickListener() {
							@SuppressWarnings("deprecation")
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Toast.makeText(WeixinSetUpActivity.this,
										edit.getText().toString(),
										Toast.LENGTH_SHORT).show();
                                WeixinSetUpActivity.this.connect(1, edit.getText().toString());
                                WeixinSetUpActivity.this.Prodialog = new ProgressDialog(
										WeixinSetUpActivity.this);
                                WeixinSetUpActivity.this.Prodialog.setTitle("信息保存");
                                WeixinSetUpActivity.this.Prodialog.setMessage("正在保存，请稍候...");
                                WeixinSetUpActivity.this.Prodialog.setIndeterminate(false);
                                WeixinSetUpActivity.this.Prodialog.setCanceledOnTouchOutside(false);
                                WeixinSetUpActivity.this.Prodialog.setButton("取消",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												dialog.dismiss();
											}
										});
                                WeixinSetUpActivity.this.Prodialog.show();
							}
						}).setNegativeButton("取消", null).create().show();
	}

	private void connect(int count) {
		WeixinSetUpActivity.MyAsyncTask task = new WeixinSetUpActivity.MyAsyncTask(this, count);
		task.execute();
	}

	private void connect(int count, String token) {
		WeixinSetUpActivity.MyAsyncTask task = new WeixinSetUpActivity.MyAsyncTask(count, this, token);
		task.execute();
	}

	class MyAsyncTask extends AsyncTask<String, Integer, String> {

		Context context;
		int count;
		private String Token;

		public MyAsyncTask(Context c, int count) {
            context = c;
			this.count = count;
		}

		public MyAsyncTask(int count, Context context, String Token) {
            this.context = context;
			this.count = count;
			this.Token = Token;
		}

		// 运行在UI线程中，在调用doInBackground()之前执行
		@Override
		protected void onPreExecute() {
			if (this.count == 0) {
                WeixinSetUpActivity.this.Prodialog.show();
			}
		}

		// 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		@Override
		protected String doInBackground(String... params) {
			HttpConnectionUtil connection = new HttpConnectionUtil();
			if (this.count == 0) {
				String verifyURL = WeixinSetUpActivity.this.IniFile.getIniString(WeixinSetUpActivity.this.userIni, "WeiXin",
						"WeToken", "http://e.tbs.com.cn/wechat.do", (byte) 0)
						+ "?action=getApiUrl";
				// String verifyURL =
				// "http://e.tbs.com.cn/wechat.do?action=getApiUrl";
				return connection.asyncConnect(verifyURL, null,
						HttpConnectionUtil.HttpMethod.POST, this.context);
			} else if (this.count == 1) {
				String verifyURL = WeixinSetUpActivity.this.IniFile.getIniString(WeixinSetUpActivity.this.userIni, "WeiXin",
						"WeToken", "http://e.tbs.com.cn/wechat.do", (byte) 0)
						+ "?action=setApiUrl";
				// String verifyURL =
				// "http://e.tbs.com.cn/wechat.do?action=setApiUrl";
				return connection.asyncConnect(verifyURL, this.setUrl(),
						HttpConnectionUtil.HttpMethod.GET, this.context);
			} else {
				String verifyURL = WeixinSetUpActivity.this.IniFile.getIniString(WeixinSetUpActivity.this.userIni, "WeiXin",
						"WeToken", "http://e.tbs.com.cn/wechat.do", (byte) 0)
						+ "?action=setNewsUrl";
				// String verifyURL =
				// "http://e.tbs.com.cn/wechat.do?action=setApiUrl";
				return connection.asyncConnect(verifyURL, this.setUrl(),
						HttpConnectionUtil.HttpMethod.GET, this.context);
			}
		}

		public Map<String, String> setUrl() {
			Map<String, String> params = new HashMap<String, String>();
			params.put("apiUrl", this.Token);
			return params;
		}

		public Map<String, String> receive() {
			Map<String, String> params = new HashMap<String, String>();
			params.put("newsUrl", this.Token);
			return params;
		}

		// 运行在ui线程中，在doInBackground()执行完毕后执行
		@Override
		protected void onPostExecute(String result) {
            WeixinSetUpActivity.this.Prodialog.dismiss();
			if (result == null) {
				Toast.makeText(this.context, "请检查网络设置", Toast.LENGTH_SHORT).show();
			} else {
				if (this.count == 0) {
                    WeixinSetUpActivity.this.Api.setText(result);
				} else if (this.count == 1) {
					if (result.equalsIgnoreCase("true")) {
                        WeixinSetUpActivity.this.Api.setText(this.Token);
						Toast.makeText(this.context, "保存成功", Toast.LENGTH_SHORT)
								.show();
					} else {
						Toast.makeText(this.context, "保存失败", Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					if (result.equalsIgnoreCase("true")) {
                        WeixinSetUpActivity.this.receive.setText(this.Token);
                        WeixinSetUpActivity.this.IniFile.writeIniString(WeixinSetUpActivity.this.userIni, "WeiXin", "newsUrl",
                                this.Token);
						Toast.makeText(this.context, "保存成功", Toast.LENGTH_SHORT)
								.show();
					} else {
						Toast.makeText(this.context, "保存失败", Toast.LENGTH_SHORT)
								.show();
					}
				}

			}
		}

		// 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		@Override
		protected void onProgressUpdate(Integer... values) {
		}
	}
}
