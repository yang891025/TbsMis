package com.tbs.tbsmis.download;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.source.FileSourceActivity;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by TBS on 2018/3/9.
 */

@SuppressLint("ValidFragment")
public class DownLoadingFragment extends Fragment
{
    private ListView mDownloadingListView;
    private List<ChapterDownloadTask> mDownloadinglist;
    private DownloadingAdapter mDownloadingAdapter;
    private RelativeLayout btnDownloaded;
    private Activity mContext;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.mContext = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.download_list, container, false);
        btnDownloaded = (RelativeLayout) view.findViewById(R.id.buttonDownloaded);
        btnDownloaded.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(mContext, FileSourceActivity.class);
                startActivity(intent);
            }
        });
        mDownloadingListView = (ListView) view.findViewById(R.id.downloadingListView);
        // downloading list
        mDownloadingListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                ChapterDownloadTask task = mDownloadinglist.get(arg2);
                switch (task.getDownloadState()) {
                    case PAUSE:
                        Log.i(TAG, "PAUSE continue " + task.getFileName());
                        DownloadTaskManager.getInstance(mContext).continueDownload(task);
                        //addListener(task);
                        break;
                    case FAILED:
                        Log.i(TAG, "FAILED continue " + task.getFileName());
                        DownloadTaskManager.getInstance(mContext).continueDownload(task);
                        //addListener(task);
                        break;
                    case DOWNLOADING:
                        Log.i(TAG, "DOWNLOADING pause " + task.getFileName());
                        DownloadTaskManager.getInstance(mContext).pauseDownload(task);

                        break;
                    case INITIALIZE:

                        break;
                    default:
                        break;
                }

            }
        });

        mDownloadingListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2,
                                           final long arg3) {
                new AlertDialog.Builder(mContext)
                        .setItems(
                                new String[]{
                                        mContext.getString(R.string.download_delete_task),
                                        mContext.getString(R.string.download_delete_task_file)
                                }, new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            Toast.makeText(mContext,
                                                    R.string.download_deleted_task_ok,
                                                    Toast.LENGTH_LONG).show();
                                            DownloadTaskManager.getInstance(mContext)
                                                    .deleteDownloadTask(mDownloadinglist.get(arg2));
                                            mDownloadinglist.remove(arg2);
                                            mDownloadingAdapter.notifyDataSetChanged();
                                        } else if (which == 1) {
                                            Toast.makeText(mContext,
                                                    R.string.download_deleted_task_file_ok,
                                                    Toast.LENGTH_LONG).show();
                                            DownloadTaskManager.getInstance(mContext)
                                                    .deleteDownloadTask(mDownloadinglist.get(arg2));
                                            DownloadTaskManager.getInstance(mContext)
                                                    .deleteDownloadTaskFile(mDownloadinglist.get(arg2));
                                            mDownloadinglist.remove(arg2);
                                            mDownloadingAdapter.notifyDataSetChanged();
                                        }

                                    }
                                }).create().show();
                return true;
            }
        });

        mDownloadinglist = DownloadTaskManager.getInstance(mContext).getDownloadingTask();
        mDownloadingAdapter = new DownloadingAdapter(mContext, 0, mDownloadinglist);
        mDownloadingListView.setAdapter(mDownloadingAdapter);

        for (final ChapterDownloadTask task : mDownloadinglist) {
            if (!task.getDownloadState().equals(DownloadState.FINISHED)) {
                Log.d(TAG, "add listener");
                addListener(task);
            }
        }
        //DownloadOperator.check(mContext);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    class MyDownloadListener implements DownloadListener
    {
        private ChapterDownloadTask task;

        public MyDownloadListener(ChapterDownloadTask downloadTask) {
            task = downloadTask;
        }

        @Override
        public void onDownloadFinish(String filepath, String title) {
            Log.d(TAG, "onDownloadFinish");
            task.setDownloadState(DownloadState.FINISHED);
            task.setFinishedSize(task.getFinishedSize());
            task.setPercent(100);
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run() {
                    mDownloadingAdapter.notifyDataSetChanged();
                    mDownloadingAdapter.remove(task);
                    // toggleView(true);
                }
            });

        }

        @Override
        public void onDownloadStart() {
            task.setDownloadState(DownloadState.INITIALIZE);
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run() {
                    mDownloadingAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onDownloadPause() {
            Log.d(TAG, "onDownloadPause");
            task.setDownloadState(DownloadState.PAUSE);
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run() {
                    mDownloadingAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onDownloadStop() {
            Log.d(TAG, "onDownloadStop");
            task.setDownloadState(DownloadState.PAUSE);
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run() {
                    mDownloadingAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onDownloadFail() {
            Log.d(TAG, "onDownloadFail");
            task.setDownloadState(DownloadState.FAILED);
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run() {
                    mDownloadingAdapter.notifyDataSetChanged();
                }
            });
        }


        @Override
        public void onDownloadProgress(long finishedSize, long totalSize,
                                       int speed) {
            Log.d(TAG, "download " + finishedSize);
            task.setDownloadState(DownloadState.DOWNLOADING);
            task.setFinishedSize(finishedSize);
            task.setTotalSize(totalSize);
            task.setPercent((int) (finishedSize * 100 / totalSize));
            task.setSpeed(speed);

            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run() {
                    mDownloadingAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    public void addListener(ChapterDownloadTask task) {
        DownloadTaskManager.getInstance(mContext).registerListener(task, new MyDownloadListener(task));
    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
