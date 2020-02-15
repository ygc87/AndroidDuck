package com.xhy.neihanduanzi.model.event;

/**
 * Created by mkt on 2018/5/8.
 */

public class PicDiggEvent {

    private int position;

    private int count;

    private int state;

    private boolean isGoodView;

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

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return this.state;
    }

    public void setGoodViewShow(boolean isShow) {
        this.isGoodView = isShow;
    }

    public boolean isShowGoodView() {
        return this.isGoodView;
    }
}
