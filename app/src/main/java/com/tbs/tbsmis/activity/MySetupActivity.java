package com.tbs.tbsmis.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.city.SelectProvinceActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.BitmapUtil;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.ImageLoader;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MySetupActivity extends Activity implements View.OnClickListener
{

    protected static final String TAG = "MySetupActivity";
    private ImageView leftBtn;
    private TextView title;

    private ImageView avatar_iv;
    private TextView nickname_tv;
    private TextView gender;
    private TextView district;

    private RelativeLayout avatar_click;
    private RelativeLayout nickname_click;
    private RelativeLayout signature_click;
    private RelativeLayout web_click;
    private RelativeLayout district_click;

    private String headPath;
    private Dialog dialog;
    private ProgressDialog Prodialog;
    private  Bitmap bitmap = null;
    private String webRoot;
    private IniFile m_iniFileIO;
    private String appFile;

    private final String[] items = {"选择本地图片", "拍照"};
    private final String[] items1 = {"男", "女"};
    private TextView contact;
    private RelativeLayout contact_click;
    private RelativeLayout sex_click;
    private Button quitBtn;
    private TextView signature;
    private TextView mobile;
    private TextView web;
    private String mNewIntentCity = "";
    private TextView location;
    private Dialog ModifyDialog;
    private LinearLayout dynamicTag;
    private TextView dynamicTxt;
    private LinearLayout progressTag;
    private ImageView iv;
    private EditText edit;
    private RelativeLayout mobile_click;
    private EditText edit1;
    private RelativeLayout pwd_click;
    private ImageView RightBtn;
    private RelativeLayout account_click;
    private boolean InfoMark;
    private TextView location_province;
    private ImageLoader imageLoader;
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESIZE_REQUEST_CODE = 2;
    private static final int CITY_REQUEST_CODE = 3;
    private static final int AVATAR_REQUEST_CODE = 4;
    private static final int PHONE_REQUEST_CODE = 5;
    private static final int EMAIL_REQUEST_CODE = 6;
    private static final int PASSWORD_REQUEST_CODE = 7;
    private static final int QUIT_REQUEST_CODE = 8;
    private static final int USERNAME_REQUEST_CODE = 9;
    private static final int TEST_REQUEST_CODE = 10;

    private static final String IMAGE_FILE_NAME = "header.jpg";
    private static boolean Unupdate;
    private String  userIni;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        MyActivity.getInstance().addActivity(this);
        this.setContentView(R.layout.layout_sapi_home);
        this.initPath();
        this.titleView();
        this.setting();
    }

    private void initPath() {
        this.m_iniFileIO = new IniFile();
        this.webRoot = UIHelper.getSoftPath(this);
        if (this.webRoot.endsWith("/") == false) {
            this.webRoot += "/";
        }
        this.webRoot += getString(R.string.SD_CARD_TBSAPP_PATH2);
        this.webRoot = UIHelper.getShareperference(this, constants.SAVE_INFORMATION,
                "Path", this.webRoot);
        if (this.webRoot.endsWith("/") == false) {
            this.webRoot += "/";
        }
        String WebIniFile = this.webRoot + constants.WEB_CONFIG_FILE_NAME;
        this.appFile = this.webRoot
                + this.m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        this.userIni = this.appFile;
        if(Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1){
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            this.userIni = dataPath + "TbsApp.ini";
        }
    }

    private void titleView() {
        this.leftBtn = (ImageView) this.findViewById(R.id.more_btn);
        this.RightBtn = (ImageView) this.findViewById(R.id.finish_btn);
        this.title = (TextView) this.findViewById(R.id.textView1);
        this.title.setText("个人信息");
    }

    public void setting() {

        this.avatar_iv = (ImageView) this.findViewById(R.id.avatar_iv);
        this.nickname_tv = (TextView) this.findViewById(R.id.sapi_account_showname);
        this.gender = (TextView) this.findViewById(R.id.sapi_account_sex);
        this.district = (TextView) this.findViewById(R.id.sapi_account_username);
        this.contact = (TextView) this.findViewById(R.id.sapi_account_contact);
        this.signature = (TextView) this.findViewById(R.id.sapi_account_signature);
        this.web = (TextView) this.findViewById(R.id.sapi_account_web);
        this.mobile = (TextView) this.findViewById(R.id.sapi_account_mobile);
        this.location_province = (TextView) this.findViewById(R.id.sapi_account_province);
        this.location = (TextView) this.findViewById(R.id.sapi_account_city);

        this.web_click = (RelativeLayout) this.findViewById(R.id.sapi_account_web_li);
        this.signature_click = (RelativeLayout) this.findViewById(R.id.sapi_account_signature_li);
        this.contact_click = (RelativeLayout) this.findViewById(R.id.sapi_account_username_contact);
        this.avatar_click = (RelativeLayout) this.findViewById(R.id.sapi_account_displayname_li);
        this.nickname_click = (RelativeLayout) this.findViewById(R.id.sapi_account_username_li);
        this.account_click = (RelativeLayout) this.findViewById(R.id.sapi_account_li);
        this.district_click = (RelativeLayout) this.findViewById(R.id.sapi_account_where_li);
        this.mobile_click = (RelativeLayout) this.findViewById(R.id.sapi_account_mobile_li);
        this.sex_click = (RelativeLayout) this.findViewById(R.id.sapi_account_username_sex);
        this.pwd_click = (RelativeLayout) this.findViewById(R.id.modifyPwd);
        this.quitBtn = (Button) this.findViewById(R.id.sapi_logout_btn);

        this.leftBtn.setOnClickListener(this);
        this.RightBtn.setOnClickListener(this);
        this.quitBtn.setOnClickListener(this);
        this.sex_click.setOnClickListener(this);
        this.pwd_click.setOnClickListener(this);
        this.web_click.setOnClickListener(this);
        this.contact_click.setOnClickListener(this);
        this.avatar_click.setOnClickListener(this);
        this.nickname_click.setOnClickListener(this);
        this.account_click.setOnClickListener(this);
        this.district_click.setOnClickListener(this);
        this.signature_click.setOnClickListener(this);
        this.mobile_click.setOnClickListener(this);
        // ����ͷ��
        this.headPath = this.webRoot
                + "user/"
                + this.m_iniFileIO.getIniString(this.userIni, "Login", "Account", "",
                (byte) 0) + "/head/head.jpg";
        String Path = "http://"
                + this.m_iniFileIO.getIniString(this.userIni, "Login", "ebsAddress",
                constants.DefaultServerIp, (byte) 0)
                + ":"
                + this.m_iniFileIO.getIniString(this.userIni, "Login", "ebsPort",
                "8083", (byte) 0)
                + this.m_iniFileIO.getIniString(this.userIni, "Login", "ebsPath",
                "/EBS/UserServlet", (byte) 0)
                + "?act=downloadHead&account="
                + this.m_iniFileIO.getIniString(this.userIni, "Login", "Account", "",
                (byte) 0);
        this.imageLoader = new ImageLoader(this, R.drawable.default_avatar);
        this.imageLoader.DisplayImage(Path, this.avatar_iv);

        if (StringUtils.isEmpty(this.m_iniFileIO.getIniString(this.userIni, "Login",
                "NickName", "匿名", (byte) 0))) {
            this.nickname_tv.setText("匿名");
        } else {
            this.nickname_tv.setText(this.m_iniFileIO.getIniString(this.userIni, "Login",
                    "NickName", "匿名", (byte) 0));
        }
        if (StringUtils.isEmpty(this.m_iniFileIO.getIniString(this.userIni, "Login",
                "Contact", "未绑定", (byte) 0))) {
            if (StringUtils.isEmpty(this.m_iniFileIO.getIniString(this.userIni, "Login",
                    "newEMail", "未绑定", (byte) 0))) {
                this.contact.setText("未绑定");
            } else {
                this.contact.setText(this.m_iniFileIO.getIniString(this.userIni, "Login",
                        "newEMail", "未绑定", (byte) 0) + "(未激活)");
            }
        } else {
            this.contact.setText(this.m_iniFileIO.getIniString(this.userIni, "Login",
                    "Contact", "未绑定", (byte) 0));
        }
        if (StringUtils.isEmpty(this.m_iniFileIO.getIniString(this.userIni, "Login",
                "UserCode", "", (byte) 0))) {
            this.district.setText(this.m_iniFileIO.getIniString(this.userIni, "Login",
                    "Account", "", (byte) 0));
        } else {
            this.district.setText(this.m_iniFileIO.getIniString(this.userIni, "Login",
                    "UserCode", "", (byte) 0));
        }
        this.signature.setText(this.m_iniFileIO.getIniString(this.userIni, "Login",
                "Signature", "", (byte) 0));
        this.gender.setText(this.m_iniFileIO.getIniString(this.userIni, "Login", "Sex", "男",
                (byte) 0));
        this.web.setText(this.m_iniFileIO.getIniString(this.userIni, "Login", "Address", "",
                (byte) 0));
        this.location.setText(this.m_iniFileIO.getIniString(this.userIni, "Login",
                "Location_city", "", (byte) 0));
        this.location_province.setText(this.m_iniFileIO.getIniString(this.userIni, "Login",
                "Location_province", "", (byte) 0));
        if (StringUtils.isEmpty(this.m_iniFileIO.getIniString(this.userIni, "Login",
                "Mobile", "未绑定", (byte) 0))) {
            this.mobile.setText("未绑定");
        } else {
            this.mobile.setText(this.m_iniFileIO.getIniString(this.userIni, "Login", "Mobile",
                    "未绑定", (byte) 0));
        }
    }

    //
    @Override
    protected void onResume() {
        super.onResume();
        if (!StringUtils.isEmpty(UIHelper.getShareperference(this, "city",
                "_city", ""))) {
            this.location.setText(UIHelper.getShareperference(this, "city", "_city",
                    ""));
            this.location_province.setText(UIHelper.getShareperference(this, "city",
                    "_province", ""));
            this.InfoMark = true;
            UIHelper.setSharePerference(this, "city", "_city", "");
            UIHelper.setSharePerference(this, "city", "_province", "");
        }
        if (!StringUtils.isEmpty(UIHelper.getShareperference(this,
                constants.SAVE_LOCALMSGNUM, "mobile", ""))) {
            this.m_iniFileIO.writeIniString(this.userIni, "Login", "Mobile", UIHelper
                    .getShareperference(this, constants.SAVE_LOCALMSGNUM,
                            "mobile", ""));
            this.mobile.setText(UIHelper.getShareperference(this,
                    constants.SAVE_LOCALMSGNUM, "mobile", ""));
            UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM,
                    "mobile", "");
        }
        if (!StringUtils.isEmpty(UIHelper.getShareperference(this,
                constants.SAVE_LOCALMSGNUM, "email", ""))) {
            this.m_iniFileIO.writeIniString(this.userIni, "Login", "Contact", UIHelper
                    .getShareperference(this, constants.SAVE_LOCALMSGNUM,
                            "email", ""));
            this.contact.setText(UIHelper.getShareperference(this,
                    constants.SAVE_LOCALMSGNUM, "email", ""));
            UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM,
                    "email", "");

        }
    }

    // ���ʱ��������
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.bitmap != null) {
            this.bitmap.recycle();
        }
        this.imageLoader.clearCache();
        // 结束Activity&从堆栈中移除
        MyActivity.getInstance().finishActivity(this);

    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sapi_account_displayname_li:
                this.showDialog();
                break;
            case R.id.more_btn:
                if (this.InfoMark == true) {
                    this.InfoMark = false;
                    this.showModifyDialog();
                } else {
                    this.finish();
                    this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
                }
                break;
            case R.id.finish_btn:
                if (this.InfoMark == true) {
                    this.InfoMark = false;
                    this.connect(MySetupActivity.AVATAR_REQUEST_CODE);
                    this.Prodialog = new ProgressDialog(this);
                    this.Prodialog.setTitle("信息保存");
                    this.Prodialog.setMessage("正在保存，请稍候...");
                    this.Prodialog.setIndeterminate(false);
                    this.Prodialog.setCanceledOnTouchOutside(false);
                    this.Prodialog.setButton("取消",
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub
                                    dialog.dismiss();
                                }
                            });
                    this.Prodialog.show();
                } else {
                    this.finish();
                    this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
                }
                break;
            case R.id.sapi_account_username_li:
                this.showNickDialog();
                break;
            case R.id.sapi_account_li:
                if (Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni, "Login",
                        "AccountFlag", "0", (byte) 0)) == 0) {
                    this.showAccountDialog();
                } else {
                    Toast.makeText(this, "不可修改",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.sapi_account_web_li:
                this.showWebDialog();
                break;
            case R.id.sapi_account_mobile_li:
                this.showPhoneDialog();
                break;
            case R.id.sapi_account_username_contact:
                this.showContactDialog();
                break;
            case R.id.sapi_account_where_li:
                Intent i = new Intent(this, SelectProvinceActivity.class);
                this.startActivityForResult(i, MySetupActivity.CITY_REQUEST_CODE);
                break;
            case R.id.sapi_account_username_sex:
                this.showSexDialog();
                break;
            case R.id.modifyPwd:
                this.showPwdDialog();
                break;
            case R.id.sapi_account_signature_li:
                this.showSignatureDialog();
                break;
            case R.id.sapi_logout_btn:
                this.connect(MySetupActivity.QUIT_REQUEST_CODE);
                this.Prodialog = new ProgressDialog(this);
                this.Prodialog.setTitle("退出登录");
                this.Prodialog.setMessage("正在退出，请稍候...");
                this.Prodialog.setIndeterminate(false);
                this.Prodialog.setCanceledOnTouchOutside(false);
                this.Prodialog.setButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });
                this.Prodialog.show();
                break;
            default:
                break;
        }
    }

    private void showModifyDialog() {
        new Builder(this)
                .setMessage("是否保存修改信息")
                .setPositiveButton("确定",// 提示框的两个按钮
                        new DialogInterface.OnClickListener()
                        {
                            @SuppressWarnings("deprecation")
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                MySetupActivity.this.InfoMark = false;
                                MySetupActivity.this.connect(MySetupActivity.AVATAR_REQUEST_CODE);
                                MySetupActivity.this.Prodialog = new ProgressDialog(
                                        MySetupActivity.this);
                                MySetupActivity.this.Prodialog.setTitle("信息保存");
                                MySetupActivity.this.Prodialog.setMessage("正在保存，请稍候...");
                                MySetupActivity.this.Prodialog.setIndeterminate(false);
                                MySetupActivity.this.Prodialog.setCanceledOnTouchOutside(false);
                                MySetupActivity.this.Prodialog.setButton("取消",
                                        new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                // TODO Auto-generated method
                                                // stub
                                                dialog.dismiss();
                                            }
                                        });
                                MySetupActivity.this.Prodialog.show();
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                MySetupActivity.this.finish();
                                MySetupActivity.this.overridePendingTransition(R.anim.push_in,
                                        R.anim.push_out);
                            }
                        }).create().show();
    }

    private void showDialog() {
        new Builder(this)
                .setTitle("设置头像")
                .setItems(this.items, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intentFromGallery = new Intent(
                                        Intent.ACTION_GET_CONTENT);
                                intentFromGallery.setType("image/*");
                                intentFromGallery
                                        .addCategory(Intent.CATEGORY_OPENABLE);
                                MySetupActivity.this.startActivityForResult(Intent.createChooser(
                                        intentFromGallery, "选择图片"),
                                        MySetupActivity.IMAGE_REQUEST_CODE);
                                break;
                            case 1:
                                Intent intentFromCapture = new Intent(
                                        MediaStore.ACTION_IMAGE_CAPTURE);
                                // 判断存储卡是否可以用，可用进行存储
                                if (MySetupActivity.this.isSdcardExisting()) {
                                    intentFromCapture.putExtra(
                                            MediaStore.EXTRA_OUTPUT,
                                            Uri.fromFile(new File(Environment
                                                    .getExternalStorageDirectory(),
                                                    MySetupActivity.IMAGE_FILE_NAME)));
                                }
                                MySetupActivity.this.startActivityForResult(intentFromCapture,
                                        MySetupActivity.CAMERA_REQUEST_CODE);
                                break;
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void showSexDialog() {
        new Builder(this).setTitle("设置性别")
                .setItems(this.items1, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                MySetupActivity.this.InfoMark = true;
                                MySetupActivity.this.gender.setText("男");
                                break;
                            case 1:
                                MySetupActivity.this.InfoMark = true;
                                MySetupActivity.this.gender.setText("女");
                                break;
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void showNickDialog() {
        LayoutInflater factory = LayoutInflater.from(this);// 提示框
        View view = factory.inflate(R.layout.set_account_username, null);// 这里必须是final的
        final EditText edit = (EditText) view.findViewById(R.id.editText);// 获得输入框对象
        edit.setText(this.m_iniFileIO.getIniString(this.userIni, "Login", "NickName", "",
                (byte) 0));
        edit.setSelection(edit.getText().toString().length());// 光标位置
        // edit.setCursorVisible(false);
        edit.setSelectAllOnFocus(true);// 全选文本
        new Builder(this).setTitle("修改昵称")// 提示框标题
                .setView(view).setPositiveButton("提交",// 提示框的两个按钮
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        MySetupActivity.this.InfoMark = true;
                        MySetupActivity.this.nickname_tv.setText(edit.getText().toString());
                    }
                }).setNegativeButton("取消", null).create().show();
    }

    private void showSignatureDialog() {
        LayoutInflater factory = LayoutInflater.from(this);// 提示框
        View view = factory.inflate(R.layout.set_account_username, null);// 这里必须是final的
        final EditText edit = (EditText) view.findViewById(R.id.editText);// 获得输入框对象
        edit.setHint(R.string.sapi_signature_hint);
        edit.setText(this.m_iniFileIO.getIniString(this.userIni, "Login", "Signature", "",
                (byte) 0));
        edit.setSelection(edit.getText().toString().length());// 光标位置在文本最后
        edit.setSelectAllOnFocus(true);// 全选文本
        new Builder(this).setTitle("修改个性签名")// 提示框标题
                .setView(view).setPositiveButton("提交",// 提示框的两个按钮
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        MySetupActivity.this.InfoMark = true;
                        MySetupActivity.this.signature.setText(edit.getText().toString());
                    }
                }).setNegativeButton("取消", null).create().show();
    }

    private void showAccountDialog() {
        this.initPath();
        LayoutInflater factory = LayoutInflater.from(this);// 提示框
        View view = factory.inflate(R.layout.set_account_username, null);// 这里必须是final的
        this.edit = (EditText) view.findViewById(R.id.editText);// 获得输入框对象
        this.dynamicTag = (LinearLayout) view.findViewById(R.id.account_tip);
        this.progressTag = (LinearLayout) view.findViewById(R.id.account_progress);
        this.dynamicTxt = (TextView) view.findViewById(R.id.account_error_text);// 错误信息显示
        this.iv = (ImageView) view.findViewById(R.id.loading);
        this.dynamicTag.setVisibility(View.VISIBLE);
        this.dynamicTxt.setText(R.string.sapi_account_new_tip);
        this.edit.setHint(R.string.sapi_account_new_hint);
        this.edit.setText(this.m_iniFileIO.getIniString(this.userIni, "Login", "Account", "",
                (byte) 0));
        this.edit.setSelection(this.edit.getText().toString().length());// 光标位置
        // edit.setSelectAllOnFocus(true);// 全选文本
        Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改账号");// 提示框标题
        builder.setCancelable(false);
        builder.setView(view);
        builder.setPositiveButton("提交",// 提示框的两个按钮
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (StringUtils
                                .checkUserName(MySetupActivity.this.edit.getText().toString())) {
                            MySetupActivity.this.connect(MySetupActivity.USERNAME_REQUEST_CODE);
                        } else {
                            MySetupActivity.this.dynamicTag.setVisibility(View.VISIBLE);
                            MySetupActivity.this.dynamicTxt
                                    .setText(R.string.sapi_username_format_error);
                        }
                        try {
                            Field field = dialog.getClass().getSuperclass()
                                    .getDeclaredField("mShowing");
                            field.setAccessible(true);
                            // 将mShowing变量设为false，表示对话框已关闭
                            field.set(dialog, false);
                            dialog.dismiss();
                        } catch (Exception e) {
                        }
                    }
                });
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Field field = dialog.getClass().getSuperclass()
                                    .getDeclaredField("mShowing");
                            field.setAccessible(true);
                            // 将mShowing变量设为false，表示对话框已关闭
                            field.set(dialog, true);
                            dialog.dismiss();
                        } catch (Exception e) {

                        }
                    }
                });
        this.ModifyDialog = builder.create();
        this.ModifyDialog.show();
    }

    private void showPhoneDialog() {
        this.initPath();
        LayoutInflater factory = LayoutInflater.from(this);// 提示框
        View view = factory.inflate(R.layout.set_account_username, null);// 这里必须是final的
        this.edit = (EditText) view.findViewById(R.id.editText);// 获得输入框对象
        this.dynamicTag = (LinearLayout) view.findViewById(R.id.account_tip);
        this.progressTag = (LinearLayout) view.findViewById(R.id.account_progress);
        this.dynamicTxt = (TextView) view.findViewById(R.id.account_error_text);// 错误信息显示
        this.iv = (ImageView) view.findViewById(R.id.loading);
        this.edit.setHint(R.string.sapi_phone_hint);
        this.edit.setText(this.m_iniFileIO.getIniString(this.userIni, "Login", "Mobile", "",
                (byte) 0));
        this.edit.setSelection(this.edit.getText().toString().length());// 光标位置
        // edit.setSelectAllOnFocus(true);// 全选文本
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改绑定手机");// 提示框标题
        builder.setCancelable(false);
        builder.setView(view);
        builder.setPositiveButton("提交",// 提示框的两个按钮
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (StringUtils.checkPhone(MySetupActivity.this.edit.getText().toString())) {
                            MySetupActivity.this.connect(MySetupActivity.PHONE_REQUEST_CODE);
                        } else {
                            MySetupActivity.this.dynamicTag.setVisibility(View.VISIBLE);
                            MySetupActivity.this.dynamicTxt
                                    .setText(R.string.sapi_phone_format_error);
                        }
                        try {
                            Field field = dialog.getClass().getSuperclass()
                                    .getDeclaredField("mShowing");
                            field.setAccessible(true);
                            // 将mShowing变量设为false，表示对话框已关闭
                            field.set(dialog, false);
                            dialog.dismiss();
                        } catch (Exception e) {
                        }

                    }
                });
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Field field = dialog.getClass().getSuperclass()
                                    .getDeclaredField("mShowing");
                            field.setAccessible(true);
                            // 将mShowing变量设为false，表示对话框已关闭
                            field.set(dialog, true);
                            dialog.dismiss();
                        } catch (Exception e) {

                        }
                    }
                });
        this.ModifyDialog = builder.create();
        this.ModifyDialog.show();
    }

    private void showContactDialog() {
        this.initPath();
        LayoutInflater factory = LayoutInflater.from(this);// 提示框
        View view = factory.inflate(R.layout.set_account_username, null);// 这里必须是final的
        this.edit = (EditText) view.findViewById(R.id.editText);// 获得输入框对象
        this.dynamicTag = (LinearLayout) view.findViewById(R.id.account_tip);
        this.progressTag = (LinearLayout) view.findViewById(R.id.account_progress);
        this.dynamicTxt = (TextView) view.findViewById(R.id.account_error_text);// 错误信息显示
        this.iv = (ImageView) view.findViewById(R.id.loading);
        this.edit.setHint(R.string.sapi_email);
        this.edit.setText(this.m_iniFileIO.getIniString(this.userIni, "Login", "Contact",
                this.m_iniFileIO.getIniString(this.userIni, "Login", "newEMail", "",
                        (byte) 0), (byte) 0));
        this.edit.setSelection(this.edit.getText().toString().length());// 光标位置
        // edit.setSelectAllOnFocus(true);// 全选文本
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改绑定邮箱");// 提示框标题
        builder.setCancelable(false);
        builder.setView(view);
        builder.setPositiveButton("提交",// 提示框的两个按钮
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (StringUtils.checkEmail(MySetupActivity.this.edit.getText().toString())) {
                            MySetupActivity.this.connect(MySetupActivity.EMAIL_REQUEST_CODE);
                        } else {
                            MySetupActivity.this.dynamicTag.setVisibility(View.VISIBLE);
                            MySetupActivity.this.dynamicTxt
                                    .setText(R.string.sapi_email_format_error);
                        }
                        try {
                            Field field = dialog.getClass().getSuperclass()
                                    .getDeclaredField("mShowing");
                            field.setAccessible(true);
                            // 将mShowing变量设为false，表示对话框已关闭
                            field.set(dialog, false);
                            dialog.dismiss();
                        } catch (Exception e) {

                        }
                    }

                });
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Field field = dialog.getClass().getSuperclass()
                                    .getDeclaredField("mShowing");
                            field.setAccessible(true);
                            // 将mShowing变量设为false，表示对话框已关闭
                            field.set(dialog, true);
                            dialog.dismiss();
                        } catch (Exception e) {

                        }
                    }
                });
        this.ModifyDialog = builder.create();
        this.ModifyDialog.show();
    }

    private void showWebDialog() {
        LayoutInflater factory = LayoutInflater.from(this);// 提示框
        View view = factory.inflate(R.layout.set_account_username, null);// 这里必须是final的
        final EditText edit = (EditText) view.findViewById(R.id.editText);// 获得输入框对象
        edit.setHint(R.string.sapi_web_hint);
        edit.setText(this.m_iniFileIO.getIniString(this.userIni, "Login", "Address", "",
                (byte) 0));
        edit.setSelection(edit.getText().toString().length());
        edit.setSelectAllOnFocus(true);// 文本全选
        new AlertDialog.Builder(this).setTitle("修改网址")// 提示框标题
                .setView(view).setPositiveButton("提交",// 提示框的两个按钮
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        MySetupActivity.this.web.setText(edit.getText().toString());
                        MySetupActivity.this.InfoMark = true;
                    }
                }).setNegativeButton("取消", null).create().show();
    }

    private void showPwdDialog() {
        this.initPath();
        LayoutInflater factory = LayoutInflater.from(this);// 提示框
        View view = factory.inflate(R.layout.set_account_password, null);// 这里必须是final的
        this.edit = (EditText) view.findViewById(R.id.editText);// 获得输入框对象
        this.edit1 = (EditText) view.findViewById(R.id.newPwdText);// 获得输入框对象
        final EditText edit2 = (EditText) view.findViewById(R.id.newPwdText2);// 获得输入框对象
        this.dynamicTag = (LinearLayout) view.findViewById(R.id.account_tip);
        this.progressTag = (LinearLayout) view.findViewById(R.id.account_progress);
        this.dynamicTxt = (TextView) view.findViewById(R.id.account_error_text);// 错误信息显示
        this.iv = (ImageView) view.findViewById(R.id.loading);
        // edit.setSelectAllOnFocus(true);// 全选文本
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改密码");// 提示框标题
        builder.setCancelable(false);
        builder.setView(view);
        builder.setPositiveButton("提交",// 提示框的两个按钮
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (StringUtils.isEmpty(MySetupActivity.this.edit.getText().toString())
                                || StringUtils.isEmpty(MySetupActivity.this.edit1.getText()
                                .toString())
                                || StringUtils.isEmpty(edit2.getText()
                                .toString())) {
                            MySetupActivity.this.dynamicTag.setVisibility(View.VISIBLE);
                            MySetupActivity.this.dynamicTxt.setText("不可为空");
                        } else {
                            if (!StringUtils.checkPassWord(MySetupActivity.this.edit1
                                    .getText().toString())
                                    || !StringUtils.checkPassWord(edit2
                                    .getText().toString())) {
                                MySetupActivity.this.dynamicTag.setVisibility(View.VISIBLE);
                                MySetupActivity.this.dynamicTxt
                                        .setText(R.string.sapi_password_format_error);
                            } else if (!MySetupActivity.this.edit1.getText().toString()
                                    .equals(edit2.getText().toString())) {
                                MySetupActivity.this.dynamicTag.setVisibility(View.VISIBLE);
                                MySetupActivity.this.dynamicTxt.setText("两次输入的密码不相同");
                            } else {
                                MySetupActivity.this.connect(MySetupActivity.PASSWORD_REQUEST_CODE);
                            }
                        }
                        try {
                            Field field = dialog.getClass().getSuperclass()
                                    .getDeclaredField("mShowing");
                            field.setAccessible(true);
                            // 将mShowing变量设为false，表示对话框已关闭
                            field.set(dialog, false);
                            dialog.dismiss();
                        } catch (Exception e) {

                        }
                    }

                });
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Field field = dialog.getClass().getSuperclass()
                                    .getDeclaredField("mShowing");
                            field.setAccessible(true);
                            // 将mShowing变量设为false，表示对话框已关闭
                            field.set(dialog, true);
                            dialog.dismiss();
                        } catch (Exception e) {

                        }
                    }
                });
        this.ModifyDialog = builder.create();
        this.ModifyDialog.show();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        } else {
            switch (requestCode) {
                case MySetupActivity.IMAGE_REQUEST_CODE:
                    this.resizeImage(data.getData());
                    break;
                case MySetupActivity.CAMERA_REQUEST_CODE:
                    if (this.isSdcardExisting()) {
                        File tempFile = new File(
                                Environment.getExternalStorageDirectory(),
                                MySetupActivity.IMAGE_FILE_NAME);
                        this.resizeImage(Uri.fromFile(tempFile));
                    } else {
                        Toast.makeText(this,
                                "未找到存储卡，无法存储照片！", Toast.LENGTH_LONG).show();
                    }
                    break;

                case MySetupActivity.CITY_REQUEST_CODE:
                    this.mNewIntentCity = data.getStringExtra("city");
                    if (!StringUtils.isEmail(this.mNewIntentCity)) {
                        this.location.setText(this.mNewIntentCity);
                        UIHelper.setSharePerference(this, "city", "_city",
                                this.mNewIntentCity);
                        this.m_iniFileIO.writeIniString(this.userIni, "Login", "Location",
                                this.mNewIntentCity);
                    }
                    break;
                case MySetupActivity.RESIZE_REQUEST_CODE:
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        File fileTemp = FileUtils.createHeadFile(this.headPath);
                        this.headPath = fileTemp.getAbsolutePath();
                        boolean result = FileUtils.writeFile(fileTemp,
                                (Bitmap) extras.getParcelable("data"));
                        if (result) {
                            this.Prodialog = new ProgressDialog(this);
                            this.Prodialog.setTitle("上传头像");
                            this.Prodialog.setMessage("正在上传，请稍候...");
                            this.Prodialog.setIndeterminate(false);
                            this.Prodialog.setCanceledOnTouchOutside(false);
                            this.Prodialog.setButton("取消",
                                    new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            // TODO Auto-generated method
                                            // stub
                                            MySetupActivity.Unupdate = false;
                                            HttpConnectionUtil
                                                    .setUnupdata(MySetupActivity.Unupdate);
                                            dialog.dismiss();
                                        }
                                    });
                            this.Prodialog.show();
                            this.connect(MySetupActivity.TEST_REQUEST_CODE);
                        }
                        Bitmap photo = extras.getParcelable("data");
                        photo = BitmapUtil.toRoundBitmap(photo);
                        // Drawable drawable = new BitmapDrawable(photo);
                        this.avatar_iv.setImageBitmap(photo);
                    }
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isSdcardExisting() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    public void resizeImage(Uri uri) {
        if (uri == null) {
            Log.i(MySetupActivity.TAG, "The uri is not exist.");
        } else {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 150);
            intent.putExtra("outputY", 150);
            intent.putExtra("return-data", true);
            this.startActivityForResult(intent, MySetupActivity.RESIZE_REQUEST_CODE);
        }
    }

    //
    public Map<String, String> ModifyEmail() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("act", "modifyEMail");
        params.put("account", this.district.getText().toString());
        params.put("newEMail", this.edit.getText().toString());
        params.put("loginId", this.m_iniFileIO.getIniString(this.userIni, "Login",
                "LoginId", "", (byte) 0));
        return params;
    }

    public Map<String, String> ModifyFace() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("act", "uploadHead");
        params.put("account", this.district.getText().toString());
        return params;
    }

    public Map<String, String> ModifyPwd() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("act", "modifyPassword");
        params.put("account", this.district.getText().toString());
        params.put("oldPassword", this.edit.getText().toString());
        params.put("newPassword", this.edit1.getText().toString());
        params.put("loginId", this.m_iniFileIO.getIniString(this.userIni, "Login",
                "LoginId", "", (byte) 0));
        return params;
    }

    public Map<String, String> LoginQuit() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("act", "logOut");
        params.put("clientId", UIHelper.DeviceMD5ID(this));
        params.put("loginId", this.m_iniFileIO.getIniString(this.userIni, "Login",
                "LoginId", "", (byte) 0));
        return params;
    }

    public Map<String, String> ModifyAccount() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("act", "modifySelfAccount");
        params.put("newAccount", this.edit.getText().toString());
        params.put("loginId", this.m_iniFileIO.getIniString(this.userIni, "Login",
                "LoginId", "", (byte) 0));
        return params;
    }

    public Map<String, String> ModifyUser() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("act", "modifyUser");
        params.put("account", this.district.getText().toString());
        params.put("idiograph", this.signature.getText().toString());
        params.put("province", this.location_province.getText().toString());
        params.put("city", this.location.getText().toString());
        params.put("myURL", this.web.getText().toString());
        params.put("userName", this.nickname_tv.getText().toString());

        params.put("sex", this.gender.getText().toString());
        params.put("loginId", this.m_iniFileIO.getIniString(this.userIni, "Login",
                "LoginId", "", (byte) 0));
        return params;
    }

    public Map<String, String> checkPhone() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("act", "checkAccount");
        params.put("account", this.edit.getText().toString());
        return params;
    }

    private void connect(int count) {
        MySetupActivity.MyAsyncTask task = new MySetupActivity.MyAsyncTask(this, count);
        task.execute();
    }

    // �첽���� ��̨����ǰ ��̨������ ��̨�������
    @SuppressLint("ShowToast")
    class MyAsyncTask extends AsyncTask<String, Integer, String>
    {

        // private static final String TAG = "MyAsyncTask";
        private final Context context;
        private final int action;
        private AnimationDrawable loadingAnima;
        private String retStr;
        private String retEmail;

        public MyAsyncTask(Context c, int action) {
            context = c;
            this.action = action;
        }

        // ������UI�߳��У��ڵ���doInBackground()֮ǰִ��
        @Override
        protected void onPreExecute() {
            switch (this.action) {
                case MySetupActivity.EMAIL_REQUEST_CODE:
                    MySetupActivity.this.progressTag.setVisibility(View.VISIBLE);
                    this.loadingAnima = (AnimationDrawable) MySetupActivity.this.iv.getBackground();
                    this.loadingAnima.start();
                    break;
                case MySetupActivity.PHONE_REQUEST_CODE:
                    MySetupActivity.this.progressTag.setVisibility(View.VISIBLE);
                    this.loadingAnima = (AnimationDrawable) MySetupActivity.this.iv.getBackground();
                    this.loadingAnima.start();
                    break;
                case MySetupActivity.PASSWORD_REQUEST_CODE:
                    MySetupActivity.this.progressTag.setVisibility(View.VISIBLE);
                    this.loadingAnima = (AnimationDrawable) MySetupActivity.this.iv.getBackground();
                    this.loadingAnima.start();
                    break;
                case MySetupActivity.USERNAME_REQUEST_CODE:
                    MySetupActivity.this.progressTag.setVisibility(View.VISIBLE);
                    this.loadingAnima = (AnimationDrawable) MySetupActivity.this.iv.getBackground();
                    this.loadingAnima.start();
                    break;
            }
        }

        // ��̨���еķ������������з�UI�̣߳�����ִ�к�ʱ�ķ���
        @Override
        protected String doInBackground(String... params) {
            MySetupActivity.this.initPath();
            HttpConnectionUtil connection = new HttpConnectionUtil();
            constants.verifyURL = "http://"
                    + MySetupActivity.this.m_iniFileIO.getIniString(MySetupActivity.this.userIni, "Login",
                    "ebsAddress", constants.DefaultServerIp, (byte) 0)
                    + ":"
                    + MySetupActivity.this.m_iniFileIO.getIniString(MySetupActivity.this.userIni, "Login", "ebsPort",
                    "8083", (byte) 0)
                    + MySetupActivity.this.m_iniFileIO.getIniString(MySetupActivity.this.userIni, "Login", "ebsPath",
                    "/EBS/UserServlet", (byte) 0);
            switch (this.action) {
                case MySetupActivity.EMAIL_REQUEST_CODE:
                    return connection.asyncConnect(constants.verifyURL,
                            MySetupActivity.this.ModifyEmail(), HttpConnectionUtil.HttpMethod.POST, this.context);
                case MySetupActivity.PASSWORD_REQUEST_CODE:
                    return connection.asyncConnect(constants.verifyURL,
                            MySetupActivity.this.ModifyPwd(), HttpConnectionUtil.HttpMethod.POST, this.context);
                case MySetupActivity.PHONE_REQUEST_CODE:
                    return connection.asyncConnect(constants.verifyURL,
                            MySetupActivity.this.checkPhone(), HttpConnectionUtil.HttpMethod.POST, this.context);
                case MySetupActivity.QUIT_REQUEST_CODE:
                    return connection.asyncConnect(constants.verifyURL,
                            MySetupActivity.this.LoginQuit(), HttpConnectionUtil.HttpMethod.POST, this.context);
                case MySetupActivity.USERNAME_REQUEST_CODE:
                    return connection.asyncConnect(constants.verifyURL,
                            MySetupActivity.this.ModifyAccount(), HttpConnectionUtil.HttpMethod.POST, this.context);
                case MySetupActivity.AVATAR_REQUEST_CODE:
                    return connection.asyncConnect(constants.verifyURL,
                            MySetupActivity.this.ModifyUser(), HttpConnectionUtil.HttpMethod.POST, this.context);
                case MySetupActivity.TEST_REQUEST_CODE:
                    return connection.asyncConnect(constants.verifyURL,
                            MySetupActivity.this.ModifyFace(), MySetupActivity.this.headPath, this.context);
            }
            return null;
        }

        // ������ui�߳��У���doInBackground()ִ����Ϻ�ִ��
        @Override
        protected void onPostExecute(String result) {
            switch (this.action) {
                case MySetupActivity.QUIT_REQUEST_CODE:
                    MySetupActivity.this.Prodialog.dismiss();
                    if (result.equals("退出成功")) {
                        // 登陆标志位下线
                        MySetupActivity.this.m_iniFileIO.writeIniString(MySetupActivity.this.userIni, "Login", "LoginFlag",
                                "0");
                        MySetupActivity.this.startActivity(new Intent(MySetupActivity.this,
                                SetUpActivity.class));
                        MySetupActivity.this.finish();
                    } else {
                        MySetupActivity.this.Prodialog.dismiss();
                        Toast.makeText(MySetupActivity.this, result,
                                Toast.LENGTH_LONG).show();
                    }
                    break;
                case MySetupActivity.AVATAR_REQUEST_CODE:
                    MySetupActivity.this.Prodialog.dismiss();
                    if (result.equals("修改成功")) {
                        MySetupActivity.this.m_iniFileIO.writeIniString(MySetupActivity.this.userIni, "Login", "Address", MySetupActivity.this.web
                                .getText().toString());
                        MySetupActivity.this.m_iniFileIO.writeIniString(MySetupActivity.this.userIni, "Login", "Sex", MySetupActivity.this.gender
                                .getText().toString());
                        MySetupActivity.this.m_iniFileIO.writeIniString(MySetupActivity.this.userIni, "Login", "NickName",
                                MySetupActivity.this.nickname_tv.getText().toString());
                        MySetupActivity.this.m_iniFileIO.writeIniString(MySetupActivity.this.userIni, "Login", "Signature",
                                MySetupActivity.this.signature.getText().toString());
                        MySetupActivity.this.m_iniFileIO.writeIniString(MySetupActivity.this.userIni, "Login",
                                "Location_city", MySetupActivity.this.location.getText().toString());
                        MySetupActivity.this.m_iniFileIO.writeIniString(MySetupActivity.this.userIni, "Login",
                                "Location_province", MySetupActivity.this.location_province.getText()
                                        .toString());
                        MySetupActivity.this.finish();
                        MySetupActivity.this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
                    } else {
                        Toast.makeText(MySetupActivity.this, result,
                                Toast.LENGTH_LONG).show();
                    }
                    break;
                case MySetupActivity.TEST_REQUEST_CODE:
                    MySetupActivity.this.Prodialog.dismiss();
//				String headPath = webRoot + "Login";
//				FileUtils.deleteDirectory(headPath);
                    Toast.makeText(MySetupActivity.this, result,
                            Toast.LENGTH_LONG).show();
                    break;
                default:
                    if (result != null) {
                        JSONObject json;
                        try {
                            json = new JSONObject(result);
                            this.retStr = json.getString("regMsg");
                            this.retEmail = json.getString("sendMsg");
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        if (this.retStr != null && this.retEmail != null) {
                            if (this.retStr.equals("修改邮箱成功")
                                    && this.retEmail.equals("邮件发送成功")) {
                                try {
                                    Field field = MySetupActivity.this.ModifyDialog.getClass()
                                            .getSuperclass()
                                            .getDeclaredField("mShowing");
                                    field.setAccessible(true);
                                    // 将mShowing变量设为false，表示对话框已关闭
                                    field.set(MySetupActivity.this.ModifyDialog, true);
                                    MySetupActivity.this.ModifyDialog.dismiss();
                                } catch (Exception e) {

                                }
                                Intent intent = new Intent();
                                intent.putExtra("phoneNum", MySetupActivity.this.edit.getText()
                                        .toString());
                                intent.putExtra("action", "点击邮件中的确认链接来完成修改");
                                intent.putExtra("title", "修改邮箱");
                                intent.setClass(this.context, EmailCodeActivity.class);
                                MySetupActivity.this.startActivity(intent);
                            }
                        } else if (result.equals("修改密码成功")) {
                            MySetupActivity.this.m_iniFileIO.writeIniString(MySetupActivity.this.userIni, "Login", "PassWord",
                                    MySetupActivity.this.edit1.getText().toString());
                            try {
                                Field field = MySetupActivity.this.ModifyDialog.getClass()
                                        .getSuperclass()
                                        .getDeclaredField("mShowing");
                                field.setAccessible(true);
                                // 将mShowing变量设为false，表示对话框已关闭
                                field.set(MySetupActivity.this.ModifyDialog, true);
                                MySetupActivity.this.ModifyDialog.dismiss();
                            } catch (Exception e) {

                            }
                            Toast.makeText(MySetupActivity.this, result,
                                    Toast.LENGTH_SHORT).show();
                        } else if (result.equals("修改账号成功")) {
                            MySetupActivity.this.m_iniFileIO.writeIniString(MySetupActivity.this.userIni, "Login", "Account",
                                    MySetupActivity.this.edit.getText().toString());
                            MySetupActivity.this.m_iniFileIO.writeIniString(MySetupActivity.this.userIni, "Login",
                                    "AccountFlag", "1");
                            MySetupActivity.this.district.setText(MySetupActivity.this.edit.getText().toString());
                            try {
                                Field field = MySetupActivity.this.ModifyDialog.getClass()
                                        .getSuperclass()
                                        .getDeclaredField("mShowing");
                                field.setAccessible(true);
                                // 将mShowing变量设为false，表示对话框已关闭
                                field.set(MySetupActivity.this.ModifyDialog, true);
                                MySetupActivity.this.ModifyDialog.dismiss();
                            } catch (Exception e) {

                            }
                            Toast.makeText(MySetupActivity.this, result,
                                    Toast.LENGTH_SHORT).show();
                        } else if (result.equals("false")) {
                            UIHelper.setSharePerference(
                                    MySetupActivity.this,
                                    constants.SAVE_LOCALMSGNUM, "username",
                                    MySetupActivity.this.district.getText().toString());
                            UIHelper.setSharePerference(
                                    MySetupActivity.this,
                                    constants.SAVE_LOCALMSGNUM, "loginId",
                                    MySetupActivity.this.m_iniFileIO.getIniString(MySetupActivity.this.userIni, "Login",
                                            "LoginId", "", (byte) 0));
                            try {
                                Field field = MySetupActivity.this.ModifyDialog.getClass()
                                        .getSuperclass()
                                        .getDeclaredField("mShowing");
                                field.setAccessible(true);
                                // 将mShowing变量设为false，表示对话框已关闭
                                field.set(MySetupActivity.this.ModifyDialog, true);
                                MySetupActivity.this.ModifyDialog.dismiss();
                            } catch (Exception e) {

                            }
                            Intent intent = new Intent();
                            intent.putExtra("phoneNum", MySetupActivity.this.edit.getText().toString());
                            intent.putExtra("title", "绑定手机");
                            intent.setClass(MySetupActivity.this,
                                    SmscodeActivity.class);
                            MySetupActivity.this.startActivity(intent);
                        } else if (result.equals("true")) {
                            MySetupActivity.this.progressTag.setVisibility(View.GONE);
                            MySetupActivity.this.dynamicTag.setVisibility(View.VISIBLE);
                            MySetupActivity.this.dynamicTxt.setText("手机号码已注册");
                        } else {
                            MySetupActivity.this.progressTag.setVisibility(View.GONE);
                            MySetupActivity.this.dynamicTag.setVisibility(View.VISIBLE);
                            if (StringUtils.isEmpty(this.retStr)) {
                                MySetupActivity.this.dynamicTxt.setText(result);
                            } else {
                                MySetupActivity.this.dynamicTxt.setText(this.retStr);
                            }
                        }
                    } else {
                        MySetupActivity.this.progressTag.setVisibility(View.GONE);
                        MySetupActivity.this.dynamicTag.setVisibility(View.VISIBLE);
                        MySetupActivity.this.dynamicTxt.setText(this.context
                                .getString(R.string.sapi_login_error));
                    }

            }
        }

        // ��publishProgress()�������Ժ�ִ�У�publishProgress()���ڸ��½��
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    public void dismissDialog() {
        this.dialog.cancel();
    }
}