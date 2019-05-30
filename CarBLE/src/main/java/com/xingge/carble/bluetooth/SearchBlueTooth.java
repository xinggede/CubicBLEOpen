package com.xingge.carble.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.xingge.carble.util.Tool;

import java.util.List;


public class SearchBlueTooth {

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner scanner;
    private ScanCallback scanCallback;
    private BluetoothAdapter.LeScanCallback leScanCallback;
    private Handler handler;
    private boolean mScanning = false;

    public SearchBlueTooth(Handler handler, BluetoothAdapter bluetoothAdapter) {
        this.handler = handler;
        mBluetoothAdapter = bluetoothAdapter;
        createScanCallback();
    }

    private Runnable searchRunnable = new Runnable() {
        @Override
        public void run() {
            stop();
        }
    };

    public boolean startSearch(int time) {
        if (mBluetoothAdapter == null) {
            return false;
        }
        /*if (mScanning) {
            return false;
        }*/
        Tool.logd("scan: " + mScanning);
        mScanning = true;
        handler.removeCallbacks(searchRunnable);
        handler.postDelayed(searchRunnable, time);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scanner = mBluetoothAdapter.getBluetoothLeScanner();
            if (scanner != null) {
                ScanSettings scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
                scanner.startScan(null, scanSettings, scanCallback);
            }
        } else {
            mBluetoothAdapter.startLeScan(leScanCallback);
        }
        Message.obtain(handler, States.SEARCH_START).sendToTarget();
        return true;
    }

    public void stopSearch() {
        handler.removeCallbacks(searchRunnable);
        stop();
    }

    private void stop() {
        try {
            mScanning = false;
            Message.obtain(handler, States.SEARCH_END).sendToTarget();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (scanner != null) {
                    scanner.stopScan(scanCallback);
                }
            } else {
                mBluetoothAdapter.stopLeScan(leScanCallback);
            }
        } catch (Exception e) {
            Tool.logd("stop search Exception");
        }
    }

    private void createScanCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    BluetoothDevice device = result.getDevice();
                    if (TextUtils.isEmpty(device.getName())) {
                        return;
                    }
                    Message.obtain(handler, States.SEARCH_DEVICE, device).sendToTarget();
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    super.onBatchScanResults(results);
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                }
            };
        } else {
            if (leScanCallback != null) {
                return;
            }
            leScanCallback = new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                    if (TextUtils.isEmpty(device.getName())) {
                        return;
                    }
                    Message.obtain(handler, States.SEARCH_DEVICE, device).sendToTarget();
                }
            };
        }
    }
}
