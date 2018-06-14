package com.tbs.tbsmis.file;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.tbs.tbsmis.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint({ "NewApi", "UseSparseArrays", "HandlerLeak" })
public class FileCategoryActivity extends Fragment implements
		IFileInteractionListener, FavoriteDatabaseHelper.FavoriteDatabaseListener,
		FileExplorerTabActivity.IBackPressedListener {

	public static final String EXT_FILETER_KEY = "ext_filter";

	private static final String LOG_TAG = "FileCategoryActivity";

	private static final HashMap<Integer, FileCategoryHelper.FileCategory> button2Category = new HashMap<Integer, FileCategoryHelper.FileCategory>();

	private final HashMap<FileCategoryHelper.FileCategory, Integer> categoryIndex = new HashMap<FileCategoryHelper.FileCategory, Integer>();

	private FileListCursorAdapter mAdapter;

	private FileViewInteractionHub mFileViewInteractionHub;

	private FileCategoryHelper mFileCagetoryHelper;

	private FileIconHelper mFileIconHelper;

	private CategoryBar mCategoryBar;

	private FileCategoryActivity.ScannerReceiver mScannerReceiver;

	private FavoriteList mFavoriteList;

	private FileCategoryActivity.ViewPage curViewPage = FileCategoryActivity.ViewPage.Invalid;

	private FileCategoryActivity.ViewPage preViewPage = FileCategoryActivity.ViewPage.Invalid;

	private Activity mActivity;

	private View mRootView;

	private FileViewActivity mFileViewActivity;

	private boolean mConfigurationChanged;

	public void setConfigurationChanged(boolean changed) {
        this.mConfigurationChanged = changed;
	}

	static {
        FileCategoryActivity.button2Category.put(R.id.category_music, FileCategoryHelper.FileCategory.Music);
        FileCategoryActivity.button2Category.put(R.id.category_video, FileCategoryHelper.FileCategory.Video);
        FileCategoryActivity.button2Category.put(R.id.category_picture, FileCategoryHelper.FileCategory.Picture);
        FileCategoryActivity.button2Category.put(R.id.category_theme, FileCategoryHelper.FileCategory.Theme);
        FileCategoryActivity.button2Category.put(R.id.category_document, FileCategoryHelper.FileCategory.Doc);
        FileCategoryActivity.button2Category.put(R.id.category_zip, FileCategoryHelper.FileCategory.Zip);
        FileCategoryActivity.button2Category.put(R.id.category_apk, FileCategoryHelper.FileCategory.Apk);
        FileCategoryActivity.button2Category.put(R.id.category_favorite, FileCategoryHelper.FileCategory.Favorite);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        this.mActivity = this.getActivity();
        this.mFileViewActivity = (FileViewActivity) ((FileExplorerTabActivity) this.mActivity)
				.getFragment(Util.SDCARD_TAB_INDEX);
        this.mRootView = inflater.inflate(R.layout.file_explorer_category,
				container, false);
        this.curViewPage = FileCategoryActivity.ViewPage.Invalid;
        this.mFileViewInteractionHub = new FileViewInteractionHub(this);
        this.mFileViewInteractionHub.setMode(FileViewInteractionHub.Mode.View);
        this.mFileViewInteractionHub.setRootPath("/");
        this.mFileIconHelper = new FileIconHelper(this.mActivity);
        this.mFavoriteList = new FavoriteList(this.mActivity,
				(ListView) this.mRootView.findViewById(R.id.favorite_list), this,
                this.mFileIconHelper);
        this.mFavoriteList.initList();
        this.mAdapter = new FileListCursorAdapter(this.mActivity, null,
                this.mFileViewInteractionHub, this.mFileIconHelper);

		ListView fileListView = (ListView) this.mRootView
				.findViewById(R.id.file_path_list);
		fileListView.setAdapter(this.mAdapter);

        this.setupClick();
        this.setupCategoryInfo();
        this.updateUI();
        this.registerScannerReceiver();

		return this.mRootView;
	}

	private void registerScannerReceiver() {
        this.mScannerReceiver = new FileCategoryActivity.ScannerReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.addDataScheme("file");
        this.mActivity.registerReceiver(this.mScannerReceiver, intentFilter);
	}

	private void setupCategoryInfo() {
        this.mFileCagetoryHelper = new FileCategoryHelper(this.mActivity);

        this.mCategoryBar = (CategoryBar) this.mRootView.findViewById(R.id.category_bar);
		int[] imgs = { R.drawable.category_bar_music,
                R.drawable.category_bar_video, R.drawable.category_bar_picture,
                R.drawable.category_bar_theme,
                R.drawable.category_bar_document, R.drawable.category_bar_zip,
                R.drawable.category_bar_apk, R.drawable.category_bar_other };

		for (int i = 0; i < imgs.length; i++) {
            this.mCategoryBar.addCategory(imgs[i]);
		}

		for (int i = 0; i < FileCategoryHelper.sCategories.length; i++) {
            this.categoryIndex.put(FileCategoryHelper.sCategories[i], i);
		}
	}

	public void refreshCategoryInfo() {
		Util.SDCardInfo sdCardInfo = Util.getSDCardInfo();
		if (sdCardInfo != null) {
            this.mCategoryBar.setFullValue(sdCardInfo.total);
            this.setTextView(
                    R.id.sd_card_capacity,
                    this.getString(R.string.sd_card_size,
							Util.convertStorage(sdCardInfo.total)));
            this.setTextView(
                    R.id.sd_card_available,
                    this.getString(R.string.sd_card_available,
							Util.convertStorage(sdCardInfo.free)));
		}

        this.mFileCagetoryHelper.refreshCategoryInfo();

		// the other category size should include those files didn't get
		// scanned.
		long size = 0;
		for (FileCategoryHelper.FileCategory fc : FileCategoryHelper.sCategories) {
			FileCategoryHelper.CategoryInfo categoryInfo = this.mFileCagetoryHelper.getCategoryInfos()
					.get(fc);
            this.setCategoryCount(fc, categoryInfo.count);

			// other category size should be set separately with calibration
			if (fc == FileCategoryHelper.FileCategory.Other)
				continue;

            this.setCategorySize(fc, categoryInfo.size);
            this.setCategoryBarValue(fc, categoryInfo.size);
			size += categoryInfo.size;
		}

		if (sdCardInfo != null) {
			long otherSize = sdCardInfo.total - sdCardInfo.free - size;
            this.setCategorySize(FileCategoryHelper.FileCategory.Other, otherSize);
            this.setCategoryBarValue(FileCategoryHelper.FileCategory.Other, otherSize);
		}

        this.setCategoryCount(FileCategoryHelper.FileCategory.Favorite, this.mFavoriteList.getCount());

		if (this.mCategoryBar.getVisibility() == View.VISIBLE) {
            this.mCategoryBar.startAnimation();
		}
	}

	public enum ViewPage {
		Home, Favorite, Category, NoSD, Invalid
	}

	private void showPage(FileCategoryActivity.ViewPage p) {
		if (this.curViewPage == p)
			return;

        this.curViewPage = p;

        this.showView(R.id.file_path_list, false);
        this.showView(R.id.navigation_bar, false);
        this.showView(R.id.category_page, false);
        this.showView(R.id.operation_bar, false);
        this.showView(R.id.sd_not_available_page, false);
        this.mFavoriteList.show(false);
        this.showEmptyView(false);

		switch (p) {
		case Home:
            this.showView(R.id.category_page, true);
			if (this.mConfigurationChanged) {
				((FileExplorerTabActivity) this.mActivity)
						.reInstantiateCategoryTab();
                this.mConfigurationChanged = false;
			}
			break;
		case Favorite:
            this.showView(R.id.navigation_bar, true);
            this.mFavoriteList.show(true);
            this.showEmptyView(this.mFavoriteList.getCount() == 0);
			break;
		case Category:
            this.showView(R.id.navigation_bar, true);
            this.showView(R.id.file_path_list, true);
            this.showEmptyView(this.mAdapter.getCount() == 0);
			break;
		case NoSD:
            this.showView(R.id.sd_not_available_page, true);
			break;
		}
	}

	private void showEmptyView(boolean show) {
		View emptyView = this.mActivity.findViewById(R.id.empty_view);
		if (emptyView != null)
			emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	private void showView(int id, boolean show) {
		View view = this.mRootView.findViewById(id);
		if (view != null) {
			view.setVisibility(show ? View.VISIBLE : View.GONE);
		}
	}

	OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			FileCategoryHelper.FileCategory f = FileCategoryActivity.button2Category.get(v.getId());
			if (f != null) {
                FileCategoryActivity.this.onCategorySelected(f);
				if (f != FileCategoryHelper.FileCategory.Favorite) {
                    FileCategoryActivity.this.setHasOptionsMenu(true);
				}
			}
		}

	};

	private void setCategoryCount(FileCategoryHelper.FileCategory fc, long count) {
		int id = FileCategoryActivity.getCategoryCountId(fc);
		if (id == 0)
			return;

        this.setTextView(id, "(" + count + ")");
	}

	private void setTextView(int id, String t) {
		TextView text = (TextView) this.mRootView.findViewById(id);
		text.setText(t);
	}

	private void onCategorySelected(FileCategoryHelper.FileCategory f) {
		if (this.mFileCagetoryHelper.getCurCategory() != f) {
            this.mFileCagetoryHelper.setCurCategory(f);
            this.mFileViewInteractionHub.setCurrentPath(this.mFileViewInteractionHub
					.getRootPath()
					+ this.getString(this.mFileCagetoryHelper.getCurCategoryNameResId()));
            this.mFileViewInteractionHub.refreshFileList();
		}

		if (f == FileCategoryHelper.FileCategory.Favorite) {
            this.showPage(FileCategoryActivity.ViewPage.Favorite);
		} else {
            this.showPage(FileCategoryActivity.ViewPage.Category);
		}
	}

	private void setupClick(int id) {
		View button = this.mRootView.findViewById(id);
		button.setOnClickListener(this.onClickListener);
	}

	private void setupClick() {
        this.setupClick(R.id.category_music);
        this.setupClick(R.id.category_video);
        this.setupClick(R.id.category_picture);
        this.setupClick(R.id.category_theme);
        this.setupClick(R.id.category_document);
        this.setupClick(R.id.category_zip);
        this.setupClick(R.id.category_apk);
        this.setupClick(R.id.category_favorite);
	}

	@Override
	public boolean onBack() {
		if (this.isHomePage() || this.curViewPage == FileCategoryActivity.ViewPage.NoSD
				|| this.mFileViewInteractionHub == null) {
			return false;
		}

		return this.mFileViewInteractionHub.onBackPressed();
	}

	public boolean isHomePage() {
		return this.curViewPage == FileCategoryActivity.ViewPage.Home;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (this.curViewPage != FileCategoryActivity.ViewPage.Category
				&& this.curViewPage != FileCategoryActivity.ViewPage.Favorite) {
			return;
		}
        this.mFileViewInteractionHub.onCreateOptionsMenu(menu);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		if (!this.isHomePage()
				&& this.mFileCagetoryHelper.getCurCategory() != FileCategoryHelper.FileCategory.Favorite) {
            this.mFileViewInteractionHub.onPrepareOptionsMenu(menu);
		}
	}

	@Override
	public boolean onRefreshFileList(String path, FileSortHelper sort) {
		FileCategoryHelper.FileCategory curCategory = this.mFileCagetoryHelper.getCurCategory();
		if (curCategory == FileCategoryHelper.FileCategory.Favorite
				|| curCategory == FileCategoryHelper.FileCategory.All)
			return false;

		Cursor c = this.mFileCagetoryHelper.query(curCategory, sort.getSortMethod());
        this.showEmptyView(c == null || c.getCount() == 0);
        this.mAdapter.changeCursor(c);

		return true;
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
        this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
                FileCategoryActivity.this.mAdapter.notifyDataSetChanged();
                FileCategoryActivity.this.mFavoriteList.getArrayAdapter().notifyDataSetChanged();
                FileCategoryActivity.this.showEmptyView(FileCategoryActivity.this.mAdapter.getCount() == 0);
			}

		});
	}

	@Override
	public void onPick(FileInfo f) {
		// do nothing
	}

	@Override
	public boolean shouldShowOperationPane() {
		return true;
	}

	@Override
	public boolean onOperation(int id) {
        this.mFileViewInteractionHub.addContextMenuSelectedItem();
		switch (id) {
		case R.id.button_operation_copy:
		case GlobalConsts.MENU_COPY:
            this.copyFileInFileView(this.mFileViewInteractionHub.getSelectedFileList());
            this.mFileViewInteractionHub.clearSelection();
			break;
		case R.id.button_operation_move:
		case GlobalConsts.MENU_MOVE:
            this.startMoveToFileView(this.mFileViewInteractionHub.getSelectedFileList());
            this.mFileViewInteractionHub.clearSelection();
			break;
		case GlobalConsts.OPERATION_UP_LEVEL:
            this.setHasOptionsMenu(false);
            this.showPage(FileCategoryActivity.ViewPage.Home);
			break;
		default:
			return false;
		}
		return true;
	}

	@Override
	public String getDisplayPath(String path) {
		return this.getString(R.string.tab_category) + path;
	}

	@Override
	public String getRealPath(String displayPath) {
		return "";
	}

	@Override
	public boolean onNavigation(String path) {
        this.showPage(FileCategoryActivity.ViewPage.Home);
		return true;
	}

	@Override
	public boolean shouldHideMenu(int menu) {
		return menu == GlobalConsts.MENU_NEW_FOLDER
				|| menu == GlobalConsts.MENU_FAVORITE
				|| menu == GlobalConsts.MENU_PASTE || menu == GlobalConsts.MENU_SHOWHIDE;
	}

	@Override
	public void addSingleFile(FileInfo file) {
        this.refreshList();
	}

	@Override
	public Collection<FileInfo> getAllFiles() {
		return this.mAdapter.getAllFiles();
	}

	@Override
	public FileInfo getItem(int pos) {
		return this.mAdapter.getFileItem(pos);
	}

	@Override
	public int getItemCount() {
		return this.mAdapter.getCount();
	}

	@Override
	public void sortCurrentList(FileSortHelper sort) {
        this.refreshList();
	}

	private void refreshList() {
        this.mFileViewInteractionHub.refreshFileList();
	}

	private void copyFileInFileView(ArrayList<FileInfo> files) {
		if (files.size() == 0)
			return;
        this.mFileViewActivity.copyFile(files);
        this.mActivity.getActionBar().setSelectedNavigationItem(
				Util.SDCARD_TAB_INDEX);
	}

	private void startMoveToFileView(ArrayList<FileInfo> files) {
		if (files.size() == 0)
			return;
        this.mFileViewActivity.moveToFile(files);
        this.mActivity.getActionBar().setSelectedNavigationItem(
				Util.SDCARD_TAB_INDEX);
	}

	@Override
	public FileIconHelper getFileIconHelper() {
		return this.mFileIconHelper;
	}

	private static int getCategoryCountId(FileCategoryHelper.FileCategory fc) {
		switch (fc) {
		case Music:
			return R.id.category_music_count;
		case Video:
			return R.id.category_video_count;
		case Picture:
			return R.id.category_picture_count;
		case Theme:
			return R.id.category_theme_count;
		case Doc:
			return R.id.category_document_count;
		case Zip:
			return R.id.category_zip_count;
		case Apk:
			return R.id.category_apk_count;
		case Favorite:
			return R.id.category_favorite_count;
		}

		return 0;
	}

	private void setCategorySize(FileCategoryHelper.FileCategory fc, long size) {
		int txtId = 0;
		int resId = 0;
		switch (fc) {
		case Music:
			txtId = R.id.category_legend_music;
			resId = R.string.category_music;
			break;
		case Video:
			txtId = R.id.category_legend_video;
			resId = R.string.category_video;
			break;
		case Picture:
			txtId = R.id.category_legend_picture;
			resId = R.string.category_picture;
			break;
		case Theme:
			txtId = R.id.category_legend_theme;
			resId = R.string.category_theme;
			break;
		case Doc:
			txtId = R.id.category_legend_document;
			resId = R.string.category_document;
			break;
		case Zip:
			txtId = R.id.category_legend_zip;
			resId = R.string.category_zip;
			break;
		case Apk:
			txtId = R.id.category_legend_apk;
			resId = R.string.category_apk;
			break;
		case Other:
			txtId = R.id.category_legend_other;
			resId = R.string.category_other;
			break;
		}

		if (txtId == 0 || resId == 0)
			return;

        this.setTextView(txtId, this.getString(resId) + ":" + Util.convertStorage(size));
	}

	private void setCategoryBarValue(FileCategoryHelper.FileCategory f, long size) {
		if (this.mCategoryBar == null) {
            this.mCategoryBar = (CategoryBar) this.mRootView
					.findViewById(R.id.category_bar);
		}
        this.mCategoryBar.setCategoryValue(this.categoryIndex.get(f), size);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (this.mActivity != null) {
            this.mActivity.unregisterReceiver(this.mScannerReceiver);
		}
	}

	private class ScannerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.v(FileCategoryActivity.LOG_TAG, "received broadcast: " + action);
			// handle intents related to external storage
			if (action.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)
					|| action.equals(Intent.ACTION_MEDIA_MOUNTED)
					|| action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                FileCategoryActivity.this.notifyFileChanged();
			}
		}
	}

	private void updateUI() {
		boolean sdCardReady = Util.isSDCardReady();
		if (sdCardReady) {
			if (this.preViewPage != FileCategoryActivity.ViewPage.Invalid) {
                this.showPage(this.preViewPage);
                this.preViewPage = FileCategoryActivity.ViewPage.Invalid;
			} else if (this.curViewPage == FileCategoryActivity.ViewPage.Invalid
					|| this.curViewPage == FileCategoryActivity.ViewPage.NoSD) {
                this.showPage(FileCategoryActivity.ViewPage.Home);
			}
            this.refreshCategoryInfo();
			// refresh file list
            this.mFileViewInteractionHub.refreshFileList();
			// refresh file list view in another tab
            this.mFileViewActivity.refresh();
		} else {
            this.preViewPage = this.curViewPage;
            this.showPage(FileCategoryActivity.ViewPage.NoSD);
		}
	}

	// process file changed notification, using a timer to avoid frequent
	// refreshing due to batch changing on file system
    public synchronized void notifyFileChanged() {
		if (this.timer != null) {
            this.timer.cancel();
		}
        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {

			@Override
			public void run() {
                FileCategoryActivity.this.timer = null;
				Message message = new Message();
				message.what = FileCategoryActivity.MSG_FILE_CHANGED_TIMER;
                FileCategoryActivity.this.handler.sendMessage(message);
			}

		}, 1000);
	}

	private static final int MSG_FILE_CHANGED_TIMER = 100;

	private Timer timer;

	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case FileCategoryActivity.MSG_FILE_CHANGED_TIMER:
                FileCategoryActivity.this.updateUI();
				break;
			}
			super.handleMessage(msg);
		}

	};

	// update the count of favorite
	@Override
	public void onFavoriteDatabaseChanged() {
        this.setCategoryCount(FileCategoryHelper.FileCategory.Favorite, this.mFavoriteList.getCount());
	}

	@Override
	public void runOnUiThread(Runnable r) {
        this.mActivity.runOnUiThread(r);
	}
}
