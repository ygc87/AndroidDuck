package com.xhy.neihanduanzi.model.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 视频评论
 */
public class VideoComment implements Serializable {

    private Data[] data;
    private int have_hot;
    private int next_page;
    private int count;

    private AdItem ad_item;

    public Data[] getComentData() {
        return data;
    }

    public void setImages(Data[] data) {
        this.data = data;
    }

    public void setHotState(int state) {
        this.have_hot = state;
    }

    public int getHotState() {
        return this.have_hot;
    }

    public void setNextPage(int page) {
        this.next_page = page;
    }

    public int getNextPage() {
        return this.next_page;
    }

    public void setCommentsCount(int count) {
        this.count = count;
    }

    public int getCommentsCount() {
        return this.count;
    }

    public AdItem getAdItem() {
        return ad_item;
    }


    //评论实体
    public static class Data {

        private long comment_id;
        private String content;
        private String ctime;
        private Comment.UserBean user_info;
        private int digg_count;
        private int is_digg;
        private String is_hot;

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

        public String getHotState() {
            return is_hot;
        }

        public void setHotState(String is_hot) {
            this.is_hot = is_hot;
        }


        public String getPubDate() {
            return ctime;
        }

        public void setPubDate(String ctime) {
            this.ctime = ctime;
        }


        public Comment.UserBean getUserInfo() {
            return user_info;
        }

        public void setUserInfo(Comment.UserBean userInfo) {
            this.user_info = userInfo;
        }

        public static List<Data> getCommentList(Data[] datas) {
            if (datas == null || datas.length == 0)
                return null;

            List<Data> dataList = new ArrayList<>();
            for (Data data : datas) {
                dataList.add(data);
            }

            return dataList;
        }

        /**
         * 封装 userbean
         */
        public static class UserBean {
            private int uid = 0;
            private String uname;
            private String email;
            //head icon
            private String avatar_tiny;

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
        }
    }

    /**
     * ad item
     */
    public class AdItem {

        private String title;
        private String content;
        private String mp4_url;

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public void setDirectToUrl(String url) {
            this.mp4_url = url;
        }

        public String getDirectToUrl() {
            return mp4_url;
        }
    }

    @Override
    public String toString() {
        return "ViewComment{" +
                //"status='" + Data.content + '\'' +
                // ", message='" + msg + '\'' +
                '}';
    }
}
