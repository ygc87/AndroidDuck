package com.xhy.neihanduanzi.view;


import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.widget.TextView;

public class NavigationButton extends TextView {
    private Fragment mFragment = null;
    private Class<?> mClx;
    private String mTag;

    public NavigationButton(Context context) {
        super(context);
    }

    public NavigationButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NavigationButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public NavigationButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public void setSelected(boolean selected) {
        super.setSelected(selected);
    }

    public void init(Class<?> clx) {
        mClx = clx;
        mTag = mClx.getName();
    }

    public Class<?> getClx() {
        return mClx;
    }

    public Fragment getFragment() {
        return mFragment;
    }

    public void setFragment(Fragment fragment) {
        this.mFragment = fragment;
    }

    public String getTag() {
        return mTag;
    }
}
