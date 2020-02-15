package com.xhy.neihanduanzi.model.bean;


import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.xhy.neihanduanzi.utils.TextUtil;

public class CommentMultipleItem implements MultiItemEntity,Comparable<CommentMultipleItem> {

    public static final int VIDEO_INFO = 1;
    public static final int GROUP_HEAD = 2;
    public static final int COMMENT_ITEM = 3;
    public static final int EMPTY_VIEW = 4;

    private int itemType;
    private int object_id;
    private int digg_state;
    private int coll_state;
    private int digg_count;
    private int coll_count;
    private int comments_count;
    private int position;

    private String avatar;
    private String name;
    private String content;
    private String time;

    private String category;
    private boolean is_vip;

    private Drawable drawable_left;
    private transient Drawable diggDrawable;
    private transient int diggColor;
    private transient Drawable favoriteDrawable;
    private transient int favoriteColor;
    private transient Drawable vipDrawable;

    public Drawable getVipDrawable() {
        return vipDrawable;
    }

    public void setVipDrawable(Drawable vipDrawable) {
        this.vipDrawable = vipDrawable;
    }

    public Drawable getDiggDrawable() {
        return diggDrawable;
    }

    public void setDiggDrawable(Drawable diggDrawable) {
        this.diggDrawable = diggDrawable;
    }

    public int getDiggColor() {
        return diggColor;
    }

    public void setDiggColor(int diggColor) {
        this.diggColor = diggColor;
    }

    public Drawable getFavoriteDrawable() {
        return favoriteDrawable;
    }

    public void setFavoriteDrawable(Drawable favoriteDrawable) {
        this.favoriteDrawable = favoriteDrawable;
    }

    public int getFavoriteColor() {
        return favoriteColor;
    }

    public void setFavoriteColor(int favoriteColor) {
        this.favoriteColor = favoriteColor;
    }

    private VideoComment.AdItem adItem;

    private transient int sortId;
    private String channelCode;

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public CommentMultipleItem(int itemType) {
        this.itemType = itemType;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public int getDiggCount() {
        return digg_count;
    }

    public void setDiggCount(int digg_count) {
        this.digg_count = digg_count;
    }

    public int getCollCount() {
        return coll_count;
    }

    public void setCollCount(int coll_count) {
        this.coll_count = coll_count;
    }

    public boolean isVip() {
        return is_vip;
    }

    public void setVipState(boolean is_vip) {
        this.is_vip = is_vip;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setObjectID(int id) {
        this.object_id = id;
    }

    public int getObjectID() {
        return object_id;
    }

    public void setObjectPosition(int position) {
        this.position = position;
    }

    public int getObjectPosition() {
        return position;
    }

    public void setDiggState(int digg_state) {
        this.digg_state = digg_state;
    }

    public int getDiggState() {
        return digg_state;
    }

    public void setCollState(int coll_state) {
        this.coll_state = coll_state;
    }

    public int getCollState() {
        return coll_state;
    }

    public void setCommentsCount(int count) {
        this.comments_count = count;
    }

    public int getCommentsCount() {
        return comments_count;
    }

    public String getCategoryType() {
        return category;
    }

    public void setCategoryType(String category) {
        this.category = category;
    }

    public Drawable getDrawableLeft() {
        return drawable_left;
    }

    public void setDrawableLeft(Drawable drawable_left) {
        this.drawable_left = drawable_left;
    }

    public int getSortId() {
        return sortId;
    }

    public void setSortId(int sortId) {
        this.sortId = sortId;
    }

    public VideoComment.AdItem getAdItem() {
        return adItem;
    }

    public void setAdItem(VideoComment.AdItem item) {
        this.adItem = item;
    }

    @Override
    public int compareTo(@NonNull CommentMultipleItem item) {
        int sort = this.sortId - item.sortId;
        if(sort == 0){
            if(TextUtil.isNull(item.time)){
                return -1;
            }
            sort = item.time.compareTo(time);
        }
        return sort;
    }
}
