package com.xhy.neihanduanzi.model.bean;

import android.text.TextUtils;

import com.xhy.neihanduanzi.utils.CollectionUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mkt on 2017/10/24.
 */

public class Picture implements Serializable {

    private long id;
    private String title;
    private Image[] images;
    private int collect;
    private int digg_count;
    private int charge;
    private int user_score;
    private int pic_count;
    private int is_digg;
    private int is_collected;
    private int is_payed;
    private int status;
    private String msg;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return msg;
    }

    public void setMessage(String message) {
        this.msg = message;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int isDigg() {
        return is_digg;
    }

    public void setCollectedState(int state) {
        is_collected = state;
    }

    public boolean isCollected() {
        return is_collected == 1;
    }

    //图片是否购买
    public boolean isPayed() {
        return (is_payed == 1);
    }

    //图片是否购买
    public void setPayedState(int payed) {
        is_payed = payed;
    }

    public int getDiggCount() {
        return digg_count;
    }

    public void setDiggCount(int digg_count) {
        this.digg_count = digg_count;
    }

    public int getCollectCount() {
        return collect;
    }

    public void setCollectCount(int collectCount) {
        this.collect = collectCount;
    }

    public int getObjectCharge() {
        return charge;
    }

    public int setObjectCharge(int charge) {
        return this.charge = charge;
    }

    public int getUserScore() {
        return user_score;
    }

    public int setUserScore(int score) {
        return this.user_score = score;
    }

    public Picture.Image[] getImages() {
        return images;
    }

    public void setImages(Picture.Image[] images) {
        this.images = images;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setPicCount(int pic_count) {
        this.pic_count = pic_count;
    }

    public int getPicCount() {
        return pic_count;
    }


    public void setDiggState(int state) {
        this.is_digg = state;
    }

    public void setCollectState(int state) {
        this.is_collected = state;
    }

    public void setPayState(int state) {
        this.is_payed = state;
    }

    public static class Image implements Serializable {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public static Picture.Image create(String href) {
            Picture.Image image = new Picture.Image();
            return image;
        }

        public static String[] getImagePath(Picture.Image[] images) {
            if (images == null || images.length == 0)
                return null;

            List<String> paths = new ArrayList<>();
            for (Picture.Image image : images) {
                if (check(image))
                    paths.add(image.url);
            }

            return CollectionUtil.toArray(paths, String.class);
        }

        public static Picture.Image[] getImageArray(String[] images) {
            if (images == null || images.length == 0)
                return null;

            List<String> paths = CollectionUtil.toArrayList(images);

            int count = paths.size();

            Picture.Image[] imgs = new Picture.Image[count];

            for (int i = 0; i < count; i++) {
                Picture.Image image = Picture.Image.create("");
                image.setUrl(paths.get(i));
                if (check(image)) {
                    imgs[i] = image;
                }
            }

            return imgs;
        }


        public static String getCoverImagePath(Picture.Image[] images) {
            if (images == null || images.length == 0)
                return null;

            String coverUrl = images[0].url;
            
            return coverUrl;
        }

        public static boolean check(Picture.Image image) {
            return image != null
                    //&& !TextUtils.isEmpty(image.getThumb())
                    && !TextUtils.isEmpty(image.url);
        }
    }

    @Override
    public String toString() {
        return "";
//        return "Picture{" +
//                "id=" + id +
//                //", content='" + content + '\'' +
//                //", commentCount=" + commentCount +
//                //", likeCount=" + likeCount +
//                //", liked=" + liked +
//                //", pubDate='" + pubDate + '\'' +
//                ", images=" + Arrays.toString(Oimages) +
//                '}';
    }
}
