package com.example.mplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.StyledPlayerControlView;
import com.google.android.exoplayer2.ui.TimeBar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.Manifest;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;


public class MainActivity extends AppCompatActivity implements ChangeSongListener {
    CardView playPauseCard;
    ImageView nextBtn;
    ImageView previousBtn;
    LinearLayout searchBtn;
    LinearLayout menuBtn;

    private RecyclerView musicRecyclerView;
    private TextView endTime, startTime;
    private DefaultTimeBar playerSeekbar;
    private ImageView playPauseImg;
    private StyledPlayerControlView musicView;
    private int playingPosition;
    private MusicList currentMusicList;

    ExoPlayer player;

    private final List<MusicList> musicLists = new ArrayList<>();
    private MusicAdapter musicAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menuBtn = findViewById(R.id.menuBtn);
        musicRecyclerView = findViewById(R.id.musicRecyclerView);
        nextBtn = findViewById(R.id.nextBtn);
        playPauseImg = findViewById(R.id.playPauseImg);
        previousBtn = findViewById(R.id.previousBtn);
        playPauseCard = findViewById(R.id.playPauseCard);
        searchBtn = findViewById(R.id.searchBtn);
        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);
        playerSeekbar = findViewById(R.id.playerSeekBar);

        musicRecyclerView.setHasFixedSize(false);
        musicRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        musicView = findViewById(R.id.playerView);
        player = new ExoPlayer.Builder(this).build();
        musicView.setPlayer(player);
        player.setRepeatMode(Player.REPEAT_MODE_ALL);

        requestPermission();

        playPauseCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player.isPlaying()) {
                    player.pause();
                    playPauseImg.setImageResource(R.drawable.ic_play_arrow);
                } else {
                    play();
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(player.hasNextMediaItem())
                    player.seekToNextMediaItem();
            }
        });

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(player.hasPreviousMediaItem()) {
                    player.seekToPreviousMediaItem();
                }
            }
        });

        player.addListener(new ExoPlayer.Listener() {
            @Override
            public void onMediaItemTransition(MediaItem newMediaItem, @com.google.android.exoplayer2.Player.MediaItemTransitionReason int reason) {
                if(currentMusicList != null)
                    currentMusicList.setPlaying(false);

                for(MusicList m : musicLists)
                    if(m.getMediaItem().equals(newMediaItem)) currentMusicList = m;

                updateUi(currentMusicList);
            }
        });

        seekWithTheBar();
    }

    void findSongs() {
        try {
            ContentResolver contentResolver = getApplicationContext().getContentResolver();

            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String sortOrder = MediaStore.Audio.Media.DATE_ADDED + " DESC";
            String selection = MediaStore.Audio.Media.IS_MUSIC +  " != 0";

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
                int x = 0; //para remover no fim
                do {
                    x++; //para remover no fim
                    long cursorId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    Uri musicFileUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursorId);
                    final String getMusicFileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    final String getArtistName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    String getDuration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION));

                    final MusicList musicList = new MusicList(getMusicFileName, getArtistName, generateTime(getDuration), false, musicFileUri);
                    musicLists.add(musicList);

                    if(x == 104) break; //para remover no fim
                } while(cursor.moveToNext());
                musicAdapter = new MusicAdapter(musicLists, MainActivity.this);
                musicRecyclerView.setAdapter(musicAdapter);

                Toast.makeText(this, musicAdapter.getItemCount() + " Songs found", Toast.LENGTH_SHORT).show();
                Log.d("FindSongs", musicAdapter.getItemCount() + " Songs found");

                cursor.close();
            }
        } catch (Exception e) {
            Log.e("FindSongs error", "Error: " + e.getMessage());
        }
    }

    @Override
    public void onChanged(int position) {
        player.stop();
        player.clearMediaItems();

        playingPosition = position;
        MusicList firstItem = musicLists.get(position);
        MediaItem mediaItem = firstItem.getMediaItem();
        player.addMediaItem(mediaItem);

        for(int i = position + 1; i < musicAdapter.getItemCount() + 1; i++) {
            if(i == musicAdapter.getItemCount()) i = 0;
            if(i == position) break;

            MediaItem nextMediaItem = musicLists.get(i).getMediaItem();
            player.addMediaItem(nextMediaItem);
        }

        updateUi(firstItem);
        play();
    }

    void seekWithTheBar() {
        playerSeekbar.addListener(new TimeBar.OnScrubListener() {
            @Override
            public void onScrubStart(TimeBar timeBar, long position) {
                //player.pause();
            }

            @Override
            public void onScrubMove(TimeBar timeBar, long position) {
                player.seekTo(position);
            }

            @Override
            public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
                //play();
            }
        });
    }

    void updateUi(MusicList currentMusicList) {
        //playerSeekbar.setDuration(player.getDuration());
        endTime.setText(currentMusicList.getDuration());
        startTime.setText(generateTime(player.getCurrentPosition()));
        currentMusicList.setPlaying(true);
        musicAdapter.notifyDataSetChanged();
    }

    String generateTime(String duration) {
        long time = Long.parseLong(duration);
        long seconds = time / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;

        String stringedMinutes = Long.toString(minutes);
        String stringedSeconds = Long.toString(seconds);

        if(seconds < 10) return (stringedMinutes + ":0" + stringedSeconds);
        else return (stringedMinutes + ":" + stringedSeconds);
    }

    String generateTime(long duration) {
        long seconds = duration / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;

        String stringedMinutes = Long.toString(minutes);
        String stringedSeconds = Long.toString(seconds);

        if(seconds < 10) return (stringedMinutes + ":0" + stringedSeconds);
        else return (stringedMinutes + ":" + stringedSeconds);
    }

    void play() {
        player.prepare();
        player.play();
        playPauseImg.setImageResource(R.drawable.btn_pause);
    }

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
                        if (permissionDeniedResponse.isPermanentlyDenied()) {
                            Toast.makeText(getApplicationContext(), "If you don't allow storage access we can't get your music", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
}