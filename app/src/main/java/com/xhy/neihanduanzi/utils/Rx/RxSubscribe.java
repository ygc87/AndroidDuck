package com.xhy.neihanduanzi.utils.Rx;

import android.content.Context;

import com.xhy.neihanduanzi.utils.NetWorkUtil;

import rx.Subscriber;

/**
 * Created by john on 2016/9/8.
 */
public abstract class RxSubscribe<T> extends Subscriber<T> {
    private Context mContext;

    public RxSubscribe(Context context) {
        mContext = context;
    }
    public RxSubscribe() {}



    protected abstract void _onNext(T t);

    protected abstract void _onError(String message);



    @Override
    public void onNext(T t) {
        _onNext(t);
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();

        try {
            if (!NetWorkUtil.isNetWorkConnected(mContext)) {
                //L.d("RxSubscribe====>网络不可用！zone");
                _onError("网络错误，请检查网络！");
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        _onError(e.getMessage());
    }
}
