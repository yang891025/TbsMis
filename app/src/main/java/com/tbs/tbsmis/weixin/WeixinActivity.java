package com.tbs.tbsmis.weixin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.Tbszlib.JTbszlib;
import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.check.PopMenus;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

@SuppressLint("SetJavaScriptEnabled")
public class WeixinActivity extends Activity implements View.OnClickListener,
		View.OnTouchListener {

	PopMenus popupWindow_custommenu;
	LinearLayout layout_custommenu;

	/**
	 * 这个字符串可以从服务端获取
	 */
	private ImageView backBtn;
	private ImageView menuBtn;
	private WebView webview;
	private RelativeLayout toolBar;
	private LinearLayout toolBar2;
	private RelativeLayout ibtnback;
	private RelativeLayout loadingIV;
	private ImageView iv;
	private AnimationDrawable loadingAnima;
	private boolean loadingDialogState;
	protected ValueCallback<Uri> mUploadMessage;
	private TextView wetitle;
	private String type;
	private PopupWindow moreWindow2;
	protected boolean isOpenPop;
	private RelativeLayout cloudTitle;
	private IniFile IniFile;
	private String userIni;
	private boolean isFresh = true;
	private boolean isAccount = true;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		MyActivity.getInstance().addActivity(this);
        this.setContentView(R.layout.weixin_activity);
        this.backBtn = (ImageView) this.findViewById(R.id.more_btn2);
        this.menuBtn = (ImageView) this.findViewById(R.id.search_btn2);
        this.wetitle = (TextView) this.findViewById(R.id.title_tv);
        this.cloudTitle = (RelativeLayout) this.findViewById(R.id.titlebar_layout);

        this.webview = (WebView) this.findViewById(R.id.webview3);
        this.loadingIV = (RelativeLayout) this.findViewById(R.id.loading_dialog2);
        this.iv = (ImageView) this.findViewById(R.id.gifview2);

		//layout_customemenu = (LinearLayout) findViewById(R.R.id.layout_customemenu);
        this.layout_custommenu = (LinearLayout) this.findViewById(R.id.layout_custommenu);

        this.toolBar = (RelativeLayout) this.findViewById(R.id.include_bottom);
        this.toolBar2 = (LinearLayout) this.findViewById(R.id.layout_btn);

        this.ibtnback = (RelativeLayout) this.findViewById(R.id.ibtnback);
        this.ibtnback.setOnClickListener(this);
        this.backBtn.setOnClickListener(this);
        this.menuBtn.setOnClickListener(this);

        this.IniFile = new IniFile();
		String webRoot = UIHelper.getShareperference(this,
				constants.SAVE_INFORMATION, "Path", "");
		if (webRoot.endsWith("/") == false) {
			webRoot += "/";
		}
		String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;

        String appIniFile = webRoot
				+ this.IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
						constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        userIni = appIniFile;
        if (Integer.parseInt(IniFile.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (this.isFresh) {
            this.connect(0, 0);
		} else {
            this.isFresh = true;
		}
        this.wetitle.setText(this.IniFile.getIniString(userIni, "WeiXin", "Account",
				"TBS软件", (byte) 0));
	}

	private void iniMenu(String jsonStr) {
		try {
            this.IniCustomMenu(new JSONObject(jsonStr));
            this.setCustomMenu(new JSONObject(jsonStr));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            this.layout_custommenu.setVisibility(View.INVISIBLE);
		}
        this.webview.loadUrl(this.IniFile.getIniString(userIni, "WeiXin", "WeHome",
                "http://e.tbs.com.cn:2011/tbsnews/home/introduce.cbs", (byte) 0));
	}

	private void init(String jsonStr) {
		try {
            this.setCustomMenu(new JSONObject(jsonStr));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            this.layout_custommenu.setVisibility(View.INVISIBLE);
		}
		/**
		 * webview��������
		 */
        this.webview.getSettings().setSaveFormData(false);
        this.webview.getSettings().setSupportZoom(true);
        this.webview.getSettings().setBuiltInZoomControls(true);
        this.webview.getSettings().setJavaScriptEnabled(true);
		// 3.0版本才有setDisplayZoomControls的功能
		if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1) {
            this.webview.getSettings().setDisplayZoomControls(false);
		}
        this.webview.getSettings().setSupportMultipleWindows(true);
        this.webview.getSettings().setDomStorageEnabled(true);
        this.webview.getSettings().setUseWideViewPort(true);
        this.webview.getSettings().setLoadWithOverviewMode(true);
		// flash支持
		//webview.getSettings().setPluginsEnabled(true);
        this.webview.getSettings().setPluginState(WebSettings.PluginState.ON);
        this.webview.getSettings().setAppCacheEnabled(true);
        this.webview.clearCache(true);
        this.webview.setClickable(true);
        this.webview.setLongClickable(true);
        this.webview.setOnTouchListener(this);

		// 修改ua使得web端正确判断 @ 2013-11-07 11:13:28
		String ua = this.webview.getSettings().getUserAgentString();
        this.webview.getSettings().setUserAgentString(ua + "; tbsmis/2015");
		// ȡ�������
        //this.webview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		UIHelper.addJavascript(this, this.webview);
        this.webview.setWebViewClient(UIHelper.getWebViewClient());
        this.webview.setDownloadListener(UIHelper.MyWebViewDownLoadListener(this));
        this.webview.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);

			}

			@Override
			public void onProgressChanged(WebView view, int progress) {
                WeixinActivity.this.loadingDialogState = progress < 100;
				if (WeixinActivity.this.loadingDialogState) {
                    WeixinActivity.this.startAnimation();
				} else {
                    WeixinActivity.this.stopAnimation();
				}
			}

			public void openFileChooser(ValueCallback<Uri> uploadMsg,
					String acceptType) {
				if (WeixinActivity.this.mUploadMessage != null)
					return;
                WeixinActivity.this.mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("*/*");
                WeixinActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"),
						0);
			}

			// For Android < 3.0
			public void openFileChooser(ValueCallback<Uri> uploadMsg) {
				openFileChooser(uploadMsg, "");
			}

			// For Android > 4.1.1
			public void openFileChooser(ValueCallback<Uri> uploadMsg,
					String acceptType, String capture) {
				openFileChooser(uploadMsg, acceptType);
			}
		});
        this.webview.setFocusable(true);
        this.webview.requestFocus();
        this.webview.loadUrl(this.IniFile.getIniString(userIni, "WeiXin", "WeHome",
				"http://e.tbs.com.cn:2011/tbsnews/home/introduce.cbs", (byte) 0));
	}

	private void setCustomMenu(JSONObject jsonObject) throws JSONException {
		String jsonMenu = jsonObject.get("menu").toString();
		JSONArray jsonCustomMenu = new JSONObject(jsonMenu)
				.getJSONArray("button");
		if (jsonCustomMenu != null && jsonCustomMenu.length() > 0) {
            this.layout_custommenu.setVisibility(View.VISIBLE);
            this.layout_custommenu.removeAllViews();
			JSONArray btnJson = jsonCustomMenu;
			for (int i = 0; i < btnJson.length(); i++) {
				final JSONObject ob = btnJson.getJSONObject(i);
				LinearLayout layout = (LinearLayout) ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
						.inflate(R.layout.item_custommenu, null);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,
						1.0f);
				layout.setLayoutParams(lp);
				TextView tv_custommenu_name = (TextView) layout
						.findViewById(R.id.tv_custommenu_name);
				String name = ob.getString("name");
                this.type = "";
				tv_custommenu_name.setText(name);
				if (ob.getJSONArray("sub_button").length() > 0) // 显示三角
				{
					tv_custommenu_name.setCompoundDrawablesWithIntrinsicBounds(
							0, 0, R.drawable.ic_arrow_up_black, 0);
				} else // 隐藏三角
				{
                    this.type = ob.getString("type");
					tv_custommenu_name.setCompoundDrawablesWithIntrinsicBounds(
							0, 0, 0, 0);
				}
				layout.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						try {
							if (ob.getJSONArray("sub_button").length() == 0) {
								if (WeixinActivity.this.type.equalsIgnoreCase("view")) {
                                    WeixinActivity.this.webview.loadUrl(ob.getString("url"));
								} else {
									Toast.makeText(WeixinActivity.this.getApplicationContext(),
											"类型不支持", Toast.LENGTH_SHORT).show();
								}
							} else {
                                WeixinActivity.this.popupWindow_custommenu = new PopMenus(WeixinActivity.this.webview,
                                        WeixinActivity.this.getApplicationContext(), ob
												.getJSONArray("sub_button"), v
												.getWidth() + 10, 0);
                                WeixinActivity.this.popupWindow_custommenu.showAtLocation(v);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
                this.layout_custommenu.addView(layout);
			}
		} else {
            this.layout_custommenu.setVisibility(View.INVISIBLE);
		}
	}

	private void IniCustomMenu(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
		String jsonMenu = jsonObject.get("menu").toString();
		JSONArray jsonCustomMenu = new JSONObject(jsonMenu)
				.getJSONArray("button");
		if (jsonCustomMenu != null && jsonCustomMenu.length() > 0) {
			JSONArray btnJson = jsonCustomMenu;
			for (int i = 0; i < btnJson.length(); i++) {
				JSONObject ob = btnJson.getJSONObject(i);
				String name = ob.getString("name");
                this.type = "";
				int resnum = Integer.parseInt(this.IniFile.getIniString(userIni,
						"MENU_ALL", "Count", "0", (byte) 0));
				boolean isResnum = true;
				for (int j = 1; j <= resnum; j++) {
					if (this.IniFile.getIniString(userIni, "MENU_ALL", "ID" + j,
							"", (byte) 0).equalsIgnoreCase(
                            "menu_weixin" + i + 1)) {
						isResnum = false;
                        this.IniFile.writeIniString(userIni, "MENU_ALL", "Title"
								+ j, name);
						if (ob.getJSONArray("sub_button").length() <= 0) // 显示三角
						{
                            this.IniFile.writeIniString(userIni, "menu_weixin"
									+ (i + 1), "Count", "0");
                            this.type = ob.getString("type");
							if (this.type.equalsIgnoreCase("view")) {
                                this.IniFile.writeIniString(userIni, "MENU_ALL",
										"Type" + j, this.type);
                                this.IniFile.writeIniString(userIni, "MENU_ALL",
										"Url" + j, ob.getString("url"));
							} else {
                                this.IniFile.writeIniString(userIni, "MENU_ALL",
										"Type" + j, this.type);
                                this.IniFile.writeIniString(userIni, "MENU_ALL",
										"Key" + j, ob.getString("key"));
							}
						} else {
							JSONArray jsonArray = ob.getJSONArray("sub_button");
							for (int n = 0; n < jsonArray.length(); n++) {
								JSONObject sub = jsonArray.getJSONObject(n);
								String subname = sub.getString("name");
								String subtype = sub.getString("type");
                                this.IniFile.writeIniString(userIni,
										"menu_weixin" + (i + 1), "Count",
										jsonArray.length() + "");
								if (subtype.equalsIgnoreCase("view")) {
                                    this.IniFile.writeIniString(userIni,
											"menu_weixin" + (i + 1), "Title"
													+ (n + 1), subname);
                                    this.IniFile.writeIniString(userIni,
											"menu_weixin" + (i + 1), "Type"
													+ (n + 1), subtype);
                                    this.IniFile.writeIniString(userIni,
											"menu_weixin" + (i + 1), "Url"
													+ (n + 1),
											sub.getString("url"));
								} else {
                                    this.IniFile.writeIniString(userIni,
											"menu_weixin" + (i + 1), "Title"
													+ (n + 1), subname);
                                    this.IniFile.writeIniString(userIni,
											"menu_weixin" + (i + 1), "Type"
													+ (n + 1), subtype);
                                    this.IniFile.writeIniString(userIni,
											"menu_weixin" + (i + 1), "Kye"
													+ (n + 1),
											sub.getString("key"));
								}
							}
						}
						break;
					}
				}
				if (isResnum) {
					int noResnum = resnum + 1;
                    this.IniFile.writeIniString(userIni, "MENU_ALL", "ID"
							+ noResnum, "menu_weixin" + (i + 1));
                    this.IniFile.writeIniString(userIni, "MENU_ALL", "Title"
							+ noResnum, name);
                    this.IniFile.writeIniString(userIni, "MENU_ALL", "Count",
							noResnum + "");
					if (ob.getJSONArray("sub_button").length() <= 0) // 显示三角
					{
                        this.IniFile.writeIniString(userIni, "menu_weixin"
								+ (i + 1), "Count", "0");
                        this.type = ob.getString("type");
						if (this.type.equalsIgnoreCase("view")) {
                            this.IniFile.writeIniString(userIni, "MENU_ALL",
									"Type" + noResnum, this.type);
                            this.IniFile.writeIniString(userIni, "MENU_ALL",
									"Url" + noResnum, ob.getString("url"));
						} else {
                            this.IniFile.writeIniString(userIni, "MENU_ALL",
									"Type" + noResnum, this.type);
                            this.IniFile.writeIniString(userIni, "MENU_ALL",
									"Key" + noResnum, ob.getString("key"));
						}
					} else {
						JSONArray jsonArray = ob.getJSONArray("sub_button");
						for (int n = 0; n < jsonArray.length(); n++) {
							JSONObject sub = jsonArray.getJSONObject(n);
							String subname = sub.getString("name");
							String subtype = sub.getString("type");
                            this.IniFile.writeIniString(userIni, "menu_weixin"
									+ (i + 1), "Count", jsonArray.length() + "");
							if (subtype.equalsIgnoreCase("view")) {
                                this.IniFile.writeIniString(userIni,
										"menu_weixin" + (i + 1), "Title"
												+ (n + 1), subname);
                                this.IniFile.writeIniString(userIni,
										"menu_weixin" + (i + 1), "Type"
												+ (n + 1), subtype);
                                this.IniFile.writeIniString(userIni,
										"menu_weixin" + (i + 1), "Url"
												+ (n + 1), sub.getString("url"));
							} else if(subtype.equalsIgnoreCase("miniprogram")){
								this.IniFile.writeIniString(userIni,
										"menu_weixin" + (i + 1), "Title"
												+ (n + 1), subname);
								this.IniFile.writeIniString(userIni,
										"menu_weixin" + (i + 1), "Type"
												+ (n + 1), subtype);
								this.IniFile.writeIniString(userIni,
										"menu_weixin" + (i + 1), "Url"
												+ (n + 1), sub.getString("url"));
								this.IniFile.writeIniString(userIni,
										"menu_weixin" + (i + 1), "appid"
												+ (n + 1), sub.getString("appid"));
								this.IniFile.writeIniString(userIni,
										"menu_weixin" + (i + 1), "pagepath"
												+ (n + 1), sub.getString("pagepath"));
							}else {
                                this.IniFile.writeIniString(userIni,
										"menu_weixin" + (i + 1), "Title"
												+ (n + 1), subname);
                                this.IniFile.writeIniString(userIni,
										"menu_weixin" + (i + 1), "Type"
												+ (n + 1), subtype);
                                this.IniFile.writeIniString(userIni,
										"menu_weixin" + (i + 1), "Kye"
												+ (n + 1), sub.getString("key"));
							}
						}
					}
				}
			}
		}
	}

	public void btnShowExchange_Click(View v) {
        this.toolBar.setVisibility(View.VISIBLE);
        this.toolBar2.setVisibility(View.GONE);
	}
    @Override
    public void finish() {
        super.finish();
    }
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
        super.onDestroy();
		if (this.webview != null) {
            this.webview.setVisibility(View.GONE);
            this.webview.destroy();
            this.webview = null;
		}
        MyActivity.getInstance().finishActivity(this);
	}

	public void changMorePopState2(View v) {
        this.isOpenPop = !this.isOpenPop;
		if (this.isOpenPop) {
            this.popWindow2(v);
		} else {
			if (this.moreWindow2 != null) {
                this.moreWindow2.dismiss();
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void popWindow2(View parent) {
		LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = lay.inflate(R.layout.weixin_manager_menu, null);
		RelativeLayout account = (RelativeLayout) view
				.findViewById(R.id.account);
		RelativeLayout send = (RelativeLayout) view.findViewById(R.id.send);
		RelativeLayout weixin_set = (RelativeLayout) view
				.findViewById(R.id.weixin_set);
		RelativeLayout news = (RelativeLayout) view.findViewById(R.id.news);
		RelativeLayout people = (RelativeLayout) view.findViewById(R.id.people);
		RelativeLayout menu = (RelativeLayout) view.findViewById(R.id.menu);
		// RelativeLayout syn_menu = (RelativeLayout) view
		// .findViewById(R.R.id.syn_menu);
		weixin_set.setOnClickListener(this);
		send.setOnClickListener(this);
		account.setOnClickListener(this);
		news.setOnClickListener(this);
		people.setOnClickListener(this);
		menu.setOnClickListener(this);

		// syn_menu.setOnClickListener(WeixinActivity.this);
        this.moreWindow2 = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		// �˶�ʵ�ֵ���հ״�����popwindow
        this.moreWindow2.setFocusable(true);
        this.moreWindow2.setOutsideTouchable(false);
        this.moreWindow2.setBackgroundDrawable(new BitmapDrawable());
        this.moreWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
                WeixinActivity.this.isOpenPop = false;
			}
		});
        this.moreWindow2.showAtLocation(parent, Gravity.RIGHT | Gravity.TOP, 10,
                this.cloudTitle.getHeight() * 3 / 2);
        this.moreWindow2.update();
	}

	@SuppressLint("ShowToast")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.more_btn2:
            this.finish();
			// overridePendingTransition(R.anim.push_down, R.anim.push_up);
			break;
		case R.id.account:
            this.moreWindow2.dismiss();
			Intent account = new Intent();
			account.setClass(this, WeixinAccountActivity.class);
            this.startActivity(account);
            this.finish();
			break;
		case R.id.weixin_set:
            this.moreWindow2.dismiss();
            this.isAccount = false;
			Intent set = new Intent();
			set.setClass(this, WeixinSetUpActivity.class);
            this.startActivity(set);
			break;
		case R.id.send:
            this.moreWindow2.dismiss();
            this.isFresh = false;
            this.isAccount = false;
			Intent send = new Intent();
			send.setClass(this, WeixinSendManager.class);
            this.startActivity(send);
			break;
		case R.id.news:
            this.moreWindow2.dismiss();
            this.isAccount = false;
            this.isFresh = false;
			Intent news = new Intent();
			news.setClass(this, WeixinNewMessage.class);
            this.startActivity(news);
			break;
		case R.id.people:
            this.moreWindow2.dismiss();
            this.isAccount = false;
            this.isFresh = false;
			Intent people = new Intent();
			people.setClass(this, WeiXinAddressActivity.class);
            this.startActivity(people);
			break;
		case R.id.menu:
            this.moreWindow2.dismiss();
            this.isAccount = false;
			Intent intent = new Intent();
			intent.setClass(this, WeixinMenuActivity.class);
            this.startActivity(intent);
			break;
		case R.id.search_btn2:
            this.changMorePopState2(v);
			break;
		case R.id.ibtnback:
            this.toolBar.setVisibility(View.GONE);
            this.toolBar2.setVisibility(View.VISIBLE);
			break;
		}
	}

	public void startAnimation() {
        this.loadingAnima = (AnimationDrawable) this.iv.getBackground();
        this.loadingAnima.start();
        this.loadingIV.setVisibility(View.VISIBLE);
	}

	public void stopAnimation() {
		// loadingAnima.stop();
        this.loadingIV.setVisibility(View.INVISIBLE);
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	private void connect(int count, int flag) {
		WeixinActivity.GetAsyncTask task = new WeixinActivity.GetAsyncTask(count, this, flag);
		task.execute();
	}

	private void connect(int count, String token, int flag) {
		WeixinActivity.GetAsyncTask task = new WeixinActivity.GetAsyncTask(count, this, token, flag);
		task.execute();
	}
	/**
	 * 生成该类的对象，并调用execute方法之后 首先执行的是onProExecute方法 其次执行doInBackgroup方法
	 * 
	 */
	class GetAsyncTask extends AsyncTask<Integer, Integer, String> {

		private final Context context;
		private final int count;
		private String Token;
		private final int flag;

		public GetAsyncTask(int count, Context context, int flag) {
            this.context = context;
			this.count = count;
			this.flag = flag;
		}

		public GetAsyncTask(int count, Context context, String Token, int flag) {
            this.context = context;
			this.count = count;
			this.Token = Token;
			this.flag = flag;
		}

		// 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
		@Override
		protected void onPreExecute() {
			if (this.count == 0)
                WeixinActivity.this.startAnimation();
		}

		/**
		 * 这里的Integer参数对应AsyncTask中的第一个参数 这里的String返回值对应AsyncTask的第三个参数
		 * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
		 * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
		 */
		@Override
		protected String doInBackground(Integer... params) {
			HttpConnectionUtil connection = new HttpConnectionUtil();
			if (this.count == 0) {
                WeixinActivity.this.doDeploy();
				String verifyURL = WeixinActivity.this.IniFile.getIniString(userIni, "WeiXin",
						"WeToken", "http://e.tbs.com.cn/wechat.do", (byte) 0)
						+ "?action=getToken";
				return connection.asyncConnect(verifyURL, null, HttpConnectionUtil.HttpMethod.GET,
                        this.context);
			} else {
				String verifyURL = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token="
						+ this.Token;
				return connection.asyncConnect(verifyURL, null,
						HttpConnectionUtil.HttpMethod.POST, this.context);
			}
		}

		/**
		 * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
		 * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
		 */
		@Override
		protected void onPostExecute(String result) {
			if (result == null) {
                WeixinActivity.this.stopAnimation();
				Toast.makeText(this.context, "请检查网络设置", Toast.LENGTH_SHORT).show();
			} else {
				if (this.count == 0) {
					if (result == "" || result.equalsIgnoreCase("请求无响应")) {
						if (this.flag == 1) {
							Toast.makeText(this.context, "同步失败，请稍后重试",
									Toast.LENGTH_SHORT).show();
                            WeixinActivity.this.stopAnimation();
						} else {
							result = "{\"menu\":{\"button\":[{\"name\":\"林业信息\",\"sub_button\":[{\"name\":\"林业新闻\",\"type\":\"view\",\"url\":\"http://e.tbs.com.cn:801/tbsnews/page/listnrfl_wx.cbs?resname=mrxw\"},{\"name\":\"获奖成果\",\"type\":\"view\",\"url\":\"http://e.tbs.com.cn:801/tbsnews/page/listnrfl_wx.cbs?resname=gjjlcg\"}]},{\"name\":\"林业科普\",\"sub_button\":[{\"name\":\"授权新品种\",\"type\":\"view\",\"url\":\"http://e.tbs.com.cn:801/tbsnews/page/listnrfl_wx.cbs?resname=sqpzsjk\"},{\"name\":\"濒危动植物\",\"type\":\"view\",\"url\":\"http://e.tbs.com.cn:801/tbsnews/page/listnrfl_wx.cbs?resname=dwbh\"}]},{\"name\":\"技术支持\",\"sub_button\":[{\"name\":\"APP下载\",\"type\":\"view\",\"url\":\"http://e.tbs.com.cn:801/tbsnews/home/tbsapp.cbs\"},{\"name\":\"技术支持\",\"type\":\"view\",\"url\":\"http://e.tbs.com.cn:801/tbsnews/home/introduce.cbs\"},{\"name\":\"官方网站\",\"type\":\"view\",\"url\":\"http://www.lknet.ac.cn\"}]}]}}";

                            WeixinActivity.this.init(result);
                            WeixinActivity.this.stopAnimation();
						}
					} else {
                        WeixinActivity.this.connect(1, result, this.flag);
					}
				} else {
					if (this.flag == 1) {
                        WeixinActivity.this.iniMenu(result);
                        WeixinActivity.this.stopAnimation();
					} else {
                        WeixinActivity.this.init(result);
                        WeixinActivity.this.stopAnimation();
					}
				}
			}
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

	private boolean doDeploy() {
		String webRoot = UIHelper.getShareperference(this,
				constants.SAVE_INFORMATION, "Path", "");
		if (webRoot.endsWith("/") == false) {
			webRoot += "/";
		}
		String doPath = webRoot
				+ "WeiXin/"
				+ this.IniFile.getIniString(userIni, "WeiXin", "Account",
						"TBS软件", (byte) 0);
		File webRootFile = new File(doPath);
		String launchState = UIHelper.getShareperference(this,
				constants.SHARED_PREFERENCE_FIRST_LAUNCH, "launchState",
				"1.0.0");
		if ((webRootFile.exists() && webRootFile.isDirectory()) == false
				|| !launchState.equals(this.getVersionName())) {
			webRootFile.mkdirs();
		} else {
			return true;
		}
		String webRootTbk = webRoot + "WeiXin.tbk";
		try {
			InputStream is = this.getBaseContext().getAssets().open(
					"config/WeiXin.tbk");
			OutputStream os = new FileOutputStream(webRootTbk);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
			os.flush();
			os.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		int resoult = JTbszlib.UnZipFile(webRootTbk, doPath, 1, "");
		if (0 != resoult) {
			return false;
		}
        this.delZipFile(webRootTbk);
		return true;
	}

	// 获取程序的版本号
	private String getVersionName() {
		// ��ȡpackagemanager��ʵ��
		PackageManager packageManager = this.getPackageManager();
		// getPackageName()���㵱ǰ��İ���0����ǻ�ȡ�汾��Ϣ
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return packInfo.versionName;
	}

	protected void delZipFile(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
	}
}
