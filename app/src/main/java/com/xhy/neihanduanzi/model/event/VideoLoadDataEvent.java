package com.xhy.neihanduanzi.model.event;

import com.xhy.neihanduanzi.model.bean.Video;

import java.util.List;

/**
 * Created by mkt on 2018/5/5.
 */

public class VideoLoadDataEvent {

    private List<Video> resultList;

    private String channel_code;

    private int type;

    public void setData(List<Video> resultList) {
        this.resultList = resultList;
    }

    public List<Video> getData() {
        return this.resultList;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public void setChannel(String code) {
        this.channel_code = code;
    }

    public String getChannel() {
        return this.channel_code;
    }

}
