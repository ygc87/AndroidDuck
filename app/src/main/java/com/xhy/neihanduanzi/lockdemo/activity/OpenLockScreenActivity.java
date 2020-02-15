package com.xhy.neihanduanzi.lockdemo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.xhy.neihanduanzi.AppContext;
import com.xhy.neihanduanzi.activity.LoginActivity;
import com.xhy.neihanduanzi.app.AppOperator;
import com.xhy.neihanduanzi.app.api.XHYApi;
import com.xhy.neihanduanzi.model.bean.Status;
import com.xhy.neihanduanzi.base.AccountBaseActivity;
import com.xhy.neihanduanzi.utils.ToastUtils;
import com.example.chaokun.neihanduanzi.R;
import com.loopj.android.http.TextHttpResponseHandler;

import butterknife.BindView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by mkt on 2017/10/26.
 */

public class OpenLockScreenActivity extends AccountBaseActivity implements View.OnClickListener, View.OnFocusChangeListener {


    public static final String HOLD_USERNAME_KEY = "holdUsernameKey";

    private Activity mActivity;

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
    @BindView(R.id.open_lock_screen)
    Button mBtLoginSubmit;

    /**
     * hold account information
     */
//    private void holdAccount() {
//        String username = mEtLoginEmail.getText().toString().trim();
//        if (!TextUtils.isEmpty(username)) {
//            SharedPreferences sp = getSharedPreferences(HOLD_ACCOUNT, Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = sp.edit();
//            editor.putString(HOLD_USERNAME_KEY, username);
//            SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
//        }
//    }

    /**
     * show the login activity_lock_screen
     *
     * @param context context
     */
    public static void show(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    /**
     * show the login activity_lock_screen
     *
     * @param context context
     */
    public static void show(Activity context, int requestCode) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * show the login activity_lock_screen
     *
     * @param fragment fragment
     */
    public static void show(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), LoginActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_open_lockscreen;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        //mLlLoginLayer.setVisibility(View.GONE);
        mEtLoginEmail.setOnFocusChangeListener(this);
        mEtLoginEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressWarnings("deprecation")
            @Override
            public void afterTextChanged(Editable s) {
                String username = s.toString().trim();
                if (username.length() > 0) {
                    //mLlLoginUsername.setBackgroundResource(R.drawable.bg_login_input_ok);
                    //mIvLoginUsernameDel.setVisibility(View.VISIBLE);
                } else {
                    //mLlLoginUsername.setBackgroundResource(R.drawable.bg_login_input_ok);
                    //mIvLoginUsernameDel.setVisibility(View.INVISIBLE);
                }

                String pwd = mEtLoginPwd.getText().toString().trim();
//                if (!TextUtils.isEmpty(pwd)) {
//                    mBtLoginSubmit.setBackgroundResource(R.drawable.bg_login_submit);
//                    mBtLoginSubmit.setTextColor(getResources().getColor(R.color.white));
//                } else {
//                    mBtLoginSubmit.setBackgroundResource(R.drawable.bg_login_submit_lock);
//                    mBtLoginSubmit.setTextColor(getResources().getColor(R.color.account_lock_font_color));
//                }

            }
        });

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
                int length = s.length();
                if (length > 0) {
                    //mLlLoginPwd.setBackgroundResource(R.drawable.bg_login_input_ok);
                    //mIvLoginPwdDel.setVisibility(View.VISIBLE);
                } else {
                    //mIvLoginPwdDel.setVisibility(View.INVISIBLE);
                }

                String username = mEtLoginEmail.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    showToastForKeyBord(R.string.message_username_null);
                }
                String pwd = mEtLoginPwd.getText().toString().trim();
//
            }
        });

    }

    @Override
    protected void initData() {
        super.initData();//必须要,用来注册本地广播
        mActivity = this;
        //初始化控件状态数据
//        SharedPreferences sp = getSharedPreferences(UserConstants.HOLD_ACCOUNT, Context.MODE_PRIVATE);
//        String holdUsername = sp.getString(HOLD_USERNAME_KEY, null);
//        mEtLoginEmail.setText(holdUsername);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideKeyBoard(getCurrentFocus().getWindowToken());
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick({R.id.email, R.id.password, R.id.open_lock_screen})
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
            case R.id.open_lock_screen:
                //用户登录
                loginRequest();
                break;
            default:
                break;
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void loginRequest() {

        String tempUsername = mEtLoginEmail.getText().toString().trim();
        String tempPwd = mEtLoginPwd.getText().toString().trim();

        mActivity = this;
        if (!TextUtils.isEmpty(tempPwd) && !TextUtils.isEmpty(tempUsername)) {
            //登录成功,请求数据进入用户个人中心页面

            //if (TDevice.hasInternet()) {
            requestLogin(tempUsername, tempPwd);
            //} else {
            //showToastForKeyBord(R.string.footer_type_net_error);
            //}

        } else {
            showToastForKeyBord(R.string.login_input_username_hint_error);
        }

    }

    private void requestLogin(String tempUsername, String tempPwd) {

        XHYApi.openLockScreen(tempUsername, tempPwd, new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                //showFocusWaitDialog();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                requestFailureHint(throwable);
                showToastForKeyBord("登录失败");
                AppContext.showToast("登录失败");
            }

            @SuppressWarnings("ConstantConditions")
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Status status = AppOperator.createGson().fromJson(responseString, Status.class);
                try {
                    if (status != null && mActivity != null) {
                        if (status.getStatus() == 1) {
//                            Account account = AppOperator.createGson().fromJson(responseString, Account.class);
//                            //解锁成功去手势页
//                            Intent intent = new Intent(OpenLockScreenActivity.this, CreateGestureActivity.class);
//                            Bundle bundle = new Bundle();
//                            bundle.putSerializable("userinfo", account);
//                            intent.putExtras(bundle);
//                            startActivity(intent);
                            finish();
                        } else if (status.getStatus() == 0) {
                            ToastUtils.showToast(mActivity, status.getMessage());
                        } else {
                            ToastUtils.showToast(mActivity, "登录异常");
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
                hideWaitDialog();
            }

            @Override
            public void onCancel() {
                super.onCancel();
                hideWaitDialog();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        int id = v.getId();

//        if (id == R.id.et_login_username) {
//            if (hasFocus) {
//                //mLlLoginUsername.setActivated(true);
//                //mLlLoginPwd.setActivated(false);
//            }
//        } else {
//            if (hasFocus) {
//                //mLlLoginPwd.setActivated(true);
//                //mLlLoginUsername.setActivated(false);
//            }
//        }
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_open_lockscreen;
    }

}
