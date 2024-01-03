package com.xing.sd.base.mode;

import android.content.Context;


public class BasePresenter extends BaseNetPresenter<BaseContract.View, BaseContract.Model> implements BaseContract.Presenter {

    public BasePresenter(Context context) {
        super(context);
    }

    @Override
    public void attachView(BaseContract.View view) {
        super.attachView(view);
        mModel = new BaseModel();
    }

}
