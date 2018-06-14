package com.tbs.tbsmis.Live.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tbs.circle.adapter.BaseRecycleViewAdapter;
import com.tbs.circle.adapter.viewholder.CircleViewHolder;
import com.tbs.circle.bean.CircleItem;
import com.tbs.circle.bean.CommentConfig;
import com.tbs.circle.bean.CommentItem;
import com.tbs.circle.utils.DatasUtil;
import com.tbs.circle.utils.DateUtils;
import com.tbs.circle.utils.UrlUtils;
import com.tbs.circle.widgets.CommentListView;
import com.tbs.circle.widgets.ExpandTextView;
import com.tbs.ini.IniFile;
import com.tbs.tbsmis.Live.bean.CommentAll;
import com.tbs.tbsmis.Live.mvp.presenter.CommentsPresenter;
import com.tbs.tbsmis.Live.utils.CommentDialog;
import com.tbs.tbsmis.Live.utils.commentViewHolder;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.UIHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by TBS on 2018/1/11.
 */

public class CommentAdapter extends BaseRecycleViewAdapter
{
    public final static int TYPE_HEAD = 10;
    private static final int STATE_IDLE = 0;
    private static final int STATE_ACTIVED = 1;
    private static final int STATE_DEACTIVED = 2;
    private int videoState = STATE_IDLE;
    public static final int HEADVIEW_SIZE = 1;

    private int curType = 0;
    private CommentsPresenter presenter;
    private Context context;

    public void setCommentPresenter(CommentsPresenter presenter) {
        this.presenter = presenter;
    }

