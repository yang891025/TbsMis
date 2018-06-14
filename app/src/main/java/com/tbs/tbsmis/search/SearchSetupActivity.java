package com.tbs.tbsmis.search;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.check.SearchCategoryAdapter;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchSetupActivity extends Activity implements View.OnClickListener {

	protected static final String TAG = "SearchSetupActivity";
	private IniFile m_iniFileIO;
	private String WebIniFile;
	private ImageView leftBtn;
	private ImageView RightBtn;
	private TextView title;
	private LinearLayout search_interface;
	private LinearLayout search_interface_set;
	private CheckBox search_interface_checkbox;
	private LinearLayout window_option;
	private LinearLayout window_option_set;
	private CheckBox window_option_checkbox;
	private RelativeLayout search_interface_category;
	private CheckBox search_interface_category_box;
	private RelativeLayout search_interface_history;
	private CheckBox search_interface_history_box;
	private RelativeLayout search_interface_auto;
	private CheckBox search_interface_auto_box;
	private TextView show_window_hight;
	private SeekBar option_window_hight;
	private TextView show_window_width;
	private SeekBar option_window_width;
	private TextView show_window_border;
	private SeekBar option_window_border;
	private LinearLayout search_option;
	private LinearLayout search_option_set;
	private CheckBox search_option_checkbox;
	private ImageView add_search_category;
	private ListView search_option_Items;
	private ArrayList<HashMap<String, Object>> searchList;
	private SearchCategoryAdapter SearchCategoryAdapter;
	private Button search_personal_keyword;
	private Button search_common_keyword;
	private WebView search_keyword_webview;
	private String baseUrl;
	private LinearLayout window_option1;
	private LinearLayout window_option1_set;
	private CheckBox window_option1_checkbox;
	private Button search_personal_keyword1;
	private Button search_common_keyword1;
	private WebView search_keyword_webview1;
    private String userIni;
    private RelativeLayout search_layout;
    private RelativeLayout search_show_layout;
    private CheckBox search_show_box;
    private EditText search_address;
    private EditText search_port;

    @Override
	public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		MyActivity.getInstance().addActivity(this);
        this.setContentView(R.layout.search_setup);
        this.initPath();
        this.titleView();
        this.initView();
        this.appListInit();
	}

	private void initPath() {
        this.m_iniFileIO = new IniFile();
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
        this.WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String appFile = webRoot
				+ this.m_iniFileIO.getIniString(this.WebIniFile, "TBSWeb", "IniName",
						constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        userIni = appFile;
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
		String ipUrl = this.m_iniFileIO.getIniString(userIni, "SERVICE",
				"currentAddress", constants.DefaultLocalIp, (byte) 0);
		String portUrl = this.m_iniFileIO.getIniString(userIni, "SERVICE",
				"currentPort", constants.DefaultLocalPort, (byte) 0);
        this.baseUrl = "http://" + ipUrl + ":" + portUrl;
	}

	private void titleView() {
        this.leftBtn = (ImageView) this.findViewById(R.id.more_btn);
        this.RightBtn = (ImageView) this.findViewById(R.id.finish_btn);
        this.title = (TextView) this.findViewById(R.id.textView1);
        this.leftBtn.setOnClickListener(this);
        this.RightBtn.setOnClickListener(this);
        this.title.setText(R.string.search_setup);
	}

	private void initView() {
        this.search_interface = (LinearLayout) this.findViewById(R.id.search_interface);
        this.search_interface_set = (LinearLayout) this.findViewById(R.id.search_interface_set);
        this.search_interface_checkbox = (CheckBox) this.findViewById(R.id.search_interface_checkbox);
        this.window_option = (LinearLayout) this.findViewById(R.id.window_option);
        this.window_option_set = (LinearLayout) this.findViewById(R.id.window_option_set);
        this.window_option_checkbox = (CheckBox) this.findViewById(R.id.window_option_checkbox);
        this.window_option1 = (LinearLayout) this.findViewById(R.id.window_option1);
        this.window_option1_set = (LinearLayout) this.findViewById(R.id.window_option1_set);
        this.window_option1_checkbox = (CheckBox) this.findViewById(R.id.window_option1_checkbox);
        this.search_option = (LinearLayout) this.findViewById(R.id.search_option);
        this.search_option_set = (LinearLayout) this.findViewById(R.id.search_option_set);
        this.search_option_checkbox = (CheckBox) this.findViewById(R.id.search_option_checkbox);

        this.search_interface_category = (RelativeLayout) this.findViewById(R.id.search_interface_category);
        this.search_interface_category_box = (CheckBox) this.findViewById(R.id.search_interface_category_box);
        this.search_interface_history = (RelativeLayout) this.findViewById(R.id.search_interface_history);
        this.search_interface_history_box = (CheckBox) this.findViewById(R.id.search_interface_history_box);
        this.search_interface_auto = (RelativeLayout) this.findViewById(R.id.search_interface_auto);
        this.search_interface_auto_box = (CheckBox) this.findViewById(R.id.search_interface_auto_box);

        this.show_window_hight = (TextView) this.findViewById(R.id.show_window_hight);
        this.option_window_hight = (SeekBar) this.findViewById(R.id.option_window_hight);
        this.show_window_width = (TextView) this.findViewById(R.id.show_window_width);
        this.option_window_width = (SeekBar) this.findViewById(R.id.option_window_width);
        this.show_window_border = (TextView) this.findViewById(R.id.show_window_border);
        this.option_window_border = (SeekBar) this.findViewById(R.id.option_window_border);

        this.add_search_category = (ImageView) this.findViewById(R.id.add_search_category);
        this.search_option_Items = (ListView) this.findViewById(R.id.search_option_Items);

        this.search_personal_keyword = (Button) this.findViewById(R.id.search_personal_keyword);
        this.search_common_keyword = (Button) this.findViewById(R.id.search_common_keyword);
        this.search_personal_keyword.setOnClickListener(this);
        this.search_common_keyword.setOnClickListener(this);

        this.search_keyword_webview = (WebView) this.findViewById(R.id.search_keyword_webview);

        this.search_personal_keyword1 = (Button) this.findViewById(R.id.search_personal_keyword1);
        this.search_common_keyword1 = (Button) this.findViewById(R.id.search_common_keyword1);
        this.search_personal_keyword1.setOnClickListener(this);
        this.search_common_keyword1.setOnClickListener(this);

        this.search_keyword_webview1 = (WebView) this.findViewById(R.id.search_keyword_webview1);

        search_layout = (RelativeLayout) findViewById(R.id.search_layout);
        search_show_layout = (RelativeLayout) findViewById(R.id.search_show_layout);
        search_show_box = (CheckBox) findViewById(R.id.search_show_box);
        search_address = (EditText) findViewById(R.id.search_address);
        search_port = (EditText) findViewById(R.id.search_port);

        search_address.setText(m_iniFileIO.getIniString(userIni,
                "search", "searchAddress", constants.DefaultServerIp, (byte) 0));
        search_port.setText(m_iniFileIO.getIniString(userIni,
                "search", "searchPort", constants.DefaultServerPort, (byte) 0));
        int nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "search",
                "search_show_in_menu", "0", (byte) 0));
        if (nVal == 1) {
            search_show_box.setChecked(true);
        }
        search_layout.setOnClickListener(this);
        search_show_layout.setOnClickListener(this);
        this.search_interface_category.setOnClickListener(this);
        this.search_interface_history.setOnClickListener(this);
        this.search_interface_auto.setOnClickListener(this);
        this.add_search_category.setOnClickListener(this);
        this.search_interface.setOnClickListener(this);
        this.window_option.setOnClickListener(this);
        this.window_option1.setOnClickListener(this);
        this.search_option.setOnClickListener(this);
        this.option_window_hight.setMax(60);
        this.option_window_hight
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() // ����������
				{
					private int size;

					@Override
					public void onProgressChanged(SeekBar arg0, int progress,
							boolean fromUser) {
                        this.size = progress;
                        SearchSetupActivity.this.show_window_hight.setText((progress + 20) + "%");
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
                        SearchSetupActivity.this.m_iniFileIO.writeIniString(SearchSetupActivity.this.userIni, "search",
								"Window_Hight", (this.size + 20) + "");
					}
				});
        this.option_window_width.setMax(90);
        this.option_window_width
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() // ����������
				{
					private int size;

					@Override
					public void onProgressChanged(SeekBar arg0, int progress,
							boolean fromUser) {
                        this.size = progress;
                        SearchSetupActivity.this.show_window_width.setText((progress + 10) + "%");
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
                        SearchSetupActivity.this.m_iniFileIO.writeIniString(SearchSetupActivity.this.userIni, "search",
								"Window_Width", (this.size + 10) + "");
					}
				});
        this.option_window_border.setMax(10);
        this.option_window_border
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() // ����������
				{
					private int size;

					@Override
					public void onProgressChanged(SeekBar arg0, int progress,
							boolean fromUser) {
                        this.size = progress;
                        SearchSetupActivity.this.show_window_border.setText(progress + "%");
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
                        SearchSetupActivity.this.m_iniFileIO.writeIniString(SearchSetupActivity.this.userIni, "search",
								"Window_Border", this.size + "");
					}
				});
		if (Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni, "search",
				"SearchBar", "0", (byte) 0)) == 0) {
            this.search_interface_category_box.setChecked(false);
		} else {
            this.search_interface_category_box.setChecked(true);
		}
		if (Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni, "search",
				"HisWordBar", "0", (byte) 0)) == 0) {
            this.search_interface_history_box.setChecked(false);
		} else {
            this.search_interface_history_box.setChecked(true);
		}
		if (Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni, "search",
				"HisWordWindow", "0", (byte) 0)) == 0) {
            this.search_interface_auto_box.setChecked(false);
		} else {
            this.search_interface_auto_box.setChecked(true);
		}
        this.option_window_hight
				.setProgress(Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni,
						"search", "Window_Hight", "75", (byte) 0)) - 20);
        this.show_window_hight.setText(Integer.parseInt(this.m_iniFileIO.getIniString(
                this.userIni, "search", "Window_Hight", "75", (byte) 0)) + "%");
        this.option_window_width
				.setProgress(Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni,
						"search", "Window_Width", "50", (byte) 0)) - 10);
        this.show_window_width.setText(Integer.parseInt(this.m_iniFileIO.getIniString(
                this.userIni, "search", "Window_Width", "50", (byte) 0)) + "%");
        this.option_window_border
				.setProgress(Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni,
						"search", "Window_Border", "1", (byte) 0)));
        this.show_window_border.setText(Integer.parseInt(this.m_iniFileIO.getIniString(
                this.userIni, "search", "Window_Border", "1", (byte) 0)) + "%");
        this.initwebview();
	}
    private void saveET(){
        String address_editTxt = String
                .valueOf(search_address.getText());
        if (null !=address_editTxt
                && !address_editTxt.equals("")
                && !address_editTxt.equals("\\s{1,}")) {
            if (!StringUtils.isIp(address_editTxt)) {
                Toast.makeText(this,
                        "请检查地址的正确性", Toast.LENGTH_SHORT)
                        .show();
                search_address.setFocusable(true);
                return;
            }
        }else{
            Toast.makeText(this,
                    "地址不正确或为空", Toast.LENGTH_SHORT)
                    .show();
            search_address.setFocusable(true);
            return;
        }
        address_editTxt = String
                .valueOf(search_port.getText());
        if (StringUtils.isEmpty(address_editTxt)) {
            Toast.makeText(this,
                    "端口不可为空", Toast.LENGTH_SHORT)
                    .show();
            search_port.setFocusable(true);
            return;
        }
        m_iniFileIO.writeIniString(userIni, "search",
                "searchAddress", search_address.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "search",
                "searchPort", search_port.getText()
                        .toString());
        finish();
    }
	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	private void initwebview() {
		// TODO Auto-generated method stub
		WebSettings Settings = this.search_keyword_webview.getSettings();
		Settings.setDefaultTextEncodingName("gb2312");
		Settings.setSupportZoom(true);
		Settings.setBuiltInZoomControls(false);
		Settings.setJavaScriptEnabled(true);
		// 3.0版本才有setDisplayZoomControls的功能
		if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1) {
			Settings.setDisplayZoomControls(false);
		}
		//Settings.setPluginsEnabled(true);
		Settings.setPluginState(WebSettings.PluginState.ON);
		Settings.setSupportMultipleWindows(true);
		Settings.setJavaScriptCanOpenWindowsAutomatically(true);
		Settings.setDomStorageEnabled(true);
        this.search_keyword_webview.setWebViewClient(UIHelper.getWebViewClient());
		String key_word = this.m_iniFileIO.getIniString(this.userIni, "search",
				"KeywordURL", "", (byte) 0);
		key_word = StringUtils.isUrl(key_word, this.baseUrl, UIHelper
				.getShareperference(this,
						constants.SAVE_LOCALMSGNUM, "resname", "yqxx"));
        this.search_keyword_webview.loadUrl(key_word);

		Settings = this.search_keyword_webview1.getSettings();
		Settings.setDefaultTextEncodingName("gb2312");
		Settings.setSupportZoom(true);
		Settings.setBuiltInZoomControls(false);
		Settings.setJavaScriptEnabled(true);
		// 3.0版本才有setDisplayZoomControls的功能
		if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1) {
			Settings.setDisplayZoomControls(false);
		}
		//Settings.setPluginsEnabled(true);
		Settings.setPluginState(WebSettings.PluginState.ON);
		Settings.setSupportMultipleWindows(true);
		Settings.setJavaScriptCanOpenWindowsAutomatically(true);
		Settings.setDomStorageEnabled(true);
        this.search_keyword_webview1.setWebViewClient(UIHelper.getWebViewClient());
		key_word = this.m_iniFileIO.getIniString(this.userIni, "search",
				"HistoryUrl", "", (byte) 0);
		key_word = StringUtils.isUrl(key_word, this.baseUrl, UIHelper
				.getShareperference(this,
						constants.SAVE_LOCALMSGNUM, "resname", "yqxx"));
        this.search_keyword_webview1.loadUrl(key_word);
	}

	private void appListInit() {
		// TODO Auto-generated method stub
        this.searchList = new ArrayList<HashMap<String, Object>>();/* 在数组中存放数据 */
		int cateNum = Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni,
				"search", "SearchCount", "0", (byte) 0));
		for (int i = 0; i < cateNum; i++) {
			String search_Name = this.m_iniFileIO.getIniString(this.userIni, "search",
					"search" + i, "", (byte) 0);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("SearchName", search_Name);
			map.put("SearchTitle", this.m_iniFileIO.getIniString(this.userIni,
					search_Name, "SearchTitle", "本机检索", (byte) 0));
			map.put("SearchURL", this.m_iniFileIO.getIniString(this.userIni, search_Name,
					"SearchURL", "", (byte) 0));
			map.put("HistoryURL", this.m_iniFileIO.getIniString(this.userIni,
					search_Name, "HistoryURL", "", (byte) 0));
			map.put("KeywordURL", this.m_iniFileIO.getIniString(this.userIni,
					search_Name, "KeywordURL", "", (byte) 0));
			map.put("SearchAction", this.m_iniFileIO.getIniString(this.userIni,
					search_Name, "SearchAction", "", (byte) 0));
			map.put("KeyAction", this.m_iniFileIO.getIniString(this.userIni, search_Name,
					"KeyAction", "", (byte) 0));
            this.searchList.add(map);
		}
        this.SearchCategoryAdapter = new SearchCategoryAdapter(this.searchList, this,
                this.userIni);
        this.search_option_Items.setAdapter(this.SearchCategoryAdapter);
		UIHelper.setListViewHeightBasedOnChildren(this.search_option_Items, 200);
		// ��listView�ļ�����
        this.search_option_Items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				SearchCategoryAdapter.ViewHolder holder = (SearchCategoryAdapter.ViewHolder) arg1.getTag();
				if (holder.btnLayout.getVisibility() == View.GONE) {
					holder.btnLayout.setVisibility(View.VISIBLE);
					holder.more.setBackgroundResource(R.drawable.update_up);
				} else {
					holder.btnLayout.setVisibility(View.GONE);
					holder.more.setBackgroundResource(R.drawable.update_down);
				}
			}
		});
	}

	protected void AddMsgCategoryDialog() {
		LayoutInflater factory = LayoutInflater.from(this);// 提示框
		View view = factory.inflate(R.layout.search_edit, null);// 这里必须是final的
		final EditText set_category_name = (EditText) view
				.findViewById(R.id.set_category_name);
		final EditText set_english_name = (EditText) view
				.findViewById(R.id.set_english_name);// 获得输入框对象
		final EditText set_search_path = (EditText) view
				.findViewById(R.id.set_search_path);
		final EditText set_keyword_path = (EditText) view
				.findViewById(R.id.set_keyword_path);
		final EditText set_history_path = (EditText) view
				.findViewById(R.id.set_history_path);
		final EditText set_search_action = (EditText) view
				.findViewById(R.id.set_search_action);
		final EditText set_keyword_action = (EditText) view
				.findViewById(R.id.set_keyword_action);
		set_search_path.addTextChangedListener(new TextWatcher() {
			private String localPortTxt;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
                this.localPortTxt = String.valueOf(set_search_path.getText());
				if (null != this.localPortTxt && !this.localPortTxt.equals("")
						&& !this.localPortTxt.equals("\\s{1,}")) {
					set_history_path.setText(this.localPortTxt);
					set_keyword_path.setText(this.localPortTxt);
				}
				Log.i("localPortTxt", "onTextChanged...");
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				Log.i("localPortTxt", "beforeTextChanged...");
			}

			@Override
			public void afterTextChanged(Editable s) {
				Log.i("localPortTxt", "afterTextChanged...");
			}
		});
		new Builder(this)
				.setTitle("添加选项")
				.setCancelable(false)
				// 提示框标题
				.setView(view)
				.setPositiveButton("确定",// 提示框的两个按钮
						new DialogInterface.OnClickListener() {
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
								if (StringUtils.isEmpty(set_category_name
										.getText().toString())) {
									Toast.makeText(SearchSetupActivity.this,
											"检索名称不可为空", Toast.LENGTH_SHORT)
											.show();
									return;
								} else if (StringUtils.isEmpty(set_english_name
										.getText().toString())) {
									Toast.makeText(SearchSetupActivity.this,
											"检索链接不可为空", Toast.LENGTH_SHORT)
											.show();
									return;
								} else if (StringUtils.isEmpty(set_search_path
										.getText().toString())) {
									Toast.makeText(SearchSetupActivity.this,
											"检索链接不可为空", Toast.LENGTH_SHORT)
											.show();
									return;
								} else {
									if (SearchSetupActivity.this.AddSearchCategory(set_category_name
											.getText().toString(),
											set_english_name.getText()
													.toString(),
											set_search_path.getText()
													.toString(),
											set_history_path.getText()
													.toString(),
											set_keyword_path.getText()
													.toString(),
											set_search_action.getText()
													.toString(),
											set_keyword_action.getText()
													.toString())) {
										Toast.makeText(
												SearchSetupActivity.this,
												"检索选项添加成功", Toast.LENGTH_SHORT)
												.show();
									} else {
										Toast.makeText(
												SearchSetupActivity.this,
												"检索选项已存在", Toast.LENGTH_SHORT)
												.show();
										return;
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
							}
						})
				.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
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

	protected boolean AddSearchCategory(String category_name,
			String english_name, String search_path, String history_path,
			String keyword_path, String search_action, String keyword_action) {
		int cateNum = Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni,
				"search", "SearchCount", "0", (byte) 0));
		for (int i = 0; i < cateNum; i++) {
			if (this.m_iniFileIO.getIniString(this.userIni, "search", "search" + i, "",
					(byte) 0).equalsIgnoreCase(english_name)) {
				return false;
			}
		}
        this.m_iniFileIO.writeIniString(this.userIni, "search", "search" + cateNum,
				english_name);
        this.m_iniFileIO.writeIniString(this.userIni, "search", "SearchCount", cateNum
				+ 1 + "");
        this.m_iniFileIO.writeIniString(this.userIni, english_name, "SearchTitle",
				category_name);
        this.m_iniFileIO.writeIniString(this.userIni, english_name, "SearchURL",
				search_path);
        this.m_iniFileIO.writeIniString(this.userIni, english_name, "HistoryURL",
				history_path);
        this.m_iniFileIO.writeIniString(this.userIni, english_name, "KeywordURL",
				keyword_path);
        this.m_iniFileIO.writeIniString(this.userIni, english_name, "SearchAction",
				search_action);
        this.m_iniFileIO.writeIniString(this.userIni, english_name, "KeyAction",
				keyword_action);
        this.appListInit();
		return true;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.more_btn:
            this.finish();
            this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
			break;
		case R.id.finish_btn:
            saveET();
            this.finish();
            this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
			break;
		case R.id.add_search_category:
            this.AddMsgCategoryDialog();
			break;
		case R.id.search_interface:
			if (this.search_interface_checkbox.isChecked()) {
                this.search_interface_checkbox.setChecked(false);
                this.search_interface_set.setVisibility(View.GONE);
			} else {
                this.search_interface_checkbox.setChecked(true);
                this.search_interface_set.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.window_option:
			if (this.window_option_checkbox.isChecked()) {
                this.window_option_checkbox.setChecked(false);
                this.window_option_set.setVisibility(View.GONE);
			} else {
                this.window_option_checkbox.setChecked(true);
                this.window_option_set.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.window_option1:
			if (this.window_option1_checkbox.isChecked()) {
                this.window_option1_checkbox.setChecked(false);
                this.window_option1_set.setVisibility(View.GONE);
			} else {
                this.window_option1_checkbox.setChecked(true);
                this.window_option1_set.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.search_option:
			if (this.search_option_checkbox.isChecked()) {
                this.search_option_checkbox.setChecked(false);
                this.search_option_set.setVisibility(View.GONE);
			} else {
                this.search_option_checkbox.setChecked(true);
                this.search_option_set.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.search_interface_category:
			if (this.search_interface_category_box.isChecked()) {
                this.search_interface_category_box.setChecked(false);
                this.m_iniFileIO.writeIniString(this.userIni, "search", "SearchBar", "0");
			} else {
                this.search_interface_category_box.setChecked(true);
                this.m_iniFileIO.writeIniString(this.userIni, "search", "SearchBar", "1");
			}
			break;
		case R.id.search_interface_history:
			if (this.search_interface_history_box.isChecked()) {
                this.search_interface_history_box.setChecked(false);
                this.m_iniFileIO
						.writeIniString(this.userIni, "search", "HisWordBar", "0");
			} else {
                this.search_interface_history_box.setChecked(true);
                this.m_iniFileIO
						.writeIniString(this.userIni, "search", "HisWordBar", "1");
			}
			break;
		case R.id.search_interface_auto:
			if (this.search_interface_auto_box.isChecked()) {
                this.search_interface_auto_box.setChecked(false);
                this.m_iniFileIO.writeIniString(this.userIni, "search", "HisWordWindow",
						"0");
			} else {
                this.search_interface_auto_box.setChecked(true);
                this.m_iniFileIO.writeIniString(this.userIni, "search", "HisWordWindow",
						"1");
			}
			break;
		case R.id.search_personal_keyword:
            this.initPath();
			String key_word = this.m_iniFileIO.getIniString(this.userIni, "search",
					"KeywordURL", "", (byte) 0);
			key_word = StringUtils.isUrl(key_word, this.baseUrl, UIHelper
					.getShareperference(this,
							constants.SAVE_LOCALMSGNUM, "resname", "yqxx"));
            this.search_keyword_webview.loadUrl(key_word);
			break;
		case R.id.search_common_keyword:
            this.initPath();
			String key_word1 = this.m_iniFileIO.getIniString(this.userIni, "search",
					"KeywordURL", "", (byte) 0);
			key_word1 = StringUtils.isUrl(key_word1, this.baseUrl, UIHelper
					.getShareperference(this,
							constants.SAVE_LOCALMSGNUM, "resname", "yqxx"));
            this.search_keyword_webview.loadUrl(key_word1);
			break;
		case R.id.search_personal_keyword1:
            this.initPath();
			String key_word2 = this.m_iniFileIO.getIniString(this.userIni, "search",
					"HistoryUrl", "", (byte) 0);
			key_word2 = StringUtils.isUrl(key_word2, this.baseUrl, UIHelper
					.getShareperference(this,
							constants.SAVE_LOCALMSGNUM, "resname", "yqxx"));
            this.search_keyword_webview.loadUrl(key_word2);
			break;
		case R.id.search_common_keyword1:
            this.initPath();
			String key_word3 = this.m_iniFileIO.getIniString(this.userIni, "search",
					"HistoryUrl", "", (byte) 0);
			key_word3 = StringUtils.isUrl(key_word3, this.baseUrl, UIHelper
					.getShareperference(this,
							constants.SAVE_LOCALMSGNUM, "resname", "yqxx"));
            this.search_keyword_webview.loadUrl(key_word3);
			break;
            case R.id.search_show_layout:
                if (search_show_box.isChecked()) {
                    search_show_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "search", "search_show_in_menu", "0");
                } else {
                    search_show_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "search", "search_show_in_menu", "1");
                }
                break;
            case R.id.search_layout:
                String address_editTxt = String
                        .valueOf(search_address.getText());
                if (null !=address_editTxt
                        && !address_editTxt.equals("")
                        && !address_editTxt.equals("\\s{1,}")) {
                    if (!StringUtils.isIp(address_editTxt)) {
                        Toast.makeText(this,
                                "请检查地址的正确性", Toast.LENGTH_SHORT)
                                .show();
                        search_address.setFocusable(true);
                        return;
                    }
                }else{
                    Toast.makeText(this,
                            "地址不正确或为空", Toast.LENGTH_SHORT)
                            .show();
                    search_address.setFocusable(true);
                    return;
                }
                address_editTxt = String
                        .valueOf(search_port.getText());
                if (StringUtils.isEmpty(address_editTxt)) {
                    Toast.makeText(this,
                            "端口不可为空", Toast.LENGTH_SHORT)
                            .show();
                    search_port.setFocusable(true);
                    return;
                }
                m_iniFileIO.writeIniString(userIni, "search",
                        "searchAddress", search_address.getText()
                                .toString());
                m_iniFileIO.writeIniString(userIni, "search",
                        "searchPort", search_port.getText()
                                .toString());
                Intent intent = new Intent();
                intent.setClass(SearchSetupActivity.this, SearchActivityNew.class);
                startActivity(intent);
                break;
		}
	}
}