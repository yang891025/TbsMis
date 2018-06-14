package com.tbs.tbsmis.check;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.file.Util;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * 路径选择弹窗
 * 
 * @author @yeah.net 修改 yzt
 * 
 */
public class ApkChooseDialog extends Dialog {
    private ListView expandableList;
    private Button cancleBtn;
    private Button setBtn;
    // private UpChooseListener listener;
    private TextView tvCurPath;
    private TextView up_file_txt;
    private List<String> data;
    private final Stack<String> pathStack = new Stack<String>();
    private ApkChooseAdapter listAdapter;
    private int firstIndex;
    protected int checkNum;
    protected String currentPath;
    private ImageView backBtn;
    private boolean isOpenPop;
    private ImageView path_up_level;
    private int lenght;
    private View mDropdownNavigation;
    private ImageView orderBtn;
    private RelativeLayout up_file_title;
	private  Context context;
	private  ApkChooseDialog.ChooseListener listener;

	public interface ChooseListener {
		void onComplete(String CheckedFile);
	}

	public ApkChooseDialog(Context context, ApkChooseDialog.ChooseListener listener) {
		super(context);
		this.context = context;
		this.listener = listener;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.up_file_path);
        setCanceledOnTouchOutside(false);
        init();
	}

    private void init() {
        // TODO Auto-generated method stub
        backBtn = (ImageView) findViewById(R.id.up_file_back);
        orderBtn = (ImageView) findViewById(R.id.file_order);
        setBtn = (Button) findViewById(R.id.up_file);
        setBtn.setText("确定");
        cancleBtn = (Button) findViewById(R.id.cancleBtn);
        path_up_level = (ImageView) findViewById(R.id.path_pane_up_level);
        tvCurPath = (TextView) findViewById(R.id.up_cur_path);
        up_file_txt = (TextView) findViewById(R.id.up_file_txt);
        expandableList = (ListView) findViewById(android.R.id.list);
        up_file_title = (RelativeLayout) findViewById(R.id.up_file_title);
        up_file_txt.setText("选择APK文件");
        initData();
        setupNaivgationBar();
        path_up_level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDropdownNavigation.getVisibility() == View.VISIBLE) {
                    showDropdownNavigation(false);
                }
                checkNum = 0;
                if (pathStack.size() > 2) {
                    pathStack.pop();
                    data = FileUtils.listapk(pathStack.peek());
                    tvCurPath.setText(pathStack.peek());
                    refleshListView(data, firstIndex);
                } else {
                    path_up_level.setClickable(false);
                }
            }
        });
        // 取消
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changMenu(v);
            }
        });
        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listAdapter.getSelected().size() < 1) {
                    Toast.makeText(context, "请选择APK文件", Toast.LENGTH_SHORT).show();
                } else {
                   listener.onComplete(listAdapter.getSelected().get(0));
                    dismiss();
                }
            }
        });
        // 单击
        expandableList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (mDropdownNavigation.getVisibility() == View.VISIBLE) {
                    showDropdownNavigation(false);
                }
                UpFileAdapter.ViewHolder holder = (UpFileAdapter.ViewHolder) view.getTag();
                String Path = data.get(position);
                File isFile = new File(Path);
                if (checkNum == 1) {
                    if (isFile.isDirectory()) {
                        checkNum = 0;
                        try {
                            data = FileUtils.listapk(Path);
                            tvCurPath.setText(Path);
                            pathStack.add(Path);
                            refleshListView(data, pathStack.size() - 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (holder.children_cb.isChecked() == true) {
                            holder.children_cb.toggle();
                            checkNum--;
                            listAdapter.getIsSelected().put(position,
                                    holder.children_cb.isChecked());
                        } else {
                            holder.children_cb.toggle();
                            checkNum++;
                            listAdapter.getIsSelected().put(position,
                                    holder.children_cb.isChecked());
                        }
                    }
                } else {
                    if (isFile.isDirectory()) {
                        checkNum = 0;
                        try {
                            data = FileUtils.listapk(Path);
                            tvCurPath.setText(Path);
                            pathStack.add(Path);
                            refleshListView(data, pathStack.size() - 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        // 改变CheckBox的状态
                        holder.children_cb.toggle();
                        // 将CheckBox的选中状况记录下来
                        listAdapter.getIsSelected().put(position,
                                holder.children_cb.isChecked());
                        // 调整选定条目
                        if (holder.children_cb.isChecked() == true) {
                            checkNum++;
                            currentPath = Path;
                        } else {
                            checkNum--;
                        }
                    }
                }
                firstIndex = position;
            }
        });
    }

    protected void onNavigationBarClick() {
        if (mDropdownNavigation.getVisibility() == View.VISIBLE) {
            showDropdownNavigation(false);
        } else {
            LinearLayout list = (LinearLayout) mDropdownNavigation
                    .findViewById(R.id.dropdown_navigation_list);
            list.removeAllViews();
            int pos = 0;
            String displayPath = tvCurPath.getText().toString();
            boolean root = true;
            int left = 0;
            while (pos != -1 && !displayPath.equals("/")) {// 如果当前位置在根文件夹则不显示导航条
                int end = displayPath.indexOf("/", pos);
                if (end == -1)
                    break;

                View listItem = LayoutInflater.from(context).inflate(R.
                        layout.dropdown_item, null);

                View listContent = listItem.findViewById(R.id.list_item);
                listContent.setPadding(left, 0, 0, 0);
                left += 20;
                ImageView img = (ImageView) listItem
                        .findViewById(R.id.item_icon);

                img.setImageResource(root ? R.drawable.dropdown_icon_root
                        : R.drawable.dropdown_icon_folder);
                root = false;

                TextView text = (TextView) listItem
                        .findViewById(R.id.path_name);
                String substring = displayPath.substring(pos, end);
                if (substring.isEmpty())
                    substring = "/";
                text.setText(substring);

                listItem.setOnClickListener(navigationClick);
                listItem.setTag(displayPath.substring(0, end));
                pos = end + 1;
                list.addView(listItem);
            }
            if (list.getChildCount() > 0)
                showDropdownNavigation(true);
        }
    }

    private final View.OnClickListener navigationClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String path = (String) v.getTag();
            assert path != null;
            showDropdownNavigation(false);
            if (path.isEmpty()) {
                path = "/";
            }
            addPath(path);
            data = FileUtils.listapk(path);
            tvCurPath.setText(path);
            refleshListView(data, firstIndex);
        }

    };
    private TextView mNavigationBarText;
    private ImageView mNavigationBarUpDownArrow;
    private PopupWindow SetWindow;

    private void setupNaivgationBar() {
        mNavigationBarText = (TextView) findViewById(R.id.current_path_view);
        mNavigationBarUpDownArrow = (ImageView) findViewById(R.id.path_pane_arrow);
        View clickable = findViewById(R.id.current_path_pane);
        clickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigationBarClick();
            }
        });
        mDropdownNavigation = findViewById(R.id.dropdown_navigation);
    }

    private void showDropdownNavigation(boolean show) {
        mDropdownNavigation.setVisibility(show ? View.VISIBLE : View.GONE);
        mNavigationBarUpDownArrow.setImageResource(mDropdownNavigation
                .getVisibility() == View.VISIBLE ? R.drawable.arrow_up
                : R.drawable.arrow_down);
    }

    private void initData() {
        String webRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        lenght = webRoot.length();
        addPath(webRoot);
        pathStack.add(webRoot);
        data = FileUtils.listapk(webRoot);
        tvCurPath.setText(webRoot);
        refleshListView(data, 0);
    }

    /**
     * 更新listView视图
     *
     * @param data
     */
    private void refleshListView(List<String> data, int firstItem) {
        String lost = FileUtils.getSDRoot() + "lost+found";
        data.remove(lost);
        path_up_level.setClickable(true);
        if (!data.isEmpty()) {
            Collections.sort(data, new Comparator<String>() {
                @Override
                public int compare(String object1, String object2) {
                    File o1 = new File(object1);
                    File o2 = new File(object2);
                    // 根据字段"LEVEL"排序
                    if (o1.isDirectory() == o2.isDirectory()) {
                        int order = UIHelper.getShareperference(
                                context,
                                constants.SAVE_LOCALMSGNUM, "sort", 0);
                        if (order == 0) {
                            return object1.compareToIgnoreCase(object2);
                        } else if (order == 1) {
                            return longToCompareInt(o1.length() - o2.length());
                        } else if (order == 2) {
                            return longToCompareInt(o1.lastModified()
                                    - o2.lastModified());
                        } else if (order == 3) {
                            int result = Util.getExtFromFilename(object1)
                                    .compareToIgnoreCase(
                                            Util.getExtFromFilename(object2));
                            if (result != 0)
                                return result;
                            return Util.getNameFromFilename(object1)
                                    .compareToIgnoreCase(
                                            Util.getNameFromFilename(object2));
                        }
                    }
                    return o1.isDirectory() ? -1 : 1;
                }
            });
        }
        listAdapter = new ApkChooseAdapter(data, context);
        expandableList.setAdapter(listAdapter);
        expandableList.setSelection(firstItem);
    }

    private int longToCompareInt(long result) {
        return result > 0 ? 1 : result < 0 ? -1 : 0;
    }

    private void addPath(String webRoot) {
        // TODO Auto-generated method stub
        if (pathStack.size() > 1) {
            pathStack.clear();
        }
        int start = 0;
        String rootPath = "/";
        int iIndex = webRoot.indexOf("/", start);
        while (iIndex >= 0) {
            if (iIndex == 0) {
                start = iIndex + 1;
                rootPath = webRoot.substring(0, start);
            } else {
                start = iIndex + 1;
                rootPath = webRoot.substring(0, iIndex);
            }
            iIndex = webRoot.indexOf("/", start);
            pathStack.add(rootPath);
        }
    }

    public void changMenu(View v) {
        isOpenPop = !isOpenPop;
        if (isOpenPop) {
            popWindow_Menu(v);
        } else {
            if (SetWindow != null) {
                SetWindow.dismiss();
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void popWindow_Menu(View parent) {
        // iniPath();
        String[] sortValue = { "排序", "刷新" };
        ArrayList<Map<String, String>> MenuList = new ArrayList<Map<String, String>>();
        LayoutInflater lay = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = lay.inflate(R.layout.main_menu_prefrences, null);
        // iniPath();
        // ImageView avatar_iv = (ImageView) view.findViewById(R.id.userimage);
        // TextView nickname_tv = (TextView)
        // view.findViewById(R.id.usernickname);
        // TextView district = (TextView) view.findViewById(R.id.useraccount);
        LinearLayout userinfo = (LinearLayout) view.findViewById(R.id.userinfo);
        ListView menu_list = (ListView) view.findViewById(R.id.listMenuItems);
        userinfo.setVisibility(View.GONE);

        for (int i = 0; i < sortValue.length; i++) {
            Map<String, String> group = new HashMap<String, String>();
            group.put("Title", sortValue[i]);
            group.put("Icon", "0");
            MenuList.add(group);
        }

        PopMenuAdapter MenuListAdapter = new PopMenuAdapter(MenuList,
                context);
        menu_list.setAdapter(MenuListAdapter);
        // ��listView�ļ�����
        menu_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                if (arg2 == 0) {
                    showSortDialog();
                }  else if (arg2 == 1) {
                    data = FileUtils.listapk(tvCurPath.getText().toString());
                    refleshListView(data, pathStack.size() - 1);
                }
                SetWindow.dismiss();
            }
        });
        SetWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // �˶�ʵ�ֵ���հ״�����popwindow
        SetWindow.setFocusable(true);
        SetWindow.setOutsideTouchable(false);
        SetWindow.setBackgroundDrawable(new BitmapDrawable());
        SetWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                isOpenPop = false;
            }
        });

        SetWindow.showAtLocation(parent, Gravity.RIGHT | Gravity.TOP, 10,
                up_file_title.getHeight() * 3 / 2);
        SetWindow.update();
    }


    protected void showSortDialog() {
        // TODO Auto-generated method stub
        new AlertDialog.Builder(context)
                .setTitle("选择类型")
                .setSingleChoiceItems(
                        R.array.sort_options,
                        UIHelper.getShareperference(context,
                                constants.SAVE_LOCALMSGNUM, "sort", 0),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                UIHelper.setSharePerference(
                                        context,
                                        constants.SAVE_LOCALMSGNUM, "sort",
                                        which);
                                refleshListView(data, firstIndex);
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", null).show();
    }
}