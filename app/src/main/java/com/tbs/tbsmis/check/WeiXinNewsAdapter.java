package com.tbs.tbsmis.check;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WeiXinNewsAdapter extends BaseAdapter
{
    // �����ݵ�list
    private  ArrayList<HashMap<String, String>> list;
    private LayoutInflater inflater;
    private  List<String> listTag;
    private  Context context;
    private  ImageLoader imageLoader;

    // ������
    @SuppressLint("UseSparseArrays")
    public WeiXinNewsAdapter(ArrayList<HashMap<String, String>> list,
                             Context context, List<String> listTag) {
        this.context = context;
        this.list = list;
        this.listTag = listTag;
        this.imageLoader = new ImageLoader(context,R.drawable.clean_category_thumbnails);
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
    public boolean isEnabled(int position) {
        if (this.listTag.contains(this.list.get(position).get("fileName"))) {
            return false;
        }
        return super.isEnabled(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WeiXinNewsAdapter.ViewHolder holder = null;

        // ���ViewHolder����
        holder = new WeiXinNewsAdapter.ViewHolder();
        // ���벼�ֲ���ֵ��convertview
        if (this.listTag.contains(this.list.get(position).get("fileName"))) {
            // 如果是标签项
            convertView = this.inflater.inflate(R.layout.group_list_tag, null);
            holder.group = (TextView) convertView
                    .findViewById(R.id.group_list_item_text);
            holder.group.setText(FileUtils.getFileNameNoFormat(this.list.get(
                    position).get("fileName")));
        } else {
            convertView = this.inflater.inflate(R.layout.weixin_news_item, null);
            holder.tv = (TextView) convertView.findViewById(R.id.news_title);
            holder.re = (TextView) convertView.findViewById(R.id.news_des);
            holder.ty = (TextView) convertView.findViewById(R.id.news_type);
            holder.head = (ImageView) convertView.findViewById(R.id.imageview);
            holder.url = this.list.get(position).get("HL:");
            holder.tv.setText(this.list.get(position).get("TI:"));
            holder.re.setText(this.list.get(position).get("EW:"));
            if (this.list.get(position).get("CL:").equalsIgnoreCase("1"))
                holder.ty.setText("（预）");
            else if (this.list.get(position).get("CL:").equalsIgnoreCase("2"))
                holder.ty.setText("（群）");
            this.imageLoader
                    .DisplayImage(this.list.get(position).get("PL:"), holder.head);
        }
        convertView.setTag(holder);
        return convertView;
    }

    public static class ViewHolder
    {
        public TextView tv;
        public TextView re;
        public TextView ty;
        public String url;
        public TextView group;
        public ImageView head;

    }
}