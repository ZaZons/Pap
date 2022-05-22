package com.example.mplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {

    private List<MusicList> list;
    private final ChangeSongListener changeSongListener;

    public MusicAdapter(List<MusicList> list, Context context) {
        this.list = list;
        this.changeSongListener = ((ChangeSongListener)context);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.music_adapter_layout, null));
    }

    @Override
    public void onBindViewHolder(MusicAdapter.MyViewHolder holder, int position) {
        MusicList list2 = list.get(position);

        holder.title.setText(list2.getTitle());
        holder.artist.setText(list2.getArtist());
        holder.musicDuration.setText(list2.getDuration());

        if(list2.isPlaying())
            holder.rootLayout.setBackgroundResource(R.drawable.bg_purple);
        else
            holder.rootLayout.setBackgroundResource(R.drawable.bg_blue);

        holder.rootLayout.setOnClickListener(v -> changeSongListener.onChanged(list2.getId()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private final RelativeLayout rootLayout;
        private final TextView title;
        private final TextView artist;
        private final TextView musicDuration;
        //private final ImageView contextMenu;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            rootLayout = itemView.findViewById(R.id.rootLayout);
            title = itemView.findViewById(R.id.musicTitle);
            artist = itemView.findViewById(R.id.musicArtist);
            musicDuration = itemView.findViewById(R.id.musicDuration);
            /*contextMenu = itemView.findViewById(R.id.contextMenu);
            contextMenu.setOnClickListener(v -> {
                contextMenu.setOnCreateContextMenuListener(this);
            });*/
        }
    }

    public void filter(List<MusicList> filteredList) {
        list = filteredList;
        notifyDataSetChanged();
    }

        /*
        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.add(this.getAbsoluteAdapterPosition(), Menu.NONE, 0, "Play now");
            contextMenu.add(this.getAbsoluteAdapterPosition(), Menu.NONE, 0, "Play next");
        }*/

    /*
    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener {

        ImageView contextMenu;
        TextView fileName;

        public ViewHolder(View v) {
            super(v);
            contextMenu = v.findViewById(R.id.contextMenu);
            v.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            //menuInfo is null
            contextMenu.setHeaderTitle("Select The Action");
            contextMenu.add(0, view.getId(), 0, "Call");//groupId, itemId, order, title
            contextMenu.add(0, view.getId(), 0, "SMS");
        }
    }*/
}
