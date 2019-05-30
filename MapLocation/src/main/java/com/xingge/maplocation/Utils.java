package com.xingge.maplocation;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;

public class Utils {

    public final static String GAODE_PKG = "com.autonavi.minimap"; //高德地图包名
    public final static String BAIDU_PKG = "com.baidu.BaiduMap";//百度地图的包名
    public final static String TX_PKG = "com.tencent.map";//腾讯地图包名


    /**
     * 检测地图应用是否安装
     *
     * @param context
     * @param packagename
     * @return
     */
    public static boolean checkMapAppsIsExist(Context context, String packagename) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (Exception e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 高德
     * 把LatLonPoint对象转化为LatLon对象
     */
    public static LatLng convertToLatLng(LatLonPoint latLonPoint) {
        return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
    }

    public static String getGaodeURL(String name, String poi, String latitude, String longitude) {
        StringBuffer sb = new StringBuffer();
        sb.append("androidamap://viewMap?sourceApplication=" + name + "&");
        sb.append("poiname=" + poi + "&");
        sb.append("lat=" + latitude + "&");
        sb.append("lon=" + longitude + "&");
        sb.append("dev=0");
        return sb.toString();
    }

}
