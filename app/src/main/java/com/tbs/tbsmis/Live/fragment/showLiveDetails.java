package com.tbs.tbsmis.Live.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tbs.circle.widgets.ExpandTextView;
import com.tbs.tbsmis.Live.bean.LiveAlllist;
import com.tbs.tbsmis.Live.mvp.contract.LiverContract;
import com.tbs.tbsmis.Live.utils.DataUtils;
import com.tbs.tbsmis.R;


/**
 * Created by ELVIS on 2015/10/25.
 */
public class showLiveDetails extends Fragment implements LiverContract.View{
    private ImageView userimage;
    private TextView usernickname;
    private TextView room_id;
    private Button subject;
    private ExpandTextView contentTv;
    private LiveAlllist liver;

    public static showLiveDetails newInstance(LiveAlllist liver) {
        showLiveDetails fragment = new showLiveDetails();
        Bundle bundle = new Bundle();
        bundle.putSerializable("liver", liver);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        if (args != null) {
            liver = (LiveAlllist) args.getSerializable("liver");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_live_liverinfo, container, false);
        //inflater.inflate(R.layout.fragment_pot, container, false);
        this.initViews(view);
        return view;
    }

    private void initViews(View view) {
        usernickname = (TextView) view.findViewById(R.id.usernickname);
        room_id = (TextView) view.findViewById(R.id.room_id);
        subject = (Button) view.findViewById(R.id.subject);
        userimage = (ImageView) view.findViewById(R.id.userimage);
        contentTv = (ExpandTextView)view.findViewById(R.id.contentTv);
        Glide.with(this).load(DataUtils.HOST+"filePath/static/tbsermImage/liver/"+liver.getOwner_pic()).diskCacheStrategy(DiskCacheStrategy
                .ALL)
                .placeholder(R.drawable.default_avatar).into(userimage);
        usernickname.setText(liver.getNickname());
        room_id.setText("房间ID：" + liver.getRoom_id());
        contentTv.setText(liver.getSpecific_catalog());

    }

    @Override
    public void showLoading(String msg) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void LoadingProgress(int progress) {

    }

    @Override
    public void showError(String errorMsg) {

    }

    @Override
    public Context getBaseContext() {
        return null;
    }

    @Override
    public void addentiomCallback() {

    }
}
