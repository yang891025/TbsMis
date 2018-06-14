package me.wcy.music.executor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import me.wcy.music.R;
import me.wcy.music.model.Music;
import me.wcy.music.service.AudioPlayer;
import me.wcy.music.service.OnPlayerEventListener;
import me.wcy.music.utils.CoverLoader;

/**
 * Created by hzwangchenyan on 2018/1/26.
 */
@SuppressLint("ValidFragment")
public class ControlPanel extends Fragment implements View.OnClickListener, OnPlayerEventListener
{
    //@Bind(R.id.iv_play_bar_cover)
    private ImageView ivPlayBarCover;
    //@Bind(R.id.tv_play_bar_title)
    private TextView tvPlayBarTitle;
    //@Bind(R.id.tv_play_bar_artist)
    private TextView tvPlayBarArtist;
    //@Bind(R.id.iv_play_bar_play)
    private ImageView ivPlayBarPlay;
    //@Bind(R.id.iv_play_bar_next)
    private ImageView ivPlayBarNext;
    //@Bind(R.id.v_play_bar_playlist)
    private ImageView vPlayBarPlaylist;
    //@Bind(R.id.pb_play_bar)
    private ProgressBar mProgressBar;
    private Activity mContext;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
    }

    @SuppressLint("ValidFragment")
    public ControlPanel(View view) {
        vPlayBarPlaylist = (ImageView) view.findViewById(R.id.iv_play_bar_playlist);
        ivPlayBarNext = (ImageView) view.findViewById(R.id.iv_play_bar_next);
        ivPlayBarCover = (ImageView) view.findViewById(R.id.iv_play_bar_cover);
        tvPlayBarTitle = (TextView) view.findViewById(R.id.tv_play_bar_title);
        tvPlayBarArtist = (TextView) view.findViewById(R.id.tv_play_bar_artist);
        ivPlayBarPlay = (ImageView) view.findViewById(R.id.iv_play_bar_play);
        mProgressBar = (ProgressBar) view.findViewById(R.id.pb_play_bar);
        ivPlayBarPlay.setOnClickListener(this);
        ivPlayBarNext.setOnClickListener(this);
        //vPlayBarPlaylist.setOnClickListener(this);
        onChange(AudioPlayer.get().getPlayMusic());
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_play_bar_play) {
            AudioPlayer.get().playPause();

        } else if (i == R.id.iv_play_bar_next) {
            AudioPlayer.get().next();
        }
//        else if (i == R.id.v_play_bar_playlist) {
//
//            Context context = vPlayBarPlaylist.getContext();
//            Intent intent = new Intent(context, PlaylistActivity.class);
//            context.startActivity(intent);

//            try {
//                PlaylistFragment playQueueFragment = new PlaylistFragment();
//                playQueueFragment.show(getFragmentManager(), "PlaylistFragment");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

//        }
    }

    @Override
    public void onChange(Music music) {
        if (music == null) {
            return;
        }
        if (music.getCoverPath() != null && !music.getCoverPath().contains("content://")) {
            Glide.with(ivPlayBarCover.getContext())
                    .load(music.getCoverPath())
                    .placeholder(R.drawable.default_cover)
                    .error(R.drawable.default_cover)
                    .into(ivPlayBarCover);
        } else {
            Bitmap cover = CoverLoader.get().loadThumb(music);
            ivPlayBarCover.setImageBitmap(cover);
        }

        tvPlayBarTitle.setText(music.getTitle());
        tvPlayBarArtist.setText(music.getArtist());
        ivPlayBarPlay.setSelected(AudioPlayer.get().isPlaying() || AudioPlayer.get().isPreparing());
        mProgressBar.setMax((int) music.getDuration());
        mProgressBar.setProgress((int) AudioPlayer.get().getAudioPosition());
    }

    @Override
    public void onPlayerStart() {
        ivPlayBarPlay.setSelected(true);
    }

    @Override
    public void onPlayerPause() {
        ivPlayBarPlay.setSelected(false);
    }

    @Override
    public void onPublish(int total, int progress) {
        mProgressBar.setMax(total);
        mProgressBar.setProgress(progress);
    }

    @Override
    public void onBufferingUpdate(int percent) {
    }
}
