package com.xing.sd.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * Created by 星哥的 on 2018/3/29.
 */

public interface SearchCallback {

    void onStartSearch();

    void onFindDev(BluetoothDevice device);

    void onStopSearch();
}
