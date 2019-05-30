package com.xingge.carble.base.mode;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xingge.carble.base.BaseFragment;


/**
 * Created by 星哥的 on 2018/3/22.
 */

public abstract class IBaseFragment<P extends BaseNetPresenter> extends BaseFragment implements BaseNetContract.View {

    protected P mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = onLoadPresenter();
        try {
            getPresenter().attachView(this);
        } catch (Exception e) {

        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initEventAndData();
    }

    protected abstract P onLoadPresenter();

    protected abstract void initEventAndData();


    public P getPresenter() {
        return mPresenter;
    }

    @Override
    public void onDestroy() {
        if (getPresenter() != null) {
            getPresenter().detachView();
        }
        super.onDestroy();
    }
}
