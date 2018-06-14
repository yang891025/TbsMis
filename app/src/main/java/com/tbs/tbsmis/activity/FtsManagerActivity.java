package com.tbs.tbsmis.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.fts.FileInfo;
import com.tbs.fts.FtClient;
import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.check.FtsFileAdapter;
import com.tbs.tbsmis.check.LoginDialogResult;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.file.FileExplorerTabActivity;
import com.tbs.tbsmis.file.FileExplorerTabActivity.IBackPressedListener;
import com.tbs.tbsmis.file.FileViewActivity;
import com.tbs.tbsmis.file.FtMSGNotify;
import com.tbs.tbsmis.file.IntentBuilder;
import com.tbs.tbsmis.file.TextInputDialog;
import com.tbs.tbsmis.file.TextInputDialog.OnFinishListener;
import com.tbs.tbsmis.file.Util;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by TBS on 2015/12/17.
 */
@TargetApi(VERSION_CODES.HONEYCOMB)
public class FtsManagerActivity extends Fragment implements IBackPressedListener
{
    private FileInfo fileInfo;
    private ListView expandableList;
    // private UpChooseListener listener;
    private TextView tvCurPath;
    private List<FileInfo> data = new ArrayList<FileInfo>();
    private final Stack<String> pathStack = new Stack<String>();
    private String rootPath;
    private ProgressDialog Prodialog;
    private ImageView backBtn;
    private ImageView path_up_level;
    private View mDropdownNavigation;
    private ImageView orderBtn;
    private RelativeLayout up_file_title;
    private LinearLayout layoutButton;
    private TextView up_file_txt;
    private FtClient ftsClient;
    private FtsFileAdapter listAdapter;
    private View mRootView;
    private Activity mActivity;
    private String rootPath1;
    private View mConfirmOperationBar;
    private final ArrayList<com.tbs.tbsmis.file.FileInfo> mCurFileNameList = new ArrayList<com.tbs.tbsmis.file
            .FileInfo>();
    private FtMSGNotify ftMSGNotify;
    private AlertDialog ModifyDialog;
    private FtsManagerActivity.mTimerTask myTimerTask;
    private boolean isCover;
    private boolean isopen;
    //private final boolean iscancle;
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
    private int isMove;
    private static String ftscategory = "";

    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // mActivity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.mActivity = this.getActivity();
        this.mRootView = inflater.inflate(R.layout.file_explorer_list, container, false);
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.up_file_path);
        MyActivity.getInstance().addActivity(this.mActivity);
        this.init();
        //setHasOptionsMenu(true);
        return this.mRootView;
    }

    private void init() {
        // TODO Auto-generated method stub
        this.backBtn = (ImageView) this.mRootView.findViewById(R.id.up_file_back);
        this.orderBtn = (ImageView) this.mRootView.findViewById(R.id.file_order);
        this.path_up_level = (ImageView) this.mRootView.findViewById(R.id.path_pane_up_level);
        this.tvCurPath = (TextView) this.mRootView.findViewById(R.id.current_path_view);
        this.up_file_txt = (TextView) this.mRootView.findViewById(R.id.up_file_txt);
        this.expandableList = (ListView) this.mRootView.findViewById(R.id.file_path_list);
        this.up_file_title = (RelativeLayout) this.mRootView.findViewById(R.id.up_file_title);
//        layoutButton = (LinearLayout) mRootView.findViewById(R.id.layoutButton);
//        layoutButton.setVisibility(View.GONE);
//        up_file_title.setVisibility(View.GONE);
//        up_file_txt.setText("远程管理");
        if (mActivity.getIntent().getExtras() != null) {
            Bundle bundle = mActivity.getIntent().getExtras();
            ftscategory = bundle.getString("upload");
        }
        this.initData(0);

        this.setupNaivgationBar();
        // adapter = new ListViewAdapter(context, dataList);
        // expandableList.setAdapter(adapter);
        this.path_up_level.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                ActionMode actionMode = ((FileExplorerTabActivity) FtsManagerActivity.this.mActivity).getActionMode();
                if (actionMode != null) {
                    actionMode.finish();
                }
//                if (mDropdownNavigation.getVisibility() == View.VISIBLE) {
//                    showDropdownNavigation(false);
//                }
                if (FtsManagerActivity.this.pathStack.size() >= 2) {
                    FtsManagerActivity.this.pathStack.pop();
                    String path = FtsManagerActivity.this.pathStack.peek();
                    FtsManagerActivity.this.tvCurPath.setText(path);
                    FtsManagerActivity.this.showModifyDialog(1, "..");
                } else {
                    FtsManagerActivity.this.path_up_level.setClickable(false);
                }
            }
        });
        // 退出
        this.backBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                FtsManagerActivity.this.mActivity.finish();
            }
        });
        // 单击list
        this.expandableList.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ActionMode actionMode = ((FileExplorerTabActivity) FtsManagerActivity.this.mActivity).getActionMode();
                if (actionMode != null) {
                    actionMode.finish();
                }
