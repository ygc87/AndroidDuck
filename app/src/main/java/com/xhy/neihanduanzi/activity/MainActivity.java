package com.xhy.neihanduanzi.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import com.umeng.analytics.MobclickAgent;
import com.xhy.neihanduanzi.activity.socres.ScoreRuleActivity;
import com.xhy.neihanduanzi.model.bean.Account;
import com.xhy.neihanduanzi.model.event.LoadAllDataEvent;
import com.xhy.neihanduanzi.model.event.ScoreChangeEvent;
import com.xhy.neihanduanzi.update.CheckUpdateManager;
import com.xhy.neihanduanzi.utils.StringUtils;
import com.xhy.neihanduanzi.utils.TextUtil;
import com.xhy.neihanduanzi.videoplayer.VideoPlayerManager;
import com.xhy.neihanduanzi.app.AppOperator;
import com.xhy.neihanduanzi.app.api.XHYApi;
import com.xhy.neihanduanzi.base.BaseActivity;
import com.xhy.neihanduanzi.model.bean.SignBean;
import com.xhy.neihanduanzi.utils.dbutils.AccountHelper;
import com.xhy.neihanduanzi.fragment.NavFragment;
import com.xhy.neihanduanzi.utils.ToastUtils;
import com.baidu.mobstat.StatService;
import com.example.chaokun.neihanduanzi.R;
import com.loopj.android.http.TextHttpResponseHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends BaseActivity {

    private final String mPageName = "MainActivity";

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private NavFragment mNavFragment;

    public boolean isReset = true;

    private boolean isCheckUpdate = false;

    @Override
    public int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(View view) {
        mNavFragment = new NavFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        mNavFragment.setup(MainActivity.this, manager, R.id.frame_container);
        ft.add(R.id.frame_container, mNavFragment, "NavFragment");
        ft.commit();

        boolean isHasShow = getIntent().getBooleanExtra("isHasShow", false);
        CheckUpdateManager updateManager = new CheckUpdateManager(MainActivity.this, false);
        updateManager.checkUpdate(isHasShow);
        boolean isRegister = getIntent().getBooleanExtra("isRegister", false);
        String msg = getIntent().getStringExtra("register_msg");
        if (isRegister && !StringUtils.isEmpty(msg)) {
            showAlertDialog(msg, null, "知道了", "获取积分", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ScoreRuleActivity.show(MainActivity.this);
                }
            });
        }
    }


    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void initdata() {
        super.initdata();
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        Account account = AccountHelper.getAccount();
        //每天执行签到
        chcekin(account);
        return true;
    }

    @Override
    protected int getContentView() {
        return 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBackKeyState(true);
        //pgyUpdateMethod();
        VideoPlayerManager.getInstance().onResume();
        StatService.onPageStart(this, mPageName);
        MobclickAgent.onPageStart(mPageName);
    }

    @Override
    protected void onPause() {
        super.onPause();
        VideoPlayerManager.getInstance().onPause(isReset);
        StatService.onPageEnd(this, mPageName);
        MobclickAgent.onPageEnd(mPageName);
    }

    //签到功能
    public void chcekin(Account account) {
        final Account account1 = account;
        if (AccountHelper.isLogin()) {
            TextHttpResponseHandler handler = new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString
                        , Throwable throwable) {
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        final SignBean signBean = AppOperator.createGson().fromJson(responseString, SignBean.class);
                        if (!signBean.getCheckState()) {
                            XHYApi.signin(account1, new TextHttpResponseHandler() {
                                @Override
                                public void onStart() {
                                    super.onStart();
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString
                                        , Throwable throwable) {
                                }

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                                    try {
                                        SignBean signBean = AppOperator.createGson().fromJson(responseString, SignBean.class);
                                        ToastUtils.show(MainActivity.this, signBean.getMessage(), 300);
                                        //post Score change Event
                                        ScoreChangeEvent event = new ScoreChangeEvent();
                                        EventBus.getDefault().post(event);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFinish() {
                                    super.onFinish();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                }
            };
            XHYApi.checkSignin(account, handler);
        }
    }

    @Override
    public void onBackPressed() {
        if (VideoPlayerManager.getInstance().onBackPressed()) {
            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
        //overridePendingTransition(R.anim.bottom_push_out, 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        registerEventBus(MainActivity.this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        VideoPlayerManager.getInstance().onDestroy();
        unregisterEventBus(MainActivity.this);
    }

    //添加数据
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataLoadFinish(LoadAllDataEvent event) {
        if (!isCheckUpdate) {
            isCheckUpdate = true;
        }
    }

    //写一个alterDialog 方法
    private void showAlertDialog(String title, String msg, String negativeText, String positiveText, DialogInterface.OnClickListener posListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(title);
        if (!TextUtil.isNull(msg)) {
            builder.setMessage(msg);
        }

        builder.setCancelable(false);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

        builder.setPositiveButton(positiveText, posListener);
        builder.show();
    }
}
