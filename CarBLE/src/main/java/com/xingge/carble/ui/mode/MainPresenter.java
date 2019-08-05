package com.xingge.carble.ui.mode;

import android.content.Context;

import com.xingge.carble.base.mode.BaseNetPresenter;
import com.xingge.carble.bean.GpsInfo;
import com.xingge.carble.bluetooth.BlueToothCallback;
import com.xingge.carble.bluetooth.States;
import com.xingge.carble.util.Tool;


/**
 * @author 星哥的
 */
public class MainPresenter extends BaseNetPresenter<MainContract.View, MainContract.Model> implements MainContract.Presenter, BlueToothCallback {

    private MainModel mainModel;

    public MainPresenter(Context context) {
        super(context);
    }

    @Override
    public void attachView(MainContract.View view) {
        super.attachView(view);
        mainModel = new MainModel(getContext());
        mModel = mainModel;
    }


    @Override
    public void onBleConnectState(int state) {
        if (getView() != null) {
            getView().onConnectState(state);
        }
    }

    public boolean confirmConnect() {
        return mModel.confirmConnect();
    }

    public boolean setRead(boolean b) {
        return mModel.setAutoRead(b);
    }

    public void closeConnect() {
        mModel.closeConnect();
    }

    public boolean getAll() {
        mainModel.setBlueToothCallback(this);
        return mModel.getAll();
    }

    public void setCallback() {
        mainModel.setBlueToothCallback(this);
    }

    public boolean getGMDF() {
        mainModel.setBlueToothCallback(this);
        mModel.getGMDF();
        getVer();
        return true;
    }

    public boolean getVer() {
        mModel.getVer();
        return true;
    }

    public boolean getSetAll() {
        mainModel.setBlueToothCallback(this);
        mModel.getACC();
        mModel.getGMT();
        mModel.getGMDF();
        mModel.getRFPvs();
        mModel.getRFReq();
        return true;
    }

    public void savePwd(String pwd) {
        mModel.savePwd(mainModel.getMac(), pwd);
    }

    public synchronized GpsInfo analysisGps(String data) {
        String[] vs = data.split(",");
        if (vs.length == 11) {
            GpsInfo gpsInfo = new GpsInfo();
            gpsInfo.mode = Tool.stringToInt(vs[0]);
            gpsInfo.showType = Tool.stringToInt(vs[1]);
            gpsInfo.state = Tool.stringToInt(vs[2]);
            gpsInfo.satellite = Tool.stringToInt(vs[3]);
            gpsInfo.delayState = Tool.stringToInt(vs[4]);
            String d = vs[5];
            if (d.length() == 12) {
                gpsInfo.date = d.substring(0, 6);
//                gpsInfo.time = d.substring(6, 8) + ":" + d.substring(8, 10) + ":" + d.substring(10, 12);
            }
            gpsInfo.longitude = vs[6];
            gpsInfo.latitude = vs[7];

            d = vs[8];
            if (d.length() == 5) {
                int t = Tool.stringToInt(d.substring(1, 5));
                d = d.substring(0, 1) + t;
            }
            gpsInfo.altitude = d;
            gpsInfo.speed = String.valueOf(Tool.stringToInt(vs[9]));
            gpsInfo.course = Tool.stringToInt(vs[10]);
            return gpsInfo;
        }
        return null;
    }

    public GpsInfo checkGps(String data) {
        Tool.logd("gps= " + data);
        String[] vs = data.split(",");
        if (vs.length == 8) {
            if (!vs[0].equals("20") || !vs[7].equals("85")) {
                return null;
            }
            GpsInfo gpsInfo = new GpsInfo();
            String d = vs[1];
            if (d.length() == 12) {
                gpsInfo.date = d.substring(0, 6);
//                gpsInfo.time = d.substring(6, 8) + ":" + d.substring(8, 10) + ":" + d.substring(10, 12);
            }
            gpsInfo.longitude = vs[2];
            gpsInfo.latitude = vs[3];
            gpsInfo.speed = String.valueOf(Tool.stringToInt(vs[4]));
            gpsInfo.course = Tool.stringToInt(vs[5]);
            d = vs[6];
            if (d.length() == 5) {
                int t = Tool.stringToInt(d.substring(1, 5));
                d = d.substring(0, 1) + t;
            }
            gpsInfo.altitude = d;
            return gpsInfo;
        }
        return null;
    }


