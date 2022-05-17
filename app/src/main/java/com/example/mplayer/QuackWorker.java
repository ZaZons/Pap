package com.example.mplayer;

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;

public class QuackWorker extends Worker {
    Context context;
    public QuackWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    ExoPlayer player = MainActivity.getPlayer();

    @NonNull
    @Override
    public Result doWork() {

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
        return null;
    }

    String createNotificationChannel() {
        CharSequence name = "0";
        String channelId = "playback_channel";
        String description = "Playback notifications";
        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel channel = new NotificationChannel(channelId, name, importance);
        channel.setDescription(description);

        NotificationManager notificationChannelManager = context.getSystemService(NotificationManager.class);
        notificationChannelManager.createNotificationChannel(channel);

        return channel.getId();
    }
}
