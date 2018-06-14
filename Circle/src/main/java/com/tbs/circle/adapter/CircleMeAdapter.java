package com.tbs.circle.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tbs.circle.R;
import com.tbs.circle.activity.CircleDetailActivity;
import com.tbs.circle.adapter.viewholder.CircleMeViewHolder;
import com.tbs.circle.adapter.viewholder.ImageMeViewHolder;
import com.tbs.circle.adapter.viewholder.URLMeViewHolder;
import com.tbs.circle.adapter.viewholder.VideoMeViewHolder;
import com.tbs.circle.bean.CircleItem;
import com.tbs.circle.bean.PhotoInfo;
import com.tbs.circle.utils.DatasUtil;

import java.util.List;

/**
 * Created by TBS on 2017/10/10.
 */

public class CircleMeAdapter extends BaseRecycleViewAdapter
{
    public final static int TYPE_HEAD = 10;
    private Context context;
    private LayoutInflater inflater;

    public CircleMeAdapter(Context context1) {
        this.context = context1;
        inflater = LayoutInflater.from(context);
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder
    {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEAD;
        }

        int itemType = 0;
        CircleItem item = (CircleItem) datas.get(position - 1);
        if (CircleItem.TYPE_URL.equals(item.getType())) {
            itemType = CircleMeViewHolder.TYPE_URL;
        } else if (CircleItem.TYPE_IMG.equals(item.getType())) {
            itemType = CircleMeViewHolder.TYPE_IMAGE;
        } else if (CircleItem.TYPE_VIDEO.equals(item.getType())) {
            itemType = CircleMeViewHolder.TYPE_VIDEO;
        } else if (CircleItem.TYPE_FOOTER.equals(item.getType())) {
            itemType = CircleMeViewHolder.TYPE_FOOTER;
        }
        return itemType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == TYPE_HEAD) {
            View headView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_circle_header, parent,
                    false);
            viewHolder = new HeaderViewHolder(headView);
        } else if (viewType == CircleMeViewHolder.TYPE_FOOTER) {
            View headView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_footer, parent, false);
            viewHolder = new HeaderViewHolder(headView);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_moments_me, parent, false);
            if (viewType == CircleMeViewHolder.TYPE_URL) {
                viewHolder = new URLMeViewHolder(view);
            } else if (viewType == CircleMeViewHolder.TYPE_IMAGE) {
                viewHolder = new ImageMeViewHolder(view);
            } else if (viewType == CircleMeViewHolder.TYPE_VIDEO) {
                viewHolder = new VideoMeViewHolder(view);
            } else {
                viewHolder = new URLMeViewHolder(view);
            }

        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEAD) {
            HeaderViewHolder holder1 = (HeaderViewHolder) holder;
            ImageView iv_avatar = (ImageView) holder1.itemView.findViewById(R.id.iv_avatar);
            Glide.with(context).load(DatasUtil.getUserMsg(context).getHeadUrl()).diskCacheStrategy(DiskCacheStrategy
                    .ALL)
                    .placeholder(R.drawable.default_avatar).into(iv_avatar);
            holder1.itemView.setEnabled(false);
        } else if (getItemViewType(position) == CircleMeViewHolder.TYPE_FOOTER) {

        } else {
            CircleMeViewHolder holder1 = (CircleMeViewHolder) holder;
            final CircleItem json = (CircleItem) datas.get(position - 1);
            // 如果数据出错....
            if (json == null) {
                datas.remove(position - 1);
                this.notifyDataSetChanged();
            }
            final String content = json.getContent();
            String location = "0";
            //String avatar = json.getUser().getHeadUrl();
            String rel_time = json.getCreateTime();
            if(position >= 2){
                CircleItem json2 = (CircleItem) datas.get(position - 2);
                String moth = rel_time.substring(5, 7);
                String day = rel_time.substring(8, 10);
                String moth2 = json2.getCreateTime().substring(5, 7);
                String day2 = json2.getCreateTime().substring(8, 10);
                if(moth.equals(moth2) && day.equals(day2)){
                    holder1.re_time.setVisibility(View.INVISIBLE);
                }else{
                    holder1.re_time.setVisibility(View.VISIBLE);
                }
            }else{
                holder1.re_time.setVisibility(View.VISIBLE);
            }
            // 显示位置
            if (location != null && !location.equals("0")) {
                holder1.tv_location.setVisibility(View.VISIBLE);
                holder1.tv_location.setText(location);
            }
            setDateText(rel_time, holder1.tv_day, holder1.tv_month, holder1.view_header);
            switch (holder1.viewType) {
                case CircleMeViewHolder.TYPE_URL:// 处理链接动态的链接内容和和图片
                    if (holder instanceof URLMeViewHolder) {
                        final String linkImg = json.getLinkImg();
                        final String linkTitle = json.getLinkTitle();
                        if (!linkImg.isEmpty())
                            Glide.with(context).load(linkImg).into(((URLMeViewHolder) holder).urlImageIv);
                        ((URLMeViewHolder) holder).urlContentTv.setText(linkTitle);
                        ((URLMeViewHolder) holder).urlBody.setVisibility(View.VISIBLE);
                        //((URLMeViewHolder) holder).urlTipTv.setVisibility(View.VISIBLE);
//                        ((URLViewHolder) holder).urlBody.setOnClickListener(new View.OnClickListener()
//                        {
//                            @Override
//                            public void onClick(View view) {
//                                Intent intent = new Intent();
//                                intent.putExtra("url", json.getLink());
//                                intent.putExtra("title", linkTitle);
//                                intent.setClass(context, CircleWebViewActivity.class);
//                                context.startActivity(intent);
//
//                            }
//                        });
                    }

                    break;
                case CircleMeViewHolder.TYPE_IMAGE:// 处理图片
                    if (holder instanceof ImageMeViewHolder) {
                        final List<PhotoInfo> photos = json.getPhotos();
                        Glide.with(context).load(photos.get(0).url).into(((ImageMeViewHolder) holder).urlImageIv);
                        // 显示文章内容
                        ((ImageMeViewHolder) holder).urlContentTv.setText(content);
                        ((ImageMeViewHolder) holder).tv_num.setVisibility(View.VISIBLE);
                        ((ImageMeViewHolder) holder).tv_num.setText(context.getString(R.string.total) + String
                                .valueOf(photos.size()) + context
                                .getString
                                        (R.string.pieces));
                    }
                    break;
                case CircleMeViewHolder.TYPE_VIDEO:
                    if (holder instanceof VideoMeViewHolder) {
                        ((VideoMeViewHolder) holder).urlContentTv.setText(json.getContent());
                    }
                    break;
                default:
                    break;
            }
            holder1.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, CircleDetailActivity.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable("data", json);
                    intent.putExtras(mBundle);
                    context.startActivity(intent);
                }
            });
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return datas.size() + 1;
    }


    private String recordDate = "";

    private void setDateText(String rel_time, TextView tv_day,
                             TextView tv_month, View view_header) {
        String date = rel_time.substring(0, 10);
        String moth = rel_time.substring(5, 7);
        String day = rel_time.substring(8, 10);
        if (moth.startsWith("0")) {
            moth = moth.substring(1);
        }
        if (!date.equals(recordDate)) {
            view_header.setVisibility(View.VISIBLE);
            tv_day.setVisibility(View.VISIBLE);
            tv_month.setVisibility(View.VISIBLE);
            tv_day.setText(day);
            tv_month.setText(moth + context.getString(R.string.Moth));
        } else {
            view_header.setVisibility(View.GONE);
            tv_day.setVisibility(View.GONE);
            tv_month.setVisibility(View.GONE);
        }
    }


}

