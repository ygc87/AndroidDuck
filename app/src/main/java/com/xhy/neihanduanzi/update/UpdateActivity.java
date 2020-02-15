package com.xhy.neihanduanzi.update;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.chaokun.neihanduanzi.BuildConfig;
import com.example.chaokun.neihanduanzi.R;
import com.lidroid.xutils.exception.DbException;
import com.xhy.neihanduanzi.base.BaseActivity;
import com.xhy.neihanduanzi.model.bean.AppVersionBean;
import com.xhy.neihanduanzi.utils.DialogHelper;
import com.xhy.neihanduanzi.utils.TDevice;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class UpdateActivity extends BaseActivity implements View.OnClickListener,
        EasyPermissions.PermissionCallbacks {
    @BindView(R.id.tv_update_info)
    TextView mTextUpdateInfo;
    private AppVersionBean mVersion;
    private static final int RC_EXTERNAL_STORAGE = 0x04;//存储权限

    public static void show(Activity activity, AppVersionBean version) {
        Intent intent = new Intent(activity, UpdateActivity.class);
        //intent.putExtra("version", version);
        activity.startActivity(intent);
        //activity.startActivityForResult(intent, 0x01);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_update;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void initData() {
        super.initData();
        setTitle("");
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        mVersion = (AppVersionBean) getIntent().getSerializableExtra("version");
        mTextUpdateInfo.setText(Html.fromHtml(mVersion.getInfo()));
    }

    @OnClick({R.id.btn_update, R.id.btn_close, R.id.btn_not_show})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update:
                if (!TDevice.isWifiOpen()) {
                    DialogHelper.getConfirmDialog(this, "当前非wifi环境，是否升级？", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestExternalStorage();
                            finish();
                        }
                    }).show();
                } else {
                    requestExternalStorage();
                    finish();
                }
                break;
            case R.id.btn_not_show:
                XHYSharedPreference.getInstance().putShowUpdate(false);
                XHYSharedPreference.getInstance().putUpdateVersion(BuildConfig.VERSION_CODE);
                finish();
                break;
            case R.id.btn_close:
                finish();
                break;
        }
    }

    @AfterPermissionGranted(RC_EXTERNAL_STORAGE)
    public void requestExternalStorage() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            mVersion = (AppVersionBean) getIntent().getSerializableExtra("version");
            if (mVersion != null) {
                //DownloadService.startService(this, mVersion.getApkUrl());
            }
        } else {
            EasyPermissions.requestPermissions(this, "", RC_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        DialogHelper.getConfirmDialog(this, "温馨提示", "需要开启开源中国对您手机的存储权限才能下载安装，是否现在开启", "去开启", "取消", true, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
            }
        }, null).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_update;
    }

    @Override
    public void initView(View view) throws DbException {

    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
