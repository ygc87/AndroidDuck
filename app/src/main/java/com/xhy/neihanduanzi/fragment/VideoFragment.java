package com.xhy.neihanduanzi.fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.chaokun.neihanduanzi.R;
import com.github.nukc.stateview.StateView;
import com.lidroid.xutils.exception.DbException;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;
import com.victor.loading.rotate.RotateLoading;
import com.xhy.neihanduanzi.AppContext;
import com.xhy.neihanduanzi.adapter.VideoAdapter;
import com.xhy.neihanduanzi.base.BasePresenter;
import com.xhy.neihanduanzi.base.BaseDataFragment;
import com.xhy.neihanduanzi.callback.LoadMoreListener;
import com.xhy.neihanduanzi.callback.LoadResultCallBack;
import com.xhy.neihanduanzi.model.bean.Video;
import com.xhy.neihanduanzi.model.event.ExitAppEvent;
import com.xhy.neihanduanzi.model.event.VideoCollectEvent;
import com.xhy.neihanduanzi.model.event.VideoCommentEvent;
import com.xhy.neihanduanzi.model.event.VideoDiggEvent;
import com.xhy.neihanduanzi.model.event.VideoLoadDataEvent;
import com.xhy.neihanduanzi.model.event.VideoPayEvent;
import com.xhy.neihanduanzi.model.event.VideoPlayerEvent;
import com.xhy.neihanduanzi.utils.ListUtils;
import com.xhy.neihanduanzi.utils.TDevice;
import com.xhy.neihanduanzi.utils.ToastUtils;
import com.xhy.neihanduanzi.utils.dbutils.AccountHelper;
import com.xhy.neihanduanzi.utils.dbutils.VideoRecordHelper;
import com.xhy.neihanduanzi.videoplayer.VideoPlayerManager;
import com.xhy.neihanduanzi.videoplayer.VideoPlayerView;
import com.xhy.neihanduanzi.view.AutoLoadRecyclerView;
import com.xhy.neihanduanzi.view.GoodView;
import com.xhy.neihanduanzi.view.TipView;
import com.xhy.neihanduanzi.widget.XHYPlayer;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_FLING;
import static android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_IDLE;
import static android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;


public class VideoFragment extends BaseDataFragment<BasePresenter> implements LoadResultCallBack<Video> {

    private final String mPageName = "VideoFragment";

