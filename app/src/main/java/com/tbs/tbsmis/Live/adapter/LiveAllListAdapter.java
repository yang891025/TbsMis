package com.tbs.tbsmis.Live.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tbs.circle.adapter.BaseRecycleViewAdapter;
import com.tbs.tbsmis.Live.LivePlayer;
import com.tbs.tbsmis.Live.bean.LiveAlllist;
import com.tbs.tbsmis.Live.utils.CalculationUtils;
import com.tbs.tbsmis.Live.utils.DataUtils;
import com.tbs.tbsmis.R;

/**
 * 版本号：1.0
 * 类描述：
 * 备注消息：
 * 修改时间：2017/2/7 下午6:17
 **/
public class LiveAllListAdapter extends BaseRecycleViewAdapter
{

    private Context context;

    public LiveAllListAdapter(Context context) {
        this.context = context;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void bindLiveAll(LiveAllListHolder holder, final int position) {
        //holder.img_item_gridview.setImageURI(Uri.parse(mLiveList.get(position).getOwner_pic()));
        final LiveAlllist circleItem = (LiveAlllist) datas.get(position);
        Glide.with(context).load(DataUtils.HOST+"filePath/static/tbsermImage/live/"+circleItem.getRoom_src()).diskCacheStrategy(DiskCacheStrategy
                .ALL)
                .placeholder(R.drawable.image_loading_5_3).into(holder.img_item_gridview);
        holder.tv_column_item_nickname.setText(circleItem.getRoom_name());
        holder.tv_nickname.setText(circleItem.getNickname());
        holder.tv_online_num.setText(CalculationUtils.getOnLine(circleItem.getOnline()));
        holder.rl_live_icon.setBackgroundResource(R.drawable.search_header_live_type_pc_prepare);
        if (circleItem.getShow_status().equals("6")) {
            holder.rl_live_icon.setBackgroundResource(R.drawable.search_header_live_type_mobile);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  颜值栏目 竖屏播放
                Intent intent = new Intent();
                intent.setClass(context, LivePlayer.class);
                Bundle bundle = new Bundle();
                bundle.putString("liveId", circleItem.getId());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LiveAllListHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_recommend_view, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LiveAllListHolder) {
            bindLiveAll((LiveAllListHolder) holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return this.datas.size();
    }

    public class LiveAllListHolder extends RecyclerView.ViewHolder
    {
        //        图片
        public ImageView img_item_gridview;
        //        房间名称
        public TextView tv_column_item_nickname;
        //        在线人数
        public TextView tv_online_num;
        //        昵称
        public TextView tv_nickname;
        //        Icon
        public RelativeLayout rl_live_icon;

        public LiveAllListHolder(View view) {
            super(view);
            img_item_gridview = (ImageView) view.findViewById(R.id.img_item_gridview);
            tv_column_item_nickname = (TextView) view.findViewById(R.id.tv_column_item_nickname);
            tv_online_num = (TextView) view.findViewById(R.id.tv_online_num);
            tv_nickname = (TextView) view.findViewById(R.id.tv_nickname);
            rl_live_icon = (RelativeLayout) view.findViewById(R.id.rl_live_icon);
        }
    }
}
