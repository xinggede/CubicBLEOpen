package com.xingge.carble.base;

import android.app.Application;

public class CBApp extends Application {

    private static CBApp _instance;

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;
        CrashHandler.getInstance().init(this);
//        PgyCrashManager.register();
    }

    public static CBApp getInstance() {
        return _instance;
    }

}
