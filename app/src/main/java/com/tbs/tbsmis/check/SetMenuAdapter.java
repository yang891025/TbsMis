package com.tbs.tbsmis.check;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tbs.tbsmis.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SetMenuAdapter extends BaseAdapter {
	private final ArrayList<Map<String, String>> list;
	private LayoutInflater inflater;
	private final Context context;
	private HashMap<Integer, Boolean> isSelected;

	@SuppressLint("UseSparseArrays")
	public SetMenuAdapter(ArrayList<Map<String, String>> list, Context context) {
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
			if (this.list.get(i).get("Check").equals("1"))
                this.getIsSelected().put(i, true);
			else
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
	public View getView(int position, View convertView, ViewGroup parent) {
		SetMenuAdapter.ViewHolder holder = null;
		if (convertView == null) {
			// ���ViewHolder����
			holder = new SetMenuAdapter.ViewHolder();
			// ���벼�ֲ���ֵ��convertview
			convertView = this.inflater.inflate(R.layout.offline_manage_item, null);
			holder.tv = (TextView) convertView.findViewById(R.id.topicName);
			holder.size = (TextView) convertView.findViewById(R.id.resName);
			holder.children_cb = (CheckBox) convertView
					.findViewById(R.id.chxItem);
			holder.size.setVisibility(View.VISIBLE);
			holder.children_cb.setClickable(false);
			holder.children_cb.setFocusable(false);
			// Ϊview���ñ�ǩ
			convertView.setTag(holder);
		} else {
			// ȡ��holder
			holder = (SetMenuAdapter.ViewHolder) convertView.getTag();
		}
		// 根据isSelected来设置checkbox的选中状况
		holder.children_cb.setChecked(this.getIsSelected().get(position));
		// ����list��TextView����ʾ
		holder.size.setText("(" + this.list.get(position).get("ID") + ")");
		holder.tv.setText(this.list.get(position).get("Title"));
		return convertView;
	}

	public HashMap<Integer, Boolean> getIsSelected() {
		return this.isSelected;
	}

	public ArrayList<Map<String, String>> getSelected() {
		ArrayList<Map<String, String>> Checklist = new ArrayList<Map<String, String>>();
		for (int i = 0; i < this.list.size(); i++) {
			if (this.isSelected.get(i)) {
				Checklist.add(this.list.get(i));
			}
		}
		return Checklist;
	}

	public int countSelected() {
		int Checklist = 0;
		for (int i = 0; i < this.list.size(); i++) {
			if (this.isSelected.get(i)) {
				Checklist++;
			}
		}
		return Checklist;
	}

	public void setIsSelected(HashMap<Integer, Boolean> isSelected) {
		this.isSelected = isSelected;
	}

	public static class ViewHolder {
		public TextView tv;
		public TextView size;
		public CheckBox children_cb;
	}
}