package com.tbs.tbsmis.check;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.activity.InitializeToolbarActivity;
import com.tbs.tbsmis.activity.MyCloudActivity;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.app.StartTbsweb;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppGroupAdapter extends BaseExpandableListAdapter {
	class ExpandableListHolder { // 定义一个内部类，用于保存listitem的3个子视图引用,2个textview和1个checkbox
		public TextView tvName;
		public TextView count;
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

	private final Context context; // 父activity
	private final LayoutInflater mChildInflater; // 用于加载listitem的布局xml
	private final LayoutInflater mGroupInflater; // 用于加载group的布局xml
	private final List<Map<String, String>> groups; // 所有group
	private final List<List<Map<String, String>>> childs; // 所有group
	private String webRoot;
	private ProgressDialog Prodialog;
	// 构造方法：参数c － activity，参数group － 所有group
	protected boolean Unupdate = true;

	public AppGroupAdapter(Context c, List<Map<String, String>> groups,
			List<List<Map<String, String>>> childs) {
        context = c;
        mChildInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mGroupInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.groups = groups;
		this.childs = childs;
	}

	@Override
	public Object getChild(int arg0, int arg1) {// 根据组索引和item索引，取得listitem //
												// TODO Auto-generated method
												// stub
		return childs.get(arg0).get(arg1);
	}

	@Override
	public long getChildId(int arg0, int arg1) {// 返回item索引
		return arg1;
	}

	@Override
	public int getChildrenCount(int groupPosition) {// 根据组索引返回分组的子item数
		return childs.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {// 根据组索引返回组
		return groups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {// 返回分组数
		return groups.size();
	}

	@Override
	public long getGroupId(int groupPosition) {// 返回分组索引
		return groupPosition;
	}

	@Override
	public View getGroupView(int position, boolean isExpanded, View view,
			ViewGroup parent) {// 根据组索引渲染"组视图"
		AppGroupAdapter.ExpandableListHolder holder = null; // 清空临时变量holder
		if (view == null) { // 判断view（即view是否已构建好）是否为空
			// 若组视图为空，构建组视图。注意flate的使用，R.layout.browser_expandable_list_item代表了
			// 已加载到内存的browser_expandable_list_item.xml文件
			view = mGroupInflater.inflate(R.layout.select_city_item, null);
			// 下面主要是取得组的各子视图，设置子视图的属性。用tag来保存各子视图的引用
			holder = new AppGroupAdapter.ExpandableListHolder();
			// 从view中取得textView
			holder.tvName = (TextView) view.findViewById(R.id.column_title);
			holder.count = (TextView) view.findViewById(R.id.column_count);
			view.setTag(holder);
		} else { // 若view不为空，直接从view的tag属性中获得各子视图的引用
			holder = (AppGroupAdapter.ExpandableListHolder) view.getTag();
		}
		int count = 0;
		for (int i = 0; i < getChildrenCount(position); i++) {
			String path = childs.get(position).get(i).get("path");
			if (path.startsWith("/")) {
                webRoot = path;
			} else {
                webRoot = UIHelper.getStoragePath(context);
                webRoot = webRoot + constants.SD_CARD_TBSSOFT_PATH3 + "/" + path;
			}
			if (webRoot.endsWith("/") == false) {
                webRoot += "/";
			}
			File configFile = new File(webRoot);
			if (configFile.exists() == true) {
				count = count + 1;
			}
		}
		holder.count.setText("(" + count + "/" + getChildrenCount(position)
				+ ")");
		holder.tvName.setText(groups.get(position).get("group"));
		// TODO Auto-generated method stub
		return view;
	}

	// 行渲染方法
	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		final AppGroupAdapter.ExpandableListHolder holder; // 清空临时变量
		if (convertView == null) { // 若行未初始化
			// 通过flater初始化行视图
			convertView = mChildInflater.inflate(R.layout.app_list_item, null);
			// 并将行视图的3个子视图引用放到tag中
			holder = new AppGroupAdapter.ExpandableListHolder();
			holder.tvName = (TextView) convertView.findViewById(R.id.app_name);
			holder.re = (TextView) convertView.findViewById(R.id.app_title);
			holder.title = (TextView) convertView.findViewById(R.id.app_id);
			holder.cb = (Button) convertView.findViewById(R.id.app_btn);
			holder.dl = (Button) convertView.findViewById(R.id.setdelete);
			holder.df = (Button) convertView.findViewById(R.id.setdefault);
			holder.ds = (Button) convertView.findViewById(R.id.setdeskshort);
			holder.cy = (Button) convertView.findViewById(R.id.setcopy);
			holder.se = (Button) convertView.findViewById(R.id.setedit);
			holder.im = (ImageView) convertView.findViewById(R.id.img_app_icon);
			holder.more = (ImageView) convertView
					.findViewById(R.id.update_more);
			holder.msg = (RelativeLayout) convertView
					.findViewById(R.id.app_msg);
			holder.btnLayout = (LinearLayout) convertView
					.findViewById(R.id.updateLayout);
			convertView.setTag(holder);
		} else { // 若行已初始化，直接从tag属性获得子视图的引用
			holder = (AppGroupAdapter.ExpandableListHolder) convertView.getTag();
		}
		holder.df.setVisibility(View.GONE);
		holder.tvName.setText(childs.get(groupPosition).get(childPosition)
				.get("child"));
		holder.re.setText(childs.get(groupPosition).get(childPosition)
				.get("path"));
		holder.title.setText(childs.get(groupPosition).get(childPosition)
				.get("res"));
		holder.se.setText("修改路径");
		holder.cy.setText("修改应用");
		if (holder.re.getText().toString().startsWith("/")) {
            webRoot = holder.re.getText().toString();
		} else {
            webRoot = UIHelper.getStoragePath(context);
            webRoot = webRoot + constants.SD_CARD_TBSSOFT_PATH3 + "/"
					+ holder.re.getText();
		}
		if (webRoot.endsWith("/") == false) {
            webRoot += "/";
		}
		holder.dl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                DeleteAppDialog(holder.tvName.getText().toString(), holder.re
						.getText().toString(), groupPosition, childPosition);
			}
		});
		String appPath = UIHelper.getShareperference(context,
				constants.SAVE_INFORMATION, "Path", "");
		if (appPath.endsWith("/") == false) {
			appPath += "/";
		}
		if (appPath.equalsIgnoreCase(webRoot)) {
			holder.dl.setEnabled(false);
		} else {
			holder.dl.setEnabled(true);
		}
		File configFile1 = new File(webRoot);
		if (configFile1.exists() == false) {
            holder.cb.setText("去下载");
		} else {
			holder.cb.setText("启动");
		}
		holder.cb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (holder.cb.getText().toString().equals("启动")) {
					if (holder.re.getText().toString().startsWith("/")) {
                        webRoot = holder.re.getText().toString();
					} else {
                        webRoot = UIHelper.getStoragePath(context);
                        webRoot = webRoot + constants.SD_CARD_TBSSOFT_PATH3
								+ "/" + holder.re.getText();
					}
					if (webRoot.endsWith("/") == false) {
                        webRoot += "/";
					}
					StartTbsweb.Startapp(context, 0);
					UIHelper.setSharePerference(context,
							constants.SAVE_INFORMATION, "Path", webRoot);
					StartTbsweb.Startapp(context, 1);
					Intent intent = new Intent();
					intent.setAction("Action_main"
							+ context.getString(R.string.about_title));
					intent.putExtra("flag", 12);
                    context.sendBroadcast(intent);
					intent.setAction("loadView"
							+ context.getString(R.string.about_title));
					intent.putExtra("flag", 5);
					intent.putExtra("author", 0);
                    context.sendBroadcast(intent);
					Intent mainIntent = new Intent(context,
							InitializeToolbarActivity.class);
                    context.startActivity(mainIntent);
					MyActivity.getInstance().finishAllActivity();
				} else if (holder.cb.getText().toString().equals("去下载")) {

                    Intent intent = new Intent();
                    intent.setClass(context, MyCloudActivity.class);
                    context.startActivity(intent);
//                    webRoot = holder.re.getText().toString();
//					if (webRoot.startsWith("/")) {
//						if (webRoot.endsWith("/") == false) {
//                            webRoot = webRoot.substring(webRoot
//									.lastIndexOf("/") + 1);
//						} else {
//                            webRoot = webRoot.substring(0,
//                                    webRoot.lastIndexOf("/"));
//                            webRoot = webRoot.substring(webRoot
//									.lastIndexOf("/") + 1);
//						}
//					}
//                    showRightsDialog(webRoot,
//                            groups.get(groupPosition).get("group"));
				}
			}
		});
		holder.ds.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (holder.re.getText().toString().startsWith("/")) {
                    webRoot = holder.re.getText().toString();
				} else {
                    webRoot = UIHelper.getStoragePath(context);
                    webRoot = webRoot + constants.SD_CARD_TBSSOFT_PATH3 + "/"
							+ holder.re.getText();
				}
				if (webRoot.endsWith("/") == false) {
                    webRoot += "/";
				}
				UIHelper.showcreateDeskShortCut(context, holder.tvName
						.getText().toString(), "", webRoot);
			}
		});
		holder.se.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String webRoot;
				if (holder.re.getText().toString().startsWith("/")) {
					webRoot = holder.re.getText().toString();
				} else {
					webRoot = UIHelper.getStoragePath(context);
					webRoot = webRoot + constants.SD_CARD_TBSSOFT_PATH3 + "/"
							+ holder.re.getText();
				}
				if (webRoot.endsWith("/") == false) {
					webRoot += "/";
				}
				UIHelper.showFilePathDialog(context, 1, webRoot,
						new PathChooseDialog.ChooseCompleteListener() {
							@Override
							public void onComplete(String finalPath) {
								finalPath = finalPath + File.separator;
								IniFile m_iniFileIO = new IniFile();
								String configPath = context.getFilesDir()
										.getParentFile().getAbsolutePath();
								if (configPath.endsWith("/") == false) {
									configPath = configPath + "/";
								}
								configPath = configPath
										+ constants.USER_CONFIG_FILE_NAME;
								m_iniFileIO.writeIniString(configPath,
										holder.title.getText().toString(),
										"instdir", finalPath);
								holder.re.setText(finalPath);
								holder.cb.setText("启动");
							}
						});
			}
		});
		holder.cy.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                showNewAppDialog(groupPosition, childPosition);
			}
		});
		holder.btnLayout.setVisibility(View.GONE);
		holder.more.setBackgroundResource(R.drawable.update_down);

		holder.msg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!holder.btnLayout.isShown()) {
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

	@Override
	public boolean hasStableIds() {// 行是否具有唯一id
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {// 行是否可选
		return true;
	}

	@SuppressWarnings("deprecation")
	private void showRightsDialog(String webRoot, String cateGory) {
        Prodialog = new ProgressDialog(context);
        Prodialog.setTitle("验证");
        Prodialog.setMessage("正在获取应用信息，请稍候...");
        Prodialog.setIndeterminate(false);
        Prodialog.setCanceledOnTouchOutside(false);
        Prodialog.setButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method
				// stub
				dialog.dismiss();
                Unupdate = false;
			}
		});
        connect(webRoot, cateGory);
	}

	private void showNewAppDialog(final int groupPosition,
			final int childPosition) {
		final IniFile IniFile = new IniFile();
		String configPath = context.getFilesDir().getParentFile()
				.getAbsolutePath();
		if (configPath.endsWith("/") == false) {

			configPath = configPath + "/";
		}
		configPath = configPath + constants.USER_CONFIG_FILE_NAME;
		final String appIniFile = configPath;
		LayoutInflater factory = LayoutInflater.from(context);// 提示框
		View view = factory.inflate(R.layout.set_new_app, null);// 这里必须是final的
		final EditText edit_name = (EditText) view
				.findViewById(R.id.edit_name_Text);// 获得输入框对象
		final EditText edit_code = (EditText) view
				.findViewById(R.id.edit_code_Text);
		RelativeLayout edit_file = (RelativeLayout) view
				.findViewById(R.id.edit_file_view);
		RelativeLayout edit_dir = (RelativeLayout) view
				.findViewById(R.id.edit_dir_view);
		RelativeLayout edit_web = (RelativeLayout) view
				.findViewById(R.id.edit_web_view);
		final LinearLayout dynamicTag = (LinearLayout) view
				.findViewById(R.id.account_tip);
		final TextView dynamicTxt = (TextView) view
				.findViewById(R.id.account_error_text);// 错误信息显示
		edit_file.setVisibility(View.GONE);
		edit_dir.setVisibility(View.GONE);
		edit_web.setVisibility(View.GONE);
		final String resCode = IniFile.getIniString(appIniFile, "group"
				+ (groupPosition + 1), "res" + (childPosition + 1), "",
				(byte) 0);
		edit_code.setText(resCode);
		edit_name.setText(IniFile.getIniString(appIniFile, resCode, "title",
				"", (byte) 0));
		// edit_web.setText(resCode);
		new Builder(context)
				.setTitle("修改应用")
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
								if (StringUtils.isEmpty(edit_name.getText()
										.toString())) {
									dynamicTag.setVisibility(View.VISIBLE);
									dynamicTxt.setText("应用名称不可为空");
									return;
								} else if (StringUtils.isEmpty(edit_code
										.getText().toString())) {
									dynamicTag.setVisibility(View.VISIBLE);
									dynamicTxt.setText("应用代号不可为空");
									return;
								} else {
									int resnum = Integer.parseInt(IniFile
											.getIniString(appIniFile,
													"resource", "resnum", "0",
													(byte) 0));
									for (int i = 1; i <= resnum; i++) {
										int groupresnum = Integer
												.parseInt(IniFile.getIniString(
														appIniFile,
														"group" + i, "resnum",
														"0", (byte) 0));
										for (int j = 1; j <= groupresnum; j++) {
											if (j != childPosition + 1) {
												if (edit_code
														.getText()
														.toString()
														.equals(IniFile
																.getIniString(
																		appIniFile,
																		"group"
																				+ i,
																		"res"
																				+ j,
																		"",
																		(byte) 0))) {
													dynamicTag
															.setVisibility(View.VISIBLE);
													dynamicTxt
															.setText("应用代号已存在");
													return;
												}
											}
										}
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
                                NewApp(resCode, edit_name.getText().toString(),
										edit_code.getText().toString(),
										groupPosition, childPosition);
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

	protected void NewApp(String oldCode, String appName, String appCode,
			int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		IniFile IniFile = new IniFile();
		String configPath = context.getFilesDir().getParentFile()
				.getAbsolutePath();
		if (configPath.endsWith("/") == false) {

			configPath = configPath + "/";
		}
		configPath = configPath + constants.USER_CONFIG_FILE_NAME;
		String from = IniFile.getIniString(configPath, oldCode, "from", "0",
				(byte) 0);
		String instdir = IniFile.getIniString(configPath, oldCode, "instdir",
				"", (byte) 0);
		IniFile.deleteIniSection(configPath, oldCode);
		IniFile.writeIniString(configPath, appCode, "title", appName);
		IniFile.writeIniString(configPath, appCode, "from", from);
		IniFile.writeIniString(configPath, appCode, "instdir", instdir);
		IniFile.writeIniString(configPath, "group" + (groupPosition + 1), "res"
				+ (childPosition + 1), appCode);
        childs.get(groupPosition).get(childPosition).put("child", appName);
        notifyDataSetChanged();
	}

	protected void DeleteAppDialog(final String apptext, final String appPath,
			final int groupPosition, final int childPosition) {
		// TODO Auto-generated method stub
		new Builder(context).setCancelable(false)
				.setMessage("确定删除:" + apptext + " 应用")// 提示框标题
				.setPositiveButton("确定",// 提示框的两个按钮
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
                                DeleteCategory(apptext, appPath, groupPosition,
										childPosition);
							}
						}).setNegativeButton("取消", null).create().show();
	}

	protected void DeleteMsgDialog(String apptext, final int groupPosition,
			final int childPosition) {
		// TODO Auto-generated method stub
		new Builder(context)
				.setCancelable(false)
				.setMessage("是否一起删除:" + apptext + " 信息")
				// 提示框标题
				.setPositiveButton("确定",// 提示框的两个按钮
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent();
								intent.putExtra("groupPosition", groupPosition);
								intent.putExtra("childPosition", childPosition);
								// childs.remove(childPosition);
								intent.setAction("recommend"
										+ context
												.getString(R.string.about_title));
                                context.sendBroadcast(intent);
								Toast.makeText(context, "应用及信息已删除",
										Toast.LENGTH_LONG).show();
							}
						})
				.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent();
								intent.setAction("recommend"
										+ context
												.getString(R.string.about_title));
                                context.sendBroadcast(intent);
								Toast.makeText(context, "应用已删除",
										Toast.LENGTH_LONG).show();
							}
						}).create().show();
	}

	private void DeleteCategory(String apptext, String appPath,
			int groupPosition, int childPosition) {
		if (!StringUtils.isEmpty(appPath)) {

			if (appPath.startsWith("/")) {
                webRoot = appPath;
			} else {
                webRoot = UIHelper.getStoragePath(context);
                webRoot = webRoot + constants.SD_CARD_TBSSOFT_PATH3 + "/"
						+ appPath;
			}
			if (webRoot.endsWith("/") == false) {
                webRoot += "/";
			}

			File configFile = new File(webRoot);
			if (configFile.exists() == false) {
				Intent intent = new Intent();
				if (Integer.parseInt(childs.get(groupPosition)
						.get(childPosition).get("from")) == 1) {
					intent.putExtra("groupPosition", groupPosition);
					intent.putExtra("childPosition", childPosition);
					Toast.makeText(context, "应用已删除", Toast.LENGTH_LONG).show();
					intent.setAction("recommend"
							+ context.getString(R.string.about_title));
                    context.sendBroadcast(intent);
				} else {
                    DeleteMsgDialog(apptext, groupPosition, childPosition);
				}
				Toast.makeText(context, "应用目录不存在", Toast.LENGTH_LONG).show();
			} else {
				FileUtils.deleteDirectory(webRoot);
				if (configFile.exists() == false) {
					Intent intent = new Intent();
					if (Integer.parseInt(childs.get(groupPosition)
							.get(childPosition).get("from")) == 1) {
						intent.putExtra("groupPosition", groupPosition);
						intent.putExtra("childPosition", childPosition);
						// childs.remove(childPosition);
						intent.setAction("recommend"
								+ context.getString(R.string.about_title));
                        context.sendBroadcast(intent);
						Toast.makeText(context, "应用已删除", Toast.LENGTH_LONG)
								.show();
					} else {
                        DeleteMsgDialog(apptext, groupPosition, childPosition);
					}
				} else {
					Toast.makeText(context, "应用删除失败", Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	private void connect(String CheckedFile, String cateGory) {
		AppGroupAdapter.MyAsyncTask task = new AppGroupAdapter.MyAsyncTask(context, CheckedFile, cateGory);
		task.execute();
	}

	class MyAsyncTask extends AsyncTask<Integer, Integer, String> {
		private final Context context;
		private final String CheckedPath;
		private final IniFile m_iniFileIO;
		private final String cateGory;

		public MyAsyncTask(Context context, String CheckedPath, String cateGory) {
			this.context = context;
            this.CheckedPath = CheckedPath;
            this.cateGory = cateGory;
            m_iniFileIO = new IniFile();
		}

		/**
		 * 这里的Integer参数对应AsyncTask中的第一个参数 这里的String返回值对应AsyncTask的第三个参数
		 * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
		 * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
		 */
		@Override
		protected String doInBackground(Integer... params) {
			String rootPath = UIHelper.getSoftPath(context);
			if (rootPath.endsWith("/") == false) {
				rootPath += "/";
			}
			rootPath += context.getString(R.string.SD_CARD_TBSAPP_PATH2);
			rootPath = UIHelper.getShareperference(context,
					constants.SAVE_INFORMATION, "Path", rootPath);
			if (rootPath.endsWith("/") == false) {
				rootPath += "/";
			}
			String WebIniFile = rootPath + constants.WEB_CONFIG_FILE_NAME;
			rootPath = rootPath
					+ m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
							constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
            String userIni = rootPath;
            if(Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                    "LoginType", "0", (byte) 0)) == 1){
                String dataPath = context.getFilesDir().getParentFile()
                        .getAbsolutePath();
                if (dataPath.endsWith("/") == false) {
                    dataPath = dataPath + "/";
                }
                userIni = dataPath + "TbsApp.ini";
            }
			HttpConnectionUtil connection = new HttpConnectionUtil();
			constants.verifyURL = "http://"
					+ m_iniFileIO
							.getIniString(userIni, "Store", "storeAddress",
									constants.DefaultServerIp, (byte) 0)
					+ ":"
					+ m_iniFileIO.getIniString(userIni, "Store",
							"storePort", constants.DefaultServerPort, (byte) 0)
					+ "/Store/GetAppUrl.cbs";
			return connection.asyncConnect(
					constants.verifyURL,
                    UserRights(m_iniFileIO.getIniString(userIni, "Login",
							"LoginId", "", (byte) 0), m_iniFileIO.getIniString(
                            userIni, "Login", "Account", "", (byte) 0)),
					HttpConnectionUtil.HttpMethod.GET, context);

		}

		private Map<String, String> UserRights(String LoginId, String UserName) {
			// TODO Auto-generated method stub
			Map<String, String> params = new HashMap<String, String>();
			params.put("flag", "getUrl");
			params.put("rePath", CheckedPath);
			params.put("cateGory", cateGory);
			params.put("userName", UserName);
			params.put("login_id", LoginId);
			return params;
		}

		/**
		 * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
		 * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
		 */
		@Override
		protected void onPostExecute(String result) {
			if (Prodialog.isShowing()) {
                Prodialog.dismiss();
			}
			if (result == null) {
				Toast.makeText(context, "错误(E0022)：请检查网络连接是否正常！",
						Toast.LENGTH_LONG).show();

			} else {
				if (Unupdate) {
					if (result.indexOf(";") == -1) {
						Toast.makeText(context, result, Toast.LENGTH_LONG)
								.show();
					} else {
                        //System.out.println(result);
						String url = result.substring(0, result.indexOf(";"));
						String savePath = result
								.substring(result.indexOf(";") + 1);
						FileUtils.downFile(context, url, savePath, true);
					}
				}
			}

		}

		// 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
		@Override
		protected void onPreExecute() {
            Prodialog.show();
		}

		/**
		 * 这里的Intege参数对应AsyncTask中的第二个参数
		 * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行
		 * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {
		}
	}
}