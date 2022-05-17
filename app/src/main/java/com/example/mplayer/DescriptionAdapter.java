package com.example.mplayer;

import android.app.PendingIntent;
import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;

public class DescriptionAdapter implements PlayerNotificationManager.MediaDescriptionAdapter {

    MusicList getMusicList(MediaItem currentMediaItem) {
        MusicList currentMusicList = null;
        for(MusicList m : MainActivity.musicLists) {
            if(m.getMediaItem() == currentMediaItem)
                currentMusicList = m;
        }
        return currentMusicList;
    }

    @Override
    public CharSequence getCurrentContentTitle(Player player) {
        //MediaItem currentMediaItem = player.getCurrentMediaItem();
        //return getMusicList(currentMediaItem).getTitle();
        return player.getCurrentMediaItem().mediaMetadata.displayTitle;
    }

    @Nullable
    @Override
    public PendingIntent createCurrentContentIntent(Player player) {
        return null;
    }

    @Nullable
    @Override
    public CharSequence getCurrentContentText(Player player) {
        //MediaItem currentMediaItem = player.getCurrentMediaItem();
        //return getMusicList(currentMediaItem).getArtist();
        return null;
    }

    @Nullable
    @Override
    public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
        return null;
    }
}
