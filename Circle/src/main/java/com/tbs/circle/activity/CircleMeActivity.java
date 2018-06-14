package com.tbs.circle.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.Utils;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tbs.circle.R;
import com.tbs.circle.adapter.CircleMeAdapter;
import com.tbs.circle.bean.CircleItem;
import com.tbs.circle.utils.DatasUtil;
import com.tbs.circle.utils.DateUtils;
import com.tbs.circle.utils.GlideImageLoader;
import com.tbs.circle.widgets.DivItemDecoration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by TBS on 2017/10/10.
 */

public class CircleMeActivity extends YWActivity
{
    private static final String isFriend = "0";//默认经过好友关系查询
    private static final String isFold = "1";//默认返回
    private static final String category = "0";//默认没有经纬度0
    private String currentTime;

    private PopupWindow pop;
    private LinearLayout ll_popup;

    private int screenHeight;
    private int editTextBodyHeight;
    private int currentKeyboardH;

    private boolean isInitCache = false;
    private SuperRecyclerView recyclerView;
    private RelativeLayout bodyLayout;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout.OnRefreshListener refreshListener;
    /**
     * 页数索引
     **/
    public int index = 1;
    private CircleMeAdapter circleAdapter;
    public final static int REQUEST_TAKE_PHOTO = 1001;
    private View topBar;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_moments_me);

        //因为状态栏透明后，布局整体会上移，所以给头部加上状态栏的margin值，保证头部不会被覆盖
        topBar = findViewById(R.id.title);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) topBar.getLayoutParams();
            params.topMargin = Utils.getStatusHeight(this);
            topBar.setLayoutParams(params);
        }

        TextView tvTitle = (TextView) this.findViewById(R.id.tv_title);
        ImageView ivNotice = (ImageView) this.findViewById(R.id.iv_notice);
        ImageView iv_back = (ImageView) this.findViewById(R.id.iv_back);
        tvTitle.setText("我的相册");
        initView();
        //实现自动下拉刷新功能
        recyclerView.getSwipeToRefresh().post(new Runnable()
        {
            @Override
            public void run() {
                recyclerView.setRefreshing(true);//执行下拉刷新的动画
                refreshListener.onRefresh();//执行数据加载操作
            }
        });
        ivNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_popup.startAnimation(AnimationUtils.loadAnimation(CircleMeActivity.this, R.anim
                        .activity_translate_in));
                pop.showAtLocation(v, Gravity.BOTTOM, 0, 0);
            }

        });
        iv_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }

        });
        initPop();
    }

    private void initView() {
        recyclerView = (SuperRecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DivItemDecoration(2, true));
        recyclerView.getMoreProgressView().getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        circleAdapter = new CircleMeAdapter(this);
        recyclerView.setAdapter(circleAdapter);
        recyclerView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
            }
        });
        refreshListener = new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run() {
                        index = 1;
                        getData(index);
                    }
                }, 10);
            }
        };
        recyclerView.setRefreshListener(refreshListener);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Glide.with(CircleMeActivity.this).resumeRequests();
                } else {
                    Glide.with(CircleMeActivity.this).pauseRequests();
                }
            }
        });
    }

    public void update2loadData(int loadType, List<CircleItem> datas) {
        if (datas.size() < 20 && datas.size() > 0) {
            recyclerView.removeMoreListener();
            recyclerView.hideMoreProgress();
            CircleItem item = new CircleItem();
            item.setType("N");
            datas.add(item);
        } else {
            recyclerView.setupMoreListener(new OnMoreListener()
            {
                @Override
                public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run() {
                            getData(index++);
                        }
                    }, 10);
                }
            }, 1);
        }
        if (loadType == 1) {
            recyclerView.setRefreshing(false);
            circleAdapter.setDatas(datas);
        } else {
            circleAdapter.getDatas().addAll(datas);
        }
        circleAdapter.notifyDataSetChanged();
    }
    public void initPop() {

        pop = new PopupWindow(this);

        View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);

        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);

        pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);

        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        RelativeLayout bt1 = (RelativeLayout) view
                .findViewById(R.id.item_popupwindows_camera);
        Button bt2 = (Button) view
                .findViewById(R.id.item_popupwindows_Photo);
        Button bt3 = (Button) view
                .findViewById(R.id.item_popupwindows_cancel);
        parent.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt1.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                Intent intent = new Intent(CircleMeActivity.this, TakePhotoActivity.class);
                startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt2.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                ImagePicker imagePicker = ImagePicker.getInstance();
                imagePicker.setImageLoader(new GlideImageLoader());
                imagePicker.setShowCamera(false);
                imagePicker.setSelectLimit(9);
                imagePicker.setCrop(false);
                Intent intent = new Intent(getApplicationContext(), ImageGridActivity.class);
                startActivityForResult(intent, 100);
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt3.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });


    }
    private void getData(final int pageIndex) {
        Map params = new HashMap();
        params.put("userId", DatasUtil.getUserMsg(this).getId());
        params.put("isFriend", isFriend);
        params.put("category", category);
        params.put("currentPage", pageIndex + "");
        params.put("pageSize", 20 + "");
        params.put("behot_time", getCurrentTime());
        params.put("isFold", isFold);
        OkGo.<String>get(DatasUtil.URL_SOCIAL_FRIEND)//
                .tag(this)
                .params(params, false)
                .cacheKey("TabFragment_" + pageIndex)       //由于该fragment会被复用,必须保证key唯一,否则数据会发生覆盖
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
                            int code = jsonObject.getInt("code");
                            switch (code) {
                                case 1:
                                    currentTime = jsonObject.getString("behot_time");
                                    DatasUtil.setSharePerference(getBaseContext(), DatasUtil.SAVE_INFORMATION,
                                            "behot_time_me", currentTime);
                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                    List<CircleItem> datas = DatasUtil.createCircleDatas(jsonArray);
                                    update2loadData(pageIndex, datas);

                                    break;
                                case -1:
                                    update2loadData(pageIndex, new ArrayList<CircleItem>());
                                    Toast.makeText(getBaseContext(), "暂无数据", Toast.LENGTH_LONG).show();
                                    break;
                            }
                        } catch (Exception e) {
                            update2loadData(pageIndex, new ArrayList<CircleItem>());
                            Toast.makeText(getBaseContext(), "请求失败 原因:" + response.body().toString(), Toast
                                    .LENGTH_LONG).show();
                            e.printStackTrace();
                        } finally {
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        update2loadData(pageIndex, new ArrayList<CircleItem>());
                        Toast.makeText(getBaseContext(), "请求失败,请稍候重试", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public String getCurrentTime() {
        String time = DatasUtil.getShareperference(getBaseContext(), DatasUtil.SAVE_INFORMATION, "behot_time_me", "");
        if (TextUtils.isEmpty(time)) {
            currentTime = DateUtils.getStringTime(System.currentTimeMillis());
        } else {
            currentTime = time;
        }
        return currentTime;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 100) {
                //noinspection unchecked
                List<ImageItem> images = (List<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                ImgDynamicActivity.startPostActivity(CircleMeActivity.this,
                        images);
            }
        } else if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                String path = data.getStringExtra("path");
                if (data.getBooleanExtra("take_photo", true)) {
                    //照片
                    ImageItem imageItem = new ImageItem();
                    imageItem.path = path;
                    List<ImageItem> images = new ArrayList<ImageItem>();
                    images.add(imageItem);
                    ImgDynamicActivity.startPostActivity(CircleMeActivity.this,
                            images);
                } else {
                    //小视频
                    //Toast.makeText(ScrollingActivity.this, "小视频", Toast.LENGTH_LONG).show();
                    VideoDynamicActivity.startPostActivity(CircleMeActivity.this,
                            path);
                }
            }
        }
    }

}


