package com.tbs.circle.adapter.viewholder;

import android.view.View;
import android.view.ViewStub;

import com.lzy.ninegrid.NineGridView;
import com.tbs.circle.R;
import com.tbs.circle.utils.GlideImageLoader;

/**
 * Created by suneee on 2016/8/16.
 */
public class ImageViewHolder extends CircleViewHolder {
    /** 图片*/
    public NineGridView nineGrid;

    public ImageViewHolder(View itemView){
        super(itemView, TYPE_IMAGE);
    }

    @Override
    public void initSubView(int viewType, ViewStub viewStub) {
        if(viewStub == null){
            throw new IllegalArgumentException("viewStub is null...");
        }
        viewStub.setLayoutResource(R.layout.viewstub_imgbody);
        View subView = viewStub.inflate();
        NineGridView.setImageLoader(new GlideImageLoader());
        NineGridView nineGrid = (NineGridView)subView.findViewById(R.id.multiImagView);
        //MultiImageView multiImageView = (MultiImageView) subView.findViewById(R.id.multiImagView);
        if(nineGrid != null){
            this.nineGrid = nineGrid;
        }
    }
}
