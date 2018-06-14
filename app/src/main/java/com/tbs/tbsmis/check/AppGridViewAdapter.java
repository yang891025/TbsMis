package com.tbs.tbsmis.check;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.activity.InitializeToolbarActivity;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.app.StartTbsweb;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.ImageLoader;
import com.tbs.tbsmis.util.UIHelper;

import java.io.File;
import java.util.List;
import java.util.Map;

// 完成gridview 数据到界面的适配 
@SuppressLint("ResourceAsColor")
public class AppGridViewAdapter extends BaseAdapter
{
    private static final String TAG = "AppGridViewAdapter";
    private Context context;
    private ImageLoader imageLoader;
    LayoutInflater infalter;
    private List<Map<String, String>> childs;
    private IniFile m_iniFileIO;
    private String userIni;
    private boolean isShowDelete = false;

    public AppGridViewAdapter(Context context, List<Map<String, String>> childs) {
        this.context = context;
        this.childs = childs;
        // 方法1 通过系统的service 获取到 试图填充器
        this.infalter = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.imageLoader = new ImageLoader(context, R.drawable.format_app);
        // 方法2 通过layoutinflater的静态方法获取到 视图填充器
        // infalter = LayoutInflater.from(context);
        initPath();
    }

    public void setShowDelete(boolean isShowDelete) {
        this.isShowDelete = isShowDelete;
        notifyDataSetChanged();
    }

    // 返回gridview里面有多少个条目
    @Override
    public int getCount() {
        return this.childs.size();
    }

    // 返回某个position对应的条目
    @Override
    public Object getItem(int position) {
        return position;
    }

