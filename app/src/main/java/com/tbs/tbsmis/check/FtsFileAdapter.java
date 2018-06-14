package com.tbs.tbsmis.check;


import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Build.VERSION_CODES;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.fts.FileInfo;
import com.tbs.fts.FtClient;
import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.activity.FtsManagerActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.file.FileExplorerTabActivity;
import com.tbs.tbsmis.file.FileIconHelper;
import com.tbs.tbsmis.file.FileViewActivity;
import com.tbs.tbsmis.file.TextInputDialog;
import com.tbs.tbsmis.file.TextInputDialog.OnFinishListener;
import com.tbs.tbsmis.file.Util;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by TBS on 2015/12/17.
 */
public class FtsFileAdapter extends BaseAdapter
{
    private FtClient ftsClient;
    private List<FileInfo> Filelist;
    private final Context context;
    private HashMap<Integer, Boolean> isSelected;
    private final LayoutInflater inflater;
    private final String rootPath;
    private ActionMode actionMode;

    //private FileIconHelper  fileIco;
    public FtsFileAdapter(List<FileInfo> Filelist, Context context, String rootPath) {
        //this.fileIco = new FileIconHelper(context);
        this.Filelist = Filelist;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.isSelected = new HashMap<Integer, Boolean>();
        this.rootPath = rootPath;
        // 初始化数据
        this.initDate();
    }

    // 初始化isSelected的数据
    private void initDate() {
        for (int i = 0; i < this.Filelist.size(); i++) {
            this.getIsSelected().put(i, false);
        }
    }

    @Override
    public int getCount() {
        return this.Filelist.size();
    }

    @Override
    public Object getItem(int i) {
        return this.Filelist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {

            holder = new ViewHolder();
            // convertview
            convertView = this.inflater.inflate(R.layout.children_item, null);
            holder.tv = (TextView) convertView.findViewById(R.id.tvPath);
            holder.size = (TextView) convertView.findViewById(R.id.file_size);
            holder.count = (TextView) convertView.findViewById(R.id.file_count);
            holder.time = (TextView) convertView
                    .findViewById(R.id.modified_time);
            holder.tvImage = (ImageView) convertView.findViewById(R.id.tvImage);
            holder.children_cb = (CheckBox) convertView
                    .findViewById(R.id.children_cb);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.time.setText(Util.formatDateString(this.context,
                this.Filelist.get(i).getCreateTime()));
        if (!this.Filelist.get(i).isDirectory()) {
            //fileIco.setIcon(Filelist.get(i),holder.tvImage,);
            holder.tvImage.setImageResource(FileIconHelper.getFileIcon(FileUtils.getFileFormat(this.Filelist.get(i)
                    .getM_path())));
            holder.size.setText(Util.convertStorage(this.Filelist.get(i).getLength()));
        } else if (this.Filelist.get(i).isDirectory()) {
            holder.tvImage.setImageResource(R.drawable.format_folder);
            //holder.count.setText("文件夹");
            //holder.children_cb.setVisibility(View.GONE);
        } else {
            holder.tvImage.setImageResource(R.drawable.format_folder);
            //holder.children_cb.setVisibility(View.GONE);
        }
        holder.tv.setText(this.Filelist.get(i).getPath());
        // 根据isSelected来设置checkbox的选中状况
        holder.children_cb.setChecked(this.getIsSelected().get(i));
        holder.children_cb.setOnClickListener(new View.OnClickListener()
        {
            @TargetApi(VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View v) {
                if (FtsFileAdapter.this.isSelected.get(i)) {
                    FtsFileAdapter.this.isSelected.put(i, false);
                } else {
                    FtsFileAdapter.this.isSelected.put(i, true);
                }
                FtsFileAdapter.this.actionMode = ((FileExplorerTabActivity) FtsFileAdapter.this.context).getActionMode();
                if (FtsFileAdapter.this.actionMode == null) {
                    FtsFileAdapter.this.actionMode = ((FileExplorerTabActivity) FtsFileAdapter.this.context)
                            .startActionMode(new ModeCallback(FtsFileAdapter.this.context));
                    ((FileExplorerTabActivity) FtsFileAdapter.this.context).setActionMode(FtsFileAdapter.this.actionMode);
                } else {
                    FtsFileAdapter.this.actionMode.invalidate();
                }
                Util.updateActionModeTitle(FtsFileAdapter.this.actionMode, FtsFileAdapter.this.context,
                        FtsFileAdapter.this.getSelected().size());
                FtsFileAdapter.this.notifyDataSetChanged();
            }
        });
        return convertView;
    }

    public HashMap<Integer, Boolean> getIsSelected() {
        return this.isSelected;
    }

    public List<FileInfo> getSelected() {
        List<FileInfo> Checklist = new ArrayList<FileInfo>();
        for (int i = 0; i < this.Filelist.size(); i++) {
            if (this.isSelected.get(i)) {
                Checklist.add(this.Filelist.get(i));
            }
        }
        return Checklist;
    }

    public void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        this.isSelected = isSelected;
    }

