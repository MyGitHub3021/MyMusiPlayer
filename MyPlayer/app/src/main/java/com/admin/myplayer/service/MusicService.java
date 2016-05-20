package com.admin.myplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.admin.myplayer.config.Constants;
import com.admin.myplayer.util.MediaUtil;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/5/14.
 */
public class MusicService extends Service implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private Messenger mMessenger;
    private MediaPlayer mediaPlayer;
    private Timer mtimer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        mediaPlayer = new MediaPlayer();
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
            MediaUtil.CURSTATE = Constants.STATE_PLAY;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //暂停
    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            MediaUtil.CURSTATE = Constants.STATE_PAUSE;
        }
    }

    //继续播放
    public void continuePlay() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            MediaUtil.CURSTATE = Constants.STATE_PLAY;
        }
    }

    //停止播放
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            MediaUtil.CURSTATE = Constants.STATE_STOP;
        }
    }

    //跳转到指定进度
    public  void seekPlay(int progress){
        if(mediaPlayer!=null && mediaPlayer.isPlaying()){
                mediaPlayer.seekTo(progress);
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String option = intent.getStringExtra("option");
        if (mMessenger == null) {
            mMessenger = (Messenger) intent.getExtras().get("messenger");
        }
        if ("play".equals(option)) {
            String path = intent.getStringExtra("path");
            play(path);
        } else if ("pause".equals(option)) {
            pause();
        } else if ("continueplay".equals(option)) {
            continuePlay();
        } else if ("stop".equals(option)) {
            stop();
        } else if("进度".equals(option)){
            int progress = intent.getIntExtra("progress",-1);
            seekPlay(progress);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        Toast.makeText(getApplicationContext(), "资源有问题", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onPrepared(final MediaPlayer mediaPlayer) {
        //告诉Activity当前歌曲总时长
        if (mtimer == null) {
            mtimer = new Timer();
        }
        mtimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    int totalDuration = mediaPlayer.getDuration();
                    Message msg = new Message();
                    msg.what = Constants.MSG_PREPARED;
                    msg.arg1 = currentPosition;
                    msg.arg2 = totalDuration;
                    //发送消息
                    mMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000);

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }
}
