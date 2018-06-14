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
import com.tbs.tbsmis.util.ImageLoader;
import com.tbs.tbsmis.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class NotificationAdapter extends BaseAdapter
{
    // �����ݵ�list
    private final ArrayList<HashMap<String, String>> list;
    private LayoutInflater inflater;
    // private List<String> listTag;
    private final Context context;
    private final ImageLoader imageLoader;

    // ������
    @SuppressLint("UseSparseArrays")
    public NotificationAdapter(ArrayList<HashMap<String, String>> list,
                               Context context) {
        this.context = context;
        this.list = list;
        this.imageLoader = new ImageLoader(context,R.drawable.default_avatar);
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
        NotificationAdapter.ViewHolder holder = null;

        // ���ViewHolder����
        holder = new NotificationAdapter.ViewHolder();
        // ���벼�ֲ���ֵ��convertview
        convertView = this.inflater.inflate(R.layout.weixin_news_item, null);
        holder.tv = (TextView) convertView.findViewById(R.id.news_title);
        holder.re = (TextView) convertView.findViewById(R.id.news_des);
        holder.ty = (TextView) convertView.findViewById(R.id.news_type);
        holder.time = (TextView) convertView.findViewById(R.id.news_time);
        holder.head = (ImageView) convertView.findViewById(R.id.imageview);
        holder.url = this.list.get(position).get("url:");
        holder.tv.setText(this.list.get(position).get("title:"));
        holder.re.setText(this.list.get(position).get("msg:"));
        holder.time.setText(StringUtils.friendly_time(this.list.get(position).get("time:")));
//            if (list.get(position).get("CL:").equalsIgnoreCase("1"))
//                holder.ty.setText("（预）");
//            else if (list.get(position).get("CL:").equalsIgnoreCase("2"))
//                holder.ty.setText("（群）");
//            imageLoader
//                    .DisplayImage(list.get(position).get("PL:"), holder.head);
        convertView.setTag(holder);
        return convertView;
    }

    public static class ViewHolder
    {
        public TextView tv;
        public TextView re;
        public TextView ty;
        public String url;
        public TextView time;
        public ImageView head;

    }
}