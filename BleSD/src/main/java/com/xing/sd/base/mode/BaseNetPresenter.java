package com.xing.sd.base.mode;

import android.content.Context;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;


/**
 * @author 星哥的
 */

public class BaseNetPresenter<T extends BaseNetContract.View, M extends BaseNetContract.Model> implements BaseNetContract.Presenter<T> {
    protected Reference<T> mView;
    protected M mModel;
    protected Reference<Context> mContext;
    protected int requestCode = 0;

    public BaseNetPresenter(Context context) {
        mContext = new WeakReference<>(context);
    }

    @Override
    public void attachView(T view) {
        mView = new WeakReference<>(view);
    }

    @Override
    public void detachView() {
        if (mView != null) {
            mView.clear();
            mView = null;
        }
        if (mContext != null) {
            mContext.clear();
            mContext = null;
        }
        mModel = null;
    }

    /**
     * 是否自动处理TCP ture自动接收TCP  false 不处理
     *
     * @return
     */
    public boolean isAutoCallBack() {
        return true;
    }


    @Override
    public void cancelRequest() {

    }

    @Override
    public void checkAppUpdate() {
        /*HashMap<String, Object> params = new HashMap<>();
        params.put("Ver", Tool.getVersionName(getContext()));
        params.put("Type", 2);
        requestGetData(MyConstants.URLConstant.CheckAppVersion, params, 100, false);*/
    }

    @Override
    public void releaseAll() {

    }


    public T getView() {
        if (mView != null) {
            return mView.get();
        }
        return null;
    }

    public M getModel() {
        return mModel;
    }

    public Context getContext() {
        return mContext.get();
    }


    private void startUpdate(String url) {
//        UpdateService.setDownloadListener(null);
//        Intent intentService = new Intent(getContext(), UpdateService.class);
//        intentService.putExtra("filePath", url);
//        intentService.putExtra("savePath", getContext().getExternalCacheDir().getAbsolutePath());
//        getContext().startService(intentService);
    }

}
