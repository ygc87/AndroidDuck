package com.xhy.neihanduanzi.model.event;

/**
 * Created by mkt on 2018/5/9.
 */

public class CommentDiggEvent {

    private int position;

    private int count;

    public boolean isRefresh;


    public void setRefresh(boolean isRefresh) {
        this.isRefresh = isRefresh;
    }

    public boolean isRefresh() {
        return this.isRefresh;
    }

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
        return count;
    }
}
