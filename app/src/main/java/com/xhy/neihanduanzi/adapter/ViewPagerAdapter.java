package com.xhy.neihanduanzi.adapter;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mList;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        mList = new ArrayList();
    }

    public void setList(List list) {
        mList = list;
        this.notifyDataSetChanged();
    }

    public void destroyItem(ViewGroup arg1, int arg2, Object arg3) {
    }

    public int getCount() {
        return mList.size();
    }

    public Fragment getItem(int arg2) {
        return mList.get(arg2);
    }

    public Parcelable saveState() {
        return null;
    }
}