    String cacheData = "";

    private synchronized void analysisData(String data) {
        cacheData += data;
        int index = cacheData.indexOf("\r\n");
        int lastIndex = cacheData.lastIndexOf("\r\n");
        if (index != -1) {
            String[] commands = cacheData.split("\r\n");
            if (commands.length == 1) {
                checkCommand(commands[0]);
                cacheData = "";
            } else {
                for (int i = 0; i < commands.length - 1; i++) {
                    checkCommand(commands[i]);
                }
                if (lastIndex != cacheData.length() - 2) {
                    cacheData = commands[commands.length - 1];
                } else {
                    checkCommand(commands[commands.length - 1]);
                    cacheData = "";
                }
            }
        }
    }

    private void checkCommand(String data) {
        if (data.equals("VGH:OK") || data.equals("VGH:ERR")) {
            return;
        }
        String command = data.replace("VGH:", "");
        int i = command.indexOf("-");
        if (i != -1) {
            String c = command.substring(0, i);
            String d = command.substring(i + 1, command.length());
            if (mModel.checkData(d)) {
                mModel.receiveOK();
                if (getView() != null) {
                    getView().onReceiveData(c, d);
                }
            } else {
                mModel.receiveError();
                Tool.loge("数据效验错误");
            }
        } else {
            Tool.loge("接收数据不正确:" + data);
        }
    }

    @Override
    public void onReceiveData(String characteristicId, byte[] data) {
        if (mModel.isReadCid(characteristicId)) {
            String rData = new String(data);
//            Tool.logd("Receive= " + rData);
//            LogUtils.getLogUtils().writeLog(rData);
            analysisData(rData);
        } else if (new SearchModel(getContext()).isPwdCid(characteristicId)) {
            if (getView() != null) {
                int state = -1;
                if (data.length > 0) {
                    state = data[0];
                }
                getView().onReceiveData("SET_PWD", String.valueOf(state));
            }
        }
    }

    @Override
    public void onWriteData(int state, String characteristicId) {
        if(state == States.WRITE_ERROR) {
            if(getView() != null){
                getView().onConnectState(States.ERROR);
                getView().dismissProDialog();
            }
        }
    }

    @Override
    public void onReadData(int state, String characteristicId, byte[] value) {

    }

    @Override
    public boolean setACC(int value) {
        return mModel.setACC(value);
    }

    @Override
    public boolean setGMT(String value) {
        return mModel.setGMT(value);
    }

    @Override
    public boolean setGMdf(int mode, int showType) {
        return mModel.setGMDF(mode, showType);
    }

    @Override
    public boolean setRFPvs(int flag, int power, int volume, int level, int mt, int sn, int location) {
        return mModel.setRFPvs(flag, power, volume, level, mt, sn, location);
    }

    @Override
    public boolean setRFReq(int channel, int id, int frequency) {
        return mModel.setRFReq(channel, id, frequency);
    }

    @Override
    public boolean setTime(String time) {
        return mModel.setTime(time);
    }

    @Override
    public boolean reset(int value) {
        return mModel.reset(value);
    }

    @Override
    public boolean save() {
        return mModel.save();
    }

    @Override
    public boolean updatePwd(String oldPwd, String newPwd) {
        return new SearchModel(getContext()).updatePwd(mainModel.getMac(), oldPwd, newPwd);
    }

    @Override
    public boolean getGTRki() {
        mainModel.setBlueToothCallback(this);
        return mModel.getGTRki();
    }

    @Override
    public boolean getGTRkd(int day, String sDay, String eDay) {
        mainModel.setBlueToothCallback(this);
        return mModel.getGTRkd(day, sDay, eDay);
    }

    @Override
    public boolean setGTime(int value) {
        return mModel.setGTime(value);
    }

    @Override
    public boolean setRFCtrl(int state, int channel, int id) {
        return mModel.setRFCtrl(state, channel, id);
    }

    @Override
    public void setTLTime(String time) {
        mModel.setTLTime(time);
    }

    @Override
    public boolean setRFRpt(int state, int value) {
        return mModel.setRFRpt(state, value);
    }

    @Override
    public String getTLTime() {
        return mModel.getTLTime();
    }
}
