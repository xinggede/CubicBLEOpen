package com.xingge.carble.ui.mode;


import com.xingge.carble.base.mode.BaseNetContract;


public class MainContract {

    public interface View extends BaseNetContract.View {

        void onConnectState(int state);

        void onReceiveData(String command, String data);
    }

    interface Model extends BaseNetContract.Model {

        boolean confirmConnect();

        boolean getAll();

        boolean setAutoRead(boolean b);

        boolean isReadCid(String uuid);

        boolean checkData(String data);

        boolean receiveOK();

        boolean receiveError();

        void closeConnect();

        boolean getACC();

        boolean setACC(int value);

        boolean getGMT();

        boolean setGMT(String value);

        boolean getGMDF();

        boolean getVer();

        boolean setGMDF(int mode, int showType);

        boolean getRFPvs();

        boolean setRFPvs(int flag, int power, int volume, int level, int mt, int sn);

        boolean getRFReq();

        boolean setRFReq(int channel, int id, int frequency);

        boolean setTime(String time);

        boolean reset(int value);

        boolean save();

        boolean getGTRki();

        boolean getGTRkd(int day, String sDay, String eDay);

        boolean setGTime(int value);

        boolean setRFCtrl(int state, int channel, int id);

        boolean setRFRpt(int state, int value);

        void setTLTime(String time);

        String getTLTime();

    }


    interface Presenter extends BaseNetContract.Presenter<View> {

        boolean setACC(int value);


        boolean setGMT(String value);


        boolean setGMdf(int mode, int showType);


        boolean setRFPvs(int flag, int power, int volume, int level, int mt, int sn);


        boolean setRFReq(int channel, int id, int frequency);

        boolean setTime(String time);

        boolean reset(int value);

        boolean save();

        boolean updatePwd(String oldPwd, String newPwd);

        boolean getGTRki();

        boolean getGTRkd(int day, String sDay, String eDay);

        boolean setGTime(int value);

        boolean setRFCtrl(int state, int channel, int id);

        void setTLTime(String time);

        boolean setRFRpt(int state, int value);

        String getTLTime();
    }


}
