package com.tbs.circle.mvp.presenter;

import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tbs.circle.bean.CircleItem;
import com.tbs.circle.bean.CommentConfig;
import com.tbs.circle.bean.CommentItem;
import com.tbs.circle.bean.FavortItem;
import com.tbs.circle.mvp.contract.CircleContract;
import com.tbs.circle.utils.DatasUtil;
import com.tbs.circle.utils.DateUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yiw
 * @ClassName: CirclePresenter
 * @Description: 通知model请求服务器和通知view更新
 * @date 2015-12-28 下午4:06:03
 */
public class CirclePresenter implements CircleContract.Presenter
{
    private CircleContract.View view;
    private static final String isFriend = "0";//默认经过好友关系查询
    private static final String isFold = "1";//默认返回
    private static final String category = "0";//默认没有经纬度0
    private boolean isInitCache = false;

    public CirclePresenter(CircleContract.View view) {
        this.view = view;
    }

    private String currentTime;

    public void loadData(final int loadType) {
        Map params = new HashMap();
        params.put("userId", DatasUtil.getUserMsg(view.getBaseContext()).getId());
        params.put("isFriend", isFriend);
        params.put("category", category);
        params.put("currentPage", loadType + "");
        params.put("pageSize", 20 + "");
        params.put("behot_time", getCurrentTime());
        params.put("isFold", isFold);
        OkGo.<String>get(DatasUtil.URL_SOCIAL)//
                .tag(this)
                .params(params, false)
                .cacheKey("TabFragment_" + loadType)       //由于该fragment会被复用,必须保证key唯一,否则数据会发生覆盖
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)  //缓存模式先使用缓存,然后使用网络数据
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
                            int code = jsonObject.getInt("code");
                            switch (code) {
                                case 1:
                                    currentTime = jsonObject.getString("behot_time");
                                    DatasUtil.setSharePerference(view.getBaseContext(), DatasUtil.SAVE_INFORMATION,
                                            "behot_time", currentTime);
                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                    List<CircleItem> datas = DatasUtil.createCircleDatas(jsonArray);
                                    if (view != null) {
                                        view.update2loadData(loadType, datas);
                                    }
                                    break;
                                case -1:
                                    view.update2loadData(loadType, new ArrayList<CircleItem>());
                                    Toast.makeText(view.getBaseContext(), "暂无数据", Toast.LENGTH_LONG).show();
                                    break;
                            }
                        } catch (Exception e) {
                            view.update2loadData(loadType, new ArrayList<CircleItem>());
                            Toast.makeText(view.getBaseContext(), "请求失败 原因:" + response.body().toString(), Toast
                                    .LENGTH_LONG).show();
                            e.printStackTrace();
                        } finally {
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        view.update2loadData(loadType, new ArrayList<CircleItem>());
                        Toast.makeText(view.getBaseContext(), "请求失败,请稍候重试", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public String getCurrentTime() {
        String time = DatasUtil.getShareperference(view.getBaseContext(), DatasUtil.SAVE_INFORMATION, "behot_time", "");
        if (TextUtils.isEmpty(time)) {
            currentTime = DateUtils.getStringTime(System.currentTimeMillis());
        } else {
            currentTime = time;
        }
        return currentTime;
    }

    /**
     * @param circleId
     * @return void    返回类型
     * @throws
     * @Title: deleteCircle
     * @Description: 删除动态
     */
    public void deleteCircle(final String circleId) {
        Map params = new HashMap();
        params.put("cId", circleId);
        OkGo.<String>get(DatasUtil.URL_SOCIAL_DELETE)//
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
                                        view.update2DeleteCircle(circleId);
                                    }
                                    break;
                            }
                        } catch (Exception e) {
                            Toast.makeText(view.getBaseContext(), "请求失败 原因:" + response.body().toString(), Toast
                                    .LENGTH_LONG).show();
                            e.printStackTrace();
                        } finally {
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Toast.makeText(view.getBaseContext(), "请求失败,请稍候重试", Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * @param circlePosition
     * @return void    返回类型
     * @throws
     * @Title: addFavort
     * @Description: 点赞
     */
    public void addFavort(final int circlePosition, String cId) {
        Map params = new HashMap();
        params.put("cId", cId);
        params.put("userId", DatasUtil.getUserMsg(view.getBaseContext()).getId());
        OkGo.<String>get(DatasUtil.URL_SOCIAL_GOOD)//
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
                                    int cId = jsonObject.getInt("id");
                                    FavortItem item = DatasUtil.createCurUserFavortItem(cId,view.getBaseContext());
                                    if (view != null) {
                                        view.update2AddFavorite(circlePosition, item);
                                    }
                                    break;
                            }
                        } catch (Exception e) {
                            Toast.makeText(view.getBaseContext(), "请求失败 原因:" + response.body().toString(), Toast
                                    .LENGTH_LONG).show();
                            e.printStackTrace();
                        } finally {
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Toast.makeText(view.getBaseContext(), "请求失败,请稍候重试", Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * @param @param circlePosition
     * @param @param favortId
     * @return void    返回类型
     * @throws
     * @Title: deleteFavort
     * @Description: 取消点赞
     */
    public void deleteFavort(final int circlePosition, final String favortId) {
        Map params = new HashMap();
        params.put("cId", favortId);
        OkGo.<String>get(DatasUtil.URL_SOCIAL_GOOD_CANCEL)//
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
                                        view.update2DeleteFavort(circlePosition, favortId);
                                    }
                                    break;
                            }
                        } catch (Exception e) {
                            Toast.makeText(view.getBaseContext(), "请求失败 原因:" + response.body().toString(), Toast
                                    .LENGTH_LONG).show();
                            e.printStackTrace();
                        } finally {
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Toast.makeText(view.getBaseContext(), "请求失败,请稍候重试", Toast.LENGTH_LONG).show();
                    }
                });


    }

    /**
     * @param content
     * @param config  CommentConfig
     * @return void    返回类型
     * @throws
     * @Title: addComment
     * @Description: 增加评论
     */
    public void addComment(final String content, final CommentConfig config) {
        if (config == null) {
            return;
        }
        if (config.commentType == CommentConfig.Type.PUBLIC) {
            Map params = new HashMap();
            params.put("cId", config.circleId+"");
            params.put("userId",DatasUtil.getUserMsg(view.getBaseContext()).getId());
            params.put("content", content);
            OkGo.<String>get(DatasUtil.URL_SOCIAL_COMMENT)//
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
                                        int cId = jsonObject.getInt("id");
                                        CommentItem newItem = null;
                                        newItem = DatasUtil.createPublicComment(cId,content,view.getBaseContext());
                                        if (view != null) {
                                            view.update2AddComment(config.circlePosition, newItem);
                                        }
                                        break;
                                }
                            } catch (Exception e) {
                                Toast.makeText(view.getBaseContext(), "请求失败 原因:" + response.body().toString(), Toast
                                        .LENGTH_LONG).show();
                                e.printStackTrace();
                            } finally {
                            }
                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            Toast.makeText(view.getBaseContext(), "请求失败,请稍候重试", Toast.LENGTH_LONG).show();
                        }
                    });
        } else if (config.commentType == CommentConfig.Type.REPLY) {
            Map params = new HashMap();
            params.put("cId", config.circleId+"");
            params.put("userId",DatasUtil.getUserMsg(view.getBaseContext()).getId());
            params.put("replyContent", content);
            params.put("replyUid", config.replyUser);
            OkGo.<String>get(DatasUtil.URL_SOCIAL_REPLY_COMMENT)//
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
                                        int cId = jsonObject.getInt("id");
                                        CommentItem newItem = null;
                                        newItem = DatasUtil.createReplyComment(config.replyName, content, cId,view.getBaseContext());
                                        if (view != null) {
                                            view.update2AddComment(config.circlePosition, newItem);
                                        }
                                        break;
                                }
                            } catch (Exception e) {
                                Toast.makeText(view.getBaseContext(), "请求失败 原因:" + response.body().toString(), Toast
                                        .LENGTH_LONG).show();
                                e.printStackTrace();
                            } finally {
                            }
                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            Toast.makeText(view.getBaseContext(), "请求失败,请稍候重试", Toast.LENGTH_LONG).show();
                        }
                    });
        }

    }

    /**
     * @param @param circlePosition
     * @param @param commentId
     * @return void    返回类型
     * @throws
     * @Title: deleteComment
     * @Description: 删除评论
     */
    public void deleteComment(final int circlePosition, final int commentId) {
        Map params = new HashMap();
        params.put("cId", commentId+"");
        OkGo.<String>get(DatasUtil.URL_SOCIAL_DELETE_COMMENT)//
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
                                        view.update2DeleteComment(circlePosition, commentId);
                                    }
                                    break;
                            }
                        } catch (Exception e) {
                            Toast.makeText(view.getBaseContext(), "请求失败 原因:" + response.body().toString(), Toast
                                    .LENGTH_LONG).show();
                            e.printStackTrace();
                        } finally {
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Toast.makeText(view.getBaseContext(), "请求失败,请稍候重试", Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * @param commentConfig
     */
    public void showEditTextBody(CommentConfig commentConfig) {
        if (view != null) {
            view.updateEditTextBodyVisible(View.VISIBLE, commentConfig);
        }
    }


    /**
     * 清除对外部对象的引用，防止内存泄露。
     */
    public void recycle() {
        this.view = null;
        OkGo.getInstance().cancelTag(this);
    }
}
