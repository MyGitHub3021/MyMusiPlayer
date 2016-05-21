package com.admin.myplayer.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.admin.myplayer.R;
import com.admin.myplayer.adapter.MyAdapter;
import com.admin.myplayer.util.LrcUtil;
import com.admin.myplayer.config.Constants;
import com.admin.myplayer.service.MusicService;
import com.admin.myplayer.util.MediaUtil;
import com.admin.myplayer.view.LyricShow;
import com.admin.myplayer.view.ScrollableViewGroup;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.Random;

public class MainActivity extends Activity implements View.OnClickListener {
    private TextView tv_duration, tv_totalduration, tv_minilrc;
    private ImageView iv_bottom_play, iv_bottom_model;
    private SeekBar sk_duration;
    private ScrollableViewGroup svg;
    private LyricShow ls;
    MainActivity instance;
    MyBroadcastReceiver receiver;
    LrcUtil mlrcutil;
    MyAdapter adapter;

    private ImageButton ib_bottom_play, ib_bottom_last, ib_bottom_next,
            ib_bottom_model, ib_bottom_update,
            ib_top_list, ib_top_lrc, ib_top_play, ib_top_volumn;
    private ListView lv_list;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MSG_PREPARED:
                    int currentPosition = msg.arg1;
                    int totalDuration = msg.arg2;
                    tv_duration.setText(MediaUtil.durationtoStr(currentPosition));
                    tv_totalduration.setText(MediaUtil.durationtoStr(totalDuration));
                    sk_duration.setMax(totalDuration);
                    sk_duration.setProgress(currentPosition);
                    if (mlrcutil == null) {
                        mlrcutil = new LrcUtil(instance);
                    }
                    //序列化歌词
                    File f = MediaUtil.getlrcFile(MediaUtil.songlist.get(MediaUtil.POSITION).getPath());
                    mlrcutil.ReadLRC(f);
                    //使用刷新lrc功能
                    mlrcutil.RefreshLRC(currentPosition);

