package com.tbs.tbsmis.Live.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tbs.circle.bean.CommentConfig;
import com.tbs.circle.bean.CommentItem;
import com.tbs.circle.bean.FavortItem;
import com.tbs.circle.bean.User;
import com.tbs.circle.utils.CommonUtils;
import com.tbs.circle.widgets.CommentListView;
import com.tbs.circle.widgets.DivItemDecoration;
import com.tbs.ini.IniFile;
import com.tbs.tbsmis.Live.adapter.CommentAdapter;
import com.tbs.tbsmis.Live.bean.CommentAll;
import com.tbs.tbsmis.Live.mvp.contract.CommentContract;
import com.tbs.tbsmis.Live.mvp.presenter.CommentsPresenter;
import com.tbs.tbsmis.Live.utils.DataUtils;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by ELVIS on 2015/10/25.
 */
public class showComments extends Fragment implements CommentContract.View
{
    private String mId;
    private AnimationDrawable loadingAnima;
    private RelativeLayout loadingIV;
    private ImageView iv;
    private CommentsPresenter presenter;
    private SuperRecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout.OnRefreshListener refreshListener;
    private LinearLayout edittextbody;
    private EditText editText;
    private ImageView sendIv;
    private RelativeLayout bodyLayout;
    private int screenHeight;
    private int editTextBodyHeight;
    private int currentKeyboardH;
    private int selectCircleItemH;
    private int selectCommentItemOffset;
    private CommentConfig commentConfig;
    private CommentAdapter commentAdapter;
    private IniFile iniFile;
    private String userIni;
    private TextView commentText;
    private String mType;