//                if (mDropdownNavigation.getVisibility() == View.VISIBLE) {
//                    showDropdownNavigation(false);
//                }
                if (FtsManagerActivity.this.data.get(position).isDirectory()) {
                    try {
                        FtsManagerActivity.this.rootPath1 = FtsManagerActivity.this.rootPath1 + "/" +
                                FtsManagerActivity.this.data.get(position).getPath();
                        FtsManagerActivity.this.tvCurPath.setText(FtsManagerActivity.this.rootPath1.substring
                                (FtsManagerActivity.this.rootPath.lastIndexOf("/") + 1));
                        FtsManagerActivity.this.pathStack.add(FtsManagerActivity.this.rootPath1.substring
                                (FtsManagerActivity.this.rootPath.lastIndexOf("/") + 1));
                        FtsManagerActivity.this.path_up_level.setClickable(true);
                        FtsManagerActivity.this.showModifyDialog(1, FtsManagerActivity.this.data.get(position)
                                .getPath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    FtsManagerActivity.this.showModifyDialog(5, FtsManagerActivity.this.data.get(position));
                }
            }
        });
    }

    protected void onNavigationBarClick() {
        if (this.mDropdownNavigation.getVisibility() == View.VISIBLE) {
            this.showDropdownNavigation(false);
        } else {
            LinearLayout list = (LinearLayout) this.mDropdownNavigation
                    .findViewById(R.id.dropdown_navigation_list);
            list.removeAllViews();
            int pos = 0;
            String displayPath = this.tvCurPath.getText().toString();
            boolean root = true;
            int left = 0;
            while (pos != -1 && !displayPath.equals("/")) {
                int end = displayPath.indexOf("/", pos);
                if (end == -1)
                    break;

                View listItem = LayoutInflater.from(this.mActivity).inflate(
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
                listItem.setTag(displayPath.substring(0, end));
                pos = end + 1;
                list.addView(listItem);
            }
            if (list.getChildCount() > 0)
                this.showDropdownNavigation(true);
        }
    }

    private final View.OnClickListener navigationClick = new View.OnClickListener()
    {

        @Override
        public void onClick(View v) {
            String path = (String) v.getTag();
            assert path != null;
            FtsManagerActivity.this.showDropdownNavigation(false);
            if (path.isEmpty()) {
                path = "网盘";
            }
            FtsManagerActivity.this.addPath(path);
            String currPath = FtsManagerActivity.this.tvCurPath.getText().toString();
            FtsManagerActivity.this.tvCurPath.setText(path);
            String Back = "..";
            int pos = 0;
            path = currPath.substring(path.length() + 1);
            //System.out.println("path = " + path);
            while (pos != -1 && !path.equals("/")) {
                int end = path.indexOf("/", pos);
                if (end == -1)
                    break;
                Back += "/..";
                pos = end + 1;
            }
            FtsManagerActivity.this.showModifyDialog(1, Back);
        }
    };
    private TextView mNavigationBarText;
    private ImageView mNavigationBarUpDownArrow;
    private PopupWindow SetWindow;

    private void setupNaivgationBar() {
        // mNavigationBar = ()getViewById(R.id.navigation_bar);
        this.mNavigationBarText = (TextView) this.mRootView.findViewById(R.id.current_path_view);
        this.mNavigationBarUpDownArrow = (ImageView) this.mRootView.findViewById(R.id.path_pane_arrow);
        View clickable = this.mRootView.findViewById(R.id.current_path_pane);
        clickable.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                FtsManagerActivity.this.onNavigationBarClick();
            }
        });
        this.mDropdownNavigation = this.mRootView.findViewById(R.id.dropdown_navigation);
        // setupClick(mNavigationBar, R.id.path_pane_up_level);
    }

    private void showDropdownNavigation(boolean show) {
        this.mDropdownNavigation.setVisibility(show ? View.VISIBLE : View.GONE);
        this.mNavigationBarUpDownArrow.setImageResource(this.mDropdownNavigation
                .getVisibility() == View.VISIBLE ? R.drawable.arrow_up
                : R.drawable.arrow_down);
    }

    private int initData(int cate) {
        if (cate == 0) {
            this.rootPath1 = this.rootPath;
            String webRoot = "TBS";
            this.addPath(webRoot);
            this.pathStack.add("TBS");
            //data = FileUtils.listAll(webRoot);
            this.tvCurPath.setText(webRoot);
        }
        String iniRoot = UIHelper.getSoftPath(this.mActivity);
        if (iniRoot.endsWith("/") == false) {
            iniRoot += "/";
        }
        iniRoot += this.mActivity.getString(R.string.SD_CARD_TBSAPP_PATH2);
        iniRoot = UIHelper.getShareperference(this.mActivity, constants.SAVE_INFORMATION,
                "Path", iniRoot);
        if (iniRoot.endsWith("/") == false) {
            iniRoot += "/";
        }
        String WebIniFile = iniRoot + constants.WEB_CONFIG_FILE_NAME;
        IniFile m_iniFileIO = new IniFile();
        String appNewsFile = iniRoot
                + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        String userIni = appNewsFile;
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = this.mActivity.getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginFlag", "0", (byte) 0)) == 1) {
            if (cate == 0)
                this.showModifyDialog(0, this.rootPath);
            else
                return 1;
        } else {

            this.showEmptyView(true);
            this.startActivityForResult(new Intent(this.mActivity, LoginDialogResult.class), 1);
        }
        return 0;
        //showModifyDialog(0, rootPath);
        //connect(rootPath);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent intent) {
        if (requestCode == 1 && resultCode == 1) {
            this.showModifyDialog(0, this.rootPath);
        }
    }

    /**
     * ����listView��ͼ
     *
     * @param data
     */
    private void refleshListView(List<FileInfo> data, int count) {
        // String lost = FileUtils.getSDRoot() + "lost+found";
        // data.remove(lost);
        // path_up_level.setClickable(true);
        if (!data.isEmpty()) {
            Collections.sort(data, new Comparator<FileInfo>()
            {
                @Override
                public int compare(FileInfo object1, FileInfo object2) {
                    // 根据字段"LEVEL"排序
                    if (object1.isDirectory() == object2.isDirectory()) {
                        int order = UIHelper.getShareperference(
                                FtsManagerActivity.this.mActivity,
                                constants.SAVE_LOCALMSGNUM, "sort", 0);
                        if (order == 0) {
                            return object1.getPath().compareToIgnoreCase(object2.getPath());
                        } else if (order == 1) {
                            return FtsManagerActivity.this.longToCompareInt(object1.getLength() - object2.getLength());
                        } else if (order == 2) {
                            return FtsManagerActivity.this.longToCompareInt(object1.getModifyTime()
                                    - object2.getModifyTime());
                        } else if (order == 3) {
                            int result = Util.getExtFromFilename(object1.getPath())
                                    .compareToIgnoreCase(
                                            Util.getExtFromFilename(object2.getPath()));
                            if (result != 0)
                                return result;
                            return Util.getNameFromFilename(object1.getPath())
                                    .compareToIgnoreCase(
                                            Util.getNameFromFilename(object2.getPath()));
                        }
                    }
                    return object1.isDirectory() ? -1 : 1;
                }
            });
            if (count == 1) {
                this.listAdapter = new FtsFileAdapter(data, this.mActivity, this.rootPath1);
                this.expandableList.setAdapter(this.listAdapter);
            }
        }
    }

    private int longToCompareInt(long result) {
        return result > 0 ? 1 : result < 0 ? -1 : 0;
    }

    private void addPath(String webRoot) {
        // TODO Auto-generated method stub
        if (this.pathStack.size() > 1) {
            this.pathStack.clear();
        }
        int start = 0;
        String rootPath = "/";
        int iIndex = webRoot.indexOf("/", start);
        while (iIndex >= 0) {
            if (iIndex == 0) {
                start = iIndex + 1;
                rootPath = webRoot.substring(0, start);
            } else {
                start = iIndex + 1;
                rootPath = webRoot.substring(0, iIndex);
            }
            iIndex = webRoot.indexOf("/", start);
            this.pathStack.add(rootPath);
        }
    }

    //    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//        // mFileViewInteractionHub.onPrepareOptionsMenu(menu);
