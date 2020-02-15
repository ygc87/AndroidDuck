package com.xhy.neihanduanzi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.View;

import com.baidu.mobstat.StatService;
import com.umeng.analytics.MobclickAgent;
import com.xhy.neihanduanzi.base.BaseActivity;
import com.example.chaokun.neihanduanzi.R;
import com.xhy.neihanduanzi.utils.dbutils.AccountHelper;

/**
 * 启动页面
 */
public class SplashAction extends BaseActivity {

    private final String mPageName = "SplashAction";

    @Override
    public int bindLayout() {
        return R.layout.activity_splash_action;
    }

    @Override
    public void initView(View view) {
        new Thread() {
            public void run() {
                SystemClock.sleep(3000);
                Intent intent = new Intent();
                //登陆判断
                if (!AccountHelper.isLogin()) {
                    intent.setClass(SplashAction.this, DisplayFreeActivity.class);
                } else {
                    intent.setClass(SplashAction.this, MainActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }.start();
    }

    @Override
    public void doBusiness(Context mContext) {

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
