package com.tbs.tbsmis.check;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.UIHelper;

import java.util.ArrayList;

public class TimerMyAdapter extends BaseAdapter {
	private final ArrayList<String> list;
	private LayoutInflater inflater;
	private final Context context;
	private IniFile m_iniFileIO;

	@SuppressLint("UseSparseArrays")
	public TimerMyAdapter(ArrayList<String> list, Context context) {
		this.list = list;
        this.inflater = LayoutInflater.from(context);
		this.context = context;
		// list.add("8:30-11:30");
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
		TimerMyAdapter.ViewHolder holder = null;
		if (convertView == null) {
			// ���ViewHolder����
			holder = new TimerMyAdapter.ViewHolder();
			// ���벼�ֲ���ֵ��convertview
			convertView = this.inflater.inflate(R.layout.offline_timer_item, null);
			holder.tv = (TextView) convertView.findViewById(R.id.txtTimerTime);
			holder.cb = (Button) convertView.findViewById(R.id.btnDelTimer);
			// Ϊview���ñ�ǩ
			convertView.setTag(holder);
		} else {
			// ȡ��holder
			holder = (TimerMyAdapter.ViewHolder) convertView.getTag();
		}
		// ����list��TextView����ʾ
		holder.tv.setText(this.list.get(position));
		// ���isSelected������checkbox��ѡ��״��
		holder.cb.setTag(position);
		holder.cb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int position = Integer.parseInt(v.getTag().toString());
                TimerMyAdapter.this.delTime(position + 1);
                TimerMyAdapter.this.list.remove(position);
                notifyDataSetChanged();
				Intent intent = new Intent();
				intent.setAction("Offline"+ TimerMyAdapter.this.context.getString(R.string.about_title));
				intent.putExtra("flag", 2);
                TimerMyAdapter.this.context.sendBroadcast(intent);
			}
		});
		return convertView;
	}

	public static class ViewHolder {
		public TextView tv;
		public Button cb;
	}

	public void delTime(int position) {
        this.m_iniFileIO = new IniFile();
		String webRoot = UIHelper.getSoftPath(context);
		if (webRoot.endsWith("/") == false) {
			webRoot += "/";
		}
		webRoot += this.context.getString(R.string.SD_CARD_TBSAPP_PATH2);
		webRoot = UIHelper.getShareperference(this.context,
				constants.SAVE_INFORMATION, "Path", webRoot);
		if (webRoot.endsWith("/") == false) {
			webRoot += "/";
		}
		String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
		String appTestFile = webRoot
				+ this.m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
						constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
		int TimeNumber = Integer.parseInt(this.m_iniFileIO.getIniString(appTestFile,
				"CheckTime", "TimeNumber", "0", (byte) 0));
		for (int i = 1; i <= TimeNumber; i++) {
			String key = String.valueOf(i);
			if (i == position) {
                this.m_iniFileIO.deleteIniString(appTestFile, "CheckTime",
						"StartTime" + key);
                this.m_iniFileIO.deleteIniString(appTestFile, "CheckTime", "EndTime"
						+ key);
			} else if (i > position) {
				String midkey = String.valueOf(i - 1);
				String starTime = this.m_iniFileIO.getIniString(appTestFile,
						"CheckTime", "StartTime" + key, "0", (byte) 0);
                this.m_iniFileIO.writeIniString(appTestFile, "CheckTime",
						"StartTime" + midkey, starTime);
				String endTime = this.m_iniFileIO.getIniString(appTestFile,
						"CheckTime", "EndTime" + key, "0", (byte) 0);
                this.m_iniFileIO.writeIniString(appTestFile, "CheckTime", "EndTime"
						+ midkey, endTime);
                this.m_iniFileIO.deleteIniString(appTestFile, "CheckTime",
						"StartTime" + key);
                this.m_iniFileIO.deleteIniString(appTestFile, "CheckTime", "EndTime"
						+ key);
			}
		}
		String key = String.valueOf(TimeNumber - 1);
        this.m_iniFileIO.writeIniString(appTestFile, "CheckTime", "TimeNumber", key);
	}
}