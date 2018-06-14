package com.tbs.tbsmis.video;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.tbs.tbsmis.util.UIHelper;

import java.util.List;

public class VideolistAdapter extends ArrayAdapter<ChapterDownloadTask>
{
    // �����ݵ�list
    private final List<ChapterDownloadTask> list;
    private LayoutInflater inflater;
    private final Context context;
    private Activity activity;

    // ������
    @SuppressLint("UseSparseArrays")
    public VideolistAdapter(Context context, int textViewResourceId, List<ChapterDownloadTask> taskList) {
        super(context, textViewResourceId, taskList);
        this.context = context;
        this.activity = (Activity) context;
        this.list = taskList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public ChapterDownloadTask getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        holder = new ViewHolder();
        if (this.list.get(position).getSection().equals("0")) {
            // 如果是标签项
            convertView = this.inflater.inflate(R.layout.chapter_tag, null);
            holder.name = (TextView) convertView
                    .findViewById(R.id.group_list_item_text);
            holder.name.setText(this.list.get(position).getTitle());
        } else {
            convertView = this.inflater.inflate(R.layout.section_tag, null);
            holder.name = (TextView) convertView
                    .findViewById(R.id.name);
            holder.name.setText(list.get(position).getTitle());
            holder.round_progress = (CircleProgressView) convertView
                    .findViewById(R.id.circleProgressbar);
            holder.media_size = (TextView) convertView
                    .findViewById(R.id.media_size);
            holder.download_controll = (ImageView) convertView
                    .findViewById(R.id.download_controll);
            holder.round_progress.setMaxProgress(100);
            if (list.get(position).getPercent() > 0) {
                holder.round_progress.setProgress(list.get(position).getPercent());
            }
            if (list.get(position).getType().equalsIgnoreCase("0")||list.get(position).getType().equalsIgnoreCase("4")) {
//            System.out.println(list.get(position).getFilePath()+list.get(position).getFileName());
                //File file = new File(list.get(position).getFilePath() + "/" + list.get(position).getFileName());
                if (DownloadTaskManager.getInstance(context).isUrlDownloaded(list.get(position).getUrl())) {
                    IniFile IniFile = new IniFile();
                    String configPath = context.getFilesDir().getParentFile()
                            .getAbsolutePath();
                    if (configPath.endsWith("/") == false) {
                        configPath = configPath + "/";
                    }
                    String appIniFile = configPath + constants.USER_CONFIG_FILE_NAME;
                    String endTime = IniFile.getIniString(appIniFile, "data", list.get(position).getUrl(),
                            "", (byte) 0);
                    if (StringUtils.isEmpty(endTime)) {
                        holder.media_size.setText("已下载");
                        holder.download_controll.setVisibility(View.GONE);
                    } else {
//                        System.out.println("startTime="+list.get(position).getUpdateTime());
//                        System.out.println("endTime="+endTime);
                        if (StringUtils.isInTimerFormat(list.get(position).getUpdateTime(), endTime) == -1) {
                            holder.media_size.setText("可更新");
                        } else {
                            holder.media_size.setText("已下载");
                            holder.download_controll.setVisibility(View.GONE);
                        }
                    }

                } else if (DownloadTaskManager.getInstance(context).isUrlDownloading(list.get(position).getUrl())) {
                    holder.media_size.setText("正在下载");
                    holder.download_controll.setBackgroundResource(R.drawable.download_waiting_icon);
                } else if (DownloadTaskManager.getInstance(context).isUrlDownloadPause(list.get(position).getUrl())) {
                    holder.media_size.setText("已暂停");
                    holder.download_controll.setBackgroundResource(R.drawable.download_pausing_icon);
                } else if (DownloadTaskManager.getInstance(context).isUrlDownloadWaiting(list.get(position).getUrl())) {
                    holder.media_size.setText("等待下载");
                    holder.download_controll.setBackgroundResource(R.drawable.download_waiting_icon);
                }
//
                    //final ViewHolder finalHolder = holder;
                    holder.download_controll.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v) {
                            switch (list.get(position).getDownloadState()) {
//                                case FINISHED:
//                                    //onDownloadFinishedClick(list.get(position));
//                                    FileUtils.deleteFileWithPath(list.get(position).getFilePath() + "/" + list.get
//                                            (position).getFileName());
//                                    addListener(list.get(position));
//                                    DownloadTaskManager.getInstance(context).startDownload(list.get(position));
//                                    notifyDataSetChanged();
//                                    break;
                                case INITIALIZE:
                                    int wifiState = UIHelper.downloadWifiState(context);
                                    //System.out.println("wifiState = "+wifiState);
                                    switch (wifiState) {
                                        case 1:
                                            FileUtils.deleteFileWithPath(list.get(position).getFilePath() + "/" +
                                                    list.get(position).getFileName());
                                            addListener(list.get(position));
                                            DownloadTaskManager.getInstance(context).startDownload(list.get(position));
                                            notifyDataSetChanged();
                                            break;
                                        case 2:
                                            new AlertDialog.Builder(context)
                                                    .setTitle("提醒")
                                                    .setCancelable(false)
                                                    .setMessage("当前非WIFI，是否继续下载？")
                                                    .setPositiveButton("取消", new DialogInterface.OnClickListener()
                                                    {
                                                        @Override
                                                        public void onClick(
                                                                DialogInterface dialog,
                                                                int which) {
                                                            dialog.dismiss();
                                                            return;
                                                        }
                                                    })
                                                    .setNegativeButton("确定", new DialogInterface.OnClickListener()
                                                    {
                                                        @Override
                                                        public void onClick(
                                                                DialogInterface dialog,
                                                                int which) {
                                                            FileUtils.deleteFileWithPath(list.get(position).getFilePath() + "/" +
                                                                    list.get(position).getFileName());
                                                            addListener(list.get(position));
                                                            DownloadTaskManager.getInstance(context).startDownload(list.get(position));
                                                            notifyDataSetChanged();
                                                        }
                                                    }).create().show();

                                            break;
                                        case 3:
                                            new AlertDialog.Builder(context)
                                                    .setTitle("提醒")
                                                    .setCancelable(false)
                                                    .setMessage("当前无网络，请取消！")
                                                    .setPositiveButton("取消", new DialogInterface.OnClickListener()
                                                    {
                                                        @Override
                                                        public void onClick(
                                                                DialogInterface dialog,
                                                                int which) {
                                                            dialog.dismiss();
                                                            return;
                                                        }
                                                    }).create().show();

                                            break;
                                    }
                                default:
                                    break;
                            }
                        }
                    });

            } else {
                holder.download_controll.setVisibility(View.GONE);
            }
        }

        convertView.setTag(holder);
        return convertView;
    }

    private String formatSize(long finishedSize, long totalSize) {
        StringBuilder sb = new StringBuilder(50);

        float finished = ((float) finishedSize) / 1024 / 1024;
        if (finished < 1) {
            sb.append(String.format("%1$.2f K / ", ((float) finishedSize) / 1024));
        } else {
            sb.append((String.format("%1$.2f M / ", finished)));
        }

        float total = ((float) totalSize) / 1024 / 1024;
        if (total < 1) {
            sb.append(String.format("%1$.2f K ", ((float) totalSize) / 1024));
        } else {
            sb.append(String.format("%1$.2f M ", total));
        }
        return sb.toString();
    }

    public static class ViewHolder
    {
        public TextView name;
        public String childName;
        public String time;
        public String sort;
        public String allchapter;
        private CircleProgressView round_progress;
        public TextView media_size;
        public ImageView download_controll;
    }

    //    class MyDownloadListener implements DownloadListener
