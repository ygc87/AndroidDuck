package com.xhy.neihanduanzi.model.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mkt on 2018/3/12.
 */

public class PageBean<T> implements Serializable {

    private int next_page;

    private List<T> object_list;

    public int getNextPageToken() {
        return next_page;
    }

    public void setNextPageToken(int nextPageToken) {
        this.next_page = nextPageToken;
    }

    public List<T> getCollectedJson() {
        return object_list;
    }

    public void setCollectedJson(List<T> object_list) {
        this.object_list = object_list;
    }

}