    public CommentAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
//        if (position == 0) {
//            return TYPE_HEAD;
//        }
        int itemType = 0;
        CommentAll item = (CommentAll) datas.get(position);
        if (CircleItem.TYPE_FOOTER.equals(item.getType())) {
            itemType = CircleViewHolder.TYPE_FOOTER;
        }
        return itemType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == TYPE_HEAD) {
            View headView = LayoutInflater.from(parent.getContext()).inflate(com.tbs.circle.R.layout
                    .item_list_footer, parent, false);
            viewHolder = new HeaderViewHolder(headView);
        } else if (viewType == CircleViewHolder.TYPE_FOOTER) {
            View headView = LayoutInflater.from(parent.getContext()).inflate(com.tbs.circle.R.layout
                    .item_list_footer, parent, false);
            viewHolder = new HeaderViewHolder(headView);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_video_comment_item,
                    parent, false);
            viewHolder = new commentViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (getItemViewType(position) == TYPE_HEAD) {

        } else if (getItemViewType(position) == CircleViewHolder.TYPE_FOOTER) {

        } else {

            final commentViewHolder holder = (commentViewHolder) viewHolder;
            final CommentAll circleItem = (CommentAll) datas.get(position);
            //System.out.println(circleItem.toString());
            final String circleId = circleItem.getId();
            String name = circleItem.getUser().getName();
            String headImg = circleItem.getUser().getHeadUrl();
            final String content = circleItem.getContent();
            String createTime = circleItem.getCreateTime();
            final List<CommentItem> commentsDatas = circleItem.getComments();
            boolean hasComment = circleItem.hasComment();
            holder.nameTv.setText(name);
            holder.timeTv.setText(DateUtils.getDuration(context, createTime, DateUtils.getStringTime(System
                    .currentTimeMillis())));
            holder.iv_comment_icon.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    if (presenter != null) {
                        CommentConfig config = new CommentConfig();
                        config.circlePosition = position;
                        config.circleId = circleId;
                        config.path = circleItem.getPath();
                        config.commentType = CommentConfig.Type.PUBLIC;
                        presenter.showEditTextBody(config);
                    }
                }
            });

            if (!TextUtils.isEmpty(content)) {
                holder.contentTv.setExpand(circleItem.isExpand());
                holder.contentTv.setExpandStatusListener(new ExpandTextView.ExpandStatusListener()
                {
                    @Override
                    public void statusChange(boolean isExpand) {
                        circleItem.setExpand(isExpand);
                    }
                });

                holder.contentTv.setText(UrlUtils.formatUrlString(content));
            }
            holder.contentTv.setVisibility(TextUtils.isEmpty(content) ? View.GONE : View.VISIBLE);
            if (hasComment) {//处理评论列表
                holder.commentList.setOnItemClickListener(new CommentListView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(int commentPosition) {
                        CommentItem commentItem = commentsDatas.get(commentPosition);
                        if (DatasUtil.getUserMsg(context).getId().equals(commentItem.getUser())) {//复制或者删除自己的评论

                            CommentDialog dialog = new CommentDialog(context, presenter, commentItem,
                                    position);
                            dialog.show();
                        } else {//回复别人的评论
                            if (presenter != null) {
                                CommentConfig config = new CommentConfig();
                                config.circlePosition = position;
                                config.circleId = commentItem.getCid();
                                config.commentPosition = commentPosition;
                                config.commentType = CommentConfig.Type.REPLY;
                                config.path = commentItem.getPath();
                                config.replyUser = commentItem.getUser();
                                config.replyName = commentItem.getUnickname();
                                presenter.showEditTextBody(config);
                            }
                        }
                    }
                });
                holder.commentList.setOnItemLongClickListener(new CommentListView.OnItemLongClickListener()
                {
                    @Override
                    public void onItemLongClick(int commentPosition) {
                        //长按进行复制或者删除
                        CommentItem commentItem = commentsDatas.get(commentPosition);
                        CommentDialog dialog = new CommentDialog(context, presenter, commentItem, position);
                        dialog.show();
                    }
                });
                holder.commentList.setVisibility(View.VISIBLE);
                holder.digCommentBody.setVisibility(View.VISIBLE);
                holder.commentList.setDatas(commentsDatas);

            } else {
                holder.commentList.setVisibility(View.GONE);
                holder.digCommentBody.setVisibility(View.GONE);
            }
            final int favorterPosition = circleItem.hasFavorter(DatasUtil.getUserMsg(context).getId());
            final int unfavorterPosition = circleItem.hasUnFavorter(DatasUtil.getUserMsg(context).getId());

            if (unfavorterPosition >= 0) {
                holder.iv_unlike_icon.setBackgroundResource(R.drawable.timeline_icon_unlike_check);
                holder.ll_unlike.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view) {
                        Map map = new HashMap();
                        map.put("praisesId", circleItem.getUnFavorters().get(circleItem.hasUnFavorter(DatasUtil.getUserMsg(context).getId())).getId());
                        presenter.cancelPraises(map, circleItem.hasUnFavorter(DatasUtil.getUserMsg(context).getId()), position, 0);
                    }
                });
            } else {
                holder.iv_unlike_icon.setBackgroundResource(R.drawable.timeline_icon_unlike);
                holder.ll_unlike.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view) {
                        IniFile iniFile = new IniFile();
                        String webRoot = UIHelper.getStoragePath(context);
                        webRoot += context.getString(R.string.SD_CARD_TBSAPP_PATH2);
                        webRoot = UIHelper.getShareperference(context,
                                constants.SAVE_INFORMATION, "Path", webRoot);
                        if (!webRoot.endsWith("/")) {
                            webRoot += "/";
                        }
                        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
                        String appNewsFile = webRoot
                                + iniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
                        String userIni = appNewsFile;
                        if (Integer.parseInt(iniFile.getIniString(userIni, "Login",
                                "LoginType", "0", (byte) 0)) == 1) {
                            String dataPath = context.getFilesDir().getParentFile()
                                    .getAbsolutePath();
                            if (dataPath.endsWith("/") == false) {
                                dataPath = dataPath + "/";
                            }
                            userIni = dataPath + "TbsApp.ini";
                        }
                        Map map = new HashMap();
                        map.put("commentId", circleId);
                        map.put("account", iniFile.getIniString(userIni, "Login",
                                "Account", "", (byte) 0));
                        map.put("type", "0");
                        presenter.addPraises(map, position);
                    }
                });
            }

            if (favorterPosition >= 0) {
                holder.iv_like_icon.setBackgroundResource(R.drawable.timeline_icon_like_check);
                holder.ll_like.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view) {
                        Map map = new HashMap();
                        map.put("praisesId", circleItem.getFavorters().get(circleItem.hasFavorter(DatasUtil.getUserMsg(context).getId())).getId());
                        presenter.cancelPraises(map, circleItem.hasFavorter(DatasUtil.getUserMsg(context).getId()), position, 1);
                    }
                });
            } else {
                holder.iv_like_icon.setBackgroundResource(R.drawable.timeline_icon_like);
                holder.ll_like.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view) {
                        IniFile iniFile = new IniFile();
                        String webRoot = UIHelper.getStoragePath(context);
                        webRoot += context.getString(R.string.SD_CARD_TBSAPP_PATH2);
                        webRoot = UIHelper.getShareperference(context,
                                constants.SAVE_INFORMATION, "Path", webRoot);
                        if (!webRoot.endsWith("/")) {
                            webRoot += "/";
                        }
                        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
                        String appNewsFile = webRoot
                                + iniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
                        String userIni = appNewsFile;
                        if (Integer.parseInt(iniFile.getIniString(userIni, "Login",
                                "LoginType", "0", (byte) 0)) == 1) {
                            String dataPath = context.getFilesDir().getParentFile()
                                    .getAbsolutePath();
                            if (dataPath.endsWith("/") == false) {
                                dataPath = dataPath + "/";
                            }
                            userIni = dataPath + "TbsApp.ini";
                        }
                        Map map = new HashMap();
                        map.put("commentId", circleId);
                        map.put("account", iniFile.getIniString(userIni, "Login",
                                "Account", "", (byte) 0));
                        map.put("type", "1");
                        presenter.addPraises(map, position);
                    }
                });
            }

//            if (refresh) {
//                Map map = new HashMap();
//                map.put("commentId", circleId);
//                presenter.getPraises(map, position);
//
//            }
            holder.tv_like_count.setText("(" + circleItem.getFavorters().size() + ")");
            holder.tv_unlike_count.setText("(" + circleItem.getUnFavorters().size() + ")");
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();//有head需要加1
    }


    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder
    {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

}
