package com.xingge.carble.bluetooth;

/**
 * Created by 星哥的 on 2018/3/29.
 */

public interface BlueToothCallback {

    void onBleConnectState(int state);

    void onReceiveData(String characteristicId, byte[] data);

    void onWriteData(int state, String characteristicId);

    void onReadData(int state, String characteristicId, byte[] value);
}