                    //设置集合
                    ls.SetTimeLrc(mlrcutil.getLrcList());
                    //更新滚动歌词
                    ls.SetNowPlayIndex(currentPosition);
                    //
                    break;
                case Constants.MSG_COMPLETION:
                    //歌曲播放完了,根据歌曲当前的播放模式做相应的处理
                    if (MediaUtil.CURMODEL == Constants.MODEL_NORMAL) {
                        if (MediaUtil.POSITION < MediaUtil.songlist.size() - 1) {
                            changeColorWhite();
                            MediaUtil.POSITION++;
                            changeColorGreen();
                            startMediaService("play", MediaUtil.songlist.get(MediaUtil.POSITION).getPath());
                        } else {
                            startMediaService("stop");
                        }

                    } else if (MediaUtil.CURMODEL == Constants.MODEL_RANDOM) {
                        Random random = new Random();
                        int pos = random.nextInt(MediaUtil.songlist.size());
                        changeColorWhite();
                        MediaUtil.POSITION = pos;
                        changeColorGreen();
                        startMediaService("play", MediaUtil.songlist.get(MediaUtil.POSITION).getPath());
                    } else if (MediaUtil.CURMODEL == Constants.MODEL_REPEAT) {
                        if (MediaUtil.POSITION < MediaUtil.songlist.size() - 1) {
                            changeColorWhite();
                            MediaUtil.POSITION++;
                            changeColorGreen();
                            startMediaService("play", MediaUtil.songlist.get(MediaUtil.POSITION).getPath());
                        } else {
                            changeColorWhite();
                            MediaUtil.POSITION = 0;
                            changeColorWhite();
                            startMediaService("play", MediaUtil.songlist.get(MediaUtil.POSITION).getPath());
                        }

                    } else if (MediaUtil.CURMODEL == Constants.MODEL_SINGLE) {
                        //单曲循环,一直播放当前的同一首歌曲
                        startMediaService("play", MediaUtil.songlist.get(MediaUtil.POSITION).getPath());
                    }

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        receiver = new MyBroadcastReceiver();
        initView();
        initData();
        initListener();
    }

    private int[] topArr = {R.id.ib_top_play, R.id.ib_top_list, R.id.ib_top_lrc, R.id.ib_top_volumn};

    private void initListener() {
        ib_bottom_play.setOnClickListener(this);
        ib_bottom_last.setOnClickListener(this);
        ib_bottom_next.setOnClickListener(this);
        ib_bottom_model.setOnClickListener(this);
        ib_bottom_update.setOnClickListener(this);
        ib_top_list.setOnClickListener(this);
        ib_top_lrc.setOnClickListener(this);
        ib_top_play.setOnClickListener(this);
        ib_top_volumn.setOnClickListener(this);

        svg.setOnCurrentViewChangedListener(new ScrollableViewGroup.OnCurrentViewChangedListener() {
            @Override
            public void onCurrentViewChanged(View view, int currentview) {
                setTopSelect(topArr[currentview]);
            }
        });
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //修改curposition
                changeColorWhite();
                MediaUtil.POSITION = i;
                changeColorGreen();
                startMediaService("play", MediaUtil.songlist.get(MediaUtil.POSITION).getPath());
                iv_bottom_play.setImageResource(R.drawable.appwidget_pause);
            }
        });
        sk_duration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //进度改变
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //触摸到按钮,开始滑动
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {//停止拖拽
                sk_duration.setProgress(seekBar.getProgress());
                startMediaService("进度", seekBar.getProgress());
                //音乐播放器,跳转到指定的位置播放
            }
        });


    }

    private void initView() {
        tv_duration = (TextView) findViewById(R.id.tv_duration);
        tv_totalduration = (TextView) findViewById(R.id.tv_totalduration);
        tv_minilrc = (TextView) findViewById(R.id.tv_minilrc);
        iv_bottom_model = (ImageView) findViewById(R.id.iv_bottom_model);
        iv_bottom_play = (ImageView) findViewById(R.id.iv_bottom_play);
        sk_duration = (SeekBar) findViewById(R.id.sk_duration);
        svg = (ScrollableViewGroup) findViewById(R.id.svg);
        ib_bottom_play = (ImageButton) findViewById(R.id.ib_bottom_play);
        ib_bottom_last = (ImageButton) findViewById(R.id.ib_bottom_last);
        ib_bottom_next = (ImageButton) findViewById(R.id.ib_bottom_next);
        ib_bottom_model = (ImageButton) findViewById(R.id.ib_bottom_model);
        ib_bottom_update = (ImageButton) findViewById(R.id.ib_bottom_update);
        ib_top_list = (ImageButton) findViewById(R.id.ib_top_list);
        ib_top_lrc = (ImageButton) findViewById(R.id.ib_top_lrc);
        ib_top_play = (ImageButton) findViewById(R.id.ib_top_play);
        ib_top_volumn = (ImageButton) findViewById(R.id.ib_top_volumn);
        lv_list = (ListView) findViewById(R.id.lv_list);
        ls = (LyricShow) findViewById(R.id.ls_lrc);
        ib_top_play.setSelected(true);

    }

    private void initData() {
        MediaUtil.getSonglist(this);
        lv_list.setAdapter(new MyAdapter(this));
    }

    private void setTopSelect(int selectedId) {
        ib_top_play.setSelected(false);
        ib_top_list.setSelected(false);
        ib_top_lrc.setSelected(false);
        ib_top_volumn.setSelected(false);

        findViewById(selectedId).setSelected(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_top_play:
                svg.setCurrentView(0);
                break;
            case R.id.ib_top_list:
                svg.setCurrentView(1);
                break;
            case R.id.ib_top_lrc:
                svg.setCurrentView(2);
                break;
            case R.id.ib_bottom_play: //底部播放按钮
                if (MediaUtil.CURSTATE == Constants.STATE_STOP) {//默认状态为stop
                    startMediaService("play", MediaUtil.songlist.get(MediaUtil.POSITION).getPath());
                    iv_bottom_play.setImageResource(R.drawable.appwidget_pause);
                } else if (MediaUtil.CURSTATE == Constants.STATE_PLAY) {
                    startMediaService("pause");
                    iv_bottom_play.setImageResource(R.drawable.img_playback_bt_play);
                } else if (MediaUtil.CURSTATE == Constants.STATE_PAUSE) {
                    startMediaService("continueplay");
                    iv_bottom_play.setImageResource(R.drawable.appwidget_pause);
                }
                break;
            case R.id.ib_bottom_last:
                if (MediaUtil.POSITION <= 0) {
                    Toast.makeText(this, "this is the first song", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    changeColorWhite();
                    MediaUtil.POSITION--;
                    changeColorGreen();
                    startMediaService("play", MediaUtil.songlist.get(MediaUtil.POSITION).getPath());
                    iv_bottom_play.setImageResource(R.drawable.appwidget_pause);
                }
                break;
            case R.id.ib_bottom_next:
                changeColorWhite();
                if (MediaUtil.POSITION >= MediaUtil.songlist.size() - 1) {
//                    Toast.makeText(this, "this is the last song", Toast.LENGTH_SHORT).show();
                    MediaUtil.POSITION = 0;
                } else {
                    //首先找到之前绿色的textview,然后给他变成白色
                    //MediaUtils.POSITION+1,播放下一曲
                    MediaUtil.POSITION++;
                }
                changeColorGreen();
                startMediaService("play", MediaUtil.songlist.get(MediaUtil.POSITION).getPath());
                iv_bottom_play.setImageResource(R.drawable.appwidget_pause);

//                if (MediaUtil.POSITION >= MediaUtil.songlist.size() - 1) {
//                    Toast.makeText(this, "this is the last song", Toast.LENGTH_SHORT).show();
//                } else {
//                    //首先找到之前绿色的textview,然后给他变成白色
//                    changeColorWhite();
//                    //MediaUtils.POSITION+1,播放下一曲
//                    MediaUtil.POSITION++;
//                    changeColorGreen();
//                    startMediaService("play", MediaUtil.songlist.get(MediaUtil.POSITION).getPath());
//                    iv_bottom_play.setImageResource(R.drawable.appwidget_pause);
//                }
                break;
            case R.id.ib_bottom_model:
                if (MediaUtil.CURMODEL == Constants.MODEL_NORMAL) {
                    //如果当前是顺序播放,点击后模式变成随机播放
                    MediaUtil.CURMODEL = Constants.MODEL_RANDOM;
                    //更新ui
                    iv_bottom_model.setImageResource(R.drawable.icon_playmode_shuffle);
                    Toast.makeText(this, "随机播放", Toast.LENGTH_SHORT).show();
                } else if (MediaUtil.CURMODEL == Constants.MODEL_RANDOM) {
                    //如果当前是随机播放,点击后模式变成重复播放
                    MediaUtil.CURMODEL = Constants.MODEL_REPEAT;
                    iv_bottom_model.setImageResource(R.drawable.icon_playmode_repeat);
                    Toast.makeText(this, "重复播放", Toast.LENGTH_SHORT).show();
                } else if (MediaUtil.CURMODEL == Constants.MODEL_REPEAT) {
                    //如果当前是重复播放,点击后模式变成单曲播放
                    MediaUtil.CURMODEL = Constants.MODEL_SINGLE;
                    iv_bottom_model.setImageResource(R.drawable.icon_playmode_single);
                    Toast.makeText(this, "单曲播放", Toast.LENGTH_SHORT).show();
                } else if (MediaUtil.CURMODEL == Constants.MODEL_SINGLE) {
                    //如果当前为单曲播放,点击后转变为顺序播放
                    MediaUtil.CURMODEL = Constants.MODEL_NORMAL;
                    iv_bottom_model.setImageResource(R.drawable.icon_playmode_normal);
                    Toast.makeText(this, "顺序播放", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ib_bottom_update:
                refresh();
                break;
            case R.id.ib_top_volumn:
                AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume / 2, AudioManager.FLAG_PLAY_SOUND);
                break;
        }
    }

    public void changeColorWhite() {
        TextView tv = (TextView) lv_list.findViewWithTag(MediaUtil.POSITION);
        if (tv != null) {
            tv.setTextColor(Color.WHITE);
        }
    }

    public void changeColorGreen() {
        TextView tv = (TextView) lv_list.findViewWithTag(MediaUtil.POSITION);
        //再把字体颜色改变过来
        if (tv != null) {
            tv.setTextColor(Color.GREEN);
        }
    }

    public void startMediaService(String option) {
        Intent service = new Intent(this, MusicService.class);
        service.putExtra("messenger", new Messenger(handler));
        service.putExtra("option", option);
        startService(service);
    }

    public void startMediaService(String option, String path) {
        Intent service = new Intent(this, MusicService.class);
        service.putExtra("messenger", new Messenger(handler));
        service.putExtra("option", option);
        service.putExtra("path", path);
        startService(service);
    }

    public void startMediaService(String option, int progress) {
        Intent service = new Intent(this, MusicService.class);
        service.putExtra("messenger", new Messenger(handler));
        service.putExtra("option", option);
        service.putExtra("progress", progress);
        startService(service);
    }

    public void setMiniLrc(String lrcString) {
        tv_minilrc.setText(lrcString);
    }

    public void refresh() {
        //接收系统扫描结束的广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        filter.addDataScheme("file");
        registerReceiver(receiver, filter);
        //发送广播让系统扫描更新媒体库
        Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
        intent.setData(Uri.parse("file://" + Environment.getExternalStorageDirectory()));
        sendBroadcast(intent);
    }

    class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            unregisterReceiver(receiver);
            //重新更新songlist更新歌曲列表需要查询数据库,这是个耗时操作,所以不能放在onReceive方法中执行
            new MyScanTask().execute();
            adapter.notifyDataSetChanged();
        }
    }

    class MyScanTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog;

        @Override
        protected Void doInBackground(Void... voids) {
            MediaUtil.getSonglist(MainActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //方法执行之后
            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //方法执行之前
            dialog = ProgressDialog.show(MainActivity.this, "提示", "努力加载中");
        }
    }
}
