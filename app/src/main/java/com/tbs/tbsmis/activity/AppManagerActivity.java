package com.tbs.tbsmis.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.Tbszlib.JTbszlib;
import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.app.NewApplication;
import com.tbs.tbsmis.check.AppGroupAdapter;
import com.tbs.tbsmis.check.DeleteCategoryAdapter;
import com.tbs.tbsmis.check.PathChooseDialog;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppManagerActivity extends ExpandableListActivity implements
        View.OnClickListener
{
    private ImageView finishBtn;
    private ImageView downBtn;
    private TextView title;
    private String appIniFile;
    private IniFile IniFile;
    private int groupnum;
    private AppGroupAdapter exlist_adapter;
    private ExpandableListView exlist;
    private ArrayList<Map<String, String>> groups;
    private ArrayList<List<Map<String, String>>> childs;
    private PopupWindow menuWindow;
    protected boolean isOpenPop;
    private RelativeLayout managerTitle;
    private DeleteCategoryAdapter DeletemAdapter;
    private AlertDialog ModifyDialog;
    private AppManagerActivity.MyBroadcastReciver MyBroadcastReciver;
    private boolean refreshing;
    private boolean reloading = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.recommend_listview);
        MyActivity.getInstance().addActivity(this);
        this.finishBtn = (ImageView) this.findViewById(R.id.more_btn);
        this.downBtn = (ImageView) this.findViewById(R.id.finish_btn);
        this.title = (TextView) this.findViewById(R.id.textView1);
        this.managerTitle = (RelativeLayout) this.findViewById(R.id.include_top);
        this.title.setText("应用管理");
        this.finishBtn.setOnClickListener(this);
        this.downBtn.setOnClickListener(this);
        this.initPath();
        initData();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("recommend"
                + getString(R.string.about_title));
        this.MyBroadcastReciver = new AppManagerActivity.MyBroadcastReciver();
        this.registerReceiver(this.MyBroadcastReciver, intentFilter);
    }

    private void initData() {
        this.groups = new ArrayList<Map<String, String>>();
        this.childs = new ArrayList<List<Map<String, String>>>();
        // 创建两个一级条目标题
        int resnum = Integer.parseInt(this.IniFile.getIniString(this.appIniFile,
                "resource", "resnum", "0", (byte) 0));
        for (int i = 1; i <= resnum; i++) {
            Map<String, String> group = new HashMap<String, String>();
            List<Map<String, String>> child = new ArrayList<Map<String, String>>();
            group.put("group", this.IniFile.getIniString(this.appIniFile, "resource",
                    "resname" + i, "", (byte) 0));
            this.groups.add(group);
            String groupid = this.IniFile.getIniString(this.appIniFile, "resource",
                    "resid" + i, "", (byte) 0);
            this.groupnum = Integer.parseInt(this.IniFile.getIniString(this.appIniFile,
                    groupid, "resnum", "0", (byte) 0));
            for (int j = 1; j <= this.groupnum; j++) {
                String resname = this.IniFile.getIniString(this.appIniFile, groupid,
                        "res" + j, "", (byte) 0);
                Map<String, String> childdata = new HashMap<String, String>();
                childdata.put("child", this.IniFile.getIniString(this.appIniFile,
                        resname, "title", "", (byte) 0));
                childdata.put("from", this.IniFile.getIniString(this.appIniFile, resname,
                        "from", "0", (byte) 0));
                childdata.put("path", this.IniFile.getIniString(this.appIniFile, resname,
                        "instdir", "", (byte) 0));
                childdata.put("res", resname);
                child.add(childdata);
            }
            this.childs.add(child);
        }
        this.exlist = (ExpandableListView) this.findViewById(android.R.id.list);
        @SuppressWarnings("deprecation")
        int width = this.getWindowManager().getDefaultDisplay().getWidth();
        this.exlist.setIndicatorBounds(width - 40, width - 20);
        // 构建expandablelistview的适配器
        this.exlist_adapter = new AppGroupAdapter(this, this.groups, this.childs);
        this.exlist.setAdapter(this.exlist_adapter); // 绑定视图－适配器
        // exlist.setGroupIndicator(null);
    }

    private void initPath() {
        // TODO Auto-generated method stub
        this.IniFile = new IniFile();
        String configPath = this.getApplicationContext().getFilesDir()
                .getParentFile().getAbsolutePath();
        if (configPath.endsWith("/") == false) {
            configPath = configPath + "/";
        }
        this.appIniFile = configPath + constants.USER_CONFIG_FILE_NAME;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (reloading) {
            initData();
            reloading = false;
        }
    }

    public void changMenuPopState(View v) {
        this.isOpenPop = !this.isOpenPop;
        if (this.isOpenPop) {
            this.popWindow2(v);
        } else {
            if (this.menuWindow != null) {
                this.menuWindow.dismiss();
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void popWindow2(View parent) {
        LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = lay.inflate(R.layout.appmanager_menu, null);
        RelativeLayout add_category = (RelativeLayout) view
                .findViewById(R.id.add_category);
        RelativeLayout delete_category = (RelativeLayout) view
                .findViewById(R.id.delete_category);
        RelativeLayout edit_category = (RelativeLayout) view
                .findViewById(R.id.edit_category);
        RelativeLayout add_app = (RelativeLayout) view
                .findViewById(R.id.add_app);
        RelativeLayout refresh_app = (RelativeLayout) view
                .findViewById(R.id.refresh_app);
        RelativeLayout new_app = (RelativeLayout) view
                .findViewById(R.id.new_app);
        add_category.setOnClickListener(this);
        delete_category.setOnClickListener(this);
        edit_category.setOnClickListener(this);
        add_app.setOnClickListener(this);
        refresh_app.setOnClickListener(this);
        new_app.setOnClickListener(this);
        this.menuWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // �˶�ʵ�ֵ���հ״�����popwindow
        this.menuWindow.setFocusable(true);
        this.menuWindow.setOutsideTouchable(false);
        this.menuWindow.setBackgroundDrawable(new BitmapDrawable());
        this.menuWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
        {
            @Override
            public void onDismiss() {
                AppManagerActivity.this.isOpenPop = false;
            }
        });
        this.menuWindow.showAtLocation(parent, Gravity.RIGHT | Gravity.TOP, 10,
                this.managerTitle.getHeight() * 3 / 2);
        this.menuWindow.update();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(this.MyBroadcastReciver);
        MyActivity.getInstance().finishActivity(this);
    }

    /**
     * 设置哪个二级目录被默认选中
     */
    @Override
    public boolean setSelectedChild(int groupPosition, int childPosition,
                                    boolean shouldExpandGroup) {
        // do something
        return super.setSelectedChild(groupPosition, childPosition,
                shouldExpandGroup);
    }

    /**
     * 设置哪个一级目录被默认选中
     */
    @Override
    public void setSelectedGroup(int groupPosition) {
        // do something
        super.setSelectedGroup(groupPosition);
    }

    /**
     * 当二级条目被点击时响应
     */
    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
                                int groupPosition, int childPosition, long id) {
        // do something
        return super.onChildClick(parent, v, groupPosition, childPosition, id);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case 0:
//               initData();
//            case 1:
//                //来自按钮2的请求，作相应业务处理
//        }
//    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.more_btn:
                this.finish();
                this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
                break;
            case R.id.finish_btn:
                this.changMenuPopState(arg0);
                break;
            case R.id.add_category:
                this.initPath();
                this.showAddCategoryDialog();
                this.menuWindow.dismiss();
                break;
            case R.id.edit_category:
                this.showAddApplicationDialog(3, "");
                this.menuWindow.dismiss();
                break;
            case R.id.new_app:
                reloading = true;
//                this.initPath();
//                this.showNewAppDialog();
                Intent intent = new Intent();
                intent.setClass(this, NewApplication.class);
                startActivity(intent);
                this.menuWindow.dismiss();
                break;
            case R.id.delete_category:
                this.initPath();
                this.showDeleteCategoryDialog();
                this.menuWindow.dismiss();
                break;
            case R.id.refresh_app:
                this.showRefreshApp();
                this.menuWindow.dismiss();
                break;
            case R.id.add_app:
                this.initPath();
                this.showAddApplicationDialog(1, null);
                this.menuWindow.dismiss();
                break;
        }
    }


    @SuppressWarnings("deprecation")
    private void showRefreshApp() {
        this.refreshing = true;
        ProgressDialog Prodialog = new ProgressDialog(this);
        Prodialog.setTitle("扫描应用");
        Prodialog.setMessage("正在扫描，请稍候...");
        Prodialog.setIndeterminate(false);
        Prodialog.setCanceledOnTouchOutside(false);
        Prodialog.setButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method
                // stub
                AppManagerActivity.this.refreshing = false;
            }
        });
        Prodialog.show();
        String webRoot = UIHelper.getStoragePath(this);
        webRoot = webRoot + constants.SD_CARD_TBSSOFT_PATH3 + "/";
        this.initPath();
        File path = new File(webRoot);
        for (File f : path.listFiles()) {
            if (this.refreshing) {
                if (f.isDirectory()) {
                    String appPath = f.getAbsolutePath() + "/";
                    String appDir = f.getName();
                    File inifile = new File(appPath
                            + constants.WEB_CONFIG_FILE_NAME);
                    if (inifile.exists()) {
                        String inifle = this.IniFile.getIniString(appPath
                                        + constants.WEB_CONFIG_FILE_NAME, "TBSWeb",
                                "IniName", constants.NEWS_CONFIG_FILE_NAME,
                                (byte) 0);
                        String title = this.IniFile.getIniString(appPath + inifle,
                                "TBSAPP", "AppName", "", (byte) 0);
                        String resTitle = this.IniFile.getIniString(
                                appPath + inifle, "TBSAPP", "AppCode",
                                "tbs-mis", (byte) 0);
                        String AppCategory = this.IniFile.getIniString(appPath
                                        + inifle, "TBSAPP", "AppCategory", "",
                                (byte) 0);
                        String AppVersion = this.IniFile.getIniString(appPath
                                        + inifle, "TBSAPP", "AppVersion", "1.0",
                                (byte) 0);
                        int resnum = Integer.parseInt(this.IniFile
                                .getIniString(this.appIniFile, "resource", "resnum",
                                        "0", (byte) 0));
                        int curresname = 0;
                        for (int i = 1; i <= resnum; i++) {
                            if (this.IniFile.getIniString(this.appIniFile, "resource",
                                    "resname" + i, "0", (byte) 0)
                                    .equalsIgnoreCase(AppCategory)) {
                                int groupresnum = Integer.parseInt(this.IniFile
                                        .getIniString(this.appIniFile, "group" + i,
                                                "resnum", "0", (byte) 0));
                                for (int j = 1; j <= groupresnum; j++) {
                                    if (resTitle.equalsIgnoreCase(this.IniFile
                                            .getIniString(this.appIniFile, "group"
                                                            + i, "res" + j, "",
                                                    (byte) 0))) {
                                        break;
                                    }
                                    curresname = j;
                                }
                                if (curresname == groupresnum) {
                                    this.IniFile.writeIniString(this.appIniFile, "group"
                                            + i, "resnum", ""
                                            + (groupresnum + 1));
                                    this.IniFile.writeIniString(this.appIniFile, "group"
                                                    + i, "res" + (groupresnum + 1),
                                            resTitle);
                                    this.IniFile.writeIniString(this.appIniFile,
                                            resTitle, "title", title);
                                    this.IniFile.writeIniString(this.appIniFile,
                                            resTitle, "from", "1");
                                    this.IniFile.writeIniString(this.appIniFile,
                                            resTitle, "instdir", appDir);
                                    this.IniFile.writeIniString(this.appIniFile, resTitle, "storePath", AppCategory +
                                            "/" +
                                            appDir);
                                    this.IniFile.writeIniString(this.appIniFile, resTitle, "version", AppVersion);
                                    Map<String, String> childdata = new HashMap<String, String>();
                                    childdata.put("child", title);
                                    childdata.put("from", "1");
                                    childdata.put("path", appDir);
                                    this.childs.get(i - 1).add(childdata);
                                } else {
                                    break;
                                }
                            }
                        }

                    }
                }
            }
        }
        this.exlist_adapter.notifyDataSetChanged();
        Prodialog.dismiss();
    }

    private void showNewAppDialog() {
        LayoutInflater factory = LayoutInflater.from(this);// 提示框
        View view = factory.inflate(R.layout.set_new_app, null);// 这里必须是final的
        final EditText edit_name = (EditText) view
                .findViewById(R.id.edit_name_Text);// 获得输入框对象
        final EditText edit_code = (EditText) view
                .findViewById(R.id.edit_code_Text);
        final EditText edit_file = (EditText) view
                .findViewById(R.id.edit_file_Text);
        final EditText edit_dir = (EditText) view
                .findViewById(R.id.edit_dir_Text);
        final EditText edit_web = (EditText) view
                .findViewById(R.id.edit_web_Text);
        final LinearLayout dynamicTag = (LinearLayout) view
                .findViewById(R.id.account_tip);
        final TextView dynamicTxt = (TextView) view
                .findViewById(R.id.account_error_text);// 错误信息显示
        edit_dir.addTextChangedListener(new TextWatcher()
        {

            private String localPortTxt;

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                this.localPortTxt = String.valueOf(edit_dir.getText());

                if (null != this.localPortTxt && !this.localPortTxt.equals("")
                        && !this.localPortTxt.equals("\\s{1,}")) {
                    edit_file.setText(this.localPortTxt + ".ini");

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
        new Builder(this)
                .setTitle("新建应用")
                .setCancelable(false)
                // 提示框标题
                .setView(view)
                .setPositiveButton("下一步",// 提示框的两个按钮
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                try {
                                    Field field = dialog.getClass()
                                            .getSuperclass()
                                            .getDeclaredField("mShowing");
                                    field.setAccessible(true);
                                    // 将mShowing变量设为false，表示对话框已关闭
                                    field.set(dialog, false);
                                    dialog.dismiss();
                                } catch (Exception e) {

                                }
                                if (StringUtils.isEmpty(edit_name.getText()
                                        .toString())) {
                                    dynamicTag.setVisibility(View.VISIBLE);
                                    dynamicTxt.setText("应用名称不可为空");
                                    return;
                                } else if (StringUtils.isEmpty(edit_code
                                        .getText().toString())) {
                                    dynamicTag.setVisibility(View.VISIBLE);
                                    dynamicTxt.setText("应用代号不可为空");
                                    return;
                                } else if (StringUtils.isEmpty(edit_file
                                        .getText().toString())) {
                                    dynamicTag.setVisibility(View.VISIBLE);
                                    dynamicTxt.setText("配置文件不可为空");
                                    return;
                                } else if (StringUtils.isEmpty(edit_web
                                        .getText().toString())) {
                                    dynamicTag.setVisibility(View.VISIBLE);
                                    dynamicTxt.setText("主页链接不可为空");
                                    return;
                                } else if (StringUtils.isEmpty(edit_dir
                                        .getText().toString())) {
                                    dynamicTag.setVisibility(View.VISIBLE);
                                    dynamicTxt.setText("应用目录不可为空");
                                    return;
                                } else {
                                    int resnum = Integer.parseInt(AppManagerActivity.this.IniFile
                                            .getIniString(AppManagerActivity.this.appIniFile,
                                                    "resource", "resnum", "0",
                                                    (byte) 0));
                                    for (int i = 1; i <= resnum; i++) {
                                        int groupresnum = Integer
                                                .parseInt(AppManagerActivity.this.IniFile.getIniString(
                                                        AppManagerActivity.this.appIniFile,
                                                        "group" + i, "resnum",
                                                        "0", (byte) 0));
                                        for (int j = 1; j <= groupresnum; j++) {
                                            if (edit_code
                                                    .getText()
                                                    .toString()
                                                    .equals(AppManagerActivity.this.IniFile
                                                            .getIniString(
                                                                    AppManagerActivity.this.appIniFile,
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
                                    String webRoot = UIHelper.getStoragePath(AppManagerActivity.this);
                                    webRoot = webRoot
                                            + constants.SD_CARD_TBSSOFT_PATH3
                                            + "/";
                                    File dirFile = new File(webRoot
                                            + edit_dir.getText());
                                    if (dirFile.exists()) {
                                        File iniFile = new File(webRoot
                                                + edit_dir.getText()
                                                + "/"
                                                + edit_file.getText());
                                        if (iniFile.exists()) {
                                            dynamicTag
                                                    .setVisibility(View.VISIBLE);
                                            dynamicTxt.setText("配置文件已存在");
                                            return;
                                        }
                                    } else {
                                        dirFile.mkdirs();

                                        File iniFile = new File(webRoot
                                                + edit_dir.getText()
                                                + "/"
                                                + edit_file.getText());
                                        if (iniFile.exists()) {
                                            dynamicTag
                                                    .setVisibility(View.VISIBLE);
                                            dynamicTxt.setText("配置文件已存在");
                                            return;
                                        }

                                    }
                                    try {
                                        Field field = dialog.getClass()
                                                .getSuperclass()
                                                .getDeclaredField("mShowing");
                                        field.setAccessible(true);
                                        // 将mShowing变量设为false，表示对话框已关闭
                                        field.set(dialog, true);
                                        dialog.dismiss();
                                    } catch (Exception e) {

                                    }
                                }
                                AppManagerActivity.this.NewApp(edit_name.getText().toString(),
                                        edit_code.getText().toString(),
                                        edit_file.getText().toString(),
                                        edit_web.getText().toString(), edit_dir
                                                .getText().toString());


                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                try {
                                    Field field = dialog.getClass()
                                            .getSuperclass()
                                            .getDeclaredField("mShowing");
                                    field.setAccessible(true);
                                    // 将mShowing变量设为false，表示对话框已关闭
                                    field.set(dialog, true);
                                    dialog.dismiss();
                                } catch (Exception e) {

                                }
                            }
                        }).create().show();
    }

    protected void NewApp(String name, String code, String file, String web,
                          String dir) {
        // TODO Auto-generated method stub
        String webRoot = UIHelper.getStoragePath(AppManagerActivity.this);
        webRoot = webRoot + constants.SD_CARD_TBSSOFT_PATH3 + "/";
        File newfile = new File(webRoot + dir + "/" + file);
/*		File newfile2 = new File(webRoot + dir + "/"
                + constants.WEB_CONFIG_FILE_NAME);*/
        try {
            newfile.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        this.IniFile.writeIniString(webRoot + dir + "/" + file, "TBSAPP",
                "defaultUrl", web);
        this.IniFile.writeIniString(webRoot + dir + "/" + file, "TBSAPP", "AppCode",
                code);
        this.IniFile.writeIniString(webRoot + dir + "/" + file, "TBSAPP", "AppName",
                name);
        this.IniFile.writeIniString(webRoot + dir + "/" + file, "TBSAPP", "AppVersion",
                "1.0");
        AppManagerActivity.myThread handThread = new AppManagerActivity.myThread(dir, file);
        handThread.run();
    }

    private boolean doDeploy(String dir) {
        String webRoot = UIHelper.getStoragePath(AppManagerActivity.this);
        webRoot = webRoot + constants.SD_CARD_TBSSOFT_PATH3 + "/" + dir;
        String webRootTbk = webRoot + "Web.tbk";
        try {
            InputStream is = this.getBaseContext().getAssets().open(
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
        this.delZipFile(webRootTbk);
        return true;
    }

    private void showAddApplicationDialog(final int count, final String Path) {
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
                final String apptext = (String) holder.tv.getText();
                final int position = arg2;
                AppManagerActivity.this.ModifyDialog.dismiss();
                if (count == 1) {
                    UIHelper.showFilePathDialog(AppManagerActivity.this, 2,
                            null, new PathChooseDialog.ChooseCompleteListener()
                            {
                                @Override
                                public void onComplete(String finalPath) {
                                    AppManagerActivity.this.AddApplicationDialog(apptext, finalPath,
                                            position);
                                }
                            });
                } else if (count == 2) {
                    String webRoot = UIHelper.getStoragePath(AppManagerActivity.this);
                    webRoot = webRoot + constants.SD_CARD_TBSSOFT_PATH3 + "/";
                    String finalPath = webRoot + Path;
                    AppManagerActivity.this.AddApplicationDialog(apptext, finalPath, position);
                } else if (count == 3) {
                    AppManagerActivity.this.showEditCategoryDialog(position + 1);
                }
            }
        });
        this.DeletemAdapter = new DeleteCategoryAdapter(this.groups, this);
        categoryList.setAdapter(this.DeletemAdapter);
        Builder builder = new AlertDialog.Builder(this);
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
                                File file = new File(path + File.separator
                                        + constants.WEB_CONFIG_FILE_NAME);
                                if (file.exists()) {
                                    if (!StringUtils.isEmpty(AppManagerActivity.this.IniFile
                                            .getIniString(
                                                    path
                                                            + File.separator
                                                            + constants.WEB_CONFIG_FILE_NAME,
                                                    "TBSWeb", "IniName", "",
                                                    (byte) 0))) {
                                        if (AppManagerActivity.this.AddApplication(path, apptext,
                                                position)) {
                                            Toast.makeText(
                                                    AppManagerActivity.this,
                                                    "应用添加成功",
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(
                                                    AppManagerActivity.this,
                                                    "应用已存在", Toast.LENGTH_SHORT)
                                                    .show();
                                        }
                                    } else {
                                        Toast.makeText(
                                                AppManagerActivity.this,
                                                "应用格式不正确，无法添加",
                                                Toast.LENGTH_SHORT).show();
                                    }
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
        Map<String, String> childdata = new HashMap<String, String>();
        childdata.put("child", title);
        childdata.put("from", "1");
        childdata.put("path", inistdir);
        this.childs.get(position).add(childdata);
        this.exlist_adapter.notifyDataSetChanged();
        return true;
    }

    private void showDeleteCategoryDialog() {
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
                int position = arg2 + 1;
                if (Integer.parseInt(AppManagerActivity.this.IniFile.getIniString(AppManagerActivity.this.appIniFile,
                        "group"
                                + position, "resnum", "0", (byte) 0)) <= 0) {
                    AppManagerActivity.this.ModifyDialog.dismiss();
                    AppManagerActivity.this.DeleteCategoryDialog(apptext, position, arg2);
                } else {
                    Toast.makeText(AppManagerActivity.this, "该分类不为空",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        this.DeletemAdapter = new DeleteCategoryAdapter(this.groups, this);
        categoryList.setAdapter(this.DeletemAdapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("删除分类").setCancelable(false)// 提示框标题
                .setView(view).setPositiveButton("取消", null);
        this.ModifyDialog = builder.create();
        this.ModifyDialog.show();
    }

    protected void DeleteCategoryDialog(String apptext, final int position,
                                        final int arg2) {
        // TODO Auto-generated method stub
        new AlertDialog.Builder(this).setCancelable(false)
                .setMessage("确定删除:" + apptext + " 分类")// 提示框标题
                .setPositiveButton("确定",// 提示框的两个按钮
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                AppManagerActivity.this.DeleteCategory(position, arg2);
                            }
                        }).setNegativeButton("取消", null).create().show();
    }

    protected int EditCategory(String categoryName, String categoryColor,
                               int position) {

        // TODO Auto-generated method stub
        int resnum = Integer.parseInt(this.IniFile.getIniString(this.appIniFile,
                "resource", "resnum", "0", (byte) 0));
        for (int i = 1; i <= resnum; i++) {
            if (i != position) {
                String resname = this.IniFile.getIniString(this.appIniFile, "resource",
                        "resname" + i, "0", (byte) 0);
                if (resname.equals(categoryName)) {
                    return 0;
                }
            }
        }
        this.IniFile.writeIniString(this.appIniFile, "resource", "resname" + position,
                categoryName);
        this.IniFile.writeIniString(this.appIniFile, "resource", "resbkcolor" + position,
                categoryColor);
        this.groups.get(position - 1).put("group", categoryName);
        this.exlist_adapter.notifyDataSetChanged();
        return 1;
    }

    protected void DeleteCategory(int position, int arg2) {
        // TODO Auto-generated method stub
        int resnum = Integer.parseInt(this.IniFile.getIniString(this.appIniFile,
                "resource", "resnum", "0", (byte) 0));
        for (int i = 1; i <= resnum; i++) {
            if (i == position) {
                this.IniFile.deleteIniString(this.appIniFile, "resource", "resid" + i);
                this.IniFile.deleteIniString(this.appIniFile, "resource", "resname" + i);
                this.IniFile.deleteIniString(this.appIniFile, "resource", "resicon" + i);
                this.IniFile.deleteIniString(this.appIniFile, "resource", "resbkcolor"
                        + i);
            } else if (i > position) {
                String resid = this.IniFile.getIniString(this.appIniFile, "resource",
                        "resid" + i, "", (byte) 0);
                String resname = this.IniFile.getIniString(this.appIniFile, "resource",
                        "resname" + i, "", (byte) 0);
                String resicon = this.IniFile.getIniString(this.appIniFile, "resource",
                        "resicon" + i, "", (byte) 0);
                String resbkcolor = this.IniFile.getIniString(this.appIniFile,
                        "resource", "resbkcolor" + i, "", (byte) 0);

                this.IniFile.writeIniString(this.appIniFile, "resource", "resid"
                        + (i - 1), resid);
                this.IniFile.writeIniString(this.appIniFile, "resource", "resname"
                        + (i - 1), resname);
                this.IniFile.writeIniString(this.appIniFile, "resource", "resicon"
                        + (i - 1), resicon);
                this.IniFile.writeIniString(this.appIniFile, "resource", "resbkcolor"
                        + (i - 1), resbkcolor);
                this.IniFile.deleteIniString(this.appIniFile, "resource", "resid" + i);
                this.IniFile.deleteIniString(this.appIniFile, "resource", "resname" + i);
                this.IniFile.deleteIniString(this.appIniFile, "resource", "resicon" + i);
                this.IniFile.deleteIniString(this.appIniFile, "resource", "resbkcolor"
                        + i);
            }
        }
        this.IniFile.writeIniString(this.appIniFile, "resource", "resnum", (resnum - 1)
                + "");
        this.groups.remove(arg2);
        this.exlist_adapter.notifyDataSetChanged();
    }

    private void showEditCategoryDialog(final int position) {
        LayoutInflater factory = LayoutInflater.from(this);// 提示框
        View view = factory.inflate(R.layout.set_add_category, null);// 这里必须是final的
        final EditText edit_name = (EditText) view
                .findViewById(R.id.edit_Name_Text);// 获得输入框对象
        final EditText edit_color = (EditText) view
                .findViewById(R.id.edit_Color_Text);// 获得输入框对象
        final LinearLayout dynamicTag = (LinearLayout) view
                .findViewById(R.id.account_tip);
        final TextView dynamicTxt = (TextView) view
                .findViewById(R.id.account_error_text);// 错误信息显示
        edit_name.setText(this.IniFile.getIniString(this.appIniFile, "resource",
                "resname" + position, "", (byte) 0));
        edit_color.setText(this.IniFile.getIniString(this.appIniFile, "resource",
                "resbkcolor" + position, "", (byte) 0));
        new AlertDialog.Builder(this)
                .setTitle("修改应用分类")
                .setCancelable(false)
                // 提示框标题
                .setView(view)
                .setPositiveButton("确定",// 提示框的两个按钮
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                try {
                                    Field field = dialog.getClass()
                                            .getSuperclass()
                                            .getDeclaredField("mShowing");
                                    field.setAccessible(true);
                                    // 将mShowing变量设为false，表示对话框已关闭
                                    field.set(dialog, false);
                                    dialog.dismiss();
                                } catch (Exception e) {

                                }
                                if (StringUtils.isEmpty(edit_name.getText()
                                        .toString())) {
                                    dynamicTag.setVisibility(View.VISIBLE);
                                    dynamicTxt.setText("名字不可为空");
                                } else if (StringUtils.isEmpty(edit_color
                                        .getText().toString())) {
                                    dynamicTag.setVisibility(View.VISIBLE);
                                    dynamicTxt.setText("颜色不可为空");
                                } else {
                                    switch (AppManagerActivity.this.EditCategory(edit_name.getText()
                                            .toString(), edit_color.getText()
                                            .toString(), position)) {
                                        case 0:
                                            dynamicTag.setVisibility(View.VISIBLE);
                                            dynamicTxt.setText("类别已存在");
                                            break;
                                        case 1:
                                            Toast.makeText(
                                                    AppManagerActivity.this,
                                                    "添加成功", Toast.LENGTH_SHORT)
                                                    .show();
                                            try {
                                                Field field = dialog
                                                        .getClass()
                                                        .getSuperclass()
                                                        .getDeclaredField(
                                                                "mShowing");
                                                field.setAccessible(true);
                                                // 将mShowing变量设为false，表示对话框已关闭
                                                field.set(dialog, true);
                                                dialog.dismiss();
                                            } catch (Exception e) {

                                            }
                                            break;
                                    }
                                }
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                try {
                                    Field field = dialog.getClass()
                                            .getSuperclass()
                                            .getDeclaredField("mShowing");
                                    field.setAccessible(true);
                                    // 将mShowing变量设为false，表示对话框已关闭
                                    field.set(dialog, true);
                                    dialog.dismiss();
                                } catch (Exception e) {

                                }
                            }
                        }).create().show();
    }

    private void showAddCategoryDialog() {
        LayoutInflater factory = LayoutInflater.from(this);// 提示框
        View view = factory.inflate(R.layout.set_add_category, null);// 这里必须是final的
        final EditText edit_name = (EditText) view
                .findViewById(R.id.edit_Name_Text);// 获得输入框对象
        final EditText edit_color = (EditText) view
                .findViewById(R.id.edit_Color_Text);// 获得输入框对象
        final LinearLayout dynamicTag = (LinearLayout) view
                .findViewById(R.id.account_tip);
        final TextView dynamicTxt = (TextView) view
                .findViewById(R.id.account_error_text);// 错误信息显示
        new AlertDialog.Builder(this)
                .setTitle("添加应用分类")
                .setCancelable(false)
                // 提示框标题
                .setView(view)
                .setPositiveButton("添加",// 提示框的两个按钮
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                try {
                                    Field field = dialog.getClass()
                                            .getSuperclass()
                                            .getDeclaredField("mShowing");
                                    field.setAccessible(true);
                                    // 将mShowing变量设为false，表示对话框已关闭
                                    field.set(dialog, false);
                                    dialog.dismiss();
                                } catch (Exception e) {

                                }
                                if (StringUtils.isEmpty(edit_name.getText()
                                        .toString())) {
                                    dynamicTag.setVisibility(View.VISIBLE);
                                    dynamicTxt.setText("名字不可为空");
                                } else if (StringUtils.isEmpty(edit_color
                                        .getText().toString())) {
                                    dynamicTag.setVisibility(View.VISIBLE);
                                    dynamicTxt.setText("颜色不可为空");
                                } else {
                                    switch (AppManagerActivity.this.AddCategory(edit_name.getText()
                                            .toString(), edit_color.getText()
                                            .toString())) {
                                        case 0:
                                            dynamicTag.setVisibility(View.VISIBLE);
                                            dynamicTxt.setText("类别已存在");
                                            break;
                                        case 1:
                                            Toast.makeText(
                                                    AppManagerActivity.this,
                                                    "添加成功", Toast.LENGTH_SHORT)
                                                    .show();
                                            try {
                                                Field field = dialog
                                                        .getClass()
                                                        .getSuperclass()
                                                        .getDeclaredField(
                                                                "mShowing");
                                                field.setAccessible(true);
                                                // 将mShowing变量设为false，表示对话框已关闭
                                                field.set(dialog, true);
                                                dialog.dismiss();
                                            } catch (Exception e) {

                                            }
                                            break;
                                    }
                                }
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                try {
                                    Field field = dialog.getClass()
                                            .getSuperclass()
                                            .getDeclaredField("mShowing");
                                    field.setAccessible(true);
                                    // 将mShowing变量设为false，表示对话框已关闭
                                    field.set(dialog, true);
                                    dialog.dismiss();
                                } catch (Exception e) {

                                }
                            }
                        }).create().show();
    }

    protected int AddCategory(String categoryName, String categoryColor) {
        // TODO Auto-generated method stub
        int resnum = Integer.parseInt(this.IniFile.getIniString(this.appIniFile,
                "resource", "resnum", "0", (byte) 0));
        for (int i = 1; i <= resnum; i++) {
            String resname = this.IniFile.getIniString(this.appIniFile, "resource",
                    "resname" + i, "0", (byte) 0);
            if (resname.equals(categoryName)) {
                return 0;
            }
        }
        resnum = resnum + 1;
        this.IniFile.writeIniString(this.appIniFile, "resource", "resnum", resnum + "");
        this.IniFile.writeIniString(this.appIniFile, "resource", "resid" + resnum,
                "group" + resnum);
        this.IniFile.writeIniString(this.appIniFile, "resource", "resname" + resnum,
                categoryName);
        this.IniFile.writeIniString(this.appIniFile, "resource", "resicon" + resnum, "");
        this.IniFile.writeIniString(this.appIniFile, "resource", "resbkcolor" + resnum,
                categoryColor);
        Map<String, String> group = new HashMap<String, String>();
        group.put("group", categoryName);
        this.groups.add(group);
        List<Map<String, String>> child = new ArrayList<Map<String, String>>();
        this.childs.add(child);
        this.exlist_adapter.notifyDataSetChanged();
        return 1;
    }

    private class MyBroadcastReciver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("recommend"
                    + getString(R.string.about_title))) {
                int groupPosition = intent.getIntExtra("groupPosition", -1);
                int childPosition = intent.getIntExtra("childPosition", -1);
                if (groupPosition != -1 && childPosition != -1) {
                    int resnum = Integer.parseInt(AppManagerActivity.this.IniFile.getIniString(
                            AppManagerActivity.this.appIniFile, "group" + (groupPosition + 1),
                            "resnum", "0", (byte) 0));
                    for (int i = 1; i <= resnum; i++) {
                        if (i == childPosition + 1) {
                            AppManagerActivity.this.IniFile.deleteIniString(AppManagerActivity.this.appIniFile, "group"
                                    + (groupPosition + 1), "res" + i);
                        } else if (i > childPosition + 1) {
                            String resid = AppManagerActivity.this.IniFile.getIniString(AppManagerActivity.this
                                            .appIniFile,
                                    "group" + (groupPosition + 1), "res" + i,
                                    "", (byte) 0);
                            AppManagerActivity.this.IniFile.writeIniString(AppManagerActivity.this.appIniFile, "group"
                                            + (groupPosition + 1), "res" + (i - 1),
                                    resid);
                            AppManagerActivity.this.IniFile.deleteIniString(AppManagerActivity.this.appIniFile, "group"
                                    + (groupPosition + 1), "res" + i);
                        }
                    }
                    AppManagerActivity.this.IniFile.writeIniString(AppManagerActivity.this.appIniFile, "group"
                            + (groupPosition + 1), "resnum", (resnum - 1) + "");
                    AppManagerActivity.this.childs.get(groupPosition).remove(childPosition);
                    // groups.remove(arg2);
                }
                AppManagerActivity.this.exlist_adapter.notifyDataSetChanged();
            }
        }
    }

    protected void delZipFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    Handler myHandler = new Handler()
    {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String webRoot = UIHelper.getStoragePath(AppManagerActivity.this);
                    webRoot = webRoot + constants.SD_CARD_TBSSOFT_PATH3 + "/";
                    Bundle b = msg.getData();
                    String dir = b.getString("dir");
                    String file = b.getString("file");
                    AppManagerActivity.this.IniFile.writeIniString(webRoot + dir + "/"
                            + constants.WEB_CONFIG_FILE_NAME, "TBSWeb", "IniName", file);
                    AppManagerActivity.this.showAddApplicationDialog(2, dir);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    class myThread implements Runnable
    {
        private final String file;
        private final String dir;

        myThread(String dir, String file) {
            this.dir = dir;
            this.file = file;
        }

        public void run() {

            AppManagerActivity.this.doDeploy(this.dir);
            Message message = new Message();
            message.what = 1;
            Bundle b = new Bundle();
            b.putString("file", this.file);
            b.putString("dir", this.dir);
            message.setData(b);
            myHandler.sendMessage(message);
        }
    }
}