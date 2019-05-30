package com.xingge.carble.base.mode;

import com.xingge.carble.base.IModel;
import com.xingge.carble.base.IPresenter;
import com.xingge.carble.base.IView;
import com.xingge.carble.net.NetRequest;
import com.xingge.carble.net.NetResultListener;

/**
 * Created by 星哥的 on 2018/3/30.
 */

public class BaseNetContract {

    public interface View extends IView {

        void showProDialog();

        void dismissProDialog();

        void setShowText(String text);

        void setCancelable(boolean b);

    }

    public interface Model extends IModel {

        void requestHttp(NetRequest netRequest, NetResultListener netResultListener);

        void cancelRequest(Object o);

        void saveMac(String mac);

        String getMac();

        void savePwd(String mac, String pwd);

        String getPwd(String mac);
    }

    public interface Presenter<T extends View> extends IPresenter<T> {

        void requestHttp(NetRequest netRequest, NetResultListener netResultListener);

        void cancelRequest();

        void checkAppUpdate();

        void releaseAll();
    }
}
