package com.xing.sd.base;

import android.app.Application;

public class SDApp extends Application {

    private static SDApp _instance;

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;
        CrashHandler.getInstance().init(this);
//        PgyCrashManager.register();
    }

    public static SDApp getInstance() {
        return _instance;
    }

}
