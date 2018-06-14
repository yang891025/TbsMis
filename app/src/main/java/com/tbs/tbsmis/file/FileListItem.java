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

import android.app.ActionBar;
import android.content.Context;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;

import org.json.JSONException;
import org.json.JSONObject;


public class FileListItem
{
    public static void setupFileListItemInfo(Context context, View view,
                                             FileInfo fileInfo, FileIconHelper fileIcon,
                                             FileViewInteractionHub fileViewInteractionHub) {

        // if in moving mode, show selected file always
        if (fileViewInteractionHub.isMoveState()) {
            fileInfo.Selected = fileViewInteractionHub.isFileSelected(fileInfo.filePath);
        }

        ImageView checkbox = (ImageView) view.findViewById(R.id.file_checkbox);
        if (fileViewInteractionHub.getMode() == FileViewInteractionHub.Mode.Pick) {
            checkbox.setVisibility(View.GONE);
        } else {
            checkbox.setVisibility(fileViewInteractionHub.canShowCheckBox() ? View.VISIBLE : View.GONE);
            checkbox.setImageResource(fileInfo.Selected ? R.drawable.btn_check_on_holo_light
                    : R.drawable.btn_check_off_holo_light);
            checkbox.setTag(fileInfo);
            view.setSelected(fileInfo.Selected);
        }

        Util.setText(view, R.id.file_name, fileInfo.fileName);
        Util.setText(view, R.id.file_count, fileInfo.IsDir ? "(" + fileInfo.Count + ")" : "");
        Util.setText(view, R.id.modified_time, Util.formatDateString(context, fileInfo.ModifiedDate));
        Util.setText(view, R.id.file_size, fileInfo.IsDir ? "" : Util.convertStorage(fileInfo.fileSize));

        ImageView lFileImage = (ImageView) view.findViewById(R.id.file_image);
        ImageView lFileImageFrame = (ImageView) view.findViewById(R.id.file_image_frame);

        if (fileInfo.IsDir) {
            lFileImageFrame.setVisibility(View.GONE);
            lFileImage.setImageResource(R.drawable.format_folder);
        } else {
            fileIcon.setIcon(fileInfo, lFileImage, lFileImageFrame);
        }
    }

    public static class FileItemOnClickListener implements View.OnClickListener
    {
        private final Context mContext;
        private final FileViewInteractionHub mFileViewInteractionHub;
        private ActionMode actionMode;

        public FileItemOnClickListener(Context context,
                                       FileViewInteractionHub fileViewInteractionHub) {
            this.mContext = context;
            this.mFileViewInteractionHub = fileViewInteractionHub;
        }

        @Override
        public void onClick(View v) {
            ImageView img = (ImageView) v.findViewById(R.id.file_checkbox);
            assert img != null && img.getTag() != null;

            FileInfo tag = (FileInfo) img.getTag();
            tag.Selected = !tag.Selected;
            if (this.mFileViewInteractionHub.onCheckItem(tag, v)) {
                img.setImageResource(tag.Selected ? R.drawable.btn_check_on_holo_light
                        : R.drawable.btn_check_off_holo_light);
            } else {
                tag.Selected = !tag.Selected;
            }
            this.actionMode = ((FileExplorerTabActivity) this.mContext).getActionMode();
            if (this.actionMode == null) {
                this.actionMode = ((FileExplorerTabActivity) this.mContext)
                        .startActionMode(new FileListItem.ModeCallback(this.mContext,
                                this.mFileViewInteractionHub));
                ((FileExplorerTabActivity) this.mContext).setActionMode(this.actionMode);
            } else {
                this.actionMode.invalidate();
            }

            Util.updateActionModeTitle(this.actionMode, this.mContext,
                    this.mFileViewInteractionHub.getSelectedFileList().size());
        }

    }

    public static class ModeCallback implements Callback
    {
        private Menu mMenu;
        private final Context mContext;
        private final FileViewInteractionHub mFileViewInteractionHub;

