package me.wcy.music.service;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import me.wcy.music.R;
import me.wcy.music.application.AppCache;
import me.wcy.music.model.Music;
import me.wcy.music.utils.CoverLoader;

/**
 * Created by hzwangchenyan on 2017/8/8.
 */
public class MediaSessionManager
{
    private static final String TAG = "MediaSessionManager";
    private static final long MEDIA_SESSION_ACTIONS = PlaybackStateCompat.ACTION_PLAY
            | PlaybackStateCompat.ACTION_PAUSE
            | PlaybackStateCompat.ACTION_PLAY_PAUSE
            | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
            | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            | PlaybackStateCompat.ACTION_STOP
            | PlaybackStateCompat.ACTION_SEEK_TO;

    private PlayService playService;
    private MediaSessionCompat mediaSession;

    public static MediaSessionManager get() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder
    {
        private static MediaSessionManager instance = new MediaSessionManager();
    }

    private MediaSessionManager() {
    }

    public void init(PlayService playService) {
        this.playService = playService;
        setupMediaSession();
    }

    private void setupMediaSession() {
        mediaSession = new MediaSessionCompat(playService, TAG);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS | MediaSessionCompat
                .FLAG_HANDLES_MEDIA_BUTTONS);
        mediaSession.setCallback(callback);
        mediaSession.setActive(true);
    }

    public void updatePlaybackState() {
        int state = (AudioPlayer.get().isPlaying() || AudioPlayer.get().isPreparing()) ? PlaybackStateCompat
                .STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;
        mediaSession.setPlaybackState(
                new PlaybackStateCompat.Builder()
                        .setActions(MEDIA_SESSION_ACTIONS)
                        .setState(state, AudioPlayer.get().getAudioPosition(), 1)
                        .build());
    }

    public void updateMetaData(Music music) {
        if (music == null) {
            mediaSession.setMetadata(null);
            return;
        }

        MediaMetadataCompat.Builder metaData = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, music.getTitle())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, music.getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, music.getAlbum())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, music.getArtist())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, music.getDuration());
        if (music.getCoverPath() != null && !music.getCoverPath().contains("content://")) {
            Glide.with(playService.getApplicationContext())
                    .load(music.getCoverPath())
                    .asBitmap()
                    .placeholder(R.drawable.default_cover)
                    .error(R.drawable.default_cover)
                    .into(new SimpleTarget<Bitmap>()
                    {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            metaData.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, resource);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                metaData.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, AppCache.get()
                                        .getLocalMusicList().size());
                            }

                            mediaSession.setMetadata(metaData.build());
                        }
                    });
        } else {
            metaData.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, CoverLoader.get().loadThumb(music));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                metaData.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, AppCache.get().getLocalMusicList().size
                        ());
            }

            mediaSession.setMetadata(metaData.build());
        }

    }

    private MediaSessionCompat.Callback callback = new MediaSessionCompat.Callback()
    {
        @Override
        public void onPlay() {
            AudioPlayer.get().playPause();
        }

        @Override
        public void onPause() {
            AudioPlayer.get().playPause();
        }

        @Override
        public void onSkipToNext() {
            AudioPlayer.get().next();
        }

        @Override
        public void onSkipToPrevious() {
            AudioPlayer.get().prev();
        }

        @Override
        public void onStop() {
            AudioPlayer.get().stopPlayer();
        }

        @Override
        public void onSeekTo(long pos) {
            AudioPlayer.get().seekTo((int) pos);
        }
    };
}
