package com.example.playmedia;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;


import com.example.playmedia.adapter.SongAdapter;
import com.example.playmedia.data.DatabaseHandler;
import com.example.playmedia.model.Song;

import java.util.ArrayList;


public class AllSongFragment extends ListFragment {

    private static ContentResolver contentResolver1;
    private DatabaseHandler databaseHandler;
    private ListView listView;
    private ContentResolver contentResolver;
    private ArrayList<Song> songsList;
    private ArrayList<Song> searchList;
    private boolean isSearch = false;

    private SendData sendData;

    public interface SendData {

        void onDataPass(String name, String path);
        void allSongList(ArrayList<Song> songList, int position);
        String queryText();
        void getAllSongsTime(int time);
        void handleView(ListView list);
    }

    public AllSongFragment() {
        // Required empty public constructor
    }


    public static Fragment getInstance(int position, ContentResolver mcontentResolver) {
        AllSongFragment fragment = new AllSongFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        contentResolver1 = mcontentResolver;
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_song, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        databaseHandler = new DatabaseHandler(context);
        sendData = (SendData) context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listView = view.findViewById(R.id.music_list);
        contentResolver = contentResolver1;
        searchList = new ArrayList<>();
        songsList = getMusic();
        SongAdapter adapter = new SongAdapter(getContext(), songsList);
        if(!sendData.queryText().equals("")){
            adapter = onQueryTextChange();
            adapter.notifyDataSetChanged();
            isSearch = true;
        } else {
            isSearch = false;
        }

        sendData.getAllSongsTime(songsList.size());
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        sendData.handleView(listView);

        //handle click on song in the list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(isSearch){
                    sendData.onDataPass(searchList.get(position).getTitle(), searchList.get(position).getPath());
                    sendData.allSongList(searchList, position);
                    listView.smoothScrollToPosition(position);

                } else{
                    sendData.onDataPass(songsList.get(position).getTitle(), songsList.get(position).getPath());
                    sendData.allSongList(songsList, position);
                }
            }
        });

    }


    private ArrayList<Song> getMusic(){

        ArrayList<Song> allMusicFiles = new ArrayList<>();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst()) {

            do {
                int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int songPath = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                long songTime = songCursor.getLong(songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

                String songDuration = convertSongDuration(songTime);

                Song song = new Song();
                song.setTitle(songCursor.getString(songTitle));
                song.setSubTitle(songCursor.getString(songArtist));
                song.setPath(songCursor.getString(songPath));
                song.setTime(songDuration);

                allMusicFiles.add(song);

            } while (songCursor.moveToNext());
            songCursor.close();
        }
        return allMusicFiles;
    }

    private String convertSongDuration(long time){
        String timeString = "";
        if(String.valueOf(time) != null) {

                long seconds = time/1000;
                long minutes = seconds/60;
                seconds = seconds % 60;

                if(seconds<10) {
                    timeString = minutes + ":0" + seconds;
                } else {
                    timeString = minutes + ":" + seconds;
                }

        } else {
            timeString = "0";
        }
        return timeString;
    }


    public SongAdapter onQueryTextChange() {
        String searchText = sendData.queryText();
        for (Song song : songsList) {
            String title = song.getTitle().toLowerCase();
            if (title.contains(searchText)) {
                searchList.add(song);
            }
        }
        return new SongAdapter(getContext(), searchList);
    }


}
