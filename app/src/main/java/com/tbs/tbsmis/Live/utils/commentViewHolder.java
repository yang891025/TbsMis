package com.tbs.tbsmis.Live.utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tbs.circle.widgets.CommentListView;
import com.tbs.circle.widgets.ExpandTextView;
import com.tbs.tbsmis.R;

/**
 * Created by TBS on 2018/1/11.
 */

public class commentViewHolder extends RecyclerView.ViewHolder {

    public final static int TYPE_URL = 1;
    public final static int TYPE_IMAGE = 2;
    public final static int TYPE_VIDEO = 3;
    public final static int TYPE_FOOTER = 21;
    public ImageView iv_like_icon;
    public TextView tv_like_count;
    public ImageView iv_unlike_icon;
    public TextView tv_unlike_count;
    public  ImageView iv_comment_icon;
    public  LinearLayout ll_like;
    public  LinearLayout ll_unlike;
    public int viewType;

    public ImageView headIv;
    public TextView nameTv;
    /** 动态的内容 */
    public ExpandTextView contentTv;
    public TextView timeTv;
    public TextView deleteBtn;

    public LinearLayout digCommentBody;

    /** 评论列表 */
    public CommentListView commentList;
    // ===========================

    public commentViewHolder(View itemView) {
        super(itemView);
        headIv = (ImageView) itemView.findViewById(R.id.headIv);
        nameTv = (TextView) itemView.findViewById(R.id.nameTv);
        iv_comment_icon = (ImageView) itemView.findViewById(R.id.iv_comment_icon);
        contentTv = (ExpandTextView) itemView.findViewById(R.id.contentTv);
        timeTv = (TextView) itemView.findViewById(R.id.timeTv);
        deleteBtn = (TextView) itemView.findViewById(R.id.deleteBtn);
        digCommentBody = (LinearLayout) itemView.findViewById(R.id.digCommentBody);
        ll_like = (LinearLayout) itemView.findViewById(R.id.ll_like);
        iv_like_icon= (ImageView) itemView.findViewById(R.id.iv_like_icon);
        tv_like_count = (TextView) itemView.findViewById(R.id.tv_like_count);
        ll_unlike = (LinearLayout) itemView.findViewById(R.id.ll_unlike);
        iv_unlike_icon= (ImageView) itemView.findViewById(R.id.iv_unlike_icon);
        tv_unlike_count = (TextView) itemView.findViewById(R.id.tv_unlike_count);
        commentList = (CommentListView)itemView.findViewById(R.id.commentList);

    }

}
