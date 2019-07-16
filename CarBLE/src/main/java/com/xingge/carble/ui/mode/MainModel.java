package com.xingge.carble.ui.mode;

import android.content.Context;

import com.xingge.carble.util.CommandUtil;

/**
 * @author 星哥的
 */
public class MainModel extends BleModel implements MainContract.Model {

    private final String TL_TIME = "tl_time";
    private String mac;

    public MainModel(Context context) {
        super(context);
        mac = getMac();
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
    public boolean getACC() {
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.getCommandByte(CommandUtil.ACCIN));
    }

    @Override
    public boolean setACC(int value) {
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.setCommandByte(CommandUtil.ACCIN, String.valueOf(value)));
    }

    @Override
    public boolean getGMT() {
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.getCommandByte(CommandUtil.GMT));
    }

    @Override
    public boolean setGMT(String value) {
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.setCommandByte(CommandUtil.GMT, value));
    }

    @Override
    public boolean getGMDF() {
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.getCommandByte(CommandUtil.GMDF));
    }

    @Override
    public boolean getVer() {
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.getCommandByte(CommandUtil.VER));
    }

    @Override
    public boolean setGMDF(int mode, int showType) {
        String data = mode + "," + showType;
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.setCommandByte(CommandUtil.GMDF, data));
    }

    @Override
    public boolean getRFPvs() {
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.getCommandByte(CommandUtil.RFPVS));
    }

    @Override
    public boolean setRFPvs(int flag, int power, int volume, int level, int mt, int sn, int location) {
        String data = flag + "," + power + "," + volume + "," + level + "," + mt + "," + sn + "," + location;
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.setCommandByte(CommandUtil.RFPVS, data));
    }

    @Override
    public boolean getRFReq() {
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.getCommandByte(CommandUtil.RFREQ));
    }

    @Override
    public boolean setRFReq(int channel, int id, int frequency) {
        String data = getLenData(channel, 2) + "," + getLenData(id, 3) + "," + getLenData(frequency, 7);
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.setCommandByte(CommandUtil.RFREQ, data));
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
    public boolean getGTRki() {
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.getCommandByte(CommandUtil.GTRKI));
    }

    @Override
    public boolean getGTRkd(int day, String sDay, String eDay) {
        String data = getLenData(day, 2) + "," + sDay + "000000" + "," + eDay + "235959";
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.setCommandByte(CommandUtil.GTRKD, data));
    }

    @Override
    public boolean setGTime(int value) {
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.setCommandByte(CommandUtil.GTIME, getLenData(value, 3)));
    }

    @Override
    public boolean setRFCtrl(int state, int channel, int id) {
        String data = state + "," + getLenData(channel, 2) + "," + getLenData(id, 3);
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.setCommandByte(CommandUtil.RFCTRL, data));
    }

    @Override
    public boolean setRFRpt(int state, int value) {
        String data = state + "," + getLenData(value, 2);
        return sendData(mac, CommandUtil.SEND_SID, CommandUtil.SEND_CID, CommandUtil.setCommandByte(CommandUtil.RFRPT, data));
    }

    @Override
    public void setTLTime(String time) {
        prefs.saveString(TL_TIME, time);
    }

    @Override
    public String getTLTime() {
        return prefs.getString(TL_TIME, "1-30分钟");
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
