package com.tbs.tbsmis.weixin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.DES;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.HttpConnectionUtil.HttpMethod;
import com.tbs.tbsmis.util.ImageLoader;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by TBS on 2016/4/25.
 */
public class WeiXinTemplateEditActivity extends Activity implements View.OnClickListener
{
    private ImageView template_menu_btn;
    private ImageView template_back_btn;
    private TextView template_title;
    private ImageView template_imageview;
    private EditText template_content;
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESIZE_REQUEST_CODE = 2;
    private static String IMAGE_FILE_NAME = "";
    //private static String IMAGE_FILE_TEMP_NAME = "temp.jpg";
    private static boolean isNew;
    private static boolean isCode;
    private final String[] items = {"选择本地图片", "拍照"};
    protected static final String TAG = "WeiXinTemplateEditActivity";
    private ProgressDialog Prodialog;
    private String content = "";
    private String pic = "";
    private String name = "";
    private String count = "";
    private Button template_button;
    private Button template_show;
    private EditText template_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        MyActivity.getInstance().addActivity(this);
        this.setContentView(R.layout.template_edit);
        this.template_back_btn = (ImageView) this.findViewById(R.id.template_back_btn);
        this.template_menu_btn = (ImageView) this.findViewById(R.id.tempalte_menu_btn);
        this.template_button = (Button) this.findViewById(R.id.template_button);
        this.template_show = (Button) this.findViewById(R.id.template_show);
        this.template_title = (TextView) this.findViewById(R.id.template_title);
        this.template_imageview = (ImageView) this.findViewById(R.id.template_imageview);
        this.template_content = (EditText) this.findViewById(R.id.template_content);
        this.template_name = (EditText) this.findViewById(R.id.template_name);
        this.template_back_btn.setOnClickListener(this);
        this.template_show.setOnClickListener(this);
        this.template_menu_btn.setOnClickListener(this);
        this.template_menu_btn.setVisibility(View.GONE);
        this.template_imageview.setOnClickListener(this);
        this.template_button.setOnClickListener(this);
        if (this.getIntent().getExtras() != null) {
            Intent intent = this.getIntent();
            WeiXinTemplateEditActivity.isNew = intent.getBooleanExtra("isNew", true);
            if (WeiXinTemplateEditActivity.isNew) {
                this.count = intent.getStringExtra("count");
                this.pic = intent.getStringExtra("pic");
                this.template_title.setText("新建模板");
                WeiXinTemplateEditActivity.IMAGE_FILE_NAME = StringUtils.getTime() + ".jpg";
                this.template_show.setText("预览");
                WeiXinTemplateEditActivity.isCode = true;
            } else {
                this.content = intent.getStringExtra("WXhtml");
                this.pic = intent.getStringExtra("pic");
                this.name = intent.getStringExtra("name");
                this.count = intent.getStringExtra("count");
                this.template_name.setText(this.name);
                this.template_title.setText("模板修改");
                WeiXinTemplateEditActivity.IMAGE_FILE_NAME = FileUtils.getFileName(this.pic);
                ImageLoader imageLoader = new ImageLoader(this, R.drawable.clean_category_thumbnails);
                imageLoader.DisplayImage(this.pic, this.template_imageview);
                this.Prodialog = new ProgressDialog(this);
                this.Prodialog.setMessage("正在加载，请稍候...");
                this.Prodialog.setIndeterminate(false);
                this.Prodialog.setCanceledOnTouchOutside(false);
                this.Prodialog.setButton("取消", new OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method
                        dialog.dismiss();
                    }
                });
                this.Prodialog.show();
                new imageThread(this.content, 1).start();
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyActivity.getInstance().finishActivity(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.template_imageview:
                this.showDialog();
                break;
            case R.id.template_back_btn:
                this.finish();
                break;
            case R.id.template_show:
                if (WeiXinTemplateEditActivity.isCode) {
                    if (this.template_content.getText().length() < 0) {
                        Toast.makeText(this, "内容为空", Toast.LENGTH_SHORT).show();
                    } else {
                        WeiXinTemplateEditActivity.isCode = false;
                        this.template_show.setText("代码");
                        this.Prodialog = new ProgressDialog(this);
                        this.Prodialog.setMessage("正在加载，请稍候...");
                        this.Prodialog.setIndeterminate(false);
                        this.Prodialog.setCanceledOnTouchOutside(false);
                        this.Prodialog.setButton("取消", new OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method
                                dialog.dismiss();
                            }
                        });
                        this.Prodialog.show();
                        new imageThread(this.template_content.getText().toString().replaceAll("(\r\n|\r|\n|\n\r)", " ")
                                .replaceAll("\"", "\'").trim(), 1).start();
                    }
                } else {
                    if (this.template_content.getText().length() < 0) {
                        Toast.makeText(this, "内容为空", Toast.LENGTH_SHORT).show();
                    } else {
                        WeiXinTemplateEditActivity.isCode = true;
                        this.template_show.setText("预览");
                        this.template_content.setText(Html.toHtml(this.template_content.getText()).replaceAll("(\r\n|\r|\n|\n\r)",
                                " ")
                                .replaceAll("\"", "\'").trim());
                    }
                }
                break;
            case R.id.template_button:
                if (this.template_content.getText().length() < 0) {
                    Toast.makeText(this, "内容为空", Toast.LENGTH_SHORT).show();
                } else if (this.template_name.getText().length() < 0) {
                    Toast.makeText(this, "模板名称为空", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, String> param = new HashMap<>();
                    if (WeiXinTemplateEditActivity.isCode)
                        param.put("content", this.template_content.getText().toString().replaceAll("(\r\n|\r|\n|\n\r)", " ")
                                .replaceAll("\"", "\'").trim());
                    else
                        param.put("content", Html.toHtml(this.template_content.getText()).replaceAll("(\r\n|\r|\n|\n\r)",
                                " ")
                                .replaceAll("\"", "\'").trim());
                    param.put("count", this.count);
                    param.put("name", DES.encrypt(this.template_name.getText().toString()));
                    if (WeiXinTemplateEditActivity.isNew)
                        param.put("pic", this.pic + WeiXinTemplateEditActivity.IMAGE_FILE_NAME);
                    else
                        param.put("pic", this.pic);
                    WeiXinTemplateEditActivity.UploadTask uploadTask = new WeiXinTemplateEditActivity.UploadTask(2, this, param);
                    uploadTask.execute();
                }
                break;
        }
    }

    private void showDialog() {
        new Builder(this)
                .setTitle("设置概览图")
                .setItems(this.items, new OnClickListener()
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
                                WeiXinTemplateEditActivity.this.startActivityForResult(Intent.createChooser(
                                        intentFromGallery, "选择图片"),
                                        WeiXinTemplateEditActivity.IMAGE_REQUEST_CODE);
                                break;
                            case 1:
                                Intent intentFromCapture = new Intent(
                                        MediaStore.ACTION_IMAGE_CAPTURE);
                                // 判断存储卡是否可以用，可用进行存储
                                if (WeiXinTemplateEditActivity.this.isSdcardExisting()) {
                                    intentFromCapture.putExtra("return-data", false);
                                    intentFromCapture.putExtra("outputFormat", CompressFormat.JPEG.toString());
                                    intentFromCapture.putExtra("noFaceDetection", true);
                                    intentFromCapture.putExtra(
                                            MediaStore.EXTRA_OUTPUT,
                                            Uri.fromFile(new File(Environment
                                                    .getExternalStorageDirectory(),
                                                    WeiXinTemplateEditActivity.IMAGE_FILE_NAME)));

                                }
                                WeiXinTemplateEditActivity.this.startActivityForResult(intentFromCapture,
                                        WeiXinTemplateEditActivity.CAMERA_REQUEST_CODE);
                                break;
                        }
                    }
                })
                .setNegativeButton("取消", new OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public class imageThread extends Thread
    {
        private String html = "";
        private int cate = 1;

        public imageThread(String html, int cate) {
            this.html = html;
            this.cate = cate;
        }

        Message msg = Message.obtain();

        @Override
        public void run() {
            // TODO Auto-generated method stub
            /**
             * 要实现图片的显示需要使用Html.fromHtml的一个重构方法：public static Spanned
             * fromHtml (String source, Html.ImageGetterimageGetter,
             * Html.TagHandler
             * tagHandler)其中Html.ImageGetter是一个接口，我们要实现此接口，在它的getDrawable
             * (String source)方法中返回图片的Drawable对象才可以。
             */
            ImageGetter imageGetter = new ImageGetter()
            {

                @Override
                public Drawable getDrawable(String source) {
                    // TODO Auto-generated method stub
                    //System.out.println(source);
                    URL url;
                    Drawable drawable = null;
                    try {
                        url = new URL(source);
                        drawable = Drawable.createFromStream(
                                url.openStream(), null);
                        drawable.setBounds(0, 0,
                                drawable.getIntrinsicWidth(),
                                drawable.getIntrinsicHeight());
                    } catch (MalformedURLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return drawable;
                }
            };
            CharSequence test = Html.fromHtml(this.html, imageGetter, null);
            this.msg.what = this.cate;
            this.msg.obj = test;
            WeiXinTemplateEditActivity.this.handler.sendMessage(this.msg);
        }

    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if (msg.what == 1) {
                WeiXinTemplateEditActivity.this.Prodialog.dismiss();
                WeiXinTemplateEditActivity.this.template_content.setText((CharSequence) msg.obj);
            } else if (msg.what == 2) {

            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        } else {
            switch (requestCode) {
                case WeiXinTemplateEditActivity.IMAGE_REQUEST_CODE:
                    this.resizeImage(data.getData());
                    break;
                case WeiXinTemplateEditActivity.CAMERA_REQUEST_CODE:
                    if (this.isSdcardExisting()) {
                        File tempFile = new File(
                                Environment.getExternalStorageDirectory(),
                                WeiXinTemplateEditActivity.IMAGE_FILE_NAME);
                        this.resizeImage(Uri.fromFile(tempFile));
                    } else {
                        Toast.makeText(this,
                                "未找到存储卡，无法存储照片！", Toast.LENGTH_LONG).show();
                    }
                    break;
                case WeiXinTemplateEditActivity.RESIZE_REQUEST_CODE:
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            String headPath = Environment
                                    .getExternalStorageDirectory() + "/" +
                                    WeiXinTemplateEditActivity.IMAGE_FILE_NAME;
                            Bitmap photo = null;
                            try {
                                photo = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(Uri.fromFile(new File(headPath))));
                                //mImage.setImageBitmap(bitmap);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            this.template_imageview.setImageBitmap(photo);
                            WeiXinTemplateEditActivity.UploadTask uploadTask = new WeiXinTemplateEditActivity.UploadTask(1, this, headPath);
                            uploadTask.execute();
                        }
                    } else
                        Toast.makeText(this,
                                "无法存储照片！", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    @SuppressLint("LongLogTag")
    public void resizeImage(Uri uri) {
        if (uri == null) {
            Log.i(WeiXinTemplateEditActivity.TAG, "The uri is not exist.");
        } else {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,  Uri.fromFile(new File(Environment
                    .getExternalStorageDirectory(),
                    WeiXinTemplateEditActivity.IMAGE_FILE_NAME)));
            intent.putExtra("outputFormat", CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection", true);
            this.startActivityForResult(intent, WeiXinTemplateEditActivity.RESIZE_REQUEST_CODE);
//            Intent intent = new Intent("com.android.camera.action.CROP");
//            intent.setDataAndType(uri, "image/*");
//            intent.putExtra("crop", "true");
//            intent.putExtra("return-data", true);
//            startActivityForResult(intent, RESIZE_REQUEST_CODE);
        }
    }

    private boolean isSdcardExisting() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    class UploadTask extends AsyncTask<Integer, Integer, String>
    {
        private final Context context;
        private final int count;
        private String filePath;
        private Map<String, String> param;

        public UploadTask(int count, Context context, String Path) {
            this.context = context;
            this.count = count;
            WeiXinTemplateEditActivity.this.Prodialog = new ProgressDialog(context);
            filePath = Path;
        }

        public UploadTask(int count, Context context, Map<String, String> param) {
            this.context = context;
            this.count = count;
            WeiXinTemplateEditActivity.this.Prodialog = new ProgressDialog(context);
            this.param = param;
        }

        @Override
        protected void onPreExecute() {
            if (this.count == 1) {
                WeiXinTemplateEditActivity.this.Prodialog.setMessage("正在上传，请稍候...");

            } else {
                WeiXinTemplateEditActivity.this.Prodialog.setMessage("正在加载，请稍候...");
            }
            WeiXinTemplateEditActivity.this.Prodialog.setIndeterminate(false);
            WeiXinTemplateEditActivity.this.Prodialog.setCanceledOnTouchOutside(false);
            WeiXinTemplateEditActivity.this.Prodialog.setButton("取消", new OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method
                    dialog.dismiss();
                }
            });
            WeiXinTemplateEditActivity.this.Prodialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Integer... integers) {
            HttpConnectionUtil connection = new HttpConnectionUtil();
            IniFile IniFile = new IniFile();
            String webRoot = UIHelper.getShareperference(context,constants.SAVE_INFORMATION,"Path","");
            String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
            String appIniFile = webRoot
                    + IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                    constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
            String userIni = appIniFile;
            if(Integer.parseInt(IniFile.getIniString(userIni, "Login",
                    "LoginType", "0", (byte) 0)) == 1){
                String dataPath = getFilesDir().getParentFile()
                        .getAbsolutePath();
                if (dataPath.endsWith("/") == false) {
                    dataPath = dataPath + "/";
                }
                userIni = dataPath + "TbsApp.ini";
            }
            String verifyURL = IniFile.getIniString(userIni, "WeiXin",
                    "WeTemplate", "http://e.tbs.com.cn/wechatServlet.do", (byte) 0);
            if (this.count == 1) {
                verifyURL = verifyURL + "?action=setTemplate";
                Map<String, String> param = new HashMap<>();

                return connection.asyncConnect(verifyURL,
                        param, this.filePath, this.context);
            } else if (this.count == 2) {
                verifyURL = verifyURL + "?action=updateTemplate";
                return connection.asyncConnect(verifyURL, this.param, HttpMethod.GET,
                        this.context);
            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                WeiXinTemplateEditActivity.this.Prodialog.dismiss();
                Toast.makeText(this.context, "请检查网络设置", Toast.LENGTH_SHORT).show();
            } else {
                if (this.count == 1) {
                    WeiXinTemplateEditActivity.this.Prodialog.dismiss();
                    if ("true".equalsIgnoreCase(result)) {
                        Toast.makeText(this.context, "上传成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this.context, "上传失败", Toast.LENGTH_SHORT).show();
                    }
                    FileUtils.deleteFile(this.filePath);
                } else if (this.count == 2) {
                    WeiXinTemplateEditActivity.this.Prodialog.dismiss();
                    if ("true".equalsIgnoreCase(result)) {
                        Toast.makeText(this.context, "保存成功", Toast.LENGTH_SHORT).show();
                        WeiXinTemplateEditActivity.this.finish();
                    } else {
                        Toast.makeText(this.context, "保存失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            super.onPostExecute(result);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.KEYCODE_BACK) {
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        super.finish();
        ImageLoader imageLoader = new ImageLoader(this, R.drawable.clean_category_thumbnails);
        imageLoader.clearCache();
    }
}
