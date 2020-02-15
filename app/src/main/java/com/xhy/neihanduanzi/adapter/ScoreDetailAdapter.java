package com.xhy.neihanduanzi.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chaokun.neihanduanzi.R;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lidroid.xutils.exception.DbException;
import com.loopj.android.http.TextHttpResponseHandler;
import com.xhy.neihanduanzi.app.api.XHYApi;
import com.xhy.neihanduanzi.callback.LoadFinishCallBack;
import com.xhy.neihanduanzi.callback.LoadResultCallBack;
import com.xhy.neihanduanzi.model.bean.DataBase;
import com.xhy.neihanduanzi.model.bean.ScoreDetail;
import com.xhy.neihanduanzi.utils.DataBaseCrete;
import com.xhy.neihanduanzi.utils.NetWorkUtil;
import com.xhy.neihanduanzi.utils.StringUtils;
import com.xhy.neihanduanzi.utils.dbutils.AccountHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * 个人积分详情页
 */

public class ScoreDetailAdapter extends RecyclerView.Adapter {

    public static final int ONLY_HEADER = 1;
    public static final int ONLY_FOOTER = 2;
    public static final int BOTH_HEADER_FOOTER = 3;

    public static final int VIEW_TYPE_NORMAL = 0;
    public static final int VIEW_TYPE_HEADER = -1;
    public static final int VIEW_TYPE_FOOTER = -2;

    public static final int STATE_NO_MORE = 1;
    public static final int STATE_LOAD_MORE = 2;
    public static final int STATE_INVALID_NETWORK = 3;
    public static final int STATE_HIDE = 5;
    public static final int STATE_REFRESHING = 6;
    public static final int STATE_LOAD_ERROR = 7;
    public static final int STATE_LOADING = 8;
    public static final int STATE_CLICK_LOADMORE = 9;

    public int BEHAVIOR_MODE;

    private int page;

    //状态
    protected int mState;

    private Activity mActivity;
    private List<ScoreDetail> detailList;

    private LoadResultCallBack mLoadResultCallBack;
    private LoadFinishCallBack mLoadFinisCallBack;
    private DataBaseCrete dataBaseCrete;

    protected LayoutInflater mInflater;

    public ScoreDetailAdapter(Activity activity, LoadFinishCallBack loadFinisCallBack, LoadResultCallBack loadResultCallBack) throws DbException {
        mActivity = activity;
        mLoadFinisCallBack = loadFinisCallBack;
        mLoadResultCallBack = loadResultCallBack;
        detailList = new ArrayList<>();
        dataBaseCrete = new DataBaseCrete(activity);
        this.mInflater = LayoutInflater.from(activity);
        page = 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_HEADER:
            case VIEW_TYPE_FOOTER:
                return new FooterViewHolder(mInflater.inflate(R.layout.recycler_footer_view, parent, false));
            default:
                View view = mInflater.inflate(R.layout.list_item_my_score_detail, parent, false);
                return new ScoreDetailViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_FOOTER:
                FooterViewHolder fvh = (FooterViewHolder) holder;
                switch (mState) {
                    case STATE_INVALID_NETWORK:
                        fvh.tv_footer.setText(mActivity.getResources().getString(R.string.state_network_error));
                        fvh.pb_footer.setVisibility(View.GONE);
                        break;
                    case STATE_LOAD_MORE:
                    case STATE_LOADING:
                        fvh.tv_footer.setText(mActivity.getResources().getString(R.string.state_loading));
                        fvh.pb_footer.setVisibility(View.VISIBLE);
                        break;
                    case STATE_NO_MORE:
                        fvh.tv_footer.setText(mActivity.getResources().getString(R.string.state_not_more));
                        fvh.pb_footer.setVisibility(View.GONE);
                        break;
                    case STATE_REFRESHING:
                        fvh.tv_footer.setText(mActivity.getResources().getString(R.string.state_refreshing));
                        fvh.pb_footer.setVisibility(View.GONE);
                        break;
                    case STATE_LOAD_ERROR:
                        fvh.tv_footer.setText(mActivity.getResources().getString(R.string.state_load_error));
                        fvh.pb_footer.setVisibility(View.GONE);
                        break;
                    case STATE_HIDE:
                        fvh.itemView.setVisibility(View.GONE);
                        break;
                    case STATE_CLICK_LOADMORE:
                        fvh.tv_footer.setText(mActivity.getResources().getString(R.string.state_click_loadmore));
                        fvh.pb_footer.setVisibility(View.GONE);
                        break;
                }
            default:
                ScoreDetailViewHolder svh = (ScoreDetailViewHolder) holder;
                final ScoreDetail detail = detailList.get(position);
                if (detail != null) {
                    svh.tvDetailName.setText(detail.getAction());
                    svh.tvDetailResult.setText(detail.getScore());
                    String time = String.valueOf(detail.getCtime());
                    svh.tvDetailTime.setText(StringUtils.timedate(time));
                    int score = 0;
                    try {
                        score = Integer.parseInt(detail.getScore());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    if (score > 0) {
                        svh.tvDetailResult.setTextColor(mActivity.getResources().getColor(R.color.bg_my_score_reuslt));
                    } else {
                        svh.tvDetailResult.setTextColor(mActivity.getResources().getColor(R.color.themeColor));
                    }
                }
                break;
        }
    }

