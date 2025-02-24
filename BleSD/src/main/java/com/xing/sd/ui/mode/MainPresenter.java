package com.xing.sd.ui.mode;

import android.content.Context;
import android.net.Uri;

import com.xing.sd.base.mode.BaseNetPresenter;
import com.xing.sd.bean.CurrentImageInfo;
import com.xing.sd.bluetooth.BlueToothCallback;
import com.xing.sd.bluetooth.States;
import com.xing.sd.bluetooth.UpdateCallback;
import com.xing.sd.util.CommandUtil;
import com.xing.sd.util.Tool;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


/**
 * @author 星哥的
 */
public class MainPresenter extends BaseNetPresenter<MainContract.View, MainContract.Model> implements MainContract.Presenter, BlueToothCallback {

    private MainModel mainModel;

    public MainPresenter(Context context) {
        super(context);
    }

    @Override
    public void attachView(MainContract.View view) {
        super.attachView(view);
        mainModel = new MainModel(getContext());
        mModel = mainModel;
    }


    @Override
    public void onBleConnectState(int state) {
        if (getView() != null) {
            getView().onConnectState(state);
        }
    }

    public boolean confirmConnect() {
        return mModel.confirmConnect();
    }

    public boolean setRead(boolean b) {
        return mModel.setAutoRead(b);
    }

    public void getMainAll(){
        setCallback();
        mainModel.getDTitle();
        mainModel.getDGType();
        mainModel.getDGShow();
    }

    public void getMcuInfo(){
        mainModel.getMCUInfo();
        mainModel.readUpdateData(true);
    }

    public void closeConnect() {
        mModel.closeConnect();
    }

    @Override
    public boolean setLPNum(int x, int y, int maxX, int maxY) {
        return mainModel.setLPNum(x, y, maxX, maxY);
    }

    @Override
    public boolean setPType(int range, int scanMode, int oe, int data, int move) {
        return mainModel.setPType(range, scanMode, oe, data, move);
    }

    @Override
    public boolean setFont(int type, int sys, int x, int y, int face) {
        return mainModel.setFont(type, sys, x, y, face);
    }

    @Override
    public boolean setLPBri(int flag1, int flag2) {
        return mainModel.setLPBri(flag1, flag2);
    }

    @Override
    public boolean setRCPwm(int flag, int r) {
        return mainModel.setRCPwm(flag, r);
    }

    @Override
    public boolean setUart(String cmd, int mode, int baud, int dataBit, int stopBit, int checkBit, int protocol) {
        return mainModel.setUart(cmd, mode, baud, dataBit, stopBit, checkBit, protocol);
    }

    public boolean setModbus(int d1, int d2, int d3){
        return mainModel.setModbus(d1, d2, d3);
    }

    @Override
    public boolean setEnetIp(int mode, String ip, String gateway, String mask, String dns, int port) {
        getView().showProDialog();
        return mainModel.setEnetIp(mode, ip, gateway, mask, dns, port);
    }

    @Override
    public boolean setDTitle(int d1, int d2, String title) {
        getView().showProDialog();
        return mainModel.setDTitle(d1, d2, title);
    }

    @Override
    public boolean setDGType(int d1, int d2, int d3, int d4, int d5, int d6) {
        getView().showProDialog();
        return mainModel.setDGType(d1, d2, d3, d4, d5, d6);
    }

    @Override
    public boolean setDGShow(int d1, int d2, int d3, int d4, int d5, int d6, int d7, String content) {
        getView().showProDialog();
        return mainModel.setDGShow(d1, d2, d3, d4, d5, d6, d7, content);
    }

    @Override
    public boolean setUPG(int type) {
        return mainModel.setUPG(type);
    }

    public void setUpdateCallback(UpdateCallback updateCallback){
        mainModel.setUpdateCallback(updateCallback);
    }

    public boolean getCurrentImageInfo(){
        return mainModel.getMCUInfo();
    }

    public boolean updateFont(Uri uri){
        return mainModel.startUpdateFont(uri);
    }

    public boolean updateSys(Uri uri, CurrentImageInfo currentImageInfo){
        return mainModel.startUpdateMcu(uri, currentImageInfo);
    }

    public void stopUpdate(int i){
        mainModel.stopUpdate(i);
    }

    public boolean getAll() {
        setCallback();
        return mModel.getAll();
    }

    public void setCallback() {
        mainModel.setBlueToothCallback(this);
    }

    @Override
    public void onReceiveData(String characteristicId, String cmd, String data) {
        if (mModel.isReadCid(characteristicId)) {
            if (cmd.equals("OK") || cmd.equals("ERR")) {
                return;
            }
            if (mModel.checkData(data)) {
                mModel.receiveOK();
                if (getView() != null) {
                    getView().onReceiveData(cmd, data);
                }
            } else {
                mModel.receiveError();
                Tool.loge("数据效验错误");
            }
        } else if(characteristicId.contains(CommandUtil.OTA_RID)){
            mainModel.stopReadData();
        }
    }

    @Override
    public void onWriteData(int state, String characteristicId) {
        getView().dismissProDialog();
        if(state == States.WRITE_ERROR) {
            if(getView() != null){
                getView().onConnectState(States.ERROR);
            }
        }
    }

    @Override
    public void onReadData(int state, String characteristicId, byte[] value) {

    }


    @Override
    public boolean setTime(String time) {
        return mModel.setTime(time);
    }

    @Override
    public boolean reset(int value) {
        return mModel.reset(value);
    }

    @Override
    public boolean save() {
        return mModel.save();
    }

}
