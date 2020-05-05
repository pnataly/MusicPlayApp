package com.example.playmedia;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.playmedia.adapter.ViewPagerAdapter;
import com.example.playmedia.data.DatabaseHandler;
import com.example.playmedia.model.Song;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements AllSongFragment.SendData, FavoritesFragment.SendData{

    private final int MY_PERMISSION_REQUEST = 100;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private TextView currentTime;
    private TextView totalTime;
    private SeekBar seekBar;
    private ImageView playButton;
    private ImageView prevButton;
    private ImageView nextButton;
    private ImageView repeatButton;
    private ImageView shuffleButton;

    private static MediaPlayer mediaPlayer;
    private DatabaseHandler databaseHandler;
    private ListView listView;

    private Menu menu;

    private String searchText = "";
    private int allSongsTime;
    private int currentPosition;
    private ArrayList<Song> songsList;
    private boolean playContinueFlag = false;
    private boolean playlistFlag = false;
    private boolean repeatFlag = false;
    private boolean shuffleFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        databaseHandler = new DatabaseHandler(this);

        setupUI();
        grantedPermission();
    }

    private void setupUI(){

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewPager);

        currentTime = findViewById(R.id.current_time);
        totalTime = findViewById(R.id.total_time);
        seekBar = findViewById(R.id.seekBar);
        playButton = findViewById(R.id.play_button);
        prevButton = findViewById(R.id.prev_button);
        nextButton = findViewById(R.id.next_button);
        repeatButton = findViewById(R.id.repeat);
        shuffleButton = findViewById(R.id.shuffle);
        mediaPlayer = new MediaPlayer();

        handleClicks();
    }

    private void setPagerLayout() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), getContentResolver(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void grantedPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                }
            }
        }
        else {
            setPagerLayout();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                        setPagerLayout();

                    } else {
                        finish();
                    }
                }
        }
    }

    private void handleClicks(){
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(shuffleFlag){

                    Random rand = new Random();
                    int randomPosition = rand.nextInt(songsList.size());
                    currentPosition = randomPosition;
                    listView.setSelection(currentPosition);
                    initMusicPlayer(songsList.get(randomPosition).getTitle(), songsList.get(randomPosition).getPath());

                } else{
                    if(currentPosition < songsList.size() - 1){

                        currentPosition++;

                    } else {
                        currentPosition = 0;
                    }
                    //play the next song;
                    listView.setSelection(currentPosition);
                    initMusicPlayer(songsList.get(currentPosition).getTitle(), songsList.get(currentPosition).getPath());
                }
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shuffleFlag){

                    Random rand = new Random();
                    int randomPosition = rand.nextInt(songsList.size());
                    listView.setSelection(randomPosition);

                    initMusicPlayer(songsList.get(randomPosition).getTitle(), songsList.get(randomPosition).getPath());

                } else {

                    if(currentPosition <= 0) {
                        currentPosition = songsList.size() -1;
                    } else {

                        currentPosition--;
                    }
                    listView.setSelection(currentPosition);
                    initMusicPlayer(songsList.get(currentPosition).getTitle(), songsList.get(currentPosition).getPath());
                }
            }
        });

        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(repeatFlag){
                    repeatButton.setImageResource(R.drawable.ic_no_repeat);
                    mediaPlayer.setLooping(false);
                    repeatFlag = false;
                } else {
                    repeatButton.setImageResource(R.drawable.repeat);
                    mediaPlayer.setLooping(true);
                    repeatFlag = true;
                }
            }
        });

        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shuffleFlag){
                    shuffleFlag = false;
                    shuffleButton.setImageResource(R.drawable.no_shuffle);

                } else {
                    shuffleFlag = true;
                    shuffleButton.setImageResource(R.drawable.shuffle);
                }
            }
        });
    }

    private void initMusicPlayer(String name, String path){

        boolean isExist = databaseHandler.isSongExist(path);
        if(isExist){
            menu.findItem(R.id.menu_fav).setIcon(R.drawable.favorite_full);
        }else{
            menu.findItem(R.id.menu_fav).setIcon(R.drawable.favorite_empty);
        }

        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.reset();
        }

        setTitle(name);
        Uri uri = Uri.parse(path);

        //create media player
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                seekBar.setMax(mediaPlayer.getDuration());
                totalTime.setText(createTimeLabel(mediaPlayer.getDuration()));

                mediaPlayer.start();
                playButton.setImageResource(R.drawable.pause_button);

            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //the current song is finish playing.

                if(shuffleFlag){
                    Random rand = new Random();
                    int randomPosition = rand.nextInt(songsList.size());
                    listView.setSelection(randomPosition);
                    initMusicPlayer(songsList.get(randomPosition).getTitle(), songsList.get(randomPosition).getPath());
                }

                else if(repeatFlag){

                    if(currentPosition < songsList.size()-1){
                        currentPosition++;
                    } else {
                        currentPosition = 0;
                    }
                    listView.setSelection(currentPosition);
                    initMusicPlayer(songsList.get(currentPosition).getTitle(), songsList.get(currentPosition).getPath());

                } else {

                    if(playContinueFlag){
                        if(currentPosition + 1 < songsList.size()-1){
                            currentPosition++;
                            listView.setSelection(currentPosition);                            initMusicPlayer(songsList.get(currentPosition).getTitle(), songsList.get(currentPosition).getPath());

                        } else {
                            currentPosition = 0;
                            Toast.makeText(MainActivity.this, "PlayList Ended", Toast.LENGTH_SHORT).show();
                            playButton.setImageResource(R.drawable.play_button);
                        }
                    }
                }

            }
        });

        //setting up the seekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mediaPlayer.seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //setup the seek bar to change with the song duration
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mediaPlayer != null){
                    try{
                        if(mediaPlayer.isPlaying()){
                            Message message = new Message();
                            message.what = mediaPlayer.getCurrentPosition();
                            handler.sendMessage(message);
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    //create handler
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(@NonNull Message msg) {
            int currentPosition = msg.what;
            currentTime.setText(createTimeLabel(currentPosition));
            seekBar.setProgress(currentPosition);
        }
    };

    public String createTimeLabel(int duration){
        String timeLabel = "";

        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        timeLabel += min + ":";
        if(sec < 10){
            timeLabel += "0";
        }
        timeLabel += sec;

        return timeLabel;
    }

    private void play(){

        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            playButton.setImageResource(R.drawable.play_button);

        } else{
            mediaPlayer.start();
            playButton.setImageResource(R.drawable.pause_button);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchText = newText;
                searchText.toLowerCase();
                setPagerLayout();
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_search:
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.menu_fav:

                if(databaseHandler.isSongExist(songsList.get(currentPosition).getPath())){
                    songsList.get(currentPosition).setFavFlag(0);
                    item.setIcon(R.drawable.favorite_empty);
                    databaseHandler.removeSong(songsList.get(currentPosition));

                } else {
                    databaseHandler.addSongFav(songsList.get(currentPosition));
                    item.setIcon(R.drawable.favorite_full);
                }

                setPagerLayout();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDataPass(String name, String path) {
        initMusicPlayer(name, path);
    }

    @Override
    public void getAllSongsTime(int time) {
        this.allSongsTime = time;
    }

    @Override
    public void handleView(ListView list) {
        this.listView = list;
    }

    @Override
    public void allSongList(ArrayList<Song> songsList, int position) {
        this.songsList = songsList;
        this.currentPosition = position;
        this.playlistFlag = songsList.size() -1 == allSongsTime;
        this.playContinueFlag = !playlistFlag;
    }

    @Override
    public String queryText() {
        return searchText.toLowerCase();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

}
