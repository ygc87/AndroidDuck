package com.xhy.neihanduanzi.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.loopj.android.http.TextHttpResponseHandler;
import com.xhy.neihanduanzi.app.AppOperator;
import com.xhy.neihanduanzi.app.api.XHYApi;
import com.xhy.neihanduanzi.base.PBase;
import com.xhy.neihanduanzi.contract.AdContract;
import com.xhy.neihanduanzi.model.AdModelImpl;
import com.xhy.neihanduanzi.model.bean.AdMessageBean;
import com.xhy.neihanduanzi.utils.Rx.RxSubscribe;
import com.xhy.neihanduanzi.utils.SPUtils;
import com.xhy.neihanduanzi.utils.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cz.msebera.android.httpclient.Header;
import okhttp3.ResponseBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class AdPresenterImpl extends PBase<AdContract.View> {
    //ad model
    private AdModelImpl mAdModel;
    //ad handler
    private TextHttpResponseHandler mAdHandler = new TextHttpResponseHandler() {

        @Override
        public void onStart() {
            super.onStart();

        }

        @Override
        public void onFinish() {
            super.onFinish();

        }

        @Override
        public void onCancel() {
            super.onCancel();

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                AdMessageBean adBean = AppOperator.createGson().fromJson(responseString, AdMessageBean.class);
                if (adBean != null) {
                    if (StringUtils.isEmpty(adBean.getAdPictureUrl())) {
                        getMyView().showEmptyAd();
                    }else{
                        getMyView().setLayoutSkipVisible(View.VISIBLE);
                        getAdPicture(adBean.getAdPictureUrl(), "welcome_ad.jpg", adBean.getAdTime());
                        SPUtils.put((Context) getMyView(), "adUrl", adBean.getAdUrl());
                    }
                } else {
                    getMyView().showEmptyAd();
                }
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(statusCode, headers, responseString, e);
            }

        }
    };

    public AdPresenterImpl() {
        mAdModel = new AdModelImpl();
    }

    public void getAdBean() {
        XHYApi.getWelcomeAD(mAdHandler);
    }

    //setAdTime
    private void getLocalPicture(String localUrl, int time) {
        Bitmap bitmap = BitmapFactory.decodeFile(localUrl);
        getMyView().setAdImg(bitmap);
        getMyView().setAdTime(time);
    }

    public void getAdPicture(final String fileUrl, final String fileName, final int adTime) {//获取要展示的广告图片
        if (SPUtils.get((Context) getMyView(), "adPictureUrl", "").equals(fileUrl)) {
            getLocalPicture((String) SPUtils.get((Context) getMyView(), "adPictureAddress", ""), adTime);
        } else {
            mAdModel.downLoadFile(fileUrl)
                    .subscribeOn(Schedulers.newThread())                            //发布者在后台线程中运行
                    .observeOn(AndroidSchedulers.mainThread())               //订阅者在Android主线程中运行
                    .map(new Func1<ResponseBody, Bitmap>() {
                        @Override
                        public Bitmap call(ResponseBody responseBody) {
                            if (responseBody != null) {
                            }
                            if (writeResponseBodyToDisk(responseBody, fileName, fileUrl)) {
                                Bitmap bitmap = BitmapFactory.decodeFile(((Context) getMyView()).getExternalFilesDir(null) + File.separator + fileName);
                                return bitmap;
                            }
                            return null;
                        }
                    }).subscribe(new RxSubscribe<Bitmap>((Context) getMyView()) {
                @Override
                protected void _onNext(Bitmap bitmap) {
                    getMyView().setAdImg(bitmap);
                    getMyView().setAdTime(adTime);
                }

                @Override
                protected void _onError(String message) {

                }

                @Override
                public void onCompleted() {

                }
            });
        }
    }

    private boolean writeResponseBodyToDisk(ResponseBody body, String fileName, String fileUrl) {//保存图片到本地
        try {
            File futureStudioIconFile = new File(((Context) getMyView()).getExternalFilesDir(null) + File.separator + fileName);
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                }
                outputStream.flush();
                SPUtils.put((Context) getMyView(), "adPictureAddress", ((Context) getMyView()).getExternalFilesDir(null) + File.separator + fileName);
                SPUtils.put((Context) getMyView(), "adPictureUrl", fileUrl);
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}