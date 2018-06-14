package com.tbs.tbsmis.video.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tbs.ini.IniFile;
import com.tbs.tbsmis.Live.utils.DataUtils;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.download.ChapterDownloadTask;
import com.tbs.tbsmis.download.DownloadState;
import com.tbs.tbsmis.util.FileIO;
import com.tbs.tbsmis.util.UIHelper;
import com.tbs.tbsmis.video.VideoListAdapter2;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by TBS on 2018/1/26.
 */

public class showChapter extends Fragment
{
    private ExpandableListView video_list_info;
    private MyBroadcastReciver MyBroadcastReciver;
    private ArrayList<ChapterDownloadTask> ChapterList;
    private VideoListAdapter2 ChapterlistAdaper;
    private ArrayList<ChapterDownloadTask> groups;
    private ArrayList<List<ChapterDownloadTask>> childs;
    private String sourceId;
    private static int groupPosition = 0;
    private static int childPosition = 0;
    private String title;

    public static showChapter newInstance(String id, String title) {
        showChapter fragment = new showChapter();
        Bundle bundle = new Bundle();
        bundle.putString("sourceId", id);
        bundle.putString("title", title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        if (args != null) {
            sourceId = args.getString("sourceId");
            title = args.getString("title");
        }
        //注册监听
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("mChapter" + getString(R.string.app_name));
        intentFilter.addAction("download_refresh" + getString(R.string.app_name));
        MyBroadcastReciver = new MyBroadcastReciver();
        getActivity().registerReceiver(MyBroadcastReciver, intentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_pot, container, false);
        video_list_info = (ExpandableListView) view.findViewById(R.id.video_list_info);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ChapterList = new ArrayList<ChapterDownloadTask>();
        ChapterDownloadTask Chapter = new ChapterDownloadTask();
        Chapter.setChapter("1");
        Chapter.setSection("0");
        Chapter.setTitle(title);
        ChapterList.add(Chapter);
        getChapter(sourceId);
    }

    public void initData() {
        if (ChapterList.size() > 1) {
            video_list_info.setIndicatorBounds(20, 60);
            ChapterlistAdaper = new VideoListAdapter2(getActivity(), groups, childs);
            ChapterlistAdaper.setSelectItem(groupPosition, childPosition);
            video_list_info.setAdapter(ChapterlistAdaper);
            video_list_info.expandGroup(groupPosition);
            video_list_info.setOnChildClickListener(new ExpandableListView.OnChildClickListener()
            {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition,
                                            long id) {
                    if (!childs.get(groupPosition).get(childPosition).getChapter().equalsIgnoreCase("1")) {
                        Intent intent = new Intent();
                        intent.setAction("player_chapter" + getActivity().getString(R.string.app_name));
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("ChatTask", childs.get(groupPosition).get(childPosition));
                        intent.putExtras(bundle);
                        intent.putExtra("childPosition", childPosition);
                        intent.putExtra("groupPosition", groupPosition);
                        intent.putExtra("playtime", "0");
                        getActivity().sendBroadcast(intent);
                        video_list_info.setSelection(childPosition);
                        ChapterlistAdaper.setSelectItem(groupPosition, childPosition);
                        ChapterlistAdaper.notifyDataSetChanged();
                    }
                    return false;
                }
            });

            String savePath = UIHelper.getStoragePath(this.getContext()) + "/Log/playerLog.ini";
            File file = new File(savePath);
            if (!file.exists()) {
                FileIO.CreateNewFile(savePath);
            }
            IniFile iniFile = new IniFile();
            String chater = iniFile.getIniString(savePath, sourceId,
                    "chapter", "", (byte) 0);
            if (!chater.isEmpty()) {
                groupPosition = Integer.parseInt(chater);
            }
            String section = iniFile.getIniString(savePath, sourceId,
                    "section", "", (byte) 0);
            if (!section.isEmpty()) {
                childPosition = Integer.parseInt(section);
            }
            String playtime = iniFile.getIniString(savePath, sourceId,
                    "playtime", "0", (byte) 0);
            if (!childs.get(groupPosition).get(childPosition).getChapter().equalsIgnoreCase("1")) {
                Intent intent = new Intent();
                intent.setAction("player_chapter" + getActivity().getString(R.string.app_name));
                Bundle bundle = new Bundle();
                bundle.putSerializable("ChatTask", childs.get(groupPosition).get(childPosition));
                intent.putExtras(bundle);
                intent.putExtra("childPosition", childPosition);
                intent.putExtra("groupPosition", groupPosition);
                intent.putExtra("playtime", playtime);
                getActivity().sendBroadcast(intent);
                video_list_info.setSelection(childPosition);
                ChapterlistAdaper.setSelectItem(groupPosition, childPosition);
                ChapterlistAdaper.notifyDataSetChanged();
            }
        }
    }

