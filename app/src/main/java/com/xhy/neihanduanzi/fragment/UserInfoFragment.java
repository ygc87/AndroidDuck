package com.xhy.neihanduanzi.fragment;

/**
 * Created by mkt on 2017/9/21.
 */

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.umeng.analytics.MobclickAgent;
import com.xhy.neihanduanzi.AppContext;
import com.xhy.neihanduanzi.activity.LoginActivity;
import com.xhy.neihanduanzi.activity.ScanActivity;
import com.xhy.neihanduanzi.activity.socres.ChongzhiActivity;
import com.xhy.neihanduanzi.activity.socres.ScoreActivity;
import com.xhy.neihanduanzi.activity.socres.ScoreShopActivity;
import com.xhy.neihanduanzi.activity.userinfo.BindPhoneActivity;
import com.xhy.neihanduanzi.activity.userinfo.ChangePasswdActivity;
import com.xhy.neihanduanzi.activity.userinfo.CollectedActivity;
import com.xhy.neihanduanzi.activity.userinfo.FeedBackActivity;
import com.xhy.neihanduanzi.activity.userinfo.PurchaseActivity;
import com.xhy.neihanduanzi.app.AppOperator;
import com.xhy.neihanduanzi.app.api.XHYApi;
import com.xhy.neihanduanzi.model.bean.Account;
import com.xhy.neihanduanzi.model.bean.Status;
import com.xhy.neihanduanzi.improve.notice.NoticeBean;
import com.xhy.neihanduanzi.improve.notice.NoticeManager;
import com.xhy.neihanduanzi.activity.SelectImageActivity;
import com.xhy.neihanduanzi.media.SelectOptions;
import com.xhy.neihanduanzi.model.event.PicCollectEvent;
import com.xhy.neihanduanzi.model.event.PicPayEvent;
import com.xhy.neihanduanzi.model.event.ScoreChangeEvent;
import com.xhy.neihanduanzi.model.event.VideoCollectEvent;
import com.xhy.neihanduanzi.model.event.VideoCommentEvent;
import com.xhy.neihanduanzi.model.event.VideoPayEvent;
import com.xhy.neihanduanzi.utils.DialogHelper;
import com.xhy.neihanduanzi.utils.FileUtil;
import com.xhy.neihanduanzi.utils.ImageUtils;
import com.xhy.neihanduanzi.utils.MethodsCompat;
import com.xhy.neihanduanzi.utils.TDevice;
import com.xhy.neihanduanzi.utils.ToastUtils;
import com.xhy.neihanduanzi.utils.UIHelper;
import com.xhy.neihanduanzi.utils.dbutils.AccountHelper;
import com.xhy.neihanduanzi.utils.dbutils.PictureRecordHelper;
import com.xhy.neihanduanzi.utils.dbutils.VideoRecordHelper;
import com.xhy.neihanduanzi.utils.glide.ImageLoader;
import com.xhy.neihanduanzi.widget.PortraitView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.chaokun.neihanduanzi.R;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.lidroid.xutils.exception.DbException;
import com.loopj.android.http.TextHttpResponseHandler;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 个人中心
 */

