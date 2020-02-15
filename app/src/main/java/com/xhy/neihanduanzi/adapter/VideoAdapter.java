package com.xhy.neihanduanzi.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.loopj.android.http.TextHttpResponseHandler;
import com.xhy.neihanduanzi.activity.DetailVideoActivity;
import com.xhy.neihanduanzi.activity.WebViewActivity;
import com.xhy.neihanduanzi.app.AppOperator;
import com.xhy.neihanduanzi.app.api.XHYApi;
import com.xhy.neihanduanzi.callback.ListActionCallBack;
import com.xhy.neihanduanzi.callback.LoadFinishCallBack;
import com.xhy.neihanduanzi.callback.LoadResultCallBack;
import com.xhy.neihanduanzi.model.bean.Account;
import com.xhy.neihanduanzi.model.bean.Constants;
import com.xhy.neihanduanzi.model.bean.PageBean;
import com.xhy.neihanduanzi.model.bean.Status;
import com.xhy.neihanduanzi.model.bean.StatusCount;
import com.xhy.neihanduanzi.model.bean.Video;
import com.xhy.neihanduanzi.model.bean.VideoRecord;
import com.xhy.neihanduanzi.model.event.VideoCollectEvent;
import com.xhy.neihanduanzi.model.event.VideoDiggEvent;
import com.xhy.neihanduanzi.model.event.VideoLoadDataEvent;
import com.xhy.neihanduanzi.utils.DialogHelper;
import com.xhy.neihanduanzi.utils.GsonUtil;
import com.xhy.neihanduanzi.utils.NetWorkUtil;
import com.xhy.neihanduanzi.utils.StringUtils;
import com.xhy.neihanduanzi.utils.TDevice;
import com.xhy.neihanduanzi.utils.ToastUtils;
import com.xhy.neihanduanzi.utils.dbutils.AccountHelper;
import com.xhy.neihanduanzi.utils.dbutils.VideoRecordHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import com.example.chaokun.neihanduanzi.R;

/**
 * Created by mkt on 2018/3/21.
 */

public class VideoAdapter extends BaseVideoAdapter implements ListActionCallBack, BaseQuickAdapter.OnItemClickListener {

    private Context mContext;

    private Account mAccount;

    private String mChanelCode;

    private String mAdapterType;

    private PageBean mPageBean;

    private int mType;

    private VideoRecord mVideoRecord;

    public VideoAdapter(Activity activity, @Nullable List<Video> data, LoadFinishCallBack loadFinisCallBack, LoadResultCallBack loadResultCallBack) {
        super(activity, data, loadFinisCallBack, loadResultCallBack);
        mContext = activity;
        mAccount = AccountHelper.getAccount();
        setOnItemClickListener(this);
        mPageBean = new PageBean<Video>();
    }

    public void loadFirst() {
        mPageBean.setNextPageToken(1);
        loadDataByNetworkType(0);
    }

    public void loadDataByNetworkType(int type) {
        if (NetWorkUtil.isNetWorkConnected(mContext)) {
            loadData(type);
        } else {
            loadCache();
        }
    }

    private void loadCache() {
        if (mAccount == null) {
            return;
        }
        String uid = String.valueOf(mAccount.getUid());
        mVideoRecord = VideoRecordHelper.getLastVideoRecord(uid, mChanelCode);
        List<Video> videoList = new ArrayList<>();
        if (mVideoRecord != null) {
            videoList = VideoRecordHelper.convertToVideoList(mVideoRecord.getJson());
            setNewData(videoList);
        }
        mLoadResultCallBack.onSuccess(videoList);
    }

    public void setChanelcode(String code) {
        mChanelCode = code;
    }

    public void setVideoAdapterType(String type) {
        mAdapterType = type;
    }

    public String getVideoAdapterType() {
        return mAdapterType;
    }


    public String getChanelCode() {
        return mChanelCode;
    }

    public void loadData(final int type) {
        if (mAdapterType == null) {
            return;
        }
        try {
            mAccount = AccountHelper.getAccount();
        } catch (Exception e) {
            e.printStackTrace();
            mLoadResultCallBack.onError();
            return;
        }
        if (mAccount == null) {
            return;
        }
        mType = type;
        if (mType == 0) {
            mPageBean.setNextPageToken(1);
        }
        page = mPageBean.getNextPageToken();
        if (mAdapterType.equals("normal")) {
            XHYApi.getVideoList(mAccount, mChanelCode, getLoadDataHandler());
        } else if (mAdapterType.equals("collect")) {
            setDeleteVisible(true);
            XHYApi.getVideoCollected(mAccount, page, mChanelCode, getSpecialDataHandler());
        } else if (mAdapterType.equals("payed")) {
            XHYApi.getBuyVideoList(mAccount, page, mChanelCode, getSpecialDataHandler());
        }
    }

