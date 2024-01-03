package com.xing.sd.bluetooth;

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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.xing.sd.bean.CurrentImageInfo;
import com.xing.sd.util.CommandUtil;
import com.xing.sd.util.FormatUtil;
import com.xing.sd.util.Tool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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
    private UpdateMcuThread updateMcuThread;
    private UpdateFontThread updateFontThread;

    private UpdateCallback updateCallback;
    public static int maxSize = 20;

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

    public void setUpdateCallback(UpdateCallback updateCallback) {
        this.updateCallback = updateCallback;
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
                curBluetoothGatt.disconnect();
                curBluetoothGatt.close();
                mBluetoothGatts.remove(curBluetoothGatt);
                curBluetoothGatt = null;
            }
        }
    }

    private boolean isConnect(String deviceId){
        if (!mBluetoothGatts.isEmpty()) {
            for (BluetoothGatt mBluetoothGatt : mBluetoothGatts) {
                if (mBluetoothGatt.getDevice().getAddress().equals(deviceId)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void closeConnected(String deviceId) {
        Tool.logd("close= " + deviceId);
        buffer.reset();
        stopRead();
        if(updateMcuThread != null){
            updateMcuThread.closeUpdateThread();
        }
        if(updateFontThread != null){
            updateFontThread.closeUpdateThread();
        }
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

    public String getDeviceId(){
        if(curBluetoothGatt != null){
            return curBluetoothGatt.getDevice().getAddress();
        }
        return "";
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
            sendDataThread = new SendDataThread(bluetoothGatt, handler);
            threadHashMap.put(deviceId, sendDataThread);
        }
        return sendDataThread.sendData(new SendValue(serviceId, characteristicId, deviceId, value));
    }

    ReadDataThread readDataThread;

    @Override
    public boolean readData(String deviceId, String serviceId, String characteristicId, boolean readOnly) {
        BluetoothGatt bluetoothGatt = getBluetoothGatt(deviceId);
        if (bluetoothGatt == null) {
            Tool.loge("readData 失败");
            return false;
        }
        if (readDataThread == null || readDataThread.stop) {
            readDataThread = new ReadDataThread(bluetoothGatt);
            readDataThread.setReadOnly(readOnly);
            readDataThread.setSleepTime(200);
            readDataThread.startRead(deviceId, serviceId, characteristicId);
        } else {
            readDataThread.setReadOnly(readOnly);
        }
        return true;
    }

    public void stopRead(){
        if(readDataThread != null){
            readDataThread.stopRead();
        }
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
            sendDataThread = new SendDataThread(bluetoothGatt, handler);
            threadHashMap.put(deviceId, sendDataThread);
        }
        return sendDataThread.sendData(new SendValue(1, serviceId, characteristicId, deviceId, enable));
    }

    public boolean startUpdateMCU(String deviceId, Uri uri, CurrentImageInfo currentImageInfo){
        BluetoothGatt bluetoothGatt = getBluetoothGatt(deviceId);
        if (bluetoothGatt == null) {
            Tool.loge("startUpdateMCU 失败");
            return false;
        }
        if(updateMcuThread == null || updateMcuThread.isStop()){
            updateMcuThread = new UpdateMcuThread(bluetoothGatt, handler);
            updateMcuThread.startUpdate(context, uri, currentImageInfo);
        }
        return true;
    }

    public boolean startUpdateFont(String deviceId, Uri uri){
        BluetoothGatt bluetoothGatt = getBluetoothGatt(deviceId);
        if (bluetoothGatt == null) {
            Tool.loge("startUpdateFont 失败");
            return false;
        }
        if(updateFontThread == null || updateFontThread.isStop()){
            updateFontThread = new UpdateFontThread(bluetoothGatt, handler);
            updateFontThread.startUpdate(context, uri);
        }
        return true;
    }

    public void stopUpdateFont(){
        if(updateFontThread != null){
            updateFontThread.closeUpdateThread();
        }
    }

    public void stopUpdateMcu(){
        if(updateMcuThread != null){
            updateMcuThread.closeUpdateThread();
        }
    }

    public boolean getImageInfo(String deviceId){
        BluetoothGatt bluetoothGatt = getBluetoothGatt(deviceId);
        if (bluetoothGatt == null) {
            Tool.loge("getImageInfo 失败");
            return false;
        }
        if(updateMcuThread == null || updateMcuThread.isStop()){
            updateMcuThread = new UpdateMcuThread(bluetoothGatt, handler);
            updateMcuThread.startGetInfo();
        }
        return true;
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
//            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    try {
                        Thread.sleep(500);
                        gatt.discoverServices();
                        retryCount = 0;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    if(isConnect(gatt.getDevice().getAddress())){
                        closeConnected(gatt.getDevice().getAddress());
                        gatt.close();
                        mBluetoothGatts.remove(gatt);
                        handler.sendEmptyMessage(States.DISCONNECTED);
                    } else {
                        if(status != 0){
                            retryConnect(gatt.getDevice().getAddress());
                        } else {
                            handler.sendEmptyMessage(States.DISCONNECTED);
                        }
                    }

                }
         /*   } else {
                if (status == 8) {
                    if (gatt != null) {
                        gatt.close();
                    }
                    mBluetoothGatts.remove(gatt);
                    handler.sendEmptyMessage(States.DISCONNECTED);
                    return;
                }
                mBluetoothGatts.remove(gatt);
                gatt.close();
                retryConnect(gatt.getDevice().getAddress());
            }*/
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Tool.logd("onServicesDiscovered= " + status + ":" + gatt.getDevice().getAddress());
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mBluetoothGatts.add(gatt);
                handler.sendEmptyMessage(States.CONNECTED);
//                gatt.requestMtu(500);
            } else {
                mBluetoothGatts.remove(gatt);
                gatt.close();
                handler.sendEmptyMessage(States.DISCONNECTED);
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            //调用requestMtu 成功的回调  mtu一次传输的数据大小
            Tool.loge("onMtuChanged= " + mtu + "  <>  " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                maxSize = mtu - 3;
            } else {
                maxSize = 20;
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            onCheckData(characteristic, gatt.getDevice().getAddress());
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Tool.logd("read= " + characteristic.getUuid());
            if(characteristic.getUuid().toString().contains(CommandUtil.OTA_RID)){
                if(characteristic.getValue()==null || characteristic.getValue().length==0){
                    Tool.loge("READ NULL");
                    updateMcuThread.setREAD_NULL(true);
                    updateMcuThread.setRwBusy(false);
                    return;
                }
                updateMcuThread.setREAD_NULL(false);
                updateMcuThread.setReadData(characteristic.getValue());
                updateMcuThread.setRwBusy(false);
            } else if(characteristic.getUuid().toString().contains(CommandUtil.FONT_CID)){
                if(characteristic.getValue()==null || characteristic.getValue().length==0){
                    Tool.loge("READ NULL");
                    updateFontThread.setREAD_NULL(true);
                    updateFontThread.setRwBusy(false);
                    return;
                }
                updateFontThread.setREAD_NULL(false);
                updateFontThread.setReadData(characteristic.getValue());
                updateFontThread.setRwBusy(false);
            } else {
                onCheckData(characteristic, gatt.getDevice().getAddress());
            }

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if(characteristic.getUuid().toString().contains(CommandUtil.OTA_RID)){
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    updateMcuThread.setRwBusy(false);
                }
                return;
            }

            if(characteristic.getUuid().toString().contains(CommandUtil.FONT_CID)){
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    updateFontThread.setRwBusy(false);
                }
                return;
            }

            ResultData blueData = new ResultData(ResultData.WRITE, characteristic.getUuid().toString());
            blueData.mac = gatt.getDevice().getAddress();
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Message.obtain(handler, States.WRITE_OK, blueData).sendToTarget();
            } else {
                Message.obtain(handler, States.WRITE_ERROR, blueData).sendToTarget();
            }
        }
    };

    private ByteArrayOutputStream buffer = new ByteArrayOutputStream(1024 * 100);

    private void onCheckData(BluetoothGattCharacteristic characteristic, String mac) {
        String uuid = characteristic.getUuid().toString();
        synchronized (buffer) {
            if(uuid.contains(CommandUtil.READ_CID)){
                byte[] v = characteristic.getValue();
                Tool.logd("read data = " + v.length);
                if (v.length == 0) {
                    return;
                }
                buffer.write(v, 0, v.length);
                byte[] data = buffer.toByteArray();
                byte[] d = analysisCmd(data, mac, uuid);
                buffer.reset();
                try {
                    buffer.write(d);
                } catch (IOException e) {
                    e.printStackTrace();
                    buffer.reset();
                }
            }
        }
    }

    private byte[] analysisCmd(byte[] data, String mac, String uuid) {
        if (data.length >= 5) {
            int len = -1;
            for (int i = 0; i < data.length; i++) {
                if (data[i] == CommandUtil.CHECK) {
                    len = i;
                    break;
                }
            }
            if (len == -1) {
                Tool.loge("数据不完整");
                return data;
            }
            byte[] b = new byte[len];
            System.arraycopy(data, 0, b, 0, b.length);
            String d = new String(data, CommandUtil.charset);
            checkCommand(d, mac, uuid);
            if (len == data.length - 1) {//ok
                return new byte[0];
            } else {
                Tool.logd("数据超出");
                byte[] dd = new byte[data.length - (len + 1)];
                System.arraycopy(data, len + 1, dd, 0, dd.length);
                return analysisCmd(dd, mac, uuid);
            }
        }
        return data;
    }

    private void checkCommand(String data, String mac, String uuid) {
        Tool.logd("checkCommand： " + data);
        data = data.replace("\r","");
        ResultData blueData = new ResultData(ResultData.READ, uuid);
        blueData.mac = mac;

        String command = data.replace("BLP:", "");
        int i = command.indexOf("-");
        if (i != -1) {
            String c = command.substring(0, i);
            String d = command.substring(i + 1, command.length());
            blueData.cmd = c;
            blueData.data = d;
            Message.obtain(handler, States.RECEIVE_OK, blueData).sendToTarget();
        } else {
            blueData.cmd = data;
            blueData.data = data;
            Message.obtain(handler, States.RECEIVE_OK, blueData).sendToTarget();
        }
    }


    @Override
    public void releaseAll() {
        stopRead();
        if(updateMcuThread != null){
            updateMcuThread.closeUpdateThread();
        }
        if(updateFontThread != null){
            updateFontThread.closeUpdateThread();
        }
        try {
            buffer.reset();
            buffer.close();
        } catch (IOException e) {

        }
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
                    blueToothCallback.onWriteData(States.WRITE_OK, blueData.bluetoothGattCharacteristic);
                }
            } else if (msg.what == States.WRITE_ERROR) {
                ResultData blueData = (ResultData) msg.obj;
                if (blueToothCallback != null) {
                    blueToothCallback.onWriteData(States.WRITE_ERROR, blueData.bluetoothGattCharacteristic);
                }

            } else if (msg.what == States.READ_OK) {
                ResultData blueData = (ResultData) msg.obj;
                if (blueToothCallback != null) {
                    blueToothCallback.onReadData(States.READ_OK, blueData.bluetoothGattCharacteristic, blueData.value);
                }
            } else if (msg.what == States.READ_ERROR) {
                ResultData blueData = (ResultData) msg.obj;
                if (blueToothCallback != null) {
                    blueToothCallback.onReadData(States.READ_ERROR, blueData.bluetoothGattCharacteristic, blueData.value);
                }
                closeConnected(blueData.mac);
            } else if (msg.what == States.RECEIVE_OK) {
                ResultData blueData = (ResultData) msg.obj;
                if (blueToothCallback != null) {
                    blueToothCallback.onReceiveData(blueData.bluetoothGattCharacteristic, blueData.cmd, blueData.data);
                }
            } else if (msg.what == States.UPDATE_OK) {
                ResultData blueData = (ResultData) msg.obj;
                if (updateCallback != null) {
                    updateCallback.onUpdate(true, blueData.type, blueData.data, blueData.progress, blueData.total);
                }
            } else if (msg.what == States.UPDATE_ERROR) {
                ResultData blueData = (ResultData) msg.obj;
                if (updateCallback != null) {
                    updateCallback.onUpdate(false,  blueData.type, blueData.data, 0, 0);
                }
            } else if (msg.what == States.UPDATE_IMAGE) {
                CurrentImageInfo currentImageInfo = (CurrentImageInfo) msg.obj;
                if (updateCallback != null) {
                    updateCallback.onCurrentImageInfo(currentImageInfo);
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
