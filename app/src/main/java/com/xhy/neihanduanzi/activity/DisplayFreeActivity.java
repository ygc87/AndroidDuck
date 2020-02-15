package com.xhy.neihanduanzi.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.github.nukc.stateview.StateView;
import com.umeng.analytics.MobclickAgent;
import com.xhy.neihanduanzi.AppContext;
import com.xhy.neihanduanzi.adapter.VideoAdapter;
import com.xhy.neihanduanzi.base.BaseActivity;
import com.xhy.neihanduanzi.callback.LoadResultCallBack;
import com.xhy.neihanduanzi.model.bean.BannerBean;
import com.xhy.neihanduanzi.model.event.BannerLoadEvent;
import com.xhy.neihanduanzi.model.event.VideoLoadDataEvent;
import com.xhy.neihanduanzi.utils.ListUtils;
import com.xhy.neihanduanzi.utils.TDevice;
import com.xhy.neihanduanzi.utils.ToastUtils;
import com.xhy.neihanduanzi.videoplayer.VideoPlayerView;
import com.xhy.neihanduanzi.view.AutoLoadRecyclerView;
import com.example.chaokun.neihanduanzi.R;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.xhy.neihanduanzi.view.RecycleViewDivider;
import com.xhy.neihanduanzi.widget.XHYPlayer;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_FLING;
import static android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_IDLE;
import static android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;

/**
 * 用于展示免费的视频页
 */
public class DisplayFreeActivity extends BaseActivity implements LoadResultCallBack {

    private final String mPageName = "DisplayFreeActivity";

    @BindView(R.id.recycler_view)
    AutoLoadRecyclerView mRecyclerView;

    //登录按钮
    @BindView(R.id.tv_login_btn)
    TextView tvLogingBtn;

    //注册按钮
    @BindView(R.id.tv_register_btn)
    TextView tvRegisterBtn;

    //Content
    @BindView(R.id.content)
    FrameLayout flContent;

    //BannerBean
    @BindView(R.id.ad_banner)
    LinearLayout adBanner;

    //Empty view
    protected StateView mStateView;

    List<BannerBean> bannerList = new ArrayList<>();

    private Banner mBanner;

    //视频适配器
    private VideoAdapter mAdapter;

    @Override
    protected boolean initBundle(Bundle savedInstanceState) {
        super.initBundle(savedInstanceState);
        return true;
    }

    private void initAdapter() {
        //mAdapter = new VideoAdapter(DisplayFreeActivity.this, null, VideoAdapter.VIDEO_TYPE_FREE, mRecyclerView, this);
        //mAdapter.setAdapterEnum(VideoAdapter.AdapterEnum.FVIDEO);
    }

    @Override
    public void initView(View view) {

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);

        mStateView = StateView.inject(flContent);
        mStateView.setLoadingResource(R.layout.page_loading);
        mStateView.setRetryResource(R.layout.page_net_error);
        mStateView.setEmptyResource(R.layout.page_no_data);

