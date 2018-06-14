package com.tbs.tbsmis.activity;

/**
 * Created by TBS on 2017/8/11.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.Formatter;
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

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.GetRequest;
import com.tbs.Tbszlib.JTbszlib;
import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.check.DiskFileAdapter;
import com.tbs.tbsmis.check.LoginDialogResult;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.file.FileExplorerTabActivity;
import com.tbs.tbsmis.file.FileExplorerTabActivity.IBackPressedListener;
import com.tbs.tbsmis.file.FileInfo;
import com.tbs.tbsmis.file.FileViewActivity;
import com.tbs.tbsmis.file.IntentBuilder;
import com.tbs.tbsmis.file.TextInputDialog;
import com.tbs.tbsmis.file.TextInputDialog.OnFinishListener;
import com.tbs.tbsmis.file.Util;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created by TBS on 2015/12/17.
 */
@TargetApi(VERSION_CODES.HONEYCOMB)
public class DiskManagerActivity extends Fragment implements IBackPressedListener
{
    private boolean isInitCache = false;
    private FileInfo tempFileInfo;
    private ListView expandableList;
    // private UpChooseListener listener;
    private TextView tvCurPath;
    private List<FileInfo> data = new ArrayList<FileInfo>();
    private final Stack<String> pathStack = new Stack<String>();
    private String rootPath = "网盘";
    private ProgressDialog Prodialog;
    private ImageView backBtn;
    private ImageView path_up_level;
    private View mDropdownNavigation;
    private ImageView orderBtn;
    private RelativeLayout up_file_title;
    private LinearLayout layoutButton;
    private TextView up_file_txt;
    private DiskFileAdapter listAdapter;
    private View mRootView;
    private Activity mActivity;
    private String rootPath1;
    private View mConfirmOperationBar;
    private final ArrayList<FileInfo> mCurFileNameList = new ArrayList<FileInfo>();
    private AlertDialog ModifyDialog;
    private boolean isCover = false;
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
    private TextView speed_t;
    private int time;
    private int isMove;
    private IniFile IniFile;
    private String userIni;

    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mActivity = this.getActivity();
        this.mRootView = inflater.inflate(R.layout.file_explorer_list, container, false);
        MyActivity.getInstance().addActivity(this.mActivity);
        this.init();
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
        this.initData(0);

