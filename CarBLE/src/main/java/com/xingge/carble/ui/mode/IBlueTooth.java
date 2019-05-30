package com.xingge.carble.ui.mode;

import com.xingge.carble.bluetooth.BlueToothCallback;
import com.xingge.carble.bluetooth.SearchCallback;

/**
 * Created by 星哥的 on 2018/3/29.
 */

public interface IBlueTooth {

    boolean isOpenBlue();

    void setSearchCallback(SearchCallback searchCallback);

    void searchDevices(int time);

    void stopSearch();

    void setBlueToothCallback(BlueToothCallback blueToothCallback);

    void connect(String deviceId);

    void cancelConnect(String deviceId);

    void disConnect(String deviceId);

    boolean sendData(String deviceId, String sid, String cid, byte[] value);

    boolean setNotification(String deviceId, String serviceId, String characteristicId, boolean enable);

    void releaseAll();
}
