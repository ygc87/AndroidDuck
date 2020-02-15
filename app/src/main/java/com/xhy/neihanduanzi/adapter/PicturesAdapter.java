package com.xhy.neihanduanzi.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.chaokun.neihanduanzi.R;
import com.loopj.android.http.TextHttpResponseHandler;
import com.xhy.neihanduanzi.AppContext;
import com.xhy.neihanduanzi.activity.ImageGalleryActivity;
import com.xhy.neihanduanzi.activity.LoginActivity;
import com.xhy.neihanduanzi.app.AppOperator;
import com.xhy.neihanduanzi.app.api.XHYApi;
import com.xhy.neihanduanzi.callback.LoadFinishCallBack;
import com.xhy.neihanduanzi.callback.LoadResultCallBack;
import com.xhy.neihanduanzi.model.bean.Account;
import com.xhy.neihanduanzi.model.bean.PageBean;
import com.xhy.neihanduanzi.model.bean.Picture;
import com.xhy.neihanduanzi.model.bean.PictureRecord;
import com.xhy.neihanduanzi.model.bean.Status;
import com.xhy.neihanduanzi.model.event.LoadAllDataEvent;
import com.xhy.neihanduanzi.model.event.PicCollectEvent;
import com.xhy.neihanduanzi.model.event.PicDiggEvent;
import com.xhy.neihanduanzi.utils.DialogHelper;
import com.xhy.neihanduanzi.utils.GsonUtil;
import com.xhy.neihanduanzi.utils.NetWorkUtil;
import com.xhy.neihanduanzi.utils.TDevice;
import com.xhy.neihanduanzi.utils.ToastUtils;
import com.xhy.neihanduanzi.utils.dbutils.AccountHelper;
import com.xhy.neihanduanzi.utils.dbutils.PictureRecordHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by mkt on 2018/3/21.
 */

public class PicturesAdapter extends BasePictureAdapter implements BaseQuickAdapter.OnItemClickListener {

    public int page = 0;

    public int mCurrentPos = -1;

    private Activity mActivity;

    private Account mAccount;

    private PictureRecord mPictureRecord;

    private String mAdapterType;

    private int mType;

    public LoadResultCallBack mLoadResultCallBack;
    public LoadFinishCallBack mLoadFinisCallBack;

    private PageBean<Picture> mPageBean;


    public PicturesAdapter(Activity activity, @Nullable List<Picture> data, LoadFinishCallBack loadFinisCallBack, LoadResultCallBack loadResultCallBack) {
        super(activity, data, loadFinisCallBack, loadResultCallBack);
        mActivity = activity;
        mContext = AppContext.context();
        mAccount = AccountHelper.getAccount();
        mLoadResultCallBack = loadResultCallBack;
        mLoadFinisCallBack = loadFinisCallBack;
        setOnItemClickListener(this);
        mPageBean = new PageBean<>();
    }

    public void loadFirst() {
        loadDataByNetworkType(0);
    }

