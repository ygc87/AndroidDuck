package com.xhy.neihanduanzi.adapter;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.chaokun.neihanduanzi.R;
import com.loopj.android.http.TextHttpResponseHandler;
import com.orhanobut.logger.Logger;
import com.xhy.neihanduanzi.activity.LoginActivity;
import com.xhy.neihanduanzi.activity.WebViewActivity;
import com.xhy.neihanduanzi.app.AppOperator;
import com.xhy.neihanduanzi.app.api.XHYApi;
import com.xhy.neihanduanzi.improve.helper.InputHelper;
import com.xhy.neihanduanzi.model.bean.Account;
import com.xhy.neihanduanzi.model.bean.CommentMultipleItem;
import com.xhy.neihanduanzi.model.bean.Constants;
import com.xhy.neihanduanzi.model.bean.StatusCount;
import com.xhy.neihanduanzi.model.bean.VideoComment;
import com.xhy.neihanduanzi.model.event.CommentDiggEvent;
import com.xhy.neihanduanzi.model.event.VideoCollectEvent;
import com.xhy.neihanduanzi.model.event.VideoDiggEvent;
import com.xhy.neihanduanzi.utils.StringUtils;
import com.xhy.neihanduanzi.utils.TDevice;
import com.xhy.neihanduanzi.utils.ToastUtils;
import com.xhy.neihanduanzi.utils.dbutils.AccountHelper;
import com.xhy.neihanduanzi.utils.glide.ImageLoader;
import com.xhy.neihanduanzi.view.ResizableImageView;
import com.xhy.neihanduanzi.widget.PortraitView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cz.msebera.android.httpclient.Header;


public class CommentsAdapter extends BaseMultiItemQuickAdapter<CommentMultipleItem, BaseViewHolder> {

    /**视频点赞*/
    private TextHttpResponseHandler mVideoDiggHanddler;
    /**视频收藏*/
    private TextHttpResponseHandler mVideoCollHandler;
    /**评论点赞*/
    private TextHttpResponseHandler mCommentDiggHandler;
    /**视频在列表中的索引位置*/
    private int mVideoPos;
    /**短视频、长视频、图片类型，定义在数组R.array.channel_code中*/
    private String channelCode;

