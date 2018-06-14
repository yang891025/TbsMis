package com.tbs.tbsmis.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.check.ButtonGridviewAdapter;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.headergridview.selectbuttonaction.StickyGridItem;
import com.tbs.tbsmis.headergridview.selectbuttonaction.StickyGridAdapter;
import com.tbs.tbsmis.lib.SlidingMenu;
import com.tbs.tbsmis.util.UIHelper;

import java.util.ArrayList;
import java.util.List;

public class AppearanceSettingActivity extends Activity implements View.OnClickListener
{
    private ImageView finishBtn;
    private ImageView downBtn;
    private TextView title;
    private CheckBox main_checkbox;
    private LinearLayout main_url;
    private CheckBox left_checkbox;
    private LinearLayout left_url;
    private CheckBox right_checkbox;
    private LinearLayout right_url;
    private CheckBox detail_checkbox;
    private LinearLayout detail_url;
    private CheckBox bottomtool_checkbox;
    private LinearLayout bottomtool_url;
    private CheckBox bottommsg_checkbox;
    private LinearLayout bottommsg_url;

    private IniFile IniFile;
    private boolean m_bChanged;
    private int ShowMenu;
    private int ShowMore;
    private AppearanceSettingActivity.SetMain SetMain;

    // private RelativeLayout ShowNavBar;
    // private CheckBox NavBar_box;

    private RelativeLayout maintitle;
    private TextView title_subtitle;
    private CheckBox title_box;

    private RelativeLayout touchmode;
    private TextView mode_subtitle;

    private RelativeLayout leftwidth;
    private TextView leftwidth_subtitle;

    private RelativeLayout rightwidth;
    private TextView rightwidth_subtitle;
    private RelativeLayout mainmsgwidth;
    private TextView mainmsgwidth_subtitle;
    private RelativeLayout left;
    private TextView left_subtitle;
    private CheckBox left_box;
    private RelativeLayout lefttitle;
    private TextView lefttitle_subtitle;
    private CheckBox lefttitle_box;
    private RelativeLayout right;
    private TextView right_subtitle;
    private CheckBox right_box;
    private RelativeLayout righttitle;
    private TextView righttitle_subtitle;
    private CheckBox righttitle_box;
    private RelativeLayout detail;
    private TextView detail_subtitle;
    private CheckBox detail_box;
    private RelativeLayout detail_open_in_browse;
    private TextView detail_open_subtitle;
    private CheckBox detail_open_box;
    private RelativeLayout maintool;
    private TextView maintool_subtitle;
    private CheckBox maintool_box;
    private RelativeLayout mainmsg;
    private TextView mainmsg_subtitle;
    private CheckBox mainmsg_box;
    private RelativeLayout prompt;
    private TextView prompt_subtitle;
    private CheckBox prompt_box;
    private RelativeLayout NavigationBtn;
    private TextView navigation_subtitle;
    private CheckBox left_one_box;
    private RelativeLayout BOT1;
    private TextView BOT1_subtitle;
    private CheckBox left_two_box;
    private RelativeLayout BOT2;
    private TextView BOT2_subtitle;
    private CheckBox middle_box;
    private RelativeLayout BOT3;
    private TextView BOT3_subtitle;
    private CheckBox right_two_box;
    private RelativeLayout MoreBtn;
    private TextView more_subtitle;
    private CheckBox right_one_box;
    private RelativeLayout detailshow;
    private TextView detailshow_subtitle;
    private RelativeLayout showin;
    private TextView showin_subtitle;
    private RelativeLayout buttonshow;
    private TextView buttonshow_subtitle;
    private LinearLayout main_set;
    private LinearLayout left_set;
    private LinearLayout right_set;
    private LinearLayout detail_set;
    private LinearLayout bottomtool_set;
    private LinearLayout bottommsg_set;
    private LinearLayout left_one_set;
    private LinearLayout left_two_set;
    private LinearLayout middle_set;
    private LinearLayout right_two_set;
    private LinearLayout right_one_set;
    private CheckBox right_one_open_box;
    private RelativeLayout right_one_open;
    private EditText right_one_url;
    private EditText right_one_name;
    private RelativeLayout right_two_open;
    private CheckBox right_two_open_box;
    private EditText right_two_url;
    private EditText right_two_name;
    private RelativeLayout middle_open;
    private CheckBox middle_open_box;
    private EditText middle_url;
    private EditText middle_name;
    private EditText left_two_name;
    private EditText left_two_url;
    private CheckBox left_two_open_box;
    private RelativeLayout left_two_open;
    private RelativeLayout left_one_open;
    private CheckBox left_one_open_box;
    private EditText left_one_url;
    private EditText left_one_name;
    private ButtonGridviewAdapter ButtonmAdapter;
    private RelativeLayout left_one_image;
    private RelativeLayout middle_image;
    private ImageView middle_imageview;
    private RelativeLayout right_two_image;
    private ImageView right_two_imageview;
    private RelativeLayout right_one_image;
    private ImageView right_one_imageview;
    private RelativeLayout left_two_image;
    private ImageView left_two_imageview;
    private ImageView left_one_imageview;
    private AlertDialog ModifyDialog;
    private EditText main_load_url;
    private LinearLayout left_title_set;
    private RelativeLayout left_title_open;
    private CheckBox left_title_open_box;
    private EditText left_title_name;
    private LinearLayout right_title_set;
    private RelativeLayout right_title_open;
    private CheckBox right_title_open_box;
    private EditText right_title_name;
    private RelativeLayout leftleftBtn;
    private TextView left_left_subtitle;
    private CheckBox left_left_box;
    private LinearLayout left_left_set;
    private RelativeLayout left_left_open;
    private CheckBox left_left_open_box;
    private EditText left_left_url;
    private RelativeLayout left_left_image;
    private ImageView left_left_imageview;
    private RelativeLayout leftrightBtn;
    private TextView left_right_subtitle;
    private CheckBox left_right_box;
    private LinearLayout left_right_set;
    private RelativeLayout left_right_open;
    private CheckBox left_right_open_box;
    private EditText left_right_url;
    private RelativeLayout left_right_image;
    private ImageView left_right_imageview;
    private String userIni;
    private RelativeLayout rightleftBtn;
    private TextView right_left_subtitle;
    private CheckBox right_left_box;
    private LinearLayout right_left_set;
    private RelativeLayout right_left_open;
    private CheckBox right_left_open_box;
    private EditText right_left_url;
    private RelativeLayout right_left_image;
    private ImageView right_left_imageview;
    private RelativeLayout rightrightBtn;
    private TextView right_right_subtitle;
    private CheckBox right_right_box;
    private LinearLayout right_right_set;
    private RelativeLayout right_right_open;
    private CheckBox right_right_open_box;
    private EditText right_right_url;
    private RelativeLayout right_right_image;
    private ImageView right_right_imageview;
    private EditText mainmsg_name;
    private EditText mainmsg_url;
    private EditText right_default_url;
    private EditText left_default_url;
    private EditText main_right_btn;
    private EditText main_left_btn;
    private EditText popBtn_url;
    private RelativeLayout mainleftBtn;
    private TextView main_left_subtitle;
    private CheckBox main_left_box;
    private LinearLayout main_left_set;
    private RelativeLayout main_left_open;
    private CheckBox main_left_open_box;
    private RelativeLayout main_left_image;
    private ImageView main_left_imageview;
    private RelativeLayout mainrightBtn;
    private TextView main_right_subtitle;
    private CheckBox main_right_box;
    private LinearLayout main_right_set;
    private RelativeLayout main_right_open;
    private CheckBox main_right_open_box;
    private RelativeLayout main_right_image;
    private ImageView main_right_imageview;
    private EditText main_menu_url;
    private EditText menuBtn_url;

