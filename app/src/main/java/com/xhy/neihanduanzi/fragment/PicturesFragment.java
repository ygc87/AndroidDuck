package com.xhy.neihanduanzi.fragment;

import android.content.Context;
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
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.example.chaokun.neihanduanzi.R;
import com.github.nukc.stateview.StateView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;
import com.victor.loading.rotate.RotateLoading;
import com.xhy.neihanduanzi.AppContext;
import com.xhy.neihanduanzi.adapter.PicturesAdapter;
import com.xhy.neihanduanzi.base.BaseDataFragment;
import com.xhy.neihanduanzi.base.BasePresenter;
import com.xhy.neihanduanzi.callback.LoadMoreListener;
import com.xhy.neihanduanzi.callback.LoadResultCallBack;
import com.xhy.neihanduanzi.model.bean.Picture;
import com.xhy.neihanduanzi.model.event.PicCollectEvent;
import com.xhy.neihanduanzi.model.event.PicDiggEvent;
import com.xhy.neihanduanzi.model.event.PicPayEvent;
import com.xhy.neihanduanzi.utils.ListUtils;
import com.xhy.neihanduanzi.utils.TDevice;
import com.xhy.neihanduanzi.utils.ToastUtils;
import com.xhy.neihanduanzi.utils.dbutils.AccountHelper;
import com.xhy.neihanduanzi.utils.dbutils.PictureRecordHelper;
import com.xhy.neihanduanzi.view.AutoLoadRecyclerView;
import com.xhy.neihanduanzi.view.GoodView;
import com.xhy.neihanduanzi.view.TipView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_FLING;
import static android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_IDLE;
import static android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;

/**
 * Created by mkt on 2018/3/21.
 */

public class PicturesFragment extends BaseDataFragment<BasePresenter> implements LoadResultCallBack<Picture>, View.OnClickListener {

    private final String mPageName = "PicturesFragment";

