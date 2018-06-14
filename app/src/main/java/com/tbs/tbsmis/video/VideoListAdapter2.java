package com.tbs.tbsmis.video;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.download.ChapterDownloadTask;
import com.tbs.tbsmis.download.CircleProgressView;
import com.tbs.tbsmis.download.DownloadNotificationListener;
import com.tbs.tbsmis.download.DownloadTaskManager;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.StringUtils;

import java.io.File;
import java.util.List;

/**
 * Created by TBS on 2017/4/24.
 */

public class VideoListAdapter2 extends BaseExpandableListAdapter
{
    private int checkChPosition;
    private int checkGrPosition;

    class ExpandableListHolder
    { // 定义一个内部类，用于保存listitem的3个子视图引用,2个textview和1个checkbox
        public TextView tvName;
        public TextView name;
        public String time;
        public String sort;
        private CircleProgressView round_progress;
        public TextView media_size;
        public TextView media_checked;
        public ImageView download_controll;
    }

    private final Context context; // 父activity
    private final LayoutInflater mChildInflater; // 用于加载listitem的布局xml
    private final LayoutInflater mGroupInflater; // 用于加载group的布局xml
    private final List<ChapterDownloadTask> groups; // 所有group
    private final List<List<ChapterDownloadTask>> childs; // 所有group
    private String webRoot;
    private ProgressDialog Prodialog;
    // 构造方法：参数c － activity，参数group － 所有group
    protected boolean Unupdate = true;

    public VideoListAdapter2(Context c, List<ChapterDownloadTask> groups,
                             List<List<ChapterDownloadTask>> childs) {
        this.context = c;
        this.mChildInflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mGroupInflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.groups = groups;
        this.childs = childs;
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
        ExpandableListHolder holder = null; // 清空临时变量holder
        if (view == null) { // 判断view（即view是否已构建好）是否为空
            // 若组视图为空，构建组视图。注意flate的使用，R.layout.browser_expandable_list_item代表了
            // 已加载到内存的browser_expandable_list_item.xml文件
            view = this.mGroupInflater.inflate(R.layout.chapter_tag, null);
            // 下面主要是取得组的各子视图，设置子视图的属性。用tag来保存各子视图的引用
            holder = new ExpandableListHolder();
            // 从view中取得textView
            holder.tvName = (TextView) view
                    .findViewById(R.id.group_list_item_text);
            if (position == checkGrPosition) {
                holder.tvName.setTextColor(context.getResources().getColor(R.color.red));
            } else {
                holder.tvName.setTextColor(context.getResources().getColor(R.color.black));
            }
            holder.tvName.setText(groups.get(position).getTitle());
            view.setTag(holder);
        } else { // 若view不为空，直接从view的tag属性中获得各子视图的引用
            holder = (ExpandableListHolder) view.getTag();
        }
        // TODO Auto-generated method stub
        return view;
    }

