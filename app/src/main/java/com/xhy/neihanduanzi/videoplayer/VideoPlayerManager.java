package com.xhy.neihanduanzi.videoplayer;


import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


/**
 * author yangc
 * date 2017/2/27
 * E-Mail:1007181167@qq.com
 * Description： video播放列表控制类
 */
public class VideoPlayerManager {
    private ManualPlayer mVideoPlayer;
    private boolean isClick = false;

    private VideoPlayerManager() {
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static VideoPlayerManager getInstance() {
        return Holder.holder;
    }

    private static  class Holder {

        static VideoPlayerManager holder = new VideoPlayerManager();
    }

    /***
     * 设置当前播放 控制类
     *
     * @param videoPlayer 播放页
     */
    public void setCurrentVideoPlayer(@NonNull ManualPlayer videoPlayer) {
        if (mVideoPlayer == null || !videoPlayer.toString().equals(mVideoPlayer.toString())) {
            releaseVideoPlayer();
        }
        this.mVideoPlayer = videoPlayer;
    }

    /***
     * 释放当前播放
     */
    public void releaseVideoPlayer() {
        if (mVideoPlayer != null) {
            mVideoPlayer.reset();
            mVideoPlayer = null;
        }
    }

    /***
     * d手机屏幕旋转配置
     * @param newConfig newConfig
     */
    public void onConfigurationChanged(Configuration newConfig) {
        if (mVideoPlayer != null) {
            mVideoPlayer.onConfigurationChanged(newConfig);
        }
    }

    /***
     * 设置返回建监听
     *
     * @return boolean boolean
     */
    public boolean onBackPressed() {
        return mVideoPlayer == null || mVideoPlayer.onBackPressed();
    }

    /**
     * 页面暂停播放暂停
     *
     * @param isReset isReset  没有特殊情况 默认 true 释放
     */
    public void onPause(boolean isReset) {
        if (mVideoPlayer != null) {
            mVideoPlayer.onListPause(isReset);
        }
    }

    /**
     * 页面恢复
     */
    public void onResume() {
        if (mVideoPlayer != null) {
            mVideoPlayer.onResume();
        }
    }

    /**
     * 页面销毁
     */
    public void onDestroy() {
        if (mVideoPlayer != null) {
            mVideoPlayer.onDestroy();
            mVideoPlayer = null;
        }
    }

    /**
     * 获取当前播放类
     *
     * @return ManualPlayer video player
     */
    @Nullable
    public ManualPlayer getVideoPlayer() {
        if (mVideoPlayer != null && mVideoPlayer.getPlayer() != null) {
            return mVideoPlayer;
        }
        return null;
    }

    /**
     * 获取当前状态
     *
     * @return ManualPlayer boolean
     */
    boolean isClick() {
        return isClick;
    }

    /**
     * 获取当前播放类
     *
     * @param click 实例
     */
    public void setClick(boolean click) {
        isClick = click;
    }

    /*****
     * @param player 播放控制器
     *@param  newPlayerView 新的view
     *@param    isPlay  isPlay 是否播放
     * ****/
//    public void switchTargetView(@NonNull ManualPlayer player, @Nullable VideoPlayerView newPlayerView, boolean isPlay) {
//        VideoPlayerView oldPlayerView = player.getVideoPlayerView();
//        if (oldPlayerView == newPlayerView) {
//            return;
//        }
//        if (newPlayerView != null) {
//            newPlayerView.getPlayerView().setPlayer(player.getPlayer());
//            player.setVideoPlayerView(newPlayerView);
//        }
//        if (oldPlayerView != null) {
//            oldPlayerView.resets();
//            oldPlayerView.getPlayerView().setPlayer(null);
//        }
//        if (isPlay) {
//            player.setStartOrPause(true);
//        } else {
//            if (newPlayerView != null) {
//                player.resetInit();
//            }
//        }
//
//    }
}
