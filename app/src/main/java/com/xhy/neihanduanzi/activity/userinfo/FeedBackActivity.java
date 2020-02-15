package com.xhy.neihanduanzi.activity.userinfo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.example.chaokun.neihanduanzi.R;
import com.lidroid.xutils.exception.DbException;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;
import com.xhy.neihanduanzi.app.AppOperator;
import com.xhy.neihanduanzi.app.api.XHYApi;
import com.xhy.neihanduanzi.base.BaseActivity;
import com.xhy.neihanduanzi.model.bean.Status;
import com.xhy.neihanduanzi.utils.ToastUtils;
import com.xhy.neihanduanzi.utils.dbutils.AccountHelper;

import butterknife.BindView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by mkt on 2017/10/10.
 */

public class FeedBackActivity extends BaseActivity {

    private final String mPageName = "FeedBackActivity";

    @BindView(R.id.iv_title_left)
    ImageView mBackButton;

    @BindView(R.id.tv_title_center)
    TextView mTitle;

    //发送
    @BindView(R.id.tv_title_right)
    TextView mSendButton;

    //意见
    @BindView(R.id.et_feedback_content)
    EditText mFeedbackContent;

    @Override
    protected boolean initBundle(Bundle bundle) {
        return super.initBundle(bundle);
    }


    @Override
    protected int getContentView() {
        return 0;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_feed_back;
    }

    @Override
    public void initView(View view) throws DbException {
        String title = getResources().getString(R.string.tv_feedback);
        mTitle.setText(title);
        String actionText = getResources().getString(R.string.send_action);
        mSendButton.setText(actionText);
        mSendButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    //提交反馈
    private void sendFeedBack() {

        String feedback = mFeedbackContent.getText().toString().trim();

        if (feedback.length() > 0) {
            XHYApi.commitFeedBack(feedback, AccountHelper.getAccount(), new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    ToastUtils.showToast(FeedBackActivity.this, "请求出现异常,请稍后重试");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        Status status = AppOperator.createGson().fromJson(responseString, Status.class);
                        int code = status.getStatus();
                        if (code == 1) {
                            ToastUtils.showToast(FeedBackActivity.this, "感谢您的反馈");
                            finish();
                        } else {
                            ToastUtils.showToast(FeedBackActivity.this, status.getMessage());
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick({R.id.iv_title_left, R.id.tv_title_right})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_title_left:
                finish();
                break;
            case R.id.tv_title_right:
                sendFeedBack();
                break;
            default:
                break;
        }
    }

    /**
     * show the feedback activity_lock_screen
     *
     * @param context context
     */
    public static void show(Context context) {
        Intent intent = new Intent(context, FeedBackActivity.class);
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
