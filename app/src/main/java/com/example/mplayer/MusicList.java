package com.example.mplayer;

import android.net.Uri;

import com.google.android.exoplayer2.MediaItem;

public class MusicList {

    private boolean isPlaying;
    private final String title, artist, duration;
    private final MediaItem mediaItem;

    public MusicList(String title, String artist, String duration, boolean isPlaying, Uri musicFile) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.isPlaying = isPlaying;
        this.mediaItem = MediaItem.fromUri(musicFile);
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getDuration() {
        return duration;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    /*public Uri getMusicFile() {
        return musicFile;
    }*/

    public MediaItem getMediaItem() {return mediaItem;}

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
