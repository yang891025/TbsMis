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
import android.widget.Toast;

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
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;
import com.tbs.tbsmis.video.Video;
import com.tbs.tbsmis.video.VideoListAdapter2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tbs on 2016/10/25.
 */
public class showVideoChapter extends Fragment
{

    private ExpandableListView video_list_info;
    private MyBroadcastReciver MyBroadcastReciver;
    private ArrayList<ChapterDownloadTask> ChapterList;
    private VideoListAdapter2 ChapterlistAdaper;
    private ArrayList<ChapterDownloadTask> groups;
    private ArrayList<List<ChapterDownloadTask>> childs;
    private int groupPosition = 0;
    private int childPosition = 0;
    private String type;
    private Video playVideoInfo;

    public static showVideoChapter newInstance(Video chapter, String type) {
        showVideoChapter fragment = new showVideoChapter();
        Bundle bundle = new Bundle();
        bundle.putSerializable("video", chapter);
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        if (args != null) {
            playVideoInfo = (Video) args.getSerializable("video");
            type = args.getString("type", "local");
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
        initChapter();
    }

    @Override
    public boolean getUserVisibleHint() {
        return super.getUserVisibleHint();
    }

    public void initViews() {
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
        String chater = iniFile.getIniString(savePath, playVideoInfo.getCode(),
                "chapter", "0", (byte) 0);
        if (!chater.isEmpty()) {
            groupPosition = Integer.parseInt(chater);
        }
        String section = iniFile.getIniString(savePath, playVideoInfo.getCode(),
                "section", "0", (byte) 0);
        if (!section.isEmpty()) {
            childPosition = Integer.parseInt(section);
        }
        String playtime = iniFile.getIniString(savePath, playVideoInfo.getCode(),
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


    private void initChapter() {
        groups = new ArrayList<ChapterDownloadTask>();
        childs = new ArrayList<List<ChapterDownloadTask>>();
        if (type.equalsIgnoreCase("local")) {
            String webRoot = UIHelper.getSoftPath(this.getContext());
            if (webRoot.endsWith("/") == false) {
                webRoot += "/";
            }
            webRoot += getString(R.string.SD_CARD_TBSAPP_PATH2);
            webRoot = UIHelper.getShareperference(this.getContext(), constants.SAVE_INFORMATION,
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
            String mPath = playVideoInfo.getPath();
            String mVideoPath = StringUtils.getHost(mPath);
            String webRootLocal = UIHelper.getStoragePath(this.getContext());
            webRootLocal = webRootLocal + constants.SD_CARD_TBSFILE_PATH6;
            try {
                JSONArray json = new JSONArray(playVideoInfo.getChapter());
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonObject = json.getJSONObject(i);
                    ChapterDownloadTask Chapter = new ChapterDownloadTask();
                    String name = jsonObject.getString("name");
                    if (name.contains(";"))
                        name = name.substring(0, name.indexOf(";"));
                    String type = jsonObject.getString("type");
                    if (!mPath.startsWith("http:")) {
                        File file = new File(webRootLocal + mPath);
                        if (file.exists()) {
                            if ((type.equalsIgnoreCase("0") || type.equalsIgnoreCase("4")) && !mVideoPath.isEmpty())
                                Chapter.setUrl(webRootLocal + mPath);
                            else {
                                Chapter.setUrl(baseUrl + "/" + name);
                            }
                        } else
                            Chapter.setUrl(baseUrl + "/" + name);
                        Chapter.setFilePath(webRootLocal + "/" + mPath.substring(0, mPath.lastIndexOf("/")));
                    } else {
                        Chapter.setUrl(baseUrl + "/" + name);
                        Chapter.setFilePath("");
                    }
                    if (name.startsWith("/"))
                        name = name.substring(name.indexOf("/") + 1);
                    if (name.contains("/")) {
                        Chapter.setFileName(name.substring(name.lastIndexOf("/") + 1));
                    } else {
                        Chapter.setFileName(name);
                    }
                    Chapter.setCode(playVideoInfo.getCode());
                    Chapter.setId(playVideoInfo.getCode());
                    Chapter.setDownloadState(DownloadState.INITIALIZE);
                    Chapter.setTitle(jsonObject.getString("content"));
                    Chapter.setChapter(jsonObject.getString("chapter"));
                    Chapter.setChildName(jsonObject.getString("childName"));
                    Chapter.setTime(jsonObject.getString("time"));
                    Chapter.setType(type);
                    Chapter.setUpdateTime(jsonObject.getString("updatetime"));
                    Chapter.setSection(jsonObject.getString("section"));
                    Chapter.setSort(jsonObject.getString("sort"));
                    Chapter.setThumbnail(null);
                    ChapterList.add(Chapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

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
            initViews();
        } else {
            getChapter();
        }
    }

    private void getChapter() {
        OkGo.<String>get(DataUtils.URL_COURSE_CHAPTER)//
                .tag(this)
                .params("account", "")
                .params("sourceId", playVideoInfo.getCode())
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
                                    String parentId = json.getString("parentId");
                                    String id = json.getString("id");
                                    String name = json.getString("name");
                                    String resourceId = json.getString("resourceId");
                                    String resourceName = json.getString("resourceName");
                                    String type = json.getString("type");
                                    ChapterDownloadTask Chapter = new ChapterDownloadTask();
                                    String webRoot = UIHelper.getStoragePath(showVideoChapter.this.getContext());
                                    webRoot += constants.SD_CARD_TBSFILE_PATH6 + "/课程/" + playVideoInfo.getTitle();
                                    if (resourceName.contains("/"))
                                        resourceName = resourceName.substring(resourceName.lastIndexOf("/") + 1);
                                    if (resourceName.contains(";"))
                                        resourceName = resourceName.substring(0, resourceName.indexOf(";"));
                                    File file = new File(webRoot + "/" + resourceName);
                                    if (file.exists()) {
                                        if ((type.equalsIgnoreCase("0") || type.equalsIgnoreCase("4")))
                                            Chapter.setUrl(webRoot + "/" + resourceName);
                                        else if (type.equalsIgnoreCase("1")) {
                                            if (resourceId.contains("_"))
                                                resourceId = resourceId.substring(resourceId.lastIndexOf("_") + 1);
                                            Chapter.setUrl(DataUtils.HOST + "project/info_mobileApi.cbs?file=@12&id="
                                                    + resourceId);
                                        } else if (type.equalsIgnoreCase("2")) {
                                            if (resourceId.contains("="))
                                                resourceId = resourceId.substring(resourceId.lastIndexOf("=") + 1);
                                            Chapter.setUrl(DataUtils.HOST + "project/info_mobileApi.cbs?file=@13&id="
                                                    + resourceId);
                                        } else if (type.equalsIgnoreCase("5")) {
                                            Chapter.setUrl(resourceId);
                                        } else {
                                            Chapter.setUrl(DataUtils.HOST + resourceId);
                                        }

                                    } else {
                                        if (type.equalsIgnoreCase("1")) {
                                            if (resourceId.contains("_"))
                                                resourceId = resourceId.substring(resourceId.lastIndexOf("_") + 1);
                                            Chapter.setUrl(DataUtils.HOST + "project/info_mobileApi.cbs?file=@12&id="
                                                    + resourceId);
                                        } else if (type.equalsIgnoreCase("2")) {
                                            if (resourceId.contains("="))
                                                resourceId = resourceId.substring(resourceId.lastIndexOf("=") + 1);
                                            Chapter.setUrl(DataUtils.HOST + "project/info_mobileApi.cbs?file=@13&id="
                                                    + resourceId);
                                        } else if (type.equalsIgnoreCase("5")) {
                                            Chapter.setUrl(resourceId);
                                        } else {
                                            Chapter.setUrl(DataUtils.HOST + resourceId);
                                        }
                                    }
                                    Chapter.setFileName(resourceName);
                                    Chapter.setFilePath(webRoot);
                                    Chapter.setId(id);
                                    Chapter.setCode(playVideoInfo.getCode());
                                    Chapter.setDownloadState(DownloadState.INITIALIZE);
                                    Chapter.setTitle(name);

                                    Chapter.setChildName(parentId);
                                    Chapter.setTime(json.getString("time"));
                                    Chapter.setType(type);
                                    Chapter.setUpdateTime(json.getString("updateTime"));
                                    Chapter.setSort(json.getString("sort"));
                                    Chapter.setThumbnail(null);
                                    if (parentId.equalsIgnoreCase("0")) {
                                        Chapter.setChapter("1");
                                        Chapter.setSection("0");
                                        groups.add(Chapter);
                                    } else {
                                        Chapter.setChapter("0");
                                        Chapter.setSection("1");
                                        ChapterList.add(Chapter);
                                    }
                                }
                                if (ChapterList.size() > 0) {
                                    for (int i = 0; i < groups.size(); i++) {
                                        ArrayList<ChapterDownloadTask> child = new ArrayList<ChapterDownloadTask>();
                                        for (int j = 0; j < ChapterList.size(); j++) {
                                            if (ChapterList.get(j).getChildName().equalsIgnoreCase(groups.get(i)
                                                    .getId())) {
                                                child.add(ChapterList.get(j));
                                            }
                                        }
                                        childs.add(child);
                                    }
                                    initViews();
                                } else {
                                    Toast.makeText(showVideoChapter.this.getContext(), "获取章节失败", Toast.LENGTH_SHORT)
                                            .show();
                                }

                            } else {
                                Toast.makeText(showVideoChapter.this.getContext(), "获取章节失败", Toast.LENGTH_SHORT).show();
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
                if (groupPosition < (groups.size() - 1)) {
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
                    } else {
                        groupPosition = groupPosition + 1;
                        position = 0;
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
                } else {
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
