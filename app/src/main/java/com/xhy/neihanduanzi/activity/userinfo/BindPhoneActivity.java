package com.xhy.neihanduanzi.activity.userinfo;

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
import com.xhy.neihanduanzi.app.AppOperator;
import com.xhy.neihanduanzi.app.api.XHYApi;
import com.xhy.neihanduanzi.model.bean.Status;
import com.xhy.neihanduanzi.improve.RichTextParser;
import com.xhy.neihanduanzi.base.AccountBaseActivity;
import com.xhy.neihanduanzi.utils.TDevice;
import com.xhy.neihanduanzi.utils.ToastUtils;
import com.example.chaokun.neihanduanzi.R;
import com.loopj.android.http.TextHttpResponseHandler;
import com.xhy.neihanduanzi.utils.dbutils.AccountHelper;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;


public class BindPhoneActivity extends AccountBaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private final String mPageName = "BindPhoneActivity";

    @BindView(R.id.iv_title_left)
    ImageView mBackButton;

    @BindView(R.id.tv_title_center)
    TextView mTitle;

    //输入手机号
    @BindView(R.id.ed_phone)
    EditText mEtRetrieveTel;

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
            //绑定手机成功要有提示消息
            //发送验证码button要加时间进程限制多次点击
            try {
                Status status = AppOperator.createGson().fromJson(responseString, Status.class);
                ToastUtils.showToast(BindPhoneActivity.this, status.getMessage());
                if (status.getStatus() == 0) {
                    if (mTimer != null) {
                        mTimer.onFinish();
                        mTimer.cancel();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(statusCode, headers, responseString, e);
            }
        }
    };

    //绑定手机号的回调
    TextHttpResponseHandler mBindPhoneHandler = new TextHttpResponseHandler() {

        @Override
        public void onStart() {
            //负责添加菊花，不能打断，防止多次点击。
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
            //请求失败,比如服务器连接超时,回收timer,需重新请求发送验证码
            if (mRequestType == 1) {
                if (mTimer != null) {
                    mTimer.onFinish();
                    mTimer.cancel();
                }
            }
            requestFailureHint(throwable);
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("msg", responseString);
            StatService.onEvent(BindPhoneActivity.this, "BindPhoneActivity", "bind_fail");
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            //绑定手机成功要有提示消息
            try {
                Status status = AppOperator.createGson().fromJson(responseString, Status.class);
                HashMap<String, String> hashMap = new HashMap<>();
                if (status.getStatus() == 1) {
                    ToastUtils.showToast(BindPhoneActivity.this, "恭喜您，手机绑定成功");
                    StatService.onEvent(BindPhoneActivity.this, "BindPhoneActivity", "bind_success");
                    finish();
                } else if (status.getStatus() == 0) {
                    if (mTimer != null) {
                        mTimer.onFinish();
                        mTimer.cancel();
                    }
                    ToastUtils.showToast(BindPhoneActivity.this, status.getMessage());
                    hashMap.put("msg", status.getMessage());
                    StatService.onEvent(BindPhoneActivity.this, "BindPhoneActivity", "bind_fail");
                } else {
                    if (mTimer != null) {
                        mTimer.onFinish();
                        mTimer.cancel();
                    }
                    ToastUtils.showToast(BindPhoneActivity.this, "登录异常");
                    hashMap.put("msg", status.getMessage());
                    StatService.onEvent(BindPhoneActivity.this, "BindPhoneActivity", "bind_fail");
                }
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showToast(BindPhoneActivity.this, "手机绑定失败，稍后重试");
            }

        }
    };

    @Override
    public int bindLayout() {
        return R.layout.activity_bind_phone;
    }

    @Override
    public void initView(View view) {

    }

    @Override
    protected void initWidget() {
        super.initWidget();
        String title = getResources().getString(R.string.tv_bind_phone);
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
                        showToastForKeyBord(R.string.hint_phone_ok);
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
    public void doBusiness(Context mContext) {

    }

    @Override
    protected int getContentView() {
        return 0;
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

    private void requestSmsCode() {
        if (!mMachPhoneNum) {

            //showToastForKeyBord(R.string.hint_phone_ok);
            return;
        }

        if (!TDevice.hasInternet()) {
            //showToastForKeyBord(R.string.tip_network_error);
            return;
        }

        if (mTvRetrieveSmsCall.getTag() == null) {
            mRequestType = 1;
            mTvRetrieveSmsCall.setAlpha(0.6f);
            mTvRetrieveSmsCall.setTag(true);
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
                }
            }.start();
            String phoneNumber = mEtRetrieveTel.getText().toString().trim();
            //获取验证码
            //再次绑定手机如何处理
            XHYApi.sendRegisterCode(phoneNumber, mHandler);
        } else {
            AppContext.showToast(getResources().getString(R.string.register_sms_wait_hint), Toast.LENGTH_SHORT);
        }
    }


    private void requestRetrievePwd() {
        String smsCode = mEtRetrieveCodeInput.getText().toString().trim();
        if (!mMachPhoneNum || TextUtils.isEmpty(smsCode)) {
            // showToastForKeyBord(R.string.hint_username_ok);
            return;
        }

        if (!TDevice.hasInternet()) {
            //showToastForKeyBord(R.string.tip_network_error);
            return;
        }
        mRequestType = 2;
        String phoneNumber = mEtRetrieveTel.getText().toString().trim();
        //提交绑定的手机号
        XHYApi.do_bind_phone(phoneNumber, AccountHelper.getAccount(), smsCode, mBindPhoneHandler);
    }

    /**
     * request network error
     *
     * @param throwable throwable
     */
    protected void requestFailureHint(Throwable throwable) {
        if (throwable != null) {
            throwable.printStackTrace();
        }
        //showToastForKeyBord(R.string.request_error_hint);
    }

    public static void show(Context context) {
        Intent intent = new Intent(context, BindPhoneActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

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
