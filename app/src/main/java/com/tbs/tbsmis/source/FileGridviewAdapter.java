package com.tbs.tbsmis.source;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.file.FileInfo;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.ImageLoader;
import com.tbs.tbsmis.util.UIHelper;

import java.io.File;
import java.util.List;

// 完成gridview 数据到界面的适配
@SuppressLint("ResourceAsColor")
public class FileGridviewAdapter extends BaseAdapter
{
    private static final String TAG = "AppGridViewAdapter";
    private Context context;
    private ImageLoader imageLoader;
    LayoutInflater infalter;
    private List<FileInfo> childs;
    private IniFile m_iniFileIO;
    private String userIni;
    private boolean isShowDelete = false;

    public FileGridviewAdapter(Context context, List<FileInfo> childs) {
        this.context = context;
        this.childs = childs;
        // 方法1 通过系统的service 获取到 试图填充器
        this.imageLoader = new ImageLoader(context,R.drawable.format_folder);
        this.infalter = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // 方法2 通过layoutinflater的静态方法获取到 视图填充器
        // infalter = LayoutInflater.from(context);
        initPath();
    }
    protected void setShowDelete(boolean isShowDelete){
        this.isShowDelete=isShowDelete;
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
        FileGridviewAdapter.ViewHolder holder = null;
        if (convertView == null) {
            // ���ViewHolder����
            holder = new FileGridviewAdapter.ViewHolder();
            // ���벼�ֲ���ֵ��convertview
            convertView = this.infalter.inflate(R.layout.card_background_item, null);
            // Ϊview���ñ�ǩ
            convertView.setTag(holder);
        } else {
            // ȡ��holder
            holder = (FileGridviewAdapter.ViewHolder) convertView.getTag();
        }
        RelativeLayout lv = (RelativeLayout) convertView.findViewById(R.id.main_layout);
        ImageView iv = (ImageView) convertView.findViewById(R.id.main_gv_iv);
        ImageView update = (ImageView) convertView.findViewById(R.id.main_update);
        TextView tv = (TextView) convertView.findViewById(R.id.main_gv_tv);
        ImageView dele = (ImageView) convertView.findViewById(R.id.delete_iv);
        dele.setVisibility(isShowDelete?View.VISIBLE:View.GONE);
        dele.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).setMessage(context.getString(R.string.delete) + "' " + childs.get(position).fileName + " '")
                        .setCancelable(false)
                        .setNeutralButton(android.R
                                .string.ok, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File file = new File(childs.get(position).filePath);
                                if (file.exists() && file.isDirectory()) {
                                    FileUtils.deleteDirectory(childs.get(position).filePath);
                                }
                                childs.remove(position);
                                notifyDataSetChanged();
                            }
                        }).setNegativeButton(R.string.cancel, null).show();
            }
        });
        //TextView path = (TextView) convertView.findViewById(id.main_path);
        update.setVisibility(View.INVISIBLE);
        lv.setBackgroundResource(R.color.default_style);
        String Url;
        String ipUrl = this.m_iniFileIO.getIniString(this.userIni, "Store",
                "sourceStoreAddress", constants.DefaultServerIp, (byte) 0);
        String portUrl = this.m_iniFileIO.getIniString(this.userIni, "Store",
                "sourceStorePort", constants.DefaultServerPort, (byte) 0);
        Url = "http://" + ipUrl + ":" + portUrl;
        this.imageLoader.DisplayImage(Url + "/FileStore/image/" + childs.get(position).fileName + ".png",
                iv);
        //iv.setImageResource(drawable.clean_category_thumbnails);
        tv.setText(this.childs.get(position).fileName);
        return convertView;
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