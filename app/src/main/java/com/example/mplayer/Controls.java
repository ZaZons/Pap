package com.example.mplayer;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;

public class Controls {
    ExoPlayer player;

    public Controls(ExoPlayer player) {
        this.player = player;
    }

    public void loop() {
        int repeatMode = player.getRepeatMode();

        switch (repeatMode) {
            case Player.REPEAT_MODE_OFF:
                player.setRepeatMode(Player.REPEAT_MODE_ALL);
                break;

            case Player.REPEAT_MODE_ONE:
                player.setRepeatMode(Player.REPEAT_MODE_OFF);
                break;

            case Player.REPEAT_MODE_ALL:
                player.setRepeatMode(Player.REPEAT_MODE_ONE);
                break;
        }
    }

    public void skipNext() {
        if(player.hasNextMediaItem())
            player.seekToNextMediaItem();

        if(!player.isPlaying())
            player.play();
    }

    public void playPause() {
        if(player.isPlaying())
            player.pause();
        else
            player.play();
    }

    public void skipPrevious() {
        long millisecondsToGoBack = 3000;
        if(player.getCurrentPosition() >= millisecondsToGoBack) {
            player.seekTo(0);
        } else {
            if(player.hasPreviousMediaItem())
                player.seekToPreviousMediaItem();
        }
    }

    public void shuffle() {
        player.setShuffleModeEnabled(!player.getShuffleModeEnabled());
    }
}
