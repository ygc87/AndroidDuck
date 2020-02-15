package com.xhy.neihanduanzi.widget.progress1;


import com.xhy.neihanduanzi.utils.glide.ProgressResponseBody;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by chenpengfei on 2016/11/9.
 */
public class ProgressInterceptor implements Interceptor {

    private ProgressListener progressListener;

    public ProgressInterceptor(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder().body(new ProgressResponseBody(originalResponse.body(), progressListener)).build();
    }

//    @Override
//    public Response intercept(Chain chain) throws IOException {
//        Response originalResponse = chain.proceed(chain.request());
//        return originalResponse.newBuilder().body(new ProgressResponseBody(originalResponse.body(), progressListener)).build();
//    }

}
