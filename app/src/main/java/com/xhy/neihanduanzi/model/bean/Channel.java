package com.xhy.neihanduanzi.model.bean;

import java.io.Serializable;

public class Channel implements Serializable {
    public static final int TYPE_MY_CHANNEL = 3;

    public int id;
    public String channel_title;
    public String channel_category;
    public String channel_code;
    public int is_active;
    public int sort;

    public String getChannelTitle() {
        return channel_title;
    }

    public String setChannelTitle(String title) {
        return channel_title = title;
    }

    public String getChannelCategory() {
        return channel_category;
    }

    public String setChannelCategory(String category) {
        return channel_category = category;
    }

    public String getChannelCode() {
        return channel_code;
    }

    public String setChannel(String title) {
        return channel_code = channel_code;
    }

    public int isActive() {
        return is_active;
    }

    public int getSort() {
        return sort;
    }

}