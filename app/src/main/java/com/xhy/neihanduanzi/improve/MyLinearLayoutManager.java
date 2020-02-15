package com.xhy.neihanduanzi.improve;

/**
 * Created by mkt on 2018/7/3.
 */

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * 去除动画防止，防止出现recyclerView的bug
 * Created by zhanglin on 2016/11/3.
 */

public class MyLinearLayoutManager extends LinearLayoutManager {
    public MyLinearLayoutManager(Context context) {
        super(context);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }

    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();

        }
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            return super.scrollVerticallyBy(dy, recycler, state);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void collectAdjacentPrefetchPositions(int dx, int dy, RecyclerView.State state, LayoutPrefetchRegistry layoutPrefetchRegistry) {
        try {
            super.collectAdjacentPrefetchPositions(dx, dy, state, layoutPrefetchRegistry);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

}