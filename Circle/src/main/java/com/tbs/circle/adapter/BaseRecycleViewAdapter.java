package com.tbs.circle.adapter;

import android.support.v7.widget.RecyclerView;

import com.tbs.circle.listener.RecycleViewItemListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tbs on 16/4/9.
 */
public abstract class BaseRecycleViewAdapter<T,VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected RecycleViewItemListener itemListener;
    protected List<T> datas = new ArrayList<T>();

    public List<T> getDatas() {
        if (datas==null)
            datas = new ArrayList<T>();
        return datas;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
    }

    public void setItemListener(RecycleViewItemListener listener){
        this.itemListener = listener;
    }

}
