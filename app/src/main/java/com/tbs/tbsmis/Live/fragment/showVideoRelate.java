package com.tbs.tbsmis.Live.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.activity.OverviewActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.UIHelper;
import com.tbs.tbsmis.video.VideoRelateAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tbs on 2016/10/25.
 */
public class showVideoRelate extends Fragment
{

    private ListView video_list_info;
    private ArrayList<HashMap<String, String>> videolist;
    private String mRelate;
    private String mRelateUrl;

    public static showVideoRelate newInstance(String relate, String relateUrl) {
        showVideoRelate fragment = new showVideoRelate();
        Bundle bundle = new Bundle();
        bundle.putString("relate", relate);
        bundle.putString("relateUrl", relateUrl);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        if (args != null) {
            mRelate = args.getString("relate");
            mRelateUrl = args.getString("relateUrl");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_relate, container, false);
        this.initViews(view);
        return view;
    }

    public void initViews(View view) {
        this.video_list_info = (ListView) view.findViewById(R.id.video_list_info);
        this.videolist = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> grouplist = new ArrayList<HashMap<String, String>>();
        String [] Relate = new String[0];
        String [] RelateUrl = new String[0] ;
        if(mRelate.contains(",")){
            Relate = mRelate.split(",");
            RelateUrl = mRelateUrl.split(",");
        }else if(!mRelate.isEmpty()){
            Relate = new String[1];
            RelateUrl = new String[1] ;
            Relate[0] = mRelate;
            RelateUrl[0] = mRelateUrl;
        }
        for(int i = 0;i < Relate.length; i++ ){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("name", Relate[i]);
            map.put("Url", RelateUrl[i]);
            videolist.add(map);
        }
        if (videolist.size() > 0) {
            VideoRelateAdapter videolistAdaper = new VideoRelateAdapter(this.videolist, getActivity());
            video_list_info.setAdapter(videolistAdaper);
            video_list_info.setOnItemClickListener(new OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String webRoot = UIHelper.getSoftPath(getActivity());
                    if (webRoot.endsWith("/") == false) {
                        webRoot += "/";
                    }
                    webRoot += getString(R.string.SD_CARD_TBSAPP_PATH2);
                    webRoot = UIHelper.getShareperference(getActivity(), constants.SAVE_INFORMATION,
                            "Path", webRoot);
                    if (webRoot.endsWith("/") == false) {
                        webRoot += "/";
                    }
                    String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
                    IniFile m_iniFileIO = new IniFile();
                    String appNewsFile = webRoot
                            + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                            constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
                    String ipUrl = m_iniFileIO.getIniString(appNewsFile, "TBSAPP",
                            "webAddress", constants.DefaultServerIp, (byte) 0);
                    String portUrl = m_iniFileIO.getIniString(appNewsFile, "TBSAPP",
                            "webPort", constants.DefaultServerPort, (byte) 0);
                    String baseUrl = "http://" + ipUrl + ":" + portUrl;
                    Intent intent = new Intent();
                    intent.setClass(getActivity(),
                            OverviewActivity.class);
                    intent.putExtra("tempUrl", baseUrl+videolist.get(position).get("Url"));
                    intent.putExtra("ResName", videolist.get(position).get("name"));
                    getActivity().startActivity(intent);
                }
            });
        }
    }
}
