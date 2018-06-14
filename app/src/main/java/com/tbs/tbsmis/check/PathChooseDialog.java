package com.tbs.tbsmis.check;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

/**
 * 路径选择弹窗
 * 
 * @author yeguozhong@yeah.net 修改 yzt
 * 
 */
public class PathChooseDialog extends Dialog {

	private ListView lv;
	private Button btnComfirm;
	private ImageView btnBack;
	// private Button btnNew;

	private TextView tvCurPath;
	// private TextView tvOldPath;
	private final Context ctx;

	private List<String> data;
	private ListAdapter listAdapter;

	private final PathChooseDialog.ChooseCompleteListener listener;

	private final Stack<String> pathStack = new Stack<String>();

	private int firstIndex;
	private View lastSelectItem; // 上一个长按操作的View
	private View mDropdownNavigation;
	// 监听操作事件
	private final ListViewPathAdapter.OnPathOperateListener pListener = new ListViewPathAdapter.OnPathOperateListener() {
		@Override
		public void onPathOperate(int type, final int position,
				final TextView pathName) {
			if (type == ListViewPathAdapter.OnPathOperateListener.DEL) {
				String path = PathChooseDialog.this.data.get(position);
				int rs = FileUtils.deleteBlankPath(path);
				if (rs == 0) {
                    PathChooseDialog.this.data.remove(position);
                    PathChooseDialog.this.refleshListView(PathChooseDialog.this.data, PathChooseDialog.this.firstIndex);
					Toast.makeText(PathChooseDialog.this.ctx, "删除成功", Toast.LENGTH_SHORT).show();
				} else if (rs == 1) {
					Toast.makeText(PathChooseDialog.this.ctx, "没有权限", Toast.LENGTH_SHORT).show();
				} else if (rs == 2) {
					Toast.makeText(PathChooseDialog.this.ctx, "不能删除非空目录", Toast.LENGTH_SHORT).show();
				}

			} else if (type == ListViewPathAdapter.OnPathOperateListener.RENAME) {
				final EditText et = new EditText(PathChooseDialog.this.ctx);
				et.setText(FileUtils.getPathName(PathChooseDialog.this.data.get(position)));
				Builder builder = new Builder(PathChooseDialog.this.ctx);
				builder.setTitle("重命名");
				builder.setView(et);
				builder.setCancelable(true);
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String input = et.getText().toString();
						if (StringUtils.isEmpty(input)) {
							Toast.makeText(PathChooseDialog.this.ctx, "输入不能为空", Toast.LENGTH_SHORT)
									.show();
						} else {
							String newPath = PathChooseDialog.this.pathStack.peek() + File.separator
									+ input;
							boolean rs = FileUtils.reNamePath(
                                    PathChooseDialog.this.data.get(position), newPath);
							if (rs == true) {
								pathName.setText(input);
                                PathChooseDialog.this.data.set(position, newPath);
								Toast.makeText(PathChooseDialog.this.ctx, "重命名成功", Toast.LENGTH_SHORT)
										.show();
							} else {
								Toast.makeText(PathChooseDialog.this.ctx, "重命名失败", Toast.LENGTH_SHORT)
										.show();
							}
						}
						dialog.dismiss();
					}
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
			}
		}
	};
	private final int count;
	private final String defPath;
	private Button cancleBtn;
	private ImageView path_up_level;
	private TextView up_file_txt;

	public interface ChooseCompleteListener {
		void onComplete(String finalPath);
	}

	public PathChooseDialog(Context context, int count, String defPath,
			PathChooseDialog.ChooseCompleteListener listener) {
		super(context);
        ctx = context;
		this.listener = listener;
		this.count = count;
		this.defPath = defPath;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.file_path_chooser);
        this.setCanceledOnTouchOutside(false);
        this.init();
	}

	private void init() {
        this.lv = (ListView) this.findViewById(android.R.id.list);
        this.btnComfirm = (Button) this.findViewById(R.id.up_file);
        this.btnBack = (ImageView) this.findViewById(R.id.up_file_back);
        this.up_file_txt = (TextView) this.findViewById(R.id.up_file_txt);
        this.cancleBtn = (Button) this.findViewById(R.id.cancleBtn);
        this.path_up_level = (ImageView) this.findViewById(R.id.path_pane_up_level);
		// btnNew = (Button) findViewById(R.id.btn_new);
        this.tvCurPath = (TextView) this.findViewById(R.id.up_cur_path);
        this.btnComfirm.setText("确定");
        this.up_file_txt.setText("选择路径");
        this.setupNaivgationBar();
		// tvOldPath = (TextView) findViewById(R.id.tv_old_path);
		String webRoot = UIHelper.getSoftPath(ctx);
		if (webRoot.endsWith("/") == false) {
			webRoot += "/";
		}
		String nowPath = webRoot + this.ctx.getString(R.string.SD_CARD_TBSAPP_PATH2);
		if (nowPath.endsWith("/") == false) {
			nowPath += "/";
		}
		String softPath = webRoot;
		if (softPath.endsWith("/") == false) {
			softPath += "/";
		}
		String appPath = UIHelper.getShareperference(this.ctx,
				constants.SAVE_INFORMATION, "Path", nowPath);
		switch (this.count) {
		case 0:
            this.data = FileUtils.listPath(appPath);
            this.tvCurPath.setText(appPath);
			// tvOldPath.setText("原路径:" + appPath);
            this.addPath(appPath);
			break;
		case 1:
			File file = new File(this.defPath);
			if (file.exists()) {
                this.data = FileUtils.listPath(this.defPath);
                this.tvCurPath.setText(this.defPath);
				// tvOldPath.setText("原路径:" + defPath);
                this.addPath(this.defPath);
			} else {
                this.data = FileUtils.listPath(softPath);
                this.tvCurPath.setText(softPath);
				// tvOldPath.setText("原路径:" + defPath + "(不存在)");
                this.addPath(softPath);
			}
			break;
		case 2:
            this.data = FileUtils.listPath(softPath);
            this.tvCurPath.setText(softPath);
			// tvOldPath.setVisibility(View.GONE);
            this.addPath(softPath);
			break;
		}
        this.refleshListView(this.data, 0);
		// 单击
        this.lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
                PathChooseDialog.this.firstIndex = position;
				String currentPath = PathChooseDialog.this.data.get(position);
				try {
                    PathChooseDialog.this.data = FileUtils.listPath(currentPath);
                    PathChooseDialog.this.tvCurPath.setText(currentPath);
                    PathChooseDialog.this.pathStack.add(currentPath);
                    PathChooseDialog.this.refleshListView(PathChooseDialog.this.data, PathChooseDialog.this.pathStack.size() - 1);
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(PathChooseDialog.this.ctx, "该文件夹无法访问", Toast.LENGTH_SHORT).show();
				}
			}
		});
		// 长按
        this.lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (PathChooseDialog.this.lastSelectItem != null && !PathChooseDialog.this.lastSelectItem.equals(view)) {
                    PathChooseDialog.this.lastSelectItem.findViewById(R.id.ll_op).setVisibility(
							View.GONE);
				}
				LinearLayout llOp = (LinearLayout) view
						.findViewById(R.id.ll_op);
				int visible = llOp.getVisibility() == View.GONE ? View.VISIBLE
						: View.GONE;
				llOp.setVisibility(visible);
                PathChooseDialog.this.lastSelectItem = view;
				return true;
			}
		});
        this.path_up_level.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (PathChooseDialog.this.mDropdownNavigation.getVisibility() == View.VISIBLE) {
                    PathChooseDialog.this.showDropdownNavigation(false);
				}
				// checkNum = 0;
				if (PathChooseDialog.this.pathStack.size() >= 2) {
                    PathChooseDialog.this.pathStack.pop();
                    PathChooseDialog.this.data = FileUtils.listAll(PathChooseDialog.this.pathStack.peek());
                    PathChooseDialog.this.tvCurPath.setText(PathChooseDialog.this.pathStack.peek());
                    PathChooseDialog.this.refleshListView(PathChooseDialog.this.data, PathChooseDialog.this.firstIndex);
				} else {
                    PathChooseDialog.this.path_up_level.setClickable(false);
				}
			}
		});
		// 确认
        this.btnComfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (PathChooseDialog.this.pathStack.size() >= 2) {
                    PathChooseDialog.this.listener.onComplete(PathChooseDialog.this.pathStack.peek());
				} else {
					Toast.makeText(PathChooseDialog.this.ctx, "路径未做修改", Toast.LENGTH_SHORT).show();
				}
                PathChooseDialog.this.dismiss();
			}
		});
        this.cancleBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                PathChooseDialog.this.dismiss();
			}
		});
		// 后退
        this.btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                PathChooseDialog.this.dismiss();
			}
		});
		// 返回上一级
        this.path_up_level.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (PathChooseDialog.this.pathStack.size() >= 2) {
                    PathChooseDialog.this.pathStack.pop();
                    PathChooseDialog.this.data = FileUtils.listPath(PathChooseDialog.this.pathStack.peek());
                    PathChooseDialog.this.tvCurPath.setText(PathChooseDialog.this.pathStack.peek());
                    PathChooseDialog.this.refleshListView(PathChooseDialog.this.data, PathChooseDialog.this.firstIndex);
				} else {
                    PathChooseDialog.this.path_up_level.setClickable(false);
				}
			}
		});

		// 新建
		// btnNew.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// final EditText et = new EditText(ctx);
		// et.setText("新建文件夹");
		// AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		// builder.setTitle("新建文件夹");
		// builder.setView(et);
		// builder.setCancelable(true);
		// builder.setPositiveButton("确定", new OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// String rs = et.getText().toString();
		// if (StringUtils.isEmpty(rs)) {
		// Toast.makeText(ctx, "输入不能为空", Toast.LENGTH_SHORT)
		// .show();
		// } else {
		// String newPath = pathStack.peek() + File.separator
		// + rs;
		// PathStatus status = FileUtils.createPath(newPath);
		// switch (status) {
		// case SUCCESS:
		// data.add(newPath);
		// refleshListView(data, data.size() - 1);
		// Toast.makeText(ctx, "创建成功", Toast.LENGTH_SHORT)
		// .show();
		// break;
		// case ERROR:
		// Toast.makeText(ctx, "创建失败", Toast.LENGTH_SHORT)
		// .show();
		// break;
		// case EXITS:
		// Toast.makeText(ctx, "文件名重复", Toast.LENGTH_SHORT)
		// .show();
		// break;
		// }
		// }
		// dialog.dismiss();
		// }
		// });
		// builder.setNegativeButton("取消", new OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// dialog.dismiss();
		// }
		// });
		// builder.create().show();
		// }
		// });
	}

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
                PathChooseDialog.this.onNavigationBarClick();
			}
		});

        this.mDropdownNavigation = this.findViewById(R.id.dropdown_navigation);

		// setupClick(mNavigationBar, R.id.path_pane_up_level);
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

				View listItem = LayoutInflater.from(this.ctx).inflate(
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
            PathChooseDialog.this.showDropdownNavigation(false);
			if (path.isEmpty()) {
				path = "/";
			}
			// upLevel.setVisibility(mRoot.equals(mCurrentPath) ? View.INVISIBLE
			// : View.VISIBLE);
            PathChooseDialog.this.addPath(path);
            PathChooseDialog.this.data = FileUtils.listAll(path);
            PathChooseDialog.this.tvCurPath.setText(path);
            PathChooseDialog.this.refleshListView(PathChooseDialog.this.data, PathChooseDialog.this.firstIndex);
		}

	};

	private void showDropdownNavigation(boolean show) {
        this.mDropdownNavigation.setVisibility(show ? View.VISIBLE : View.GONE);
        this.mNavigationBarUpDownArrow.setImageResource(this.mDropdownNavigation
				.getVisibility() == View.VISIBLE ? R.drawable.arrow_up
				: R.drawable.arrow_down);
	}

	private void addPath(String webRoot) {
		// TODO Auto-generated method stub
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

	/**
	 * 更新listView视图
	 * 
	 * @param data
	 */
	private void refleshListView(List<String> data, int firstItem) {
		String lost = FileUtils.getSDRoot() + "lost+found";
		data.remove(lost);
		if (!data.isEmpty()) {
			Collections.sort(data, new Comparator<String>() {
				@Override
				public int compare(String object1, String object2) {
					// 根据字段"LEVEL"排序
					return object2.compareToIgnoreCase(object1);
				}
			});
		}
        this.listAdapter = new ListViewPathAdapter(this.ctx, data,
                R.layout.file_path_listitem, this.pListener);
        this.lv.setAdapter(this.listAdapter);
        this.lv.setSelection(firstItem);
	}
}
