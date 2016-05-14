package com.admin.myplayer.bean;

/**
 * Created by Administrator on 2016/5/14.
 */
public class Music {
    private String title; //歌曲名
    private String artist; //歌手
    private String path;

    public Music() {
    }

    public Music(String title, String artist, String path) {
        this.title = title;
        this.path = path;
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Music{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
