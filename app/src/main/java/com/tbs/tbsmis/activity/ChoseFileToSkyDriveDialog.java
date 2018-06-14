package com.tbs.tbsmis.activity;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.Tbszlib.JTbszlib;
import com.tbs.ini.IniFile;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.check.PopMenuAdapter;
import com.tbs.tbsmis.check.UpFileAdapter;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.file.TextInputDialog;
import com.tbs.tbsmis.file.Util;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * 路径选择弹窗 上传文件到网盘
 * 
 * @author @yeah.net 修改 yzt
 * 
 */
public class ChoseFileToSkyDriveDialog extends Activity {
	private ListView expandableList;
	private Button cancleBtn;
	private Button setBtn;
	// private UpChooseListener listener;
	private TextView tvCurPath;
	private List<String> data;
	private final Stack<String> pathStack = new Stack<String>();
	private UpFileAdapter listAdapter;
	private int firstIndex;
	protected int checkNum;
	private String Path;
	private ProgressDialog Prodialog;
	protected String currentPath;
	private ImageView backBtn;
	private boolean isOpenPop;
	private ImageView path_up_level;
	private int lenght;
	private View mDropdownNavigation;
	private ImageView orderBtn;
	private RelativeLayout up_file_title;
	private LinearLayout layoutButton;
	private static boolean Unupdate;
	private static boolean isUnzip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.up_file_path);
		MyActivity.getInstance().addActivity(this);
        this.init();
	}

	private void init() {
		// TODO Auto-generated method stub
        this.backBtn = (ImageView) this.findViewById(R.id.up_file_back);
        this.orderBtn = (ImageView) this.findViewById(R.id.file_order);
        this.setBtn = (Button) this.findViewById(R.id.up_file);
        this.cancleBtn = (Button) this.findViewById(R.id.cancleBtn);
        this.path_up_level = (ImageView) this.findViewById(R.id.path_pane_up_level);
        this.tvCurPath = (TextView) this.findViewById(R.id.up_cur_path);
        this.expandableList = (ListView) this.findViewById(android.R.id.list);
        this.up_file_title = (RelativeLayout) this.findViewById(R.id.up_file_title);
        this.layoutButton = (LinearLayout) this.findViewById(R.id.layoutButton);
        this.initData();
		if (this.getIntent().getExtras() != null) {
			Intent intent = this.getIntent();
            this.Path = intent.getStringExtra("Path");
		}
        this.setupNaivgationBar();
		// adapter = new ListViewAdapter(context, dataList);
		// expandableList.setAdapter(adapter);
        this.path_up_level.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ChoseFileToSkyDriveDialog.this.mDropdownNavigation.getVisibility() == View.VISIBLE) {
                    ChoseFileToSkyDriveDialog.this.showDropdownNavigation(false);
				}
                ChoseFileToSkyDriveDialog.this.checkNum = 0;
				if (ChoseFileToSkyDriveDialog.this.pathStack.size() > 2) {
                    ChoseFileToSkyDriveDialog.this.pathStack.pop();
                    ChoseFileToSkyDriveDialog.this.data = FileUtils.listAll(ChoseFileToSkyDriveDialog.this.pathStack.peek());
                    ChoseFileToSkyDriveDialog.this.tvCurPath.setText(ChoseFileToSkyDriveDialog.this.pathStack.peek());
                    ChoseFileToSkyDriveDialog.this.refleshListView(ChoseFileToSkyDriveDialog.this.data, ChoseFileToSkyDriveDialog.this.firstIndex);
				} else {
                    ChoseFileToSkyDriveDialog.this.path_up_level.setClickable(false);
				}
			}
		});
		// 取消
        this.backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                ChoseFileToSkyDriveDialog.this.finish();
			}
		});
        this.orderBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                ChoseFileToSkyDriveDialog.this.changMenu(v);
			}
		});
        this.cancleBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                ChoseFileToSkyDriveDialog.this.finish();
			}
		});
        this.setBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ChoseFileToSkyDriveDialog.this.listAdapter.getSelected().size() < 1) {
					Toast.makeText(ChoseFileToSkyDriveDialog.this, "请至少选择一个上传文件",
							Toast.LENGTH_LONG).show();
				} else {
                    ChoseFileToSkyDriveDialog.this.showModifyDialog(ChoseFileToSkyDriveDialog.this.listAdapter.getSelected(), ChoseFileToSkyDriveDialog.this.tvCurPath
							.getText().toString());
				}
			}
		});
		// 单击
        this.expandableList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (ChoseFileToSkyDriveDialog.this.mDropdownNavigation.getVisibility() == View.VISIBLE) {
                    ChoseFileToSkyDriveDialog.this.showDropdownNavigation(false);
				}
				UpFileAdapter.ViewHolder holder = (UpFileAdapter.ViewHolder) view.getTag();
				String Path = ChoseFileToSkyDriveDialog.this.data.get(position);
				File isFile = new File(Path);
				if (ChoseFileToSkyDriveDialog.this.checkNum == 1) {
					if (isFile.isDirectory()) {
                        ChoseFileToSkyDriveDialog.this.checkNum = 0;
						try {
                            ChoseFileToSkyDriveDialog.this.data = FileUtils.listAll(Path);
                            ChoseFileToSkyDriveDialog.this.tvCurPath.setText(Path);
                            ChoseFileToSkyDriveDialog.this.pathStack.add(Path);
                            ChoseFileToSkyDriveDialog.this.refleshListView(ChoseFileToSkyDriveDialog.this.data, ChoseFileToSkyDriveDialog.this.pathStack.size() - 1);
						} catch (Exception e) {
							e.printStackTrace();
						}
						// setBtn.setEnabled(false);
						// layoutButton.setVisibility(View.GONE);
					} else {
						if (holder.children_cb.isChecked() == true) {
							holder.children_cb.toggle();
                            ChoseFileToSkyDriveDialog.this.checkNum--;
                            ChoseFileToSkyDriveDialog.this.listAdapter.getIsSelected().put(position,
									holder.children_cb.isChecked());
							// setBtn.setEnabled(false);
							// layoutButton.setVisibility(View.GONE);
						} else {
							holder.children_cb.toggle();
                            ChoseFileToSkyDriveDialog.this.checkNum++;
                            ChoseFileToSkyDriveDialog.this.listAdapter.getIsSelected().put(position,
									holder.children_cb.isChecked());
						}
					}
				} else {
					if (isFile.isDirectory()) {
                        ChoseFileToSkyDriveDialog.this.checkNum = 0;
						try {
                            ChoseFileToSkyDriveDialog.this.data = FileUtils.listAll(Path);
                            ChoseFileToSkyDriveDialog.this.tvCurPath.setText(Path);
                            ChoseFileToSkyDriveDialog.this.pathStack.add(Path);
                            ChoseFileToSkyDriveDialog.this.refleshListView(ChoseFileToSkyDriveDialog.this.data, ChoseFileToSkyDriveDialog.this.pathStack.size() - 1);
						} catch (Exception e) {
							e.printStackTrace();
						}
						// setBtn.setEnabled(false);
						// layoutButton.setVisibility(View.GONE);
					} else {
						// 改变CheckBox的状态
						holder.children_cb.toggle();
						// 将CheckBox的选中状况记录下来
                        ChoseFileToSkyDriveDialog.this.listAdapter.getIsSelected().put(position,
								holder.children_cb.isChecked());
						// 调整选定条目
						if (holder.children_cb.isChecked() == true) {
                            ChoseFileToSkyDriveDialog.this.checkNum++;
                            ChoseFileToSkyDriveDialog.this.currentPath = Path;
						} else {
                            ChoseFileToSkyDriveDialog.this.checkNum--;
						}
						// setBtn.setEnabled(true);
					}
				}
                ChoseFileToSkyDriveDialog.this.firstIndex = position;
			}
		});
	}

	protected void onNavigationBarClick() {
		if (this.mDropdownNavigation.getVisibility() == View.VISIBLE) {
            this.showDropdownNavigation(false);
		} else {
			LinearLayout list = (LinearLayout) this.mDropdownNavigation
					.findViewById(R.id.dropdown_navigation_list);
			list.removeAllViews();
			int pos = 0;
			String displayPath = this.tvCurPath.getText().toString();
			boolean root = true;
			int left = 0;
			while (pos != -1 && !displayPath.equals("/")) {// 如果当前位置在根文件夹则不显示导航条
				int end = displayPath.indexOf("/", pos);
				if (end == -1)
					break;

				View listItem = LayoutInflater.from(this).inflate(
                        R.layout.dropdown_item, null);

				View listContent = listItem.findViewById(R.id.list_item);
				listContent.setPadding(left, 0, 0, 0);
				left += 20;
				ImageView img = (ImageView) listItem
						.findViewById(R.id.item_icon);

				img.setImageResource(root ? R.drawable.dropdown_icon_root
						: R.drawable.dropdown_icon_folder);
				root = false;

				TextView text = (TextView) listItem
						.findViewById(R.id.path_name);
				String substring = displayPath.substring(pos, end);
				if (substring.isEmpty())
					substring = "/";
				text.setText(substring);

				listItem.setOnClickListener(this.navigationClick);
				listItem.setTag(displayPath.substring(0, end));
				pos = end + 1;
				list.addView(listItem);
			}
			if (list.getChildCount() > 0)
                this.showDropdownNavigation(true);
		}
	}

	private final View.OnClickListener navigationClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String path = (String) v.getTag();
			assert path != null;
            ChoseFileToSkyDriveDialog.this.showDropdownNavigation(false);
			if (path.isEmpty()) {
				path = "/";
			}
            ChoseFileToSkyDriveDialog.this.addPath(path);
            ChoseFileToSkyDriveDialog.this.data = FileUtils.listAll(path);
            ChoseFileToSkyDriveDialog.this.tvCurPath.setText(path);
            ChoseFileToSkyDriveDialog.this.refleshListView(ChoseFileToSkyDriveDialog.this.data, ChoseFileToSkyDriveDialog.this.firstIndex);
		}

	};
	private TextView mNavigationBarText;
	private ImageView mNavigationBarUpDownArrow;
	private PopupWindow SetWindow;

	private void setupNaivgationBar() {
		// mNavigationBar = ()getViewById(R.id.navigation_bar);
        this.mNavigationBarText = (TextView) this.findViewById(R.id.current_path_view);
        this.mNavigationBarUpDownArrow = (ImageView) this.findViewById(R.id.path_pane_arrow);
		View clickable = this.findViewById(R.id.current_path_pane);
		clickable.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                ChoseFileToSkyDriveDialog.this.onNavigationBarClick();
			}
		});

        this.mDropdownNavigation = this.findViewById(R.id.dropdown_navigation);

		// setupClick(mNavigationBar, R.id.path_pane_up_level);
	}

	private void showDropdownNavigation(boolean show) {
        this.mDropdownNavigation.setVisibility(show ? View.VISIBLE : View.GONE);
        this.mNavigationBarUpDownArrow.setImageResource(this.mDropdownNavigation
				.getVisibility() == View.VISIBLE ? R.drawable.arrow_up
				: R.drawable.arrow_down);
	}

	private void initData() {
		String webRoot = UIHelper.getSoftPath(this);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
		webRoot += getString(R.string.SD_CARD_TBSAPP_PATH2);
		webRoot = UIHelper.getShareperference(this, constants.SAVE_INFORMATION,
				"Path", webRoot);
		if (webRoot.endsWith("/") == false) {
			webRoot += "/";
		}
        this.lenght = webRoot.length();
        this.addPath(webRoot);
        this.pathStack.add(webRoot);
        this.data = FileUtils.listAll(webRoot);
        this.tvCurPath.setText(webRoot);
        this.refleshListView(this.data, 0);
	}

	/**
	 * 更新listView视图
	 *
	 * @param data
	 */
	private void refleshListView(List<String> data, int firstItem) {
		String lost = FileUtils.getSDRoot() + "lost+found";
		data.remove(lost);
        this.path_up_level.setClickable(true);
		if (!data.isEmpty()) {
			Collections.sort(data, new Comparator<String>() {
				@Override
				public int compare(String object1, String object2) {
					File o1 = new File(object1);
					File o2 = new File(object2);
					// 根据字段"LEVEL"排序
					if (o1.isDirectory() == o2.isDirectory()) {
						int order = UIHelper.getShareperference(
								ChoseFileToSkyDriveDialog.this,
								constants.SAVE_LOCALMSGNUM, "sort", 0);
						if (order == 0) {
							return object1.compareToIgnoreCase(object2);
						} else if (order == 1) {
							return ChoseFileToSkyDriveDialog.this.longToCompareInt(o1.length() - o2.length());
						} else if (order == 2) {
							return ChoseFileToSkyDriveDialog.this.longToCompareInt(o1.lastModified()
									- o2.lastModified());
						} else if (order == 3) {
							int result = Util.getExtFromFilename(object1)
									.compareToIgnoreCase(
											Util.getExtFromFilename(object2));
							if (result != 0)
								return result;
							return Util.getNameFromFilename(object1)
									.compareToIgnoreCase(
											Util.getNameFromFilename(object2));
						}
					}
					return o1.isDirectory() ? -1 : 1;
				}
			});
		}
        this.listAdapter = new UpFileAdapter(data, this);
        this.expandableList.setAdapter(this.listAdapter);
        this.expandableList.setSelection(firstItem);
	}

	private int longToCompareInt(long result) {
		return result > 0 ? 1 : result < 0 ? -1 : 0;
	}

	private void addPath(String webRoot) {
		// TODO Auto-generated method stub
		if (this.pathStack.size() > 1) {
            this.pathStack.clear();
		}
		int start = 0;
		String rootPath = "/";
		int iIndex = webRoot.indexOf("/", start);
		while (iIndex >= 0) {
			if (iIndex == 0) {
				start = iIndex + 1;
				rootPath = webRoot.substring(0, start);
			} else {
				start = iIndex + 1;
				rootPath = webRoot.substring(0, iIndex);
			}
			iIndex = webRoot.indexOf("/", start);
            this.pathStack.add(rootPath);
		}
	}

	public void changMenu(View v) {
        this.isOpenPop = !this.isOpenPop;
		if (this.isOpenPop) {
            this.popWindow_Menu(v);
		} else {
			if (this.SetWindow != null) {
                this.SetWindow.dismiss();
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void popWindow_Menu(View parent) {
		// iniPath();
		String[] sortValue = { "排序", "新建", "刷新" };
		ArrayList<Map<String, String>> MenuList = new ArrayList<Map<String, String>>();
		LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = lay.inflate(R.layout.main_menu_prefrences, null);
		// iniPath();
		// ImageView avatar_iv = (ImageView) view.findViewById(R.id.userimage);
		// TextView nickname_tv = (TextView)
		// view.findViewById(R.id.usernickname);
		// TextView district = (TextView) view.findViewById(R.id.useraccount);
		LinearLayout userinfo = (LinearLayout) view.findViewById(R.id.userinfo);
		ListView menu_list = (ListView) view.findViewById(R.id.listMenuItems);
		userinfo.setVisibility(View.GONE);

		for (int i = 0; i < sortValue.length; i++) {
			Map<String, String> group = new HashMap<String, String>();
			group.put("Title", sortValue[i]);
			group.put("Icon", "0");
			MenuList.add(group);
		}

		PopMenuAdapter MenuListAdapter = new PopMenuAdapter(MenuList,
                this);
		menu_list.setAdapter(MenuListAdapter);
		// ��listView�ļ�����
		menu_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				if (arg2 == 0) {
                    ChoseFileToSkyDriveDialog.this.showSortDialog();
				} else if (arg2 == 1) {
                    ChoseFileToSkyDriveDialog.this.showNewDialog();
				} else if (arg2 == 2) {
                    ChoseFileToSkyDriveDialog.this.data = FileUtils.listAll(ChoseFileToSkyDriveDialog.this.tvCurPath.getText().toString());
                    ChoseFileToSkyDriveDialog.this.refleshListView(ChoseFileToSkyDriveDialog.this.data, ChoseFileToSkyDriveDialog.this.pathStack.size() - 1);
				}
                ChoseFileToSkyDriveDialog.this.SetWindow.dismiss();
			}
		});
        this.SetWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		// �˶�ʵ�ֵ���հ״�����popwindow
        this.SetWindow.setFocusable(true);
        this.SetWindow.setOutsideTouchable(false);
        this.SetWindow.setBackgroundDrawable(new BitmapDrawable());
        this.SetWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

			@Override
			public void onDismiss() {
                ChoseFileToSkyDriveDialog.this.isOpenPop = false;
			}
		});

        this.SetWindow.showAtLocation(parent, Gravity.RIGHT | Gravity.TOP, 10,
                this.up_file_title.getHeight() * 3 / 2);
        this.SetWindow.update();
	}

	protected void showNewDialog() {
		// TODO Auto-generated method stub
		TextInputDialog dialog = new TextInputDialog(this,
                getString(R.string.operation_create_folder),
                getString(R.string.operation_create_folder_message),
                getString(R.string.new_folder_name),
				new TextInputDialog.OnFinishListener() {
					@Override
					public boolean onFinish(String text) {
						if (TextUtils.isEmpty(text))
							return false;
						File f = new File(Util.makePath(ChoseFileToSkyDriveDialog.this.tvCurPath.getText()
								.toString(), text));
						if (f.exists()) {
							new Builder(ChoseFileToSkyDriveDialog.this)
									.setMessage(
                                            getString(R.string.fail_to_create_folder))
									.setPositiveButton(R.string.confirm, null)
									.create().show();
							return false;
						} else {
                            ChoseFileToSkyDriveDialog.this.data = FileUtils.listAll(ChoseFileToSkyDriveDialog.this.tvCurPath.getText()
									.toString());
                            ChoseFileToSkyDriveDialog.this.refleshListView(ChoseFileToSkyDriveDialog.this.data, ChoseFileToSkyDriveDialog.this.pathStack.size() - 1);
							return f.mkdirs();
						}
					}
				});

		dialog.show();
	}

	protected void showSortDialog() {
		// TODO Auto-generated method stub
		new Builder(this)
				.setTitle("选择类型")
				.setSingleChoiceItems(
                        R.array.sort_options,
						UIHelper.getShareperference(this,
								constants.SAVE_LOCALMSGNUM, "sort", 0),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								UIHelper.setSharePerference(
										ChoseFileToSkyDriveDialog.this,
										constants.SAVE_LOCALMSGNUM, "sort",
										which);
                                ChoseFileToSkyDriveDialog.this.refleshListView(ChoseFileToSkyDriveDialog.this.data, ChoseFileToSkyDriveDialog.this.firstIndex);
								dialog.dismiss();
							}
						}).setNegativeButton("取消", null).show();
	}

	@SuppressWarnings("deprecation")
	private void showModifyDialog(List<String> FilePath, String currentPath) {
        this.Prodialog = new ProgressDialog(this);
        this.Prodialog.setTitle("上传文件");
        this.Prodialog.setMessage("正在打包，请稍候...");
        this.Prodialog.setIndeterminate(false);
        this.Prodialog.setCanceledOnTouchOutside(false);
        this.Prodialog.setButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method
				// stub
                ChoseFileToSkyDriveDialog.Unupdate = false;
                ChoseFileToSkyDriveDialog.isUnzip = false;
				HttpConnectionUtil.setUnupdata(ChoseFileToSkyDriveDialog.Unupdate);
				dialog.dismiss();
			}
		});
        ChoseFileToSkyDriveDialog.isUnzip = true;
        this.connect(FilePath, currentPath + "temp.zip", currentPath, 2);
	}

	private void connect(String CheckedFile, String Path, int count) {
		ChoseFileToSkyDriveDialog.MyAsyncTask task = new ChoseFileToSkyDriveDialog.MyAsyncTask(CheckedFile, Path, count, this);
		task.execute();
	}

	private void connect(List<String> CheckedFile, String zipFile,
			String CheckedPath, int count) {
		ChoseFileToSkyDriveDialog.MyAsyncTask task = new ChoseFileToSkyDriveDialog.MyAsyncTask(CheckedFile, zipFile, count, this,
				CheckedPath);
		task.execute();
	}

	class MyAsyncTask extends AsyncTask<Integer, Integer, String> {

		private String FilePath;
		private final Context context;
		private String path;
		private final int count;
		private List<String> CheckedFile;
		private String zipFile;
		private String CheckedPath;
		private  PowerManager.WakeLock wakeLock = null;

		public MyAsyncTask(String CheckedFile, String Path, int count,
				Context context) {
            FilePath = CheckedFile;
			this.context = context;
            path = Path;
			this.count = count;
		}

		public MyAsyncTask(List<String> CheckedFile, String zipFile, int count,
				Context context, String CheckedPath) {
			this.CheckedFile = CheckedFile;
			this.zipFile = zipFile;
			this.context = context;
			this.count = count;
			this.CheckedPath = CheckedPath;
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
			rootPath += this.context.getString(R.string.SD_CARD_TBSAPP_PATH2);
			rootPath = UIHelper.getShareperference(this.context,
					constants.SAVE_INFORMATION, "Path", rootPath);
			if (rootPath.endsWith("/") == false) {
				rootPath += "/";
			}
			String WebIniFile = rootPath + constants.WEB_CONFIG_FILE_NAME;
			IniFile m_iniFileIO = new IniFile();
			rootPath = rootPath
					+ m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
							constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
            String userIni = rootPath;
            if(Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                    "LoginType", "0", (byte) 0)) == 1){
                String dataPath = this.context.getFilesDir().getParentFile()
                        .getAbsolutePath();
                if (dataPath.endsWith("/") == false) {
                    dataPath = dataPath + "/";
                }
                userIni = dataPath + "TbsApp.ini";
            }
			HttpConnectionUtil connection = new HttpConnectionUtil();

			if (this.count == 0) {
                constants.verifyURL = "http://"
                        + m_iniFileIO.getIniString(userIni, "Skydrive",
                        "skydriveAddress", constants.DefaultServerIp,
                        (byte) 0)
                        + ":"
                        + m_iniFileIO.getIniString(userIni, "Skydrive",
                        "skydrivePort", constants.DefaultServerPort,
                        (byte) 0) + "/SkyDrive/SaveFile.cbs";
				return connection.asyncConnect(
						constants.verifyURL,
                        this.UpdateFile(m_iniFileIO.getIniString(userIni, "Login",
								"LoginId", "", (byte) 0), m_iniFileIO
								.getIniString(userIni, "Login", "Account", "",
										(byte) 0), this.path), this.FilePath, this.context);
			} else if (this.count == 1) {
                constants.verifyURL = "http://"
                        + m_iniFileIO.getIniString(userIni, "Skydrive",
                        "skydriveAddress", constants.DefaultServerIp,
                        (byte) 0)
                        + ":"
                        + m_iniFileIO.getIniString(userIni, "Skydrive",
                        "skydrivePort", constants.DefaultServerPort,
                        (byte) 0) + "/SkyDrive/SaveFile.cbs";
				return connection.asyncConnect(
						constants.verifyURL,
                        this.UserRights(m_iniFileIO.getIniString(userIni, "Login",
								"LoginId", "", (byte) 0), m_iniFileIO
								.getIniString(userIni, "Login", "Account", "",
										(byte) 0)), HttpConnectionUtil.HttpMethod.GET, this.context);
			} else if (this.count == 2) {
				long Openzip = JTbszlib.OpenZip(this.zipFile, 0);
				for (String FilePath : this.CheckedFile) {
					if (ChoseFileToSkyDriveDialog.isUnzip) {
						File fs = new File(FilePath);
						if (fs.isDirectory()) {
							String strRelative = FilePath.substring(this.CheckedPath
									.length());
							JTbszlib.EnZipDir(FilePath, Openzip, 1, 1, "",
									strRelative);
						} else {
							JTbszlib.EnZipFile(FilePath, "", Openzip, 1, 1, "");
						}
					} else {
						JTbszlib.CloseZip(Openzip);
                        this.CheckedFile.clear();
						return this.zipFile;
					}
				}
				JTbszlib.CloseZip(Openzip);
                this.CheckedFile.clear();
				// downloadDialog.dismiss();
				return this.zipFile;
			}
			return null;

		}

		private Map<String, String> UpdateFile(String LoginId, String UserName,
				String Path) {
			// TODO Auto-generated method stub
			Map<String, String> params = new HashMap<String, String>();
            params.put("rePath", Path);
            params.put("flag", "upload");
			params.put("filepath", this.FilePath);
			params.put("userName", UserName);
            params.put("login_id", LoginId);
			return params;
		}

		private Map<String, String> UserRights(String LoginId, String UserName) {
			// TODO Auto-generated method stub
			Map<String, String> params = new HashMap<String, String>();
            params.put("rePath", "");
			params.put("flag", "getRights");
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
			if (StringUtils.isEmpty(result)) {
                ChoseFileToSkyDriveDialog.this.Prodialog.dismiss();
				Toast.makeText(this.context, "无法连接到网盘服务！",
						Toast.LENGTH_LONG).show();
			} else {
				if (this.count == 1) {
					if (result.equalsIgnoreCase("true")) {
                        ChoseFileToSkyDriveDialog.this.connect(this.FilePath, this.path, 0);
					} else {
                        ChoseFileToSkyDriveDialog.this.Prodialog.dismiss();
						Toast.makeText(this.context, "您还未开通网盘服务，无法上传文件！",
								Toast.LENGTH_LONG).show();
					}
				} else if (this.count == 0) {
                    ChoseFileToSkyDriveDialog.this.Prodialog.dismiss();
					FileUtils.deleteFileWithPath(this.FilePath);
					Intent refreshIntent = new Intent("freshView"
							+ this.context.getString(R.string.about_title));
                    ChoseFileToSkyDriveDialog.this.sendBroadcast(refreshIntent);
                    ChoseFileToSkyDriveDialog.this.finish();
                    //System.out.println(result);
					Toast.makeText(this.context, result, Toast.LENGTH_LONG).show();
				} else if (this.count == 2 && ChoseFileToSkyDriveDialog.isUnzip) {
                    ChoseFileToSkyDriveDialog.this.Prodialog.dismiss();
                    ChoseFileToSkyDriveDialog.this.connect(result, ChoseFileToSkyDriveDialog.this.Path, 1);

				} else if (this.count == 2 && !ChoseFileToSkyDriveDialog.isUnzip) {
					FileUtils.deleteFileWithPath(result);
					Toast.makeText(this.context, "上传已取消", Toast.LENGTH_LONG).show();
				}
			}
			UIHelper.releaseWakeLock(this.wakeLock);
		}

		// 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
		@Override
		protected void onPreExecute() {
			if (this.count == 1) {
                ChoseFileToSkyDriveDialog.this.Prodialog.setMessage("正在上传，请稍候...");
			}
            ChoseFileToSkyDriveDialog.this.Prodialog.show();
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyActivity.getInstance().finishActivity(this);

	}
}