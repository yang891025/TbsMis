package com.tbs.tbsmis.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.check.ListViewRemoteAdapter;
import com.tbs.tbsmis.file.FileInfo;
import com.tbs.tbsmis.util.FileIO;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.StringUtils;

import java.util.ArrayList;
import java.util.Stack;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

/**
 *
 * 
 * @author yeguozhong@yeah.net 修改 yzt
 * 
 */
public class SmbFileListActivity extends Activity {

	private ListView lv;
	private ImageView btnComfirm;
	private ImageView btnBack;
	// private Button btnNew;

	private TextView tvCurPath;
	// private TextView tvOldPath;
	// private Context ctx;
	private int firstIndex;
	private SmbFile[] data;
	private ListAdapter listAdapter;
	private ArrayList<FileInfo> FileNameList;
	private final Stack<String> pathStack = new Stack<String>();

	// 监听操作事件
	private final ListViewRemoteAdapter.OnPathOperateListener pListener = new ListViewRemoteAdapter.OnPathOperateListener() {
		@Override
		public void onPathOperate(int type, int position,
				TextView pathName) {
			// if (type == OnPathOperateListener.DEL) {
			// String path = data.get(position);
			// int rs = FileUtils.deleteBlankPath(path);
			// if (rs == 0) {
			// data.remove(position);
			// refleshListView(data, firstIndex);
			// Toast.makeText(ctx, "删除成功", Toast.LENGTH_SHORT).show();
			// } else if (rs == 1) {
			// Toast.makeText(ctx, "没有权限", Toast.LENGTH_SHORT).show();
			// } else if (rs == 2) {
			// Toast.makeText(ctx, "不能删除非空目录", Toast.LENGTH_SHORT).show();
			// }
			//
			// } else if (type == OnPathOperateListener.RENAME) {
			// final EditText et = new EditText(ctx);
			// et.setText(FileUtils.getPathName(data.get(position)));
			// AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
			// builder.setTitle("重命名");
			// builder.setView(et);
			// builder.setCancelable(true);
			// builder.setPositiveButton("确定", new OnClickListener() {
			//
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// String input = et.getText().toString();
			// if (StringUtils.isEmpty(input)) {
			// Toast.makeText(ctx, "输入不能为空", Toast.LENGTH_SHORT)
			// .show();
			// } else {
			// String newPath = pathStack.peek() + File.separator
			// + input;
			// boolean rs = FileUtils.reNamePath(
			// data.get(position), newPath);
			// if (rs == true) {
			// pathName.setText(input);
			// data.set(position, newPath);
			// Toast.makeText(ctx, "重命名成功", Toast.LENGTH_SHORT)
			// .show();
			// } else {
			// Toast.makeText(ctx, "重命名失败", Toast.LENGTH_SHORT)
			// .show();
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
		}
	};
	private String remotUrl;
	private Handler handler;
	private ProgressDialog mProDialog;
	private ArrayList<FileInfo> FileOrderList;