        mStateView.showRetry().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TDevice.hasInternet()) {
//                    if (mAdapter != null) {
//                        mAdapter.loadFreeData();
//                    }
                } else {
                    String msg = getString(R.string.tip_network_error);
                    ToastUtils.showToast(AppContext.context(), msg);
                }
            }
        });
        if (!TDevice.hasInternet()) {
            mStateView.showRetry();
            return;
        } else {
            initAdapter();
            registerEventBus(this);
            //添加Banner
            View header = LayoutInflater.from(this).inflate(R.layout.list_item_video_banner, adBanner, false);
            mBanner = (Banner) header;
            mBanner.setImageLoader(new GlideImageLoader());
            adBanner.addView(mBanner);
            mBanner.setOnBannerListener(new OnBannerListener() {
                @Override
                public void OnBannerClick(int position) {
                    StatService.onEvent(DisplayFreeActivity.this, "FreePageBanner", "banner_click");
                    LoginActivity.show(DisplayFreeActivity.this);
                }
            });
            if (mRecyclerView != null) {

                mRecyclerView.setHasFixedSize(false);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                mRecyclerView.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL, 12, Color.parseColor("#FFFFFF")));
                mRecyclerView.addOnScrollListener(new RecyclerScrollListener());
                mRecyclerView.setAdapter(mAdapter);
                mStateView.showLoading();
                //mAdapter.loadFreeData();
                mAdapter.openLoadAnimation();
            }
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_display_free;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_display_free;
    }

    @OnClick({R.id.tv_login_btn, R.id.tv_register_btn})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_login_btn:
                StatService.onEvent(DisplayFreeActivity.this, "DisplayFreeActivity", "login_btn");
                LoginActivity.show(DisplayFreeActivity.this);
                break;
            case R.id.tv_register_btn:
                StatService.onEvent(DisplayFreeActivity.this, "DisplayFreeActivity", "register_btn");
                RegisterActivity.show(DisplayFreeActivity.this);
                break;
            //返回
            default:
                break;
        }
    }

    @Override
    public void onSuccess(List list) {
        if (!TDevice.hasInternet()) {
            ToastUtils.showToast(this, "网络异常");
        } else {
            if (ListUtils.isEmpty(list)) {
                mStateView.showEmpty();
            } else {
                mStateView.showContent();
            }
        }
    }

    @Override
    public void onError() {
        if (!TDevice.hasInternet()) {
            mStateView.showRetry();
            ToastUtils.showToast(this, "网络异常");
        } else {
            if (mAdapter.getItemCount() == 0) {
                mStateView.showEmpty();
            } else {
                mStateView.showContent();
            }
        }
    }

    @Override
    public void onMessage(String msg) {

    }


    /**
     * show the ScoreRuleActivity activity_lock_screen
     *
     * @param context context
     */
    public static void show(Context context) {
        Intent intent = new Intent(context, DisplayFreeActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterEventBus(this);
    }

    //定义视频页
    private int page = 1;

    //添加数据
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void addListData(VideoLoadDataEvent event) {
        if (event != null && mAdapter != null) {
            if (mAdapter.getAdapterType() == event.getType()) {
                mAdapter.loadData(event.getData());
                mStateView.showContent();
            }
            if (mAdapter.getItemCount() == 0) {
                mStateView.showEmpty();
            }
        }
    }

    //添加数据
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void addBannerData(BannerLoadEvent event) {
        if (event != null) {
            bannerList = event.getData();
            List<String> list = new ArrayList<String>();
            for (BannerBean banner : bannerList) {
                list.add(banner.getDisplayImage());
            }
            mBanner.setImages(list);
            mBanner.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBackKeyState(true);
        StatService.onPageStart(this, mPageName);
        MobclickAgent.onPageStart(mPageName);

    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPageEnd(this, mPageName);
        MobclickAgent.onPageEnd(mPageName);
    }

    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(DisplayFreeActivity.this)
                    .load(path)
                    .into(imageView);
        }
    }

    public class RecyclerScrollListener extends RecyclerView.OnScrollListener {
        private int _firstItemPosition = -1, _lastItemPosition;
        private View fistView, lastView;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
            //super.onScrollStateChanged(recyclerView, scrollState);
            //每一条数据都是一个Map
            switch (scrollState) {
                case SCROLL_STATE_FLING:
                    System.out.println("用户在手指离开屏幕之前，由于滑了一下，视图仍然依靠惯性继续滑动");
                    Glide.with(getApplicationContext()).pauseRequests();
                    //刷新
                    break;
                case SCROLL_STATE_IDLE:
                    System.out.println("视图已经停止滑动");
                    Glide.with(getApplicationContext()).resumeRequests();
                    break;
                case SCROLL_STATE_TOUCH_SCROLL:
                    System.out.println("手指没有离开屏幕，视图正在滑动");
                    Glide.with(getApplicationContext()).resumeRequests();
                    break;
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            // 判断是当前layoutManager 是否为 LinearLayoutManager
            // 只有LinearLayoutManager 才有查找第一个和最后一个可见 view 位置的方法
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                //获取最后一个可见view的位置
                int lastItemPosition = linearManager.findLastVisibleItemPosition();
                //获取第一个可见view的位置
                int firstItemPosition = linearManager.findFirstVisibleItemPosition();
                //获取可见view的总数
                int visibleItemCount = linearManager.getChildCount();

                if (_firstItemPosition < firstItemPosition) {
                    _firstItemPosition = firstItemPosition;
                    _lastItemPosition = lastItemPosition;
                    GCView(fistView);
                    fistView = recyclerView.getChildAt(0);
                    lastView = recyclerView.getChildAt(visibleItemCount - 1);
                } else if (_lastItemPosition > lastItemPosition) {
                    _firstItemPosition = firstItemPosition;
                    _lastItemPosition = lastItemPosition;
                    GCView(lastView);
                    fistView = recyclerView.getChildAt(0);
                    lastView = recyclerView.getChildAt(visibleItemCount - 1);
                } else if (_firstItemPosition > firstItemPosition) {
                    _firstItemPosition = firstItemPosition;
                    _lastItemPosition = lastItemPosition;
                    fistView = recyclerView.getChildAt(0);
                    lastView = recyclerView.getChildAt(visibleItemCount - 1);
                } else if (_lastItemPosition < lastItemPosition) {
                    _firstItemPosition = firstItemPosition;
                    _lastItemPosition = lastItemPosition;
                    fistView = recyclerView.getChildAt(0);
                    lastView = recyclerView.getChildAt(visibleItemCount - 1);
                }
            }
        }

        /**
         * 回收播放
         *
         * @param gcView
         */
        public void GCView(View gcView) {
            if (gcView != null && gcView.findViewById(R.id.videoplayer) != null) {
                VideoPlayerView videoplayer = gcView.findViewById(R.id.videoplayer);
                XHYPlayer xhyPlayer = (XHYPlayer) videoplayer.getTag();
                if (xhyPlayer != null && xhyPlayer.isPlaying()) {
                    xhyPlayer.reset();
                    xhyPlayer.onDestroy();
                }
            }
        }
    }
}