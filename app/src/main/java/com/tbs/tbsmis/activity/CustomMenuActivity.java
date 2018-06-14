package com.tbs.tbsmis.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.check.DefaultMenuGridViewAdapter;
import com.tbs.tbsmis.check.DeleteCategoryAdapter;
import com.tbs.tbsmis.check.MenuGroupAdapter;
import com.tbs.tbsmis.check.OrderCategoryAdapter;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.headergridview.selectbuttonaction.StickyGridItem;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomMenuActivity extends ExpandableListActivity implements
        View.OnClickListener
{
    private ImageView finishBtn;
    private ImageView downBtn;
    private TextView title;
    private ExpandableListView exlist;
    private IniFile IniFile;
    private String userIni;
    private ArrayList<Map<String, String>> groups;
    private ArrayList<List<Map<String, String>>> childs;
    private ArrayList<StickyGridItem> default_menu_item;
    private int groupnum;
    private MenuGroupAdapter exlist_adapter;
    private boolean isOpenPop;
    private PopupWindow menuWindow;
    private RelativeLayout managerTitle;
    private AlertDialog ModifyDialog;
    private DeleteCategoryAdapter DeletemAdapter;
    private Map<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
    private DefaultMenuGridViewAdapter defaultMenuAdapter;
    private GridView default_menu_gridview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.menu_setup);
        MyActivity.getInstance().addActivity(this);
        this.init();
    }

    private void init() {
        // TODO Auto-generated method stub
        this.finishBtn = (ImageView) this.findViewById(R.id.more_btn);
        this.downBtn = (ImageView) this.findViewById(R.id.finish_btn);
        this.title = (TextView) this.findViewById(R.id.textView1);
        this.managerTitle = (RelativeLayout) this.findViewById(R.id.include_top);
        this.exlist = (ExpandableListView) this.findViewById(android.R.id.list);
        this.title.setText("自定义菜单");
        this.downBtn.setOnClickListener(this);
        this.finishBtn.setOnClickListener(this);
        this.initPath();
        this.initMenu();
        //this.exlist = (ExpandableListView) this.findViewById(android.R.id.list);
        @SuppressWarnings("deprecation")
        int width = this.getWindowManager().getDefaultDisplay().getWidth();
        this.exlist.setIndicatorBounds(width - 40, width - 20);
        // 构建expandablelistview的适配器

        // exlist.setGroupIndicator(null);
    }

    private void initMenu() {
        // TODO Auto-generated method stub
        this.groups = new ArrayList<Map<String, String>>();
        this.childs = new ArrayList<List<Map<String, String>>>();
        // 创建两个一级条目标题
        int resnum = Integer.parseInt(this.IniFile.getIniString(userIni,
                "MENU_ALL", "Count", "0", (byte) 0));
        for (int i = 1; i <= resnum; i++) {
            Map<String, String> group = new HashMap<String, String>();
            List<Map<String, String>> child = new ArrayList<Map<String, String>>();
            group.put("group", this.IniFile.getIniString(userIni, "MENU_ALL",
                    "Title" + i, "", (byte) 0));
            group.put("groupId", this.IniFile.getIniString(userIni, "MENU_ALL",
                    "ID" + i, "", (byte) 0));
            group.put("groupUrl", this.IniFile.getIniString(userIni, "MENU_ALL",
                    "Url" + i, "", (byte) 0));
            this.groups.add(group);
            String groupid = this.IniFile.getIniString(userIni, "MENU_ALL", "ID"
                    + i, "", (byte) 0);
            this.groupnum = Integer.parseInt(this.IniFile.getIniString(userIni,
                    groupid, "Count", "0", (byte) 0));
            for (int j = 1; j <= this.groupnum; j++) {
                Map<String, String> childdata = new HashMap<String, String>();
                childdata.put("Title", this.IniFile.getIniString(userIni,
                        groupid, "Title" + j, "", (byte) 0));
                childdata.put("Type", this.IniFile.getIniString(userIni, groupid,
                        "Type" + j, "", (byte) 0));
                childdata.put("Key", this.IniFile.getIniString(userIni, groupid,
                        "Key" + j, "", (byte) 0));
                childdata.put("Icon", this.IniFile.getIniString(userIni, groupid,
                        "Icon" + j, "0", (byte) 0));
                childdata.put("Url", this.IniFile.getIniString(userIni, groupid,
                        "Url" + j, "", (byte) 0));
                childdata.put("appid", this.IniFile.getIniString(userIni, groupid,
                        "appid" + j, "", (byte) 0));
                childdata.put("pagepath", this.IniFile.getIniString(userIni, groupid,
                        "pagepath" + j, "", (byte) 0));
                child.add(childdata);
            }
            this.childs.add(child);
        }
        this.exlist_adapter = new MenuGroupAdapter(this, this.groups, this.childs, userIni);
        this.exlist.setAdapter(this.exlist_adapter); // 绑定视图－适配器
    }

    private void initPath() {
        // TODO Auto-generated method stub
        this.IniFile = new IniFile();
        String webRoot = UIHelper.getSoftPath(this);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        webRoot += getString(R.string.SD_CARD_TBSAPP_PATH2);
        webRoot = UIHelper.getShareperference(this, constants.SAVE_INFORMATION,
                "Path", webRoot);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String appIniFile = webRoot
                + IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        userIni = appIniFile;
        if (Integer.parseInt(IniFile.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
    }

    public void changMenuPopState(View v) {
        this.isOpenPop = !this.isOpenPop;
        if (this.isOpenPop) {
            this.popWindow2(v);
        } else {
            if (this.menuWindow != null) {
                this.menuWindow.dismiss();
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void popWindow2(View parent) {
        LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = lay.inflate(R.layout.menumanager_menu, null);
        RelativeLayout add_category = (RelativeLayout) view
                .findViewById(R.id.add_menu);
        RelativeLayout delete_category = (RelativeLayout) view
                .findViewById(R.id.delete_menu);
        RelativeLayout order_category = (RelativeLayout) view
                .findViewById(R.id.order_menu);
        RelativeLayout edit_category = (RelativeLayout) view
                .findViewById(R.id.edit_menu);
        RelativeLayout add_menu_option = (RelativeLayout) view
                .findViewById(R.id.add_menu_option);
        add_menu_option.setOnClickListener(this);
        add_category.setOnClickListener(this);
        delete_category.setOnClickListener(this);
        order_category.setOnClickListener(this);
        edit_category.setOnClickListener(this);
        this.menuWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        //popwindow
        this.menuWindow.setFocusable(true);
        this.menuWindow.setOutsideTouchable(false);
        this.menuWindow.setBackgroundDrawable(new BitmapDrawable());
        this.menuWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
        {
            @Override
            public void onDismiss() {
                CustomMenuActivity.this.isOpenPop = false;
            }
        });
        this.menuWindow.showAtLocation(parent, Gravity.RIGHT | Gravity.TOP, 10,
                this.managerTitle.getHeight() * 3 / 2);
        this.menuWindow.update();
    }

    @Override
    public void onClick(View v) {
        this.initPath();
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.more_btn:
                this.finish();
                this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
                break;
            case R.id.finish_btn:
                this.changMenuPopState(v);
                break;
            case R.id.add_menu:
                this.menuWindow.dismiss();
                this.showNewMenuDialog();
                break;
            case R.id.edit_menu:
                this.menuWindow.dismiss();
                this.NewMenuOption(null, null, null,null,null, 1);
                break;
            case R.id.delete_menu:
                this.menuWindow.dismiss();
                this.showDeleteMenuDialog();
                break;
            case R.id.order_menu:
                this.menuWindow.dismiss();
                this.showOrderMenuDialog();
                break;
            case R.id.add_menu_option:
                this.menuWindow.dismiss();
                this.showDefaultMenuOptionDialog();
                break;
        }
    }

    private void showOrderMenuDialog() {
        // TODO Auto-generated method stub
        LayoutInflater factory = LayoutInflater.from(this);// 提示框
        View view = factory.inflate(R.layout.set_delete_category, null);// 这里必须是final的
        ListView categoryList = (ListView) view
                .findViewById(R.id.AppCategorylistItems);// 获得AppCategorylistItems对象

        OrderCategoryAdapter OrdermAdapter = new OrderCategoryAdapter(this.groups,
                this);
        categoryList.setAdapter(OrdermAdapter);
        Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("菜单调序").setCancelable(false)
                // 提示框标题
                .setView(view)
                .setPositiveButton("关闭", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CustomMenuActivity.this.initMenu();
                        CustomMenuActivity.this.exlist_adapter.notifyDataSetChanged();
                    }
                });
        this.ModifyDialog = builder.create();
        this.ModifyDialog.show();

    }

    /**
     * 设置哪个二级目录被默认选中
     */
    @Override
    public boolean setSelectedChild(int groupPosition, int childPosition,
                                    boolean shouldExpandGroup) {
        // do something
        return super.setSelectedChild(groupPosition, childPosition,
                shouldExpandGroup);
    }

    /**
     * 设置哪个一级目录被默认选中
     */
    @Override
    public void setSelectedGroup(int groupPosition) {
        // do something
        super.setSelectedGroup(groupPosition);
    }

    /**
     * 当二级条目被点击时响应
     */
    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
                                int groupPosition, int childPosition, long id) {
        // do something
        return super.onChildClick(parent, v, groupPosition, childPosition, id);
    }

    private void showDefaultMenuOptionDialog() {
        if (groups.size() > 0) {
            int section = 0;
            default_menu_item = new ArrayList<StickyGridItem>();
            String configPath = this.getApplicationContext().getFilesDir()
                    .getParentFile().getAbsolutePath();
            if (configPath.endsWith("/") == false) {
                configPath = configPath + "/";
            }
            String appIniFile = configPath + constants.USER_CONFIG_FILE_NAME;
            int groupnum = Integer.parseInt(this.IniFile.getIniString(appIniFile,
                    "menu_group", "Count", "0", (byte) 0));
            for (int i = 1; i <= groupnum; i++) {
                String Url = IniFile.getIniString(appIniFile,
                        "menu_group", "Url" + i, "", (byte) 0);
                String Title = IniFile.getIniString(appIniFile,
                        "menu_group", "Title" + i, "", (byte) 0);
                int itemnum = Integer.parseInt(this.IniFile.getIniString(appIniFile,
                        Url, "Count", "0", (byte) 0));
                if (itemnum >= 1)
                    section = section + 1;
                for (int j = 1; j <= itemnum; j++) {
                    String title = IniFile.getIniString(appIniFile,
                            Url, "Title" + j, "", (byte) 0);
                    String path = IniFile.getIniString(appIniFile, Url,
                            "Url" + j, "", (byte) 0);
                    String Section = IniFile.getIniString(appIniFile, Url,
                            "Section" + j, "", (byte) 0);
                    String Key = IniFile.getIniString(appIniFile, Url,
                            "Key" + j, "", (byte) 0);
                    String defaultV = IniFile.getIniString(appIniFile,
                            Url, "default" + j, "0", (byte) 0);
                    int show = Integer.parseInt(IniFile.getIniString(userIni,
                            Section, Key, defaultV, (byte) 0));
                    if (show == 1) {
                        StickyGridItem mGridItem = new StickyGridItem(path, title, section, Title);
                        mGridItem.setPic(IniFile.getIniString(appIniFile, Url,
                                "Icon" + j, "0", (byte) 0));
                        default_menu_item.add(mGridItem);
                    }
                }
            }
            section = section + 1;
            int resnum = Integer.parseInt(this.IniFile.getIniString(userIni,
                    "MENU_ALL", "Count", "0", (byte) 0));
            for (int i = 1; i <= resnum; i++) {
                String title = IniFile.getIniString(userIni, "MENU_ALL",
                        "Title" + i, "", (byte) 0);
                String path = IniFile.getIniString(userIni, "MENU_ALL",
                        "ID" + i, "", (byte) 0);
                StickyGridItem mGridItem = new StickyGridItem("tbs:" + path, title, section, "自定义菜单");
                mGridItem.setPic("0");
                default_menu_item.add(mGridItem);
            }
            section = section + 1;
            StickyGridItem mGridItem = new StickyGridItem("tbs:left_menu", "默认左菜单", section, "默认菜单");
            mGridItem.setPic("0");
            default_menu_item.add(mGridItem);
            mGridItem = new StickyGridItem("tbs:custom_menu", "默认右菜单", section, "默认菜单");
            default_menu_item.add(mGridItem);
            mGridItem.setPic("0");
            if (default_menu_item.size() > 0) {
                LayoutInflater factory = LayoutInflater.from(this);// 提示框
                View view = factory.inflate(R.layout.default_menu_layout, null);// 这里必须是final
                default_menu_gridview = (GridView) view.findViewById(R.id.gv_all);
                default_menu_gridview.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
                defaultMenuAdapter = new DefaultMenuGridViewAdapter(CustomMenuActivity.this, default_menu_item,
                        mSelectMap);
                default_menu_gridview.setAdapter(defaultMenuAdapter);
                default_menu_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        default_menu_gridview.setItemChecked(position, !(mSelectMap.get(position) == null ? false
                                : mSelectMap.get(position)));
                        //System.out.println("setOnItemClickListener =" + position);
                    }
                });
                default_menu_gridview.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener()
                {
                    @Override
                    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                        if (mSelectMap.get(position) == null) {
                            mSelectMap.put(position, checked);/* 放入选中的集合中 */
                        } else {
                            mSelectMap.remove(position);
                            if (checked)
                                mSelectMap.put(position, checked);
                        }
                        //System.out.println("position =" + position);
                        mode.invalidate();
                    }

                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return true;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        return true;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        defaultMenuAdapter.notifyDataSetChanged();
                    }
                });// 设置多选模式监听器

                new AlertDialog.Builder(this)
                        .setTitle("可选默认菜单项")
                        .setCancelable(false)
                        // 提示框标题
                        .setView(view)
                        .setPositiveButton("自定义",// 提示框的两个按钮
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        mSelectMap.clear();
                                        showNewMenuOptionDialog();
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        mSelectMap.clear();
                                        dialog.dismiss();
                                    }

                                })
                        .setNeutralButton("确定", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                NewMenuOption();
                            }
                        }).create().show();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("可选默认菜单项")
                        .setCancelable(false)
                        // 提示框标题
                        .setMessage("暂无默认菜单项？ 去自定义！！")
                        .setPositiveButton("自定义",// 提示框的两个按钮
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        showNewMenuOptionDialog();
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }

                                }).create().show();
            }
        } else {
            Toast.makeText(CustomMenuActivity.this,
                    "目前您还没有菜单，请先新建菜单", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void showNewMenuOptionDialog() {
        LayoutInflater factory = LayoutInflater.from(this);// 提示框
        View view = factory.inflate(R.layout.set_new_menu_option, null);// 这里必须是final的
        final EditText edit_name = (EditText) view
                .findViewById(R.id.edit_name_Text);// 获得输入框对象
        final EditText edit_web = (EditText) view
                .findViewById(R.id.edit_url_Text);
        final EditText edit_type = (EditText) view
                .findViewById(R.id.edit_type_Text);// 获得输入框对象
        final EditText edit_key = (EditText) view
                .findViewById(R.id.edit_key_Text);
        final EditText edit_appid = (EditText) view
                .findViewById(R.id.edit_appid_Text);
        final EditText edit_pagepath = (EditText) view
                .findViewById(R.id.edit_pagepath_Text);

        final LinearLayout small_layout = (LinearLayout) view
                .findViewById(R.id.small_layout);
        final RelativeLayout url_layout = (RelativeLayout) view
                .findViewById(R.id.url_layout);
        final RelativeLayout key_layout = (RelativeLayout) view
                .findViewById(R.id.key_layout);
        edit_name.setText("菜单项1");
        edit_key.setText("M100");
        edit_web.setText("http://e.tbs.com.cn/");
        final RadioButton url_check_box = (RadioButton) view.findViewById(R.id.radioBtnImg);
        final RadioButton key_check_box = (RadioButton) view.findViewById(R.id.radioBtnTxt);
        final RadioButton small_check_box = (RadioButton) view.findViewById(R.id.radioBtnSmall);
        url_check_box.setChecked(true);
        key_layout.setVisibility(View.GONE);
        url_check_box.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (url_check_box.isChecked()) {
                    url_layout.setVisibility(View.VISIBLE);
                    key_layout.setVisibility(View.GONE);
                    small_layout.setVisibility(View.GONE);
                }
            }
        });
        key_check_box.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (key_check_box.isChecked()) {
                    url_layout.setVisibility(View.GONE);
                    key_layout.setVisibility(View.VISIBLE);
                    small_layout.setVisibility(View.GONE);
                }
            }
        });
        small_check_box.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (key_check_box.isChecked()) {
                    url_layout.setVisibility(View.VISIBLE);
                    key_layout.setVisibility(View.GONE);
                    small_layout.setVisibility(View.VISIBLE);
                }
            }
        });
        new AlertDialog.Builder(this)
                .setTitle("新建菜单项")
                .setCancelable(false)
                // 提示框标题
                .setView(view)
                .setPositiveButton("新建",// 提示框的两个按钮
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                try {
                                    Field field = dialog.getClass()
                                            .getSuperclass()
                                            .getDeclaredField("mShowing");
                                    field.setAccessible(true);
                                    // 将mShowing变量设为false，表示对话框已关闭
                                    field.set(dialog, false);
                                    dialog.dismiss();
                                } catch (Exception e) {

                                }
                                if (url_check_box.isChecked()) {
                                    edit_type.setText("view");
                                } else if (key_check_box.isChecked()) {
                                    edit_type.setText("click");
                                } else if (small_check_box.isChecked()) {
                                    edit_type.setText("miniprogram");
                                }
                                if (StringUtils.isEmpty(edit_name.getText()
                                        .toString())) {
                                    Toast.makeText(CustomMenuActivity.this,
                                            "菜单名称不可为空", Toast.LENGTH_SHORT)
                                            .show();
                                    return;
                                } else if (StringUtils.isEmpty(edit_type
                                        .getText().toString())) {
                                    Toast.makeText(CustomMenuActivity.this,
                                            "菜单项类型不可为空", Toast.LENGTH_SHORT)
                                            .show();
                                    return;
                                } else if (edit_type.getText().toString()
                                        .equalsIgnoreCase("view")) {
                                    if (StringUtils.isEmpty(edit_web.getText()
                                            .toString())) {
                                        Toast.makeText(CustomMenuActivity.this,
                                                "菜单项链接不可为空", Toast.LENGTH_SHORT)
                                                .show();
                                        return;
                                    }
                                    CustomMenuActivity.this.NewMenuOption(edit_type.getText()
                                            .toString().toLowerCase(), edit_name.getText()
                                            .toString(), edit_web.getText()
                                            .toString(),"","",0);
                                } else if (edit_type.getText().toString()
                                        .equalsIgnoreCase("click")) {
                                    if (StringUtils.isEmpty(edit_key.getText()
                                            .toString())) {
                                        Toast.makeText(CustomMenuActivity.this,
                                                "菜单项键值不可为空", Toast.LENGTH_SHORT)
                                                .show();
                                        return;
                                    }
                                    CustomMenuActivity.this.NewMenuOption(edit_type.getText()
                                            .toString().toLowerCase(), edit_name.getText()
                                            .toString(), edit_key.getText()
                                            .toString(),"","",0);
                                }else if (edit_type.getText().toString()
                                        .equalsIgnoreCase("miniprogram")) {
                                    if (StringUtils.isEmpty(edit_appid.getText()
                                            .toString())) {
                                        Toast.makeText(CustomMenuActivity.this,
                                                "小程序ID不可为空", Toast.LENGTH_SHORT)
                                                .show();
                                        return;
                                    }if (StringUtils.isEmpty(edit_pagepath.getText()
                                            .toString())) {
                                        Toast.makeText(CustomMenuActivity.this,
                                                "小程序路径不可为空", Toast.LENGTH_SHORT)
                                                .show();
                                        return;
                                    }
                                    CustomMenuActivity.this.NewMenuOption(edit_type.getText()
                                            .toString().toLowerCase(), edit_name.getText()
                                            .toString(), edit_key.getText()
                                            .toString(),edit_appid.getText()
                                            .toString(), edit_pagepath.getText()
                                            .toString(),0);
                                }

                                try {
                                    Field field = dialog.getClass()
                                            .getSuperclass()
                                            .getDeclaredField("mShowing");
                                    field.setAccessible(true);
                                    // 将mShowing变量设为false，表示对话框已关闭
                                    field.set(dialog, true);
                                    dialog.dismiss();
                                } catch (Exception e) {

                                }
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                try {
                                    Field field = dialog.getClass()
                                            .getSuperclass()
                                            .getDeclaredField("mShowing");
                                    field.setAccessible(true);
                                    // 将mShowing变量设为false，表示对话框已关闭
                                    field.set(dialog, true);
                                    dialog.dismiss();
                                } catch (Exception e) {

                                }
                            }
                        }).create().show();
    }

    protected void NewMenuOption(final String edit_type,
                                 final String edit_name, final String edit_web,final String edit_appid,final String edit_apppath, final int flag) {
        // TODO Auto-generated method stub
        LayoutInflater factory = LayoutInflater.from(this);// 提示框
        View view = factory.inflate(R.layout.set_delete_category, null);// 这里必须是final的
        ListView categoryList = (ListView) view
                .findViewById(R.id.AppCategorylistItems);// 获得AppCategorylistItems对象
        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @SuppressLint("ShowToast")
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                DeleteCategoryAdapter.ViewHolder holder = (DeleteCategoryAdapter.ViewHolder) arg1.getTag();
                String apptext = (String) holder.tv.getText();
                int position = arg2 + 1;
                CustomMenuActivity.this.ModifyDialog.dismiss();
                if (flag == 0) {
                    CustomMenuActivity.this.AddMenuOptionDialog(apptext, edit_type, edit_name,
                            edit_web, edit_appid,edit_apppath,position);
                } else if (flag == 1) {
                    CustomMenuActivity.this.EditeMenuDialog(apptext, position);
                }

            }
        });
        this.DeletemAdapter = new DeleteCategoryAdapter(this.groups, this);
        categoryList.setAdapter(this.DeletemAdapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择菜单").setCancelable(false)// 提示框标题
                .setView(view).setPositiveButton("取消", null);
        this.ModifyDialog = builder.create();
        this.ModifyDialog.show();
    }


    protected void NewMenuOption() {
        // TODO Auto-generated method stub
        LayoutInflater factory = LayoutInflater.from(this);// 提示框
        View view = factory.inflate(R.layout.set_delete_category, null);// 这里必须是final的
        ListView categoryList = (ListView) view
                .findViewById(R.id.AppCategorylistItems);// 获得AppCategorylistItems对象
        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @SuppressLint("ShowToast")
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                DeleteCategoryAdapter.ViewHolder holder = (DeleteCategoryAdapter.ViewHolder) arg1.getTag();
                String apptext = (String) holder.tv.getText();
                int position = arg2 + 1;
                CustomMenuActivity.this.AddMenuOptionDialog(apptext, position);
                ModifyDialog.dismiss();
            }
        });
        this.DeletemAdapter = new DeleteCategoryAdapter(this.groups, this);
        categoryList.setAdapter(this.DeletemAdapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择菜单").setCancelable(false)// 提示框标题
                .setView(view).setPositiveButton("取消", null);
        this.ModifyDialog = builder.create();
        this.ModifyDialog.show();
    }

    protected void EditeMenuDialog(String apptext, final int position) {
        // TODO Auto-generated method stub
        LayoutInflater factory = LayoutInflater.from(this);// 提示框
        View view = factory.inflate(R.layout.set_new_menu, null);// 这里必须是final的
        final EditText edit_name = (EditText) view
                .findViewById(R.id.edit_name_Text);// 获得输入框对象
        final EditText edit_location = (EditText) view
                .findViewById(R.id.edit_location_Text);
        RelativeLayout edit_code = (RelativeLayout) view
                .findViewById(R.id.menu_code);
        final EditText edit_web = (EditText) view
                .findViewById(R.id.edit_url_Text);
        edit_code.setVisibility(View.GONE);
        final SeekBar SeekBar_X = (SeekBar) view.findViewById(R.id.SeekBar_X);
        final TextView show_x = (TextView) view.findViewById(R.id.show_x);
        final SeekBar SeekBar_Y = (SeekBar) view.findViewById(R.id.SeekBar_Y);
        final TextView show_y = (TextView) view.findViewById(R.id.show_y);
        Switch menu_defaults = (Switch) view.findViewById(R.id.menu_default);
        final String groupsName = this.IniFile.getIniString(userIni, "MENU_ALL",
                "ID" + position, "", (byte) 0);
        edit_name.setText(this.IniFile.getIniString(userIni, "MENU_ALL", "Title"
                + position, "", (byte) 0));
        edit_web.setText(this.IniFile.getIniString(userIni, "MENU_ALL", "Url"
                + position, "", (byte) 0));
        edit_location.setText(this.IniFile.getIniString(userIni, "MENU_ALL",
                "location" + position, "", (byte) 0));
        final RadioButton left_check_box = (RadioButton) view.findViewById(R.id.radioBtnImg);
        left_check_box.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(left_check_box.isChecked()){
                    SeekBar_X.setProgress(100);
                    show_x.setText("右边距：(100%)");
                    UIHelper.setSharePerference(CustomMenuActivity.this,
                            constants.SAVE_INFORMATION, "show_x", "100");
                }
            }
        });

        final RadioButton right_check_box = (RadioButton) view.findViewById(R.id.radioBtnTxt);
        right_check_box.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(right_check_box.isChecked()){
                    SeekBar_X.setProgress(0);
                    show_x.setText("右边距：(0%)");
                    UIHelper.setSharePerference(CustomMenuActivity.this,
                            constants.SAVE_INFORMATION, "show_x", "0");
                }
            }
        });

        int location_x = Integer.parseInt(this.IniFile.getIniString(userIni,
                groupsName, "location_x", "0", (byte) 0));
        SeekBar_X.setProgress(location_x);
        UIHelper.setSharePerference(this,
                constants.SAVE_INFORMATION, "show_x", this.IniFile.getIniString(
                        userIni, groupsName, "location_x", "0", (byte) 0));
        show_x.setText("右边距：("+location_x+"%)");
        if (this.IniFile.getIniString(userIni, "MENU_ALL",
                "location" + position, "", (byte) 0).equalsIgnoreCase("right_top")) {
            right_check_box.setChecked(true);

        } else if (this.IniFile.getIniString(userIni, "MENU_ALL",
                "location" + position, "", (byte) 0).equalsIgnoreCase("left_top")) {
            left_check_box.setChecked(true);
        }


        SeekBar_Y.setProgress(Integer.parseInt(this.IniFile.getIniString(userIni,
                groupsName, "location_y", "0", (byte) 0)));
        if (IniFile.getIniString(userIni, groupsName,
                "defaults", "false", (byte) 0).equalsIgnoreCase("true")) {
            menu_defaults.setChecked(true);
        } else {
            menu_defaults.setChecked(false);
        }

        UIHelper.setSharePerference(this,
                constants.SAVE_INFORMATION, "show_y", this.IniFile.getIniString(
                        userIni, groupsName, "location_y", "0", (byte) 0));

        show_y.setText("上边距：("
                + this.IniFile.getIniString(userIni, groupsName, "location_y",
                "0", (byte) 0) + "%)");
        SeekBar_X.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                show_x.setText("右边距：(" + progress + "%)");
                UIHelper.setSharePerference(CustomMenuActivity.this,
                        constants.SAVE_INFORMATION, "show_x", progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

        });
        SeekBar_Y.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                show_y.setText("上边距：(" + progress + "%)");
                UIHelper.setSharePerference(CustomMenuActivity.this,
                        constants.SAVE_INFORMATION, "show_y", progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

        });
        menu_defaults.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    UIHelper.setSharePerference(CustomMenuActivity.this,
                            constants.SAVE_INFORMATION, "defaults", true);
                } else {
                    UIHelper.setSharePerference(CustomMenuActivity.this,
                            constants.SAVE_INFORMATION, "defaults", false);
                }
            }
        });
        new AlertDialog.Builder(this)
                .setTitle("修改菜单")
                .setCancelable(false)
                // 提示框标题
                .setView(view)
                .setPositiveButton("保存",// 提示框的两个按钮
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                try {
                                    Field field = dialog.getClass()
                                            .getSuperclass()
                                            .getDeclaredField("mShowing");
                                    field.setAccessible(true);
                                    // 将mShowing变量设为false，表示对话框已关闭
                                    field.set(dialog, false);
                                    dialog.dismiss();
                                } catch (Exception e) {

                                }
                                if (left_check_box.isChecked()) {
                                    edit_location.setText("left_top");
                                } else if (right_check_box.isChecked()) {
                                    edit_location.setText("right_top");
                                }
                                if (StringUtils.isEmpty(edit_name.getText()
                                        .toString())) {
                                    Toast.makeText(CustomMenuActivity.this,
                                            "菜单名称不可为空", Toast.LENGTH_SHORT)
                                            .show();
                                    return;
                                } else {
                                    try {
                                        Field field = dialog.getClass()
                                                .getSuperclass()
                                                .getDeclaredField("mShowing");
                                        field.setAccessible(true);
                                        // 将mShowing变量设为false，表示对话框已关闭
                                        field.set(dialog, true);
                                        dialog.dismiss();
                                    } catch (Exception e) {

                                    }
                                }
                                CustomMenuActivity.this.SaveMenu(groupsName, edit_name.getText()
                                        .toString(), edit_location.getText()
                                        .toString(), edit_web.getText()
                                        .toString(), position);
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                try {
                                    Field field = dialog.getClass()
                                            .getSuperclass()
                                            .getDeclaredField("mShowing");
                                    field.setAccessible(true);
                                    // 将mShowing变量设为false，表示对话框已关闭
                                    field.set(dialog, true);
                                    dialog.dismiss();
                                } catch (Exception e) {

                                }

                            }
                        }).create().show();

    }

    protected void SaveMenu(String groupsName, String name, String location,
                            String web, int position) {
        // TODO Auto-generated method stub
        this.IniFile.writeIniString(userIni, "MENU_ALL", "Title" + position, name);
        this.IniFile.writeIniString(userIni, "MENU_ALL", "location" + position,
                location);
        this.IniFile.writeIniString(userIni, groupsName, "location_x", UIHelper
                .getShareperference(this,
                        constants.SAVE_INFORMATION, "show_x", "0"));
        this.IniFile.writeIniString(userIni, groupsName, "location_y", UIHelper
                .getShareperference(this,
                        constants.SAVE_INFORMATION, "show_y", "0"));
        this.IniFile.writeIniString(userIni, groupsName, "defaults", UIHelper
                .getShareperference(this,
                        constants.SAVE_INFORMATION, "defaults", false) + "");
        UIHelper.setSharePerference(this,
                constants.SAVE_INFORMATION, "defaults", false);
        UIHelper.setSharePerference(this,
                constants.SAVE_INFORMATION, "show_x", "0");
        UIHelper.setSharePerference(this,
                constants.SAVE_INFORMATION, "show_y", "0");
        this.IniFile.writeIniString(userIni, "MENU_ALL", "Url" + position, web);
        this.groups.get(position - 1).put("group", name);
        this.exlist_adapter.notifyDataSetChanged();
    }

    protected void AddMenuOptionDialog(String apptext, final String edit_type,
                                       final String edit_name, final String edit_web,final String edit_appid,final String edit_apppath, final int position) {
        // TODO Auto-generated method stub
        new AlertDialog.Builder(this).setTitle("添加菜单项")
                .setMessage("菜单：" + apptext + "\n" + "菜单项：" + edit_name)// 提示框标题
                .setPositiveButton("确定",// 提示框的两个按钮
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String groupsname = CustomMenuActivity.this.IniFile.getIniString(
                                        userIni, "MENU_ALL",
                                        "ID" + position, "0", (byte) 0);
                                int groupresnum = Integer.parseInt(CustomMenuActivity.this.IniFile
                                        .getIniString(userIni, groupsname,
                                                "Count", "0", (byte) 0));
                                for (int j = 1; j <= groupresnum; j++) {
                                    if (edit_name.equals(CustomMenuActivity.this.IniFile.getIniString(
                                            userIni, groupsname,
                                            "Title" + j, "", (byte) 0))) {
                                        Toast.makeText(CustomMenuActivity.this,
                                                "菜单项名称已存在,添加失败",
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                                Map<String, String> childdata = new HashMap<String, String>();
                                CustomMenuActivity.this.IniFile.writeIniString(userIni,
                                        groupsname,
                                        "Count", "" + (groupresnum + 1));
                                CustomMenuActivity.this.IniFile.writeIniString(userIni,
                                        groupsname,
                                        "Title" + (groupresnum + 1), edit_name);
                                CustomMenuActivity.this.IniFile.writeIniString(userIni,
                                        groupsname,
                                        "Type" + (groupresnum + 1), edit_type);
                                CustomMenuActivity.this.IniFile.writeIniString(userIni,
                                        groupsname,
                                        "Icon" + (groupresnum + 1), "0");
                                if (edit_type
                                        .equalsIgnoreCase("click")) {
                                    CustomMenuActivity.this.IniFile.writeIniString(userIni, groupsname,
                                            "Key" + (groupresnum + 1), edit_web);
                                    CustomMenuActivity.this.IniFile.writeIniString(userIni, groupsname,
                                            "Url" + (groupresnum + 1), "");
                                    childdata.put("Key", edit_web);
                                    childdata.put("Url", "");
                                } else if(edit_type
                                        .equalsIgnoreCase("miniprogram")){
                                    CustomMenuActivity.this.IniFile.writeIniString(userIni, groupsname,
                                            "Key" + (groupresnum + 1), "");
                                    CustomMenuActivity.this.IniFile.writeIniString(userIni, groupsname,
                                            "Url" + (groupresnum + 1), edit_web);
                                    CustomMenuActivity.this.IniFile.writeIniString(userIni, groupsname,
                                            "appid" + (groupresnum + 1), edit_appid);
                                    CustomMenuActivity.this.IniFile.writeIniString(userIni, groupsname,
                                            "pagepath" + (groupresnum + 1), edit_apppath);
                                    childdata.put("Key", "");
                                    childdata.put("Url", edit_web);
                                    childdata.put("appid", edit_web);
                                    childdata.put("pagepath", edit_web);
                                }
                                childdata.put("Title", edit_name);
                                childdata.put("Type", edit_type);
                                childdata.put("Icon", "0");
                                CustomMenuActivity.this.childs.get(position - 1).add(childdata);
                                CustomMenuActivity.this.exlist_adapter.notifyDataSetChanged();
                                Toast.makeText(CustomMenuActivity.this,
                                        "添加菜单项成功", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("取消", null).create().show();

    }

    protected void AddMenuOptionDialog(String apptext, final int position) {
        // TODO Auto-generated method stub
        new AlertDialog.Builder(this).setTitle("添加菜单项")
                .setMessage("菜单：" + apptext + "\n" + "菜单项：" + mSelectMap.size() + " 个")// 提示框标题
                .setPositiveButton("确定",// 提示框的两个按钮
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String groupsname = CustomMenuActivity.this.IniFile.getIniString(
                                        userIni, "MENU_ALL",
                                        "ID" + position, "0", (byte) 0);

                                for (Map.Entry<Integer, Boolean> entry : mSelectMap.entrySet()) {
                                    if (entry.getValue()) {
                                        int groupresnum = Integer.parseInt(CustomMenuActivity.this.IniFile
                                                .getIniString(userIni, groupsname,
                                                        "Count", "0", (byte) 0));
                                        Map<String, String> childdata = new HashMap<String, String>();
                                        CustomMenuActivity.this.IniFile.writeIniString(userIni,
                                                groupsname,
                                                "Count", "" + (groupresnum + 1));
                                        CustomMenuActivity.this.IniFile.writeIniString(userIni,
                                                groupsname,
                                                "Title" + (groupresnum + 1), default_menu_item.get(entry.getKey())
                                                        .getName());
                                        CustomMenuActivity.this.IniFile.writeIniString(userIni,
                                                groupsname,
                                                "Type" + (groupresnum + 1), "");
                                        CustomMenuActivity.this.IniFile.writeIniString(userIni,
                                                groupsname,
                                                "Icon" + (groupresnum + 1), default_menu_item.get(entry.getKey())
                                                        .getPic());
                                        CustomMenuActivity.this.IniFile.writeIniString(userIni, groupsname,
                                                "Key" + (groupresnum + 1), "");
                                        CustomMenuActivity.this.IniFile.writeIniString(userIni, groupsname,
                                                "Url" + (groupresnum + 1), default_menu_item.get(entry.getKey())
                                                        .getPath());
                                        childdata.put("Key", "");
                                        childdata.put("Url", default_menu_item.get(entry.getKey()).getPath());
                                        childdata.put("Title", default_menu_item.get(entry.getKey()).getName());
                                        childdata.put("Type", "");
                                        childdata.put("Icon", default_menu_item.get(entry.getKey()).getPic());
                                        CustomMenuActivity.this.childs.get(position - 1).add(childdata);
                                        CustomMenuActivity.this.exlist_adapter.notifyDataSetChanged();

                                        Toast.makeText(CustomMenuActivity.this,
                                                "添加菜单项成功", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                mSelectMap.clear();
                            }

                        }).setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mSelectMap.clear();
            }
        }).create().show();

    }

    private void showNewMenuDialog() {
        LayoutInflater factory = LayoutInflater.from(this);// 提示框
        View view = factory.inflate(R.layout.set_new_menu, null);// 这里必须是final的
        final EditText edit_name = (EditText) view
                .findViewById(R.id.edit_name_Text);// 获得输入框对象
        final EditText edit_location = (EditText) view
                .findViewById(R.id.edit_location_Text);
        final EditText edit_code = (EditText) view
                .findViewById(R.id.edit_code_Text);
        final EditText edit_web = (EditText) view
                .findViewById(R.id.edit_url_Text);
        edit_name.setText("新建菜单1");
        edit_code.setText("menu_new1");
        final RadioButton left_check_box = (RadioButton) view.findViewById(R.id.radioBtnImg);
        final RadioButton right_check_box = (RadioButton) view.findViewById(R.id.radioBtnTxt);
        right_check_box.setChecked(true);
        final SeekBar SeekBar_X = (SeekBar) view.findViewById(R.id.SeekBar_X);
        final TextView show_x = (TextView) view.findViewById(R.id.show_x);
        SeekBar SeekBar_Y = (SeekBar) view.findViewById(R.id.SeekBar_Y);
        show_x.setText("右边距：(0%)");
        final TextView show_y = (TextView) view.findViewById(R.id.show_y);
        show_y.setText("上边距：(0%)");
        left_check_box.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(left_check_box.isChecked()){
                    SeekBar_X.setProgress(100);
                    show_x.setText("右边距：(100%)");
                    UIHelper.setSharePerference(CustomMenuActivity.this,
                            constants.SAVE_INFORMATION, "show_x", "100");
                }
            }
        });

        right_check_box.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(right_check_box.isChecked()){
                    SeekBar_X.setProgress(0);
                    show_x.setText("右边距：(0%)");
                    UIHelper.setSharePerference(CustomMenuActivity.this,
                            constants.SAVE_INFORMATION, "show_x", "0");
                }
            }
        });
        SeekBar SeekBar_Color = (SeekBar) view.findViewById(R.id.SeekBar_Color);
        final LinearLayout color_show = (LinearLayout) view
                .findViewById(R.id.color_show);
        Switch menu_defaults = (Switch) view.findViewById(R.id.menu_default);
        menu_defaults.setChecked(false);
        SeekBar_X.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                show_x.setText("右边距：(" + progress + "%)");
                UIHelper.setSharePerference(CustomMenuActivity.this,
                        constants.SAVE_INFORMATION, "show_x", progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });
        SeekBar_Y.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                show_y.setText("上边距：(" + progress + "%)");
                UIHelper.setSharePerference(CustomMenuActivity.this,
                        constants.SAVE_INFORMATION, "show_y", progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });
        SeekBar_Color.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                color_show.setBackgroundColor(Color.argb(progress, progress,
                        progress, progress));
                // UIHelper.setSharePerference(CustomMenuActivity.this,
                // constants.SAVE_INFORMATION, "show_y", progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });
        menu_defaults.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    UIHelper.setSharePerference(CustomMenuActivity.this,
                            constants.SAVE_INFORMATION, "defaults", true);
                } else {
                    UIHelper.setSharePerference(CustomMenuActivity.this,
                            constants.SAVE_INFORMATION, "defaults", false);
                }
            }
        });
        new AlertDialog.Builder(this)
                .setTitle("新建菜单")
                .setCancelable(false)
                // 提示框标题
                .setView(view)
                .setPositiveButton("新建",// 提示框的两个按钮
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                try {
                                    Field field = dialog.getClass()
                                            .getSuperclass()
                                            .getDeclaredField("mShowing");
                                    field.setAccessible(true);
                                    // 将mShowing变量设为false，表示对话框已关闭
                                    field.set(dialog, false);
                                    dialog.dismiss();
                                } catch (Exception e) {

                                }
                                if (left_check_box.isChecked()) {
                                    edit_location.setText("left_top");
                                } else if (right_check_box.isChecked()) {
                                    edit_location.setText("right_top");
                                }
                                if (StringUtils.isEmpty(edit_name.getText()
                                        .toString())) {
                                    Toast.makeText(CustomMenuActivity.this,
                                            "菜单名称不可为空", Toast.LENGTH_SHORT)
                                            .show();
                                    return;
                                } else if (StringUtils.isEmpty(edit_code
                                        .getText().toString())) {
                                    Toast.makeText(CustomMenuActivity.this,
                                            "菜单代号不可为空", Toast.LENGTH_SHORT)
                                            .show();
                                    return;
                                } else {
                                    int resnum = Integer.parseInt(CustomMenuActivity.this.IniFile
                                            .getIniString(userIni,
                                                    "MENU_ALL", "Count", "0",
                                                    (byte) 0));
                                    for (int j = 1; j <= resnum; j++) {
                                        if (edit_code
                                                .getText()
                                                .toString()
                                                .equals(CustomMenuActivity.this.IniFile.getIniString(
                                                        userIni, "MENU_ALL",
                                                        "ID" + j, "", (byte) 0))) {
                                            Toast.makeText(
                                                    CustomMenuActivity.this,
                                                    "菜单代号已存在",
                                                    Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                    try {
                                        Field field = dialog.getClass()
                                                .getSuperclass()
                                                .getDeclaredField("mShowing");
                                        field.setAccessible(true);
                                        // 将mShowing变量设为false，表示对话框已关闭
                                        field.set(dialog, true);
                                        dialog.dismiss();
                                    } catch (Exception e) {

                                    }
                                }
                                CustomMenuActivity.this.NewMenu(edit_name.getText().toString(),
                                        edit_location.getText().toString(),
                                        edit_code.getText().toString(),
                                        edit_web.getText().toString());
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                try {
                                    Field field = dialog.getClass()
                                            .getSuperclass()
                                            .getDeclaredField("mShowing");
                                    field.setAccessible(true);
                                    // 将mShowing变量设为false，表示对话框已关闭
                                    field.set(dialog, true);
                                    dialog.dismiss();
                                } catch (Exception e) {

                                }
                            }
                        }).create().show();
    }

    protected void NewMenu(String name, String location, String code, String web) {
        // TODO Auto-generated method stub
        int resnum = Integer.parseInt(this.IniFile.getIniString(userIni,
                "MENU_ALL", "Count", "0", (byte) 0)) + 1;
        this.IniFile.writeIniString(userIni, "MENU_ALL", "Title" + resnum, name);
        this.IniFile.writeIniString(userIni, "MENU_ALL", "location" + resnum,
                location);
        this.IniFile.writeIniString(userIni, "MENU_ALL", "ID" + resnum, code);

        this.IniFile.writeIniString(userIni, code, "location_x", UIHelper
                .getShareperference(this,
                        constants.SAVE_INFORMATION, "show_x", "0"));
        this.IniFile.writeIniString(userIni, code, "defaults", UIHelper
                .getShareperference(this,
                        constants.SAVE_INFORMATION, "defaults", false) + "");
        UIHelper.setSharePerference(this,
                constants.SAVE_INFORMATION, "defaults", false);
        this.IniFile.writeIniString(userIni, code, "location_y", UIHelper
                .getShareperference(this,
                        constants.SAVE_INFORMATION, "show_y", "0"));

        UIHelper.setSharePerference(this,
                constants.SAVE_INFORMATION, "show_x", "0");
        UIHelper.setSharePerference(this,
                constants.SAVE_INFORMATION, "show_y", "0");
        this.IniFile.writeIniString(userIni, "MENU_ALL", "Url" + resnum, web);
        this.IniFile.writeIniString(userIni, "MENU_ALL", "Count", resnum + "");
        Map<String, String> group = new HashMap<String, String>();
        group.put("group", name);
        group.put("groupId", code);
        group.put("groupUrl", web);
        this.groups.add(group);
        List<Map<String, String>> child = new ArrayList<Map<String, String>>();
        this.childs.add(child);
        this.exlist_adapter.notifyDataSetChanged();
    }

    private void showDeleteMenuDialog() {
        LayoutInflater factory = LayoutInflater.from(this);// 提示框
        View view = factory.inflate(R.layout.set_delete_category, null);// 这里必须是final的
        ListView categoryList = (ListView) view
                .findViewById(R.id.AppCategorylistItems);// 获得AppCategorylistItems对象
        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @SuppressLint("ShowToast")
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                DeleteCategoryAdapter.ViewHolder holder = (DeleteCategoryAdapter.ViewHolder) arg1.getTag();
                String apptext = (String) holder.tv.getText();
                int position = arg2 + 1;
                if (Integer.parseInt(CustomMenuActivity.this.IniFile.getIniString(
                        userIni,
                        CustomMenuActivity.this.IniFile.getIniString(userIni, "MENU_ALL", "ID"
                                + position, "", (byte) 0), "Count", "0",
                        (byte) 0)) <= 0) {
                    CustomMenuActivity.this.ModifyDialog.dismiss();
                    CustomMenuActivity.this.DeleteMenuDialog(apptext, position, arg2);
                } else {
                    Toast.makeText(CustomMenuActivity.this, "该分类不为空",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        this.DeletemAdapter = new DeleteCategoryAdapter(this.groups, this);
        categoryList.setAdapter(this.DeletemAdapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("删除菜单").setCancelable(false)// 提示框标题
                .setView(view).setPositiveButton("取消", null);
        this.ModifyDialog = builder.create();
        this.ModifyDialog.show();
    }

    protected void DeleteMenuDialog(String apptext, final int position,
                                    final int arg2) {
        // TODO Auto-generated method stub
        new AlertDialog.Builder(this).setCancelable(false)
                .setMessage("确定删除:" + apptext)// 提示框标题
                .setPositiveButton("确定",// 提示框的两个按钮
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                CustomMenuActivity.this.DeleteMenu(position, arg2);
                            }
                        }).setNegativeButton("取消", null).create().show();
    }

    protected void DeleteMenu(int position, int arg2) {
        // TODO Auto-generated method stub
        int resnum = Integer.parseInt(this.IniFile.getIniString(userIni,
                "MENU_ALL", "Count", "0", (byte) 0));
        for (int i = 1; i <= resnum; i++) {
            if (i == position) {
                this.IniFile.deleteIniString(userIni, "MENU_ALL", "Title" + i);
                this.IniFile.deleteIniString(userIni, "MENU_ALL", "ID" + i);
                this.IniFile.deleteIniString(userIni, "MENU_ALL", "Url" + i);
                this.IniFile.deleteIniSection(userIni, IniFile.getIniString(userIni, "MENU_ALL", "ID" + i, "", (byte)
                        0));
            } else if (i > position) {
                String resid = this.IniFile.getIniString(userIni, "MENU_ALL",
                        "Title" + i, "", (byte) 0);
                String resname = this.IniFile.getIniString(userIni, "MENU_ALL",
                        "ID" + i, "", (byte) 0);
                String resicon = this.IniFile.getIniString(userIni, "MENU_ALL",
                        "Url" + i, "", (byte) 0);

                this.IniFile.writeIniString(userIni, "MENU_ALL", "Title"
                        + (i - 1), resid);
                this.IniFile.writeIniString(userIni, "MENU_ALL", "ID" + (i - 1),
                        resname);
                this.IniFile.writeIniString(userIni, "MENU_ALL", "Url" + (i - 1),
                        resicon);
                this.IniFile.deleteIniString(userIni, "MENU_ALL", "Title" + i);
                this.IniFile.deleteIniString(userIni, "MENU_ALL", "ID" + i);
                this.IniFile.deleteIniString(userIni, "MENU_ALL", "Url" + i);
            }
        }
        this.IniFile.writeIniString(userIni, "MENU_ALL", "Count", (resnum - 1)
                + "");
        this.groups.remove(arg2);
        this.exlist_adapter.notifyDataSetChanged();
    }

}