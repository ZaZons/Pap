package com.example.mplayer;

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.session.MediaSession;
import android.os.IBinder;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.media.session.MediaButtonReceiver;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;

public class QuackService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Context context = getApplicationContext();
    MediaSessionCompat mediaSession = MainActivity.getMediaSession();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaControllerCompat controller = mediaSession.getController();
        MediaMetadataCompat mediaMetadata = controller.getMetadata();
        MediaDescriptionCompat description = mediaMetadata.getDescription();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "playback_channel");

        builder
                // Add the metadata for the currently playing track
                .setContentTitle(description.getTitle())
                .setContentText(description.getSubtitle())
                .setSubText(description.getDescription())
                .setLargeIcon(description.getIconBitmap())

                // Enable launching the player by clicking the notification
                .setContentIntent(controller.getSessionActivity())

                // Stop the service when the notification is swiped away
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                        PlaybackStateCompat.ACTION_STOP))

                // Make the transport controls visible on the lockscreen
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                // Add an app icon and set its accent color
                // Be careful about the color
                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setColor(ContextCompat.getColor(context, R.color.primaryDark))

                // Add a pause button
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_pause, "Pause",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                                PlaybackStateCompat.ACTION_PLAY_PAUSE)))

                // Take advantage of MediaStyle features
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0)

                        // Add a cancel button
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                                PlaybackStateCompat.ACTION_STOP)));

// Display the notification and place the service in the foreground
        startForeground(startId, builder.build());
//        Log.d("Service", "Service started");
//
//        DescriptionAdapter descriptionAdapter = new DescriptionAdapter();
//
//        PlayerNotificationManager playerNotificationManager =
//                new PlayerNotificationManager.Builder(getApplicationContext(), 1, createNotificationChannel())
//                        .setMediaDescriptionAdapter(descriptionAdapter)
//                        .build();
//
//        playerNotificationManager.setUseFastForwardAction(false);
//        playerNotificationManager.setUsePreviousAction(true);
//        playerNotificationManager.setUseNextAction(true);
//        playerNotificationManager.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
//        playerNotificationManager.setUseRewindAction(false);
//        playerNotificationManager.setPriority(PRIORITY_HIGH);
//        playerNotificationManager.setSmallIcon(R.mipmap.ic_launcher_foreground);
//        playerNotificationManager.setPlayer(player);
//
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent =
//                PendingIntent.getActivity(this, 0, notificationIntent, 0);
//
//        Notification notification =
//                new Notification.Builder(this, "playback_channel")
//                        .setCategory(MEDIA_SESSION_SERVICE)
//                        .setContentIntent(pendingIntent)
//                        .build();
//
//        Log.d("Service", "Service started");
//
//        startForeground(0, notification);
        return START_STICKY;
    }

//    ExoPlayer player = MainActivity.getPlayer();

    /*String createNotificationChannel() {
        CharSequence name = "0";
        String channelId = "playback_channel";
        String description = "Playback notifications";
        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel channel = new NotificationChannel(channelId, name, importance);
        channel.setDescription(description);

        NotificationManager notificationChannelManager = getSystemService(NotificationManager.class);
        notificationChannelManager.createNotificationChannel(channel);

        return channel.getId();
    }*/
}
