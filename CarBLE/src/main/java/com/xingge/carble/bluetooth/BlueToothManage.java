package com.xingge.carble.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.xingge.carble.util.Tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class BlueToothManage implements IBle {

    private static BlueToothManage instance;
    private Context context;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<BluetoothGatt> mBluetoothGatts = new ArrayList<>();
    private List<BluetoothDevice> findDevices = new ArrayList<>();
    private boolean isRegister = false;
    private SearchBlueTooth searchBlueTooth;
    private SearchCallback searchCallback;
    private BlueToothCallback blueToothCallback;
    private BluetoothGatt curBluetoothGatt;
    private HashMap<String, SendDataThread> threadHashMap = new HashMap<>();

    public synchronized static BlueToothManage getInstance(Context context) {
        if (instance == null) {
            instance = new BlueToothManage(context);
        }
        return instance;
    }

    private BlueToothManage(Context context) {
        this.context = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        registerDev();
    }

    private void registerDev() {
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        iFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        iFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        context.registerReceiver(mSearchReceiver, iFilter);
        isRegister = true;
    }

    public void setSearchCallback(SearchCallback searchCallback) {
        this.searchCallback = searchCallback;
    }

    public void setBlueToothCallback(BlueToothCallback blueToothCallback) {
        this.blueToothCallback = blueToothCallback;
    }

    @Override
    public boolean checkDev() {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            if (mBluetoothAdapter != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isOpen() {
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
    }

    @Override
    public boolean open(boolean b) {
        if (mBluetoothAdapter != null) {
            return b ? mBluetoothAdapter.enable() : mBluetoothAdapter.disable();
        }
        return false;
    }

    @Override
    public void searchDevices(int time) {
        if (searchBlueTooth == null) {
            searchBlueTooth = new SearchBlueTooth(handler, mBluetoothAdapter);
        }

        if (searchBlueTooth.startSearch(time)) {
            findDevices.clear();
        }
    }

    @Override
    public void stopSearch() {
        if (searchBlueTooth != null) {
            searchBlueTooth.stopSearch();
        }
    }

    @Override
    public boolean connectDevice(String deviceId) {
        if (mBluetoothAdapter == null || TextUtils.isEmpty(deviceId)) {
            Tool.logd("connectDev error: " + "mBluetoothAdapter= " + mBluetoothAdapter + " and  address= " + deviceId);
            if (blueToothCallback != null) {
                blueToothCallback.onBleConnectState(States.ERROR);
            }
            return false;
        }

        if (!mBluetoothGatts.isEmpty()) {
            for (BluetoothGatt mBluetoothGatt : mBluetoothGatts) {
                if (mBluetoothGatt.getDevice().getAddress().equals(deviceId)) {
                    Tool.logd("已经连接: " + deviceId);
                    if (blueToothCallback != null) {
                        blueToothCallback.onBleConnectState(States.CONNECTED);
                    }
                    return true;
                }
            }
        }

        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceId);
        Tool.logd("开始连接: " + deviceId);
        if (device == null) {
            Tool.logd("未找到指定设备: " + deviceId);
            if (blueToothCallback != null) {
                blueToothCallback.onBleConnectState(States.ERROR);
            }
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            curBluetoothGatt = device.connectGatt(context, false, mGattCallback, BluetoothDevice.TRANSPORT_LE);
        } else {
            curBluetoothGatt = device.connectGatt(context, false, mGattCallback);
        }
        Tool.logd("正在创建一个新的连接" + curBluetoothGatt);
        if (curBluetoothGatt == null) {
            if (blueToothCallback != null) {
                blueToothCallback.onBleConnectState(States.ERROR);
            }
            return false;
        }
        return true;
    }

    @Override
    public void cancelConnect(String deviceId) {
        if (curBluetoothGatt != null) {
            if (curBluetoothGatt.getDevice().getAddress().equals(deviceId)) {
                curBluetoothGatt.close();
                mBluetoothGatts.remove(curBluetoothGatt);
                curBluetoothGatt = null;
            }
        }
    }

    @Override
    public void closeConnected(String deviceId) {
        Tool.logd("close= " + deviceId);
        if (!mBluetoothGatts.isEmpty()) {
            BluetoothGatt bg = null;
            for (BluetoothGatt mBluetoothGatt : mBluetoothGatts) {
                if (mBluetoothGatt.getDevice().getAddress().equals(deviceId)) {
                    Tool.logd("remove gatt= " + deviceId);
                    mBluetoothGatt.disconnect();
                    mBluetoothGatt.close();
                    bg = mBluetoothGatt;
                    break;
                }
            }
            if (bg != null) {
                mBluetoothGatts.remove(bg);
            }

            SendDataThread sendDataThread = threadHashMap.get(deviceId);
            if (sendDataThread != null) {
                sendDataThread.closeSendThread();
                threadHashMap.remove(deviceId);
            }
        }
    }

    private BluetoothGatt getBluetoothGatt(String deviceId) {
        if (!mBluetoothGatts.isEmpty()) {
            for (BluetoothGatt mBluetoothGatt : mBluetoothGatts) {
                if (mBluetoothGatt.getDevice().getAddress().equals(deviceId)) {
                    return mBluetoothGatt;
                }
            }
        }
        return null;
    }

    @Override
    public synchronized boolean sendData(String serviceId, String characteristicId, String deviceId, byte[] value) {
        BluetoothGatt bluetoothGatt = getBluetoothGatt(deviceId);
        if (bluetoothGatt == null) {
            Tool.logd("发送失败");
            return false;
        }
        SendDataThread sendDataThread = threadHashMap.get(deviceId);
        if (sendDataThread == null || sendDataThread.isStop()) {
            sendDataThread = new SendDataThread(bluetoothGatt);
            threadHashMap.put(deviceId, sendDataThread);
        }
        return sendDataThread.sendData(new SendValue(serviceId, characteristicId, deviceId, value));
    }

    @Override
    public boolean readData(String deviceId, String serviceId, String characteristicId) {
        return false;
    }

    @Override
    public boolean setNotification(String deviceId, String serviceId, String characteristicId, boolean enable) {
        BluetoothGatt bluetoothGatt = getBluetoothGatt(deviceId);
        if (bluetoothGatt == null) {
            Tool.logd("发送失败");
            return false;
        }
        SendDataThread sendDataThread = threadHashMap.get(deviceId);
        if (sendDataThread == null || sendDataThread.isStop()) {
            sendDataThread = new SendDataThread(bluetoothGatt);
            threadHashMap.put(deviceId, sendDataThread);
        }
        return sendDataThread.sendData(new SendValue(1, serviceId, characteristicId, deviceId, enable));
    }

    int retryCount = 0;

    private void retryConnect(String address) {
        Tool.logd("retryConnect: " + retryCount + " - " + address);
        if (retryCount < 3) {
            if (connectDevice(address)) {
                retryCount++;
            }
        } else {
            retryCount = 0;
            blueToothCallback.onBleConnectState(States.ERROR);
        }
    }

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Tool.logd("onConnectionStateChange= " + status + " <> " + newState);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    try {
                        Thread.sleep(500);
                        gatt.discoverServices();
                        retryCount = 0;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    if (gatt != null) {
                        gatt.close();
                    }
                    mBluetoothGatts.remove(gatt);
                    handler.sendEmptyMessage(States.DISCONNECTED);
                }
            } else {
                mBluetoothGatts.remove(gatt);
                gatt.close();
                retryConnect(gatt.getDevice().getAddress());
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Tool.logd("onServicesDiscovered= " + status + ":" + gatt.getDevice().getAddress());
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mBluetoothGatts.add(gatt);
                handler.sendEmptyMessage(States.CONNECTED);
            } else {
                mBluetoothGatts.remove(gatt);
                gatt.close();
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            byte[] data = characteristic.getValue();
            ResultData blueData = new ResultData(ResultData.READ, characteristic);
            blueData.mac = gatt.getDevice().getAddress();
            blueData.value = data;
            Message.obtain(handler, States.RECEIVE_OK, blueData).sendToTarget();
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Tool.logd("read= " + status);
            byte[] data = characteristic.getValue();
            ResultData blueData = new ResultData(ResultData.READ, characteristic);
            blueData.value = data;
            blueData.mac = gatt.getDevice().getAddress();
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Message.obtain(handler, States.READ_OK, blueData).sendToTarget();
            } else {
                Message.obtain(handler, States.READ_ERROR, blueData).sendToTarget();
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            ResultData blueData = new ResultData(ResultData.WRITE, characteristic);
            blueData.mac = gatt.getDevice().getAddress();
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Message.obtain(handler, States.WRITE_OK, blueData).sendToTarget();
            } else {
                Message.obtain(handler, States.WRITE_ERROR, blueData).sendToTarget();
            }
        }
    };


    @Override
    public void releaseAll() {
        findDevices.clear();
        if (isRegister) {
            context.unregisterReceiver(mSearchReceiver);
            isRegister = false;
        }
        if (searchBlueTooth != null) {
            searchBlueTooth.stopSearch();
            searchBlueTooth = null;
        }
        if (!threadHashMap.isEmpty()) {
            for (String s : threadHashMap.keySet()) {
                SendDataThread sendDataThread = threadHashMap.get(s);
                if (sendDataThread != null) {
                    sendDataThread.closeSendThread();
                }
            }
            threadHashMap.clear();
        }
        if (!mBluetoothGatts.isEmpty()) {
            for (BluetoothGatt mBluetoothGatt : mBluetoothGatts) {
                mBluetoothGatt.disconnect();
                mBluetoothGatt.close();
            }
            mBluetoothGatts.clear();
        }
        context = null;
        mBluetoothAdapter = null;
        instance = null;
    }

    private boolean deviceExisted(BluetoothDevice device) {
        if (!device.getName().startsWith("OMFID")) {
            return true;
        }
        for (BluetoothDevice info : findDevices) {
            if (info.getAddress().equals(device.getAddress())) {
                return true;
            }
        }
        return false;
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == States.SEARCH_START) {
                if (searchCallback != null) {
                    searchCallback.onStartSearch();
                }
            } else if (msg.what == States.SEARCH_DEVICE) {
                BluetoothDevice device = (BluetoothDevice) msg.obj;
                if (searchCallback != null) {
                    if (!deviceExisted(device)) {
                        synchronized (findDevices) {
                            findDevices.add(device);
                            searchCallback.onFindDev(device);
                        }
                    }
                }
            } else if (msg.what == States.SEARCH_END) {
                if (searchCallback != null) {
                    searchCallback.onStopSearch();
                }
            } else if (msg.what == States.CONNECTED) {
                if (blueToothCallback != null) {
                    blueToothCallback.onBleConnectState(States.CONNECTED);
                }
            } else if (msg.what == States.DISCONNECTED) {
                if (blueToothCallback != null) {
                    blueToothCallback.onBleConnectState(States.DISCONNECTED);
                }
            } else if (msg.what == States.WRITE_OK) {
                ResultData blueData = (ResultData) msg.obj;
                if (blueToothCallback != null) {
                    blueToothCallback.onWriteData(States.WRITE_OK, blueData.bluetoothGattCharacteristic.getUuid().toString());
                }
            } else if (msg.what == States.WRITE_ERROR) {
                ResultData blueData = (ResultData) msg.obj;
                if (blueToothCallback != null) {
                    blueToothCallback.onWriteData(States.WRITE_ERROR, blueData.bluetoothGattCharacteristic.getUuid().toString());
                }
            } else if (msg.what == States.READ_OK) {
                ResultData blueData = (ResultData) msg.obj;
                if (blueToothCallback != null) {
                    blueToothCallback.onReadData(States.READ_OK, blueData.bluetoothGattCharacteristic.getUuid().toString(), blueData.value);
                }
            } else if (msg.what == States.READ_ERROR) {
                ResultData blueData = (ResultData) msg.obj;
                if (blueToothCallback != null) {
                    blueToothCallback.onReadData(States.READ_ERROR, blueData.bluetoothGattCharacteristic.getUuid().toString(), blueData.value);
                }
            } else if (msg.what == States.RECEIVE_OK) {
                ResultData blueData = (ResultData) msg.obj;
                if (blueToothCallback != null) {
                    blueToothCallback.onReceiveData(blueData.bluetoothGattCharacteristic.getUuid().toString(), blueData.value);
                }
            }

        }
    };

    private BroadcastReceiver mSearchReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                Bundle b = intent.getExtras();
                int state = b.getInt(BluetoothAdapter.EXTRA_STATE);
                switch (state) {
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        if (!threadHashMap.isEmpty()) {
                            for (String s : threadHashMap.keySet()) {
                                SendDataThread sendDataThread = threadHashMap.get(s);
                                if (sendDataThread != null) {
                                    sendDataThread.closeSendThread();
                                }
                            }
                            threadHashMap.clear();
                        }
                        if (!mBluetoothGatts.isEmpty()) {
                            for (BluetoothGatt mBluetoothGatt : mBluetoothGatts) {
                                mBluetoothGatt.close();
                            }
                            mBluetoothGatts.clear();
                        }
                        break;

                    case BluetoothAdapter.STATE_OFF:
                        handler.sendEmptyMessage(States.DISCONNECTED);
                        break;

                    //开启中
                    case BluetoothAdapter.STATE_TURNING_ON:

                        break;

                    //开启
                    case BluetoothAdapter.STATE_ON:
                        break;

                    default:
                        break;
                }
            } else if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {

            } else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!mBluetoothGatts.isEmpty()) {
                    BluetoothGatt bg = null;
                    for (BluetoothGatt mBluetoothGatt : mBluetoothGatts) {
                        if (mBluetoothGatt.getDevice().getAddress().equals(device.getAddress())) {
                            bg = mBluetoothGatt;
                            break;
                        }
                    }
                    if (bg != null) {
                        mBluetoothGatts.remove(bg);
                    }
                }
            }
        }
    };
}
