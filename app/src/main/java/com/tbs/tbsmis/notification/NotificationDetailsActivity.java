/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tbs.tbsmis.notification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbs.tbsmis.R;

/**
 * Activity for displaying the notification details view.
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class NotificationDetailsActivity extends Activity {

	private String callbackActivityPackageName;

	private String callbackActivityClassName;

	private ImageView backBtn;

	private TextView Title;

	private TextView textDetails;

	private String notificationUri;

	private String notificationId;

	private String notificationTitle;

	private String notificationMessage;

	public NotificationDetailsActivity() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences sharedPrefs = getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        this.callbackActivityPackageName = sharedPrefs.getString(
				Constants.CALLBACK_ACTIVITY_PACKAGE_NAME, "");
        this.callbackActivityClassName = sharedPrefs.getString(
				Constants.CALLBACK_ACTIVITY_CLASS_NAME, "");
		Intent intent = this.getIntent();
        this.notificationId = intent.getStringExtra(Constants.NOTIFICATION_ID);
		intent
				.getStringExtra(Constants.NOTIFICATION_API_KEY);
        this.notificationTitle = intent.getStringExtra(Constants.NOTIFICATION_TITLE);
        this.notificationMessage = intent
				.getStringExtra(Constants.NOTIFICATION_MESSAGE);
        this.notificationUri = intent.getStringExtra(Constants.NOTIFICATION_URI);
		// View rootView = createView(notificationTitle, notificationMessage,
		// notificationUri);
        this.setContentView(R.layout.notification_detail);
        this.init(this.notificationTitle, this.notificationMessage);
	}

	private void init(String title, String message) {
		// TODO Auto-generated method stub
        this.backBtn = (ImageView) this.findViewById(R.id.basic_back_btn);
        this.Title = (TextView) this.findViewById(R.id.title_tvv);
        this.textDetails = (TextView) this.findViewById(R.id.textDetails);
        this.Title.setText(title);
        this.textDetails.setText(message);
        this.backBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent;
				if (NotificationDetailsActivity.this.notificationUri != null
						&& NotificationDetailsActivity.this.notificationUri.length() > 0
						&& (NotificationDetailsActivity.this.notificationUri.startsWith("http:")
								|| NotificationDetailsActivity.this.notificationUri.startsWith("https:")
								|| NotificationDetailsActivity.this.notificationUri.startsWith("tel:") || NotificationDetailsActivity.this
                        .notificationUri
									.startsWith("geo:"))) {
					intent = new Intent(Intent.ACTION_VIEW, Uri
							.parse(NotificationDetailsActivity.this.notificationUri));
				} else {
					intent = new Intent().setClassName(
                            NotificationDetailsActivity.this.callbackActivityPackageName,
                            NotificationDetailsActivity.this.callbackActivityClassName);
					intent.putExtra("flag", 2);
					intent.putExtra(Constants.NOTIFICATION_ID, NotificationDetailsActivity.this.notificationId);
					intent.putExtra(Constants.NOTIFICATION_TITLE, NotificationDetailsActivity.this.notificationTitle);
					intent.putExtra(Constants.NOTIFICATION_URI, NotificationDetailsActivity.this.notificationUri);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					// intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					// intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
					// intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				}

                NotificationDetailsActivity.this.startActivity(intent);
                NotificationDetailsActivity.this.finish();
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
        this.finish();
	}

	@Override
	protected void onStop() {
		super.onStop();
        this.finish();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onNewIntent(Intent intent) {
        this.setIntent(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent;
			if (this.notificationUri != null
					&& this.notificationUri.length() > 0
					&& (this.notificationUri.startsWith("http:")
							|| this.notificationUri.startsWith("https:")
							|| this.notificationUri.startsWith("tel:") || this.notificationUri
								.startsWith("geo:"))) {
				intent = new Intent(Intent.ACTION_VIEW,
						Uri.parse(this.notificationUri));
			} else {
				intent = new Intent().setClassName(this.callbackActivityPackageName,
                        this.callbackActivityClassName);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				// intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
				// intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			}

            this.startActivity(intent);
            this.finish();
		}
		return true;
	}
}
