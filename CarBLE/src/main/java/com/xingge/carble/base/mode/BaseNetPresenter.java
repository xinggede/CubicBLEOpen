package com.xingge.carble.base.mode;

import android.content.Context;

import com.xingge.carble.bluetooth.BlueToothManage;
import com.xingge.carble.net.NetRequest;
import com.xingge.carble.net.NetResultListener;
import com.xingge.carble.net.NetTask;
import com.xingge.carble.util.Tool;

import org.json.JSONObject;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * @author 星哥的
 */

public class BaseNetPresenter<T extends BaseNetContract.View, M extends BaseNetContract.Model> implements BaseNetContract.Presenter<T>, NetResultListener {
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
    public void requestHttp(NetRequest netRequest, NetResultListener netResultListener) {
        getModel().requestHttp(netRequest, this);
    }

    @Override
    public void cancelRequest() {
        if (requestCode == 1) {
        } else if (requestCode == 2) {
            getModel().cancelRequest(this);
            NetTask.cancelLast(mContext);
        }
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
        BlueToothManage.getInstance(getContext().getApplicationContext()).releaseAll();
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


    public void requestGetData(String urlConstant, Map<String, Object> params, int code, boolean isShow) {
        requestData(urlConstant, params, code, NetRequest.METHOD.GET, isShow);
    }

    public void requestPostData(String urlConstant, Map<String, Object> params, int code) {
        requestData(urlConstant, params, code, NetRequest.METHOD.POST, true);
    }

    public void requestData(String urlConstant, Map<String, Object> params, int code, NetRequest.METHOD method, boolean isShow) {
        requestCode = 0;
        if (isShow) {
            requestCode = 2;
            if (getView() != null) {
                getView().showProDialog();
            }
        }
        NetRequest netRequest = new NetRequest(code, urlConstant, params, method);
        netRequest.setTag(this);
        requestHttp(netRequest, this);
    }

    public Object getRequestTag() {
        return this;
    }

    private void startUpdate(String url) {
//        UpdateService.setDownloadListener(null);
//        Intent intentService = new Intent(getContext(), UpdateService.class);
//        intentService.putExtra("filePath", url);
//        intentService.putExtra("savePath", getContext().getExternalCacheDir().getAbsolutePath());
//        getContext().startService(intentService);
    }

    @Override
    public void netResultSuccess(int what, JSONObject object, NetRequest request) {
        if (getView() != null) {
            getView().dismissProDialog();
        }
        int status = object.optInt("status");
        String data = object.optString("data");
        Tool.logd("onSucceed: " + object.toString());
        if (status == 1) {
            if (what == 100) {
//                if (!TextUtils.isEmpty(data)) {// 需要
//                    if (!Tool.getNetworkType(getContext())) {
//                        startUpdate(data);
//                    }
//                }
            }
        }
    }

    @Override
    public void netResultFailed(int what, int mHttpCode, String message, NetRequest request) {
        try {
            if (getView() != null) {
                getView().dismissProDialog();
            }
            Tool.logd("onFailed: " + message);
            if (what != 100) {

            }
        } catch (Exception e) {

        }
    }

    @Override
    public void netProgress(int what, long bytesWritten, long totalSize, NetRequest request) {

    }

}
