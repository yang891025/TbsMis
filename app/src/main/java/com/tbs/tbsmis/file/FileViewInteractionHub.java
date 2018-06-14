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

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.activity.FtsManagerActivity;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.StringUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

@TargetApi(VERSION_CODES.HONEYCOMB)
public class FileViewInteractionHub implements FileOperationHelper.IOperationProgressListener
{
    private static final String LOG_TAG = "FileViewInteractionHub";

    private final IFileInteractionListener mFileViewListener;

    private final ArrayList<FileInfo> mCheckedFileNameList = new ArrayList<FileInfo>();

    private final FileOperationHelper mFileOperationHelper;

    private final FileSortHelper mFileSortHelper;

    private View mConfirmOperationBar;

    private ProgressDialog progressDialog;

    private View mNavigationBar;

    private TextView mNavigationBarText;

    private View mDropdownNavigation;

    private ImageView mNavigationBarUpDownArrow;

    private final Context mContext;
    private TextView message;
    private TextView message_t;
    private TextView from;
    private TextView to;
    private TextView curr_message;
    private TextView item_percent;
    private ProgressBar item_progress_bar;
    private TextView num_completed;
    private LinearLayout time_remaining_panel;
    private LinearLayout total_progress_panel;
    private ProgressBar total_progress_bar;
    private TextView num_total;
    private TextView time_remaining;
    private TextView total_percent;
    private TextView speed;
    private int time;
    private FtMSGNotify ftMSGNotify;
    private AlertDialog ModifyDialog;
    private FileViewInteractionHub.mTimerTask myTimerTask;

    public enum Mode
    {
        View, Pick
    }

    public FileViewInteractionHub(IFileInteractionListener fileViewListener) {
        assert fileViewListener != null;
        this.mFileViewListener = fileViewListener;
        this.setup();
        this.mFileOperationHelper = new FileOperationHelper(this);
        this.mFileSortHelper = new FileSortHelper();
        this.mContext = fileViewListener.getContext();
    }

    private void showProgress(String msg) {
        this.progressDialog = new ProgressDialog(this.mContext);
        // dialog.setIcon(R.drawable.icon);
        this.progressDialog.setMessage(msg);
        this.progressDialog.setIndeterminate(true);
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();
    }

    public void sortCurrentList() {
        this.mFileViewListener.sortCurrentList(this.mFileSortHelper);
    }

    public boolean canShowCheckBox() {
        return this.mConfirmOperationBar.getVisibility() != View.VISIBLE;
    }

