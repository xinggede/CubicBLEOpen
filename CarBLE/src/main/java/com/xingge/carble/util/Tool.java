package com.xingge.carble.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.xingge.carble.BuildConfig;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Tool {

    public static boolean debug = BuildConfig.DEBUG;
    public static String TAG = "test";

    public static void logd(String str) {
        if (debug) {
            Log.d(TAG, str);
        }
    }

    public static void logd(Class<?> clazz, String str) {
        if (debug) {
            Log.d(clazz.getName(), str);
        }
    }

    public static void loge(String str) {
        if (debug) {
            Log.e(TAG, str);
        }
    }

    private static Toast toast;

    /**
     * 显示与隐藏toast
     *
     * @param context
     * @param msg
     */
    public static void toastShow(Context context, String msg) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void toastLongShow(Context context, String msg) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void toastShow(Context context, int msg) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void startActivityClearTop(Activity nowActivity, Class<?> desClass) {
        Intent intent = new Intent(nowActivity, desClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        nowActivity.startActivity(intent);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int stringToInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return 0;
        }
    }

    public static String stringToDate(String str) {
        String d = str;
        if (str.length() == 6) {
            d = str.substring(0, 2) + "年" + str.substring(2, 4) + "月" + str.substring(4, 6) + "日";
        }
        return d;
    }

    public static String dateToString(String str) {
        String d = str;
        if (str.length() == 9) {
            d = str.substring(0, 2) + str.substring(3, 5) + str.substring(6, 8);
        }
        return d;
    }

    public static String dateToTime(String str) {
        String d = str;
        if (str.length() == 12) {
            d = str.substring(0, 2) + "-" + str.substring(2, 4) + "-" + str.substring(4, 6) + " "
                    + str.substring(6, 8) + ":" + str.substring(8, 10) + ":" + str.substring(10, 12);
        }
        return d;
    }

    public static double stringToDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0;
        }
    }

    public static String getOneDecimal(String str) {
        Double d = Double.parseDouble(str);
        d = d / 10;
        DecimalFormat df = new DecimalFormat("0.0");
        return df.format(d);
    }

    public static String getCurrentDate() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyMMddHHmmss", Locale.getDefault());
        return sDateFormat.format(new Date());
    }

    public static String getCurrentTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sDateFormat.format(new Date());
    }

    /**
     * @param dir 存储目录
     * @return
     */
    public static File getSaveFile(String dir) {
        String directoryPath = getSavePath();
        File file = new File(directoryPath + File.separator + dir);
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
                file.mkdirs();
            }
        } else {
            file.mkdirs();
        }
        return file;
    }

    private static String getSavePath() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(path + File.separator + "AppSaveData");
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
                file.mkdirs();
            }
        } else {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    public static float mToKM(float f) {
        BigDecimal bigDecimal = new BigDecimal(f);
        return bigDecimal.divide(new BigDecimal(1000), 2, BigDecimal.ROUND_HALF_UP).floatValue();
    }

}
