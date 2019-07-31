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

    public static String dateToHour(String str) {
        String d = str;
        if (str.length() == 12) {
            d = str.substring(6, 8) + ":" + str.substring(8, 10);
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

    public static String calcMtoS(String str) {
        Double d = stringToDouble(str) * 0.006;
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(d);
    }

    public static String calcMtoD(double d) {
        DecimalFormat df = new DecimalFormat("0.000000");
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

    public static String sToM(long time) {
        long second = time / 1000;
        if (second < 60) {
            return second + "秒";
        }

        int min = (int) (second / 60);
        second = second % 60;
        if (min < 60) {
            return min + "分" + second + "秒";
        }

        int hour = min / 60;
        min = min % 60;

        return hour + "时" + min + "分" + second + "秒";
    }

    public static float sToh(long time) {
        long second = time / 1000;
        float hour = second * 1.0f / (60 * 60);
        return hour;
    }

    public static String calcAverageSpeed(float distance, long time) {
        float h = sToh(time);

        float average = mToKM(distance) / h;
        DecimalFormat df = new DecimalFormat("0.00");

        return df.format(average) + "KM/H";
    }

    private static double rad(double d) {
        return d * Math.PI / 180d;
    }

    public static double getDirection(double lng1, double lat1, double lng2, double lat2) {
        //地球半径，单位米
//        double radLng1 = rad(lng1);
//        double radLat1 = rad(lat1);
//        double radLng2 = rad(lng2);
//        double radLat2 = rad(lat2);

        double a = rad(90 - lat2);
        double b = rad(90 - lat1);
        double c = rad(lng2 - lng1);
        double cos_c = (Math.cos(b) * Math.cos(a)) + (Math.sin(b) * Math.sin(a) * Math.cos(c));
        double sin_c = Math.sqrt(1 - Math.pow(cos_c, 2));
        double sin_a = Math.sin(Math.sin(b) * Math.sin(c) / sin_c);
        double Azimuth = Math.asin(sin_a) * 180 / Math.PI;

        double x = lng2 - lng1;//经度
        double y = lat2 - lat1;//纬度
        if (x > 0 && y > 0)//第一象限
        {
            return Azimuth;
        } else if (y > 0 && x < 0)//第二象限
        {
            return 360 + Azimuth;
        } else//第三第四象限
        {
            return 180 - Azimuth;
        }
    }

}
