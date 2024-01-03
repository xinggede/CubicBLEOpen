package com.xing.sd.ui.mode;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.TextUtils;

import com.xing.sd.base.mode.BaseNetPresenter;
import com.xing.sd.bluetooth.BlueToothCallback;
import com.xing.sd.bluetooth.SearchCallback;
import com.xing.sd.bluetooth.States;
import com.xing.sd.util.Tool;

import java.util.ArrayList;


/**
 * @author 星哥的
 */
public class SearchPresenter extends BaseNetPresenter<SearchContract.View, SearchContract.Model> implements SearchContract.Presenter {

    private ArrayList<BluetoothDevice> devices;
    boolean scanning = false;
    private SearchModel searchModel;
    private String mac, pwd;

    public ArrayList<BluetoothDevice> getDevices() {
        return devices;
    }

    public SearchPresenter(Context context) {
        super(context);
        devices = new ArrayList<>();
    }

    @Override
    public void attachView(SearchContract.View view) {
        super.attachView(view);
        mModel = new SearchModel(getContext());
        searchModel = (SearchModel) mModel;
    }


    @Override
    public boolean isOpenBlue() {
        return searchModel.isOpenBlue();
    }

    @Override
    public void cancelRequest() {
        super.cancelRequest();
        if (!TextUtils.isEmpty(mac)) {
            searchModel.cancelConnect(mac);
        }
    }

    @Override
    public boolean searchDevices(int time) {
        /*if (scanning) {
            return false;
        }*/
        startBlueSearch(time);
        scanning = true;
        return true;
    }

    public boolean refreshDevices(int time) {
        devices.clear();
        getView().updateDevices();
        return searchDevices(time);
    }

    public void closeConnect() {
        if (!TextUtils.isEmpty(mac)) {
            searchModel.disConnect(mac);
        }
    }

    public void startBlueSearch(int time) {
        if (!isOpenBlue()) {
            return;
        }
        searchModel.setSearchCallback(new SearchCallback() {
            @Override
            public void onStartSearch() {

            }

            @Override
            public void onFindDev(BluetoothDevice device) {
                onBlueDevice(device);
            }

            @Override
            public void onStopSearch() {
                scanning = false;
                getView().onSearchStop();
            }
        });
        searchModel.searchDevices(time);
    }

    public void onItem(int position) {
        getView().setShowText("连接中...");
        getView().showProDialog();
        BluetoothDevice bluetoothDevice = devices.get(position);
        mac = bluetoothDevice.getAddress();
        searchModel.setBlueToothCallback(new BlueToothCallback() {
            @Override
            public void onBleConnectState(int state) {
                if (getView() != null) {
                    getView().onConnectState(state);
                    getView().dismissProDialog();
                }
            }

            @Override
            public void onReceiveData(String characteristicId, String cmd, String data) {

            }


            @Override
            public void onWriteData(int state, String characteristicId) {
                if(state == States.WRITE_ERROR) {

                }
            }

            @Override
            public void onReadData(int state, String characteristicId, byte[] value) {

            }
        });
        searchModel.connect(bluetoothDevice.getAddress());

    }

    @Override
    public void stopSearch() {
        searchModel.stopSearch();
    }

    private void onBlueDevice(BluetoothDevice bluetoothDevice) {
        Tool.loge("蓝牙： " + bluetoothDevice.toString());
        devices.add(bluetoothDevice);
        getView().updateDevices();
    }


}