    public CommentsAdapter(List<CommentMultipleItem> data,int position) {
        super(data);
        mVideoPos = position;
        addItemType(CommentMultipleItem.VIDEO_INFO, R.layout.comment_head_view);
        addItemType(CommentMultipleItem.GROUP_HEAD, R.layout.video_recycler_headone_view);
        addItemType(CommentMultipleItem.COMMENT_ITEM, R.layout.comments_item);
        addItemType(CommentMultipleItem.EMPTY_VIEW, R.layout.comment_empty);

        mVideoDiggHanddler = new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                ToastUtils.showToast(mContext, "点赞失败");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    StatusCount status = AppOperator.createGson().fromJson(responseString, StatusCount.class);
                    if (status.getStatus() == 1) {
                        BaseViewHolder holder = (BaseViewHolder) mVideoDiggHanddler.getTag();
                        postVideoDiggCount(holder.getAdapterPosition(), status.getCount(), 1);
                    }
                    ToastUtils.showToast(mContext, status.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        mVideoCollHandler = new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                ToastUtils.showToast(mContext, "收藏失败");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    StatusCount status = AppOperator.createGson().fromJson(responseString, StatusCount.class);
                    //点赞成功后，返回当前的数量
                    if (status.getStatus() == 1) {
                        BaseViewHolder holder = (BaseViewHolder) getTag();
                        int count = status.getCount();
                        postVideoColCount(holder.getAdapterPosition(), count, 1);
                    }
                    ToastUtils.showToast(mContext, status.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        mCommentDiggHandler = new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                ToastUtils.showToast(mContext, "点赞失败");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    StatusCount status = AppOperator.createGson().fromJson(responseString, StatusCount.class);
                    if (status.getStatus() == 1) {
                        final BaseViewHolder holder = (BaseViewHolder) getTag();
                        int digg_count = status.getCount();
                        postCommentDigg(holder.getAdapterPosition(), digg_count);
                    }
                    ToastUtils.showToast(mContext, status.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    protected void convert(final BaseViewHolder helper, final CommentMultipleItem item) {

        PortraitView ivPortrait;
        TextView tvName;
        TextView tvContent;
        TextView tvDiggCount;
        TextView tvCollCount;
        ResizableImageView imAd;

        switch (helper.getItemViewType()) {
            case CommentMultipleItem.VIDEO_INFO:
                Logger.v("Vedio Information show...........");
                channelCode = item.getChannelCode();
                final String category = item.getCategoryType();
                final int videoId = item.getObjectID();
                final Account account = AccountHelper.getAccount();
                ivPortrait = helper.getView(R.id.iv_portrait);
                ImageLoader.load(mContext, item.getAvatar(), ivPortrait);

                tvName = helper.getView(R.id.tv_nick);
                tvName.setText(item.getName());

                tvContent = helper.getView(R.id.tv_content);
                tvContent.setText(item.getContent());
                // 点赞
                tvDiggCount = helper.getView(R.id.tv_digg);
                tvDiggCount.setText(String.valueOf(item.getDiggCount()));
                tvDiggCount.setCompoundDrawablesWithIntrinsicBounds(item.getDiggDrawable(), null, null, null);
                tvDiggCount.setTextColor(item.getDiggColor());
                // 收藏
                tvCollCount = helper.getView(R.id.tv_collect);
                tvCollCount.setText(String.valueOf(item.getCollCount()));
                tvCollCount.setCompoundDrawablesWithIntrinsicBounds(item.getFavoriteDrawable(), null, null, null);
                tvCollCount.setTextColor(item.getFavoriteColor());

                mVideoDiggHanddler.setTag(helper);
                mVideoCollHandler.setTag(helper);

                //点赞
                tvDiggCount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TDevice.hasInternet()) {
                            ToastUtils.showToast(mContext, "网络异常");
                            return;
                        }

                        if (!AccountHelper.isLogin()) {
                            ToastUtils.showToast(mContext, "请登录点赞");
                            LoginActivity.show(mContext);
                        } else {
                            if (channelCode != null) {
                                if (channelCode.equals(Constants.SVIDEO)) {
                                    XHYApi.addVideoDigg((int) videoId, account, "svideo_digg", mVideoDiggHanddler);
                                } else if (channelCode.equals(Constants.LVIDEO)) {
                                    XHYApi.addVideoDigg((int) videoId, account, "lvideo_digg", mVideoDiggHanddler);
                                }
                            }
                        }
                    }
                });

                //收藏
                tvCollCount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TDevice.hasInternet()) {
                            ToastUtils.showToast(mContext, "网络异常");
                            return;
                        }
                        if (!AccountHelper.isLogin()) {
                            ToastUtils.showToast(mContext, "请登录收藏");
                            LoginActivity.show(mContext);
                        } else {
                            if (channelCode != null) {
                                if (channelCode.equals(Constants.SVIDEO)) {
                                    XHYApi.addSvideoCollected((int) videoId, AccountHelper.getAccount(), mVideoCollHandler);
                                } else if (channelCode.equals(Constants.LVIDEO)) {
                                    XHYApi.addLvideoCollected((int) videoId, AccountHelper.getAccount(), mVideoCollHandler);
                                }
                            }
                        }
                    }
                });

                // 广告加载
                final VideoComment.AdItem adItem = item.getAdItem();
                if (adItem != null && !StringUtils.isEmpty(adItem.getDirectToUrl()) && StringUtils.isImgUrl(adItem.getContent())) {
                    imAd = helper.getView(R.id.comment_ad);
                    imAd.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .asDrawable()
                            .load(adItem.getContent())
                            .transition(new DrawableTransitionOptions().crossFade())
                            .thumbnail(0.2f)
                            .into(imAd);
                    imAd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (StringUtils.isUrl(adItem.getDirectToUrl())) {
                                WebViewActivity.show(mContext, adItem.getTitle(), adItem.getDirectToUrl());
                            } else if (StringUtils.isPackage(adItem.getDirectToUrl())) {
                                try {
                                    Class clz = Class.forName(adItem.getDirectToUrl());
                                    mContext.startActivity(new Intent(mContext, clz));
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }

                break;
            case CommentMultipleItem.GROUP_HEAD:// 标题加载
                TextView tvGroupComment = helper.getView(R.id.tv_group_comment);
                tvGroupComment.setCompoundDrawablesWithIntrinsicBounds(item.getDrawableLeft(), null, null, null);
                tvGroupComment.setText(item.getName());
                break;
            case CommentMultipleItem.COMMENT_ITEM:// 评论加载
                ivPortrait = helper.getView(R.id.iv_avatar);
                tvName = helper.getView(R.id.tv_name);
                tvContent = helper.getView(R.id.tv_content);
                tvDiggCount = helper.getView(R.id.tv_comment_diggcount);
                TextView tvTime = helper.getView(R.id.tv_pub_date);
                ImageLoader.load(mContext, item.getAvatar(), ivPortrait);
                tvName.setText(item.getName());
                tvName.setCompoundDrawablesWithIntrinsicBounds(null, null, item.getVipDrawable(), null);
                tvContent.setText(InputHelper.displayEmoji(mContext.getResources(), item.getContent()));
                tvTime.setText(item.getTime());
                tvDiggCount.setText(String.valueOf(item.getDiggCount()));
                tvDiggCount.setCompoundDrawablesWithIntrinsicBounds(item.getDiggDrawable(), null, null, null);
                tvDiggCount.setTextColor(item.getDiggColor());
                //评论点赞
                tvDiggCount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //这个拿到外面，避免多次创建对象
                        if (!AccountHelper.isLogin()) {
                            ToastUtils.showToast(mContext, "请登录点赞");
                            LoginActivity.show(mContext);
                        } else {
                            mCommentDiggHandler.setTag(helper);
                            XHYApi.addCommentDigg(item.getObjectID(), AccountHelper.getAccount(), mCommentDiggHandler);
                        }
                    }
                });
                break;
        }
    }

    private void postVideoDiggCount(int commentsPosition, int count, int state) {
        VideoDiggEvent event = new VideoDiggEvent();
        event.setVideoPosition(mVideoPos);
        event.setCommentsPosition(commentsPosition);
        event.setChanelCode(channelCode);
        event.setCount(count);
        event.setState(state);
        event.setGoodViewCommentShow(true);
        EventBus.getDefault().post(event);
    }

    private void postCommentDigg(int position, int count) {
        CommentDiggEvent event = new CommentDiggEvent();
        event.setPosition(position);
        event.setCount(count);
        EventBus.getDefault().post(event);
    }

    private void postVideoColCount(int position, int count, int state) {
        VideoCollectEvent event = new VideoCollectEvent();
        event.setCount(count);
        event.setState(state);
        event.setRefresh(true);
        event.setVideoPosition(mVideoPos);
        event.setCommentsPosition(position);
        event.setChanelCode(channelCode);
        event.setAction(VideoCollectEvent.ADD_VIDEO);
        EventBus.getDefault().post(event);
    }

}
