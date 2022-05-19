package com.example.mplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.google.android.exoplayer2.ui.PlayerNotificationManager;

class NotificationReceiver extends BroadcastReceiver {

    IntentFilter intentFilter = new IntentFilter(PlayerNotificationManager.ACTION_PREVIOUS);

    @Override
    public void onReceive(Context context, Intent intent) {
        switch(intent.toString()) {
            case PlayerNotificationManager.ACTION_PREVIOUS: {
                MainActivity.getControls().skipPrevious();
            }
        }
    }
}


