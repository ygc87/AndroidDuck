package com.xhy.neihanduanzi.model.event;

import com.xhy.neihanduanzi.model.bean.Video;

/**
 * Created by mkt on 2018/5/5.
 */

public class VideoCommentEvent {

    private int position;

    private int count;

    private Video video;

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return this.position;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return this.count;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public Video getVideo() {
        return video;
    }
}