public class UserInfoFragment extends BaseFragment implements View.OnClickListener,
        EasyPermissions.PermissionCallbacks, NoticeManager.NoticeNotify {

    private final String mPageName = "UserInfoFragment";

    //用户昵称
    @BindView(R.id.tv_my_username)
    TextView mTvName;

    //用户ID
    @BindView(R.id.tv_my_uid)
    TextView mUserId;

    //用户评论
    @BindView(R.id.tv_count_comments)
    TextView mTvCountComments;

    //用户积分
    @BindView(R.id.tv_count_score)
    TextView mTvCountScore;

    //用户收藏
    @BindView(R.id.tv_count_collected)
    TextView mTvCountCollected;

    //积分
    @BindView(R.id.tv_my_score)
    TextView mTvScore;

    @BindView(R.id.user_pay_layout)
    LinearLayout mPayLayout;

    @BindView(R.id.user_score_layout)
    LinearLayout mScoreLayout;

    @BindView(R.id.user_collect_layout)
    LinearLayout mCollectLayout;

    //用户头像
    @BindView(R.id.img_user_header)
    PortraitView mPortrait;

    //我的收藏
    @BindView(R.id.ll_mycollection)
    LinearLayout mCollection;

    //我的购买
    @BindView(R.id.ll_mybuy)
    LinearLayout mBuy;

    //我的积分
    @BindView(R.id.ll_myscore)
    LinearLayout mScore;

    //商城
    @BindView(R.id.ll_store)
    LinearLayout mStore;

    //绑定手机
    @BindView(R.id.ll_my_verify)
    LinearLayout mVerify;

    //绑定手机获取积分
    @BindView(R.id.tv_bindphone_hint)
    TextView mBindPhoneHint;

    //二维码入口
    @BindView(R.id.tv_erweima)
    TextView mErweima;

    //缓存大小
    @BindView(R.id.tv_cache_clear)
    TextView mTvCacheClear;

    //退出
    @BindView(R.id.btn_exit)
    Button mExitButton;

    @BindView(R.id.iv_refresh)
    ImageView mIvRefresh;

    private boolean mIsUploadIcon;

    private ProgressDialog mDialog;

    private RequestOptions options;

    private boolean isNotFirst = false;

    private File mCacheFile;

    private Account mAccount;

    private TextHttpResponseHandler requestUserInfoHandler = new TextHttpResponseHandler() {

        private ProgressDialog dialog;

        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString
                , Throwable throwable) {
            if (mIsUploadIcon) {
                Toast.makeText(getActivity(), R.string.title_update_fail_status, Toast.LENGTH_SHORT).show();
                deleteCacheImage();
            }
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                mAccount = AppOperator.createGson().fromJson(responseString, Account.class);
                mAccount.setState(1);
                AccountHelper.save(mAccount);
                //只更新积分等数据，不更新头像
                if (mAccount.getAvatarSmall() != null) {
                    isUpdateHeadView = true;
                    updateView(mAccount);
                }
                if (mIsUploadIcon) {
                    deleteCacheImage();
                }
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(statusCode, headers, responseString, e);
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            if (mIsUploadIcon) mIsUploadIcon = false;
            if (dialog != null && dialog.isShowing()) dialog.dismiss();
            if (mDialog != null && mDialog.isShowing()) mDialog.dismiss();
        }
    };

    private TextHttpResponseHandler uploadHandler = new TextHttpResponseHandler() {
        @Override
        public void onStart() {
            super.onStart();
            if (mIsUploadIcon) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        hud = KProgressHUD.create(mContext)
                                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                .setLabel("请稍后...")
                                .setCancellable(true)
                                .setAnimationSpeed(2)
                                .setDimAmount(0.5f)
                                .show();
                    }
                });
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString
                , Throwable throwable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (hud != null) {
                        hud.dismiss();
                    }
                }
            }, 500);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                Status status = AppOperator.createGson().fromJson(responseString, Status.class);
                if (status.getStatus() == 1) {
                    updateUserIcon(status.getMessage());
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = status.getMessage();
                    mHandler.sendMessage(msg);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (hud != null) {
                                hud.dismiss();
                            }
                        }
                    }, 500);
                } else {
                    ToastUtils.show(getActivity(), status.getMessage(), 1000);
                }
                if (mIsUploadIcon) {
                    deleteCacheImage();
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
    };

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            String picUrl = (String) msg.obj;
            updateUserIcon(picUrl);
        }
    };

    /**
     * delete the cache image file for upload action
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void deleteCacheImage() {
        File file = this.mCacheFile;
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        if (bundle != null) {
            mAccount = AccountHelper.getAccount();
        }
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mIvRefresh = root.findViewById(R.id.iv_refresh);
        mIvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    initData();
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });
        registerEventBus(this);
    }

    @Override
    protected void initData() throws DbException {
        super.initData();
        if (TDevice.hasInternet()) {
            if (mIvRefresh != null) {
                mIvRefresh.setVisibility(View.GONE);
            }
            options = new RequestOptions();
            options.placeholder(R.drawable.default_user);
            //options.diskCacheStrategy(DiskCacheStrategy.ALL);
            mAccount = AccountHelper.getAccount();
            requestUserCache();
        }

        calculateCacheSize();
    }

    public void requestUserCache() {
        if (AccountHelper.isLogin()) {
            sendRequestData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterEventBus(this);
    }

    public static boolean isUpdateHeadView;

    /**
     * 个人详细信息
     */
    private void updateView(Account account) {
        if (mPortrait != null) {
            mPortrait.setVisibility(View.VISIBLE);
        }
        if (AccountHelper.isLogin() && account != null) {
            //增加判断
            if (!isNotFirst) {
                updateUserIcon(account.getAvatarOriginal());
                isNotFirst = true;
            }
            updateVipIcon(account.isVip());
            if (mExitButton != null) {
                mExitButton.setVisibility(View.VISIBLE);
            }

            mTvName.setText(account.getUname());
            mTvName.setVisibility(View.VISIBLE);

            int uid = account.getUid();
            mUserId.setText("ID：" + String.valueOf(uid));

            if (account.getCreditInfoBean() != null && account.getCreditInfoBean().getCreditBean() != null) {
                //更新积分
                int score = account.getCreditInfoBean().getCreditBean().getScore().getValue();
                mTvCountScore.setText(String.valueOf(score));
            }

            if (account.getCountInfoBean() != null) {
                int faCount = account.getCountInfoBean().getFavoriteCount();
                mTvCountCollected.setText(String.valueOf(faCount));
                int payCount = account.getCountInfoBean().getPayCount();
                mTvCountComments.setText(String.valueOf(payCount));
            }
            mBindPhoneHint.setText(getContext().getString(R.string.tv_bind_phone_hint, account.getBindPhoneCredit()));
        } else {
            mExitButton.setVisibility(View.GONE);
        }
    }

    /**
     * 更新个人头像
     */
    private void updateUserIcon(String picUrl) {
        mPortrait.setVisibility(View.VISIBLE);
        clearCacheDiskSelf();
        ImageLoader.load(getActivity(), picUrl, mPortrait, options);
    }

    /**
     * 更新VIP标志
     */
    private void updateVipIcon(boolean isVip) {
        Drawable vipDrawable;
        if (isVip) {
            vipDrawable = mContext.getResources().getDrawable(R.drawable.icon_vip_light);
        } else {
            vipDrawable = mContext.getResources().getDrawable(R.drawable.icon_vip_gray);
        }
        mTvName.setCompoundDrawablesWithIntrinsicBounds(null, null, vipDrawable, null);
        mTvName.setCompoundDrawablePadding(15);
    }

    /**
     * 注销登录信息
     */
    private void clearInfo() {
        mAccount = null;
        mPortrait.setImageResource(R.drawable.default_user);
        mTvName.setText("");
        mUserId.setText("");
        mTvCountScore.setText("0");
        mTvCountCollected.setText("0");
        mTvCountComments.setText("0");
    }

    /**
     * requestData
     */
    private void sendRequestData() {
        mAccount = AccountHelper.getAccount();
        if (TDevice.hasInternet()) {
            XHYApi.getUserInfo(mAccount, requestUserInfoHandler);
        } else {
            updateView(mAccount);
        }
    }

    @SuppressWarnings("deprecation")
    @OnClick({
            R.id.img_user_header, R.id.ll_myscore, R.id.ll_mycollection, R.id.ll_mybuy, R.id.ll_store
            , R.id.tv_erweima, R.id.ll_my_verify, R.id.ll_modify_password, R.id.ll_clean_cache, R.id.ll_feedback, R.id.btn_exit, R.id.user_pay_layout, R.id.user_score_layout, R.id.user_collect_layout, R.id.ll_chongzhi})
    @Override
    public void onClick(View v) {
        //注销用户
        int id = v.getId();
        if (id == R.id.btn_exit) {
            clearAccountDB();
            clearInfo();
            mExitButton.setVisibility(View.GONE);
            LoginActivity.show(getContext());
            System.exit(0);
            ToastUtils.showToast(getContext(), "注销成功");
            return;
        } else if (id == R.id.ll_clean_cache) {
            //清除缓存数据
            onClickCleanCache();
        }

        if (!TDevice.hasInternet()) {
            ToastUtils.showToast(mContext, "网络异常,请检查您的网络");
            return;
        }
        if (!AccountHelper.isLogin()) {
            LoginActivity.show(getActivity());
            return;
        }
        switch (id) {
            case R.id.img_user_header:
                //查看头像 or 更换头像
                showAvatarOperation();
                break;
            //扫描二维码
            case R.id.tv_erweima:
                ScanActivity.show(getActivity());
                break;
            //我的收藏
            case R.id.user_collect_layout:
            case R.id.ll_mycollection:
                CollectedActivity.show(getActivity());
                break;
            //我的购买
            case R.id.user_pay_layout:
            case R.id.ll_mybuy:
                PurchaseActivity.show(getActivity());
                break;
            //我的积分
            case R.id.user_score_layout:
            case R.id.ll_myscore:
                ScoreActivity.show(getActivity());
                break;
            //我要成为VIP
            case R.id.ll_store:
                ScoreShopActivity.show(getActivity());
                break;
            //我要
            case R.id.ll_chongzhi:
                ChongzhiActivity.show(getActivity());
                break;
            //绑定手机
            case R.id.ll_my_verify:
                BindPhoneActivity.show(getActivity());
                break;
            //修改密码
            case R.id.ll_modify_password:
                ChangePasswdActivity.show(getActivity());
                break;
            //意见反馈
            case R.id.ll_feedback:
                FeedBackActivity.show(getActivity());
                break;
            default:
                break;
        }
    }

    /**
     * 查看头像
     */
    private void showAvatarOperation() {
        if (!AccountHelper.isLogin()) {
            LoginActivity.show(getActivity());
        } else {
            SelectImageActivity.show(getContext(), new SelectOptions.Builder()
                    .setSelectCount(1)
                    .setHasCam(true)
                    .setCrop(700, 700)
                    .setCallback(new SelectOptions.Callback() {
                        @Override
                        public void doSelected(String[] images) throws IOException {
                            String path = images[0];
                            uploadNewPhoto(new File(path));
                        }
                    }).build());
        }
    }

    /**
     * take photo
     */
    private void startTakePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,
                ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
    }

    private KProgressHUD hud;

    /**
     * 更新个人头像
     */
    private void uploadNewPhoto(File file) {
        // 获取头像缩略图
        if (file == null || !file.exists() || file.length() == 0) {
            ToastUtils.show(getContext(), getString(R.string.title_icon_null), 500);//showToast(getString(R.string.title_icon_null),500);
        } else {
            mIsUploadIcon = true;
            this.mCacheFile = file;

            try {
                XHYApi.uploadFile(AccountHelper.getAccount(), file, uploadHandler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        try {
            startTakePhoto();
        } catch (Exception e) {
            Toast.makeText(this.getContext(), R.string.permissions_camera_error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(this.getContext(), R.string.permissions_camera_error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions
            , @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onNoticeArrived(NoticeBean bean) {
    }

    /**
     * 计算缓存的大小
     */
    private void calculateCacheSize() {
        try {
            long fileSize = 0;
            String cacheSize = "0KB";
            File filesDir = getActivity().getFilesDir();
            File cacheDir = getActivity().getCacheDir();

            fileSize += FileUtil.getDirSize(filesDir);
            fileSize += FileUtil.getDirSize(cacheDir);
            //2.2版本才有将应用缓存转移到sd卡的功能
            if (AppContext.isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
                File externalCacheDir = MethodsCompat
                        .getExternalCacheDir(getActivity());
                fileSize += FileUtil.getDirSize(externalCacheDir);
            }
//            if (fileSize > 0)
//                cacheSize = FileUtil.formatFileSize(fileSize);
            //mTvCacheClear.setText(cacheSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //清空缓存
    private void onClickCleanCache() {
        DialogHelper.getConfirmDialog(getActivity(), "是否清空缓存?", new DialogInterface.OnClickListener
                () {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                UIHelper.clearAppCache(getActivity(), true);
                int uid = AccountHelper.getAccount().getUid();
                PictureRecordHelper.deleteAll(uid);
                VideoRecordHelper.deleteAll(uid);
            }
        }).show();
    }

    public boolean clearCacheDiskSelf() {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(getActivity()).clearDiskCache();
                    }
                }).start();
            } else {
                Glide.get(getActivity()).clearDiskCache();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    //评论积分更新
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void updateUserInfo(VideoCommentEvent event) {
        Account account = AccountHelper.getAccount();
        XHYApi.getUserInfo(account, requestUserInfoHandler);
    }

    //收藏数量更新
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void updateUserInfo(VideoCollectEvent videoCollectEvent) {
        Account account = AccountHelper.getAccount();
        XHYApi.getUserInfo(account, requestUserInfoHandler);
    }

    //收藏数量更新
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void updateUserInfo(PicCollectEvent picCollectEvent) {
        Account account = AccountHelper.getAccount();
        XHYApi.getUserInfo(account, requestUserInfoHandler);
    }

    //更新视频购买状态显示
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void updateUserInfo(VideoPayEvent event) {
        Account account = AccountHelper.getAccount();
        XHYApi.getUserInfo(account, requestUserInfoHandler);
    }

    //更新图片购买状态显示
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void updateUserInfo(PicPayEvent event) {
        Account account = AccountHelper.getAccount();
        XHYApi.getUserInfo(account, requestUserInfoHandler);
    }

    //更新图片购买状态显示
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void updateUserInfo(ScoreChangeEvent event) {
        Account account = AccountHelper.getAccount();
        XHYApi.getUserInfo(account, requestUserInfoHandler);
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            //加载数据
            initData();
        } catch (DbException e) {
            e.printStackTrace();
        }
        mIsUploadIcon = false;
        StatService.onPageStart(getActivity(), mPageName);
        MobclickAgent.onPageStart(mPageName);
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPageEnd(getActivity(), mPageName);
        MobclickAgent.onPageEnd(mPageName);
    }

    private void clearAccountDB() {
        if (mAccount != null) {
            VideoRecordHelper.deleteAll(mAccount.getUid());
            PictureRecordHelper.deleteAll(mAccount.getUid());
        }
        AccountHelper.deleteAccount(String.valueOf(AccountHelper.getUserId()));
        MobclickAgent.onProfileSignOff();
    }
}
