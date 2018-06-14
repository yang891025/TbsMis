package com.tbs.tbsmis.activity;

import com.tbs.tbsmis.R;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

public class SDUnavailableActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen_sd_unavailable);
		setResult(RESULT_CANCELED);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.retry:
            if(true == Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
this.setResult(Activity.RESULT_OK);
this.finish();
            }
            break;
		default:
		break;
		}
		
	}	

	@Override
	public boolean onKeyDown(int kCode, KeyEvent kEvent)
	{
		switch(kCode)
		{
		case KeyEvent.KEYCODE_DPAD_LEFT:
			return true;

		case KeyEvent.KEYCODE_DPAD_UP:
			return true;

		case KeyEvent.KEYCODE_DPAD_RIGHT:
			return true;

		case KeyEvent.KEYCODE_DPAD_DOWN:
			return true;
		case KeyEvent.KEYCODE_DPAD_CENTER:
			return true;
		case KeyEvent.KEYCODE_BACK:
            this.setResult(Activity.RESULT_CANCELED);
            this.finish();
            return true;

        }
		return super.onKeyDown(kCode, kEvent);
	}
}