    public void loadFirst()  {
        loadDataByNetworkType();
    }

    public void loadNextPage()  {
        loadDataByNetworkType();
    }

    private void loadDataByNetworkType() {
        if (NetWorkUtil.isNetWorkConnected(mActivity)) {
            loadData();
        } else {
            loadCache();
        }
    }

    private void loadData() {
        if(AccountHelper.getAccount() == null){
                return;
        }
        XHYApi.getScoreDetailList(AccountHelper.getAccount(), page, 0, new TextHttpResponseHandler() {

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

                    List<ScoreDetail> resultList = stringToList(responseString, ScoreDetail.class);
                    mLoadResultCallBack.onSuccess(resultList);
                    mLoadFinisCallBack.loadFinish(null);
                    detailList.addAll(resultList);
                    int count = resultList.size();
                    if (count > 0) {
                        page++;
                    }
                    if (detailList.size() < 20) {
                        //setState(BaseCommentsAdapter.STATE_NO_MORE, true);
                    } else {
                        //setState(BaseCommentsAdapter.STATE_HIDE, true);
                    }
                    notifyDataSetChanged();
                    SaveDataBase(responseString);
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
        });
    }

    public void loadData(int limit) {
        XHYApi.getScoreDetailList(AccountHelper.getAccount(), page, limit, new TextHttpResponseHandler() {

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
                    detailList = stringToList(responseString, ScoreDetail.class);
                    notifyDataSetChanged();
                    mLoadResultCallBack.onSuccess(detailList);
                    mLoadFinisCallBack.loadFinish(null);
                    SaveDataBase(responseString);
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
        });
    }

    //refresh will load
    private void loadCache() {

    }

    /**
     * 缓存数据,保存数据库
     *
     * @param request
     * @throws DbException
     */
    private void SaveDataBase(String request) throws DbException {
//        dataBaseCrete = new DataBaseCrete(mActivity);
//        dataBaseCrete.delete(page, Constants.menu5);

        DataBase data = new DataBase();
        data.setId(page);
        data.setRequest(request);
        data.setPage(page);
        //data.setMenuNumber(Constants.menu5);
        dataBaseCrete.sava(data);
    }

    public static <T> List<T> stringToList(String json, Class<T> cls) {
        Gson gson = new Gson();
        List<T> list = new ArrayList<T>();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        for (final JsonElement elem : array) {
            list.add(gson.fromJson(elem, cls));
        }
        return list;
    }

    public void setState(int mState, boolean isUpdate) {
        this.mState = mState;
        if (isUpdate)
            updateItem(getItemCount() - 1);
    }

    public void updateItem(int position) {
        if (getItemCount() > position) {
            notifyItemChanged(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && (BEHAVIOR_MODE == ONLY_HEADER || BEHAVIOR_MODE == BOTH_HEADER_FOOTER))
            return VIEW_TYPE_HEADER;
        if (position + 1 == getItemCount() && (BEHAVIOR_MODE == ONLY_FOOTER || BEHAVIOR_MODE == BOTH_HEADER_FOOTER))
            return VIEW_TYPE_FOOTER;
        else return VIEW_TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        return detailList.size();
    }

    public static class ScoreDetailViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.tv_my_score_detail_name)
        TextView tvDetailName;

        @BindView(R.id.tv_my_score_detail_time)
        TextView tvDetailTime;

        @BindView(R.id.tv_my_score_detail_result)
        TextView tvDetailResult;


        public ScoreDetailViewHolder(View contentView) {
            super(contentView);
            ButterKnife.bind(this, contentView);
        }
    }


    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar pb_footer;
        public TextView tv_footer;

        public FooterViewHolder(View view) {
            super(view);
            pb_footer = (ProgressBar) view.findViewById(R.id.pb_footer);
            tv_footer = (TextView) view.findViewById(R.id.tv_footer);
        }
    }
}
