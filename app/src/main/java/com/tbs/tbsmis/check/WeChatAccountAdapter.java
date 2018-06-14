package com.tbs.tbsmis.check;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tbs.tbsmis.R;

import java.util.ArrayList;
import java.util.HashMap;

public class WeChatAccountAdapter extends BaseAdapter {
	// �����ݵ�list
	private final ArrayList<HashMap<String, String>> list;
	private LayoutInflater inflater;
	private final Context context;

	// ������
	@SuppressLint("UseSparseArrays")
	public WeChatAccountAdapter(ArrayList<HashMap<String, String>> list,
			Context context) {
		this.context = context;
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
		WeChatAccountAdapter.ViewHolder holder = null;
		if (convertView == null) {
			// ���ViewHolder����
			holder = new WeChatAccountAdapter.ViewHolder();
			// ���벼�ֲ���ֵ��convertview
			convertView = this.inflater.inflate(R.layout.weixin_user_item, null);
			holder.tv = (TextView) convertView.findViewById(R.id.name);

			convertView.setTag(holder);
		} else {
			// ȡ��holder
			holder = (WeChatAccountAdapter.ViewHolder) convertView.getTag();
		}
		// ����list��TextView����ʾ
		holder.tv.setText(this.list.get(position).get("name"));
		holder.svrPath = this.list.get(position).get("svrPath");
		holder.iniPath = this.list.get(position).get("iniPath");
		holder.category = this.list.get(position).get("category");
		holder.newsUrl = this.list.get(position).get("newsUrl");
		return convertView;
	}

	public static class ViewHolder {
		public TextView tv;
		public String svrPath;
		public String iniPath;
		public String newsUrl;
		public String category;
	}
}