package com.tbs.tbsmis.Musics.fragment;

import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tbs.tbsmis.Live.utils.DataUtils;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.util.UIHelper;


/**
 * Created by ELVIS on 2015/10/25.
 */
public class showMusicContent extends Fragment{
    private String mCode;
    private TextView contentView;
    private WebView webview;
    private AnimationDrawable loadingAnima;
    private RelativeLayout loadingIV;
    private ImageView iv;
    private boolean loadingDialogState;
    private RelativeLayout content_webview;

    public static showMusicContent newInstance(String code) {
        showMusicContent fragment = new showMusicContent();
        Bundle bundle = new Bundle();
        bundle.putString("code", code);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        if (args != null) {
            mCode = args.getString("code");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_rec, container, false);
        //inflater.inflate(R.layout.fragment_pot, container, false);
        this.initViews(view);
        return view;
    }

    private void initViews(View view) {
        this.contentView = (TextView) view.findViewById(R.id.content);
        this.content_webview = (RelativeLayout) view.findViewById(R.id.content_webview);
        iv = (ImageView) view.findViewById(R.id.gifview);
        loadingIV = (RelativeLayout) view.findViewById(R.id.loading_dialog);
        webview = (WebView) view.findViewById(R.id.video_content);
        webview.getSettings().setJavaScriptEnabled(true);// 允许JS执行
        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setBuiltInZoomControls(true);
        UIHelper.addJavascript(this.getContext(), webview);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webview.getSettings().setDisplayZoomControls(false);
        }
        // 取消滚动
        webview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                loadingDialogState = progress < 100;
                if (loadingDialogState) {
                    startAnimation();
                } else {
                    stopAnimation();
                }

            }
        });
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);// 点击超链接的时候重新在原来进程上加载URL
                return true;
            }
        });
//        String webRoot = UIHelper.getSoftPath(getActivity());
//        if (webRoot.endsWith("/") == false) {
//            webRoot += "/";
//        }
//        webRoot += getString(R.string.SD_CARD_TBSAPP_PATH2);
//        webRoot = UIHelper.getShareperference(getActivity(), constants.SAVE_INFORMATION,
//                "Path", webRoot);
//        if (webRoot.endsWith("/") == false) {
//            webRoot += "/";
//        }
//        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
//        IniFile m_iniFileIO = new IniFile();
//        String appNewsFile = webRoot
//                + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
//                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
//        String ipUrl = m_iniFileIO.getIniString(appNewsFile, "TBSAPP",
//                "webAddress", constants.DefaultServerIp, (byte) 0);
//        String portUrl = m_iniFileIO.getIniString(appNewsFile, "TBSAPP",
//                "webPort", constants.DefaultServerPort, (byte) 0);
//        String baseUrl = "http://" + ipUrl + ":" + portUrl;
        webview.loadUrl(DataUtils.HOST+ "project/info_mobileApi.cbs?file=@20&id="+mCode);
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
}
