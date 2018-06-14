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
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.activity.DiskManagerActivity;
import com.tbs.tbsmis.activity.FtsManagerActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.file.FileSortHelper.SortMethod;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;


@SuppressLint("NewApi")
public class FileViewActivity extends Fragment implements
        IFileInteractionListener, FileExplorerTabActivity.IBackPressedListener
{

    public static final String EXT_FILTER_KEY = "ext_filter";

    private static final String LOG_TAG = "FileViewActivity";

    public static final String EXT_FILE_FIRST_KEY = "ext_file_first";

    public static final String ROOT_DIRECTORY = "root_directory";

    public static final String PICK_FOLDER = "pick_folder";

    private ListView mFileListView;

    // private TextView mCurrentPathTextView;
    private ArrayAdapter<FileInfo> mAdapter;

    private FileViewInteractionHub mFileViewInteractionHub;

    private FileCategoryHelper mFileCagetoryHelper;

    private FileIconHelper mFileIconHelper;

    private final ArrayList<FileInfo> mFileNameList = new ArrayList<FileInfo>();

    private Activity mActivity;

    private View mRootView;

    private static final String sdDir = Util.getSdDirectory();

    // memorize the scroll positions of previous paths
    private final ArrayList<FileViewActivity.PathScrollPositionItem> mScrollPositionList = new
            ArrayList<FileViewActivity.PathScrollPositionItem>();
    private String mPreviousPath;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            Log.v(FileViewActivity.LOG_TAG, "received broadcast:" + intent);
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED) || action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                FileViewActivity.this.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run() {
                        FileViewActivity.this.updateUI();
                    }
                });
            }
        }
    };

    private boolean mBackspaceExit;

    private ImageView file_order;

    private ImageView up_file_back;
    private static String type = "upload";

    @SuppressWarnings("static-access")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mActivity = this.getActivity();
        // getWindow().setFormat(android.graphics.PixelFormat.RGBA_8888);
        this.mRootView = inflater.inflate(R.layout.file_explorer_list, container, false);
        ActivitiesManager.getInstance().registerActivity(ActivitiesManager.ACTIVITY_FILE_VIEW, this.mActivity);

        this.mFileCagetoryHelper = new FileCategoryHelper(this.mActivity);
        this.mFileViewInteractionHub = new FileViewInteractionHub(this);
        Intent intent = this.mActivity.getIntent();
        String action = intent.getAction();
        if (!TextUtils.isEmpty(action)
                && (action.equals(Intent.ACTION_PICK) || action.equals(Intent.ACTION_GET_CONTENT))) {
            this.mFileViewInteractionHub.setMode(FileViewInteractionHub.Mode.Pick);

            boolean pickFolder = intent.getBooleanExtra(FileViewActivity.PICK_FOLDER, false);
            if (!pickFolder) {
                String[] exts = intent.getStringArrayExtra(FileViewActivity.EXT_FILTER_KEY);
                if (exts != null) {
                    this.mFileCagetoryHelper.setCustomCategory(exts);
                }
            } else {
                this.mFileCagetoryHelper.setCustomCategory(new String[]{} /*folder only*/);
                this.mRootView.findViewById(R.id.pick_operation_bar).setVisibility(View.VISIBLE);
                this.mRootView.findViewById(R.id.button_pick_confirm).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = Intent.parseUri(FileViewActivity.this.mFileViewInteractionHub
                                    .getCurrentPath(), 0);
                            FileViewActivity.this.mActivity.setResult(Activity.RESULT_OK, intent);
                            FileViewActivity.this.mActivity.finish();
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                });

                this.mRootView.findViewById(R.id.button_pick_cancel).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        FileViewActivity.this.mActivity.finish();
                    }
                });
            }
        } else {
            this.mFileViewInteractionHub.setMode(FileViewInteractionHub.Mode.View);
        }
        if (intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            type = bundle.getString("type");
            System.out.println("intype ="+type);
        }
        System.out.println("action ="+action);
        //(Activity)
        this.mFileListView = (ListView) this.mRootView.findViewById(R.id.file_path_list);
        this.file_order = (ImageView) this.mRootView.findViewById(R.id.file_order);
        this.up_file_back = (ImageView) this.mRootView.findViewById(R.id.up_file_back);
        this.mFileIconHelper = new FileIconHelper(this.mActivity);
        this.mAdapter = new FileListAdapter(this.mActivity, R.layout.file_browser_item, this.mFileNameList, this
                .mFileViewInteractionHub,
                this.mFileIconHelper);
        this.file_order.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                FileViewActivity.this.mActivity.openOptionsMenu();
            }
        });
        this.up_file_back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                FileViewActivity.this.mActivity.finish();
            }
        });
        boolean baseSd = intent.getBooleanExtra(GlobalConsts.KEY_BASE_SD, !FileExplorerPreferenceActivity.isReadRoot
                (this.mActivity));
        Log.i(FileViewActivity.LOG_TAG, "baseSd = " + baseSd);

        String rootDir = intent.getStringExtra(FileViewActivity.ROOT_DIRECTORY);
        if (!TextUtils.isEmpty(rootDir)) {
            if (baseSd && sdDir.startsWith(rootDir)) {
                rootDir = sdDir;
            }
        } else {
            rootDir = baseSd ? sdDir : GlobalConsts.ROOT_PATH;
        }
        this.mFileViewInteractionHub.setRootPath(rootDir);

        String currentDir = FileExplorerPreferenceActivity.getPrimaryFolder(this.mActivity);
        Uri uri = intent.getData();
        if (uri != null) {
            if (baseSd && sdDir.startsWith(uri.getPath())) {
                currentDir = sdDir;
            } else {
                currentDir = uri.getPath();
            }
        }
        String webRoot = UIHelper.getShareperference(this.mActivity, constants.SAVE_INFORMATION,
                "Path", "");
        if (!StringUtils.isEmpty(webRoot)) {
            currentDir = webRoot;
        }
        this.mFileViewInteractionHub.setCurrentPath(currentDir);
        Log.i(FileViewActivity.LOG_TAG, "CurrentDir = " + currentDir);

        this.mBackspaceExit = uri != null
                && (TextUtils.isEmpty(action)
                || !action.equals(Intent.ACTION_PICK) && !action.equals(Intent.ACTION_GET_CONTENT));

        this.mFileListView.setAdapter(this.mAdapter);
        this.mFileViewInteractionHub.refreshFileList();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addAction("Action_FileView"
                + getString(R.string.about_title));
        intentFilter.addDataScheme("file");
        this.mActivity.registerReceiver(this.mReceiver, intentFilter);

        this.updateUI();
        //setHasOptionsMenu(true);
        return this.mRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.mActivity.unregisterReceiver(this.mReceiver);
    }