    public void setData(List<Video> list) {
        getData().addAll(list);
    }

    @Override
    public void loadData(List<Video> resultList) {
        int pos = 0;
        int count = resultList.size();
        String msg;
        if (count > 0) {
            msg = "当前更新" + String.valueOf(count - 1) + "条数据";
        } else {
            msg = "没有数据";
        }
        if (msg != null) {
            mLoadResultCallBack.onMessage(msg);
        }
        //}

        if (mType == 0) {
            addData(pos, resultList);
        } else if (mType == 1) {
            pos = getData().size();
            addData(pos, resultList);

        }
        notifyItemRangeChanged(pos, count);
    }

    @Override
    protected void convert(BaseViewHolder h, Video video) {
        super.convert(h, video);
    }

    //@Override
    public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {
        if (!AccountHelper.isLogin()) {
            callLoginPage();
        } else {
            if (!TDevice.hasInternet()) {
                ToastUtils.showToast(mContext, "网络异常");
                return;
            }
            int id = view.getId();
            Video video = getItem(position);
            final RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) view.getTag();
            switch (id) {
                case R.id.tv_digg:
                    TextHttpResponseHandler handler = getDiggHandler(mContext, view, position);
                    if (mChanelCode.equals(Constants.SVIDEO)) {
                        XHYApi.addVideoDigg((int) getItem(position).getId(), mAccount, "svideo_digg", handler);
                    } else if (mChanelCode.equals(Constants.LVIDEO)) {
                        XHYApi.addVideoDigg((int) getItem(position).getId(), mAccount, "lvideo_digg", handler);
                    }
                    break;
                case R.id.tv_collected:
                    if (mChanelCode.equals(Constants.SVIDEO)) {
                        XHYApi.addSvideoCollected((int) getItem(position).getId(), mAccount, getCollHandler(mContext, view, position));
                    } else if (mChanelCode.equals(Constants.LVIDEO)) {
                        XHYApi.addLvideoCollected((int) getItem(position).getId(), mAccount, getCollHandler(mContext, view, position));
                    }
                    break;
                case R.id.bottom:
                case R.id.item_layout:
                case R.id.rl_info:
                case R.id.tv_comment_count:
                    if (video.getLabel() == 1) {
                        DetailVideoActivity.show(mContext, position, getItem(position));
                    }
                    break;
                case R.id.im_delete:
                    DialogHelper.getConfirmDialog(mContext, "", "是否取消该收藏", "确定", "取消", false, new DialogInterface.OnClickListener() {
                        //确定
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            TextHttpResponseHandler handler = getDellHandler(mContext, holder);
                            HashMap<String, String> hashMap = new HashMap<>();
                            if (mChanelCode.equals(Constants.SVIDEO)) {
                                hashMap.put("id", String.valueOf(getItem(position).getId()));
                                StatService.onEvent(mContext, "VideoAdapter", "cancel_sv_collect", 1, hashMap);
                                XHYApi.dellVideoCollected((int) getItem(position).getId(), mAccount, "svideo", handler);
                            } else if (mChanelCode.equals(Constants.LVIDEO)) {
                                hashMap.put("id", String.valueOf(getItem(position).getId()));
                                StatService.onEvent(mContext, "VideoAdapter", "cancel_lv_collect", 1, hashMap);
                                XHYApi.dellVideoCollected((int) getItem(position).getId(), mAccount, "lvideo", handler);
                            }
                        }
                    }, new DialogInterface.OnClickListener() {
                        //取消
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
                    break;
                case R.id.tv_ad_button:
                case R.id.ad_item:
                    if (StringUtils.isUrl(video.getMp4Url())) {
                        WebViewActivity.show(mContext, video.getTitle(), video.getMp4Url());
                    } else if (StringUtils.isPackage(video.getMp4Url())) {
                        try {
                            Class clz = Class.forName(video.getMp4Url());
                            mContext.startActivity(new Intent(mContext, clz));
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    break;

            }
        }
    }

    public void postListData(List<Video> mVideoList, int type) {
        VideoLoadDataEvent event = new VideoLoadDataEvent();
        event.setData(mVideoList);
        event.setType(type);
        event.setChannel(mChanelCode);
        EventBus.getDefault().post(event);
    }

    private void postAddDigg(int position, int count, int state) {
        VideoDiggEvent event = new VideoDiggEvent();
        event.setVideoPosition(position);
        event.setCount(count);
        event.setState(state);
        event.setGoodViewVideoShow(true);
        event.setChanelCode(mChanelCode);
        EventBus.getDefault().post(event);
    }

    private void postAddCollect(Video video, int position, int count, int state) {
        VideoCollectEvent event = new VideoCollectEvent();
        event.setVideoPosition(position);
        event.setCount(count);
        event.setState(state);
        event.setRefresh(false);
        event.setVideo(video);
        event.setGoodViewVideoShow(true);
        event.setAction(VideoCollectEvent.ADD_VIDEO);
        event.setChanelCode(mChanelCode);
        EventBus.getDefault().post(event);
    }

    public void postDelCollect(Video video, int state) {
        VideoCollectEvent event = new VideoCollectEvent();
        event.setVideo(video);
        event.setState(state);
        event.setAction(VideoCollectEvent.DEL_VIDEO);
        event.setRefresh(true);
        EventBus.getDefault().post(event);
    }

    //请求数据的接口回调
    public TextHttpResponseHandler getLoadDataHandler() {
        return new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mLoadResultCallBack.onError();
                mLoadFinisCallBack.loadFinish(null);
            }

            @SuppressWarnings("ConstantConditions")
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    List<Video> resultList = GsonUtil.stringToList(responseString, Video.class);
                    postListData(resultList, getAdapterType());
                    mLoadResultCallBack.onSuccess(resultList);
                    mLoadFinisCallBack.loadFinish(null);
                    if (resultList.size() < 11) {
                        loadMoreEnd(true);
                    }
                    //postLoadFinish();
                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onCancel() {
                super.onCancel();
            }
        };
    }

