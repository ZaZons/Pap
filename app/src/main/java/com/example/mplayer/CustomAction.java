package com.example.mplayer;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomAction implements PlayerNotificationManager.CustomActionReceiver {
    @Override
    public Map<String, NotificationCompat.Action> createCustomActions(Context context, int instanceId) {
        NotificationCompat.Action action = new NotificationCompat.Action(context.getResources().getIdentifier("ACTION_SKIP_PREVIOUS","drawable",context.getPackageName()),"skip_previous",null);

        Map<String, NotificationCompat.Action> actionMap = new HashMap<>();
        actionMap.put("skip_previous", action);
        return actionMap;
    }

    @Override
    public List<String> getCustomActions(Player player) {
        List<String> customActions = new ArrayList<>();
        customActions.add("skip_previous");
        return customActions;
    }

    @Override
    public void onCustomAction(Player player, String action, Intent intent) {
        Log.d("Action", "" + action);
    }
}
