package com.tbs.tbsmis.check;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.file.FileIconHelper;
import com.tbs.tbsmis.file.Util;
import com.tbs.tbsmis.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApkChooseAdapter extends BaseAdapter {
    private final List<String> list;
    private LayoutInflater inflater;
    private final Context context;
    private HashMap<Integer, Boolean> isSelected;

    @SuppressLint("UseSparseArrays")
    public ApkChooseAdapter(List<String> list, Context context) {
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.isSelected = new HashMap<Integer, Boolean>();
        // 初始化数据
        this.initDate();
    }

    // 初始化isSelected的数据
    private void initDate() {
        for (int i = 0; i < this.list.size(); i++) {
            this.getIsSelected().put(i, false);
        }
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        UpFileAdapter.ViewHolder holder = null;
        if (convertView == null) {
            // ���ViewHolder����
            holder = new UpFileAdapter.ViewHolder();
            // ���벼�ֲ���ֵ��convertview
            convertView = this.inflater.inflate(R.layout.children_item, null);
            holder.tv = (TextView) convertView.findViewById(R.id.tvPath);
            holder.size = (TextView) convertView.findViewById(R.id.file_size);
            holder.count = (TextView) convertView.findViewById(R.id.file_count);
            holder.time = (TextView) convertView
                    .findViewById(R.id.modified_time);
            holder.tvImage = (ImageView) convertView.findViewById(R.id.tvImage);
            holder.children_cb = (CheckBox) convertView
                    .findViewById(R.id.children_cb);

            convertView.setTag(holder);
        } else {
            // ȡ��holder
            holder = (UpFileAdapter.ViewHolder) convertView.getTag();
        }
        File pathFile = new File(this.list.get(position));

        holder.time.setText(Util.formatDateString(this.context,
                pathFile.lastModified()));
        if (pathFile.isFile()) {
            holder.tvImage.setImageResource(FileIconHelper.getFileIcon(FileUtils.getFileFormat(pathFile.getName())));
            holder.size.setText(Util.convertStorage(pathFile.length()));
            holder.children_cb.setVisibility(View.VISIBLE);
        } else if (pathFile.isDirectory()) {
            int lCount = 0;
            File[] files = pathFile.listFiles();
            if (files != null) {
                for (File child : files) {
                    lCount++;
                }
                holder.tvImage.setImageResource(R.drawable.format_folder);
                holder.count.setText("(" + lCount + ")");
                //holder.children_cb.setVisibility(View.GONE);
            } else {
                holder.tvImage.setImageResource(R.drawable.format_folder);
                //holder.children_cb.setVisibility(View.GONE);
            }
            holder.children_cb.setVisibility(View.GONE);
        }
        // 根据isSelected来设置checkbox的选中状况
        holder.children_cb.setChecked(this.getIsSelected().get(position));
        holder.children_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ApkChooseAdapter.this.isSelected.get(position)){
                    ApkChooseAdapter.this.isSelected.put(position, false);
                }else{
                    ApkChooseAdapter.this.isSelected.put(position, true);
                }
                ApkChooseAdapter.this.notifyDataSetChanged();
            }
        });
        // ����list��TextView����ʾ
        holder.tv.setText(FileUtils.getFileName(this.list.get(position)));
        return convertView;
    }

    public HashMap<Integer, Boolean> getIsSelected() {
        return this.isSelected;
    }

    public List<String> getSelected() {
        List<String> Checklist = new ArrayList<String>();
        for (int i = 0; i < this.list.size(); i++) {
            if (this.isSelected.get(i)) {
                Checklist.add(this.list.get(i));
            }
        }
        return Checklist;
    }

    public void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        this.isSelected = isSelected;
    }

    public static class ViewHolder {
        public TextView tv;
        public TextView count;
        public TextView size;
        public TextView time;
        public ImageView tvImage;
        public CheckBox children_cb;
    }
}