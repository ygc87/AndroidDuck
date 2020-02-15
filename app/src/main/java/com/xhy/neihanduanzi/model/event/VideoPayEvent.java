package com.xhy.neihanduanzi.model.event;

import com.xhy.neihanduanzi.model.bean.Video;

/**
 * Created by mkt on 2018/5/5.
 */

public class VideoPayEvent {

    private int position;

    private int state;

    private boolean isNotifyChange;

    private Video video;

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return this.position;
    }


    public void setState(int paystate) {
        this.state = paystate;
    }

    public int getState() {
        return this.state;
    }

    public void setNotifyChange(boolean notifyChange) {
        isNotifyChange = notifyChange;
    }

    public boolean isNotifyChange() {
        return isNotifyChange;
    }

}
