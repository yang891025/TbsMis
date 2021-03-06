package com.tbs.circle.adapter.viewholder;

import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tbs.circle.R;

/**
 * Created by TBS on 2017/10/27.
 */

public class URLMeViewHolder extends CircleMeViewHolder
{
    public RelativeLayout urlBody;
    /** 链接的图片 */
    public ImageView urlImageIv;
    /** 链接的标题 */
    public TextView urlContentTv;

    public URLMeViewHolder(View itemView){
        super(itemView, TYPE_URL);
    }

    @Override
    public void initSubView(int viewType, ViewStub viewStub) {
        if(viewStub == null){
            throw new IllegalArgumentException("viewStub is null...");
        }

        viewStub.setLayoutResource(R.layout.viewstub_urlbody);
        View subViw  = viewStub.inflate();
        RelativeLayout urlBodyView = (RelativeLayout) subViw.findViewById(R.id.urlBody);
        if(urlBodyView != null){
            urlBody = urlBodyView;
            urlImageIv = (ImageView) subViw.findViewById(R.id.urlImageIv);
            urlContentTv = (TextView) subViw.findViewById(R.id.urlContentTv);
        }
    }
}