//        super.onPrepareOptionsMenu(menu);
//    }
//    private MenuItem mMenuItem;
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.operation_top_menu, menu);
//        //setIconEnable(menu, true);   //调用这句实现显示ICON
//        // mFileViewInteractionHub.onCreateOptionsMenu(menu);
//        mMenuItem = menu.findItem(R.id.action_order);
//        mMenuItem.setTitle(getResources().getString(R.string.menu_item_sort));
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        int id = item.getItemId();
//        //通过ID来响应菜单项
//        if (id == R.id.action_order) {
//            showSortDialog();
//            return true;
//        } else if (id == R.id.action_new) {
//            showNewDialog();
//            return true;
//        } else if (id == R.id.action_refresh) {
//            showModifyDialog(0, rootPath);
//
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
    public void actionCopy() {
        ((FileExplorerTabActivity) this.mActivity).setIsAction(true);
        this.listAdapter.actionCopy();
        ((FileExplorerTabActivity) this.mActivity).setIsAction(false);
    }

    public void actionPase() {
        ((FileViewActivity) ((FileExplorerTabActivity) this.mActivity)
                .getFragment(Util.SDCARD_TAB_INDEX))
                .onOperationButtonCancel();
        this.onOperationPaste(this.isMove);
        //mConfirmOperationBar.setVisibility(View.GONE);
        try {
            ((FileExplorerTabActivity) this.mActivity).setCustomMenu(new JSONObject(constants.firstmenu));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void actionPaseCancle() {
        ((FileViewActivity) ((FileExplorerTabActivity) this.mActivity)
                .getFragment(Util.SDCARD_TAB_INDEX))
                .onOperationButtonCancel();
        // mConfirmOperationBar.setVisibility(View.GONE);
        try {
            ((FileExplorerTabActivity) this.mActivity).setCustomMenu(new JSONObject(constants.firstmenu));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void actionCancle() {
        this.listAdapter.actionCancle();
    }

    public void actionDelete() {
        this.listAdapter.actionDelete();
    }

    public void actionMove() {
        ((FileExplorerTabActivity) this.mActivity).setIsAction(true);
        this.listAdapter.actionMove();
        ((FileExplorerTabActivity) this.mActivity).setIsAction(false);
    }

    public void actionRename() {
        this.listAdapter.actionRename();
    }

    public void actionSend() {
        this.listAdapter.actionSend();
    }

    public void showNewDialog() {
        if (this.initData(1) == 1) {
            // TODO Auto-generated method stub
            TextInputDialog dialog = new TextInputDialog(this.mActivity,
                    getString(R.string.operation_create_folder),
                    getString(R.string.operation_create_folder_message),
                    getString(R.string.new_folder_name),
                    new OnFinishListener()
                    {
                        @Override
                        public boolean onFinish(String text) {
                            if (TextUtils.isEmpty(text))
                                return false;
                            for (int i = 0; i < FtsManagerActivity.this.data.size(); i++) {
                                if (text.equalsIgnoreCase(FtsManagerActivity.this.data.get(i).getPath())) {
                                    new Builder(FtsManagerActivity.this.mActivity)
                                            .setMessage(
                                                    getString(R.string.fail_to_create_folder))
                                            .setPositiveButton(R.string.confirm, null)
                                            .create().show();
                                    return false;
                                }
                            }
                            FtsManagerActivity.this.showModifyDialog(2, text);
                            return true;
                        }
                    });
            dialog.show();
        }

    }

    public void copyFile(ArrayList<com.tbs.tbsmis.file.FileInfo> files, int count) {
//        //System.out.println(files.size());
//        mConfirmOperationBar = mRootView
//                .findViewById(R.id.moving_operation_bar);
//        mConfirmOperationBar.setVisibility(View.VISIBLE);
//        View button = mConfirmOperationBar.findViewById(R.id.button_moving_confirm);
//        View button1 = mConfirmOperationBar.findViewById(R.id.button_moving_cancel);
//        button.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v) {
//                ((FileViewActivity) ((FileExplorerTabActivity) mActivity)
//                        .getFragment(Util.SDCARD_TAB_INDEX))
//                        .onOperationButtonCancel();
//                onOperationPaste(count);
//                //mConfirmOperationBar.setVisibility(View.GONE);
//                try {
//                    ((FileExplorerTabActivity) mActivity).setCustomMenu(new JSONObject(constants.firstmenu));
//                } catch (JSONException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//        });
//        button1.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v) {
//                ((FileViewActivity) ((FileExplorerTabActivity) mActivity)
//                        .getFragment(Util.SDCARD_TAB_INDEX))
//                        .onOperationButtonCancel();
//               // mConfirmOperationBar.setVisibility(View.GONE);
//                try {
//                    ((FileExplorerTabActivity) mActivity).setCustomMenu(new JSONObject(constants.firstmenu));
//                } catch (JSONException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//        });
        this.isMove = count;
        synchronized (this.mCurFileNameList) {
            this.mCurFileNameList.clear();
            for (com.tbs.tbsmis.file.FileInfo f : files) {
                this.mCurFileNameList.add(f);
            }
        }
    }

    public void showConfirmOperationBar(boolean show) {
        this.mConfirmOperationBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showEmptyView(boolean show) {
        View emptyView = this.mRootView.findViewById(R.id.empty_view);
        //System.out.println("emptyView != null"+emptyView != null);
        if (emptyView != null)
            // System.out.println(show ? View.VISIBLE : View.GONE);
            emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void onOperationPaste(final int count) {
        if (this.mCurFileNameList.size() != 0) {
            if (!this.mCurFileNameList.get(0).isLoacal) {
                if (count == 1) {
                    this.showModifyDialog(4, this.rootPath1);
                } else {
                    this.showModifyDialog(3, this.rootPath1);
                }
            } else {
                //System.out.println("在这里执行3");
                this.ftMSGNotify = new FtMSGNotify();
                this.ftMSGNotify.setiCancelFlag(0);
                Builder builder = new Builder(this.mActivity);
                builder.setTitle("粘贴");
//                builder.setMessage("");
                builder.setCancelable(false);
                LayoutInflater inflater = LayoutInflater.from(this.mActivity);
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
                builder.setNeutralButton("取消", new OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //showModifyDialog(4, ftMSGNotify.CurrentFile());
                        FtsManagerActivity.this.ftMSGNotify.setCancelFlag(true);
                        FtsManagerActivity.this.ftMSGNotify.setiCancelFlag(2);
                        dialog.dismiss();
                    }
                });
                this.ModifyDialog = builder.create();
                this.ModifyDialog.setCanceledOnTouchOutside(false);
                this.message.setText("正在计算文件大小和个数...");
                this.from.setText(this.mCurFileNameList.get(0).filePath.substring(0, this.mCurFileNameList.get(0)
                        .filePath
                        .lastIndexOf("/")));
                this.to.setText(this.rootPath1);
                for (int i = 0; i < this.mCurFileNameList.size(); i++) {
                    if (!this.isCover && this.data != null) {
                        for (int j = 0; j < this.data.size(); j++) {
                            if (this.mCurFileNameList.get(i).IsDir == this.data.get(j).isDirectory()) {
                                //System.out.println(mCurFileNameList.get(i).fileName + " == " + data.get(j).getPath());
                                if (this.mCurFileNameList.get(i).fileName.equalsIgnoreCase(this.data.get(j).getPath()
                                )) {
                                    this.isCover = true;
                                    break;
                                }
                            }
                        }
                    } else
                        break;
                }
                if (this.isCover) {
                    new Builder(this.mActivity)
                            .setMessage("存在重复文件，覆盖or重命名？")
                            .setPositiveButton("覆盖", new OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    FtsManagerActivity.this.isCover = false;
                                    if (FtsManagerActivity.this.Paste(FtsManagerActivity.this.ftMSGNotify, count)) {
                                        FtsManagerActivity.this.ModifyDialog.show();
                                        Timer timer = new Timer();
                                        if (timer != null) {
                                            if (FtsManagerActivity.this.myTimerTask != null) {
                                                FtsManagerActivity.this.time = 0;
                                                FtsManagerActivity.this.myTimerTask.cancel();
                                            }
                                        }
                                        FtsManagerActivity.this.myTimerTask = new FtsManagerActivity.mTimerTask();
                                        timer.schedule(FtsManagerActivity.this.myTimerTask, 1000, 1000);
                                    }
                                }
                            }).setNeutralButton("重命名", new OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            if (FtsManagerActivity.this.Paste(FtsManagerActivity.this.ftMSGNotify, count)) {
                                FtsManagerActivity.this.ModifyDialog.show();
                                Timer timer = new Timer();
                                if (timer != null) {
                                    if (FtsManagerActivity.this.myTimerTask != null) {
                                        FtsManagerActivity.this.time = 0;
                                        FtsManagerActivity.this.myTimerTask.cancel();
                                    }
                                }
                                FtsManagerActivity.this.myTimerTask = new FtsManagerActivity.mTimerTask();
                                timer.schedule(FtsManagerActivity.this.myTimerTask, 1000, 1000);
                            }
                        }
                    }).create().show();
                } else {
                    if (this.Paste(this.ftMSGNotify, count)) {
                        this.ModifyDialog.show();
                        Timer timer = new Timer();
                        if (timer != null) {
                            if (this.myTimerTask != null) {
                                this.time = 0;
                                this.myTimerTask.cancel();
                            }
                        }
                        this.myTimerTask = new FtsManagerActivity.mTimerTask();
                        timer.schedule(this.myTimerTask, 1000, 1000);
                    }
                }
            }
        }

    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void asnycExecute(Runnable r) {
        final Runnable _r = r;
        new AsyncTask()
        {
            @Override
            protected Object doInBackground(Object... params) {
                synchronized (FtsManagerActivity.this.mCurFileNameList) {
                    _r.run();
                }
                return null;
            }
        }.execute();
    }

    private boolean Paste(final FtMSGNotify ftMSGNotify, final int count) {
        this.asnycExecute(new Runnable()
        {
            @Override
            public void run() {
                long size = 0;
                long sum = 0;
                List<com.tbs.tbsmis.file.FileInfo> mOperFileNameList = new ArrayList<com.tbs.tbsmis.file.FileInfo>();
                String messages = "名称:";
                try {
                    IniFile m_iniFileIO = new IniFile();
                    String webRoot = UIHelper.getSoftPath(mActivity);
                    if (webRoot.endsWith("/") == false) {
                        webRoot += "/";
                    }
                    webRoot += FtsManagerActivity.this.mActivity.getString(R.string.SD_CARD_TBSAPP_PATH2);
                    webRoot = UIHelper.getShareperference(FtsManagerActivity.this.mActivity, constants.SAVE_INFORMATION,
                            "Path", webRoot);
                    if (webRoot.endsWith("/") == false) {
                        webRoot += "/";
                    }
                    String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
                    String appNewsFile = webRoot
                            + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                            constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
                    String userIni = appNewsFile;
                    if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                            "LoginType", "0", (byte) 0)) == 1) {
                        String dataPath = FtsManagerActivity.this.mActivity.getFilesDir().getParentFile()
                                .getAbsolutePath();
                        if (dataPath.endsWith("/") == false) {
                            dataPath = dataPath + "/";
                        }
                        userIni = dataPath + "TbsApp.ini";
                    }
                    String ftsAddress = m_iniFileIO.getIniString(userIni, "Skydrive",
                            "ftsAddress", constants.DefaultServerIp, (byte) 0);
                    String ftsPort = m_iniFileIO.getIniString(userIni, "Skydrive",
                            "ftsPort", "1239", (byte) 0);
                    if (ftscategory.equalsIgnoreCase("appUpload")) {
                        ftsAddress = m_iniFileIO.getIniString(userIni, "Store",
                                "ftsAddress", constants.DefaultServerIp, (byte) 0);
                        ftsPort = m_iniFileIO.getIniString(userIni, "Store",
                                "ftsPort", "1239", (byte) 0);
                    } else if (ftscategory.equalsIgnoreCase("imageUpload")) {
                        ftsAddress = m_iniFileIO.getIniString(userIni, "Store",
                                "ftsAddress", constants.DefaultServerIp, (byte) 0);
                        ftsPort = m_iniFileIO.getIniString(userIni, "Store",
                                "ftsPort", "1239", (byte) 0);
                    }else if (ftscategory.equalsIgnoreCase("source_appUpload")) {
                        ftsAddress = m_iniFileIO.getIniString(userIni, "Store",
                                "source_ftsAddress", constants.DefaultServerIp, (byte) 0);
                        ftsPort = m_iniFileIO.getIniString(userIni, "Store",
                                "source_ftsPort", "1239", (byte) 0);
                    } else if (ftscategory.equalsIgnoreCase("source_imageUpload")) {
                        ftsAddress = m_iniFileIO.getIniString(userIni, "Store",
                                "source_ftsAddress", constants.DefaultServerIp, (byte) 0);
                        ftsPort = m_iniFileIO.getIniString(userIni, "Store",
                                "source_ftsPort", "1239", (byte) 0);
                    }
                    FtClient ftclient = FtClient.createObject("fts://" + ftsAddress + ":" + ftsPort,
                            FtsManagerActivity.this.rootPath1);
                    String LoginId = m_iniFileIO.getIniString(userIni, "Login",
                            "LoginId", "", (byte) 0);
                    ftclient.setLoginID(LoginId, "");
                    ftclient.connect();
                    for (com.tbs.tbsmis.file.FileInfo f : FtsManagerActivity.this.mCurFileNameList) {
                        if (ftMSGNotify.getiCancelFlag() == 2) {
                            break;
                        }
                        f.refileName = f.fileName;
                        if (FtsManagerActivity.this.isCover) {
                            for (int j = 0; j < FtsManagerActivity.this.data.size(); j++) {
                                if (f.IsDir == FtsManagerActivity.this.data.get(j).isDirectory()) {
                                    if (f.fileName.equalsIgnoreCase(FtsManagerActivity.this.data.get(j).getPath())) {
                                        FtsManagerActivity.this.reName(f);
                                    }
                                }
                            }
                        }
                        if (ftclient == null) {
                            ftMSGNotify.setCancelFlag(true);
                            ftMSGNotify.setiCancelFlag(3);
                            return;
                        }
                        if (f.IsDir) {
                            long dir = 0;
                            List<com.tbs.tbsmis.file.FileInfo> allDir = FileUtils.GetallPath(f.filePath);
                            for (com.tbs.tbsmis.file.FileInfo fs : allDir) {
                                fs.refileName = f.refileName + "/" +
                                        fs.filePath.substring(f.filePath.length() + 1);
                                dir = dir + fs.fileSize;
                            }
                            mOperFileNameList.addAll(allDir);
                            sum = mOperFileNameList.size();
                            size = size + dir;
                            ftMSGNotify.setAllPath("总共:" + sum +
                                    "项,  大小:" + FileUtils.formatFileSize(size));
                        } else {
                            size = size + f.fileSize;
                            mOperFileNameList.add(f);
                            sum = mOperFileNameList.size();
                            ftMSGNotify.setAllPath("总共:" + sum +
                                    "项,  大小:" + FileUtils.formatFileSize(size));
                        }
                        messages = messages + f.fileName + ",";
                    }
                    //System.out.println("message = "+messages);
                    ftMSGNotify.setAllmessage(messages.substring(0, messages.lastIndexOf(",")));
                    ftMSGNotify.setFileCount((int) sum);
                    ftMSGNotify.setTotalSize(size);
                    long filesize = 0;
                    int i = 1;
                    for (com.tbs.tbsmis.file.FileInfo f : mOperFileNameList) {
                        if (ftMSGNotify.getiCancelFlag() == 2) {
                            break;
                        }
                        ftMSGNotify.setCancelFlag(false);
                        ftMSGNotify.setCfilecount(i++);
                        ftMSGNotify.setiSize(ftMSGNotify.getiSize() + filesize);
                        filesize = f.fileSize;
                        boolean isOK = ftclient.uploadFile(f.filePath, f.refileName, true, ftMSGNotify);
                    }
                    ftMSGNotify.setiSize(size);
                    if (count == 2) {
                        for (com.tbs.tbsmis.file.FileInfo f : FtsManagerActivity.this.mCurFileNameList) {
                            if (f.IsDir)
                                FileUtils.deleteDirectory(f.filePath);
                            else
                                FileUtils.deleteFile(f.filePath);
                        }
                    }
                    if (mOperFileNameList.size() <= 0) {
                        ftMSGNotify.setCancelFlag(true);
                        ftMSGNotify.setiCancelFlag(7);
                    }
                    ftclient.disconnect();
                } catch (URISyntaxException e) {
                    ftMSGNotify.setCancelFlag(true);
                    ftMSGNotify.setiCancelFlag(4);
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    ftMSGNotify.setiCancelFlag(5);
                    ftMSGNotify.setCancelFlag(true);
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                FtsManagerActivity.this.isCover = false;
                FtsManagerActivity.this.mCurFileNameList.clear();
            }
        });
        return true;
    }

    private void reName(com.tbs.tbsmis.file.FileInfo f) {

        String name = FileUtils.getFileNameNoFormat(f.fileName);
        String format = FileUtils.getFileFormat(f.fileName);
        for (int i = 1; i < this.data.size(); i++) {

            for (int j = 0; j < this.data.size(); j++) {
                if (f.refileName.equalsIgnoreCase(this.data.get(j).getPath())) {
                    break;
                }
                if (j == this.data.size() - 1) {
                    return;
                }
            }
            if (f.IsDir)
                f.refileName = f.fileName + "(" + i + ")";
            else
                f.refileName = name + "(" + i + ")." + format;
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
        } else if (this.ftMSGNotify.getTotalSize() == this.ftMSGNotify.getiSize() && this.ftMSGNotify.getCancelFlag()) {
            Message message = new Message();
            message.what = 6;
            this.handler.sendMessage(message);
            this.myTimerTask.cancel();
            this.ModifyDialog.cancel();
        } else {
            //System.out.println(ftMSGNotify.getTotal()+" = getTotal");
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
            FtsManagerActivity.this.time = FtsManagerActivity.this.time + 1;
            FtsManagerActivity.this.start();
        }
    }

    Handler handler = new Handler()
    {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    FtsManagerActivity.this.message_t.setText(FtsManagerActivity.this.ftMSGNotify.getAllPath());
                    FtsManagerActivity.this.message.setText(FtsManagerActivity.this.ftMSGNotify.getAllmessage());
                    FtsManagerActivity.this.time_remaining_panel.setVisibility(View.VISIBLE);
                    if (FtsManagerActivity.this.ftMSGNotify.getFileCount() > 1) {
                        FtsManagerActivity.this.total_progress_panel.setVisibility(View.VISIBLE);
                        FtsManagerActivity.this.total_progress_bar.setMax((int) FtsManagerActivity.this.ftMSGNotify
                                .getTotalSize());
                        FtsManagerActivity.this.total_progress_bar.setProgress((int) (FtsManagerActivity.this
                                .ftMSGNotify.getiSize() + FtsManagerActivity.this
                                .ftMSGNotify.getProg()));
                        FtsManagerActivity.this.num_completed.setText(FtsManagerActivity.this.ftMSGNotify
                                .getCfilecount() + "");
                        FtsManagerActivity.this.num_total.setText(FtsManagerActivity.this.ftMSGNotify.getFileCount()
                                + "");
                        FtsManagerActivity.this.total_percent.setText((int) ((FtsManagerActivity.this.ftMSGNotify
                                .getiSize() + FtsManagerActivity.this.ftMSGNotify.getProg()) * 1.0 /
                                FtsManagerActivity.this.ftMSGNotify
                                        .getTotalSize()
                                * 100)
                                + "%");
                    }
                    if (FtsManagerActivity.this.ftMSGNotify.getProg() > FtsManagerActivity.this.ftMSGNotify.getPiProg
                            ()) {
                        FtsManagerActivity.this.speed.setText(FileUtils.formatFileSize(FtsManagerActivity.this
                                .ftMSGNotify.getProg() - FtsManagerActivity.this
                                .ftMSGNotify.getPiProg()) + "/s");
                        FtsManagerActivity.this.ftMSGNotify.setPiProg(FtsManagerActivity.this.ftMSGNotify.getProg());
                    }
                    FtsManagerActivity.this.item_progress_bar.setMax((int) FtsManagerActivity.this.ftMSGNotify
                            .getTotal());
                    FtsManagerActivity.this.item_progress_bar.setProgress((int) FtsManagerActivity.this.ftMSGNotify
                            .getProg());
                    FtsManagerActivity.this.time_remaining.setText(StringUtils.secToTime(FtsManagerActivity.this.time));
                    FtsManagerActivity.this.curr_message.setText(FileUtils.getFileName(FtsManagerActivity.this
                            .ftMSGNotify.CurrentFile()));
                    FtsManagerActivity.this.item_percent.setText((int) (
                            FtsManagerActivity.this.ftMSGNotify.getProg() * 1.0 / FtsManagerActivity.this.ftMSGNotify
                                    .getTotal() * 100) + "%");
                    break;
                case 2:
                    //FileUtils.deleteFile(ftMSGNotify.CurrentFile());

                    FtsManagerActivity.this.showModifyDialog(0, FtsManagerActivity.this.rootPath1);
                    Toast.makeText(FtsManagerActivity.this.mActivity, "取消操作", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(FtsManagerActivity.this.mActivity, "创建链接失败", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(FtsManagerActivity.this.mActivity, "创建链接失败", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    //FileUtils.deleteFile(ftMSGNotify.CurrentFile());
                    FtsManagerActivity.this.showModifyDialog(0, FtsManagerActivity.this.rootPath1);
                    Toast.makeText(FtsManagerActivity.this.mActivity, "文件传输异常", Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    FtsManagerActivity.this.showModifyDialog(0, FtsManagerActivity.this.rootPath1);
                    Toast.makeText(FtsManagerActivity.this.mActivity, "操作文件完成", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }

    };

    public void showSortDialog() {
        if (this.initData(1) == 1) {
            // TODO Auto-generated method stub
            new Builder(this.mActivity)
                    .setTitle("选择类型")
                    .setSingleChoiceItems(
                            R.array.sort_options,
                            UIHelper.getShareperference(this.mActivity,
                                    constants.SAVE_LOCALMSGNUM, "sort", 0),
                            new OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    UIHelper.setSharePerference(FtsManagerActivity.this.mActivity,
                                            constants.SAVE_LOCALMSGNUM, "sort",
                                            which);
                                    FtsManagerActivity.this.refleshListView(FtsManagerActivity.this.data, 1);
                                    dialog.dismiss();
                                }
                            }).setNegativeButton("取消", null).show();
        }
    }

    @SuppressWarnings("deprecation")
    private void showModifyDialog(int count, FileInfo FilePath) {
        this.ftMSGNotify = new FtMSGNotify();
        if (count == 5) {
            this.Prodialog = new ProgressDialog(this.mActivity);
            this.Prodialog.setTitle("操作");
            this.Prodialog.setMessage("正在缓存，请稍候...");
            this.Prodialog.setIndeterminate(false);
            this.Prodialog.setCanceledOnTouchOutside(false);
        } else if (count == 6) {
            this.Prodialog = new ProgressDialog(this.mActivity);
            this.Prodialog.setTitle("操作");
            this.Prodialog.setMessage("正在保存，请稍候...");
            this.Prodialog.setIndeterminate(false);
            this.Prodialog.setCanceledOnTouchOutside(false);
        }
        this.Prodialog.setButton("取消", new OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method
                // stub
                FtsManagerActivity.this.ftMSGNotify.setCancelFlag(true);
                FtsManagerActivity.this.ftMSGNotify.setiCancelFlag(1);
                dialog.dismiss();
            }
        });
        this.connect(count, FilePath);
    }

    @SuppressWarnings("deprecation")
    public void showModifyDialog(int count, String FilePath) {
        if (count == 0) {
            if (this.initData(1) != 1)
                return;
        }
        if (count == 0 || count == 1) {
            this.Prodialog = new ProgressDialog(this.mActivity);
            this.Prodialog.setTitle("加载中");
            this.Prodialog.setMessage("正在加载，请稍候...");
            this.Prodialog.setIndeterminate(false);
            this.Prodialog.setCanceledOnTouchOutside(false);
        } else if (count == 2) {
            this.Prodialog = new ProgressDialog(this.mActivity);
            this.Prodialog.setTitle("操作");
            this.Prodialog.setMessage("正在新建，请稍候...");
            this.Prodialog.setIndeterminate(false);
            this.Prodialog.setCanceledOnTouchOutside(false);
        } else if (count == 3 || count == 4) {
            this.Prodialog = new ProgressDialog(this.mActivity);
            this.Prodialog.setTitle("操作");
            this.Prodialog.setMessage("正在粘贴，请稍候...");
            this.Prodialog.setIndeterminate(false);
            this.Prodialog.setCanceledOnTouchOutside(false);
        }
        this.Prodialog.setButton("取消", new OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method
                // stub
                dialog.dismiss();
            }
        });
        this.connect(count, FilePath);
    }

    private void connect(int count, String CheckedFile) {
        FtsManagerActivity.MyAsyncTask task = new FtsManagerActivity.MyAsyncTask(count, CheckedFile, this.mActivity);
        task.execute();
    }

    private void connect(int count, FileInfo CheckedFile) {
        FtsManagerActivity.MyAsyncTask task = new FtsManagerActivity.MyAsyncTask(count, CheckedFile, this.mActivity);
        task.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.isopen) {
            this.isopen = false;
            String webRoot = UIHelper.getStoragePath(this.mActivity);
            webRoot = webRoot + constants.SD_CARD_TEMP_PATH3 + this.fileInfo.getPath();
            File file = new File(webRoot);
            if (this.fileInfo.getModifyTime() < file.lastModified()) {
                this.showModifyDialog(6, this.fileInfo);
            } else {
                FileUtils.deleteFile(webRoot);
            }
        }

    }

    public boolean onBack() {
        try {
            if (this.ftsClient != null)
                this.ftsClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    class MyAsyncTask extends AsyncTask<Integer, Integer, String>
    {

        private final int count;
        private String FilePath;
        private final Context context;

        public MyAsyncTask(int count, String CheckedFile, Context context) {
            FilePath = CheckedFile;
            this.context = context;
            this.count = count;
        }

        public MyAsyncTask(int count, FileInfo CheckedFile, Context context) {
            FtsManagerActivity.this.fileInfo = CheckedFile;
            this.context = context;
            this.count = count;
        }

        /**
         * 这里的Integer参数对应AsyncTask中的第一个参数 这里的String返回值对应AsyncTask的第三个参数
         * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
         * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
         */
        @Override
        protected String doInBackground(Integer... params) {
            try {
                if (FtsManagerActivity.this.ftsClient == null) {
                    IniFile m_iniFileIO = new IniFile();
                    String webRoot = UIHelper.getSoftPath(context);
                    if (webRoot.endsWith("/") == false) {
                        webRoot += "/";
                    }
                    webRoot += this.context.getString(R.string.SD_CARD_TBSAPP_PATH2);
                    webRoot = UIHelper.getShareperference(this.context, constants.SAVE_INFORMATION,
                            "Path", webRoot);
                    if (webRoot.endsWith("/") == false) {
                        webRoot += "/";
                    }
                    String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
                    String appNewsFile = webRoot
                            + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                            constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
                    String userIni = appNewsFile;
                    if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                            "LoginType", "0", (byte) 0)) == 1) {
                        String dataPath = this.context.getFilesDir().getParentFile()
                                .getAbsolutePath();
                        if (dataPath.endsWith("/") == false) {
                            dataPath = dataPath + "/";
                        }
                        userIni = dataPath + "TbsApp.ini";
                    }
                    String ftsAddress = m_iniFileIO.getIniString(userIni, "Skydrive",
                            "ftsAddress", constants.DefaultServerIp, (byte) 0);
                    String ftsPort = m_iniFileIO.getIniString(userIni, "Skydrive",
                            "ftsPort", "1239", (byte) 0);
                    FtsManagerActivity.this.rootPath = m_iniFileIO.getIniString(appNewsFile, "Skydrive",
                            "ftsPath", "/home/tbs/tbs_soft/TBS", (byte) 0);
                    if (ftscategory.equalsIgnoreCase("appUpload")) {
                        ftsAddress = m_iniFileIO.getIniString(userIni, "Store",
                                "ftsAddress", constants.DefaultServerIp, (byte) 0);
                        ftsPort = m_iniFileIO.getIniString(userIni, "Store",
                                "ftsPort", "1239", (byte) 0);
                        FtsManagerActivity.this.rootPath = m_iniFileIO.getIniString(appNewsFile, "Store",
                                "ftsAppPath", "/home/tbs/tbs_soft/TBS/TBS-App", (byte) 0);
                    } else if (ftscategory.equalsIgnoreCase("imageUpload")) {
                        ftsAddress = m_iniFileIO.getIniString(userIni, "Store",
                                "ftsAddress", constants.DefaultServerIp, (byte) 0);
                        ftsPort = m_iniFileIO.getIniString(userIni, "Store",
                                "ftsPort", "1239", (byte) 0);
                        FtsManagerActivity.this.rootPath = m_iniFileIO.getIniString(appNewsFile, "Store",
                                "ftsImagePath", "/home/tbs/tbs_soft/TBSWcm/Web/Store/image", (byte) 0);
                    }else if (ftscategory.equalsIgnoreCase("source_appUpload")) {
                        ftsAddress = m_iniFileIO.getIniString(userIni, "Store",
                                "source_ftsAddress", constants.DefaultServerIp, (byte) 0);
                        ftsPort = m_iniFileIO.getIniString(userIni, "Store",
                                "source_ftsPort", "1239", (byte) 0);
                        FtsManagerActivity.this.rootPath = m_iniFileIO.getIniString(appNewsFile, "Store",
                                "source_ftsAppPath", "/home/tbs/tbs_soft/TBS/TBS-File", (byte) 0);
                    } else if (ftscategory.equalsIgnoreCase("source_imageUpload")) {
                        ftsAddress = m_iniFileIO.getIniString(userIni, "Store",
                                "source_ftsAddress", constants.DefaultServerIp, (byte) 0);
                        ftsPort = m_iniFileIO.getIniString(userIni, "Store",
                                "source_ftsPort", "1239", (byte) 0);
                        FtsManagerActivity.this.rootPath = m_iniFileIO.getIniString(appNewsFile, "Store",
                                "source_ftsImagePath", "/home/tbs/tbs_soft/TBSWcm/Web/FileStore/image", (byte) 0);
                    }
                    FtsManagerActivity.this.ftsClient = FtClient.createObject("fts://" + ftsAddress + ":" + ftsPort,
                            FtsManagerActivity.this.rootPath);

                    String LoginId = m_iniFileIO.getIniString(userIni, "Login",
                            "LoginId", "", (byte) 0);
                    FtsManagerActivity.this.ftsClient.setLoginID(LoginId, "");
                    FtsManagerActivity.this.ftsClient.connect();
                }
                if (this.count == 1) {
                    FtsManagerActivity.this.ftsClient.chdir(this.FilePath);
                } else if (this.count == 2) {
                    FtsManagerActivity.this.ftsClient.mkdir(this.FilePath);
                } else if (this.count == 5) {
                    String webRoot = UIHelper.getStoragePath(context);
                    webRoot = webRoot + constants.SD_CARD_TEMP_PATH3;
                    File file = new File(webRoot);
                    if (!file.exists())
                        file.mkdirs();

                    boolean isOK = FtsManagerActivity.this.ftsClient.downloadToFile(FtsManagerActivity.this.fileInfo
                            .getPath(), webRoot +
                            FtsManagerActivity.this.fileInfo.getPath(), FtsManagerActivity.this.ftMSGNotify, true);
                    file = new File(webRoot + FtsManagerActivity.this.fileInfo.getPath());
                    //System.out.println(fileInfo.getModifyTime());
                    //System.out.println(file.setLastModified(fileInfo.getModifyTime()));
                    FtsManagerActivity.this.fileInfo.setModifyTime(file.lastModified());
                    if (isOK)
                        return "true";
                    else return "false";
                } else if (this.count == 6) {
                    String webRoot = UIHelper.getStoragePath(context);
                    webRoot = webRoot + constants.SD_CARD_TEMP_PATH3 + FtsManagerActivity.this.fileInfo.getPath();
//                    System.out.println(ftsClient.getCwd()+ fileInfo.getPath());
                    boolean isOK = FtsManagerActivity.this.ftsClient.uploadFile(webRoot, FtsManagerActivity.this
                            .fileInfo.getPath(), true, FtsManagerActivity.this
                            .ftMSGNotify);
                } else if (this.count == 4) {
                    for (com.tbs.tbsmis.file.FileInfo f : FtsManagerActivity.this.mCurFileNameList) {
                        f.refileName = f.fileName;
                        FtsManagerActivity.this.reName(f);
                        String Back = "";
                        if (!FtsManagerActivity.this.rootPath.equalsIgnoreCase(FtsManagerActivity.this.rootPath1)) {
                            Back = "..";
                            int pos = 0;
                            String path = FtsManagerActivity.this.rootPath1.substring(FtsManagerActivity.this
                                    .rootPath.length() + 1);
                            while (pos != -1 && !path.equals("/")) {
                                int end = path.indexOf("/", pos);
                                if (end == -1)
                                    break;
                                Back += "/..";
                                pos = end + 1;
                            }
                        }
                        if (f.IsDir) {
                            if (!f.filePath.equalsIgnoreCase(FtsManagerActivity.this.rootPath1)) {
                                if (!f.filePath.equalsIgnoreCase(FtsManagerActivity.this.rootPath)) {
                                    if (Back == "") {
                                        boolean isOK = FtsManagerActivity.this.ftsClient.copyDirectory(f.filePath
                                                .substring(FtsManagerActivity.this.rootPath.length()
                                                + 1) + "/" + f
                                                .fileName, f.refileName);
                                        if (isOK) {
                                            FtsManagerActivity.this.ftsClient.deleteDirectory(f.filePath.substring
                                                    (FtsManagerActivity.this
                                                    .rootPath.length() + 1) +
                                                    "/" + f
                                                    .fileName);
                                        }
                                    } else {
                                        boolean isOK = FtsManagerActivity.this.ftsClient.copyDirectory(Back + "/" + f
                                                .filePath.substring
                                                (FtsManagerActivity.this.rootPath.length() +
                                                        1) + "/" + f.fileName, f.refileName);
                                        if (isOK) {
                                            FtsManagerActivity.this.ftsClient.deleteDirectory(Back + "/" + f.filePath
                                                    .substring(FtsManagerActivity.this.rootPath
                                                    .length() +
                                                    1) + "/" + f.fileName);
                                        }
                                    }
                                } else {
                                    if (Back != "") {
                                        boolean isOK = FtsManagerActivity.this.ftsClient.copyDirectory(Back + "/" + f
                                                .fileName, f.refileName);
                                        if (isOK) {
                                            FtsManagerActivity.this.ftsClient.deleteDirectory(Back + "/" + f.fileName);
                                        }
                                    }
                                }
                            }
                        } else {
                            if (!f.filePath.equalsIgnoreCase(FtsManagerActivity.this.rootPath1)) {
                                if (!f.filePath.equalsIgnoreCase(FtsManagerActivity.this.rootPath)) {
                                    if (Back == "") {
                                        boolean isOK = FtsManagerActivity.this.ftsClient.copy(f.filePath.substring
                                                (FtsManagerActivity.this
                                                .rootPath.length() + 1) +
                                                "/" + f.fileName, f
                                                .refileName);
                                        if (isOK) {
                                            FtsManagerActivity.this.ftsClient.unlink(f.filePath.substring
                                                    (FtsManagerActivity.this.rootPath.length() + 1) + "/" + f
                                                    .fileName, false);
                                        }
                                    } else {
                                        boolean isOK = FtsManagerActivity.this.ftsClient.copy(Back + "/" + f.filePath
                                                .substring(FtsManagerActivity.this.rootPath
                                                .length() + 1) + "/"
                                                + f
                                                .fileName, f.refileName);
                                        if (isOK) {
                                            FtsManagerActivity.this.ftsClient.unlink(Back + "/" + f.filePath
                                                    .substring(FtsManagerActivity
                                                    .this.rootPath.length() + 1)
                                                    + "/"
                                                    + f
                                                    .fileName, false);
                                        }
                                    }
                                } else {
                                    if (Back != "") {
                                        boolean isOK = FtsManagerActivity.this.ftsClient.copy(Back + "/" + f
                                                .fileName, f.refileName);
                                        if (isOK) {
                                            FtsManagerActivity.this.ftsClient.unlink(Back + "/" + f
                                                    .fileName, false);
                                        }
                                    }
                                }
                            }
                        }

                    }
                } else if (this.count == 3) {
                    for (com.tbs.tbsmis.file.FileInfo f : FtsManagerActivity.this.mCurFileNameList) {
                        f.refileName = f.fileName;
                        FtsManagerActivity.this.reName(f);
                        String Back = "";
                        if (!FtsManagerActivity.this.rootPath.equalsIgnoreCase(FtsManagerActivity.this.rootPath1)) {
                            Back = "..";
                            int pos = 0;
                            String path = FtsManagerActivity.this.rootPath1.substring(FtsManagerActivity.this
                                    .rootPath.length() + 1);
                            while (pos != -1 && !path.equals("/")) {
                                int end = path.indexOf("/", pos);
                                if (end == -1)
                                    break;
                                Back += "/..";
                                pos = end + 1;
                            }
                        }
                        if (f.IsDir) {
                            if (f.filePath.equalsIgnoreCase(FtsManagerActivity.this.rootPath1)) {
                                FtsManagerActivity.this.ftsClient.copyDirectory(f
                                        .fileName, f.refileName);
                            } else {
                                if (!f.filePath.equalsIgnoreCase(FtsManagerActivity.this.rootPath)) {
                                    if (Back == "") {
                                        FtsManagerActivity.this.ftsClient.copyDirectory(f.filePath.substring
                                                (FtsManagerActivity.this.rootPath.length() + 1) + "/" + f
                                                .fileName, f.refileName);
                                    } else {
                                        FtsManagerActivity.this.ftsClient.copyDirectory(Back + "/" + f.filePath
                                                .substring(FtsManagerActivity
                                                .this.rootPath.length() +
                                                1) + "/" + f.fileName, f.refileName);
                                    }
                                } else {
                                    if (Back == "") {
                                        FtsManagerActivity.this.ftsClient.copyDirectory(f.fileName, f.refileName);
                                    } else {
                                        FtsManagerActivity.this.ftsClient.copyDirectory(Back + "/" + f.fileName, f
                                                .refileName);
                                    }
                                }
                            }
                        } else {
                            if (f.filePath.equalsIgnoreCase(FtsManagerActivity.this.rootPath1)) {
                                FtsManagerActivity.this.ftsClient.copy(f
                                        .fileName, f.refileName);
                            } else {
                                if (!f.filePath.equalsIgnoreCase(FtsManagerActivity.this.rootPath)) {
                                    if (Back == "") {
                                        FtsManagerActivity.this.ftsClient.copy(f.filePath.substring
                                                (FtsManagerActivity.this.rootPath.length() + 1) + "/" + f.fileName, f
                                                .refileName);
                                    } else {
                                        FtsManagerActivity.this.ftsClient.copy(Back + "/" + f.filePath.substring
                                                (FtsManagerActivity.this
                                                .rootPath.length() + 1) + "/"
                                                + f
                                                .fileName, f.refileName);
                                    }
                                } else {
                                    if (Back == "") {
                                        FtsManagerActivity.this.ftsClient.copy(f.fileName, f.refileName);
                                    } else {
                                        FtsManagerActivity.this.ftsClient.copy(Back + "/" + f.fileName, f.refileName);
                                    }
                                }
                            }
                        }
                    }
                }
//                if (data != null) {
//                    data.clear();
//                }
                FtsManagerActivity.this.data = FtsManagerActivity.this.ftsClient.list("*");
                if (FtsManagerActivity.this.data == null) {
                    return "false";
                }
                int n = FtsManagerActivity.this.data.size();
                for (int i = n - 1; i >= 0; i--) {
                    if (FtsManagerActivity.this.data.get(i).getPath().equalsIgnoreCase(".")
                            || FtsManagerActivity.this.data.get(i).getPath().equalsIgnoreCase("..")) {
                        FtsManagerActivity.this.data.remove(i);
                    }
                }
                FtsManagerActivity.this.rootPath1 = FtsManagerActivity.this.ftsClient.getCwd();
                FtsManagerActivity.this.refleshListView(FtsManagerActivity.this.data, 0);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return "true";

        }

        /**
         * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
         * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
         */
        @Override
        protected void onPostExecute(String result) {
            if (this.count == 5) {
                if (FtsManagerActivity.this.ftMSGNotify.getiCancelFlag() == 0) {
                    if (result.equalsIgnoreCase("true")) {
                        String webRoot = UIHelper.getStoragePath(context);
                        FtsManagerActivity.this.isopen = true;
                        IntentBuilder.viewFile(this.context, webRoot + constants.SD_CARD_TEMP_PATH3 +
                                FtsManagerActivity.this.fileInfo.getPath());
                    } else {
                        Toast.makeText(this.context, "缓存失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ftMSGNotify.setiCancelFlag(0);
                    String webRoot = UIHelper.getStoragePath(context);
                    FileUtils.deleteFile(webRoot + constants.SD_CARD_TEMP_PATH3 +
                            FtsManagerActivity.this.fileInfo.getPath());
                    FtsManagerActivity.this.fileInfo = null;
                }
            } else if (this.count == 6) {
                if (FtsManagerActivity.this.ftMSGNotify.getiCancelFlag() == 1) {
                    ftMSGNotify.setiCancelFlag(0);
                    String webRoot = UIHelper.getStoragePath(context);
                    FileUtils.deleteFile(webRoot + constants.SD_CARD_TEMP_PATH3 +
                            FtsManagerActivity.this.fileInfo.getPath());
                    FtsManagerActivity.this.fileInfo = null;
                }
            }
            if (FtsManagerActivity.this.data != null) {
                FtsManagerActivity.this.showEmptyView(FtsManagerActivity.this.data.size() == 0);
                FtsManagerActivity.this.listAdapter = new FtsFileAdapter(FtsManagerActivity.this.data, this.context,
                        FtsManagerActivity.this.rootPath1);
                FtsManagerActivity.this.expandableList.setAdapter(FtsManagerActivity.this.listAdapter);
            } else {
                FtsManagerActivity.this.showEmptyView(true);
            }
            FtsManagerActivity.this.Prodialog.dismiss();

        }

        // 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
        @Override
        protected void onPreExecute() {
            FtsManagerActivity.this.Prodialog.show();
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
