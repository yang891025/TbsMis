package com.tbs.tbsmis.check;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.util.ImageLoader;

import java.util.List;
import java.util.Map;

/**
 * Created by TBS on 2016/4/18.
 */
public class TemplateViewAdapter extends BaseAdapter
{
    private static  String TAG = "AppGridViewAdapter";
    private  Context context;
    LayoutInflater infalter;
    private  List<Map<String, String>> childs;
    private  ImageLoader imageLoader;

    public TemplateViewAdapter(Context context, List<Map<String, String>> childs)
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
        TemplateViewAdapter.ViewHolder holder = null;
        if (convertView == null)
        {
            // ���ViewHolder����
            holder = new TemplateViewAdapter.ViewHolder();
            // ���벼�ֲ���ֵ��convertview
            convertView = this.infalter.inflate(R.layout.template_item, null);
            // Ϊview���ñ�ǩ
            convertView.setTag(holder);
        } else
        {
            // ȡ��holder
            holder = (TemplateViewAdapter.ViewHolder) convertView.getTag();
        }
        ImageView iv = (ImageView) convertView.findViewById(R.id.button_imageview);
        TextView text = (TextView) convertView.findViewById(R.id.template_text);
        holder.downPath = this.childs.get(position).get("pic");
        this.imageLoader.DisplayImage( holder.downPath,
                iv);
        text.setText(this.childs.get(position).get("name"));
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
