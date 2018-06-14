package com.tbs.tbsmis.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tbs.ini.IniFile;
import com.tbs.player.bean.VideoijkBean;
import com.tbs.player.listener.OnShowThumbnailListener;
import com.tbs.player.widget.PlayStateParams;
import com.tbs.player.widget.PlayerView;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.FileIO;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.io.File;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;

@SuppressLint("HandlerLeak")
public class VideoPlayerWeb extends AppCompatActivity
{

    private String mPath;
    private String mTitle;
    private PlayerView mVideoView;
    private Video playVideoFile;
    private RelativeLayout llVideo;
    private String mRelateKnowledUrl;
    private String mStartTime;
    private int mPlayTime;
    private String playtime;
    private String mVideoPath;
    private WebView mWebView;
    private AnimationDrawable loadingAnima;
    private RelativeLayout loadingIV;
    private ImageView iv;
    private boolean loadingDialogState;
    private PowerManager.WakeLock wakeLock;
    private boolean isInitCache = false;
    private LinearLayout video_top_box;
    private TextView video_title;
    private ImageView video_finish;
    private ImageView video_display;
    private ImageView app_video_display;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
        // ~~~ 获取播放地址和标题
        initdata();
        initwebview();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        this.recreate();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void initdata() {
        Bundle bundle = this.getIntent().getExtras();
        this.playVideoFile = (Video) bundle.getSerializable("video");
        this.mPath = this.playVideoFile.getPath();
        mVideoPath = StringUtils.getHost(mPath);
        this.mTitle = this.playVideoFile.getTitle();
        this.mRelateKnowledUrl = this.playVideoFile.getRelateKnowledUrl();
        View rootView = getLayoutInflater().from(this).inflate(R.layout.web_video_layout, null);
        setContentView(rootView);
        this.llVideo = (RelativeLayout) rootView.findViewById(R.id.app_video_box);
        mWebView = (WebView) rootView.findViewById(R.id.video_webview);
        video_top_box = (LinearLayout) rootView.findViewById(R.id.video_top_box);
        video_title = (TextView) rootView.findViewById(R.id.video_title);
        video_finish = (ImageView) rootView.findViewById(R.id.video_finish);
        video_display = (ImageView) rootView.findViewById(R.id.video_display);
        app_video_display = (ImageView) rootView.findViewById(R.id.app_video_display);
        mStartTime = StringUtils.getDate();
        /**常亮*/
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "liveTAG");
        wakeLock.acquire();
        String savePath = UIHelper.getStoragePath(this) + "/Log/playerLog.ini";
        File file = new File(savePath);
        if (!file.exists()) {
            FileIO.CreateNewFile(savePath);
        }
        IniFile iniFile = new IniFile();
        playtime = iniFile.getIniString(savePath, playVideoFile.getCode(),
                "playtime", "", (byte) 0);
        if (mPath.isEmpty() || !mPath.contains(".")) {
            llVideo.setVisibility(View.GONE);
            video_top_box.setVisibility(View.GONE);
        } else {
            video_title.setText(mTitle);
            video_top_box.setVisibility(View.GONE);
            video_finish.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            video_display.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    llVideo.setVisibility(View.VISIBLE);
                    video_top_box.setVisibility(View.GONE);
                }
            });
            app_video_display.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    llVideo.setVisibility(View.GONE);
                    video_top_box.setVisibility(View.VISIBLE);
                }
            });
            mVideoView = new PlayerView(this, rootView)
            {
                @Override
                public PlayerView toggleProcessDurationOrientation() {
                    hideSteam(getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    return setProcessDurationOrientation(getScreenOrientation() == ActivityInfo
                            .SCREEN_ORIENTATION_PORTRAIT ? PlayStateParams.PROCESS_PORTRAIT : PlayStateParams
                            .PROCESS_LANDSCAPE);
                }

                @Override
                public PlayerView setPlaySource(List<VideoijkBean> list) {
                    return super.setPlaySource(list);
                }
            }
                    .setTitle(mTitle)
                    //.setProcessDurationOrientation(PlayStateParams.PROCESS_PORTRAIT)
                    .setScaleType(PlayStateParams.wrapcontent)
                    .forbidTouch(false)
                    .hideMenu(true)
                    .hideSteam(true)
                    .hideCenterPlayer(true)
                    .setNetWorkTypeTie(true)
                    .setShowSpeed(true)
                    .showThumbnail(new OnShowThumbnailListener()
                    {
                        @Override
                        public void onShowThumbnail(ImageView ivThumbnail) {
//                            Glide.with(this)
//                                    .load("http://pic2.nipic.com/20090413/406638_125424003_2.jpg")
//                                    .placeholder(R.color.cl_default)
//                                    .error(R.color.cl_error)
//                                    .into(ivThumbnail);
                        }
                    });
            //.setPlaySource(list)
            //.setChargeTie(true, 60);是否收费
//            mVideoView.pausePlay()
            //startPlayPlay();
            //mVideoView.setOnInfoListener()
            mVideoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener()
            {
                @Override
                public void onPrepared(IMediaPlayer mediaPlayer) {
                    if (!playtime.isEmpty()) {
                        if (Long.parseLong(playtime) >= mVideoView.getDuration()) {
                            mVideoView.seekTo(0);
                        } else {
                            mVideoView.seekTo(Integer.parseInt(playtime));
                        }
                    }

                }
            });
            mVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener()
            {
                @Override
                public void onCompletion(IMediaPlayer iMediaPlayer) {
                    isInitCache = true;
                    finish();
                }
            });
            if (this.mPath.startsWith("http:")) {
                this.mVideoView.setPlaySource(this.mPath);
                //this.mVideoView.setVideoURI(Uri.parse("http://e.tbs.com.cn/cese.mp4"));
            } else {
                String webRoot = UIHelper.getStoragePath(this);
                webRoot = webRoot + constants.SD_CARD_TBSFILE_PATH6;
                if (!this.mPath.contains(webRoot))
                    this.mPath = webRoot + "/" + this.mPath;
                this.mVideoView.setPlaySource(this.mPath);
            }
            mVideoView.startPlay();
        }

    }
    
    private void initwebview(){
        this.loadingIV = (RelativeLayout) this.findViewById(R.id.loading_dialog);
        this.iv = (ImageView) this.findViewById(R.id.gifview);
        this.mWebView.getSettings().setSupportZoom(true);
        this.mWebView.getSettings().setBuiltInZoomControls(true);
        this.mWebView.getSettings().setJavaScriptEnabled(true);
        // 3.0版本才有setDisplayZoomControls的功能
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            this.mWebView.getSettings().setDisplayZoomControls(false);
        }
        this.mWebView.getSettings().setSupportMultipleWindows(true);
        this.mWebView.getSettings().setDomStorageEnabled(true);

        this.mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        this.mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        this.mWebView.setWebViewClient(UIHelper.getWebViewClient());
        // 修改ua使得web端正确判断 @ 2013-11-07 11:13:28
        String ua = this.mWebView.getSettings().getUserAgentString();
        this.mWebView.getSettings().setUserAgentString(ua + "; tbsmis/2015");
        UIHelper.addJavascript(this, this.mWebView);
        this.mWebView.setWebChromeClient(new WebChromeClient()
        {
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
        mWebView.loadUrl(mRelateKnowledUrl);
    }

    private void startAnimation() {
        this.loadingAnima = (AnimationDrawable) this.iv.getBackground();
        this.loadingAnima.start();
        this.loadingIV.setVisibility(View.VISIBLE);
    }

    private void stopAnimation() {
        // loadingAnima.stop();
        this.loadingIV.setVisibility(View.INVISIBLE);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//
//        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (mVideoView != null && mVideoView.onBackPressed()) {
            return;
        }
        super.onBackPressed();
        /**demo的内容，恢复设备亮度状态*/
        if (wakeLock != null) {
            wakeLock.release();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this.mVideoView != null) {
            if(mVideoView.getCurrentPosition() > mPlayTime)
            mPlayTime = mVideoView.getCurrentPosition();
            if (isInitCache)
                mPlayTime = (int) mVideoView.getDuration();
            this.mVideoView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.mVideoView != null)
            this.mVideoView.onResume();
        /**demo的内容，激活设备常亮状态*/
        if (wakeLock != null) {
            wakeLock.acquire();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        writeLog();
        if (mVideoView != null) {
            mVideoView.onDestroy();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mVideoView != null) {
            mVideoView.onConfigurationChanged(newConfig);
        }
    }
    public void writeLog() {
            String savePath = UIHelper.getStoragePath(this) + "/Log/playerLog.ini";
            File file = new File(savePath);
            if (!file.exists()) {
                FileIO.CreateNewFile(savePath);
            }
            IniFile iniFile = new IniFile();
            String webRoot = UIHelper.getStoragePath(this);
            webRoot += getApplicationContext().getString(R.string.SD_CARD_TBSAPP_PATH2);
            webRoot = UIHelper.getShareperference(getApplicationContext(),
                    constants.SAVE_INFORMATION, "Path", webRoot);
            if (!webRoot.endsWith("/")) {
                webRoot += "/";
            }
            String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
            String appNewsFile = webRoot
                    + iniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                    constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
            String userIni = appNewsFile;
            if (Integer.parseInt(iniFile.getIniString(userIni, "Login",
                    "LoginType", "0", (byte) 0)) == 1) {
                String dataPath = getApplicationContext().getFilesDir().getParentFile()
                        .getAbsolutePath();
                if (dataPath.endsWith("/") == false) {
                    dataPath = dataPath + "/";
                }
                userIni = dataPath + "TbsApp.ini";
            }
        if (mPlayTime > 0) {
            if (iniFile.getIniString(userIni, "Login",
                    "LoginFlag", "0", (byte) 0) == "1") {
                int count = Integer.parseInt(iniFile.getIniString(savePath, "Log", "count", "0",
                        (byte) 0));
                iniFile.writeIniString(savePath, "log" + (count + 1), "starttime", mStartTime);
                iniFile.writeIniString(savePath, "log" + (count + 1), "UserName", iniFile.getIniString(userIni, "Login",
                        "Account", "", (byte) 0));
                iniFile.writeIniString(savePath, "log" + (count + 1), "code", playVideoFile.getCode());
                iniFile.writeIniString(savePath, "log" + (count + 1), "path", playVideoFile.getTxtPath());
                iniFile.writeIniString(savePath, "log" + (count + 1), "stoptime", StringUtils.getDate());
                iniFile.writeIniString(savePath, "log" + (count + 1), "playtime", mPlayTime + "");
                iniFile.writeIniString(savePath, "Log", "count", (count + 1) + "");
                Intent mIntent = new Intent();
                mIntent.setAction("com.tbs.tbsmis.PlayerLogService");//你定义的service的action
                mIntent.setPackage(getApplicationContext().getPackageName());//这里你需要设置你应用的包名
                getApplicationContext().startService(mIntent);
            }
            iniFile.writeIniString(savePath, playVideoFile.getCode(), "playtime", mPlayTime + "");
        }else if (mPath.isEmpty() || !mPath.contains(".")) {
            if (iniFile.getIniString(userIni, "Login",
                    "LoginFlag", "0", (byte) 0) == "1") {
                int count = Integer.parseInt(iniFile.getIniString(savePath, "Log", "count", "0",
                        (byte) 0));
                iniFile.writeIniString(savePath, "log" + (count + 1), "starttime", mStartTime);
                iniFile.writeIniString(savePath, "log" + (count + 1), "UserName", iniFile.getIniString(userIni, "Login",
                        "Account", "", (byte) 0));
                iniFile.writeIniString(savePath, "log" + (count + 1), "code", playVideoFile.getCode());
                iniFile.writeIniString(savePath, "log" + (count + 1), "path", playVideoFile.getTxtPath());
                iniFile.writeIniString(savePath, "log" + (count + 1), "stoptime", StringUtils.getDate());
                iniFile.writeIniString(savePath, "Log", "count", (count + 1) + "");
                Intent mIntent = new Intent();
                mIntent.setAction("com.tbs.tbsmis.PlayerLogService");//你定义的service的action
                mIntent.setPackage(getApplicationContext().getPackageName());//这里你需要设置你应用的包名
                getApplicationContext().startService(mIntent);
            }
        }
    }
}
