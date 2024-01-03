package com.xing.sd.ui.mode;

import android.net.Uri;

import com.xing.sd.bean.CurrentImageInfo;
import com.xing.sd.bluetooth.BlueToothCallback;
import com.xing.sd.bluetooth.SearchCallback;
import com.xing.sd.bluetooth.UpdateCallback;

import java.io.File;

/**
 * Created by 星哥的 on 2018/3/29.
 */

public interface IBlueTooth {

    boolean isOpenBlue();

    void setSearchCallback(SearchCallback searchCallback);

    void searchDevices(int time);

    void stopSearch();

    void setBlueToothCallback(BlueToothCallback blueToothCallback);

    void setUpdateCallback(UpdateCallback updateCallback);

    void connect(String deviceId);

    void cancelConnect(String deviceId);

    void disConnect(String deviceId);

    boolean sendData(String deviceId, String sid, String cid, byte[] value);

    boolean setNotification(String deviceId, String serviceId, String characteristicId, boolean enable);

    boolean startReadData(String deviceId, String serviceId, String characteristicId, boolean isReadOnly);

    void stopReadData();

    boolean getMCUInfo(String deviceId);

    boolean startUpdateMcu(String deviceId, Uri uri, CurrentImageInfo currentImageInfo);

    boolean startUpdateFont(String deviceId, Uri uri);

    void stopUpdateFont();

    void stopUpdateMcu();

    void releaseAll();
}
