package com.xhy.neihanduanzi.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chaokun.neihanduanzi.R;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lidroid.xutils.exception.DbException;
import com.loopj.android.http.TextHttpResponseHandler;
import com.xhy.neihanduanzi.activity.socres.GiftDetailActivity;
import com.xhy.neihanduanzi.app.api.XHYApi;
import com.xhy.neihanduanzi.callback.LoadFinishCallBack;
import com.xhy.neihanduanzi.callback.LoadResultCallBack;
import com.xhy.neihanduanzi.model.bean.Gift;
import com.xhy.neihanduanzi.utils.DataBaseCrete;
import com.xhy.neihanduanzi.utils.NetWorkUtil;
import com.xhy.neihanduanzi.utils.dbutils.AccountHelper;
import com.xhy.neihanduanzi.utils.glide.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class ScoreGiftAdapter extends RecyclerView.Adapter<ScoreGiftAdapter.GiftViewHolder> {

    private Activity mActivity;

    private LoadResultCallBack mLoadResultCallBack;
    private LoadFinishCallBack mLoadFinisCallBack;
    private DataBaseCrete dataBaseCrete;

    private int page;
    private List<Gift> giftList;

    public ScoreGiftAdapter(Activity activity, LoadFinishCallBack loadFinisCallBack, LoadResultCallBack loadResultCallBack) throws DbException {

        mActivity = activity;
        mLoadFinisCallBack = loadFinisCallBack;
        mLoadResultCallBack = loadResultCallBack;

        giftList = new ArrayList<>();
        dataBaseCrete = new DataBaseCrete(activity);

    }

    @Override
    public GiftViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_my_gift, parent, false);
        return new GiftViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final GiftViewHolder holder, int position) {
        final Gift gift = giftList.get(position);
        if (gift != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    GiftDetailActivity.show(mActivity, gift);
                }
            });
            ImageLoader.load(mActivity, gift.getImage(), holder.ivGift);
            holder.tvGiftName.setText(gift.getName());
            holder.tvScore.setText(String.valueOf(gift.getScore())+" 积分");
        }
    }

    @Override
    public int getItemCount() {
        return giftList.size();
    }

    public void loadFirst() throws DbException {
        page = 1;
        loadDataByNetworkType();
    }

    public void loadNextPage() throws DbException {
        page++;
        loadDataByNetworkType();
    }

    private void loadDataByNetworkType() throws DbException {
        if (NetWorkUtil.isNetWorkConnected(mActivity)) {
            loadData();
        } else {
            loadCache();
        }
    }

    public void loadData() {
        //获取礼物数据
        XHYApi.getGiftList(AccountHelper.getAccount(), new TextHttpResponseHandler() {

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
                            giftList = stringToList(responseString, Gift.class);
                            notifyDataSetChanged();
                            mLoadResultCallBack.onSuccess(giftList);
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
                }

        );
    }

    //refresh will load
    private void loadCache() throws DbException {
        if (dataBaseCrete == null) {
            dataBaseCrete = new DataBaseCrete(mActivity);
        }
//        DataBase db = dataBaseCrete.findPage(page, Constants.menu5);
//        if (null != db) {
//            String request = db.getRequest();
//            giftList = GsonUtil.parseJsonArrayWithGson(request,
//                    Gift.class);
            notifyDataSetChanged();
            mLoadResultCallBack.onSuccess(giftList);
            mLoadFinisCallBack.loadFinish(null);
//        }
    }

    /**
     * 缓存数据,保存数据库
     *
     * @param request
     * @throws DbException
     */
    private void SaveDataBase(String request) throws DbException {
        dataBaseCrete = new DataBaseCrete(mActivity);
//        dataBaseCrete.delete(page, Constants.menu5);
//
//        DataBase data = new DataBase();
//        data.setId(page);
//        data.setRequest(request);
//        data.setPage(page);
//        data.setMenuNumber(Constants.menu5);
//        dataBaseCrete.sava(data);
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

    public static class GiftViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_my_gift)
        ImageView ivGift;

        @BindView(R.id.tv_my_gift)
        TextView tvGiftName;

        @BindView(R.id.tv_score)
        TextView tvScore;

        public GiftViewHolder(View contentView) {
            super(contentView);
            ButterKnife.bind(this, contentView);
        }
    }
}
