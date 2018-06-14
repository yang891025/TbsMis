package me.wcy.music.application;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import me.wcy.music.R;
import me.wcy.music.activity.MusicActivity;
import me.wcy.music.constants.Extras;
import me.wcy.music.model.Music;
import me.wcy.music.receiver.StatusBarReceiver;
import me.wcy.music.service.PlayService;
import me.wcy.music.utils.CoverLoader;
import me.wcy.music.utils.FileUtils;

/**
 * Created by wcy on 2017/4/18.
 */
public class Notifier
{
    private static final int NOTIFICATION_ID = 0x111;
    private PlayService playService;
    private NotificationManager notificationManager;

    public static Notifier get() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder
    {
        private static Notifier instance = new Notifier();
    }

    private Notifier() {
    }

    public void init(PlayService playService) {
        this.playService = playService;
        notificationManager = (NotificationManager) playService.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void showPlay(Music music) {
        if (music == null) {
            return;
        }
        playService.startForeground(NOTIFICATION_ID, buildNotification(playService, music, true));
    }

    public void showPause(Music music) {
        if (music == null) {
            return;
        }
        playService.stopForeground(false);
        notificationManager.notify(NOTIFICATION_ID, buildNotification(playService, music, false));
    }

    public void cancelAll() {
        notificationManager.cancelAll();
    }

    private Notification buildNotification(Context context, Music music, boolean isPlaying) {
        Intent intent = new Intent(context, MusicActivity.class);
        intent.putExtra(Extras.EXTRA_NOTIFICATION, true);
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews remoteViews = getRemoteViews(context, music, isPlaying);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notification)
                .setCustomContentView(remoteViews);
        Notification notification = builder.build();

        if (music.getCoverPath() != null&&!music.getCoverPath().contains("content://")) {
            NotificationTarget notificationTarget = new NotificationTarget(context, remoteViews, R.id.iv_icon,
                    notification, NOTIFICATION_ID);
            Glide.with(context).load(music.getCoverPath()).asBitmap().placeholder(R.drawable.default_cover).error(R
                    .drawable.default_cover)
                    .listener(new RequestListener<String, Bitmap>()
                    {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean
                                isFirstResource) {
                            //Log.e("TAG", e.toString());
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean
                                isFromMemoryCache, boolean isFirstResource) {
                            //Log.e("TAG", "1111111111111111111");
                            return false;
                        }
                    }).dontAnimate().into(notificationTarget);
        }

