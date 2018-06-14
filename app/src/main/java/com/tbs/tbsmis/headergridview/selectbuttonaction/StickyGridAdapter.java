package com.tbs.tbsmis.headergridview.selectbuttonaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.headergridview.stickygridheaders.StickyGridHeadersSimpleAdapter;

import java.util.List;

public class StickyGridAdapter extends BaseAdapter implements StickyGridHeadersSimpleAdapter
{

    private List<StickyGridItem> list;
    private LayoutInflater mInflater;

    public StickyGridAdapter(Context context, List<StickyGridItem> list) {
        this.list = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        mViewHolder = new ViewHolder();
        convertView = mInflater.inflate(R.layout.select_menu_item, parent, false);
        TextView menu_title = (TextView) convertView.findViewById(R.id.menu_title);
        TextView menu_url = (TextView) convertView.findViewById(R.id.menu_url);
        menu_title.setText(list.get(position).getName());
        String url = list.get(position).getPath();
        if (url.contains(":"))
            menu_url.setText(url.substring(url.lastIndexOf(":") + 1));
        else {
            menu_url.setText(url);
        }
        convertView.setTag(mViewHolder);
        return convertView;
    }


    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder mHeaderHolder;
        if (convertView == null) {
            mHeaderHolder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.select_tag_item, parent, false);
            mHeaderHolder.mTextView = (TextView) convertView
                    .findViewById(R.id.tag);

        } else {
            mHeaderHolder = (HeaderViewHolder) convertView.getTag();
        }
        mHeaderHolder.mTextView.setText(list.get(position).getAction());
        convertView.setTag(mHeaderHolder);
        return convertView;
    }

    public static class ViewHolder
    {
        //public MyImageView mImageView;
    }

    public static class HeaderViewHolder
    {
        public TextView mTextView;
    }

    @Override
    public long getHeaderId(int position) {
        return list.get(position).getSection();
    }

}
