package com.admin.myplayer.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.admin.myplayer.R;
import com.admin.myplayer.adapter.MyAdapter;
import com.admin.myplayer.service.MusicService;
import com.admin.myplayer.util.MediaUtil;
import com.admin.myplayer.view.ScrollableViewGroup;

public class MainActivity extends Activity implements View.OnClickListener {
    private TextView tv_duration,tv_totalduration,tv_minilrc;
    private ImageView iv_bottom_play,iv_bottom_model;
    private SeekBar sk_duration;
    private ScrollableViewGroup svg;
    private ImageButton ib_bottom_play,ib_bottom_last,ib_bottom_next,ib_bottom_model,ib_bottom_update,
                    ib_top_list,ib_top_lrc,ib_top_play,ib_top_volumn;
    private ListView lv_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initListener();
    }
    private int[] topArr = {R.id.ib_top_play,R.id.ib_top_list,R.id.ib_top_lrc,R.id.ib_top_volumn};
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


    }

    private void initView() {
        tv_duration= (TextView) findViewById(R.id.tv_duration);
        tv_totalduration= (TextView) findViewById(R.id.tv_totalduration);
        tv_minilrc= (TextView) findViewById(R.id.tv_minilrc);
        iv_bottom_model= (ImageView) findViewById(R.id.iv_bottom_model);
        iv_bottom_play= (ImageView) findViewById(R.id.iv_bottom_play);
        sk_duration= (SeekBar) findViewById(R.id.sk_duration);
        svg= (ScrollableViewGroup) findViewById(R.id.svg);
        ib_bottom_play= (ImageButton) findViewById(R.id.ib_bottom_play);
        ib_bottom_last= (ImageButton) findViewById(R.id.ib_bottom_last);
        ib_bottom_next= (ImageButton) findViewById(R.id.ib_bottom_next);
        ib_bottom_model= (ImageButton) findViewById(R.id.ib_bottom_model);
        ib_bottom_update= (ImageButton) findViewById(R.id.ib_bottom_update);
        ib_top_list= (ImageButton) findViewById(R.id.ib_top_list);
        ib_top_lrc= (ImageButton) findViewById(R.id.ib_top_lrc);
        ib_top_play= (ImageButton) findViewById(R.id.ib_top_play);
        ib_top_volumn= (ImageButton) findViewById(R.id.ib_top_volumn);
        lv_list= (ListView) findViewById(R.id.lv_list);

        ib_top_play.setSelected(true);

    }
    private void initData(){
        MediaUtil.getSonglist(this);
        lv_list.setAdapter(new MyAdapter(this));
    }
    private void setTopSelect(int selectedId){
        ib_top_play.setSelected(false);
        ib_top_list.setSelected(false);
        ib_top_lrc.setSelected(false);
        ib_top_volumn.setSelected(false);

        findViewById(selectedId).setSelected(true);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
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
                Intent service = new Intent(MainActivity.this, MusicService.class);
                service.putExtra("option","play");
                service.putExtra("path",MediaUtil.songlist.get(0).getPath());
                startService(service);
                iv_bottom_play.setImageResource(R.drawable.appwidget_pause);
        }
    }
}
