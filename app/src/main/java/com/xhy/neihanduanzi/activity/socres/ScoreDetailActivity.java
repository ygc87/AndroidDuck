package com.xhy.neihanduanzi.activity.socres;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.umeng.analytics.MobclickAgent;
import com.xhy.neihanduanzi.callback.LoadResultCallBack;
import com.xhy.neihanduanzi.utils.TDevice;
import com.example.chaokun.neihanduanzi.R;
import com.xhy.neihanduanzi.adapter.ScoreDetailAdapter;
import com.xhy.neihanduanzi.base.BaseActivity;
import com.xhy.neihanduanzi.callback.LoadMoreListener;
import com.xhy.neihanduanzi.utils.ToastUtils;
import com.xhy.neihanduanzi.view.AutoLoadRecyclerView;
import com.xhy.neihanduanzi.view.RecycleViewDivider;
import com.lidroid.xutils.exception.DbException;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 积分详情页
 */
public class ScoreDetailActivity extends BaseActivity implements LoadResultCallBack {

    private final String mPageName = "ScoreDetailActivity";

    /**
     * 返回键
     */
    @BindView(R.id.iv_title_left)
    ImageView mBack;
    @BindView(R.id.tv_title_center)
    TextView mTitle;
    @BindView(R.id.pull_refresh_list)
    AutoLoadRecyclerView mRecyclerView;

    @BindView(R.id.smart_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    private ScoreDetailAdapter mAdapter;

    @Override
    protected boolean initBundle(Bundle savedInstanceState) {
        super.initBundle(savedInstanceState);
        return true;
    }

    @Override
    public void initView(View view) {
        String title = getResources().getString(R.string.tv_score_detail);
        mTitle.setText(title);
        if (TDevice.hasInternet()) {
            if (mRecyclerView != null) {
                mRecyclerView.setHasFixedSize(false);
                //添加分割线
                mRecyclerView.addItemDecoration(new RecycleViewDivider(
                        this, LinearLayoutManager.HORIZONTAL));

                mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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
                            mAdapter.loadFirst();
                        } else {
                            ToastUtils.showToast(ScoreDetailActivity.this, "网络异常");
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });

                try {
                    mAdapter = new ScoreDetailAdapter(this, mRecyclerView, this);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.loadFirst();

                //loading.start();
            }
        } else {
            ToastUtils.showToast(this, "网络异常，请检查你的网络！");
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_score_detail;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_score_detail;
    }


    @Override
    public void doBusiness(Context mContext) {

    }


    @SuppressWarnings("ConstantConditions")
    @OnClick({R.id.iv_title_left})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            //返回
            case R.id.iv_title_left:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onSuccess(List list) {
        if (TDevice.hasInternet()) {
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        } else {
            ToastUtils.showToast(this, "网络异常");
        }
    }

    @Override
    public void onError() {
        if (TDevice.hasInternet()) {
            //loading.stop();
            //ToastUtils.showToast(this, LOAD_FAILED);
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        } else {
            ToastUtils.showToast(this, "网络异常");
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
        Intent intent = new Intent(context, ScoreDetailActivity.class);
        context.startActivity(intent);
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
}

