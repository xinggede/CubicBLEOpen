package com.xingge.carble.ui.mode;

import android.content.Context;

import com.xingge.carble.base.mode.BaseNetMode;
import com.xingge.carble.bluetooth.BlueToothCallback;
import com.xingge.carble.bluetooth.BlueToothManage;
import com.xingge.carble.bluetooth.SearchCallback;

/**
 * Created by 星哥的 on 2018/4/2.
 */

public class BleModel extends BaseNetMode implements IBlueTooth {

    private Context context;

    public BleModel(Context context) {
        this.context = context.getApplicationContext();
    }

    public BlueToothManage getBlueToothManage() {
        return BlueToothManage.getInstance(context);
    }

    @Override
    public boolean isOpenBlue() {
        return getBlueToothManage().isOpen();
    }

    @Override
    public void setSearchCallback(SearchCallback searchCallback) {
        getBlueToothManage().setSearchCallback(searchCallback);
    }

    @Override
    public void searchDevices(int time) {
        getBlueToothManage().searchDevices(time);
    }

    @Override
    public void stopSearch() {
        getBlueToothManage().stopSearch();
    }

    @Override
    public void setBlueToothCallback(BlueToothCallback blueToothCallback) {
        getBlueToothManage().setBlueToothCallback(blueToothCallback);
    }

    @Override
    public void connect(String deviceId) {
        getBlueToothManage().connectDevice(deviceId);
    }

    @Override
    public void cancelConnect(String deviceId) {
        getBlueToothManage().cancelConnect(deviceId);
    }

    @Override
    public void disConnect(String deviceId) {
        getBlueToothManage().closeConnected(deviceId);
    }

    @Override
    public boolean sendData(String deviceId, String sid, String cid, byte[] value) {
        return getBlueToothManage().sendData(sid, cid, deviceId, value);
    }

    @Override
    public boolean setNotification(String deviceId, String serviceId, String characteristicId, boolean enable) {
        return getBlueToothManage().setNotification(deviceId, serviceId, characteristicId, enable);
    }

    @Override
    public void releaseAll() {
        getBlueToothManage().releaseAll();
    }
}
