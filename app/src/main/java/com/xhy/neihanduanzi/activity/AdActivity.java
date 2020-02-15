package com.xhy.neihanduanzi.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chaokun.neihanduanzi.BuildConfig;
import com.example.chaokun.neihanduanzi.R;
import com.lidroid.xutils.exception.DbException;
import com.xhy.neihanduanzi.base.BaseActivity;
import com.xhy.neihanduanzi.contract.AdContract;
import com.xhy.neihanduanzi.presenter.AdPresenterImpl;
import com.xhy.neihanduanzi.utils.NetWorkUtil;
import com.xhy.neihanduanzi.utils.SPUtils;
import com.xhy.neihanduanzi.utils.ToastUtils;
import com.xhy.neihanduanzi.utils.dbutils.AccountHelper;
import com.xhy.neihanduanzi.view.ResizableImageView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 启动页广告页面
 */

public class AdActivity extends BaseActivity implements AdContract.View {
    // 常量标志
    private static final int GO_HOME = 1000;
    private static final int CHECK_DOMAIN = 1001;
    private static final int AD_SECOEND = 1002;

    boolean continueCount = true;

    private Bitmap mBitmap;
    private AdPresenterImpl pAd;
    private int initTimeCount;

    @BindView(R.id.tv_second)
    TextView tvSecond;
    @BindView(R.id.app_version)
    TextView tvVersion;
    @BindView(R.id.layout_skip)
    LinearLayout layoutSkip;
    @BindView(R.id.iv_advertising)
    ResizableImageView ivAdvertising;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GO_HOME:
                    goHome();
                    break;
                case AD_SECOEND:
                    if (continueCount) {
                        if (initTimeCount <= 0) {
                            continueCount = false;
                            if (handler != null && handler.hasMessages(0)) {
                                handler.removeMessages(0);
                            }
                            toNextActivity();
                        } else {
                            initTimeCount = initTimeCount - 1;
                            tvSecond.setText(String.valueOf(initTimeCount));
                            handler.sendEmptyMessageDelayed(AD_SECOEND, 1000);
                        }
                    }
                    break;
            }
        }
    };

    @Override
    public void initView(View view) throws DbException {
        handler.sendEmptyMessage(CHECK_DOMAIN);
        pAd = new AdPresenterImpl();
        pAd.attachView(this);

        layoutSkip = view.findViewById(R.id.layout_skip);
        ivAdvertising = view.findViewById(R.id.iv_advertising);
        tvSecond = (TextView) view.findViewById(R.id.tv_second);
        tvVersion = (TextView) view.findViewById(R.id.app_version);
        ivAdvertising.setVisibility(View.VISIBLE);
        tvVersion.setText(BuildConfig.VERSION_NAME);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (NetWorkUtil.isNetWorkConnected(AdActivity.this)) {
            pAd.getAdBean();
        } else {
            showEmptyAd();
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_ad;
    }

    @OnClick({R.id.iv_advertising, R.id.layout_skip})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_advertising:
                if (NetWorkUtil.isNetWorkConnected(AdActivity.this)) {
                    String url = (String) SPUtils.get(this, "adUrl", "null");
                    if (!url.equals("null")) {
                        continueCount = false;
                        Intent intent = new Intent(AdActivity.this, WebViewActivity.class);
                        intent.putExtra("web_title", "广告");
                        intent.putExtra("web_url", url);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    ToastUtils.showToast(this, getResources().getString(R.string.tip_network_check));
                }
                break;
            case R.id.layout_skip:
                continueCount = false;
                toNextActivity();
                finish();
                break;
        }
    }

    public void toNextActivity() {
        handler.sendEmptyMessage(GO_HOME);
    }

    @Override
    public void setAdTime(int count) {
        initTimeCount = count;
    }

    @Override
    public void setAdImg(Bitmap bitmap) {
        if (bitmap != null) {
            mBitmap = bitmap;
            ivAdvertising.setImageBitmap(mBitmap);
            handler.sendEmptyMessage(AD_SECOEND);
        } else {//加强用户体验，如果是获取到的bitmap为null，则直接跳过
            continueCount = false;
            toNextActivity();
            finish();
        }
    }

    @Override
    public void showEmptyAd() {
        setLayoutSkipVisible(View.INVISIBLE);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.splash);
        setAdImg(bitmap);
        setAdTime(3);
    }

    @Override
    public void setLayoutSkipVisible(int visible) {
        layoutSkip.setVisibility(visible);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBitmap != null) {
            mBitmap.recycle();
        }
        pAd.detachView();
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_ad;
    }


    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void showToast(String message) {

    }

    @Override
    public void showAlertDialog(String title, String message) {

    }

    @Override
    public void hideProgressDialog() {

    }

    @Override
    public void showProgressDialog(String message) {

    }

    private void goHome() {
        Intent intent = new Intent();
        if (!AccountHelper.isLogin()) {
            intent.setClass(AdActivity.this, RegisterActivity.class);
        } else {
            intent.setClass(AdActivity.this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }

}
