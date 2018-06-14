package com.tbs.tbsmis.Film;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tbs.ini.IniFile;
import com.tbs.player.bean.VideoijkBean;
import com.tbs.player.listener.OnShowThumbnailListener;
import com.tbs.player.widget.PlayStateParams;
import com.tbs.player.widget.PlayerView;
import com.tbs.tbsmis.Film.fragment.showFilmContent;
import com.tbs.tbsmis.Live.fragment.showComments;
import com.tbs.tbsmis.Live.utils.DataUtils;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.download.ChapterDownloadTask;
import com.tbs.tbsmis.util.FileIO;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;
import com.tbs.tbsmis.video.Video;
import com.tbs.tbsmis.video.fragment.showChapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class FilmActivity extends AppCompatActivity
{

    private String mPath;
    private String mTitle;
    private PlayerView mVideoView;
    private Video playVideoFile;
    private RelativeLayout llVideo;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private List<String> mTitleList;
    private List<Fragment> list_fragment;
    private showChapter mHotpot;
    private showFilmContent mHotRec;
    private MyBroadcastReciver MyBroadcastReciver;
    private static String mStartTime;
    private int mPlayTime;
    private boolean isInitCache = false;
    private PowerManager.WakeLock wakeLock;
    private String playtime;
    private LinearLayout video_top_box;
    private TextView video_title;
    private ImageView video_finish;
    private ImageView video_display;
    private ImageView app_video_display;
    private showComments mFragmPraises;
    private View rootView;
    private String type;
    private static int chapterNum = 0;
    private static int sectionNum = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        rootView = getLayoutInflater().from(this).inflate(R.layout.jie_video_little, null);
        setContentView(rootView);
        //注册监听
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("player_chapter" + getString(R.string.app_name));
        this.MyBroadcastReciver = new MyBroadcastReciver();
        this.registerReceiver(this.MyBroadcastReciver, intentFilter);
        // ~~~ 绑定控件
        //getSupportActionBar().hide();
        // ~~~ 获取播放地址和标题
        initView();
        Bundle bundle = this.getIntent().getExtras();
        type = bundle.getString("type", "local");
        if (type.equalsIgnoreCase("local")) {
            playVideoFile = (Video) bundle.getSerializable("video");
            initdata();
        } else {
            String sourceId = bundle.getString("sourceId");
            getVideo(sourceId);
        }
    }

    private void getVideo(String sourceId) {
        OkGo.<String>get(DataUtils.URL_FILM_INFO)//
                .tag(this)
                .params("account", "")
                .params("sourceId", sourceId)
                .params("LoginId", "")
                .execute(new StringCallback()
                {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());// 转换为JSONObject
                            boolean code = jsonObject.getBoolean("result");
                            if (code) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                JSONObject json = jsonArray.getJSONObject(0);
                                String id = json.getString("id");
                                String keyword = json.getString("keyword");
                                String tetyName = json.getString("tetyName");
                                String cateName = json.getString("cateName");
                                String name = json.getString("name");
                                String description = json.getString("description");
                                String author = json.getString("author");
                                String pic = json.getString("pic");
                                //String video = json.getString("video");
                                // String updateTime = json.getString("updateTime");
                                playVideoFile = new Video();
                                playVideoFile.setType(Video.Type.ONLINE);
                                playVideoFile.setTitle(name);
                                playVideoFile.setTitle_key(keyword);
                                playVideoFile.setCate(cateName);
                                playVideoFile.setDescription(description);
                                playVideoFile.setArtist(author);
                                playVideoFile.setTety(tetyName);
                                playVideoFile.setAlbum(DataUtils.HOST + "filePath/static/tbsermImage/film/" + pic);
                                playVideoFile.setCode(id);
                                // playVideoFile.setPath(DataUtils.HOST + "filePath/static/tbsermVideo/video/" + video);
                                initdata();
                            } else {
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        this.recreate();
    }

    private void initView() {
        this.llVideo = (RelativeLayout) rootView.findViewById(R.id.app_video_box);
        video_top_box = (LinearLayout) rootView.findViewById(R.id.video_top_box);
        video_title = (TextView) rootView.findViewById(R.id.video_title);
        video_finish = (ImageView) rootView.findViewById(R.id.video_finish);
        video_display = (ImageView) rootView.findViewById(R.id.video_display);
        app_video_display = (ImageView) rootView.findViewById(R.id.app_video_display);
        /**常亮*/
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "liveTAG");
        wakeLock.acquire();

        this.mViewPager = (ViewPager) rootView.findViewById(R.id.vp_view);
        mViewPager.setOffscreenPageLimit(2);
        this.mTabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        this.list_fragment = new ArrayList<>();
        video_top_box.setVisibility(View.GONE);
        video_title.setText(mTitle);
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
                //.setTitle(mTitle.substring(0, mTitle.lastIndexOf(".")))
                //.setProcessDurationOrientation(PlayStateParams.PROCESS_PORTRAIT)
                .setScaleType(PlayStateParams.wrapcontent)
                .forbidTouch(false)
                .hideMenu(true)
                .hideSteam(true)
                .setShowSpeed(true)
                .setNetWorkTypeTie(true)
                .hideCenterPlayer(true)
                .showThumbnail(new OnShowThumbnailListener()
                {
                    @Override
                    public void onShowThumbnail(ImageView ivThumbnail) {
                    }
                });
        mVideoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener()
        {

            @Override
            public void onPrepared(IMediaPlayer mediaPlayer) {
                if (!isInitCache) {
                    //一般来说,缓存回调成功和网络回调成功做的事情是一样的,所以这里直接回调onSuccess
                    if (!playtime.isEmpty()) {
                        if (Long.parseLong(playtime) >= mVideoView.getDuration()) {
                            mVideoView.seekTo(0);
                        } else {
                            mVideoView.seekTo(Integer.parseInt(playtime));
                        }
                    }
                    isInitCache = true;
                }
            }
        });
        mVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                Intent intent = new Intent();
                intent.setAction("mChapter" + getString(R.string.app_name));
                intent.putExtra("position", sectionNum);
                intent.putExtra("groupPosition", chapterNum);
                sendBroadcast(intent);
            }
        });
    }

    private void initdata() {
        mTitleList = new ArrayList<String>();
        this.mTitle = this.playVideoFile.getTitle();
        mStartTime = StringUtils.getDate();
        initViewPage();
    }


    private void initViewPage() {
        this.mHotpot = showChapter.newInstance(playVideoFile.getCode(), mTitle);
        this.list_fragment.add(this.mHotpot);
        this.mTitleList.add("剧集");

        this.mHotRec = showFilmContent.newInstance(playVideoFile.getCode());
        this.list_fragment.add(this.mHotRec);
        this.mTitleList.add("相关内容");

//        else if (!mDescription.isEmpty()) {
//            this.mHotRec = showVideoContent.newInstance(mDescription, "description");
//            this.list_fragment.add(this.mHotRec);
//            this.mTitleList.add("详情");
//        }
        this.mFragmPraises = showComments.newInstance(playVideoFile.getCode(), "11");
        this.list_fragment.add(this.mFragmPraises);
        this.mTitleList.add("评论互动");

        this.mTabLayout.setTabMode(TabLayout.MODE_FIXED);// 设置tab模式，当前为系统默认模式
        this.mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager())
        {
            @Override
            public Fragment getItem(int position) {
                return list_fragment.get(position);
            }

            @Override
            public long getItemId(int position) {
                if (list_fragment != null) {
                    if (position < list_fragment.size()) {
                        return list_fragment.get(position).hashCode();
                    }
                }
                return super.getItemId(position);
            }

            @Override
            public int getCount() {
                return mTitleList.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTitleList.get(position);
            }
        });
        this.mTabLayout.setupWithViewPager(this.mViewPager);// 将TabLayout和ViewPager关联起来。
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
            mPlayTime = mVideoView.getCurrentPosition();
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
        this.unregisterReceiver(this.MyBroadcastReciver);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mVideoView != null) {
            mVideoView.onConfigurationChanged(newConfig);
        }
    }

    private class MyBroadcastReciver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int childPosition = intent.getIntExtra("childPosition", 0);
            int groupPosition = intent.getIntExtra("groupPosition", 0);
            playtime = intent.getStringExtra("playtime");
            if (action.equalsIgnoreCase("player_chapter" + getString(R.string.app_name))) {
                ChapterDownloadTask Task = (ChapterDownloadTask) intent.getSerializableExtra("ChatTask");
                File file = new File(Task.getFilePath() + "/" + Task.getFileName());
                if (file.isFile() && file.exists()) {
                    if (!(Task.getFilePath() + "/" + Task.getFileName()).equalsIgnoreCase(mPath)) {
                        mVideoView.setPlaySource(Task.getFilePath() + "/" + Task.getFileName());
                        mVideoView.startPlay();
                    }
                    if (!playtime.isEmpty()) {
                        mVideoView.seekTo(Integer.parseInt(playtime));
                    }
                    if (!mVideoView.isPlaying()) {
                        mVideoView.startPlay();
                    }
                    mPath = Task.getFilePath() + "/" + Task.getFileName();
                } else {
                    if (Task.getUrl().equalsIgnoreCase(mPath)) {
                        if (!playtime.isEmpty()) {
                            mVideoView.seekTo(Integer.parseInt(playtime));
                        }
                    } else {
                        mVideoView.setPlaySource(Task.getUrl());
                        mVideoView.startPlay();
                        if (!playtime.isEmpty()) {
                            mVideoView.seekTo(Integer.parseInt(playtime));
                        }
                        mPath = Task.getUrl();
                        //LivePlayer.this.mVideoView.start();
                    }
                    if (!mVideoView.isPlaying()) {
                        mVideoView.startPlay();
                    }
                }
                chapterNum = groupPosition;
                sectionNum = childPosition;
                mVideoView.setTitle(Task.getTitle());
            }
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
                    "LoginFlag", "0", (byte) 0).equalsIgnoreCase("1")) {
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
            iniFile.writeIniString(savePath, playVideoFile.getCode(), "chapter",
                    chapterNum + "");
            iniFile.writeIniString(savePath, playVideoFile.getCode(), "section",
                    (sectionNum) + "");
            iniFile.writeIniString(savePath, playVideoFile.getCode(), "playtime", mPlayTime + "");
        } else if (mPath.isEmpty() || !mPath.substring(mPath.lastIndexOf("/")).contains(".")) {
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
