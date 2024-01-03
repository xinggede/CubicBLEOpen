package com.xing.sd.ui.mode;


import android.net.Uri;

import com.xing.sd.base.mode.BaseNetContract;
import com.xing.sd.bean.CurrentImageInfo;

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

        void getDTitle();

        void getDGType();

        void getDGShow();

        boolean setLPNum(int x, int y, int maxX, int maxY);

        boolean setPType(int range, int scanMode, int oe, int data, int move);

        boolean setFont(int type, int sys, int x, int y);

        boolean setLPBri(int flag1, int flag2);

        boolean setRCPwm(int flag);

        boolean setUart(String cmd, int mode, int baud, int dataBit, int stopBit, int checkBit, int protocol);

        boolean setEnetIp(int mode, String ip, String mask, String gateway, String dns);

        boolean setDTitle(int d1, int d2, String title);

        boolean setDGType(int d1, int d2, int d3, int d4, int d5);

        boolean setDGShow(int d1, int d2, int d3, int d4, int d5, int d6, int d7, String content);

        boolean setUPG(int type);

        boolean setTime(String time);

        boolean reset(int value);

        boolean save();

        boolean getMCUInfo();

        boolean readUpdateData(boolean isReadOnly);

        void stopRead();

        boolean startUpdateMcu(Uri uri, CurrentImageInfo currentImageInfo);

        boolean startUpdateFont(Uri uri);

    }


    interface Presenter extends BaseNetContract.Presenter<View> {

        boolean confirmConnect();

        boolean getAll();

        void closeConnect();

        boolean setLPNum(int x, int y, int maxX, int maxY);

        boolean setPType(int range, int scanMode, int oe, int data, int move);

        boolean setFont(int type, int sys, int x, int y);

        boolean setLPBri(int flag1, int flag2);

        boolean setRCPwm(int flag);

        boolean setUart(String cmd, int mode, int baud, int dataBit, int stopBit, int checkBit, int protocol);

        boolean setEnetIp(int mode, String ip, String mask, String gateway, String dns);

        boolean setDTitle(int d1, int d2, String title);

        boolean setDGType(int d1, int d2, int d3, int d4, int d5);

        boolean setDGShow(int d1, int d2, int d3, int d4, int d5, int d6, int d7, String content);

        boolean setUPG(int type);

        boolean setTime(String time);

        boolean reset(int value);

        boolean save();
    }


}
