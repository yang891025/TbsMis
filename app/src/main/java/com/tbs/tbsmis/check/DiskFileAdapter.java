package com.tbs.tbsmis.check;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.os.Build;
import android.view.ActionMode;
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

import com.tbs.fts.FtClient;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.activity.DiskManagerActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.file.FileExplorerTabActivity;
import com.tbs.tbsmis.file.FileIconHelper;
import com.tbs.tbsmis.file.FileInfo;
import com.tbs.tbsmis.file.FileViewActivity;
import com.tbs.tbsmis.file.Util;
import com.tbs.tbsmis.util.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by TBS on 2017/8/11.
 */

public class DiskFileAdapter extends BaseAdapter
{
    private FtClient ftsClient;
    private List<FileInfo> Filelist;
    private final Context context;
    private HashMap<Integer, Boolean> isSelected;
    private final LayoutInflater inflater;
    private final String rootPath;

    public ActionMode actionMode;

    //private FileIconHelper  fileIco;
    public DiskFileAdapter(List<FileInfo> Filelist, Context context, String rootPath) {
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
    public void initDate() {
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
                this.Filelist.get(i).ModifiedDate));
        if (!this.Filelist.get(i).IsDir) {
            //fileIco.setIcon(Filelist.get(i),holder.tvImage,);
            holder.tvImage.setImageResource(FileIconHelper.getFileIcon(FileUtils.getFileFormat(this.Filelist.get(i).fileName)));
            holder.size.setText(Filelist.get(i).fileLength);
        } else if (this.Filelist.get(i).IsDir) {
            holder.tvImage.setImageResource(R.drawable.format_folder);
            holder.size.setText(Filelist.get(i).fileLength);
            //holder.count.setText("文件夹");
            //holder.children_cb.setVisibility(View.GONE);
        } else {
            holder.tvImage.setImageResource(R.drawable.format_folder);
            //holder.children_cb.setVisibility(View.GONE);
            holder.size.setText(Filelist.get(i).fileLength);
        }
        holder.tv.setText(this.Filelist.get(i).fileName);
        // 根据isSelected来设置checkbox的选中状况
        holder.children_cb.setChecked(this.getIsSelected().get(i));
        holder.children_cb.setOnClickListener(new View.OnClickListener()
        {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View v) {
                if (DiskFileAdapter.this.isSelected.get(i)) {
                    DiskFileAdapter.this.isSelected.put(i, false);
                } else {
                    DiskFileAdapter.this.isSelected.put(i, true);
                }
                DiskFileAdapter.this.actionMode = ((FileExplorerTabActivity) DiskFileAdapter.this.context).getActionMode();
                if (DiskFileAdapter.this.actionMode == null) {
                    DiskFileAdapter.this.actionMode = ((FileExplorerTabActivity) DiskFileAdapter.this.context)
                            .startActionMode(new DiskFileAdapter.ModeCallback(DiskFileAdapter.this.context));
                    ((FileExplorerTabActivity) DiskFileAdapter.this.context).setActionMode(DiskFileAdapter.this.actionMode);
                } else {
                    DiskFileAdapter.this.actionMode.invalidate();
                }
                Util.updateActionModeTitle(DiskFileAdapter.this.actionMode, DiskFileAdapter.this.context,
                        DiskFileAdapter.this.getSelected().size());
                DiskFileAdapter.this.notifyDataSetChanged();
            }
        });
        return convertView;
    }

    public HashMap<Integer, Boolean> getIsSelected() {
        return this.isSelected;
    }

    public ArrayList<FileInfo> getSelected() {
        ArrayList<FileInfo> Checklist = new ArrayList<FileInfo>();
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

//    public void actionDelete() {
//        new AlertDialog.Builder(this.context)
//                .setMessage("确认删除？")
//                .setPositiveButton("确认", new DialogInterface.OnClickListener()
//                {
//                    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        DiskFileAdapter.this.connect(2, DiskFileAdapter.this.getSelected());
//                        if(DiskFileAdapter.this.actionMode != null)
//                            DiskFileAdapter.this.actionMode.finish();
//                        DiskFileAdapter.this.initDate();
//                        DiskFileAdapter.this.notifyDataSetChanged();
//                    }
//                }).setNeutralButton("取消", new DialogInterface.OnClickListener()
//        {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//            }
//        }).show();
//    }

    public void actionCopy() {
        ((FileViewActivity) ((FileExplorerTabActivity) this.context)
                .getFragment(Util.SDCARD_TAB_INDEX))
                .copyFile(getSelected());
        ((DiskManagerActivity) ((FileExplorerTabActivity) this.context)
                .getFragment(Util.CATEGORY_TAB_INDEX))
                .copyFile(getSelected(), 0);
        if(this.actionMode != null)
            this.actionMode.finish();
        this.initDate();
        //scrollToSDcardTab();
        this.notifyDataSetChanged();
    }

    public void actionMove() {

        ((FileViewActivity) ((FileExplorerTabActivity) this.context)
                .getFragment(Util.SDCARD_TAB_INDEX))
                .copyFile(getSelected());
        ((DiskManagerActivity) ((FileExplorerTabActivity) this.context)
                .getFragment(Util.CATEGORY_TAB_INDEX))
                .copyFile(getSelected(), 1);
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

    public void actionCancle() {
        if(this.actionMode != null)
            this.actionMode.finish();
        this.initDate();
        this.notifyDataSetChanged();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public class ModeCallback implements ActionMode.Callback
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
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
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
            DiskFileAdapter.this.notifyDataSetChanged();
            DiskFileAdapter.this.initDate();
            ((FileExplorerTabActivity) this.mContext).setActionMode(null);

        }
    }


    private int longToCompareInt(long result) {
        return result > 0 ? 1 : result < 0 ? -1 : 0;
    }
}

