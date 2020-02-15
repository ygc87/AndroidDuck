package com.xhy.neihanduanzi.model.event;

import com.xhy.neihanduanzi.adapter.VideoAdapter;
import com.xhy.neihanduanzi.model.bean.Video;

/**
 * item collect event
 */

public class VideoCollectEvent {

    public static final String ADD_VIDEO = "add_video";

    public static final String DEL_VIDEO = "del_video";

    private int videoPosition;

    private int commentsPosition;

    private int count;

    private int state;

    private boolean isRefresh;

    private boolean isGoodViewComment;

    private boolean isGoodViewVideo;

    private Video video;

    private String action;

    private String mChanelCode;

    //private VideoAdapter.AdapterEnum adapterType;

    public void setVideo(Video video) {
        this.video = video;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideoPosition(int videoPosition) {
        this.videoPosition = videoPosition;
    }

    public int getVideoPosition() {
        return this.videoPosition;
    }

    public void setCommentsPosition(int commentsPosition) {
        this.commentsPosition = commentsPosition;
    }

    public int getCommentsPosition() {
        return this.commentsPosition;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return this.count;
    }

    public void setState(int paystate) {
        this.state = paystate;
    }

    public int getState() {
        return this.state;
    }

    public void setRefresh(boolean isRefresh) {
        this.isRefresh = isRefresh;
    }

    public boolean isRefresh() {
        return this.isRefresh;
    }

    public void setGoodViewCommentShow(boolean isShow) {
        this.isGoodViewComment = isShow;
    }

    public boolean isShowGoodViewComment() {
        return this.isGoodViewComment;
    }

    public void setGoodViewVideoShow(boolean isShow) {
        this.isGoodViewVideo = isShow;
    }

    public boolean isShowGoodViewVideo() {
        return this.isGoodViewVideo;
    }


//    public void setAdapterType(VideoAdapter.AdapterEnum type) {
//        this.adapterType = type;
//    }
//
//    public VideoAdapter.AdapterEnum getAdapterType() {
//        return this.adapterType;
//    }

    public void setChanelCode(String chanelCode) {
        this.mChanelCode = chanelCode;
    }

    public String getChanelCode() {
        return mChanelCode;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return this.action;
    }
}
