package com.xhy.neihanduanzi.callback;

import java.util.List;

public interface LoadResultCallBack<T> {

    void onSuccess(List<T> list);

    void onError();

    void onMessage(String msg);

}
