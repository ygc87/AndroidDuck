package com.xhy.neihanduanzi.base;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBar;

import com.xhy.neihanduanzi.utils.DialogHelper;


/**
 * Created by JuQiu
 * on 16/6/20.
 */

public abstract class BaseBackActivity extends BaseActivity {

    @Override
    protected void initWindow() {
        super.initWindow();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(false);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
