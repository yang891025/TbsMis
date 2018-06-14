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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.tbs.tbsmis.R;

import java.io.File;

@SuppressLint("HandlerLeak")
public class InformationDialog extends AlertDialog {
	protected static final int ID_USER = 100;
	private final FileInfo mFileInfo;
	@SuppressWarnings("unused")
	private final FileIconHelper mFileIconHelper;
	private final Context mContext;
	private View mView;

	public InformationDialog(Context context, FileInfo f,
			FileIconHelper iconHelper) {
		super(context);
        this.mFileInfo = f;
        this.mFileIconHelper = iconHelper;
        this.mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        this.mView = this.getLayoutInflater().inflate(R.layout.information_dialog, null);

		if (this.mFileInfo.IsDir) {
            this.setIcon(R.drawable.format_folder);
            this.asyncGetSize();
		} else {
            this.setIcon(R.drawable.format_picture);
		}
        this.setTitle(this.mFileInfo.fileName);

		((TextView) this.mView.findViewById(R.id.information_size))
				.setText(this.formatFileSizeString(this.mFileInfo.fileSize));
		((TextView) this.mView.findViewById(R.id.information_location))
				.setText(this.mFileInfo.filePath);
		((TextView) this.mView.findViewById(R.id.information_modified)).setText(Util
				.formatDateString(this.mContext, this.mFileInfo.ModifiedDate));
		((TextView) this.mView.findViewById(R.id.information_canread))
				.setText(this.mFileInfo.canRead ? R.string.yes : R.string.no);
		((TextView) this.mView.findViewById(R.id.information_canwrite))
				.setText(this.mFileInfo.canWrite ? R.string.yes : R.string.no);
		((TextView) this.mView.findViewById(R.id.information_ishidden))
				.setText(this.mFileInfo.isHidden ? R.string.yes : R.string.no);

        this.setView(this.mView);
        this.setButton(DialogInterface.BUTTON_NEGATIVE, this.mContext.getString(R.string.confirm_know),
				(OnClickListener) null);

		super.onCreate(savedInstanceState);
	}

	private final Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case InformationDialog.ID_USER:
				Bundle data = msg.getData();
				long size = data.getLong("SIZE");
				((TextView) InformationDialog.this.mView.findViewById(R.id.information_size))
						.setText(InformationDialog.this.formatFileSizeString(size));
			}
		}
    };

	@SuppressWarnings({ "rawtypes", "unused" })
	private AsyncTask task;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void asyncGetSize() {
        this.task = new AsyncTask() {
			private long size;

			@Override
			protected Object doInBackground(Object... params) {
				String path = (String) params[0];
                this.size = 0;
				getSize(path);
                InformationDialog.this.task = null;
				return null;
			}

			private void getSize(String path) {
				if (this.isCancelled())
					return;
				File file = new File(path);
				if (file.isDirectory()) {
					File[] listFiles = file.listFiles();
					if (listFiles == null)
						return;

					for (File f : listFiles) {
						if (this.isCancelled())
							return;

						getSize(f.getPath());
					}
				} else {
                    this.size += file.length();
                    InformationDialog.this.onSize(this.size);
				}
			}

		}.execute(this.mFileInfo.filePath);
	}

	private void onSize(long size) {
		Message msg = new Message();
		msg.what = InformationDialog.ID_USER;
		Bundle bd = new Bundle();
		bd.putLong("SIZE", size);
		msg.setData(bd);
        this.mHandler.sendMessage(msg); // 向Handler发送消息,更新UI
	}

	private String formatFileSizeString(long size) {
		String ret = "";
		if (size >= 1024) {
			ret = Util.convertStorage(size);
			ret += " ("
					+ this.mContext.getResources().getString(R.string.file_size,
							size) + ")";
		} else {
			ret = this.mContext.getResources().getString(R.string.file_size, size);
		}

		return ret;
	}
}
