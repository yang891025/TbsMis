package com.tbs.tbsmis.source;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.file.Util;
import com.tbs.tbsmis.update.AndroidDownloadAsyncTask;
import com.tbs.tbsmis.util.FileIO;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.ImageLoader;
import com.tbs.tbsmis.util.JsoupExam;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;
import com.tbs.tbsmis.util.htmlJsoupUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

// 完成gridview 数据到界面的适配
@SuppressLint("ResourceAsColor")
public class FileSourceViewAdapter extends BaseAdapter
{
    private static final String TAG = "AppGridViewAdapter";
    private Context context;
    private String iniPath;
    private ImageLoader imageLoader;
    private int isload = 1;
    LayoutInflater infalter;
    private List<SourceInfo> childs;

    public FileSourceViewAdapter(Context context, List<SourceInfo> childs, String iniPath) {
        this.context = context;
        this.childs = childs;
        this.iniPath = iniPath;
        // 方法1 通过系统的service 获取到 试图填充器
        this.infalter = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // 方法2 通过layoutinflater的静态方法获取到 视图填充器
        // infalter = LayoutInflater.from(context);
        this.imageLoader = new ImageLoader(context, R.drawable.format_picture);
        this.isload = 1;
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
        FileSourceViewAdapter.ViewHolder holder = null;
        if (convertView == null) {
            // ���ViewHolder����
            holder = new FileSourceViewAdapter.ViewHolder();
            // ���벼�ֲ���ֵ��convertview
            convertView = this.infalter.inflate(R.layout.file_source_view, null);
            // Ϊview���ñ�ǩ
            convertView.setTag(holder);
        } else {
            // ȡ��holder
            holder = (FileSourceViewAdapter.ViewHolder) convertView.getTag();
        }
        ImageView img = (ImageView) convertView.findViewById(R.id.icon_iv);
        ImageView type_iv = (ImageView) convertView.findViewById(R.id.type_iv);
        ImageView share_iv = (ImageView) convertView.findViewById(R.id.share_iv);
        ImageView delete_iv = (ImageView) convertView.findViewById(R.id.delete_iv);
        ImageView collect_iv = (ImageView) convertView.findViewById(R.id.collect_iv);
        TextView tv = (TextView) convertView.findViewById(R.id.title_tv);
        TextView description = (TextView) convertView.findViewById(R.id.intro_tv);
        TextView category = (TextView) convertView.findViewById(R.id.tag_tv);
        TextView time = (TextView) convertView.findViewById(R.id.time_tv);
        TextView play = (TextView) convertView.findViewById(R.id.count_tv);
        TextView longtime = (TextView) convertView.findViewById(R.id.type_info_tv);
        RelativeLayout stop_layout = (RelativeLayout) convertView.findViewById(R.id.stop_layout);
        RelativeLayout controll_layout = (RelativeLayout) convertView.findViewById(R.id.controll_layout);
        String Pic = childs.get(position).getPic();
        if (!Pic.isEmpty()) {
            if (Pic.substring(Pic.lastIndexOf("/")).contains(".") && Pic.contains("http://")) {
                this.imageLoader.DisplayImage(Pic,
                        img);
            } else {
                this.imageLoader.DisplayImage(childs.get(position).getPath(),
                        img);
            }
        } else {
            this.imageLoader.DisplayImage(childs.get(position).getPath(),
                    img);
        }
        description.setText(this.childs.get(position).getDescription());
        category.setText(this.childs.get(position).getCategory());
        time.setText(this.childs.get(position).getTime());
        play.setText(this.childs.get(position).getPlay() + "观看");
        if (isload <= childs.size()) {
            isload++;
            MyAsyncTask task = new MyAsyncTask(context, position, 1);
            task.execute();
        }
        if (childs.get(position).getIsStop() == 1) {
            stop_layout.setVisibility(View.VISIBLE);
        } else {
            stop_layout.setVisibility(View.GONE);
        }
        if (childs.get(position).getUpateUrl().isEmpty()) {
            controll_layout.setVisibility(View.GONE);
        } else {
            controll_layout.setVisibility(View.VISIBLE);
        }

        final String title = this.childs.get(position).getTitle();
        if (title.isEmpty())
            tv.setText(this.childs.get(position).getName());
        else
            tv.setText(title);
        controll_layout.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).setMessage("更新 ' " + title + " '")
                        .setCancelable(false)
                        .setNeutralButton(android.R
                                .string.ok, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (childs.get(position).getType().equalsIgnoreCase("file")) {
                                    String filepath = childs.get(position).getPath();
                                    String urlPath = childs.get(position).getNetworkPath();
                                    if (filepath.toLowerCase().endsWith(".html")) {
                                        String jsonString = "{'category':'" + childs.get(position).getCategory() +
                                                "'," +
                                                "'range':'" + childs.get(position).getRange() + "'," +
                                                "'type':'知识'," +
                                                "'pic':'" + childs.get(position).getPic() + "'}";
                                        if (childs.get(position).getCategory().equalsIgnoreCase("考试"))
                                            jsonString = "{'category':'" + childs.get(position).getCategory() +
                                                    "'," +
                                                    "'range':'" + childs.get(position).getRange() + "'," +
                                                    "'type':'" + childs.get(position).getCategory() + "'," +
                                                    "'pic':'" + childs.get(position).getPic() + "'}";
                                        JsoupExam.getHtmlSource(jsonString, childs.get(position).getTitle(), urlPath,
                                                context);
                                        childs.get(position).setUpateUrl("");
                                        notifyDataSetChanged();
                                    } else {
                                        IniFile IniFile = new IniFile();
                                        String webRoot = UIHelper.getShareperference(context, constants
                                                        .SAVE_INFORMATION,
                                                "Path", "");
                                        if (webRoot.endsWith("/") == false) {
                                            webRoot += "/";
                                        }
                                        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
                                        String appTestFile = webRoot
                                                + IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                                                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
                                        String ipUrl = IniFile.getIniString(appTestFile, "TBSAPP",
                                                "webAddress", constants.DefaultServerIp, (byte) 0);
                                        String portUrl = IniFile.getIniString(appTestFile, "TBSAPP",
                                                "webPort", constants.DefaultServerPort, (byte) 0);
                                        String baseUrl = "http://" + ipUrl + ":" + portUrl;
                                        String FilePath = UIHelper.getStoragePath(context);
                                        FilePath = FilePath + constants.SD_CARD_TBSFILE_PATH6;
                                        String subPath = iniPath.substring(0, iniPath.lastIndexOf("/"));
                                        subPath = subPath.substring(FilePath.length() + 1, subPath.length());
                                        AndroidDownloadAsyncTask task = new AndroidDownloadAsyncTask(
                                                context, baseUrl + childs.get(position).getUpateUrl(), subPath);
                                        task.execute();
                                        IniFile iniFile = new IniFile();
                                        String time = StringUtils.getDate();
                                        childs.get(position).setUpateUrl("");
                                        notifyDataSetChanged();
                                        iniFile.writeIniString(iniPath, "file" + childs.get(position).getLocation(),
                                                "time", time);

                                    }
                                } else {
                                    IniFile IniFile = new IniFile();
                                    String webRoot = UIHelper.getShareperference(context, constants.SAVE_INFORMATION,
                                            "Path", "");
                                    if (webRoot.endsWith("/") == false) {
                                        webRoot += "/";
                                    }
                                    String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
                                    String appTestFile = webRoot
                                            + IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                                            constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
                                    String ipUrl = IniFile.getIniString(appTestFile, "TBSAPP",
                                            "webAddress", constants.DefaultServerIp, (byte) 0);
                                    String portUrl = IniFile.getIniString(appTestFile, "TBSAPP",
                                            "webPort", constants.DefaultServerPort, (byte) 0);
                                    String baseUrl = "http://" + ipUrl + ":" + portUrl;
                                    String FilePath = UIHelper.getStoragePath(context);
                                    FilePath = FilePath + constants.SD_CARD_TBSFILE_PATH6;
                                    String subPath = iniPath.substring(0, iniPath.lastIndexOf("/"));
                                    subPath = subPath.substring(FilePath.length() + 1, subPath.length());
                                    AndroidDownloadAsyncTask task = new AndroidDownloadAsyncTask(
                                            context, baseUrl + childs.get(position).getUpateUrl(), subPath);
                                    task.execute();
                                    IniFile iniFile = new IniFile();
                                    String time = StringUtils.getDate();
                                    childs.get(position).setUpateUrl("");
                                    notifyDataSetChanged();
                                    iniFile.writeIniString(iniPath, "file" + childs.get(position).getLocation(),
                                            "time", time);
                                }
                            }
                        }).setNegativeButton(R.string.cancel, null).show();
            }
        });
        if (this.childs.get(position).getType().equalsIgnoreCase("file")) {
            type_iv.setImageResource(R.drawable.icon_article);
            String filepath = childs.get(position).getPath();
            if (filepath.toLowerCase().endsWith(".html")) {
                File file = new File(filepath.substring(0, filepath.lastIndexOf("/")));
                if (file.isDirectory())
                    longtime.setText(Util.convertStorage(FileUtils.getDirSize(file)));
            } else {
                File file = new File(filepath);
                longtime.setText(Util.convertStorage(file.length()));
            }
        } else {
            type_iv.setImageResource(R.drawable.icon_video);
            if (this.childs.get(position).getLongtime().isEmpty()) {
                Cursor cursor = context.getContentResolver().query(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.Media
                                .DURATION}, MediaStore.Video.Media.TITLE + "='" + this.childs.get(position).getTitle
                                () + "'",
                        null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToNext();
                    long duration = cursor
                            .getInt(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                    long min = duration / 1000 / 60;
                    long sec = duration / 1000 % 60;
                    longtime.setText(min + " : " + sec);
                    IniFile iniFile = new IniFile();
                    iniFile.writeIniString(iniPath, "file" + childs.get(position).getLocation(), "longtime", min + " " +
                            ": " + sec);
                    childs.get(position).setLongtime(min + " : " + sec);
                    cursor.close();
                }
            } else {
                longtime.setText(this.childs.get(position).getLongtime());
            }
        }

        delete_iv.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context).setMessage(context.getString(R.string.delete) + "' " + title + " '")
                        .setCancelable(false)
                        .setNeutralButton(android.R
                                .string.ok, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FileUtils.deleteFileWithPath(childs.get(position).getPath());
                                if (childs.get(position).getName().contains("/")) {
                                    String fileDir = iniPath.substring(0, iniPath.lastIndexOf("/") + 1);
                                    String dir = childs.get(position).getName().substring(0, childs.get(position)
                                            .getName().indexOf("/"));
                                    File file = new File(fileDir + dir);
                                    if (file.exists() && file.isDirectory()) {
                                        FileUtils.deleteDirectory(fileDir + dir);
                                    }
                                }
                                delData(iniPath, childs.get(position).getLocation());
                                childs.remove(position);
                                notifyDataSetChanged();
                            }
                        }).setNegativeButton(R.string.cancel, null).show();

            }
        });
        share_iv.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view) {
                UIHelper.Share(context, childs.get(position).getNetworkPath(), title.trim(), childs.get(position)
                        .getDescription(), childs.get(position).getPic());
            }
        });
        collect_iv.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Toast.makeText(FileSourceViewAdapter.this.context, "点击了收藏按钮", Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    private void delData(String iniPath, int location) {
        IniFile iniFile = new IniFile();
        int count = Integer.parseInt(iniFile.getIniString(iniPath, "file", "count", "0", (byte) 0));
        for (int i = location; i <= count; i++) {
            String category = iniFile.getIniString(iniPath, "file" + (i + 1), "category", "", (byte) 0);
            String description = iniFile.getIniString(iniPath, "file" + (i + 1), "description", "", (byte) 0);
            String longtime = iniFile.getIniString(iniPath, "file" + (i + 1), "longtime", "", (byte) 0);
            String stop = iniFile.getIniString(iniPath, "file" + (i + 1), "stop", "", (byte) 0);
            String time = iniFile.getIniString(iniPath, "file" + (i + 1), "time", "", (byte) 0);
            String type = iniFile.getIniString(iniPath, "file" + (i + 1), "type", "", (byte) 0);
            String range = iniFile.getIniString(iniPath, "file" + (i + 1), "range", "", (byte) 0);
            String size = iniFile.getIniString(iniPath, "file" + (i + 1), "size", "0", (byte) 0);
            String shareUrl = iniFile.getIniString(iniPath, "file" + (i + 1), "shareUrl", "", (byte) 0);
            String download = iniFile.getIniString(iniPath, "file" + (i + 1), "download", "0",
                    (byte) 0);
            String title = iniFile.getIniString(iniPath, "file" + (i + 1), "title", "", (byte) 0);
            String name = iniFile.getIniString(iniPath, "file" + (i + 1), "name", "", (byte) 0);
            String code = iniFile.getIniString(iniPath, "file" + (i + 1), "code", "", (byte) 0);
            String MD5code = iniFile.getIniString(iniPath, "file" + (i + 1), "MD5code", "", (byte) 0);
            String play = iniFile.getIniString(iniPath, "file" + (i + 1), "play", "0", (byte) 0);
            String chapter = iniFile.getIniString(iniPath, "file" + (i + 1), "chapter", "", (byte) 0);
            String path = iniFile.getIniString(iniPath, "file" + (i + 1), "path", "", (byte) 0);
            iniFile.writeIniString(iniPath, "file" + i, "category", category);
            iniFile.writeIniString(iniPath, "file" + i, "range", range);
            iniFile.writeIniString(iniPath, "file" + i, "longtime", longtime);
            iniFile.writeIniString(iniPath, "file" + i, "description", description);
            iniFile.writeIniString(iniPath, "file" + i, "stop", stop);
            iniFile.writeIniString(iniPath, "file" + i, "time", time);
            iniFile.writeIniString(iniPath, "file" + i, "type", type);
            iniFile.writeIniString(iniPath, "file" + i, "size", size);
            iniFile.writeIniString(iniPath, "file" + i, "shareUrl", shareUrl);
            iniFile.writeIniString(iniPath, "file" + i, "download", download);
            iniFile.writeIniString(iniPath, "file" + i, "title", title);
            iniFile.writeIniString(iniPath, "file" + i, "name", name);
            iniFile.writeIniString(iniPath, "file" + i, "code", code);
            iniFile.writeIniString(iniPath, "file" + i, "MD5code", MD5code);
            iniFile.writeIniString(iniPath, "file" + i, "play", play);
            iniFile.writeIniString(iniPath, "file" + i, "chapter", chapter);
            iniFile.writeIniString(iniPath, "file" + i, "path", path);
        }
        iniFile.deleteIniSection(iniPath, "file" + count);
        iniFile.writeIniString(iniPath, "file", "count", (count - 1) + "");
    }


    public static class ViewHolder
    {
        public String update;
        public String downPath;
    }

    class MyAsyncTask extends AsyncTask<Integer, Integer, String>
    {
        private final Context context;
        private int position;
        private int count;
        private IniFile IniFile;

        public MyAsyncTask(Context c, int position, int count) {
            context = c;
            this.count = count;
            this.position = position;
            IniFile = new IniFile();
        }

        /**
         * 这里的Integer参数对应AsyncTask中的第一个参数 这里的String返回值对应AsyncTask的第三个参数
         * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
         * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
         */
        @Override
        protected String doInBackground(Integer... params) {
            String webRoot = UIHelper.getShareperference(this.context, constants.SAVE_INFORMATION,
                    "Path", "");
            if (webRoot.endsWith("/") == false) {
                webRoot += "/";
            }
            String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
            String appTestFile = webRoot
                    + IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                    constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
            String ipUrl = IniFile.getIniString(appTestFile, "TBSAPP",
                    "webAddress", constants.DefaultServerIp, (byte) 0);
            String portUrl = IniFile.getIniString(appTestFile, "TBSAPP",
                    "webPort", constants.DefaultServerPort, (byte) 0);
            String baseUrl = "http://" + ipUrl + ":" + portUrl;
            if (count == 1) {
                HttpConnectionUtil connection = new HttpConnectionUtil();
                String verifyURL = baseUrl
                        + "/FileStore/checkUpdate.cbs?fileCode=" + childs.get(position).getCode() + "&Path=" + childs
                        .get
                                (position).getTxtPath() + "&Time=" + URLEncoder.encode(childs.get(position).getTime());
                if (childs.get(position).getName()
                        .endsWith("html")) {
                    verifyURL = baseUrl
                            + "/FileStore/checkUpdate.cbs?fileCode=" + childs.get(position).getCode() +
                            "&Path=knowledge&Time=" + URLEncoder.encode(childs.get(position).getTime());
                }
                return connection.asyncConnect(verifyURL, null,
                        HttpConnectionUtil.HttpMethod.GET, this.context);
            } else if (count == 2) {
                String filepath = childs.get(position).getPath();
                String urlPath = childs.get(position).getNetworkPath();
                try {
                    FileIO.CreateFile(filepath, htmlJsoupUtil.modifyLink(JsoupExam.getHtml(urlPath), "", getWebPath
                            (urlPath) + "/" + getRelatePath(urlPath) + "/"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "ok";
            } else {

                return "ok";
            }
        }

        /**
         * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
         * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
         */
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                if (count == 1) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String courseTimes = jsonObject.getString("courseTimes");
                        String updateUrl = jsonObject.getString("updateUrl");
                        String isStop = jsonObject.getString("isStop");
                        childs.get(position).setPlay(Long.parseLong(courseTimes));
                        childs.get(position).setIsStop(Integer.parseInt(isStop));
                        childs.get(position).setUpateUrl(updateUrl);
                        notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (count == 2) {
                    IniFile iniFile = new IniFile();
                    String time = StringUtils.getDate();
                    childs.get(position).setTime(time);
                    notifyDataSetChanged();
                    iniFile.writeIniString(iniPath, "file" + childs.get(position).getLocation(), "time", time);
                } else {

                }
            }

        }

        // 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
        @Override
        protected void onPreExecute() {

        }

        /**
         * 这里的Intege参数对应AsyncTask中的第二个参数
         * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行
         * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
        }
    }

    private static String getRelatePath(String srcPath) {
        if (srcPath.contains("?"))
            srcPath = srcPath.substring(0, srcPath.indexOf("?"));
        if (srcPath.contains("//"))
            srcPath = srcPath.substring(srcPath.indexOf("//") + 2);
        if (srcPath.contains("/"))
            srcPath = srcPath.substring(srcPath.indexOf("/") + 1);
        else
            srcPath = "";
        if (srcPath.contains("/"))
            srcPath = srcPath.substring(0, srcPath.lastIndexOf("/"));
        return srcPath;
    }

    private static String getWebPath(String srcPath) {
        if (srcPath.contains("?"))
            srcPath = srcPath.substring(0, srcPath.indexOf("?"));
        if (srcPath.contains("//"))
            srcPath = srcPath.substring(srcPath.indexOf("//") + 2);
        if (srcPath.contains("/"))
            srcPath = srcPath.substring(0, srcPath.indexOf("/"));
        return "http://" + srcPath;
    }

}