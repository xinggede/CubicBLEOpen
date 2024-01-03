package com.xing.sd.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Handler;
import android.os.Message;

import com.xing.sd.util.CommandUtil;
import com.xing.sd.util.Tool;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SendDataThread extends Thread {

    private final Object mPauseLock;
    private boolean isSendingData = false;
    private boolean stop = false;
    private ConcurrentLinkedQueue<SendValue> commands = new ConcurrentLinkedQueue<>();
    private BluetoothGatt bluetoothGatt;

    private BluetoothGattCharacteristic curBC;
    private Handler handler;

    public SendDataThread(BluetoothGatt bluetoothGatt, Handler handler) {
        this.bluetoothGatt = bluetoothGatt;
        this.handler = handler;
        mPauseLock = new Object();
        start();
    }

    public boolean sendData(SendValue data) {
        boolean b = commands.offer(data);
        if (!isSendingData) {
            onResume();
        }
        return b;
    }

    public void onResume() {
        synchronized (mPauseLock) {
            mPauseLock.notifyAll();
        }
    }

    private void pauseThread() {
        synchronized (mPauseLock) {
            try {
                mPauseLock.wait();
            } catch (InterruptedException e) {
            }
        }
    }

    public boolean isStop() {
        return stop;
    }

    public void closeSendThread() {
        commands.clear();
        onResume();
        stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            processCommand();
        }
    }

    private void processCommand() {
        SendValue blueData;
        while ((blueData = commands.poll()) != null) {
            boolean b = send(blueData);
            ResultData resultData = new ResultData(ResultData.WRITE, blueData.characteristicId);
            resultData.mac = blueData.deviceId;
            if (blueData.type == 0) {
                if (b) {
                    Message.obtain(handler, States.WRITE_OK, resultData).sendToTarget();
                } else {
                    Message.obtain(handler, States.WRITE_ERROR, resultData).sendToTarget();
                }
            }
        }
        isSendingData = false;
        if (!stop) {
            pauseThread();
        }
    }

    private boolean send(SendValue sendValue) {
        boolean b = true;
        if (sendValue.type == 0) {
            b = check(sendValue.serviceId, sendValue.characteristicId, sendValue.deviceId, sendValue.value);
        } else if (sendValue.type == 1) {
            b = setNotification(sendValue.serviceId, sendValue.characteristicId, sendValue.enable);
        }
        return b;
    }

    private List<BluetoothGattService> getServices(BluetoothGatt bluetoothGatt, String deviceId) {
        if (bluetoothGatt != null) {
            if (bluetoothGatt.getDevice().getAddress().equals(deviceId)) {
                return bluetoothGatt.getServices();
            }
        }
        return null;
    }

    public boolean check(String serviceId, String characteristicId, String deviceId, byte[] value) {
        Tool.logd("send: " + new String(value, CommandUtil.charset));
        if(curBC != null){
            String characterUUID = Long.toHexString(curBC.getUuid().getMostSignificantBits()).substring(0, 4);
            if (characterUUID.equals(characteristicId)) {
                List<byte[]> list = CommandUtil.fen(value, BlueToothManage.maxSize);
                boolean b = false;
                for (byte[] bytes : list) {
                    if(stop){
                        return false;
                    }
                    curBC.setValue(bytes);
                    b = writeValue(curBC);
                    Tool.logd("send " + bytes.length + "  " + b);
                }
                return b;
            }
        }
        List<BluetoothGattService> gattServices = getServices(bluetoothGatt, deviceId);
        for (BluetoothGattService gattService : gattServices) {
            String gattServiceUUID = Long.toHexString(gattService.getUuid().getMostSignificantBits()).substring(0, 4);
            if (gattServiceUUID.equals(serviceId)) {
                for (BluetoothGattCharacteristic bluetoothGattCharacteristic : gattService.getCharacteristics()) {
                    String characterUUID = Long.toHexString(bluetoothGattCharacteristic.getUuid().getMostSignificantBits()).substring(0, 4);
                    if (characterUUID.equals(characteristicId)) {
                        curBC = bluetoothGattCharacteristic;
                        bluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                        List<byte[]> list = CommandUtil.fen(value, BlueToothManage.maxSize);
                        boolean b = false;
                        for (byte[] bytes : list) {
                            if(stop){
                                return false;
                            }
                            bluetoothGattCharacteristic.setValue(bytes);
                            b = writeValue(bluetoothGattCharacteristic);
                            Tool.logd("send " + bytes.length + "  " + b);
                            if(!b){
                                return b;
                            }
                        }
                        return b;
                    }
                }
            }
        }
        return false;
    }

    private synchronized boolean writeValue(BluetoothGattCharacteristic characteristic) {
        if (bluetoothGatt == null) {
            return false;
        }
        int count = 0;
        while (!bluetoothGatt.writeCharacteristic(characteristic) && (count < 5)) {
            if(stop){
                return false;
            }
            count++;
            try {
                Thread.sleep(count * 200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Tool.logd("重新发送: " + count);
        }
        return count != 5;
    }

    public boolean setNotification(String serviceId, String characteristicId, boolean enable) {
        if (bluetoothGatt != null) {
            for (BluetoothGattService bluetoothGattService : bluetoothGatt.getServices()) {
                String gattServiceUUID = Long.toHexString(bluetoothGattService.getUuid().getMostSignificantBits()).substring(0, 4);
                if (gattServiceUUID.equals(serviceId)) {
                    for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService.getCharacteristics()) {
                        String characterUUID = Long.toHexString(bluetoothGattCharacteristic.getUuid().getMostSignificantBits()).substring(0, 4);
                        if (characterUUID.equals(characteristicId)) {
                            boolean b = setCharacteristicNotification(bluetoothGatt, bluetoothGattCharacteristic, enable);
                            Tool.logd("setNotification:" + b);
                            return b;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean setCharacteristicNotification(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, boolean enable) {
        BluetoothGattDescriptor localBluetoothGattDescriptor;
        UUID localUUID = UUID.fromString("00002902-0000-1000-8000-00805F9B34FB");
        localBluetoothGattDescriptor = characteristic.getDescriptor(localUUID);
        if (localBluetoothGattDescriptor != null) {
            if (enable) {
                byte[] arrayOfByte = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
                localBluetoothGattDescriptor.setValue(arrayOfByte);
            } else {
                byte[] arrayOfByte = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
                localBluetoothGattDescriptor.setValue(arrayOfByte);
            }
            gatt.writeDescriptor(localBluetoothGattDescriptor);
        }

        int count = 0;
        while (!gatt.setCharacteristicNotification(characteristic, enable) && (count < 5)) {
            count++;
            try {
                Thread.sleep(count * 200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Tool.logd("setNotification: " + count);
        }
        return count != 5;
    }
}
