package com.xingge.carble.base;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.xingge.carble.ui.CrashActivity;
import com.xingge.carble.util.Tool;

public class CBApp extends Application {

    private static CBApp _instance;

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;
//        CrashHandler.getInstance().init(this);
//        PgyCrashManager.register();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Looper.loop();
                    } catch (Throwable e) {
                        Tool.loge(e.getMessage());
                        Intent intent = new Intent(_instance, CrashActivity.class);
                        intent.putExtra("data", e);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        _instance.startActivity(intent);
                    }
                }
            }
        });
    }

    public static CBApp getInstance() {
        return _instance;
    }

}
