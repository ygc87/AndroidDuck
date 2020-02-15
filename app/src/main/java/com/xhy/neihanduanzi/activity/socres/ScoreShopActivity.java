package com.xhy.neihanduanzi.activity.socres;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.umeng.analytics.MobclickAgent;
import com.xhy.neihanduanzi.base.BaseActivity;
import com.xhy.neihanduanzi.fragment.ScoreShopFragment;
import com.example.chaokun.neihanduanzi.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 积分商店
 */

public class ScoreShopActivity extends BaseActivity {

    private ScoreShopFragment mScoreShopFragment;

    private final String mPageName = "ScoreShopActivity";

    /**
     * 页面标题
     */
    @BindView(R.id.tv_title_center)
    TextView mTitle;


    @Override
    protected boolean initBundle(Bundle bundle) {
        initFragment();
        return super.initBundle(bundle);
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_score_shop;
    }

    @Override
    public void initView(View view) {
        String title = getResources().getString(R.string.tv_gift_shop);
        mTitle.setText(title);
        initFragment();
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    private void initFragment() {
        if (!(mScoreShopFragment instanceof ScoreShopFragment)) {
            mScoreShopFragment = ScoreShopFragment.init("VIP");
            FragmentTransaction f = this.getSupportFragmentManager().beginTransaction();
            f.replace(R.id.fl_content, mScoreShopFragment);
            f.commit();
        }
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
            //立即充值
            case R.id.btn_top_up_now:
                break;
            default:
                break;
        }
    }

    /**
     * show the score shop  activity_lock_screen
     *
     * @param context context
     */
    public static void show(Context context) {
        Intent intent = new Intent(context, ScoreShopActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return 0;
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
