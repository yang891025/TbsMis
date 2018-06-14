package com.tbs.tbsmis.file;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.tbs.tbsmis.R;

import java.io.File;
import java.util.ArrayList;

public class FavoriteList implements FavoriteDatabaseHelper.FavoriteDatabaseListener {
	private static final String LOG_TAG = "FavoriteList";

	private final ArrayList<FavoriteItem> mFavoriteList = new ArrayList<FavoriteItem>();

	private final ArrayAdapter<FavoriteItem> mFavoriteListAdapter;

	private final FavoriteDatabaseHelper mFavoriteDatabase;

	private ListView mListView;

	private final FavoriteDatabaseHelper.FavoriteDatabaseListener mListener;

	private final Context mContext;

	public FavoriteList(Context context, ListView list,
			FavoriteDatabaseHelper.FavoriteDatabaseListener listener, FileIconHelper iconHelper) {
        this.mContext = context;

        this.mFavoriteDatabase = new FavoriteDatabaseHelper(context, this);
        this.mFavoriteListAdapter = new FavoriteListAdapter(context,
                R.layout.favorite_item, this.mFavoriteList, iconHelper);
        this.setupFavoriteListView(list);
        this.mListener = listener;
	}

	public ArrayAdapter<FavoriteItem> getArrayAdapter() {
		return this.mFavoriteListAdapter;
	}

	public void update() {
        this.mFavoriteList.clear();

		Cursor c = this.mFavoriteDatabase.query();
		if (c != null) {
			while (c.moveToNext()) {
				FavoriteItem item = new FavoriteItem(c.getLong(0),
						c.getString(1), c.getString(2));
				item.fileInfo = Util.GetFileInfo(item.location);
                this.mFavoriteList.add(item);
			}
			c.close();
		}

		// remove not existing items
		if (Util.isSDCardReady()) {
			for (int i = this.mFavoriteList.size() - 1; i >= 0; i--) {
				File file = new File(this.mFavoriteList.get(i).location);
				if (file.exists())
					continue;

				FavoriteItem favorite = this.mFavoriteList.get(i);
                this.mFavoriteDatabase.delete(favorite.id, false);
                this.mFavoriteList.remove(i);
			}
		}

        this.mFavoriteListAdapter.notifyDataSetChanged();
	}

	public void initList() {
        this.mFavoriteList.clear();
		Cursor c = this.mFavoriteDatabase.query();
		if (c != null)
			c.close();

		if (this.mFavoriteDatabase.isFirstCreate()) {
			for (FavoriteItem fi : Util.getDefaultFavorites(this.mContext)) {
                this.mFavoriteDatabase.insert(fi.title, fi.location);
			}
		}

        this.update();
	}

	public long getCount() {
		return this.mFavoriteList.size();
	}

	public void show(boolean show) {
        this.mListView.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	private void setupFavoriteListView(ListView list) {
        this.mListView = list;
        this.mListView.setAdapter(this.mFavoriteListAdapter);
        this.mListView.setLongClickable(true);
        this.mListView.setOnCreateContextMenuListener(this.mListViewContextMenuListener);
        this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
                FavoriteList.this.onFavoriteListItemClick(parent, view, position, id);
			}
		});
	}

	public void onFavoriteListItemClick(AdapterView<?> parent, View view,
			int position, long id) {
		FavoriteItem favorite = this.mFavoriteList.get(position);

		if (favorite.fileInfo.IsDir) {
			FileExplorerTabActivity activity = (FileExplorerTabActivity) this.mContext;
			((FileViewActivity) activity.getFragment(Util.SDCARD_TAB_INDEX))
					.setPath(favorite.location);
			activity.getActionBar().setSelectedNavigationItem(
					Util.SDCARD_TAB_INDEX);
		} else {
			try {
				IntentBuilder.viewFile(this.mContext, favorite.fileInfo.filePath);
			} catch (ActivityNotFoundException e) {
				Log.e(FavoriteList.LOG_TAG, "fail to view file: " + e);
			}
		}
	}

	private static final int MENU_UNFAVORITE = 100;

	// context menu
	private final View.OnCreateContextMenuListener mListViewContextMenuListener = new View.OnCreateContextMenuListener() {
		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenu.ContextMenuInfo menuInfo) {
			menu.add(0, FavoriteList.MENU_UNFAVORITE, 0, R.string.operation_unfavorite)
					.setOnMenuItemClickListener(FavoriteList.this.menuItemClick);
		}
	};

	private final MenuItem.OnMenuItemClickListener menuItemClick = new MenuItem.OnMenuItemClickListener() {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			int itemId = item.getItemId();
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
					.getMenuInfo();
			int position = info != null ? info.position : -1;

			switch (itemId) {
			case FavoriteList.MENU_UNFAVORITE:
				if (position != -1) {
                    FavoriteList.this.deleteFavorite(position);
				}
				break;

			default:
				return false;
			}

			return true;
		}
	};

	private void deleteFavorite(int position) {
		FavoriteItem favorite = this.mFavoriteList.get(position);
        this.mFavoriteDatabase.delete(favorite.id, false);
        this.mFavoriteList.remove(position);
        this.mFavoriteListAdapter.notifyDataSetChanged();
        this.mListener.onFavoriteDatabaseChanged();
	}

	@Override
	public void onFavoriteDatabaseChanged() {
        this.update();
        this.mListener.onFavoriteDatabaseChanged();
	}
}
