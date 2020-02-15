package com.xhy.neihanduanzi;

import android.app.Activity;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.commonsdk.UMConfigure;
import com.xhy.neihanduanzi.app.api.ApiHttpClient;
import com.xhy.neihanduanzi.app.api.XHYApi;
import com.xhy.neihanduanzi.lockdemo.util.constant.Constant;
import com.xhy.neihanduanzi.utils.NetWorkUtil;
import com.xhy.neihanduanzi.utils.SPUtils;
import com.xhy.neihanduanzi.utils.dbutils.AccountHelper;

import java.util.HashSet;
import java.util.Set;

import cz.msebera.android.httpclient.Header;

public class XHYApplication extends AppContext {

    private static Context sContext;

    private static XHYApplication instance;

    private Set<Activity> allActivities;

    private String mSelectedChannelJson;

    private TextHttpResponseHandler mChangelHandler = new TextHttpResponseHandler() {

        @Override
        public void onStart() {
            super.onStart();

        }

        @Override
        public void onFinish() {
            super.onFinish();

        }

        @Override
        public void onCancel() {
            super.onCancel();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                String selectedChannelJson = SPUtils.getString(getApplicationContext(), Constant.SELECTED_CHANNEL_JSON, "");
                if (selectedChannelJson != null && !selectedChannelJson.equals(responseString)) {
                    mSelectedChannelJson = responseString;
                }
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(statusCode, headers, responseString, e);
            }
        }
    };

    public static XHYApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化操作
        sContext = this;
        instance = this;
        init();
    }

    public static void reInit() {
        (getInstance()).init();
    }

    private void init() {
        // 初始化异常捕获类
        //AppCrashHandler.getInstance().init(this);
//        // 初始化账户基础信息
        //AccountHelper.init(this);
//        // 初始化网络请求
        ApiHttpClient.init(this);

        UMConfigure.init(this, "5b610bee8f4a9d17af000177", "xiaohuangya", UMConfigure.DEVICE_TYPE_PHONE,
                "");

        requestChanel();
    }

    public void registerActivity(Activity act) {
        if (allActivities == null) {
            allActivities = new HashSet<Activity>();
        }
        allActivities.add(act);
    }

    public void unregisterActivity(Activity act) {
        if (allActivities != null) {
            allActivities.remove(act);
        }
    }

    public void exitApp() {
        if (allActivities != null) {
            synchronized (allActivities) {
                for (Activity act : allActivities) {
                    if (act != null && !act.isFinishing())
                        act.finish();
                }
            }
        }
        if (mSelectedChannelJson != null) {
            SPUtils.putString(getApplicationContext(), Constant.SELECTED_CHANNEL_JSON, mSelectedChannelJson);
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void requestChanel() {
        //判断网络情况
        if (NetWorkUtil.isNetWorkConnected(this) && AccountHelper.getAccount() != null) {
            XHYApi.getChannelList(AccountHelper.getAccount(), mChangelHandler);
        }
    }

    public static Context getAppContext() {
        return sContext;
    }

}
