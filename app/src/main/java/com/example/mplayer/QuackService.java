package com.example.mplayer;

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        MediaControllerCompat controller = mediaSession.getController();
//        MediaMetadataCompat mediaMetadata = controller.getMetadata();
//        MediaDescriptionCompat description = mediaMetadata.getDescription();
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "playback_channel");
//
//        builder
//                // Add the metadata for the currently playing track
//                .setContentTitle(description.getTitle())
//                .setContentText(description.getSubtitle())
//                .setSubText(description.getDescription())
//                .setLargeIcon(description.getIconBitmap())
//
//                // Enable launching the player by clicking the notification
//                .setContentIntent(controller.getSessionActivity())
//
//                // Stop the service when the notification is swiped away
//                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context,
//                        PlaybackStateCompat.ACTION_STOP))
//
//                // Make the transport controls visible on the lockscreen
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//
//                // Add an app icon and set its accent color
//                // Be careful about the color
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
////                .setColor(ContextCompat.getColor(context, R.color.primaryDark))
//
//                // Add a pause button
//                .addAction(new NotificationCompat.Action(
//                        R.drawable.ic_pause, "Pause",
//                        MediaButtonReceiver.buildMediaButtonPendingIntent(context,
//                                PlaybackStateCompat.ACTION_PLAY_PAUSE)))
//
//                // Take advantage of MediaStyle features
//                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
//                        .setMediaSession(mediaSession.getSessionToken())
//                        .setShowActionsInCompactView(0)
//
//                        // Add a cancel button
//                        .setShowCancelButton(true)
//                        .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context,
//                                PlaybackStateCompat.ACTION_STOP)));

//
//        PendingIntent previousIntent = null;
//        Notification notification = new NotificationCompat.Builder(getApplicationContext(), "playback_channel")
//                // Show controls on lock screen even when user hides sensitive content.
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                // Add media control buttons that invoke intents in your media service
//                .addAction(R.drawable.ic_skip_previous, "Previous", previousIntent) // #0
//                .addAction(R.drawable.ic_pause, "Pause", previousIntent)  // #1
//                .addAction(R.drawable.ic_skip_next, "Next", previousIntent)     // #2
//                // Apply the media style template
//                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
//                        .setShowActionsInCompactView(1 /* #1: pause button */)
//                        .setMediaSession(mediaSession.getSessionToken()))
//                .setContentTitle("Wonderful music")
//                .setContentText("My Awesome Band")
//                //.setLargeIcon()
//                .build();

        DescriptionAdapter descriptionAdapter = new DescriptionAdapter();

        PlayerNotificationManager playerNotificationManager =
                new PlayerNotificationManager.Builder(getApplicationContext(), 1, "playback_channel")
                        .setMediaDescriptionAdapter(descriptionAdapter)
                        .setNextActionIconResourceId(R.drawable.ic_skip_next)
                        .setPauseActionIconResourceId(R.drawable.ic_pause)
                        .setPreviousActionIconResourceId(R.drawable.ic_skip_previous)
                        .setPlayActionIconResourceId(R.drawable.ic_play_arrow)
                        .setStopActionIconResourceId(R.drawable.ic_stop)
                        .setSmallIconResourceId(R.mipmap.ic_launcher_foreground)
                        .setNotificationListener(new PlayerNotificationManager.NotificationListener() {
                            @Override
                            public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
                                PlayerNotificationManager.NotificationListener.super.onNotificationPosted(notificationId, notification, ongoing);
                                if(!ongoing) {
                                    stopForeground(false);
                                } else {
                                    startForeground(startId, notification);
                                }
                            }

                            @Override
                            public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
                                PlayerNotificationManager.NotificationListener.super.onNotificationCancelled(notificationId, dismissedByUser);
                                stopSelf();
                            }
                        })
                        .build();

        playerNotificationManager.setUseNextActionInCompactView(true);
        playerNotificationManager.setUsePreviousActionInCompactView(true);
        playerNotificationManager.setUseFastForwardAction(false);
        playerNotificationManager.setUseRewindAction(false);
        playerNotificationManager.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        playerNotificationManager.setColor(MainActivity.getColorPrimary());
        playerNotificationManager.setColorized(false);
        playerNotificationManager.setPriority(PRIORITY_HIGH);
        playerNotificationManager.setPlayer(MainActivity.getPlayer());
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
        return START_NOT_STICKY;
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
