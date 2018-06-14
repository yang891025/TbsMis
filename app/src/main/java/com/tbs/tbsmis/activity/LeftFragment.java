package com.tbs.tbsmis.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

@SuppressLint({ "SetJavaScriptEnabled", "ValidFragment" })
public class LeftFragment extends Fragment implements View.OnClickListener,
		View.OnTouchListener, GestureDetector.OnGestureListener {
	private RelativeLayout loadingIV;
	private TextView title1;
	private ImageView iv;
	private ImageView menuData;
	private WebView webview;
	private ImageView refreshBtn;
	private boolean loadingDialogState;
	protected int flag;
	private AnimationDrawable loadingAnima;
	private String tempUrl, resname;
	private GestureDetector mGestureDetector;
	// private ProgressBar pb;
	private RelativeLayout leftmenu_title;
	private View view;
	private LeftFragment.MyBroadcastReciver MyBroadcastReciver;
	private String appNewsFile;
	private IniFile IniFile;
	// private String appUserFile;
	private String userIni;
	private String baseUrl;

	// ���ƿؼ��ĸ߶ȺͿ��
	// LinearLayout leftview;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("loadLeft"
				+ getString(R.string.about_title));
		MyActivity.getInstance().addActivity(this.getActivity());
        this.MyBroadcastReciver = new LeftFragment.MyBroadcastReciver();
        this.getActivity().registerReceiver(this.MyBroadcastReciver, intentFilter);
        this.view = inflater.inflate(R.layout.left, null);
		// pb = (ProgressBar) view.findViewById(R.id.left_progressbar4);
        this.title1 = (TextView) this.view.findViewById(R.id.tool_date_text);
        this.loadingIV = (RelativeLayout) this.view.findViewById(R.id.loading_dialog);
        this.leftmenu_title = (RelativeLayout) this.view
				.findViewById(R.id.leftmenu_title);
        this.iv = (ImageView) this.view.findViewById(R.id.gifview);
        this.menuData = (ImageView) this.view.findViewById(R.id.menudata);
        this.refreshBtn = (ImageView) this.view.findViewById(R.id.refresh_btn);
        this.webview = (WebView) this.view.findViewById(R.id.webview2);
		/**
		 * webview��������
		 */
        this.webview.getSettings().setSupportZoom(true);
        this.webview.getSettings().setBuiltInZoomControls(true);
        this.webview.getSettings().setJavaScriptEnabled(true);
		// 3.0版本才有setDisplayZoomControls的功能
		if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1) {
            this.webview.getSettings().setDisplayZoomControls(false);
		}
        this.webview.getSettings().setSupportMultipleWindows(true);
        this.webview.getSettings().setDomStorageEnabled(true);
        this.webview.getSettings().setDefaultTextEncodingName("gb2312");
        this.webview.getSettings().setUseWideViewPort(true);
        this.webview.getSettings().setLoadWithOverviewMode(true);
        this.webview.setOnTouchListener(this);
		// 修改ua使得web端正确判断 @ 2013-11-07 11:13:28
		String ua = this.webview.getSettings().getUserAgentString();
        this.webview.getSettings().setUserAgentString(ua + "; tbsmis/2015");
		
		UIHelper.addJavascript(this.getActivity(), this.webview);
        this.webview.setFocusable(true);
        this.webview.requestFocus();
        this.refreshBtn.setOnClickListener(this);
        this.menuData.setOnClickListener(this);
		return this.view;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        this.IniFile = new IniFile();
        this.iniPath();
		// appNewsFile = webRoot + constants.SD_CARD_TBSAPP_PATH + "/"
		// + constants.NEWS_CONFIG_FILE_NAME;
		int MenuLeftBar = Integer.parseInt(this.IniFile.getIniString(userIni,
				"APPSHOW", "MenuLeftBar", "0", (byte) 0));
		int MenuRightBar = Integer.parseInt(this.IniFile.getIniString(userIni,
				"APPSHOW", "MenuRightBar", "0", (byte) 0));
		int ShowMenuTitle = Integer.parseInt(this.IniFile.getIniString(userIni,
				"APPSHOW", "ShowMenuTitle", "1", (byte) 0));
		if (MenuLeftBar == 0) {
            this.menuData.setVisibility(View.GONE);
		}
		if (MenuRightBar == 0) {
            this.refreshBtn.setVisibility(View.GONE);
		}
        this.menuData.setBackgroundResource(constants.TopButtonIcoId[Integer
				.parseInt(this.IniFile.getIniString(userIni, "BUTTON",
						"TOP1ImageId", "0", (byte) 0))]);
        this.refreshBtn.setBackgroundResource(constants.TopButtonIcoId[Integer
				.parseInt(this.IniFile.getIniString(userIni, "BUTTON",
						"TOP2ImageId", "1", (byte) 0))]);
		String Title = this.IniFile.getIniString(userIni, "APPSHOW",
				"MenuTitle", "", (byte) 0);
        this.title1.setText(Title);
		if (ShowMenuTitle == 0) {
            this.leftmenu_title.setVisibility(View.GONE);
		}
		// 实例化手势对�?
        this.mGestureDetector = new GestureDetector(this);
        this.mGestureDetector.setIsLongpressEnabled(true);
        this.webview.setWebViewClient(UIHelper.getWebViewClient());
        this.webview.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int progress) {
                LeftFragment.this.loadingDialogState = progress < 100;
				if (LeftFragment.this.loadingDialogState) {
                    LeftFragment.this.startAnimation();
				} else {
                    LeftFragment.this.stopAnimation();
				}
			}
		});
		if (UIHelper.TbsMotion(this.getActivity(), this.tempUrl)) {
            this.webview.loadUrl(this.tempUrl);
		}
        this.loadingIV.setVisibility(View.INVISIBLE);
	}

	private void iniPath() {
		// TODO Auto-generated method stub
		String webRoot = UIHelper.getSoftPath(this.getActivity());
		if (webRoot.endsWith("/") == false) {
			webRoot += "/";
		}
		webRoot += getString(R.string.SD_CARD_TBSAPP_PATH2);
		webRoot = UIHelper.getShareperference(this.getActivity(),
				constants.SAVE_INFORMATION, "Path", webRoot);
		if (webRoot.endsWith("/") == false) {
			webRoot += "/";
		}
		String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        appNewsFile = webRoot
				+ IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
						constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        userIni = appNewsFile;
        if (Integer.parseInt(IniFile.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = getActivity().getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
		String ipUrl = this.IniFile.getIniString(userIni, "SERVICE",
				"currentAddress", constants.DefaultLocalIp, (byte) 0);
		String portUrl = this.IniFile.getIniString(userIni, "SERVICE",
				"currentPort", constants.DefaultLocalPort, (byte) 0);
        this.baseUrl = "http://" + ipUrl + ":" + portUrl;
		String Topmenu = this.IniFile.getIniString(userIni, "TBSAPP", "leftUrl",
				"", (byte) 0);
        this.resname = this.IniFile.getIniString(userIni, "TBSAPP", "resname", "",
				(byte) 0);
        this.tempUrl = StringUtils.isUrl(Topmenu, this.baseUrl, this.resname);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
        this.webview.setBackgroundColor(Color.parseColor(this.IniFile
				.getIniString(this.appNewsFile, "BASIC_SETUP", "BackColorValue",
						"#f6f6f6", (byte) 0)));
		// 3.0版本才有setDisplayZoomControls的功能
		if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1) {
            this.webview.getSettings().setTextZoom(
					Integer.parseInt(this.IniFile.getIniString(this.appNewsFile,
							"BASIC_SETUP", "TextZoom", "100", (byte) 0)));
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// MyActivity.getInstance().finishActivity(getActivity());
		if (this.webview != null) {
            this.webview.clearCache(true);
            this.webview.setVisibility(View.GONE);
            this.webview.destroy();
            this.webview = null;
		}
        this.getActivity().unregisterReceiver(this.MyBroadcastReciver);
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

	private class MyBroadcastReciver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
            LeftFragment.this.iniPath();
			String action = intent.getAction();
			if (action.equals("loadLeft"
					+ getString(R.string.about_title))) {
				int SelBtn = intent.getIntExtra("flag", 1);
				switch (SelBtn) {
				case 0:
					if (UIHelper.TbsMotion(LeftFragment.this.getActivity(), LeftFragment.this.tempUrl)) {
                        LeftFragment.this.webview.loadUrl(LeftFragment.this.tempUrl);
                        //System.out.println("leftFragmentReciver"+tempUrl);
					}
					break;
				case 1:
					String sourceUrl = intent.getStringExtra("sourceUrl");
                    LeftFragment.this.tempUrl = StringUtils.isUrl(sourceUrl, LeftFragment.this.baseUrl, UIHelper
							.getShareperference(context,
									constants.SAVE_LOCALMSGNUM, "resname",
									"yqxx"));
					if (UIHelper.TbsMotion(LeftFragment.this.getActivity(), LeftFragment.this.tempUrl)) {
                        LeftFragment.this.webview.loadUrl(LeftFragment.this.tempUrl);
					}
					break;
				case 2:
					String title = intent.getStringExtra("name");
                    LeftFragment.this.title1.setText(title);
					break;
				case 3:
					int MenuLeftBar = Integer.parseInt(LeftFragment.this.IniFile.getIniString(
                            userIni, "APPSHOW", "MenuLeftBar", "0",
							(byte) 0));
					int MenuRightBar = Integer.parseInt(LeftFragment.this.IniFile.getIniString(
                            userIni, "APPSHOW", "MenuRightBar", "0",
							(byte) 0));
					int ShowMenuTitle = Integer.parseInt(LeftFragment.this.IniFile.getIniString(
                            userIni, "APPSHOW", "ShowMenuTitle", "1",
							(byte) 0));
					if (MenuLeftBar == 0) {
                        LeftFragment.this.menuData.setVisibility(View.GONE);
					} else {
                        LeftFragment.this.menuData.setVisibility(View.VISIBLE);
					}
					if (MenuRightBar == 0) {
                        LeftFragment.this.refreshBtn.setVisibility(View.GONE);
					} else {
                        LeftFragment.this.refreshBtn.setVisibility(View.VISIBLE);
					}
                    LeftFragment.this.title1.setText(LeftFragment.this.IniFile.getIniString(userIni, "APPSHOW",
							"MenuTitle", "", (byte) 0));
					if (ShowMenuTitle == 0) {
                        LeftFragment.this.leftmenu_title.setVisibility(View.GONE);
					} else {
                        LeftFragment.this.leftmenu_title.setVisibility(View.VISIBLE);
					}
					break;
				case 4:
                    LeftFragment.this.menuData.setBackgroundResource(constants.TopButtonIcoId[Integer
							.parseInt(LeftFragment.this.IniFile.getIniString(userIni,
									"BUTTON", "TOP1ImageId", "0", (byte) 0))]);
                    LeftFragment.this.refreshBtn
							.setBackgroundResource(constants.TopButtonIcoId[Integer
									.parseInt(LeftFragment.this.IniFile.getIniString(userIni,
											"BUTTON", "TOP2ImageId", "1",
											(byte) 0))]);
					break;
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
        this.iniPath();
		switch (v.getId()) {
		case R.id.refresh_btn:
			String Topmenu = this.IniFile.getIniString(userIni, "BUTTONURL",
					"TOP2", "", (byte) 0);
            this.tempUrl = StringUtils.isUrl(Topmenu, this.baseUrl, this.resname);
			if (UIHelper.TbsMotion(this.getActivity(), this.tempUrl)) {
				if (!UIHelper.MenuMotion(this.getActivity(), this.tempUrl)) {
					Intent intent = new Intent();
                    intent.setAction("Action_main"
							+ getString(R.string.about_title));
					intent.putExtra("tempUrl", this.tempUrl);
					intent.putExtra("flag", 21);
                    this.getActivity().sendBroadcast(intent);
				} else {
                    this.webview.loadUrl(this.tempUrl);
				}

			}
			break;
		case R.id.menudata:
			String Topmore = this.IniFile.getIniString(userIni, "BUTTONURL",
					"TOP1", "", (byte) 0);
            this.tempUrl = StringUtils.isUrl(Topmore, this.baseUrl, this.resname);
			if (UIHelper.TbsMotion(this.getActivity(), this.tempUrl)) {
				if (!UIHelper.MenuMotion(this.getActivity(), this.tempUrl)) {
					Intent intent = new Intent();
                    intent.setAction("Action_main"
							+ getString(R.string.about_title));
					intent.putExtra("tempUrl", this.tempUrl);
					intent.putExtra("flag", 21);
                    this.getActivity().sendBroadcast(intent);
				} else {
                    this.webview.loadUrl(this.tempUrl);
				}

			}
			break;
		}
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		return this.mGestureDetector.onTouchEvent(arg1);
		// }
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		float diffX = e1.getX() - e2.getX();
		float diffY = e1.getY() - e2.getY();
		// TODO Auto-generated method stub
		if (diffY > this.webview.getHeight() / 5 && Math.abs(velocityX) > 30) {
            this.webview.loadUrl("javascript:jump()");
		}else if (diffY < -(this.webview.getHeight() / 5) && Math.abs(velocityX) > 30) {
            this.webview.loadUrl("javascript:OnUpdate()");
		} else if (diffX > this.webview.getWidth() / 5 && Math.abs(diffX) > Math.abs(diffY)
				&& Math.abs(velocityX) > 20) {
            this.webview.loadUrl("javascript:tonext()");
            this.webview.loadUrl("javascript:jumppage(3)");
		} else if (diffX < -(this.webview.getWidth() / 5) && Math.abs(diffX) > Math.abs(diffY)
				&& Math.abs(velocityX) > 20) {
            this.webview.loadUrl("javascript:toprev()");
            this.webview.loadUrl("javascript:jumppage(2)");
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
}
