package com.tbs.tbsmis.Live;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tbs.ini.IniFile;
import com.tbs.player.bean.VideoijkBean;
import com.tbs.player.listener.OnShowThumbnailListener;
import com.tbs.player.service.RecordService;
import com.tbs.player.widget.LiveView;
import com.tbs.player.widget.PlayStateParams;
import com.tbs.tbsmis.Live.bean.LiveAlllist;
import com.tbs.tbsmis.Live.fragment.showLiveChat;
import com.tbs.tbsmis.Live.fragment.showLiveDetails;
import com.tbs.tbsmis.Live.fragment.showComments;
import com.tbs.tbsmis.Live.utils.DataUtils;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.FileIO;
import com.tbs.tbsmis.util.UIHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;


@SuppressLint("HandlerLeak")
public class LivePlayer extends AppCompatActivity
{

    private String mPath;
    private String mTitle;
    private LiveView mVideoView;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private List<String> mTitleList;
    private List<Fragment> list_fragment;
    private showLiveChat mHotpot;
    private showLiveDetails mHotRec;
    private boolean isInitCache = false;
    private PowerManager.WakeLock wakeLock;
    private LiveAlllist playVideoFile;
    // private Button shot;
    private static final int RECORD_REQUEST_CODE = 101;

    private MediaProjectionManager projectionManager;
    private MediaProjection mediaProjection;
    private RecordService recordService;
    private String userIni;
    private IniFile iniFile;
    private View rootView;
    private showComments mFragmRelateCourse;

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        super.onCreate(savedInstanceState);
        rootView = getLayoutInflater().from(this).inflate(R.layout.live_video, null);
        setContentView(rootView);
        //注册监听
        intPath();
        initdata();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        this.recreate();
    }

    private void initdata() {
        mTitleList = new ArrayList<String>();
        Bundle bundle = this.getIntent().getExtras();
        String liveId = bundle.getString("liveId");
        Map params = new HashMap();
        params.put("liveId", liveId);
        params.put("account", iniFile.getIniString(userIni, "Login",
                "Account", "", (byte) 0));
        OkGo.<String>get(DataUtils.URL_ENTRY_LIVING)//
                .tag(this)
                .params(params, false)
                .execute(new StringCallback()
                {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());// 转换为JSONObject
                            boolean code = jsonObject.getBoolean("result");
                            if (code) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                List<LiveAlllist> datas = DataUtils.createAllLiveDatas(jsonArray);
                                if (datas.size() > 0) {
                                    playVideoFile = datas.get(0);
                                    initView();
                                } else {
                                    Toast.makeText(LivePlayer.this, "进入直播间失败,请稍候重试", Toast.LENGTH_LONG).show();
                                    finish();
                                }

                            } else {
                                Toast.makeText(LivePlayer.this, "进入直播间失败,请稍候重试", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(LivePlayer.this, "请求数据异常,请稍候重试", Toast.LENGTH_LONG).show();
                            finish();
                        } finally {
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Toast.makeText(LivePlayer.this, "请求失败,请稍候重试", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    private void initView() {
        this.mPath = this.playVideoFile.getUrl();
        this.mTitle = this.playVideoFile.getRoom_name();

        /**常亮*/
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "liveTAG");
        wakeLock.acquire();
        this.mViewPager = (ViewPager) rootView.findViewById(R.id.vp_view);
        this.mTabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        //shot = (Button) rootView.findViewById(R.id.shot);
        this.list_fragment = new ArrayList<>();

        this.mHotpot = showLiveChat.newInstance();
        this.list_fragment.add(this.mHotpot);
        this.mTitleList.add("聊天");

        this.mHotRec = showLiveDetails.newInstance(playVideoFile);
        this.list_fragment.add(this.mHotRec);
        this.mTitleList.add("主播详情");

        this.mFragmRelateCourse = showComments.newInstance(playVideoFile.getId(),"6");
        this.list_fragment.add(this.mFragmRelateCourse);
        this.mTitleList.add("评论互动");

        this.mTabLayout.setTabMode(TabLayout.MODE_FIXED);// 设置tab模式，当前为系统默认模式
        this.mViewPager.setAdapter(new FragmentPagerAdapter(LivePlayer.this.getSupportFragmentManager())
        {
            @Override
            public Fragment getItem(int position) {
                return LivePlayer.this.list_fragment.get(position);
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
                return LivePlayer.this.mTitleList.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return LivePlayer.this.mTitleList.get(position);
            }
        });
        this.mTabLayout.setupWithViewPager(this.mViewPager);// 将TabLayout和ViewPager关联起来。

        mVideoView = new LiveView(this, rootView)
        {
            @Override
            public LiveView toggleProcessDurationOrientation() {
                hideSteam(getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                return setProcessDurationOrientation(getScreenOrientation() == ActivityInfo
                        .SCREEN_ORIENTATION_PORTRAIT ? PlayStateParams.PROCESS_PORTRAIT : PlayStateParams
                        .PROCESS_LANDSCAPE);
            }

            @Override
            public LiveView setPlaySource(List<VideoijkBean> list) {
                return super.setPlaySource(list);
            }
        }
                .setTitle(mTitle)
                //.setProcessDurationOrientation(PlayStateParams.PROCESS_PORTRAIT)
                .setScaleType(PlayStateParams.wrapcontent)
                .forbidTouch(false)
                .hideMenu(true)
                .setShowSpeed(true)
                .hideCenterPlayer(true)
                .setNetWorkTypeTie(true)
                .setShotPicPath(UIHelper.getStoragePath(this) + constants.SD_CARD_TBSFILE_PATH5)
                .setPlaySource(mPath)
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
                    isInitCache = true;
                }
            }
        });
        mVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                finish();
            }
        });
        mVideoView.getRecordView().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if (recordService.isRunning()) {
                    recordService.stopRecord();
                    Toast.makeText(LivePlayer.this, "停止录制", Toast.LENGTH_LONG).show();
                } else {
                    Intent captureIntent = projectionManager.createScreenCaptureIntent();
                    startActivityForResult(captureIntent, RECORD_REQUEST_CODE);
                }
            }
        });
        startService(new Intent(this, RecordService.class));
        Intent intent = new Intent(this, RecordService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);

        this.mVideoView.startPlay();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECORD_REQUEST_CODE && resultCode == RESULT_OK) {
            mediaProjection = projectionManager.getMediaProjection(resultCode, data);
            recordService.setMediaProject(mediaProjection);
            recordService.startRecord();
            Toast.makeText(LivePlayer.this, "开始录制", Toast.LENGTH_LONG).show();
        }
    }

