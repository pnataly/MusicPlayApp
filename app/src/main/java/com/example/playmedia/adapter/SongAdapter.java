package com.example.playmedia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.playmedia.R;
import com.example.playmedia.model.Song;

import java.util.ArrayList;

public class SongAdapter extends ArrayAdapter<Song> implements Filterable {

    private Context mContext;
    private ArrayList<Song> songList = new ArrayList<>();

    public SongAdapter(Context mContext, ArrayList<Song> songList) {
        super(mContext, 0, songList);
        this.mContext = mContext;
        this.songList = songList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.playlist_items, parent, false);
        }
        Song currentSong = songList.get(position);
        TextView title = listItem.findViewById(R.id.music_name);
        TextView subtitle = listItem.findViewById(R.id.music_subtitle);
        TextView time = listItem.findViewById(R.id.music_time);
        title.setText(currentSong.getTitle());
        subtitle.setText(currentSong.getSubTitle());
        time.setText(currentSong.getTime());

        return listItem;
    }
}
