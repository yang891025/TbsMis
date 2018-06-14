package com.tbs.tbsmis.check;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.StringUtils;

import java.util.ArrayList;
import java.util.Map;

public class PopMenuAdapter extends BaseAdapter
{
    private final ArrayList<Map<String, String>> list;
    private LayoutInflater inflater;

    @SuppressLint("UseSparseArrays")
    public PopMenuAdapter(ArrayList<Map<String, String>> list, Context context) {
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
        PopMenuAdapter.ViewHolder holder = null;
        if (convertView == null) {
            holder = new PopMenuAdapter.ViewHolder();
            convertView = this.inflater.inflate(R.layout.main_menu_item, null);
            holder.tv = (TextView) convertView.findViewById(R.id.menu_title);
            holder.ImageView = (ImageView) convertView
                    .findViewById(R.id.menu_image);
            holder.menu_bk = (LinearLayout) convertView
                    .findViewById(R.id.menu_bk);
            convertView.setTag(holder);
        } else {
            holder = (PopMenuAdapter.ViewHolder) convertView.getTag();
        }
        holder.tv.setText(this.list.get(position).get("Title"));
        if (position == 0) {
            holder.menu_bk.setBackgroundResource(R.drawable.more_up);
        } else if (position == this.getCount() - 1) {
            holder.menu_bk.setBackgroundResource(R.drawable.more_down);
        } else {
            holder.menu_bk.setBackgroundResource(R.drawable.more_middle);
        }
        if (!StringUtils.isEmpty(this.list.get(position).get("Icon"))) {
            holder.ImageView
                    .setBackgroundResource(constants.MenuButtonIcoId[Integer
                            .parseInt(this.list.get(position).get("Icon"))]);
        }
        return convertView;
    }

    public static class ViewHolder
    {
        public TextView tv;
        public ImageView ImageView;
        public LinearLayout menu_bk;

    }
}