	// public interface ChooseCompleteListener {
	// void onComplete(String finalPath);
	// }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.new_remote_filelist);
		MyActivity.getInstance().addActivity(this);
        this.init();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyActivity.getInstance().finishActivity(this);
	}

	@SuppressLint("HandlerLeak")
	@SuppressWarnings("static-access")
	private void init() {
        this.lv = (ListView) this.findViewById(R.id.file_list);
        this.btnComfirm = (ImageView) this.findViewById(R.id.finish_btn);
        this.btnBack = (ImageView) this.findViewById(R.id.more_btn);
        this.tvCurPath = (TextView) this.findViewById(R.id.textView1);
        this.btnComfirm.setVisibility(View.GONE);
        this.btnBack.setVisibility(View.GONE);
		if (this.getIntent().getExtras() != null) {
			Intent intent = this.getIntent();
            this.remotUrl = intent.getStringExtra("tempUrl");
		}
		String path = FileUtils.getSmbFileName(this.remotUrl);
		path = path.substring(0, path.length() - 1);
        this.tvCurPath.setText(path);
		// tvCurPath.setText(remotUrl);
		// smb = "smb://tbs:tbs@";
		// remotUrl = smb + remotUrl + "/";
        this.showScanDialog();
		// Environment.getExternalStorageDirectory().getAbsolutePath();
		new Thread() {
			@Override
			public void run() {
                SmbFileListActivity.this.data = FileIO.getInstance().smbTraversal(SmbFileListActivity.this.remotUrl);
                SmbFileListActivity.this.FileNameList = new ArrayList<FileInfo>();
				if (SmbFileListActivity.this.data != null) {
					for (int i = 0; i < SmbFileListActivity.this.data.length; i++) {
						FileInfo info = new FileInfo();
						try {
							info.IsDir = SmbFileListActivity.this.data[i].isDirectory();
							info.fileName = SmbFileListActivity.this.data[i].getName();
						} catch (SmbException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        SmbFileListActivity.this.FileNameList.add(info);
					}
				}
				Message msg = new Message();
				msg.what = 1;
                SmbFileListActivity.this.handler.sendMessage(msg);
			}
		}.start();
		// tvCurPath.setText("当前路径:" + rootPath);
		// tvOldPath.setText("原路径:" + webRoot);
		// 单击
        this.lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

                SmbFileListActivity.this.remotUrl = SmbFileListActivity.this.remotUrl + SmbFileListActivity.this.FileOrderList.get(position).fileName;
                SmbFileListActivity.this.FileNameList = new ArrayList<FileInfo>();
				try {
					if (SmbFileListActivity.this.FileOrderList.get(position).IsDir) {
                        SmbFileListActivity.this.firstIndex = position;
						String path = FileUtils.getSmbFileName(SmbFileListActivity.this.remotUrl);
						path = path.substring(0, path.length() - 1);
                        SmbFileListActivity.this.tvCurPath.setText(path);
						new Thread() {
							@Override
							public void run() {
                                SmbFileListActivity.this.data = FileIO.getInstance().smbTraversal(
                                        SmbFileListActivity.this.remotUrl);
								if (SmbFileListActivity.this.data != null) {
									for (int i = 0; i < SmbFileListActivity.this.data.length; i++) {
										FileInfo info = new FileInfo();
										try {
											info.IsDir = SmbFileListActivity.this.data[i].isDirectory();
											info.fileName = SmbFileListActivity.this.data[i].getName();
										} catch (SmbException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
                                        SmbFileListActivity.this.FileNameList.add(info);
									}
								} else {
                                    Message msg = new Message();
                                    msg.what = 3;
                                    SmbFileListActivity.this.handler.sendMessage(msg);
								}
								Message msg = new Message();
								msg.what = 1;
                                SmbFileListActivity.this.handler.sendMessage(msg);
							}
						}.start();
					} else {
						Toast.makeText(SmbFileListActivity.this, "无法操作文件",
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(SmbFileListActivity.this, "该文件夹无法访问",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		// 长按
        this.lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// if (lastSelectItem != null && !lastSelectItem.equals(view)) {
				// lastSelectItem.findViewById(R.R.id.ll_op).setVisibility(
				// View.GONE);
				// }
				// LinearLayout llOp = (LinearLayout) view
				// .findViewById(R.R.id.ll_op);
				// int visible = llOp.getVisibility() == View.GONE ?
				// View.VISIBLE
				// : View.GONE;
				// llOp.setVisibility(visible);
				// lastSelectItem = view;
				return true;
			}
		});

        this.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
                    SmbFileListActivity.this.mProDialog.dismiss();
                    SmbFileListActivity.this.pathStack.add(SmbFileListActivity.this.remotUrl);
                    SmbFileListActivity.this.refleshListView(SmbFileListActivity.this.pathStack.size() - 1);
				} else if (msg.what == 2) {
                    SmbFileListActivity.this.mProDialog.dismiss();
                    SmbFileListActivity.this.refleshListView(SmbFileListActivity.this.firstIndex);
				}else if(3 == msg.what){
                    Toast.makeText(SmbFileListActivity.this,
                            "文件夹为空", Toast.LENGTH_SHORT).show();
                }
			}
		};
		// 确认
		// btnComfirm.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// if (pathStack.size() >= 2) {
		// listener.onComplete(pathStack.peek());
		// } else {
		// Toast.makeText(ctx, "路径未做修改", Toast.LENGTH_SHORT).show();
		// }
		// dismiss();
		// }
		// });
		//
		// // 后退
		// btnBack.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// if (pathStack.size() >= 2) {
		// pathStack.pop();
		// data = FileUtils.listPath(pathStack.peek());
		// tvCurPath.setText("当前路径:" + pathStack.peek());
		// refleshListView(data, firstIndex);
		// } else {
		// dismiss();
		// }
		// }
		// });
		//
		// // 新建
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

	/**
	 * 显示进度条对话框
	 */
	private void showScanDialog() {
		if (this.mProDialog == null) {
			// 创建ProgressDialog对象
            this.mProDialog = new ProgressDialog(this);
			// 设置进度条风格，风格为圆形，旋转的
            this.mProDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			// 设置ProgressDialog 标题
            this.mProDialog.setTitle("载入中...");
			// 设置ProgressDialog 提示信息
            this.mProDialog.setMessage("正在加载，请稍候...");
			// 设置ProgressDialog 的进度条是否不明确
            this.mProDialog.setIndeterminate(false);
			// 设置ProgressDialog 是否可以按退回按键取消
            this.mProDialog.setCancelable(true);
			// 让ProgressDialog显示
            this.mProDialog.show();
		}
	}

	/**
	 * 更新listView视图
	 * 
	 * @param firstItem
	 */
	private void refleshListView(int firstItem) {
		// String lost = FileUtils.getSDRoot() + "lost+found";
		// data.remove(lost);
        this.FileOrderList = StringUtils.strOrderIng(this.FileNameList);
        this.listAdapter = new ListViewRemoteAdapter(this,
                this.FileOrderList, R.layout.file_path_listitem, this.pListener);
        this.lv.setAdapter(this.listAdapter);
        this.lv.setSelection(firstItem);
	}

	// 返回键
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (this.pathStack.size() >= 2) {
                this.pathStack.pop();
                this.remotUrl = this.pathStack.peek();
				String path = FileUtils.getSmbFileName(this.remotUrl);
				path = path.substring(0, path.length() - 1);
                this.tvCurPath.setText(path);
				new Thread() {
					@Override
					@SuppressWarnings("static-access")
					public void run() {
                        SmbFileListActivity.this.data = FileIO.getInstance().smbTraversal(SmbFileListActivity.this.remotUrl);
                        SmbFileListActivity.this.FileNameList = new ArrayList<FileInfo>();
						if (SmbFileListActivity.this.data != null) {
							for (int i = 0; i < SmbFileListActivity.this.data.length; i++) {
								FileInfo info = new FileInfo();
								try {
									info.IsDir = SmbFileListActivity.this.data[i].isDirectory();
									info.fileName = SmbFileListActivity.this.data[i].getName();
								} catch (SmbException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
                                SmbFileListActivity.this.FileNameList.add(info);
							}
						}
						Message msg = new Message();
						msg.what = 2;
                        SmbFileListActivity.this.handler.sendMessage(msg);
					}
				}.start();
				// data = FileUtils.listPath(pathStack.peek());
				// tvCurPath.setText("当前路径:" + pathStack.peek());
				// refleshListView(data, firstIndex);
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}
}
