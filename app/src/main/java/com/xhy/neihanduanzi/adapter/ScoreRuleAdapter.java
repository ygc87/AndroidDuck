package com.xhy.neihanduanzi.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.xhy.neihanduanzi.model.bean.ScoreRule;
import com.xhy.neihanduanzi.utils.DataBaseCrete;
import com.xhy.neihanduanzi.utils.NetWorkUtil;
import com.xhy.neihanduanzi.utils.dbutils.AccountHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by mkt on 2017/10/2.
 */

public class ScoreRuleAdapter extends RecyclerView.Adapter<ScoreRuleAdapter.RuleViewHolder> {

    private int page;

    private Activity mActivity;
    private List<ScoreRule> ruleList;

    private LoadResultCallBack mLoadResultCallBack;
    private LoadFinishCallBack mLoadFinisCallBack;
    private DataBaseCrete dataBaseCrete;


    public ScoreRuleAdapter(Activity activity, LoadFinishCallBack loadFinisCallBack, LoadResultCallBack loadResultCallBack) throws DbException {

        mActivity = activity;
        mLoadFinisCallBack = loadFinisCallBack;
        mLoadResultCallBack = loadResultCallBack;
        ruleList = new ArrayList<>();
        dataBaseCrete = new DataBaseCrete(activity);

    }

    @Override
    public RuleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_score_rule, parent, false);
        return new RuleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RuleViewHolder holder, int position) {
        final ScoreRule rule = ruleList.get(position);
        if (rule != null) {
            holder.tvRuleName.setText(rule.getAlias());
            if (rule.getExperience() > 0) {
                holder.tvRuleScore.setText(String.valueOf(rule.getScore()));
            } else {
                //holder.tvRuleExp.setText("经验" + String.valueOf(rule.getExperience()));
                holder.tvRuleScore.setText(String.valueOf(rule.getScore()));
            }
        }
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

    private void loadData() {
        //获取礼物数据
        XHYApi.getScoreRuleList(AccountHelper.getAccount(), new TextHttpResponseHandler() {

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
                            ruleList = stringToList(responseString, ScoreRule.class);
                            notifyDataSetChanged();
                            mLoadResultCallBack.onSuccess(ruleList);
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
//            ruleList = GsonUtil.parseJsonArrayWithGson(request,
//                    ScoreRule.class);
            notifyDataSetChanged();
            mLoadResultCallBack.onSuccess(ruleList);
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
//        dataBaseCrete = new DataBaseCrete(mActivity);
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

    @Override
    public int getItemCount() {
        return ruleList.size();
    }

    public static class RuleViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_score_rule_name)
        TextView tvRuleName;

        @BindView(R.id.tv_score_rule_score)
        TextView tvRuleScore;


        public RuleViewHolder(View contentView) {
            super(contentView);
            ButterKnife.bind(this, contentView);
        }
    }
}
