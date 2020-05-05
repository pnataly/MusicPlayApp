package com.example.playmedia.model;

public class Song {

    private String title;
    private String subTitle;
    private String path;
    private String time;
    private int favFlag;


    public Song() { }

    public Song(String title, String subTitle, String path, String time) {
        this.title = title;
        this.subTitle = subTitle;
        this.path = path;
        this.time = time;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getFavFlag() {
        return favFlag;
    }

    public void setFavFlag(int favFlag) {
        this.favFlag = favFlag;
    }

}
