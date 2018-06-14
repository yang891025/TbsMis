package com.tbs.tbsmis.weixin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.check.WeChatMessageAdapter;
import com.tbs.tbsmis.database.dao.DBUtil;
import com.tbs.tbsmis.entity.WXTextEntity;

import java.util.ArrayList;

public class WeixinSearchActivity extends Activity {
	private ImageView finishBtn;
	private int searchState;
	private EditText searchEdit;
	private ImageButton cleanBtn;
	private ArrayList<WXTextEntity> message;
	private TextView empty_voicesearch_tip_tv;
	private TextView empty_blacklist_tip_tv;
	private ListView search_chat_content_lv;
	private DBUtil dao;
	protected WeChatMessageAdapter searchAdapter;
	private String openId = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.weixin_search);
		MyActivity.getInstance().addActivity(this);
        this.init();
	}

	private void init() {
        this.dao = DBUtil.getInstance(this);
        this.finishBtn = (ImageView) this.findViewById(R.id.more_btn2);
        this.searchEdit = (EditText) this.findViewById(R.id.search_bar_et);
        this.cleanBtn = (ImageButton) this.findViewById(R.id.search_clear_bt);
        this.empty_voicesearch_tip_tv = (TextView) this.findViewById(R.id.empty_voicesearch_tip_tv);
        this.empty_blacklist_tip_tv = (TextView) this.findViewById(R.id.empty_blacklist_tip_tv);
        this.search_chat_content_lv = (ListView) this.findViewById(R.id.search_listview);
        this.finishBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                WeixinSearchActivity.this.finish();
                WeixinSearchActivity.this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
			}
		});
        this.searchEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
                WeixinSearchActivity.this.searchState = 1;// 短信搜索
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (TextUtils.isEmpty(s)) {
                    WeixinSearchActivity.this.cleanBtn.setVisibility(View.INVISIBLE);
                    WeixinSearchActivity.this.message.clear();
                    WeixinSearchActivity.this.searchAdapter.notifyDataSetChanged();
                    WeixinSearchActivity.this.searchState = 0;// 短信搜索
				} else {
                    WeixinSearchActivity.this.cleanBtn.setVisibility(View.VISIBLE);
                    WeixinSearchActivity.this.initAdapter();
					if (WeixinSearchActivity.this.message.size() > 0) {
                        WeixinSearchActivity.this.empty_voicesearch_tip_tv.setVisibility(View.GONE);
                        WeixinSearchActivity.this.search_chat_content_lv.setAdapter(WeixinSearchActivity.this.searchAdapter);
					} else {
                        WeixinSearchActivity.this.empty_voicesearch_tip_tv.setVisibility(View.VISIBLE);
					}
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

        this.searchEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
                    WeixinSearchActivity.this.cleanBtn.setVisibility(View.INVISIBLE);
				} else {
					if (((EditText) v).getText().length() > 0) {
                        WeixinSearchActivity.this.cleanBtn.setVisibility(View.VISIBLE);
					}
				}
			}
		});

        this.cleanBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
                WeixinSearchActivity.this.searchEdit.setText("");
                WeixinSearchActivity.this.empty_voicesearch_tip_tv.setVisibility(View.GONE);
                WeixinSearchActivity.this.message.clear();
                WeixinSearchActivity.this.searchAdapter.notifyDataSetChanged();
                WeixinSearchActivity.this.searchState = 0;// 短信搜索
			}
		});
        this.search_chat_content_lv
				.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						WeChatMessageAdapter.ViewHolder holder = (WeChatMessageAdapter.ViewHolder) view.getTag();
						String apptext = holder.openId;
						long msgid = holder.msgid;
						// 跳转到回复详情
						Intent intent = new Intent();
						intent.putExtra("search", 1);
						intent.putExtra("msgid", msgid);
						intent.putExtra("openId", apptext);
						intent.setClass(WeixinSearchActivity.this,
								WeixinChatActivity.class);
                        WeixinSearchActivity.this.startActivity(intent);
					}
				});
		if (this.getIntent().getExtras() != null) {
			Intent intent = this.getIntent();
            this.openId = intent.getStringExtra("openId");
		}
	}

	protected void initAdapter() {
		// TODO Auto-generated method stub
		if(this.openId.equals("")){
            this.message = new ArrayList<WXTextEntity>();
            this.message = this.dao.getMessage2(this,
                    this.searchEdit.getText().toString());
            this.searchAdapter = new WeChatMessageAdapter(this.message,
                    this, this.dao);
		}else{
            this.message = new ArrayList<WXTextEntity>();
            this.message = this.dao.getMessage2(this,
                    this.searchEdit.getText().toString(), this.openId);
            this.searchAdapter = new WeChatMessageAdapter(this.message,
                    this, this.dao);
		}
		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MyActivity.getInstance().finishActivity(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.onBackPressed();
            this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
		}
		return true;
	}
}