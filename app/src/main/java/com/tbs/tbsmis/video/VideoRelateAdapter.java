package com.tbs.tbsmis.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tbs.tbsmis.R;

import java.util.ArrayList;
import java.util.HashMap;

public class VideoRelateAdapter extends BaseAdapter
{
    // �����ݵ�list
    private final ArrayList<HashMap<String, String>> list;
    private LayoutInflater inflater;
    private final Context context;

    // ������
    @SuppressLint("UseSparseArrays")
    public VideoRelateAdapter(ArrayList<HashMap<String, String>> list,
                              Context context) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        holder = new ViewHolder();
        // 如果是标签项
        convertView = this.inflater.inflate(R.layout.chapter_tag, null);
        // System.out.println("section "+ position+"= " + list.get(position).get("name:"));
        holder.name = (TextView) convertView
                .findViewById(R.id.group_list_item_text);
        holder.name.setText(this.list.get(position).get("name"));
        holder.Url = this.list.get(position).get("Url");
        convertView.setTag(holder);
        return convertView;
    }

    public static class ViewHolder
    {
        public TextView name;
        public String Url;
    }
}