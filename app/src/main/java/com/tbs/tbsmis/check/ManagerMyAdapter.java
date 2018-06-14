package com.tbs.tbsmis.check;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ManagerMyAdapter extends BaseAdapter {
	// �����ݵ�list
	private final ArrayList<HashMap<String, String>> list;
	private LayoutInflater inflater;
	private final String webRoot;
	private final IniFile m_iniFileIO;

	// ������
	@SuppressLint("UseSparseArrays")
	public ManagerMyAdapter(ArrayList<HashMap<String, String>> list,
			Context context, String webRoot) {
		this.list = list;
		this.webRoot = webRoot;
        this.inflater = LayoutInflater.from(context);
        this.m_iniFileIO = new IniFile();
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
		ManagerMyAdapter.ViewHolder holder = null;
		if (convertView == null) {
			// ���ViewHolder����
			holder = new ManagerMyAdapter.ViewHolder();
			// ���벼�ֲ���ֵ��convertview
			convertView = this.inflater.inflate(R.layout.offline_manage_item, null);
			holder.tv = (TextView) convertView.findViewById(R.id.topicName);
			holder.re = (TextView) convertView.findViewById(R.id.resName);
			holder.cb = (CheckBox) convertView.findViewById(R.id.chxItem);
			// Ϊview���ñ�ǩ
			convertView.setTag(holder);
		} else {
			// ȡ��holder
			holder = (ManagerMyAdapter.ViewHolder) convertView.getTag();
		}
		// ����list��TextView����ʾ
		//String ResName = list.get(position).get("ResName").toString();
		holder.tv.setText(this.list.get(position).get("AppName").toString());
		holder.re.setText(this.list.get(position).get("ResName").toString());
		// ���isSelected������checkbox��ѡ��״��
		if (Integer.parseInt(this.m_iniFileIO.getIniString(this.webRoot, "WEBDATA",
                this.list.get(position).get("ResName").toString(), "0", (byte) 0)) == 1) {
			holder.cb.setChecked(true);
		} else {
			holder.cb.setChecked(false);
		}
		return convertView;
	}

	public static class ViewHolder {
		public TextView tv;
		public TextView re;
		public CheckBox cb;
	}
}