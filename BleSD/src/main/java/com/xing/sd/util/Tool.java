package com.xing.sd.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import com.xing.sd.BuildConfig;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.core.content.ContextCompat;


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
        if(TextUtils.isEmpty(str)){
            return 0;
        }
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

    public static int getDegree(double lng1, double lat1, double lng2, double lat2) {
        double d = lng2 - lng1;
        if (d > 0) { //1\2
            double x = lng2 - lng1;
            double y = lat2 - lat1;
            double a = Math.atan(y / x);
            int degree = (int) (180 * a / Math.PI);
            return 90 - degree;
        } else if (d < 0) {  //3/4
            double x = lng1 - lng2;
            double y = lat1 - lat2;
            double a = Math.atan(y / x);
            int degree = (int) (180 * a / Math.PI);
            return 270 - degree;
        } else {
            if (lat2 - lat1 >= 0) {
                return 0;
            }
            return 180;
        }
    }

    public static String getDirection(int degree) {
        if (degree >= 345 || degree <= 15) {
            return "北";
        }

        if (degree < 75) {
            return "东北";
        }

        if (degree <= 105) {
            return "东";
        }

        if (degree < 165) {
            return "东南";
        }

        if (degree <= 195) {
            return "南";
        }

        if (degree < 255) {
            return "西南";
        }

        if (degree <= 285) {
            return "西";
        }

        /*if(degree < 345){
            return "西北";
        }*/
        return "西北";
    }

    private static double rad(double d) {
        return d * Math.PI / 180d;
    }

    public static double getDirection(double lng1, double lat1, double lng2, double lat2) {
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

    public static boolean isIP(String ips) {
        String str = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(ips);
        return m.matches();
    }

    public static String parserIp(String text){
        if(text.length() >= 12){
            return Tool.stringToInt(text.substring(0,3)) + "." + Tool.stringToInt(text.substring(3,6)) + "." + Tool.stringToInt(text.substring(6,9)) + "." + Tool.stringToInt(text.substring(9));
        }
        return text;
    }

    public static String concatIp(String text){
        String[] str = text.split("\\.");
        String value = "";
        for (String s : str) {
            value += getLenData(s, 3);
        }
        return value;
    }

    public static String getLenData(Object value, int len) {
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

    public static long getVerCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return packInfo.getLongVersionCode();
            } else {
                return packInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean hasPermissions(Context context, String... perms) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        for (String perm : perms) {
            boolean hasPerm = (ContextCompat.checkSelfPermission(context, perm) ==
                    PackageManager.PERMISSION_GRANTED);
            if (!hasPerm) {
                return false;
            }
        }
        return true;
    }

    public static String uriToFileName(Context context, Uri uri) {
        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            return new File(uri.getPath()).getName();
        } else {
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null);
                if (cursor != null && cursor.moveToFirst() && !cursor.isNull(0)) {
                    return cursor.getString(0);
                } else {
                    return MimeTypeMap.getSingleton().getExtensionFromMimeType(context.getContentResolver().getType(uri));
                }
            } catch (Exception e) {
                return MimeTypeMap.getSingleton().getExtensionFromMimeType(context.getContentResolver().getType(uri));
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

    public static String getUriFileType(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }
        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            String type = MimeTypeMap.getSingleton().getExtensionFromMimeType(context.getContentResolver().getType(uri));
            if (TextUtils.isEmpty(type)) {
                String path = uri.toString();
                int index = path.lastIndexOf(".");
                if (index == -1) {
                    return null;
                }
                type = path.substring(index + 1).toLowerCase();
            }
            return type;
        } else {
            String path = uri.getPath();
            int index = path.lastIndexOf(".");
            if (index == -1) {
                return null;
            }
            String end = path.substring(index + 1).toLowerCase();
            return end;
        }
    }

    public static InputStream uriToInputStream(Context context, Uri uri) {
        try {
            if(uri.toString().equals("/assets/GB2312_16_16.FON")) {
                return context.getAssets().open("GB2312_16_16.FON");
            } else {
                return context.getContentResolver().openInputStream(uri);
            }
        } catch (IOException e) {

        }
        return null;
    }

    public static File uriToFile(Context context, Uri uri) {
        Tool.logd("uriToFile: " + uri);
        File cacheDir = context.getExternalFilesDir("update");
        try {
            String fileName = uriToFileName(context, uri);
            File f = new File(cacheDir, fileName);
            if (f.exists() && f.length() > 0L) {
                return f;
            }
            FileOutputStream fos = new FileOutputStream(f);
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            byte[] buf = new byte[10240];
            int len;
            while ((len = inputStream.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.close();
            inputStream.close();
            return f;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
