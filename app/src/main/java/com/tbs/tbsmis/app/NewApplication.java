package com.tbs.tbsmis.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.Tbszlib.JTbszlib;
import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.activity.TerminalSetupActivity;
import com.tbs.tbsmis.check.DeleteCategoryAdapter;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by TBS on 2017/4/1.
 */

public class NewApplication extends Activity
{
    private IniFile IniFile;
    private String appIniFile;

    private EditText new_app_code;
    private EditText new_app_dir;
    private EditText new_app_ini;
    private EditText new_app_name;
    private EditText web_address;
    private EditText web_port;
    private EditText new_app_path;

    private LinearLayout dynamicTag;
    private TextView dynamicTxt;

    private Button next_btn;

    private ImageView finishBtn;

    private DeleteCategoryAdapter DeletemAdapter;
    private AlertDialog ModifyDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_application_layout);
        MyActivity.getInstance().addActivity(this);
        initPath();
        initview();
    }

    private void initPath() {
        // TODO Auto-generated method stub
        IniFile = new IniFile();
        String configPath = getApplicationContext().getFilesDir()
                .getParentFile().getAbsolutePath();
        if (configPath.endsWith("/") == false) {
            configPath = configPath + "/";
        }
        appIniFile = configPath + constants.USER_CONFIG_FILE_NAME;
    }


    private void initview() {

        finishBtn = (ImageView) this.findViewById(R.id.more_btn);

        new_app_code = (EditText) findViewById(R.id.new_app_code);
        new_app_dir = (EditText) findViewById(R.id.new_app_dir);
        new_app_ini = (EditText) findViewById(R.id.new_app_ini);
        new_app_name = (EditText) findViewById(R.id.new_app_name);
        web_address = (EditText) findViewById(R.id.web_address);
        web_port = (EditText) findViewById(R.id.web_port);
        new_app_path = (EditText) findViewById(R.id.new_app_path);

        finishBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        new_app_dir.addTextChangedListener(new TextWatcher()
        {

            private String localPortTxt;

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                localPortTxt = String.valueOf(new_app_dir.getText());

                if (null != localPortTxt && !localPortTxt.equals("")
                        && !localPortTxt.equals("\\s{1,}")) {
                    new_app_ini.setText(localPortTxt + ".ini");

                }
                Log.i("localPortTxt", "onTextChanged...");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                Log.i("localPortTxt", "beforeTextChanged...");
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i("localPortTxt", "afterTextChanged...");
            }
        });

        dynamicTag = (LinearLayout) findViewById(R.id.account_tip);
        dynamicTxt = (TextView) findViewById(R.id.account_error_text);

        next_btn = (Button) findViewById(R.id.next_btn);
        next_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                checkContent();
            }
        });
    }

    private void checkContent() {
        if (StringUtils.isEmpty(new_app_name.getText()
                .toString())) {
            dynamicTag.setVisibility(View.VISIBLE);
            dynamicTxt.setText("应用名称不可为空");
            return;
        } else if (StringUtils.isEmpty(new_app_code
                .getText().toString())) {
            dynamicTag.setVisibility(View.VISIBLE);
            dynamicTxt.setText("应用代号不可为空");
            return;
        } else if (StringUtils.isEmpty(new_app_ini
                .getText().toString())) {
            dynamicTag.setVisibility(View.VISIBLE);
            dynamicTxt.setText("配置文件不可为空");
            return;
        } else if (StringUtils.isEmpty(web_address
                .getText().toString()) && StringUtils.isEmpty(new_app_path
                .getText().toString())) {
            dynamicTag.setVisibility(View.VISIBLE);
            dynamicTxt.setText("应用地址和应用路径至少一个不为空");
            return;
        } else if (!StringUtils.isEmpty(web_address
                .getText().toString())) {
            if (StringUtils.isEmpty(web_port
                    .getText().toString())) {
                dynamicTag.setVisibility(View.VISIBLE);
                dynamicTxt.setText("应用地址不为空时端口同时不可为空");
                return;
            }

        } else if (StringUtils.isEmpty(new_app_dir
                .getText().toString())) {
            dynamicTag.setVisibility(View.VISIBLE);
            dynamicTxt.setText("应用目录不可为空");
            return;
        } else {
            int resnum = Integer.parseInt(IniFile
                    .getIniString(appIniFile,
                            "resource", "resnum", "0",
                            (byte) 0));
            for (int i = 1; i <= resnum; i++) {
                int groupresnum = Integer
                        .parseInt(IniFile.getIniString(
                                appIniFile,
                                "group" + i, "resnum",
                                "0", (byte) 0));
                for (int j = 1; j <= groupresnum; j++) {
                    if (new_app_code
                            .getText()
                            .toString()
                            .equals(IniFile
                                    .getIniString(
                                            appIniFile,
                                            "group" + i,
                                            "res" + j,
                                            "",
                                            (byte) 0))) {
                        dynamicTag
                                .setVisibility(View.VISIBLE);
                        dynamicTxt.setText("应用代号已存在");
                        return;
                    }
                }
            }
        }
        String webRoot = UIHelper.getStoragePath(this);
        webRoot = webRoot
                + constants.SD_CARD_TBSSOFT_PATH3
                + "/";
        File dirFile = new File(webRoot
                + new_app_dir.getText());
        if (dirFile.exists()) {
            File iniFile = new File(webRoot
                    + new_app_dir.getText()
                    + "/"
                    + new_app_ini.getText());
            if (iniFile.exists()) {
                dynamicTag
                        .setVisibility(View.VISIBLE);
                dynamicTxt.setText("配置文件已存在");
                return;
            }
        } else {
            dirFile.mkdirs();

            File iniFile = new File(webRoot
                    + new_app_dir.getText()
                    + "/"
                    + new_app_ini.getText());
            if (iniFile.exists()) {
                dynamicTag
                        .setVisibility(View.VISIBLE);
                dynamicTxt.setText("配置文件已存在");
                return;
            }

        }
        saveNewApp();
    }

    protected void saveNewApp() {
        String name = new_app_name.getText().toString();
        String code = new_app_code.getText().toString();
        String file = new_app_ini.getText().toString();
        String address = web_address.getText().toString();
        String port = web_port.getText().toString();
        String path = new_app_path.getText().toString();
        String dir = new_app_dir.getText().toString();
        // TODO Auto-generated method stub
        String webRoot = UIHelper.getStoragePath(this);
        webRoot = webRoot + constants.SD_CARD_TBSSOFT_PATH3 + "/";
        File newfile = new File(webRoot + dir + "/" + file);
        try {
            newfile.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (StringUtils.isEmpty(address)) {
            IniFile.writeIniString(webRoot + dir + "/" + file, "TBSAPP",
                    "defaultUrl", path);
        } else {
            IniFile.writeIniString(webRoot + dir + "/" + file, "TBSAPP",
                    "defaultUrl", "http://" + address + ":" + port + path);
            IniFile.writeIniString(webRoot + dir + "/" + file, "TBSAPP",
                    "webAddress", address);
            IniFile.writeIniString(webRoot + dir + "/" + file, "TBSAPP",
                    "webPort", port);
        }
        IniFile.writeIniString(webRoot + dir + "/" + file, "TBSAPP", "AppCode",
                code);
        IniFile.writeIniString(webRoot + dir + "/" + file, "TBSAPP", "AppName",
                name);
        IniFile.writeIniString(webRoot + dir + "/" + file, "TBSAPP", "AppVersion",
                "1.0");
        myThread handThread = new myThread(dir, file);
        handThread.run();
    }

    private boolean doDeploy(String dir) {
        String webRoot = UIHelper.getStoragePath(this);
        webRoot = webRoot + constants.SD_CARD_TBSSOFT_PATH3 + "/" + dir;
        String webRootTbk = webRoot + "Web.tbk";
        try {
            InputStream is = getBaseContext().getAssets().open(
                    "config/Web.tbk");
            OutputStream os = new FileOutputStream(webRootTbk);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
            os.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int resoult = JTbszlib.UnZipFile(webRootTbk, webRoot, 1, "");
        if (0 != resoult) {
            return false;
        }
        delZipFile(webRootTbk);
        return true;
    }

    protected void delZipFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyActivity.getInstance().finishActivity(this);
    }

    class myThread implements Runnable
    {
        private final String file;
        private final String dir;

        myThread(String dir, String file) {
            this.dir = dir;
            this.file = file;
        }

        public void run() {

            doDeploy(this.dir);
            Message message = new Message();
            message.what = 1;
            Bundle b = new Bundle();
            b.putString("file", this.file);
            b.putString("dir", this.dir);
            message.setData(b);
            myHandler.sendMessage(message);
        }
    }

    Handler myHandler = new Handler()
    {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String webRoot = UIHelper.getStoragePath(NewApplication.this);
                    webRoot = webRoot + constants.SD_CARD_TBSSOFT_PATH3 + "/";
                    Bundle b = msg.getData();
                    String dir = b.getString("dir");
                    String file = b.getString("file");
                    IniFile.writeIniString(webRoot + dir + "/"
                            + constants.WEB_CONFIG_FILE_NAME, "TBSWeb", "IniName", file);
                    showAddApplicationDialog(dir);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void showAddApplicationDialog(final String Path) {
        ArrayList<Map<String, String>> groups = new ArrayList<Map<String, String>>();
        int resnum = Integer.parseInt(this.IniFile.getIniString(this.appIniFile,
                "resource", "resnum", "0", (byte) 0));
        for (int i = 1; i <= resnum; i++) {
            Map<String, String> group = new HashMap<String, String>();
            group.put("group", this.IniFile.getIniString(this.appIniFile, "resource",
                    "resname" + i, "", (byte) 0));
            groups.add(group);
        }
        LayoutInflater factory = LayoutInflater.from(this);// 提示框
        View view = factory.inflate(R.layout.set_delete_category, null);// 这里必须是final的
        ListView categoryList = (ListView) view
                .findViewById(R.id.AppCategorylistItems);// 获得AppCategorylistItems对象
        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @SuppressLint("ShowToast")
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                DeleteCategoryAdapter.ViewHolder holder = (DeleteCategoryAdapter.ViewHolder) arg1.getTag();
                String apptext = (String) holder.tv.getText();
                int position = arg2;
                ModifyDialog.dismiss();
                String webRoot = UIHelper.getStoragePath(NewApplication.this);
                webRoot = webRoot + constants.SD_CARD_TBSSOFT_PATH3 + "/";
                String finalPath = webRoot + Path;
                AddApplicationDialog(apptext, finalPath, position);
            }
        });
        DeletemAdapter = new DeleteCategoryAdapter(groups, this);
        categoryList.setAdapter(this.DeletemAdapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择分类").setCancelable(false)// 提示框标题
                .setView(view).setPositiveButton("取消", null);
        this.ModifyDialog = builder.create();
        this.ModifyDialog.show();
    }

    protected void AddApplicationDialog(final String apptext,
                                        final String path, final int position) {
        // TODO Auto-generated method stub
        new AlertDialog.Builder(this).setTitle("添加应用")
                .setMessage("分类：" + apptext + "\n" + "路径：" + path)// 提示框标题
                .setPositiveButton("确定",// 提示框的两个按钮
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if (AddApplication(path, apptext,
                                        position)) {
                                    Toast.makeText(
                                            NewApplication.this,
                                            "应用添加成功",
                                            Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent();
                                    intent.setClass(NewApplication.this, TerminalSetupActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(
                                            NewApplication.this,
                                            "应用已存在", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        }).setNegativeButton("取消", null).create().show();
    }

    protected boolean AddApplication(String path, String appcategory,
                                     int position) {
        // TODO Auto-generated method stub
        String inifle = this.IniFile.getIniString(path + File.separator
                        + constants.WEB_CONFIG_FILE_NAME, "TBSWeb", "IniName", "",
                (byte) 0);
        String title = this.IniFile.getIniString(path + File.separator + inifle,
                "TBSAPP", "AppName", "", (byte) 0);
        String resTitle = this.IniFile.getIniString(path + File.separator + inifle,
                "TBSAPP", "AppCode", "tbs-mis", (byte) 0);

        String inistdir = path;
        int resnum = Integer.parseInt(this.IniFile.getIniString(this.appIniFile,
                "resource", "resnum", "0", (byte) 0));
        int groupresnum = 0;
        for (int i = 1; i <= resnum; i++) {
            groupresnum = Integer.parseInt(this.IniFile.getIniString(this.appIniFile,
                    "group" + i, "resnum", "0", (byte) 0));
            for (int j = 1; j <= groupresnum; j++) {
                if (resTitle.equals(this.IniFile.getIniString(this.appIniFile, "group"
                        + i, "res" + j, "", (byte) 0))) {
                    return false;
                }
            }
        }
        this.IniFile.writeIniString(path + File.separator + inifle, "TBSAPP",
                "AppCategory", appcategory);
        groupresnum = Integer.parseInt(this.IniFile.getIniString(this.appIniFile, "group"
                + (position + 1), "resnum", "0", (byte) 0));
        this.IniFile.writeIniString(this.appIniFile, "group" + (position + 1), "resnum",
                "" + (groupresnum + 1));
        this.IniFile.writeIniString(this.appIniFile, "group" + (position + 1), "res"
                + (groupresnum + 1), resTitle);
        this.IniFile.writeIniString(this.appIniFile, resTitle, "title", title);
        this.IniFile.writeIniString(this.appIniFile, resTitle, "from", "1");
        this.IniFile.writeIniString(this.appIniFile, resTitle, "instdir", inistdir);
        return true;
    }
}
