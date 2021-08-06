package com.amrit.practice.musicplayer;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.RemoteViews;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import java.io.IOException;

public class NotificationUtil {

    public static final String LOG_TAG = NotificationUtil.class.getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private static NotificationCompat.Builder mGlobalNotification = null;
    public static final int NOTIFICATION_ID = 5;
    public static final String NOTIFICATION_CHANNEL_NAME = "song_notification_channel";
    private static NotificationManager manager;
    private static final Handler seekHandler = new Handler();
    private static int PROGRESS_MAX = MusicController.getFinalDuration();

    private static RemoteViews notificationLayout, notificationLayoutExpanded;
    private static PendingIntent resultPendingIntent;

    @SuppressLint("RemoteViewLayout")
    public static void showNotification(Context context, String uri_string){

        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel mChannel = new NotificationChannel(
                NOTIFICATION_CHANNEL_NAME,
                "primary",
                NotificationManager.IMPORTANCE_DEFAULT);
        assert manager != null;
        manager.createNotificationChannel(mChannel);

        declareAttributes(context);

        Bitmap bitmap = null;

        Uri uri = Uri.parse(uri_string);
        try {
            bitmap = context.getContentResolver().loadThumbnail(
                    uri,
                    new Size(150, 150),
                    null);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Bitmap not available");
        }

        if(bitmap != null) notificationLayoutExpanded.setImageViewBitmap(R.id.image_notification, bitmap);
        else notificationLayoutExpanded.setImageViewResource(R.id.image_notification, R.drawable.music);

        mGlobalNotification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_NAME)
                .setColor(ContextCompat.getColor(context, android.R.color.holo_blue_dark))
                .setSmallIcon(R.drawable.music)
                .setCustomContentView(notificationLayout)
                .setCustomBigContentView(notificationLayoutExpanded)
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(resultPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        manager.notify(NOTIFICATION_ID, mGlobalNotification.build());
        seekBarRunnable.run();

    }
    
    @SuppressLint("ResourceAsColor")
    private static void declareAttributes(Context context){
        String songName = MusicController.songName();
         PROGRESS_MAX = MusicController.getFinalDuration();

        Intent resultIntent = new Intent(context, MusicActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent previousIntent = new Intent(context, Broadcaster.class);
        previousIntent.putExtra("action","previous");
        PendingIntent previousSong = PendingIntent.getBroadcast(context,1, previousIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(context, Broadcaster.class);
        nextIntent.putExtra("action", "next");
        PendingIntent nextSong = PendingIntent.getBroadcast(context, 2, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playPauseIntent = new Intent(context, Broadcaster.class);
        playPauseIntent.putExtra("action", "play_pause");
        PendingIntent playPauseSong = PendingIntent.getBroadcast(context, 3, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationLayout = new RemoteViews(context.getPackageName(), R.layout.mini_notification);
        notificationLayoutExpanded = new RemoteViews(context.getPackageName(), R.layout.notification);

        notificationLayout.setOnClickPendingIntent(R.id.previous_noti, previousSong);
        notificationLayout.setOnClickPendingIntent(R.id.play_pause_noti, playPauseSong);
        notificationLayout.setOnClickPendingIntent(R.id.next_noti, nextSong);

        notificationLayoutExpanded.setOnClickPendingIntent(R.id.previous_noti, previousSong);
        notificationLayoutExpanded.setOnClickPendingIntent(R.id.play_pause_noti, playPauseSong);
        notificationLayoutExpanded.setOnClickPendingIntent(R.id.next_noti, nextSong);

        notificationLayoutExpanded.setProgressBar(R.id.seek_bar_noti, PROGRESS_MAX, 0,false);
        notificationLayoutExpanded.setTextViewText(R.id.current_position_noti, "0");
        notificationLayoutExpanded.setTextViewText(R.id.final_position_noti, time(PROGRESS_MAX));

        notificationLayout.setTextViewText(R.id.song_name_notif, songName);
        notificationLayoutExpanded.setTextViewText(R.id.song_name_notif, songName);
    }

    public static void cancelNotification(Context context){
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mGlobalNotification.setOngoing(false);
        seekHandler.removeCallbacks(seekBarRunnable);
        assert manager != null;
        manager.notify(NOTIFICATION_ID, mGlobalNotification.build());
    }

    public static Runnable seekBarRunnable = new Runnable() {
        @Override
        public void run() {
            int currentTime = MusicController.getCurrentTime();
            PROGRESS_MAX = MusicController.getFinalDuration();

            notificationLayoutExpanded.setProgressBar(R.id.seek_bar_noti, PROGRESS_MAX, currentTime,false);
            mGlobalNotification.setProgress(PROGRESS_MAX, currentTime, false);
            manager.notify(NOTIFICATION_ID, mGlobalNotification.build());
            seekHandler.postDelayed(seekBarRunnable, 1000);
            notificationLayoutExpanded.setTextViewText(R.id.current_position_noti, time(currentTime));
        }
    };

    private static String time(int time_int){
        time_int = time_int/1000;
        String output = "";
        int sec = time_int % 60;
        int min = time_int / 60;

        output += min + ":";

        if(sec >= 10) output += sec + "";
        else output += "0" + sec;
        return output;
    }

}
