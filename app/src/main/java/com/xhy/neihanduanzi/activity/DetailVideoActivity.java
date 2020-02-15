package com.xhy.neihanduanzi.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.chaokun.neihanduanzi.R;
import com.lidroid.xutils.exception.DbException;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;
import com.xhy.neihanduanzi.Interface.DetailContract;
import com.xhy.neihanduanzi.app.AppOperator;
import com.xhy.neihanduanzi.app.api.XHYApi;
import com.xhy.neihanduanzi.base.BaseActivity;
import com.xhy.neihanduanzi.fragment.CommentFragment;
import com.xhy.neihanduanzi.model.bean.StatusCount;
import com.xhy.neihanduanzi.model.bean.Video;
import com.xhy.neihanduanzi.model.event.VideoCollectEvent;
import com.xhy.neihanduanzi.model.event.VideoCommentEvent;
import com.xhy.neihanduanzi.model.event.VideoPlayerEvent;
import com.xhy.neihanduanzi.utils.TDevice;
import com.xhy.neihanduanzi.utils.ToastUtils;
import com.xhy.neihanduanzi.utils.UIHelper;
import com.xhy.neihanduanzi.utils.dbutils.AccountHelper;
import com.xhy.neihanduanzi.utils.glide.ImageLoader;
import com.xhy.neihanduanzi.videoplayer.VideoPlayerManager;
import com.xhy.neihanduanzi.videoplayer.VideoPlayerView;
import com.xhy.neihanduanzi.view.CommentBar;
import com.xhy.neihanduanzi.widget.XHYPlayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;


/**
 * 视频详情页面
 */

public class DetailVideoActivity extends BaseActivity implements DetailContract.Operator {

    public static final String BUNDLE_KEY_POSITION = "BUNDLE_KEY_POSITION";
    public static final String BUNDLE_KEY_VIDEO = "BUNDLE_KEY_VIDEO";
    private static final String mPageName = "DetailVideoActivity";

