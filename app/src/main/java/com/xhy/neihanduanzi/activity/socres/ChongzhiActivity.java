package com.xhy.neihanduanzi.activity.socres;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.umeng.analytics.MobclickAgent;
import com.xhy.neihanduanzi.app.AppOperator;
import com.xhy.neihanduanzi.app.api.XHYApi;
import com.xhy.neihanduanzi.base.BaseActivity;
import com.xhy.neihanduanzi.model.bean.OrderBean;
import com.xhy.neihanduanzi.utils.dbutils.AccountHelper;
import com.xhy.neihanduanzi.model.bean.ChargeBean;
import com.xhy.neihanduanzi.utils.TDevice;
import com.xhy.neihanduanzi.utils.ToastUtils;
import com.example.chaokun.neihanduanzi.R;
import com.loopj.android.http.TextHttpResponseHandler;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;


public class ChongzhiActivity extends BaseActivity {

    private final String mPageName = "ChongzhiActivity";

    /**
     * 页面标题
     */
    @BindView(R.id.tv_title_center)
    TextView mTitle;

    //选择充值金额
    @BindView(R.id.tv_chongzhi_value)
    TextView tvChargeValue;

    //选择充值方式
    @BindView(R.id.tv_chongzhi_type)
    TextView tvChargeType;

    @BindView(R.id.im_kefu)
    ImageView imKefu;

    @BindView(R.id.pay_zfb)
    RelativeLayout zfb_layout;

    @BindView(R.id.pay_wx)
    RelativeLayout wx_layout;
    /**
     * 充值金额输入框
     */
    @BindView(R.id.zfb_checkbox)
    CheckBox zfbCheckbox;

    @BindView(R.id.wx_checkbox)
    CheckBox wxCheckbox;

    /**
     * 充值金额列表
     */
    @BindView(R.id.charge_list)
    ListView mListView;

    @BindView(R.id.scrollView)
    ScrollView mScrollView;

    /**
     * 充值金额数据
     */
    private ChargeAdapter mAdapter;

    private Activity mActivity;

    private WebView mWebView;

    private CountDownTimer mTimer;

    private String mOrderNum;


    @Override
    protected int getContentView() {
        return 0;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_chongzhi;
    }