        private void initMenuItemSelectAllOrCancel() {
            boolean isSelectedAll = this.mFileViewInteractionHub.isSelectedAll();
//            mMenu.findItem(R.id.action_cancel).setVisible(isSelectedAll);
//            mMenu.findItem(R.id.action_select_all).setVisible(!isSelectedAll);
        }

        private void scrollToSDcardTab() {
            ActionBar bar = ((FileExplorerTabActivity) this.mContext).getActionBar();
            if (bar.getSelectedNavigationIndex() != Util.SDCARD_TAB_INDEX) {
                bar.setSelectedNavigationItem(Util.SDCARD_TAB_INDEX);
            }
        }

        public ModeCallback(Context context,
                            FileViewInteractionHub fileViewInteractionHub) {
            this.mContext = context;
            this.mFileViewInteractionHub = fileViewInteractionHub;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//            MenuInflater inflater = ((Activity) mContext).getMenuInflater();
//            mMenu = menu;
//            inflater.inflate(R.menu.operation_menu, mMenu);
//            initMenuItemSelectAllOrCancel();
            try {
                ((FileExplorerTabActivity) this.mContext).setCustomMenu(new JSONObject(constants.secondmenu));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
//            Util.updateActionModeTitle(mode, mContext, mFileViewInteractionHub
//                    .getSelectedFileList().size());
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//            mMenu.findItem(R.id.action_copy_path).setVisible(
//                    mFileViewInteractionHub.getSelectedFileList().size() == 1);
//            mMenu.findItem(R.id.action_cancel).setVisible(
//            		mFileViewInteractionHub.isSelected());
//            mMenu.findItem(R.id.action_select_all).setVisible(
//            		!mFileViewInteractionHub.isSelectedAll());
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.action_delete:
//                    mFileViewInteractionHub.onOperationDelete();
//                    mode.finish();
//                    break;
//                case R.id.action_copy:
//                    ((FtsManagerActivity) ((FileExplorerTabActivity) mContext)
//                            .getFragment(Util.CATEGORY_TAB_INDEX))
//                            .copyFile(mFileViewInteractionHub.getSelectedFileList(), 0);
//                    ((FileViewActivity) ((FileExplorerTabActivity) mContext)
//                            .getFragment(Util.SDCARD_TAB_INDEX))
//                            .copyFile(mFileViewInteractionHub.getSelectedFileList());
//
//                    mode.finish();
//                    scrollToSDcardTab();
//                    break;
//                case R.id.action_move:
//                    ((FtsManagerActivity) ((FileExplorerTabActivity) mContext)
//                            .getFragment(Util.CATEGORY_TAB_INDEX))
//                            .copyFile(mFileViewInteractionHub.getSelectedFileList(), 2);
//                    ((FileViewActivity) ((FileExplorerTabActivity) mContext)
//                            .getFragment(Util.SDCARD_TAB_INDEX))
//                            .moveToFile(mFileViewInteractionHub.getSelectedFileList());
//                    mode.finish();
//                    scrollToSDcardTab();
//                    break;
//                case R.id.action_send:
//                    mFileViewInteractionHub.onOperationSend();
//                    mode.finish();
//                    break;
//                case R.id.action_copy_path:
//                    mFileViewInteractionHub.onOperationCopyPath();
//                    mode.finish();
//                    break;
//                case R.id.action_cancel:
//                    mFileViewInteractionHub.clearSelection();
//                    initMenuItemSelectAllOrCancel();
//                    mode.finish();
//                    break;
//                case R.id.action_select_all:
//                    mFileViewInteractionHub.onOperationSelectAll();
//                    initMenuItemSelectAllOrCancel();
//                    break;
//            }
//            Util.updateActionModeTitle(mode, mContext, mFileViewInteractionHub
//                    .getSelectedFileList().size());
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            this.mFileViewInteractionHub.clearSelection();
            try {
                if (((FileExplorerTabActivity) this.mContext).isAction())
                    ((FileExplorerTabActivity) this.mContext).setCustomMenu(new JSONObject(constants.thirdmenu));
                else
                    ((FileExplorerTabActivity) this.mContext).setCustomMenu(new JSONObject(constants.firstmenu));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            ((FileExplorerTabActivity) this.mContext).setActionMode(null);

        }
    }
}
