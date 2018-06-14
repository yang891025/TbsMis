package me.wcy.music.fragment;

/**
 * Created by TBS on 2018/3/7.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import me.wcy.music.R;
import me.wcy.music.adapter.OnMoreClickListener;
import me.wcy.music.adapter.PlaylistAdapter;
import me.wcy.music.model.Music;
import me.wcy.music.service.AudioPlayer;
import me.wcy.music.service.OnPlayerEventListener;

/**
 * 播放列表
 */
public class PlaylistFragment extends AttachDialogFragment implements AdapterView.OnItemClickListener,
        OnMoreClickListener, OnPlayerEventListener
{
    //@Bind(R.id.lv_playlist)
    private ListView lvPlaylist;

    private PlaylistAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//设置样式
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomDatePickerDialog);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //设置无标题
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置从底部弹出
        WindowManager.LayoutParams params = getDialog().getWindow()
                .getAttributes();
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setAttributes(params);


        View view = inflater.inflate(R.layout.fragment_play_list, container);
        //setContentView(R.layout.activity_playlist);
        lvPlaylist = (ListView) view.findViewById(R.id.lv_playlist);
        adapter = new PlaylistAdapter(AudioPlayer.get().getMusicList());
        adapter.setIsPlaylist(true);
        adapter.setOnMoreClickListener(this);
        lvPlaylist.setAdapter(adapter);
        lvPlaylist.setOnItemClickListener(this);
        AudioPlayer.get().addOnPlayEventListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //设置fragment高度 、宽度
        int dialogHeight = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.6);
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, dialogHeight);
        getDialog().setCanceledOnTouchOutside(true);

    }

//    @Override
//    protected void onServiceBound() {
//
//    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AudioPlayer.get().play(position);
    }

    @Override
    public void onMoreClick(int position) {
        String[] items = new String[]{"移除"};
        Music music = AudioPlayer.get().getMusicList().get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle(music.getTitle());
        dialog.setItems(items, (dialog1, which) -> {
            AudioPlayer.get().delete(position);
            adapter.notifyDataSetChanged();
        });
        dialog.show();
    }

    @Override
    public void onChange(Music music) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPlayerStart() {
    }

    @Override
    public void onPlayerPause() {
    }

    @Override
    public void onPublish(int total, int progress) {
    }

    @Override
    public void onBufferingUpdate(int percent) {
    }

    @Override
    public void onDestroy() {
        AudioPlayer.get().removeOnPlayEventListener(this);
        super.onDestroy();
    }

}