    @BindView(R.id.recycler_view)
    AutoLoadRecyclerView mRecyclerView;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.fl_content)
    FrameLayout rootLayout;
    @BindView(R.id.loading)
    RotateLoading loading;
    @BindView(R.id.iv_refresh)
    ImageView mIvRefresh;
    @BindView(R.id.tip_view)
    TipView mTipView;

    private VideoAdapter mAdapter;

    private String mChanelCode;

    private String mType;

    protected StateView mStateView;//用于显示加载中、网络异常，空布局、内容布局

    private Handler mHandler = new Handler();

    private int mLoadType = -1;

    @Override
    public void initView(View root) {
        super.initView(root);

        mRecyclerView = root.findViewById(R.id.recycler_view);
        mSmartRefreshLayout = root.findViewById(R.id.smartRefreshLayout);
        rootLayout = root.findViewById(R.id.fl_content);
        mStateView = StateView.inject(rootLayout);
        mStateView.setLoadingResource(R.layout.page_loading);
        mStateView.setRetryResource(R.layout.page_net_error);
        mStateView.setEmptyResource(R.layout.page_no_data);
        mTipView = root.findViewById(R.id.tip_view);
        mStateView.showEmpty().setOnClickListener(new ReTryClickListener());
        mStateView.showRetry().setOnClickListener(new ReTryClickListener());

        mIvRefresh = root.findViewById(R.id.iv_refresh);
        mIvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatService.onEvent(getActivity(), "RefreshBtn", "refresh", 1);
                mIvRefresh.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.refresh));
                mLoadType = 0;
                onLoadData(mLoadType);
            }
        });
    }

    public void initAdapter() {
        List<Video> videoList = new ArrayList<Video>();
        mAdapter = new VideoAdapter(getActivity(), videoList,  mRecyclerView, this);
        mAdapter.setVideoAdapterType(mType);
        mAdapter.setChanelcode(mChanelCode);
    }

    public void setChanelCode(String code) {
        mChanelCode = code;
    }

    //普通，收藏，已购
    public void setType(String type) {
        mType = type;
    }

    public void setRefreshButtonVisible(int visible) {
        mIvRefresh.setVisibility(visible);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auto_load, null, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    //加载视频的数据
    private void onLoadData(final int type) {
        if (TDevice.hasInternet()) {
            if (mAdapter.getItemCount() == 0) {
                mStateView.showLoading();
            }
            mSmartRefreshLayout.setVisibility(View.VISIBLE);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter != null) {
                        mAdapter.loadDataByNetworkType(type);
                    }
                }
            }, 1500);
        } else {
            if(this.mStateView != null){
                mTipView.showError(getResources().getString(R.string.net_unable));
                mIvRefresh.clearAnimation();
                mStateView.showRetry();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onSuccess(List<Video> list) {
        if (!TDevice.hasInternet()) {
            mTipView.showError(getResources().getString(R.string.net_unable));
            mStateView.showRetry();
        } else {
            if (ListUtils.isEmpty(list)) {
                mStateView.showEmpty();
            } else {
                mStateView.showContent();//显示内容
                if (mLoadType == 0) {
                    mRecyclerView.scrollToPosition(0);
                }
            }
        }
        mIvRefresh.clearAnimation();
        mSmartRefreshLayout.finishRefresh();
    }

    @Override
    public void onError() {
        if (!TDevice.hasInternet()) {
            mTipView.showError(getResources().getString(R.string.net_unable));
            mStateView.showRetry();
        }
        if (ListUtils.isEmpty(mAdapter.getData())) {
            mStateView.showEmpty();
        } else {
            mStateView.showContent();//显示内容
        }
        mIvRefresh.clearAnimation();
        mSmartRefreshLayout.finishRefresh();
    }

    @Override
    public void onMessage(String msg) {
        mTipView.show(msg);
    }

    @Override
    public void initData() {
        initAdapter();
        if (mRecyclerView != null) {
            mRecyclerView.setHasFixedSize(false);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.addOnScrollListener(new RecyclerScrollListener());
            mRecyclerView.setLoadMoreListener(new LoadMoreListener() {
                @Override
                public void loadMore() throws DbException {

                    mIvRefresh.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.refresh));
                    mLoadType = 1;
                    onLoadData(mLoadType);
                    // mRecyclerView.scrollToPosition();
                }
            });

            mSmartRefreshLayout.setEnableAutoLoadMore(true);//开启自动加载功能（非必须）
            mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                    mIvRefresh.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.refresh));
                    refreshLayout.getLayout().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mLoadType = 0;
                            onLoadData(mLoadType);
                            //refreshLayout.finishRefresh();
                            refreshLayout.setNoMoreData(false);
                        }
                    }, 2000);
                }
            });

            if (mAdapter != null) {
                mRecyclerView.setAdapter(mAdapter);
                mStateView.showLoading();
                mAdapter.loadFirst();
            }
        }
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.fragment_auto_load;
    }

    @Override
    protected void loadData() {
        initData();
    }

    //更新点赞
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void itemUpdateDigg(VideoDiggEvent event) {
        if (event == null || mAdapter == null) {
            return;
        }
        if (event.getChanelCode().equals(mChanelCode)) {
            mAdapter.getItem(event.getVideoPosition()).setDiggCount(event.getCount());
            mAdapter.getItem(event.getVideoPosition()).setDiggState(event.getState());
            Drawable drawableLeft = getActivity().getResources().getDrawable(
                    R.drawable.icon_like_light);
            TextView tvDigg = (TextView) mAdapter.getViewByPosition(mRecyclerView, event.getVideoPosition(), R.id.tv_digg);
            tvDigg.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                    null, null, null);
            tvDigg.setText(String.valueOf(event.getCount()));
            tvDigg.setTextColor(Color.parseColor("#f66467"));
            if (event.isShowGoodViewVideo()) {
                GoodView goodView = new GoodView(getActivity());
                goodView.setTextInfo("+1", Color.parseColor("#f66467"), 25);
                goodView.show(tvDigg);
            } else {
                mAdapter.notifyItemChanged(event.getVideoPosition());
            }
        }
    }

    //更新收藏
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void itemUpdateCollection(VideoCollectEvent event) {
        if (event == null || mAdapter == null) {
            return;
        }
        if (event.getAction() == VideoCollectEvent.ADD_VIDEO) {
            if (mAdapter.getVideoAdapterType().equals("collect")) {
                event.getVideo().setCollectedState(event.getState());
                mAdapter.getData().add(0, event.getVideo());
            } else {
                if (mAdapter.getChanelCode() == mAdapter.getChanelCode()) {
                    mAdapter.getItem(event.getVideoPosition()).setUserLikeCount(event.getCount());
                    mAdapter.getItem(event.getVideoPosition()).setCollectedState(event.getState());
                    changeCollectUI(event.getVideoPosition(), event.getCount(), true, event.isShowGoodViewVideo());
                }
            }
            if (event.isRefresh()) {
                mAdapter.notifyItemChanged(event.getVideoPosition());
            }
        } else if (event.getAction() == VideoCollectEvent.DEL_VIDEO) {
            if (!mAdapter.getVideoAdapterType().equals("collect") ) {
                List<Video> list = mAdapter.getData();
                int size = list.size();
                Video video = event.getVideo();
                for (int i = 0; i < size; i++) {
                    if (list.get(i).getId() == video.getId()) {
                        list.get(i).setCollectedState(event.getState());
                        break;
                    }
                }

            } else {
                onSuccess(mAdapter.getData());
            }
            mAdapter.notifyItemChanged(event.getVideoPosition());
        }
    }


    private void changeCollectUI(int pos, int count, boolean isCheck, boolean isShow) {
        if (isCheck) {
            Drawable drawableLeft = getActivity().getResources().getDrawable(R.drawable.icon_favorite_light);
            TextView tvCollect = (TextView) mAdapter.getViewByPosition(mRecyclerView, pos, R.id.tv_collected);
            tvCollect.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
            tvCollect.setText(String.valueOf(count));
            tvCollect.setTextColor(Color.parseColor("#f66467"));
            if (isShow) {
                GoodView goodView = new GoodView(getActivity());
                goodView.setTextInfo("+1", Color.parseColor("#f66467"), 25);
                goodView.show(tvCollect);
            }
        } else {
            Drawable drawableLeft = getActivity().getResources().getDrawable(R.drawable.icon_favorite_gray_nor);
            TextView tvCollect = (TextView) mAdapter.getViewByPosition(mRecyclerView, pos, R.id.tv_collected);
            tvCollect.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
            tvCollect.setText(String.valueOf(count));
            tvCollect.setTextColor(Color.parseColor("#999999"));
        }

    }

    //更新评论
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void itemUpdateComment(VideoCommentEvent event) {
        if (event != null && mAdapter != null) {
            //取消收藏高亮显示
            Video video = mAdapter.getItem(event.getPosition());
            video.setCommentCount(event.getCount());
        }
    }

    //更新购买状态显示
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void itemUpdatePayState(VideoPayEvent event) {
        if (event != null && mAdapter != null) {
            mAdapter.getItem(event.getPosition()).setPayedState(event.getState());
            if (event.isNotifyChange()) {
                mAdapter.notifyDataSetChanged();
            }
            onSuccess(mAdapter.getData());
        }
    }

    //更新已购状态
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void itemUpdatePlayState(VideoPlayerEvent event) {
        if (event != null && mAdapter != null) {
            //取消收藏高亮显示
            boolean isPayed = mAdapter.getItem(event.getPosition()).isPayed();
            BaseViewHolder holer = (BaseViewHolder) event.getViewHodler();
            if (isPayed && holer != null) {
                ImageView ivPayed = holer.getView(R.id.iv_payed);
                if (event.getPlayState() == XHYPlayer.STATE_PLAY) {
                    ivPayed.setVisibility(View.GONE);
                } else if (event.getPlayState() == XHYPlayer.STATE_PAUSE || event.getPlayState() == XHYPlayer.STATE_RESET) {
                    ivPayed.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    //添加数据
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void addListData(VideoLoadDataEvent event) {
        if (event != null && mAdapter != null && mChanelCode.equals(event.getChannel())) {
            if (mAdapter.getAdapterType() == event.getType()) {
                mAdapter.loadData(event.getData());
            }
            if (mAdapter.getItemCount() == 0) {
                mStateView.showEmpty();
            }
        }
    }

    //添加数据
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onExitApp(ExitAppEvent event) {
        saveRecord();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        StatService.onPageEnd(getActivity(), mChanelCode + "Fragment");
        MobclickAgent.onPageStart(mPageName);
    }

    @Override
    public void onPause() {
        super.onPause();
        VideoPlayerManager.getInstance().releaseVideoPlayer();
        StatService.onPageEnd(getActivity(), mChanelCode + "Fragment");
        MobclickAgent.onPageEnd(mPageName);
    }

    @Override
    public void onStart() {
        super.onStart();
        registerEventBus(VideoFragment.this);
    }

    @Override
    public void onDestroy() {
        saveRecord();
        VideoPlayerManager.getInstance().onDestroy();
        unregisterEventBus(VideoFragment.this);
        super.onDestroy();
    }

    public void saveRecord() {
        if (mAdapter != null) {
            try {
                int uid = AccountHelper.getUserId();
                String json = VideoRecordHelper.videoListToJson(mAdapter.getData());
                VideoRecordHelper.save(uid, mChanelCode, json);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                    Glide.with(getActivity().getApplicationContext()).pauseRequests();
                    //刷新
                    break;
                case SCROLL_STATE_IDLE:
                    System.out.println("视图已经停止滑动");
                    Glide.with(getActivity().getApplicationContext()).resumeRequests();
                    break;
                case SCROLL_STATE_TOUCH_SCROLL:
                    System.out.println("手指没有离开屏幕，视图正在滑动");
                    Glide.with(getActivity().getApplicationContext()).resumeRequests();
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
                    ResumeView(fistView);
                    fistView = recyclerView.getChildAt(0);
                    lastView = recyclerView.getChildAt(visibleItemCount - 1);
                } else if (_lastItemPosition < lastItemPosition) {
                    _firstItemPosition = firstItemPosition;
                    _lastItemPosition = lastItemPosition;
                    ResumeView(lastView);
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

        /**
         * 恢复播放
         *
         * @param gcView
         */
        public void ResumeView(View gcView) {
//            if (gcView != null && gcView.findViewById(R.id.videoplayer) != null) {
//                VideoPlayerView videoplayer = (VideoPlayerView) gcView.findViewById(R.id.videoplayer);
//                XHYSuperPlayer xhySuperPlayer = (XHYSuperPlayer) videoplayer.getTag();
//                if (xhySuperPlayer != null && !xhySuperPlayer.isPlaying()){
//                    xhySuperPlayer.startPlayer();
//                }
//            }
        }
    }


    public class ReTryClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (TDevice.hasInternet()) {
                onLoadData(0);
            } else {
                mStateView.showRetry();
                String msg = getString(R.string.tip_network_error);
                ToastUtils.showToast(AppContext.context(), msg);
            }
        }
    }
}
