package com.tbs.tbsmis.city;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tbs.tbsmis.R;

import java.util.List;


public class CityAdapter extends BaseAdapter {
	// 首字母集
	private final List<String> mCities;
	private final LayoutInflater inflater;

	public CityAdapter(Context context, List<String> cities) {
		// TODO Auto-generated constructor stub
        this.inflater = LayoutInflater.from(context);
        this.mCities = cities;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.mCities.size();
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = this.inflater.inflate(R.layout.select_city_item, null);
		}
		TextView city = (TextView) convertView.findViewById(R.id.column_title);
		city.setText(this.mCities.get(position).toString());
		return convertView;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return this.mCities.get(position).toString();
	}
}
