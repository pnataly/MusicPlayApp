package com.example.playmedia.util;

public class Util {

    public static final String DATABASE_NAME = "favorites_db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_SONGS           = "favorites";
    public static final String COLUMN_ID             = "songID";
    public static final String COLUMN_TITLE          = "title";
    public static final String COLUMN_SUBTITLE       = "subtitle";
    public static final String COLUMN_PATH           = "path";
    public static final String COLUMN_TIME           = "duration";

    public static final String CREATE_FAV_TABLE = "CREATE TABLE " + TABLE_SONGS + " ("
            + COLUMN_ID + " INTEGER,"
            + COLUMN_TITLE + " TEXT,"
            + COLUMN_SUBTITLE + " TEXT,"
            + COLUMN_PATH + " TEXT PRIMARY KEY,"
            + COLUMN_TIME + " TEXT);";
}

