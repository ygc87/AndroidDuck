package com.xhy.neihanduanzi.model.bean;

import java.io.Serializable;

/**
 * Created by mkt on 2017/8/18.
 */

public class Video implements Serializable {

    private int Id;
    private int user_id;
    private String mp4_url;
    private String title;
    private Integer digg_count;
    private int duration;
    private int create_time;
    private int is_can_share;
    private int label;

    private String content;
    private int video_height;
    private int video_width;
    private int comment_count;
    private String cover_image_uri;
    private int media_type;
    private int status;
    private String has_comments;
    private int user_like;
    private int is_payed;
    private String ad_button;
    private String channel_code;

    private UserBean user;

    //是否点赞
    private int is_digg;
    //是否收藏
    private String dialog_title;

    private String dialog_info;

    private String tips_msg;

    private String tips_title;

    private int is_collected;

    public long getId() {
        return Id;
    }

    public void setId(int id) {
        this.Id = id;
    }

    public long getUserId() {
        return user_id;
    }

    public void setUserId(int uid) {
        this.user_id = uid;
    }

    //这个接口取消掉
    public String getMp4Url() {
        return mp4_url;
    }

    public void setMp4Url(String url) {
        this.mp4_url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDiggCount() {
        return String.valueOf(digg_count);
    }

    public void setDiggCount(int digg_count) {
        this.digg_count = digg_count;
    }

    public String getUserLikeCount() {
        return String.valueOf(user_like);
    }

    public void setUserLikeCount(int user_like) {
        this.user_like = user_like;
    }

    public String getDuration() {
        return String.valueOf(duration);
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    /**
     * 时间戳
     */
    public int getCreateTime() {
        return create_time;
    }

    public void setCreateTime(int create_time) {
        this.create_time = create_time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCommentCount() {
        return String.valueOf(comment_count);
    }

    public void setCommentCount(int comment_count) {
        this.comment_count = comment_count;
    }

    public String getCoverImageURI() {
        return cover_image_uri;
    }

    public void setCoverImageURI(String cover_image_uri) {
        this.cover_image_uri = cover_image_uri;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public void setDiggState(int state) {
        is_digg = state;
    }

    public void setCollectedState(int state) {
        is_collected = state;
    }

    public int isDigg() {
        return is_digg;
    }

    public int isCollected() {
        return is_collected;
    }

    public void setPayedState(int state) {
        this.is_payed = state;
    }

    public boolean isPayed() {
        return (is_payed == 1);
    }

    public String getDialogTitle() {
        return dialog_title;
    }

    public void setDialogTitle(String dialog_title) {
        this.dialog_title = dialog_title;
    }

    public String getDialogInfo() {
        return dialog_info;
    }

    public void setDialogInfo(String dialog_info) {
        this.dialog_info = dialog_info;
    }

    public String getChanelCode() {
        return channel_code;
    }

    public void setChanelCode(String chanel_code) {
        this.channel_code = chanel_code;
    }

    public String getTipsMsg() {
        return tips_msg;
    }

    public void setTipsMsg(String msg) {
        this.tips_msg = msg;
    }

    public String getTipsTitle() {
        return tips_title;
    }

    public void setTipsTitle(String title) {
        this.tips_title = title;
    }

    public String getAdButtonText() {
        return ad_button;
    }

    public void setAdButtonText(String ad_button_text) {
        this.ad_button = ad_button_text;
    }

    /**
     * 封装 userbean
     */
    public class UserBean implements Serializable {
        private String uname;
        private String avatar;

        public void setName(String name) {
            this.uname = name;
        }

        public String getName() {
            return uname;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getAvatar() {
            return avatar;
        }
    }

    @Override
    public String toString() {
        return "Video{" +
                "id=" + Id +
                ", user_id='" + user_id + '\'' +
                ", mp4_url=" + mp4_url +
                ", title=" + title +
                ", digg_count=" + digg_count +
                ", is_digg=" + is_digg +
                ",  is_collected='" + is_collected + '\'' +
                //", author=" + author +
                '}';
    }

}
