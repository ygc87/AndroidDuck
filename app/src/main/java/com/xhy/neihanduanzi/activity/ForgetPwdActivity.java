package com.xhy.neihanduanzi.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.umeng.analytics.MobclickAgent;
import com.xhy.neihanduanzi.AppContext;
import com.xhy.neihanduanzi.app.api.XHYApi;
import com.xhy.neihanduanzi.model.bean.Status;
import com.xhy.neihanduanzi.utils.TDevice;
import com.example.chaokun.neihanduanzi.R;
import com.xhy.neihanduanzi.app.AppOperator;
import com.xhy.neihanduanzi.model.bean.PhoneToken;
import com.xhy.neihanduanzi.improve.RichTextParser;
import com.xhy.neihanduanzi.base.AccountBaseActivity;
import com.xhy.neihanduanzi.utils.ToastUtils;
import com.loopj.android.http.TextHttpResponseHandler;

import butterknife.BindView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by fei
 * on 2016/10/14.
 * desc:
 */

public class ForgetPwdActivity extends AccountBaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private final String mPageName = "ForgetPwdActivity";

    @BindView(R.id.iv_title_left)
    ImageView mBackButton;

    @BindView(R.id.tv_title_center)
    TextView mTitle;

    //手机号
    @BindView(R.id.ed_phone)
    EditText mEtRetrieveTel;

    //验证码
    @BindView(R.id.ed_verifyCode)
    EditText mEtRetrieveCodeInput;

    //获取验证码
    @BindView(R.id.tv_getVerify)
    TextView mTvRetrieveSmsCall;

    @BindView(R.id.bt_next_step)
    Button mBtRetrieveSubmit;

    private boolean mMachPhoneNum;

    private CountDownTimer mTimer;

    private int mRequestType;
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
            //请求失败,比如服务器连接超时,回收timer,需重新请求发送验证码
            if (mRequestType == 1) {
                if (mTimer != null) {
                    mTimer.onFinish();
                    mTimer.cancel();
                }
            }
            requestFailureHint(throwable);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                switch (mRequestType) {
                    //第一步请求发送验证码
                    case 1:
                        Status status = AppOperator.createGson().fromJson(responseString, Status.class);
                        int code = status.getStatus();
                        switch (code) {
                            case 1:
                                //发送验证码成功,请求进入下一步
                                mRequestType = 2;
                                mEtRetrieveCodeInput.setText(null);
                                ToastUtils.show(getApplicationContext(), R.string.send_sms_code_success, Toast.LENGTH_SHORT);
                                break;
                            case 0:
                                ToastUtils.show(getApplicationContext(), status.getMessage(), Toast.LENGTH_SHORT);
                                //异常错误，发送验证码失败,回收timer,需重新请求发送验证码
                                if (mTimer != null) {
                                    mTimer.onFinish();
                                    mTimer.cancel();
                                }
                                break;
                            default:
                                break;
                        }

                        break;
                    //第二步请求进行重置密码
                    case 2:
                        PhoneToken token = AppOperator.createGson().fromJson(responseString, PhoneToken.class);
                        String smsCode = mEtRetrieveCodeInput.getText().toString().trim();
                        if (!mMachPhoneNum || TextUtils.isEmpty(smsCode)) {
                            String msg = getBaseContext().getResources().getString(R.string.hint_phone_ok);
                            ToastUtils.showToast(ForgetPwdActivity.this, msg);
                            return;
                        }
                        if (!TDevice.hasInternet()) {
                            String msg = getBaseContext().getResources().getString(R.string.hint_phone_ok);
                            ToastUtils.showToast(ForgetPwdActivity.this, msg);
                            return;
                        }
                        mRequestType = 2;
                        String phoneNumber = mEtRetrieveTel.getText().toString().trim();
                        //验证码不正确，查看请求码是多少。
                        int status1 = token.getStatus();
                        switch (status1) {
                            case 1:
                                if (token != null) {
                                    if (mTimer != null) {
                                        mTimer.onFinish();
                                        mTimer.cancel();
                                    }
                                    token.setPhonenum(phoneNumber);
                                    token.setSmsCode(smsCode);
                                    ResetPwdActivity.show(ForgetPwdActivity.this, token);
                                    ToastUtils.showToast(ForgetPwdActivity.this, token.getMessage());
                                    finish();
                                }
                                break;
                            case 0:
                            case -1:
                            case -2:
                                Toast.makeText(ForgetPwdActivity.this, token.getMessage(), Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
                onFailure(statusCode, headers, responseString, e);
            }

        }
    };


    @Override
    protected int getContentView() {
        return R.layout.activity_forget;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        String title = getResources().getString(R.string.reset_password);
        mTitle.setText(title);
        mTvRetrieveSmsCall.setAlpha(0.4f);
        mTvRetrieveSmsCall.setEnabled(false);
        mEtRetrieveTel.setOnFocusChangeListener(this);
        mEtRetrieveTel.addTextChangedListener(new TextWatcher() {
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
                String input = s.toString();
                mMachPhoneNum = RichTextParser.machPhoneNum(input);
                if (length > 0 && length < 11) {
                    mTvRetrieveSmsCall.setAlpha(0.4f);
                    mTvRetrieveSmsCall.setEnabled(false);
                } else if (length == 11) {
                    if (mMachPhoneNum) {
                        if (mTvRetrieveSmsCall.getTag() == null) {
                            mTvRetrieveSmsCall.setAlpha(1.0f);
                            mTvRetrieveSmsCall.setEnabled(true);
                        } else {
                            mTvRetrieveSmsCall.setAlpha(0.4f);
                            mTvRetrieveSmsCall.setEnabled(false);
                        }
                    } else {
                        mTvRetrieveSmsCall.setAlpha(0.4f);
                        mTvRetrieveSmsCall.setEnabled(false);
                    }
                } else if (length > 11) {
                    mTvRetrieveSmsCall.setAlpha(0.4f);
                    mTvRetrieveSmsCall.setEnabled(false);
                } else if (length <= 0) {
                    mTvRetrieveSmsCall.setAlpha(0.4f);
                    mTvRetrieveSmsCall.setEnabled(false);
                }

            }
        });
        mEtRetrieveCodeInput.setOnFocusChangeListener(this);
    }

    @Override
    protected void initData() {
        super.initData();//必须要调用,用来注册本地广播
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                hideKeyBoard(getCurrentFocus().getWindowToken());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick({R.id.iv_title_left, R.id.ed_phone, R.id.ed_verifyCode, R.id.tv_getVerify,
            R.id.bt_next_step})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_title_left:
                finish();
                break;
            case R.id.ed_phone:
                mEtRetrieveTel.setText(null);
                break;
            case R.id.tv_getVerify:
                //获取验证码
                requestSmsCode();
                break;
            case R.id.bt_next_step:
                //根据验证码获取phoneToken
                requestRetrievePwd();
                break;
            default:
                break;
        }
    }

    private void requestRetrievePwd() {
        String smsCode = mEtRetrieveCodeInput.getText().toString().trim();
        if (!mMachPhoneNum) {
            String message = getString(R.string.hint_phone_ok);
            ToastUtils.showToast(this, message);
            return;
        } else if (TextUtils.isEmpty(smsCode)) {
            String message = getString(R.string.verify_code_not_null);
            ToastUtils.showToast(this, message);
            return;
        } else if (!TDevice.hasInternet()) {
            String message = getString(R.string.tip_network_error);
            ToastUtils.showToast(this, message);
            return;
        }
        mRequestType = 2;
        String phoneNumber = mEtRetrieveTel.getText().toString().trim();
        XHYApi.validateRegisterInfo(phoneNumber, smsCode, mHandler);
    }

    //发送验证码
    private void requestSmsCode() {
        if (!mMachPhoneNum) {
            ToastUtils.showToast(getApplicationContext(), getResources().getString(R.string.hint_phone_ok));
            return;
        }

        if (!TDevice.hasInternet()) {
            ToastUtils.showToast(getApplicationContext(), getResources().getString(R.string.tip_network_error));
            return;
        }

        if (mTvRetrieveSmsCall.getTag() == null) {
            mRequestType = 1;
            mTvRetrieveSmsCall.setAlpha(0.6f);
            mTvRetrieveSmsCall.setTag(true);
            mTvRetrieveSmsCall.setEnabled(true);
            mTimer = new CountDownTimer(60 * 1000, 1000) {

                @SuppressLint("DefaultLocale")
                @Override
                public void onTick(long millisUntilFinished) {
                    mTvRetrieveSmsCall.setText(String.format("%s%s%d%s",
                            getResources().getString(R.string.register_sms_hint), "(", millisUntilFinished / 1000, ")"));
                }

                @Override
                public void onFinish() {
                    mTvRetrieveSmsCall.setTag(null);
                    mTvRetrieveSmsCall.setText(getResources().getString(R.string.register_sms_hint));
                    mTvRetrieveSmsCall.setAlpha(1.0f);
                    mTvRetrieveSmsCall.setEnabled(true);
                }
            }.start();
            String phoneNumber = mEtRetrieveTel.getText().toString().trim();
            XHYApi.sendSmsCode(phoneNumber, XHYApi.RESET_PWD_INTENT, mHandler);
        } else {
            AppContext.showToast(getResources().getString(R.string.register_sms_wait_hint), Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();
        switch (id) {
        }
    }

    //加载布局文件
    @Override
    public int bindLayout() {
        return R.layout.activity_forget;
    }

    @Override
    public void finish() {
        // 动画的使用正确与否
        // overridePendingTransition(R.anim.activity_open, R.anim.activity_close);
        super.finish();
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

    /**
     * show the retrieve activity_lock_screen
     *
     * @param context context
     */
    public static void show(Context context) {
        Intent intent = new Intent(context, ForgetPwdActivity.class);
        context.startActivity(intent);
    }
}
