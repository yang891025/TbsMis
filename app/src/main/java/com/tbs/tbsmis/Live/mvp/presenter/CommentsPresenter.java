package com.tbs.tbsmis.Live.mvp.presenter;

import android.view.View;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tbs.circle.bean.CommentConfig;
import com.tbs.circle.bean.CommentItem;
import com.tbs.circle.bean.FavortItem;
import com.tbs.tbsmis.Live.bean.CommentAll;
import com.tbs.tbsmis.Live.mvp.contract.CommentContract;
import com.tbs.tbsmis.Live.utils.DataUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by TBS on 2018/1/11.
 */

public class CommentsPresenter implements CommentContract.Presenter
{
    private CommentContract.View view;
    private boolean isInitCache = false;
    public CommentsPresenter(CommentContract.View view) {
        this.view = view;
    }

    @Override
    public void getComments(Map map) {
        OkGo.<String>get(DataUtils.URL_ALL_COMMENTS)//
                .tag(this)
                .params(map, false)
                .cacheKey("commentFragment_")       //由于该fragment会被复用,必须保证key唯一,否则数据会发生覆盖
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
                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                    List<CommentAll> datas = DataUtils.createCommentDatas(jsonArray);
                                    if (view != null) {
                                        view.getCommentsCallback(datas);
                                    }
                            }else{
                                view.showError("暂无数据");
                                //Toast.makeText(view.getBaseContext(), "暂无数据", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            view.showError("请求失败 原因:数据异常");
//                            Toast.makeText(view.getBaseContext(), "请求失败 原因:数据异常", Toast
//                                    .LENGTH_LONG).show();
                            e.printStackTrace();
                        } finally {
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        view.showError("请求失败,请稍候重试");
                        //Toast.makeText(view.getBaseContext(), "请求失败,请稍候重试", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void addComment(final Map map, final CommentConfig config) {
        OkGo.<String>get(DataUtils.URL_ADD_COMMENTS)//
                .tag(this)
                .params(map, false)
                .execute(new StringCallback()
                {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());// 转换为JSONObject
                            boolean code = jsonObject.getBoolean("result");
                            if (code) {
                                String cId = jsonObject.getString("id");
                                CommentItem newItem = null;
                                String nick = jsonObject.getString("issueName");
                                String cid = jsonObject.getString("issueUser");
                                map.put("issueName",nick);
                                map.put("issueUser",cid);
                                map.put("parentName",config.replyName);
                                newItem = DataUtils.createReplyComment(map,cId,view.getBaseContext());
                                if (view != null) {
                                    view.addCommentCallback(config.circlePosition, newItem);
                                }
                            }else{
                                view.showError("暂无数据");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            view.showError("请求失败 原因:数据异常");

                        } finally {
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        view.showError("请求失败,请稍候重试");
                    }
                });
    }

    @Override
    public void deleteComment(Map map) {

    }

    @Override
    public void getPraises(Map map, final List<CommentAll> Commentdatas) {
        OkGo.<String>get(DataUtils.URL_ALL_PRAISES)//
                .tag(this)
                .params(map, false)
                .cacheKey("Praise_Fragment")       //由于该fragment会被复用,必须保证key唯一,否则数据会发生覆盖
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
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                List<FavortItem> datas = DataUtils.createPraiseDatas(jsonArray);
                                if (view != null) {
                                    view.getPraisesCallback(datas,Commentdatas);
                                }
                            }else{
                                view.showError("暂无数据");
                                //Toast.makeText(view.getBaseContext(), "暂无数据", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            view.showError("请求失败 原因:数据异常");
//                            Toast.makeText(view.getBaseContext(), "请求失败 原因:数据异常", Toast
//                                    .LENGTH_LONG).show();
                            e.printStackTrace();
                        } finally {
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        view.showError("请求失败,请稍候重试");
                        //Toast.makeText(view.getBaseContext(), "请求失败,请稍候重试", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void addPraises(final Map map, final int position) {
        OkGo.<String>get(DataUtils.URL_ADD_PRAISES)//
                .tag(this)
                .params(map, false)
                .execute(new StringCallback()
                {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());// 转换为JSONObject
                            boolean code = jsonObject.getBoolean("result");
                            if (code) {
                                if (view != null) {
                                    String cId = jsonObject.getString("id");
                                    map.put("id",cId);
                                    view.addPraisesCallback(map,position);
                                }
                            }else{
                                view.showError("暂无数据");
                                //Toast.makeText(view.getBaseContext(), "暂无数据", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            view.showError("请求失败 原因:数据异常");
//                            Toast.makeText(view.getBaseContext(), "请求失败 原因:数据异常", Toast
//                                    .LENGTH_LONG).show();
                            e.printStackTrace();
                        } finally {
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        view.showError("请求失败,请稍候重试");
                        //Toast.makeText(view.getBaseContext(), "请求失败,请稍候重试", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void cancelPraises(final Map map, final int position, final int commentPosition, final int PraisesType) {
        OkGo.<String>get(DataUtils.URL_CANCEL_PRAISES)//
                .tag(this)
                .params(map, false)
                .execute(new StringCallback()
                {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());// 转换为JSONObject
                            boolean code = jsonObject.getBoolean("result");
                            if (code) {
                                if (view != null) {
                                    view.cancelPraisesCallback(position,commentPosition,PraisesType);
                                }
                            }else{
                                view.showError("暂无数据");
                                //Toast.makeText(view.getBaseContext(), "暂无数据", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            view.showError("请求失败 原因:数据异常");
//                            Toast.makeText(view.getBaseContext(), "请求失败 原因:数据异常", Toast
//                                    .LENGTH_LONG).show();
                            e.printStackTrace();
                        } finally {
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        view.showError("请求失败,请稍候重试");
                        //Toast.makeText(view.getBaseContext(), "请求失败,请稍候重试", Toast.LENGTH_LONG).show();
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
}
