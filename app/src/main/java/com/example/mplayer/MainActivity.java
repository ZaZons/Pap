package com.example.mplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerControlView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.Manifest;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements ChangeSongListener {

    //ListView listView;

    CardView playPauseCard;
    ImageView nextBtn;
    ImageView previousBtn;
    LinearLayout searchBtn;
    LinearLayout menuBtn;

    private boolean isPlaying = false;
    private RecyclerView musicRecyclerView;
    private MediaPlayer mediaPlayer;
    private TextView endTime, startTime;
    private SeekBar playerSeekbar;
    private ImageView playPauseImg;
    private StyledPlayerControlView musicView;
    ExoPlayer player;

    private final List<MusicList> musicLists = new ArrayList<>();
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
        endTime = findViewById(R.id.startTime);
        startTime = findViewById(R.id.endTime);
        playerSeekbar = findViewById(R.id.playerSeekBar);

        musicRecyclerView.setHasFixedSize(false);
        musicRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        musicView = findViewById(R.id.playerView);
        player = new ExoPlayer.Builder(this).build();
        musicView.setPlayer(player);

        requestPermission();
    }

    void findSongs() {
        try {
            ContentResolver contentResolver = getApplicationContext().getContentResolver();

            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String sortOrder = MediaStore.Audio.Media.DATE_ADDED;
            String[] selectionArgs = {"%.mp3%", "%.wav", "%.m4a"};
            String selection = MediaStore.Audio.AudioColumns.IS_MUSIC;
            String[] projection = {MediaStore.Audio.Media.DATE_ADDED};

            Cursor cursor = contentResolver.query(uri, null, null, null, sortOrder);

            if(cursor == null) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            } else if (!cursor.moveToNext()) {
                Toast.makeText(this, "No music found", Toast.LENGTH_SHORT).show();
            } else {
                cursor.moveToFirst();
                int x = 0;
                do {
                    x++;
                    long cursorId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    Uri musicFileUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursorId);
                    final String getMusicFileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    final String getArtistName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    String getDuration = "00:00";
                    getDuration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION));

                    final MusicList musicList = new MusicList(getMusicFileName, getArtistName, getDuration, false, musicFileUri);
                    musicLists.add(musicList);
                    if(x == 104) break;
                } while(cursor.moveToNext());
                Toast.makeText(this, x + " Songs found", Toast.LENGTH_SHORT).show();
                Log.d("FindSongs", x + " Songs found");
                musicRecyclerView.setAdapter(new MusicAdapter(musicLists, MainActivity.this));
            }
            assert cursor != null;
        } catch (Exception e) {
            Log.e("FindSongs error", "Error: " + e.getMessage());
        }
    }

    @Override
    public void onChanged(int position) {
        MusicList musicItem = musicLists.get(position);
        MediaItem mediaItem = MediaItem.fromUri(musicItem.getMusicFile());
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
        String generateDuration = musicItem.getDuration();
        playerSeekbar.setMax(1);
        playPauseImg.setImageResource(R.drawable.btn_pause);
        endTime.setText(generateDuration);
        isPlaying = true;
        /*


        mediaPlayer.setOnPreparedListener(mediaPlayer -> {
            final int getTotalDuration = mediaPlayer.getDuration();



            //mediaPlayer.start();

        });*/
    }

    void requestPermission()     {
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