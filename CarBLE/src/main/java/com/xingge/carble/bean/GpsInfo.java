package com.xingge.carble.bean;

import com.amap.api.maps.model.LatLng;
import com.xingge.carble.util.Tool;

public class GpsInfo {
    //定位模式
    public int mode;
    //显示方式
    public int showType;
    //定位状态
    public int state;
    public int delayState;
    //卫星个数
    public int satellite;
    //日期
    public String date;
    //时间
    public long time;
    //经度
    public String longitude;
    //纬度
    public String latitude;
    //海拔
    public String altitude;
    //速度
    public String speed;
    //航向
    public int course;

    public LatLng latLng;

    public int color;

    public float distance = 0;

    /**
     * 是否分包
     */
    public boolean isStart = false;

    public static String formatLongitude(String str, int type) {
        if (str.length() < 10) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        sb.append(str.charAt(0));
        if (type == 0) { //DMM
            String s = str.substring(1, 4);
            sb.append(Tool.stringToInt(s) + "°");
            s = str.substring(4, 6);
            sb.append(s + ".");
            s = str.substring(6, 10);
            sb.append(s + "'");
        } else if (type == 1) { //DMS
            String s = str.substring(1, 4);
            sb.append(Tool.stringToInt(s) + "°");
            s = str.substring(4, 6);
            sb.append(s + "'");
            s = str.substring(6, 10);
            sb.append(Tool.calcMtoS(s) + "''");
        } else { //DDD
            String a = str.substring(1, 4);
            String b = str.substring(4, 6);
            String c = str.substring(6, 10);
            double d = Tool.stringToInt(a) + Tool.stringToDouble(b) / 60 + Tool.stringToDouble(c) / 600000;
            sb.append(Tool.calcMtoD(d) + "°");
        }
        return sb.toString();
    }

    public static String formatLatitude(String str, int type) {
        if (str.length() < 9) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        sb.append(str.charAt(0));
        if (type == 0) { //DMM
            String s = str.substring(1, 3);
            sb.append(Tool.stringToInt(s) + "°");
            s = str.substring(3, 5);
            sb.append(s + ".");
            s = str.substring(5, 9);
            sb.append(s + "'");
        } else if (type == 1) { //DMS
            String s = str.substring(1, 3);
            sb.append(Tool.stringToInt(s) + "°");
            s = str.substring(3, 5);
            sb.append(s + "'");
            s = str.substring(5, 9);
            sb.append(Tool.calcMtoS(s) + "''");
        } else { //DDD
            String a = str.substring(1, 3);
            String b = str.substring(3, 5);
            String c = str.substring(5, 9);
            double d = Tool.stringToInt(a) + Tool.stringToDouble(b) / 60 + Tool.stringToDouble(c) / 600000;
            sb.append(Tool.calcMtoD(d) + "°");
        }
        return sb.toString();
    }

}
