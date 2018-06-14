package com.tbs.tbsmis.check;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.database.dao.DBUtil;
import com.tbs.tbsmis.entity.WXTextEntity;
import com.tbs.tbsmis.util.ImageLoader;
import com.tbs.tbsmis.util.StringUtils;

import java.util.ArrayList;

public class WeChatMessageAdapter extends BaseAdapter {
	// �����ݵ�list
	private  ArrayList<WXTextEntity> list;
	private WXTextEntity message;
	private LayoutInflater inflater;
	private  Context context;
	private  ImageLoader imageLoader;
	private DBUtil dao;

	// ������
	@SuppressLint("UseSparseArrays")
	public WeChatMessageAdapter(ArrayList<WXTextEntity> list, Context context,
			DBUtil dao) {
		this.context = context;
		this.list = list;
		this.dao = dao;
        this.imageLoader = new ImageLoader(context,R.drawable.default_avatar);
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
		WeChatMessageAdapter.ViewHolder holder = null;
		if (convertView == null) {
			// ���ViewHolder����
			holder = new WeChatMessageAdapter.ViewHolder();
			// ���벼�ֲ���ֵ��convertview
			convertView = this.inflater.inflate(R.layout.weixin_user_item, null);
			holder.tv = (TextView) convertView.findViewById(R.id.name);
			holder.re = (TextView) convertView.findViewById(R.id.message);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.head = (ImageView) convertView.findViewById(R.id.imageview);
			// Ϊview���ñ�ǩ
			convertView.setTag(holder);
		} else {
			// ȡ��holder
			holder = (WeChatMessageAdapter.ViewHolder) convertView.getTag();
		}
        this.message = this.list.get(position);
		String self = this.message.getToUserName();
		String user = this.message.getFromUserName();
		// ����list��TextView����ʾ
		holder.tv.setText(this.message.getNickname());
		holder.re.setText(this.message.getContent());
		holder.openId = user;
		holder.touserId = self;
		holder.msgid = this.message.getMsgId();
		holder.time.setText(StringUtils.friendly_time(StringUtils
				.LongtoDate(this.message.getCreateTime() + "000")));

        this.imageLoader.DisplayImage(this.message.getHeadimgurl(), holder.head);
		return convertView;
	}

	public static class ViewHolder {
		public TextView tv;
		public TextView re;
		public TextView time;
		public ImageView head;
		public String openId;
		public String touserId;
		public long msgid;
	}
}