package com.xhy.neihanduanzi.base;

import android.annotation.SuppressLint;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;


import com.example.chaokun.neihanduanzi.R;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.umeng.analytics.MobclickAgent;
import com.xhy.neihanduanzi.utils.ToastUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by fei
 * on 2016/10/31.
 * desc:
 */

public class AccountBaseActivity extends BaseActivity {

    public static final String ACTION_ACCOUNT_FINISH_ALL = "app.oschina.net.action.finish.all";
    protected LocalBroadcastManager mManager;
    protected InputMethodManager mInputMethodManager;
    protected Toast mToast;

    @Override
    protected int getContentView() {
        return 0;
    }

    @Override
    protected void initData() {
        super.initData();
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideWaitDialog();
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

    /**
     * showToast
     *
     * @param text text
     */
    @SuppressLint("InflateParams")
    private void showToast(String text) {
        Toast toast = this.mToast;
        if (toast == null) {
            toast = initToast();
        }
        View rootView = LayoutInflater.from(this).inflate(R.layout.view_toast, null, false);
        TextView textView = (TextView) rootView.findViewById(R.id.title_tv);
        textView.setText(text);
        toast.setView(rootView);
        //initToastGravity(toast);
        toast.show();
    }

    /**
     * showToast
     *
     * @param id id
     */
    @SuppressLint("InflateParams")
    private void showToast(@StringRes int id) {
        Toast toast = this.mToast;
        if (toast == null) {
            toast = initToast();
        }
        View rootView = LayoutInflater.from(this).inflate(R.layout.view_toast, null, false);
        TextView textView = (TextView) rootView.findViewById(R.id.title_tv);
        textView.setText(id);
        toast.setView(rootView);
        //initToastGravity(toast);
        toast.show();
    }

    @NonNull
    private Toast initToast() {
        Toast toast;
        toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_SHORT);
        this.mToast = toast;
        return toast;
    }

    protected boolean sendLocalReceiver() {
        if (mManager != null) {
            Intent intent = new Intent();
            intent.setAction(ACTION_ACCOUNT_FINISH_ALL);
            return mManager.sendBroadcast(intent);
        }

        return false;
    }

    /**
     * show WaitDialog
     *
     * @return progressDialog
     */
    protected void showWaitDialog(@StringRes int messageId) {
        hud = KProgressHUD.create(AccountBaseActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(getResources().getString(messageId))
                .setCancellable(true)
                .show();
    }

    /**
     * hide waitDialog
     */
    protected void hideWaitDialog() {
        if (hud != null) {
            hud.dismiss();
        }
    }

    protected void showToastForKeyBord(@StringRes int id) {
        showToast(id);
    }

    protected void showToastForKeyBord(String message) {
        showToast(message);
    }

    protected void hideKeyBoard(IBinder windowToken) {
        InputMethodManager inputMethodManager = this.mInputMethodManager;
        if (inputMethodManager == null) return;
        boolean active = inputMethodManager.isActive();
        if (active) {
            inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
        }
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
        ToastUtils.show(this, R.string.request_error_hint, 300);
    }

    /**
     * sha-1 to hex
     *
     * @param tempPwd tempPwd
     * @return sha-1 pwd
     */
    @NonNull
    protected String getSha1(String tempPwd) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(tempPwd.getBytes("utf-8"));
            byte[] bytes = messageDigest.digest();

            StringBuilder tempHex = new StringBuilder();
            // 字节数组转换为 十六进制数
            for (byte aByte : bytes) {
                String shaHex = Integer.toHexString(aByte & 0xff);
                if (shaHex.length() < 2) {
                    tempHex.append(0);
                }
                tempHex.append(shaHex);
            }
            return tempHex.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return tempPwd;
    }

    @Override
    public int bindLayout() {
        return 0;
    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void doBusiness(Context mContext) {

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
}
