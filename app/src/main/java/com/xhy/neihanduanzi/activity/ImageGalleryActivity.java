package com.xhy.neihanduanzi.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.umeng.analytics.MobclickAgent;
import com.xhy.neihanduanzi.activity.socres.ChongzhiActivity;
import com.xhy.neihanduanzi.activity.socres.ScoreRuleActivity;
import com.xhy.neihanduanzi.app.AppOperator;
import com.xhy.neihanduanzi.app.api.XHYApi;
import com.xhy.neihanduanzi.media.ImagePreviewView;
import com.xhy.neihanduanzi.media.LargeImageActivity;
import com.xhy.neihanduanzi.media.PreviewerViewPager;
import com.xhy.neihanduanzi.model.bean.Picture;
import com.xhy.neihanduanzi.improve.base.BaseActivity;
import com.xhy.neihanduanzi.model.bean.Score;
import com.xhy.neihanduanzi.model.bean.Status;
import com.xhy.neihanduanzi.model.event.PicPayEvent;
import com.xhy.neihanduanzi.utils.ToastUtils;
import com.xhy.neihanduanzi.utils.dbutils.AccountHelper;
import com.xhy.neihanduanzi.utils.glide.GlideImageLoader;
import com.xhy.neihanduanzi.utils.glide.OnGlideImageViewListener;
import com.xhy.neihanduanzi.widget.progress.CircleProgressView;
import com.xhy.neihanduanzi.widget.progress.InputStreamReadCallback;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.chaokun.neihanduanzi.R;
import com.loopj.android.http.TextHttpResponseHandler;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 图片预览Activity
 */
