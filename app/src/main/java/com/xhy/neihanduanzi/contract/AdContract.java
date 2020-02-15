package com.xhy.neihanduanzi.contract;

import android.graphics.Bitmap;

import com.xhy.neihanduanzi.base.IBaseView;

/**
 * Created by zone on 2017/3/26.
 */

public class AdContract  {

    public interface View extends IBaseView {
        void setAdTime(int count);

        void setAdImg(Bitmap bitmap);

        void showEmptyAd();

        void setLayoutSkipVisible(int visible);
    }

    public interface Presenter {
    }

    public interface Model {
    }
}