package com.tbs.circle.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lzy.imagepicker.Utils;
import com.tbs.circle.R;

/**
 * Created by TBS on 2017/10/17.
 */

public class CircleWebViewActivity extends YWActivity
{


    private ProgressBar progressBar1;
    private WebView webView;
    private TextView tv_des;
    private View topBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        //因为状态栏透明后，布局整体会上移，所以给头部加上状态栏的margin值，保证头部不会被覆盖
        topBar = findViewById(R.id.include);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) topBar.getLayoutParams();
            params.topMargin = Utils.getStatusHeight(this);
            topBar.setLayoutParams(params);
        }

        tv_des = (TextView) findViewById(R.id.tv_des);
        Button mBtnOk = (Button) findViewById(R.id.btn_ok);
        mBtnOk.setVisibility(View.INVISIBLE);
        ImageView mBtnBack = (ImageView) findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initView();
    }

    public void initView() {
        progressBar1 = (ProgressBar) this.findViewById(R.id.progressBar1);
        String url = this.getIntent().getStringExtra("url");
        String title = this.getIntent().getStringExtra("title");
        tv_des.setText(title);
        //System.out.println("url------------------->>>"+url);
        webView = (WebView) this.findViewById(R.id.webView);
        //设置 缓存模式
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        // 开启 DOM storage API 功能
        webView.getSettings().setDomStorageEnabled(true);

        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar1.setVisibility(View.GONE);

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)

            { // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边

                view.loadUrl(url);
                return true;

            }

        });
        webView.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                String url = view.getUrl();
                if(!title.contains("://") ||!url.contains(title))
                    tv_des.setText(title);
            }

        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (webView.canGoBack())
                webView.goBack();
            else
                finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
