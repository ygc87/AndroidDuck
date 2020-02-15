package com.xhy.neihanduanzi.base;

/**
 * Created by john on 2016/9/13.
 */
public interface IPBase<V extends IBaseView> {
    void attachView(V view);
    void detachView();
}
