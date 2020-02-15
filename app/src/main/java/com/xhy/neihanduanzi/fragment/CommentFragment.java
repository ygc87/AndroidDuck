package com.xhy.neihanduanzi.fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.example.chaokun.neihanduanzi.R;
import com.loopj.android.http.TextHttpResponseHandler;
import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.umeng.analytics.MobclickAgent;
import com.xhy.neihanduanzi.adapter.CommentsAdapter;
import com.xhy.neihanduanzi.app.AppOperator;
import com.xhy.neihanduanzi.app.api.XHYApi;
import com.xhy.neihanduanzi.base.BaseDataFragment;
import com.xhy.neihanduanzi.base.BasePresenter;
import com.xhy.neihanduanzi.improve.MyLinearLayoutManager;
import com.xhy.neihanduanzi.model.bean.CommentMultipleItem;
import com.xhy.neihanduanzi.model.bean.Video;
import com.xhy.neihanduanzi.model.bean.VideoComment;
import com.xhy.neihanduanzi.model.event.CommentDiggEvent;
import com.xhy.neihanduanzi.model.event.VideoCollectEvent;
import com.xhy.neihanduanzi.model.event.VideoDiggEvent;
import com.xhy.neihanduanzi.utils.TDevice;
import com.xhy.neihanduanzi.utils.dbutils.AccountHelper;
import com.xhy.neihanduanzi.view.GoodView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by mkt on 2018/6/13.
 */

public class CommentFragment extends BaseDataFragment<BasePresenter> {

    private final String mPageName = "CommentFragment";

    /**页数，每页20条数据 */
    private int page = 1;//
    /**该条视频在视频列表中的索引*/
    private int position = 0;//
    /**视频信息*/
    private Video mVideo;
    /**点赞PopupWindow*/
    private GoodView mGoodView;
    protected RecyclerView mRecyclerView;
    protected SmartRefreshLayout mRefreshLayout;
    /**评论列表：发布者信息*/
    private CommentsAdapter mAdapter;
    /**没有评论的时候显示的空信息*/
    private CommentMultipleItem mEmptyItem;
    /**发布者信息*/
    private CommentMultipleItem mPublisherItem;
    /**footview，主要用来显示加载数据状态*/
    private View mFooterView;
    /**评论数据列表*/
    private List<CommentMultipleItem> itemList = new ArrayList<>();

    // 点赞后的图标和文字颜色
    private Drawable diggedDrawable = null;
    private Drawable unDiggDrawable = null;
    private int diggedColor = 0;
    private int unDiggedColor = 0;
    // 收藏图标
    private Drawable favoritedDrawable = null;
    private Drawable unfavoritDrawable = null;