        this.setupNaivgationBar();
        this.path_up_level.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                ActionMode actionMode = ((FileExplorerTabActivity) mActivity).getActionMode();
                if (actionMode != null) {
                    actionMode.finish();
                }
                if (pathStack.size() >= 2) {
                    pathStack.pop();
                    String path = pathStack.peek();
                    if (path.equals(rootPath)) {
                        path = "";
                    }
                    showModifyDialog(1, path);
                } else {
                    path_up_level.setClickable(false);
                }
            }
        });
        // 退出
        this.backBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
        // 单击list
        this.expandableList.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ActionMode actionMode = ((FileExplorerTabActivity) mActivity).getActionMode();
                if (actionMode != null) {
                    actionMode.finish();
                }
                if (data.get(position).IsDir) {
                    try {
                        showModifyDialog(1, rootPath1 + "/" +
                                data.get(position).fileName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    showModifyDialog(5, data.get(position));
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
            showDropdownNavigation(false);
            if (path.isEmpty()) {
                path = "网盘";
            }
            if (path.equals(rootPath)) {
                rootPath1 = "";
                path = "";
            }
            //addPath(rootPath + path);
            showModifyDialog(1, path);
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
                onNavigationBarClick();
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

    private void initPath() {
        String webRoot = UIHelper.getSoftPath(mActivity);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        webRoot += getString(R.string.SD_CARD_TBSAPP_PATH2);
        webRoot = UIHelper.getShareperference(mActivity, constants.SAVE_INFORMATION,
                "Path", webRoot);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        this.IniFile = new IniFile();
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String appTestFile = webRoot
                + this.IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        this.userIni = appTestFile;
        if (Integer.parseInt(this.IniFile.getIniString(this.userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = mActivity.getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            this.userIni = dataPath + "TbsApp.ini";
        }
    }

    private int initData(int cate) {
        initPath();
        if (cate == 0) {
            this.rootPath1 = "";
            //this.addPath(rootPath);
            this.pathStack.add(rootPath);
            //data = FileUtils.listAll(webRoot);
            this.tvCurPath.setText(rootPath);
        }

        if (Integer.parseInt(IniFile.getIniString(userIni, "Login",
                "LoginFlag", "0", (byte) 0)) == 1) {
            if (cate == 0)
                this.showModifyDialog(0, "");
            else
                return 1;
        } else {
            this.showEmptyView(true);
            this.startActivityForResult(new Intent(this.mActivity, LoginDialogResult.class), 1);
        }
        return 0;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent intent) {
        if (requestCode == 1 && resultCode == 1) {
            this.showModifyDialog(0, "");
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
        if (data.size() >= 2) {
            Collections.sort(data, new Comparator<FileInfo>()
            {
                @Override
                public int compare(FileInfo object1, FileInfo object2) {
                    // 根据字段"LEVEL"排序
                    if (object1.IsDir == object2.IsDir) {
                        int order = UIHelper.getShareperference(
                                mActivity,
                                constants.SAVE_LOCALMSGNUM, "sort", 0);
                        if (order == 0) {
                            return object1.fileName.compareToIgnoreCase(object2.fileName);
                        } else if (order == 1) {
                            return longToCompareInt(object1.fileSize - object2.fileSize);
                        } else if (order == 2) {
                            return longToCompareInt(object1.ModifiedDate
                                    - object2.ModifiedDate);
                        } else if (order == 3) {
                            int result = Util.getExtFromFilename(object1.fileName)
                                    .compareToIgnoreCase(
                                            Util.getExtFromFilename(object2.fileName));
                            if (result != 0)
                                return result;
                            return Util.getNameFromFilename(object1.fileName)
                                    .compareToIgnoreCase(
                                            Util.getNameFromFilename(object2.fileName));
                        }
                    }
                    return object1.IsDir ? -1 : 1;
                }
            });
        }
        if (count == 1) {
            this.listAdapter = new DiskFileAdapter(data, this.mActivity, this.rootPath1);
            this.expandableList.setAdapter(listAdapter);
        }

    }

    private int longToCompareInt(long result) {
        return result > 0 ? 1 : result < 0 ? -1 : 0;
    }

    private void addPath(String webRoot) {
        // TODO Auto-generated method stub
        if (this.pathStack.size() > 0) {
            this.pathStack.clear();
            pathStack.add(rootPath);
        }
        int start = 0;
        String rootPath;
        int iIndex = webRoot.indexOf("/", start);
        if (iIndex >= 0) {
            while (iIndex >= 0) {
                start = iIndex + 1;
                iIndex = webRoot.indexOf("/", start);
                if (iIndex > 0) {
                    rootPath = webRoot.substring(0, iIndex);
                    this.pathStack.add(rootPath);
                }
            }
        }

    }

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
        // this.listAdapter.actionDelete();
        new AlertDialog.Builder(mActivity)
                .setMessage("确认删除？")
                .setPositiveButton("确认", new DialogInterface.OnClickListener()
                {
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showModifyDialog(7, "");
                    }
                }).setNeutralButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    public void actionMove() {
        ((FileExplorerTabActivity) this.mActivity).setIsAction(true);
        this.listAdapter.actionMove();
        ((FileExplorerTabActivity) this.mActivity).setIsAction(false);
    }

    public void actionRename() {

        if (listAdapter.getSelected().size() == 0)
            return;
        final FileInfo f = listAdapter.getSelected().get(0);
        TextInputDialog dialog = new TextInputDialog(mActivity,
                mActivity.getString(R.string.operation_rename),
                mActivity.getString(R.string.operation_rename_message),
                f.fileName, new TextInputDialog.OnFinishListener()
        {
            @Override
            public boolean onFinish(String text) {
                if (TextUtils.isEmpty(text))
                    return false;
                showModifyDialog(8, text);
                return true;
            }
        });

        dialog.show();
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
                            for (int i = 0; i < data.size(); i++) {
                                if (text.equalsIgnoreCase(data.get(i).fileName)) {
                                    new Builder(mActivity)
                                            .setMessage(
                                                    getString(R.string.fail_to_create_folder))
                                            .setPositiveButton(R.string.confirm, null)
                                            .create().show();
                                    return false;
                                }
                            }
                            showModifyDialog(2, text);
                            return true;
                        }
                    });
            dialog.show();
        }

    }

    public void copyFile(ArrayList<FileInfo> files, int count) {
        this.isMove = count;
        synchronized (this.mCurFileNameList) {
            this.mCurFileNameList.clear();
            for (FileInfo f : files) {
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

                Builder builder = new Builder(this.mActivity);
                builder.setTitle("粘贴");
                builder.setCancelable(false);
                LayoutInflater inflater = LayoutInflater.from(this.mActivity);
                View v = inflater.inflate(R.layout.archive_progress_dialog, null);
                this.time_remaining_panel = (LinearLayout) v.findViewById(R.id.time_remaining_panel);
                LinearLayout item_progress_panel = (LinearLayout) v.findViewById(R.id.item_progress_panel);
                this.item_progress_bar = (ProgressBar) v.findViewById(R.id.item_progress_bar);
                //this.item_percent = (TextView) v.findViewById(id.item_percent);
                this.total_progress_bar = (ProgressBar) v.findViewById(R.id.total_progress_bar);
                //this.time_remaining = (TextView) v.findViewById(id.time_remaining);
                this.message = (TextView) v.findViewById(R.id.message);
                this.from = (TextView) v.findViewById(R.id.from);
                this.to = (TextView) v.findViewById(R.id.to);
                TextView time_remaining_title = (TextView) v.findViewById(R.id.time_remaining_title);
                this.num_completed = (TextView) v.findViewById(R.id.num_completed);
                this.num_total = (TextView) v.findViewById(R.id.num_total);
                this.total_percent = (TextView) v.findViewById(R.id.total_percent);
                this.message_t = (TextView) v.findViewById(R.id.message_t);
                this.speed_t = (TextView) v.findViewById(R.id.speed);
                time_remaining_panel.setVisibility(View.VISIBLE);
                item_progress_panel.setVisibility(View.GONE);
                time_remaining_title.setVisibility(View.GONE);
                builder.setView(v);
                builder.setNeutralButton("取消", new OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

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
                            if (this.mCurFileNameList.get(i).IsDir == this.data.get(j).IsDir) {
                                if (this.mCurFileNameList.get(i).fileName.equalsIgnoreCase(this.data.get(j).fileName)) {
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
                                    formUpload(isCover);
                                    isCover = false;
                                }
                            }).setNeutralButton("跳过", new OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            formUpload(isCover);
                        }
                    }).create().show();
                } else {
                    formUpload(isCover);
                }
            }
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

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
                                    UIHelper.setSharePerference(mActivity,
                                            constants.SAVE_LOCALMSGNUM, "sort",
                                            which);
                                    refleshListView(data, 1);
                                    dialog.dismiss();
                                }
                            }).setNegativeButton("取消", null).show();
        }
    }

    @SuppressWarnings("deprecation")
    private void showModifyDialog(int count, FileInfo FilePath) {
        if (count == 5) {
            this.Prodialog = new ProgressDialog(this.mActivity);
            this.Prodialog.setTitle("操作");
            this.Prodialog.setMessage("正在缓存，请稍候...");
            this.Prodialog.setIndeterminate(false);
            this.Prodialog.setCanceledOnTouchOutside(false);
            fileTemp(FilePath);
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
                OkGo.getInstance().cancelTag(this);
                dialog.dismiss();
            }
        });
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
            this.Prodialog.setButton("取消", new OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method
                    // stub
                    OkGo.getInstance().cancelTag(this);
                    dialog.dismiss();
                }
            });
            if (FilePath == null)
                FilePath = rootPath1;
            getFileData(FilePath);
        } else if (count == 2) {
            this.Prodialog = new ProgressDialog(this.mActivity);
            this.Prodialog.setTitle("操作");
            this.Prodialog.setMessage("正在新建，请稍候...");
            this.Prodialog.setIndeterminate(false);
            this.Prodialog.setCanceledOnTouchOutside(false);
            this.Prodialog.setButton("取消", new OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method
                    // stub
                    OkGo.getInstance().cancelTag(this);
                    dialog.dismiss();
                }
            });
            fileOperation(count, FilePath);
        } else if (count == 3 || count == 4) {
            this.Prodialog = new ProgressDialog(this.mActivity);
            this.Prodialog.setTitle("操作");
            this.Prodialog.setMessage("正在粘贴，请稍候...");
            this.Prodialog.setIndeterminate(false);
            this.Prodialog.setCanceledOnTouchOutside(false);
            this.Prodialog.setButton("取消", new OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method
                    // stub
                    OkGo.getInstance().cancelTag(this);
                    dialog.dismiss();
                }
            });
            fileOperation(count, FilePath);
        } else if (count == 7) {
            this.Prodialog = new ProgressDialog(this.mActivity);
            this.Prodialog.setTitle("操作");
            this.Prodialog.setMessage("正在删除，请稍候...");
            this.Prodialog.setIndeterminate(false);
            this.Prodialog.setCanceledOnTouchOutside(false);
            this.Prodialog.setButton("取消", new OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method
                    // stub
                    OkGo.getInstance().cancelTag(this);
                    dialog.dismiss();
                }
            });
            fileDelOrReName(7, listAdapter.getSelected(), FilePath);
        } else if (count == 9) {
            this.Prodialog = new ProgressDialog(this.mActivity);
            this.Prodialog.setTitle("操作");
            this.Prodialog.setMessage("正在删除，请稍候...");
            this.Prodialog.setIndeterminate(false);
            this.Prodialog.setCanceledOnTouchOutside(false);
            this.Prodialog.setButton("取消", new OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method
                    // stub
                    OkGo.getInstance().cancelTag(this);
                    dialog.dismiss();
                }
            });
            fileDelOrReName(9, mCurFileNameList, FilePath);
        } else if (count == 8) {
            this.Prodialog = new ProgressDialog(this.mActivity);
            this.Prodialog.setTitle("操作");
            this.Prodialog.setMessage("正在重命名，请稍候...");
            this.Prodialog.setIndeterminate(false);
            this.Prodialog.setCanceledOnTouchOutside(false);
            this.Prodialog.setButton("取消", new OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method
                    // stub
                    OkGo.getInstance().cancelTag(this);
                    dialog.dismiss();
                }
            });
            fileDelOrReName(8, listAdapter.getSelected(), FilePath);
        }
    }

    public Map<String, String> getUserFileList(String filePath) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("act", "getUserFileList");
        params.put("type", "disk");
        params.put("sort", "1");
        params.put("order", "asc");
        params.put("path", filePath);
        params.put("account", IniFile.getIniString(this.userIni, "Login",
                "Account", "", (byte) 0));
        return params;
    }
    public Map<String, String> getUserFileTemp(String filePath) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("act", "downloadUserFile");
        params.put("type", "disk");
        params.put("selFiles", filePath);
        params.put("path", rootPath1);
        params.put("account", IniFile.getIniString(this.userIni, "Login",
                "Account", "", (byte) 0));
        return params;
    }
    public Map<String, String> createUserDir(String filePath) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("act", "createUserDir");
        params.put("type", "disk");
        params.put("newDir", filePath);
        params.put("path", rootPath1);
        params.put("account", IniFile.getIniString(this.userIni, "Login",
                "Account", "", (byte) 0));
        return params;
    }


    public Map<String, String> reNameUserDir(ArrayList<FileInfo> fileList, String filePath) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("act", "renameUserFile");
        params.put("type", "disk");
        params.put("path", rootPath1);
        params.put("fileName", fileList.get(0).fileName);
        params.put("newFileName", filePath);
        //newFileName：新名称
        params.put("account", IniFile.getIniString(this.userIni, "Login",
                "Account", "", (byte) 0));
        return params;
    }

    private void fileOperation(final int count, String filePath) {
        Prodialog.show();
        constants.verifyURL = "http://"
                + IniFile.getIniString(userIni, "Login",
                "ebsAddress", constants.DefaultServerIp, (byte) 0)
                + ":"
                + IniFile.getIniString(userIni, "Login", "ebsPort",
                "8083", (byte) 0)
                + "/EBS/DIRServlet";
        GetRequest<String> okgo = null;
        if (count == 2) {
            okgo = OkGo.get(constants.verifyURL);
            okgo.params(createUserDir(filePath), false);
        } else if (count == 3 || count == 4) {
            String fileName = mCurFileNameList.get(0).fileName;
            for (int i = 1; i < mCurFileNameList.size(); i++) {
                fileName = fileName + "&fileName=" + mCurFileNameList.get(i).fileName;
            }
            Map<String, String> params = new HashMap<String, String>();
            String parentPath = mCurFileNameList.get(0).filePath;
            params.put("act", "copyUserFile");
            params.put("type", "disk");
            if (filePath.isEmpty())
                params.put("desPath", "/");
            else
                params.put("desPath", filePath);
            params.put("path", parentPath);
            params.put("account", IniFile.getIniString(this.userIni, "Login",
                    "Account", "", (byte) 0));
            constants.verifyURL = constants.verifyURL + "?fileName=" + fileName;
            okgo = OkGo.get(constants.verifyURL);
            okgo.params(params, false);
        }
        okgo.tag(this)
                .cacheMode(CacheMode.NO_CACHE)  //缓存模式先使用缓存,然后使用网络数据
                .execute(new StringCallback()
                {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Prodialog.dismiss();

                        if (response.body().toString().equalsIgnoreCase("创建目录成功")) {
                            Toast.makeText(mActivity, response.body().toString(), Toast.LENGTH_SHORT).show();
                            showModifyDialog(1, rootPath1);
                        } else if (response.body().toString().equalsIgnoreCase("拷贝文件成功")) {

                            if (count == 3) {
                                showModifyDialog(1, rootPath1);
                                Toast.makeText(mActivity, response.body().toString(), Toast.LENGTH_SHORT).show();
                            } else if (count == 4) {
                                Toast.makeText(mActivity, "剪切文件成功", Toast.LENGTH_SHORT).show();
                                showModifyDialog(9, rootPath1);
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Prodialog.dismiss();
                        if (response == null)
                            Toast.makeText(mActivity, "操作失败", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(mActivity, response.body().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fileDelOrReName(final int count, ArrayList<FileInfo> fileList, String newFilePath) {
        Prodialog.show();
        constants.verifyURL = "http://"
                + IniFile.getIniString(userIni, "Login",
                "ebsAddress", constants.DefaultServerIp, (byte) 0)
                + ":"
                + IniFile.getIniString(userIni, "Login", "ebsPort",
                "8083", (byte) 0)
                + "/EBS/DIRServlet";
        GetRequest<String> okgo = null;
        if (count == 7) {
            String fileName = fileList.get(0).fileName;
            for (int i = 1; i < fileList.size(); i++) {
                fileName = fileName + "&fileName=" + fileList.get(i).fileName;
            }
            constants.verifyURL = constants.verifyURL + "?fileName=" + fileName;
            Map<String, String> params = new HashMap<String, String>();
            params.put("act", "delUserFile");
            params.put("type", "disk");
            params.put("path", rootPath1);
            params.put("account", IniFile.getIniString(this.userIni, "Login",
                    "Account", "", (byte) 0));
            okgo = OkGo.get(constants.verifyURL);
            okgo.params(params, false);
        } else if (count == 8) {
            okgo = OkGo.get(constants.verifyURL);
            okgo.params(reNameUserDir(fileList, newFilePath), false);
        } else if (count == 9) {
            String fileName = fileList.get(0).fileName;
            for (int i = 1; i < fileList.size(); i++) {
                fileName = fileName + "&fileName=" + fileList.get(i).fileName;
            }
            constants.verifyURL = constants.verifyURL + "?fileName=" + fileName;
            okgo = OkGo.get(constants.verifyURL);
            Map<String, String> params = new HashMap<String, String>();
            params.put("act", "delUserFile");
            params.put("type", "disk");
            params.put("path", "");
            params.put("account", IniFile.getIniString(this.userIni, "Login",
                    "Account", "", (byte) 0));
            okgo.params(params, false);
        }
        if (listAdapter.actionMode != null)
            listAdapter.actionMode.finish();
        okgo.tag(this)
                .cacheMode(CacheMode.NO_CACHE)
                .execute(new StringCallback()
                {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Prodialog.dismiss();
                        Toast.makeText(mActivity, response.body().toString(), Toast.LENGTH_SHORT).show();
                        if (response.body().toString().equalsIgnoreCase("重命名文件成功") || response.body().toString()
                                .equalsIgnoreCase("删除文件成功")) {
                            showModifyDialog(1, rootPath1);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Prodialog.dismiss();
                        if (response == null)
                            Toast.makeText(mActivity, "操作失败", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(mActivity, response.body().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getFileData(final String filePath) {
        Prodialog.show();
        constants.verifyURL = "http://"
                + IniFile.getIniString(userIni, "Login",
                "ebsAddress", constants.DefaultServerIp, (byte) 0)
                + ":"
                + IniFile.getIniString(userIni, "Login", "ebsPort",
                "8083", (byte) 0)
                + "/EBS/DIRServlet";
        OkGo.<String>get(constants.verifyURL)//
                .tag(this)
                .params(getUserFileList(filePath), false)
                .cacheKey("TabFragment_DiskManage")       //由于该fragment会被复用,必须保证key唯一,否则数据会发生覆盖
                .cacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)  //缓存模式先使用缓存,然后使用网络数据
                .execute(new StringCallback()
                {
                    @Override
                    public void onSuccess(Response<String> response) {
                        ArrayList<FileInfo> list = parseJson(response.body().toString(), filePath);
                        if (list != null) {
                            rootPath1 = filePath;
                            tvCurPath.setText(rootPath + rootPath1);
                            addPath(rootPath1 + "/");
                            path_up_level.setClickable(true);
                        }
                        data = list != null ? list : new ArrayList<FileInfo>();
                        if (data.size() > 0)
                            showEmptyView(false);
                        else
                            showEmptyView(true);
                        refleshListView(data, 1);
                        Prodialog.dismiss();
                    }

                    @Override
                    public void onCacheSuccess(Response<String> response) {
                        //一般来说,只需呀第一次初始化界面的时候需要使用缓存刷新界面,以后不需要,所以用一个变量标识
                        if (!isInitCache) {
                            //一般来说,缓存回调成功和网络回调成功做的事情是一样的,所以这里直接回调onSuccess
                            onSuccess(response);
                            isInitCache = true;
                        }
                    }

                    @Override
        public void onError(Response<String> response) {
            Prodialog.dismiss();
            super.onError(response);
            if (response == null)
                Toast.makeText(mActivity, "获取列表失败", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(mActivity, response.body().toString(), Toast.LENGTH_SHORT).show();
        }
    });
    }
    private void fileTemp(final FileInfo filePath) {
        Prodialog.show();
        String webRoot = UIHelper.getStoragePath(mActivity);
        webRoot = webRoot + constants.SD_CARD_TEMP_PATH3;
        File file = new File(webRoot);
        if (!file.exists())
            file.mkdirs();
        constants.verifyURL = "http://"
                + IniFile.getIniString(userIni, "Login",
                "ebsAddress", constants.DefaultServerIp, (byte) 0)
                + ":"
                + IniFile.getIniString(userIni, "Login", "ebsPort",
                "8083", (byte) 0)
                + "/EBS/DIRServlet";
        final String path = webRoot;
        OkGo.<File>get(constants.verifyURL)//
                .tag(this)
                .params(getUserFileTemp(filePath.fileName), false)
                .cacheKey("TabFragment_DiskManage")       //由于该fragment会被复用,必须保证key唯一,否则数据会发生覆盖
                .cacheMode(CacheMode.NO_CACHE)  //缓存模式先使用缓存,然后使用网络数据
                .execute(new FileCallback(path, "temp.zip")
                {

                    @Override
                    public void onSuccess(Response<File> response) {
                        JTbszlib.UnZipFile(path+"/temp.zip",path, 1, "");
                        FileUtils.deleteFileWithPath(path+"/temp.zip");
                        tempFileInfo = filePath;
                        File file = new File(path + filePath.fileName);
                        //System.out.println(fileInfo.getModifyTime());
                        //System.out.println(file.setLastModified(fileInfo.getModifyTime()));
                        tempFileInfo.ModifiedDate = (file.lastModified());
                        IntentBuilder.viewFile(mActivity, path + filePath.fileName);
                        Prodialog.dismiss();
                    }

                    @Override
                    public void onError(Response<File> response) {
                        Prodialog.dismiss();
                        Toast.makeText(mActivity, "缓存出错，已取消", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void formUpload(final boolean isCover) {
        String zipPath = "";
        ArrayList<File> files = new ArrayList<>();
        for (FileInfo f : mCurFileNameList) {
            //System.out.println("filePath ="+f.filePath);
            if (isCover) {
                for (int j = 0; j < data.size(); j++) {
                    if (f.IsDir == data.get(j).IsDir) {
                        if (f.fileName.equalsIgnoreCase(data.get(j).fileName)) {
                            continue;
                        } else {
                            if (!f.IsDir) {
                                files.add(new File(f.filePath));
                            } else {
                                message.setText("正在压缩文件夹中");
                                zipPath = f.filePath.substring(0, f.filePath.lastIndexOf("/") + 1) + "upload" +
                                        ".zip";
                                long Openzip = JTbszlib.OpenZip(zipPath, 0);
                                JTbszlib.EnZipDir(f.filePath, Openzip, 1, 1, "", f.fileName);
                                JTbszlib.CloseZip(Openzip);
                            }
                        }
                    }
                }
            } else {
                if (!f.IsDir) {
                    files.add(new File(f.filePath));
                    //System.out.println(f.filePath);
                } else {
                    message.setText("正在压缩文件夹中");
                    zipPath = f.filePath.substring(0, f.filePath.lastIndexOf("/") + 1) + "upload.zip";
                    long Openzip = JTbszlib.OpenZip(zipPath, 0);
                    JTbszlib.EnZipDir(f.filePath, Openzip, 1, 1, "", f.fileName);
                    JTbszlib.CloseZip(Openzip);
                }
            }
        }
        files.add(new File(zipPath));
        constants.verifyURL = "http://"
                + IniFile.getIniString(userIni, "Login",
                "ebsAddress", constants.DefaultServerIp, (byte) 0)
                + ":"
                + IniFile.getIniString(userIni, "Login", "ebsPort",
                "8083", (byte) 0)
                + "/EBS/DIRServlet?act=uploadUserFile&type=disk&account=" + IniFile.getIniString(userIni, "Login",
                "Account", "", (byte) 0) + "&path=" + rootPath1 + "&isRelease=1";
        //拼接参数
        ModifyDialog.show();
        message.setText("正在上传中");
        final String finalZipPath = zipPath;
        OkGo.<String>post(constants.verifyURL)//
                .tag(this)//
                .isMultipart(true)
                .addFileParams("file", files)           // 这种方式为同一个key，上传多个文件
                .execute(new StringCallback()
                {
                    @Override
                    public void onSuccess(Response<String> response) {
                        FileUtils.deleteFileWithPath(finalZipPath);
                        Toast.makeText(mActivity, response.body().toString(), Toast.LENGTH_SHORT).show();
                        showModifyDialog(1, rootPath1);
                        ModifyDialog.dismiss();
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        FileUtils.deleteFileWithPath(finalZipPath);
                        Toast.makeText(mActivity, "上传失败", Toast.LENGTH_SHORT).show();
                        ModifyDialog.dismiss();
                    }

                    @Override
                    public void uploadProgress(Progress progress) {
                        NumberFormat numberFormat = NumberFormat.getPercentInstance();
                        numberFormat.setMinimumFractionDigits(2);
                        String downloadLength = Formatter.formatFileSize(mActivity, progress
                                .currentSize);
                        String totalLength = Formatter.formatFileSize(mActivity, progress
                                .totalSize);
                        num_completed.setText(downloadLength);
                        num_total.setText(totalLength);
                        String speed = Formatter.formatFileSize(mActivity, progress.speed);
                        speed_t.setText(String.format("%s/s", speed));
                        total_percent.setText(numberFormat.format(progress.fraction));
                        total_progress_bar.setMax(10000);
                        total_progress_bar.setProgress((int) (progress.fraction * 10000));
                    }
                });


    }

    // 解析json数据
    @SuppressWarnings("finally")
    private ArrayList<FileInfo> parseJson(String json, String filePath) {
        JSONObject jsonResult = null;
        ArrayList<FileInfo> list = null;
        try {
            jsonResult = new JSONObject(json);// 转换为JSONObject
            String response = jsonResult.getString("msg");
            if (response != null && response.equals("列出文件成功")) {
                list = new ArrayList<FileInfo>();
                JSONArray array = jsonResult.getJSONArray("files");
                if (array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        FileInfo disk = new FileInfo();
                        disk.createTime = object.getString("createTime");
                        disk.ModifiedDate = Date.parse(object.getString("createTime"));
                        disk.IsDir = object.getBoolean("directory");
                        disk.fileLength = object.getString("length");
                        disk.fileName = object.getString("name");
                        disk.filePath = filePath;
                        disk.isLoacal = false;
                        disk.canRead = object.getBoolean("readonly");
                        list.add(disk);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return list;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.isopen) {
            this.isopen = false;
            String webRoot = UIHelper.getStoragePath(this.mActivity);
            webRoot = webRoot + constants.SD_CARD_TEMP_PATH3 + this.tempFileInfo.fileName;
            File file = new File(webRoot);
            if (this.tempFileInfo.ModifiedDate < file.lastModified()) {
                this.showModifyDialog(6, this.tempFileInfo);
            } else {
                FileUtils.deleteFile(webRoot);
            }
        }

    }

    public boolean onBack() {
        return false;
    }
}

