package com.tbs.tbsmis.source;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.file.FileIconHelper;
import com.tbs.tbsmis.file.FileInfo;
import com.tbs.tbsmis.file.Util;
import com.tbs.tbsmis.util.FileUtils;

import java.util.List;

// 完成gridview 数据到界面的适配
@SuppressLint("ResourceAsColor")
public class FileViewAdapter extends BaseAdapter
{
    private static final String TAG = "AppGridViewAdapter";
    private  Context context;
    LayoutInflater infalter;
    private  List<FileInfo> childs;
   // private  ImageLoader imageLoader;
    public FileViewAdapter(Context context, List<FileInfo> childs)
    {
        this.context = context;
        this.childs = childs;
        // 方法1 通过系统的service 获取到 试图填充器
        this.infalter = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // 方法2 通过layoutinflater的静态方法获取到 视图填充器
        // infalter = LayoutInflater.from(context);
        //this.imageLoader = new ImageLoader(context);
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
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        FileViewAdapter.ViewHolder holder = null;
        if (convertView == null)
        {
            // ���ViewHolder����
            holder = new FileViewAdapter.ViewHolder();
            // ���벼�ֲ���ֵ��convertview
            convertView = this.infalter.inflate(R.layout.file_card_view, null);
            // Ϊview���ñ�ǩ
            convertView.setTag(holder);
        } else
        {
            // ȡ��holder
            holder = (FileViewAdapter.ViewHolder) convertView.getTag();
        }
        ImageView img = (ImageView) convertView.findViewById(R.id.grid_item_img);
        ImageView delete_iv = (ImageView) convertView.findViewById(R.id.delete_iv);
        TextView tv = (TextView) convertView.findViewById(R.id.grid_item_subtitle);
        TextView grid_item_desc = (TextView) convertView.findViewById(R.id.grid_item_desc);
        //TextView grid_item_title = (TextView) convertView.findViewById(id.grid_item_title);
//        this.imageLoader.DisplayImage(childs.get(position).filePath,
//                img);
        FileIconHelper fileIconHelper = new FileIconHelper(context);
        fileIconHelper.setIcon(childs.get(position).filePath,img);
        tv.setText(this.childs.get(position).fileName);
        grid_item_desc.setText(Util.convertStorage(this.childs.get(position).fileSize));
//        String path = this.childs.get(position).filePath;
//        path= path.substring(0,path.lastIndexOf("/"));
//        path = path.substring(path.lastIndexOf("/")+1);
        //grid_item_title.setText("0观看");
        delete_iv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context).setMessage(context.getString(R.string.delete) + "' " + childs.get(position).fileName + " '").setCancelable(false)
                        .setNeutralButton(android.R
                                .string.ok, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String filePath = childs.get(position).filePath;
                                FileUtils.deleteFileWithPath(filePath);
                                childs.remove(position);
                                notifyDataSetChanged();
                            }
                        }).setNegativeButton(R.string.cancel, null).show();

            }
        });
        return convertView;
    }

    public static class ViewHolder
    {
        public String update;
        //public String downPath;
    }
}