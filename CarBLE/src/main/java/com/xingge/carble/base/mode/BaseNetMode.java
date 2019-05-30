package com.xingge.carble.base.mode;


import com.xingge.carble.net.NetRequest;
import com.xingge.carble.net.NetResultListener;
import com.xingge.carble.net.NetTask;
import com.xingge.carble.util.Prefs;

public abstract class BaseNetMode implements BaseNetContract.Model {

    private final String MAC = "dev_mac";
    private final String PWD = "pwd_";

    protected Prefs prefs;

    public BaseNetMode() {
        prefs = Prefs.getInstance();
    }

    @Override
    public void cancelRequest(Object c) {
        NetTask.cancelLast(c);
    }

    public void cancelRequestAll(Object c) {
        NetTask.cancelAll(c);
    }

    @Override
    public void requestHttp(NetRequest netRequest, NetResultListener netResultListener) {
        NetTask netTask = new NetTask(netResultListener);
        netTask.execute(netRequest);
    }

    @Override
    public void saveMac(String mac) {
        prefs.saveString(MAC, mac);
    }

    @Override
    public String getMac() {
        return prefs.getString(MAC);
    }

    @Override
    public void savePwd(String mac, String pwd) {
        prefs.saveString(PWD + mac, pwd);
    }

    @Override
    public String getPwd(String mac) {
        return prefs.getString(PWD + mac, "000000");
    }
}
