package com.tbs.circle.adapter.viewholder;

import android.view.View;
import android.view.ViewStub;

import com.tbs.circle.R;

import com.tbs.player.widget.PlayerView;


/**
 * Created by suneee on 2016/8/16.
 */
public class VideoViewHolder extends CircleViewHolder {

    public PlayerView videoView;
    public View subView;

    public VideoViewHolder(View itemView){
        super(itemView, TYPE_VIDEO);
    }

    @Override
    public void initSubView(int viewType, ViewStub viewStub) {
        if(viewStub == null){
            throw new IllegalArgumentException("viewStub is null...");
        }

        viewStub.setLayoutResource(R.layout.viewstub_videobody);
        subView = viewStub.inflate();
    }
}
