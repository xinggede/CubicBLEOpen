package com.xing.sd.bluetooth;

/**
 * Created by 星哥的 on 2018/3/29.
 */

public interface IBle {

    boolean checkDev();

    boolean isOpen();

    boolean open(boolean b);

    void searchDevices(int time);

    void stopSearch();

    boolean connectDevice(String deviceId);

    void cancelConnect(String deviceId);

    void closeConnected(String deviceId);

    boolean sendData(String serviceId, String characteristicId, String deviceId, byte[] value);

    boolean readData(String deviceId, String serviceId, String characteristicId, boolean readOnly);

    boolean setNotification(String deviceId, String serviceId, String characteristicId, boolean enable);

    void releaseAll();

}
