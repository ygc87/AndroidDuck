package com.xhy.neihanduanzi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.umeng.analytics.MobclickAgent;
import com.xhy.neihanduanzi.activity.DisplayFreeActivity;
import com.xhy.neihanduanzi.activity.MainActivity;
import com.xhy.neihanduanzi.activity.RegisterActivity;
import com.xhy.neihanduanzi.base.BaseActivity;

import com.xhy.neihanduanzi.lockdemo.activity.GestureLoginActivity;
import com.xhy.neihanduanzi.lockdemo.util.cache.ACache;
import com.example.chaokun.neihanduanzi.R;
import com.xhy.neihanduanzi.model.bean.Constants;
import com.xhy.neihanduanzi.utils.dbutils.AccountHelper;


/**
 * 应用启动界面
 */
public class LaunchActivity extends BaseActivity {

    boolean isFirstIn = false;
    // 常量标志
    private static final int GO_HOME = 1000;
    private static final int GO_GUIDE = 1001;
    private static final int CHECK_UPDATE_SUCC = 1002;
    private static final int CHECK_UPDATE_FAIL = 1003;
    // 延迟3秒
    private static final long SPLASH_DELAY_MILLIS = 1800;

    private static final String SHAREDPREFERENCES_NAME = "first_pref";

    private final String mPageName = "LaunchActivity";

    private ACache aCache;
    private Bitmap splashBitmap;
    private int screenWidth, screenHeight;

    private static final String TAG = "LaunchActivity";
    //值唯一即可,这是为了返回做标识使用
    private final int REQUEST_SETTING = 10;
    final String[] permissionArrays = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_SMS};
    final int permissionSize = permissionArrays.length;
    final int[] permissionInfo = {R.string.open_storage_permit};
    final int infoSize = permissionInfo.length;

    /**
     * Handler:跳转到不同界面
     */
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_HOME:
                    goHome();
                    break;
                case GO_GUIDE:
                    goGuide();
                    break;
                case CHECK_UPDATE_SUCC:
//                    AppVersion appVersion = (AppVersion) msg.obj;
//                    UpdateManager um = new UpdateManager(mContext, appVersion);
//                    if (!um.checkUpdate()) {
//                        init();
//                        break;
//                    }
//                    um.setUpdateListener(updateListener);
                    break;
                case CHECK_UPDATE_FAIL:
                    Log.e("", "获取更新失败");
                    init();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected int getContentView() {
        return R.layout.activity_splash_action;
    }


    @Override
    protected void initData() {
        super.initData();
        XHYApplication.reInit();
        init();
        redirectTo();
    }

    private void redirectTo() {
        Intent intent = new Intent();

        if (!AccountHelper.isLogin()) {
            intent.setClass(LaunchActivity.this, DisplayFreeActivity.class);
        } else {
            intent.setClass(LaunchActivity.this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_splash_action;
    }

    @Override
    public void initView(View view) {
        // 读取SharedPreferences中需要的数据
        // 使用SharedPreferences来记录程序的使用次数
        SharedPreferences preferences = getSharedPreferences(
                SHAREDPREFERENCES_NAME, MODE_PRIVATE);

        // 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
        isFirstIn = preferences.getBoolean("isFirstIn", true);

        // 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
        // / if(UpdateManager.)
        if (!isFirstIn) {
            // 使用Handler的postDelayed方法，3秒后执行跳转到MainActivity
            mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
        } else {
            mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
        }

        aCache = ACache.get(this);

        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        screenHeight = getWindowManager().getDefaultDisplay().getHeight();

//        splashBitmap = BitmapUtil.resizeBitmap(screenWidth, screenHeight - getStatusBarHeight(),
//                BitmapFactory.decodeResource(getResources(), R.mipmap.splash));
    }

    @Override
    public void doBusiness(Context mContext) {
    }

    private void init() {
        // 读取SharedPreferences中需要的数据
        // 使用SharedPreferences来记录程序的使用次数
        SharedPreferences preferences = getSharedPreferences(
                SHAREDPREFERENCES_NAME, MODE_PRIVATE);

        // 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
        isFirstIn = preferences.getBoolean("isFirstIn", true);
        // 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
        // / if(UpdateManager.)
        if (!isFirstIn) {
            //使用Handler的postDelayed方法，3秒后执行跳转到MainActivity
            mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
        } else {
            mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isFirstIn", false);
            editor.commit();
        }
    }

    // 跳转到解锁界面
    private void goHome() {
        Intent intent = new Intent(LaunchActivity.this, GestureLoginActivity.class);
        if (!AccountHelper.isLogin()) {
            intent.setClass(LaunchActivity.this, DisplayFreeActivity.class);
        } else {
            intent.setClass(LaunchActivity.this, MainActivity.class);
        }
        LaunchActivity.this.startActivity(intent);
        LaunchActivity.this.finish();
    }

    // 跳转到向导界面
    private void goGuide() {
        Intent intent = new Intent();
        if (!AccountHelper.isLogin()) {
            intent.putExtra(Constants.LEFT_BTN_VISIBLE,false);
            intent.setClass(LaunchActivity.this, RegisterActivity.class);
        } else {
            intent.setClass(LaunchActivity.this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mPageName);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
    }
}
