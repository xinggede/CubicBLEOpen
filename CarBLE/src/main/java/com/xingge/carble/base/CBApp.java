package com.xingge.carble.base;

import android.app.Application;

import com.pgyersdk.crash.PgyCrashManager;

public class CBApp extends Application {

    private static CBApp _instance;

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;
        PgyCrashManager.register();
    }

    public static CBApp getInstance() {
        return _instance;
    }

}
