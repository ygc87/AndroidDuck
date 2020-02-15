package com.xhy.neihanduanzi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.umeng.analytics.MobclickAgent;
import com.xhy.neihanduanzi.app.AppOperator;
import com.xhy.neihanduanzi.app.api.XHYApi;
import com.xhy.neihanduanzi.base.AccountBaseActivity;
import com.xhy.neihanduanzi.utils.TDevice;
import com.example.chaokun.neihanduanzi.R;
import com.xhy.neihanduanzi.model.bean.PhoneToken;
import com.loopj.android.http.TextHttpResponseHandler;
import com.xhy.neihanduanzi.view.TipView;

import butterknife.BindView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by fei
 * on 2016/10/14.
 * desc:
 */

public class ResetPwdActivity extends AccountBaseActivity implements View.OnClickListener, View.OnFocusChangeListener,
        ViewTreeObserver.OnGlobalLayoutListener {

    public static final String PHONE_TOKEN_KEY = "phoneToken";

    private final String mPageName = "ResetPwdActivity";

    //返回键
    @BindView(R.id.iv_title_left)
    ImageView mBack;

    //返回键
    @BindView(R.id.tv_title_center)
    TextView mTitle;

    //新密码
    @BindView(R.id.ed_newpwd)
    EditText mEdPwd;

    //新密码确认
    @BindView(R.id.ed_newpwd_comfirm)
    EditText mEdPwdComfiem;

    //完成
    @BindView(R.id.bt_next_step)
    Button mBtResetSubmit;

    //提示
    @BindView(R.id.tip_view)
    TipView mTipView;

    private PhoneToken mPhoneToken;
    private TextHttpResponseHandler mHandler = new TextHttpResponseHandler() {

        @Override
        public void onStart() {
            super.onStart();
            showWaitDialog(R.string.progress_submit);
        }

        @Override
        public void onFinish() {
            super.onFinish();
            hideWaitDialog();
        }

        @Override
        public void onCancel() {
            super.onCancel();
            hideWaitDialog();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            requestFailureHint(throwable);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            PhoneToken token = AppOperator.createGson().fromJson(responseString, PhoneToken.class);
            int code = token.getStatus();
            switch (code) {
                case 1:
                    mTipView.show(getResources().getString(R.string.reset_success_hint));
                    LoginActivity.show(ResetPwdActivity.this);
                    finish();
                    break;
                case -1:
                case -2:
                    mTipView.showError(token.getMessage());
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected boolean initBundle(Bundle bundle) {
        mPhoneToken = (PhoneToken) getIntent().getSerializableExtra(PHONE_TOKEN_KEY);
        return true;
    }

    /**
     * show the resetPwdActivity
     *
     * @param context context
     */
    public static void show(Context context, PhoneToken phoneToken) {
        Intent intent = new Intent(context, ResetPwdActivity.class);
        intent.putExtra(PHONE_TOKEN_KEY, phoneToken);
        context.startActivity(intent);
    }

    //加载布局文件
    @Override
    public int bindLayout() {
        return R.layout.activity_reset_pwd;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_reset_pwd;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        String title = getResources().getString(R.string.reset_password);
        mTitle.setText(title);
        mEdPwd.setOnFocusChangeListener(this);
        mEdPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressWarnings("deprecation")
            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                if (length >= 6) {
                    mBtResetSubmit.setEnabled(true);
                    mBtResetSubmit.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();//必须要调用,用来注册本地广播
        Intent intent = getIntent();
        mPhoneToken = (PhoneToken) intent.getSerializableExtra(PHONE_TOKEN_KEY);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideKeyBoard(getCurrentFocus().getWindowToken());
    }


    @SuppressWarnings("ConstantConditions")
    @OnClick({R.id.iv_title_left, R.id.bt_next_step})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_title_left:
                finish();
                break;
            //提交新的密码
            case R.id.bt_next_step:
                requestResetPwd();
                break;
            default:
                break;
        }
    }

    private void requestResetPwd() {
        if (!TDevice.hasInternet()) {
            String msg = getBaseContext().getResources().getString(R.string.tip_network_error);
            mTipView.showError(msg);
            return;
        }

        String tempPwd = mEdPwd.getText().toString();
        String confirmPwd = mEdPwdComfiem.getText().toString();

        if (tempPwd.contains(" ") || confirmPwd.contains(" ")) {
            String msg = getBaseContext().getResources().getString(R.string.pwd_no_space);
            mTipView.showError(msg);
            return;
        }

        if (TextUtils.isEmpty(tempPwd) || tempPwd.length() < 6) {
            String msg = getBaseContext().getResources().getString(R.string.reset_pwd_hint);
            mTipView.showError(msg);
            return;
        }

        //判断两个密码是否一致
        if (tempPwd.equals(confirmPwd)) {
            XHYApi.resetPwd(tempPwd, mPhoneToken, mHandler);
        } else {
            String msg = "两次输入的密码不一致,请重新输入！";
            mTipView.showError(msg);
            mEdPwd.setText("");
            mEdPwdComfiem.setText("");
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
        }
    }

    @Override
    public void onGlobalLayout() {
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