    @BindView(R.id.layout_coordinator)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.fragment_container)
    FrameLayout mFrameLayout;
    //播放器
    @BindView(R.id.video)
    VideoPlayerView videoPlayerView;
    // 返回上一个页面按钮
    @BindView(R.id.back_layout)
    RelativeLayout backBtn;
    // 收藏按钮
    @BindView(R.id.video_star)
    ImageView imStart;
    // 播放器
    XHYPlayer xhyPlayer;
    //
    private Video mVideo;
    // 底部发表评论UI和逻辑
    private CommentBar mDelegation;

    private CommentFragment mCommentFragment;

    /**表示该条vedio在视频列表中的索引位置**/
    private int mPosition = 0;

    public static void show(Context context, Object object) {
        Intent intent = new Intent(context, DetailVideoActivity.class);
        intent.putExtra(BUNDLE_KEY_VIDEO, (Serializable) object);
        context.startActivity(intent);
    }

    public static void show(Context context, int position, Object object) {
        Intent intent = new Intent(context, DetailVideoActivity.class);
        intent.putExtra(BUNDLE_KEY_VIDEO, (Serializable) object);
        intent.putExtra(BUNDLE_KEY_POSITION, position);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_video_detail;
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        mVideo = (Video) getIntent().getSerializableExtra(BUNDLE_KEY_VIDEO);
        mPosition = getIntent().getIntExtra(BUNDLE_KEY_POSITION, 0);
        registerEventBus(DetailVideoActivity.this);
        return super.initBundle(bundle);
    }

    @Override
    protected void initData() {
    }

    // 发表评论
    private TextHttpResponseHandler publishVideoCommentHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            ToastUtils.showToast(getApplicationContext(), "评论失败");
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("msg", responseString);
            StatService.onEvent(DetailVideoActivity.this, "DetailVideoActivity", "pub_comment_fail", 1, hashMap);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                StatusCount status = AppOperator.createGson().fromJson(responseString, StatusCount.class);
                if (status != null) {
                    ToastUtils.showToast(getApplicationContext(), status.getMessage());
                    if (status.getStatus() == 1) {
                        //这条要重新修改
                        mCommentFragment.needClearData();
                        mCommentFragment.requestData();
                        mDelegation.getBottomSheet().dismiss();
                        int count = status.getCount();
                        postCommentCount(mPosition, count, mVideo);
                        StatService.onEvent(DetailVideoActivity.this, "DetailVideoActivity", "pub_comment_success");
                    } else {
                        StatService.onEvent(DetailVideoActivity.this, "DetailVideoActivity", "pub_comment_fail");
                    }
                }
            } catch (Exception e) {
                ToastUtils.showToast(getApplicationContext(), "出现异常，请稍后再试");
                e.printStackTrace();
            }
        }

        @Override
        public void onStart() {
            super.onStart();
            if (mDelegation == null) return;
            mDelegation.getBottomSheet().dismiss();
            mDelegation.setCommitButtonEnable(false);
        }

        @Override
        public void onFinish() {
            super.onFinish();
            if (mDelegation == null) return;
            mDelegation.getBottomSheet().dismiss();
            mDelegation.setCommitButtonEnable(true);
        }
    };

    //收藏回调
    private TextHttpResponseHandler mCollHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            ToastUtils.showToast(getApplicationContext(), "收藏失败");
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                StatusCount status = AppOperator.createGson().fromJson(responseString, StatusCount.class);
                if (status.getStatus() == 1) {
                    postColCount(mPosition, status.getCount(), 1);
                    Drawable drawable = getApplicationContext().getResources().getDrawable(R.drawable.icon_favorite_light);
                    imStart.setBackground(drawable);
                }
                ToastUtils.showToast(getApplicationContext(), status.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public void onFinish() {
            super.onFinish();
        }
    };

    protected void initWidget() {
        registerEventBus(DetailVideoActivity.this);

        //设置收藏颜色
        if (mVideo != null && mVideo.isCollected() == 1) {
            Drawable drawable = getApplicationContext().getResources().getDrawable(R.drawable.icon_favorite_light);
            imStart.setBackground(drawable);
        }

        xhyPlayer = new XHYPlayer(DetailVideoActivity.this, videoPlayerView, null);
        xhyPlayer.setPosition(mPosition);
        xhyPlayer.setVideo(mVideo);

        RequestOptions options = new RequestOptions();
        //options.fitCenter();
        options.placeholder(R.drawable.player_preview);
        options.diskCacheStrategy(DiskCacheStrategy.ALL);
        ImageLoader.loadImage(this, mVideo.getCoverImageURI(), videoPlayerView.getPreviewImage(), options);

        mDelegation = CommentBar.delegation(this, mCoordinatorLayout);
        mDelegation.getBottomSheet().setCommitListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TDevice.hasInternet()) {
                    ToastUtils.showToast(getApplicationContext(), "网络异常");
                }
                String content = mDelegation.getBottomSheet().getCommentText().replaceAll("[\\s\\n]+", " ");
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(DetailVideoActivity.this, "请输入文字", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!AccountHelper.isLogin()) {
                    UIHelper.showLoginActivity(DetailVideoActivity.this);
                    return;
                }
                //发布评论
                if (mVideo != null) {
                    int videoId = (int) mVideo.getId();
                    String table = mVideo.getChanelCode();
                    StatService.onEvent(DetailVideoActivity.this, "DetailVideoActivity", "pub_comment");
                    XHYApi.pubVideoComment(videoId, content, AccountHelper.getAccount(), table, publishVideoCommentHandler);
                }
            }
        });

        mCommentFragment = CommentFragment.newInstance(mPosition,mVideo);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mCommentFragment)
                .commit();
    }

    //回退键
    @OnClick(R.id.back_layout)
    void onClickBack() {
        super.onBackPressed();
    }

    //收藏键
    @OnClick(R.id.start_layout)
    void onClickStar() {
        if (!TDevice.hasInternet()) {
            ToastUtils.showToast(getApplicationContext(), "网络异常");
        }
        if (mVideo.getChanelCode().equals("svideo")) {
            XHYApi.addSvideoCollected((int) mVideo.getId(), AccountHelper.getAccount(), mCollHandler);
        } else if (mVideo.getChanelCode().equals("lvideo")) {
            XHYApi.addLvideoCollected((int) mVideo.getId(), AccountHelper.getAccount(), mCollHandler);
        }
    }

    //更新点赞
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void updateUIVisible(VideoPlayerEvent event) {
        if (event != null) {
            if (event.getPlayState() == XHYPlayer.STATE_PLAY) {
                imStart.setVisibility(View.GONE);
                backBtn.setVisibility(View.GONE);
            } else if (event.getPlayState() == XHYPlayer.STATE_PAUSE || event.getPlayState() == XHYPlayer.STATE_RESET) {
                imStart.setVisibility(View.VISIBLE);
                backBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    //更新收藏
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void itemUpdateCollection(VideoCollectEvent event) {
        if (event != null) {
            Drawable drawable = getApplicationContext().getResources().getDrawable(R.drawable.icon_favorite_light);
            imStart.setBackground(drawable);
        }
    }

    @Override
    public void onBackPressed() {
        //退出全屏用
        if (VideoPlayerManager.getInstance().onBackPressed()) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onPageStart(this, mPageName);
        MobclickAgent.onPageStart(mPageName);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPageEnd(this, mPageName);
        MobclickAgent.onPageEnd(mPageName);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (xhyPlayer != null) {
            xhyPlayer.onDestroy();
        }
        unregisterEventBus(DetailVideoActivity.this);
    }

    private void postColCount(int position, int count, int state) {
        VideoCollectEvent event = new VideoCollectEvent();
        event.setVideoPosition(position);
        event.setAction(VideoCollectEvent.ADD_VIDEO);
        event.setChanelCode(mVideo.getChanelCode());
        event.setCount(count);
        event.setState(state);
        event.setVideo(mVideo);
        event.setRefresh(false);
        EventBus.getDefault().post(event);
    }

    private void postCommentCount(int position, int count, Video video) {
        VideoCommentEvent event = new VideoCommentEvent();
        event.setPosition(position);
        event.setCount(count);
        event.setVideo(video);
        EventBus.getDefault().post(event);
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_video_detail;
    }

    @Override
    public void initView(View view) throws DbException {

    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
