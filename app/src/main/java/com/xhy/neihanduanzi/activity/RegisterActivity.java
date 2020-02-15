package com.xhy.neihanduanzi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.umeng.analytics.MobclickAgent;

import com.xhy.neihanduanzi.app.api.XHYApi;
import com.xhy.neihanduanzi.model.bean.Account;
import com.xhy.neihanduanzi.model.bean.Constants;
import com.xhy.neihanduanzi.model.bean.Status;
import com.example.chaokun.neihanduanzi.R;
import com.xhy.neihanduanzi.app.AppOperator;
import com.xhy.neihanduanzi.base.AccountBaseActivity;
import com.xhy.neihanduanzi.update.CheckUpdateManager;
import com.xhy.neihanduanzi.utils.DeviceUtil;
import com.xhy.neihanduanzi.utils.TextUtil;
import com.loopj.android.http.TextHttpResponseHandler;
import com.xhy.neihanduanzi.utils.dbutils.AccountHelper;
import com.xhy.neihanduanzi.view.TipView;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by mkt on 2017/9/20.
 */

public class RegisterActivity extends AccountBaseActivity implements View.OnClickListener, View.OnFocusChangeListener,
        ViewTreeObserver.OnGlobalLayoutListener {

    private final String mPageName = "RegisterActivity";

    @BindView(R.id.iv_title_left)
    ImageView mBackButton;

    @BindView(R.id.tv_title_center)
    TextView mTitle;
    /**
     * 注册邮箱
     */
    @BindView(R.id.ed_zhanghao)
    EditText mRegisterZhanghao;
    /**
     * 密码
     */
    @BindView(R.id.ed_pwd)
    EditText mRegisterPwd;
    /**
     * 昵称
     */
    @BindView(R.id.ed_nickname)
    EditText mRegisterNick;

    /**
     * 邀请人ID
     */
    @BindView(R.id.ed_invite)
    EditText mRegisterInvite;

    /**
     * 提交按钮
     */
    @BindView(R.id.bt_register)
    Button mBtRegisterSubmit;

    /**
     * 提示
     */
    @BindView(R.id.tip_view)
    TipView mTipView;

    /**
     * 登录
     */
    @BindView(R.id.tv_login)
    TextView mLogin;


    private CountDownTimer mTimer;

    private String mMessage;

    @Override
    public void initView(View view) {
        String title = getResources().getString(R.string.register_title);
        mTitle.setText(title);
        if (!getIntent().getBooleanExtra(Constants.LEFT_BTN_VISIBLE, true)) {
            mBackButton.setVisibility(View.GONE);
        }
        CheckUpdateManager updateManager = new CheckUpdateManager(RegisterActivity.this, false);
        updateManager.checkUpdate(false);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    @Override
    public void onGlobalLayout() {

    }

    /**
     * show the register activity_lock_screen
     *
     * @param context context
     */
    public static void show(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    //加载布局文件
    @Override
    public int bindLayout() {
        return R.layout.activity_main_register;
    }

    private int mRequestType = 1;//1. 请求发送验证码  2.请求phoneToken

    private TextHttpResponseHandler mRegisterHandler = new TextHttpResponseHandler() {

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
            if (mRequestType == 1) {
                if (mTimer != null) {
                    mTimer.onFinish();
                    mTimer.cancel();
                }
            }
            requestFailureHint(throwable);
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("msg", responseString);
            StatService.onEvent(RegisterActivity.this, "RegisterActivity", "register_fail: " + responseString);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                Status status = AppOperator.createGson().fromJson(responseString, Status.class);
                if (status != null) {
                    int code = status.getStatus();
                    if (code == 1) {
                        mMessage = status.getMessage();
                        requestLogin(mRegisterZhanghao.getEditableText().toString(), mRegisterPwd.getEditableText().toString());
                        StatService.onEvent(RegisterActivity.this, "RegisterActivity", "register_success", 1);
                    } else if (code == 0) {
                        mTipView.showError(status.getMessage());
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("msg", status.getMessage());
                        StatService.onEvent(RegisterActivity.this, "RegisterActivity", "register_fail", 1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(statusCode, headers, responseString, e);
            }

        }
    };

    @SuppressWarnings("ConstantConditions")
    @OnClick({R.id.iv_title_left,
            R.id.bt_register,
            R.id.tv_login})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_title_left:
                finish();
                break;
            case R.id.bt_register:
                requestRegister();
                break;
            case R.id.tv_login:
                LoginActivity.show(RegisterActivity.this);
                break;
            default:
                break;
        }
    }

    private void requestRegister() {
        Account account = new Account();

        String zhanghao = mRegisterZhanghao.getEditableText().toString();

        String nick = mRegisterNick.getEditableText().toString();

        String pwd = mRegisterPwd.getEditableText().toString();

        //注册账号
        if (zhanghao != null && zhanghao.length() > 0 && !TextUtil.containSpace(zhanghao)) {
            account.setZhanghao(zhanghao);
        } else if (TextUtil.containSpace(zhanghao)) {
            mTipView.showError("账号不能包含空格");
            return;
        } else {
            mTipView.showError("请输入账号");
            return;
        }

        //注册昵称
        if (nick != null && nick.length() > 0 && !TextUtil.containSpace(nick)) {
            account.setUname(nick);
        } else if (TextUtil.containSpace(nick)) {
            mTipView.showError("昵称不能包含空格");
            return;
        } else {
            mTipView.showError("请输入昵称");
            return;
        }

        //设置密码
        if (pwd != null && pwd.length() > 0 && !pwd.contains(" ")) {
            account.setZhanghao(mRegisterZhanghao.getEditableText().toString());
        } else if (pwd.contains(" ")) {
            mTipView.showError("密码不能包含空格");
            return;
        } else {
            mTipView.showError("请输入密码");
            return;
        }

        //邀请码
        String invinteCode = mRegisterInvite.getEditableText().toString();
        //uuid
        String deviceID = DeviceUtil.getUniqueId(RegisterActivity.this);
        XHYApi.validateRegister(account, pwd, invinteCode, deviceID, mRegisterHandler);
    }

    private void requestLogin(String tempUsername, String tempPwd) {

        XHYApi.login(tempUsername, tempPwd, new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                try {
                    hud = KProgressHUD.create(RegisterActivity.this)
                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                            .setLabel("正在登陆...")
                            .setCancellable(true)
//                        .setAnimationSpeed(2)
//                        .setDimAmount(0.5f)
                            .show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                requestFailureHint(throwable);
            }

            @SuppressWarnings("ConstantConditions")
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Account account = AppOperator.createGson().fromJson(responseString, Account.class);
                    account.setState(1);
                    AccountHelper.save(account);
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.putExtra("isHasShow", true);
                    intent.putExtra("isRegister", true);
                    intent.putExtra("register_msg", mMessage);
                    startActivity(intent);
                    finish();
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
