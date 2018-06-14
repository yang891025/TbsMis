package com.tbs.tbsmis.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.search.SearchSetupActivity;
import com.tbs.tbsmis.util.UIHelper;
import com.tbs.tbsmis.weixin.WeixinSetUpActivity;

/**
 * Created by TBS on 2017/2/14.
 */

public class OtherSetupActivity extends Activity implements View.OnClickListener
{
    private ImageView backBtn;
    private TextView title;
    private ImageView finishBtn;
    private LinearLayout store_setup;
    private LinearLayout source_store_setup;
    private LinearLayout subscribe_setup;
    private LinearLayout weixin_setup;
    private LinearLayout search_setup;
    private LinearLayout collect_setup;
    private IniFile IniFile;
    private String appIniFile;
    private String userIni;
    private LinearLayout wallet_setup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_setup);
        MyActivity.getInstance().addActivity(this);
        initView();
    }

    private void initView() {
        backBtn = (ImageView) findViewById(R.id.more_btn);
        finishBtn = (ImageView) findViewById(R.id.finish_btn);
        title = (TextView) findViewById(R.id.textView1);
        wallet_setup = (LinearLayout) findViewById(R.id.wallet_setup);
        store_setup = (LinearLayout) findViewById(R.id.store_setup);
        source_store_setup = (LinearLayout) findViewById(R.id.source_store_setup);
        subscribe_setup = (LinearLayout) findViewById(R.id.subscribe_setup);
        search_setup = (LinearLayout)findViewById(R.id.search_setup);
        collect_setup = (LinearLayout)findViewById(R.id.collect_setup);
        weixin_setup = (LinearLayout) findViewById(R.id.weixin_setup);

        title.setText("云端应用");
        search_setup.setOnClickListener(this);
        collect_setup.setOnClickListener(this);
        wallet_setup.setOnClickListener(this);
        store_setup.setOnClickListener(this);
        source_store_setup.setOnClickListener(this);
        subscribe_setup.setOnClickListener(this);
        weixin_setup.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        finishBtn.setOnClickListener(this);
        IniFile = new IniFile();
        initPath();
    }

    private void initPath() {
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
        appIniFile = webRoot
                + IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
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
    protected void onDestroy() {
        super.onDestroy();
        MyActivity.getInstance().finishActivity(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.more_btn:
                finish();
                break;
            case R.id.finish_btn:
                finish();
                break;
            case R.id.wallet_setup:
                intent = new Intent();
                intent.setClass(this, WalletSetupActivity.class);
                startActivity(intent);
                break;
            case R.id.store_setup:
                intent = new Intent();
                intent.setClass(this, StoreSetupActivity.class);
                startActivity(intent);
                break;
            case R.id.source_store_setup:
                intent = new Intent();
                intent.setClass(this, SourceStoreSetupActivity.class);
                startActivity(intent);
                break;
            case R.id.subscribe_setup:
                intent = new Intent();
                intent.setClass(this, SubscribeSetupActivity.class);
                startActivity(intent);
                break;
            case R.id.other_setup:
                intent = new Intent();
                intent.setClass(this, OtherSetupActivity.class);
                startActivity(intent);
                break;
            case R.id.weixin_setup:
                intent = new Intent();
                intent.setClass(this, WeixinSetUpActivity.class);
                startActivity(intent);
                break;
            case R.id.search_setup:
                intent = new Intent();
                intent.setClass(this, SearchSetupActivity.class);
                startActivity(intent);
                break;
            case R.id.collect_setup:
                intent = new Intent();
                intent.setClass(this,CollectSetupActivity.class);
                startActivity(intent);
                break;
        }
    }
}
