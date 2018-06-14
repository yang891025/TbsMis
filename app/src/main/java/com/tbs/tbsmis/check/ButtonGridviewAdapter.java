package com.tbs.tbsmis.check;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbs.tbsmis.R;

// 完成gridview 数据到界面的适配 
@SuppressLint("ResourceAsColor")
public class ButtonGridviewAdapter extends BaseAdapter {
	private static final String TAG = "ButtonGridviewAdapter";
	private final Context context;
	LayoutInflater infalter;
	private final int[] childs;
	private final int count;

	public ButtonGridviewAdapter(Context context, int[] childs, int count) {
		this.childs = new int[childs.length];
		this.context = context;
		this.count = count;
		for (int i = 0; i < childs.length; i++) {
			this.childs[i] = childs[i];
		}
		// 方法1 通过系统的service 获取到 试图填充器
        this.infalter = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	// 返回gridview里面有多少个条目
	@Override
	public int getCount() {
		return this.childs.length;
	}

	// 返回某个position对应的条目
	@Override
	public Object getItem(int position) {
		return position;
	}

	// 返回某个position对应的id
	@Override
	public long getItemId(int position) {
		return position;
	}

	// 返回某个位置对应的视图
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.i(ButtonGridviewAdapter.TAG, "GETVIEW " + position);
		Log.i(ButtonGridviewAdapter.TAG, "GETVIEW " + this.count);
		// 把一个布局文件转换成视图
		View view = this.infalter.inflate(R.layout.button_drawable_item, null);
		ImageView iv = (ImageView) view.findViewById(R.id.button_imageview);
		TextView tv = (TextView) view.findViewById(R.id.button_now);
		// 设置每一个item的名字和图标
		iv.setBackgroundResource(this.childs[position]);
		if(this.count == position){
			tv.setText("当前使用");
		}
		return view;
	}
}