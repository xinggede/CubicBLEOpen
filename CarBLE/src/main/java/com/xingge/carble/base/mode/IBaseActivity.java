package com.xingge.carble.base.mode;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;

import com.xingge.carble.base.BaseActivity;
import com.xingge.carble.dialog.ProDialog;


public abstract class IBaseActivity<P extends BaseNetPresenter> extends BaseActivity implements BaseNetContract.View, DialogInterface.OnKeyListener {

    protected P mPresenter;
    private ProDialog proDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mPresenter = onLoadPresenter();
        try {
            getPresenter().attachView(this);
        } catch (Exception e) {

        }

        initViews(savedInstanceState);
        initEventAndData();
    }

    public P getPresenter() {
        return mPresenter;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    protected abstract P onLoadPresenter();

    public abstract int getLayoutId();

    protected abstract void initViews(Bundle savedInstanceState);

    protected abstract void initEventAndData();

    @Override
    public void showProDialog() {
        if (proDialog == null) {
            proDialog = new ProDialog(this);
        }
        if (!proDialog.isShowing()) {
            proDialog.show();
        }
    }

    @Override
    public void dismissProDialog() {
        if (proDialog != null) {
            proDialog.dismiss();
        }
    }

    @Override
    public void setShowText(String text) {
        if (proDialog != null) {
            proDialog.setTvShow(text);
        }
    }

    @Override
    public void setCancelable(boolean b) {
        if (proDialog != null) {
            proDialog.setCancelable(b);
        }
    }

    @Override
    protected void onDestroy() {
        dismissProDialog();
        super.onDestroy();
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dialog.dismiss();
            getPresenter().cancelRequest();
            return true;
        }
        return false;
    }
}
