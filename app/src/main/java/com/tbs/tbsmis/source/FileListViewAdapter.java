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
import com.tbs.tbsmis.file.FileInfo;
import com.tbs.tbsmis.util.FileUtils;

import java.io.File;
import java.util.List;

// 完成gridview 数据到界面的适配
@SuppressLint("ResourceAsColor")
public class FileListViewAdapter extends BaseAdapter
{
    private static final String TAG = "AppGridViewAdapter";
    private final Context context;
    LayoutInflater infalter;
    private final List<FileInfo> childs;

    public FileListViewAdapter(Context context, List<FileInfo> childs)
    {
        this.context = context;
        this.childs = childs;
        // 方法1 通过系统的service 获取到 试图填充器
        this.infalter = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // 方法2 通过layoutinflater的静态方法获取到 视图填充器
        // infalter = LayoutInflater.from(context);
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
        FileListViewAdapter.ViewHolder holder = null;
        if (convertView == null)
        {
            // ���ViewHolder����
            holder = new FileListViewAdapter.ViewHolder();
            // ���벼�ֲ���ֵ��convertview
            convertView = this.infalter.inflate(R.layout.main_menu_item, null);
            // Ϊview���ñ�ǩ
            convertView.setTag(holder);
        } else
        {
            // ȡ��holder
            holder = (FileListViewAdapter.ViewHolder) convertView.getTag();
        }
        ImageView img = (ImageView) convertView.findViewById(R.id.menu_image);
        TextView tv = (TextView) convertView.findViewById(R.id.menu_title);
        ImageView delete_iv = (ImageView) convertView.findViewById(R.id.delete_iv);
        img.setBackgroundResource(R.drawable.download_notification);
        tv.setText(this.childs.get(position).fileName);
        delete_iv.setVisibility(View.VISIBLE);
        delete_iv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context).setMessage(context.getString(R.string.delete) + "' " + childs.get(position).fileName + " '")
                        .setCancelable(false)
                        .setNeutralButton(android.R
                                .string.ok, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                    File file = new File(childs.get(position).filePath);
                                    if (file.exists() && file.isDirectory()) {
                                        FileUtils.deleteDirectory(childs.get(position).filePath);
                                    }
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
        public String downPath;
    }
}