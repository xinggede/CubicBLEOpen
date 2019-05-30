package com.xingge.carble.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;

import java.util.Arrays;

public class ResultData {

    public static int WRITE = 0;
    public static int READ = 1;
    public String mac;

    public BluetoothGattCharacteristic bluetoothGattCharacteristic;
    public byte[] value;
    public int type;

    public ResultData(int type, BluetoothGattCharacteristic bluetoothGattCharacteristic, byte[] value) {
        this.bluetoothGattCharacteristic = bluetoothGattCharacteristic;
        this.value = value;
        this.type = type;
    }

    public ResultData(int type, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        this.bluetoothGattCharacteristic = bluetoothGattCharacteristic;
        this.type = type;
    }

    @Override
    public String toString() {
        return "ResultData{" +
                "bluetoothGattCharacteristic=" + bluetoothGattCharacteristic +
                ", value=" + Arrays.toString(value) +
                ", type=" + type +
                '}';
    }
}
