package com.example.playmedia;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.example.playmedia.adapter.SongAdapter;
import com.example.playmedia.data.DatabaseHandler;
import com.example.playmedia.model.Song;

import java.util.ArrayList;


public class FavoritesFragment extends ListFragment {

    private DatabaseHandler databaseHandler;

    public ArrayList<Song> songsList;
    public ArrayList<Song> searchList;
    private ListView listView;
    private boolean isSearch = false;

    private SendData sendData;

    public interface SendData {
        void onDataPass(String name, String path);
        void allSongList(ArrayList<Song> songList, int position);
        String queryText();
    }


    public FavoritesFragment() {
        // Required empty public constructor
    }


    public static FavoritesFragment getInstance(int position) {
        FavoritesFragment fragment = new FavoritesFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listView = view.findViewById(R.id.favorite_list);
        setContent();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        databaseHandler = new DatabaseHandler(context);
        sendData = (SendData) context;
    }

    public void setContent(){
        songsList = new ArrayList<>();
        searchList = new ArrayList<>();
        songsList = databaseHandler.getAllFavorites();

        SongAdapter adapter = new SongAdapter(getContext(), songsList);
        if(!sendData.queryText().equals("")){
            adapter = onQueryTextChange();
            adapter.notifyDataSetChanged();
            isSearch = true;
        } else {
            isSearch = false;
        }
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isSearch){
                    sendData.onDataPass(searchList.get(position).getTitle(), searchList.get(position).getPath());
                    sendData.allSongList(searchList, position);
                } else{
                    sendData.onDataPass(songsList.get(position).getTitle(), songsList.get(position).getPath());
                    sendData.allSongList(songsList, position);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                deleteOption(position);
                return true;
            }
        });

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

    private void deleteOption(int position) {

        showDialog(songsList.get(position), position);
    }


    private void showDialog(final Song song, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.delete))
                .setMessage(getString(R.string.delete_text))
                .setCancelable(true)
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        songsList.get(position).setFavFlag(0);
                        databaseHandler.removeSong(song);
                        setContent();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
