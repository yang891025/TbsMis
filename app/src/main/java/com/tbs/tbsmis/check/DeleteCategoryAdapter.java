package com.tbs.tbsmis.check;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.tbs.tbsmis.R;

import java.util.ArrayList;
import java.util.Map;

public class DeleteCategoryAdapter extends BaseAdapter {
	private final ArrayList<Map<String, String>> list;
	private LayoutInflater inflater;
	@SuppressLint("UseSparseArrays")
	public DeleteCategoryAdapter(ArrayList<Map<String, String>> list, Context context) {
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
		DeleteCategoryAdapter.ViewHolder holder = null;
		if (convertView == null) {
			// ���ViewHolder����
			holder = new DeleteCategoryAdapter.ViewHolder();
			// ���벼�ֲ���ֵ��convertview
			convertView = this.inflater.inflate(R.layout.offline_timer_item, null);
			holder.tv = (TextView) convertView.findViewById(R.id.txtTimerTime);
			holder.cb = (Button) convertView.findViewById(R.id.btnDelTimer);
			// Ϊview���ñ�ǩ
			convertView.setTag(holder);
		} else {
			// ȡ��holder
			holder = (DeleteCategoryAdapter.ViewHolder) convertView.getTag();
		}
		// ����list��TextView����ʾ
		holder.tv.setText(this.list.get(position).get("group"));
		// ���isSelected������checkbox��ѡ��״��
		holder.cb.setVisibility(View.GONE);
		return convertView;
	}

	public static class ViewHolder {
		public TextView tv;
		public Button cb;
	}
}