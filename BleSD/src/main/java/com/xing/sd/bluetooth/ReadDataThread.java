package com.xing.sd.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import com.xing.sd.util.Tool;
import java.util.List;

public class ReadDataThread extends Thread {

    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCharacteristic gattCharacteristic;
    public boolean stop = false;
    private long sleepTime = 500;
    private boolean isReadOnly= false;

    public ReadDataThread(BluetoothGatt bluetoothGatt) {
        this.bluetoothGatt = bluetoothGatt;
    }

    public void setReadOnly(boolean readOnly) {
        isReadOnly = readOnly;
    }

    private List<BluetoothGattService> getServices(BluetoothGatt bluetoothGatt, String deviceId) {
        if (bluetoothGatt != null) {
            if (bluetoothGatt.getDevice().getAddress().equals(deviceId)) {
                return bluetoothGatt.getServices();
            }
        }
        return null;
    }

    public boolean startRead(String deviceId, String sid, String cid) {
        List<BluetoothGattService> gattServices = getServices(bluetoothGatt, deviceId);
        for (BluetoothGattService gattService : gattServices) {
            String gattServiceUUID = gattService.getUuid().toString();
            if (gattServiceUUID.equals(sid)) {
                for (BluetoothGattCharacteristic bluetoothGattCharacteristic : gattService.getCharacteristics()) {
                    String characterUUID = bluetoothGattCharacteristic.getUuid().toString();
                    if (characterUUID.equals(cid)) {
                        if((bluetoothGattCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ)==0){
                            gattCharacteristic = bluetoothGattCharacteristic;
                            break;
                        }
                    }
                }
            }
        }
        Tool.logd("startRead= " + gattCharacteristic);
        if (bluetoothGatt != null && gattCharacteristic != null) {
            stop = false;
            start();
        } else {
            stop = true;
        }
        return !stop;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    public void stopRead() {
        stop = true;
        interrupt();
        gattCharacteristic = null;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (gattCharacteristic == null) {
                break;
            }
            boolean b = bluetoothGatt.readCharacteristic(gattCharacteristic);
            Tool.logd("read= " + b);
            if(isReadOnly){
                break;
            }
        }
    }
}