//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//        mFileViewInteractionHub.onPrepareOptionsMenu(menu);
//        super.onPrepareOptionsMenu(menu);
//    }
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        //inflater.inflate(R.menu.operation_top_menu,menu);
//        //setIconEnable(menu, true);   //调用这句实现显示ICON
//        mFileViewInteractionHub.onCreateOptionsMenu(menu);
//    }

    @Override
    public boolean onBack() {
        if (this.mBackspaceExit || !Util.isSDCardReady() || this.mFileViewInteractionHub == null) {
            return false;
        }
        return this.mFileViewInteractionHub.onBackPressed();
    }

    private class PathScrollPositionItem
    {
        String path;
        int pos;

        PathScrollPositionItem(String s, int p) {
            this.path = s;
            this.pos = p;
        }
    }

    // execute before change, return the memorized scroll position
    private int computeScrollPosition(String path) {
        int pos = 0;
        if (this.mPreviousPath != null) {
            if (path.startsWith(this.mPreviousPath)) {
                int firstVisiblePosition = this.mFileListView.getFirstVisiblePosition();
                if (this.mScrollPositionList.size() != 0
                        && this.mPreviousPath.equals(this.mScrollPositionList.get(this.mScrollPositionList.size() -
                        1).path)) {
                    this.mScrollPositionList.get(this.mScrollPositionList.size() - 1).pos = firstVisiblePosition;
                    Log.i(FileViewActivity.LOG_TAG, "computeScrollPosition: update item: " + this.mPreviousPath + " "
                            + firstVisiblePosition
                            + " stack count:" + this.mScrollPositionList.size());
                    pos = firstVisiblePosition;
                } else {
                    this.mScrollPositionList.add(new FileViewActivity.PathScrollPositionItem(this.mPreviousPath,
                            firstVisiblePosition));
                    Log.i(FileViewActivity.LOG_TAG, "computeScrollPosition: add item: " + this.mPreviousPath + " " +
                            firstVisiblePosition
                            + " stack count:" + this.mScrollPositionList.size());
                }
            } else {
                int i;
                @SuppressWarnings("unused")
                boolean isLast = false;
                for (i = 0; i < this.mScrollPositionList.size(); i++) {
                    if (!path.startsWith(this.mScrollPositionList.get(i).path)) {
                        break;
                    }
                }
                // navigate to a totally new branch, not in current stack
                if (i > 0) {
                    pos = this.mScrollPositionList.get(i - 1).pos;
                }

                for (int j = this.mScrollPositionList.size() - 1; j >= i - 1 && j >= 0; j--) {
                    this.mScrollPositionList.remove(j);
                }
            }
        }

        Log.i(FileViewActivity.LOG_TAG, "computeScrollPosition: result pos: " + path + " " + pos + " stack count:" +
                this.mScrollPositionList.size());
        this.mPreviousPath = path;
        return pos;
    }

    @Override
    public boolean onRefreshFileList(String path, FileSortHelper sort) {
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            return false;
        }
        final int pos = this.computeScrollPosition(path);
        ArrayList<FileInfo> fileList = this.mFileNameList;
        fileList.clear();

        File[] listFiles = file.listFiles(this.mFileCagetoryHelper.getFilter());
        if (listFiles == null)
            return true;

        for (File child : listFiles) {
            // do not show selected file if in move state
            if (this.mFileViewInteractionHub.isMoveState() && this.mFileViewInteractionHub.isFileSelected(child
                    .getPath()))
                continue;

            String absolutePath = child.getAbsolutePath();
            if (Util.isNormalFile(absolutePath) && Util.shouldShowFile(absolutePath)) {
                FileInfo lFileInfo = Util.GetFileInfo(child,
                        this.mFileCagetoryHelper.getFilter(), Settings.instance().getShowDotAndHiddenFiles());
                if (lFileInfo != null) {
                    fileList.add(lFileInfo);
                }
            }
        }

        this.sortCurrentList(sort);
        this.showEmptyView(fileList.size() == 0);
        this.mFileListView.post(new Runnable()
        {
            @Override
            public void run() {
                FileViewActivity.this.mFileListView.setSelection(pos);
            }
        });
        return true;
    }

    private void updateUI() {
        boolean sdCardReady = Util.isSDCardReady();
        View noSdView = this.mRootView.findViewById(R.id.sd_not_available_page);
        noSdView.setVisibility(sdCardReady ? View.GONE : View.VISIBLE);

        View navigationBar = this.mRootView.findViewById(R.id.navigation_bar);
        navigationBar.setVisibility(sdCardReady ? View.VISIBLE : View.GONE);
        this.mFileListView.setVisibility(sdCardReady ? View.VISIBLE : View.GONE);

        if (sdCardReady) {
            this.mFileViewInteractionHub.refreshFileList();
        }
    }

    private void showEmptyView(boolean show) {
        View emptyView = this.mRootView.findViewById(R.id.empty_view);
        if (emptyView != null)
            emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public View getViewById(int id) {
        return this.mRootView.findViewById(id);
    }

    @Override
    public Context getContext() {
        return this.mActivity;
    }

    @Override
    public void onDataChanged() {
        this.runOnUiThread(new Runnable()
        {

            @Override
            public void run() {
                FileViewActivity.this.mAdapter.notifyDataSetChanged();
            }

        });
    }

    @Override
    public void onPick(FileInfo f) {
        try {
            //System.out.println(f.filePath);
            Intent intent = Intent.parseUri(Uri.fromFile(new File(f.filePath)).toString(), 0);
            this.mActivity.setResult(Activity.RESULT_OK, intent);
            this.mActivity.finish();
            return;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean shouldShowOperationPane() {
        return true;
    }

    @Override
    public boolean onOperation(int id) {
        return false;
    }

    //支持显示真实路径
    @SuppressWarnings("static-access")
    @Override
    public String getDisplayPath(String path) {
        if (path.startsWith(sdDir) && !FileExplorerPreferenceActivity.showRealPath(this.mActivity)) {
            return this.getString(R.string.sd_folder) + path.substring(sdDir.length());
        } else {
            return path;
        }
    }

    @SuppressWarnings("static-access")
    @Override
    public String getRealPath(String displayPath) {
        String perfixName = this.getString(R.string.sd_folder);
        if (displayPath.startsWith(perfixName)) {
            return sdDir + displayPath.substring(perfixName.length());
        } else {
            return displayPath;
        }
    }

    @Override
    public boolean onNavigation(String path) {
        return false;
    }

    @Override
    public boolean shouldHideMenu(int menu) {
        return false;
    }

    public void copyFile(ArrayList<FileInfo> files) {
        this.mFileViewInteractionHub.onOperationCopy(files);
    }

    public void copy() {
        ((FileExplorerTabActivity) this.mActivity).setIsAction(true);
        ((FtsManagerActivity) ((FileExplorerTabActivity) this.mActivity)
                .getFragment(Util.CATEGORY_TAB_INDEX))
                .copyFile(this.mFileViewInteractionHub.getSelectedFileList(), 0);
        this.mFileViewInteractionHub.onOperationCopy();
        ActionMode mode = ((FileExplorerTabActivity) this.mActivity)
                .getActionMode();
        if (mode != null) {
            mode.finish();
        }
        ((FileExplorerTabActivity) this.mActivity).setIsAction(false);
    }
    public void Diskcopy() {
        ((FileExplorerTabActivity) this.mActivity).setIsAction(true);
        ((DiskManagerActivity) ((FileExplorerTabActivity) this.mActivity)
                .getFragment(Util.CATEGORY_TAB_INDEX))
                .copyFile(this.mFileViewInteractionHub.getSelectedFileList(), 0);
        this.mFileViewInteractionHub.onOperationCopy();
        ActionMode mode = ((FileExplorerTabActivity) this.mActivity)
                .getActionMode();
        if (mode != null) {
            mode.finish();
        }
        ((FileExplorerTabActivity) this.mActivity).setIsAction(false);
    }
    public void moveFile() {
        ((FileExplorerTabActivity) this.mActivity).setIsAction(true);
        ((DiskManagerActivity) ((FileExplorerTabActivity) this.mActivity)
                .getFragment(Util.CATEGORY_TAB_INDEX))
                .copyFile(this.mFileViewInteractionHub.getSelectedFileList(), 2);
        this.mFileViewInteractionHub.onOperationMove();
        ActionMode mode = ((FileExplorerTabActivity) this.mActivity)
                .getActionMode();
        if (mode != null) {
            mode.finish();
        }
        ((FileExplorerTabActivity) this.mActivity).setIsAction(false);
    }
    public void moveDiskFile() {
        ((FileExplorerTabActivity) this.mActivity).setIsAction(true);
        ((DiskManagerActivity) ((FileExplorerTabActivity) this.mActivity)
                .getFragment(Util.CATEGORY_TAB_INDEX))
                .copyFile(this.mFileViewInteractionHub.getSelectedFileList(), 2);
        this.mFileViewInteractionHub.onOperationMove();
        ActionMode mode = ((FileExplorerTabActivity) this.mActivity)
                .getActionMode();
        if (mode != null) {
            mode.finish();
        }
        ((FileExplorerTabActivity) this.mActivity).setIsAction(false);
    }
    public void Rename() {
        this.mFileViewInteractionHub.onOperationRename();
        ActionMode mode = ((FileExplorerTabActivity) this.mActivity)
                .getActionMode();
        if (mode != null) {
            mode.finish();
        }
    }

    public void Delete() {

        this.mFileViewInteractionHub.onOperationDelete();
        ActionMode mode = ((FileExplorerTabActivity) this.mActivity)
                .getActionMode();
        if (mode != null) {
            mode.finish();
        }
    }

    public void Send() {
        this.mFileViewInteractionHub.onOperationSend();
        ActionMode mode = ((FileExplorerTabActivity) this.mActivity)
                .getActionMode();
        if (mode != null) {
            mode.finish();
        }
    }

    public void Info() {
        this.mFileViewInteractionHub.onOperationInfo();
        ActionMode mode = ((FileExplorerTabActivity) this.mActivity)
                .getActionMode();
        if (mode != null) {
            mode.finish();
        }
    }

    public void SelectAll() {
        //mFileViewInteractionHub.onOperationSelectAll();
    }

    public void Cancle() {
        this.mFileViewInteractionHub.onOperationButtonCancel();
        ActionMode mode = ((FileExplorerTabActivity) this.mActivity)
                .getActionMode();
        if (mode != null) {
            mode.finish();
        }
    }

    public void add() {
        this.mFileViewInteractionHub.onOperationCreateFolder();
    }

    public void Referesh() {
        this.mFileViewInteractionHub.refreshFileList();
    }

    public void Paste() {
        new Builder(this.mActivity).setTitle("下载").setMessage("是否确认粘贴？").setCancelable(false)
                .setNeutralButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            FileViewActivity.this.mFileViewInteractionHub.onOperationButtonConfirm();
                        dialog.dismiss();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
        try {
            ((FileExplorerTabActivity) this.mActivity).setCustomMenu(new JSONObject(constants.firstmenu));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void DiskPaste() {
        new Builder(this.mActivity).setTitle("下载").setMessage("是否确认粘贴？").setCancelable(false)
                .setNeutralButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FileViewActivity.this.mFileViewInteractionHub.diskOperationButtonConfirm();
                        dialog.dismiss();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();

        try {
            ((FileExplorerTabActivity) this.mActivity).setCustomMenu(new JSONObject(constants.firstmenu));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void PasteCancle() {
        this.onOperationButtonCancel();
        try {
            ((FileExplorerTabActivity) this.mActivity).setCustomMenu(new JSONObject(constants.firstmenu));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void Sort() {
        // TODO Auto-generated method stub
        new Builder(this.mActivity)
                .setTitle("选择类型")
                .setSingleChoiceItems(
                        R.array.sort_options,
                        UIHelper.getShareperference(this.mActivity,
                                constants.SAVE_LOCALMSGNUM, "sort", 0),
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                UIHelper.setSharePerference(FileViewActivity.this.mActivity,
                                        constants.SAVE_LOCALMSGNUM, "sort",
                                        which);
                                if (which == 0) {
                                    FileViewActivity.this.mFileViewInteractionHub.onSortChanged(SortMethod.name);
                                } else if (which == 1) {
                                    FileViewActivity.this.mFileViewInteractionHub.onSortChanged(SortMethod.size);
                                } else if (which == 2) {
                                    FileViewActivity.this.mFileViewInteractionHub.onSortChanged(SortMethod.date);
                                } else if (which == 3) {
                                    FileViewActivity.this.mFileViewInteractionHub.onSortChanged(SortMethod.type);
                                }
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", null).show();

    }

    public void onOperationButtonCancel() {
        // mFileViewInteractionHub.clearSelection();
        this.mFileViewInteractionHub.onOperationButtonCancel();
    }

    public void refresh() {
        if (this.mFileViewInteractionHub != null) {
            this.mFileViewInteractionHub.refreshFileList();
        }
    }

    public void moveToFile(ArrayList<FileInfo> files) {
        this.mFileViewInteractionHub.moveFileFrom(files);
    }

    public interface SelectFilesCallback
    {
        // files equals null indicates canceled
        void selected(ArrayList<FileInfo> files);
    }

    public void startSelectFiles(FileViewActivity.SelectFilesCallback callback) {
        this.mFileViewInteractionHub.startSelectFiles(callback);
    }

    @Override
    public FileIconHelper getFileIconHelper() {
        return this.mFileIconHelper;
    }

    public boolean setPath(String location) {
        if (!location.startsWith(this.mFileViewInteractionHub.getRootPath())) {
            return false;
        }
        this.mFileViewInteractionHub.setCurrentPath(location);
        this.mFileViewInteractionHub.refreshFileList();
        return true;
    }

    @Override
    public FileInfo getItem(int pos) {
        if (pos < 0 || pos > this.mFileNameList.size() - 1)
            return null;

        return this.mFileNameList.get(pos);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void sortCurrentList(FileSortHelper sort) {
        Collections.sort(this.mFileNameList, sort.getComparator());
        this.onDataChanged();
    }

    @Override
    public ArrayList<FileInfo> getAllFiles() {
        return this.mFileNameList;
    }

    @Override
    public void addSingleFile(FileInfo file) {
        this.mFileNameList.add(file);
        this.onDataChanged();
    }

    @Override
    public int getItemCount() {
        return this.mFileNameList.size();
    }

    @Override
    public void runOnUiThread(Runnable r) {
        this.mActivity.runOnUiThread(r);
    }
}