    @Override
    public void initView(View view) {
        mActivity = this;
        String title = getResources().getString(R.string.tv_score_top_up);
        mTitle.setText(title);
        zfbCheckbox = view.findViewById(R.id.zfb_checkbox);
        wxCheckbox = view.findViewById(R.id.wx_checkbox);
        mScrollView = view.findViewById(R.id.scrollView);
        mListView = view.findViewById(R.id.charge_list);
        mWebView = new WebView(ChongzhiActivity.this);
        imKefu = view.findViewById(R.id.im_kefu);
        
        hud = KProgressHUD.create(ChongzhiActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        if (TDevice.hasInternet()) {
            if (mListView != null) {
                //添加分割线
                mAdapter = new ChargeAdapter(this);
                mAdapter.loadData();
                mListView.setAdapter(mAdapter);
            }
        } else {
            ToastUtils.showToast(this, "网络异常,请检查您的网络");
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @SuppressWarnings("ConstantConditions")
    @OnClick({R.id.iv_title_left, R.id.btn_top_up_now, R.id.zfb_checkbox, R.id.wx_checkbox, R.id.im_kefu})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            //返回
            case R.id.iv_title_left:
                finish();
                break;
            //立即充值
            case R.id.btn_top_up_now:
                if (mAdapter != null) {
                    ChargeBean.PayBean payBean = (ChargeBean.PayBean) mAdapter.getCheckedItem();
                    if (payBean != null) {
                        float value =  payBean.getMoneyValue();
                        String shopName = payBean.getMoney() + "积分";
                        String type = "";
                        if (wxCheckbox.isChecked()) {
                            type = "weixin";
                        } else if (zfbCheckbox.isChecked()) {
                            type = "zfb";
                        }
//                        XHYApi.saveCharge(AccountHelper.getAccount(), value, shopName, type, new TextHttpResponseHandler() {
//
//                            @Override
//                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//
//                            }
//
//                            @Override
//                            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                                try {
//                                    OrderBean status = AppOperator.createGson().fromJson(responseString, OrderBean.class);
//                                    mOrderNum = status.getOrderID();
//                                    int code = status.getStatus();
//                                    if (code == 1) {
//                                        StatService.onEvent(ChongzhiActivity.this, "ChongzhiActivity", "chongzhi_success");
//                                        mWebView.getSettings().setJavaScriptEnabled(true);
//                                        mWebView.loadUrl(status.getMessage());
//                                        String url = status.getMessage();
//                                        URL hostURL = new URL(url);
//                                        final String host = hostURL.getProtocol() + "://" + hostURL.getHost();
//                                        //超时30s,自动关闭loading
//                                        mTimer = new CountDownTimer(30 * 1000, 1000) {
//
//                                            @SuppressLint("DefaultLocale")
//                                            @Override
//                                            public void onTick(long millisUntilFinished) {
//                                                showLoading();
//                                            }
//
//                                            @Override
//                                            public void onFinish() {
//                                                dissLoading();
//                                                ToastUtils.show(mActivity, "请求超时，请到意见反馈页面留言给管理员", 1000);
//                                            }
//                                        }.start();
//                                        mWebView.setWebViewClient(new WebViewClient() {
//                                            @Override
//                                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                                                //微信支付
//                                                if (url.startsWith("alipays:") || url.startsWith("weixin://wap/pay?")) {
//                                                    try {
//                                                        Intent intent = new Intent();
//                                                        intent.setAction(Intent.ACTION_VIEW);
//                                                        intent.setData(Uri.parse(url));
//                                                        startActivity(intent);
//                                                        showLoading();
//                                                        return true;
//                                                    } catch (ActivityNotFoundException e) {
//                                                        String msg = getResources().getString(R.string.update_weixin_app);
//                                                        ToastUtils.showToast(ChongzhiActivity.this, msg);
//                                                    }
//                                                } else {
//                                                    if (host != null) {
//                                                        //H5微信支付要用，不然说"商家参数格式有误"
//                                                        Map<String, String> extraHeaders = new HashMap<String, String>();
//                                                        extraHeaders.put("Referer", host);
//                                                        view.loadUrl(url, extraHeaders);
//                                                    } else {
//                                                        //充值域名找不到
//                                                        ToastUtils.show(mActivity, "充值异常，请到意见反馈页面留言给管理员", 1000);
//                                                    }
//                                                }
//                                                return false;
//                                            }
//                                        });
//                                    } else {
//                                        String msg = getResources().getString(R.string.chongzhi_fail);
//                                        ToastUtils.show(mActivity, msg, 1000);
//                                        HashMap<String, String> hashMap = new HashMap<>();
//                                        hashMap.put("msg", responseString);
//                                        StatService.onEvent(ChongzhiActivity.this, "ChongzhiActivity", "chongzhi_fail", 1, hashMap);
//                                    }
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
                    }
                }
                break;
            //选择支付宝充值
            case R.id.zfb_checkbox:
                wxCheckbox.setChecked(false);
                zfbCheckbox.setChecked(true);
                break;
            //选择微信充值
            case R.id.wx_checkbox:
                wxCheckbox.setChecked(true);
                zfbCheckbox.setChecked(false);
                break;
            //点击客服
            case R.id.im_kefu:
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(getString(R.string.kefu_mail));
                ToastUtils.showToast(getApplicationContext(), "复制成功，可以发邮件给客服了");
                break;
            default:
                break;
        }
    }

    public static void show(Context context) {
        Intent intent = new Intent(context, ChongzhiActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {

        if (mOrderNum != null) {
            XHYApi.queryOrder(AccountHelper.getAccount(), mOrderNum, new TextHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    OrderBean status = AppOperator.createGson().fromJson(responseString, OrderBean.class);
                    if (status.getStatus() == 1) {
                        ToastUtils.show(ChongzhiActivity.this, "恭喜充值成功，请到积分明细查看", 1000);
                    } else {
                        ToastUtils.show(ChongzhiActivity.this, status.getMessage(), 1000);
                    }
                }
            });
        }
        super.onResume();
        StatService.onPageStart(this, mPageName);
        MobclickAgent.onPageStart(mPageName);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.loadUrl("");
        if (mTimer != null) {
            mTimer.cancel();
        }
        dissLoading();
        StatService.onPageEnd(this, mPageName);
        MobclickAgent.onPageEnd(mPageName);
    }


    public static boolean isWxInstall(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }

        return false;
    }


