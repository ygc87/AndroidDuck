package com.xhy.neihanduanzi.widget;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;

import com.baidu.mobstat.StatService;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.chaokun.neihanduanzi.R;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.loopj.android.http.TextHttpResponseHandler;
import com.xhy.neihanduanzi.activity.socres.ChongzhiActivity;
import com.xhy.neihanduanzi.activity.socres.ScoreRuleActivity;
import com.xhy.neihanduanzi.activity.socres.ScoreShopActivity;
import com.xhy.neihanduanzi.app.AppOperator;
import com.xhy.neihanduanzi.app.api.XHYApi;
import com.xhy.neihanduanzi.model.bean.Account;
import com.xhy.neihanduanzi.model.bean.Constants;
import com.xhy.neihanduanzi.model.bean.Status;
import com.xhy.neihanduanzi.model.bean.Video;
import com.xhy.neihanduanzi.model.bean.VideoStatus;
import com.xhy.neihanduanzi.model.event.VideoPayEvent;
import com.xhy.neihanduanzi.model.event.VideoPlayerEvent;
import com.xhy.neihanduanzi.utils.StringUtils;
import com.xhy.neihanduanzi.utils.TDevice;
import com.xhy.neihanduanzi.utils.TextUtil;
import com.xhy.neihanduanzi.utils.ToastUtils;
import com.xhy.neihanduanzi.utils.dbutils.AccountHelper;
import com.xhy.neihanduanzi.videoplayer.ManualPlayer;
import com.xhy.neihanduanzi.videoplayer.VideoPlayerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import chuangyuan.ycj.videolibrary.listener.VideoInfoListener;
import cz.msebera.android.httpclient.Header;

/**
 * Created by mkt on 2018/3/13.
 */

public class XHYPlayer extends ManualPlayer implements View.OnClickListener {

    public static final int STATE_PLAY = 1;
    public static final int STATE_PAUSE = 2;
    public static final int STATE_RESET = 3;

    private Context mContext;

    private LayoutInflater mInflater;

    //视频的索引
    private int mPosition = -1;

    private Video video;

    private BaseViewHolder mItemViewHolder;

    private List<View> mViewList = new ArrayList<View>();

    private Account mAccount;

