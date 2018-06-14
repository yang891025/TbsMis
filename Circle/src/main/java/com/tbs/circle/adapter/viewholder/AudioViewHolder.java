package com.tbs.circle.adapter.viewholder;

import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tbs.circle.R;

/**
 * Created by suneee on 2016/8/16.
 */
public class AudioViewHolder extends CircleViewHolder {

    public RelativeLayout AudioView;
    public ImageView iv_voice;
    public View subView;

    public AudioViewHolder(View itemView){
        super(itemView, TYPE_AUDIO);
    }

    @Override
    public void initSubView(int viewType, ViewStub viewStub) {
        if(viewStub == null){
            throw new IllegalArgumentException("viewStub is null...");
        }
        viewStub.setLayoutResource(R.layout.viewstub_audiobody);

        subView = viewStub.inflate();
        AudioView = (RelativeLayout) subView.findViewById(R.id.AudioView);
        iv_voice = (ImageView) subView.findViewById(R.id.iv_voice);
    }
}
