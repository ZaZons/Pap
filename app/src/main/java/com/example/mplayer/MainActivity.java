package com.example.mplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.StyledPlayerControlView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.Manifest;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements ChangeSongListener {
    //LinearLayout searchBtn;
    //LinearLayout menuBtn;

    static ExoPlayer player;

    final List<MusicList> musicLists = new ArrayList<>();

    MusicList currentMusicList;
    MusicAdapter musicAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //menuBtn = findViewById(R.id.menuBtn);
        CardView loopBtnCard = findViewById(R.id.loopBtnCard);
        CardView nextBtnCard = findViewById(R.id.nextBtnCard);
        CardView playPauseCard = findViewById(R.id.playPauseCard);
        CardView previousBtnCard = findViewById(R.id.previousBtnCard);
        CardView repeatOneIndicator = findViewById(R.id.repeatOneIndicator);
        CardView shuffleBtnCard = findViewById(R.id.shuffleBtnCard);
        ImageView playPauseImg = findViewById(R.id.playPauseImg);
        RecyclerView musicRecyclerView = findViewById(R.id.musicRecyclerView);
        StyledPlayerControlView musicView = findViewById(R.id.playerView);
        //searchBtn = findViewById(R.id.searchBtn);
        int blue_primary = ContextCompat.getColor(getApplicationContext(), R.color.blue_primary);
        int pink_primary = ContextCompat.getColor(getApplicationContext(), R.color.pink_primary);

        //configure recyclerview e pedir perm para aceder ao storage
        musicRecyclerView.setHasFixedSize(false);
        musicRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        requestPermission(musicRecyclerView);

        //initialize the player
        player = new ExoPlayer.Builder(this).build();
        musicView.setPlayer(player);

        //controls

        //alternar entre os diferentes tipos de loop (um, todos ou nenhum)
        loopBtnCard.setOnClickListener(v -> {
            int repeatMode = player.getRepeatMode();

            switch(repeatMode) {
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
        });

        //skip to next music
        nextBtnCard.setOnClickListener(v -> {
            if(player.hasNextMediaItem())
                player.seekToNextMediaItem();

            if(!player.isPlaying())
                play();
        });

        //play or pause
        playPauseCard.setOnClickListener(v -> {
            if(player.isPlaying())
                player.pause();
            else
                play();
        });

        //go to the previous music
        previousBtnCard.setOnClickListener(v -> {
            long millisecondsToGoBack = 1000;
            if(player.getCurrentPosition() >= millisecondsToGoBack) {
                player.seekTo(0);
            } else {
                if(player.hasPreviousMediaItem())
                    player.seekToPreviousMediaItem();
            }
        });

        shuffleBtnCard.setOnClickListener(v ->
            player.setShuffleModeEnabled(!player.getShuffleModeEnabled())
        );

        player.addListener(new ExoPlayer.Listener() {
            //atualizacao da UI quando troca de mediItem
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

            //mudar a imagem do botao de play
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

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
                if(shuffleModeEnabled)
                    shuffleBtnCard.setCardBackgroundColor(pink_primary);
                else
                    shuffleBtnCard.setCardBackgroundColor(blue_primary);
            }
        });
    }

    void findSongs(RecyclerView recyclerView) {
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
                recyclerView.setAdapter(musicAdapter);

                Toast.makeText(this, musicAdapter.getItemCount() + " Songs found", Toast.LENGTH_SHORT).show();
                Log.d("FindSongs", musicAdapter.getItemCount() + " Songs found");

                cursor.close();
            }
        } catch (Exception e) {
            Log.e("FindSongs error", "Error: " + e.getMessage());
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

    //perms handled by Dexter
    void requestPermission(RecyclerView musicRecyclerView) {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        findSongs(musicRecyclerView);
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