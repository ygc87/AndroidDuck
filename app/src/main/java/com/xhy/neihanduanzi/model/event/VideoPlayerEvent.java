package com.xhy.neihanduanzi.model.event;

import com.xhy.neihanduanzi.model.bean.Video;

/**
 * Created by mkt on 2018/5/5.
 */

public class VideoPlayerEvent {

    private int position;

    private int playState;

    private Object holder;

    private Video video;

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPlayState(int playState) {
        this.playState = playState;
    }

    public int getPlayState() {
        return this.playState;
    }

    public void setViewHodler(Object hodler) {
        this.holder = hodler;
    }

    public Object getViewHodler() {
        return this.holder;
    }

}
