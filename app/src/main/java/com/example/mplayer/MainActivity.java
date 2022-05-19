package com.example.mplayer;

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.ui.StyledPlayerControlView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.Manifest;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements ChangeSongListener {
    //LinearLayout searchBtn;
    //LinearLayout menuBtn;

    CardView loopBtnCard;
    CardView nextBtnCard;
    CardView playPauseCard;
    CardView previousBtnCard;
    CardView repeatOneIndicator;
    CardView shuffleBtnCard;
    ImageView playPauseImg;
    RecyclerView musicRecyclerView;

    int blue_primary;
    int pink_primary;

    static ExoPlayer player;

    static final List<MusicList> musicLists = new ArrayList<>();

    MusicList currentMusicList;
    MusicAdapter musicAdapter;

    static MediaSessionCompat mediaSession;
    PlayerNotificationManager playerNotificationManager;
    NotificationManager notificationChannelManager;
    static Controls controls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //menuBtn = findViewById(R.id.menuBtn);
        loopBtnCard = findViewById(R.id.loopBtn);
        nextBtnCard = findViewById(R.id.nextBtn);
        playPauseCard = findViewById(R.id.playPauseBtn);
        previousBtnCard = findViewById(R.id.previousBtn);
        repeatOneIndicator = findViewById(R.id.repeatOneIndicator);
        shuffleBtnCard = findViewById(R.id.shuffleBtnCard);
        playPauseImg = findViewById(R.id.playPauseImg);
        musicRecyclerView = findViewById(R.id.musicRecyclerView);
        StyledPlayerControlView musicView = findViewById(R.id.playerView);
        //searchBtn = findViewById(R.id.searchBtn);
        blue_primary = ContextCompat.getColor(getApplicationContext(), R.color.blue_primary);
        pink_primary = ContextCompat.getColor(getApplicationContext(), R.color.pink_primary);

        //configurar recyclerview e pedir perm para aceder ao storage
        musicRecyclerView.setHasFixedSize(false);
        musicRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        requestPermission();

        //initialize the player
        player = new ExoPlayer.Builder(this).build();
        musicView.setPlayer(player);

        //controls
        controls();

        //listener
        listener();

        player.setForegroundMode(true);
        notification();

        //Context context = getApplicationContext();
        //Intent intent = new Intent(this, QuackService.class);
        //context.startForegroundService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.setForegroundMode(false);
        playerNotificationManager.setPlayer(null);
        //notificationChannelManager.cancelAll();
        player = null;
        mediaSession.setActive(false);
    }

    void notification() {
        CharSequence name = "Playback";
        String channelId = "playback_channel";
        String description = "Playback notifications";
        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel channel = new NotificationChannel(channelId, name, importance);
        channel.setDescription(description);

        NotificationManager notificationChannelManager = getSystemService(NotificationManager.class);
        notificationChannelManager.createNotificationChannel(channel);

        mediaSession = new MediaSessionCompat(this, "sample");
        MediaSessionConnector mediaSessionConnector = new MediaSessionConnector(mediaSession);
        mediaSessionConnector.setPlayer(player);
        mediaSession.setActive(true);
        Context context = getApplicationContext();
        Intent intent = new Intent();
        startService(intent);


//        PendingIntent previousIntent = null;
//
//        Notification notification = new NotificationCompat.Builder(getApplicationContext(), channelId)
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
//
//        DescriptionAdapter descriptionAdapter = new DescriptionAdapter();
//
//        PlayerNotificationManager playerNotificationManager =
//                new PlayerNotificationManager.Builder(getApplicationContext(), 1, channelId)
//                        .setMediaDescriptionAdapter(descriptionAdapter)
//                        .build();
//
//        playerNotificationManager.setUseFastForwardAction(false);
//        playerNotificationManager.setUsePreviousAction(false);
//        playerNotificationManager.setUseNextAction(true);
//        playerNotificationManager.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
//        playerNotificationManager.setUseRewindAction(false);
//        playerNotificationManager.setPriority(PRIORITY_HIGH);
//        playerNotificationManager.setSmallIcon(R.mipmap.ic_launcher_foreground);
//        playerNotificationManager.setPlayer(player);
//        NotificationReceiver notificationReceiver = new NotificationReceiver();
//        registerReceiver(notificationReceiver, new IntentFilter(PlayerNotificationManager.ACTION_PREVIOUS));
    }

    void controls() {
        controls = new Controls(player);
        //alternar entre os diferentes tipos de loop (um, todos ou nenhum)
        loopBtnCard.setOnClickListener(v -> controls.loop());

        //skip to next music
        nextBtnCard.setOnClickListener(v -> controls.skipNext());

        //play or pause
        playPauseCard.setOnClickListener(v -> controls.playPause());

        //go to the previous music
        previousBtnCard.setOnClickListener(v -> controls.skipPrevious());

        //shuffle the queue
        shuffleBtnCard.setOnClickListener(v -> controls.shuffle());
    }

    void listener() {
        player.addListener(new ExoPlayer.Listener() {
            //atualizacao da UI quando troca de mediaItem
            @Override
            public void onMediaItemTransition(MediaItem newMediaItem, @com.google.android.exoplayer2.Player.MediaItemTransitionReason int reason) {
                if(currentMusicList != null)
                    currentMusicList.setPlaying(false);

                for(MusicList m : musicLists) {
                    if(m.getMediaItem() == newMediaItem)
                        currentMusicList = m;
                }

                if(currentMusicList != null)
                    updateUi(currentMusicList);

                if(!player.isPlaying())
                    play();
            }

            //Alterar a imagem do botao de play consoante o estado da repordução
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if(isPlaying)
                    playPauseImg.setImageResource(R.drawable.ic_pause);
                else
                    playPauseImg.setImageResource(R.drawable.ic_play_arrow);
            }

            //atualizar o botao de loop
            @Override
            public void onRepeatModeChanged(int repeatMode) {
                int visibility = repeatOneIndicator.getVisibility();
                ColorStateList cardColor = loopBtnCard.getCardBackgroundColor();

                switch(repeatMode) {
                    case Player.REPEAT_MODE_OFF:
                        if(visibility == View.VISIBLE)
                            repeatOneIndicator.setVisibility(View.INVISIBLE);

                        if(cardColor.getDefaultColor() == pink_primary)
                            loopBtnCard.setCardBackgroundColor(blue_primary);
                        break;

                    case Player.REPEAT_MODE_ONE:
                        if(visibility == View.INVISIBLE)
                            repeatOneIndicator.setVisibility(View.VISIBLE);

                        if(cardColor.getDefaultColor() == blue_primary)
                            loopBtnCard.setCardBackgroundColor(pink_primary);
                        break;

                    case Player.REPEAT_MODE_ALL:
                        if(visibility == View.VISIBLE)
                            repeatOneIndicator.setVisibility(View.INVISIBLE);

                        if(cardColor.getDefaultColor() == blue_primary)
                            loopBtnCard.setCardBackgroundColor(pink_primary);
                        break;
                }
            }

            //atualizar o botao de shuffle
            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
                if(shuffleModeEnabled)
                    shuffleBtnCard.setCardBackgroundColor(pink_primary);
                else
                    shuffleBtnCard.setCardBackgroundColor(blue_primary);
            }
        });
    }

    void findSongs() {
        try {
            //usar a media store para encontrar os ficheiros
            ContentResolver contentResolver = getApplicationContext().getContentResolver();

            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String selection = MediaStore.Audio.Media.IS_MUSIC +  " != 0";
            String sortOrder = MediaStore.Audio.Media.DATE_ADDED + " DESC";

            /*
            String[] selectionArgs = {"%.mp3%", "%.wav", "%.m4a"};
            String selection = MediaStore.Audio.AudioColumns.IS_MUSIC;
            */

            Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

            if(cursor == null) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            } else if (!cursor.moveToNext()) {
                Toast.makeText(this, "No music found", Toast.LENGTH_SHORT).show();
            } else {
                cursor.moveToFirst();
                do {
                    long cursorId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));

                    //get propriedades das musicas para adicionar a lista
                    String getArtistName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    String getDuration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION));
                    String getMusicFileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));

                    Uri getMusicFileUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursorId);

                    //construir um objeto de cada musica com as propriedades adquiridas provisoriamente
                    final MusicList musicList = new MusicList(getMusicFileName, getArtistName, generateTime(getDuration), false, getMusicFileUri);

                    //adicionar o objeto a lista
                    musicLists.add(musicList);
                } while(cursor.moveToNext());

                //criar e adicionar o adaptador ao recyclerView
                musicAdapter = new MusicAdapter(musicLists, MainActivity.this);
                musicRecyclerView.setAdapter(musicAdapter);

                Toast.makeText(this, musicAdapter.getItemCount() + " Songs found", Toast.LENGTH_SHORT).show();
                Log.d("FindSongs", musicAdapter.getItemCount() + " Songs found");

                cursor.close();
            }
        } catch (Exception e) {
            Log.d("FindSongs", "Error: " + e.getMessage());
        }
    }

    //evento de quando o utilizador seleciona uma musica do recycler view
    @Override
    public void onChanged(int position) {
        //get o mediaItem do objeto selecionado
        MusicList firstItem = musicLists.get(position);
        MediaItem mediaItem = firstItem.getMediaItem();

        //se o media item q selecionar ja tiver a dar ent ele retorna
        if(player.getCurrentMediaItem() == mediaItem)

            return;

        //resetar o player
        player.stop();
        player.clearMediaItems();

        player.addMediaItem(mediaItem);

        //por os itens todos da lista em queue
        //quando chega ao ultimo volta para o primeiro, e quando chega ao selecionado baza do loop
        for(int i = position + 1; i < musicAdapter.getItemCount() + 1; i++) {
            if(i == musicAdapter.getItemCount())
                i = 0;

            if(i == position)
                break;

            MediaItem nextMediaItem = musicLists.get(i).getMediaItem();
            player.addMediaItem(nextMediaItem);
        }

        updateUi(firstItem);
        play();
    }

    @SuppressLint("NotifyDataSetChanged")
    void updateUi(MusicList currentMusicList) {
        currentMusicList.setPlaying(true);
        musicAdapter.notifyDataSetChanged();
    }

    //transformar os ms em minutos e segundos
    String generateTime(String duration) {
        long time = Long.parseLong(duration);
        long seconds = time / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;

        String stringedMinutes = Long.toString(minutes);
        String stringedSeconds = Long.toString(seconds);

        if(seconds < 10)
            return (stringedMinutes + ":0" + stringedSeconds);
        else
            return (stringedMinutes + ":" + stringedSeconds);
    }

    /*String generateTime(long duration) {
        long seconds = duration / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;

        String stringedMinutes = Long.toString(minutes);
        String stringedSeconds = Long.toString(seconds);

        if(seconds < 10) return (stringedMinutes + ":0" + stringedSeconds);
        else return (stringedMinutes + ":" + stringedSeconds);
    }*/

    static void play() {
        player.prepare();
        player.play();
    }

    public static ExoPlayer getPlayer() {
        return player;
    }

    public static List<MusicList> getList() {
        return musicLists;
    }

    public static Controls getControls() {return controls;}

    public static MediaSessionCompat getMediaSession() {
        return mediaSession;
    }

    //perms handled by Dexter
    void requestPermission() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        findSongs();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(getApplicationContext(), "We need storage access to get your music", Toast.LENGTH_SHORT).show();

                        if (permissionDeniedResponse.isPermanentlyDenied())
                            Toast.makeText(getApplicationContext(), "If you don't allow storage access we can't get your music", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
}