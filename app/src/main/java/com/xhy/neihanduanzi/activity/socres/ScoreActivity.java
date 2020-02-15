package com.xhy.neihanduanzi.activity.socres;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.umeng.analytics.MobclickAgent;
import com.xhy.neihanduanzi.app.AppOperator;
import com.xhy.neihanduanzi.app.api.XHYApi;
import com.xhy.neihanduanzi.base.BaseActivity;
import com.xhy.neihanduanzi.utils.dbutils.AccountHelper;
import com.xhy.neihanduanzi.model.bean.Score;
import com.xhy.neihanduanzi.model.bean.ScoreDetail;
import com.xhy.neihanduanzi.callback.LoadResultCallBack;
import com.xhy.neihanduanzi.utils.StringUtils;
import com.xhy.neihanduanzi.utils.TDevice;
import com.xhy.neihanduanzi.utils.ToastUtils;
import com.example.chaokun.neihanduanzi.R;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.lidroid.xutils.exception.DbException;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by mkt on 2017/9/26.
 * <p>
 * 积分详情页
 */

public class ScoreActivity extends BaseActivity implements LoadResultCallBack {

    private static final int FIXED_DISPLAY_COUNT = 5;

    private final String mPageName = "ScoreActivity";

    /**
     * 积分规则
     */
    @BindView(R.id.tv_score_rule)
    TextView mScoreRule;
    /**
     * 返回键
     */
    @BindView(R.id.iv_title_left)
    ImageView mBack;
    /**
     * 积分
     */
    @BindView(R.id.tv_my_score)
    TextView mScore;
    /**
     * 兑换
     */
    @BindView(R.id.tv_exchange_now)
    TextView mExchange;
    /**
     * 充值
     */
    @BindView(R.id.tv_chongzhi)
    TextView mChongzhi;
    /**
     * 积分明细列表
     */

    @BindView(R.id.pull_refresh_list)
    ListView mListView;

    //积分数据
    private ScoreAdapter mAdapter;


    @Override
    protected int getContentView() {
        return 0;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_my_score;
    }

    @Override
    public void initView(View view) throws DbException {
        if (TDevice.hasInternet()) {
            if (mListView != null) {
                View headView = LayoutInflater.from(ScoreActivity.this).inflate(R.layout.score_layout_head, null);
                View footView = LayoutInflater.from(ScoreActivity.this).inflate(R.layout.layout_score_foot, null);
                mListView.addHeaderView(headView);
                headView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ScoreDetailActivity.show(ScoreActivity.this);
                    }
                });
                mListView.addFooterView(footView);
                footView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ScoreDetailActivity.show(ScoreActivity.this);
                    }
                });

                hud = KProgressHUD.create(ScoreActivity.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setCancellable(true)
                        .setAnimationSpeed(2)
                        .setDimAmount(0.5f);
            }
        } else {
            ToastUtils.showToast(this, getResources().getString(R.string.tip_network_check));
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    private void loadData() {
        if (AccountHelper.isLogin()) {
            if (mAdapter == null) {
                mAdapter = new ScoreAdapter(this);
                //只显示前4个积分详情
                mAdapter.setLimit(FIXED_DISPLAY_COUNT);
                //添加底部数据
                mListView.setAdapter(mAdapter);
            }
            mAdapter.loadData();
        }
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick({R.id.iv_title_left, R.id.tv_score_rule, R.id.tv_my_score,
            R.id.tv_chongzhi, R.id.tv_exchange_now})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            //返回
            case R.id.iv_title_left:
                finish();
                break;
            //积分规则
            case R.id.tv_score_rule:
                ScoreRuleActivity.show(ScoreActivity.this);
                break;
            //我的积分
            case R.id.tv_my_score:
                break;
            case R.id.tv_exchange_now:
                ScoreShopActivity.show(ScoreActivity.this);
                break;
            case R.id.tv_chongzhi:
                ChongzhiActivity.show(ScoreActivity.this);
                break;
            default:
                break;
        }
    }

    /**
     * show the login activity_lock_screen
     *
     * @param context context
     */
    public static void show(Context context) {
        Intent intent = new Intent(context, ScoreActivity.class);
        context.startActivity(intent);
    }

    //更新用户的积分
    private void updateScore() {
        showLoading();
        XHYApi.getTotalScore(AccountHelper.getAccount(), new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                ToastUtils.showErr(ScoreActivity.this);
                dissLoading();
            }

            @SuppressWarnings("ConstantConditions")
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Score score = AppOperator.createGson().fromJson(responseString, Score.class);
                int totalScore = score.getScore();
                mScore.setText(String.valueOf(totalScore));
                //写入数据库
                dissLoading();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dissLoading();
            }

            @Override
            public void onCancel() {
                super.onCancel();
                dissLoading();
            }
        }, 0);
    }


    @Override
    public void onSuccess(List list) {
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onError() {
    }

    @Override
    public void onMessage(String msg) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
        updateScore();
        StatService.onPageStart(this, mPageName);
        MobclickAgent.onPageStart(mPageName);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPageEnd(this, mPageName);
        MobclickAgent.onPageEnd(mPageName);
    }

    private class ScoreAdapter extends BaseAdapter {

        private List<ScoreDetail> scoreList;

        private int mLimit = 0;

        private ScoreAdapter(Context context) {
            scoreList = new ArrayList<>();
        }


        private void setLimit(int limit) {
            mLimit = limit;
        }


        private void loadData() {
            int page = 1;
            XHYApi.getScoreDetailList(AccountHelper.getAccount(), page, mLimit, new TextHttpResponseHandler() {

                @Override
                public void onStart() {
                    super.onStart();
                    showLoading();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    dissLoading();
                }

                @SuppressWarnings("ConstantConditions")
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        scoreList = stringToList(responseString, ScoreDetail.class);
                        notifyDataSetChanged();
                        updateScore();
                    } catch (Exception e) {
                        e.printStackTrace();
                        onFailure(statusCode, headers, responseString, e);
                    }
                    dissLoading();
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    dissLoading();
                }

                @Override
                public void onCancel() {
                    super.onCancel();
                    dissLoading();
                }
            });
        }

        public <T> List<T> stringToList(String json, Class<T> cls) {
            Gson gson = new Gson();
            List<T> list = new ArrayList<T>();
            JsonArray array = new JsonParser().parse(json).getAsJsonArray();
            for (final JsonElement elem : array) {
                list.add(gson.fromJson(elem, cls));
            }
            return list;
        }

        @Override
        public int getCount() {
            return scoreList.size();
        }

        @Override
        public Object getItem(int position) {
            return scoreList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_my_score_detail, parent, false);
            final ScoreDetail detail = scoreList.get(position);

            TextView tvDetailName = convertView.findViewById(R.id.tv_my_score_detail_name);
            TextView tvDetailTime = convertView.findViewById(R.id.tv_my_score_detail_time);
            TextView tvDetailResult = convertView.findViewById(R.id.tv_my_score_detail_result);

            if (detail != null) {
                tvDetailName.setText(detail.getAction());
                tvDetailResult.setText(detail.getScore());
                String time = String.valueOf(detail.getCtime());
                tvDetailTime.setText(StringUtils.timedate(time));
                tvDetailResult.setText(detail.getScore());
                int score = 0;
                try {
                    score = Integer.parseInt(detail.getScore());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (score > 0) {
                    tvDetailResult.setTextColor(getResources().getColor(R.color.bg_my_score_reuslt));
                } else {
                    tvDetailResult.setTextColor(getResources().getColor(R.color.themeColor));
                }

            }

            return convertView;
        }
    }
}
