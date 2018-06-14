package com.tbs.circle.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lzy.imagepicker.bean.ImageItem;
import com.tbs.circle.R;

import java.util.List;

/**
 * 图片显示Adapter
 * Created by kuyue on 2017/6/19 下午3:59.
 * 邮箱:595327086@qq.com
 */

public class PostArticleImgAdapter extends RecyclerView.Adapter<PostArticleImgAdapter.MyViewHolder>
{

    private List<ImageItem> mDatas;
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;

    public PostArticleImgAdapter(Context context, List<ImageItem> datas) {
        this.mDatas = datas;
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(mLayoutInflater.inflate(R.layout.item_img, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (position == mDatas.size()) {
            holder.imageView.setImageBitmap(BitmapFactory.decodeResource(
                    mContext.getResources(), R.drawable.icon_addpic_unfocused));
            if (position == 9) {
                holder.imageView.setVisibility(View.GONE);
            }
        } else
            Glide.with(mContext).load(mDatas.get(position).path).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder
                    .imageView);
    }

    @Override
    public int getItemCount() {
        if (mDatas.size() == 9) {
            return 9;
        }
        return mDatas == null ? 0 : mDatas.size() + 1;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {

        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.sdv);
        }
    }

}