    public static class ViewHolder
    {
        public TextView tv;
        public TextView count;
        public TextView size;
        public TextView time;
        public ImageView tvImage;
        public CheckBox children_cb;
    }

    public void actionDelete() {
        new Builder(this.context)
                .setMessage("确认删除？")
                .setPositiveButton("确认", new OnClickListener()
                {
                    @TargetApi(VERSION_CODES.HONEYCOMB)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FtsFileAdapter.this.connect(2, FtsFileAdapter.this.getSelected());
                        if(FtsFileAdapter.this.actionMode != null)
                            FtsFileAdapter.this.actionMode.finish();
                        FtsFileAdapter.this.initDate();
                        FtsFileAdapter.this.notifyDataSetChanged();
                    }
                }).setNeutralButton("取消", new OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    public void actionCopy() {
        ArrayList<com.tbs.tbsmis.file.FileInfo> files = new ArrayList<>();
        for (int i = 0; i < this.getSelected().size(); i++) {
            com.tbs.tbsmis.file.FileInfo file = new com.tbs.tbsmis.file.FileInfo();
            file.canRead = this.getSelected().get(i).isReadonly();
            file.ModifiedDate = this.getSelected().get(i).getModifyTime();
            file.fileName = this.getSelected().get(i).getPath();
            file.filePath = this.rootPath;
            file.IsDir = this.getSelected().get(i).isDirectory();
            file.fileSize = this.getSelected().get(i).getLength();
            file.Selected = true;
            file.isLoacal = false;
            files.add(file);
            //System.out.println(this.getSelected().get(i).getPath());
        }
        ((FileViewActivity) ((FileExplorerTabActivity) this.context)
                .getFragment(Util.SDCARD_TAB_INDEX))
                .copyFile(files);
        ((FtsManagerActivity) ((FileExplorerTabActivity) this.context)
                .getFragment(Util.CATEGORY_TAB_INDEX))
                .copyFile(files, 0);
        if(this.actionMode != null)
            this.actionMode.finish();
        this.initDate();
        //scrollToSDcardTab();
        this.notifyDataSetChanged();
    }

    public void actionMove() {
        ArrayList<com.tbs.tbsmis.file.FileInfo> movefiles = new ArrayList<>();
        for (int i = 0; i < this.getSelected().size(); i++) {
            com.tbs.tbsmis.file.FileInfo file = new com.tbs.tbsmis.file.FileInfo();
            file.canRead = this.getSelected().get(i).isReadonly();
            file.ModifiedDate = this.getSelected().get(i).getModifyTime();
            file.fileName = this.getSelected().get(i).getPath();
            file.filePath = this.rootPath;
            file.IsDir = this.getSelected().get(i).isDirectory();
            file.fileSize = this.getSelected().get(i).getLength();
            file.Selected = true;
            file.isLoacal = false;
            movefiles.add(file);
        }
        ((FileViewActivity) ((FileExplorerTabActivity) this.context)
                .getFragment(Util.SDCARD_TAB_INDEX))
                .copyFile(movefiles);
        ((FtsManagerActivity) ((FileExplorerTabActivity) this.context)
                .getFragment(Util.CATEGORY_TAB_INDEX))
                .copyFile(movefiles, 1);
        if(this.actionMode != null)
            this.actionMode.finish();
        this.initDate();
        this.notifyDataSetChanged();
    }

    public void actionSend() {
        Toast.makeText(this.context, "这是个分享按钮", Toast.LENGTH_SHORT).show();
        if(this.actionMode != null)
            this.actionMode.finish();
        this.initDate();
        this.notifyDataSetChanged();
    }

    public void actionRename() {
        this.onOperationRename();
        if(this.actionMode != null)
            this.actionMode.finish();
        this.initDate();
    }

    public void actionCancle() {
        if(this.actionMode != null)
            this.actionMode.finish();
        this.initDate();
        this.notifyDataSetChanged();
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    public class ModeCallback implements Callback
    {
        private Menu mMenu;
        private final Context mContext;

//        private void initMenuItemSelectAllOrCancel()
//        {
//            //boolean isSelectedAll = mFileViewInteractionHub.isSelectedAll();
//            mMenu.findItem(R.id.action_cancel).setVisible(isSelectedAll);
//            mMenu.findItem(R.id.action_select_all).setVisible(!isSelectedAll);
//        }

        private void scrollToSDcardTab() {
            ActionBar bar = ((FileExplorerTabActivity) this.mContext).getActionBar();
            if (bar.getSelectedNavigationIndex() != Util.SDCARD_TAB_INDEX) {
                bar.setSelectedNavigationItem(Util.SDCARD_TAB_INDEX);
            }
        }

        public ModeCallback(Context context) {
            this.mContext = context;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//            MenuInflater inflater = ((Activity) mContext).getMenuInflater();
//            mMenu = menu;
//            inflater.inflate(R.menu.operation_dirve_menu, mMenu);
            //initMenuItemSelectAllOrCancel();

            try {
                ((FileExplorerTabActivity) this.mContext).setCustomMenu(new JSONObject(constants.secondmenu));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//            mMenu.findItem(R.id.action_rename).setVisible(
//                    getSelected().size() == 1);
//            mMenu.findItem(R.id.action_cancel).setVisible(
//                    isSelected());
//            mMenu.findItem(R.id.action_select_all).setVisible(
//                    isSelectedAll());
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.action_delete:
//                    new AlertDialog.Builder(context)
//                            .setMessage("确认删除？")
//                            .setPositiveButton("确认", new DialogInterface.OnClickListener()
//                            {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    connect(2, getSelected());
//                                    mode.finish();
//                                    initDate();
//                                    notifyDataSetChanged();
//                                }
//                            }).setNeutralButton("取消", new DialogInterface.OnClickListener()
//                    {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            dialogInterface.dismiss();
//                        }
//                    }).show();
//                    break;
//                case R.id.action_copy:
//                    //Toast.makeText(mContext, "这是个复制按钮", Toast.LENGTH_SHORT).show();
//                    ArrayList<com.tbs.tbsmis.file.FileInfo> files = new ArrayList<>();
//                    for (int i = 0; i < getSelected().size(); i++) {
//                        com.tbs.tbsmis.file.FileInfo file = new com.tbs.tbsmis.file.FileInfo();
//                        file.canRead = getSelected().get(i).isReadonly();
//                        file.ModifiedDate = getSelected().get(i).getModifyTime();
//                        file.fileName = getSelected().get(i).getPath();
//                        file.filePath = rootPath;
//                        file.IsDir = getSelected().get(i).isDirectory();
//                        file.fileSize = getSelected().get(i).getLength();
//                        file.Selected = true;
//                        file.isLoacal = false;
//                        files.add(file);
//                    }
//                    ((FileViewActivity) ((FileExplorerTabActivity) mContext)
//                            .getFragment(Util.SDCARD_TAB_INDEX))
//                            .copyFile(files);
//                    ((FtsManagerActivity) ((FileExplorerTabActivity) mContext)
//                            .getFragment(Util.CATEGORY_TAB_INDEX))
//                            .copyFile(files, 0);
//                    mode.finish();
//                    initDate();
//                    //scrollToSDcardTab();
//                    notifyDataSetChanged();
//                    break;
//                case R.id.action_move:
//                    // Toast.makeText(mContext, "这是个剪切按钮", Toast.LENGTH_SHORT).show();
//                    ArrayList<com.tbs.tbsmis.file.FileInfo> movefiles = new ArrayList<>();
//                    for (int i = 0; i < getSelected().size(); i++) {
//                        com.tbs.tbsmis.file.FileInfo file = new com.tbs.tbsmis.file.FileInfo();
//                        file.canRead = getSelected().get(i).isReadonly();
//                        file.ModifiedDate = getSelected().get(i).getModifyTime();
//                        file.fileName = getSelected().get(i).getPath();
//                        file.filePath = rootPath;
//                        file.IsDir = getSelected().get(i).isDirectory();
//                        file.fileSize = getSelected().get(i).getLength();
//                        file.Selected = true;
//                        file.isLoacal = false;
//                        movefiles.add(file);
//                    }
//                    ((FileViewActivity) ((FileExplorerTabActivity) mContext)
//                            .getFragment(Util.SDCARD_TAB_INDEX))
//                            .copyFile(movefiles);
//                    ((FtsManagerActivity) ((FileExplorerTabActivity) mContext)
//                            .getFragment(Util.CATEGORY_TAB_INDEX))
//                            .copyFile(movefiles, 1);
//                    mode.finish();
//                    initDate();
//                    notifyDataSetChanged();
//                    break;
//                case R.id.action_send:
//                    Toast.makeText(mContext, "这是个分享按钮", Toast.LENGTH_SHORT).show();
//                    mode.finish();
//                    initDate();
//                    notifyDataSetChanged();
//                    break;
//                case R.id.action_copy_path:
//                    initDate();
//                    // mFileViewInteractionHub.onOperationCopyPath();
//                    mode.finish();
//                    notifyDataSetChanged();
//                    break;
//                case R.id.action_cancel:
//                    initDate();
//                    // Toast.makeText(mContext, "这是个取消按钮", Toast.LENGTH_SHORT).show();
////                    mFileViewInteractionHub.clearSelection();
//                    //initMenuItemSelectAllOrCancel();
//                    mode.finish();
//                    notifyDataSetChanged();
//                    break;
//                case R.id.action_rename:
//                    onOperationRename();
//                    mode.finish();
//                    break;
//            }
//            Util.updateActionModeTitle(mode, mContext, mFileViewInteractionHub
//                    .getSelectedFileList().size());
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

            try {
                if (((FileExplorerTabActivity) this.mContext).isAction())
                    ((FileExplorerTabActivity) this.mContext).setCustomMenu(new JSONObject(constants.thirdmenu));
                else
                ((FileExplorerTabActivity) this.mContext).setCustomMenu(new JSONObject(constants.firstmenu));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            FtsFileAdapter.this.notifyDataSetChanged();
            FtsFileAdapter.this.initDate();
            ((FileExplorerTabActivity) this.mContext).setActionMode(null);

        }
    }

    public void onOperationRename() {

        if (this.getSelected().size() == 0)
            return;

        final FileInfo f = this.getSelected().get(0);
        this.initDate();

        TextInputDialog dialog = new TextInputDialog(this.context,
                this.context.getString(R.string.operation_rename),
                this.context.getString(R.string.operation_rename_message),
                f.getPath(), new OnFinishListener()
        {
            @Override
            public boolean onFinish(String text) {
                if (TextUtils.isEmpty(text))
                    return false;
                FtsFileAdapter.this.connect(1, f.getPath(), text);
                return true;
            }

        });

        dialog.show();
    }

    private void connect(int count, List<FileInfo> files) {
        FtsFileAdapter.MyAsyncTask task = new FtsFileAdapter.MyAsyncTask(count, files, this.context);
        task.execute();
    }

    private void connect(int count, String CheckedFile, String newFile) {
        FtsFileAdapter.MyAsyncTask task = new FtsFileAdapter.MyAsyncTask(count, CheckedFile, newFile, this.context);
        task.execute();
    }

    class MyAsyncTask extends AsyncTask<Integer, Integer, String>
    {

        private final int count;
        private String newFile;
        private String FilePath;
        private final Context context;
        private ProgressDialog Prodialog;
        private List<FileInfo> files;
        private boolean reback = true;

        public MyAsyncTask(int count, List<FileInfo> files, Context context) {
            this.files = files;
            this.context = context;
            this.count = count;
        }

        public MyAsyncTask(int count, String CheckedFile, String newFile, Context context) {
            this.newFile = newFile;
            FilePath = CheckedFile;
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
                if (FtsFileAdapter.this.ftsClient == null) {
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
                    if(Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                            "LoginType", "0", (byte) 0)) == 1){
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
                    FtsFileAdapter.this.ftsClient = FtClient.createObject("fts://" + ftsAddress + ":" + ftsPort,
                            FtsFileAdapter.this.rootPath);

                    String LoginId = m_iniFileIO.getIniString(userIni, "Login",
                            "LoginId", "", (byte) 0);
                    FtsFileAdapter.this.ftsClient.setLoginID(LoginId,"");
                    FtsFileAdapter.this.ftsClient.connect();
                }
                if (this.count == 1) {
                    FtsFileAdapter.this.ftsClient.rename(this.FilePath, this.newFile);
                } else if (this.count == 2) {

                    for (FileInfo f : this.files) {
                        //System.out.println("Name = "+ f.getPath());
                        if (f.isDirectory()) {
                            this.reback = FtsFileAdapter.this.ftsClient.rmdir(f.getPath());
                        } else {
                            FtsFileAdapter.this.ftsClient.unlink(f.getPath(), false);
                        }
                    }
                }
                FtsFileAdapter.this.Filelist = FtsFileAdapter.this.ftsClient.list("*");
                if (FtsFileAdapter.this.Filelist == null) {
                    return "false";
                }
                int n = FtsFileAdapter.this.Filelist.size();
                for (int i = n - 1; i >= 0; i--) {
                    //System.out.println(data.get(i).getPath());
                    if (FtsFileAdapter.this.Filelist.get(i).getPath().equalsIgnoreCase(".")
                            || FtsFileAdapter.this.Filelist.get(i).getPath().equalsIgnoreCase("..")) {
                        FtsFileAdapter.this.Filelist.remove(i);
                    }
                }
                FtsFileAdapter.this.refleshListView(FtsFileAdapter.this.Filelist);
                //rootPath1 = ftsClient.getCwd();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            if (!this.reback)
                return "false";
            return "true";

        }

        /**
         * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
         * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
         */
        @Override
        protected void onPostExecute(String result) {

            if (FtsFileAdapter.this.Filelist != null) {
                if (this.count == 2) {
                    if (result.equalsIgnoreCase("false"))
                        Toast.makeText(this.context, "无法删除非空目录", Toast.LENGTH_SHORT).show();
                }

                FtsFileAdapter.this.notifyDataSetChanged();
            }
            this.Prodialog.dismiss();

        }

        // 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
        @Override
        protected void onPreExecute() {
            this.Prodialog = new ProgressDialog(this.context);
            this.Prodialog.setTitle("操作");
            this.Prodialog.setMessage("正在执行，请稍候...");
            this.Prodialog.setIndeterminate(false);
            this.Prodialog.setCanceledOnTouchOutside(false);
            this.Prodialog.show();
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

    /**
     * ����listView��ͼ
     *
     * @param data
     */
    private void refleshListView(List<FileInfo> data) {
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
                                FtsFileAdapter.this.context,
                                constants.SAVE_LOCALMSGNUM, "sort", 0);
                        if (order == 0) {
                            return object1.getPath().compareToIgnoreCase(object2.getPath());
                        } else if (order == 1) {
                            return FtsFileAdapter.this.longToCompareInt(object1.getLength() - object2.getLength());
                        } else if (order == 2) {
                            return FtsFileAdapter.this.longToCompareInt(object1.getModifyTime()
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
        }
    }

    private int longToCompareInt(long result) {
        return result > 0 ? 1 : result < 0 ? -1 : 0;
    }
}

