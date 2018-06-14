package com.tbs.circle.mvp.presenter;

import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.tbs.circle.R;
import com.tbs.circle.mvp.contract.VideoContract;
import com.tbs.circle.utils.CommonUtils;
import com.tbs.circle.utils.DatasUtil;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by TBS on 2017/9/28.
 */

public class VideoPresenter implements VideoContract.Presenter
{
    private VideoContract.View view;

    private String pathList;
    private String content;

    public VideoPresenter(VideoContract.View view) {
        this.view = view;
    }


    @Override
    public void setPathList(String pathList) {
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
    public String getPath() {
        return pathList;
    }

    @Override
    public void upLoadVideo() {
        String fileName = pathList.substring(pathList.lastIndexOf("/") + 1);
        //拼接参数
        String verifyURL = DatasUtil.URL_PUBLISH_FILE + "?type=video&userName="+DatasUtil.getUserMsg(view.getBaseContext()).getId()+"&fileName=" + fileName;
        ArrayList<File> files = new ArrayList<>();
        files.add(new File(pathList));
        OkGo.<String>post(verifyURL)//
                .tag(this)//
                .addFileParams("file", files)   // 这/这种方式为一个key，对应一个文件
                .execute(new StringCallback()
                {
                    @Override
                    public void onSuccess(Response<String> response) {
                        view.hideLoading();
                        if (response.body().toString().equals("200")) {
                            sendSocial();
                        } else {
                            CommonUtils.showToastShort(view.getBaseContext(), R.string.update_failed);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        view.hideLoading();
                        CommonUtils.showToastShort(view.getBaseContext(), R.string.update_failed);
                    }

                    @Override
                    public void uploadProgress(Progress progress) {
                        super.uploadProgress(progress);
                        view.LoadingProgress((int) (progress.fraction * 100));
                    }
                });
    }

    private void sendSocial() {
        String fileName = pathList.substring(pathList.lastIndexOf("/") + 1);
        Map params = new HashMap();
        params.put("userId", DatasUtil.getUserMsg(view.getBaseContext()).getId());
        params.put("category", "0");
        params.put("coordinate", "");
        params.put("content", content);
        params.put("location", "");
        params.put("type", "3");
        params.put("imagestr", "video/"+fileName);
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
     * 清除对外部对象的引用，防止内存泄露。
     */
    public void recycle() {
        this.view = null;
        OkGo.getInstance().cancelTag(this);
    }
}
