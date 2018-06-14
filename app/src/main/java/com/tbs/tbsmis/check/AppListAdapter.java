package com.tbs.tbsmis.check;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class AppListAdapter extends BaseAdapter {
	private final ArrayList<HashMap<String, Object>> list;
	private LayoutInflater inflater;
	private int selectItem = -1;
	private final String webRoot;
	private final IniFile m_iniFileIO;
	public Context context;

	@SuppressLint("UseSparseArrays")
	public AppListAdapter(ArrayList<HashMap<String, Object>> list,
			Context context, String webRoot) {
		this.list = list;
		this.context = context;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		final AppListAdapter.ViewHolder holder;
		if (convertView == null) {
			// ���ViewHolder����
			holder = new AppListAdapter.ViewHolder();
			// ���벼�ֲ���ֵ��convertview
			convertView = this.inflater.inflate(R.layout.app_list_item, null);
			holder.tv = (TextView) convertView.findViewById(R.id.app_name);
			holder.re = (TextView) convertView.findViewById(R.id.app_id);
			holder.title = (TextView) convertView.findViewById(R.id.app_title);
			holder.cb = (Button) convertView.findViewById(R.id.app_btn);
			holder.cy = (Button) convertView.findViewById(R.id.setcopy);
			holder.se = (Button) convertView.findViewById(R.id.setedit);
			holder.df = (Button) convertView.findViewById(R.id.setdefault);
			holder.dl = (Button) convertView.findViewById(R.id.setdelete);
			holder.ds = (Button) convertView.findViewById(R.id.setdeskshort);
			holder.im = (ImageView) convertView.findViewById(R.id.img_app_icon);
			holder.more = (ImageView) convertView
					.findViewById(R.id.update_more);
			holder.msg = (RelativeLayout) convertView
					.findViewById(R.id.app_msg);
			holder.btnLayout = (LinearLayout) convertView
					.findViewById(R.id.updateLayout);
			// Ϊview���ñ�ǩ
			convertView.setTag(holder);
		} else {
			// ȡ��holder
			holder = (AppListAdapter.ViewHolder) convertView.getTag();
		}
		holder.btnLayout.setVisibility(View.GONE);
		holder.more.setBackgroundResource(R.drawable.update_down);
		// ����list��TextView����ʾ
		holder.tv.setText(this.list.get(position).get("AppName").toString());
		holder.im.setImageResource(Integer.parseInt(this.list.get(position)
				.get("ItemImage").toString()));
         String iniPath =this.list.get(position).get("ResName").toString();
		holder.title.setText(iniPath.substring(0,iniPath.indexOf(".")));
		holder.re.setText(this.list.get(position).get("ResName").toString());
		if (position == this.selectItem) {
			holder.cb.setText("当前应用");
			holder.dl.setEnabled(false);
			//holder.se.setEnabled(false);
		} else {
			holder.cb.setText("未启用");
			holder.dl.setEnabled(true);
			//holder.se.setEnabled(true);
		}
		holder.cb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// int position = Integer.parseInt(v.getTag().toString());
				String apptext = (String) holder.re.getText();
				String WebIniFile = AppListAdapter.this.webRoot + constants.WEB_CONFIG_FILE_NAME;
                AppListAdapter.this.m_iniFileIO.writeIniString(WebIniFile, "TBSWeb", "IniName",
						apptext);
				Toast.makeText(AppListAdapter.this.context,
						holder.tv.getText() + "：已设为当前应用",
						Toast.LENGTH_SHORT).show();
				// setSelectItem(position);
				// notifyDataSetChanged();
				Intent intent = new Intent();
				intent.setAction("Action_main"
						+ AppListAdapter.this.context.getString(R.string.about_title));
				intent.putExtra("flag", 12);
                AppListAdapter.this.context.sendBroadcast(intent);
				intent.setAction("loadView"
						+ AppListAdapter.this.context.getString(R.string.about_title));
				intent.putExtra("flag", 5);
				intent.putExtra("author", 0);
                AppListAdapter.this.context.sendBroadcast(intent);
				intent = new Intent();
				intent.setAction("app_manager"
						+ AppListAdapter.this.context.getString(R.string.about_title));
                AppListAdapter.this.context.sendBroadcast(intent);
			}
		});
		holder.df.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// int position = Integer.parseInt(v.getTag().toString());
				String apptext = (String) holder.re.getText();
				String WebIniFile = AppListAdapter.this.webRoot + constants.WEB_CONFIG_FILE_NAME;
                AppListAdapter.this.m_iniFileIO.writeIniString(WebIniFile, "TBSWeb", "IniName",
						apptext);
				Toast.makeText(AppListAdapter.this.context,
						holder.tv.getText() + "：已设为当前应用",
						Toast.LENGTH_SHORT).show();
				// setSelectItem(position);
				// notifyDataSetChanged();
				Intent intent = new Intent();
				intent.setAction("Action_main"
						+ AppListAdapter.this.context.getString(R.string.about_title));
				intent.putExtra("flag", 12);
                AppListAdapter.this.context.sendBroadcast(intent);
				intent = new Intent();
				intent.setAction("loadView"
						+ AppListAdapter.this.context.getString(R.string.about_title));
				intent.putExtra("flag", 5);
				intent.putExtra("author", 0);
                AppListAdapter.this.context.sendBroadcast(intent);
				intent = new Intent();
				intent.setAction("app_manager"
						+ AppListAdapter.this.context.getString(R.string.about_title));
                AppListAdapter.this.context.sendBroadcast(intent);
			}
		});
		holder.dl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String apptext = (String) holder.re.getText();
				final String appname = (String) holder.tv.getText();
				new Builder(AppListAdapter.this.context)
						.setCancelable(false)
						.setMessage("确定删除:" + apptext + " 应用")
						// 提示框标题
						.setPositiveButton(
								"确定",// 提示框的两个按钮
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										FileUtils.deleteFileWithPath(AppListAdapter.this.webRoot
												+ apptext);
                                        AppListAdapter.this.list.remove(position);
										if (AppListAdapter.this.selectItem > position) {
                                            AppListAdapter.this.setSelectItem(AppListAdapter.this.selectItem - 1);
										}
                                        AppListAdapter.this.notifyDataSetChanged();
										UIHelper.showdeleteDeskShortCut(
                                                AppListAdapter.this.context, appname);
									}
								}).setNegativeButton("取消", null).create()
						.show();

			}
		});
		holder.cy.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String apptext = (String) holder.re.getText();
				String appname = (String) holder.tv.getText();
				String resname = AppListAdapter.this.m_iniFileIO.getIniString(AppListAdapter.this.webRoot + apptext,
						"TBSAPP", "resname", "0", (byte) 0);
				UIHelper.showAppEditDialog(AppListAdapter.this.context, AppListAdapter.this.webRoot + apptext, appname,
						resname, 0);
			}
		});
		holder.se.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String apptext = (String) holder.re.getText();
				String appname = (String) holder.tv.getText();
				String resname = AppListAdapter.this.m_iniFileIO.getIniString(AppListAdapter.this.webRoot + apptext,
						"TBSAPP", "resname", "", (byte) 0);
				UIHelper.showAppEditDialog(AppListAdapter.this.context, AppListAdapter.this.webRoot + apptext, appname,
						resname, 1);
			}
		});
		holder.ds.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String apptext = (String) holder.re.getText();
				String appname = (String) holder.tv.getText();
				String resname = AppListAdapter.this.m_iniFileIO.getIniString(AppListAdapter.this.webRoot + apptext,
						"TBSAPP", "resname", "", (byte) 0);
				UIHelper.showcreateDeskShortCut(AppListAdapter.this.context, appname, resname,
                        AppListAdapter.this.webRoot + apptext);
			}
		});
		holder.msg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (holder.btnLayout.getVisibility() == View.GONE) {
					holder.btnLayout.setVisibility(View.VISIBLE);
					holder.more.setBackgroundResource(R.drawable.update_up);
				} else {
					holder.btnLayout.setVisibility(View.GONE);
					holder.more.setBackgroundResource(R.drawable.update_down);
				}
			}
		});
		return convertView;
	}

	public static class ViewHolder {
		public TextView tv;
		public Button cb;
		public Button df;
		public Button ds;
		public Button dl;
		public Button cy;
		public Button se;
		public TextView re;
		public TextView title;
		public ImageView im;
		public ImageView more;
		public LinearLayout btnLayout;
		public RelativeLayout msg;
	}

	public void setSelectItem(int selectItem) {
		this.selectItem = selectItem;
	}
}