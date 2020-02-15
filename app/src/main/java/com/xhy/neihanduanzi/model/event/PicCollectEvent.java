package com.xhy.neihanduanzi.model.event;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xhy.neihanduanzi.model.bean.Picture;

/**
 * Created by mkt on 2018/5/8.
 */

public class PicCollectEvent {

    public static final String ADD_PIC = "add_pic";

    public static final String DEL_PIC = "del_pic";

    private int position;

    private int count;

    private int state;

    private boolean isGoodView;

    private boolean isChangeUI;

    private String action;

    private Picture picture;


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

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return this.action;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public Picture getPicture() {
        return picture;
    }

}
