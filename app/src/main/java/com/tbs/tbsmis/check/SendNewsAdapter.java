package com.tbs.tbsmis.check;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import com.tbs.tbsmis.entity.NewsEntity;
import com.tbs.tbsmis.util.ImageLoader;
import com.tbs.tbsmis.util.StringUtils;

public class SendNewsAdapter extends BaseAdapter {
	// �����ݵ�list
	private  ArrayList<NewsEntity> list;
	private LayoutInflater inflater;
	private  Context context;
	public HashMap<Integer, Integer> visiblecheck;// 用来记录是否显示checkBox
	public HashMap<Integer, Boolean> ischeck;
	private final boolean isMulChoice; // 是否多选
	private  ImageLoader imageLoader;

	// ������
	@SuppressLint("UseSparseArrays")
	public SendNewsAdapter(boolean isMulChoice, ArrayList<NewsEntity> list,
			Context context) {
		this.context = context;
		this.list = list;
		this.isMulChoice = isMulChoice;
        this.imageLoader = new ImageLoader(context,R.drawable.clean_category_thumbnails);
        this.inflater = LayoutInflater.from(context);
        this.visiblecheck = new HashMap<Integer, Integer>();
        this.ischeck = new HashMap<Integer, Boolean>();
		if (isMulChoice) {
			for (int i = 0; i < list.size(); i++) {
                this.ischeck.put(i, false);
                this.visiblecheck.put(i, View.VISIBLE);
			}
		} else {
			for (int i = 0; i < list.size(); i++) {
                this.ischeck.put(i, false);
                this.visiblecheck.put(i, View.INVISIBLE);
			}
		}
	}

	@Override
	public int getCount() {
		return this.list.size();
	}

	public void setChecked(int position, boolean isCheck) {
        this.ischeck.put(position, isCheck);
	}
	public List<String> getChecked() {
		List<String> chenckeb= new ArrayList<String>();
		for(int i = 0; i< this.ischeck.size(); i++){
			if(this.ischeck.get(i)){
				chenckeb.add((i-1)+"");
			}
		}
		return chenckeb;
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
		SendNewsAdapter.ViewHolder holder = null;
		if (convertView == null) {
			// ���ViewHolder����
			holder = new SendNewsAdapter.ViewHolder();
			// convertview
			convertView = this.inflater.inflate(R.layout.send_news_item, null);
			holder.news_title = (TextView) convertView
					.findViewById(R.id.news_title);
			holder.news_content = (TextView) convertView
					.findViewById(R.id.news_content);
			holder.new_pic = (ImageView) convertView.findViewById(R.id.new_pic);
			holder.news_check = (CheckBox) convertView
					.findViewById(R.id.news_check);
			// Ϊview���ñ�ǩ
			convertView.setTag(holder);
		} else {
			// ȡ��holder
			holder = (SendNewsAdapter.ViewHolder) convertView.getTag();
		}
		// ����list��TextView����ʾ
		holder.news_check.setChecked(this.ischeck.get(position));
		holder.news_check.setVisibility(this.visiblecheck.get(position));
		holder.news_title.setText(this.list.get(position).getTitle());
		holder.news_content.setText(this.list.get(position).getDate());
		holder.content_source_url = this.list.get(position).getContentUrl();
		holder.fileName = this.list.get(position).getFileName();
		holder.index = this.list.get(position).getNewsCount();
		if (!StringUtils.isEmpty(this.list.get(position).getPicUrl())) {
            this.imageLoader.DisplayImage(this.list.get(position).getPicUrl(),
					holder.new_pic);
		} else {

		}

		return convertView;
	}

	public static class ViewHolder {
		public TextView news_title;
		public TextView news_content;
		public String content_source_url;
		public CheckBox news_check;
		public ImageView new_pic;
		public String fileName;
		public long index;
	}
}