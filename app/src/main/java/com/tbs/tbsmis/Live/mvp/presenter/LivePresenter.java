package com.tbs.tbsmis.Live.mvp.presenter;

import android.text.TextUtils;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tbs.tbsmis.Live.bean.LiveAlllist;
import com.tbs.tbsmis.Live.mvp.contract.LiveContract;
import com.tbs.tbsmis.Live.utils.DataUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yiw
 * @ClassName: LivePresenter
 * @Description: 通知model请求服务器和通知view更新
 * @date 2015-12-28 下午4:06:03
 */
public class LivePresenter implements LiveContract.Presenter
{
    private LiveContract.View view;
    private static final String isFriend = "0";//默认经过好友关系查询
    private static final String isFold = "1";//默认返回
    private static final String category = "0";//默认没有经纬度0
    private boolean isInitCache = false;

    public LivePresenter(LiveContract.View view) {
        this.view = view;
    }

    private String currentTime;

    public void loadData(final int loadType) {
        Map params = new HashMap();
        params.put("category", category);
        params.put("currentPage", loadType + "");
        params.put("pageSize", 20 + "");
        //params.put("behot_time", getCurrentTime());
        OkGo.<String>get(DataUtils.URL_ALL_LIVING)//
                .tag(this)
                .params(params, false)
                .cacheKey("LiveFragment_" + loadType)       //由于该fragment会被复用,必须保证key唯一,否则数据会发生覆盖
                .cacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)  //缓存模式先使用缓存,然后使用网络数据
                .execute(new StringCallback()
                {
                    @Override
                    public void onCacheSuccess(Response<String> response) {
                        super.onCacheSuccess(response);
                        if (!isInitCache) {
                            //一般来说,缓存回调成功和网络回调成功做的事情是一样的,所以这里直接回调onSuccess
                            onSuccess(response);
                            isInitCache = true;
                        }
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());// 转换为JSONObject
                            boolean code = jsonObject.getBoolean("result");
                            if (code) {
                                currentTime = getCurrentTime();
                                DataUtils.setSharePerference(view.getBaseContext(), DataUtils.SAVE_INFORMATION,
                                        "behot_time", currentTime);
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                List<LiveAlllist> datas = DataUtils.createAllLiveDatas(jsonArray);

                                if (view != null) {
                                    view.update2loadData(loadType, datas);
                                }
                            } else {
                                view.update2loadData(loadType, new ArrayList<LiveAlllist>());
                                Toast.makeText(view.getBaseContext(), "暂无数据", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            view.update2loadData(loadType, new ArrayList<LiveAlllist>());
                            Toast.makeText(view.getBaseContext(), "请求失败 原因:" + response.body().toString(), Toast
                                    .LENGTH_LONG).show();
                            e.printStackTrace();
                        } finally {
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        view.update2loadData(loadType, new ArrayList<LiveAlllist>());
                        Toast.makeText(view.getBaseContext(), "请求失败,请稍候重试", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public String getCurrentTime() {
        String time = DataUtils.getShareperference(view.getBaseContext(), DataUtils.SAVE_INFORMATION, "behot_time", "");
        if (TextUtils.isEmpty(time)) {
            currentTime = DataUtils.getStringTime(System.currentTimeMillis());
        } else {
            currentTime = time;
        }
        return currentTime;
    }


    /**
     * 清除对外部对象的引用，防止内存泄露。
     */
    public void recycle() {
        this.view = null;
        OkGo.getInstance().cancelTag(this);
    }
}
