package com.tbs.tbsmis.check;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.Tbszlib.JTbszlib;
import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.FileIO;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackupGroupAdapter extends BaseExpandableListAdapter {
	class ListHolder { // 定义一个内部类，用于保存listitem的3个子视图引用,2个textview和1个checkbox
		public TextView tvName;
	}

	class ExpandableListHolder { // 定义一个内部类，用于保存listitem的3个子视图引用,2个textview和1个checkbox
		public TextView ChildName;
		public TextView text;
		public TextView svrPath;
		public TextView time;
		public TextView dir;
		public RelativeLayout start_Btn;
		public Button delete;
		public Button path;
		public Button server_path;
		public Button edit;
		public ImageView more;
		public LinearLayout btnLayout;
		public RelativeLayout tool;
	}

	private final Context context; // 父activity
	private final LayoutInflater mChildInflater; // 用于加载listitem的布局xml
	private final LayoutInflater mGroupInflater; // 用于加载group的布局xml
	private final ArrayList<Map<String, String>> groups; // 所有group
	private final ArrayList<List<Map<String, String>>> childs; // 所有group
	private final String webRoot;
	private final IniFile m_iniFileIO;
	private AlertDialog ModifyDialog;
	private ProgressBar mProgress;
	private TextView mProgressText;
	private ProgressDialog Prodialog;
	private static boolean Unzip;
	private HttpConnectionUtil connection;
	public static boolean isUnzip() {
		return BackupGroupAdapter.Unzip;
	}

	public static void setUnzip(boolean unzip) {
        BackupGroupAdapter.Unzip = unzip;
	}

	private static boolean Unupdate;

	// 构造方法：参数c － activity，参数group － 所有group

	public static boolean isUnupdate() {
		return BackupGroupAdapter.Unupdate;
	}

	public static void setUnupdate(boolean unupdate) {
        BackupGroupAdapter.Unupdate = unupdate;
	}

	public BackupGroupAdapter(Context c, ArrayList<Map<String, String>> groups,
			ArrayList<List<Map<String, String>>> childs, String webRoot) {
        context = c;
        this.mChildInflater = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mGroupInflater = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.groups = groups;
		this.childs = childs;
		this.webRoot = webRoot;
        this.m_iniFileIO = new IniFile();
	}

	@Override
	public Object getChild(int arg0, int arg1) {// 根据组索引和item索引，取得listitem //
												// TODO Auto-generated method
												// stub
		return this.childs.get(arg0).get(arg1);
	}

	@Override
	public long getChildId(int arg0, int arg1) {// 返回item索引
		return arg1;
	}

	@Override
	public int getChildrenCount(int groupPosition) {// 根据组索引返回分组的子item数
		return this.childs.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {// 根据组索引返回组
		return this.groups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {// 返回分组数
		return this.groups.size();
	}

	@Override
	public long getGroupId(int groupPosition) {// 返回分组索引
		return groupPosition;
	}

	@Override
	public View getGroupView(int position, boolean isExpanded, View view,
			ViewGroup parent) {// 根据组索引渲染"组视图"
		BackupGroupAdapter.ListHolder holder = null; // 清空临时变量holder
		if (view == null) { // 判断view（即view是否已构建好）是否为空
			// 若组视图为空，构建组视图。注意flate的使用，R.layout.browser_expandable_list_item代表了
			// 已加载到内存的browser_expandable_list_item.xml文件
			view = this.mGroupInflater.inflate(R.layout.select_city_item, null);
			// 下面主要是取得组的各子视图，设置子视图的属性。用tag来保存各子视图的引用
			holder = new BackupGroupAdapter.ListHolder();
			// 从view中取得textView
			holder.tvName = (TextView) view.findViewById(R.id.column_title);
			view.setTag(holder);
		} else { // 若view不为空，直接从view的tag属性中获得各子视图的引用
			holder = (BackupGroupAdapter.ListHolder) view.getTag();
		}
		holder.tvName.setText(this.groups.get(position).get("group") + "("
				+ this.groups.get(position).get("groupId") + ")");
		// TODO Auto-generated method stub
		return view;
	}

	// 行渲染方法
	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		final BackupGroupAdapter.ExpandableListHolder holder; // 清空临时变量
		if (convertView == null) { // 若行未初始化
			// 通过flater初始化行视图
			convertView = this.mChildInflater.inflate(R.layout.backup_item, null);
			// 并将行视图的3个子视图引用放到tag中
			holder = new BackupGroupAdapter.ExpandableListHolder();
			holder.ChildName = (TextView) convertView
					.findViewById(R.id.backup_show_text);
			holder.text = (TextView) convertView.findViewById(R.id.backup_name);
			holder.svrPath = (TextView) convertView
					.findViewById(R.id.backup_svrPath);
			holder.time = (TextView) convertView.findViewById(R.id.backup_time);
			holder.dir = (TextView) convertView.findViewById(R.id.backup_dir);
			holder.start_Btn = (RelativeLayout) convertView
					.findViewById(R.id.start_backup);
			holder.edit = (Button) convertView.findViewById(R.id.backup_edit);
			holder.path = (Button) convertView.findViewById(R.id.backup_path);
			holder.server_path = (Button) convertView
					.findViewById(R.id.server_path);
			holder.delete = (Button) convertView
					.findViewById(R.id.backup_delete);
			holder.more = (ImageView) convertView
					.findViewById(R.id.backup_tool_more);
			holder.tool = (RelativeLayout) convertView
					.findViewById(R.id.backup_tool);
			holder.btnLayout = (LinearLayout) convertView
					.findViewById(R.id.backup_tool_Layout);
			convertView.setTag(holder);
		} else { // 若行已初始化，直接从tag属性获得子视图的引用
			holder = (BackupGroupAdapter.ExpandableListHolder) convertView.getTag();
		}
		final String groupid = this.groups.get(groupPosition).get("groupId");
		String all = "备份描述：\n  "
				+ this.m_iniFileIO.getIniString(this.webRoot, groupid, "backDescribe",
						"", (byte) 0);
		holder.ChildName.setText(all);
		all = "";
		int backOption = Integer.parseInt(this.childs.get(groupPosition)
				.get(childPosition).get("backOption"));
		if (backOption == 0) {
			all = "完全备份";
		} else if (backOption == 1) {
			all = "同步更新";
		} else if (backOption == 2) {
			all = "自定义备份";
		}
		holder.text.setText(all);
		int serverDir = Integer.parseInt(this.childs.get(groupPosition)
				.get(childPosition).get("serverDir"));
		if (serverDir == 0) {
			all = "公共目录";
		} else if (serverDir == 1) {
			all = "私人目录";
		}
		holder.svrPath.setText(all
				+ "："
				+ this.m_iniFileIO.getIniString(this.webRoot, groupid, "serverPath", "",
						(byte) 0));
		holder.dir.setText("备份目录："
				+ this.m_iniFileIO.getIniString(this.webRoot, groupid, "backDir", "",
						(byte) 0));
		holder.time.setText("上次备份时间："
				+ this.m_iniFileIO.getIniString(this.webRoot, groupid, "backDate", "",
						(byte) 0));

		// holder.btnLayout.setVisibility(View.GONE);
		holder.more.setBackgroundResource(R.drawable.update_up);
		holder.tool.setOnClickListener(new View.OnClickListener() {
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
		holder.delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                BackupGroupAdapter.this.DeleteBackupDialog(groupPosition);
			}
		});
		holder.edit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                BackupGroupAdapter.this.EditCategoryDialog(groupPosition, childPosition);
			}
		});
		holder.server_path.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int backOption = Integer.parseInt(BackupGroupAdapter.this.childs.get(groupPosition)
						.get(childPosition).get("serverDir"));
				if (backOption == 1) {
					Toast.makeText(BackupGroupAdapter.this.context, "私人目录无法选择公共目录", Toast.LENGTH_SHORT)
							.show();
				} else if (backOption == 0) {
					UIHelper.getDirPath(BackupGroupAdapter.this.context, groupid);
				}
			}
		});
		holder.path.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int backOption = Integer.parseInt(BackupGroupAdapter.this.childs.get(groupPosition)
						.get(childPosition).get("backOption"));
				if (backOption == 0) {
					Toast.makeText(BackupGroupAdapter.this.context, "完全备份无法修改备份目录", Toast.LENGTH_SHORT)
							.show();
				} else if (backOption == 2) {
					UIHelper.showFilePathDialog(BackupGroupAdapter.this.context, 0, null,
							new PathChooseDialog.ChooseCompleteListener() {
								@Override
								public void onComplete(String finalPath) {
									finalPath = finalPath + File.separator;
                                    BackupGroupAdapter.this.m_iniFileIO.writeIniString(BackupGroupAdapter.this.webRoot,
											groupid, "backDir", finalPath);
									holder.dir.setText("备份目录：" + finalPath);
								}
							});
				}
			}
		});

		holder.start_Btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                String userIni = BackupGroupAdapter.this.webRoot;
                if(Integer.parseInt(BackupGroupAdapter.this.m_iniFileIO.getIniString(userIni, "Login",
                        "LoginType", "0", (byte) 0)) == 1){
                    String dataPath = BackupGroupAdapter.this.context.getFilesDir().getParentFile()
                            .getAbsolutePath();
                    if (dataPath.endsWith("/") == false) {
                        dataPath = dataPath + "/";
                    }
                    userIni = dataPath + "TbsApp.ini";
                }
				if (Integer.parseInt(BackupGroupAdapter.this.m_iniFileIO.getIniString(userIni, "Login",
						"LoginFlag", "0", (byte) 0)) == 1) {
					int backOption = Integer.parseInt(BackupGroupAdapter.this.childs.get(groupPosition)
							.get(childPosition).get("backOption"));
					if (backOption == 0) {
						int serverDir = Integer.parseInt(BackupGroupAdapter.this.childs
								.get(groupPosition).get(childPosition)
								.get("serverDir"));
						if (serverDir == 0) {
							if (StringUtils.isEmpty(BackupGroupAdapter.this.m_iniFileIO.getIniString(
                                    BackupGroupAdapter.this.webRoot, groupid, "serverPath", "",
									(byte) 0))) {
								UIHelper.getDirPath(BackupGroupAdapter.this.context, groupid);
								return;
							}

						}
                        BackupGroupAdapter.this.showRinghtsDialog(UIHelper.getShareperference(BackupGroupAdapter.this.context,
								constants.SAVE_INFORMATION, "Path", ""),
								groupid);
                        BackupGroupAdapter.this.m_iniFileIO
								.writeIniString(BackupGroupAdapter.this.webRoot, groupid, "backDir",
										UIHelper.getShareperference(BackupGroupAdapter.this.context,
												constants.SAVE_INFORMATION,
												"Path", ""));
						String date = StringUtils.getDate();
                        BackupGroupAdapter.this.m_iniFileIO.writeIniString(BackupGroupAdapter.this.webRoot, groupid,
								"backDate", date);
						holder.time.setText("上次备份时间：" + date);
						holder.dir.setText("备份目录："
								+ UIHelper.getShareperference(BackupGroupAdapter.this.context,
										constants.SAVE_INFORMATION, "Path", ""));
					} else if (backOption == 2) {
						int serverDir = Integer.parseInt(BackupGroupAdapter.this.childs
								.get(groupPosition).get(childPosition)
								.get("serverDir"));
						if (serverDir == 0) {
							if (StringUtils.isEmpty(BackupGroupAdapter.this.m_iniFileIO.getIniString(
                                    BackupGroupAdapter.this.webRoot, groupid, "serverPath", "",
									(byte) 0))) {
								UIHelper.getDirPath(BackupGroupAdapter.this.context, groupid);
								return;
							}

						}
						if (StringUtils.isEmpty(BackupGroupAdapter.this.m_iniFileIO.getIniString(
                                BackupGroupAdapter.this.webRoot, groupid, "backDir", "", (byte) 0))) {
							UIHelper.showFilePathDialog(BackupGroupAdapter.this.context, 0, null,
									new PathChooseDialog.ChooseCompleteListener() {
										@Override
										public void onComplete(String finalPath) {
											finalPath = finalPath
													+ File.separator;
                                            BackupGroupAdapter.this.showRinghtsDialog(finalPath,
													groupid);
                                            BackupGroupAdapter.this.m_iniFileIO.writeIniString(BackupGroupAdapter.this.webRoot,
													groupid, "backDir",
													finalPath);
											String date = StringUtils.getDate();
                                            BackupGroupAdapter.this.m_iniFileIO.writeIniString(BackupGroupAdapter.this.webRoot,
													groupid, "backDate", date);
											holder.time.setText("上次备份时间："
													+ date);
											holder.dir.setText("备份目录："
													+ finalPath);
										}
									});
						} else {
                            BackupGroupAdapter.this.showRinghtsDialog(BackupGroupAdapter.this.m_iniFileIO.getIniString(BackupGroupAdapter.this.webRoot,
									groupid, "backDir", "", (byte) 0), groupid);
							String date = StringUtils.getDate();
                            BackupGroupAdapter.this.m_iniFileIO.writeIniString(BackupGroupAdapter.this.webRoot, groupid,
									"backDate", date);
							holder.time.setText("上次备份时间：" + date);
						}
					}
				} else {
					UIHelper.showLoginDialog(BackupGroupAdapter.this.context, 0);
				}
			}
		});
		return convertView;
	}

	private void EditCategoryDialog(final int groupPosition,
			final int childPosition) {
		LayoutInflater factory = LayoutInflater.from(this.context);// 提示框
		View view = factory.inflate(R.layout.backup_list_item, null);// 这里必须是final的
		RadioGroup backup_option_server = (RadioGroup) view
				.findViewById(R.id.backup_option_server);
		RadioGroup backup_option_check = (RadioGroup) view
				.findViewById(R.id.backup_option_check);
		final EditText app_show_text = (EditText) view
				.findViewById(R.id.app_show_text);
		final EditText app_show_content = (EditText) view
				.findViewById(R.id.app_show_content);
		final EditText app_show_title = (EditText) view
				.findViewById(R.id.app_show_title);
		RadioButton backup_public = (RadioButton) view
				.findViewById(R.id.backup_public);
		RadioButton backup_user = (RadioButton) view
				.findViewById(R.id.backup_user);
		RadioButton backup_all = (RadioButton) view
				.findViewById(R.id.backup_all);
		RadioButton backup_changed = (RadioButton) view
				.findViewById(R.id.backup_changed);
		RadioButton backup_auto = (RadioButton) view
				.findViewById(R.id.backup_auto);
		int backOption = Integer.parseInt(this.childs.get(groupPosition)
				.get(childPosition).get("backOption"));
		if (backOption == 0) {
			backup_all.setChecked(true);
		} else if (backOption == 1) {
			backup_changed.setChecked(true);
		} else if (backOption == 2) {
			backup_auto.setChecked(true);
		}
		int serverDir = Integer.parseInt(this.childs.get(groupPosition)
				.get(childPosition).get("serverDir"));
		if (serverDir == 0) {
			backup_public.setChecked(true);
		} else if (serverDir == 1) {
			backup_user.setChecked(true);
		}
		app_show_text.setText(this.m_iniFileIO.getIniString(this.webRoot,
                this.groups.get(groupPosition).get("groupId"), "backDescribe", "",
				(byte) 0));
		app_show_content.setText(this.m_iniFileIO.getIniString(this.webRoot,
                this.groups.get(groupPosition).get("groupId"), "backContent", "",
				(byte) 0));
		app_show_title.setText(this.groups.get(groupPosition).get("group"));
		backup_option_server
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// 根据ID判断选择的按钮
						if (checkedId == R.id.backup_public) {
							UIHelper.setSharePerference(BackupGroupAdapter.this.context,
									constants.SAVE_INFORMATION,
									"backup_server_id", 0);
						} else if (checkedId == R.id.backup_user) {
							UIHelper.setSharePerference(BackupGroupAdapter.this.context,
									constants.SAVE_INFORMATION,
									"backup_server_id", 1);
						}
					}
				});
		backup_option_check
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// 根据ID判断选择的按钮
						if (checkedId == R.id.backup_all) {
							UIHelper.setSharePerference(BackupGroupAdapter.this.context,
									constants.SAVE_INFORMATION,
									"backup_check_id", 0);
						} else if (checkedId == R.id.backup_changed) {
							UIHelper.setSharePerference(BackupGroupAdapter.this.context,
									constants.SAVE_INFORMATION,
									"backup_check_id", 1);
						} else if (checkedId == R.id.backup_auto) {
							UIHelper.setSharePerference(BackupGroupAdapter.this.context,
									constants.SAVE_INFORMATION,
									"backup_check_id", 2);
						}
					}
				});
		new Builder(this.context)
				.setTitle(
						"修改“" + this.groups.get(groupPosition).get("group") + "”备份")
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
								if (StringUtils.isEmpty(app_show_title
										.getText().toString())) {
									Toast.makeText(BackupGroupAdapter.this.context, "备份标题不可为空",
											Toast.LENGTH_SHORT).show();
									return;
								}
								if (StringUtils.isEmpty(app_show_content
										.getText().toString())) {
									Toast.makeText(BackupGroupAdapter.this.context, "备份内容不可为空",
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
                                BackupGroupAdapter.this.NewBackup(app_show_title.getText().toString(),
										app_show_content.getText().toString(),
										app_show_text.getText().toString(),
										UIHelper.getShareperference(BackupGroupAdapter.this.context,
												constants.SAVE_INFORMATION,
												"backup_server_id", 0),
										UIHelper.getShareperference(BackupGroupAdapter.this.context,
												constants.SAVE_INFORMATION,
												"backup_check_id", 0),
										groupPosition, childPosition);
								UIHelper.setSharePerference(BackupGroupAdapter.this.context,
										constants.SAVE_INFORMATION,
										"backup_server_id", 0);
								UIHelper.setSharePerference(BackupGroupAdapter.this.context,
										constants.SAVE_INFORMATION,
										"backup_check_id", 0);
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

	protected void NewBackup(String Title, String content, String Describe,
			int server, int check, int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
        this.m_iniFileIO.writeIniString(this.webRoot, "PACK_ALL", "Title"
				+ (groupPosition + 1), Title);
		String packName = this.groups.get(groupPosition).get("groupId");
		// IniFile.writeIniString(appIniFile, packName, "iniPath", packName1);
        this.m_iniFileIO.writeIniString(this.webRoot, packName, "serverDir", server + "");
        this.m_iniFileIO.writeIniString(this.webRoot, packName, "backOption", check + "");
        this.m_iniFileIO.writeIniString(this.webRoot, packName, "backContent", content);
        this.m_iniFileIO.writeIniString(this.webRoot, packName, "backDescribe", Describe);
        this.childs.get(groupPosition).get(childPosition)
				.put("backOption", check + "");
        this.childs.get(groupPosition).get(childPosition)
				.put("serverDir", server + "");
        this.groups.get(groupPosition).put("group", Title);
        this.notifyDataSetChanged();
	}

	protected void DeleteBackupDialog(final int groupPosition) {
		// TODO Auto-generated method stub
		new Builder(this.context)
				.setCancelable(false)
				.setMessage(
						"确定删除:“" + this.groups.get(groupPosition).get("group")
								+ "”备份")// 提示框标题
				.setPositiveButton("确定",// 提示框的两个按钮
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
                                BackupGroupAdapter.this.DeleteBackup(groupPosition + 1);
							}
						}).setNegativeButton("取消", null).create().show();
	}

	protected void DeleteBackup(int groupPosition) {
		// TODO Auto-generated method stub
		String menuName = this.m_iniFileIO.getIniString(this.webRoot, "PACK_ALL", "ID"
				+ groupPosition, "0", (byte) 0);
		int Count = Integer.parseInt(this.m_iniFileIO.getIniString(this.webRoot,
				"PACK_ALL", "Count", "0", (byte) 0));
		for (int i = 1; i <= Count; i++) {
			if (i == groupPosition) {
                this.m_iniFileIO.deleteIniString(this.webRoot, "PACK_ALL", "ID" + i);
                this.m_iniFileIO.deleteIniString(this.webRoot, "PACK_ALL", "Title" + i);
			} else if (i > groupPosition) {
				String resid = this.m_iniFileIO.getIniString(this.webRoot, "PACK_ALL",
						"ID" + i, "", (byte) 0);
				String resname = this.m_iniFileIO.getIniString(this.webRoot, "PACK_ALL",
						"Title" + i, "", (byte) 0);
                this.m_iniFileIO.writeIniString(this.webRoot, "PACK_ALL", "ID" + (i - 1),
						resid);
                this.m_iniFileIO.writeIniString(this.webRoot, "PACK_ALL", "Title"
						+ (i - 1), resname);
			}
		}
        this.m_iniFileIO.deleteIniString(this.webRoot, "PACK_ALL", "ID" + Count);
        this.m_iniFileIO.deleteIniString(this.webRoot, "PACK_ALL", "Title" + Count);
        this.m_iniFileIO.deleteIniSection(this.webRoot, menuName);
        this.m_iniFileIO.writeIniString(this.webRoot, "PACK_ALL", "Count", (Count - 1)
				+ "");
        this.groups.remove(groupPosition - 1);
        this.notifyDataSetChanged();
	}

	private void showDownloadDialog(String CheckedPath, String groupid,
			int serverDir) {
		String webRoot1 = UIHelper.getStoragePath(context);
		String zipFile1 = webRoot1 + constants.SD_CARD_ZIP_PATH2;
		File zip = new File(zipFile1);
		if (!zip.exists()) {
			zip.mkdirs();
		}
		String zipFile;
		String webRoot2 = UIHelper.getStoragePath(context);
		webRoot2 += constants.SD_CARD_TBSSOFT_PATH3;
		int length = webRoot2.length();
		if (CheckedPath.endsWith("/") == false) {
			String packName1 = CheckedPath.substring(CheckedPath
					.lastIndexOf(File.separator) + 1)
					+ "-"
					+ StringUtils.getTime();
			zipFile = zipFile1 + packName1 + ".tbk";
			String pckAbout = "appPack:"
					+ packName1
					+ ".tbk"
					+ "\r\nappName:"
					+ this.m_iniFileIO.getIniString(this.webRoot, "TBSAPP", "AppName",
							"", (byte) 0)
					+ "\r\nappTitle:"
					+ this.m_iniFileIO.getIniString(this.webRoot, groupid, "backContent",
							"", (byte) 0)
					+ "\r\nappCate:"
					+ this.m_iniFileIO.getIniString(this.webRoot, "TBSAPP",
							"AppCategory", "", (byte) 0)
					+ "\r\nappInfo:"
					+ this.m_iniFileIO.getIniString(this.webRoot, groupid,
							"backDescribe", "", (byte) 0)
					+ "\r\nappVer:"
					+ this.m_iniFileIO.getIniString(this.webRoot, "TBSAPP", "AppVersion",
							"1.0", (byte) 0) + "\r\nappDate:"
					+ StringUtils.getDate() + "\r\npackPath:"
					+ CheckedPath.substring(length + 1, CheckedPath.length())
					+ "\r\nappSize:";
			FileIO.CreateTxt(zipFile1, pckAbout, packName1 + ".txt");
		} else {
			CheckedPath = CheckedPath.substring(0,
					CheckedPath.lastIndexOf(File.separator));
			String packName = CheckedPath.substring(CheckedPath
					.lastIndexOf(File.separator) + 1)
					+ "-"
					+ StringUtils.getTime();
			zipFile = zipFile1 + packName + ".tbk";
			String pckAbout = "appPack:"
					+ packName
					+ ".tbk"
					+ "\r\nappName:"
					+ this.m_iniFileIO.getIniString(this.webRoot, "TBSAPP", "AppName",
							"", (byte) 0)
					+ "\r\nappTitle:"
					+ this.m_iniFileIO.getIniString(this.webRoot, groupid, "backContent",
							"", (byte) 0)
					+ "\r\nappCate:"
					+ this.m_iniFileIO.getIniString(this.webRoot, "TBSAPP",
							"AppCategory", "", (byte) 0)
					+ "\r\nappInfo:"
					+ this.m_iniFileIO.getIniString(this.webRoot, groupid,
							"backDescribe", "", (byte) 0)
					+ "\r\nappVer:"
					+ this.m_iniFileIO.getIniString(this.webRoot, "TBSAPP", "AppVersion",
							"1.0", (byte) 0) + "\r\nappDate:"
					+ StringUtils.getDate() + "\r\npackPath:"
					+ CheckedPath.substring(length + 1, CheckedPath.length())
					+ "\r\nappSize:";
			FileIO.CreateTxt(zipFile1, pckAbout, packName + ".txt");
		}
		Builder builder = new AlertDialog.Builder(this.context);
		builder.setTitle("正在压缩文件");
		builder.setCancelable(false);
		LayoutInflater inflater = LayoutInflater.from(this.context);
		View v = inflater.inflate(R.layout.update_progress, null);
        this.mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
        this.mProgressText = (TextView) v.findViewById(R.id.update_progress_text);
		builder.setView(v);
		builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
                BackupGroupAdapter.setUnzip(false);
				dialog.dismiss();

			}
		});
        this.ModifyDialog = builder.create();
        this.ModifyDialog.setCanceledOnTouchOutside(false);
        this.ModifyDialog.show();
		List<String> fileList = new ArrayList<String>();
		FileUtils.getDirFiles(CheckedPath, fileList);
		if (fileList.isEmpty()) {
			Toast.makeText(this.context, CheckedPath + "目录为空", Toast.LENGTH_SHORT)
					.show();
		} else {
            this.connect(fileList, zipFile, 0, this.m_iniFileIO.getIniString(this.webRoot,
					groupid, "serverPath", "其他", (byte) 0), serverDir);
		}

	}

	@SuppressWarnings("deprecation")
	private void showRinghtsDialog(String CheckedPath, String groupid) {
        this.Prodialog = new ProgressDialog(this.context);
        this.Prodialog.setTitle("验证");
        this.Prodialog.setMessage("正在验证权限，请稍候...");
        this.Prodialog.setIndeterminate(false);
        this.Prodialog.setCanceledOnTouchOutside(false);
        this.Prodialog.setButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method
				// stub
                BackupGroupAdapter.setUnzip(false);
				dialog.dismiss();
				// Unupdate = false;
			}
		});
		int serverDir = Integer.parseInt(this.m_iniFileIO.getIniString(this.webRoot,
				groupid, "serverDir", "0", (byte) 0));
        BackupGroupAdapter.setUnzip(true);
        this.connect(groupid, 3, CheckedPath, serverDir);
	}

	@SuppressWarnings("deprecation")
	private void showModifyDialog(String FilePath, int count,
			String CheckedPath, int serverDir) {
        this.Prodialog = new ProgressDialog(this.context);
        this.Prodialog.setTitle("上传应用包");
        this.Prodialog.setMessage("正在上传，请稍候...");
        this.Prodialog.setIndeterminate(false);
        this.Prodialog.setCanceledOnTouchOutside(false);
        this.Prodialog.setButton("取消", new DialogInterface.OnClickListener() {
			@SuppressWarnings("static-access")
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method
				// stub
                BackupGroupAdapter.setUnupdate(false);
                BackupGroupAdapter.this.connection.setUnupdata(false);
				dialog.dismiss();

			}
		});
        BackupGroupAdapter.setUnupdate(true);
        this.connect(FilePath, count, CheckedPath, serverDir);
	}

	@Override
	public boolean hasStableIds() {// 行是否具有唯一id
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {// 行是否可选
		return true;
	}

	private void connect(List<String> CheckedFile, String zipFile, int count,
			String CheckedPath, int serverDir) {
		BackupGroupAdapter.MyAsyncTask task = new BackupGroupAdapter.MyAsyncTask(CheckedFile, zipFile, count,
                this.context, CheckedPath, serverDir);
		task.execute();
	}

	private void connect(String CheckedFile, int count, String CheckedPath,
			int serverDir) {
		BackupGroupAdapter.MyAsyncTask task = new BackupGroupAdapter.MyAsyncTask(CheckedFile, count, this.context,
				CheckedPath, serverDir);
		task.execute();
	}

	class MyAsyncTask extends AsyncTask<Integer, Integer, String> {

		private List<String> CheckedFile;
		private String FilePath;
		private final Context context;
		private final int count;
		private String zipFile;
		private String CheckedPath;
		private final int serverDir;
		private final PowerManager.WakeLock wakeLock = null;

		public MyAsyncTask(List<String> CheckedFile, String zipFile, int count,
				Context context, String CheckedPath, int serverDir) {
			this.CheckedFile = CheckedFile;
			this.zipFile = zipFile;
			this.context = context;
			this.count = count;
			this.CheckedPath = CheckedPath;
			this.serverDir = serverDir;
			UIHelper.acquireWakeLock(context, this.wakeLock);
		}

		public MyAsyncTask(String CheckedFile, int count, Context context,
				String CheckedPath, int serverDir) {
            FilePath = CheckedFile;
			this.context = context;
			this.count = count;
			this.CheckedPath = CheckedPath;
			this.serverDir = serverDir;
			UIHelper.acquireWakeLock(context, this.wakeLock);
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
			rootPath += this.context.getString(R.string.SD_CARD_TBSAPP_PATH2);
			rootPath = UIHelper.getShareperference(this.context,
					constants.SAVE_INFORMATION, "Path", rootPath);
			if (rootPath.endsWith("/") == false) {
				rootPath += "/";
			}
			String WebIniFile = rootPath + constants.WEB_CONFIG_FILE_NAME;
			rootPath = rootPath
					+ BackupGroupAdapter.this.m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
							constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
            String userIni = rootPath;
            if(Integer.parseInt(BackupGroupAdapter.this.m_iniFileIO.getIniString(userIni, "Login",
                    "LoginType", "0", (byte) 0)) == 1){
                String dataPath = this.context.getFilesDir().getParentFile()
                        .getAbsolutePath();
                if (dataPath.endsWith("/") == false) {
                    dataPath = dataPath + "/";
                }
                userIni = dataPath + "TbsApp.ini";
            }
            BackupGroupAdapter.this.connection = new HttpConnectionUtil();
			switch (this.count) {
			case 3:
				if (this.serverDir == 0) {
					constants.verifyURL = "http://"
							+ BackupGroupAdapter.this.m_iniFileIO.getIniString(userIni, "Store",
									"storeAddress", constants.DefaultServerIp,
									(byte) 0)
							+ ":"
							+ BackupGroupAdapter.this.m_iniFileIO.getIniString(userIni, "Store",
									"storePort", constants.DefaultServerPort,
									(byte) 0) + m_iniFileIO.getIniString(userIni, "Store",
                            "storeUploadPath", "/Store/Upload.cbs",
                            (byte) 0);
				} else if (this.serverDir == 1) {
					constants.verifyURL = "http://"
							+ BackupGroupAdapter.this.m_iniFileIO.getIniString(userIni, "Backup",
									"personalAddress",
									constants.DefaultServerIp, (byte) 0)
							+ ":"
							+ BackupGroupAdapter.this.m_iniFileIO.getIniString(userIni, "Backup",
									"personalPort",
									constants.DefaultServerPort, (byte) 0)
							+ m_iniFileIO.getIniString(userIni, "Backup",
                            "personalPath", "/SkyDrive/SaveFile.cbs",
                            (byte) 0);
				}
				// System.out.println("constants.verifyURL ="
				// + constants.verifyURL);
				return BackupGroupAdapter.this.connection.asyncConnect(
						constants.verifyURL,
                        this.UserRights(BackupGroupAdapter.this.m_iniFileIO.getIniString(userIni, "Login",
								"LoginId", "", (byte) 0), BackupGroupAdapter.this.m_iniFileIO
								.getIniString(userIni, "Login", "Account", "",
										(byte) 0)), HttpConnectionUtil.HttpMethod.GET, this.context);
			case 0:

				int i = -1;
				long Openzip = JTbszlib.OpenZip(this.zipFile, 0);
				for (String FilePath : this.CheckedFile) {
					if (BackupGroupAdapter.isUnzip()) {
						i = i + 1;
                        this.publishProgress(i, this.CheckedFile.size());
						File fs = new File(FilePath);
						int length = UIHelper.getShareperference(this.context,
								constants.SAVE_INFORMATION, "Path", "")
								.length();
						String rootPath1 = FilePath.substring(length - 1,
								FilePath.length());
						if (fs.isDirectory()) {
							JTbszlib.EnZipDir(FilePath, Openzip, 1, 1, "",
									rootPath1);
						} else {
							if (rootPath1.indexOf("/") != -1) {
								rootPath1 = rootPath1.substring(0,
										rootPath1.lastIndexOf("/"));
							}
							if (rootPath1.endsWith("/")) {
								rootPath1 = rootPath1.substring(0,
										rootPath1.length() - 1);
							}
							JTbszlib.EnZipFile(FilePath, rootPath1, Openzip, 1,
									1, "");
						}
					} else {
						JTbszlib.CloseZip(Openzip);
                        this.CheckedFile.clear();
						// downloadDialog.dismiss();
						return this.zipFile;
					}
				}
                this.publishProgress(i + 1, this.CheckedFile.size());
				JTbszlib.CloseZip(Openzip);
                this.CheckedFile.clear();
				// downloadDialog.dismiss();
				return this.zipFile;
			case 1:
				if (this.serverDir == 0) {
                    constants.verifyURL = "http://"
                            + BackupGroupAdapter.this.m_iniFileIO.getIniString(userIni, "Store",
                            "storeAddress", constants.DefaultServerIp,
                            (byte) 0)
                            + ":"
                            + BackupGroupAdapter.this.m_iniFileIO.getIniString(userIni, "Store",
                            "storePort", constants.DefaultServerPort,
                            (byte) 0) + m_iniFileIO.getIniString(userIni, "Store",
                            "storeUploadPath", "/Store/Upload.cbs",
                            (byte) 0);
				} else if (this.serverDir == 1) {
                    this.CheckedPath = "";
                    constants.verifyURL = "http://"
                            + BackupGroupAdapter.this.m_iniFileIO.getIniString(userIni, "Backup",
                            "personalAddress",
                            constants.DefaultServerIp, (byte) 0)
                            + ":"
                            + BackupGroupAdapter.this.m_iniFileIO.getIniString(userIni, "Backup",
                            "personalPort",
                            constants.DefaultServerPort, (byte) 0)
                            + m_iniFileIO.getIniString(userIni, "Backup",
                            "personalPath", "/SkyDrive/SaveFile.cbs",
                            (byte) 0);
				}

				// connection.asyncConnect(
				// constants.verifyURL,
				// newDir(rootPath, m_iniFileIO.getIniString(rootPath,
				// "Login", "Account", "", (byte) 0)),
				// HttpMethod.POST, context);

				return BackupGroupAdapter.this.connection.asyncConnect(
						constants.verifyURL,
                        this.UpdateFile(BackupGroupAdapter.this.m_iniFileIO.getIniString(userIni, "Login",
								"LoginId", "", (byte) 0), BackupGroupAdapter.this.m_iniFileIO
								.getIniString(userIni, "Login", "Account", "",
										(byte) 0), this.CheckedPath), this.FilePath,
                        this.context);
			case 2:
				if (this.serverDir == 0) {
                    constants.verifyURL = "http://"
                            + m_iniFileIO.getIniString(userIni, "Store",
                            "storeAddress", constants.DefaultServerIp,
                            (byte) 0)
                            + ":"
                            + m_iniFileIO.getIniString(userIni, "Store",
                            "storePort", constants.DefaultServerPort,
                            (byte) 0) + m_iniFileIO.getIniString(userIni, "Store",
                            "storeUploadPath", "/Store/Upload.cbs",
                            (byte) 0);
				} else if (this.serverDir == 1) {
                    constants.verifyURL = "http://"
                            + BackupGroupAdapter.this.m_iniFileIO.getIniString(userIni, "Backup",
                            "personalAddress",
                            constants.DefaultServerIp, (byte) 0)
                            + ":"
                            + BackupGroupAdapter.this.m_iniFileIO.getIniString(userIni, "Backup",
                            "personalPort",
                            constants.DefaultServerPort, (byte) 0)
                            + m_iniFileIO.getIniString(userIni, "Backup",
                            "personalPath", "/SkyDrive/SaveFile.cbs",
                            (byte) 0);
				}
				return BackupGroupAdapter.this.connection.asyncConnect(
						constants.verifyURL,
                        this.UpdateFile(BackupGroupAdapter.this.m_iniFileIO.getIniString(userIni, "Login",
								"LoginId", "", (byte) 0), BackupGroupAdapter.this.m_iniFileIO
								.getIniString(userIni, "Login", "Account", "",
										(byte) 0), this.CheckedPath), this.FilePath,
                        this.context);
			}
			return null;
		}

		private Map<String, String> UpdateFile(String LoginId, String UserName,
				String Path) {
			// TODO Auto-generated method stub
			Map<String, String> params = new HashMap<String, String>();
			// params.put("action", "SaveFile.cbs");
			params.put("flag", "upload");
			params.put("rePath", Path);
			params.put("filepath", this.FilePath);
			params.put("userName", UserName);
			params.put("login_id", LoginId);
			return params;
		}

		private Map<String, String> UserRights(String LoginId, String UserName) {
			// TODO Auto-generated method stub
			Map<String, String> params = new HashMap<String, String>();
			params.put("flag", "getRights");
			params.put("rePath", "");
			params.put("filepath", "");
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
			//
			if (this.count == 3) {
				if (result.equalsIgnoreCase("true")&& BackupGroupAdapter.isUnzip()) {
                    BackupGroupAdapter.this.Prodialog.dismiss();
                    BackupGroupAdapter.this.showDownloadDialog(this.CheckedPath, this.FilePath, this.serverDir);
					// connect(CheckedFile, zipFile, 0, CheckedPath, serverDir);
					// connect(FilePath, path, 0);
				} else {
                    BackupGroupAdapter.this.Prodialog.dismiss();
					Toast.makeText(this.context, "您还没有上传权限，无法上传文件！",
							Toast.LENGTH_LONG).show();
				}
			} else if (this.count == 1 && BackupGroupAdapter.isUnupdate()) {
				FileUtils.deleteFileWithPath(this.FilePath);
				if (this.FilePath.indexOf(".") > 0) {
                    this.FilePath = this.FilePath.substring(0, this.FilePath.indexOf("."))
							+ ".txt";
				}
                BackupGroupAdapter.this.connect(this.FilePath, this.count + 1, this.CheckedPath, this.serverDir);
			} else if (this.count == 0 && BackupGroupAdapter.isUnzip()) {

				if (BackupGroupAdapter.this.ModifyDialog.isShowing()) {
                    BackupGroupAdapter.this.ModifyDialog.dismiss();
                    BackupGroupAdapter.this.showModifyDialog(result, this.count + 1, this.CheckedPath, this.serverDir);
				}
			} else if (this.count == 2) {
				if (BackupGroupAdapter.this.Prodialog.isShowing()) {
                    BackupGroupAdapter.this.Prodialog.dismiss();
				}
				FileUtils.deleteFileWithPath(this.FilePath);
				Toast.makeText(this.context, result, Toast.LENGTH_LONG).show();
			} else {
				if (BackupGroupAdapter.this.Prodialog.isShowing()) {
                    BackupGroupAdapter.this.Prodialog.dismiss();
				}
				FileUtils.deleteFileWithPath(result);
				FileUtils.deleteFileWithPath(this.FilePath);
				Toast.makeText(this.context, result, Toast.LENGTH_LONG).show();
			}
			UIHelper.releaseWakeLock(this.wakeLock);
		}

		// 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
		@Override
		protected void onPreExecute() {
			if (this.count == 1 || this.count == 3) {
				if (!BackupGroupAdapter.this.Prodialog.isShowing()) {
                    BackupGroupAdapter.this.Prodialog.show();
				}
			}else if(this.count == 0){
                BackupGroupAdapter.this.mProgress.setMax(this.CheckedFile.size());
            }
		}

		/**
		 * 这里的Intege参数对应AsyncTask中的第二个参数
		 * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行
		 * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {
			if (this.count == 0 && BackupGroupAdapter.isUnzip()) {
                BackupGroupAdapter.this.mProgressText.setText(values[0] + "/" + values[1]);
                BackupGroupAdapter.this.mProgress.setProgress(values[0]);
			}
		}
	}
}