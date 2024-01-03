package com.xing.sd.ui.mode;

import android.content.Context;
import android.net.Uri;

import com.xing.sd.base.mode.BaseNetMode;
import com.xing.sd.bean.CurrentImageInfo;
import com.xing.sd.bluetooth.BlueToothCallback;
import com.xing.sd.bluetooth.BlueToothManage;
import com.xing.sd.bluetooth.SearchCallback;
import com.xing.sd.bluetooth.UpdateCallback;

import java.io.File;


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

    public String getDeviceId(){
        return getBlueToothManage().getDeviceId();
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
    public void setUpdateCallback(UpdateCallback updateCallback) {
        getBlueToothManage().setUpdateCallback(updateCallback);
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
    public boolean startReadData(String deviceId, String serviceId, String characteristicId, boolean isReadOnly) {
        return getBlueToothManage().readData(deviceId, serviceId, characteristicId, isReadOnly);
    }

    @Override
    public void stopReadData() {
        getBlueToothManage().stopRead();
    }

    @Override
    public boolean getMCUInfo(String deviceId) {
        return getBlueToothManage().getImageInfo(deviceId);
    }

    @Override
    public boolean startUpdateMcu(String deviceId, Uri uri, CurrentImageInfo currentImageInfo) {
        return getBlueToothManage().startUpdateMCU(deviceId, uri, currentImageInfo);
    }

    @Override
    public boolean startUpdateFont(String deviceId, Uri uri) {
        return getBlueToothManage().startUpdateFont(deviceId, uri);
    }

    @Override
    public void stopUpdateFont() {
        getBlueToothManage().stopUpdateFont();
    }

    @Override
    public void stopUpdateMcu() {
        getBlueToothManage().stopUpdateMcu();
    }

    @Override
    public void releaseAll() {
        getBlueToothManage().releaseAll();
    }
}
