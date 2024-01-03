package com.xing.sd.base.mode;


/**
 * Created by 星哥的 on 2018/3/30.
 */

public class BaseContract {

    public interface View extends BaseNetContract.View {

    }

    public interface Model extends BaseNetContract.Model {


    }

    public interface Presenter extends BaseNetContract.Presenter<View> {

    }
}
