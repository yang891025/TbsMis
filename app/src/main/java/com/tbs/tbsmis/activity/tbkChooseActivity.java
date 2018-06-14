package com.tbs.tbsmis.activity;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.check.PopMenuAdapter;
import com.tbs.tbsmis.check.UpFileAdapter;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.file.TextInputDialog;
import com.tbs.tbsmis.file.Util;
import com.tbs.tbsmis.util.FileUtils;
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
 * 路径选择弹窗
 * 
 * @author @yeah.net 修改 yzt
 * 
 */
public class tbkChooseActivity extends Activity {

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
	// public interface UpChooseListener {
	// void onComplete(List<String> CheckedFile);
	// }
	private ImageView path_up_level;
	private int lenght;
	private View mDropdownNavigation;
	private ImageView orderBtn;
	private RelativeLayout up_file_title;
	private LinearLayout layoutButton;
	private TextView up_file_txt;
	private static boolean Uninstal = true;

	// public ChoseFileToSkyDriveDialog(Context context, String path) {
	// super();
	// this.context = context;
	// // this.listener = listener;
	// this.Path = path;
	//
	// }
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
        this.up_file_txt = (TextView) this.findViewById(R.id.up_file_txt);
        this.expandableList = (ListView) this.findViewById(android.R.id.list);
        this.up_file_title = (RelativeLayout) this.findViewById(R.id.up_file_title);
        this.layoutButton = (LinearLayout) this.findViewById(R.id.layoutButton);
        this.setBtn.setText("安装");
        this.up_file_txt.setText("选择安装包文件");
        this.initData();
        this.setupNaivgationBar();
		// adapter = new ListViewAdapter(context, dataList);
		// expandableList.setAdapter(adapter);
        this.path_up_level.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (tbkChooseActivity.this.mDropdownNavigation.getVisibility() == View.VISIBLE) {
                    tbkChooseActivity.this.showDropdownNavigation(false);
				}
                tbkChooseActivity.this.checkNum = 0;
				if (tbkChooseActivity.this.pathStack.size() >= 2) {
                    tbkChooseActivity.this.pathStack.pop();
                    tbkChooseActivity.this.data = FileUtils.listAll(tbkChooseActivity.this.pathStack.peek());
                    tbkChooseActivity.this.tvCurPath.setText(tbkChooseActivity.this.pathStack.peek());
                    tbkChooseActivity.this.refleshListView(tbkChooseActivity.this.data, tbkChooseActivity.this.firstIndex);
				} else {
                    tbkChooseActivity.this.path_up_level.setClickable(false);
				}
			}
		});
		// 取消
        this.backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                tbkChooseActivity.this.finish();
			}
		});
        this.orderBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                tbkChooseActivity.this.changMenu(v);
			}
		});
        this.cancleBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                tbkChooseActivity.this.finish();
			}
		});
        this.setBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (tbkChooseActivity.this.listAdapter.getSelected().size() <= 0) {
					Toast.makeText(tbkChooseActivity.this, "请至少选择一个安装文件",
							Toast.LENGTH_LONG).show();
				} else {
                    tbkChooseActivity.this.showModifyDialog(tbkChooseActivity.this.listAdapter.getSelected());
				}

			}
		});
		// 单击list

        this.expandableList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (tbkChooseActivity.this.mDropdownNavigation.getVisibility() == View.VISIBLE) {
                    tbkChooseActivity.this.showDropdownNavigation(false);
				}
				UpFileAdapter.ViewHolder holder = (UpFileAdapter.ViewHolder) view.getTag();
				String Path = tbkChooseActivity.this.data.get(position);
				File isFile = new File(Path);
				if (tbkChooseActivity.this.checkNum == 1) {
					if (isFile.isDirectory()) {
                        tbkChooseActivity.this.checkNum = 0;
						try {
                            tbkChooseActivity.this.data = FileUtils.listAll(Path);
                            tbkChooseActivity.this.tvCurPath.setText(Path);
                            tbkChooseActivity.this.pathStack.add(Path);
                            tbkChooseActivity.this.refleshListView(tbkChooseActivity.this.data, tbkChooseActivity.this.pathStack.size() - 1);
						} catch (Exception e) {
							e.printStackTrace();
						}
						// setBtn.setFocusable(false);
						// layoutButton.setVisibility(View.GONE);
					} else {
						if (holder.children_cb.isChecked() == true) {
							holder.children_cb.toggle();
                            tbkChooseActivity.this.checkNum--;
                            tbkChooseActivity.this.listAdapter.getIsSelected().put(position,
									holder.children_cb.isChecked());
							// setBtn.setFocusable(false);
							// layoutButton.setVisibility(View.GONE);
						} else {
							holder.children_cb.toggle();
                            tbkChooseActivity.this.checkNum++;
                            tbkChooseActivity.this.listAdapter.getIsSelected().put(position,
									holder.children_cb.isChecked());
						}
					}
				} else {
					if (isFile.isDirectory()) {
                        tbkChooseActivity.this.checkNum = 0;
						try {
                            tbkChooseActivity.this.data = FileUtils.listAll(Path);
                            tbkChooseActivity.this.tvCurPath.setText(Path);
                            tbkChooseActivity.this.pathStack.add(Path);
                            tbkChooseActivity.this.refleshListView(tbkChooseActivity.this.data, tbkChooseActivity.this.pathStack.size() - 1);
						} catch (Exception e) {
							e.printStackTrace();
						}
						// setBtn.setFocusable(false);
						// layoutButton.setVisibility(View.GONE);
					} else {
						// 改变CheckBox的状态
						holder.children_cb.toggle();
						// 将CheckBox的选中状况记录下来
                        tbkChooseActivity.this.listAdapter.getIsSelected().put(position,
								holder.children_cb.isChecked());
						// 调整选定条目
						if (holder.children_cb.isChecked() == true) {
                            tbkChooseActivity.this.checkNum++;
                            tbkChooseActivity.this.currentPath = Path;
						} else {
                            tbkChooseActivity.this.checkNum--;
						}
						// setBtn.setFocusable(true);
						// layoutButton.setVisibility(View.VISIBLE);

					}
				}
                tbkChooseActivity.this.firstIndex = position;
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
            tbkChooseActivity.this.showDropdownNavigation(false);
			if (path.isEmpty()) {
				path = "/";
			}
			// upLevel.setVisibility(mRoot.equals(mCurrentPath) ? View.INVISIBLE
			// : View.VISIBLE);
            tbkChooseActivity.this.addPath(path);
            tbkChooseActivity.this.data = FileUtils.listAll(path);
            tbkChooseActivity.this.tvCurPath.setText(path);
            tbkChooseActivity.this.refleshListView(tbkChooseActivity.this.data, tbkChooseActivity.this.firstIndex);
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
                tbkChooseActivity.this.onNavigationBarClick();
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
								tbkChooseActivity.this,
								constants.SAVE_LOCALMSGNUM, "sort", 0);
						if (order == 0) {
							return object1.compareToIgnoreCase(object2);
						} else if (order == 1) {
							return tbkChooseActivity.this.longToCompareInt(o1.length() - o2.length());
						} else if (order == 2) {
							return tbkChooseActivity.this.longToCompareInt(o1.lastModified()
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
                    tbkChooseActivity.this.showSortDialog();
				} else if (arg2 == 1) {
                    tbkChooseActivity.this.showNewDialog();
				} else if (arg2 == 2) {
                    tbkChooseActivity.this.data = FileUtils.listAll(tbkChooseActivity.this.tvCurPath.getText().toString());
                    tbkChooseActivity.this.refleshListView(tbkChooseActivity.this.data, tbkChooseActivity.this.firstIndex);
				}
                tbkChooseActivity.this.SetWindow.dismiss();
			}
		});
        this.SetWindow = new PopupWindow(view,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		// �˶�ʵ�ֵ���հ״�����popwindow
        this.SetWindow.setFocusable(true);
        this.SetWindow.setOutsideTouchable(false);
        this.SetWindow.setBackgroundDrawable(new BitmapDrawable());
        this.SetWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

			@Override
			public void onDismiss() {
                tbkChooseActivity.this.isOpenPop = false;
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
						File f = new File(Util.makePath(tbkChooseActivity.this.tvCurPath.getText()
								.toString(), text));
						if (f.exists()) {
							new Builder(tbkChooseActivity.this)
									.setMessage(
                                            getString(R.string.fail_to_create_folder))
									.setPositiveButton(R.string.confirm, null)
									.create().show();
							return false;
						} else {
							f.mkdirs();
                            tbkChooseActivity.this.data = FileUtils.listAll(tbkChooseActivity.this.tvCurPath.getText()
									.toString());
                            tbkChooseActivity.this.refleshListView(tbkChooseActivity.this.data, tbkChooseActivity.this.pathStack.size() - 1);
							return true;
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
										tbkChooseActivity.this,
										constants.SAVE_LOCALMSGNUM, "sort",
										which);
                                tbkChooseActivity.this.refleshListView(tbkChooseActivity.this.data, tbkChooseActivity.this.firstIndex);
								dialog.dismiss();
							}
						}).setNegativeButton("取消", null).show();
	}

	@SuppressWarnings("deprecation")
	private void showModifyDialog(List<String> FilePath) {
        this.Prodialog = new ProgressDialog(this);
        this.Prodialog.setTitle("安装数据");
        this.Prodialog.setMessage("正在安装，请稍候...");
        this.Prodialog.setIndeterminate(false);
        this.Prodialog.setCanceledOnTouchOutside(false);
        this.Prodialog.setButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method
				// stub
                tbkChooseActivity.Uninstal = false;
				dialog.dismiss();
			}
		});
        this.connect(FilePath);
	}

	private void connect(List<String> CheckedFile) {
		tbkChooseActivity.MyAsyncTask task = new tbkChooseActivity.MyAsyncTask(CheckedFile, this);
		task.execute();
	}

	class MyAsyncTask extends AsyncTask<Integer, Integer, String> {

		private final List<String> FilePath;
		private final Context context;

		public MyAsyncTask(List<String> CheckedFile, Context context) {
            FilePath = CheckedFile;
			this.context = context;
		}

		/**
		 * 这里的Integer参数对应AsyncTask中的第一个参数 这里的String返回值对应AsyncTask的第三个参数
		 * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
		 * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
		 */
		@Override
		protected String doInBackground(Integer... params) {
			String rootPath = UIHelper.getShareperference(this.context,
					constants.SAVE_INFORMATION, "Path", "");
			if (rootPath.endsWith("/") == false) {
				rootPath += "/";
			}
			for (int i = 0; i < this.FilePath.size(); i++) {
				if (tbkChooseActivity.Uninstal) {
                    this.publishProgress(i, this.FilePath.size());
					if (FileUtils.getFileFormat(this.FilePath.get(i))
							.equalsIgnoreCase("tbk")) {
						if(rootPath.contentEquals(FileUtils.getFileNameNoFormat(this.FilePath.get(i)))){
							JTbszlib.UnZipFile(this.FilePath.get(i), rootPath, 1, "");
						}else {
                            this.publishProgress(i, 1);
						}
					} else {
                        this.publishProgress(i, 0);
					}
				} else {
					return "false";
				}
			}
			return "true";

		}

		/**
		 * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
		 * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
		 */
		@Override
		protected void onPostExecute(String result) {
			if (result.equalsIgnoreCase("false")) {
				Toast.makeText(this.context, "安装已取消！", Toast.LENGTH_LONG).show();
			} else {
                tbkChooseActivity.this.finish();
				Toast.makeText(this.context, "安装已完成！", Toast.LENGTH_LONG).show();
			}
            tbkChooseActivity.this.Prodialog.dismiss();

		}

		// 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
		@Override
		protected void onPreExecute() {
            tbkChooseActivity.this.Prodialog.show();
		}

		/**
		 * 这里的Intege参数对应AsyncTask中的第二个参数
		 * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行
		 * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {
			String FileName = FileUtils.getFileName(this.FilePath.get(values[0]));
			if (values[1] == 0) {
				Toast.makeText(this.context, FileName + " 安装包格式不正确！",
						Toast.LENGTH_LONG).show();
			} else if(values[1] == 1){
				Toast.makeText(this.context, FileName + " 安装包不属于该应用！",
						Toast.LENGTH_LONG).show();
			}else {

                tbkChooseActivity.this.Prodialog.setMessage(FileName + " 正在安装中(" + (values[0] + 1)
						+ "/" + values[1] + ")");
			}

		}
	}
}