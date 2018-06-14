package com.tbs.circle.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.tbs.circle.R;
import com.tbs.circle.activity.CircleWebViewActivity;
import com.tbs.circle.adapter.viewholder.CircleViewHolder;
import com.tbs.circle.adapter.viewholder.ImageViewHolder;
import com.tbs.circle.adapter.viewholder.URLViewHolder;
import com.tbs.circle.adapter.viewholder.VideoViewHolder;
import com.tbs.circle.bean.ActionItem;
import com.tbs.circle.bean.CircleItem;
import com.tbs.circle.bean.CommentConfig;
import com.tbs.circle.bean.CommentItem;
import com.tbs.circle.bean.FavortItem;
import com.tbs.circle.bean.PhotoInfo;
import com.tbs.circle.mvp.presenter.CirclePresenter;
import com.tbs.circle.utils.DatasUtil;
import com.tbs.circle.utils.DateUtils;
import com.tbs.circle.utils.GlideCircleTransform;
import com.tbs.circle.utils.UrlUtils;
import com.tbs.circle.widgets.CommentListView;
import com.tbs.circle.widgets.ExpandTextView;
import com.tbs.circle.widgets.PraiseListView;
import com.tbs.circle.widgets.SnsPopupWindow;
import com.tbs.circle.widgets.dialog.CommentDialog;

import java.util.ArrayList;
import java.util.List;

import com.tbs.player.listener.OnShowThumbnailListener;
import com.tbs.player.widget.PlayStateParams;
import com.tbs.player.widget.PlayerView;

/**
 * Created by tbs on 16/5/17.
 */
public class CircleDetailAdapter extends BaseRecycleViewAdapter
{

    private static final int STATE_IDLE = 0;
    private int videoState = STATE_IDLE;
    public static final int HEADVIEW_SIZE = 1;

    int curPlayIndex = -1;

    private CirclePresenter presenter;
    private Context context;

    public void setCirclePresenter(CirclePresenter presenter) {
        this.presenter = presenter;
    }

    public CircleDetailAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {

        int itemType = 0;
        CircleItem item = (CircleItem) datas.get(position);
        if (CircleItem.TYPE_URL.equals(item.getType())) {
            itemType = CircleViewHolder.TYPE_URL;
        } else if (CircleItem.TYPE_IMG.equals(item.getType())) {
            itemType = CircleViewHolder.TYPE_IMAGE;
        } else if (CircleItem.TYPE_VIDEO.equals(item.getType())) {
            itemType = CircleViewHolder.TYPE_VIDEO;
        }
        return itemType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_circle_item, parent, false);
        if (viewType == CircleViewHolder.TYPE_URL) {
            viewHolder = new URLViewHolder(view);
        } else if (viewType == CircleViewHolder.TYPE_IMAGE) {
            viewHolder = new ImageViewHolder(view);
        } else if (viewType == CircleViewHolder.TYPE_VIDEO) {
            viewHolder = new VideoViewHolder(view);
        } else {
            viewHolder = new URLViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int circlePosition) {

        // final int circlePosition = position - HEADVIEW_SIZE;
        final CircleViewHolder holder = (CircleViewHolder) viewHolder;
        final CircleItem circleItem = (CircleItem) datas.get(circlePosition);
        final String circleId = circleItem.getId();
        String name = circleItem.getUser().getName();
        String headImg = circleItem.getUser().getHeadUrl();
        final String content = circleItem.getContent();
        String createTime = circleItem.getCreateTime();
        final List<FavortItem> favortDatas = circleItem.getFavorters();
        final List<CommentItem> commentsDatas = circleItem.getComments();
        boolean hasFavort = circleItem.hasFavort();
        boolean hasComment = circleItem.hasComment();

        Glide.with(context).load(headImg).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.color
                .bg_no_photo).transform(new GlideCircleTransform(context)).into(holder.headIv);

        holder.nameTv.setText(name);
        holder.timeTv.setText(DateUtils.getDuration(context, createTime, DateUtils.getStringTime(System
                .currentTimeMillis())));

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

