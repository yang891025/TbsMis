package com.tbs.tbsmis.check;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.util.ImageLoader;

import java.util.List;

/**
 * Created by TBS on 2016/3/9.
 */
public class PicGridViewAdapter extends BaseAdapter
{
    private static  String TAG = "AppGridViewAdapter";
    private final Context context;
    LayoutInflater infalter;
    private  List<String> childs;
    private  ImageLoader imageLoader;

    public PicGridViewAdapter(Context context, List<String> childs)
    {
        this.context = context;
        this.childs = childs;
        // 方法1 通过系统的service 获取到 试图填充器
        this.infalter = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // 方法2 通过layoutinflater的静态方法获取到 视图填充器
        // infalter = LayoutInflater.from(context);
        this.imageLoader = new ImageLoader(context,R.drawable.clean_category_thumbnails);
    }

    // 返回gridview里面有多少个条目
    @Override
    public int getCount()
    {
        return this.childs.size();
    }

    // 返回某个position对应的条目
    @Override
    public Object getItem(int position)
    {
        return position;
    }

    // 返回某个position对应的id
    @Override
    public long getItemId(int position)
    {
        return position;
    }

    // 返回某个位置对应的视图
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        PicGridViewAdapter.ViewHolder holder = null;
        if (convertView == null)
        {
            // ���ViewHolder����
            holder = new PicGridViewAdapter.ViewHolder();
            // ���벼�ֲ���ֵ��convertview
            convertView = this.infalter.inflate(R.layout.button_drawable_item, null);
            // Ϊview���ñ�ǩ
            convertView.setTag(holder);
        } else
        {
            // ȡ��holder
            holder = (PicGridViewAdapter.ViewHolder) convertView.getTag();
        }
        ImageView iv = (ImageView) convertView.findViewById(R.id.button_imageview);
        holder.downPath = this.childs.get(position);
        this.imageLoader.DisplayImage(this.childs.get(position),
                iv);
        //System.out.println("picUrl = "+ childs.get(position));
        //iv.setImageResource(R.drawable.default_pic);
        //tv.setText(childs.get(position).get("child"));
        return convertView;
    }



    public static class ViewHolder
    {
        public String downPath;
    }
}
