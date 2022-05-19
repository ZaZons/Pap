//package com.example.mplayer;
//
//import static androidx.core.content.PackageManagerCompat.LOG_TAG;
//
//import android.os.Bundle;
//import android.support.v4.media.MediaBrowserCompat;
//import android.support.v4.media.session.MediaSessionCompat;
//import android.support.v4.media.session.PlaybackStateCompat;
//import android.text.TextUtils;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.media.MediaBrowserServiceCompat;
//
//import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MediaPlaybackService extends MediaBrowserServiceCompat {
//    private static final String MY_MEDIA_ROOT_ID = "media_root_id";
//    private static final String MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id";
//
//    private MediaSessionCompat mediaSession;
//    private PlaybackStateCompat.Builder stateBuilder;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        // Create a MediaSessionCompat
//        mediaSession = new MediaSessionCompat(this, "media_session");
//        MediaSessionConnector mediaSessionConnector = new MediaSessionConnector(mediaSession);
//        mediaSessionConnector.setPlayer(MainActivity.getPlayer());
//
//        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
//        stateBuilder = new PlaybackStateCompat.Builder()
//                .setActions(
//                        PlaybackStateCompat.ACTION_PLAY |
//                                PlaybackStateCompat.ACTION_PLAY_PAUSE);
//        mediaSession.setPlaybackState(stateBuilder.build());
//
//        // MySessionCallback() has methods that handle callbacks from a media controller
//        mediaSession.setCallback(new MySessionCallback());
//
//        // Set the session's token so that client activities can communicate with it.
//        setSessionToken(mediaSession.getSessionToken());
//    }
//
//    @Nullable
//    @Override
//    public BrowserRoot onGetRoot(String clientPackageName, int clientUid,
//                                 Bundle rootHints) {
//        return new BrowserRoot(MY_EMPTY_MEDIA_ROOT_ID, null);
//    }
//
//    @Override
//    public void onLoadChildren(final String parentMediaId,
//                               final Result<List<MediaBrowserCompat.MediaItem>> result) {
//
//        //  Browsing not allowed
//        if (TextUtils.equals(MY_EMPTY_MEDIA_ROOT_ID, parentMediaId)) {
//            result.sendResult(null);
//            return;
//        }
//
//        // Assume for example that the music catalog is already loaded/cached.
//
//        List<MediaBrowserCompat.MediaItem> mediaItems = new  ArrayList<>();
//
//        // Check if this is the root menu:
//        if (MY_MEDIA_ROOT_ID.equals(parentMediaId)) {
//            // Build the MediaItem objects for the top level,
//            // and put them in the mediaItems list...
//        } else {
//            // Examine the passed parentMediaId to see which submenu we're at,
//            // and put the children of that menu in the mediaItems list...
//        }
//        result.sendResult(mediaItems);
//    }
//}