    // 行渲染方法
    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        ExpandableListHolder holder; // 清空临时变量
        // 通过flater初始化行视图
        convertView = this.mChildInflater.inflate(R.layout.section_tag, null);
        // 并将行视图的3个子视图引用放到tag中
        holder = new ExpandableListHolder();
        holder.tvName = (TextView) convertView.findViewById(R.id.name);
        holder.tvName.setText(childs.get(groupPosition).get(childPosition).getTitle());
        holder.round_progress = (CircleProgressView) convertView
                .findViewById(R.id.circleProgressbar);
        holder.media_size = (TextView) convertView
                .findViewById(R.id.media_size);
        holder.media_checked = (TextView) convertView
                .findViewById(R.id.media_checked);
        holder.download_controll = (ImageView) convertView
                .findViewById(R.id.download_controll);
        holder.round_progress.setMaxProgress(100);
        if (checkChPosition == childPosition && groupPosition == checkGrPosition) {
            holder.tvName.setTextColor(context.getResources().getColor(R.color.red));
            holder.media_checked.setTextColor(context.getResources().getColor(R.color.red));
        } else {
            holder.tvName.setTextColor(context.getResources().getColor(R.color.black));
            holder.media_checked.setTextColor(context.getResources().getColor(R.color.black));
        }
        if (childs.get(groupPosition).get(childPosition).getPercent() > 0) {
            holder.round_progress.setProgress(childs.get(groupPosition).get(childPosition).getPercent());
        }
        if (childs.get(groupPosition).get(childPosition).getType().equalsIgnoreCase("0") || childs.get(groupPosition)
                .get(childPosition).getType().equalsIgnoreCase("4")) {
//            System.out.println(childs.get(groupPosition).get(childPosition).getFilePath() + childs.get(groupPosition)
//                    .get(childPosition).getFileName());
            File temFile = new File(childs.get(groupPosition).get(childPosition).getFilePath() + "/" + childs.get
                    (groupPosition).get(childPosition).getFileName());
            //File file = new File(childs.get(groupPosition).get(childPosition).getFilePath() + "/" + childs.get
            // (groupPosition).get(childPosition).getFileName());
            if (DownloadTaskManager.getInstance(context).isUrlDownloading(childs.get(groupPosition).get
                    (childPosition).getUrl())) {
                holder.media_size.setText("正在下载");
                holder.download_controll.setBackgroundResource(R.drawable.download_waiting_icon);
            } else if (DownloadTaskManager.getInstance(context).isUrlDownloadPause(childs.get(groupPosition).get
                    (childPosition).getUrl())) {
                holder.media_size.setText("已暂停");
                holder.download_controll.setBackgroundResource(R.drawable.download_pausing_icon);
            } else if (DownloadTaskManager.getInstance(context).isUrlDownloadWaiting(childs.get(groupPosition)
                    .get(childPosition).getUrl())) {
                holder.media_size.setText("等待下载");
                holder.download_controll.setBackgroundResource(R.drawable.download_waiting_icon);
            } else if (temFile.isFile() && temFile.exists()) {
                IniFile IniFile = new IniFile();
                String configPath = context.getFilesDir().getParentFile()
                        .getAbsolutePath();
                if (configPath.endsWith("/") == false) {
                    configPath = configPath + "/";
                }
                String appIniFile = configPath + constants.USER_CONFIG_FILE_NAME;
                String endTime = IniFile.getIniString(appIniFile, "data", childs.get(groupPosition).get
                                (childPosition).getUrl(),
                        "", (byte) 0);
                if (StringUtils.isEmpty(endTime)) {
                    holder.media_size.setText("已下载");
                    holder.download_controll.setBackgroundResource(R.drawable.downloaded_button);
                } else {
                    if (StringUtils.isInTimerFormat(childs.get(groupPosition).get(childPosition).getUpdateTime(),
                            endTime) == -1) {
                        holder.media_size.setText("可更新");
                    } else {
                        holder.media_size.setText("已下载");
                        holder.download_controll.setBackgroundResource(R.drawable.downloaded_button);
                    }
                }

            }
            if (temFile.isFile() && temFile.exists()) {
                holder.download_controll.setOnClickListener(null);
            } else {
                holder.download_controll.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        switch (childs.get(groupPosition).get(childPosition).getDownloadState()) {
                            case FINISHED:
                                //onDownloadFinishedClick(childs.get(groupPosition).get(childPosition));
                                FileUtils.deleteFileWithPath(childs.get(groupPosition).get(childPosition).getFilePath
                                        () + "/" + childs.get(groupPosition).get
                                        (childPosition).getFileName());
                                addListener(childs.get(groupPosition).get(childPosition));
                                DownloadTaskManager.getInstance(context).startDownload(childs.get(groupPosition).get
                                        (childPosition));
                                notifyDataSetChanged();
                                break;
                            case INITIALIZE:
                                FileUtils.deleteFileWithPath(childs.get(groupPosition).get(childPosition).getFilePath
                                        () + "/" + childs.get(groupPosition).get
                                        (childPosition).getFileName());
                                addListener(childs.get(groupPosition).get(childPosition));
                                DownloadTaskManager.getInstance(context).startDownload(childs.get(groupPosition).get
                                        (childPosition));
                                notifyDataSetChanged();
                                break;
                            default:
                                break;
                        }
                    }
                });
            }

        } else {
            holder.download_controll.setVisibility(View.GONE);
        }
        convertView.setTag(holder);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {// 行是否具有唯一id
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {// 行是否可选
        return true;
    }

    public void setSelectItem(int groupPosition, int childPosition) {
        this.checkGrPosition = groupPosition;
        this.checkChPosition = childPosition;
    }

    public void addListener(ChapterDownloadTask task) {
        DownloadTaskManager.getInstance(context).registerListener(task, new DownloadNotificationListener(context,
                task));
    }
}
