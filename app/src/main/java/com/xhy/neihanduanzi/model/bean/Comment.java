package com.xhy.neihanduanzi.model.bean;

import java.io.Serializable;

/**
 * Created by mkt on 2017/8/22.
 */

public class Comment implements Serializable {

    private long comment_id;
    private String content;
    private String ctime;
    private UserBean user_info;
    private int digg_count;
    private int is_digg;

    public long getId() {
        return comment_id;
    }

    public void setId(long id) {
        this.comment_id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getDiggCount() {
        return digg_count;
    }

    public void setDiggCount(int count) {
        this.digg_count = count;
    }

    public int getDiggState() {
        return is_digg;
    }

    public void setDiggState(int is_digg) {
        this.is_digg = is_digg;
    }


    public String getPubDate() {
        return ctime;
    }

    public void setPubDate(String ctime) {
        this.ctime = ctime;
    }


    public UserBean getUserInfo() {
        return user_info;
    }

    public void setUserInfo(UserBean userInfo) {
        this.user_info = userInfo;
    }

    /**
     * 封装 userbean
     */
    public class UserBean {
        private int uid = 0;
        private String uname;
        private String email;
        //head icon
        private String avatar_tiny;

        private int is_vip;

        public void setUid(int uid) {
            this.uid = uid;
        }

        public int getUid() {
            return uid;
        }


        public void setName(String name) {
            this.uname = name;
        }

        public String getName() {
            return uname;
        }


        public void setEmail(String email) {
            this.email = email;
        }

        public String getEmail() {
            return email;
        }

        public void setAvatar(String avatar) {
            this.avatar_tiny = avatar;
        }

        public String getAvatar() {
            return avatar_tiny;
        }

        public boolean isVip() {
            return (is_vip == 1);
        }

    }

}
