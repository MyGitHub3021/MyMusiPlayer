package com.admin.myplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;

/**
 * Created by Administrator on 2016/5/14.
 */
public class MusicService extends Service implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    public void onCreate() {

        //设置监听器
        mediaPlayer.setOnErrorListener(this);//设置资源的时候出错了
        mediaPlayer.setOnPreparedListener(this);//准备好播放
        mediaPlayer.setOnCompletionListener(this);//播放完成
        super.onCreate();

    }

    //播放音乐
    public void play(String path) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);//设置歌曲路径
            mediaPlayer.prepare();//准备播放
            mediaPlayer.start();//开始播放
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //暂停
    public void pause(){
        if(mediaPlayer!=null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }
    //继续播放
    public void continuePlay(){
        if(mediaPlayer!=null && !mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
    }
    //停止播放
    public void stop(){
        if(mediaPlayer!=null){
            mediaPlayer.stop();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String option = intent.getStringExtra("option");
        if("play".equals(option)){
            String path = intent.getStringExtra("path");
            play(path);
        }else if("pause".equals(option)){
            pause();
        }else if("continueplay".equals(option)){
            continuePlay();
        }else if("stop".equals(option)) {
            stop();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }
}
