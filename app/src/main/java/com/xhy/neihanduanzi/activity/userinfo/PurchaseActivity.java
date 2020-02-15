package com.xhy.neihanduanzi.activity.userinfo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.flyco.tablayout.SlidingTabLayout;
import com.umeng.analytics.MobclickAgent;
import com.xhy.neihanduanzi.adapter.CommonFragmentAdapter;
import com.xhy.neihanduanzi.app.AppOperator;
import com.xhy.neihanduanzi.base.BaseActivity;
import com.xhy.neihanduanzi.fragment.PicturesFragment;
import com.xhy.neihanduanzi.fragment.VideoFragment;
import com.example.chaokun.neihanduanzi.R;
import com.xhy.neihanduanzi.lockdemo.util.constant.Constant;
import com.xhy.neihanduanzi.model.bean.Channel;
import com.xhy.neihanduanzi.utils.SPUtils;
import com.xhy.neihanduanzi.videoplayer.VideoPlayerManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class PurchaseActivity extends BaseActivity {

    private final String mPageName = "PurchaseActivity";

    @BindView(R.id.vp_home)
    ViewPager mViewPager;

    @BindView(R.id.iv_back)
    protected ImageView mBack;

    @BindView(R.id.sliding_tabs)
    SlidingTabLayout mSlidingTab;

    @BindView(R.id.tv_title_center)
    protected TextView mTitle;

    private ArrayList<Fragment> mList = new ArrayList<>();

    private CommonFragmentAdapter mFragmentAdapter;


    @Override
    protected int getContentView() {
        return R.layout.activity_collected;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_collected;
    }

    @Override
    public void initView(View view) {
        initChannelData();
        mTitle.setText("我的购买");
    }

    @Override
    public void doBusiness(Context mContext) {
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick({R.id.iv_back})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        VideoPlayerManager.getInstance().releaseVideoPlayer();
        switch (id) {
            //返回
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void initChannelData() {
        //判断网络情况
        String selectedChannelJson = SPUtils.getString(this, Constant.SELECTED_CHANNEL_JSON, "");
        String unselectChannel = SPUtils.getString(this, Constant.UNSELECTED_CHANNEL_JSON, "");
        String[] channels = null;
        String[] channelCodes = null;
        String[] channelCategory = null;
        if (TextUtils.isEmpty(selectedChannelJson) || TextUtils.isEmpty(unselectChannel)) {
            //本地没有title
            channels = getResources().getStringArray(R.array.channel);
            channelCodes = getResources().getStringArray(R.array.channel_code);
            channelCategory = getResources().getStringArray(R.array.channel_category);
        } else {
            List<Channel> channelList = AppOperator.GsonStringToList(selectedChannelJson, Channel.class);
            channels = new String[channelList.size()];
            channelCategory = new String[channelList.size()];
            channelCodes = new String[channelList.size()];
            for (int i = 0; i < channelList.size(); i++) {
                if (!channelList.get(i).getChannelTitle().equals("my")) {
                    channels[i] = channelList.get(i).getChannelTitle();
                    channelCategory[i] = channelList.get(i).getChannelCategory();
                    channelCodes[i] = channelList.get(i).getChannelCode();
                }
            }
        }

        if (channelCategory != null && channelCategory.length > 0) {
            mList.clear();
            for (int i = 0; i < channelCategory.length; i++) {
                addFragment(channelCategory[i], channelCodes[i]);
            }
        }

        mFragmentAdapter = new CommonFragmentAdapter(getSupportFragmentManager(), mList);
        int pageCount = mFragmentAdapter.getCount();
        mViewPager.setAdapter(mFragmentAdapter);
        mViewPager.setOffscreenPageLimit(pageCount);
        mSlidingTab.setViewPager(mViewPager, channels, this, mList);
    }

    private void addFragment(String category, String chanelCode) {
        if (category.equals("video")) {
            VideoFragment fragment = new VideoFragment();
            fragment.setChanelCode(chanelCode);
            fragment.setType("payed");
            mList.add(fragment);
        } else if (category.equals("picture")) {
            PicturesFragment picFragment = new PicturesFragment();
            picFragment.setChanelCode(chanelCode);
            picFragment.setType("payed");
            mList.add(picFragment);
        }
    }

    /**
     * show the Purchase activity
     *
     * @param context context
     */
    public static void show(Context context) {
        Intent intent = new Intent(context, PurchaseActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onPageStart(this, mPageName);
        MobclickAgent.onPageStart(mPageName);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPageEnd(this, mPageName);
        MobclickAgent.onPageEnd(mPageName);
    }

    @Override
    protected void onDestroy() {
        VideoPlayerManager.getInstance().onDestroy();
        super.onDestroy();
        StatService.onPageEnd(this, mPageName);
    }
}