    public static showComments newInstance(String id, String type) {
        showComments fragment = new showComments();
        Bundle bundle = new Bundle();
        bundle.putString("Id", id);
        bundle.putString("Type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        if (args != null) {
            mId = args.getString("Id");
            mType = args.getString("Type");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        //inflater.inflate(R.layout.fragment_pot, container, false);
        this.initViews(view);
        return view;
    }

    private void initViews(View view) {
        iv = (ImageView) view.findViewById(R.id.gifview);
        loadingIV = (RelativeLayout) view.findViewById(R.id.loading_dialog);
        recyclerView = (SuperRecyclerView) view.findViewById(com.tbs.circle.R.id.recyclerView);
        bodyLayout = (RelativeLayout) view.findViewById(R.id.bodyLayout);
        layoutManager = new LinearLayoutManager(this.getBaseContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DivItemDecoration(2, true));
        recyclerView.getMoreProgressView().getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        recyclerView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (edittextbody.getVisibility() == View.VISIBLE) {
                    updateEditTextBodyVisible(View.GONE, null);
                    return true;
                }
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
                        Map map = new HashMap();
                        map.put("resourceId", mId);
                        map.put("resourceType", mType);
                        map.put("account", iniFile.getIniString(userIni, "Login",
                                "Account", "", (byte) 0));
                        presenter.getComments(map);
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
                    Glide.with(showComments.this).resumeRequests();
                } else {
                    Glide.with(showComments.this).pauseRequests();
                }

            }
        });
        edittextbody = (LinearLayout) view.findViewById(com.tbs.circle.R.id.editTextBodyLl);
        editText = (EditText) view.findViewById(com.tbs.circle.R.id.circleEt);
        sendIv = (ImageView) view.findViewById(com.tbs.circle.R.id.sendIv);
        sendIv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (presenter != null) {
                    //发布评论
                    String content = editText.getText().toString().trim();
                    if (TextUtils.isEmpty(content)) {
                        Toast.makeText(showComments.this.getBaseContext(), "评论内容不能为空...", Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    Map map = new HashMap();
                    map.put("content", content);
                    map.put("resourceId", mId);
                    map.put("resourceType", mType);
                    map.put("account", iniFile.getIniString(userIni, "Login",
                            "Account", "", (byte) 0));
                    map.put("parentId", commentConfig.circleId);
                    if (commentConfig.circlePosition == -1)
                        map.put("path", commentConfig.path);
                    else
                        map.put("path", commentConfig.path + "," + commentConfig.circleId);
                    presenter.addComment(map, commentConfig);
                }
                updateEditTextBodyVisible(View.GONE, null);
            }
        });
        commentText = (TextView) view.findViewById(R.id.commentText);
        commentText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                CommentConfig config = new CommentConfig();
                config.circlePosition = -1;
                config.circleId = "0";
                config.path = "0";
                config.commentType = CommentConfig.Type.PUBLIC;
                //presenter.showEditTextBody(config);
                updateEditTextBodyVisible(View.VISIBLE, config);
            }
        });
        setViewTreeObserver();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        intPath();
        presenter = new CommentsPresenter(this);
        commentAdapter = new CommentAdapter(this.getContext());
        commentAdapter.setCommentPresenter(presenter);
        recyclerView.setAdapter(commentAdapter);

        //实现自动下拉刷新功能
        recyclerView.getSwipeToRefresh().post(new Runnable()
        {
            @Override
            public void run() {
                recyclerView.setRefreshing(true);//执行下拉刷新的动画
                refreshListener.onRefresh();//执行数据加载操作
            }
        });
    }

    public void intPath() {
        iniFile = new IniFile();
        String webRoot = UIHelper.getStoragePath(this.getContext());
        webRoot += this.getContext().getString(R.string.SD_CARD_TBSAPP_PATH2);
        webRoot = UIHelper.getShareperference(this.getContext(),
                constants.SAVE_INFORMATION, "Path", webRoot);
        if (!webRoot.endsWith("/")) {
            webRoot += "/";
        }
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String appNewsFile = webRoot
                + iniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        userIni = appNewsFile;
        if (Integer.parseInt(iniFile.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = this.getContext().getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        String ipUrl = iniFile.getIniString(userIni, "Live",
                "liveAddress", constants.DefaultServerIp, (byte) 0);
        String portUrl = iniFile.getIniString(userIni, "Live",
                "livePort", "1115", (byte) 0);
        //String baseUrl = "http://" + ipUrl + ":" + portUrl;
        DataUtils.HOST = "http://" + ipUrl + ":" + portUrl+"/";
        String ipPull = iniFile.getIniString(userIni, "Live",
                "pullAddress", constants.DefaultServerIp, (byte) 0);
        String portPUll = iniFile.getIniString(userIni, "Live",
                "pullPort", "1936", (byte) 0);
        DataUtils.GETHOST = "http://" + ipPull + ":" + portPUll+"/hls/";
    }

    private void setViewTreeObserver() {

        final ViewTreeObserver swipeRefreshLayoutVTO = bodyLayout.getViewTreeObserver();
        swipeRefreshLayoutVTO.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                bodyLayout.getWindowVisibleDisplayFrame(r);
                int statusBarH = getStatusBarHeight();//状态栏高度
                int screenH = bodyLayout.getRootView().getHeight();
                if (r.top != statusBarH) {
                    //在这个demo中r.top代表的是状态栏高度，在沉浸式状态栏时r.top＝0，通过getStatusBarHeight获取状态栏高度
                    r.top = statusBarH;
                }
                int keyboardH = screenH - (r.bottom - r.top);

                if (keyboardH == currentKeyboardH) {//有变化时才处理，否则会陷入死循环
                    return;
                }
                currentKeyboardH = keyboardH;
                screenHeight = screenH;//应用屏幕的高度
                editTextBodyHeight = edittextbody.getHeight();

                if (keyboardH < 150) {//说明是隐藏键盘的情况
                    updateEditTextBodyVisible(View.GONE, null);
                    return;
                }
                //偏移listview
                if (layoutManager != null && commentConfig != null) {
                    layoutManager.scrollToPositionWithOffset(commentConfig.circlePosition + CommentAdapter
                            .HEADVIEW_SIZE, getListviewOffset(commentConfig));
                }
            }
        });
    }

    /**
     * 测量偏移量
     *
     * @param commentConfig
     * @return
     */
    private int getListviewOffset(CommentConfig commentConfig) {
        if (commentConfig == null)
            return 0;
        //这里如果你的listview上面还有其它占高度的控件，则需要减去该控件高度，listview的headview除外。
        //int listviewOffset = mScreenHeight - mSelectCircleItemH - mCurrentKeyboardH - mEditTextBodyHeight;
        int listviewOffset = screenHeight - selectCircleItemH - currentKeyboardH - editTextBodyHeight;
        if (commentConfig.commentType == CommentConfig.Type.REPLY) {
            //回复评论的情况
            listviewOffset = listviewOffset + selectCommentItemOffset;
        }
        //Log.i(TAG, "listviewOffset : " + listviewOffset);
        return listviewOffset;
    }

    @Override
    public void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig) {
        this.commentConfig = commentConfig;
        edittextbody.setVisibility(visibility);

        measureCircleItemHighAndCommentItemOffset(commentConfig);

        if (View.VISIBLE == visibility) {
            editText.requestFocus();
            //弹出键盘
            CommonUtils.showSoftInput(editText.getContext(), editText);
            commentText.setVisibility(View.GONE);
        } else if (View.GONE == visibility) {
            //隐藏键盘
            CommonUtils.hideSoftInput(editText.getContext(), editText);
            commentText.setVisibility(View.VISIBLE);
        }
    }

    private void measureCircleItemHighAndCommentItemOffset(CommentConfig commentConfig) {
        if (commentConfig == null)
            return;

        int firstPosition = layoutManager.findFirstVisibleItemPosition();
        //只能返回当前可见区域（列表可滚动）的子项
        View selectCircleItem = layoutManager.getChildAt(commentConfig.circlePosition + CommentAdapter.HEADVIEW_SIZE -
                firstPosition);

        if (selectCircleItem != null) {
            selectCircleItemH = selectCircleItem.getHeight();
        }

        if (commentConfig.commentType == CommentConfig.Type.REPLY) {
            //回复评论的情况
            CommentListView commentLv = (CommentListView) selectCircleItem.findViewById(com.tbs.circle.R.id
                    .commentList);
            if (commentLv != null) {
                //找到要回复的评论view,计算出该view距离所属动态底部的距离
                View selectCommentItem = commentLv.getChildAt(commentConfig.commentPosition);
                if (selectCommentItem != null) {
                    //选择的commentItem距选择的CircleItem底部的距离
                    selectCommentItemOffset = 0;
                    View parentView = selectCommentItem;
                    do {
                        int subItemBottom = parentView.getBottom();
                        parentView = (View) parentView.getParent();
                        if (parentView != null) {
                            selectCommentItemOffset += (parentView.getHeight() - subItemBottom);
                        }
                    } while (parentView != null && parentView != selectCircleItem);
                }
            }
        }
    }

    public void startAnimation() {
        this.loadingAnima = (AnimationDrawable) this.iv.getBackground();
        this.loadingAnima.start();
        this.loadingIV.setVisibility(View.VISIBLE);
    }

    public void stopAnimation() {
        // loadingAnima.stop();
        this.loadingIV.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showLoading(String msg) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void LoadingProgress(int progress) {

    }

    @Override
    public void showError(String errorMsg) {
        recyclerView.setRefreshing(false);
        Toast.makeText(this.getContext(), errorMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public Context getBaseContext() {
        return null;
    }

    @Override
    public void getPraisesCallback(List<FavortItem> datas,List<CommentAll> commentData) {
        for(int i = 0;i< commentData.size();i++){
            List<FavortItem> favorters = new ArrayList<FavortItem>();
            List<FavortItem> unFavorters = new ArrayList<FavortItem>();
            for(int j=0;j< datas.size();j++){
                if(datas.get(j).getZid().equalsIgnoreCase(commentData.get(i).getId())){
                    if(datas.get(j).getType().equals("0"))
                        unFavorters.add(datas.get(j));
                    else
                        favorters.add(datas.get(j));
                }
            }
            commentData.get(i).getFavorters().addAll(favorters);
            commentData.get(i).getUnFavorters().addAll(unFavorters);
        }
        recyclerView.removeMoreListener();
        recyclerView.hideMoreProgress();
        CommentAll item = new CommentAll();
        item.setType("N");
        commentData.add(item);
        recyclerView.setRefreshing(false);
        commentAdapter.setDatas(commentData);
        commentAdapter.notifyDataSetChanged();
        //commentAdapter.notifyDataSetChanged();
    }

    @Override
    public void addPraisesCallback(Map map, int position) {
        CommentAll item = (CommentAll) commentAdapter.getDatas().get(position);
        List<FavortItem> favorters = item.getFavorters();
        List<FavortItem> unFavorters = item.getUnFavorters();
        for(int i = 0; i<favorters.size();i++){
            FavortItem item1 = favorters.get(i);
            if(item1.getUserName().equals(map.get("account"))){
                item1.setType("0");
                unFavorters.add(item1);
                favorters.remove(i);
                item.setFavorters(favorters);
                item.setUnFavorters(unFavorters);
                commentAdapter.notifyDataSetChanged();
                return;
            }
        }
        for(int i = 0; i<unFavorters.size();i++){
            FavortItem item1 = unFavorters.get(i);
            if(item1.getUserName().equals(map.get("account"))){
                item1.setType("1");
                favorters.add(item1);
                unFavorters.remove(i);
                item.setFavorters(favorters);
                item.setUnFavorters(unFavorters);
                commentAdapter.notifyDataSetChanged();
                return;
            }
        }
        FavortItem item1 = new FavortItem();
        item1.setType((String)map.get("type"));
        item1.setUserName((String)map.get("account"));
        item1.setZid((String)map.get("commentId"));
        item1.setId((String)map.get("id"));
        if(map.get("type").equals("1")){
            favorters.add(item1);
        }else{
            unFavorters.add(item1);
        }
        item.setFavorters(favorters);
        item.setUnFavorters(unFavorters);
        commentAdapter.notifyDataSetChanged();
    }

    @Override
    public void cancelPraisesCallback(int position, int commentPosition,int PraisesType) {
        if(PraisesType == 0){
            CommentAll item = (CommentAll) commentAdapter.getDatas().get(commentPosition);
            List<FavortItem> unFavorters = item.getUnFavorters();
            unFavorters.remove(position);
            item.setUnFavorters(unFavorters);
        }else{
            CommentAll item = (CommentAll) commentAdapter.getDatas().get(commentPosition);
            List<FavortItem> favortItems = item.getFavorters();
            favortItems.remove(position);
            item.setFavorters(favortItems);
        }
        commentAdapter.notifyDataSetChanged();
    }

    @Override
    public void getCommentsCallback(List<CommentAll> datas) {
        Map map = new HashMap();
        map.put("resourceId", mId);
        map.put("resourceType", mType);
        map.put("account", iniFile.getIniString(userIni, "Login",
                "Account", "", (byte) 0));
        presenter.getPraises(map,datas);
    }

    @Override
    public void addCommentCallback(int circlePosition, CommentItem addItem) {
        if (addItem != null) {
            if (circlePosition == -1) {
                CommentAll Pitem = new CommentAll();
                User user = new User(addItem.getUser(), addItem.getUnickname(), "");
                String Pid = addItem.getCid();
                Pitem.setId(Pid);
                Pitem.setContent(addItem.getContent());
                Pitem.setUser(user);
                Pitem.setPath(addItem.getPath());
                Pitem.setCreateTime(StringUtils.getDate());
                Pitem.setFavorters(new ArrayList<FavortItem>());
                Pitem.setUnFavorters(new ArrayList<FavortItem>());
                List<CommentAll> commentDatas = new ArrayList<CommentAll>();
                commentDatas.add(Pitem);
                commentDatas.addAll(commentAdapter.getDatas());
                commentAdapter.setDatas(commentDatas);
                commentAdapter.notifyDataSetChanged();
            } else {
                CommentAll item = (CommentAll) commentAdapter.getDatas().get(circlePosition);
                item.getComments().add(addItem);
                commentAdapter.notifyDataSetChanged();
                //circleAdapter.notifyItemChanged(circlePosition+1);
            }
        }
        //清空评论文本
        editText.setText("");
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
