package com.xhy.neihanduanzi.model.event;


/**
 * Created by mkt on 2018/5/5.
 */

public class VideoDiggEvent {

    private int videoPosition;

    private int commentsPosition;

    private int count;

    private int state;

    private boolean isGoodViewComment;

    private boolean isGoodViewVideo;

    //private VideoAdapter.AdapterEnum adapterType;

    private String mChanelCode;

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
}
