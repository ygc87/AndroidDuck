package com.xhy.neihanduanzi.model.bean;

public class AdMessageBean {

    int ad_time;//播放广告的时长
    String title;//广告名称
    String ad_url;//点击广告图片时，展示详情 webview 的地址
    String display_image;//广告图片的地址

    public AdMessageBean(int ad_time, String adPictureUrl, String adUrl) {
        this.ad_time = ad_time;
        this.display_image = adPictureUrl;
        this.ad_url = adUrl;
    }

    public int getAdTime() {
        return ad_time;
    }

    public void setAdTime(int adTime) {
        this.ad_time = adTime;
    }

    public String getAdPictureUrl() {
        return display_image;
    }

    public void setAdPictureUrl(String adPictureUrl) {
        this.display_image = adPictureUrl;
    }

    public String getAdUrl() {
        return ad_url;
    }

    public void setAdUrl(String adUrl) {
        this.ad_url = adUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
