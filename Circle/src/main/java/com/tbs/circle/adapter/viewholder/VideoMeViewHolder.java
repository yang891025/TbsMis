package com.tbs.circle.adapter.viewholder;

import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import com.tbs.circle.R;

/**
 * Created by TBS on 2017/10/27.
 */

public class VideoMeViewHolder extends CircleMeViewHolder {

    public View subView;
    public TextView urlContentTv;

    public VideoMeViewHolder(View itemView){
        super(itemView, TYPE_VIDEO);
    }

    @Override
    public void initSubView(int viewType, ViewStub viewStub) {
        if(viewStub == null){
            throw new IllegalArgumentException("viewStub is null...");
        }
        viewStub.setLayoutResource(R.layout.viewstub_me_videobody);
        subView = viewStub.inflate();
        urlContentTv = (TextView) subView.findViewById(R.id.tv_content);
    }
}