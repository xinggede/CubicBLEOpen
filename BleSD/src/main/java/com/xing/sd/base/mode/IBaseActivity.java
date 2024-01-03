package com.xing.sd.base.mode;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;

import com.xing.sd.base.BaseActivity;
import com.xing.sd.dialog.ProDialog;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import androidx.viewbinding.ViewBinding;

public abstract class IBaseActivity<VB extends ViewBinding, P extends BaseNetPresenter> extends BaseActivity implements BaseNetContract.View, DialogInterface.OnKeyListener {

    protected P mPresenter;
    private ProDialog proDialog;

    protected VB binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = createBinding();
        setContentView(binding.getRoot());
        mPresenter = onLoadPresenter();
        try {
            getPresenter().attachView(this);
        } catch (Exception e) {

        }

        initViews(savedInstanceState);
        initEventAndData();
    }

    private VB createBinding() {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        Class<VB> vbClass = (Class<VB>) type.getActualTypeArguments()[0];
        try {
            Method inflateMethod = vbClass.getMethod("inflate", LayoutInflater.class);
            return (VB) inflateMethod.invoke(null, getLayoutInflater());
        } catch (Exception e) {

        }
        return null;
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

    public boolean isShowDialog(){
        return proDialog != null && proDialog.isShowing();
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
            getPresenter().cancelRequest();
            dialog.dismiss();
            return true;
        }
        return false;
    }
}
