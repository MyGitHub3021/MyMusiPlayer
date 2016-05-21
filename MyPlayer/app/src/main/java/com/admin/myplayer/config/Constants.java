package com.admin.myplayer.config;

/**
 * Created by Administrator on 2016/5/16.
 */
public class Constants {
    public static final int STATE_STOP = 1001;
    public static final int STATE_PLAY = 1002;
    public static final int STATE_PAUSE = 1003;

    public static final int MSG_PREPARED =  1004;//准备播放
    public static final int MSG_COMPLETION =1005;//播放完成

    public static final int MODEL_NORMAL = 1006;//默认模式,常规模式
    public static final int MODEL_REPEAT = 1007;//重复播放
    public static final int MODEL_SINGLE = 1008;//单曲播放
    public static final int MODEL_RANDOM = 1009;//随机播放
}