    public static CommentFragment newInstance(int position,Video mVideo) {
        Bundle args = new Bundle();
        args.putInt("videoIndex",position);
        args.putSerializable("video",mVideo);
        CommentFragment fragment = new CommentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initView(View root) {
        super.initView(root);
        Bundle bundle = getArguments();
        if(bundle != null){
            position = bundle.getInt("videoIndex");
            mVideo = (Video) bundle.getSerializable("video");
        }
        mRecyclerView = root.findViewById(R.id.recyclerView);
        mRefreshLayout = root.findViewById(R.id.smartRefreshLayout);
        mGoodView = new GoodView(getActivity());

        diggedColor = Color.parseColor("#f66467");
        unDiggedColor = Color.parseColor("#999999");
        unDiggDrawable = getResources().getDrawable(R.drawable.icon_like_nor);
        diggedDrawable = getResources().getDrawable(R.drawable.icon_like_light);
        unfavoritDrawable = getResources().getDrawable(R.drawable.icon_favorite_gray_nor);
        favoritedDrawable = getResources().getDrawable(R.drawable.icon_favorite_light);
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.fragment_video_comments;
    }

    @Override
    protected void loadData() {
        // 获取评论数据之前先添加视频发表者信息，点赞数、收藏数、广告等信息
        addVideoInfoHead(null);
        mAdapter = new CommentsAdapter(itemList,position);
        mRecyclerView.setAdapter(mAdapter);
        //防止RecycleView BUG
        mRecyclerView.setLayoutManager(new MyLinearLayoutManager(getActivity()));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (RecyclerView.SCROLL_STATE_DRAGGING == newState && getActivity() != null
                        && getActivity().getCurrentFocus() != null) {
                    TDevice.hideSoftKeyboard(getActivity().getCurrentFocus());
                }
            }
        });
        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setOnLoadMoreListener(
                new OnLoadMoreListener() {
                    @Override
                    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                        requestData();
                    }
                }
        );
        /** 加载评论数据和广告数据 */
        requestData();
    }

    public void needClearData() {// 发表评论的时候要重新获取数据
        page = 1;
        // 这里为什么从1开始移除，因为1是发布者信息和广告部分，不是评论数据
        itemList.subList(1, itemList.size()).clear();
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 获取评论数据
     */
    public void requestData() {
        if (mAdapter.getFooterLayoutCount() == 0) {
            footviewMessage("数据加载中...");
            mAdapter.addFooterView(mFooterView);
        }
        // 获取评论列表和广告数据
        XHYApi.getVideoCommentList(mVideo.getId(),
                AccountHelper.getAccount(), page, mVideo.getChanelCode(), mHandler);
    }

    /**
     * 添加评论头文件:发布者信息、点赞、收藏数量、广告
     */
    private void addVideoInfoHead(VideoComment.AdItem adItem) {
        if (mVideo == null || itemList.contains(mPublisherItem)) {
            return;
        }
        mPublisherItem = new CommentMultipleItem(CommentMultipleItem.VIDEO_INFO);
        mPublisherItem.setObjectID((int) mVideo.getId());
        mPublisherItem.setAvatar(mVideo.getUser().getAvatar());
        mPublisherItem.setName(mVideo.getUser().getName());
        mPublisherItem.setContent(mVideo.getTitle().replaceAll("[\n\\s]+", " "));
        mPublisherItem.setCollCount(Integer.parseInt(mVideo.getUserLikeCount()));
        mPublisherItem.setDiggCount(Integer.parseInt(mVideo.getDiggCount()));
        mPublisherItem.setCollState(mVideo.isCollected());
        mPublisherItem.setDiggState(mVideo.isDigg());
        mPublisherItem.setChannelCode(mVideo.getChanelCode());
        if(mVideo.isDigg()==1){
            mPublisherItem.setDiggDrawable(diggedDrawable);
            mPublisherItem.setDiggColor(diggedColor);
        }else{
            mPublisherItem.setDiggDrawable(unDiggDrawable);
            mPublisherItem.setDiggColor(unDiggedColor);
        }

        if(mVideo.isCollected()==1){
            mPublisherItem.setFavoriteDrawable(favoritedDrawable);
            mPublisherItem.setFavoriteColor(diggedColor);
        }else{
            mPublisherItem.setFavoriteDrawable(unfavoritDrawable);
            mPublisherItem.setFavoriteColor(unDiggedColor);
        }

        mPublisherItem.setCategoryType(mVideo.getChanelCode());
        mPublisherItem.setAdItem(adItem);
        mPublisherItem.setSortId(1);
        itemList.add(0, mPublisherItem);
    }

    /**
     * 热门评论或者新鲜评论group item
     * @param headName
     * @param drawableLeft
     * @return
     */
    private CommentMultipleItem addGroupHead(String headName, Drawable drawableLeft) {
        CommentMultipleItem item = new CommentMultipleItem(CommentMultipleItem.GROUP_HEAD);
        item.setName(headName);
        item.setDrawableLeft(drawableLeft);
        return item;
    }

    /**
     * 构建评论item
     * @param data
     * @return
     */
    private CommentMultipleItem addCommentMultipleItem(VideoComment.Data data) {
        CommentMultipleItem item = new CommentMultipleItem(CommentMultipleItem.COMMENT_ITEM);
        item.setObjectID((int) data.getId());
        item.setAvatar(data.getUserInfo().getAvatar());
        item.setName(data.getUserInfo().getName());
        item.setContent(data.getContent());
        item.setTime(data.getPubDate());
        item.setDiggState(data.getDiggState());
        item.setDiggCount(data.getDiggCount());
        item.setVipState(data.getUserInfo().isVip());
        if (item.getDiggState() == 1) {
            item.setDiggDrawable(diggedDrawable);
            item.setDiggColor(diggedColor);
        }else{
            item.setDiggDrawable(unDiggDrawable);
            item.setDiggColor(unDiggedColor);
        }
        if(item.isVip()){
            Drawable vipDrawable = getResources().getDrawable(R.drawable.icon_vip_light);
            item.setVipDrawable(vipDrawable);
        }else{
            item.setVipDrawable(null);
        }
        return item;
    }

    /**
     * 没有评论的时候显示抢沙发内容
     * @param itemList
     */
    private void addEmptyView(List<CommentMultipleItem> itemList) {
        mEmptyItem = new CommentMultipleItem(CommentMultipleItem.EMPTY_VIEW);
        mEmptyItem.setSortId(2);
        itemList.add(mEmptyItem);
    }

    /**
     * 加载中的footview
     * @return
     */
    private void footviewMessage(String message) {// 正在加载中
        if(mFooterView == null){
            mFooterView = getLayoutInflater().inflate(R.layout.layout_comments_foot,
                    (ViewGroup) mRecyclerView.getParent(), false);
        }
        TextView tv = mFooterView.findViewById(R.id.textView);
        tv.setText(message);
    }

    // 获取数据后的处理逻辑
    private TextHttpResponseHandler mHandler = new TextHttpResponseHandler() {

        int hotCommentCount = 0;
        int newCommentCount = 0;

        @Override
        public void onStart() {
            //Logger.i("Starting..............");
            if(page == 1){
                hotCommentCount = 0;
                newCommentCount = 0;
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            mRefreshLayout.finishLoadMore();
            mRefreshLayout.setEnableLoadMore(true);
            footviewMessage("加载数据失败！");
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, final String responseString) {
            try {
                Logger.v("Load Comment Finished !");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final VideoComment videoComment = AppOperator.createGson().fromJson(responseString, VideoComment.class);
                        VideoComment.Data[] datas = videoComment.getComentData();
                        List<VideoComment.Data> dataList = VideoComment.Data.getCommentList(datas);
                        final int totalCount = videoComment.getCommentsCount();
                        final int next_page = videoComment.getNextPage();
                        if (dataList != null && dataList.size() > 0) {
                            for (int i = 0; i < dataList.size(); i++) {
                                VideoComment.Data data = dataList.get(i);
                                CommentMultipleItem item = addCommentMultipleItem(data);
                                if (data.getHotState().equals("hot")) {
                                    item.setSortId(1100);
                                    hotCommentCount++;
                                } else {
                                    item.setSortId(2200);
                                    newCommentCount++;
                                }
                                itemList.add(item);
                            }
                        }

                        if(getActivity() != null && !getActivity().isFinishing()){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (page == 1) {
                                        // 设置广告信息
                                        itemList.get(0).setAdItem(videoComment.getAdItem());

                                        // 没有评论的情况下显示的内容
                                        if (newCommentCount == 0) {
                                            addEmptyView(itemList);
                                            mRefreshLayout.setEnableLoadMore(false);
                                            mAdapter.removeFooterView(mFooterView);
                                        }else{
                                            if (itemList.contains(mEmptyItem)) {
                                                itemList.remove(mEmptyItem);
                                            }
                                        }

                                        if (hotCommentCount > 0) {
                                            Drawable drawable = getActivity().getResources().getDrawable(R.drawable.comment_hot);
                                            String hotComent = "热门评论  " + hotCommentCount;
                                            CommentMultipleItem item = addGroupHead(hotComent, drawable);
                                            item.setSortId(1000);
                                            itemList.add(item);
                                        }

                                        if (newCommentCount > 0) {
                                            Drawable drawable = getActivity().getApplicationContext().getResources().getDrawable(R.drawable.comment_new);
                                            String newComent = "新鲜评论  " + String.valueOf(totalCount);
                                            CommentMultipleItem item = addGroupHead(newComent, drawable);
                                            item.setSortId(2000);
                                            itemList.add(item);
                                        }
                                    }

                                    // footview处理
                                    if (newCommentCount < page * 20) {//
                                        footviewMessage("已加载全部数据！");
                                        mRefreshLayout.setEnableLoadMore(false);
                                    } else {
                                        page = next_page;
                                        mAdapter.removeFooterView(mFooterView);
                                        mRefreshLayout.setEnableLoadMore(true);
                                    }
                                    Collections.sort(itemList);
                                    mRefreshLayout.finishLoadMore();
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(statusCode, headers, responseString, e);
            }
        }
    };


    //更新热评
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateHotComment(CommentDiggEvent event) {
        if (event != null) {
            TextView tvDigg = (TextView) mAdapter.getViewByPosition(mRecyclerView, event.getPosition(), R.id.tv_comment_diggcount);
            mAdapter.getItem(event.getPosition()).setDiggState(1);
            mAdapter.getItem(event.getPosition()).setDiggCount(event.getCount());
            tvDigg.setText(String.valueOf(event.getCount()));
            tvDigg.setTextColor(diggedColor);
            tvDigg.setCompoundDrawablesWithIntrinsicBounds(diggedDrawable, null, null, null);
            mGoodView.setTextInfo("+1", diggedColor, 25);
            mGoodView.show(tvDigg);
        }
    }

    //更新点赞
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void itemUpdateDigg(VideoDiggEvent event) {
        if (event != null) {
            mAdapter.getItem(event.getCommentsPosition()).setDiggCount(event.getCount());
            mAdapter.getItem(event.getCommentsPosition()).setDiggState(event.getState());
            TextView tvDigg = (TextView) mAdapter.getViewByPosition(mRecyclerView, event.getCommentsPosition(), R.id.tv_digg);
            tvDigg.setCompoundDrawablesWithIntrinsicBounds(diggedDrawable, null, null, null);
            tvDigg.setText(String.valueOf(event.getCount()));
            tvDigg.setTextColor(diggedColor);
            mAdapter.getItem(event.getCommentsPosition()).setDiggState(1);
            mAdapter.getItem(event.getCommentsPosition()).setDiggCount(event.getCount());
            if (event.isShowGoodViewComment()) {
                mGoodView.setTextInfo("+1", diggedColor, 25);
                mGoodView.show(tvDigg);
            } else {
                mAdapter.notifyItemChanged(event.getCommentsPosition());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void itemUpdateCollection(VideoCollectEvent event) {// 收藏操作，UI更新和数据更新
        if (event == null || mAdapter == null) {
            return;
        }
        if (VideoCollectEvent.ADD_VIDEO.equals(event.getAction())) {
            int count = event.getCount();
            //头部收藏状态更改
            mVideo.setCollectedState(1);
            mVideo.setUserLikeCount(count);
            mAdapter.getItem(event.getCommentsPosition()).setCollCount(event.getCount());
            mAdapter.getItem(event.getCommentsPosition()).setCollState(event.getState());
            TextView tvColl = (TextView) mAdapter.getViewByPosition(mRecyclerView, event.getCommentsPosition(), R.id.tv_collect);
            tvColl.setCompoundDrawablesWithIntrinsicBounds(favoritedDrawable, null, null, null);
            tvColl.setText(String.valueOf(event.getCount()));
            tvColl.setTextColor(diggedColor);
            mGoodView.setTextInfo("+1", diggedColor, 25);
            mGoodView.show(tvColl);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        registerEventBus(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterEventBus(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onPageStart(getActivity(), mPageName);
        MobclickAgent.onPageStart(mPageName);
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPageEnd(getActivity(), mPageName);
        MobclickAgent.onPageEnd(mPageName);
    }
}
