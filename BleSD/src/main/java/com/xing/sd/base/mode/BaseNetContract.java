package com.xing.sd.base.mode;


import com.xing.sd.base.IModel;
import com.xing.sd.base.IPresenter;
import com.xing.sd.base.IView;

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


    }

    public interface Presenter<T extends View> extends IPresenter<T> {

        void cancelRequest();

        void checkAppUpdate();

        void releaseAll();


    }
}
