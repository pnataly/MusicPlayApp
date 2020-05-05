package com.example.playmedia.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.playmedia.model.Song;
import com.example.playmedia.util.Util;
import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper{

    private Context context;

    public DatabaseHandler(@Nullable Context context) {
        super(context, Util.DATABASE_NAME, null, Util.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(Util.CREATE_FAV_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Util.TABLE_SONGS);

        onCreate(db);
    }

    public void addSongFav(Song song) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Util.COLUMN_TITLE, song.getTitle());
        values.put(Util.COLUMN_SUBTITLE, song.getSubTitle());
        values.put(Util.COLUMN_PATH, song.getPath());
        values.put(Util.COLUMN_TIME, song.getTime());

        db.insertWithOnConflict(Util.TABLE_SONGS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public ArrayList<Song> getAllFavorites() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Song> songList = new ArrayList<>();

        String SELECT_ALL = "SELECT * FROM " + Util.TABLE_SONGS;
        Cursor cursor = db.rawQuery(SELECT_ALL, null);

        if(cursor.moveToNext()){
            do {
                Song song = new Song();

                song.setTitle(cursor.getString(cursor.getColumnIndex(Util.COLUMN_TITLE)));
                song.setSubTitle(cursor.getString(cursor.getColumnIndex(Util.COLUMN_SUBTITLE)));
                song.setPath(cursor.getString(cursor.getColumnIndex(Util.COLUMN_PATH)));
                song.setTime(cursor.getString(cursor.getColumnIndex(Util.COLUMN_TIME)));

                songList.add(song);
            } while (cursor.moveToNext());
        }

        return songList;
    }

    public Song getSong(String songPath){
        SQLiteDatabase db = this.getReadableDatabase();
        Song song = new Song();

        String SELECT_ALL = "SELECT * FROM " + Util.TABLE_SONGS;
        Cursor cursor = db.rawQuery(SELECT_ALL, null);

        if(cursor != null){
            cursor.moveToFirst();
        }
        if(cursor != null){
            song.setTitle(cursor.getString(cursor.getColumnIndex(Util.COLUMN_TITLE)));
            song.setSubTitle(cursor.getString(cursor.getColumnIndex(Util.COLUMN_SUBTITLE)));
            song.setPath(cursor.getString(cursor.getColumnIndex(Util.COLUMN_PATH)));
            song.setTime(cursor.getString(cursor.getColumnIndex(Util.COLUMN_TIME)));

            return song;
        }
        return null;
    }

    public boolean isSongExist(String songPath){
        SQLiteDatabase db = this.getReadableDatabase();

        String whereClause = "path = ?";
        String[] whereArgs = new String[]{songPath};

        Cursor cursor = db.query(Util.TABLE_SONGS, null, whereClause, whereArgs, null, null, null);
        boolean exist = (cursor.getCount() > 0);
        cursor.close();
        return exist;
    }

    public void removeSong(Song song) {
        SQLiteDatabase db = this.getWritableDatabase();

        song.setFavFlag(0);

        String whereClause = Util.COLUMN_PATH + "=?";
        String[] whereArgs = new String[]{song.getPath()};
        db.delete(Util.TABLE_SONGS, whereClause, whereArgs);
    }

    public int getFavCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String SELECT_ALL = "SELECT * FROM " + Util.TABLE_SONGS;

        Cursor cursor = db.rawQuery(SELECT_ALL, null);

        return cursor.getCount();
    }

}






















