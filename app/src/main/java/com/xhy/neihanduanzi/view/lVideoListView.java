package com.xhy.neihanduanzi.view;

import com.xhy.neihanduanzi.model.bean.Video;

import java.util.List;

/**
 * @author ChayChan
 * @description: 获取各种频道广告的View回调接口
 * @date 2017/6/18  9:33
 */

public interface lVideoListView {

    void onGetNewsListSuccess(List<Video> videoList, String tipInfo);

    void onError();
}
