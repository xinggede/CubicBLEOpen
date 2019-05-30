package com.xingge.carble.base.mode;

import android.content.Context;

import com.xingge.carble.net.NetRequest;

public class BasePresenter extends BaseNetPresenter<BaseContract.View, BaseContract.Model> implements BaseContract.Presenter {

    public BasePresenter(Context context) {
        super(context);
    }

    @Override
    public void attachView(BaseContract.View view) {
        super.attachView(view);
        mModel = new BaseModel();
    }

    @Override
    public void netProgress(int what, long bytesWritten, long totalSize, NetRequest request) {

    }
}
