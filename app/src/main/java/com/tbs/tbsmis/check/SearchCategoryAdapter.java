package com.tbs.tbsmis.check;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchCategoryAdapter extends BaseAdapter {
	private final ArrayList<HashMap<String, Object>> list;
	private LayoutInflater inflater;
	public Context context;
	private final IniFile m_iniFileIO;
	private final String webRoot;

	@SuppressLint("UseSparseArrays")
	public SearchCategoryAdapter(ArrayList<HashMap<String, Object>> list,
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
		final SearchCategoryAdapter.ViewHolder holder;
		if (convertView == null) {
			// ���ViewHolder����
			holder = new SearchCategoryAdapter.ViewHolder();
			// ���벼�ֲ���ֵ��convertview
			convertView = this.inflater.inflate(R.layout.search_category_item, null);
			holder.search_path_name = (TextView) convertView
					.findViewById(R.id.search_path_name);
			holder.SearchTitle = (TextView) convertView
					.findViewById(R.id.search_category_name);
			holder.SearchURL = (TextView) convertView
					.findViewById(R.id.search_path_text);
			holder.HistoryURL = (TextView) convertView
					.findViewById(R.id.history_path_text);
			holder.KeywordURL = (TextView) convertView
					.findViewById(R.id.keyword_path_text);
			holder.SearchAction = (TextView) convertView
					.findViewById(R.id.search_action_text);
			holder.KeyAction = (TextView) convertView
					.findViewById(R.id.keyword_action_text);

			holder.edit = (Button) convertView
					.findViewById(R.id.search_category_editBtn);
			holder.delete = (Button) convertView
					.findViewById(R.id.search_category_deleBtn);
			holder.up = (Button) convertView
					.findViewById(R.id.search_category_up);
			holder.down = (Button) convertView
					.findViewById(R.id.search_category_down);
			holder.copy = (Button) convertView
					.findViewById(R.id.search_category_copy);
			holder.more = (ImageView) convertView
					.findViewById(R.id.search_category_pic);
			holder.search = (RelativeLayout) convertView
					.findViewById(R.id.search_category_btn);
			holder.btnLayout = (LinearLayout) convertView
					.findViewById(R.id.search_category);
			// Ϊview���ñ�ǩ
			convertView.setTag(holder);
		} else {
			// ȡ��holder
			holder = (SearchCategoryAdapter.ViewHolder) convertView.getTag();
		}
		holder.btnLayout.setVisibility(View.GONE);
		holder.more.setBackgroundResource(R.drawable.update_down);
		// ����list��TextView����ʾ
		holder.search_path_name.setText(this.list.get(position).get("SearchName")
				.toString());
		holder.SearchTitle.setText(this.list.get(position).get("SearchTitle")
				.toString());
		holder.SearchURL
				.setText(this.list.get(position).get("SearchURL").toString());
		holder.HistoryURL.setText(this.list.get(position).get("HistoryURL")
				.toString());
		holder.KeywordURL.setText(this.list.get(position).get("KeywordURL")
				.toString());
		holder.SearchAction.setText(this.list.get(position).get("SearchAction")
				.toString());
		holder.KeyAction
				.setText(this.list.get(position).get("KeyAction").toString());
		if (position == 0) {
			holder.delete.setEnabled(false);
			holder.up.setEnabled(false);
		} else if (position + 1 == this.getCount()) {
			holder.down.setEnabled(false);
		}
		if(this.getCount() <= 1){
            holder.down.setEnabled(false);
        }
		holder.delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                SearchCategoryAdapter.this.DeleteCategoryDialog(position, holder.SearchTitle.getText()
						.toString());
			}
		});
		holder.up.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                SearchCategoryAdapter.this.upcategory(position);
			}
		});
		holder.copy.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                SearchCategoryAdapter.this.CopyMsgCategoryDialog(position, holder.SearchTitle.getText()
						.toString());
			}
		});
		holder.down.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                SearchCategoryAdapter.this.downcategory(position);
			}
		});
		holder.edit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                SearchCategoryAdapter.this.EditMsgCategoryDialog(position, holder.SearchTitle.getText()
						.toString());
			}
		});

		return convertView;
	}

	protected void downcategory(int position) {
		// TODO Auto-generated method stub
		int forward = position + 1;
		// TODO Auto-generated method stub
		String search_Name = this.m_iniFileIO.getIniString(this.webRoot, "search",
				"search" + forward, "", (byte) 0);
		String SearchTitle = this.m_iniFileIO.getIniString(this.webRoot, search_Name,
				"SearchTitle", "", (byte) 0);
		String SearchURL = this.m_iniFileIO.getIniString(this.webRoot, search_Name,
				"SearchURL", "", (byte) 0);
		String HistoryURL = this.m_iniFileIO.getIniString(this.webRoot, search_Name,
				"HistoryURL", "", (byte) 0);
		String KeywordURL = this.m_iniFileIO.getIniString(this.webRoot, search_Name,
				"KeywordURL", "", (byte) 0);
		String SearchAction = this.m_iniFileIO.getIniString(this.webRoot, search_Name,
				"SearchAction", "", (byte) 0);
		String KeyAction = this.m_iniFileIO.getIniString(this.webRoot, search_Name,
				"KeyAction", "", (byte) 0);
		String search_Name0 = this.m_iniFileIO.getIniString(this.webRoot, "search",
				"search" + position, "", (byte) 0);
        this.m_iniFileIO.writeIniString(this.webRoot, "search", "search" + forward,
				search_Name0);
        this.m_iniFileIO.writeIniString(this.webRoot, "search", "search" + position,
				search_Name);
        this.list.get(forward).put("SearchName",
                this.list.get(position).get("SearchName").toString());
        this.list.get(forward).put("SearchTitle",
                this.list.get(position).get("SearchTitle").toString());
        this.list.get(forward).put("SearchURL",
                this.list.get(position).get("SearchURL").toString());
        this.list.get(forward).put("HistoryURL",
                this.list.get(position).get("HistoryURL").toString());
        this.list.get(forward).put("KeywordURL",
                this.list.get(position).get("KeywordURL").toString());
        this.list.get(forward).put("SearchAction",
                this.list.get(position).get("SearchAction").toString());
        this.list.get(forward).put("KeyAction",
                this.list.get(position).get("KeyAction").toString());
        this.list.get(position).put("SearchName", search_Name);
        this.list.get(position).put("SearchTitle", SearchTitle);
        this.list.get(position).put("SearchURL", SearchURL);
        this.list.get(position).put("HistoryURL", HistoryURL);
        this.list.get(position).put("KeywordURL", KeywordURL);
        this.list.get(position).put("SearchAction", SearchAction);
        this.list.get(position).put("KeyAction", KeyAction);
        this.notifyDataSetChanged();
	}

	protected void upcategory(int position) {
		// TODO Auto-generated method stub
		int forward = position - 1;
		// TODO Auto-generated method stub
		String search_Name = this.m_iniFileIO.getIniString(this.webRoot, "search",
				"search" + forward, "", (byte) 0);
		String SearchTitle = this.m_iniFileIO.getIniString(this.webRoot, search_Name,
				"SearchTitle", "", (byte) 0);
		String SearchURL = this.m_iniFileIO.getIniString(this.webRoot, search_Name,
				"SearchURL", "", (byte) 0);
		String HistoryURL = this.m_iniFileIO.getIniString(this.webRoot, search_Name,
				"HistoryURL", "", (byte) 0);
		String KeywordURL = this.m_iniFileIO.getIniString(this.webRoot, search_Name,
				"KeywordURL", "", (byte) 0);
		String SearchAction = this.m_iniFileIO.getIniString(this.webRoot, search_Name,
				"SearchAction", "", (byte) 0);
		String KeyAction = this.m_iniFileIO.getIniString(this.webRoot, search_Name,
				"KeyAction", "", (byte) 0);
		String search_Name0 = this.m_iniFileIO.getIniString(this.webRoot, "search",
				"search" + position, "", (byte) 0);
        this.m_iniFileIO.writeIniString(this.webRoot, "search", "search" + forward,
				search_Name0);
        this.m_iniFileIO.writeIniString(this.webRoot, "search", "search" + position,
				search_Name);
        this.list.get(forward).put("SearchName",
                this.list.get(position).get("SearchName").toString());
        this.list.get(forward).put("SearchTitle",
                this.list.get(position).get("SearchTitle").toString());
        this.list.get(forward).put("SearchURL",
                this.list.get(position).get("SearchURL").toString());
        this.list.get(forward).put("HistoryURL",
                this.list.get(position).get("HistoryURL").toString());
        this.list.get(forward).put("KeywordURL",
                this.list.get(position).get("KeywordURL").toString());
        this.list.get(forward).put("SearchAction",
                this.list.get(position).get("SearchAction").toString());
        this.list.get(forward).put("KeyAction",
                this.list.get(position).get("KeyAction").toString());
        this.list.get(position).put("SearchName", search_Name);
        this.list.get(position).put("SearchTitle", SearchTitle);
        this.list.get(position).put("SearchURL", SearchURL);
        this.list.get(position).put("HistoryURL", HistoryURL);
        this.list.get(position).put("KeywordURL", KeywordURL);
        this.list.get(position).put("SearchAction", SearchAction);
        this.list.get(position).put("KeyAction", KeyAction);
        this.notifyDataSetChanged();
	}

	protected void CopyMsgCategoryDialog(final int position, String msgName) {
		LayoutInflater factory = LayoutInflater.from(this.context);// 提示框
		View view = factory.inflate(R.layout.search_edit, null);// 这里必须是final的
		final EditText set_category_name = (EditText) view
				.findViewById(R.id.set_category_name);
		final EditText set_english_name = (EditText) view
				.findViewById(R.id.set_english_name);// 获得输入框对象
		final EditText set_search_path = (EditText) view
				.findViewById(R.id.set_search_path);
		final EditText set_keyword_path = (EditText) view
				.findViewById(R.id.set_keyword_path);
		final EditText set_history_path = (EditText) view
				.findViewById(R.id.set_history_path);
		final EditText set_search_action = (EditText) view
				.findViewById(R.id.set_search_action);
		final EditText set_keyword_action = (EditText) view
				.findViewById(R.id.set_keyword_action);
		// LinearLayout show_english_name = (LinearLayout) view
		// .findViewById(R.R.id.show_english_name);
		String search_Name = this.m_iniFileIO.getIniString(this.webRoot, "search",
				"search" + position, "", (byte) 0);
		set_english_name.setText(search_Name);
		// show_english_name.setVisibility(View.GONE);
		set_category_name.setText(this.m_iniFileIO.getIniString(this.webRoot,
				search_Name, "SearchTitle", "", (byte) 0));
		set_search_path.setText(this.m_iniFileIO.getIniString(this.webRoot, search_Name,
				"SearchURL", "", (byte) 0));
		set_history_path.setText(this.m_iniFileIO.getIniString(this.webRoot, search_Name,
				"HistoryURL", "", (byte) 0));
		set_keyword_path.setText(this.m_iniFileIO.getIniString(this.webRoot, search_Name,
				"KeywordURL", "", (byte) 0));
		set_search_action.setText(this.m_iniFileIO.getIniString(this.webRoot,
				search_Name, "SearchAction", "", (byte) 0));
		set_keyword_action.setText(this.m_iniFileIO.getIniString(this.webRoot,
				search_Name, "KeyAction", "", (byte) 0));
		set_search_path.addTextChangedListener(new TextWatcher() {
			private String localPortTxt;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
                this.localPortTxt = String.valueOf(set_search_path.getText());
				if (null != this.localPortTxt && !this.localPortTxt.equals("")
						&& !this.localPortTxt.equals("\\s{1,}")) {
					set_history_path.setText(this.localPortTxt);
					set_keyword_path.setText(this.localPortTxt);
				}
				Log.i("localPortTxt", "onTextChanged...");
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				Log.i("localPortTxt", "beforeTextChanged...");
			}

			@Override
			public void afterTextChanged(Editable s) {
				Log.i("localPortTxt", "afterTextChanged...");
			}
		});
		new Builder(this.context)
				.setTitle("复制“" + msgName + "”选项并添加到检索选项")
				.setCancelable(false)
				// 提示框标题
				.setView(view)
				.setPositiveButton("确定",// 提示框的两个按钮
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								try {
									Field field = dialog.getClass()
											.getSuperclass()
											.getDeclaredField("mShowing");
									field.setAccessible(true);
									// 将mShowing变量设为false，表示对话框已关闭
									field.set(dialog, false);
									dialog.dismiss();
								} catch (Exception e) {

								}
								if (StringUtils.isEmpty(set_category_name
										.getText().toString())) {
									Toast.makeText(SearchCategoryAdapter.this.context, "检索名称不可为空",
											Toast.LENGTH_SHORT).show();
									return;
								} else if (StringUtils.isEmpty(set_english_name
										.getText().toString())) {
									Toast.makeText(SearchCategoryAdapter.this.context, "检索链接不可为空",
											Toast.LENGTH_SHORT).show();
									return;
								} else if (StringUtils.isEmpty(set_search_path
										.getText().toString())) {
									Toast.makeText(SearchCategoryAdapter.this.context, "检索链接不可为空",
											Toast.LENGTH_SHORT).show();
									return;
								} else {
									if (SearchCategoryAdapter.this.copyMsgCategory(position,
											set_category_name.getText()
													.toString(),
											set_english_name.getText()
													.toString(),
											set_search_path.getText()
													.toString(),
											set_history_path.getText()
													.toString(),
											set_keyword_path.getText()
													.toString(),
											set_search_action.getText()
													.toString(),
											set_keyword_action.getText()
													.toString())) {
										Toast.makeText(SearchCategoryAdapter.this.context, "检索选项添加成功",
												Toast.LENGTH_SHORT).show();
									} else {
										Toast.makeText(SearchCategoryAdapter.this.context, "英文名字不能相同",
												Toast.LENGTH_SHORT).show();
										return;
									}
									try {
										Field field = dialog.getClass()
												.getSuperclass()
												.getDeclaredField("mShowing");
										field.setAccessible(true);
										// 将mShowing变量设为false，表示对话框已关闭
										field.set(dialog, true);
										dialog.dismiss();
									} catch (Exception e) {

									}
								}
							}
						})
				.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								try {
									Field field = dialog.getClass()
											.getSuperclass()
											.getDeclaredField("mShowing");
									field.setAccessible(true);
									// 将mShowing变量设为false，表示对话框已关闭
									field.set(dialog, true);
									dialog.dismiss();
								} catch (Exception e) {

								}
							}
						}).create().show();
	}

	protected boolean copyMsgCategory(int position, String category_name,
			String english_name, String search_path, String history_path,
			String keyword_path, String search_action, String keyword_action) {
		int cateNum = Integer.parseInt(this.m_iniFileIO.getIniString(this.webRoot,
				"search", "SearchCount", "0", (byte) 0));
		for (int i = 0; i < cateNum; i++) {
			if (this.m_iniFileIO.getIniString(this.webRoot, "search", "search" + i, "",
					(byte) 0).equalsIgnoreCase(english_name)) {
				return false;
			}
		}
        this.m_iniFileIO.writeIniString(this.webRoot, "search", "search" + cateNum,
				english_name);
        this.m_iniFileIO.writeIniString(this.webRoot, "search", "SearchCount",
				(cateNum + 1) + "");
        this.m_iniFileIO.writeIniString(this.webRoot, english_name, "SearchTitle",
				category_name);
        this.m_iniFileIO.writeIniString(this.webRoot, english_name, "SearchURL",
				search_path);
        this.m_iniFileIO.writeIniString(this.webRoot, english_name, "HistoryURL",
				history_path);
        this.m_iniFileIO.writeIniString(this.webRoot, english_name, "KeywordURL",
				keyword_path);
        this.m_iniFileIO.writeIniString(this.webRoot, english_name, "SearchAction",
				search_action);
        this.m_iniFileIO.writeIniString(this.webRoot, english_name, "KeyAction",
				keyword_action);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("SearchName", english_name);
		map.put("SearchTitle", category_name);
		map.put("SearchURL", search_path);
		map.put("HistoryURL", history_path);
		map.put("KeywordURL", keyword_path);
		map.put("SearchAction", search_action);
		map.put("KeyAction", keyword_action);
        this.list.add(map);
        this.notifyDataSetChanged();
		return true;
		// TODO Auto-generated method stub

	}

	protected void EditMsgCategoryDialog(final int position, String msgName) {
		LayoutInflater factory = LayoutInflater.from(this.context);// 提示框
		View view = factory.inflate(R.layout.search_edit, null);// 这里必须是final的
		final EditText set_category_name = (EditText) view
				.findViewById(R.id.set_category_name);
		final EditText set_english_name = (EditText) view
				.findViewById(R.id.set_english_name);// 获得输入框对象
		final EditText set_search_path = (EditText) view
				.findViewById(R.id.set_search_path);
		final EditText set_keyword_path = (EditText) view
				.findViewById(R.id.set_keyword_path);
		final EditText set_history_path = (EditText) view
				.findViewById(R.id.set_history_path);
		final EditText set_search_action = (EditText) view
				.findViewById(R.id.set_search_action);
		final EditText set_keyword_action = (EditText) view
				.findViewById(R.id.set_keyword_action);
		LinearLayout show_english_name = (LinearLayout) view
				.findViewById(R.id.show_english_name);
		String search_Name = this.m_iniFileIO.getIniString(this.webRoot, "search",
				"search" + position, "", (byte) 0);
		set_english_name.setText(search_Name);
		show_english_name.setVisibility(View.GONE);
		set_category_name.setText(this.m_iniFileIO.getIniString(this.webRoot,
				search_Name, "SearchTitle", "", (byte) 0));
		set_search_path.setText(this.m_iniFileIO.getIniString(this.webRoot, search_Name,
				"SearchURL", "", (byte) 0));
		set_history_path.setText(this.m_iniFileIO.getIniString(this.webRoot, search_Name,
				"HistoryURL", "", (byte) 0));
		set_keyword_path.setText(this.m_iniFileIO.getIniString(this.webRoot, search_Name,
				"KeywordURL", "", (byte) 0));
		set_search_action.setText(this.m_iniFileIO.getIniString(this.webRoot,
				search_Name, "SearchAction", "", (byte) 0));
		set_keyword_action.setText(this.m_iniFileIO.getIniString(this.webRoot,
				search_Name, "KeyAction", "", (byte) 0));
		set_search_path.addTextChangedListener(new TextWatcher() {
			private String localPortTxt;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
                this.localPortTxt = String.valueOf(set_search_path.getText());
				if (null != this.localPortTxt && !this.localPortTxt.equals("")
						&& !this.localPortTxt.equals("\\s{1,}")) {
					set_history_path.setText(this.localPortTxt);
					set_keyword_path.setText(this.localPortTxt);
				}
				Log.i("localPortTxt", "onTextChanged...");
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				Log.i("localPortTxt", "beforeTextChanged...");
			}

			@Override
			public void afterTextChanged(Editable s) {
				Log.i("localPortTxt", "afterTextChanged...");
			}
		});
		new Builder(this.context)
				.setTitle("修改“" + msgName + "”分类")
				.setCancelable(false)
				// 提示框标题
				.setView(view)
				.setPositiveButton("确定",// 提示框的两个按钮
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								try {
									Field field = dialog.getClass()
											.getSuperclass()
											.getDeclaredField("mShowing");
									field.setAccessible(true);
									// 将mShowing变量设为false，表示对话框已关闭
									field.set(dialog, false);
									dialog.dismiss();
								} catch (Exception e) {

								}
								if (StringUtils.isEmpty(set_category_name
										.getText().toString())) {
									Toast.makeText(SearchCategoryAdapter.this.context, "检索名称不可为空",
											Toast.LENGTH_SHORT).show();
									return;
								} else if (StringUtils.isEmpty(set_english_name
										.getText().toString())) {
									Toast.makeText(SearchCategoryAdapter.this.context, "检索链接不可为空",
											Toast.LENGTH_SHORT).show();
									return;
								} else if (StringUtils.isEmpty(set_search_path
										.getText().toString())) {
									Toast.makeText(SearchCategoryAdapter.this.context, "检索链接不可为空",
											Toast.LENGTH_SHORT).show();
									return;
								} else {
                                    SearchCategoryAdapter.this.EditMsgCategory(position, set_category_name
											.getText().toString(),
											set_english_name.getText()
													.toString(),
											set_search_path.getText()
													.toString(),
											set_history_path.getText()
													.toString(),
											set_keyword_path.getText()
													.toString(),
											set_search_action.getText()
													.toString(),
											set_keyword_action.getText()
													.toString());
									Toast.makeText(SearchCategoryAdapter.this.context, "信息分类修改成功",
											Toast.LENGTH_SHORT).show();
									try {
										Field field = dialog.getClass()
												.getSuperclass()
												.getDeclaredField("mShowing");
										field.setAccessible(true);
										// 将mShowing变量设为false，表示对话框已关闭
										field.set(dialog, true);
										dialog.dismiss();
									} catch (Exception e) {

									}
								}
							}
						})
				.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								try {
									Field field = dialog.getClass()
											.getSuperclass()
											.getDeclaredField("mShowing");
									field.setAccessible(true);
									// 将mShowing变量设为false，表示对话框已关闭
									field.set(dialog, true);
									dialog.dismiss();
								} catch (Exception e) {

								}
							}
						}).create().show();
	}

	protected void EditMsgCategory(int position, String category_name,
			String english_name, String search_path, String history_path,
			String keyword_path, String search_action, String keyword_action) {
		// TODO Auto-generated method stub
        this.m_iniFileIO.writeIniString(this.webRoot, english_name, "SearchTitle",
				category_name);
        this.m_iniFileIO.writeIniString(this.webRoot, english_name, "SearchURL",
				search_path);
        this.m_iniFileIO.writeIniString(this.webRoot, english_name, "HistoryURL",
				history_path);
        this.m_iniFileIO.writeIniString(this.webRoot, english_name, "KeywordURL",
				keyword_path);
        this.m_iniFileIO.writeIniString(this.webRoot, english_name, "SearchAction",
				search_action);
        this.m_iniFileIO.writeIniString(this.webRoot, english_name, "KeyAction",
				keyword_action);
        this.list.get(position).put("SearchTitle", category_name);
        this.list.get(position).put("SearchURL", search_path);
        this.list.get(position).put("HistoryURL", history_path);
        this.list.get(position).put("KeywordURL", keyword_path);
        this.list.get(position).put("SearchAction", search_action);
        this.list.get(position).put("KeyAction", keyword_action);
        this.notifyDataSetChanged();
	}

	protected void DeleteCategoryDialog(final int position, String msgName) {
		// TODO Auto-generated method stub
		new Builder(this.context).setCancelable(false)
				.setMessage("确定删除:" + msgName + " 选项")// 提示框标题
				.setPositiveButton("确定",// 提示框的两个按钮
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
                                SearchCategoryAdapter.this.DeleteCategory(position);
							}
						}).setNegativeButton("取消", null).create().show();
	}

	protected void DeleteCategory(int position) {
		// TODO Auto-generated method stub
		int cateNum = Integer.parseInt(this.m_iniFileIO.getIniString(this.webRoot,
				"search", "SearchCount", "0", (byte) 0));
		String search_Name = this.m_iniFileIO.getIniString(this.webRoot, "search",
				"search" + position, "", (byte) 0);
		for (int i = 0; i < cateNum; i++) {
			if (i == position) {
                this.m_iniFileIO.deleteIniString(this.webRoot, "search", "search" + i);
			} else if (i > position) {
				String resid = this.m_iniFileIO.getIniString(this.webRoot, "search",
						"search" + i, "", (byte) 0);
                this.m_iniFileIO.writeIniString(this.webRoot, "search", "search"
						+ (i - 1), resid);
			}
		}
        this.m_iniFileIO.deleteIniSection(this.webRoot, search_Name);
        this.m_iniFileIO.writeIniString(this.webRoot, "search", "SearchCount",
				(cateNum - 1) + "");
        this.list.remove(position);
        this.notifyDataSetChanged();

	}

	public static class ViewHolder {

		public Button delete;
		public Button edit;
		public Button up;
		public Button down;
		public Button copy;

		public TextView SearchTitle;
		public TextView SearchURL;
		public TextView HistoryURL;
		public TextView KeywordURL;
		public TextView SearchAction;
		public TextView KeyAction;
		public TextView search_path_name;

		public ImageView more;
		public LinearLayout btnLayout;
		public RelativeLayout search;
	}

}