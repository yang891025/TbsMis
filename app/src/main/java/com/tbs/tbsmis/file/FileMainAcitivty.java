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
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.tbs.tbsmis.R;

@SuppressLint("NewApi")
public class FileMainAcitivty extends Activity {
	private static final String INSTANCESTATE_TAB = "tab";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.fragment_page);
		ActionBar bar = this.getActionBar();
		bar.setDisplayShowTitleEnabled(false);
		bar.setDisplayShowHomeEnabled(false);
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE
				| ActionBar.DISPLAY_SHOW_HOME);
		bar.hide();
		bar.setSelectedNavigationItem(PreferenceManager
				.getDefaultSharedPreferences(this).getInt(FileMainAcitivty.INSTANCESTATE_TAB,
						Util.CATEGORY_TAB_INDEX));
		FileViewActivity mf = new FileViewActivity();
		FragmentTransaction action = getFragmentManager()
				.beginTransaction();
		//action.replace(R.id.pager, mf);
		action.commit();

	}

}