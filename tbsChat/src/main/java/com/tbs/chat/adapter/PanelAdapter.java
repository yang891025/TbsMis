package com.tbs.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbs.chat.R;

public class PanelAdapter extends BaseAdapter{

	private Context mContext;
	private int[] ivArray = { R.drawable.app_panel_expression_icon,
			R.drawable.app_panel_pic_icon, R.drawable.app_panel_video_icon,
			R.drawable.app_panel_location_icon,
			R.drawable.app_panel_friendcard_icon };
	
	private String[] strArray = {"表情","图片","视频","位置","名片"};

	public PanelAdapter(Context mContext) {
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		return ivArray.length;
	}

	@Override
	public Object getItem(int position) {
		return ivArray[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int drawable = ivArray[position];
		String name = strArray[position];
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.app_grid_item, null);
			viewHolder = new ViewHolder();
			viewHolder.app_grid_item_icon = (ImageView) convertView.findViewById(R.id.app_grid_item_icon);
			viewHolder.app_grid_item_icon_mask = (ImageView) convertView.findViewById(R.id.app_grid_item_icon_mask);
			viewHolder.app_grid_item_name = (TextView) convertView.findViewById(R.id.app_grid_item_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.app_grid_item_icon.setImageResource(drawable);
		viewHolder.app_grid_item_name.setText(name);
		return convertView;
	}

	class ViewHolder {
		ImageView app_grid_item_icon;// 国家
		ImageView app_grid_item_icon_mask;// 号码
		TextView app_grid_item_name;// 目录
	}
}