package com.tbs.circle.adapter.viewholder;

import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbs.circle.R;

/**
 * Created by TBS on 2017/10/27.
 */

public class ImageMeViewHolder extends CircleMeViewHolder
{
    /** 图片*/
    //public NineGridView nineGrid;
    public ImageView urlImageIv;
    public TextView urlContentTv;
    public TextView tv_num;

    public ImageMeViewHolder(View itemView){
        super(itemView, TYPE_IMAGE);
    }

    @Override
    public void initSubView(int viewType, ViewStub viewStub) {
        if(viewStub == null){
            throw new IllegalArgumentException("viewStub is null...");
        }
        viewStub.setLayoutResource(R.layout.viewsub_me_imgbody);
        View subView = viewStub.inflate();
        urlImageIv = (ImageView) subView.findViewById(R.id.multiImagView);
        urlContentTv = (TextView) subView.findViewById(R.id.tv_content);
        tv_num = (TextView) subView.findViewById(R.id.tv_num);
    }
}
