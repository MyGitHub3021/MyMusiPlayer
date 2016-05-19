package com.admin.myplayer.util;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.admin.myplayer.bean.Music;
import com.admin.myplayer.config.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/14.
 */
public class MediaUtil {
    public static List<Music> songlist = new ArrayList<Music>();
    public static int CURSTATE = Constants.STATE_STOP;
    public static int POSITION = 0;

    //加载本地音乐
    public static void getSonglist(Context context) {
        songlist.clear();
        ContentResolver cr = context.getContentResolver();
        String[] projection = {MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DATA};
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor c = cr.query(uri, projection, null, null, null);
//        Music music = new Music();
        while (c.moveToNext()) {

//            music.setTitle(c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE)));
//            music.setArtist(c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
//            music.setPath(c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA)));
            String title = c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String artist = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String path = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
            Music music = new Music(title, artist, path);
            songlist.add(music);
        }

    }

    public static String durationtoStr(int duration) {
        String result = "";
        int i = duration / 1000;
        int min = i / 60;
        int sec = i % 60;
        if (min > 9) {
            if (sec > 9) {
                result = min + ":" + sec;
            } else {
                result = min + ":0" + sec;
            }
        } else {
            if (sec > 9) {
                result = "0" + min + ":" + sec;
            } else {
                result = "0" + min + ":0" + sec;
            }
        }
        return result;
    }
}
