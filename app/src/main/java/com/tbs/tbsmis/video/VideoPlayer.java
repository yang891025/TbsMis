package com.tbs.tbsmis.video;

import android.annotation.SuppressLint;
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
import com.tbs.tbsmis.Live.LivePlayer;
import com.tbs.tbsmis.Live.fragment.showComments;
import com.tbs.tbsmis.Live.utils.DataUtils;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.activity.OverviewActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.download.ChapterDownloadTask;
import com.tbs.tbsmis.util.FileIO;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;
import com.tbs.tbsmis.video.fragment.showVideoChapter;
import com.tbs.tbsmis.video.fragment.showVideoContent;
import com.tbs.tbsmis.video.fragment.showVideoRelateCourse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;


@SuppressLint("HandlerLeak")
public class VideoPlayer extends AppCompatActivity
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
    private showVideoChapter mHotpot;
    private showVideoContent mHotRec;
    private VideoPlayer.MyBroadcastReciver MyBroadcastReciver;
    private String mChapter;
    private String mContent;
    private String mDescription;
    private static String mStartTime;
    private int mPlayTime;
    private static int chapterNum = 0;
    private static int sectionNum = 0;
    private static String mVideoPath = "";
    private boolean isInitCache = false;
    private PowerManager.WakeLock wakeLock;
    private showVideoRelateCourse mFragmRelateCourse;
    private String playtime;
    private LinearLayout video_top_box;
    private TextView video_title;
    private ImageView video_finish;
    private ImageView video_display;
    private ImageView app_video_display;
    private showComments mFragmPraises;
    private View rootView;
    private String type;

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
        OkGo.<String>get(DataUtils.URL_COURSE_INFO)//
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
                                String keyword = json.getString("keyWord");
                                String tetyName = json.getString("tetyName");
                                String cateName = json.getString("cateName");
                                String name = json.getString("name");
                                String description = json.getString("description");
                                String content = json.getString("content");
                                String pic = json.getString("pic");
                                String video = json.getString("video");
                                // String updateTime = json.getString("updateTime");
                                playVideoFile = new Video();
                                playVideoFile.setType(Video.Type.ONLINE);
                                playVideoFile.setTitle(name);
                                playVideoFile.setTitle_key(keyword);
                                playVideoFile.setCate(cateName);
                                playVideoFile.setDescription(description);
                                playVideoFile.setContent(content);
                                playVideoFile.setTety(tetyName);
                                playVideoFile.setAlbum(DataUtils.HOST + "filePath/static/tbsermImage/video/" + pic);
                                playVideoFile.setCode(id);
                                playVideoFile.setPath(DataUtils.HOST + "filePath/static/tbsermVideo/video/" + video);
                                initdata();
                            } else {
                            }
                        } catch (JSONException je) {

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

        mViewPager = (ViewPager) rootView.findViewById(R.id.vp_view);
        mViewPager.setOffscreenPageLimit(3);
        mTabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        list_fragment = new ArrayList<>();
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
        this.mPath = this.playVideoFile.getPath();
        mVideoPath = StringUtils.getHost(mPath);
        this.mTitle = this.playVideoFile.getTitle();
        this.mChapter = this.playVideoFile.getChapter();
        this.mContent = this.playVideoFile.getContent();
        this.mDescription = this.playVideoFile.getDescription();

        initChapterDate();
    }

    private void initChapterDate() {
        if (type.equalsIgnoreCase("local")) {
            if (!mChapter.isEmpty() && !mChapter.equalsIgnoreCase("[]")) {
                this.mHotpot = showVideoChapter.newInstance(playVideoFile, type);
                this.list_fragment.add(this.mHotpot);
                this.mTitleList.add("课程目录");
            }
        } else {
            this.mHotpot = showVideoChapter.newInstance(playVideoFile, type);
            this.list_fragment.add(this.mHotpot);
            this.mTitleList.add("课程目录");
        }
//        if (!mContent.isEmpty()) {
//            this.mHotRec = showVideoContent.newInstance(playVideoFile.getCode());
//            this.list_fragment.add(this.mHotRec);
//            this.mTitleList.add("相关内容");
//        }
//        else if (!mDescription.isEmpty()) {
            this.mHotRec = showVideoContent.newInstance(playVideoFile.getCode());
            this.list_fragment.add(this.mHotRec);
            this.mTitleList.add("课程详情");
//        }
//        if (!mRelateExam.isEmpty()) {
//            this.mFragmRelateExam = showVideoRelate.newInstance(mRelateExam, mRelateExamUrl);
//            this.list_fragment.add(this.mFragmRelateExam);
//            this.mTitleList.add("课程考试");
//        }
        this.mFragmPraises = showComments.newInstance(playVideoFile.getCode(),"2");
        this.list_fragment.add(this.mFragmPraises);
        this.mTitleList.add("评论互动");

        this.mTabLayout.setTabMode(TabLayout.MODE_FIXED);// 设置tab模式，当前为系统默认模式

        this.mViewPager.setAdapter(new FragmentPagerAdapter(VideoPlayer.this.getSupportFragmentManager())
        {
            @Override
            public Fragment getItem(int position) {
                return VideoPlayer.this.list_fragment.get(position);
            }

            @Override
            public long getItemId(int position) {
                //return super.getItemId(position);
                if (list_fragment != null) {
                    if (position < list_fragment.size()) {
                        return list_fragment.get(position).hashCode();
                    }
                }
                return super.getItemId(position);
            }

            @Override
            public int getCount() {
                return VideoPlayer.this.mTitleList.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return VideoPlayer.this.mTitleList.get(position);
            }
        });
        this.mTabLayout.setupWithViewPager(this.mViewPager);// 将TabLayout和ViewPager关联起来。
        mStartTime = StringUtils.getDate();
    }

    public long getTimes(String time) {
        int index1 = time.indexOf(":");
        int index2 = time.indexOf(":", index1 + 1);
        int hh = 0;
        int mi = 0;
        int ss = 0;
        if (index2 <= 0) {
            mi = Integer.parseInt(time.substring(0, index1));
            ss = Integer.parseInt(time.substring(index1 + 1));
        } else {
            hh = Integer.parseInt(time.substring(0, index1));
            mi = Integer.parseInt(time.substring(index1 + 1, index2));
            ss = Integer.parseInt(time.substring(index2 + 1));
        }
        return (hh * 60 * 60 + mi * 60 + ss) * (long) 1000;

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
           // System.out.println("playtime ="+playtime);

            if (action.equalsIgnoreCase("player_chapter" + getString(R.string.app_name))) {
                ChapterDownloadTask Task = (ChapterDownloadTask) intent.getSerializableExtra("ChatTask");
                if (Task.getType().equalsIgnoreCase("0") || Task.getType().equalsIgnoreCase("4")) {
                    //System.out.println("Task.getTime() ="+Task.getTime());
                    File file = new File(Task.getFilePath() + "/" + Task.getFileName());
                    if (file.isFile() && file.exists()) {
                        if (!(Task.getFilePath() + "/" + Task.getFileName()).equalsIgnoreCase(mPath)) {
                            VideoPlayer.this.mVideoView.setPlaySource(Task.getFilePath() + "/" + Task.getFileName());
                            VideoPlayer.this.mVideoView.startPlay();
                        }
                        if (!StringUtils.isEmpty(Task.getTime()))
                            if (!playtime.equalsIgnoreCase("0"))
                                VideoPlayer.this.mVideoView.seekTo(Integer.parseInt(playtime));
                            else
                                VideoPlayer.this.mVideoView.seekTo((int) getTimes(Task.getTime()));
                        if (!mVideoView.isPlaying()) {
                            mVideoView.startPlay();
                        }
                        mPath = Task.getFilePath() + "/" + Task.getFileName();
                    } else {
                        if (Task.getUrl().equalsIgnoreCase(mPath)) {
                            if (!StringUtils.isEmpty(Task.getTime()))
                                if (!playtime.equalsIgnoreCase("0"))
                                    VideoPlayer.this.mVideoView.seekTo(Integer.parseInt(playtime));
                                else
                                    VideoPlayer.this.mVideoView.seekTo((int) getTimes(Task.getTime()));
                        } else {
                            VideoPlayer.this.mVideoView.setPlaySource(Task.getUrl());
                            VideoPlayer.this.mVideoView.startPlay();
                            if (!StringUtils.isEmpty(Task.getTime()))
                                if (!playtime.equalsIgnoreCase("0"))
                                    VideoPlayer.this.mVideoView.seekTo(Integer.parseInt(playtime));
                                else
                                    VideoPlayer.this.mVideoView.seekTo((int) getTimes(Task.getTime()));
                            mPath = Task.getUrl();
                            //LivePlayer.this.mVideoView.start();
                        }
                        if (!mVideoView.isPlaying()) {
                            mVideoView.startPlay();
                        }
                    }

                } else if(Task.getType().equalsIgnoreCase("5")){
                    Intent intent4 = new Intent();
                    intent4.setClass(VideoPlayer.this, LivePlayer.class);
                    Bundle bundle4 = new Bundle();
                    bundle4.putString("liveId", Task.getUrl());
                    intent4.putExtras(bundle4);
                    startActivity(intent4);
                }else {
                    Intent intent2 = new Intent();
                    intent2.setClass(VideoPlayer.this,
                            OverviewActivity.class);
                    intent2.putExtra("tempUrl", Task.getUrl());
                    intent2.putExtra("ResName", Task.getTitle());
                    startActivity(intent2);
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
                iniFile.writeIniString(savePath, "log" + (count + 1), "UserName", iniFile.getIniString(userIni,
                        "Login",
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
                iniFile.writeIniString(savePath, "log" + (count + 1), "UserName", iniFile.getIniString(userIni,
                        "Login",
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
