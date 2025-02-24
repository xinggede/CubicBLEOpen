package com.xing.sd.ui.mode;

import android.content.Context;
import android.net.Uri;

import com.xing.sd.bean.CurrentImageInfo;
import com.xing.sd.util.CommandUtil;


/**
 * @author 星哥的
 */
public class MainModel extends BleModel implements MainContract.Model {

    private String mac;

    public MainModel(Context context) {
        super(context);
        mac = getDeviceId();
    }

    @Override
    public boolean confirmConnect() {
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.commandToByte(CommandUtil.CONNECT));
    }

    @Override
    public boolean getAll() {
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.commandToByte(CommandUtil.GALL));
    }

    @Override
    public boolean setAutoRead(boolean b) {
        return setNotification(mac, CommandUtil.READ_SID, CommandUtil.READ_CID, b);
    }

    @Override
    public boolean isReadCid(String uuid) {
        return uuid.contains(CommandUtil.READ_CID);
    }

    @Override
    public boolean checkData(String data) {
        return true;
    }

    @Override
    public boolean receiveOK() {
//        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.commandToByte(CommandUtil.OK));
        return true;
    }

    @Override
    public boolean receiveError() {
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.commandToByte(CommandUtil.ERR));
    }

    @Override
    public void closeConnect() {
        disConnect(mac);
    }

    @Override
    public void getDTitle() {
        sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.getCommandByte(CommandUtil.DTITLE));
    }

    @Override
    public void getDGType() {
        sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.getCommandByte(CommandUtil.DGTYPE));
    }

    @Override
    public void getDGShow() {
        sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.getCommandByte(CommandUtil.DGSHOW));
    }

    @Override
    public boolean setLPNum(int x, int y, int maxX, int maxY) {
        String data = getLenData(x, 2) + "," + getLenData(y, 2) + "," + getLenData(maxX, 2) + "," + getLenData(maxY, 2);
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.setCommandByte(CommandUtil.LPNUM, data));
    }

    @Override
    public boolean setPType(int range, int scanMode, int oe, int data, int move) {
        String dd = range + "," + scanMode + "," + oe + "," + data+ "," + move;
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.setCommandByte(CommandUtil.PTYPE, dd));
    }

    @Override
    public boolean setFont(int type, int sys, int x, int y, int face) {
        String data = getLenData(type, 2) + "," + getLenData(sys, 2) + "," + getLenData(x, 2) + "," + getLenData(y, 2) + "," + face;
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.setCommandByte(CommandUtil.FONT, data));
    }

    @Override
    public boolean setLPBri(int flag1, int flag2) {
        String dd = flag1 + "," + flag2;
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.setCommandByte(CommandUtil.LPBRI, dd));
    }

    @Override
    public boolean setRCPwm(int flag, int r) {
        String data = getLenData(flag, 2) + "," + r;
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.setCommandByte(CommandUtil.RCPWM, data));
    }

    @Override
    public boolean setUart(String cmd, int mode, int baud, int dataBit, int stopBit, int checkBit, int protocol) {
        String data = mode + "," + getLenData(baud, 7) + "," + dataBit + "," + stopBit + "," + checkBit + "," + protocol;
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.setCommandByte(cmd, data));
    }

    @Override
    public boolean setModbus(int d1, int d2, int d3) {
        String data = getLenData(d1, 3) + "," + getLenData(d2, 2) + "," + getLenData(d3, 4);
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.setCommandByte(CommandUtil.MODBUS, data));
    }

    @Override
    public boolean setEnetIp(int mode, String ip, String gateway, String mask, String dns, int port) {
        String data = mode + "," + ip + "," + gateway + "," + mask /*+ "," + dns*/ + "," + getLenData(port, 4);
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.setCommandByte(CommandUtil.ENETIP, data));
    }

    @Override
    public boolean setDTitle(int d1, int d2, String title) {
        String data = getLenData(d1, 2) + "," + getLenData(d2, 2) + "," + title;
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.setCommandByte(CommandUtil.DTITLE, data));
    }

    @Override
    public boolean setDGType(int d1, int d2, int d3, int d4, int d5, int d6) {
        String data = getLenData(d1, 2) + "," + getLenData(d2, 2) + "," + d3 + "," + d4 + "," + d5 + "," + getLenData(d6, 2);
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.setCommandByte(CommandUtil.DGTYPE, data));
    }

    @Override
    public boolean setDGShow(int d1, int d2, int d3, int d4, int d5, int d6, int d7, String content) {
        String data = d1 + "," + getLenData(d2, 3) + "," + getLenData(d3, 3) + "," + d4 + ","
                + getLenData(d5, 2) + "," + getLenData(d6, 3) + "," + getLenData(d7, 2) + "," + content;
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.setCommandByte(CommandUtil.DGSHOW, data));
    }

    @Override
    public boolean setUPG(int type) {
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.setCommandByte(CommandUtil.UPG, String.valueOf(type)));
    }

    @Override
    public boolean setTime(String time) {
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.setCommandByte(CommandUtil.TIME, time));
    }

    @Override
    public boolean reset(int value) {
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.setCommandByte(CommandUtil.RESET, String.valueOf(value)));
    }

    @Override
    public boolean save() {
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.commandToByte(CommandUtil.SALL));
    }

    @Override
    public boolean getMCUInfo() {
        return getMCUInfo(mac);
    }

    @Override
    public boolean readUpdateData(boolean isReadOnly) {
        return startReadData(mac, CommandUtil.OTA_SID, CommandUtil.OTA_RID, isReadOnly);
    }

    @Override
    public void stopRead() {
        stopReadData();
    }

    @Override
    public boolean startUpdateMcu(Uri uri, CurrentImageInfo currentImageInfo) {
        return startUpdateMcu(mac, uri, currentImageInfo);
    }

    @Override
    public boolean startUpdateFont(Uri uri) {
        return startUpdateFont(mac, uri);
    }

    public void stopUpdate(int i) {
        if(i == 0){
            stopUpdateFont();
        } else {
            stopUpdateMcu();
        }
    }


    public String getLenData(int value, int len) {
        String str = String.valueOf(value);
        if (str.length() < len) {
            String d = "";
            for (int i = 0; i < len - str.length(); i++) {
                d += "0";
            }
            d += value;
            return d;
        }
        return str;
    }

}
