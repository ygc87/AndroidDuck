package com.xhy.neihanduanzi.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mobstat.StatService;
import com.umeng.analytics.MobclickAgent;
import com.xhy.neihanduanzi.adapter.CommonFragmentAdapter;
import com.xhy.neihanduanzi.app.AppOperator;
import com.xhy.neihanduanzi.lockdemo.util.constant.Constant;
import com.xhy.neihanduanzi.model.bean.Channel;
import com.xhy.neihanduanzi.utils.NetWorkUtil;
import com.xhy.neihanduanzi.utils.SPUtils;
import com.xhy.neihanduanzi.utils.dbutils.AccountHelper;
import com.xhy.neihanduanzi.videoplayer.VideoPlayerManager;
import com.example.chaokun.neihanduanzi.R;
import com.flyco.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 导航
 */
public class NavFragment extends Fragment implements View.OnClickListener {

    private final String mPageName = "NavFragment";

    private ViewPager mViewPager;

    private CommonFragmentAdapter mFragmentAdapter;

    private ArrayList<Fragment> mFragments = new ArrayList<Fragment>();

    private String[] mChannels;

    @BindView(R.id.sliding_tabs)
    SlidingTabLayout mSlidingTab;

    private FragmentManager mFragmentManager;

    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        //设置头像为圆形头像
        mSlidingTab = view.findViewById(R.id.sliding_tabs);

        //得到屏幕的宽度和高度
        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);

        //获取viewpager设置内部分类列表
        mViewPager = view.findViewById(R.id.home_pager);

        //设置ViewPager光晕颜色
        mViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);

        //对viewpager设置监听器
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                VideoPlayerManager.getInstance().onPause(true);
                //登陆，注销应该用广播通知各个fragment,便于清理数据
                if (!AccountHelper.isLogin()) {
                    if (position == 3) {
                        UserInfoFragment userInfoFragment = (UserInfoFragment) mFragmentAdapter.getItem(position);
                        //force to update view when selected.
                        if (userInfoFragment != null) {
                            userInfoFragment.isUpdateHeadView = false;
                        }
                    } else {
                        mFragmentAdapter.getItem(position).onResume();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        initChannelData();
        return view;
    }

    public void setup(Context context, FragmentManager fragmentManager, int contentId) {
        mContext = context;
        mFragmentManager = fragmentManager;
    }

    @Override
    public void onClick(View view) {

    }

    private void initChannelData() {
        String selectedChannelJson = SPUtils.getString(mContext, Constant.SELECTED_CHANNEL_JSON, "");
        String[] channelCodes = null;
        String[] channelCategory = null;
        if (!NetWorkUtil.isNetWorkConnected(mContext) || TextUtils.isEmpty(selectedChannelJson)) {
            mChannels = getResources().getStringArray(R.array.channel);
            channelCodes = getResources().getStringArray(R.array.channel_code);
            channelCategory = getResources().getStringArray(R.array.channel_category);
        } else {
            List<Channel> channelList = AppOperator.GsonStringToList(selectedChannelJson, Channel.class);
            mChannels = new String[channelList.size()];
            channelCategory = new String[channelList.size()];
            channelCodes = new String[channelList.size()];
            for (int i = 0; i < channelList.size(); i++) {
                mChannels[i] = channelList.get(i).getChannelTitle();
                channelCategory[i] = channelList.get(i).getChannelCategory();
                channelCodes[i] = channelList.get(i).getChannelCode();
            }
        }

        if (channelCategory != null && channelCategory.length > 0) {
            for (int i = 0; i < channelCategory.length; i++) {
                addFragment(channelCategory[i], channelCodes[i], mFragments);
            }
        }

        if (mFragmentAdapter == null) {
            mFragmentAdapter = new CommonFragmentAdapter(getChildFragmentManager(), mFragments);
            mViewPager.setAdapter(mFragmentAdapter);
            mSlidingTab.setViewPager(mViewPager, mChannels, getActivity(), mFragments);
        } else {
            mFragmentAdapter.notifyDataSetChanged();
            mSlidingTab.notifyDataSetChanged();
        }
    }

    private void addFragment(String category, String chanelCode, ArrayList<Fragment> list) {
        if (category == null || chanelCode == null) {
            return;
        }

        if (category.equals("video")) {
            VideoFragment fragment = new VideoFragment();
            fragment.setChanelCode(chanelCode);
            fragment.setType("normal");
            list.add(fragment);
        } else if (category.equals("picture")) {
            PicturesFragment picFragment = new PicturesFragment();
            picFragment.setChanelCode(chanelCode);
            picFragment.setType("normal");
            list.add(picFragment);
        } else if (category.equals("my")) {
            list.add(new UserInfoFragment());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFragmentAdapter != null) {
            int position = mViewPager.getCurrentItem();
            mFragmentAdapter.getItem(position).onResume();
        }
        StatService.onPageStart(getActivity(), mPageName);
        MobclickAgent.onPageStart(mPageName);
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPageEnd(getActivity(), mPageName);
        MobclickAgent.onPageEnd(mPageName);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        StatService.onPageEnd(getActivity(), mPageName);
        MobclickAgent.onPageEnd(mPageName);
    }

}