    @BindView(R.id.fl_content)
    FrameLayout mRootLayout;
    @BindView(R.id.recycler_view)
    AutoLoadRecyclerView mRecyclerView;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.loading)
    RotateLoading loading;
    @BindView(R.id.iv_refresh)
    ImageView mIvRefresh;
    @BindView(R.id.tip_view)
    TipView mTipView;

    protected StateView mStateView;

    private PicturesAdapter mAdapter;

    private Handler handler = new Handler();

    private String mChanelCode;

    private String mType;

    private int mLoadType = -1;

    @Override
    public void initView(View root) {
        super.initView(root);
        mRecyclerView = root.findViewById(R.id.recycler_view);
        mSmartRefreshLayout = root.findViewById(R.id.smartRefreshLayout);
        mRootLayout = root.findViewById(R.id.fl_content);
        mStateView = StateView.inject(mRootLayout);
        mStateView.setEmptyResource(R.layout.page_no_data);
        mStateView.setLoadingResource(R.layout.page_loading);
        mStateView.setRetryResource(R.layout.page_net_error);
        mStateView.showEmpty().setOnClickListener(new ReTryClickListener());
        mStateView.showRetry().setOnClickListener(new ReTryClickListener());
        mTipView = root.findViewById(R.id.tip_view);
        mIvRefresh = root.findViewById(R.id.iv_refresh);
        mIvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIvRefresh.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.refresh));
                StatService.onEvent(getActivity(), "RefreshBtn", "refresh", 1);
                mLoadType = 0;
                onLoadData(mLoadType);
            }
        });

        if (mType.equals("collect")) {
            mIvRefresh.setVisibility(View.GONE);
        }
        initAdapter();
    }

    public void initAdapter() {
        mAdapter = new PicturesAdapter(getActivity(), null, mRecyclerView, this);
    }

    @Override
    protected void loadData() {
        initData();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        View view = inflater.inflate(R.layout.fragment_pictures, null, false);
        initView(view);
        registerEventBus(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onSuccess(List<Picture> list) {
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
    }

    @Override
    public void onError() {
        if (!TDevice.hasInternet()) {
            mTipView.showError(getResources().getString(R.string.net_unable));
        }
        mIvRefresh.clearAnimation();
        if (ListUtils.isEmpty(mAdapter.getData())) {
            mStateView.showEmpty();
        } else {
            mStateView.showContent();
        }
    }

    @Override
    public void onMessage(String msg) {
        mTipView.show(msg);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void initData() {
        if (mRecyclerView != null) {
            mRecyclerView.setHasFixedSize(false);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setLoadMoreListener(new LoadMoreListener() {
                @Override
                public void loadMore() {
                    mLoadType = 1;
                    onLoadData(mLoadType);
                }
            });
            mRecyclerView.addOnScrollListener(new RecyclerScrollListener());
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
                            refreshLayout.finishRefresh();
                            refreshLayout.setNoMoreData(false);
                        }
                    }, 2000);
                }
            });

            if (mAdapter != null) {
                mRecyclerView.setAdapter(mAdapter);
                mStateView.showLoading();
                mAdapter.setAdapterType(mType);
                mAdapter.loadFirst();
            }
        }

        if (!TDevice.hasInternet()) {
            mTipView.showError(getResources().getString(R.string.net_unable));
        }
    }

    public void setChanelCode(String code) {
        mChanelCode = code;
    }

    //普通，收藏，已购
    public void setType(String type) {
        mType = type;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.fragment_pictures;
    }

    public void setAdapterType(String type) {
        mType = type;
    }

    //加载视频的数据
    private void onLoadData(final int type) {
        if (TDevice.hasInternet()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter.getItemCount() == 0) {
                        mStateView.showLoading();
                    }
                    mAdapter.loadData(type);
                }
            }, 1500);
        } else {
            mIvRefresh.clearAnimation();
            mTipView.showError(getResources().getString(R.string.net_unable));
        }
    }

    //刷新数据按钮
    @OnClick({R.id.iv_refresh})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_refresh:
                mLoadType = 0;
                onLoadData(mLoadType);
                StatService.onEvent(getActivity(), "RefreshBtn", "refresh");
                break;
        }
    }

    //更新购买状态显示
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void itemUpdatePayState(PicPayEvent event) {
        if (event != null && mAdapter != null && mAdapter.getItem(event.getPosition()) == null) {
            return;
        }
        mAdapter.getItem(event.getPosition()).setPayedState(event.getState());
        Picture.Image[] images = Picture.Image.getImageArray(event.getImageSources());
        mAdapter.getItem(event.getPosition()).setImages(images);
        mAdapter.notifyItemChanged(event.getPosition());
    }

    //更新点赞
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void itemUpdateDigg(PicDiggEvent event) {
        if (event != null && mAdapter != null && mAdapter.getItem(event.getPosition()) == null) {
            return;
        }
        int count = event.getCount();
        Picture picture = mAdapter.getItem(event.getPosition());
        picture.setDiggCount(count);
        picture.setDiggState(event.getState());
        changeDiggtUI(event.getPosition(), event.getCount(), event.isShowGoodView());
        if (!event.isShowGoodView()) {
            mAdapter.notifyItemChanged(event.getPosition());
        }
    }

    //更新收藏
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void itemUpdateCollection(PicCollectEvent event) {
        if (event == null && mAdapter == null) {
            return;
        }

        int collCount = event.getCount();
        if (event.getAction() == PicCollectEvent.ADD_PIC) {
            Picture picture = mAdapter.getItem(event.getPosition());
            picture.setCollectCount(collCount);
            picture.setCollectedState(event.getState());
            changeCollectUI(event.getPosition(), event.getCount(), true, event.isShowGoodView());
            if (!event.isShowGoodView()) {
                mAdapter.notifyItemChanged(event.getPosition());
            } else {
                mAdapter.notifyDataSetChanged();
            }
        } else if (event.getAction() == PicCollectEvent.DEL_PIC) {
            if (!mType.equals("collect")) {
                List<Picture> list = mAdapter.getData();
                int size = list.size();
                Picture picture = event.getPicture();
                if (mAdapter.getData().contains(picture)) {
                    for (int i = 0; i < size; i++) {
                        if (mAdapter.getData().get(i).getId() == picture.getId()) {
                            mAdapter.getData().get(i).setCollectedState(0);
                            break;
                        }
                    }
                }
            } else {
                onSuccess(mAdapter.getData());
            }
            //只更新position是不正确的
            mAdapter.notifyDataSetChanged();
        }
    }

    private void changeDiggtUI(int pos, int count, boolean isShow) {
        Drawable drawableLeft = getActivity().getResources().getDrawable(
                R.drawable.icon_like_light);
        TextView tvDigg = (TextView) mAdapter.getViewByPosition(mRecyclerView, pos, R.id.tv_digg);
        tvDigg.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                null, null, null);
        tvDigg.setText(String.valueOf(count));
        tvDigg.setTextColor(Color.parseColor("#f66467"));
        if (isShow) {
            GoodView goodView = new GoodView(getActivity());
            goodView.setTextInfo("+1", Color.parseColor("#f66467"), 25);
            goodView.show(tvDigg);
        }
    }

    private void changeCollectUI(int pos, int count, boolean isCheck, boolean isShow) {
        if (isCheck) {
            Drawable drawableLeft = getActivity().getResources().getDrawable(
                    R.drawable.icon_favorite_light);
            TextView tvCollect = (TextView) mAdapter.getViewByPosition(mRecyclerView, pos, R.id.tv_collected);
            tvCollect.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                    null, null, null);
            tvCollect.setText(String.valueOf(count));
            tvCollect.setTextColor(Color.parseColor("#f66467"));
            if (isShow) {
                GoodView goodView = new GoodView(getActivity());
                goodView.setTextInfo("+1", Color.parseColor("#f66467"), 25);
                goodView.show(tvCollect);
            }
        } else {
            Drawable drawableLeft = getActivity().getResources().getDrawable(
                    R.drawable.icon_favorite_gray_nor);
            TextView tvCollect = (TextView) mAdapter.getViewByPosition(mRecyclerView, pos, R.id.tv_collected);
            tvCollect.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                    null, null, null);
            tvCollect.setText(String.valueOf(count));
            tvCollect.setTextColor(Color.parseColor("#999999"));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }

        if (mType.equals("normal")) {
            StatService.onPageStart(getActivity(), "PictureNormalFragment");
        } else if (mType.equals("collect")) {
            mAdapter.setDeleteVisible(true);
            StatService.onPageStart(getActivity(), "PictureCollectedFragment");
        } else if (mType.equals("payed")) {
            StatService.onPageStart(getActivity(), "PictureHavePayedFragment");
        }

        MobclickAgent.onPageStart(mPageName);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mType.equals("normal")) {
            StatService.onPageEnd(getActivity(), "PictureNormalFragment");
        } else if (mType.equals("collect")) {
            StatService.onPageEnd(getActivity(), "PictureCollectedFragment");
        } else if (mType.equals("payed")) {
            StatService.onPageEnd(getActivity(), "PictureHavePayedFragment");
        }

        MobclickAgent.onPageEnd(mPageName);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveRecord();
        unregisterEventBus(this);
    }

    public void saveRecord() {
        if (mAdapter != null) {
            int uid = AccountHelper.getAccount().getUid();
            String json = PictureRecordHelper.pictureListToJson(mAdapter.getData());
            PictureRecordHelper.save(uid, mType, json);
        }
    }


    public class RecyclerScrollListener extends RecyclerView.OnScrollListener {
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
                    //Log.i("Main", "视图已经停止滑动");
                    Glide.with(getActivity().getApplicationContext()).resumeRequests();
                    break;
                case SCROLL_STATE_TOUCH_SCROLL:
                    //Log.i("Main","手指没有离开屏幕，视图正在滑动");
                    System.out.println("手指没有离开屏幕，视图正在滑动");
                    Glide.with(getActivity().getApplicationContext()).resumeRequests();
                    break;
            }
        }
    }

    public class ReTryClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (TDevice.hasInternet()) {
                mLoadType = 0;
                onLoadData(mLoadType);
            } else {
                mStateView.showRetry();
                String msg = getString(R.string.tip_network_error);
                ToastUtils.showToast(AppContext.context(), msg);
            }
        }
    }
}
