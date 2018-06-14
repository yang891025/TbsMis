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
public class RightFragment extends Fragment implements View.OnClickListener,
		View.OnTouchListener, GestureDetector.OnGestureListener {
	// private ProgressBar pb;
	private RelativeLayout loadingIV;
	private TextView more_title;
	private ImageView iv;
	private WebView webview;
	private boolean loadingDialogState;
	private AnimationDrawable loadingAnima;
	private ImageView setupInfo;
	private ImageView menuData;
	private String tempUrl;

	private IniFile m_iniFileIO;
	private GestureDetector mRightGestureDetector;
	private RightFragment.MyBroadcastReciver MyBroadcastReciver;
	private RelativeLayout layoutUserInfo;
//	private String appTestFile;
    private String userIni;
	private String appNewsFile;
	private String baseUrl;
	private String Topmore;
	private String resname;

	@Override
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("loadRight"
				+ getString(R.string.about_title));
        this.mRightGestureDetector = new GestureDetector(this);
        this.MyBroadcastReciver = new RightFragment.MyBroadcastReciver();
        this.getActivity().registerReceiver(this.MyBroadcastReciver, intentFilter);
		View view = inflater.inflate(R.layout.right_menu_list, null);
		MyActivity.getInstance().addActivity(this.getActivity());
        this.more_title = (TextView) view.findViewById(R.id.more_title);
		// pb = (ProgressBar) view.findViewById(R.R.id.progressbar_right);
        this.loadingIV = (RelativeLayout) view.findViewById(R.id.loading_dialog2);
        this.layoutUserInfo = (RelativeLayout) view
				.findViewById(R.id.layoutUserInfo);
        this.iv = (ImageView) view.findViewById(R.id.gifview2);
        this.menuData = (ImageView) view.findViewById(R.id.loginpage);
        this.webview = (WebView) view.findViewById(R.id.webview_right);
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
        this.webview.getSettings().setDefaultTextEncodingName("gb2312");
        this.webview.getSettings().setDomStorageEnabled(true);
        this.webview.getSettings().setUseWideViewPort(true);
        this.webview.getSettings().setLoadWithOverviewMode(true);
		
		// 修改ua使得web端正确判断 @ 2013-11-07 11:13:28
		String ua = this.webview.getSettings().getUserAgentString();
        this.webview.getSettings().setUserAgentString(ua + "; tbsmis/2015");
		
		UIHelper.addJavascript(this.getActivity(), this.webview);
        this.webview.setOnTouchListener(this);
        this.menuData.setOnClickListener(this);
        this.setupInfo = (ImageView) view.findViewById(R.id.setupInfo);
        this.setupInfo.setOnClickListener(this);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        this.iniPath();
		int MoreLeftBar = Integer.parseInt(this.m_iniFileIO.getIniString(
                userIni, "APPSHOW", "MoreLeftBar", "0", (byte) 0));
		int MoreRightBar = Integer.parseInt(this.m_iniFileIO.getIniString(
                userIni, "APPSHOW", "MoreRightBar", "0", (byte) 0));
		int ShowMoreTitle = Integer.parseInt(this.m_iniFileIO.getIniString(
                userIni, "APPSHOW", "ShowMoreTitle", "0", (byte) 0));
		if (MoreLeftBar == 0) {
            this.menuData.setVisibility(View.GONE);
		}
		if (MoreRightBar == 0) {
            this.setupInfo.setVisibility(View.GONE);
		}
        this.menuData.setBackgroundResource(constants.TopButtonIcoId[Integer
				.parseInt(this.m_iniFileIO.getIniString(userIni, "BUTTON",
						"TOP3ImageId", "2", (byte) 0))]);
        this.setupInfo.setBackgroundResource(constants.TopButtonIcoId[Integer
				.parseInt(this.m_iniFileIO.getIniString(userIni, "BUTTON",
						"TOP4ImageId", "3", (byte) 0))]);
		if (ShowMoreTitle == 0) {
            this.layoutUserInfo.setVisibility(View.GONE);
		}
		String Title = this.m_iniFileIO.getIniString(userIni, "APPSHOW",
				"MoreTitle", "", (byte) 0);
        this.more_title.setText(Title);
        this.webview.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int progress) {
                RightFragment.this.loadingDialogState = progress < 100;
				if (RightFragment.this.loadingDialogState) {
                    RightFragment.this.startAnimation();
				} else {
                    RightFragment.this.stopAnimation();
				}
			}
		});
        this.webview.setWebViewClient(UIHelper.getWebViewClient());
		if (UIHelper.TbsMotion(this.getActivity(), this.tempUrl)) {
            this.webview.loadUrl(this.tempUrl);
		}
		System.out.println(this.tempUrl);
        this.loadingIV.setVisibility(View.INVISIBLE);
	}

	private void iniPath() {
		// TODO Auto-generated method stub
        this.m_iniFileIO = new IniFile();
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
        this.appNewsFile = webRoot
				+ this.m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
						constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        userIni = appNewsFile;
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = getActivity().getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        this.resname = UIHelper.getShareperference(this.getActivity(),
				constants.SAVE_LOCALMSGNUM, "resname", "yqxx");
		String ipUrl = this.m_iniFileIO.getIniString(userIni, "SERVICE",
				"currentAddress", constants.DefaultLocalIp, (byte) 0);
		String portUrl = this.m_iniFileIO.getIniString(userIni, "SERVICE",
				"currentPort", constants.DefaultLocalPort, (byte) 0);
        this.baseUrl = "http://" + ipUrl + ":" + portUrl;
        this.Topmore = this.m_iniFileIO.getIniString(userIni, "TBSAPP", "rightUrl",
				"", (byte) 0);
//		if (StringUtils.isEmpty(this.Topmore)) {
//            this.Topmore = this.m_iniFileIO.getIniString(userIni, "BUTTONURL",
//					"TOP3", "", (byte) 0);
//		}
        this.tempUrl = StringUtils.isUrl(this.Topmore, this.baseUrl, this.resname);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
        this.webview.setBackgroundColor(Color
				.parseColor(this.m_iniFileIO.getIniString(this.appNewsFile,
						"BASIC_SETUP", "BackColorValue", "#f6f6f6", (byte) 0)));
		// 3.0版本才有setDisplayZoomControls的功能
		if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1) {
            this.webview.getSettings().setTextZoom(
					Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile,
							"BASIC_SETUP", "TextZoom", "100", (byte) 0)));
		}

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
        this.getActivity().unregisterReceiver(this.MyBroadcastReciver);
		if (this.webview != null) {
            this.webview.clearCache(true);
            this.webview.setVisibility(View.GONE);
            this.webview.destroy();
            this.webview = null;
		}
	}

	@Override
	public void onClick(View v) {
        this.iniPath();
		switch (v.getId()) {
		case R.id.setupInfo:
			String Topmenu = this.m_iniFileIO.getIniString(userIni, "BUTTONURL",
					"TOP4", "", (byte) 0);
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
		case R.id.loginpage:
			String Topmore = this.m_iniFileIO.getIniString(userIni, "BUTTONURL",
					"TOP3", "", (byte) 0);
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
			String action = intent.getAction();
            RightFragment.this.iniPath();
			if (action.equals("loadRight"
					+ getString(R.string.about_title))) {
				int SelBtn = intent.getIntExtra("flag", 1);
				switch (SelBtn) {
				case 1:
					if (UIHelper.TbsMotion(RightFragment.this.getActivity(), RightFragment.this.tempUrl)) {
                        RightFragment.this.webview.loadUrl(RightFragment.this.tempUrl);
					}
					break;
				case 2:
					String tempUrl = intent.getStringExtra("tempUrl");
					String ResName = intent.getStringExtra("ResName");
					tempUrl = StringUtils.isUrl(tempUrl, RightFragment.this.baseUrl, ResName);
					if (UIHelper.TbsMotion(RightFragment.this.getActivity(), tempUrl)) {
                        RightFragment.this.webview.loadUrl(tempUrl);
					}
                    RightFragment.this.more_title.setText(ResName);
					break;
				case 3:
					String Title = intent.getStringExtra("name");
                    RightFragment.this.more_title.setText(Title);
					break;
				case 4:
                    RightFragment.this.layoutUserInfo.setVisibility(View.GONE);
					int MoreLeftBar = Integer.parseInt(RightFragment.this.m_iniFileIO
							.getIniString(userIni, "APPSHOW",
									"MoreLeftBar", "0", (byte) 0));
					int MoreRightBar = Integer.parseInt(RightFragment.this.m_iniFileIO
							.getIniString(userIni, "APPSHOW",
									"MoreRightBar", "0", (byte) 0));
					int ShowMoreTitle = Integer.parseInt(RightFragment.this.m_iniFileIO
							.getIniString(userIni, "APPSHOW",
									"ShowMoreTitle", "0", (byte) 0));
					if (MoreLeftBar == 0) {
                        RightFragment.this.menuData.setVisibility(View.GONE);
					} else {
                        RightFragment.this.menuData.setVisibility(View.VISIBLE);
					}
					if (MoreRightBar == 0) {
                        RightFragment.this.setupInfo.setVisibility(View.GONE);
					} else {
                        RightFragment.this.setupInfo.setVisibility(View.VISIBLE);
					}
					if (ShowMoreTitle == 0) {
                        RightFragment.this.layoutUserInfo.setVisibility(View.GONE);
					} else {
                        RightFragment.this.layoutUserInfo.setVisibility(View.VISIBLE);
					}
                    RightFragment.this.more_title.setText(RightFragment.this.m_iniFileIO.getIniString(userIni,
							"APPSHOW", "MoreTitle", "0", (byte) 0));

					break;
				case 5:
                    RightFragment.this.menuData.setBackgroundResource(constants.TopButtonIcoId[Integer
							.parseInt(RightFragment.this.m_iniFileIO.getIniString(userIni,
									"BUTTON", "TOP3ImageId", "2", (byte) 0))]);
                    RightFragment.this.setupInfo
							.setBackgroundResource(constants.TopButtonIcoId[Integer
									.parseInt(RightFragment.this.m_iniFileIO.getIniString(
                                            userIni, "BUTTON",
											"TOP4ImageId", "3", (byte) 0))]);
					break;
				}
			}
		}
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return this.mRightGestureDetector.onTouchEvent(arg1);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
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
