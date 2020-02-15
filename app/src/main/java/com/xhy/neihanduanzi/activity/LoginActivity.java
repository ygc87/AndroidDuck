package com.xhy.neihanduanzi.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.umeng.analytics.MobclickAgent;
import com.xhy.neihanduanzi.app.api.XHYApi;
import com.xhy.neihanduanzi.model.bean.Account;
import com.xhy.neihanduanzi.model.bean.Constants;
import com.xhy.neihanduanzi.utils.TDevice;
import com.example.chaokun.neihanduanzi.R;
import com.xhy.neihanduanzi.app.AppOperator;
import com.xhy.neihanduanzi.model.bean.Status;
import com.xhy.neihanduanzi.base.AccountBaseActivity;
import com.xhy.neihanduanzi.utils.dbutils.AccountHelper;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.loopj.android.http.TextHttpResponseHandler;
import com.xhy.neihanduanzi.view.TipView;

import butterknife.BindView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AccountBaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

    public static final String HOLD_USERNAME_KEY = "holdUsernameKey";

    private final String mPageName = "LoginActivity";

    String HOLD_ACCOUNT = "hold_account";

    private Activity mActivity;

    @BindView(R.id.iv_title_left)
    ImageView mBackButton;

    @BindView(R.id.tv_title_center)
    TextView mTitle;

    /**
     * 用户名
     */
    @BindView(R.id.email)
    EditText mEtLoginEmail;

    /**
     * 登录密码
     */
    @BindView(R.id.password)
    EditText mEtLoginPwd;

    /**
     * 登录button
     */
    @BindView(R.id.bt_login)
    Button mBtLoginSubmit;

    /**
     * 注册button
     */
    @BindView(R.id.bt_register)
    Button mBtRegister;

    /**
     * 忘记密码button
     */
    @BindView(R.id.bt_login_forget)
    Button mBtForget;

    /**
     * 提示
     */
    @BindView(R.id.tip_view)
    TipView mTipView;

    /**
     * 加载框
     */
    private KProgressHUD hud;


    private void logSucceed() {
        View view;
        if ((view = getCurrentFocus()) != null) {
            hideKeyBoard(view.getWindowToken());
        }
        setResult(RESULT_OK);
        sendLocalReceiver();
        String uid = String.valueOf(AccountHelper.getUserId());
        MobclickAgent.onProfileSignIn(uid);
    }


    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTitle.setText("登 录");
        mEtLoginEmail.setOnFocusChangeListener(this);
        mEtLoginPwd.setOnFocusChangeListener(this);
        mEtLoginPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressWarnings("deprecation")
            @Override
            public void afterTextChanged(Editable s) {
                String username = mEtLoginEmail.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    String msg = getBaseContext().getResources().getString(R.string.message_username_null);
                    mTipView.showError(msg);
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        mActivity = this;
        //初始化控件状态数据
        SharedPreferences sp = getSharedPreferences(HOLD_ACCOUNT, Context.MODE_PRIVATE);
        String holdUsername = sp.getString(HOLD_USERNAME_KEY, null);
        mEtLoginEmail.setText(holdUsername);

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
    @OnClick({R.id.email, R.id.password, R.id.bt_login,
            R.id.bt_register, R.id.bt_login_forget, R.id.iv_title_left})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.email:
                mEtLoginPwd.clearFocus();
                mEtLoginEmail.setFocusableInTouchMode(true);
                mEtLoginEmail.requestFocus();
                break;
            case R.id.password:
                mEtLoginEmail.clearFocus();
                mEtLoginPwd.setFocusableInTouchMode(true);
                mEtLoginPwd.requestFocus();
                break;
            case R.id.bt_login_forget:
                ForgetPwdActivity.show(LoginActivity.this);
                break;
            //用户登录
            case R.id.bt_login:
                loginRequest();
                break;
            //用户注册
            case R.id.bt_register:
                RegisterActivity.show(LoginActivity.this);
                break;
            //回退
            case R.id.iv_title_left:
                finish();
                break;
            default:
                break;
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void loginRequest() {
        String tempUsername = mEtLoginEmail.getText().toString().trim();
        String tempPwd = mEtLoginPwd.getText().toString().trim();
        if (tempPwd.toString().contains(" ")) {
            mTipView.showError("密码不能包含空格");
            return;
        }
        mActivity = this;
        if (!TextUtils.isEmpty(tempPwd) && !TextUtils.isEmpty(tempUsername)) {
            //登录成功,请求数据进入用户个人中心页面
            if (TDevice.hasInternet()) {
                requestLogin(tempUsername, tempPwd);
            }
        } else {
            String msg = getBaseContext().getResources().getString(R.string.login_input_username_hint_error);
            mTipView.showError(msg);
        }
    }

    private void requestLogin(String tempUsername, String tempPwd) {

        XHYApi.login(tempUsername, tempPwd, new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                hud = KProgressHUD.create(LoginActivity.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel("正在登录...")
                        .setCancellable(true)
//                        .setAnimationSpeed(2)
//                        .setDimAmount(0.5f)
                        .show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                requestFailureHint(throwable);
                scheduleDismiss();
                mTipView.showError("登录失败");
            }

            @SuppressWarnings("ConstantConditions")
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Status status = AppOperator.createGson().fromJson(responseString, Status.class);
                    if (status != null && mActivity != null) {
                        if (status.getStatus() == 1) {
                            Account account = AppOperator.createGson().fromJson(responseString, Account.class);
                            account.setState(1);
                            AccountHelper.save(account);
                            Intent intent = new Intent();
                            intent.putExtra("isHasShow", true);
                            intent.setClass(LoginActivity.this, MainActivity.class);
                            logSucceed();
                            startActivity(intent);
                            finish();
                        } else if (status.getStatus() == 0) {
                            mTipView.showError(status.getMessage());
                        } else {
                            mTipView.showError("登录异常");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                scheduleDismiss();
            }

            @Override
            public void onCancel() {
                super.onCancel();
                scheduleDismiss();
            }
        });
    }

    private void scheduleDismiss() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //会有空指针发生
                try {
                   // hud.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 2000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_login;
    }

    //back键返回上一个 activity
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //isBackFinish = true;
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * show the login activity_lock_screen
     *
     * @param context context
     */
    public static void show(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * show the login activity_lock_screen
     *
     * @param context context
     */
    public static void show(Context context, int flag) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(flag);
        context.startActivity(intent);
    }

    public static void show(Context context, boolean isVisible) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(Constants.LEFT_BTN_VISIBLE,isVisible);
        context.startActivity(intent);
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
