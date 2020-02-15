package com.xhy.neihanduanzi.model.event;

/**
 * Created by mkt on 2018/5/5.
 */

public class PicPayEvent {

    private int position;

    private int state;

    private String[] mImageSources;

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

    public void setImageSources(String[] sources) {
        this.mImageSources = sources;
    }

    public String[] getImageSources() {
        return this.mImageSources;
    }


}