//    {
//        private ChapterDownloadTask task;
//
//        public MyDownloadListener(ChapterDownloadTask downloadTask) {
//            task = downloadTask;
//        }
//
//        @Override
//        public void onDownloadFinish(String filepath) {
//
//            task.setDownloadState(FINISHED);
//            task.setFinishedSize(task.getFinishedSize());
//            task.setPercent(100);
//            activity.runOnUiThread(new Runnable()
//            {
//                @Override
//                public void run() {
//                    notifyDataSetChanged();
//                    //ChapterlistAdaper.remove(task);
//                    // toggleView(true);
//                }
//            });
//
//        }
//
//        @Override
//        public void onDownloadStart() {
//            task.setDownloadState(INITIALIZE);
//            activity.runOnUiThread(new Runnable()
//            {
//                @Override
//                public void run() {
//                    notifyDataSetChanged();
//                }
//            });
//        }
//
//        @Override
//        public void onDownloadPause() {
//            task.setDownloadState(PAUSE);
//            activity.runOnUiThread(new Runnable()
//            {
//                @Override
//                public void run() {
//                    notifyDataSetChanged();
//                }
//            });
//        }
//
//        @Override
//        public void onDownloadStop() {
//            task.setDownloadState(PAUSE);
//            activity.runOnUiThread(new Runnable()
//            {
//                @Override
//                public void run() {
//                    notifyDataSetChanged();
//                }
//            });
//        }
//
//        @Override
//        public void onDownloadFail() {
//            task.setDownloadState(FAILED);
//            activity.runOnUiThread(new Runnable()
//            {
//                @Override
//                public void run() {
//                    notifyDataSetChanged();
//                }
//            });
//        }
//
//
//        @Override
//        public void onDownloadProgress(long finishedSize, long totalSize,
//                                       int speed) {
//            // Log.d(TAG, "download " + finishedSize);
//            task.setDownloadState(DOWNLOADING);
//            task.setFinishedSize(finishedSize);
//            task.setTotalSize(totalSize);
//            task.setPercent((int) (finishedSize * 100 / totalSize));
//            task.setSpeed(speed);
//
//            activity.runOnUiThread(new Runnable()
//            {
//                @Override
//                public void run() {
//                    notifyDataSetChanged();
//                }
//            });
//        }
//    }
//
    public void addListener(ChapterDownloadTask task) {
        DownloadTaskManager.getInstance(context).registerListener(task, new DownloadNotificationListener(context,
                task));
    }
}