    public void loadDataByNetworkType(int type) {
        if (NetWorkUtil.isNetWorkConnected(mActivity)) {
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
        mPictureRecord = PictureRecordHelper.getLastNewsRecord(uid, mAdapterType);
        List<Picture> picList = new ArrayList<Picture>();
        if (mPictureRecord != null) {
            picList = PictureRecordHelper.convertToPictureList(mPictureRecord.getJson());
            setNewData(picList);
        }
        mLoadResultCallBack.onSuccess(picList);
    }

    public void setAdapterType(String type) {
        mAdapterType = type;
    }

    public void loadData(final int type) {
        if (mAdapterType == null || mAccount == null) {
            return;
        }
        mType = type;
        if (mType == 0) {
            mPageBean.setNextPageToken(1);
        }
        page = mPageBean.getNextPageToken();
        if (mAdapterType.equals("normal")) {
            XHYApi.getAllPicList(mAccount, getDataHandler(type));
        } else if (mAdapterType.equals("collect")) {
            XHYApi.getPicCollectedList(mAccount, page, getSpecialDataHandler(type));
        } else if (mAdapterType.equals("payed")) {
            XHYApi.getBuyPicObjectList(mAccount, page, getSpecialDataHandler(type));
        }
    }

    public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {
        if (!AccountHelper.isLogin()) {
            callLoginPage();
        } else {
            if (!TDevice.hasInternet()) {
                ToastUtils.showToast(mActivity, "网络异常");
                return;
            }
            int id = view.getId();
            final Picture picture = getItem(position);
            BaseViewHolder holder = (BaseViewHolder) view.getTag(id);
            switch (id) {
                case R.id.title_picture:
                    callImageGallery(position, picture);
                    break;
                case R.id.tv_digg:
                    TextHttpResponseHandler diggHandler = getDiggHandler();
                    diggHandler.setTag(holder);
                    XHYApi.addPicLike((int) picture.getId(), AccountHelper.getAccount(), diggHandler);
                    break;
                case R.id.tv_collected:
                    TextHttpResponseHandler collHandler = getCollHandler();
                    collHandler.setTag(holder);
                    XHYApi.addPicCollected((int) picture.getId(), AccountHelper.getAccount(), collHandler);
                    break;
                case R.id.bottom:
                case R.id.item_layout:
                    break;
                case R.id.content_picture:
                    callImageGallery(position, picture);
                    break;
                case R.id.im_delete:
                    getDeleteHandler().setTag(holder);
                    final TextHttpResponseHandler deleteHandler = getDeleteHandler();
                    deleteHandler.setTag(holder);
                    DialogHelper.getConfirmDialog(mActivity, "", "是否取消该收藏", "确定", "取消", false, new DialogInterface.OnClickListener() {
                        //确定
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", String.valueOf(picture.getId()));
                            deleteHandler.setTag(picture);
                            XHYApi.dellPicCollected((int) picture.getId(), AccountHelper.getAccount(), deleteHandler);
                            StatService.onEvent(mActivity, "BaseRecyclerPicAdapter", "delete_pic", 1, hashMap);
                        }
                    }, new DialogInterface.OnClickListener() {
                        //取消
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
                    break;
                case R.id.ad_item:
                    break;
                default:
                    break;

            }
        }
    }

    public void callImageGallery(int position, Picture picture) {
        String[] paths = Picture.Image.getImagePath(picture.getImages());
        mCurrentPos = position;
        ImageGalleryActivity.show(mActivity, paths, position, false, picture);
    }

    public TextHttpResponseHandler getDataHandler(final int type) {
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
                    List<Picture> resultList = GsonUtil.stringToList(responseString, Picture.class);
                    int pos;
                    int count = resultList.size();
                    if (type == 0) {
                        getData().addAll(0, resultList);
                        notifyDataSetChanged();
                    } else {
                        pos = getData().size();
                        getData().addAll(resultList);
                        notifyItemRangeInserted(pos, count);
                    }
                    if (resultList.size() < 10) {
                        loadMoreEnd(true);
                    }
                    mLoadResultCallBack.onSuccess(resultList);
                    mLoadFinisCallBack.loadFinish(null);
                    String msg = "当前更新" + String.valueOf(count) + "条数据";
                    mLoadResultCallBack.onMessage(msg);
                    if (mAccount != null) {
                        PictureRecordHelper.save(mAccount.getUid(), mAdapterType, responseString);
                    }
                    postLoadFinish();
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

    public TextHttpResponseHandler getSpecialDataHandler(final int type) {
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
                    List<Picture> resultList = AppOperator.GsonStringToList(s2, Picture.class);
                    if (mType == 0) {
                        getData().clear();
                    }
                    getData().addAll(resultList);
                    mLoadResultCallBack.onSuccess(getData());
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

    public TextHttpResponseHandler getDiggHandler() {
        return new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                ToastUtils.showToast(mActivity, "点赞失败");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                Status status = AppOperator.createGson().fromJson(responseString, Status.class);
                //根据status code
                if (status.getStatus() == 1) {
                    BaseViewHolder holder = (BaseViewHolder) getTag();
                    TextView tvDigg = holder.getView(R.id.tv_digg);
                    int digg_count = Integer.parseInt(tvDigg.getText().toString());
                    postDiggEvent(holder.getAdapterPosition(), digg_count + 1, 1);
                }
                ToastUtils.showToast(mActivity, status.getMessage());
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

    public TextHttpResponseHandler getCollHandler() {

        return new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                ToastUtils.showToast(mActivity, "收藏失败");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Status status = AppOperator.createGson().fromJson(responseString, Status.class);
                    if (status.getStatus() == 1) {
                        BaseViewHolder holder = (BaseViewHolder) getTag();
                        TextView tvCollected = holder.getView(R.id.tv_collected);
                        int coll_count = Integer.parseInt(tvCollected.getText().toString());
                        postCollectEvent(holder.getAdapterPosition(), coll_count + 1, 1);
                    }
                    ToastUtils.showToast(mActivity, status.getMessage());
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

    public TextHttpResponseHandler getDeleteHandler() {
        return new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                ToastUtils.showToast(mActivity, "取消收藏失败");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Status status = AppOperator.createGson().fromJson(responseString, Status.class);
                    if (status.getStatus() == 1) {
                        Picture picture = (Picture) getTag();
                        if (picture != null) {
                            getData().remove(picture);
                            ToastUtils.showToast(mActivity, "已取消收藏");
                            postDelCollect(picture, 0);
                        }
                    } else {
                        ToastUtils.showToast(mActivity, status.getMessage());
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

    public void postDiggEvent(int position, int diggCount, int state) {
        PicDiggEvent event = new PicDiggEvent();
        event.setPosition(position);
        event.setCount(diggCount);
        event.setState(state);
        event.setGoodViewShow(true);
        EventBus.getDefault().post(event);
    }

    public void postCollectEvent(int position, int count, int state) {
        PicCollectEvent event = new PicCollectEvent();
        event.setPosition(position);
        event.setCount(count);
        event.setState(state);
        event.setGoodViewShow(true);
        event.setAction(PicCollectEvent.ADD_PIC);
        EventBus.getDefault().post(event);
    }

    public void postDelCollect(Picture picture, int state) {
        PicCollectEvent event = new PicCollectEvent();
        event.setPicture(picture);
        event.setState(state);
        event.setAction(PicCollectEvent.DEL_PIC);
        EventBus.getDefault().post(event);
    }

    public void callLoginPage() {
        ToastUtils.showToast(mActivity, "请登录");
        LoginActivity.show(mActivity);
    }

    private void postLoadFinish() {
        LoadAllDataEvent event = new LoadAllDataEvent();
        EventBus.getDefault().post(event);
    }
}
