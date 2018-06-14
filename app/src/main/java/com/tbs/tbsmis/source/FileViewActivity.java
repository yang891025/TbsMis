package com.tbs.tbsmis.source;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.activity.OverviewActivity;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.file.FileInfo;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;
import com.tbs.tbsmis.util.getMediaType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileViewActivity extends Activity implements View.OnClickListener
{

    private TextView mTitleTextView;
    private ImageView mBackBtn;
    private ListView mListView;
    private FileViewAdapter exlist_adapter;
    private FileSourceViewAdapter soulist_adapter;
    private List<FileInfo> allDir;
    private TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        MyActivity.getInstance().addActivity(this);
        this.setContentView(R.layout.biz_plugin_weather_select_city);
        this.initView();
        if (this.getIntent().getExtras() != null) {
            Intent intent = this.getIntent();
            String fileName = intent.getStringExtra("fileName");
            final String filePath = intent.getStringExtra("filePath");
            this.mTitleTextView.setText(fileName);
            final String iniPath = filePath + "/" + fileName + ".ini";
            String webRoot = UIHelper.getStoragePath(this);
            webRoot = webRoot + constants.SD_CARD_TBSFILE_PATH6;
            final String iniLog = webRoot + "/" + "log.ini";
            File file = new File(iniPath);
            final IniFile iniFile = new IniFile();
            if (file.exists()) {
                int count = Integer.parseInt(iniFile.getIniString(iniPath, "file", "count", "0", (byte) 0));
                final List<SourceInfo> allFiles = new ArrayList<SourceInfo>();
                for (int i = 1; i <= count; i++) {
                    String Type = iniFile.getIniString(iniPath, "file" + i, "type", "", (byte) 0);
                    String fileMidName = iniFile.getIniString(iniPath, "file" + i, "name", "", (byte) 0);
                    String path = filePath + "/" + fileMidName;
                    if (!Type.equalsIgnoreCase("file")) {
                        if (!fileMidName.endsWith(".html")) {
                            if (fileMidName.contains("/")) {
                                fileMidName = fileMidName.substring(fileMidName.lastIndexOf("/") + 1);
                                path = filePath + "/" + fileMidName;
                            }
                        }
                    }
//                    String path = filePath + "/" + iniFile.getIniString(iniPath, "file" + i, "name", "", (byte) 0);
                    File filepath = new File(path);
                    if (filepath.exists() && filepath.isFile()) {
                        SourceInfo source = new SourceInfo();
                        source.setCategory(iniFile.getIniString(iniPath, "file" + i, "category", "", (byte) 0));
                        source.setDescription(iniFile.getIniString(iniPath, "file" + i, "description", "", (byte) 0));
                        if (!iniFile.getIniString(iniPath, "file" + i, "download", "0",
                                (byte) 0).isEmpty())
                            source.setDownload(Long.parseLong(iniFile.getIniString(iniPath, "file" + i, "download", "0",
                                    (byte) 0)));
                        source.setRange(iniFile.getIniString(iniPath, "file" + i, "range", "", (byte) 0));
                        source.setTitle(iniFile.getIniString(iniPath, "file" + i, "title", "", (byte) 0));
                        source.setName(fileMidName);
                        source.setCode(iniFile.getIniString(iniPath, "file" + i, "code", "", (byte) 0));
                        source.setNetworkPath(iniFile.getIniString(iniPath, "file" + i, "shareUrl", "", (byte) 0));
                        source.setPath(path);
                        source.setPic(iniFile.getIniString(iniPath, "file" + i, "pic", "", (byte) 0));
                        if (!iniFile.getIniString(iniPath, "file" + i, "play", "0",
                                (byte) 0).isEmpty())
                            source.setPlay(Long.parseLong(iniFile.getIniString(iniPath, "file" + i, "play", "0", (byte)
                                    0)));
                        if (!iniFile.getIniString(iniPath, "file" + i, "size", "0",
                                (byte) 0).isEmpty())
                            source.setSize(Long.parseLong(iniFile.getIniString(iniPath, "file" + i, "size", "0", (byte)
                                    0)));
                        source.setLongtime(iniFile.getIniString(iniPath, "file" + i, "longtime", "", (byte) 0));
                        source.setStop(iniFile.getIniString(iniPath, "file" + i, "stop", "", (byte) 0));
                        source.setTime(iniFile.getIniString(iniPath, "file" + i, "time", "", (byte) 0));
                        source.setType(Type);
                        source.setChapter(iniFile.getIniString(iniPath, "file" + i, "chapter", "", (byte) 0));
                        source.setRelateExam(iniFile.getIniString(iniPath, "file" + i, "relateExam", "", (byte) 0));
                        source.setRelateExamUrl(iniFile.getIniString(iniPath, "file" + i, "relateExamUrl", "", (byte)
                                0));
                        source.setRelateKnowled(iniFile.getIniString(iniPath, "file" + i, "relateKnowled", "", (byte)
                                0));
                        source.setRelateKnowledUrl(iniFile.getIniString(iniPath, "file" + i, "relateKnowledUrl", "",
                                (byte) 0));
                        source.setContent(iniFile.getIniString(iniPath, "file" + i, "content", "",
                                (byte) 0));
                        source.setLocation(i);
                        source.setTxtPath(iniFile.getIniString(iniPath, "file" + i, "path", "",
                                (byte) 0));
                        allFiles.add(source);
                    }
                }
                if (allFiles.size() > 0) {
                    this.soulist_adapter = new FileSourceViewAdapter(this, allFiles, iniPath);
                    this.mListView.setAdapter(this.soulist_adapter);
                    this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            // TODO Auto-generated method stub
                            getMediaType mide = new getMediaType();
                            mide.initReflect();
                            int type = mide.getMediaFileType(filePath + "/" + allFiles.get(position).getName());
                            boolean isAudio = mide.isAudioFile(type);
                            boolean isVideo = mide.isVideoFile(type);
//                             if (isAudio) {
//                                Intent intent = new Intent();
//                                intent.setClass(FileViewActivity.this, AcsAudioAcitvity.class);
//                                Bundle bundle = new Bundle();
//                                Music music = new Music();
//                                music.setUri(allFiles.get(position).getPath());
//                                bundle.putSerializable("music", music);
//                                intent.putExtras(bundle);
//                                startActivity(intent);
//                            } else
//                                if (isVideo || allFiles.get(position).getName()
//                                    .endsWith("flv") || allFiles.get(position).getName()
//                                    .endsWith("flt")) {
//                                Intent intent = new Intent();
//                                intent.setClass(FileViewActivity.this, VideoPlayer.class);
//                                Bundle bundle = new Bundle();
//                                bundle.putString("sourceId", allFiles.get(position).getCode());
//                                bundle.putString("type", "Online");
//                                Video video = new Video();
//                                video.setPath(filePath + "/" + allFiles.get(position).getName());
//                                video.setTitle(allFiles.get(position).getName());
//                                video.setChapter(allFiles.get(position).getChapter());
//                                video.setRelateKnowledUrl(allFiles.get
//                                        (position).getRelateKnowledUrl());
//                                video.setRelateKnowled(allFiles.get(position)
//                                        .getRelateKnowled());
//                                video.setRelateExamUrl(allFiles.get(position)
//                                        .getRelateExamUrl());
//                                video.setRelateExam(allFiles.get(position)
//                                        .getRelateExam());
//                                video.setCode(allFiles.get(position).getCode());
//                                video.setTxtPath(allFiles.get(position).getTxtPath());
//                                video.setContent(allFiles.get(position).getContent());
//                                video.setDescription(allFiles.get(position).getDescription());
//                                video.setAlbum(allFiles.get(position).getPic());
//                                bundle.putSerializable("video", video);
//                                intent.putExtras(bundle);
//                                FileViewActivity.this.startActivity(intent);
//                            } else
                                if (allFiles.get(position).getName().endsWith("html")) {
                                Intent intent = new Intent();
                                intent.setClass(FileViewActivity.this, OverviewActivity.class);
                                intent.putExtra("tempUrl", "file://" + filePath + "/" + allFiles.get
                                        (position).getName());
                                intent.putExtra("ResName", allFiles.get
                                        (position).getTitle());
                                FileViewActivity.this.startActivity(intent);
                            } else {
                                UIHelper.openFile(FileViewActivity.this, filePath + "/" + allFiles.get(position)
                                        .getName());
                            }
                            int count = Integer.parseInt(iniFile.getIniString(iniLog, "Log", "count", "0",
                                    (byte) 0));
                            soulist_adapter.notifyDataSetChanged();
                            iniFile.writeIniString(iniLog, "log" + (count + 1), "starttime", StringUtils.getDate
                                    ());
                            iniFile.writeIniString(iniLog, "log" + (count + 1), "name", allFiles.get(position)
                                    .getName());
                            iniFile.writeIniString(iniLog, "log" + (count + 1), "path", allFiles.get(position)
                                    .getPath());
                            iniFile.writeIniString(iniLog, "log" + (count + 1), "endtime", StringUtils.getDate());
                            iniFile.writeIniString(iniLog, "Log", "count", (count + 1) + "");
                        }
                    });
                } else {
                    this.allDir = FileUtils.GetallPath(filePath);
                    for (int i = 0; i < this.allDir.size(); i++) {
                        if (this.allDir.get(i).fileName.endsWith(".ini")) {
                            this.allDir.remove(i);
                        }
                    }
                    this.exlist_adapter = new FileViewAdapter(this, this.allDir);
                    this.mListView.setAdapter(this.exlist_adapter);
                    this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            // TODO Auto-generated method stub
                            getMediaType mide = new getMediaType();
                            mide.initReflect();
                            int type = mide.getMediaFileType(FileViewActivity.this.allDir.get(position)
                                    .filePath);
                            boolean isAudio = mide.isAudioFile(type);
//                            if (isAudio) {
//                                Intent intent = new Intent();
//                                intent.setClass(FileViewActivity.this, AcsAudioAcitvity.class);
//                                Bundle bundle = new Bundle();
//                                Music music = new Music();
//                                music.setUri(allDir.get(position)
//                                        .filePath);
//                                bundle.putSerializable("music", music);
//                                intent.putExtras(bundle);
//                                startActivity(intent);
//                            }  else
                                if (allDir.get(position).fileName.endsWith("html")) {
                                Intent intent = new Intent();
                                intent.setClass(FileViewActivity.this, OverviewActivity.class);
                                intent.putExtra("tempUrl", "file://" + FileViewActivity.this.allDir.get(position)
                                        .filePath);
                                intent.putExtra("ResName", allDir.get(position).fileName);
                                FileViewActivity.this.startActivity(intent);
                            } else {
                                if (!UIHelper.openFile(FileViewActivity.this, FileViewActivity.this.allDir.get
                                        (position).filePath)) {
                                    Toast.makeText(FileViewActivity.this, "文件不存在", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                            int count = Integer.parseInt(iniFile.getIniString(iniLog, "Log", "count", "0",
                                    (byte) 0));

                            iniFile.writeIniString(iniLog, "log" + (count + 1), "starttime", StringUtils.getDate
                                    ());
                            iniFile.writeIniString(iniLog, "log" + (count + 1), "path", FileViewActivity.this
                                    .allDir.get(position)
                                    .filePath);
                            iniFile.writeIniString(iniLog, "log" + (count + 1), "name", FileViewActivity.this
                                    .allDir.get(position)
                                    .fileName);
                            iniFile.writeIniString(iniLog, "log" + (count + 1), "endtime", StringUtils
                                    .getDate());
                            iniFile.writeIniString(iniLog, "Log", "count", (count + 1) + "");
                        }
                    });
                }
            } else {
                this.allDir = FileUtils.GetallPath(filePath);
                for (int i = 0; i < this.allDir.size(); i++) {
                    if (this.allDir.get(i).fileName.endsWith(".ini")) {
                        this.allDir.remove(i);
                    }
                }
                this.exlist_adapter = new FileViewAdapter(this, this.allDir);
                this.mListView.setAdapter(this.exlist_adapter);
                this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        // TODO Auto-generated method stub
                        getMediaType mide = new getMediaType();
                        mide.initReflect();
                        int type = mide.getMediaFileType(FileViewActivity.this.allDir.get(position)
                                .filePath);
                        boolean isAudio = mide.isAudioFile(type);
//                       if (isAudio) {
//                            Intent intent = new Intent();
//                            intent.setClass(FileViewActivity.this, AcsAudioAcitvity.class);
//                            Bundle bundle = new Bundle();
//                            Music music = new Music();
//                            music.setUri(allDir.get(position)
//                                    .filePath);
//                            bundle.putSerializable("music", music);
//                            intent.putExtras(bundle);
//                            startActivity(intent);
//                        }  else
                            if (allDir.get(position).fileName.endsWith("html")) {
                            Intent intent = new Intent();
                            intent.setClass(FileViewActivity.this, OverviewActivity.class);
                            intent.putExtra("tempUrl", "file://" + FileViewActivity.this.allDir.get(position).filePath);
                            intent.putExtra("ResName", allDir.get(position).fileName);
                            FileViewActivity.this.startActivity(intent);
                        } else {
                            if (!UIHelper.openFile(FileViewActivity.this, FileViewActivity.this.allDir.get(position)
                                    .filePath)) {
                                Toast.makeText(FileViewActivity.this, "文件不存在", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        int count = Integer.parseInt(iniFile.getIniString(iniLog, "Log", "count", "0",
                                (byte) 0));
                        iniFile.writeIniString(iniLog, "log" + (count + 1), "starttime", StringUtils.getDate
                                ());
                        iniFile.writeIniString(iniLog, "log" + (count + 1), "path", FileViewActivity.this.allDir.get
                                (position)
                                .filePath);
                        iniFile.writeIniString(iniLog, "log" + (count + 1), "name", FileViewActivity.this.allDir.get
                                (position)
                                .fileName);
                        iniFile.writeIniString(iniLog, "log" + (count + 1), "endtime", StringUtils.getDate());
                        iniFile.writeIniString(iniLog, "Log", "count", (count + 1) + "");
                    }
                });
            }
        }

    }

    private void initView() {
        this.mTitleTextView = (TextView) this.findViewById(R.id.title_name);
        this.empty = (TextView) this.findViewById(R.id.empty);
        this.empty.setText("暂无文件");
        this.mBackBtn = (ImageView) this.findViewById(R.id.title_back);
        this.mBackBtn.setOnClickListener(this);
        this.mListView = (ListView) this.findViewById(R.id.citys_list);
        // mCityContainer = findViewById(R.id.city_content_container);
        this.mListView.setEmptyView(this.findViewById(R.id.citys_list_empty));
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        // 结束Activity&从堆栈中移除
        MyActivity.getInstance().finishActivity(this);
    }
}