    /*
       菜单按钮初始化
     */
    private ImageView menu_select_btn;
    private ImageView left_btn_url;
    private ImageView right_btn_url;
    private ImageView left_left_select_url;
    private ImageView left_right_select_url;
    private ImageView right_left_select_url;
    private ImageView right_right_select_url;
    private ImageView left_one_select_url;
    private ImageView left_two_select_url;
    private ImageView middle_select_url;
    private ImageView right_two_select_url;
    private ImageView right_one_select_url;
    private ImageView popBtn_select_url;
    private ImageView menuBtn_select_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.basic_setting_activity);
        MyActivity.getInstance().addActivity(this);
        this.init();
        this.initlayout();
    }

    public void init() {
        this.finishBtn = (ImageView) this.findViewById(R.id.more_btn);
        this.downBtn = (ImageView) this.findViewById(R.id.finish_btn);
        this.title = (TextView) this.findViewById(R.id.textView1);

        this.main_checkbox = (CheckBox) this.findViewById(R.id.main_checkbox);
        this.main_url = (LinearLayout) this.findViewById(R.id.main_url);
        this.main_set = (LinearLayout) this.findViewById(R.id.main_set);
        this.main_load_url = (EditText) this.findViewById(R.id.main_load_url);
        this.main_menu_url = (EditText) this.findViewById(R.id.main_menu_url);
        this.main_left_btn = (EditText) this.findViewById(R.id.main_left_btn);
        this.popBtn_url = (EditText) this.findViewById(R.id.popBtn_url);
        this.menuBtn_url = (EditText) this.findViewById(R.id.menuBtn_url);
        this.main_right_btn = (EditText) this.findViewById(R.id.main_right_btn);

        this.mainleftBtn = (RelativeLayout) this.findViewById(R.id.mainleftBtn);
        this.main_left_subtitle = (TextView) this.findViewById(R.id.main_left_subtitle);
        this.main_left_box = (CheckBox) this.findViewById(R.id.main_left_box);
        this.main_left_set = (LinearLayout) this.findViewById(R.id.main_left_set);
        this.main_left_open = (RelativeLayout) this.findViewById(R.id.main_left_open);
        this.main_left_open_box = (CheckBox) this.findViewById(R.id.main_left_open_box);
        this.main_left_image = (RelativeLayout) this.findViewById(R.id.main_left_image);
        this.main_left_imageview = (ImageView) this.findViewById(R.id.main_left_imageview);

        this.mainrightBtn = (RelativeLayout) this.findViewById(R.id.mainrightBtn);
        this.main_right_subtitle = (TextView) this.findViewById(R.id.main_right_subtitle);
        this.main_right_box = (CheckBox) this.findViewById(R.id.main_right_box);
        this.main_right_set = (LinearLayout) this.findViewById(R.id.main_right_set);
        this.main_right_open = (RelativeLayout) this.findViewById(R.id.main_right_open);
        this.main_right_open_box = (CheckBox) this.findViewById(R.id.main_right_open_box);
        this.main_right_image = (RelativeLayout) this.findViewById(R.id.main_right_image);
        this.main_right_imageview = (ImageView) this.findViewById(R.id.main_right_imageview);

        this.left_checkbox = (CheckBox) this.findViewById(R.id.left_checkbox);
        this.left_url = (LinearLayout) this.findViewById(R.id.left_url);
        this.left_set = (LinearLayout) this.findViewById(R.id.left_set);
        this.right_checkbox = (CheckBox) this.findViewById(R.id.right_checkbox);
        this.right_url = (LinearLayout) this.findViewById(R.id.right_url);
        this.right_set = (LinearLayout) this.findViewById(R.id.right_set);
        this.detail_checkbox = (CheckBox) this.findViewById(R.id.detail_checkbox);
        this.detail_url = (LinearLayout) this.findViewById(R.id.detail_url);
        this.detail_set = (LinearLayout) this.findViewById(R.id.detail_set);
        this.bottomtool_checkbox = (CheckBox) this.findViewById(R.id.bottomtool_checkbox);
        this.bottomtool_url = (LinearLayout) this.findViewById(R.id.bottomtool_url);
        this.bottomtool_set = (LinearLayout) this.findViewById(R.id.bottomtool_set);
        this.bottommsg_checkbox = (CheckBox) this.findViewById(R.id.bottommsg_checkbox);
        this.bottommsg_url = (LinearLayout) this.findViewById(R.id.bottommsg_url);
        this.bottommsg_set = (LinearLayout) this.findViewById(R.id.bottommsg_set);

        this.maintitle = (RelativeLayout) this.findViewById(R.id.maintitle);
        this.title_subtitle = (TextView) this.findViewById(R.id.title_subtitle);
        this.title_box = (CheckBox) this.findViewById(R.id.title_box);

        this.touchmode = (RelativeLayout) this.findViewById(R.id.touchmode);
        this.mode_subtitle = (TextView) this.findViewById(R.id.mode_subtitle);

        this.left = (RelativeLayout) this.findViewById(R.id.left);
        this.left_subtitle = (TextView) this.findViewById(R.id.left_subtitle);
        this.left_box = (CheckBox) this.findViewById(R.id.left_box);
        this.lefttitle = (RelativeLayout) this.findViewById(R.id.lefttitle);
        this.left_title_set = (LinearLayout) this.findViewById(R.id.left_title_set);
        this.left_title_open = (RelativeLayout) this.findViewById(R.id.left_title_open);
        this.left_title_open_box = (CheckBox) this.findViewById(R.id.left_title_open_box);
        this.left_title_name = (EditText) this.findViewById(R.id.left_title_name);
        this.left_default_url = (EditText) this.findViewById(R.id.left_default_url);
        this.lefttitle_subtitle = (TextView) this.findViewById(R.id.lefttitle_subtitle);
        this.lefttitle_box = (CheckBox) this.findViewById(R.id.lefttitle_box);
        this.leftwidth = (RelativeLayout) this.findViewById(R.id.leftwidth);
        this.leftwidth_subtitle = (TextView) this.findViewById(R.id.leftwidth_subtitle);
        this.leftleftBtn = (RelativeLayout) this.findViewById(R.id.leftleftBtn);
        this.left_left_subtitle = (TextView) this.findViewById(R.id.left_left_subtitle);
        this.left_left_box = (CheckBox) this.findViewById(R.id.left_left_box);
        this.left_left_set = (LinearLayout) this.findViewById(R.id.left_left_set);
        this.left_left_open = (RelativeLayout) this.findViewById(R.id.left_left_open);
        this.left_left_open_box = (CheckBox) this.findViewById(R.id.left_left_open_box);
        this.left_left_url = (EditText) this.findViewById(R.id.left_left_url);
        this.left_left_image = (RelativeLayout) this.findViewById(R.id.left_left_image);
        this.left_left_imageview = (ImageView) this.findViewById(R.id.left_left_imageview);
        this.leftrightBtn = (RelativeLayout) this.findViewById(R.id.leftrightBtn);
        this.left_right_subtitle = (TextView) this.findViewById(R.id.left_right_subtitle);
        this.left_right_box = (CheckBox) this.findViewById(R.id.left_right_box);
        this.left_right_set = (LinearLayout) this.findViewById(R.id.left_right_set);
        this.left_right_open = (RelativeLayout) this.findViewById(R.id.left_right_open);
        this.left_right_open_box = (CheckBox) this.findViewById(R.id.left_right_open_box);
        this.left_right_url = (EditText) this.findViewById(R.id.left_right_url);
        this.left_right_image = (RelativeLayout) this.findViewById(R.id.left_right_image);
        this.left_right_imageview = (ImageView) this.findViewById(R.id.left_right_imageview);

        this.right = (RelativeLayout) this.findViewById(R.id.right);
        this.right_subtitle = (TextView) this.findViewById(R.id.right_subtitle);
        this.right_box = (CheckBox) this.findViewById(R.id.right_box);
        this.righttitle = (RelativeLayout) this.findViewById(R.id.righttitle);
        this.right_title_set = (LinearLayout) this.findViewById(R.id.right_title_set);
        this.right_title_open = (RelativeLayout) this.findViewById(R.id.right_title_open);
        this.right_title_open_box = (CheckBox) this.findViewById(R.id.right_title_open_box);
        this.right_title_name = (EditText) this.findViewById(R.id.right_title_name);
        this.right_default_url = (EditText) this.findViewById(R.id.right_default_url);
        this.righttitle_subtitle = (TextView) this.findViewById(R.id.righttitle_subtitle);
        this.righttitle_box = (CheckBox) this.findViewById(R.id.righttitle_box);
        this.rightwidth = (RelativeLayout) this.findViewById(R.id.rightwidth);
        this.rightwidth_subtitle = (TextView) this.findViewById(R.id.rightwidth_subtitle);
        this.rightleftBtn = (RelativeLayout) this.findViewById(R.id.rightleftBtn);
        this.right_left_subtitle = (TextView) this.findViewById(R.id.right_left_subtitle);
        this.right_left_box = (CheckBox) this.findViewById(R.id.right_left_box);
        this.right_left_set = (LinearLayout) this.findViewById(R.id.right_left_set);
        this.right_left_open = (RelativeLayout) this.findViewById(R.id.right_left_open);
        this.right_left_open_box = (CheckBox) this.findViewById(R.id.right_left_open_box);
        this.right_left_url = (EditText) this.findViewById(R.id.right_left_url);
        this.right_left_image = (RelativeLayout) this.findViewById(R.id.right_left_image);
        this.right_left_imageview = (ImageView) this.findViewById(R.id.right_left_imageview);
        this.rightrightBtn = (RelativeLayout) this.findViewById(R.id.rightrightBtn);
        this.right_right_subtitle = (TextView) this.findViewById(R.id.right_right_subtitle);
        this.right_right_box = (CheckBox) this.findViewById(R.id.right_right_box);
        this.right_right_set = (LinearLayout) this.findViewById(R.id.right_right_set);
        this.right_right_open = (RelativeLayout) this.findViewById(R.id.right_right_open);
        this.right_right_open_box = (CheckBox) this.findViewById(R.id.right_right_open_box);
        this.right_right_url = (EditText) this.findViewById(R.id.right_right_url);
        this.right_right_image = (RelativeLayout) this.findViewById(R.id.right_right_image);
        this.right_right_imageview = (ImageView) this.findViewById(R.id.right_right_imageview);

        this.detail = (RelativeLayout) this.findViewById(R.id.detail);
        this.detail_subtitle = (TextView) this.findViewById(R.id.detail_subtitle);
        this.detail_box = (CheckBox) this.findViewById(R.id.detail_box);

        this.detail_open_in_browse = (RelativeLayout) this.findViewById(R.id.detail_open_in_browse);
        this.detail_open_subtitle = (TextView) this.findViewById(R.id.detail_open_subtitle);
        this.detail_open_box = (CheckBox) this.findViewById(R.id.detail_open_box);

        this.maintool = (RelativeLayout) this.findViewById(R.id.maintool);
        this.maintool_subtitle = (TextView) this.findViewById(R.id.maintool_subtitle);
        this.maintool_box = (CheckBox) this.findViewById(R.id.maintool_box);

        this.mainmsg = (RelativeLayout) this.findViewById(R.id.mainmsg);
        this.mainmsg_subtitle = (TextView) this.findViewById(R.id.mainmsg_subtitle);
        this.mainmsg_box = (CheckBox) this.findViewById(R.id.mainmsg_box);

        this.prompt = (RelativeLayout) this.findViewById(R.id.prompt);
        this.prompt_subtitle = (TextView) this.findViewById(R.id.prompt_subtitle);
        this.prompt_box = (CheckBox) this.findViewById(R.id.prompt_box);

        this.NavigationBtn = (RelativeLayout) this.findViewById(R.id.NavigationBtn);
        this.navigation_subtitle = (TextView) this.findViewById(R.id.navigation_subtitle);
        this.left_one_name = (EditText) this.findViewById(R.id.left_one_name);
        this.left_one_url = (EditText) this.findViewById(R.id.left_one_url);
        this.left_one_open_box = (CheckBox) this.findViewById(R.id.left_one_open_box);
        this.left_one_open = (RelativeLayout) this.findViewById(R.id.left_one_open);
        this.left_one_image = (RelativeLayout) this.findViewById(R.id.left_one_image);
        this.left_one_imageview = (ImageView) this.findViewById(R.id.left_one_imageview);
        this.left_one_box = (CheckBox) this.findViewById(R.id.left_one_box);
        this.left_one_set = (LinearLayout) this.findViewById(R.id.left_one_set);
        // left_one_url.setEnabled(false);

        this.BOT1 = (RelativeLayout) this.findViewById(R.id.BOT1);
        this.BOT1_subtitle = (TextView) this.findViewById(R.id.BOT1_subtitle);
        // BOT1_box = (CheckBox) findViewById(R.id.BOT1_box);
        this.left_two_name = (EditText) this.findViewById(R.id.left_two_name);
        this.left_two_url = (EditText) this.findViewById(R.id.left_two_url);
        this.left_two_open_box = (CheckBox) this.findViewById(R.id.left_two_open_box);
        this.left_two_open = (RelativeLayout) this.findViewById(R.id.left_two_open);
        this.left_two_image = (RelativeLayout) this.findViewById(R.id.left_two_image);
        this.left_two_imageview = (ImageView) this.findViewById(R.id.left_two_imageview);
        this.left_two_box = (CheckBox) this.findViewById(R.id.left_two_box);
        this.left_two_set = (LinearLayout) this.findViewById(R.id.left_two_set);

        this.BOT2 = (RelativeLayout) this.findViewById(R.id.BOT2);
        this.BOT2_subtitle = (TextView) this.findViewById(R.id.BOT2_subtitle);
        // BOT2_box = (CheckBox) findViewById(R.id.BOT2_box);
        this.middle_name = (EditText) this.findViewById(R.id.middle_name);
        this.middle_url = (EditText) this.findViewById(R.id.middle_url);
        this.middle_open_box = (CheckBox) this.findViewById(R.id.middle_open_box);
        this.middle_open = (RelativeLayout) this.findViewById(R.id.middle_open);
        this.middle_image = (RelativeLayout) this.findViewById(R.id.middle_image);
        this.middle_imageview = (ImageView) this.findViewById(R.id.middle_imageview);
        this.middle_box = (CheckBox) this.findViewById(R.id.middle_box);
        this.middle_set = (LinearLayout) this.findViewById(R.id.middle_set);

        this.BOT3 = (RelativeLayout) this.findViewById(R.id.BOT3);
        this.BOT3_subtitle = (TextView) this.findViewById(R.id.BOT3_subtitle);
        // BOT3_box = (CheckBox) findViewById(R.id.BOT3_box);
        this.right_two_name = (EditText) this.findViewById(R.id.right_two_name);
        this.right_two_url = (EditText) this.findViewById(R.id.right_two_url);
        this.right_two_open_box = (CheckBox) this.findViewById(R.id.right_two_open_box);
        this.right_two_open = (RelativeLayout) this.findViewById(R.id.right_two_open);
        this.right_two_image = (RelativeLayout) this.findViewById(R.id.right_two_image);
        this.right_two_imageview = (ImageView) this.findViewById(R.id.right_two_imageview);
        this.right_two_box = (CheckBox) this.findViewById(R.id.right_two_box);
        this.right_two_set = (LinearLayout) this.findViewById(R.id.right_two_set);

        this.MoreBtn = (RelativeLayout) this.findViewById(R.id.MoreBtn);
        this.more_subtitle = (TextView) this.findViewById(R.id.more_subtitle);
        // more_box = (CheckBox) findViewById(R.id.more_box);
        this.right_one_name = (EditText) this.findViewById(R.id.right_one_name);
        this.right_one_url = (EditText) this.findViewById(R.id.right_one_url);
        this.right_one_open_box = (CheckBox) this.findViewById(R.id.right_one_open_box);
        this.right_one_open = (RelativeLayout) this.findViewById(R.id.right_one_open);
        this.right_one_image = (RelativeLayout) this.findViewById(R.id.right_one_image);
        this.right_one_imageview = (ImageView) this.findViewById(R.id.right_one_imageview);
        this.right_one_box = (CheckBox) this.findViewById(R.id.right_one_box);
        this.right_one_set = (LinearLayout) this.findViewById(R.id.right_one_set);
        // right_one_url.setEnabled(false);

        this.mainmsgwidth = (RelativeLayout) this.findViewById(R.id.mainmsgwidth);
        this.mainmsgwidth_subtitle = (TextView) this.findViewById(R.id.mainmsgwidth_subtitle);
        this.mainmsg_name = (EditText) this.findViewById(R.id.mainmsg_name);
        this.mainmsg_url = (EditText) this.findViewById(R.id.mainmsg_url);

        this.detailshow = (RelativeLayout) this.findViewById(R.id.detailshow);
        this.detailshow_subtitle = (TextView) this.findViewById(R.id.detailshow_subtitle);

        this.showin = (RelativeLayout) this.findViewById(R.id.showin);
        this.showin_subtitle = (TextView) this.findViewById(R.id.showin_subtitle);

        this.buttonshow = (RelativeLayout) this.findViewById(R.id.buttonshow);
        this.buttonshow_subtitle = (TextView) this.findViewById(R.id.buttonshow_subtitle);

        menu_select_btn = (ImageView) this.findViewById(R.id.menu_select_btn);

        left_btn_url = (ImageView) this.findViewById(R.id.left_btn_url);
        right_btn_url = (ImageView) this.findViewById(R.id.right_btn_url);
        left_left_select_url = (ImageView) this.findViewById(R.id.left_left_select_url);
        left_right_select_url = (ImageView) this.findViewById(R.id.left_right_select_url);
        right_left_select_url = (ImageView) this.findViewById(R.id.right_left_select_url);
        right_right_select_url = (ImageView) this.findViewById(R.id.right_right_select_url);
        left_one_select_url = (ImageView) this.findViewById(R.id.left_one_select_url);
        left_two_select_url = (ImageView) this.findViewById(R.id.left_two_select_url);
        middle_select_url = (ImageView) this.findViewById(R.id.middle_select_url);
        right_two_select_url = (ImageView) this.findViewById(R.id.right_two_select_url);
        right_one_select_url = (ImageView) this.findViewById(R.id.right_one_select_url);
        popBtn_select_url = (ImageView) this.findViewById(R.id.popBtn_select_url);
        menuBtn_select_url = (ImageView) this.findViewById(R.id.menuBtn_select_url);


        this.title.setText("外观设置");
        menu_select_btn.setOnClickListener(this);
        left_btn_url.setOnClickListener(this);
        right_btn_url.setOnClickListener(this);
        left_left_select_url.setOnClickListener(this);
        left_right_select_url.setOnClickListener(this);
        right_left_select_url.setOnClickListener(this);
        right_right_select_url.setOnClickListener(this);
        left_one_select_url.setOnClickListener(this);
        left_two_select_url.setOnClickListener(this);
        middle_select_url.setOnClickListener(this);
        right_two_select_url.setOnClickListener(this);
        right_one_select_url.setOnClickListener(this);
        popBtn_select_url.setOnClickListener(this);
        menuBtn_select_url.setOnClickListener(this);

        this.mainrightBtn.setOnClickListener(this);
        this.mainleftBtn.setOnClickListener(this);
        this.mainrightBtn.setOnClickListener(this);
        this.main_left_open.setOnClickListener(this);
        this.main_right_open.setOnClickListener(this);
        this.main_left_image.setOnClickListener(this);
        this.main_right_image.setOnClickListener(this);
        this.leftleftBtn.setOnClickListener(this);
        this.leftrightBtn.setOnClickListener(this);
        this.left_left_open.setOnClickListener(this);
        this.left_right_open.setOnClickListener(this);
        this.left_left_image.setOnClickListener(this);
        this.left_right_image.setOnClickListener(this);
        this.rightleftBtn.setOnClickListener(this);
        this.rightrightBtn.setOnClickListener(this);
        this.right_left_open.setOnClickListener(this);
        this.right_right_open.setOnClickListener(this);
        this.right_left_image.setOnClickListener(this);
        this.right_right_image.setOnClickListener(this);
        this.left_one_image.setOnClickListener(this);
        this.left_two_image.setOnClickListener(this);
        this.middle_image.setOnClickListener(this);
        this.right_one_image.setOnClickListener(this);
        this.right_two_image.setOnClickListener(this);
        this.left_one_open.setOnClickListener(this);
        this.left_two_open.setOnClickListener(this);
        this.middle_open.setOnClickListener(this);
        this.right_two_open.setOnClickListener(this);
        this.right_one_open.setOnClickListener(this);
        this.NavigationBtn.setOnClickListener(this);
        this.BOT1.setOnClickListener(this);
        this.BOT3.setOnClickListener(this);
        this.BOT2.setOnClickListener(this);
        this.MoreBtn.setOnClickListener(this);
        this.prompt.setOnClickListener(this);
        this.mainmsg.setOnClickListener(this);
        this.maintool.setOnClickListener(this);
        this.detail.setOnClickListener(this);
        this.detail_open_in_browse.setOnClickListener(this);
        this.right.setOnClickListener(this);
        this.righttitle.setOnClickListener(this);
        this.right_title_open.setOnClickListener(this);
        this.left.setOnClickListener(this);
        this.lefttitle.setOnClickListener(this);
        this.left_title_open.setOnClickListener(this);
        this.mainmsgwidth.setOnClickListener(this);
        this.rightwidth.setOnClickListener(this);
        this.leftwidth.setOnClickListener(this);
        this.maintitle.setOnClickListener(this);
        this.touchmode.setOnClickListener(this);
        this.detailshow.setOnClickListener(this);
        this.showin.setOnClickListener(this);
        this.buttonshow.setOnClickListener(this);

        this.main_set.setOnClickListener(this);
        this.left_set.setOnClickListener(this);
        this.right_set.setOnClickListener(this);
        this.detail_set.setOnClickListener(this);
        this.bottomtool_set.setOnClickListener(this);
        this.bottommsg_set.setOnClickListener(this);
        this.downBtn.setOnClickListener(this);
        this.finishBtn.setOnClickListener(this);
    }

    public void initlayout() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter
                .addAction("SetMain" + getString(R.string.about_title));
        this.SetMain = new AppearanceSettingActivity.SetMain();
        this.registerReceiver(this.SetMain, intentFilter);
        this.initPath();
        int nVal = Integer.parseInt(this.IniFile.getIniString(userIni,
                "APPSHOW", "ShowMainTitle", "1", (byte) 0));
        if (nVal == 1) {
            this.title_box.setChecked(true);
        } else {
            this.mainleftBtn.setEnabled(false);
            this.mainrightBtn.setEnabled(false);
            this.title_subtitle.setText(R.string.app_off);
        }
        nVal = Integer.parseInt(this.IniFile.getIniString(userIni, "APPSHOW",
                "TouchMode", "0", (byte) 0));
        if (nVal == 0) {
            this.mode_subtitle.setText("边界可滑");
        } else {
            this.mode_subtitle.setText("全屏可滑");
        }
        nVal = Integer.parseInt(this.IniFile.getIniString(userIni, "APPSHOW",
                "ButtonMode", "0", (byte) 0));
        if (nVal == 0) {
            this.buttonshow_subtitle.setText("文字显示在底部");
        } else if (nVal == 1) {
            this.buttonshow_subtitle.setText("文字显示在右边");
        } else {
            this.buttonshow_subtitle.setText("不显示文字");
        }
        nVal = Integer.parseInt(this.IniFile.getIniString(userIni, "APPSHOW",
                "DetailShow", "0", (byte) 0));
        if (nVal == 0) {
            this.detailshow_subtitle.setText("显示在弹出窗口");
        } else if (nVal == 1) {
            this.detailshow_subtitle.setText("显示在底部信息区");
        } else {
            this.detailshow_subtitle.setText("显示在右窗口");
        }
        nVal = Integer.parseInt(this.IniFile.getIniString(userIni, "APPSHOW",
                "DetailShowIn", "1", (byte) 0));
        if (nVal == 0) {
            this.showin_subtitle.setText("在本窗口");
        } else if (nVal == 1) {
            this.showin_subtitle.setText("在弹出窗口");
        } else {
            this.showin_subtitle.setText("在其他窗口");
        }
        int splitratio = Integer.parseInt(this.IniFile.getIniString(userIni,
                "APPSHOW", "LeftWidth", "50", (byte) 0));
        int splitratio1 = Integer.parseInt(this.IniFile.getIniString(userIni,
                "APPSHOW", "RightWidth", "50", (byte) 0));
        int mainmsghight = Integer.parseInt(this.IniFile.getIniString(userIni,
                "APPSHOW", "PromptHight", "50", (byte) 0));
        this.mainmsgwidth_subtitle.setText("为高度屏幕的" + mainmsghight + "%");
        this.leftwidth_subtitle.setText("为屏幕宽度的" + splitratio + "%");
        this.rightwidth_subtitle.setText("为屏幕宽度的" + (100 - splitratio1) + "%");

        this.left_left_url.setText(this.IniFile.getIniString(userIni, "BUTTONURL",
                "TOP1", "", (byte) 0));
        this.left_right_url.setText(this.IniFile.getIniString(userIni, "BUTTONURL",
                "TOP2", "", (byte) 0));
        this.right_left_url.setText(this.IniFile.getIniString(userIni, "BUTTONURL",
                "TOP3", "", (byte) 0));
        this.right_right_url.setText(this.IniFile.getIniString(userIni, "BUTTONURL",
                "TOP4", "", (byte) 0));
        this.mainmsg_name.setText(this.IniFile.getIniString(userIni, "APPSHOW",
                "ShowMsg", "", (byte) 0));
        this.mainmsg_url.setText(this.IniFile.getIniString(userIni, "MSGURL",
                "PromptMsg", "", (byte) 0));
        this.left_left_imageview
                .setBackgroundResource(constants.TopButtonImageId[Integer
                        .parseInt(this.IniFile.getIniString(userIni, "BUTTON",
                                "TOP1ImageId", "0", (byte) 0))]);
        this.left_right_imageview
                .setBackgroundResource(constants.TopButtonImageId[Integer
                        .parseInt(this.IniFile.getIniString(userIni, "BUTTON",
                                "TOP2ImageId", "1", (byte) 0))]);
        this.right_left_imageview
                .setBackgroundResource(constants.TopButtonImageId[Integer
                        .parseInt(this.IniFile.getIniString(userIni, "BUTTON",
                                "TOP3ImageId", "2", (byte) 0))]);
        this.right_right_imageview
                .setBackgroundResource(constants.TopButtonImageId[Integer
                        .parseInt(this.IniFile.getIniString(userIni, "BUTTON",
                                "TOP4ImageId", "3", (byte) 0))]);
        this.main_right_imageview
                .setBackgroundResource(constants.TopButtonImageId[Integer
                        .parseInt(this.IniFile.getIniString(userIni, "BUTTON",
                                "MainRightImageId", "4", (byte) 0))]);
        this.main_left_imageview
                .setBackgroundResource(constants.TopButtonImageId[Integer
                        .parseInt(this.IniFile.getIniString(userIni, "BUTTON",
                                "MainLeftImageId", "5", (byte) 0))]);

        nVal = Integer.parseInt(this.IniFile.getIniString(userIni, "APPSHOW",
                "ShowLeftBar", "1", (byte) 0));
        if (nVal == 1) {
            this.main_left_open_box.setChecked(true);
        } else {
            this.main_left_subtitle.setText(R.string.app_off);
        }
        nVal = Integer.parseInt(this.IniFile.getIniString(userIni, "APPSHOW",
                "ShowRightBar", "1", (byte) 0));
        if (nVal == 1) {
            this.main_right_open_box.setChecked(true);
        } else {
            this.main_right_subtitle.setText(R.string.app_off);
        }

        nVal = Integer.parseInt(this.IniFile.getIniString(userIni, "APPSHOW",
                "MoreLeftBar", "0", (byte) 0));
        if (nVal == 1) {
            this.right_left_open_box.setChecked(true);
        } else {
            this.right_left_subtitle.setText(R.string.app_off);
        }
        nVal = Integer.parseInt(this.IniFile.getIniString(userIni, "APPSHOW",
                "MoreRightBar", "0", (byte) 0));
        if (nVal == 1) {
            this.right_right_open_box.setChecked(true);
        } else {
            this.right_right_subtitle.setText(R.string.app_off);
        }

        nVal = Integer.parseInt(this.IniFile.getIniString(userIni, "APPSHOW",
                "MenuLeftBar", "0", (byte) 0));
        if (nVal == 1) {
            this.left_left_open_box.setChecked(true);
        } else {
            this.left_left_subtitle.setText(R.string.app_off);
        }
        nVal = Integer.parseInt(this.IniFile.getIniString(userIni, "APPSHOW",
                "MenuRightBar", "0", (byte) 0));
        if (nVal == 1) {
            this.left_right_open_box.setChecked(true);
        } else {
            this.left_right_subtitle.setText(R.string.app_off);
        }
        nVal = Integer.parseInt(this.IniFile.getIniString(userIni, "APPSHOW",
                "ShowMenuTitle", "0", (byte) 0));
        this.left_title_name.setText(this.IniFile.getIniString(userIni, "APPSHOW",
                "MenuTitle", "", (byte) 0));
        if (nVal == 1) {
            this.left_title_open_box.setChecked(true);
        } else {
            this.lefttitle_subtitle.setText(R.string.app_off);
        }
        this.ShowMenu = Integer.parseInt(this.IniFile.getIniString(userIni,
                "APPSHOW", "ShowMenu", "0", (byte) 0));
        if (this.ShowMenu == 1) {
            this.left_box.setChecked(true);
        } else {
            this.left_subtitle.setText(R.string.app_off);
            this.lefttitle.setEnabled(false);
            this.leftleftBtn.setEnabled(false);
            this.leftrightBtn.setEnabled(false);
            this.leftwidth.setEnabled(false);
            this.left_default_url.setEnabled(false);
        }
        nVal = Integer.parseInt(this.IniFile.getIniString(userIni, "APPSHOW",
                "ShowMoreTitle", "0", (byte) 0));
        this.right_title_name.setText(this.IniFile.getIniString(userIni, "APPSHOW",
                "MoreTitle", "", (byte) 0));
        if (nVal == 1) {
            this.right_title_open_box.setChecked(true);
        } else {
            this.righttitle_subtitle.setText(R.string.app_off);
        }
        this.ShowMore = Integer.parseInt(this.IniFile.getIniString(userIni,
                "APPSHOW", "ShowMore", "0", (byte) 0));
        if (this.ShowMore == 1) {
            this.right_box.setChecked(true);
        } else {
            this.right_subtitle.setText(R.string.app_off);
            this.righttitle.setEnabled(false);
            this.rightleftBtn.setEnabled(false);
            this.rightrightBtn.setEnabled(false);
            this.rightwidth.setEnabled(false);
            this.right_default_url.setEnabled(false);
        }
        nVal = Integer.parseInt(this.IniFile.getIniString(userIni, "APPSHOW",
                "DetailTitle", "1", (byte) 0));
        if (nVal == 1) {
            this.detail_box.setChecked(true);
        } else {
            this.detail_subtitle.setText(R.string.app_off);
        }

        nVal = Integer.parseInt(this.IniFile.getIniString(userIni, "APPSHOW",
                "open_in_browse", "1", (byte) 0));
        if (nVal == 1) {
            this.detail_open_box.setChecked(true);
        } else {
            this.detail_open_subtitle.setText("不允许出现");
        }

        this.main_load_url.setText(this.IniFile.getIniString(userIni, "TBSAPP",
                "defaultUrl", "", (byte) 0));
        this.main_menu_url.setText(this.IniFile.getIniString(userIni, "TBSAPP",
                "defaultMenu", "", (byte) 0));
        this.main_left_btn.setText(this.IniFile.getIniString(userIni, "BUTTONURL",
                "MainLeft", "", (byte) 0));
        this.popBtn_url.setText(this.IniFile.getIniString(userIni, "BUTTONURL",
                "PopMenu", "", (byte) 0));
        this.menuBtn_url.setText(this.IniFile.getIniString(userIni, "BUTTONURL",
                "PopSysMenu", "", (byte) 0));
        this.main_right_btn.setText(this.IniFile.getIniString(userIni, "BUTTONURL",
                "MainRight", "", (byte) 0));
        this.left_default_url.setText(this.IniFile.getIniString(userIni, "TBSAPP",
                "leftUrl", "", (byte) 0));
        this.right_default_url.setText(this.IniFile.getIniString(userIni, "TBSAPP",
                "rightUrl", "", (byte) 0));
        nVal = Integer.parseInt(this.IniFile.getIniString(userIni, "APPSHOW",
                "ToolSet", "0", (byte) 0));
        if (nVal == 1) {
            this.maintool_box.setChecked(true);
        } else {
            this.maintool_subtitle.setText(R.string.app_off);
            this.buttonshow.setEnabled(false);
            this.NavigationBtn.setEnabled(false);
            this.BOT1.setEnabled(false);
            this.BOT2.setEnabled(false);
            this.BOT3.setEnabled(false);
            this.MoreBtn.setEnabled(false);
        }
        nVal = Integer.parseInt(this.IniFile.getIniString(userIni, "APPSHOW",
                "StatusLine", "0", (byte) 0));
        if (nVal == 1) {
            this.mainmsg_box.setChecked(true);
        } else {
            this.mainmsg_name.setEnabled(false);
            this.mainmsg_url.setEnabled(false);
            this.mainmsg_subtitle.setText(R.string.off);
        }
        nVal = Integer.parseInt(this.IniFile.getIniString(userIni, "APPSHOW",
                "Prompt", "0", (byte) 0));
        if (nVal == 1) {
            this.prompt_box.setChecked(true);
        } else {
            this.prompt_subtitle.setText(R.string.off);
            this.mainmsgwidth.setEnabled(false);
        }
        nVal = UIHelper.getShareperference(this,
                constants.SAVE_LOCALMSGNUM, "NavigationBtn", 1);
        if (nVal == 0) {
            this.navigation_subtitle.setText(R.string.off);
        } else {
            this.left_one_open_box.setChecked(true);
        }
        this.left_one_name.setText(this.IniFile.getIniString(userIni, "BUTTON",
                "NavigationTxt", "导航", (byte) 0));
        this.left_one_url.setText(this.IniFile.getIniString(userIni, "BUTTONURL",
                "BOT1", "", (byte) 0));
        this.left_one_imageview
                .setBackgroundResource(constants.ButtonImageId[Integer
                        .parseInt(this.IniFile.getIniString(userIni, "BUTTON",
                                "NavigationImageId", "0", (byte) 0))]);
        nVal = Integer.parseInt(this.IniFile.getIniString(userIni, "APPSHOW",
                "ShowBOT1", "0", (byte) 0));
        if (nVal == 0) {
            this.BOT1_subtitle.setText(R.string.off);
        } else {
            this.left_two_open_box.setChecked(true);
        }
        this.left_two_name.setText(this.IniFile.getIniString(userIni, "BUTTON",
                "BOT1Txt", "浏览", (byte) 0));
        this.left_two_url.setText(this.IniFile.getIniString(userIni, "BUTTONURL",
                "BOT2", "", (byte) 0));
        this.left_two_imageview
                .setBackgroundResource(constants.ButtonImageId[Integer
                        .parseInt(this.IniFile.getIniString(userIni, "BUTTON",
                                "BOT1ImageId", "1", (byte) 0))]);
        nVal = Integer.parseInt(this.IniFile.getIniString(userIni, "APPSHOW",
                "ShowBOT2", "0", (byte) 0));
        if (nVal == 0) {
            this.BOT2_subtitle.setText(R.string.off);
        } else {
            this.middle_open_box.setChecked(true);
        }
        this.middle_name.setText(this.IniFile.getIniString(userIni, "BUTTON",
                "BOT2Txt", "检索", (byte) 0));
        this.middle_url.setText(this.IniFile.getIniString(userIni, "BUTTONURL",
                "BOT3", "", (byte) 0));
        this.middle_imageview.setBackgroundResource(constants.ButtonImageId[Integer
                .parseInt(this.IniFile.getIniString(userIni, "BUTTON",
                        "BOT2ImageId", "2", (byte) 0))]);
        nVal = Integer.parseInt(this.IniFile.getIniString(userIni, "APPSHOW",
                "ShowBOT3", "0", (byte) 0));
        if (nVal == 0) {
            this.BOT3_subtitle.setText(R.string.off);
        } else {
            this.right_two_open_box.setChecked(true);
        }
        this.right_two_name.setText(this.IniFile.getIniString(userIni, "BUTTON",
                "BOT3Txt", "订阅", (byte) 0));
        this.right_two_url.setText(this.IniFile.getIniString(userIni, "BUTTONURL",
                "BOT4", "", (byte) 0));
        this.right_two_imageview
                .setBackgroundResource(constants.ButtonImageId[Integer
                        .parseInt(this.IniFile.getIniString(userIni, "BUTTON",
                                "BOT3ImageId", "3", (byte) 0))]);
        nVal = UIHelper.getShareperference(this,
                constants.SAVE_LOCALMSGNUM, "MoreBtn", 1);
        if (nVal == 0) {
            this.more_subtitle.setText(R.string.off);
        } else {
            this.right_one_open_box.setChecked(true);
        }
        this.right_one_name.setText(this.IniFile.getIniString(userIni, "BUTTON",
                "MoreBtnTxt", "更多", (byte) 0));
        this.right_one_url.setText(this.IniFile.getIniString(userIni, "BUTTONURL",
                "BOT5", "", (byte) 0));
        this.right_one_imageview
                .setBackgroundResource(constants.ButtonImageId[Integer
                        .parseInt(this.IniFile.getIniString(userIni, "BUTTON",
                                "MoreBtnImageId", "4", (byte) 0))]);
        if (this.ShowMenu == 0 && this.ShowMore == 0) {
            this.touchmode.setVisibility(View.GONE);
        }
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
        String appNewsFile = webRoot
                + this.IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        userIni = appNewsFile;
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

    private void setButtonNature() {
        this.initPath();
        this.IniFile.writeIniString(userIni, "TBSAPP", "defaultUrl",
                this.main_load_url.getText().toString());
        this.IniFile.writeIniString(userIni, "TBSAPP", "defaultMenu",
                this.main_menu_url.getText().toString());
        this.IniFile.writeIniString(userIni, "BUTTONURL", "MainLeft",
                this.main_left_btn.getText().toString());
        this.IniFile.writeIniString(userIni, "BUTTONURL", "PopMenu", this.popBtn_url
                .getText().toString());
        this.IniFile.writeIniString(userIni, "BUTTONURL", "PopSysMenu",
                this.menuBtn_url.getText().toString());
        this.IniFile.writeIniString(userIni, "BUTTONURL", "MainRight",
                this.main_right_btn.getText().toString());
        this.IniFile.writeIniString(userIni, "TBSAPP", "leftUrl",
                this.left_default_url.getText().toString());
        this.IniFile.writeIniString(userIni, "TBSAPP", "rightUrl",
                this.right_default_url.getText().toString());
        this.IniFile.writeIniString(userIni, "APPSHOW", "MenuTitle",
                this.left_title_name.getText().toString());
        this.IniFile.writeIniString(userIni, "APPSHOW", "MoreTitle",
                this.right_title_name.getText().toString());
        this.IniFile.writeIniString(userIni, "BUTTONURL", "TOP1", this.left_left_url
                .getText().toString());
        this.IniFile.writeIniString(userIni, "BUTTONURL", "TOP2", this.left_right_url
                .getText().toString());
        this.IniFile.writeIniString(userIni, "BUTTONURL", "TOP3", this.right_left_url
                .getText().toString());
        this.IniFile.writeIniString(userIni, "BUTTONURL", "TOP4",
                this.right_right_url.getText().toString());
        this.IniFile.writeIniString(userIni, "BUTTON", "BOT1Txt", this.left_two_name
                .getText().toString());
        this.IniFile.writeIniString(userIni, "BUTTONURL", "BOT2", this.left_two_url
                .getText().toString());
        this.IniFile.writeIniString(userIni, "BUTTON", "BOT2Txt", this.middle_name
                .getText().toString());
        this.IniFile.writeIniString(userIni, "BUTTONURL", "BOT3", this.middle_url
                .getText().toString());
        this.IniFile.writeIniString(userIni, "BUTTON", "BOT3Txt", this.right_two_name
                .getText().toString());
        this.IniFile.writeIniString(userIni, "BUTTONURL", "BOT4", this.right_two_url
                .getText().toString());
        this.IniFile.writeIniString(userIni, "BUTTON", "MoreBtnTxt",
                this.right_one_name.getText().toString());
        this.IniFile.writeIniString(userIni, "BUTTONURL", "BOT5", this.right_one_url
                .getText().toString());
        this.IniFile.writeIniString(userIni, "BUTTON", "NavigationTxt",
                this.left_one_name.getText().toString());
        this.IniFile.writeIniString(userIni, "BUTTONURL", "BOT1", this.left_one_url
                .getText().toString());
        this.IniFile.writeIniString(userIni, "APPSHOW", "ShowMsg", this.mainmsg_name
                .getText().toString());
        this.IniFile.writeIniString(userIni, "MSGURL", "PromptMsg", this.mainmsg_url
                .getText().toString());
        if (this.left_one_open_box.isChecked()) {
            UIHelper.setSharePerference(this,
                    constants.SAVE_LOCALMSGNUM, "NavigationBtn", 1);
        } else {
            UIHelper.setSharePerference(this,
                    constants.SAVE_LOCALMSGNUM, "NavigationBtn", 0);
        }
        if (this.right_one_open_box.isChecked()) {
            UIHelper.setSharePerference(this,
                    constants.SAVE_LOCALMSGNUM, "MoreBtn", 1);
        } else {
            UIHelper.setSharePerference(this,
                    constants.SAVE_LOCALMSGNUM, "MoreBtn", 0);
        }
        if (this.right_two_open_box.isChecked()) {
            this.IniFile.writeIniString(userIni, "APPSHOW", "ShowBOT3", "1");
        } else {
            this.IniFile.writeIniString(userIni, "APPSHOW", "ShowBOT3", "0");
        }
        if (this.right_two_open_box.isChecked()) {
            this.IniFile.writeIniString(userIni, "APPSHOW", "ShowBOT3", "1");
        } else {
            this.IniFile.writeIniString(userIni, "APPSHOW", "ShowBOT3", "0");
        }
        if (this.left_two_open_box.isChecked()) {
            this.IniFile.writeIniString(userIni, "APPSHOW", "ShowBOT1", "1");
        } else {
            this.IniFile.writeIniString(userIni, "APPSHOW", "ShowBOT1", "0");
        }
        if (this.middle_open_box.isChecked()) {
            this.IniFile.writeIniString(userIni, "APPSHOW", "ShowBOT2", "1");
        } else {
            this.IniFile.writeIniString(userIni, "APPSHOW", "ShowBOT2", "0");
        }
        if (this.right_title_open_box.isChecked()) {
            this.IniFile.writeIniString(userIni, "APPSHOW", "ShowMoreTitle", "1");
        } else {
            this.IniFile.writeIniString(userIni, "APPSHOW", "ShowMoreTitle", "0");
        }
        if (this.left_title_open_box.isChecked()) {
            this.IniFile.writeIniString(userIni, "APPSHOW", "ShowMenuTitle", "1");
        } else {
            this.IniFile.writeIniString(userIni, "APPSHOW", "ShowMenuTitle", "0");
        }
        if (this.main_left_open_box.isChecked()) {
            this.IniFile.writeIniString(userIni, "APPSHOW", "ShowLeftBar", "1");
        } else {
            this.IniFile.writeIniString(userIni, "APPSHOW", "ShowLeftBar", "0");
        }
        if (this.main_right_open_box.isChecked()) {
            this.IniFile.writeIniString(userIni, "APPSHOW", "ShowRightBar", "1");
        } else {
            this.IniFile.writeIniString(userIni, "APPSHOW", "ShowRightBar", "0");
        }
        if (this.left_left_open_box.isChecked()) {
            this.IniFile.writeIniString(userIni, "APPSHOW", "MenuLeftBar", "1");
        } else {
            this.IniFile.writeIniString(userIni, "APPSHOW", "MenuLeftBar", "0");
        }
        if (this.left_right_open_box.isChecked()) {
            this.IniFile.writeIniString(userIni, "APPSHOW", "MenuRightBar", "1");
        } else {
            this.IniFile.writeIniString(userIni, "APPSHOW", "MenuRightBar", "0");
        }
        if (this.right_left_open_box.isChecked()) {
            this.IniFile.writeIniString(userIni, "APPSHOW", "MoreLeftBar", "1");
        } else {
            this.IniFile.writeIniString(userIni, "APPSHOW", "MoreLeftBar", "0");
        }
        if (this.right_right_open_box.isChecked()) {
            this.IniFile.writeIniString(userIni, "APPSHOW", "MoreRightBar", "1");
        } else {
            this.IniFile.writeIniString(userIni, "APPSHOW", "MoreRightBar", "0");
        }
        if (this.title_box.isChecked()) {
            this.IniFile.writeIniString(userIni, "APPSHOW", "ShowMainTitle", "1");
        } else {
            this.IniFile.writeIniString(userIni, "APPSHOW", "ShowMainTitle", "0");
        }
        Intent intent1 = new Intent();
        intent1.setAction("Action_main" + getString(R.string.about_title));
        intent1.putExtra("flag", 13);
        this.sendBroadcast(intent1);
        Intent intent = new Intent();
        intent.setAction("loadView" + getString(R.string.about_title));
        intent.putExtra("flag", 5);
        intent.putExtra("author", 1);
        this.sendBroadcast(intent);
        intent = new Intent();
        intent.setAction("loadRight" + getString(R.string.about_title));
        intent.putExtra("flag", 4);
        this.sendBroadcast(intent);
        intent = new Intent();
        intent.setAction("loadLeft" + getString(R.string.about_title));
        intent.putExtra("flag", 3);
        this.sendBroadcast(intent);
    }

    private void TopbuttonImageDialog(final int count) {
        LayoutInflater factory = LayoutInflater.from(this);// 提示框
        View view = factory.inflate(R.layout.button_image_set, null);// 这里必须是final的
        GridView categoryList = (GridView) view
                .findViewById(R.id.button_gridview);// 获得AppCategorylistItems对象
        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @SuppressLint("ShowToast")
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent intent;
                switch (count) {
                    case 0:
                        AppearanceSettingActivity.this.left_left_imageview
                                .setBackgroundResource(constants.TopButtonImageId[arg2]);
                        AppearanceSettingActivity.this.IniFile.writeIniString(userIni, "BUTTON",
                                "TOP1ImageId", arg2 + "");
                        intent = new Intent();
                        intent.setAction("loadLeft"
                                + getString(R.string.about_title));
                        intent.putExtra("flag", 4);
                        AppearanceSettingActivity.this.sendBroadcast(intent);
                        break;
                    case 1:
                        AppearanceSettingActivity.this.left_right_imageview
                                .setBackgroundResource(constants.TopButtonImageId[arg2]);
                        AppearanceSettingActivity.this.IniFile.writeIniString(userIni, "BUTTON",
                                "TOP2ImageId", arg2 + "");
                        intent = new Intent();
                        intent.setAction("loadLeft"
                                + getString(R.string.about_title));
                        intent.putExtra("flag", 4);
                        AppearanceSettingActivity.this.sendBroadcast(intent);
                        break;
                    case 2:
                        AppearanceSettingActivity.this.right_left_imageview
                                .setBackgroundResource(constants.TopButtonImageId[arg2]);
                        AppearanceSettingActivity.this.IniFile.writeIniString(userIni, "BUTTON",
                                "TOP3ImageId", arg2 + "");
                        intent = new Intent();
                        intent.setAction("loadRight"
                                + getString(R.string.about_title));
                        intent.putExtra("flag", 5);
                        AppearanceSettingActivity.this.sendBroadcast(intent);
                        break;
                    case 3:
                        AppearanceSettingActivity.this.right_right_imageview
                                .setBackgroundResource(constants.TopButtonImageId[arg2]);
                        AppearanceSettingActivity.this.IniFile.writeIniString(userIni, "BUTTON",
                                "TOP4ImageId", arg2 + "");
                        intent = new Intent();
                        intent.setAction("loadRight"
                                + getString(R.string.about_title));
                        intent.putExtra("flag", 5);
                        AppearanceSettingActivity.this.sendBroadcast(intent);
                        break;
                    case 4:
                        AppearanceSettingActivity.this.main_right_imageview
                                .setBackgroundResource(constants.TopButtonImageId[arg2]);
                        AppearanceSettingActivity.this.IniFile.writeIniString(userIni, "BUTTON",
                                "MainRightImageId", arg2 + "");
                        intent = new Intent();
                        intent.setAction("Action_main"
                                + getString(R.string.about_title));
                        intent.putExtra("flag", 3);
                        AppearanceSettingActivity.this.sendBroadcast(intent);
                        break;
                    case 5:
                        AppearanceSettingActivity.this.main_left_imageview
                                .setBackgroundResource(constants.TopButtonImageId[arg2]);
                        AppearanceSettingActivity.this.IniFile.writeIniString(userIni, "BUTTON",
                                "MainLeftImageId", arg2 + "");
                        intent = new Intent();
                        intent.setAction("Action_main"
                                + getString(R.string.about_title));
                        intent.putExtra("flag", 3);
                        AppearanceSettingActivity.this.sendBroadcast(intent);
                        break;
                }
                AppearanceSettingActivity.this.ModifyDialog.dismiss();
            }
        });
        switch (count) {
            case 0:
                this.ButtonmAdapter = new ButtonGridviewAdapter(this,
                        constants.TopButtonImageId, Integer.parseInt(this.IniFile
                        .getIniString(userIni, "BUTTON", "TOP1ImageId",
                                "0", (byte) 0)));
                break;
            case 1:
                this.ButtonmAdapter = new ButtonGridviewAdapter(this,
                        constants.TopButtonImageId, Integer.parseInt(this.IniFile
                        .getIniString(userIni, "BUTTON", "TOP2ImageId",
                                "1", (byte) 0)));
                break;
            case 2:
                this.ButtonmAdapter = new ButtonGridviewAdapter(this,
                        constants.TopButtonImageId, Integer.parseInt(this.IniFile
                        .getIniString(userIni, "BUTTON", "TOP3ImageId",
                                "2", (byte) 0)));
                break;
            case 3:
                this.ButtonmAdapter = new ButtonGridviewAdapter(this,
                        constants.TopButtonImageId, Integer.parseInt(this.IniFile
                        .getIniString(userIni, "BUTTON", "TOP4ImageId",
                                "3", (byte) 0)));
                break;
            case 4:
                this.ButtonmAdapter = new ButtonGridviewAdapter(this,
                        constants.TopButtonImageId, Integer.parseInt(this.IniFile
                        .getIniString(userIni, "BUTTON",
                                "MainRightImageId", "4", (byte) 0)));
                break;
            case 5:
                this.ButtonmAdapter = new ButtonGridviewAdapter(this,
                        constants.TopButtonImageId, Integer.parseInt(this.IniFile
                        .getIniString(userIni, "BUTTON",
                                "MainLeftImageId", "5", (byte) 0)));
                break;
        }
        categoryList.setAdapter(this.ButtonmAdapter);
        Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择图片").setCancelable(false)// 提示框标题
                .setView(view).setPositiveButton("取消", null);
        this.ModifyDialog = builder.create();
        this.ModifyDialog.show();
    }

    private void buttonImageDialog(final int count) {
        LayoutInflater factory = LayoutInflater.from(this);// 提示框
        View view = factory.inflate(R.layout.button_image_set, null);// 这里必须是final的
        GridView categoryList = (GridView) view
                .findViewById(R.id.button_gridview);// 获得AppCategorylistItems对象
        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @SuppressLint("ShowToast")
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                switch (count) {
                    case 0:
                        AppearanceSettingActivity.this.left_one_imageview
                                .setBackgroundResource(constants.ButtonImageId[arg2]);
                        AppearanceSettingActivity.this.IniFile.writeIniString(userIni, "BUTTON",
                                "NavigationImageId", arg2 + "");
                        break;
                    case 1:
                        AppearanceSettingActivity.this.left_two_imageview
                                .setBackgroundResource(constants.ButtonImageId[arg2]);
                        AppearanceSettingActivity.this.IniFile.writeIniString(userIni, "BUTTON",
                                "BOT1ImageId", arg2 + "");
                        break;
                    case 2:
                        AppearanceSettingActivity.this.middle_imageview
                                .setBackgroundResource(constants.ButtonImageId[arg2]);
                        AppearanceSettingActivity.this.IniFile.writeIniString(userIni, "BUTTON",
                                "BOT2ImageId", arg2 + "");
                        break;
                    case 3:
                        AppearanceSettingActivity.this.right_two_imageview
                                .setBackgroundResource(constants.ButtonImageId[arg2]);
                        AppearanceSettingActivity.this.IniFile.writeIniString(userIni, "BUTTON",
                                "BOT3ImageId", arg2 + "");
                        break;
                    case 4:
                        AppearanceSettingActivity.this.right_one_imageview
                                .setBackgroundResource(constants.ButtonImageId[arg2]);
                        AppearanceSettingActivity.this.IniFile.writeIniString(userIni, "BUTTON",
                                "MoreBtnImageId", arg2 + "");
                        break;
                }
                Intent intent = new Intent();
                intent.setAction("loadView"
                        + getString(R.string.about_title));
                intent.putExtra("flag", 9);
                intent.putExtra("author", 0);
                AppearanceSettingActivity.this.sendBroadcast(intent);
                AppearanceSettingActivity.this.ModifyDialog.dismiss();
            }
        });
        switch (count) {
            case 0:
                this.ButtonmAdapter = new ButtonGridviewAdapter(this,
                        constants.ButtonImageId, Integer.parseInt(this.IniFile
                        .getIniString(userIni, "BUTTON",
                                "NavigationImageId", "0", (byte) 0)));
                break;
            case 1:
                this.ButtonmAdapter = new ButtonGridviewAdapter(this,
                        constants.ButtonImageId, Integer.parseInt(this.IniFile
                        .getIniString(userIni, "BUTTON", "BOT1ImageId",
                                "1", (byte) 0)));
                break;
            case 2:
                this.ButtonmAdapter = new ButtonGridviewAdapter(this,
                        constants.ButtonImageId, Integer.parseInt(this.IniFile
                        .getIniString(userIni, "BUTTON", "BOT2ImageId",
                                "2", (byte) 0)));
                break;
            case 3:
                this.ButtonmAdapter = new ButtonGridviewAdapter(this,
                        constants.ButtonImageId, Integer.parseInt(this.IniFile
                        .getIniString(userIni, "BUTTON", "BOT3ImageId",
                                "3", (byte) 0)));
                break;
            case 4:
                this.ButtonmAdapter = new ButtonGridviewAdapter(this,
                        constants.ButtonImageId, Integer.parseInt(this.IniFile
                        .getIniString(userIni, "BUTTON",
                                "MoreBtnImageId", "4", (byte) 0)));
                break;
        }
        categoryList.setAdapter(this.ButtonmAdapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择图片").setCancelable(false)// 提示框标题
                .setView(view).setPositiveButton("取消", null);
        this.ModifyDialog = builder.create();
        this.ModifyDialog.show();
    }

    @Override
    public void onClick(View v) {
        this.initPath();
        Intent intent = new Intent();
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.left_left_select_url:
                showSelectActionView(left_left_url, 4);
                break;
            case R.id.left_right_select_url:
                showSelectActionView(left_right_url, 4);
                break;
            case R.id.right_left_select_url:
                showSelectActionView(right_left_url, 4);
                break;
            case R.id.right_right_select_url:
                showSelectActionView(right_right_url, 4);
                break;
            case R.id.left_one_select_url:
                showSelectActionView(left_one_url, 1);
                break;
            case R.id.left_two_select_url:
                showSelectActionView(left_two_url, 1);
                break;
            case R.id.middle_select_url:
                showSelectActionView(middle_url, 1);
                break;
            case R.id.right_two_select_url:
                showSelectActionView(right_two_url, 1);
                break;
            case R.id.right_one_select_url:
                showSelectActionView(right_one_url, 1);
                break;
            case R.id.popBtn_select_url:
                showSelectActionView(popBtn_url, 5);
                break;
            case R.id.menuBtn_select_url:
                showSelectActionView(menuBtn_url, 5);
                break;
            case R.id.menu_select_btn:
                showSelectActionView(main_menu_url, 5);
                break;
            case R.id.left_btn_url:
                showSelectActionView(main_left_btn, 7);
                break;
            case R.id.right_btn_url:
                showSelectActionView(main_right_btn, 7);
                break;
            case R.id.main_left_open:
                if (this.main_left_open_box.isChecked()) {
                    this.main_left_open_box.setChecked(false);
                    this.main_left_subtitle.setText(R.string.off);
                } else {
                    this.main_left_open_box.setChecked(true);
                    this.main_left_subtitle.setText(R.string.on);
                }
                break;
            case R.id.main_right_open:
                if (this.main_right_open_box.isChecked()) {
                    this.main_right_open_box.setChecked(false);
                    this.main_right_subtitle.setText(R.string.off);
                } else {
                    this.main_right_open_box.setChecked(true);
                    this.main_right_subtitle.setText(R.string.on);
                }
                break;
            case R.id.right_left_open:
                if (this.right_left_open_box.isChecked()) {
                    this.right_left_open_box.setChecked(false);
                    this.right_left_subtitle.setText(R.string.off);
                } else {
                    this.right_left_open_box.setChecked(true);
                    this.right_left_subtitle.setText(R.string.on);
                }
                break;
            case R.id.right_right_open:
                if (this.right_right_open_box.isChecked()) {
                    this.right_right_open_box.setChecked(false);
                    this.right_right_subtitle.setText(R.string.off);
                } else {
                    this.right_right_open_box.setChecked(true);
                    this.right_right_subtitle.setText(R.string.on);
                }
                break;
            case R.id.rightleftBtn:
                if (this.right_left_box.isChecked()) {
                    this.right_left_box.setChecked(false);
                    this.right_left_set.setVisibility(View.GONE);
                } else {
                    this.right_left_box.setChecked(true);
                    this.right_left_set.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.mainleftBtn:
                if (this.main_left_box.isChecked()) {
                    this.main_left_box.setChecked(false);
                    this.main_left_set.setVisibility(View.GONE);
                } else {
                    this.main_left_box.setChecked(true);
                    this.main_left_set.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.mainrightBtn:
                if (this.main_right_box.isChecked()) {
                    this.main_right_box.setChecked(false);
                    this.main_right_set.setVisibility(View.GONE);
                } else {
                    this.main_right_box.setChecked(true);
                    this.main_right_set.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.rightrightBtn:
                if (this.right_right_box.isChecked()) {
                    this.right_right_box.setChecked(false);
                    this.right_right_set.setVisibility(View.GONE);
                } else {
                    this.right_right_box.setChecked(true);
                    this.right_right_set.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.left_left_open:
                if (this.left_left_open_box.isChecked()) {
                    this.left_left_open_box.setChecked(false);
                    this.left_left_subtitle.setText(R.string.off);
                } else {
                    this.left_left_open_box.setChecked(true);
                    this.left_left_subtitle.setText(R.string.on);
                }
                break;
            case R.id.left_right_open:
                if (this.left_right_open_box.isChecked()) {
                    this.left_right_open_box.setChecked(false);
                    this.left_right_subtitle.setText(R.string.off);
                } else {
                    this.left_right_open_box.setChecked(true);
                    this.left_right_subtitle.setText(R.string.on);
                }
                break;
            case R.id.leftleftBtn:
                if (this.left_left_box.isChecked()) {
                    this.left_left_box.setChecked(false);
                    this.left_left_set.setVisibility(View.GONE);
                } else {
                    this.left_left_box.setChecked(true);
                    this.left_left_set.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.leftrightBtn:
                if (this.left_right_box.isChecked()) {
                    this.left_right_box.setChecked(false);
                    this.left_right_set.setVisibility(View.GONE);
                } else {
                    this.left_right_box.setChecked(true);
                    this.left_right_set.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.left_left_image:
                this.TopbuttonImageDialog(0);
                break;
            case R.id.left_right_image:
                this.TopbuttonImageDialog(1);
                break;
            case R.id.right_left_image:
                this.TopbuttonImageDialog(2);
                break;
            case R.id.right_right_image:
                this.TopbuttonImageDialog(3);
                break;
            case R.id.main_right_image:
                this.TopbuttonImageDialog(4);
                break;
            case R.id.main_left_image:
                this.TopbuttonImageDialog(5);
                break;
            case R.id.left_one_image:
                this.buttonImageDialog(0);
                break;
            case R.id.left_two_image:
                this.buttonImageDialog(1);
                break;
            case R.id.middle_image:
                this.buttonImageDialog(2);
                break;
            case R.id.right_two_image:
                this.buttonImageDialog(3);
                break;
            case R.id.right_one_image:
                this.buttonImageDialog(4);
                break;
            case R.id.left_one_open:
                if (this.left_one_open_box.isChecked()) {
                    this.left_one_open_box.setChecked(false);
                    this.navigation_subtitle.setText(R.string.off);
                } else {
                    this.left_one_open_box.setChecked(true);
                    this.navigation_subtitle.setText(R.string.on);
                }
                break;
            case R.id.left_two_open:
                if (this.left_two_open_box.isChecked()) {
                    this.left_two_open_box.setChecked(false);
                    this.BOT1_subtitle.setText(R.string.off);
                } else {
                    this.left_two_open_box.setChecked(true);
                    this.BOT1_subtitle.setText(R.string.on);
                }
                break;
            case R.id.middle_open:
                if (this.middle_open_box.isChecked()) {
                    this.middle_open_box.setChecked(false);
                    this.BOT2_subtitle.setText(R.string.off);
                } else {
                    this.middle_open_box.setChecked(true);
                    this.BOT2_subtitle.setText(R.string.on);
                }
                break;
            case R.id.right_one_open:
                if (this.right_one_open_box.isChecked()) {
                    this.right_one_open_box.setChecked(false);
                    this.BOT3_subtitle.setText(R.string.off);
                } else {
                    this.right_one_open_box.setChecked(true);
                    this.BOT3_subtitle.setText(R.string.on);
                }
                break;
            case R.id.right_two_open:
                if (this.right_two_open_box.isChecked()) {
                    this.right_two_open_box.setChecked(false);
                    this.more_subtitle.setText(R.string.off);
                } else {
                    this.right_two_open_box.setChecked(true);
                    this.more_subtitle.setText(R.string.on);
                }
                break;
            case R.id.NavigationBtn:
                if (this.left_one_box.isChecked()) {
                    this.left_one_box.setChecked(false);
                    this.left_one_set.setVisibility(View.GONE);
                } else {
                    this.left_one_box.setChecked(true);
                    this.left_one_set.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.BOT1:
                if (this.left_two_box.isChecked()) {
                    this.left_two_box.setChecked(false);
                    this.left_two_set.setVisibility(View.GONE);
                } else {
                    this.left_two_box.setChecked(true);
                    this.left_two_set.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.BOT2:
                if (this.middle_box.isChecked()) {
                    this.middle_box.setChecked(false);
                    this.middle_set.setVisibility(View.GONE);
                } else {
                    this.middle_box.setChecked(true);
                    this.middle_set.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.BOT3:
                if (this.right_two_box.isChecked()) {
                    this.right_two_box.setChecked(false);
                    this.right_two_set.setVisibility(View.GONE);
                } else {
                    this.right_two_box.setChecked(true);
                    this.right_two_set.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.MoreBtn:
                if (this.right_one_box.isChecked()) {
                    this.right_one_box.setChecked(false);
                    this.right_one_set.setVisibility(View.GONE);
                } else {
                    this.right_one_box.setChecked(true);
                    this.right_one_set.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.prompt:
                if (this.prompt_box.isChecked()) {
                    this.prompt_subtitle.setText(R.string.off);
                    this.prompt_box.setChecked(false);
                    this.mainmsgwidth.setEnabled(false);
                    this.IniFile.writeIniString(userIni, "APPSHOW", "Prompt", "0");
                    intent.setAction("loadView"
                            + getString(R.string.about_title));
                    intent.putExtra("flag", 7);
                    intent.putExtra("author", 2);
                    this.sendBroadcast(intent);
                } else {
                    this.prompt_subtitle.setText(R.string.on);
                    this.prompt_box.setChecked(true);
                    this.mainmsgwidth.setEnabled(true);
                    this.IniFile.writeIniString(userIni, "APPSHOW", "Prompt", "1");
                    intent.setAction("loadView"
                            + getString(R.string.about_title));
                    intent.putExtra("flag", 7);
                    intent.putExtra("author", 1);
                    this.sendBroadcast(intent);
                }
                break;
            case R.id.mainmsg:
                if (this.mainmsg_box.isChecked()) {
                    this.mainmsg_subtitle.setText(R.string.off);
                    this.mainmsg_box.setChecked(false);
                    this.mainmsg_name.setEnabled(false);
                    this.mainmsg_url.setEnabled(false);
                    this.IniFile.writeIniString(userIni, "APPSHOW", "StatusLine",
                            "0");
                    intent.setAction("loadView"
                            + getString(R.string.about_title));
                    intent.putExtra("flag", 4);
                    intent.putExtra("author", 1);
                    this.sendBroadcast(intent);
                } else {
                    this.mainmsg_subtitle.setText(R.string.on);
                    this.mainmsg_box.setChecked(true);
                    this.mainmsg_name.setEnabled(true);
                    this.mainmsg_url.setEnabled(true);
                    this.IniFile.writeIniString(userIni, "APPSHOW", "StatusLine",
                            "1");
                    intent.setAction("loadView"
                            + getString(R.string.about_title));
                    intent.putExtra("flag", 4);
                    intent.putExtra("author", 2);
                    this.sendBroadcast(intent);
                }
                break;
            case R.id.maintool:
                if (this.maintool_box.isChecked()) {
                    this.maintool_subtitle.setText(R.string.app_off);
                    this.maintool_box.setChecked(false);
                    this.buttonshow.setEnabled(false);
                    this.NavigationBtn.setEnabled(false);
                    this.BOT1.setEnabled(false);
                    this.BOT2.setEnabled(false);
                    this.BOT3.setEnabled(false);
                    this.MoreBtn.setEnabled(false);
                    this.IniFile.writeIniString(userIni, "APPSHOW", "ToolSet", "0");
                    intent.setAction("loadView"
                            + getString(R.string.about_title));
                    intent.putExtra("flag", 10);
                    intent.putExtra("author", 1);
                    this.sendBroadcast(intent);
                } else {
                    this.maintool_subtitle.setText(R.string.app_on);
                    this.maintool_box.setChecked(true);
                    this.NavigationBtn.setEnabled(true);
                    this.buttonshow.setEnabled(true);
                    this.BOT1.setEnabled(true);
                    this.BOT2.setEnabled(true);
                    this.BOT3.setEnabled(true);
                    this.MoreBtn.setEnabled(true);
                    this.IniFile.writeIniString(userIni, "APPSHOW", "ToolSet", "1");
                    intent.setAction("loadView"
                            + getString(R.string.about_title));
                    intent.putExtra("flag", 10);
                    intent.putExtra("author", 2);
                    this.sendBroadcast(intent);
                }
                break;
            case R.id.detail:
                if (this.detail_box.isChecked()) {
                    this.detail_subtitle.setText(R.string.app_off);
                    this.detail_box.setChecked(false);
                    this.IniFile.writeIniString(userIni, "APPSHOW", "DetailTitle",
                            "0");
                } else {
                    this.detail_subtitle.setText(R.string.app_on);
                    this.detail_box.setChecked(true);
                    this.IniFile.writeIniString(userIni, "APPSHOW", "DetailTitle",
                            "1");
                }
                break;
            case R.id.detail_open_in_browse:
                if (this.detail_open_box.isChecked()) {
                    this.detail_open_subtitle.setText("不允许出现");
                    this.detail_open_box.setChecked(false);
                    this.IniFile.writeIniString(userIni, "APPSHOW", "open_in_browse",
                            "0");
                } else {
                    this.detail_open_subtitle.setText("允许出现");
                    this.detail_open_box.setChecked(true);
                    this.IniFile.writeIniString(userIni, "APPSHOW", "open_in_browse",
                            "1");
                }
                break;
            case R.id.right:
                if (this.right_box.isChecked()) {
                    this.right_subtitle.setText(R.string.app_off);
                    this.right_box.setChecked(false);
                    this.righttitle.setEnabled(false);
                    this.rightleftBtn.setEnabled(false);
                    this.rightrightBtn.setEnabled(false);
                    this.rightwidth.setEnabled(false);
                    this.right_default_url.setEnabled(false);
                    this.IniFile.writeIniString(userIni, "APPSHOW", "ShowMore", "0");
                } else {
                    this.right_subtitle.setText(R.string.app_on);
                    this.right_box.setChecked(true);
                    this.righttitle.setEnabled(true);
                    this.rightleftBtn.setEnabled(true);
                    this.rightrightBtn.setEnabled(true);
                    this.rightwidth.setEnabled(true);
                    this.right_default_url.setEnabled(true);
                    this.IniFile.writeIniString(userIni, "APPSHOW", "ShowMore", "1");
                }
                this.m_bChanged = true;
                break;
            case R.id.righttitle:
                if (this.righttitle_box.isChecked()) {
                    this.right_title_set.setVisibility(View.GONE);
                    this.righttitle_box.setChecked(false);
                } else {
                    this.right_title_set.setVisibility(View.VISIBLE);
                    this.righttitle_box.setChecked(true);
                }
                break;
            case R.id.right_title_open:
                if (this.right_title_open_box.isChecked()) {
                    this.righttitle_subtitle.setText(R.string.app_off);
                    this.right_title_open_box.setChecked(false);
                } else {
                    this.righttitle_subtitle.setText(R.string.app_on);
                    this.right_title_open_box.setChecked(true);
                }
                break;
            case R.id.left:
                if (this.left_box.isChecked()) {
                    this.left_subtitle.setText(R.string.app_off);
                    this.left_box.setChecked(false);
                    this.lefttitle.setEnabled(false);
                    this.leftleftBtn.setEnabled(false);
                    this.leftrightBtn.setEnabled(false);
                    this.leftwidth.setEnabled(false);
                    this.left_default_url.setEnabled(false);
                    this.IniFile.writeIniString(userIni, "APPSHOW", "ShowMenu", "0");
                } else {
                    this.left_subtitle.setText(R.string.app_on);
                    this.left_box.setChecked(true);
                    this.lefttitle.setEnabled(true);
                    this.leftleftBtn.setEnabled(true);
                    this.leftrightBtn.setEnabled(true);
                    this.leftwidth.setEnabled(true);
                    this.left_default_url.setEnabled(true);
                    this.IniFile.writeIniString(userIni, "APPSHOW", "ShowMenu", "1");
                }
                this.m_bChanged = true;
                break;
            case R.id.lefttitle:
                if (this.lefttitle_box.isChecked()) {
                    this.lefttitle_box.setChecked(false);
                    this.left_title_set.setVisibility(View.GONE);
                } else {
                    this.lefttitle_box.setChecked(true);
                    this.left_title_set.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.left_title_open:
                if (this.left_title_open_box.isChecked()) {
                    this.left_title_open_box.setChecked(false);
                    this.lefttitle_subtitle.setText(R.string.app_off);
                } else {
                    this.left_title_open_box.setChecked(true);
                    this.lefttitle_subtitle.setText(R.string.app_on);
                }
                break;
            case R.id.leftwidth:
                UIHelper.showSeekBar(this, 1);
                break;
            case R.id.rightwidth:
                UIHelper.showSeekBar(this, 2);
                break;
            case R.id.mainmsgwidth:
                UIHelper.showSeekBar(this, 3);
                break;
            case R.id.buttonshow:
                new AlertDialog.Builder(this)
                        .setTitle("按钮名称位置设置")
                        .setSingleChoiceItems(
                                R.array.Button_options,
                                Integer.parseInt(this.IniFile.getIniString(userIni,
                                        "APPSHOW", "ButtonMode", "0", (byte) 0)),
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Intent intent = new Intent();
                                        if (which == 0) {
                                            AppearanceSettingActivity.this.IniFile.writeIniString(userIni,
                                                    "APPSHOW", "ButtonMode", "0");
                                            intent.setAction("loadView"
                                                    + getString(R.string.about_title));
                                            intent.putExtra("flag", 9);
                                            intent.putExtra("author", 0);
                                            AppearanceSettingActivity.this.sendBroadcast(intent);
                                            AppearanceSettingActivity.this.buttonshow_subtitle.setText("文字显示在底部");
                                        } else {
                                            AppearanceSettingActivity.this.IniFile.writeIniString(userIni,
                                                    "APPSHOW", "ButtonMode", "1");
                                            intent.setAction("loadView"
                                                    + getString(R.string.about_title));
                                            intent.putExtra("flag", 9);
                                            intent.putExtra("author", 1);
                                            AppearanceSettingActivity.this.sendBroadcast(intent);
                                            AppearanceSettingActivity.this.buttonshow_subtitle.setText("文字显示在右边");
                                        }
                                        dialog.dismiss();
                                    }
                                }).setNegativeButton("取消", null).show();
                break;
            case R.id.showin:
                new AlertDialog.Builder(this)
                        .setTitle("细览内链接打开位置")
                        .setSingleChoiceItems(
                                R.array.showin_options,
                                Integer.parseInt(this.IniFile.getIniString(userIni,
                                        "APPSHOW", "DetailShowIn", "0", (byte) 0)),
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        if (which == 0) {
                                            AppearanceSettingActivity.this.IniFile.writeIniString(userIni,
                                                    "APPSHOW", "DetailShowIn", "0");
                                            AppearanceSettingActivity.this.showin_subtitle.setText("在本窗口");
                                        } else if (which == 1) {
                                            AppearanceSettingActivity.this.IniFile.writeIniString(userIni,
                                                    "APPSHOW", "DetailShowIn", "1");
                                            AppearanceSettingActivity.this.showin_subtitle.setText("在弹出窗口");
                                        } else {
                                            AppearanceSettingActivity.this.IniFile.writeIniString(userIni,
                                                    "APPSHOW", "DetailShowIn", "2");
                                            AppearanceSettingActivity.this.showin_subtitle.setText("在其他");
                                        }
                                        Toast.makeText(AppearanceSettingActivity.this,
                                                "只在显示在信息区和右窗口时有效",
                                                Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    }
                                }).setNegativeButton("取消", null).show();
                break;
            case R.id.detailshow:
                new AlertDialog.Builder(this)
                        .setTitle("细览内容显示位置")
                        .setSingleChoiceItems(
                                R.array.detailshow_options,
                                Integer.parseInt(this.IniFile.getIniString(userIni,
                                        "APPSHOW", "DetailShow", "0", (byte) 0)),
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        if (which == 0) {
                                            AppearanceSettingActivity.this.IniFile.writeIniString(userIni,
                                                    "APPSHOW", "DetailShow", "0");
                                            AppearanceSettingActivity.this.detailshow_subtitle.setText("显示在弹出窗口");
                                        } else if (which == 1) {
                                            AppearanceSettingActivity.this.IniFile.writeIniString(userIni,
                                                    "APPSHOW", "DetailShow", "1");
                                            AppearanceSettingActivity.this.detailshow_subtitle.setText("显示在底部信息区");
                                        } else {
                                            if (AppearanceSettingActivity.this.ShowMore == 0) {
                                                Toast.makeText(
                                                        AppearanceSettingActivity.this,
                                                        "右窗口不可用", Toast.LENGTH_LONG)
                                                        .show();
                                            } else {
                                                AppearanceSettingActivity.this.IniFile.writeIniString(userIni,
                                                        "APPSHOW", "DetailShow",
                                                        "2");
                                                AppearanceSettingActivity.this.detailshow_subtitle
                                                        .setText("显示在右窗口");
                                            }
                                        }
                                        dialog.dismiss();
                                    }
                                }).setNegativeButton("取消", null).show();
                break;
            case R.id.touchmode:
                new AlertDialog.Builder(this)
                        .setTitle("左右滑动模式选择")
                        .setSingleChoiceItems(
                                R.array.touchmode_options,
                                Integer.parseInt(this.IniFile.getIniString(userIni,
                                        "APPSHOW", "TouchMode", "0", (byte) 0)),
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        if (which == 0) {
                                            AppearanceSettingActivity.this.IniFile.writeIniString(userIni,
                                                    "APPSHOW", "TouchMode", "0");
                                            AppearanceSettingActivity.this.mode_subtitle.setText("边界可滑");
                                            SlidingActivity.mSlidingMenu
                                                    .setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
                                        } else {
                                            AppearanceSettingActivity.this.IniFile.writeIniString(userIni,
                                                    "APPSHOW", "TouchMode", "1");
                                            AppearanceSettingActivity.this.mode_subtitle.setText("全屏可滑");
                                            SlidingActivity.mSlidingMenu
                                                    .setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                                        }
                                        dialog.dismiss();
                                    }
                                }).setNegativeButton("取消", null).show();
                break;
            case R.id.maintitle:
                if (this.title_box.isChecked()) {
                    this.title_subtitle.setText(R.string.app_off);
                    this.mainleftBtn.setEnabled(false);
                    this.mainrightBtn.setEnabled(false);
                    this.title_box.setChecked(false);
                } else {
                    this.title_subtitle.setText(R.string.app_on);
                    this.mainleftBtn.setEnabled(true);
                    this.mainrightBtn.setEnabled(true);
                    this.title_box.setChecked(true);
                }
                this.m_bChanged = true;
                break;
            case R.id.more_btn:
                this.finish();
                this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
                break;
            case R.id.finish_btn:
                this.setButtonNature();
                this.finish();
                this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
                break;
            case R.id.main_set:
                if (this.main_checkbox.isChecked()) {
                    this.main_checkbox.setChecked(false);
                    this.main_url.setVisibility(View.GONE);
                } else {
                    this.main_checkbox.setChecked(true);
                    this.main_url.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.left_set:
                if (this.left_checkbox.isChecked()) {
                    this.left_checkbox.setChecked(false);
                    this.left_url.setVisibility(View.GONE);
                } else {
                    this.left_checkbox.setChecked(true);
                    this.left_url.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.right_set:
                if (this.right_checkbox.isChecked()) {
                    this.right_checkbox.setChecked(false);
                    this.right_url.setVisibility(View.GONE);
                } else {
                    this.right_checkbox.setChecked(true);
                    this.right_url.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.detail_set:
                if (this.detail_checkbox.isChecked()) {
                    this.detail_checkbox.setChecked(false);
                    this.detail_url.setVisibility(View.GONE);
                } else {
                    this.detail_checkbox.setChecked(true);
                    this.detail_url.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.bottomtool_set:
                if (this.bottomtool_checkbox.isChecked()) {
                    this.bottomtool_checkbox.setChecked(false);
                    this.bottomtool_url.setVisibility(View.GONE);
                } else {
                    this.bottomtool_checkbox.setChecked(true);
                    this.bottomtool_url.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.bottommsg_set:
                if (this.bottommsg_checkbox.isChecked()) {
                    this.bottommsg_checkbox.setChecked(false);
                    this.bottommsg_url.setVisibility(View.GONE);
                } else {
                    this.bottommsg_checkbox.setChecked(true);
                    this.bottommsg_url.setVisibility(View.VISIBLE);
                }
                break;
        }

    }

    private class SetMain extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action
                    .equals("SetMain"
                            + getString(R.string.about_title))) {
                int SelBtn = intent.getIntExtra("flag", 1);
                int splitratio = intent.getIntExtra("progress", 50);
                switch (SelBtn) {
                    case 1:
                        AppearanceSettingActivity.this.leftwidth_subtitle.setText("为屏幕宽度的" + splitratio + "%");
                        break;
                    case 2:
                        AppearanceSettingActivity.this.rightwidth_subtitle.setText("为屏宽度幕的" + (100 - splitratio)
                                + "%");
                        break;
                    case 3:
                        AppearanceSettingActivity.this.mainmsgwidth_subtitle.setText("为屏幕高度的" + splitratio + "%");
                        break;
                }
            }
        }
    }

    /* 1. 只有功能项
       2. 只有自定义项
       3. 只有默认菜单项
       4. 只有功能项和自定义项
       5. 只有自定义项和默认菜单项
       6. 只有默认菜单项和功能项
       7. 三项都有

     */
    private void showSelectActionView(final EditText urlEdit, int action) {
        int section = 0;
        final List<StickyGridItem> mGirdList = new ArrayList<StickyGridItem>();
        if (action == 1 || action == 4 || action == 6 || action == 7) {
            section = section + 1;
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
                        mGirdList.add(mGridItem);
                    }
                }
                section = section + 1;
            }
        }
        if (action == 2 || action == 4 || action == 5 || action == 7) {
            section = section + 1;
            int resnum = Integer.parseInt(this.IniFile.getIniString(userIni,
                    "MENU_ALL", "Count", "0", (byte) 0));
            for (int i = 1; i <= resnum; i++) {
                String title = IniFile.getIniString(userIni, "MENU_ALL",
                        "Title" + i, "", (byte) 0);
                String path = IniFile.getIniString(userIni, "MENU_ALL",
                        "ID" + i, "", (byte) 0);
                StickyGridItem mGridItem = new StickyGridItem(path, title, section, "自定义菜单");
                mGirdList.add(mGridItem);
            }
        }
        if (action == 3 || action == 6 || action == 5 || action == 7) {
            section = section + 1;
            StickyGridItem mGridItem = new StickyGridItem("tbs:left_menu", "默认左菜单", section, "默认菜单");
            mGirdList.add(mGridItem);
            mGridItem = new StickyGridItem("tbs:custom_menu", "默认右菜单", section, "默认菜单");
            mGirdList.add(mGridItem);
        }
        if (mGirdList.size() > 0) {
            LayoutInflater factory = LayoutInflater.from(this);// 提示框
            View view = factory.inflate(R.layout.default_action_layout, null);// 这里必须是final
            GridView default_menu_gridview = (GridView) view.findViewById(R.id.gv_all);
            StickyGridAdapter defaultMenuAdapter = new StickyGridAdapter(AppearanceSettingActivity.this, mGirdList);
            default_menu_gridview.setAdapter(defaultMenuAdapter);
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("可选默认菜单项");
            builder.setCancelable(false);
            // 提示框标题
            builder.setView(view);
            builder.setNegativeButton("取消",
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog dialog = builder.create();
            dialog.show();
            default_menu_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String path = mGirdList.get(position).getPath();
                    if (path.contains(":"))
                        urlEdit.setText(path);
                    else {
                        urlEdit.setText("tbs:" + path);
                    }
                    dialog.cancel();
                }
            });
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("可选默认菜单项")
                    .setCancelable(false)
                    // 提示框标题
                    .setMessage("暂无默认可选项？ 去自定义吧")
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.m_bChanged) {
            // try {
            // m_iniFileIO.flush();
            // } catch (IOException e) {
            //
            // }
        }
        this.unregisterReceiver(this.SetMain);
        // 结束Activity&从堆栈中移除
        MyActivity.getInstance().finishActivity(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.onBackPressed();
            this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
        }
        return true;
    }
}
