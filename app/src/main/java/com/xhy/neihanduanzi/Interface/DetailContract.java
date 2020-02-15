package com.xhy.neihanduanzi.Interface;

import com.xhy.neihanduanzi.model.bean.VideoComment;

/**
 * Created by thanatosx
 * on 16/5/28.
 */

public interface DetailContract {

    interface Operator {

    }

    interface ICmnView {
        void onCommentSuccess(VideoComment comment);
    }

    interface IAgencyView {
        void resetLikeCount(int count);

        void resetCmnCount(int count);
    }

}
