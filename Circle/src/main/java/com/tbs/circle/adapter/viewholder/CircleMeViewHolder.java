package com.tbs.circle.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tbs.circle.R;

/**
 * Created by TBS on 2017/10/27.
 */

public abstract class CircleMeViewHolder extends RecyclerView.ViewHolder
{
    public final static int TYPE_URL = 1;
    public final static int TYPE_IMAGE = 2;
    public final static int TYPE_VIDEO = 3;
    public final static int TYPE_FOOTER = 21;
    public RelativeLayout re_time;
    public  TextView tv_day;
    public  TextView tv_month;
    public  TextView tv_location;
    public  View view_header;
    public int viewType;


    public CircleMeViewHolder(View itemView, int viewType) {
        super(itemView);
        this.viewType = viewType;

        ViewStub viewStub = (ViewStub) itemView.findViewById(R.id.viewStub);

        initSubView(viewType, viewStub);

        re_time = (RelativeLayout) itemView
                .findViewById(R.id.re_time);
        tv_day = (TextView) itemView
                .findViewById(R.id.tv_day);
        tv_month = (TextView) itemView
                .findViewById(R.id.tv_month);
        tv_location = (TextView) itemView
                .findViewById(R.id.tv_location);
        view_header = itemView
                .findViewById(R.id.view_header);

    }

    public abstract void initSubView(int viewType, ViewStub viewStub);
}
