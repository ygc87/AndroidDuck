package com.xhy.neihanduanzi.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.xhy.neihanduanzi.activity.LoginActivity;
import com.xhy.neihanduanzi.utils.dbutils.AccountHelper;
import com.xhy.neihanduanzi.widget.BottomSheetBar;
import com.example.chaokun.neihanduanzi.R;

/**
 * Created by haibin
 * on 2016/11/10.
 * Change by fei
 * on 2016/11/17
 * desc:详情页输入框
 */
@SuppressWarnings("all")
public class CommentBar {

    private Context mContext;
    private View mRootView;
    private FrameLayout mFrameLayout;
    private ViewGroup mParent;
    private ImageButton mFavView;
    private ImageButton mShareView;
    private TextView mCommentText;
    private BottomSheetBar mDelegation;
    private LinearLayout mCommentLayout;


    private CommentBar(Context context) {
        this.mContext = context;
    }

    public static CommentBar delegation(Context context, ViewGroup parent) {
        CommentBar bar = new CommentBar(context);
        bar.mRootView = LayoutInflater.from(context).inflate(R.layout.layout_comment_bar, parent, false);
        bar.mParent = parent;
        bar.mDelegation = BottomSheetBar.delegation(context);
        bar.mParent.addView(bar.mRootView);
        bar.initView();
        return bar;
    }

    private void initView() {
        mCommentText = (TextView) mRootView.findViewById(R.id.tv_comment);
        mCommentLayout = (LinearLayout) mRootView.findViewById(R.id.ll_comment);
        mCommentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccountHelper.isLogin()) {
                    StatService.onEvent(mContext, "CommentBar","show_comment");
                    mDelegation.show(mCommentText.getHint().toString());
                } else {
                    LoginActivity.show(mContext);
                }
            }
        });
    }

    public void setCommentListener(View.OnClickListener listener) {
        mCommentText.setOnClickListener(listener);
    }

    public void setCommentHint(String text) {
        mCommentText.setHint(text);
    }

    public void setFavDrawable(int drawable) {
        mFavView.setImageResource(drawable);
    }

    public BottomSheetBar getBottomSheet() {
        return mDelegation;
    }

    public void setCommitButtonEnable(boolean enable) {
        mDelegation.getBtnCommit().setEnabled(enable);
    }

    public TextView getCommentText() {
        return mCommentText;
    }


    public void performClick() {
        mCommentLayout.performClick();
    }

}

