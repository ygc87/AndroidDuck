package com.xhy.neihanduanzi.improve.base.swipe;
/**
 * @author Yrom
 */
 interface SwipeBackActivityBase {
    /**
     * @return the SwipeBackLayout associated with this activity_lock_screen.
     */
    SwipeBackLayout getSwipeBackLayout();

    void setSwipeBackEnable(boolean enable);

    /**
     * Scroll out contentView and finish the activity_lock_screen
     */
    void scrollToFinishActivity();

}
