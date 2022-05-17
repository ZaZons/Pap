package com.example.mplayer;

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;

public class QuackService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Service", "Service started");

        DescriptionAdapter descriptionAdapter = new DescriptionAdapter();

        PlayerNotificationManager playerNotificationManager =
                new PlayerNotificationManager.Builder(getApplicationContext(), 1, createNotificationChannel())
                        .setMediaDescriptionAdapter(descriptionAdapter)
                        .build();

        playerNotificationManager.setUseFastForwardAction(false);
        playerNotificationManager.setUsePreviousAction(true);
        playerNotificationManager.setUseNextAction(true);
        playerNotificationManager.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        playerNotificationManager.setUseRewindAction(false);
        playerNotificationManager.setPriority(PRIORITY_HIGH);
        playerNotificationManager.setSmallIcon(R.mipmap.ic_launcher_foreground);
        playerNotificationManager.setPlayer(player);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification =
                new Notification.Builder(this, "playback_channel")
                        .setCategory(MEDIA_SESSION_SERVICE)
                        .setContentIntent(pendingIntent)
                        .build();

        Log.d("Service", "Service started");

        startForeground(0, notification);
        return START_STICKY;
    }

    ExoPlayer player = MainActivity.getPlayer();

    String createNotificationChannel() {
        CharSequence name = "0";
        String channelId = "playback_channel";
        String description = "Playback notifications";
        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel channel = new NotificationChannel(channelId, name, importance);
        channel.setDescription(description);

        NotificationManager notificationChannelManager = getSystemService(NotificationManager.class);
        notificationChannelManager.createNotificationChannel(channel);

        return channel.getId();
    }
}
