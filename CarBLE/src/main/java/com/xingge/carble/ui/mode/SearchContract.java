package com.xingge.carble.ui.mode;


import com.xingge.carble.base.mode.BaseNetContract;


public class SearchContract {

    public interface View extends BaseNetContract.View {

        void updateDevices();

        void onSearchStop();

        void onConnectState(int state);

        void onCheckPwdState(int state);
    }

    interface Model extends BaseNetContract.Model {

        boolean submitPwd(String mac, String pwd);

        boolean updatePwd(String mac, String oldPwd, String newPwd);

        boolean readPwdResult(String mac);

        boolean isPwdCid(String uuid);

    }


    interface Presenter extends BaseNetContract.Presenter<View> {

        boolean isOpenBlue();

        boolean searchDevices(int time);

        void stopSearch();

    }


}
