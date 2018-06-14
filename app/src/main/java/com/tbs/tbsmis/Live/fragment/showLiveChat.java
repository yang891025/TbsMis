package com.tbs.tbsmis.Live.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.tbs.tbsmis.R;

/**
 * Created by tbs on 2016/10/25.
 */
public class showLiveChat extends Fragment
{

    private ExpandableListView video_list_info;
    private MyBroadcastReciver MyBroadcastReciver;

    public static showLiveChat newInstance() {
        showLiveChat fragment = new showLiveChat();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("mChapter" + getString(R.string.app_name));
//        intentFilter.addAction("download_refresh" + getString(R.string.app_name));
//        MyBroadcastReciver = new MyBroadcastReciver();
//        getActivity().registerReceiver(MyBroadcastReciver, intentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_pot, container, false);
        this.initViews(view);
        return view;
    }

    public void initViews(View view) {
        this.video_list_info = (ExpandableListView) view.findViewById(R.id.video_list_info);
    }

    private class MyBroadcastReciver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // getActivity().unregisterReceiver(this.MyBroadcastReciver);
    }
}
