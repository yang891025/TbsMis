/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * This file is part of FileExplorer.
 *
 * FileExplorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FileExplorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.tbs.tbsmis.file;

import android.R.string;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.tbs.tbsmis.R;

public class TextInputDialog extends AlertDialog {
    private String mInputText;
    private final String mTitle;
    private final String mMsg;
    private final TextInputDialog.OnFinishListener mListener;
    private final Context mContext;
    private View mView;
    private EditText mFolderName;

    public interface OnFinishListener {
        // return true to accept and dismiss, false reject
        boolean onFinish(String text);
    }

    public TextInputDialog(Context context, String title, String msg, String text, TextInputDialog.OnFinishListener listener) {
        super(context);
        this.mTitle = title;
        this.mMsg = msg;
        this.mListener = listener;
        this.mInputText = text;
        this.mContext = context;
    }

    public String getInputText() {
        return this.mInputText;
    }

    @Override
	protected void onCreate(Bundle savedInstanceState) {
        this.mView = this.getLayoutInflater().inflate(R.layout.textinput_dialog, null);
        this.setTitle(this.mTitle);
        this.setMessage(this.mMsg);
        this.mFolderName = (EditText) this.mView.findViewById(R.id.text);
        this.mFolderName.setText(this.mInputText);
        this.setView(this.mView);
        this.setButton(DialogInterface.BUTTON_POSITIVE, this.mContext.getString(string.ok),
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            TextInputDialog.this.mInputText = TextInputDialog.this.mFolderName.getText().toString();
                            if (TextInputDialog.this.mListener.onFinish(TextInputDialog.this.mInputText)) {
                                TextInputDialog.this.dismiss();
                            }
                        }
                    }
                });
        this.setButton(DialogInterface.BUTTON_NEGATIVE, this.mContext.getString(string.cancel),
                (OnClickListener) null);

        super.onCreate(savedInstanceState);
    }
}
