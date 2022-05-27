package com.example.mplayer;

import android.net.Uri;
import android.util.Log;

import com.google.android.exoplayer2.MediaItem;

public class MusicList {

    private final int id;
    private boolean isPlaying;
    private final String title, artist, duration;
    private MediaItem mediaItem;
    private final String musicFile;

    public MusicList(int id, String title, String artist, String duration, boolean isPlaying, Uri musicFile) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.isPlaying = isPlaying;
        this.musicFile = musicFile.toString();
        createMediaItem();
    }

    public String getMusicFile() {
        return musicFile;
    }

    public int getId() { return id; }

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

    public void setMediaItem(MediaItem mediaItem) {
        this.mediaItem = mediaItem;
    }

    public void createMediaItem() {
        mediaItem = MediaItem.fromUri(musicFile);
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
