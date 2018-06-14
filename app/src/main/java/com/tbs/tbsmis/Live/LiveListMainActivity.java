package com.tbs.tbsmis.Live;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.Live.fragment.LiveFragment;
import com.tbs.tbsmis.Live.fragment.UserFragment;
import com.tbs.tbsmis.Live.fragment.VideoFragment;
import com.tbs.tbsmis.Live.utils.DataUtils;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.UIHelper;


/**
 * 版本号：1.0
 * 类描述：
 * 备注消息：
 * 修改时间：2016/11/30 上午9:56
 **/
public class LiveListMainActivity extends AppCompatActivity
{
    private static final String TAG_PAGE_LIVE = "直播";
    private static final String TAG_PAGE_VIDEO = "视频";
    private static final String TAG_PAGE_USER = "我的";
    //    退出时间
    private long exitTime = 0;

    NavigateTabBar mNavigateTabBar;
    NavigateTabBar.ViewHolder mHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_main);
        MyActivity.getInstance().addActivity(this);
        initPath();
        mNavigateTabBar = (NavigateTabBar) findViewById(R.id.mainTabBar);
        mNavigateTabBar.onRestoreInstanceState(savedInstanceState);

        mNavigateTabBar.addTab(LiveFragment.class, new NavigateTabBar.TabParam(R.drawable
                .icon_1_n, R.drawable
                .icon_1_s, TAG_PAGE_LIVE));
        mNavigateTabBar.addTab(VideoFragment.class, new NavigateTabBar.TabParam(R.drawable.icon_5_n, R
                .drawable.icon_5_s, TAG_PAGE_VIDEO));
        mNavigateTabBar.addTab(UserFragment.class, new NavigateTabBar.TabParam(R.drawable.icon_3_n, R.drawable
                .icon_3_s, TAG_PAGE_USER));
        mNavigateTabBar.setTabSelectListener(new NavigateTabBar.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(NavigateTabBar.ViewHolder holder) {
//                Toast.makeText(MainActivity.this, "信息为:"+holder.tag, Toast.LENGTH_SHORT).show();
                switch (holder.tag.toString()) {
//                    直播
                    case TAG_PAGE_LIVE:

                        mNavigateTabBar.showFragment(holder);
                        break;
//                    视频
                    case TAG_PAGE_VIDEO:
                        mNavigateTabBar.showFragment(holder);
                        break;

//                    我的
                    case TAG_PAGE_USER:
                        if (mNavigateTabBar != null)
                            mNavigateTabBar.showFragment(holder);
                        break;
                }
            }
        });
//        // 获取所有权限
//        PermissionUtil.requestAllPermission(new PermissionUtil.RequestPermission() {
//            @Override
//            public void onRequestPermissionSuccess() {
//
//            }
//
//            @Override
//            public void onRequestPermissionFailed() {
//
//            }
//        }, new RxPermissions(MainActivity.this),this);
    }

    /**
     * 初始化基本路径
     */
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
        IniFile m_iniFileIO = new IniFile();
        String appNewsFile = webRoot
                + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);

        String userIni = appNewsFile;
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = getApplicationContext().getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }

        String ipUrl = m_iniFileIO.getIniString(userIni, "Live",
                "liveAddress", constants.DefaultServerIp, (byte) 0);
        String portUrl = m_iniFileIO.getIniString(userIni, "Live",
                "livePort", "1115", (byte) 0);
        //String baseUrl = "http://" + ipUrl + ":" + portUrl;
        DataUtils.HOST = "http://" + ipUrl + ":" + portUrl+"/";
        String ipPull = m_iniFileIO.getIniString(userIni, "Live",
                "pullAddress", constants.DefaultServerIp, (byte) 0);
        String portPUll = m_iniFileIO.getIniString(userIni, "Live",
                "pullPort", "1936", (byte) 0);
        DataUtils.GETHOST = "http://" + ipPull + ":" + portPUll+"/hls/";
     }
    @Override
    protected void onRestart() {

        super.onRestart();
    }

    /**
     * 拦截返回键，要求点击两次返回键才退出应用
     *
     * @param keyCode 按键代码
     * @param event   点击事件
     * @return 是否处理本次事件
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出直播",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyActivity.getInstance().finishActivity(this);
    }

    /**
     * 保存数据状态
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mNavigateTabBar.onSaveInstanceState(outState);
    }

}