//    /**
//     * 视频截图 对外部按钮使用接口
//     */
//    private void shotImage(final String filePath) {
//        //获取截图
//        mVideoView.VideoShotListener(new VideoShotListener()
//        {
//            @Override
//            public void getBitmap(Bitmap bitmap) {
//                if (bitmap != null) {
//                    try {
//                        File file = new File(filePath);
//                        OutputStream outputStream;
//                        outputStream = new FileOutputStream(file);
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//                        bitmap.recycle();
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                        Toast.makeText(LivePlayer.this, "保存截图失败", Toast.LENGTH_LONG).show();
//                    }
//                    Toast.makeText(LivePlayer.this, "保存截图成功", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(LivePlayer.this, "获取截图失败", Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//
//    }

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
        // writeLog();
        if (mVideoView != null) {
            mVideoView.onDestroy();
        }
        if (recordService != null) {
            if (recordService.isRunning()) {
                recordService.stopRecord();
                Toast.makeText(LivePlayer.this, "停止录制", Toast.LENGTH_LONG).show();
            }
            unbindService(connection);
            stopService(new Intent(this, RecordService.class));
        }

    }

    private ServiceConnection connection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            RecordService.RecordBinder binder = (RecordService.RecordBinder) service;
            recordService = binder.getRecordService();
            recordService.setConfig(metrics.widthPixels, metrics.heightPixels, metrics.densityDpi);
            recordService.setSavePath(UIHelper.getStoragePath(LivePlayer.this) + constants.SD_CARD_TBSFILE_PATH5 + "/");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mVideoView != null) {
            mVideoView.onConfigurationChanged(newConfig);
        }
    }


    public void intPath() {
        String savePath = UIHelper.getStoragePath(this) + "/Log/playerLog.ini";
        File file = new File(savePath);
        if (!file.exists()) {
            FileIO.CreateNewFile(savePath);
        }
        iniFile = new IniFile();
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
        userIni = appNewsFile;
        if (Integer.parseInt(iniFile.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = getApplicationContext().getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
    }
}