    public class ChargeAdapter extends BaseAdapter {

        private List<ChargeBean.PayBean> list = new ArrayList<ChargeBean.PayBean>();
        private Context mContext;
        private Map<Integer, Boolean> isCheck = new HashMap<Integer, Boolean>();
        private int checkedPosition = 0;

        public ChargeAdapter(Context mContext) {
            super();
            this.mContext = mContext;
            initCheck(false);
        }

        public void initCheck(boolean flag) {
            for (int i = 0; i < list.size(); i++) {
                isCheck.put(i, flag);
            }
            isCheck.put(0, true);
        }

        public void loadData() {
            XHYApi.getChargeList(AccountHelper.getAccount(), new TextHttpResponseHandler() {

                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    ToastUtils.showToast(ChongzhiActivity.this, getResources().getString(R.string.state_network_error));
                }

                @SuppressWarnings("ConstantConditions")
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        tvChargeValue.setVisibility(View.VISIBLE);
                        tvChargeType.setVisibility(View.VISIBLE);
                        ChargeBean bean = AppOperator.createGson().fromJson(responseString, ChargeBean.class);
                        list = bean.getPayNum();
                        notifyDataSetChanged();
                        ChargeBean.PayType payType = bean.getPayType();
                        if (payType != null) {
                            if (payType.getAlipay() == 1) {
                                tvChargeType.setVisibility(View.VISIBLE);
                                zfb_layout.setVisibility(View.VISIBLE);
                            }
                            if (payType.getWeixin() == 1) {
                                tvChargeType.setVisibility(View.VISIBLE);
                                wx_layout.setVisibility(View.VISIBLE);
                            }
                            if (payType.getAlipay() == 1) {
                                zfbCheckbox.setChecked(true);
                                wxCheckbox.setChecked(false);
                            } else if (payType.getWeixin() == 1) {
                                zfbCheckbox.setChecked(false);
                                wxCheckbox.setChecked(true);
                            }
                            Glide.with(mActivity)
                                    .asDrawable()
                                    .load(bean.getCustomerServicePic())
                                    .transition(new DrawableTransitionOptions().crossFade())
                                    .thumbnail(0.2f)
                                    .into(imKefu);
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
            });
        }

        public void setData(List<ChargeBean.PayBean> data) {
            this.list = data;
        }

        @Override
        public int getCount() {
            return list != null ? list.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        private Object getCheckedItem() {
            if (list != null && list.size() > checkedPosition) {
                return list.get(checkedPosition);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            View view;
            if (convertView == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.list_item_charge_detail, null);
                viewHolder = new ViewHolder();
                viewHolder.tvCharge = view.findViewById(R.id.tv_charge);
                viewHolder.tvScore = view.findViewById(R.id.tv_score);
                viewHolder.tvSendScore = view.findViewById(R.id.tv_send_score);
                viewHolder.cbCheckBox = view.findViewById(R.id.cb_charge);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            ChargeBean.PayBean data = list.get(position);
            viewHolder.tvCharge.setText(data.getMoney());

            if (data.getCredit() != null) {
                viewHolder.tvScore.setText(data.getCredit());
            }else{
                viewHolder.tvScore.setVisibility(View.GONE);
            }
            if (data.getSendredit() != null) {
                viewHolder.tvSendScore.setText(data.getSendredit());
            }else{
                viewHolder.tvSendScore.setVisibility(View.GONE);
            }

            viewHolder.cbCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //全部设为未选中
                    for (int i = 0; i < list.size(); i++) {
                        isCheck.put(i, false);
                    }
                    isCheck.put(position, true);
                    checkedPosition = position;
                    notifyDataSetChanged();
                }
            });

            if (isCheck.get(position) == null) {
                isCheck.put(position, false);
            }

            viewHolder.cbCheckBox.setChecked(isCheck.get(position));
            return view;
        }

    }

    public static class ViewHolder {
        public CheckBox cbCheckBox;
        public TextView tvCharge;
        public TextView tvScore;
        public TextView tvSendScore;
    }
}

