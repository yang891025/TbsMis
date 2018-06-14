package com.tbs.tbsmis.check;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.headergridview.selectbuttonaction.StickyGridItem;
import com.tbs.tbsmis.headergridview.stickygridheaders.StickyGridHeadersSimpleAdapter;

import java.util.List;
import java.util.Map;

// 完成gridview 数据到界面的适配
@SuppressLint("ResourceAsColor")
public class DefaultMenuGridViewAdapter extends BaseAdapter implements StickyGridHeadersSimpleAdapter
{
    private static final String TAG = "DefaultMenuGridViewAdapter";
    private final Context context;
    LayoutInflater infalter;
    private final List<StickyGridItem> childs;
    private Map<Integer, Boolean> mSelectMap;

    public DefaultMenuGridViewAdapter(Context context, List<StickyGridItem> childs, Map<Integer, Boolean>
            mSelectMap) {
        this.context = context;
        this.childs = childs;
        this.mSelectMap = mSelectMap;
        // 方法1 通过系统的service 获取到 试图填充器
        this.infalter = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // 方法2 通过layoutinflater的静态方法获取到 视图填充器
        // infalter = LayoutInflater.from(context);
    }

    // 返回gridview里面有多少个条目
    @Override
    public int getCount() {
        return this.childs.size();
    }

    // 返回某个position对应的条目
    @Override
    public Object getItem(int position) {
        return childs.get(position);
    }

    // 返回某个position对应的id
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 返回某个位置对应的视图
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder = new ViewHolder();
        GridItem item = new GridItem(context, null, childs.get(position));
//        item.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));
        item.setChecked(mSelectMap.get(position) == null ? false
                : mSelectMap.get(position));
        convertView = item;
        convertView.setTag(mViewHolder);
        return convertView;
    }
    public static class ViewHolder
    {
        //public MyImageView mImageView;
    }
    @Override
    public long getHeaderId(int position) {
        return childs.get(position).getSection();
    }

    public static class HeaderViewHolder
    {
        public TextView mTextView;
    }
    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder mHeaderHolder;
        if (convertView == null) {
            mHeaderHolder = new HeaderViewHolder();
            convertView = infalter.inflate(R.layout.select_tag_item, parent, false);
            mHeaderHolder.mTextView = (TextView) convertView
                    .findViewById(R.id.tag);
        } else {
            mHeaderHolder = (HeaderViewHolder) convertView.getTag();
        }
        mHeaderHolder.mTextView.setText(childs.get(position).getAction());
        convertView.setTag(mHeaderHolder);
        return convertView;
    }
}