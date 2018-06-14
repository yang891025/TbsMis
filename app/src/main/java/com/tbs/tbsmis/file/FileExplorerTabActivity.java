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
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.activity.DiskManagerActivity;
import com.tbs.tbsmis.activity.FtsManagerActivity;
import com.tbs.tbsmis.check.DiskPopMenus;
import com.tbs.tbsmis.check.PopMenus;
import com.tbs.tbsmis.constants.constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.app.ActionBar.NAVIGATION_MODE_TABS;

@SuppressLint("NewApi")
public class FileExplorerTabActivity extends FragmentActivity
{

    private static final String INSTANCESTATE_TAB = "tab";
    private static final int DEFAULT_OFFSCREEN_PAGES = 2;
    ViewPager mViewPager;
    FileExplorerTabActivity.TabsAdapter mTabsAdapter;
    ActionMode mActionMode;
    private boolean isAction;
    private LinearLayout layout_operationmenu;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        FileExplorerTabActivity.type = type;
    }

    private static String type;
    private PopMenus popupWindow_custommenu;
    private DiskPopMenus popDiskWindow_custommenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.fragment_pager);
        this.mViewPager = (ViewPager) this.findViewById(R.id.pager);
        this.mViewPager.setOffscreenPageLimit(FileExplorerTabActivity.DEFAULT_OFFSCREEN_PAGES);
        this.layout_operationmenu = (LinearLayout) this.findViewById(R.id.layout_operationmenu);
        ActionBar bar = this.getActionBar();
        if (bar != null) {
            bar.setNavigationMode(NAVIGATION_MODE_TABS);
            bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);
            this.mTabsAdapter = new FileExplorerTabActivity.TabsAdapter(this, this.mViewPager);
            if (getIntent().getExtras() != null) {
                setType("Fts");
                Intent intent = this.getIntent();
                Bundle bd = new Bundle();
                bd.putString("upload", intent.getStringExtra("upload"));
                this.mTabsAdapter.addTab(bar.newTab().setText(R.string.tab_network),
                        FtsManagerActivity.class, bd);
            } else {
                setType("Disk");
                this.mTabsAdapter.addTab(bar.newTab().setText(R.string.tab_disk),
                        DiskManagerActivity.class, null);
            }
            Bundle bd = new Bundle();
            bd.putString("type", type);
            this.mTabsAdapter.addTab(bar.newTab().setText(R.string.tab_sd),
                    FileViewActivity.class, bd);
            bar.setSelectedNavigationItem(PreferenceManager.getDefaultSharedPreferences(this)
                    .getInt(FileExplorerTabActivity.INSTANCESTATE_TAB, Util.CATEGORY_TAB_INDEX));
        }

        try {
            // IniCustomMenu(new JSONObject(jsonStr));
            this.setCustomMenu(new JSONObject(constants.firstmenu));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putInt(FileExplorerTabActivity.INSTANCESTATE_TAB, this.getActionBar().getSelectedNavigationIndex());
        editor.commit();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (this.getActionBar().getSelectedNavigationIndex() == Util.CATEGORY_TAB_INDEX) {
            FileCategoryActivity categoryFragement = (FileCategoryActivity) this.mTabsAdapter.getItem(Util
                    .CATEGORY_TAB_INDEX);
            if (categoryFragement.isHomePage()) {
                this.reInstantiateCategoryTab();
            } else {
                categoryFragement.setConfigurationChanged(true);
            }
        }
        super.onConfigurationChanged(newConfig);
    }

    public void reInstantiateCategoryTab() {
        this.mTabsAdapter.destroyItem(this.mViewPager, Util.CATEGORY_TAB_INDEX,
                this.mTabsAdapter.getItem(Util.CATEGORY_TAB_INDEX));
        this.mTabsAdapter.instantiateItem(this.mViewPager, Util.CATEGORY_TAB_INDEX);
    }

    @Override
    public void onBackPressed() {
        FileExplorerTabActivity.IBackPressedListener backPressedListener = (FileExplorerTabActivity
                .IBackPressedListener) this.mTabsAdapter
                .getItem(this.mViewPager.getCurrentItem());
        if (!backPressedListener.onBack()) {
            super.onBackPressed();
        }
    }

    public boolean isAction() {
        return this.isAction;
    }

    public void setIsAction(boolean isAction) {
        this.isAction = isAction;
    }

    public interface IBackPressedListener
    {
        /**
         * 处理back事件。
         *
         * @return True: 表示已经处理; False: 没有处理，让基类处理。
         */
        boolean onBack();
    }

    public void setActionMode(ActionMode actionMode) {
        this.mActionMode = actionMode;
    }

    public ActionMode getActionMode() {
        return this.mActionMode;
    }

    public Fragment getFragment(int tabIndex) {
        return this.mTabsAdapter.getItem(tabIndex);
    }

    /**
     * This is a helper class that implements the management of tabs and all
     * details of connecting a ViewPager with associated TabHost.  It relies on a
     * trick.  Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show.  This is not sufficient for switching
     * between pages.  So instead we make the content part of the tab host
     * 0dp high (it is not shown) and the TabsAdapter supplies its own dummy
     * view to show as the tab content.  It listens to changes in tabs, and takes
     * care of switch to the correct paged in the ViewPager whenever the selected
     * tab changes.
     */
    public class TabsAdapter extends FragmentPagerAdapter
            implements TabListener, OnPageChangeListener
    {
        private final Context mContext;
        private final ActionBar mActionBar;
        private final ViewPager mViewPager;
        private final ArrayList<FileExplorerTabActivity.TabsAdapter.TabInfo> mTabs = new
                ArrayList<FileExplorerTabActivity.TabsAdapter.TabInfo>();

        final class TabInfo
        {
            private final Class<?> clss;
            private final Bundle args;
            private Fragment fragment;

            TabInfo(Class<?> _class, Bundle _args) {
                this.clss = _class;
                this.args = _args;
            }
        }

        public TabsAdapter(FragmentActivity activity, ViewPager pager) {
            super(activity.getSupportFragmentManager());
            this.mContext = activity;
            this.mActionBar = activity.getActionBar();
            this.mViewPager = pager;
            this.mViewPager.setAdapter(this);
            this.mViewPager.setOnPageChangeListener(this);

        }

        public void addTab(Tab tab, Class<?> clss, Bundle args) {
            FileExplorerTabActivity.TabsAdapter.TabInfo info = new FileExplorerTabActivity.TabsAdapter.TabInfo(clss,
                    args);
            tab.setTag(info);
            tab.setTabListener(this);
            this.mTabs.add(info);
            this.mActionBar.addTab(tab);
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return this.mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            FileExplorerTabActivity.TabsAdapter.TabInfo info = this.mTabs.get(position);
            if (info.fragment == null) {
                info.fragment = Fragment.instantiate(this.mContext, info.clss.getName(), info.args);
            }
            return info.fragment;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            this.mActionBar.setSelectedNavigationItem(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            Object tag = tab.getTag();
            for (int i = 0; i < this.mTabs.size(); i++) {
                if (this.mTabs.get(i) == tag) {
                    this.mViewPager.setCurrentItem(i);
                }
            }
//            if (!tab.getText().equals(mContext.getString(R.string.tab_sd)))
//            {
            ActionMode actionMode = ((FileExplorerTabActivity) this.mContext).getActionMode();
            if (actionMode != null) {
                actionMode.finish();
            }
//            }
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        }
    }

    public void setCustomMenu(JSONObject jsonObject) throws JSONException {
        String jsonMenu = jsonObject.get("menu").toString();
        JSONArray jsonCustomMenu = new JSONObject(jsonMenu)
                .getJSONArray("button");
        if (jsonCustomMenu != null && jsonCustomMenu.length() > 0) {
            //layout_custommenu.setVisibility(View.VISIBLE);
            this.layout_operationmenu.removeAllViews();
            JSONArray btnJson = jsonCustomMenu;
            for (int i = 0; i < btnJson.length(); i++) {
                final JSONObject ob = btnJson.getJSONObject(i);
                RelativeLayout layout = (RelativeLayout) ((LayoutInflater) this.getSystemService(Context
                        .LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.item_file_menu, null);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
                        1.0f);
                layout.setLayoutParams(lp);
                TextView tv_custommenu_name = (TextView) layout
                        .findViewById(R.id.item_text);
                ImageView tv_custommenu_icon = (ImageView) layout
                        .findViewById(R.id.item_icon);
                String name = ob.getString("name");
                //type = "";
                tv_custommenu_name.setText(name);
//                if (ob.getJSONArray("sub_button").length() > 0) // 显示三角
//                {
//                    tv_custommenu_name.setCompoundDrawablesWithIntrinsicBounds(
//                            0, 0, R.drawable.ic_arrow_up_black, 0);
//                } else // 隐藏三角
                // {
                String key = "";
                try {
                    key = ob.getString("key");
                    // type = ob.getString("type");
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (key.equalsIgnoreCase("new")) {
                    tv_custommenu_icon.setImageResource(R.drawable.button_add);
                } else if (key.equalsIgnoreCase("refresh")) {
                    tv_custommenu_icon.setImageResource(R.drawable.button_refresh);
                } else if (key.equalsIgnoreCase("order")) {
                    tv_custommenu_icon.setImageResource(R.drawable.button_sort);
                } else if (key.equalsIgnoreCase("delete")) {
                    tv_custommenu_icon.setImageResource(R.drawable.button_delete);
                } else if (key.equalsIgnoreCase("copy")) {
                    tv_custommenu_icon.setImageResource(R.drawable.button_copy);
                } else if (key.equalsIgnoreCase("move")) {
                    tv_custommenu_icon.setImageResource(R.drawable.button_move);
                } else if (key.equalsIgnoreCase("more")) {
                    tv_custommenu_icon.setImageResource(R.drawable.button_menu);
                } else if (key.equalsIgnoreCase("share")) {
                    tv_custommenu_icon.setImageResource(R.drawable.button_send);
                } else if (key.equalsIgnoreCase("paste")) {
                    tv_custommenu_icon.setImageResource(R.drawable.button_copy);
                } else if (key.equalsIgnoreCase("search")) {
                    tv_custommenu_icon.setImageResource(R.drawable.button_search);
                } else if (key.equalsIgnoreCase("exit")) {
                    tv_custommenu_icon.setImageResource(R.drawable.button_exit);
                } else if (key.equalsIgnoreCase("pastecancle")) {
                    tv_custommenu_icon.setImageResource(R.drawable.button_cancel);
                }
                tv_custommenu_name.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, 0, 0);
                // }
                final String finalKey = key;
                layout.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        int i;
                        try {
                            i = ob.getJSONArray("sub_button").length();
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            i = 0;
                        }
                        //System.out.println("i = " + i);
                        if (i == 0) {
                            //System.out.println("key = " + finalKey);
                            //if (type.equalsIgnoreCase("button")) {
                            ActionBar bar = FileExplorerTabActivity.this.getActionBar();
                            if (bar.getSelectedNavigationIndex() != Util.SDCARD_TAB_INDEX) {
                                if (type.equals("Disk")) {
                                    if (finalKey.equalsIgnoreCase("new")) {
                                        ((DiskManagerActivity) FileExplorerTabActivity.this.getFragment(Util
                                                .CATEGORY_TAB_INDEX)).showNewDialog();
                                    } else if (finalKey.equalsIgnoreCase("refresh")) {
                                        ((DiskManagerActivity) FileExplorerTabActivity.this.getFragment(Util
                                                .CATEGORY_TAB_INDEX)).showModifyDialog
                                                (0, null);
                                    } else if (finalKey.equalsIgnoreCase("order")) {
                                        ((DiskManagerActivity) FileExplorerTabActivity.this.getFragment(Util
                                                .CATEGORY_TAB_INDEX)).showSortDialog();
                                    } else if (finalKey.equalsIgnoreCase("delete")) {
                                        ((DiskManagerActivity) FileExplorerTabActivity.this.getFragment(Util
                                                .CATEGORY_TAB_INDEX)).actionDelete();
                                    } else if (finalKey.equalsIgnoreCase("copy")) {
                                        ((DiskManagerActivity) FileExplorerTabActivity.this.getFragment(Util
                                                .CATEGORY_TAB_INDEX)).actionCopy();
                                    } else if (finalKey.equalsIgnoreCase("move")) {
                                        ((DiskManagerActivity) FileExplorerTabActivity.this.getFragment(Util
                                                .CATEGORY_TAB_INDEX)).actionMove();
                                    } else if (finalKey.equalsIgnoreCase("pastecancle")) {
                                        ((DiskManagerActivity) FileExplorerTabActivity.this.getFragment(Util
                                                .CATEGORY_TAB_INDEX)).actionPaseCancle();
                                    } else if (finalKey.equalsIgnoreCase("paste")) {
                                        ((DiskManagerActivity) FileExplorerTabActivity.this.getFragment(Util
                                                .CATEGORY_TAB_INDEX)).actionPase();
                                    } else if (finalKey.equalsIgnoreCase("search")) {
                                    } else if (finalKey.equalsIgnoreCase("exit")) {
                                        FileExplorerTabActivity.this.finish();
                                    }
                                } else {
                                    if (finalKey.equalsIgnoreCase("new")) {
                                        ((FtsManagerActivity) FileExplorerTabActivity.this.getFragment(Util
                                                .CATEGORY_TAB_INDEX)).showNewDialog();
                                    } else if (finalKey.equalsIgnoreCase("refresh")) {
                                        ((FtsManagerActivity) FileExplorerTabActivity.this.getFragment(Util
                                                .CATEGORY_TAB_INDEX)).showModifyDialog
                                                (0, null);
                                    } else if (finalKey.equalsIgnoreCase("order")) {
                                        ((FtsManagerActivity) FileExplorerTabActivity.this.getFragment(Util
                                                .CATEGORY_TAB_INDEX)).showSortDialog();
                                    } else if (finalKey.equalsIgnoreCase("delete")) {
                                        ((FtsManagerActivity) FileExplorerTabActivity.this.getFragment(Util
                                                .CATEGORY_TAB_INDEX)).actionDelete();
                                    } else if (finalKey.equalsIgnoreCase("copy")) {
                                        ((FtsManagerActivity) FileExplorerTabActivity.this.getFragment(Util
                                                .CATEGORY_TAB_INDEX)).actionCopy();
                                    } else if (finalKey.equalsIgnoreCase("move")) {
                                        ((FtsManagerActivity) FileExplorerTabActivity.this.getFragment(Util
                                                .CATEGORY_TAB_INDEX)).actionMove();
                                    } else if (finalKey.equalsIgnoreCase("pastecancle")) {
                                        ((FtsManagerActivity) FileExplorerTabActivity.this.getFragment(Util
                                                .CATEGORY_TAB_INDEX)).actionPaseCancle();
                                    } else if (finalKey.equalsIgnoreCase("paste")) {
                                        ((FtsManagerActivity) FileExplorerTabActivity.this.getFragment(Util
                                                .CATEGORY_TAB_INDEX)).actionPase();
                                    } else if (finalKey.equalsIgnoreCase("search")) {
                                    } else if (finalKey.equalsIgnoreCase("exit")) {
                                        FileExplorerTabActivity.this.finish();
                                    }
                                }
                            } else {
                                if (finalKey.equalsIgnoreCase("new")) {
                                    ((FileViewActivity) FileExplorerTabActivity.this.getFragment(Util
                                            .SDCARD_TAB_INDEX)).add();
                                } else if (finalKey.equalsIgnoreCase("refresh")) {
                                    ((FileViewActivity) FileExplorerTabActivity.this.getFragment(Util
                                            .SDCARD_TAB_INDEX)).Referesh();
                                } else if (finalKey.equalsIgnoreCase("order")) {
                                    ((FileViewActivity) FileExplorerTabActivity.this.getFragment(Util
                                            .SDCARD_TAB_INDEX)).Sort();
                                } else if (finalKey.equalsIgnoreCase("delete")) {
                                    ((FileViewActivity) FileExplorerTabActivity.this.getFragment(Util
                                            .SDCARD_TAB_INDEX)).Delete();
                                } else if (finalKey.equalsIgnoreCase("copy")) {
                                    if (type.equals("Disk"))
                                        ((FileViewActivity) FileExplorerTabActivity.this.getFragment(Util
                                                .SDCARD_TAB_INDEX)).Diskcopy();
                                    else
                                        ((FileViewActivity) FileExplorerTabActivity.this.getFragment(Util
                                                .SDCARD_TAB_INDEX)).copy();
                                } else if (finalKey.equalsIgnoreCase("move")) {
                                    if (type.equals("Disk"))
                                        ((FileViewActivity) FileExplorerTabActivity.this.getFragment(Util
                                                .SDCARD_TAB_INDEX)).moveDiskFile();
                                    else
                                        ((FileViewActivity) FileExplorerTabActivity.this.getFragment(Util
                                                .SDCARD_TAB_INDEX)).moveFile();
                                } else if (finalKey.equalsIgnoreCase("pastecancle")) {
                                    ((FileViewActivity) FileExplorerTabActivity.this.getFragment(Util
                                            .SDCARD_TAB_INDEX)).PasteCancle();
                                } else if (finalKey.equalsIgnoreCase("paste")) {
                                    if (type.equals("Disk"))
                                        ((FileViewActivity) FileExplorerTabActivity.this.getFragment(Util
                                                .SDCARD_TAB_INDEX)).DiskPaste();
                                    else
                                        ((FileViewActivity) FileExplorerTabActivity.this.getFragment(Util
                                                .SDCARD_TAB_INDEX)).Paste();
                                } else if (finalKey.equalsIgnoreCase("share")) {
                                    ((FileViewActivity) FileExplorerTabActivity.this.getFragment(Util
                                            .SDCARD_TAB_INDEX)).Send();
                                } else if (finalKey.equalsIgnoreCase("exit")) {
                                    FileExplorerTabActivity.this.finish();
                                }
                            }
                        } else {
                            if (type.equals("Disk")) {
                                try {
                                    FileExplorerTabActivity.this.popDiskWindow_custommenu = new DiskPopMenus(null,
                                            FileExplorerTabActivity.this, ob
                                            .getJSONArray("sub_button"), v
                                            .getWidth() + 10, 0);
                                    FileExplorerTabActivity.this.popDiskWindow_custommenu.showAtLocation(v);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    FileExplorerTabActivity.this.popupWindow_custommenu = new PopMenus(null,
                                            FileExplorerTabActivity.this, ob
                                            .getJSONArray("sub_button"), v
                                            .getWidth() + 10, 0);
                                    FileExplorerTabActivity.this.popupWindow_custommenu.showAtLocation(v);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
                this.layout_operationmenu.addView(layout);
            }
        } else

        {
            this.layout_operationmenu.setVisibility(View.INVISIBLE);
        }
    }
}

