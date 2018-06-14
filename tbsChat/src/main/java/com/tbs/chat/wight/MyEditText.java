package com.tbs.chat.wight;

import android.content.Context;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;

public class MyEditText extends EditText {

	Context mContext;
	
	public MyEditText(Context context) {
		super(context);
		this.mContext = context;
	}

	@Override
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
		return new LimitInputConnection(super.onCreateInputConnection(outAttrs), false);
	}
	
	
	class LimitInputConnection extends InputConnectionWrapper {

		public LimitInputConnection(InputConnection target, boolean mutable) {
			super(target, mutable);
			
		}
	}
}
