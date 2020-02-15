package com.xhy.neihanduanzi.model.bean;

import java.io.Serializable;

/**
 * Created by mkt on 2017/8/18.
 */

public class VideoAndBannerBean implements Serializable {

    private Video video[];

    private BannerBean banner[];

    public Video[] getVideo() {
        return video;
    }

    public BannerBean[] getBanner() {
        return banner;
    }
}
