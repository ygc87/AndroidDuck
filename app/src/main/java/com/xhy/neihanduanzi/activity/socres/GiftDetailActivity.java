package com.xhy.neihanduanzi.activity.socres;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.umeng.analytics.MobclickAgent;
import com.xhy.neihanduanzi.app.AppOperator;
import com.xhy.neihanduanzi.app.api.XHYApi;
import com.xhy.neihanduanzi.model.bean.StatusCount;
import com.xhy.neihanduanzi.model.event.GiftBuyEvent;
import com.xhy.neihanduanzi.utils.dbutils.AccountHelper;
import com.xhy.neihanduanzi.utils.TDevice;
import com.xhy.neihanduanzi.utils.ToastUtils;
import com.xhy.neihanduanzi.utils.glide.ImageLoader;
import com.example.chaokun.neihanduanzi.R;
import com.xhy.neihanduanzi.base.BaseActivity;
import com.xhy.neihanduanzi.model.bean.Gift;
import com.loopj.android.http.TextHttpResponseHandler;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * 礼物详情页
 */
public class GiftDetailActivity extends BaseActivity {

    public static final String BUNDLE_KEY_GIFT = "BUNDLE_KEY_GIFT";

    private final String mPageName = "GiftDetailActivity";

    private Gift mGiftInfo;
    /**
     * 返回键
     */
    @BindView(R.id.iv_title_left)
    ImageView mBack;

    @BindView(R.id.tv_title_center)
    TextView mTitle;
    /**
     * 礼物图片
     */
    @BindView(R.id.iv_detail)
    ImageView mGiftImage;
    /**
     * 礼物名称
     */
    @BindView(R.id.tv_gift_detail_name)
    TextView mGiftName;
    /**
     * 礼物兑换数量
     */
    @BindView(R.id.tv_gift_detail_get_num)
    TextView mGetNum;
    /**
     * 礼物介绍
     */
    @BindView(R.id.wv_content)
    WebView mGiftContent;
    /**
     * 售价积分
     */
    @BindView(R.id.tv_dialog_score)
    TextView mSellScore;
    /**
     * 购买礼物button
     */
    @BindView(R.id.tv_dialog_i_wanna_exchange)
    TextView mExchange;
    /**
     * 购买礼物后的回调
     */
    TextHttpResponseHandler mHandler;

    private Activity mActivity;

    @Override
    protected boolean initBundle(Bundle bundle) {
        mGiftInfo = (Gift) getIntent().getSerializableExtra(BUNDLE_KEY_GIFT);
        if (mGiftInfo == null) {
            return false;
        }
        mActivity = this;
        return super.initBundle(bundle);
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_gift_detail;
    }

    @Override
    public void initView(View view) {
        mTitle.setText("礼物详情");
        if (TDevice.hasInternet()) {
            if (mGiftInfo != null) {
                ImageLoader.load(this, mGiftInfo.getImage(), mGiftImage);
                mGiftName.setText(mGiftInfo.getName());
                mSellScore.setText(String.valueOf(mGiftInfo.getScore()));
                mGetNum.setText("已兑换 " + String.valueOf(mGiftInfo.getSaleCount()) + " 次");
                try {
                    mGiftContent.loadDataWithBaseURL(null, mGiftInfo.getInfo(), "text/html", "UTF-8", null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            mHandler = new TextHttpResponseHandler() {

                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                }

                @SuppressWarnings("ConstantConditions")
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        StatusCount status = AppOperator.createGson().fromJson(responseString, StatusCount.class);
                        if (status != null) {
                            if (status.getStatus() == 1) {
                                mGetNum.setText("已兑换 " + String.valueOf(status.getCount()) + " 次");
                                postBuyEvent();
                                StatService.onEvent(GiftDetailActivity.this, "GiftDetailActivity", "gift_change_success", 1);
                                finish();
                            } else if(status.getStatus() == -1){
                                ChongzhiActivity.show(GiftDetailActivity.this);
                            } else{
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("msg", status.getMessage());
                                StatService.onEvent(GiftDetailActivity.this, "GiftDetailActivity", "gift_change_fail", 1, hashMap);
                            }
                            ToastUtils.showToast(mActivity, status.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        onFailure(statusCode, headers, responseString, e);
                    }
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                }

                @Override
                public void onCancel() {
                    super.onCancel();
                }
            };
        } else {
            ToastUtils.showToast(this, "网络异常,请检查您的网络");
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    protected int getContentView() {
        return 0;
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick({R.id.iv_title_left, R.id.tv_dialog_i_wanna_exchange})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            //返回
            case R.id.iv_title_left:
                finish();
                break;
            //兑换
            case R.id.tv_dialog_i_wanna_exchange:
                int giftId = mGiftInfo.getId();
                XHYApi.buyGift(AccountHelper.getAccount(), giftId, mHandler);
                break;
            default:
                break;
        }
    }


    public void postBuyEvent() {
        GiftBuyEvent event = new GiftBuyEvent();
        event.setRefresh(true);
        EventBus.getDefault().post(event);
    }

    /**
     * show the GiftDetailActivity
     *
     * @param context context
     */
    public static void show(Context context, Gift gift) {
        Intent intent = new Intent(context, GiftDetailActivity.class);
        intent.putExtra(BUNDLE_KEY_GIFT, gift);
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
