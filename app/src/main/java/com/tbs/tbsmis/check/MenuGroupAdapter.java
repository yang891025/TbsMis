package com.tbs.tbsmis.check;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuGroupAdapter extends BaseExpandableListAdapter
{
    class ListHolder
    { // 定义一个内部类，用于保存listitem的3个子视图引用,2个textview和1个checkbox
        public TextView tvName;
    }

    class ExpandableListHolder
    { // 定义一个内部类，用于保存listitem的3个子视图引用,2个textview和1个checkbox
        public Button down;
        public Button up;
        public Button delete;
        public Button copy;
        public Button se;
        public TextView title;
        public TextView ChildName;
        public ImageView im;
        public ImageView more;
        public LinearLayout btnLayout;
        public RelativeLayout msg;
    }

    private final Context context; // 父activity
    private final LayoutInflater mChildInflater; // 用于加载listitem的布局xml
    private final LayoutInflater mGroupInflater; // 用于加载group的布局xml
    private final ArrayList<Map<String, String>> groups; // 所有group
    private final ArrayList<List<Map<String, String>>> childs; // 所有group
    private final String webRoot;
    private final IniFile m_iniFileIO;
    private AlertDialog ModifyDialog;
    private DeleteCategoryAdapter DeletemAdapter;
    private ButtonGridviewAdapter ButtonmAdapter;

    // 构造方法：参数c － activity，参数group － 所有group

    public MenuGroupAdapter(Context c, ArrayList<Map<String, String>> groups,
                            ArrayList<List<Map<String, String>>> childs, String webRoot) {
        context = c;
        this.mChildInflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mGroupInflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.groups = groups;
        this.childs = childs;
        this.webRoot = webRoot;
        this.m_iniFileIO = new IniFile();
    }

    @Override
    public Object getChild(int arg0, int arg1) {// 根据组索引和item索引，取得listitem //
        // TODO Auto-generated method
        // stub
        return this.childs.get(arg0).get(arg1);
    }

    @Override
    public long getChildId(int arg0, int arg1) {// 返回item索引
        return arg1;
    }

    @Override
    public int getChildrenCount(int groupPosition) {// 根据组索引返回分组的子item数
        return this.childs.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {// 根据组索引返回组
        return this.groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {// 返回分组数
        return this.groups.size();
    }

    @Override
    public long getGroupId(int groupPosition) {// 返回分组索引
        return groupPosition;
    }

    @Override
    public View getGroupView(int position, boolean isExpanded, View view,
                             ViewGroup parent) {// 根据组索引渲染"组视图"
        MenuGroupAdapter.ListHolder holder = null; // 清空临时变量holder
        if (view == null) { // 判断view（即view是否已构建好）是否为空
            // 若组视图为空，构建组视图。注意flate的使用，R.layout.browser_expandable_list_item代表了
            // 已加载到内存的browser_expandable_list_item.xml文件
            view = this.mGroupInflater.inflate(R.layout.select_city_item, null);
            // 下面主要是取得组的各子视图，设置子视图的属性。用tag来保存各子视图的引用
            holder = new MenuGroupAdapter.ListHolder();
            // 从view中取得textView
            holder.tvName = (TextView) view.findViewById(R.id.column_title);
            view.setTag(holder);
        } else { // 若view不为空，直接从view的tag属性中获得各子视图的引用
            holder = (MenuGroupAdapter.ListHolder) view.getTag();
        }

        holder.tvName.setText(this.groups.get(position).get("group") + "("
                + this.groups.get(position).get("groupId") + ")");
        // TODO Auto-generated method stub
        return view;
    }

    // 行渲染方法
    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final MenuGroupAdapter.ExpandableListHolder holder; // 清空临时变量
        if (convertView == null) { // 若行未初始化
            // 通过flater初始化行视图
            convertView = this.mChildInflater.inflate(R.layout.menu_list_item, null);
            // 并将行视图的3个子视图引用放到tag中
            holder = new MenuGroupAdapter.ExpandableListHolder();
            holder.ChildName = (TextView) convertView
                    .findViewById(R.id.app_name);
            // holder.re = (TextView) convertView.findViewById(R.id.app_title);
            holder.title = (TextView) convertView.findViewById(R.id.app_id);
            // holder.cb = (Button) convertView.findViewById(R.id.app_btn);
            holder.se = (Button) convertView.findViewById(R.id.setedit);
            holder.copy = (Button) convertView.findViewById(R.id.setcopy);
            holder.delete = (Button) convertView
                    .findViewById(R.id.setdeskshort);
            holder.up = (Button) convertView.findViewById(R.id.setdefault);
            holder.down = (Button) convertView.findViewById(R.id.setdelete);
            holder.im = (ImageView) convertView.findViewById(R.id.img_app_icon);
            holder.more = (ImageView) convertView
                    .findViewById(R.id.update_more);
            holder.msg = (RelativeLayout) convertView
                    .findViewById(R.id.app_msg);
            holder.btnLayout = (LinearLayout) convertView
                    .findViewById(R.id.updateLayout);
            convertView.setTag(holder);
        } else { // 若行已初始化，直接从tag属性获得子视图的引用
            holder = (MenuGroupAdapter.ExpandableListHolder) convertView.getTag();
        }

        holder.ChildName.setText(this.childs.get(groupPosition).get(childPosition)
                .get("Title"));
        String url = childs.get(groupPosition).get(childPosition).get("Url");
        holder.title.setText("链接："
                + url.substring(url.indexOf(":")+1));
        if (!StringUtils.isEmpty(this.childs.get(groupPosition).get(childPosition)
                .get("Icon"))) {
            holder.im.setBackgroundResource(constants.MenuButtonIcoId[Integer
                    .parseInt(this.childs.get(groupPosition).get(childPosition)
                            .get("Icon"))]);
        }

        holder.se.setText(R.string.sapi_edit);
        holder.copy.setText(R.string.sapi_picture);
        holder.down.setText(R.string.down);
        holder.delete.setText(R.string.delete);
        holder.up.setText(R.string.up);
        if (childPosition == 0) {
            holder.up.setEnabled(false);
        } else if (childPosition == this.getChildrenCount(groupPosition) - 1) {
            holder.down.setEnabled(false);
        } else {
            holder.down.setEnabled(true);
            holder.up.setEnabled(true);
        }
        holder.se.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                MenuGroupAdapter.this.EditMsgCategoryDialog(groupPosition, childPosition,
                        holder.ChildName.getText().toString());
            }
        });
        holder.btnLayout.setVisibility(View.GONE);
        holder.more.setBackgroundResource(R.drawable.update_down);
        holder.msg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (!holder.btnLayout.isShown()) {
                    holder.btnLayout.setVisibility(View.VISIBLE);
                    holder.more.setBackgroundResource(R.drawable.update_up);
                } else {
                    holder.btnLayout.setVisibility(View.GONE);
                    holder.more.setBackgroundResource(R.drawable.update_down);
                }
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                MenuGroupAdapter.this.DeleteCategoryDialog(groupPosition, childPosition,
                        holder.ChildName.getText().toString());
            }
        });
        holder.up.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                MenuGroupAdapter.this.upcategory(groupPosition + 1, childPosition);
            }
        });
        holder.copy.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                MenuGroupAdapter.this.TopbuttonImageDialog(groupPosition + 1, childPosition + 1);
            }
        });
        holder.down.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                MenuGroupAdapter.this.downcategory(groupPosition + 1, childPosition);
            }
        });
        return convertView;
    }

    private void TopbuttonImageDialog(final int groupPosition,
                                      final int childPosition) {
        LayoutInflater factory = LayoutInflater.from(this.context);// 提示框
        View view = factory.inflate(R.layout.button_image_set, null);// 这里必须是final的
        GridView categoryList = (GridView) view
                .findViewById(R.id.button_gridview);// 获得AppCategorylistItems对象
        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @SuppressLint("ShowToast")
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                String groupsname = MenuGroupAdapter.this.m_iniFileIO.getIniString(MenuGroupAdapter.this.webRoot,
                        "MENU_ALL", "ID" + groupPosition, "0", (byte) 0);
                MenuGroupAdapter.this.m_iniFileIO.writeIniString(MenuGroupAdapter.this.webRoot, groupsname, "Icon"
                        + childPosition, arg2 + "");
                MenuGroupAdapter.this.childs.get(groupPosition - 1).get(childPosition - 1)
                        .put("Icon", arg2 + "");
                MenuGroupAdapter.this.notifyDataSetChanged();
                MenuGroupAdapter.this.ModifyDialog.dismiss();
            }
        });
        if (!StringUtils.isEmpty(this.childs.get(groupPosition - 1)
                .get(childPosition - 1).get("Icon"))) {
            this.ButtonmAdapter = new ButtonGridviewAdapter(this.context,
                    constants.MenuButtonIcoId, Integer.parseInt(this.childs
                    .get(groupPosition - 1).get(childPosition - 1)
                    .get("Icon")));
        } else {
            this.ButtonmAdapter = new ButtonGridviewAdapter(this.context,
                    constants.MenuButtonIcoId, 0);
        }
        categoryList.setAdapter(this.ButtonmAdapter);
        Builder builder = new AlertDialog.Builder(this.context);
        builder.setTitle("选择图片").setCancelable(false)// 提示框标题
                .setView(view).setPositiveButton("取消", null);
        this.ModifyDialog = builder.create();
        this.ModifyDialog.show();
    }

    protected void EditMsgCategoryDialog(final int groupPosition,
                                         final int childPosition, String msgName) {
        // TODO Auto-generated method stub
        LayoutInflater factory = LayoutInflater.from(this.context);// 提示框
        View view = factory.inflate(R.layout.set_new_menu_option, null);// 这里必须是final的
        final EditText edit_name = (EditText) view
                .findViewById(R.id.edit_name_Text);// 获得输入框对象
        final EditText edit_web = (EditText) view
                .findViewById(R.id.edit_url_Text);
        final EditText edit_type = (EditText) view
                .findViewById(R.id.edit_type_Text);
        final EditText edit_key = (EditText) view
                .findViewById(R.id.edit_key_Text);
        edit_name.setText(this.childs.get(groupPosition).get(childPosition)
                .get("Title"));
        edit_web.setText(this.childs.get(groupPosition).get(childPosition)
                .get("Url"));
        String type = childs.get(groupPosition).get(childPosition)
                .get("Type");
        final RadioButton url_check_box = (RadioButton) view.findViewById(R.id.radioBtnImg);
        final RadioButton key_check_box = (RadioButton) view.findViewById(R.id.radioBtnTxt);
        final RelativeLayout url_layout= (RelativeLayout) view
                .findViewById(R.id.url_layout);
        final RelativeLayout key_layout= (RelativeLayout) view
                .findViewById(R.id.key_layout);
        if (type.isEmpty()){
            edit_type.setText("view");
            url_check_box.setChecked(true);
            key_layout.setVisibility(View.GONE);
        }
        else {
            if (type.equalsIgnoreCase("view")) {
                url_check_box.setChecked(true);
                key_layout.setVisibility(View.GONE);
                edit_type.setText(this.childs.get(groupPosition).get(childPosition)
                        .get("Type"));
            }else{
                key_check_box.setChecked(true);
                url_layout.setVisibility(View.GONE);
                edit_type.setText(this.childs.get(groupPosition).get(childPosition)
                        .get("Type"));
            }
        }
        url_check_box.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(url_check_box.isChecked()){
                    url_layout.setVisibility(View.VISIBLE);
                    key_layout.setVisibility(View.GONE);
                }
            }
        });
        key_check_box.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(key_check_box.isChecked()){
                    url_layout.setVisibility(View.GONE);
                    key_layout.setVisibility(View.VISIBLE);
                }
            }
        });
        edit_key.setText(this.childs.get(groupPosition).get(childPosition)
                .get("Key"));
        new AlertDialog.Builder(this.context)
                .setTitle("编辑“" + msgName + "”菜单")
                .setCancelable(false)
                // 提示框标题
                .setView(view)
                .setPositiveButton("确定",// 提示框的两个按钮
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
                                if(url_check_box.isChecked()){
                                    edit_type.setText("view");
                                }else if(key_check_box.isChecked()){
                                    edit_type.setText("click");
                                }
                                if (StringUtils.isEmpty(edit_name.getText()
                                        .toString())) {
                                    Toast.makeText(MenuGroupAdapter.this.context, "菜单名称不可为空",
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                } else if (StringUtils.isEmpty(edit_type
                                        .getText().toString())) {
                                    Toast.makeText(MenuGroupAdapter.this.context, "菜单项类型不可为空",
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                } else if (edit_type.getText().toString()
                                        .equalsIgnoreCase("view")) {
                                    if (StringUtils.isEmpty(edit_web.getText()
                                            .toString())) {
                                        Toast.makeText(MenuGroupAdapter.this.context, "菜单项链接不可为空",
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    if (!MenuGroupAdapter.this.EditMenuOption(edit_type.getText()
                                                    .toString(), groupPosition + 1,
                                            childPosition + 1, edit_name
                                                    .getText().toString(),
                                            edit_web.getText().toString())) {
                                        return;
                                    }
                                } else if (edit_type.getText().toString()
                                        .equalsIgnoreCase("click")) {
                                    if (StringUtils.isEmpty(edit_key.getText()
                                            .toString())) {
                                        Toast.makeText(MenuGroupAdapter.this.context, "菜单项键值不可为空",
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    if (!MenuGroupAdapter.this.EditMenuOption(edit_type.getText()
                                                    .toString(), groupPosition + 1,
                                            childPosition + 1, edit_name
                                                    .getText().toString(),
                                            edit_key.getText().toString())) {
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

    protected boolean EditMenuOption(String edit_type, int groupPosition,
                                     int childPosition, String edit_name, String edit_web) {
        // TODO Auto-generated method stub
        String groupsname = this.m_iniFileIO.getIniString(this.webRoot, "MENU_ALL", "ID"
                + groupPosition, "0", (byte) 0);
        int groupresnum = Integer.parseInt(this.m_iniFileIO.getIniString(this.webRoot,
                groupsname, "Count", "0", (byte) 0));
        for (int j = 1; j <= groupresnum; j++) {
            if (j != childPosition) {
                if (edit_name.equals(this.m_iniFileIO.getIniString(this.webRoot,
                        groupsname, "Title" + j, "", (byte) 0))) {
                    Toast.makeText(this.context, "菜单项名称已存在,编辑失败", Toast.LENGTH_SHORT)
                            .show();
                    return false;
                }
            }
        }
        this.m_iniFileIO.writeIniString(this.webRoot, groupsname,
                "Title" + childPosition, edit_name);
        this.m_iniFileIO.writeIniString(this.webRoot, groupsname, "Type" + childPosition,
                edit_type);
        if (edit_type.equalsIgnoreCase("view")) {
            this.m_iniFileIO.writeIniString(this.webRoot, groupsname, "Url"
                    + childPosition, edit_web);
            this.childs.get(groupPosition - 1).get(childPosition - 1)
                    .put("Url", edit_web);
        } else {
            this.m_iniFileIO.writeIniString(this.webRoot, groupsname, "Key"
                    + childPosition, edit_web);
            this.childs.get(groupPosition - 1).get(childPosition - 1)
                    .put("Key", edit_web);
        }
        this.childs.get(groupPosition - 1).get(childPosition - 1)
                .put("Title", edit_name);
        this.childs.get(groupPosition - 1).get(childPosition - 1)
                .put("Type", edit_type);
        this.notifyDataSetChanged();
        Toast.makeText(this.context, "编辑菜单项成功", Toast.LENGTH_SHORT).show();
        return true;
    }

    protected void CopyMsgCategoryDialog(int groupPosition, int childPosition,
                                         String msgName) {
        // TODO Auto-generated method stub

        LayoutInflater factory = LayoutInflater.from(this.context);// 提示框
        View view = factory.inflate(R.layout.set_new_menu_option, null);// 这里必须是final的
        final EditText edit_name = (EditText) view
                .findViewById(R.id.edit_name_Text);// 获得输入框对象
        final EditText edit_web = (EditText) view
                .findViewById(R.id.edit_url_Text);
        edit_name.setText(this.childs.get(groupPosition).get(childPosition)
                .get("Title"));
        edit_web.setText(this.childs.get(groupPosition).get(childPosition)
                .get("Url"));
        final String edit_icon = this.childs.get(groupPosition).get(childPosition)
                .get("Icon");
        new AlertDialog.Builder(this.context)
                .setTitle("复制“" + msgName + "”并添加到菜单")
                .setCancelable(false)
                // 提示框标题
                .setView(view)
                .setPositiveButton("确定",// 提示框的两个按钮
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
                                if (StringUtils.isEmpty(edit_name.getText()
                                        .toString())) {
                                    Toast.makeText(MenuGroupAdapter.this.context, "菜单名称不可为空",
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                } else if (StringUtils.isEmpty(edit_web
                                        .getText().toString())) {
                                    Toast.makeText(MenuGroupAdapter.this.context, "菜单项链接不可为空",
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                MenuGroupAdapter.this.NewMenuOption(edit_name.getText().toString(),
                                        edit_web.getText().toString(),
                                        edit_icon);
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

    protected void NewMenuOption(final String edit_name, final String edit_web,
                                 final String edit_icon) {
        // TODO Auto-generated method stub
        LayoutInflater factory = LayoutInflater.from(this.context);// 提示框
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
                MenuGroupAdapter.this.ModifyDialog.dismiss();
                MenuGroupAdapter.this.AddMenuOptionDialog(apptext, edit_name, edit_web, edit_icon,
                        position);
            }
        });
        this.DeletemAdapter = new DeleteCategoryAdapter(this.groups, this.context);
        categoryList.setAdapter(this.DeletemAdapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setTitle("选择菜单").setCancelable(false)// 提示框标题
                .setView(view).setPositiveButton("取消", null);
        this.ModifyDialog = builder.create();
        this.ModifyDialog.show();
    }

    protected void AddMenuOptionDialog(String apptext, final String edit_name,
                                       final String edit_web, final String edit_icon, final int position) {
        // TODO Auto-generated method stub
        new AlertDialog.Builder(this.context).setTitle("添加菜单项")
                .setMessage("菜单：" + apptext + "\n" + "菜单项：" + edit_name)// 提示框标题
                .setPositiveButton("确定",// 提示框的两个按钮
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String groupsname = MenuGroupAdapter.this.m_iniFileIO.getIniString(
                                        MenuGroupAdapter.this.webRoot, "MENU_ALL", "ID" + position,
                                        "0", (byte) 0);
                                int groupresnum = Integer.parseInt(MenuGroupAdapter.this.m_iniFileIO
                                        .getIniString(MenuGroupAdapter.this.webRoot, groupsname,
                                                "Count", "0", (byte) 0));
                                for (int j = 1; j <= groupresnum; j++) {
                                    if (edit_name.equals(MenuGroupAdapter.this.m_iniFileIO
                                            .getIniString(MenuGroupAdapter.this.webRoot, groupsname,
                                                    "Title" + j, "", (byte) 0))) {
                                        Toast.makeText(MenuGroupAdapter.this.context,
                                                "菜单项名称已存在,添加失败",
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                                MenuGroupAdapter.this.m_iniFileIO.writeIniString(MenuGroupAdapter.this.webRoot,
                                        groupsname,
                                        "Count", "" + (groupresnum + 1));
                                MenuGroupAdapter.this.m_iniFileIO.writeIniString(MenuGroupAdapter.this.webRoot,
                                        groupsname,
                                        "Title" + (groupresnum + 1), edit_name);
                                MenuGroupAdapter.this.m_iniFileIO.writeIniString(MenuGroupAdapter.this.webRoot,
                                        groupsname,
                                        "Url" + (groupresnum + 1), edit_web);
                                MenuGroupAdapter.this.m_iniFileIO.writeIniString(MenuGroupAdapter.this.webRoot,
                                        groupsname,
                                        "Icon" + (groupresnum + 1), edit_icon);
                                Map<String, String> childdata = new HashMap<String, String>();
                                childdata.put("Title", edit_name);
                                childdata.put("Url", edit_web);
                                childdata.put("Icon", "0");
                                MenuGroupAdapter.this.childs.get(position - 1).add(childdata);
                                MenuGroupAdapter.this.notifyDataSetChanged();
                                Toast.makeText(MenuGroupAdapter.this.context, "添加菜单项成功",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }).setNegativeButton("取消", null).create().show();

    }

    protected void downcategory(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        String menuName = this.m_iniFileIO.getIniString(this.webRoot, "MENU_ALL", "ID"
                + groupPosition, "0", (byte) 0);
        int Count = childPosition + 1;
        int Count1 = childPosition + 2;
        String resid = this.m_iniFileIO.getIniString(this.webRoot, menuName, "Url"
                + Count, "", (byte) 0);
        String Type = this.m_iniFileIO.getIniString(this.webRoot, menuName, "Type"
                + Count, "", (byte) 0);
        String Key = this.m_iniFileIO.getIniString(this.webRoot, menuName, "Key" + Count,
                "", (byte) 0);
        String resname = this.m_iniFileIO.getIniString(this.webRoot, menuName, "Title"
                + Count, "", (byte) 0);
        String resicon = this.m_iniFileIO.getIniString(this.webRoot, menuName, "Icon"
                + Count, "0", (byte) 0);

        this.m_iniFileIO.writeIniString(this.webRoot, menuName, "Type" + Count1, Type);
        this.m_iniFileIO.writeIniString(this.webRoot, menuName, "Key" + Count1, Key);
        this.m_iniFileIO.writeIniString(this.webRoot, menuName, "Url" + Count1, resid);
        this.m_iniFileIO
                .writeIniString(this.webRoot, menuName, "Title" + Count1, resname);
        this.m_iniFileIO.writeIniString(this.webRoot, menuName, "Icon" + Count1, resicon);
        this.m_iniFileIO.writeIniString(this.webRoot, menuName, "Type" + Count, this.childs
                .get(groupPosition - 1).get(Count).get("Type"));
        this.m_iniFileIO.writeIniString(this.webRoot, menuName, "Key" + Count, this.childs
                .get(groupPosition - 1).get(Count).get("Key"));
        this.m_iniFileIO.writeIniString(this.webRoot, menuName, "Url" + Count, this.childs
                .get(groupPosition - 1).get(Count).get("Url"));
        this.m_iniFileIO.writeIniString(this.webRoot, menuName, "Title" + Count, this.childs
                .get(groupPosition - 1).get(Count).get("Title"));
        this.m_iniFileIO.writeIniString(this.webRoot, menuName, "Icon" + Count, this.childs
                .get(groupPosition - 1).get(Count).get("Icon"));
        this.childs.get(groupPosition - 1)
                .get(childPosition)
                .put("Type",
                        this.childs.get(groupPosition - 1).get(Count).get("Type"));
        this.childs.get(groupPosition - 1)
                .get(childPosition)
                .put("Key", this.childs.get(groupPosition - 1).get(Count).get("Key"));
        this.childs.get(groupPosition - 1)
                .get(childPosition)
                .put("Url", this.childs.get(groupPosition - 1).get(Count).get("Url"));
        this.childs.get(groupPosition - 1)
                .get(childPosition)
                .put("Title",
                        this.childs.get(groupPosition - 1).get(Count).get("Title"));
        this.childs.get(groupPosition - 1)
                .get(childPosition)
                .put("Icon",
                        this.childs.get(groupPosition - 1).get(Count).get("Icon"));
        this.childs.get(groupPosition - 1).get(Count).put("Url", resid);
        this.childs.get(groupPosition - 1).get(Count).put("Title", resname);
        this.childs.get(groupPosition - 1).get(Count).put("Icon", resicon);
        this.childs.get(groupPosition - 1).get(Count).put("Type", Type);
        this.childs.get(groupPosition - 1).get(Count).put("Key", Key);

        this.notifyDataSetChanged();
    }

    protected void upcategory(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        String menuName = this.m_iniFileIO.getIniString(this.webRoot, "MENU_ALL", "ID"
                + groupPosition, "0", (byte) 0);
        int Count = childPosition + 1;
        String resid = this.m_iniFileIO.getIniString(this.webRoot, menuName, "Url"
                + childPosition, "", (byte) 0);
        String Type = this.m_iniFileIO.getIniString(this.webRoot, menuName, "Type"
                + childPosition, "", (byte) 0);
        String Key = this.m_iniFileIO.getIniString(this.webRoot, menuName, "Key"
                + childPosition, "", (byte) 0);
        String resname = this.m_iniFileIO.getIniString(this.webRoot, menuName, "Title"
                + childPosition, "", (byte) 0);
        String resicon = this.m_iniFileIO.getIniString(this.webRoot, menuName, "Icon"
                + childPosition, "0", (byte) 0);
        this.m_iniFileIO.writeIniString(this.webRoot, menuName, "Type" + Count, Type);
        this.m_iniFileIO.writeIniString(this.webRoot, menuName, "Key" + Count, Key);
        this.m_iniFileIO.writeIniString(this.webRoot, menuName, "Url" + Count, resid);
        this.m_iniFileIO.writeIniString(this.webRoot, menuName, "Title" + Count, resname);
        this.m_iniFileIO.writeIniString(this.webRoot, menuName, "Icon" + Count, resicon);
        this.m_iniFileIO.writeIniString(this.webRoot, menuName, "Type" + childPosition,
                this.childs.get(groupPosition - 1).get(childPosition).get("Type"));
        this.m_iniFileIO.writeIniString(this.webRoot, menuName, "Key" + childPosition,
                this.childs.get(groupPosition - 1).get(childPosition).get("Key"));
        this.m_iniFileIO.writeIniString(this.webRoot, menuName, "Url" + childPosition,
                this.childs.get(groupPosition - 1).get(childPosition).get("Url"));
        this.m_iniFileIO.writeIniString(this.webRoot, menuName, "Title" + childPosition,
                this.childs.get(groupPosition - 1).get(childPosition).get("Title"));
        this.m_iniFileIO.writeIniString(this.webRoot, menuName, "Icon" + childPosition,
                this.childs.get(groupPosition - 1).get(childPosition).get("Icon"));
        this.childs.get(groupPosition - 1)
                .get(childPosition - 1)
                .put("Type",
                        this.childs.get(groupPosition - 1).get(childPosition)
                                .get("Type"));
        this.childs.get(groupPosition - 1)
                .get(childPosition - 1)
                .put("Key",
                        this.childs.get(groupPosition - 1).get(childPosition)
                                .get("Key"));
        this.childs.get(groupPosition - 1)
                .get(childPosition - 1)
                .put("Url",
                        this.childs.get(groupPosition - 1).get(childPosition)
                                .get("Url"));
        this.childs.get(groupPosition - 1)
                .get(childPosition - 1)
                .put("Title",
                        this.childs.get(groupPosition - 1).get(childPosition)
                                .get("Title"));
        this.childs.get(groupPosition - 1)
                .get(childPosition - 1)
                .put("Icon",
                        this.childs.get(groupPosition - 1).get(childPosition)
                                .get("Icon"));
        this.childs.get(groupPosition - 1).get(childPosition).put("Url", resid);
        this.childs.get(groupPosition - 1).get(childPosition).put("Title", resname);
        this.childs.get(groupPosition - 1).get(childPosition).put("Icon", resicon);
        this.childs.get(groupPosition - 1).get(childPosition).put("Type", Type);
        this.childs.get(groupPosition - 1).get(childPosition).put("Key", Key);
        this.notifyDataSetChanged();
    }

    protected void DeleteCategoryDialog(final int groupPosition,
                                        final int childPosition, String msgName) {
        // TODO Auto-generated method stub
        new AlertDialog.Builder(this.context).setCancelable(false)
                .setMessage("确定删除:" + msgName + "菜单项")// 提示框标题
                .setPositiveButton("确定",// 提示框的两个按钮
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                MenuGroupAdapter.this.DeleteCategory(groupPosition + 1,
                                        childPosition + 1);
                            }
                        }).setNegativeButton("取消", null).create().show();
    }

    protected void DeleteCategory(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        String menuName = this.m_iniFileIO.getIniString(this.webRoot, "MENU_ALL", "ID"
                + groupPosition, "0", (byte) 0);
        int Count = Integer.parseInt(this.m_iniFileIO.getIniString(this.webRoot,
                menuName, "Count", "0", (byte) 0));
        for (int i = 1; i <= Count; i++) {
            if (i == childPosition) {
                this.m_iniFileIO.deleteIniString(this.webRoot, menuName, "Url" + i);
                this.m_iniFileIO.deleteIniString(this.webRoot, menuName, "Title" + i);
                this.m_iniFileIO.deleteIniString(this.webRoot, menuName, "Type" + i);
                this.m_iniFileIO.deleteIniString(this.webRoot, menuName, "Key" + i);
                this.m_iniFileIO.deleteIniString(this.webRoot, menuName, "Icon" + i);
            } else if (i > childPosition) {
                String resid = this.m_iniFileIO.getIniString(this.webRoot, menuName,
                        "Url" + i, "", (byte) 0);
                String Type = this.m_iniFileIO.getIniString(this.webRoot, menuName,
                        "Type" + i, "", (byte) 0);
                String Key = this.m_iniFileIO.getIniString(this.webRoot, menuName, "Key"
                        + i, "", (byte) 0);
                String resname = this.m_iniFileIO.getIniString(this.webRoot, menuName,
                        "Title" + i, "", (byte) 0);
                String resicon = this.m_iniFileIO.getIniString(this.webRoot, menuName,
                        "Icon" + i, "0", (byte) 0);
                this.m_iniFileIO.writeIniString(this.webRoot, menuName, "Url" + (i - 1),
                        resid);
                this.m_iniFileIO.writeIniString(this.webRoot, menuName, "Type" + (i - 1),
                        Type);
                this.m_iniFileIO.writeIniString(this.webRoot, menuName, "Key" + (i - 1),
                        Key);
                this.m_iniFileIO.writeIniString(this.webRoot, menuName,
                        "Title" + (i - 1), resname);
                this.m_iniFileIO.writeIniString(this.webRoot, menuName, "Icon" + (i - 1),
                        resicon);
            }
        }
        this.m_iniFileIO.deleteIniString(this.webRoot, menuName, "Url" + Count);
        this.m_iniFileIO.deleteIniString(this.webRoot, menuName, "Type" + Count);
        this.m_iniFileIO.deleteIniString(this.webRoot, menuName, "Key" + Count);
        this.m_iniFileIO.deleteIniString(this.webRoot, menuName, "Title" + Count);
        this.m_iniFileIO.deleteIniString(this.webRoot, menuName, "Icon" + Count);
        this.m_iniFileIO
                .writeIniString(this.webRoot, menuName, "Count", (Count - 1) + "");
        this.childs.get(groupPosition - 1).remove(childPosition - 1);
        this.notifyDataSetChanged();
    }

    @Override
    public boolean hasStableIds() {// 行是否具有唯一id
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {// 行是否可选
        return true;
    }
}