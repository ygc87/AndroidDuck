package com.xhy.neihanduanzi.update;

import android.app.ProgressDialog;
import android.content.Context;

import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.NotificationBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.example.chaokun.neihanduanzi.BuildConfig;
import com.example.chaokun.neihanduanzi.R;
import com.loopj.android.http.TextHttpResponseHandler;
import com.xhy.neihanduanzi.app.AppOperator;
import com.xhy.neihanduanzi.app.api.XHYApi;
import com.xhy.neihanduanzi.model.bean.AppVersionBean;
import com.xhy.neihanduanzi.utils.DialogHelper;
import com.xhy.neihanduanzi.utils.StringUtils;

import cz.msebera.android.httpclient.Header;

public class CheckUpdateManager {

    private ProgressDialog mWaitDialog;
    private Context mContext;
    private boolean mIsShowDialog;

    public CheckUpdateManager(Context context, boolean showWaitingDialog) {
        this.mContext = context;
        mIsShowDialog = showWaitingDialog;
        if (mIsShowDialog) {
            mWaitDialog = DialogHelper.getProgressDialog(mContext);
            mWaitDialog.setMessage("正在检查中...");
            mWaitDialog.setCancelable(true);
            mWaitDialog.setCanceledOnTouchOutside(true);
        }
    }

    public void checkUpdate(final boolean isHasShow) {
        if (mIsShowDialog) {
            mWaitDialog.show();
        }
        XHYApi.checkUpdate(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (mIsShowDialog) {
                    DialogHelper.getMessageDialog(mContext, "网络异常，无法获取新版本信息").show();
                }
                if (mWaitDialog != null) {
                    mWaitDialog.dismiss();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    AppVersionBean versionBean = AppOperator.createGson().fromJson(responseString, AppVersionBean.class);
                    int curVersionCode = BuildConfig.VERSION_CODE;
                    int code = Integer.parseInt(versionBean.getVersion());
                    if (curVersionCode < code) {
                        if (!isHasShow) {
                            //判断下下载的网址
                            if (StringUtils.isApkUrl(versionBean.getApkUrl())) {
                                String content = versionBean.getInfo().replace(";", "\n");
                                DownloadBuilder builder = AllenVersionChecker
                                        .getInstance()
                                        .downloadOnly(
                                                UIData.create()
                                                        .setTitle(mContext.getString(R.string.update_title))
                                                        .setContent(content)
                                                        .setDownloadUrl(versionBean.getApkUrl())

                                        );
                                builder.setNotificationBuilder(createCustomNotification()).excuteMission(mContext);
                            } else {
                                DialogHelper.getMessageDialog(mContext, "下载地址出错,请发邮件至 xiaohuangyavip@gmail.com 给管理员").show();
                            }
                        }
                    } else {
                        if (mIsShowDialog) {
                            DialogHelper.getMessageDialog(mContext, mContext.getString(R.string.update_already)).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (mWaitDialog != null) {
                    mWaitDialog.dismiss();
                }
            }
        });
    }

    private NotificationBuilder createCustomNotification() {
        return NotificationBuilder.create()
                .setRingtone(true)
                .setIcon(R.mipmap.neihan)
                .setContentText(mContext.getString(R.string.custom_content_text));
    }
}
