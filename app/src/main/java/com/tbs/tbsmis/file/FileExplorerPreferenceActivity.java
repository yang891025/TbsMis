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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.tbs.tbsmis.R;

import java.io.File;

/**
 * @author ShunLi
 */
public class FileExplorerPreferenceActivity extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private static final String PRIMARY_FOLDER = "pref_key_primary_folder";
    private static final String READ_ROOT = "pref_key_read_root";
    private static final String SHOW_REAL_PATH = "pref_key_show_real_path";
    private static final String SYSTEM_SEPARATOR = File.separator;

    private EditTextPreference mEditTextPreference;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.preferences);
        this.mEditTextPreference = (EditTextPreference) this.findPreference(FileExplorerPreferenceActivity
                .PRIMARY_FOLDER);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();

        // Setup the initial values
        SharedPreferences sharedPreferences = this.getPreferenceScreen()
                .getSharedPreferences();

        this.mEditTextPreference.setSummary(getString(
                R.string.pref_primary_folder_summary, sharedPreferences
                        .getString(FileExplorerPreferenceActivity.PRIMARY_FOLDER, GlobalConsts.ROOT_PATH)));

        // Set up a listener whenever a key changes
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onPause() {
        super.onPause();

        // Unregister the listener whenever a key changes
        this.getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedpreferences,
                                          String key) {
        if (FileExplorerPreferenceActivity.PRIMARY_FOLDER.equals(key)) {
            this.mEditTextPreference
                    .setSummary(getString(
                            R.string.pref_primary_folder_summary,
                            sharedpreferences.getString(FileExplorerPreferenceActivity.PRIMARY_FOLDER,
                                    GlobalConsts.ROOT_PATH)));
        }
    }

    public static String getPrimaryFolder(Context context) {
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        String primaryFolder = settings.getString(FileExplorerPreferenceActivity.PRIMARY_FOLDER, context
                .getString(R.string.default_primary_folder));
        if (TextUtils.isEmpty(primaryFolder)) { // setting primary folder =
            // empty("")
            primaryFolder = GlobalConsts.ROOT_PATH;
        }

        // it's remove the end char of the home folder setting when it with the
        // '/' at the end.
        // if has the backslash at end of the home folder, it's has minor bug at
        // "UpLevel" function.
        int length = primaryFolder.length();
        if (length > 1
                && FileExplorerPreferenceActivity.SYSTEM_SEPARATOR.equals(primaryFolder.substring(length - 1))) { //
            // length
            // =
            // 1,
            // ROOT_PATH
            return primaryFolder.substring(0, length - 1);
        } else {
            return primaryFolder;
        }
    }

    public static boolean isReadRoot(Context context) {
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);

        boolean isReadRootFromSetting = settings.getBoolean(FileExplorerPreferenceActivity.READ_ROOT, false);
        boolean isReadRootWhenSettingPrimaryFolderWithoutSdCardPrefix = !FileExplorerPreferenceActivity
                .getPrimaryFolder(
                        context).startsWith(Util.getSdDirectory());

        return isReadRootFromSetting
                || isReadRootWhenSettingPrimaryFolderWithoutSdCardPrefix;
    }

    public static boolean showRealPath(Context context) {
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        return settings.getBoolean(FileExplorerPreferenceActivity.SHOW_REAL_PATH, false);
    }

}