    public void showConfirmOperationBar(boolean show) {
        this.mConfirmOperationBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void addContextMenuSelectedItem() {
        if (this.mCheckedFileNameList.size() == 0) {
            int pos = this.mListViewContextMenuSelectedItem;
            if (pos != -1) {
                FileInfo fileInfo = this.mFileViewListener.getItem(pos);
                if (fileInfo != null) {
                    this.mCheckedFileNameList.add(fileInfo);
                }
            }
        }
    }

    public ArrayList<FileInfo> getSelectedFileList() {
        //System.out.println("size = " + mCheckedFileNameList.size());
        return this.mCheckedFileNameList;
    }

    public boolean canPaste() {
        return this.mFileOperationHelper.canPaste();
    }

    // operation finish notification
    @Override
    public void onFinish() {
        if (this.progressDialog != null) {
            this.progressDialog.dismiss();
            this.progressDialog = null;
        }

        this.mFileViewListener.runOnUiThread(new Runnable()
        {
            @Override
            public void run() {
                FileViewInteractionHub.this.showConfirmOperationBar(false);
                FileViewInteractionHub.this.clearSelection();
                FileViewInteractionHub.this.refreshFileList();
            }
        });
    }

    public FileInfo getItem(int pos) {
        return this.mFileViewListener.getItem(pos);
    }

    public boolean isInSelection() {
        return this.mCheckedFileNameList.size() > 0;
    }

    public boolean isMoveState() {
        return this.mFileOperationHelper.isMoveState()
                || this.mFileOperationHelper.canPaste();
    }

    private void setup() {
        this.setupNaivgationBar();
        this.setupFileListView();
        this.setupOperationPane();
    }

    private void setupNaivgationBar() {
        this.mNavigationBar = this.mFileViewListener.getViewById(R.id.navigation_bar);
        this.mNavigationBarText = (TextView) this.mFileViewListener
                .getViewById(R.id.current_path_view);
        this.mNavigationBarUpDownArrow = (ImageView) this.mFileViewListener
                .getViewById(R.id.path_pane_arrow);
        View clickable = this.mFileViewListener.getViewById(R.id.current_path_pane);
        clickable.setOnClickListener(this.buttonClick);

        this.mDropdownNavigation = this.mFileViewListener
                .getViewById(R.id.dropdown_navigation);

        this.setupClick(this.mNavigationBar, R.id.path_pane_up_level);
    }

    // buttons
    private void setupOperationPane() {
        this.mConfirmOperationBar = this.mFileViewListener
                .getViewById(R.id.moving_operation_bar);
        this.setupClick(this.mConfirmOperationBar, R.id.button_moving_confirm);
        this.setupClick(this.mConfirmOperationBar, R.id.button_moving_cancel);
    }

    private void setupClick(View v, int id) {
        View button = v != null ? v.findViewById(id) : this.mFileViewListener
                .getViewById(id);
        if (button != null)
            button.setOnClickListener(this.buttonClick);
    }

    private final OnClickListener buttonClick = new OnClickListener()
    {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_operation_copy:

                    ((FtsManagerActivity) ((FileExplorerTabActivity) FileViewInteractionHub.this.mContext)
                            .getFragment(Util.CATEGORY_TAB_INDEX))
                            .copyFile(FileViewInteractionHub.this.mCheckedFileNameList, 0);
                    FileViewInteractionHub.this.onOperationCopy();
                    break;
                case R.id.button_operation_move:
                    ((FtsManagerActivity) ((FileExplorerTabActivity) FileViewInteractionHub.this.mContext)
                            .getFragment(Util.CATEGORY_TAB_INDEX))
                            .copyFile(FileViewInteractionHub.this.mCheckedFileNameList, 2);
                    FileViewInteractionHub.this.onOperationMove();
                    break;
                case R.id.button_operation_send:
                    FileViewInteractionHub.this.onOperationSend();
                    break;
                case R.id.button_operation_delete:
                    FileViewInteractionHub.this.onOperationDelete();
                    break;
                case R.id.button_operation_cancel:
                    FileViewInteractionHub.this.onOperationSelectAllOrCancel();
                    break;
                case R.id.current_path_pane:
                    FileViewInteractionHub.this.onNavigationBarClick();
                    break;
                case R.id.button_moving_confirm:
//                    ((FtsManagerActivity) ((FileExplorerTabActivity) mContext)
//                            .getFragment(Util.CATEGORY_TAB_INDEX))
//                            .showConfirmOperationBar(false);
                    FileViewInteractionHub.this.onOperationButtonConfirm();
                    break;
                case R.id.button_moving_cancel:
                    ((FtsManagerActivity) ((FileExplorerTabActivity) FileViewInteractionHub.this.mContext)
                            .getFragment(Util.CATEGORY_TAB_INDEX))
                            .showConfirmOperationBar(false);
                    FileViewInteractionHub.this.onOperationButtonCancel();
                    break;
                case R.id.path_pane_up_level:
                    FileViewInteractionHub.this.onOperationUpLevel();
                    ActionMode mode = ((FileExplorerTabActivity) FileViewInteractionHub.this.mContext)
                            .getActionMode();
                    if (mode != null) {
                        mode.finish();
                    }
                    break;
            }
        }

    };

    private void onOperationReferesh() {
        this.refreshFileList();
    }

    private void onOperationFavorite() {
        String path = this.mCurrentPath;

        if (this.mListViewContextMenuSelectedItem != -1) {
            path = this.mFileViewListener.getItem(this.mListViewContextMenuSelectedItem).filePath;
        }

        this.onOperationFavorite(path);
    }

    private void onOperationSetting() {
        Intent intent = new Intent(this.mContext,
                FileExplorerPreferenceActivity.class);
        if (intent != null) {
            try {
                this.mContext.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.e(FileViewInteractionHub.LOG_TAG, "fail to start setting: " + e);
            }
        }
    }

    private void onOperationFavorite(String path) {
        FavoriteDatabaseHelper databaseHelper = FavoriteDatabaseHelper
                .getInstance();
        if (databaseHelper != null) {
            int stringId = 0;
            if (databaseHelper.isFavorite(path)) {
                databaseHelper.delete(path);
                stringId = R.string.removed_favorite;
            } else {
                databaseHelper.insert(Util.getNameFromFilepath(path), path);
                stringId = R.string.added_favorite;
            }

            Toast.makeText(this.mContext, stringId, Toast.LENGTH_SHORT).show();
        }
    }

    private void onOperationShowSysFiles() {
        Settings.instance().setShowDotAndHiddenFiles(
                !Settings.instance().getShowDotAndHiddenFiles());
        this.refreshFileList();
    }

    public void onOperationSelectAllOrCancel() {
        if (!this.isSelectedAll()) {
            this.onOperationSelectAll();
        } else {
            this.clearSelection();
        }
    }

    public void onOperationSelectAll() {
        this.mCheckedFileNameList.clear();
        for (FileInfo f : this.mFileViewListener.getAllFiles()) {
            f.Selected = true;
            this.mCheckedFileNameList.add(f);
        }
        FileExplorerTabActivity fileExplorerTabActivity = (FileExplorerTabActivity) this.mContext;
        ActionMode mode = fileExplorerTabActivity.getActionMode();
        if (mode == null) {
            mode = fileExplorerTabActivity.startActionMode(new FileListItem.ModeCallback(
                    this.mContext, this));
            fileExplorerTabActivity.setActionMode(mode);
            Util.updateActionModeTitle(mode, this.mContext, this.getSelectedFileList()
                    .size());
        }
        this.mFileViewListener.onDataChanged();
    }

    private final View.OnClickListener navigationClick = new View.OnClickListener()
    {

        @Override
        public void onClick(View v) {
            String path = (String) v.getTag();
            assert path != null;
            FileViewInteractionHub.this.showDropdownNavigation(false);
            if (FileViewInteractionHub.this.mFileViewListener.onNavigation(path))
                return;

            if (path.isEmpty()) {
                FileViewInteractionHub.this.mCurrentPath = FileViewInteractionHub.this.mRoot;
            } else {
                FileViewInteractionHub.this.mCurrentPath = path;
            }
            FileViewInteractionHub.this.refreshFileList();
        }

    };

    protected void onNavigationBarClick() {
        if (this.mDropdownNavigation.getVisibility() == View.VISIBLE) {
            this.showDropdownNavigation(false);
        } else {
            LinearLayout list = (LinearLayout) this.mDropdownNavigation
                    .findViewById(R.id.dropdown_navigation_list);
            list.removeAllViews();
            int pos = 0;
            String displayPath = this.mFileViewListener.getDisplayPath(this.mCurrentPath);
            boolean root = true;
            int left = 0;
            while (pos != -1 && !displayPath.equals("/")) {// 如果当前位置在根文件夹则不显示导航条
                int end = displayPath.indexOf("/", pos);
                if (end == -1)
                    break;

                View listItem = LayoutInflater.from(this.mContext).inflate(
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
                listItem.setTag(this.mFileViewListener.getRealPath(displayPath
                        .substring(0, end)));
                pos = end + 1;
                list.addView(listItem);
            }
            if (list.getChildCount() > 0)
                this.showDropdownNavigation(true);

        }
    }

    public boolean onOperationUpLevel() {
        this.showDropdownNavigation(false);

        if (this.mFileViewListener.onOperation(GlobalConsts.OPERATION_UP_LEVEL)) {
            return true;
        }

        if (!this.mRoot.equals(this.mCurrentPath)) {
            this.mCurrentPath = new File(this.mCurrentPath).getParent();
            this.refreshFileList();
            return true;
        }

        return false;
    }

    public void onOperationCreateFolder() {
        TextInputDialog dialog = new TextInputDialog(this.mContext,
                this.mContext.getString(R.string.operation_create_folder),
                this.mContext.getString(R.string.operation_create_folder_message),
                this.mContext.getString(R.string.new_folder_name),
                new TextInputDialog.OnFinishListener()
                {
                    @Override
                    public boolean onFinish(String text) {
                        return FileViewInteractionHub.this.doCreateFolder(text);
                    }
                });

        dialog.show();
    }

    private boolean doCreateFolder(String text) {
        if (TextUtils.isEmpty(text))
            return false;

        if (this.mFileOperationHelper.CreateFolder(this.mCurrentPath, text)) {
            this.mFileViewListener.addSingleFile(Util.GetFileInfo(Util.makePath(
                    this.mCurrentPath, text)));
            this.mFileListView.setSelection(this.mFileListView.getCount() - 1);
        } else {
            new Builder(this.mContext)
                    .setMessage(
                            this.mContext.getString(R.string.fail_to_create_folder))
                    .setPositiveButton(R.string.confirm, null).create().show();
            return false;
        }

        return true;
    }

    public void onOperationSearch() {

    }

    public void onSortChanged(FileSortHelper.SortMethod s) {
        if (this.mFileSortHelper.getSortMethod() != s) {
            this.mFileSortHelper.setSortMethog(s);
            this.sortCurrentList();
        }
    }

    public void onOperationCopy() {
        this.onOperationCopy(this.getSelectedFileList());
    }

    public void onOperationCopy(ArrayList<FileInfo> files) {
        this.mFileOperationHelper.Copy(files);
        this.clearSelection();
        this.refreshFileList();
    }

    public void onOperationCopyPath() {
        if (this.getSelectedFileList().size() == 1) {
            this.copy(this.getSelectedFileList().get(0).filePath);
        }
        this.clearSelection();
    }

    @SuppressWarnings("deprecation")
    private void copy(CharSequence text) {
        ClipboardManager cm = (ClipboardManager) this.mContext
                .getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(text);
    }

    private void onOperationPaste() {
        if (this.canPaste()) {
            if (this.mFileOperationHelper.Paste(this.mCurrentPath)) {
                this.showProgress(this.mContext.getString(R.string.operation_pasting));
            } else {
                this.ftMSGNotify = new FtMSGNotify();
                this.ftMSGNotify.setiCancelFlag(0);
                Builder builder = new Builder(this.mContext);
                builder.setTitle("粘贴");
//                builder.setMessage("");
                builder.setCancelable(false);
                LayoutInflater inflater = LayoutInflater.from(this.mContext);
                View v = inflater.inflate(R.layout.archive_progress_dialog, null);
                this.time_remaining_panel = (LinearLayout) v.findViewById(R.id.time_remaining_panel);
                this.total_progress_panel = (LinearLayout) v.findViewById(R.id.total_progress_panel);
                this.item_progress_bar = (ProgressBar) v.findViewById(R.id.item_progress_bar);
                this.item_percent = (TextView) v.findViewById(R.id.item_percent);
                this.total_progress_bar = (ProgressBar) v.findViewById(R.id.total_progress_bar);
                this.time_remaining = (TextView) v.findViewById(R.id.time_remaining);
                this.message = (TextView) v.findViewById(R.id.message);
                this.from = (TextView) v.findViewById(R.id.from);
                this.to = (TextView) v.findViewById(R.id.to);
                this.curr_message = (TextView) v.findViewById(R.id.curr_message);
                this.num_completed = (TextView) v.findViewById(R.id.num_completed);
                this.num_total = (TextView) v.findViewById(R.id.num_total);
                this.total_percent = (TextView) v.findViewById(R.id.total_percent);
                this.message_t = (TextView) v.findViewById(R.id.message_t);
                this.speed = (TextView) v.findViewById(R.id.speed);
                this.total_progress_panel.setVisibility(View.GONE);
                builder.setView(v);
                builder.setNeutralButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FileViewInteractionHub.this.ftMSGNotify.setCancelFlag(true);
                        FileViewInteractionHub.this.ftMSGNotify.setiCancelFlag(2);
                        dialog.dismiss();
                    }
                });
                this.ModifyDialog = builder.create();
                this.ModifyDialog.setCanceledOnTouchOutside(false);
                this.message.setText("正在计算文件大小和个数...");
//                from.setText(mCurFileNameList.get(0).filePath.substring(0, mCurFileNameList.get(0).filePath
//                        .lastIndexOf("/")));
                this.to.setText(this.mCurrentPath);
                if (this.mFileOperationHelper.Paste(this.mCurrentPath, this.ftMSGNotify)) {
                    this.ModifyDialog.show();
                    //System.out.println("显示进度对话框");
                    Timer timer = new Timer();
                    if (timer != null) {
                        if (this.myTimerTask != null) {
                            this.time = 0;
                            this.myTimerTask.cancel();
                        }
                    }
                    this.myTimerTask = new FileViewInteractionHub.mTimerTask();
                    timer.schedule(this.myTimerTask, 1000, 1000);
                }
                //System.out.println("界面管理完成");
                // return;
            }
        }
    }
    private void diskOperationPaste() {
        if (this.canPaste()) {
            if (this.mFileOperationHelper.Paste(this.mCurrentPath)) {
                this.showProgress(this.mContext.getString(R.string.operation_pasting));
            } else {
                mFileOperationHelper.Paste(mContext,this.mCurrentPath);
            }
        }
    }
    public void start() {
        if (this.ftMSGNotify.getiCancelFlag() != 0) {
            Message message = new Message();
            message.what = this.ftMSGNotify.getiCancelFlag();
            this.handler.sendMessage(message);
            this.time = 0;
            this.myTimerTask.cancel();
            this.ModifyDialog.cancel();
        } else if (this.ftMSGNotify.getTotal() == this.ftMSGNotify.getProg() && this.ftMSGNotify.getCancelFlag()) {
            Message message = new Message();
            message.what = 6;
            this.handler.sendMessage(message);
            this.time = 0;
            this.myTimerTask.cancel();
            this.ModifyDialog.cancel();
        } else {
            if (this.ftMSGNotify.getTotal() != 0) {
                Message message = new Message();
                message.what = 1;
                this.handler.sendMessage(message);
            }
        }
    }

    class mTimerTask extends TimerTask
    {
        public void run() {
            FileViewInteractionHub.this.time = FileViewInteractionHub.this.time + 1;
            FileViewInteractionHub.this.start();
        }
    }

    Handler handler = new Handler()
    {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    FileViewInteractionHub.this.from.setText(FileViewInteractionHub.this.ftMSGNotify.getDesPath());
                    FileViewInteractionHub.this.message_t.setText(FileViewInteractionHub.this.ftMSGNotify.getAllPath());
                    FileViewInteractionHub.this.message.setText(FileViewInteractionHub.this.ftMSGNotify.getAllmessage());
                    FileViewInteractionHub.this.time_remaining_panel.setVisibility(View.VISIBLE);
                    if (FileViewInteractionHub.this.ftMSGNotify.getFileCount() > 1) {
                        FileViewInteractionHub.this.total_progress_panel.setVisibility(View.VISIBLE);
                        FileViewInteractionHub.this.total_progress_bar.setMax((int) FileViewInteractionHub.this.ftMSGNotify.getTotalSize());
                        FileViewInteractionHub.this.total_progress_bar.setProgress((int) (FileViewInteractionHub.this.ftMSGNotify.getiSize() + FileViewInteractionHub.this
                                .ftMSGNotify.getProg()));
                        FileViewInteractionHub.this.num_completed.setText(FileViewInteractionHub.this.ftMSGNotify.getCfilecount() + "");
                        FileViewInteractionHub.this.num_total.setText(FileViewInteractionHub.this.ftMSGNotify.getFileCount() + "");
                        FileViewInteractionHub.this.total_percent.setText((int) ((FileViewInteractionHub.this.ftMSGNotify.getiSize() + FileViewInteractionHub.this.ftMSGNotify.getProg()) * 1.0 /
                                FileViewInteractionHub.this.ftMSGNotify
                                .getTotalSize()
                                * 100)
                                + "%");
                    }
                    if (FileViewInteractionHub.this.ftMSGNotify.getProg() > FileViewInteractionHub.this.ftMSGNotify.getPiProg()) {
                        FileViewInteractionHub.this.speed.setText(FileUtils.formatFileSize(FileViewInteractionHub.this.ftMSGNotify.getProg() - FileViewInteractionHub.this
                                .ftMSGNotify.getPiProg()) + "/s");
                        FileViewInteractionHub.this.ftMSGNotify.setPiProg(FileViewInteractionHub.this.ftMSGNotify.getProg());
                    }
                    FileViewInteractionHub.this.item_progress_bar.setMax((int) FileViewInteractionHub.this.ftMSGNotify.getTotal());
                    FileViewInteractionHub.this.item_progress_bar.setProgress((int) FileViewInteractionHub.this.ftMSGNotify.getProg());
                    FileViewInteractionHub.this.time_remaining.setText(StringUtils.secToTime(FileViewInteractionHub.this.time));
                    FileViewInteractionHub.this.curr_message.setText(FileUtils.getFileName(FileViewInteractionHub.this.ftMSGNotify.CurrentFile()));
                    FileViewInteractionHub.this.item_percent.setText((int) (
                            FileViewInteractionHub.this.ftMSGNotify.getProg() * 1.0 / FileViewInteractionHub.this.ftMSGNotify.getTotal() * 100) + "%");
                    break;
                case 2:
                    FileUtils.deleteFile(FileViewInteractionHub.this.ftMSGNotify.CurrentFile());
                    FileViewInteractionHub.this.ftMSGNotify.setiCancelFlag(0);
                    FileViewInteractionHub.this.refreshFileList();
                    Toast.makeText(FileViewInteractionHub.this.mContext, "取消操作", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(FileViewInteractionHub.this.mContext, "创建链接失败", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(FileViewInteractionHub.this.mContext, "创建链接失败", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    FileUtils.deleteFile(FileViewInteractionHub.this.ftMSGNotify.CurrentFile());
                    FileViewInteractionHub.this.refreshFileList();
                    Toast.makeText(FileViewInteractionHub.this.mContext, "文件传输异常", Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    Toast.makeText(FileViewInteractionHub.this.mContext, "复制文件完成", Toast.LENGTH_SHORT).show();
                    break;
                case 7:
                    Toast.makeText(FileViewInteractionHub.this.mContext, "无文件可复制", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }

    };

    public void onOperationMove() {
        this.mFileOperationHelper.StartMove(this.getSelectedFileList());
        this.clearSelection();
//        showConfirmOperationBar(true);
//        View confirmButton = mConfirmOperationBar
//                .findViewById(R.R.id.button_moving_confirm);
//        confirmButton.setEnabled(false);
        // refresh to hide selected files
        this.refreshFileList();
    }

    public void refreshFileList() {
        this.clearSelection();
        this.updateNavigationPane();

        // onRefreshFileList returns true indicates list has changed
        this.mFileViewListener.onRefreshFileList(this.mCurrentPath, this.mFileSortHelper);

        // update move operation button state
        this.updateConfirmButtons();

    }

    private void updateConfirmButtons() {
        if (this.mConfirmOperationBar.getVisibility() == View.GONE)
            return;

        Button confirmButton = (Button) this.mConfirmOperationBar
                .findViewById(R.id.button_moving_confirm);
        int text = R.string.operation_paste;
        if (this.isSelectingFiles()) {
            confirmButton.setEnabled(this.mCheckedFileNameList.size() != 0);
            text = R.string.operation_send;
        } else if (this.isMoveState()) {
            confirmButton
                    .setEnabled(this.mFileOperationHelper.canMove(this.mCurrentPath));
        }

        confirmButton.setText(text);
    }

    private void updateNavigationPane() {
        View upLevel = this.mFileViewListener.getViewById(R.id.path_pane_up_level);
        upLevel.setVisibility(this.mRoot.equals(this.mCurrentPath) ? View.INVISIBLE
                : View.VISIBLE);

        View arrow = this.mFileViewListener.getViewById(R.id.path_pane_arrow);
        arrow.setVisibility(this.mRoot.equals(this.mCurrentPath) ? View.GONE
                : View.VISIBLE);

        this.mNavigationBarText.setText(this.mFileViewListener
                .getDisplayPath(this.mCurrentPath));
    }

    public void onOperationSend() {
        ArrayList<FileInfo> selectedFileList = this.getSelectedFileList();
        for (FileInfo f : selectedFileList) {
            if (f.IsDir) {
                AlertDialog dialog = new Builder(this.mContext)
                        .setMessage(R.string.error_info_cant_send_folder)
                        .setPositiveButton(R.string.confirm, null).create();
                dialog.show();
                return;
            }
        }

        Intent intent = IntentBuilder.buildSendFile(selectedFileList);
        if (intent != null) {
            try {
                this.mFileViewListener.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.e(FileViewInteractionHub.LOG_TAG, "fail to view file: " + e);
            }
        }
        this.clearSelection();
    }

    public void onOperationRename() {
        int pos = this.mListViewContextMenuSelectedItem;
        if (pos == -1)
            return;

        if (this.getSelectedFileList().size() == 0)
            return;

        final FileInfo f = this.getSelectedFileList().get(0);
        this.clearSelection();

        TextInputDialog dialog = new TextInputDialog(this.mContext,
                this.mContext.getString(R.string.operation_rename),
                this.mContext.getString(R.string.operation_rename_message),
                f.fileName, new TextInputDialog.OnFinishListener()
        {
            @Override
            public boolean onFinish(String text) {
                return FileViewInteractionHub.this.doRename(f, text);
            }

        });

        dialog.show();
    }

    private boolean doRename(FileInfo f, String text) {
        if (TextUtils.isEmpty(text))
            return false;

        if (this.mFileOperationHelper.Rename(f, text)) {
            f.fileName = text;
            this.mFileViewListener.onDataChanged();
        } else {
            new Builder(this.mContext)
                    .setMessage(this.mContext.getString(R.string.fail_to_rename))
                    .setPositiveButton(R.string.confirm, null).create().show();
            return false;
        }

        return true;
    }

    private void notifyFileSystemChanged(String path) {
        if (path == null)
            return;
        File f = new File(path);
        Intent intent;
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(new File(path)));
            Log.v(FileViewInteractionHub.LOG_TAG, "file changed, send broadcast:" + intent);

        } else {
            intent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
            intent.setClassName("com.android.providers.media",
                    "com.android.providers.media.MediaScannerReceiver");
            intent.setData(Uri.fromFile(Environment
                    .getExternalStorageDirectory()));
            Log.v(FileViewInteractionHub.LOG_TAG,
                    "directory changed, send broadcast:" + intent);
        }
        this.mContext.sendBroadcast(intent);
    }

    public void onOperationDelete() {
        this.doOperationDelete(this.getSelectedFileList());
    }

    public void onOperationDelete(int position) {
        FileInfo file = this.mFileViewListener.getItem(position);
        if (file == null)
            return;

        ArrayList<FileInfo> selectedFileList = new ArrayList<FileInfo>();
        selectedFileList.add(file);
        this.doOperationDelete(selectedFileList);
    }

    private void doOperationDelete(ArrayList<FileInfo> selectedFileList) {
        final ArrayList<FileInfo> selectedFiles = new ArrayList<FileInfo>(
                selectedFileList);
        Dialog dialog = new Builder(this.mContext)
                .setMessage(
                        this.mContext.getString(R.string.operation_delete_confirm_message))
                .setPositiveButton(R.string.confirm,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                if (FileViewInteractionHub.this.mFileOperationHelper.Delete(selectedFiles)) {
                                    FileViewInteractionHub.this.showProgress(FileViewInteractionHub.this.mContext
                                            .getString(R.string.operation_deleting));
                                }
                                FileViewInteractionHub.this.clearSelection();
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                FileViewInteractionHub.this.clearSelection();
                            }
                        }).create();
        dialog.show();
    }

    public void onOperationInfo() {
        if (this.getSelectedFileList().size() == 0)
            return;

        FileInfo file = this.getSelectedFileList().get(0);
        if (file == null)
            return;

        InformationDialog dialog = new InformationDialog(this.mContext, file,
                this.mFileViewListener.getFileIconHelper());
        dialog.show();
        this.clearSelection();
    }

    public void onOperationButtonConfirm() {
        if (this.isSelectingFiles()) {
            this.mSelectFilesCallback.selected(this.mCheckedFileNameList);
            this.mSelectFilesCallback = null;
            this.clearSelection();
        } else if (this.mFileOperationHelper.isMoveState()) {
            if (this.mFileOperationHelper.EndMove(this.mCurrentPath)) {
                this.showProgress(this.mContext.getString(R.string.operation_moving));
            }
        } else {
            //clearSelection();
            this.onOperationPaste();
        }
    }
    public void diskOperationButtonConfirm() {
        if (this.isSelectingFiles()) {
            this.mSelectFilesCallback.selected(this.mCheckedFileNameList);
            this.mSelectFilesCallback = null;
            this.clearSelection();
        } else if (this.mFileOperationHelper.isMoveState()) {
            if (this.mFileOperationHelper.EndMove(this.mCurrentPath)) {
                this.showProgress(this.mContext.getString(R.string.operation_moving));
            }
        } else {
            //clearSelection();
            diskOperationPaste();
        }
    }

    public void onOperationButtonCancel() {
        this.mFileOperationHelper.clear();
        //showConfirmOperationBar(false);
        if (this.isSelectingFiles()) {
            this.mSelectFilesCallback.selected(null);
            this.mSelectFilesCallback = null;
            this.clearSelection();
        } else if (this.mFileOperationHelper.isMoveState()) {
            // refresh to show previously selected hidden files
            this.mFileOperationHelper.EndMove(null);
            this.refreshFileList();
        } else {
            this.refreshFileList();
        }
    }

    // context menu
    private final View.OnCreateContextMenuListener mListViewContextMenuListener = new View.OnCreateContextMenuListener()
    {
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            if (FileViewInteractionHub.this.isInSelection() || FileViewInteractionHub.this.isMoveState())
                return;

            FileViewInteractionHub.this.showDropdownNavigation(false);

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

            FavoriteDatabaseHelper databaseHelper = FavoriteDatabaseHelper
                    .getInstance();
            FileInfo file = FileViewInteractionHub.this.mFileViewListener.getItem(info.position);
            if (databaseHelper != null && file != null) {
                int stringId = databaseHelper.isFavorite(file.filePath) ? R.string.operation_unfavorite
                        : R.string.operation_favorite;
                FileViewInteractionHub.this.addMenuItem(menu, GlobalConsts.MENU_FAVORITE, 0, stringId);
            }

            FileViewInteractionHub.this.addMenuItem(menu, GlobalConsts.MENU_COPY, 0,
                    R.string.operation_copy);
            FileViewInteractionHub.this.addMenuItem(menu, GlobalConsts.MENU_COPY_PATH, 0,
                    R.string.operation_copy_path);
            // addMenuItem(menu, GlobalConsts.MENU_PASTE, 0,
            // R.string.operation_paste);
            FileViewInteractionHub.this.addMenuItem(menu, GlobalConsts.MENU_MOVE, 0,
                    R.string.operation_move);
            FileViewInteractionHub.this.addMenuItem(menu, FileViewInteractionHub.MENU_SEND, 0, R.string.operation_send);
            FileViewInteractionHub.this.addMenuItem(menu, FileViewInteractionHub.MENU_RENAME, 0, R.string.operation_rename);
            FileViewInteractionHub.this.addMenuItem(menu, FileViewInteractionHub.MENU_DELETE, 0, R.string.operation_delete);
            FileViewInteractionHub.this.addMenuItem(menu, FileViewInteractionHub.MENU_INFO, 0, R.string.operation_info);

            if (!FileViewInteractionHub.this.canPaste()) {
                MenuItem menuItem = menu.findItem(GlobalConsts.MENU_PASTE);
                if (menuItem != null)
                    menuItem.setEnabled(false);
            }
        }
    };

    // File List view setup
    private ListView mFileListView;

    private int mListViewContextMenuSelectedItem;

    private void setupFileListView() {
        this.mFileListView = (ListView) this.mFileViewListener
                .getViewById(R.id.file_path_list);
        this.mFileListView.setLongClickable(true);
        this.mFileListView
                .setOnCreateContextMenuListener(this.mListViewContextMenuListener);
        this.mFileListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                FileViewInteractionHub.this.onListItemClick(parent, view, position, id);
            }
        });
    }

    // menu
    private static final int MENU_SEARCH = 1;

    // private static final int MENU_NEW_FOLDER = 2;
    private static final int MENU_SORT = 3;

    private static final int MENU_SEND = 7;

    private static final int MENU_RENAME = 8;

    private static final int MENU_DELETE = 9;

    private static final int MENU_INFO = 10;

    private static final int MENU_SORT_NAME = 11;

    private static final int MENU_SORT_SIZE = 12;

    private static final int MENU_SORT_DATE = 13;

    private static final int MENU_SORT_TYPE = 14;

    private static final int MENU_REFRESH = 15;

    private static final int MENU_SELECTALL = 16;

    private static final int MENU_SETTING = 17;

    private static final int MENU_EXIT = 18;

    private final MenuItem.OnMenuItemClickListener menuItemClick = new MenuItem.OnMenuItemClickListener()
    {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                    .getMenuInfo();
            FileViewInteractionHub.this.mListViewContextMenuSelectedItem = info != null ? info.position
                    : -1;

            int itemId = item.getItemId();
            if (FileViewInteractionHub.this.mFileViewListener.onOperation(itemId)) {
                return true;
            }

            FileViewInteractionHub.this.addContextMenuSelectedItem();

            switch (itemId) {
                case FileViewInteractionHub.MENU_SEARCH:
                    FileViewInteractionHub.this.onOperationSearch();
                    break;
                case GlobalConsts.MENU_NEW_FOLDER:
                    FileViewInteractionHub.this.onOperationCreateFolder();
                    break;
                case FileViewInteractionHub.MENU_REFRESH:
                    FileViewInteractionHub.this.onOperationReferesh();
                    break;
                case FileViewInteractionHub.MENU_SELECTALL:
                    FileViewInteractionHub.this.onOperationSelectAllOrCancel();
                    break;
                case GlobalConsts.MENU_SHOWHIDE:
                    FileViewInteractionHub.this.onOperationShowSysFiles();
                    break;
                case GlobalConsts.MENU_FAVORITE:
                    FileViewInteractionHub.this.onOperationFavorite();
                    break;
                case FileViewInteractionHub.MENU_SETTING:
                    FileViewInteractionHub.this.onOperationSetting();
                    break;
                case FileViewInteractionHub.MENU_EXIT:
                    ((FileExplorerTabActivity) FileViewInteractionHub.this.mContext).finish();
                    break;
                // sort
                case FileViewInteractionHub.MENU_SORT_NAME:
                    item.setChecked(true);
                    FileViewInteractionHub.this.onSortChanged(FileSortHelper.SortMethod.name);
                    break;
                case FileViewInteractionHub.MENU_SORT_SIZE:
                    item.setChecked(true);
                    FileViewInteractionHub.this.onSortChanged(FileSortHelper.SortMethod.size);
                    break;
                case FileViewInteractionHub.MENU_SORT_DATE:
                    item.setChecked(true);
                    FileViewInteractionHub.this.onSortChanged(FileSortHelper.SortMethod.date);
                    break;
                case FileViewInteractionHub.MENU_SORT_TYPE:
                    item.setChecked(true);
                    FileViewInteractionHub.this.onSortChanged(FileSortHelper.SortMethod.type);
                    break;

                case GlobalConsts.MENU_COPY:
                    ((FtsManagerActivity) ((FileExplorerTabActivity) FileViewInteractionHub.this.mContext)
                            .getFragment(Util.CATEGORY_TAB_INDEX))
                            .copyFile(FileViewInteractionHub.this.mCheckedFileNameList, 0);
                    FileViewInteractionHub.this.onOperationCopy();
                    //System.out.println("在这里执行1");

                    break;
                case GlobalConsts.MENU_COPY_PATH:
                    FileViewInteractionHub.this.onOperationCopyPath();
                    break;
                case GlobalConsts.MENU_PASTE:
                    FileViewInteractionHub.this.onOperationPaste();
                    break;
                case GlobalConsts.MENU_MOVE:
                    ((FtsManagerActivity) ((FileExplorerTabActivity) FileViewInteractionHub.this.mContext)
                            .getFragment(Util.CATEGORY_TAB_INDEX))
                            .copyFile(FileViewInteractionHub.this.mCheckedFileNameList, 2);
                    FileViewInteractionHub.this.onOperationMove();
                    break;
                case FileViewInteractionHub.MENU_SEND:
                    FileViewInteractionHub.this.onOperationSend();
                    break;
                case FileViewInteractionHub.MENU_RENAME:
                    FileViewInteractionHub.this.onOperationRename();
                    break;
                case FileViewInteractionHub.MENU_DELETE:
                    FileViewInteractionHub.this.onOperationDelete();
                    break;
                case FileViewInteractionHub.MENU_INFO:
                    FileViewInteractionHub.this.onOperationInfo();
                    break;
                default:
                    return false;
            }

            FileViewInteractionHub.this.mListViewContextMenuSelectedItem = -1;
            return true;
        }

    };

    private Mode mCurrentMode;

    private String mCurrentPath;

    private String mRoot;

    private FileViewActivity.SelectFilesCallback mSelectFilesCallback;

    public boolean onCreateOptionsMenu(Menu menu) {
        this.clearSelection();
        this.showDropdownNavigation(false);

//        menu.add(0, MENU_SEARCH, 0,
//                R.string.menu_item_search).setIcon(R.drawable.ic_menu_search).setOnMenuItemClickListener(
//                menuItemClick);
        menu.add(0, FileViewInteractionHub.MENU_REFRESH, 5, R.string.operation_refresh).setIcon(R.drawable.button_refresh)
                .setOnMenuItemClickListener(
                        this.menuItemClick).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem
                .SHOW_AS_ACTION_WITH_TEXT);
        menu.add(0, GlobalConsts.MENU_SHOWHIDE, 4, R.string.operation_show_sys).setIcon(R.drawable.button_filter)
                .setOnMenuItemClickListener(
                        this.menuItemClick).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem
                .SHOW_AS_ACTION_WITH_TEXT);
        menu.add(0, GlobalConsts.MENU_NEW_FOLDER, 2, R.string.operation_create_folder).setIcon(R.drawable
                .button_add)
                .setOnMenuItemClickListener(
                        this.menuItemClick).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem
                .SHOW_AS_ACTION_WITH_TEXT);
        menu.add(0, FileViewInteractionHub.MENU_SETTING, 6, R.string.menu_setting).setIcon(R.drawable
                .button_sort)
                .setOnMenuItemClickListener(
                        this.menuItemClick).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem
                .SHOW_AS_ACTION_WITH_TEXT);
