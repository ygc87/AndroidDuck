package com.xhy.neihanduanzi.model.event;

import com.xhy.neihanduanzi.model.bean.BannerBean;

import java.util.List;

/**
 * Created by mkt on 2018/6/5.
 */

public class BannerLoadEvent {

    private List<BannerBean> resultList;

    private int type;

    public void setData(List<BannerBean> resultList) {
        this.resultList = resultList;
    }

    public List<BannerBean> getData() {
        return this.resultList;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }
}
