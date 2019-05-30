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
    public String time;
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

    public static String format(String str) {
        StringBuffer sb = new StringBuffer();
        if (str.length() == 9) {
            sb.append(str.charAt(0));
            String s = str.substring(1, 3);
            sb.append(Tool.stringToInt(s) + "°");
            s = str.substring(3, 5);
            sb.append(s + ".");
            s = str.substring(5, 9);
            sb.append(s + "'");
        } else if (str.length() == 10) {
            sb.append(str.charAt(0));
            String s = str.substring(1, 4);
            sb.append(Tool.stringToInt(s) + "°");
            s = str.substring(4, 6);
            sb.append(s + ".");
            s = str.substring(6, 10);
            sb.append(s + "'");
        }
        return sb.toString();
    }

}