    private void getChapter(final String sourceId) {
        OkGo.<String>get(DataUtils.URL_FILM_CHAPTER)//
                .tag(this)
                .params("account", "")
                .params("sourceId", sourceId)
                .params("LoginId", "")
                .execute(new StringCallback()
                {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());// 转换为JSONObject
                            boolean code = jsonObject.getBoolean("result");
                            if (code) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject json = jsonArray.getJSONObject(i);
                                    String id = json.getString("id");
                                    String name = json.getString("name");
                                    String video = json.getString("video");
                                    if (video.contains(";"))
                                        video = video.substring(0, video.indexOf(";"));
                                    String pic = json.getString("pic");
                                    ChapterDownloadTask Chapter = new ChapterDownloadTask();
                                    String webRoot = UIHelper.getStoragePath(showChapter.this.getContext());
                                    webRoot += constants.SD_CARD_TBSFILE_PATH6 + "/影视/" + title ;
                                    File file = new File(webRoot + "/" + video);
//                                    System.out.println("webRoot = " + webRoot + "/" + video);
//                                    System.out.println("file.exists() = " + file.exists());
                                    if (file.exists()) {
                                        Chapter.setUrl(webRoot + "/" + video);
                                        Chapter.setFilePath(webRoot);
                                    } else {
                                        Chapter.setUrl(DataUtils.HOST + "filePath/static/tbsermVideo/film/" + video);
                                        Chapter.setFilePath(webRoot);
                                    }
                                    Chapter.setFileName(video);
                                    Chapter.setId(id);
                                    Chapter.setCode(sourceId);
                                    Chapter.setDownloadState(DownloadState.INITIALIZE);
                                    Chapter.setTitle(name);
                                    Chapter.setChapter("0");
                                    Chapter.setSection("1");
                                    Chapter.setType("0");
                                    Chapter.setTime(json.getString("time"));
                                    Chapter.setUpdateTime(json.getString("updateTime"));
                                    Chapter.setSort(json.getString("sort"));
                                    Chapter.setThumbnail(null);
                                    ChapterList.add(Chapter);
                                }
                                groups = new ArrayList<ChapterDownloadTask>();
                                childs = new ArrayList<List<ChapterDownloadTask>>();
                                if (ChapterList.size() > 0) {
                                    ArrayList<ChapterDownloadTask> child = new ArrayList<ChapterDownloadTask>();
                                    for (int i = 0; i < ChapterList.size(); i++) {
                                        if (ChapterList.get(i).getSection().equalsIgnoreCase("0")) {
                                            groups.add(ChapterList.get(i));
                                            if (child.size() > 0) {
                                                childs.add(child);
                                                child = new ArrayList<ChapterDownloadTask>();
                                            }
                                        } else {
                                            child.add(ChapterList.get(i));
                                        }
                                    }
                                    childs.add(child);
                                }
                                initData();
                            } else {
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }

    private class MyBroadcastReciver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equalsIgnoreCase("mChapter" + getString(R.string.app_name))) {

                int position = intent.getIntExtra("position", 0);
                int groupPosition = intent.getIntExtra("groupPosition", 0);
                if (groupPosition < groups.size()) {
                    //chapterNum = i + 1;
                    if (position < (childs.get(groupPosition).size() - 1)) {
                        position = position + 1;
                        Intent intent2 = new Intent();
                        intent2.setAction("player_chapter" + getActivity().getString(R.string.app_name));
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("ChatTask", childs.get(groupPosition).get(position));
                        intent2.putExtras(bundle);
                        intent2.putExtra("childPosition", position);
                        intent2.putExtra("groupPosition", groupPosition);
                        intent2.putExtra("playtime", "0");
                        getActivity().sendBroadcast(intent2);
                        video_list_info.setSelection(position);
                        ChapterlistAdaper.setSelectItem(groupPosition, position);
                        ChapterlistAdaper.notifyDataSetChanged();
                    }
                }
//                ChapterlistAdaper.setSelectItem(groupPosition, position);
//                ChapterlistAdaper.notifyDataSetChanged();
            } else if (action.equalsIgnoreCase("download_refresh" + getString(R.string.app_name))) {
                ChapterlistAdaper.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(this.MyBroadcastReciver);
    }
}
