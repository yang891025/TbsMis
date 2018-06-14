package com.tbs.tbsmis.weixin;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.database.dao.DBUtil;
import com.tbs.tbsmis.entity.WXGroupEntity;
import com.tbs.tbsmis.entity.WXUserEntity;
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.ImageLoader;
import com.tbs.tbsmis.util.UIHelper;
import com.tbs.tbsmis.util.httpRequestUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//import com.tbs.chat.ui.address.ContactSearchUI;

public class WeixinUserInfoActivity extends Activity {
	private ImageView avatar_iv;
	private TextView nickname_tv;
	private TextView gender;
	private TextView group;
	private TextView location_province;
	private TextView location;
	private ImageView leftBtn;
	private ImageView RightBtn;
	private TextView title;
	private WXUserEntity friend;
	private RelativeLayout send_msg;
	private TextView remarkname;
	protected boolean InfoMark;
	private DBUtil dao;
	private ArrayList<WXGroupEntity> groups;
	protected int groupid;
	private RelativeLayout remark_layout;
	private RelativeLayout group_layout;
	private RelativeLayout add_ims;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.weixin_user_info);
		MyActivity.getInstance().addActivity(this);
        this.leftBtn = (ImageView) this.findViewById(R.id.more_btn);
        this.RightBtn = (ImageView) this.findViewById(R.id.finish_btn);
        this.RightBtn.setVisibility(View.GONE);
        this.title = (TextView) this.findViewById(R.id.textView1);
        this.title.setText("个人信息");
        this.dao = DBUtil.getInstance(this);
        this.avatar_iv = (ImageView) this.findViewById(R.id.weixin_avatar_iv);
        this.nickname_tv = (TextView) this.findViewById(R.id.sapi_weixin_showname);
        this.remarkname = (TextView) this.findViewById(R.id.sapi_weixin_remarkname);
        this.gender = (TextView) this.findViewById(R.id.sapi_weixin_sex);
        this.group = (TextView) this.findViewById(R.id.sapi_weixin_group);
        this.location_province = (TextView) this.findViewById(R.id.sapi_user_province);
        this.location = (TextView) this.findViewById(R.id.sapi_user_city);
        this.send_msg = (RelativeLayout) this.findViewById(R.id.send_msg);
        this.add_ims = (RelativeLayout) this.findViewById(R.id.add_ims);
        this.remark_layout = (RelativeLayout) this.findViewById(R.id.remark_layout);
        this.group_layout = (RelativeLayout) this.findViewById(R.id.group_layout);
		if (this.getIntent().getExtras() != null) {
			Intent userdata = this.getIntent();
            this.friend = (WXUserEntity) userdata
					.getSerializableExtra("WXUserEntity");
            this.nickname_tv.setText(this.friend.getNickname());
			if (this.friend.getSex() == 1)
                this.gender.setText("男");
			else
                this.gender.setText("女");
            this.remarkname.setText(this.friend.getRemark());
            this.group.setText(this.friend.getGroupname());
            this.location_province.setText(this.friend.getProvince());
            this.location.setText(this.friend.getCity());
			ImageLoader imageLoader = new ImageLoader(this, R.drawable.default_avatar);
			imageLoader.DisplayImage(this.friend.getHeadimgurl(), this.avatar_iv);
            this.send_msg.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra("openId", WeixinUserInfoActivity.this.friend.getOpenid());
					intent.setClass(WeixinUserInfoActivity.this,
							WeixinChatActivity.class);
                    WeixinUserInfoActivity.this.startActivity(intent);
				}
			});
            this.add_ims.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
                    Toast.makeText(WeixinUserInfoActivity.this,"功能关闭",Toast.LENGTH_SHORT).show();
