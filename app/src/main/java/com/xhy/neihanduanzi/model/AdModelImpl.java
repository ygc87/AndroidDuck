package com.xhy.neihanduanzi.model;

import com.xhy.neihanduanzi.contract.AdContract;
import com.xhy.neihanduanzi.service.RetrofitService;
import com.xhy.neihanduanzi.service.RetrofitServiceInstance;

import okhttp3.ResponseBody;
import rx.Observable;

public class AdModelImpl implements AdContract.Model {
    RetrofitService retrofitService;


    public AdModelImpl() {
        retrofitService = RetrofitServiceInstance.getInstance();
    }

    public Observable<ResponseBody> downLoadFile(String fileUrl) {
        return retrofitService.downLoadFile(fileUrl);
    }
}