public class ImageGalleryActivity extends BaseActivity implements ViewPager.OnPageChangeListener,
        EasyPermissions.PermissionCallbacks, InputStreamReadCallback {

    public static final int MAX_COUNT = 3;

    public static final String KEY_IMAGE = "images";
    public static final String KEY_COOKIE = "cookie_need";
    public static final String KEY_POSITION = "position";
    public static final String KEY_NEED_SAVE = "save";
    public static final String KEY_PIC_DATA = "pic_data";

    private final String mPageName = "ImageGalleryActivity";

    private PreviewerViewPager mImagePager;
    private TextView mIndexText;
    private String[] mImageSources;
    private List<String> mPageList;
    private int mCurPosition;
    private int mObjectCharge;
    private int mPicCount = 0;
    private boolean mNeedCookie;
    private boolean mIsPayed;
    private boolean[] mImageDownloadStatus;

    private ViewGroup mContainer;

    private View mPurchaseView;

    private ImagePreviewView mPreviewView;

    private PagerAdapter mAdapter;

    private static int mPicObjectId;

    private static int mPosition;

    public static void show(Context context, String images) {
        show(context, images, true, 0);
    }

    public static void show(Context context, String images, boolean needSaveLocal, boolean needCookie, int charge) {
        if (images == null)
            return;
        //show(context, new String[]{images}, 0, needSaveLocal, needCookie, charge);
    }

    public static void show(Context context, String images, boolean position, int id) {
        mPicObjectId = id;
        show(context, images, position, true, id);
    }

    public static void show(Context context, String[] images, int position, boolean needSaveLocal, Picture picture) {
        if (images == null || images.length == 0)
            return;
        if (images.length == 1 && !images[0].endsWith(".gif") && !images[0].endsWith(".GIF")) {
            LargeImageActivity.show(context, images[0]);
            return;
        }
        Intent intent = new Intent(context, ImageGalleryActivity.class);
        intent.putExtra(KEY_IMAGE, images);
        intent.putExtra(KEY_POSITION, position);
        intent.putExtra(KEY_NEED_SAVE, needSaveLocal);
        intent.putExtra(KEY_PIC_DATA, picture);
        mPosition = position;
        context.startActivity(intent);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        mNeedCookie = getIntent().getBooleanExtra(KEY_COOKIE, false);
        mImageSources = getIntent().getStringArrayExtra(KEY_IMAGE);
        //这里实时请求数据信息
        Picture picture = (Picture) getIntent().getSerializableExtra(KEY_PIC_DATA);
        mObjectCharge = picture.getObjectCharge();
        mIsPayed = picture.isPayed();
        mPicObjectId = (int) picture.getId();
        mPageList = new ArrayList(Arrays.asList(mImageSources));
        mPicCount = mPageList.size();

        //增加购买页显示,显示付费页面的套图必须大于3张
        if (!mIsPayed && picture.getPicCount() > MAX_COUNT) {//如果只有三张图片要发送特殊标志
            mPageList.add("page");
        }

        if (mImageSources != null) {
            // 初始化下载状态
            mImageDownloadStatus = new boolean[mPageList.size()];
            return true;
        }

        return false;
    }

    @Override
    protected void initWindow() {
        super.initWindow();
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_image_gallery;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mImagePager = (PreviewerViewPager) findViewById(R.id.vp_image);
        mIndexText = (TextView) findViewById(R.id.tv_index);
        mImagePager.addOnPageChangeListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        int len = mPageList.size();
        if (mCurPosition < 0 || mCurPosition >= len)
            mCurPosition = 0;

        // If only one, we not need the text to show
        if (len == 1)
            mIndexText.setVisibility(View.GONE);

        mAdapter = new ViewPagerAdapter();
        mImagePager.setAdapter(mAdapter);
        mImagePager.setCurrentItem(mCurPosition);
        // First we call to init the TextView
        onPageSelected(mCurPosition);
    }

    private static final int PERMISSION_ID = 0x0001;

    @SuppressWarnings("unused")
    @AfterPermissionGranted(PERMISSION_ID)
    @OnClick(R.id.iv_save)
    public void saveToFileByPermission() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, permissions)) {
            saveToFile();
        } else {
            EasyPermissions.requestPermissions(this, "请授予保存图片权限", PERMISSION_ID, permissions);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(this, R.string.gallery_save_file_not_have_external_storage_permission, Toast.LENGTH_SHORT).show();
//        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
//            new AppSettingsDialog.Builder(this).build().show();
//        }
    }

    private void saveToFile() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, R.string.gallery_save_file_not_have_external_storage, Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mCurPosition = position;
        mIndexText.setText(String.format("%s/%s", (position + 1), mPicCount));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void updatePage(String[] images) {
        mCurPosition = mPageList.size() - 1;
        mPageList.remove("page");
        //添加新的图片数据,并记住当前的图片position
        mPageList.clear();
        mPageList.addAll(Arrays.asList(images));
        mAdapter.notifyDataSetChanged();
        mContainer.removeView(mPurchaseView);
        onPageSelected(mCurPosition);
    }

    //更新用户的积分
    private void updateScore(final TextView tv) {
        XHYApi.getTotalScore(AccountHelper.getAccount(), new TextHttpResponseHandler() {

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
                Score score = AppOperator.createGson().fromJson(responseString, Score.class);
                int totalScore = score.getScore();
                String balanceStr = "账户还有：" + String.valueOf(totalScore) + " 积分";
                tv.setText(balanceStr);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onCancel() {
                super.onCancel();
            }
        }, 0);
    }

    @Override
    public void onRead(int offset, long length) {
    }

    private void postPicPayState(int position, int state, String[] sources) {
        PicPayEvent event = new PicPayEvent();
        event.setPosition(position);
        event.setState(state);
        event.setImageSources(sources);
        EventBus.getDefault().post(event);
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

    private void showAlertDialog(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        //msg保留接口
        builder.setCancelable(false);
        builder.setPositiveButton(getText(R.string.chongzhi_positive_button),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ChongzhiActivity.show(ImageGalleryActivity.this);
                    }
                });
        builder.setNegativeButton(getText(R.string.chongzhi_negative_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(
                    DialogInterface dialog,
                    int which) {
                dialog.dismiss();
            }
        });
        builder.setNeutralButton(getText(R.string.chongzhi_neutral_score_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(
                    DialogInterface dialog,
                    int which) {
                ScoreRuleActivity.show(ImageGalleryActivity.this);
            }
        });
        builder.show();
    }


    private class ViewPagerAdapter extends PagerAdapter implements ImagePreviewView.OnReachBorderListener {

        private View.OnClickListener mFinishClickListener;

        public ViewPagerAdapter() {

        }

        @Override
        public int getCount() {
            return mPageList.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(container.getContext())
                    .inflate(R.layout.lay_gallery_page_item_contener, container, false);
            ImagePreviewView previewView = view.findViewById(R.id.iv_preview);
            previewView.setOnReachBorderListener(this);
            mPreviewView = previewView;
            final CircleProgressView progressView1 = view.findViewById(R.id.progressView1);

            //display buy page
            if (mPageList.get(position).equals("page")) {
                //购买页面
                View purchaseViews = LayoutInflater.from(container.getContext())
                        .inflate(R.layout.page_purchase, container, false);
                //图片售价
                TextView chargeView = purchaseViews.findViewById(R.id.object_charge);
                String chargeStr = "图片售价：" + String.valueOf(mObjectCharge) + " 积分";
                chargeView.setText(chargeStr);

                //账户余额
                TextView balanceView = (TextView) purchaseViews.findViewById(R.id.account_balance);
                //String balanceStr = "账户还有：" + String.valueOf(mUserScore) + " 积分";
                updateScore(balanceView);

                TextView buyView = (TextView) purchaseViews.findViewById(R.id.purchased_view_login_or_buy);
                buyView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //后台需要判断是否可以购买
                        XHYApi.getBuyPicList(AccountHelper.getAccount(), mPicObjectId, new TextHttpResponseHandler() {

                            @Override
                            public void onStart() {
                                super.onStart();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                ToastUtils.showToast(ImageGalleryActivity.this, "购买失败");
                            }

                            @SuppressWarnings("ConstantConditions")
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                                try {
                                    Status status = AppOperator.createGson().fromJson(responseString, Status.class);
                                    int code = status.getStatus();
                                    if (code == 1) {
                                        ToastUtils.showToast(ImageGalleryActivity.this, status.getMessage());
                                        Picture picture = AppOperator.createGson().fromJson(responseString, Picture.class);
                                        mImageSources = Picture.Image.getImagePath(picture.getImages());
                                        updatePage(mImageSources);
                                        postPicPayState(mPosition, 1, mImageSources);
                                        StatService.onEvent(ImageGalleryActivity.this, "ImageGalleryActivity", "buy_pic_success", 1);
                                    } else if (code == -1) {
                                        showAlertDialog(status.getMessage());
                                    } else {
                                        ToastUtils.showToast(ImageGalleryActivity.this, status.getMessage());
                                        HashMap<String, String> hashMap = new HashMap<>();
                                        hashMap.put("msg", status.getMessage());
                                        StatService.onEvent(ImageGalleryActivity.this, "ImageGalleryActivity", "buy_pic_fail", 1, hashMap);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
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
                });

                mContainer = container;
                //此处需要添加是否显示已购页面
                //1.已购买不需要显示
                //2.免费不需要显示
                container.addView(purchaseViews);
                return purchaseViews;
            } else {
                GlideImageLoader imageLoader = GlideImageLoader.create(previewView);
                progressView1.setProgress(8);
                imageLoader.setOnGlideImageViewListener(mPageList.get(position), new OnGlideImageViewListener() {
                            @Override
                            public void onProgress(int percent, boolean isDone, GlideException exception) {
                                if (exception != null && !TextUtils.isEmpty(exception.getMessage())) {
                                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                                }
                                if (percent > 8) {
                                    progressView1.setProgress(percent);
                                }
                                progressView1.setVisibility(isDone ? View.GONE : View.VISIBLE);
                            }
                        }
                );
                imageLoader.requestBuilder(mPageList.get(position), new RequestOptions())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(previewView);
            }
            previewView.setOnClickListener(getListener());
            container.addView(view);
            return view;
        }

        private View.OnClickListener getListener() {
            if (mFinishClickListener == null) {
                mFinishClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                };
            }
            return mFinishClickListener;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public void onReachBorder(boolean isReached) {
            mImagePager.isInterceptable(isReached);
        }
    }
}
