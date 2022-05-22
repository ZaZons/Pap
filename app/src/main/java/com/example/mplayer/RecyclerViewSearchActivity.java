package com.example.mplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewSearchActivity extends AppCompatActivity {

    MusicAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view_search);

        EditText searchText = findViewById(R.id.searchText);
        searchText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });
    }

    void filter(String text) {
        List<MusicList> searchMusicList = new ArrayList<>();

        for(MusicList item : MainActivity.getList()) {
            if(item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                searchMusicList.add(item);
            }
        }

    }
}