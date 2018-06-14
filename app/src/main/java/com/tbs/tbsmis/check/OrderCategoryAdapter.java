package com.tbs.tbsmis.check;

import android.annotation.SuppressLint;
import android.content.Context;
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
import java.util.Map;

public class OrderCategoryAdapter extends BaseAdapter {
	private  ArrayList<Map<String, String>> list;
	private LayoutInflater inflater;
	private  IniFile IniFile;
	private  String userIni;

	@SuppressLint("UseSparseArrays")
	public OrderCategoryAdapter(ArrayList<Map<String, String>> list,
			Context context) {
		this.list = list;
        this.IniFile = new IniFile();
		String webRoot = UIHelper.getShareperference(context,
				constants.SAVE_INFORMATION, "Path", "");
		if (webRoot.endsWith("/") == false) {
			webRoot += "/";
		}
		String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String appIniFile = webRoot
				+ IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
						constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        userIni = appIniFile;
        if (Integer.parseInt(IniFile.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = context.getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
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
		OrderCategoryAdapter.ViewHolder holder = null;
		if (convertView == null) {
			// ���ViewHolder����
			holder = new OrderCategoryAdapter.ViewHolder();
			// ���벼�ֲ���ֵ��convertview
			convertView = this.inflater.inflate(R.layout.offline_timer_item, null);
			holder.tv = (TextView) convertView.findViewById(R.id.txtTimerTime);
			holder.cb = (Button) convertView.findViewById(R.id.btnDelTimer);
			// Ϊview���ñ�ǩ
			convertView.setTag(holder);
		} else {
			// ȡ��holder
			holder = (OrderCategoryAdapter.ViewHolder) convertView.getTag();
		}
		// ����list��TextView����ʾ
		holder.tv.setText(this.list.get(position).get("group"));
		// ���isSelected������checkbox��ѡ��״��
		if (position == 0) {
			holder.cb.setText("下移");
		} else {
			holder.cb.setText("上移");
		}
		holder.cb.setTag(position);
		holder.cb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int position = Integer.parseInt(v.getTag().toString());
				if (position == 0) {
                    OrderCategoryAdapter.this.downCategory(position + 1);
				} else {
                    OrderCategoryAdapter.this.upCategory(position);
				}
			}
		});
		return convertView;
	}

	public void upCategory(int position) {
		String resid = this.IniFile.getIniString(userIni, "MENU_ALL", "Title"
				+ position, "", (byte) 0);
		String resname = this.IniFile.getIniString(userIni, "MENU_ALL", "ID"
				+ position, "", (byte) 0);
		String resicon = this.IniFile.getIniString(userIni, "MENU_ALL", "Url"
				+ position, "", (byte) 0);
        this.IniFile.writeIniString(userIni, "MENU_ALL",
				"Title" + (position + 1), resid);
        this.IniFile.writeIniString(userIni, "MENU_ALL", "ID" + (position + 1),
				resname);
        this.IniFile.writeIniString(userIni, "MENU_ALL", "Url" + (position + 1),
				resicon);
        this.IniFile.writeIniString(userIni, "MENU_ALL", "Title" + position, this.list
				.get(position).get("group"));
        this.IniFile.writeIniString(userIni, "MENU_ALL", "ID" + position, this.list
				.get(position).get("groupId"));
        this.IniFile.writeIniString(userIni, "MENU_ALL", "Url" + position, this.list
				.get(position).get("groupUrl"));
        this.list.get(position - 1).put("group", this.list.get(position).get("group"));
        this.list.get(position - 1)
				.put("groupId", this.list.get(position).get("groupId"));
        this.list.get(position - 1).put("groupUrl",
                this.list.get(position).get("groupUrl"));
        this.list.get(position).put("group", resid);
        this.list.get(position).put("groupId", resname);
        this.list.get(position).put("groupUrl", resicon);
        this.notifyDataSetChanged();
	}

	public void downCategory(int position) {
		String resid = this.IniFile.getIniString(userIni, "MENU_ALL", "Title"
				+ position, "", (byte) 0);
		String resname = this.IniFile.getIniString(userIni, "MENU_ALL", "ID"
				+ position, "", (byte) 0);
		String resicon = this.IniFile.getIniString(userIni, "MENU_ALL", "Url"
				+ position, "", (byte) 0);
        this.IniFile.writeIniString(userIni, "MENU_ALL",
				"Title" + (position + 1), resid);
        this.IniFile.writeIniString(userIni, "MENU_ALL", "ID" + (position + 1),
				resname);
        this.IniFile.writeIniString(userIni, "MENU_ALL", "Url" + (position + 1),
				resicon);
        this.IniFile.writeIniString(userIni, "MENU_ALL", "Title" + position, this.list
				.get(position).get("group"));
        this.IniFile.writeIniString(userIni, "MENU_ALL", "ID" + position, this.list
				.get(position).get("groupId"));
        this.IniFile.writeIniString(userIni, "MENU_ALL", "Url" + position, this.list
				.get(position).get("groupUrl"));
        this.list.get(position- 1).put("group", this.list.get(position ).get("group"));
        this.list.get(position- 1)
				.put("groupId", this.list.get(position).get("groupId"));
        this.list.get(position- 1).put("groupUrl",
                this.list.get(position).get("groupUrl"));
        this.list.get(position).put("group", resid);
        this.list.get(position).put("groupId", resname);
        this.list.get(position).put("groupUrl", resicon);
        this.notifyDataSetChanged();
	}

	public static class ViewHolder {
		public TextView tv;
		public Button cb;
	}
}