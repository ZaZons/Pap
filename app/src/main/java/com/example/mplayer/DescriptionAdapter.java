package com.example.mplayer;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;

public class DescriptionAdapter implements PlayerNotificationManager.MediaDescriptionAdapter {

    MusicList getMusicList(MediaItem currentMediaItem) {
        MusicList currentMusicList = null;
        for(MusicList m : MainActivity.getList()) {
            if(m.getMediaItem() == currentMediaItem)
                currentMusicList = m;
        }
        return currentMusicList;
    }

    @Override
    public CharSequence getCurrentContentTitle(Player player) {
        MediaItem currentMediaItem = player.getCurrentMediaItem();
        return getMusicList(currentMediaItem).getTitle();
//        return player.getCurrentMediaItem().mediaMetadata.displayTitle;
//        return "title";
    }

    @Nullable
    @Override
    public PendingIntent createCurrentContentIntent(Player player) {
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent =
//                PendingIntent.getActivity(this, 0, notificationIntent, 0);
        return null;
    }

    @Nullable
    @Override
    public CharSequence getCurrentContentText(Player player) {
        MediaItem currentMediaItem = player.getCurrentMediaItem();
        return getMusicList(currentMediaItem).getArtist();
//        return "text";
    }

    @Nullable
    @Override
    public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
//        int window = player.getCurrentMediaItemIndex();
//        Bitmap largeIcon = getLargeIcon(window);
//        if (largeIcon == null && getLargeIconUri(window) != null) {
//            // load bitmap async
//            loadBitmap(getLargeIconUri(window), callback);
//            return getPlaceholderBitmap();
//        }
        return null;
    }
}
