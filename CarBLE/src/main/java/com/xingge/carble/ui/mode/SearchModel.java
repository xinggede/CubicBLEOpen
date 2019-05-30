package com.xingge.carble.ui.mode;


import android.content.Context;

import com.xingge.carble.util.CommandUtil;

/**
 * @author 星哥的
 */
public class SearchModel extends BleModel implements SearchContract.Model {

    public static final String PWD_SID = "ffc0";
    public static final String PWD_WCID = "ffc1";
    public static final String PWD_RCID = "ffc2";

    public SearchModel(Context context) {
        super(context);
    }

    @Override
    public boolean submitPwd(String mac, String pwd) {
        byte[] b = pwd.getBytes();
        return sendData(mac, PWD_SID, PWD_WCID, b);
    }

    @Override
    public boolean updatePwd(String mac, String oldPwd, String newPwd) {
        byte[] b = (oldPwd + newPwd).getBytes();
        return sendData(mac, PWD_SID, PWD_WCID, b);
    }

    @Override
    public boolean readPwdResult(String mac) {
        return setNotification(mac, PWD_SID, PWD_RCID, true);
    }

    @Override
    public boolean isPwdCid(String uuid) {
        return uuid.contains(PWD_RCID);
    }


}
