package com.tbs.tbsmis.Live.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tbs.tbsmis.R;

/**
 * Created by TBS on 2017/11/30.
 */

public class VideoFragment extends Fragment
{
    public static showLiveAllColumn newInstance() {
        showLiveAllColumn fragment = new showLiveAllColumn();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_live_commoncolumn, container, false);
        this.initViews(view);
        return view;
    }

    public void initViews(View view) {
        TextView iv_empty_text = (TextView) view.findViewById(R.id.iv_empty_text);
        iv_empty_text.setText("这里是视频界面");
    }
}