    public XHYPlayer(Activity activity, @NonNull VideoPlayerView playerView, BaseViewHolder itemViewHolder) {
        super(activity, playerView);
        mInflater = activity.getLayoutInflater();
        mContext = activity;
        mItemViewHolder = itemViewHolder;
        setOnPlayClickListener(this);
        mAccount = AccountHelper.getAccount();
        setVideoInfoListener(new VideoPlayListener());
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public void addView(View view) {
        mViewList.add(view);
    }

    @Override
    public void onClick(View v) {
        if (!TDevice.hasInternet()) {
            ToastUtils.showToast(mContext, "网络异常");
            return;
        }
        if (video == null) {
            return;
        }
        String title = video.getTipsTitle();
        String msg = video.getTipsMsg();
        if (video.isPayed()) {
            if (video.getMp4Url() != null) {
                startPlay(video.getMp4Url());
            } else {
                if (video.getChanelCode().equals(Constants.SVIDEO)) {
                    requestVideoURL();
                } else if (video.getChanelCode().equals(Constants.LVIDEO)) {
                    requestLongVideoURL();
                }
            }
        } else {
            if (video.getChanelCode() != null) {
                String neutral = null;
                if (mAccount != null && !mAccount.isVip()) {
                    neutral = mContext.getResources().getString(R.string.chongzhi_neutral_vip_button);
                }

                if (video.getChanelCode().equals(Constants.SVIDEO)) {
                    StatService.onEvent(mContext, "ShortVideo", "onClick");
                    showAlertDialog(title, msg, neutral, "取消", "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(
                                DialogInterface dialog,
                                int which) {
                            ScoreShopActivity.show(mContext);
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(
                                DialogInterface dialog,
                                int which) {
                            buyVideoProduct();
                        }
                    });
                } else if (video.getChanelCode().equals(Constants.LVIDEO)) {
                    StatService.onEvent(mContext, "LongVideo", "onClick");
                    showAlertDialog(title, msg, neutral, "取消", "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(
                                DialogInterface dialog,
                                int which) {
                            ScoreShopActivity.show(mContext);
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(
                                DialogInterface dialog,
                                int which) {
                            buyLongVideoProduct();
                        }
                    });
                }
            } else {
                if (video.getMp4Url() != null) {
                    setPlayUri(video.getMp4Url());
                    startPlayer();
                }
            }
        }
    }

    //购买短视频
    private void buyVideoProduct() {
        if (!TDevice.hasInternet()) {
            ToastUtils.showToast(mContext, "网络异常");
            return;
        }
        if (mAccount == null) {
            ToastUtils.showToast(mContext, "无法获取账号");
            return;
        }
        //获取短视频数据
        XHYApi.buySVideoProduct(AccountHelper.getAccount(), (int) video.getId(), new TextHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("msg", responseString);
                        StatService.onEvent(mContext, "PayShortVideo", "payed_fail", 1, hashMap);
                    }

                    @SuppressWarnings("ConstantConditions")
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            Status status = AppOperator.createGson().fromJson(responseString, Status.class);
                            if (status != null && mContext != null && status.getMessage() != null) {
                                if (status.getStatus() == 1) {
                                    postVideoPayState(mPosition, 1);
                                    String source = status.getMessage();
                                    startPlay(source);
                                    video.setMp4Url(source);//给video设置请求的网址
                                    StatService.onEvent(mContext, "PayShortVideo", "payed_success");
                                } else if (status.getStatus() == 0) {
                                    HashMap<String, String> hashMap = new HashMap<>();
                                    hashMap.put("msg", status.getMessage());
                                    StatService.onEvent(mContext, "PayShortVideo", "payed_fail", 1, hashMap);
                                    ToastUtils.showToast(mContext, status.getMessage());
                                } else if (status.getStatus() == -1) {
                                    notScore(status.getMessage(), "");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtils.showToast(mContext, "购买视频出错，请稍后重试！");
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }

                    @Override
                    public void onCancel() {
                        super.onCancel();
                    }
                }
        );
    }

    //购买长视频
    private void buyLongVideoProduct() {
        if (!TDevice.hasInternet()) {
            ToastUtils.showToast(mContext, "网络异常");
            return;
        }
        if (mAccount == null) {
            ToastUtils.showToast(mContext, "无法获取账号");
            return;
        }
        //获取长视频数据
        XHYApi.buyLVideoProduct(AccountHelper.getAccount(), (int) video.getId(), new TextHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("msg", responseString);
                        ToastUtils.showToast(mContext, responseString);
                        StatService.onEvent(mContext, "PayLongVideo", "payed_fail", 1, hashMap);
                    }

                    @SuppressWarnings("ConstantConditions")
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            VideoStatus status = AppOperator.createGson().fromJson(responseString, VideoStatus.class);
                            if (status != null && mContext != null && status.getMessage() != null) {
                                if (status.getStatus() == 1) {
                                    postVideoPayState(mPosition, 1);
                                    String source = status.getMessage();
                                    //购买后更改视频的类型
                                    startPlay(source);
                                    video.setMp4Url(source);//给video设置请求的网址
                                    StatService.onEvent(mContext, "PayLongVideo", "payed_success");
                                } else if (status.getStatus() == 0) {
                                    HashMap<String, String> hashMap = new HashMap<>();
                                    hashMap.put("msg", status.getMessage());
                                    ToastUtils.showToast(mContext, status.getMessage());
                                    StatService.onEvent(mContext, "PayLongVideo", "payed_fail", 1, hashMap);
                                } else if (status.getStatus() == -1) {
                                    notScore(status.getMessage(), "");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }

                    @Override
                    public void onCancel() {
                        super.onCancel();
                    }
                }
        );
    }

    //视频的id
    private void requestVideoURL() {
        if (mAccount == null) {
            ToastUtils.showToast(mContext, "无法获取账号");
            return;
        }
        //获取短视频数据
        XHYApi.requesVideoURL(AccountHelper.getAccount(), (int) video.getId(), "svideo", new TextHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    }

                    @SuppressWarnings("ConstantConditions")
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            VideoStatus status = AppOperator.createGson().fromJson(responseString, VideoStatus.class);
                            if (status != null && mContext != null) {
                                if (status.getStatus() == 1) {
                                    if (status.getMessage() != null) {
                                        String source = status.getMessage();
                                        startPlay(source);
                                    }
                                } else if (status.getStatus() == 0) {
                                    ToastUtils.showToast(mContext, status.getMessage());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }

                    @Override
                    public void onCancel() {
                        super.onCancel();
                    }
                }
        );
    }

    //请求长视频地址
    private void requestLongVideoURL() {
        if (mAccount == null) {
            ToastUtils.showToast(mContext, "无法获取账号");
            return;
        }
        XHYApi.requesVideoURL(AccountHelper.getAccount(), (int) video.getId(), "lvideo", new TextHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        //hud.show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        //hud.dismiss();
                    }

                    @SuppressWarnings("ConstantConditions")
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            VideoStatus status = AppOperator.createGson().fromJson(responseString, VideoStatus.class);
                            if (status != null && mContext != null) {
                                if (status.getStatus() == 1) {
                                    if (status.getMessage() != null) {
                                        String source = status.getMessage();
                                        startPlay(source);

                                    }
                                } else if (status.getStatus() == 0) {
                                    ToastUtils.showToast(mContext, status.getMessage());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //hud.dismiss();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        //hud.dismiss();
                    }

                    @Override
                    public void onCancel() {
                        super.onCancel();
                        //hud.dismiss();
                    }
                }
        );
    }

    private void notScore(String title, String msg) {
        String negative = mContext.getResources().getString(R.string.chongzhi_negative_button);
        String positive = mContext.getResources().getString(R.string.chongzhi_positive_button);
        String neutral = mContext.getResources().getString(R.string.chongzhi_neutral_score_button);
        showAlertDialog(title, null, neutral, negative, positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(
                    DialogInterface dialog,
                    int which) {
                ScoreRuleActivity.show(mContext);
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(
                    DialogInterface dialog,
                    int which) {
                ChongzhiActivity.show(mContext);
            }
        });
    }

    @Override
    public void reset() {
        super.reset();
        changeUIDisplay(mViewList);
        postVideoPlayState(mPosition, STATE_RESET);
    }

    private void startPlay(String url) {
        //1.验证url是否是播发地址
        if (StringUtils.isVideoUrl(url)) {
            setPlayUri(url);
            startPlayer();
        } else {
            ToastUtils.showToast(mContext, "视频播放地址异常");
        }
    }

    private void postVideoPayState(int position, int state) {
        VideoPayEvent event = new VideoPayEvent();
        event.setPosition(position);
        event.setState(state);
        event.setNotifyChange(mItemViewHolder == null);
        EventBus.getDefault().post(event);
    }

    private void postVideoPlayState(int position, int playState) {
        VideoPlayerEvent event = new VideoPlayerEvent();
        event.setPosition(position);
        event.setPlayState(playState);
        EventBus.getDefault().post(event);
    }

    private void changeUIDisplay(List<View> list) {
        if (list.size() == 0) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            if (isPlaying()) {
                list.get(i).setVisibility(View.GONE);
                postVideoPlayState(mPosition, STATE_PLAY);
            } else {
                list.get(i).setVisibility(View.VISIBLE);
                postVideoPlayState(mPosition, STATE_PAUSE);
            }
        }
    }

    //写一个alterDialog 方法
    private void showAlertDialog(String title, String msg, String neutralText, String negativeText, String positiveText, DialogInterface.OnClickListener neuListener, DialogInterface.OnClickListener posListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        if (!TextUtil.isNull(msg)) {
            builder.setMessage(msg);
        }

        builder.setCancelable(false);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

        builder.setPositiveButton(positiveText, posListener);
        builder.setNeutralButton(neutralText, neuListener);
        builder.show();
    }


    public class VideoPlayListener implements VideoInfoListener {

        @Override
        public void onPlayStart(long currPosition) {

        }

        @Override
        public void onLoadingChanged() {
        }

        @Override
        public void onPlayerError(@Nullable ExoPlaybackException e) {
        }

        @Override
        public void onPlayEnd() {
        }

        @Override
        public void isPlaying(boolean playWhenReady) {
            changeUIDisplay(mViewList);
        }
    }
}

