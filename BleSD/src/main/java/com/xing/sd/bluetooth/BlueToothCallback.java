package com.xing.sd.bluetooth;

/**
 * Created by 星哥的 on 2018/3/29.
 */

public interface BlueToothCallback {

    void onBleConnectState(int state);

    void onReceiveData(String characteristicId, String cmd, String data);

    void onWriteData(int state, String characteristicId);

    void onReadData(int state, String characteristicId, byte[] value);
}
