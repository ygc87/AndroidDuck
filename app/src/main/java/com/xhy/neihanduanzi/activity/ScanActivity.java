package com.xhy.neihanduanzi.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;
import com.xhy.neihanduanzi.app.api.XHYApi;
import com.xhy.neihanduanzi.base.BaseActivity;
import com.xhy.neihanduanzi.model.bean.Account;
import com.xhy.neihanduanzi.utils.DisplayUtils;
import com.xhy.neihanduanzi.utils.dbutils.AccountHelper;
import com.xhy.neihanduanzi.utils.glide.ImageLoader;
import com.xhy.neihanduanzi.widget.PortraitView;
import com.example.chaokun.neihanduanzi.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lidroid.xutils.exception.DbException;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class ScanActivity extends BaseActivity {

    private final String mPageName = "ScanActivity";

    //返回键
    @BindView(R.id.iv_title_left)
    ImageView mBackButton;

    //标题栏
    @BindView(R.id.tv_title_center)
    TextView mTitle;

    //二维码
    @BindView(R.id.erweima)
    ImageView mErweima;

    //头像
    @BindView(R.id.usertouxiang)
    PortraitView mHeadIcon;

    //用户名
    @BindView(R.id.tv_scan_username)
    TextView mUserName;

    //用户ID
    @BindView(R.id.tv_user_id)
    TextView mUserId;

    @Override
    public int bindLayout() {
        return R.layout.activity_scan;
    }

    @Override
    public void initView(View view) throws DbException {
        String title = getResources().getString(R.string.tv_scan);
        mTitle.setText(title);
        Account account = AccountHelper.getAccount();
        if (account != null) {
            String uname = account.getUname();
            mUserName.setText(uname);
            int id = account.getUid();
            if (id > 0) {
                mUserId.setText("ID : " + String.valueOf(id));
            }
            ImageLoader.load(this, account.getAvatarSmall(), mHeadIcon);
            ViewGroup.LayoutParams params = mErweima.getLayoutParams();
            params.height = DisplayUtils.getDisplayWidth(this);
            params.width = DisplayUtils.getDisplayHeight(this);
            mErweima.setLayoutParams(params);
            XHYApi.getApkUrl(account, new TextHttpResponseHandler() {

                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

                }

                @Override
                public void onSuccess(int i, Header[] headers, String responseString) {
                    generate(responseString);
                }
            });


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
    @OnClick({R.id.iv_title_left})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            //返回
            case R.id.iv_title_left:
                finish();
                break;
            default:
                break;
        }
    }

    //生成二维码
    private void generate(String url) {
        int width = (int) DisplayUtils.getDisplayWidth(this);
        int height = (int) DisplayUtils.getDisplayHeight(this);
        Bitmap qrBitmap = generateBitmap(url, width, (int) (height * 0.5));
        mErweima.setImageBitmap(qrBitmap);
    }

    private Bitmap generateBitmap(String content, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            BitMatrix encode = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (encode.get(j, i)) {
                        pixels[i * width + j] = 0x00000000;
                    } else {
                        pixels[i * width + j] = 0xffffffff;
                    }
                }
            }
            return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * show the 扫描二维码 activity_lock_screen
     *
     * @param context context
     */
    public static void show(Context context) {
        Intent intent = new Intent(context, ScanActivity.class);
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
