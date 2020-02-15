package com.xhy.neihanduanzi.model.bean;

public class AppVersionBean {


    private int id;
    private String apk_version;
    private int datetime;
    private String info;
    private String apk_url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDatetime() {
        return datetime;
    }

    public void setDatetime(int datetime) {
        this.datetime = datetime;
    }


    public String getVersion() {
        return apk_version;
    }

    public void setVersion(String apk_version) {
        this.apk_version = apk_version;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getApkUrl() {
        return apk_url;
    }

    public void setApkUrl(String url) {
        this.apk_url = url;
    }

}
