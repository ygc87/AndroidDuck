package com.xhy.neihanduanzi.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.xhy.neihanduanzi.utils.TDevice;
import com.example.chaokun.neihanduanzi.R;

/**
 * 底部弹出评论框
 *
 */

public class BottomSheetBar {

    private View mRootView;
    private Context mContext;
    private EditTextDialog mDialog;

    private BottomSheetBar(Context context) {
        this.mContext = context;
    }

    @SuppressLint("InflateParams")
    public static BottomSheetBar delegation(Context context) {
        BottomSheetBar bar = new BottomSheetBar(context);
        bar.mRootView = LayoutInflater.from(context).inflate(R.layout.layout_bottom_sheet_comment_bar, null, false);
        bar.initView();
        return bar;
    }

    private void initView() {
        mDialog = new EditTextDialog(mContext);
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                TDevice.closeKeyboard(mDialog.getRichEditText());
            }
        });
    }

    public void show(String hint) {
        mDialog.showDialog();
        if (!"添加评论".equals(hint)) {
            mDialog.getRichEditText().setHint(hint + " ");
        }
        mRootView.postDelayed(new Runnable() {
            @Override
            public void run() {
                TDevice.showSoftKeyboard(mDialog.getRichEditText());
            }
        }, 150);
    }

    public void dismiss() {
        TDevice.closeKeyboard(mDialog.getRichEditText());
        mDialog.dismiss();
    }

    public void setCommitListener(View.OnClickListener listener) {
        mDialog.setCommitListener(listener);
    }

    public RichEditText getEditText() {

        return mDialog.getRichEditText();
    }

    public EditTextDialog getDialog() {
        return mDialog;
    }

    public String getCommentText() {
        return mDialog.getRichEditText().getText().toString().trim();
    }

    public Button getBtnCommit() {
        return mDialog.getBtnCommit();
    }

}
