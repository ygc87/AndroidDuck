package com.xhy.neihanduanzi.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class CommonFragmentAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragments;

    private FragmentManager manager;

    public CommonFragmentAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
        this.manager = fm;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment ret = null;
        if (fragments != null) {
            ret = fragments.get(position);
        }
        return ret;
    }

    @Override
    public int getCount() {
        int ret = 0;
        if (fragments != null) {
            ret = fragments.size();
        }
        return ret;
    }

    public void updateList(ArrayList<Fragment> arrayList) {
        this.fragments.clear();
        this.fragments.addAll(arrayList);
        notifyDataSetChanged();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }

}