    // 返回某个position对应的id
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 返回某个位置对应的视图
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        AppGridViewAdapter.ViewHolder holder = null;
        if (convertView == null) {
            // ���ViewHolder����
            holder = new AppGridViewAdapter.ViewHolder();
            // ���벼�ֲ���ֵ��convertview
            convertView = this.infalter.inflate(R.layout.card_background_item, null);
            // Ϊview���ñ�ǩ
            convertView.setTag(holder);
        } else {
            // ȡ��holder
            holder = (AppGridViewAdapter.ViewHolder) convertView.getTag();
        }
        RelativeLayout lv = (RelativeLayout) convertView.findViewById(R.id.main_layout);
        ImageView iv = (ImageView) convertView.findViewById(R.id.main_gv_iv);
        final ImageView update = (ImageView) convertView.findViewById(R.id.main_update);
        TextView tv = (TextView) convertView.findViewById(R.id.main_gv_tv);
        final TextView path = (TextView) convertView.findViewById(R.id.main_path);
        ImageView dele = (ImageView) convertView.findViewById(R.id.delete_iv);
        dele.setVisibility(isShowDelete?View.VISIBLE:View.GONE);
        dele.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).setMessage(context.getString(R.string.delete) + "' " + childs.get(position).get("child") + " '")
                        .setCancelable(false)
                        .setNeutralButton(android.R
                                .string.ok, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String filePath = path.getText().toString();
                                if (!path.getText().toString().startsWith("/")) {
                                    filePath = UIHelper.getStoragePath(context);
                                    filePath = filePath + constants.SD_CARD_TBSSOFT_PATH3 + "/"
                                            + path.getText();
                                }
                                File file = new File(filePath);
                                if (file.exists() && file.isDirectory()) {
                                    FileUtils.deleteDirectory(filePath);
                                }
                                childs.remove(position);
                                notifyDataSetChanged();
                            }
                        }).setNegativeButton(R.string.cancel, null).show();
            }
        });
        path.setText(this.childs.get(position).get("path"));
        holder.update = this.childs.get(position).get("update");
        holder.downPath = this.childs.get(position).get("downUrl");
        if (holder.update.equals("1"))
            update.setVisibility(View.VISIBLE);
        else
            update.setVisibility(View.INVISIBLE);
        // 设置每一个item的名字和图标
        if (Integer.parseInt(this.childs.get(position).get("color")) == 0) {
            lv.setBackgroundResource(R.color.default_style);
        } else if (Integer.parseInt(this.childs.get(position).get("color")) == 1) {
            lv.setBackgroundResource(R.color.blue_style);
        } else if (Integer.parseInt(this.childs.get(position).get("color")) == 2) {
            lv.setBackgroundResource(R.color.green_style);
        } else if (Integer.parseInt(this.childs.get(position).get("color")) == 3) {
            lv.setBackgroundResource(R.color.pink_style);
        } else if (Integer.parseInt(this.childs.get(position).get("color")) == 4) {
            lv.setBackgroundResource(R.color.purple_style);
        } else if (Integer.parseInt(this.childs.get(position).get("color")) == 5) {
            lv.setBackgroundResource(R.color.red_style);
        } else if (Integer.parseInt(this.childs.get(position).get("color")) == 6) {
            lv.setBackgroundResource(R.color.yellow_style);
        }
        final AppGridViewAdapter.ViewHolder finalHolder = holder;
        lv.setOnClickListener(new View.OnClickListener()
                              {
                                  @Override
                                  public void onClick(View v) {
                                      update.setVisibility(View.INVISIBLE);
                                      //childs.get(position).put("","0");
                                      if (finalHolder.update.equals("1")) {
                                          AppGridViewAdapter.this.showNoticeDialog(AppGridViewAdapter.this.childs.get
                                                  (position));
                                      } else {
                                          String webRoot;
                                          if (path.getText().toString().startsWith("/")) {
                                              webRoot = path.getText().toString();
                                          } else {
                                              webRoot = UIHelper.getStoragePath(context);
                                              webRoot = webRoot + constants.SD_CARD_TBSSOFT_PATH3 + "/"
                                                      + path.getText();
                                          }
                                          if (webRoot.endsWith("/") == false) {
                                              webRoot += "/";
                                          }
                                          StartTbsweb.Startapp(AppGridViewAdapter.this.context, 0);
                                          UIHelper.setSharePerference(AppGridViewAdapter.this.context,
                                                  constants.SAVE_INFORMATION, "Path", webRoot);
                                          StartTbsweb.Startapp(AppGridViewAdapter.this.context, 1);
                                          Intent intent = new Intent();
                                          intent.setAction("Action_main" + AppGridViewAdapter.this.context.getString
                                                  (R.string.about_title));
                                          intent.putExtra("flag", 12);
                                          AppGridViewAdapter.this.context.sendBroadcast(intent);
                                          intent.setAction("loadView" + AppGridViewAdapter.this.context.getString
                                                  (R.string.about_title));
                                          intent.putExtra("flag", 5);
                                          intent.putExtra("author", 0);
                                          AppGridViewAdapter.this.context.sendBroadcast(intent);
                                          Intent mainIntent = new Intent(AppGridViewAdapter.this.context,
                                                  InitializeToolbarActivity.class);
                                          AppGridViewAdapter.this.context.startActivity(mainIntent);
                                          MyActivity.getInstance().finishAllActivity();
                                      }
                                      // int position = Integer.parseInt(v.getTag().toString());

                                  }

                              }
        );

        String Url;
        String ipUrl = this.m_iniFileIO.getIniString(this.userIni, "Store",
                "storeAddress", constants.DefaultServerIp, (byte) 0);
        String portUrl = this.m_iniFileIO.getIniString(this.userIni, "Store",
                "storePort", constants.DefaultServerPort, (byte) 0);
        Url = "http://" + ipUrl + ":" + portUrl;
        this.imageLoader.DisplayImage(Url + "/Store/image/" + childs.get(position).get("path") + ".png",
                iv);
        //iv.setImageResource(drawable.default_pic);
        tv.setText(this.childs.get(position).get("child"));
        return convertView;
    }

    /**
     * 显示版本更新通知对话框
     */
    private void showNoticeDialog(final Map<String, String> child) {
        Builder builder = new Builder(this.context);
        builder.setTitle("更新");
        builder.setMessage(child.get("appinfo"));
        builder.setCancelable(false);
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FileUtils.downFile(AppGridViewAdapter.this.context, child.get("downUrl"), child.get("path"), true);
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("打开应用", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String webRoot;
                String path = child.get("path");
                if (path.startsWith("/")) {
                    webRoot = path;
                } else {
                    webRoot = UIHelper.getStoragePath(context);
                    webRoot = webRoot + constants.SD_CARD_TBSSOFT_PATH3 + "/"
                            + path;
                }
                if (webRoot.endsWith("/") == false) {
                    webRoot += "/";
                }
                StartTbsweb.Startapp(AppGridViewAdapter.this.context, 0);
                UIHelper.setSharePerference(AppGridViewAdapter.this.context,
                        constants.SAVE_INFORMATION, "Path", webRoot);
                StartTbsweb.Startapp(AppGridViewAdapter.this.context, 1);
                Intent intent = new Intent();
                intent.setAction("Action_main" + AppGridViewAdapter.this.context.getString(R.string.about_title));
                intent.putExtra("flag", 12);
                AppGridViewAdapter.this.context.sendBroadcast(intent);
                intent.setAction("loadView" + AppGridViewAdapter.this.context.getString(R.string.about_title));
                intent.putExtra("flag", 5);
                intent.putExtra("author", 0);
                AppGridViewAdapter.this.context.sendBroadcast(intent);
                Intent mainIntent = new Intent(AppGridViewAdapter.this.context, InitializeToolbarActivity.class);
                AppGridViewAdapter.this.context.startActivity(mainIntent);
                MyActivity.getInstance().finishAllActivity();

                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Dialog noticeDialog = builder.create();
        noticeDialog.show();
    }

    public static class ViewHolder
    {
        public String update;
        public String downPath;
    }

    private void initPath() {
        m_iniFileIO = new IniFile();
        String webRoot = UIHelper.getSoftPath(context);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        webRoot += context.getString(R.string.SD_CARD_TBSAPP_PATH2);
        webRoot = UIHelper.getShareperference(context, constants.SAVE_INFORMATION,
                "Path", webRoot);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String appIniFile = webRoot
                + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        userIni = appIniFile;
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = context.getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
    }
}