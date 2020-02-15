package com.xhy.neihanduanzi.model.bean;

import java.io.Serializable;

/**
 * Created by mkt on 2018/6/5.
 */

public class BannerBean implements Serializable {

    private int ad_id;
    private String title;
    private int place;
    private String avatar_name;
    private String avatar_url;
    private String ad_button;
    private String ad_url;
    private String display_image;
    private String download_url;
    private String category;
    private int ctime;
    private int mtime;
    private int display_order;
    private int display_type;
    private int is_active;
    private int is_closable;
    private int label;


    public int getId() {
        return ad_id;
    }

    public void setId(int id) {
        this.ad_id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    public String getAvatarName() {
        return avatar_name;
    }

    public void setAvatarName(String avatar_name) {
        this.avatar_name = avatar_name;
    }

    public String getAvatarUrl() {
        return avatar_url;
    }

    public void setAvatarUrl(String url) {
        this.avatar_url = url;
    }

    public String getAdButton() {
        return ad_button;
    }

    public void setAdButton(String ad_button) {
        this.ad_button = ad_button;
    }

    public String getAdUrl() {
        return ad_url;
    }

    public void setAdUrl(String avatar_name) {
        this.ad_url = ad_url;
    }

    public String getDisplayImage() {
        return display_image;
    }

    public void setDisplayImage(String display_image) {
        this.display_image = display_image;
    }

    public String getDownloadUrl() {
        return download_url;
    }

    public void setDownloadUrl(String avatar_name) {
        this.download_url = download_url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getMtime() {
        return mtime;
    }

    public void setMtime(int mtime) {
        this.mtime = mtime;
    }


    public int getDisplayOrder() {
        return display_order;
    }

    public void setDisplayOrder(int display_order) {
        this.display_order = display_order;
    }


    public int getDisplayType() {
        return display_type;
    }

    public void setDisplayType(int display_type) {
        this.display_type = display_type;
    }


    public int getActive() {
        return is_active;
    }

    public void setActive(int is_active) {
        this.is_active = is_active;
    }


    public int getClosable() {
        return is_closable;
    }

    public void setClosable(int is_closable) {
        this.is_closable = is_active;
    }


    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

}
