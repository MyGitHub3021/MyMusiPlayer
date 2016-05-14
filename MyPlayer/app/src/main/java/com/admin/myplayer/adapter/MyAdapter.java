package com.admin.myplayer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.admin.myplayer.R;
import com.admin.myplayer.bean.Music;
import com.admin.myplayer.util.MediaUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/5/14.
 */
public class MyAdapter extends BaseAdapter {
    List<Music> songlist = MediaUtil.songlist;
    Context context;

    public MyAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        if (songlist != null) {
            return songlist.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        if (songlist != null) {
            songlist.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        if (songlist != null) {
            return i;
        }
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        /**
         * 初始化视图
         */
        viewHolder holder = null;
        if (view == null) {
            view = View.inflate(context, R.layout.item_music, null);
            holder = new viewHolder();
            holder.tv_title = (TextView) view.findViewById(R.id.tv_item_title);
            holder.tv_artist = (TextView) view.findViewById(R.id.tv_item_artist);
            view.setTag(holder);
        } else {
            holder = (viewHolder) view.getTag();
        }
        /**
         * 取数据
         */
        Music music = songlist.get(i);
        /**
         * 设置数据
         */
        holder.tv_title.setText(music.getTitle());
        holder.tv_artist.setText(music.getArtist());

        return view;
    }

    class viewHolder {
        TextView tv_title;
        TextView tv_artist;
    }
}
