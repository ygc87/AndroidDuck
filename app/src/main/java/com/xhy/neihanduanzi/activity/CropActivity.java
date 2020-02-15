package com.xhy.neihanduanzi.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.WindowManager;

import com.baidu.mobstat.StatService;
import com.umeng.analytics.MobclickAgent;
import com.xhy.neihanduanzi.base.BaseActivity;
import com.xhy.neihanduanzi.media.CropLayout;
import com.xhy.neihanduanzi.media.SelectOptions;
import com.xhy.neihanduanzi.utils.StreamUtil;
import com.example.chaokun.neihanduanzi.R;

import java.io.FileOutputStream;

import butterknife.OnClick;

/**
 * Created by haibin
 * on 2016/12/2.
 */

public class CropActivity extends BaseActivity implements View.OnClickListener {

    private final String mPageName = "CropActivity";

    private CropLayout mCropLayout;
    private static SelectOptions mOption;

    public static void show(Fragment fragment, SelectOptions options) {
        Intent intent = new Intent(fragment.getActivity(), CropActivity.class);
        mOption = options;
        fragment.startActivityForResult(intent, 0x04);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_crop;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setTitle("");
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        mCropLayout = findViewById(R.id.cropLayout);
        String url = mOption.getSelectedImages().get(0);
        getImageLoader().load(url)
                .into(mCropLayout.getImageView());

        mCropLayout.setCropWidth(mOption.getCropWidth());
        mCropLayout.setCropHeight(mOption.getCropHeight());
        mCropLayout.start();
    }

    @Override
    protected void initData() {
        super.initData();
        String url = mOption.getSelectedImages().get(0);
        getImageLoader().load(url)
                .into(mCropLayout.getImageView());

        mCropLayout.setCropWidth(mOption.getCropWidth());
        mCropLayout.setCropHeight(mOption.getCropHeight());
        mCropLayout.start();
    }

    @OnClick({R.id.tv_crop, R.id.tv_cancel})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_crop:
                Bitmap bitmap = null;
                FileOutputStream os = null;
                try {
                    bitmap = mCropLayout.cropBitmap();
                    String path = getFilesDir() + "/crop.jpg";
                    os = new FileOutputStream(path);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.flush();
                    os.close();

                    Intent intent = new Intent();
                    intent.putExtra("crop_path", path);
                    setResult(RESULT_OK, intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bitmap != null) bitmap.recycle();
                    StreamUtil.close(os);
                }
                break;
            case R.id.tv_cancel:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        mOption = null;
        super.onDestroy();
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_crop;
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
