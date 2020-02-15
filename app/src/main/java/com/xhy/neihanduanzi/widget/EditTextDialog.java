package com.xhy.neihanduanzi.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.example.chaokun.neihanduanzi.R;


/**
 * Created by xz on 2017/6/14 0014.
 * 用户输入dialog
 */

public class EditTextDialog {
    private Context mContext;
    private AlertDialog mAlertDialog;
    private View mView;
    private RichEditText mEditText;
    private Button mBtnCommit;
    private View.OnClickListener mListener;

    public EditTextDialog(Context context) {
        this.mContext = context;
        mAlertDialog = new AlertDialog.Builder(mContext, R.style.EdittextAlertDialog).create();
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_bottom_sheet_comment_bar, null);
        mEditText = (RichEditText) mView.findViewById(R.id.et_comment);
        mBtnCommit = (Button) mView.findViewById(R.id.btn_comment);
    }


    public RichEditText getRichEditText() {
        return mEditText;
    }

    public Button getBtnCommit() {
        return mBtnCommit;
    }

    public void showDialog() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_bottom_sheet_comment_bar, null);
        mAlertDialog.show();
        mAlertDialog.setContentView(mView);
        mEditText = (RichEditText) mView.findViewById(R.id.et_comment);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mBtnCommit.setEnabled(s.length() > 0);
            }
        });

        mBtnCommit = (Button) mView.findViewById(R.id.btn_comment);
        mBtnCommit.setEnabled(false);
        mBtnCommit.setOnClickListener(mListener);

        mAlertDialog.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        mAlertDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        InputMethodManager imm = (InputMethodManager) mEditText.getContext()// 弹出软键盘
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        // 必须放到显示对话框下面，否则显示不出效果
        Window window = mAlertDialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        // 加载布局组件
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        if (mAlertDialog != null) {
            mAlertDialog.setOnDismissListener(listener);
        }
    }

    public void setCommitListener(View.OnClickListener listener) {
        mListener = listener;
    }

    public void dismiss(){
        mAlertDialog.dismiss();
    }

}
