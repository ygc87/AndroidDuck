package com.xhy.neihanduanzi.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.analytics.MobclickAgent;
import com.xhy.neihanduanzi.XHYApplication;
import com.xhy.neihanduanzi.improve.base.BasePresenter;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.xhy.neihanduanzi.model.event.ExitAppEvent;
import com.xhy.neihanduanzi.utils.ToastUtils;
import com.xhy.neihanduanzi.videoplayer.VideoPlayerManager;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 整个activity的基类
 *
 * @author linchaokun
 */
public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity implements IBaseActivity {

    protected Unbinder unbinder;
    protected T mPresenter;

    /**
     * 当前Activity渲染的视图View
     **/
    public View mContextView = null;
    public Context context;

    private long exitTime;

    //是否在前台
    public boolean isActive = false;

    public Handler mHandler = new Handler(Looper.getMainLooper());

    public KProgressHUD hud;

    @Override
    public void finish() {
        super.finish();
        //overridePendingTransition(R.anim.anim_none, R.anim.trans_center_2_right);
    }

    public void initdata() {

    }

    protected RequestManager mImageLoader;


    private boolean mBackState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 添加Activity到堆栈
        StatService.setDebugOn(true);

        // 设置渲染视图View
        mContextView = LayoutInflater.from(this).inflate(bindLayout(), null);
        setContentView(mContextView);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        context = this;
        ViewUtils.inject(this);
        ButterKnife.bind(this);
        // 初始化业务数据
        initBundle(savedInstanceState);
        // 初始化控件
        try {
            initView(mContextView);
            XHYApplication.getInstance().registerActivity(this);
        } catch (DbException e) {
            e.printStackTrace();
        }
        // 初始化数据
        initdata();
        initWidget();
        //初始化控件
        doBusiness(this);
    }

    protected abstract int getContentView();

    protected boolean initBundle(Bundle bundle) {


        return true;
    }

    protected void initWindow() {
    }

    protected void initWidget() {
    }

    protected void initData() {
    }

    public void onClick(View v) {
    }

    public void initView() {
    }

    public synchronized RequestManager getImageLoader() {
        if (mImageLoader == null)
            mImageLoader = Glide.with(this);
        return mImageLoader;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        XHYApplication.getInstance().unregisterActivity(this);
    }

    protected static View getRootView(Activity context) {
        return ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isAppOnForeground()) {
            isActive = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    protected void setBackKeyState(boolean backState) {
        mBackState = backState;
    }

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public boolean isAppOnForeground() {
        // Returns a list of application processes that are running on the
        // device

        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }


    public void showLoading() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (hud != null && !isFinishing()) {
                    hud.show();
                }
            }
        }, 500);
    }

    public void dissLoading() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (hud != null && !isFinishing()) {
                    hud.dismiss();
                }
            }
        }, 500);
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

    //back键退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mBackState &&
                keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (VideoPlayerManager.getInstance().onBackPressed()) {
                if ((System.currentTimeMillis() - exitTime) > 1000) {
                    ToastUtils.showToast(this, "再按一次退出程序");
                    exitTime = System.currentTimeMillis();
                } else {
                    postExitEvent();
                    XHYApplication.getInstance().exitApp();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void postExitEvent() {
        ExitAppEvent event = new ExitAppEvent();
        EventBus.getDefault().post(event);
    }
}

