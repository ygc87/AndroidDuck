package com.xhy.neihanduanzi.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mobstat.StatService;
import com.umeng.analytics.MobclickAgent;
import com.xhy.neihanduanzi.adapter.ScoreGiftAdapter;
import com.xhy.neihanduanzi.callback.LoadMoreListener;
import com.xhy.neihanduanzi.callback.LoadResultCallBack;
import com.xhy.neihanduanzi.media.SpaceGridItemDecoration;
import com.xhy.neihanduanzi.model.event.GiftBuyEvent;
import com.xhy.neihanduanzi.utils.TDevice;
import com.xhy.neihanduanzi.view.AutoLoadRecyclerView;
import com.example.chaokun.neihanduanzi.R;
import com.xhy.neihanduanzi.utils.ToastUtils;
import com.lidroid.xutils.exception.DbException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 积分商城
 */

public class ScoreShopFragment extends BaseFragment implements LoadResultCallBack {

    private final String mPageName = "ScoreShopFragment";

    @BindView(R.id.pull_refresh_list)
    AutoLoadRecyclerView mRecyclerView;
    @BindView(R.id.smart_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private ScoreGiftAdapter mAdapter;

    public static ScoreShopFragment init(String arg3) {
        ScoreShopFragment f = new ScoreShopFragment();
        Bundle b = new Bundle();
        b.putString("type", arg3);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerEventBus(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_gift, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (TDevice.hasInternet()) {
            if (mRecyclerView != null) {
                mRecyclerView.setHasFixedSize(false);
                mRecyclerView.addItemDecoration(new SpaceGridItemDecoration((int) TDevice.dipToPx(getResources(), 3)));
                mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                mRecyclerView.setLoadMoreListener(new LoadMoreListener() {
                    @Override
                    public void loadMore() throws DbException {
                        mAdapter.loadNextPage();
                    }
                });

                mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                        android.R.color.holo_green_light,
                        android.R.color.holo_orange_light,
                        android.R.color.holo_red_light);
                mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (TDevice.hasInternet()) {
                            try {
                                mAdapter.loadFirst();
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        } else {
                            ToastUtils.showToast(getContext(), "网络异常");
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });

                try {
                    mAdapter = new ScoreGiftAdapter(getActivity(), mRecyclerView, this);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                mRecyclerView.setAdapter(mAdapter);
                try {
                    mAdapter.loadFirst();
                } catch (DbException e) {
                    e.printStackTrace();
                }

                //loading.start();
            }
        } else {
            ToastUtils.showToast(getContext(), "网络异常，请检查你的网络！");
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onSuccess(List list) {
        if (TDevice.hasInternet()) {
            //loading.stop();
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        } else {
            ToastUtils.showToast(getContext(), "网络异常");
        }
    }

    @Override
    public void onError() {
        if (TDevice.hasInternet()) {
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        } else {
            ToastUtils.showToast(getContext(), "网络异常");
        }
    }

    @Override
    public void onMessage(String msg) {

    }


    //添加数据
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void updateGiftList(GiftBuyEvent event) {
        if (mAdapter != null) {
            mAdapter.loadData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterEventBus(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_all_gift;
    }

    public boolean isEventBusRegisted(Object subscribe) {
        return EventBus.getDefault().isRegistered(subscribe);
    }


    public void registerEventBus(Object subscribe) {
        if (!isEventBusRegisted(subscribe)) {
            EventBus.getDefault().register(subscribe);
        }
    }

    public void unregisterEventBus(Object subscribe) {
        if (isEventBusRegisted(subscribe)) {
            EventBus.getDefault().unregister(subscribe);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onPageStart(getActivity(), mPageName);
        MobclickAgent.onPageStart(mPageName);
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPageEnd(getActivity(), mPageName);
        MobclickAgent.onPageEnd(mPageName);
    }

}
