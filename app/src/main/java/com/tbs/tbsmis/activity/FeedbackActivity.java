package com.tbs.tbsmis.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.util.StringUtils;

/**
 * 用户反馈
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class FeedbackActivity extends Activity implements View.OnClickListener {

	private ImageButton mClose;
	private EditText mEditer, mMsgEditer;
	private Button mPublish;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.feedback_layout);
		// 添加Activity到堆栈
		MyActivity.getInstance().addActivity(this);
        initView();
	}

	// 初始化视图控件
	private void initView() {
        this.mClose = (ImageButton) this.findViewById(R.id.feedback_close_button);
        this.mEditer = (EditText) this.findViewById(R.id.feedback_content);
        this.mMsgEditer = (EditText) this.findViewById(R.id.feedback_msg);
        this.mPublish = (Button) this.findViewById(R.id.feedback_publish);

        this.mClose.setOnClickListener(this);
        this.mPublish.setOnClickListener(this.publishClickListener);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 结束Activity&从堆栈中移除
		MyActivity.getInstance().finishActivity(this);
	}

	private final View.OnClickListener publishClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String content = FeedbackActivity.this.mEditer.getText().toString();
			String contact = FeedbackActivity.this.mMsgEditer.getText().toString();
			if (StringUtils.isEmpty(content)) {
				Toast.makeText(FeedbackActivity.this, "反馈信息不能为空",
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (!StringUtils.isEmpty(contact)) {
				if (StringUtils.checkPhone(contact) == false) {
					if (StringUtils.checkEmail(contact) == false) {
						Toast.makeText(FeedbackActivity.this, "请输入正确的邮箱或者手机号",
								Toast.LENGTH_SHORT).show();
						return;
					}
				}
			}
			Intent i = new Intent(Intent.ACTION_SEND);
			// i.setType("text/plain"); //模拟器
			String[] receiver = { "tbsinfo@sohu.com" };
			i.setType("message/rfc822"); // 真机
			i.putExtra(Intent.EXTRA_EMAIL, receiver);
			i.putExtra(Intent.EXTRA_SUBJECT, "用户反馈-TBSMis-Android客户端");
			i.putExtra(Intent.EXTRA_TEXT, content + "\r\n" + contact);
            FeedbackActivity.this.startActivity(Intent.createChooser(i, "Sending mail..."));
            FeedbackActivity.this.finish();
		}
	};

	// public boolean checkPhone(String phone) {
	// Pattern pattern = Pattern
	// .compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
	// Matcher matcher = pattern.matcher(phone);
	// if (matcher.matches()) {
	// return true;
	// }
	// return false;
	// }
	//
	// public boolean checkEmail(String email) {
	//
	// Pattern pattern = Pattern
	// .compile("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
	// Matcher matcher = pattern.matcher(email);
	// if (matcher.matches()) {
	// return true;
	// }
	// return false;
	// }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
        this.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.onBackPressed();
		}
		return true;
	}
}
