package com.xing.sd.ui.mode;


import com.xing.sd.base.mode.BaseNetContract;

public class SearchContract {

    public interface View extends BaseNetContract.View {

        void updateDevices();

        void onSearchStop();

        void onConnectState(int state);

    }

    interface Model extends BaseNetContract.Model {


    }


    interface Presenter extends BaseNetContract.Presenter<View> {

        boolean isOpenBlue();

        boolean searchDevices(int time);

        void stopSearch();

    }


}
