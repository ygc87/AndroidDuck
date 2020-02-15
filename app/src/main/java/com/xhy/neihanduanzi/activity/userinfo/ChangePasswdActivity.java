package com.xhy.neihanduanzi.activity.userinfo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.umeng.analytics.MobclickAgent;
import com.xhy.neihanduanzi.activity.LoginActivity;
import com.xhy.neihanduanzi.app.api.XHYApi;
import com.xhy.neihanduanzi.base.BaseActivity;
import com.xhy.neihanduanzi.model.bean.Account;
import com.xhy.neihanduanzi.utils.dbutils.AccountHelper;
import com.example.chaokun.neihanduanzi.R;
import com.xhy.neihanduanzi.app.AppOperator;
import com.xhy.neihanduanzi.model.bean.Status;
import com.lidroid.xutils.exception.DbException;
import com.loopj.android.http.TextHttpResponseHandler;
import com.xhy.neihanduanzi.view.TipView;

import butterknife.BindView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class ChangePasswdActivity extends BaseActivity {

    private final String mPageName = "ChangePasswdActivity";

    @BindView(R.id.iv_title_left)
    ImageView mBackButton;

    @BindView(R.id.tv_title_center)
    TextView mTitle;

    //旧密码
    @BindView(R.id.et_old_pass)
    EditText mEtOldPass;

    //新密码
    @BindView(R.id.et_new_pass)
    EditText mEtNewPass;

    //确认的新密码
    @BindView(R.id.et_new_pass_commit)
    EditText mEtNewCommitPass;

    //
    @BindView(R.id.btn_pass_ok)
    Button mBtpass;

    /**
     * 提示
     */
    @BindView(R.id.tip_view)
    TipView mTipView;

    private Account mAccount;

    @Override
    protected boolean initBundle(Bundle bundle) {
        mAccount = AccountHelper.getAccount();
        return super.initBundle(bundle);
    }

    @Override
    protected int getContentView() {
        return 0;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_change_password;
    }

    @Override
    public void initView(View view) throws DbException {
        String title = getResources().getString(R.string.set_password);
        mTitle.setText(title);
        mEtOldPass.setTypeface(Typeface.DEFAULT);
        mEtNewPass.setTypeface(Typeface.DEFAULT);
        mEtNewCommitPass.setTypeface(Typeface.DEFAULT);
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @SuppressWarnings("ConstantConditions")
    @OnClick({R.id.iv_title_left, R.id.btn_pass_ok})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_title_left:
                finish();
                break;
            case R.id.btn_pass_ok:
                changePassWord();
                break;
            default:
                break;
        }

    }

    private void changePassWord() {
        String oldpass = this.mEtOldPass.getText().toString().trim();
        String newpass = this.mEtNewPass.getText().toString().trim();
        if (mEtOldPass.getText().length() == 0) {
            mTipView.showError("请输入原始密码");
        } else if (newpass.length() == 0) {
            mTipView.showError("请输入新密码");
        } else if (newpass.contains(" ")) {
            mTipView.showError("新密码不能包含空格，请重新输入");
            this.mEtNewPass.setText("");
            this.mEtNewCommitPass.setText("");
        } else if (!newpass.equals(this.mEtNewCommitPass.getText().toString().trim())) {
            mTipView.showError("两次输入密码不一致，请重新输入");
            this.mEtNewPass.setText("");
            this.mEtNewCommitPass.setText("");
        } else {
            //提交密码
            if (newpass.length() >= 6 && newpass.length() <= 15) {
                XHYApi.modifPassword(oldpass, newpass, mAccount, new TextHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        mBtpass.setEnabled(false);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        mTipView.showError("修改密码失败");
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Status status = AppOperator.createGson().fromJson(responseString, Status.class);
                        int code = status.getStatus();
                        //修改成功后跳转到登陆页
                        if (code == 1) {
                            mTipView.show("密码修改成功");
                            String uid = String.valueOf(AccountHelper.getUserId());
                            AccountHelper.deleteAccount(uid);
                            LoginActivity.show(getApplicationContext(),
                                    Intent.FLAG_ACTIVITY_NEW_TASK |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            finish();
                        } else if (code == 0) {
                            mTipView.show(status.getMessage());
                            mBtpass.setEnabled(true);
                        }
                        clearEidtText();
                    }
                });

                return;
            }
            mTipView.showError("密码长度应为6-15位，请重新输入密码");
            clearEidtText();
        }
    }

    //清除输入框所有内容
    private void clearEidtText() {
        mEtOldPass.setText("");
        mEtNewPass.setText("");
        mEtNewCommitPass.setText("");
    }

    public static void show(Context context) {
        Intent intent = new Intent(context, ChangePasswdActivity.class);
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
