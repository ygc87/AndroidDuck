package com.xhy.neihanduanzi.media;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.xhy.neihanduanzi.improve.base.BaseActivity;
import com.xhy.neihanduanzi.utils.BitmapUtil;
import com.xhy.neihanduanzi.utils.StreamUtil;
import com.xhy.neihanduanzi.widget.ImageLoadingView;
//import com.bumptech.glide.request.animation.GlideAnimation;
//import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
//import com.davemorrissey.labs.subscaleview.ImageSource;
//import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.chaokun.neihanduanzi.R;
import com.xhy.neihanduanzi.app.AppOperator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import java.io.File;
import java.util.List;
import java.util.concurrent.Future;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;



/**
 * 大图预览
 * Created by huanghaibin on 2017/9/27.
 */


public class LargeImageActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    @BindView(R.id.imageView)
    SubsamplingScaleImageView mImageView;

    @SuppressWarnings("unused")
    @BindView(R.id.iv_save)
    ImageView mImageSave;

//    @BindView(R.id.loading)
//    Loading mLoading;

    private String mPath;
    private Context mContext;

    public static void show(Context context, String image) {
        Intent intent = new Intent(context, LargeImageActivity.class);
        intent.putExtra("image", image);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_large_image;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        mImageView.setMaxScale(15);
        mImageView.setZoomEnabled(true);
        mImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);
    }

    public static DisplayImageOptions defaultOptions;

    @Override
    protected void initData() {
        super.initData();
        mPath = getIntent().getStringExtra("image");
//        getImageLoader()
//                .load(mPath)
//                .downloadOnly(new SimpleTarget<File>() {
//                    @Override
//                    public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
////                        if (isDestroyed())
////                            return;
//                        if (!mPath.startsWith("http")) {
//                            mImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
//                        }
//                        mImageView.setImage(ImageSource.uri(Uri.fromFile(resource)), new ImageViewState(1.0f,
//                                new PointF(0, 0), 0));
//                        mImageSave.setVisibility(View.VISIBLE);
////                        mLoading.stop();
////                        mLoading.setVisibility(View.GONE);
//                    }
//                });

        final ImageLoadingView countDownIndicator = new ImageLoadingView(mContext);
        countDownIndicator.setTargetView(mImageView);

         defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
                .showImageOnLoading(R.mipmap.loading)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnFail(R.mipmap.loading)
                .showImageForEmptyUri(R.mipmap.loading)
                .build();

        //加载图片
        ImageLoader.getInstance().displayImage(mPath, (ImageAware) mImageView, defaultOptions, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                countDownIndicator.loadFaild();
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                countDownIndicator.loadCompleted();
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
                countDownIndicator.setProgress(current / (total * 1.00));//防止int相除取整型
            }
        });
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