//        addMenuItem(menu, GlobalConsts.MENU_FAVORITE, 3,
//                R.string.operation_favorite, R.drawable.ic_menu_delete_favorite);
//        addMenuItem(menu, MENU_SELECTALL, 0, R.string.operation_selectall,
//                R.drawable.ic_menu_select_all);

        SubMenu sortMenu = menu.addSubMenu(0, FileViewInteractionHub.MENU_SORT, 1,
                R.string.menu_item_sort).setIcon(R.drawable.button_sort);
        this.addMenuItem(sortMenu, FileViewInteractionHub.MENU_SORT_NAME, 0, R.string.menu_item_sort_name);
        this.addMenuItem(sortMenu, FileViewInteractionHub.MENU_SORT_SIZE, 1, R.string.menu_item_sort_size);
        this.addMenuItem(sortMenu, FileViewInteractionHub.MENU_SORT_DATE, 2, R.string.menu_item_sort_date);
        this.addMenuItem(sortMenu, FileViewInteractionHub.MENU_SORT_TYPE, 3, R.string.menu_item_sort_type);
        sortMenu.setGroupCheckable(0, true, true);
        sortMenu.getItem(0).setChecked(true);
        //setIconEnable(menu, true);   //调用这句实现显示ICON
        // addMenuItem(menu, GlobalConsts.MENU_PASTE, 2,
        // R.string.operation_paste);
        this.addMenuItem(menu, FileViewInteractionHub.MENU_EXIT, 7, R.string.menu_exit,
                R.drawable.button_cancel);
        return true;
        //return super.onCreateOptionsMenu(menu);
    }

    //enable为true时，菜单添加图标有效，enable为false时无效。4.0系统默认无效
    private void setIconEnable(Menu menu, boolean enable) {
        try {
            Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");
            Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            m.setAccessible(true);

            //MenuBuilder实现Menu接口，创建菜单时，传进来的menu其实就是MenuBuilder对象(java的多态特征)
            m.invoke(menu, enable);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addMenuItem(Menu menu, int itemId, int order, int string) {
        this.addMenuItem(menu, itemId, order, string, -1);
    }

    private void addMenuItem(Menu menu, int itemId, int order, int string,
                             int iconRes) {
        if (!this.mFileViewListener.shouldHideMenu(itemId)) {
            MenuItem item = menu.add(0, itemId, order, string)
                    .setOnMenuItemClickListener(this.menuItemClick);
            if (iconRes > 0) {
                item.setIcon(iconRes);
            }
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        this.updateMenuItems(menu);
        return true;
    }

    private void updateMenuItems(Menu menu) {
        MenuItem menuItem = menu.findItem(FileViewInteractionHub.MENU_SELECTALL);
        if (menuItem != null) {
            menu.findItem(FileViewInteractionHub.MENU_SELECTALL).setTitle(
                    this.isSelectedAll() ? R.string.operation_cancel_selectall
                            : R.string.operation_selectall);
            menu.findItem(FileViewInteractionHub.MENU_SELECTALL).setEnabled(this.mCurrentMode != FileViewInteractionHub.Mode.Pick);
        }
        menuItem = menu.findItem(GlobalConsts.MENU_SHOWHIDE);
        if (menuItem != null) {
            menuItem.setTitle(Settings.instance().getShowDotAndHiddenFiles() ? R.string.operation_hide_sys
                    : R.string.operation_show_sys);
        }

        FavoriteDatabaseHelper databaseHelper = FavoriteDatabaseHelper
                .getInstance();
        if (databaseHelper != null) {
            MenuItem item = menu.findItem(GlobalConsts.MENU_FAVORITE);
            if (item != null) {
                item.setTitle(databaseHelper.isFavorite(this.mCurrentPath) ? R.string.operation_unfavorite
                        : R.string.operation_favorite);
            }
        }

    }

    public boolean isFileSelected(String filePath) {
        return this.mFileOperationHelper.isFileSelected(filePath);
    }

    public void setMode(FileViewInteractionHub.Mode m) {
        this.mCurrentMode = m;
    }

    public FileViewInteractionHub.Mode getMode() {
        return this.mCurrentMode;
    }

    public void onListItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
        FileInfo lFileInfo = this.mFileViewListener.getItem(position);
        this.showDropdownNavigation(false);

        if (lFileInfo == null) {
            Log.e(FileViewInteractionHub.LOG_TAG, "file does not exist on position:" + position);
            return;
        }

        if (this.isInSelection()) {
            boolean selected = lFileInfo.Selected;
            ActionMode actionMode = ((FileExplorerTabActivity) this.mContext)
                    .getActionMode();
            ImageView checkBox = (ImageView) view
                    .findViewById(R.id.file_checkbox);
            if (selected) {
                this.mCheckedFileNameList.remove(lFileInfo);
                checkBox.setImageResource(R.drawable.btn_check_off_holo_light);
            } else {
                this.mCheckedFileNameList.add(lFileInfo);
                checkBox.setImageResource(R.drawable.btn_check_on_holo_light);
            }
            if (actionMode != null) {
                if (this.mCheckedFileNameList.size() == 0)
                    actionMode.finish();
                else
                    actionMode.invalidate();
            }
            lFileInfo.Selected = !selected;

            Util.updateActionModeTitle(actionMode, this.mContext,
                    this.mCheckedFileNameList.size());
            return;
        }

        if (!lFileInfo.IsDir) {
            if (this.mCurrentMode == FileViewInteractionHub.Mode.Pick) {
                this.mFileViewListener.onPick(lFileInfo);
            } else {
                this.viewFile(lFileInfo);
            }
            return;
        }

        this.mCurrentPath = this.getAbsoluteName(this.mCurrentPath, lFileInfo.fileName);
        ActionMode actionMode = ((FileExplorerTabActivity) this.mContext)
                .getActionMode();
        if (actionMode != null) {
            actionMode.finish();
        }
        this.refreshFileList();
    }

    public void setRootPath(String path) {
        this.mRoot = path;
        this.mCurrentPath = path;
    }

    public String getRootPath() {
        return this.mRoot;
    }

    public String getCurrentPath() {
        return this.mCurrentPath;
    }

    public void setCurrentPath(String path) {
        this.mCurrentPath = path;
    }

    private String getAbsoluteName(String path, String name) {
        return path.equals(GlobalConsts.ROOT_PATH) ? path + name : path
                + File.separator + name;
    }

    // check or uncheck
    public boolean onCheckItem(FileInfo f, View v) {
        if (this.isMoveState())
            return false;

        if (this.isSelectingFiles() && f.IsDir)
            return false;

        if (f.Selected) {
            this.mCheckedFileNameList.add(f);
        } else {
            this.mCheckedFileNameList.remove(f);
        }
        return true;
    }

    private boolean isSelectingFiles() {
        return this.mSelectFilesCallback != null;
    }

    public boolean isSelectedAll() {
        return this.mFileViewListener.getItemCount() != 0
                && this.mCheckedFileNameList.size() == this.mFileViewListener
                .getItemCount();
    }

    public boolean isSelected() {
        return this.mCheckedFileNameList.size() != 0;
    }

    public void clearSelection() {
        if (this.mCheckedFileNameList.size() > 0) {
            for (FileInfo f : this.mCheckedFileNameList) {
                if (f == null) {
                    continue;
                }
                f.Selected = false;
            }
            this.mCheckedFileNameList.clear();
            this.mFileViewListener.onDataChanged();
        }
    }

    private void viewFile(FileInfo lFileInfo) {
        try {
            IntentBuilder.viewFile(this.mContext, lFileInfo.filePath);
        } catch (ActivityNotFoundException e) {
            Log.e(FileViewInteractionHub.LOG_TAG, "fail to view file: " + e);
        }
    }

    public boolean onBackPressed() {
        if (this.mDropdownNavigation.getVisibility() == View.VISIBLE) {
            this.mDropdownNavigation.setVisibility(View.GONE);
        } else if (this.isInSelection()) {
            this.clearSelection();
        } else if (!this.onOperationUpLevel()) {
            return false;
        }
        return true;
    }

    public void copyFile(ArrayList<FileInfo> files) {
        this.mFileOperationHelper.Copy(files);
    }

    public void moveFileFrom(ArrayList<FileInfo> files) {
        this.mFileOperationHelper.StartMove(files);
        this.showConfirmOperationBar(true);
        this.updateConfirmButtons();
        // refresh to hide selected files
        this.refreshFileList();
    }

    private void showDropdownNavigation(boolean show) {
        this.mDropdownNavigation.setVisibility(show ? View.VISIBLE : View.GONE);
        this.mNavigationBarUpDownArrow.setImageResource(this.mDropdownNavigation
                .getVisibility() == View.VISIBLE ? R.drawable.arrow_up
                : R.drawable.arrow_down);
    }

    @Override
    public void onFileChanged(String path) {
        this.notifyFileSystemChanged(path);
    }

    public void startSelectFiles(FileViewActivity.SelectFilesCallback callback) {
        this.mSelectFilesCallback = callback;
        this.showConfirmOperationBar(true);
        this.updateConfirmButtons();
    }

}
