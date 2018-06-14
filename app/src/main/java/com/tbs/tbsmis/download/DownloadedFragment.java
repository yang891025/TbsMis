package com.tbs.tbsmis.download;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.tbs.tbsmis.R;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by TBS on 2018/3/9.
 */

public class DownloadedFragment extends Fragment
{
    private ListView mDownloadingListView;
    private List<ChapterDownloadTask> mDownloadedlist;
    private DownloadingAdapter mDownloadingAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.download_list, container, false);
        mDownloadingListView = (ListView) view.findViewById(R.id.downloadingListView);
        // downloading list
        mDownloadingListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Log.d(TAG, "arg2" + arg2 + " mDownloadedlist" + mDownloadedlist.size());
                onDownloadFinishedClick(mDownloadedlist.get(arg2));
            }
        });
        mDownloadingListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2,
                                           final long arg3) {
                new AlertDialog.Builder(getContext())
                        .setItems(
                                new String[]{
                                        getContext().getString(R.string.download_delete_task),
                                        getContext().getString(R.string.download_delete_task_file)
                                }, new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            Toast.makeText(getContext(),
                                                    R.string.download_deleted_task_ok,
                                                    Toast.LENGTH_LONG).show();
                                            DownloadTaskManager.getInstance(getContext())
                                                    .deleteDownloadTask(mDownloadedlist.get(arg2));
                                            mDownloadedlist.remove(arg2);
                                            mDownloadingAdapter.notifyDataSetChanged();
                                        } else if (which == 1) {
                                            Toast.makeText(getContext(),
                                                    R.string.download_deleted_task_file_ok,
                                                    Toast.LENGTH_LONG).show();
                                            DownloadTaskManager.getInstance(getContext())
                                                    .deleteDownloadTask(mDownloadedlist.get(arg2));
                                            DownloadTaskManager.getInstance(getContext())
                                                    .deleteDownloadTaskFile(mDownloadedlist.get(arg2));
                                            mDownloadedlist.remove(arg2);
                                            mDownloadingAdapter.notifyDataSetChanged();
                                        }

                                    }
                                }).create().show();
                return false;
            }
        });
        reload();
        //DownloadOperator.check(mContext);
        return view;
    }

    private void reload() {
        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params) {
                mDownloadedlist = DownloadTaskManager.getInstance(getContext()).getFinishedDownloadTask();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mDownloadingAdapter = new DownloadingAdapter(getContext(), 0, mDownloadedlist);
                mDownloadingListView.setAdapter(mDownloadingAdapter);

                for (final ChapterDownloadTask task : mDownloadedlist) {
                    if (!task.getDownloadState().equals(DownloadState.FINISHED)) {
                        Log.d(TAG, "add listener");
                        addListener(task);
                    }
                }
            }
        }.execute();
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
            // Log.d(TAG, "download " + finishedSize);
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
        DownloadTaskManager.getInstance(getContext()).registerListener(task, new MyDownloadListener(task));
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    /**
     * You can overwrite this method to implement what you want do after download task item is clicked.
     *
     * @param task
     */
    public void onDownloadFinishedClick(ChapterDownloadTask task) {
        Log.d(TAG, task.getFilePath() + "/" + task.getFileName());
        Intent intent = DownloadOpenFile.openFile(task.getFilePath()
                + "/" + task.getFileName());
        if (null == intent) {
            Toast.makeText(getContext(), R.string.download_file_not_exist, Toast.LENGTH_LONG).show();
        } else {
            getContext().startActivity(intent);
        }
    }
}
