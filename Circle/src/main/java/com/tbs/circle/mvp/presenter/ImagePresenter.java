package com.tbs.circle.mvp.presenter;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.tbs.circle.R;
import com.tbs.circle.mvp.contract.ImageContract;
import com.tbs.circle.utils.CommonUtils;
import com.tbs.circle.utils.DatasUtil;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by TBS on 2017/9/27.
 */

public class ImagePresenter implements ImageContract.Presenter
{

    private List<String> nameList = new ArrayList<>();
    private ImageContract.View view;
    private int temp = 0;//次数

    private List<ImageItem> pathList = new ArrayList<>();
    private String content;

    public ImagePresenter(ImageContract.View view) {
        this.view = view;
    }

    @Override
    public void upLoadImage() {
        if(pathList.size() > 0) {
            final LinkedList<Runnable> taskList = new LinkedList<>();
            final Handler handler = new Handler();
            class Task implements Runnable
            {
                String path;

                Task(String path) {
                    this.path = path;
                }

                @Override
                public void run() {
                    Luban.with(view.getBaseContext())
                            .load(new File(path))                     //传人要压缩的图片
                            .ignoreBy(100)        //设定压缩档次，默认三挡
                            .setCompressListener(new OnCompressListener()
                            { //设置回调
                                @Override
                                public void onStart() {
                                    //  AppManager.I().currentActivity().showDialog("加载中...");
                                }

                                @Override
                                public void onSuccess(final File file) {
                                    uploadImages(file.getAbsolutePath());
                                    if (!taskList.isEmpty()) {
                                        Runnable runnable = taskList.pop();
                                        handler.post(runnable);
                                    } else {
                                        //全部压缩结束
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                }
                            }).launch();    //启动压缩
                }
            }
            //循环遍历原始路径 添加至linklist中
            for (ImageItem path : pathList) {
                taskList.add(new Task(path.path));
            }
            handler.post(taskList.pop());
        }else{
            sendSocial("0");
        }
    }

    @Override
    public void setPathList(List<ImageItem> pathList) {
        this.pathList = pathList;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public List<ImageItem> getPathList() {
        return pathList;
    }

    private void uploadImages(String filePath) {
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        nameList.add("image/"+fileName);
        //拼接参数
        String verifyURL = DatasUtil.URL_PUBLISH_FILE + "?type=image&userName="+DatasUtil.getUserMsg(view.getBaseContext()).getId()+"&fileName=" + fileName;
        ArrayList<File> files = new ArrayList<>();
        files.add(new File(filePath));
        OkGo.<String>post(verifyURL)//
                .tag(this)//
                .addFileParams("file",files)   // 这/这种方式为一个key，对应一个文件
                .execute(new StringCallback()
                {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response.body().toString().equals("200")) {
                            hanler.sendEmptyMessage(0);
                        }else{
                            hanler.sendEmptyMessage(1);
                        }
                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        hanler.sendEmptyMessage(1);
                    }

                    @Override
                    public void uploadProgress(Progress progress) {
                        super.uploadProgress(progress);
                        view.LoadingProgress((int) (progress.fraction * 100));
                    }

                });
    }

    private Handler hanler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    temp++;
                    if (temp == pathList.size()) {
                        sendSocial("2");
                    }
                    break;
                case 1:
                    temp = 0;
                    view.hideLoading();
                    CommonUtils.showToastShort(view.getBaseContext(), R.string.update_failed);
                    break;
            }
        }
    };

    private void sendSocial(String type) {
        Map params = new HashMap();
        params.put("userId", DatasUtil.getUserMsg(view.getBaseContext()).getId());
        params.put("category", "0");
        params.put("coordinate", "");
        params.put("content", content);
        params.put("location", "");
        params.put("type", type);
        String imagePath = "";
        if(nameList.size() > 0) {
            if (nameList.size() != 1 || nameList.size() > 1) {
                for (int i = 1; i < nameList.size() - 1; i++) {
                    imagePath += nameList.get(i) + ",";
                }
                imagePath = nameList.get(0) + "," + imagePath + nameList.get(nameList.size() - 1);
            } else {
                imagePath = nameList.get(0);
            }
        }
        params.put("imagestr", imagePath);
        nameList.clear();
        OkGo.<String>get(DatasUtil.URL_PUBLISH)//
                .tag(this)
                .params(params, false)
                .cacheMode(CacheMode.NO_CACHE)  //无缓冲
                .execute(new StringCallback()
                {

                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());// 转换为JSONObject
                            int code = jsonObject.getInt("code");
                            switch (code) {
                                case 1:
                                    if (view != null) {
                                        view.hideLoading();
                                        view.finish();
                                    }
                                    break;
                            }
                        } catch (Exception e) {
                            view.hideLoading();
                            Toast.makeText(view.getBaseContext(), "请求失败 原因:" + response.body().toString(), Toast
                                    .LENGTH_LONG).show();
                            e.printStackTrace();
                        } finally {
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        view.hideLoading();
                        Toast.makeText(view.getBaseContext(), "请求失败,请稍候重试", Toast.LENGTH_LONG).show();
                    }
                });
    }
    /**
     * 清除对外部对象的引用，反正内存泄露。
     */
    public void recycle() {
        this.view = null;
        OkGo.getInstance().cancelTag(this);
    }
}