        if (DatasUtil.getUserMsg(context).getId().equals(circleItem.getUser().getId())) {
            holder.deleteBtn.setVisibility(View.VISIBLE);
        } else {
            holder.deleteBtn.setVisibility(View.GONE);
        }
        holder.deleteBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                //删除
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("提示");
                alertDialogBuilder.setMessage("确定删除吗？").setPositiveButton("确定", new DialogInterface
                        .OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (presenter != null) {
                            presenter.deleteCircle(circleId);
                        }
                    }
                }).setNeutralButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();

            }
        });
        if (hasFavort || hasComment) {
            if (hasFavort) {//处理点赞列表
                holder.praiseListView.setOnItemClickListener(new PraiseListView.OnItemClickListener()
                {
                    @Override
                    public void onClick(int position) {
                        String userName = favortDatas.get(position).getUser();
                        String userId = favortDatas.get(position).getId();
//                        Toast.makeText(context, userName + " &id = " + userId, Toast
//                                .LENGTH_SHORT).show();
                    }
                });
                holder.praiseListView.setDatas(favortDatas);
                holder.praiseListView.setVisibility(View.VISIBLE);
            } else {
                holder.praiseListView.setVisibility(View.GONE);
            }

            if (hasComment) {//处理评论列表
                holder.commentList.setOnItemClickListener(new CommentListView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(int commentPosition) {
                        CommentItem commentItem = commentsDatas.get(commentPosition);
                        if (DatasUtil.getUserMsg(context).getId().equals(commentItem.getUser())) {//复制或者删除自己的评论

                            CommentDialog dialog = new CommentDialog(context, presenter, commentItem,
                                    circlePosition);
                            dialog.show();
                        } else {//回复别人的评论
                            if (presenter != null) {
                                CommentConfig config = new CommentConfig();
                                config.circlePosition = circlePosition;
                                config.circleId = circleId;
                                config.commentPosition = commentPosition;
                                config.commentType = CommentConfig.Type.REPLY;
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
                        CommentDialog dialog = new CommentDialog(context, presenter, commentItem, circlePosition);
                        dialog.show();
                    }
                });
                holder.commentList.setDatas(commentsDatas);
                holder.commentList.setVisibility(View.VISIBLE);

            } else {
                holder.commentList.setVisibility(View.GONE);
            }
            holder.digCommentBody.setVisibility(View.VISIBLE);
        } else {
            holder.digCommentBody.setVisibility(View.GONE);
        }

        holder.digLine.setVisibility(hasFavort && hasComment ? View.VISIBLE : View.GONE);

        final SnsPopupWindow snsPopupWindow = holder.snsPopupWindow;
        //判断是否已点赞
        String curUserFavortId = circleItem.getCurUserFavortId(DatasUtil.getUserMsg(context).getId());
        if (!TextUtils.isEmpty(curUserFavortId)) {
            snsPopupWindow.getmActionItems().get(0).mTitle = "取消";
        } else {
            snsPopupWindow.getmActionItems().get(0).mTitle = "赞";
        }
        snsPopupWindow.update();
        snsPopupWindow.setmItemClickListener(new PopupItemClickListener(circlePosition, circleItem,
                curUserFavortId));
        holder.snsBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                //弹出popupwindow
                snsPopupWindow.showPopupWindow(view);
            }
        });

        holder.urlTipTv.setVisibility(View.GONE);
        switch (holder.viewType) {
            case CircleViewHolder.TYPE_URL:// 处理链接动态的链接内容和和图片
                if (holder instanceof URLViewHolder) {
                    String linkImg = circleItem.getLinkImg();
                    final String linkTitle = circleItem.getLinkTitle();
                    if (!linkImg.isEmpty())
                        Glide.with(context).load(linkImg).into(((URLViewHolder) holder).urlImageIv);
                    ((URLViewHolder) holder).urlContentTv.setText(linkTitle);
                    ((URLViewHolder) holder).urlBody.setVisibility(View.VISIBLE);
                    ((URLViewHolder) holder).urlTipTv.setVisibility(View.VISIBLE);
                    ((URLViewHolder) holder).urlBody.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.putExtra("url", circleItem.getLink());
                                intent.putExtra("title", linkTitle);
                                intent.setClass(context, CircleWebViewActivity.class);
                                context.startActivity(intent);

                            }
                        });
                }

                break;
            case CircleViewHolder.TYPE_IMAGE:// 处理图片
                if (holder instanceof ImageViewHolder) {
                    final List<PhotoInfo> photos = circleItem.getPhotos();
                    if (photos != null && photos.size() > 0) {
                        ArrayList<ImageInfo> imageInfo = new ArrayList<>();
                        for (PhotoInfo photoInfo : photos) {
                            ImageInfo info = new ImageInfo();
                            info.setThumbnailUrl(photoInfo.url);
                            info.setBigImageUrl(photoInfo.url);
                            imageInfo.add(info);
                        }
                        ((ImageViewHolder) holder).nineGrid.setAdapter(new NineGridViewClickAdapter(context,
                                imageInfo));
                    } else {
                        ((ImageViewHolder) holder).nineGrid.setVisibility(View.GONE);
                    }
                }

                break;
            case CircleViewHolder.TYPE_VIDEO:
                if (holder instanceof VideoViewHolder) {
                    ((VideoViewHolder) holder).videoView = new PlayerView((Activity) context, ((VideoViewHolder)
                            holder).subView)
                            .setScaleType(PlayStateParams.wrapcontent)
                            .forbidTouch(false)
                            .hideFullscreen(true)
                            .hideControlPanl(true)
                            .setNetWorkTypeTie(true)
                            .hideCenterPlayer(false)
                            .setForbidDoulbeUp(true)
                            .setPlaySource(circleItem.getVideoUrl())
                            .showThumbnail(new OnShowThumbnailListener()
                            {
                                @Override
                                public void onShowThumbnail(ImageView ivThumbnail) {
                                }
                            });
                }

                break;
            default:
                break;
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


    private class PopupItemClickListener implements SnsPopupWindow.OnItemClickListener
    {
        private String mFavorId;
        //动态在列表中的位置
        private int mCirclePosition;
        private long mLasttime = 0;
        private CircleItem mCircleItem;

        public PopupItemClickListener(int circlePosition, CircleItem circleItem, String favorId) {
            this.mFavorId = favorId;
            this.mCirclePosition = circlePosition;
            this.mCircleItem = circleItem;
        }

        @Override
        public void onItemClick(ActionItem actionitem, int position) {
            switch (position) {
                case 0://点赞、取消点赞
                    if (System.currentTimeMillis() - mLasttime < 700)//防止快速点击操作
                        return;
                    mLasttime = System.currentTimeMillis();
                    if (presenter != null) {
                        if ("赞".equals(actionitem.mTitle.toString())) {
                            presenter.addFavort(mCirclePosition, mCircleItem.getId());
                        } else {//取消点赞
                            presenter.deleteFavort(mCirclePosition, mFavorId);
                        }
                    }
                    break;
                case 1://发布评论
                    if (presenter != null) {
                        CommentConfig config = new CommentConfig();
                        config.circlePosition = mCirclePosition;
                        config.circleId = mCircleItem.getId();
                        config.commentType = CommentConfig.Type.PUBLIC;
                        presenter.showEditTextBody(config);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
