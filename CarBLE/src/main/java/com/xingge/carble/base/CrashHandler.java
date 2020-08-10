package com.xingge.carble.base;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.xingge.carble.bluetooth.BlueToothManage;
import com.xingge.carble.util.Tool;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by 星哥的 on 2018/9/10.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static CrashHandler instance = null;
    private Context context;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        if (null == instance) {
            instance = new CrashHandler();
        }
        return instance;
    }

    private Thread.UncaughtExceptionHandler mDefaultHandler;

    public void init(Context context) {
        this.context = context;
        // 系统默认的处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        // 获取错误信息 ，写入数据库
        StringBuffer crashInfo = getCrashInfo(e);
        Tool.loge(crashInfo.toString());
        BlueToothManage.getInstance(context).releaseAll();
        //处理完之后调用默认系统处理器（不调用会一直黑屏）
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        mDefaultHandler.uncaughtException(t, e);
        //打印错误信息
        e.printStackTrace();
    }

    private StringBuffer getCrashInfo(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        // 当前手机信息
        sb.append("DEVICE=" + Build.DEVICE + "\n");
        sb.append("VERSION=" + Build.VERSION.RELEASE + "\n");
        // 获取app的信息
        PackageManager pm = context.getPackageManager();
        PackageInfo pi;
        try {
            pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                // app的版本号和版本名
                sb.append("versionName=" + versionName + "\n");
                sb.append("versionCode=" + versionCode + "\n");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        // 崩溃错误日志
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result + "\n");
        return sb;
    }
}