        return notification;
    }

    private RemoteViews getRemoteViews(Context context, Music music, boolean isPlaying) {
        String title = music.getTitle();
        String subtitle = FileUtils.getArtistAndAlbum(music.getArtist(), music.getAlbum());

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification);
        if (music.getCoverPath() != null && !music.getCoverPath().contains("content://")) {

        }else{
            Bitmap cover = CoverLoader.get().loadThumb(music);
            if (cover != null) {
                remoteViews.setImageViewBitmap(R.id.iv_icon, cover);
            } else {
                remoteViews.setImageViewResource(R.id.iv_icon, R.drawable.default_cover);
            }
        }

        remoteViews.setTextViewText(R.id.tv_title, title);
        remoteViews.setTextViewText(R.id.tv_subtitle, subtitle);

        boolean isLightNotificationTheme = isLightNotificationTheme(playService);

        Intent playIntent = new Intent(StatusBarReceiver.ACTION_STATUS_BAR);
        playIntent.putExtra(StatusBarReceiver.EXTRA, StatusBarReceiver.EXTRA_PLAY_PAUSE);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(context, 0, playIntent, PendingIntent
                .FLAG_UPDATE_CURRENT);
        remoteViews.setImageViewResource(R.id.iv_play_pause, getPlayIconRes(isLightNotificationTheme, isPlaying));
        remoteViews.setOnClickPendingIntent(R.id.iv_play_pause, playPendingIntent);

        Intent nextIntent = new Intent(StatusBarReceiver.ACTION_STATUS_BAR);
        nextIntent.putExtra(StatusBarReceiver.EXTRA, StatusBarReceiver.EXTRA_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 1, nextIntent, PendingIntent
                .FLAG_UPDATE_CURRENT);
        remoteViews.setImageViewResource(R.id.iv_next, getNextIconRes(isLightNotificationTheme));
        remoteViews.setOnClickPendingIntent(R.id.iv_next, nextPendingIntent);

        Intent cancleIntent = new Intent(StatusBarReceiver.ACTION_STATUS_BAR);
        cancleIntent.putExtra(StatusBarReceiver.EXTRA, StatusBarReceiver.EXTRA_CANCLE);
        PendingIntent canclePendingIntent = PendingIntent.getBroadcast(context, 2, cancleIntent, PendingIntent
                .FLAG_UPDATE_CURRENT);
        remoteViews.setImageViewResource(R.id.iv_cancle, getCancleIconRes(isLightNotificationTheme));
        remoteViews.setOnClickPendingIntent(R.id.iv_cancle, canclePendingIntent);
        return remoteViews;
    }

    private int getPlayIconRes(boolean isLightNotificationTheme, boolean isPlaying) {
        if (isPlaying) {
            return isLightNotificationTheme
                    ? R.drawable.ic_status_bar_pause_dark_selector
                    : R.drawable.ic_status_bar_pause_light_selector;
        } else {
            return isLightNotificationTheme
                    ? R.drawable.ic_status_bar_play_dark_selector
                    : R.drawable.ic_status_bar_play_light_selector;
        }
    }

    private int getNextIconRes(boolean isLightNotificationTheme) {
        return isLightNotificationTheme
                ? R.drawable.ic_status_bar_next_dark_selector
                : R.drawable.ic_status_bar_next_light_selector;
    }

    private static int getCancleIconRes(boolean isLightNotificationTheme) {
        return isLightNotificationTheme
                ? R.drawable.ic_status_bar_cancle_dark_selector
                : R.drawable.ic_status_bar_cancle_light_selector;
    }

    private boolean isLightNotificationTheme(Context context) {
        int notificationTextColor = getNotificationTextColor(context);
        return isSimilarColor(Color.BLACK, notificationTextColor);
    }

    private int getNotificationTextColor(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder.build();
        RemoteViews remoteViews = notification.contentView;
        if (remoteViews == null) {
            return Color.BLACK;
        }
        int layoutId = remoteViews.getLayoutId();
        ViewGroup notificationLayout = (ViewGroup) LayoutInflater.from(context).inflate(layoutId, null);
        TextView title = (TextView) notificationLayout.findViewById(android.R.id.title);
        if (title != null) {
            return title.getCurrentTextColor();
        } else {
            return findTextColor(notificationLayout);
        }
    }

    /**
     * 如果通过 android.R.id.title 无法获得 title ，
     * 则通过遍历 notification 布局找到 textSize 最大的 TextView ，应该就是 title 了。
     */
    private int findTextColor(ViewGroup notificationLayout) {
        List<TextView> textViewList = new ArrayList<>();
        findTextView(notificationLayout, textViewList);

        float maxTextSize = -1;
        TextView maxTextView = null;
        for (TextView textView : textViewList) {
            if (textView.getTextSize() > maxTextSize) {
                maxTextView = textView;
            }
        }

        if (maxTextView != null) {
            return maxTextView.getCurrentTextColor();
        }

        return Color.BLACK;
    }

    private void findTextView(View view, List<TextView> textViewList) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                findTextView(viewGroup.getChildAt(i), textViewList);
            }
        } else if (view instanceof TextView) {
            textViewList.add((TextView) view);
        }
    }

    private boolean isSimilarColor(int baseColor, int color) {
        int simpleBaseColor = baseColor | 0xff000000;
        int simpleColor = color | 0xff000000;
        int baseRed = Color.red(simpleBaseColor) - Color.red(simpleColor);
        int baseGreen = Color.green(simpleBaseColor) - Color.green(simpleColor);
        int baseBlue = Color.blue(simpleBaseColor) - Color.blue(simpleColor);
        double value = Math.sqrt(baseRed * baseRed + baseGreen * baseGreen + baseBlue * baseBlue);
        return value < 180.0;
    }
}