//					Intent intent = new Intent();
//					intent.putExtra("friendid", WeixinUserInfoActivity.this.friend.getOpenid());
//					intent.putExtra("friendname", WeixinUserInfoActivity.this.friend.getNickname());
//					intent.setClass(WeixinUserInfoActivity.this,
//							ContactSearchUI.class);
//                    WeixinUserInfoActivity.this.startActivity(intent);
				}
			});
		}
        this.leftBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                WeixinUserInfoActivity.this.finish();
			}
		});
        this.group_layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                WeixinUserInfoActivity.this.ModifyGroupDialog();
			}
		});
        this.remark_layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                WeixinUserInfoActivity.this.ModifyRemarkDialog();
			}
		});
	}

	private void ModifyRemarkDialog() {
		LayoutInflater factory = LayoutInflater.from(this);// 提示框
		View view = factory.inflate(R.layout.set_account_username, null);// 这里必须是final的
		final EditText edit = (EditText) view.findViewById(R.id.editText);// 获得输入框对象
		edit.setHint("");
		edit.setText(this.friend.getRemark());
		edit.setSelection(edit.getText().toString().length());
		edit.setSelectAllOnFocus(true);// 文本全选
		new Builder(this).setTitle("修改备注")// 提示框标题
				.setView(view).setPositiveButton("提交",// 提示框的两个按钮
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
                                WeixinUserInfoActivity.this.remarkname.setText(edit.getText().toString());
                                WeixinUserInfoActivity.this.connect(1);
							}
						}).setNegativeButton("取消", null).create().show();
	}

	private void ModifyGroupDialog() {
        this.groups = this.dao.getAllGroupInfo(this);
		if (this.groups.size() > 0) {
			String[] items = new String[this.groups.size()];
			// 在数组中存放数据 */
			for (int i = 0; i < this.groups.size(); i++) {
				items[i] = this.groups.get(i).getName();
			}
			new Builder(this).setTitle("请点击选择分组")
					.setPositiveButton("关闭", null)
					.setItems(items, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
                            WeixinUserInfoActivity.this.group.setText(WeixinUserInfoActivity.this.groups.get(which).getName());
                            WeixinUserInfoActivity.this.groupid = WeixinUserInfoActivity.this.groups.get(which).getId();
							new Builder(WeixinUserInfoActivity.this)
									.setMessage(
											"修改分组为"
													+ WeixinUserInfoActivity.this.groups.get(which)
															.getName())
									.setPositiveButton("取消", null)
									.setNeutralButton(
											"确定",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
                                                    WeixinUserInfoActivity.this.connect(2);
													dialog.dismiss();
												}
											}).show();
							dialog.dismiss();
						}
					}).show();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MyActivity.getInstance().finishActivity(this);
	}

	private void connect(int count) {
		WeixinUserInfoActivity.GetAsyncTask task = new WeixinUserInfoActivity.GetAsyncTask(count, this);
		task.execute();
	}

	private void connect(int count, String token) {
		WeixinUserInfoActivity.GetAsyncTask task = new WeixinUserInfoActivity.GetAsyncTask(count, this, token);
		task.execute();
	}

	/**
	 * 生成该类的对象，并调用execute方法之后 首先执行的是onProExecute方法 其次执行doInBackgroup方法
	 * 
	 */
	class GetAsyncTask extends AsyncTask<Integer, Integer, String> {

		private final Context context;
		private final int count;
		private String Token;
        private String remarkName;
		private ProgressDialog Prodialog;

		public GetAsyncTask(int count, Context context) {
            this.context = context;
			this.count = count;
            if(count == 3)
            this.remarkName= WeixinUserInfoActivity.this.remarkname.getText().toString();
		}

		public GetAsyncTask(int count, Context context, String Token) {
            this.context = context;
			this.count = count;
			this.Token = Token;
		}

		// 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			if (this.Prodialog == null) {
                this.Prodialog = new ProgressDialog(this.context);
                this.Prodialog.setTitle("信息保存");
                this.Prodialog.setMessage("正在保存，请稍候...");
                this.Prodialog.setIndeterminate(false);
                this.Prodialog.setCanceledOnTouchOutside(false);
                this.Prodialog.setButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});
                this.Prodialog.show();
			}

		}

		/**
		 * 这里的Integer参数对应AsyncTask中的第一个参数 这里的String返回值对应AsyncTask的第三个参数
		 * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
		 * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
		 */
		@Override
		protected String doInBackground(Integer... params) {
			HttpConnectionUtil connection = new HttpConnectionUtil();
			if (this.count == 1 || this.count == 2) {
				IniFile IniFile = new IniFile();
                String webRoot = UIHelper.getShareperference(context,constants.SAVE_INFORMATION,"Paht","");
                String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
                String appIniFile = webRoot
                        + IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                        constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
                String userIni = appIniFile;
                if(Integer.parseInt(IniFile.getIniString(userIni, "Login",
                        "LoginType", "0", (byte) 0)) == 1){
                    String dataPath = getFilesDir().getParentFile()
                            .getAbsolutePath();
                    if (dataPath.endsWith("/") == false) {
                        dataPath = dataPath + "/";
                    }
                    userIni = dataPath + "TbsApp.ini";
                }
				String verifyURL = IniFile.getIniString(userIni, "WeiXin",
						"WeToken", "http://e.tbs.com.cn/wechat.do", (byte) 0)
						+ "?action=getToken";
				return connection.asyncConnect(verifyURL, null, HttpConnectionUtil.HttpMethod.GET,
                        this.context);
			} else if (this.count == 3) {
				String verifyURL = "https://api.weixin.qq.com/cgi-bin/user/info/updateremark?access_token="
						+ this.Token;
				String menuUrl = "{\"openid\":\"" + WeixinUserInfoActivity.this.friend.getOpenid()
						+ "\",\"remark\":\"" + remarkName
						+ "\"}";
				JSONObject jsonObject = httpRequestUtil.httpsRequest(verifyURL,
						"GET", menuUrl);
				if (null != jsonObject) {
					try {
						if (jsonObject.getInt("errcode") == 0) {
							return "true";
						} else {
							return jsonObject.getInt("errcode") + ":"
									+ jsonObject.getString("errmsg");
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return null;
			} else {
				String verifyURL = "https://api.weixin.qq.com/cgi-bin/groups/members/update?access_token="
						+ this.Token;
				String menuUrl = "{\"openid\":\"" + WeixinUserInfoActivity.this.friend.getOpenid()
						+ "\",\"to_groupid\":" + WeixinUserInfoActivity.this.groupid + "}";
				JSONObject jsonObject = httpRequestUtil.httpsRequest(verifyURL,
						"GET", menuUrl);
				if (null != jsonObject) {
					try {
						if (jsonObject.getInt("errcode") == 0) {
							return "true";
						} else {
							return jsonObject.getInt("errcode") + ":"
									+ jsonObject.getString("errmsg");
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return null;
			}

		}

		/**
		 * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
		 * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
		 */
		@Override
		protected void onPostExecute(String result) {
			if (result == null) {
                this.Prodialog.dismiss();
				Toast.makeText(this.context, "请检查网络设置", Toast.LENGTH_SHORT).show();
			} else {
				if (this.count == 1) {
                    WeixinUserInfoActivity.this.connect(3, result);
                    this.Prodialog.dismiss();
				} else if (this.count == 2) {
                    WeixinUserInfoActivity.this.connect(4, result);
                    this.Prodialog.dismiss();
				} else if (this.count == 3) {
					if (result.equals("true")) {
                        WeixinUserInfoActivity.this.dao.modRemark(WeixinUserInfoActivity.this.friend.getOpenid(), WeixinUserInfoActivity.this.remarkname.getText()
								.toString());
						Toast.makeText(this.context, "修改完成", Toast.LENGTH_SHORT)
								.show();
					} else {
						Toast.makeText(this.context, result, Toast.LENGTH_SHORT)
								.show();
					}
                    this.Prodialog.dismiss();
				} else {
					if (result.equals("true")) {
                        WeixinUserInfoActivity.this.dao.modGroupid(WeixinUserInfoActivity.this.friend.getOpenid(), WeixinUserInfoActivity.this.groupid);
						Toast.makeText(this.context, "修改完成", Toast.LENGTH_SHORT)
								.show();
					} else {
						Toast.makeText(this.context, result, Toast.LENGTH_SHORT)
								.show();
					}
                    this.Prodialog.dismiss();
				}
			}
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