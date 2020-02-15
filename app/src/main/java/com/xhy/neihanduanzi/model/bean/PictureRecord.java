package com.xhy.neihanduanzi.model.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by mkt on 2018/5/14.
 */

public class PictureRecord extends DataSupport {

    private int uid;
    private String channelCode;
    private int page;
    private String json;
    private long time;

    public PictureRecord(int uid,String channelCode, int page, String json, long time) {
        this.uid = uid;
        this.channelCode = channelCode;
        this.page = page;
        this.json = json;
        this.time = time;
    }
    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