    //收藏和购买的数据
    public TextHttpResponseHandler getSpecialDataHandler() {
        return new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mLoadResultCallBack.onError();
                mLoadFinisCallBack.loadFinish(null);
            }

            @SuppressWarnings("ConstantConditions")
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    mPageBean = AppOperator.createGson().fromJson(responseString, PageBean.class);
                    String s2 = GsonUtil.getGson().toJson(mPageBean.getCollectedJson());
                    List<Video> videoList = AppOperator.GsonStringToList(s2, Video.class);
                    if (mType == 0) {
                        getData().clear();
                    }
                    loadData(videoList);
                    mLoadResultCallBack.onSuccess(getData());
                    mLoadFinisCallBack.loadFinish(null);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onCancel() {
                super.onCancel();
            }
        };
    }

    //点赞接口实现
    public TextHttpResponseHandler getDiggHandler(final Context context, final View view, final int position) {
        return new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                ToastUtils.showToast(context, "点赞失败");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    StatusCount status = AppOperator.createGson().fromJson(responseString, StatusCount.class);
                    if (status.getStatus() == 1) {
                        int count = Integer.parseInt(((TextView) view).getText().toString()) + 1;
                        postAddDigg(position, count, 1);
                    }
                    ToastUtils.showToast(context, status.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.showToast(context, context.getResources().getString(R.string.state_network_json_error));
                }
            }

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

        };
    }

    //收藏实现
    public TextHttpResponseHandler getCollHandler(final Context context, final View view, final int position) {
        return new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                ToastUtils.showToast(context, "收藏失败");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    StatusCount status = AppOperator.createGson().fromJson(responseString, StatusCount.class);
                    if (status.getStatus() == 1) {
                        int count = Integer.parseInt(((TextView) view).getText().toString()) + 1;
                        postAddCollect(getItem(position), position, count, 1);
                    }

                    ToastUtils.showToast(context, status.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.showToast(context, context.getResources().getString(R.string.state_network_json_error));
                }
            }

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        };
    }

    //取消收藏实现
    public TextHttpResponseHandler getDellHandler(final Context context, final RecyclerView.ViewHolder holder) {
        return new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                String msg = context.getString(R.string.cancel_collect_fail);
                ToastUtils.showToast(context, msg);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Status status = AppOperator.createGson().fromJson(responseString, Status.class);
                    if (status.getStatus() == 1) {
                        int id = Integer.valueOf(status.getMessage()).intValue();
                        for (int i = 0; i < getData().size(); i++) {
                            if (getItem(i).getId() == id) {
                                Video video = getItem(i);
                                remove(i);
                                postDelCollect(video, 0);
                                String msg = context.getString(R.string.cancel_collect_success);
                                ToastUtils.showToast(context, msg);
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        };
    }
}
