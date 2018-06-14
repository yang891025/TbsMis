package com.tbs.circle.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cjt2325.cameralibrary.JCameraView;
import com.cjt2325.cameralibrary.listener.ErrorListener;
import com.cjt2325.cameralibrary.listener.JCameraListener;
import com.lzy.imagepicker.Utils;
import com.tbs.circle.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * @创建者 CSDN_LQR
 * @描述 拍照界面
 */
public class TakePhotoActivity extends YWActivity
{


    private JCameraView mJCameraView;
    public static final String ROOT_DIR = "Android/data/";
    private View topBar;

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        //因为状态栏透明后，布局整体会上移，所以给头部加上状态栏的margin值，保证头部不会被覆盖
        topBar = findViewById(R.id.jcameraview);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) topBar.getLayoutParams();
            params.topMargin = Utils.getStatusHeight(this);
            topBar.setLayoutParams(params);
        }
        initView();
    }
    public void initView() {
        mJCameraView = (JCameraView) findViewById(R.id.jcameraview);
        //(0.0.7+)设置视频保存路径（如果不设置默认为Environment.getExternalStorageDirectory().getPath()）
        mJCameraView.setSaveVideoPath(Environment.getExternalStorageDirectory().getPath());
        //(0.0.8+)设置手动/自动对焦，默认为自动对焦
        //mJCameraView.setAutoFoucs(false);
        //设置小视频保存路径
        File file = new File(getDir("video"));
        if (!file.exists())
            file.mkdirs();
        mJCameraView.setSaveVideoPath(getDir("video"));
        //设置视频质量
        mJCameraView.setMediaQuality(JCameraView.MEDIA_QUALITY_MIDDLE);
        //JCameraView监听
        mJCameraView.setErrorLisenter(new ErrorListener() {
            @Override
            public void onError() {
                //打开Camera失败回调
                Toast.makeText(TakePhotoActivity.this,"打开Camera失败", Toast.LENGTH_LONG).show();
            }
            @Override
            public void AudioPermissionError() {
                //没有录取权限回调
                Toast.makeText(TakePhotoActivity.this,"没有视频录取权限", Toast.LENGTH_LONG).show();
            }
        });
        mJCameraView.setJCameraLisenter(new JCameraListener()
        {

            @Override
            public void captureSuccess(Bitmap bitmap) {
                //获取到拍照成功后返回的Bitmap
                String path = saveBitmap(bitmap, getDir("photo"));
                Intent data = new Intent();
                data.putExtra("take_photo", true);
                data.putExtra("path", path);
                setResult(RESULT_OK, data);
                finish();
            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame) {
                //获取成功录像后的视频路径
                Intent data = new Intent();
                data.putExtra("take_photo", false);
                data.putExtra("path", url);
                setResult(RESULT_OK, data);
                finish();
            }

        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mJCameraView != null)
            mJCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mJCameraView != null)
            mJCameraView.onPause();
    }

    public String saveBitmap(Bitmap bm, String dir) {
        String path = "";
        File f = new File(dir, "CSDN_LQR_" + SystemClock.currentThreadTimeMillis() + ".png");
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            path = f.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }


    /**
     * 获取应用目录，当SD卡存在时，获取SD卡上的目录，当SD卡不存在时，获取应用的cache目录
     */
    public String getDir(String name) {
        StringBuilder sb = new StringBuilder();
        if (isSDCardAvailable()) {
            sb.append(getExternalStoragePath());
        } else {
            sb.append(getCachePath());
        }
        sb.append(name);
        sb.append(File.separator);
        String path = sb.toString();
        if (createDirs(path)) {
            return path;
        } else {
            return null;

        }
    }

    /**
     * 获取SD下的应用目录
     */
    public String getExternalStoragePath() {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb.append(File.separator);
        sb.append(ROOT_DIR+ getApplicationContext().getPackageName());
        sb.append(File.separator);
        return sb.toString();
    }

    /**
     * 获取应用的cache目录
     */
    public String getCachePath() {
        File f = getApplicationContext().getCacheDir();
        if (null == f) {
            return null;
        } else {
            return f.getAbsolutePath() + "/";
        }
    }

    /**
     * 创建文件夹
     */
    public static boolean createDirs(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists() || !file.isDirectory()) {
            return file.mkdirs();
        }
        return true;
    }

    /**
     * 判断SD卡是否挂载
     */
    public static boolean isSDCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }
}
