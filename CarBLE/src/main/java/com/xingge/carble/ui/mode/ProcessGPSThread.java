package com.xingge.carble.ui.mode;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.model.LatLng;
import com.xingge.carble.bean.GpsInfo;
import com.xingge.carble.bean.PackInfo;
import com.xingge.carble.util.Tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ProcessGPSThread extends Thread {

    private int[] colors = new int[]{Color.RED, Color.GREEN, Color.BLUE};

    private final Object mPauseLock;
    private boolean processing = false;
    private boolean stop = false;
    private ConcurrentLinkedQueue<String> commands = new ConcurrentLinkedQueue<>();
    private Handler handler;
    private CoordinateConverter converter;
    private int colorType = 0;
    private int stopTime;

    public ProcessGPSThread(Context context, Handler handler, int time) {
        this.handler = handler;
        converter = new CoordinateConverter(context);
        converter.from(CoordinateConverter.CoordType.GPS);
        mPauseLock = new Object();
        this.stopTime = time;
    }

    public void addData(String data) {
        commands.offer(data);
        if (!processing) {
            onResume();
        }
    }

    public void onResume() {
        synchronized (mPauseLock) {
            mPauseLock.notifyAll();
        }
    }

    private void pauseThread() {
        synchronized (mPauseLock) {
            try {
                mPauseLock.wait();
            } catch (InterruptedException e) {
            }
        }
    }

    public boolean isStop() {
        return stop;
    }

    public void closeProcessThread() {
        commands.clear();
        stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            processCommand();
        }
    }

    private double getLatitude(String latitude) {
        latitude = latitude.substring(1);
        String a = latitude.substring(0, 2);
        String b = latitude.substring(2, 4);
        String c = latitude.substring(4, latitude.length());

        double d = Tool.stringToInt(a) + Tool.stringToDouble(b) / 60 + Tool.stringToDouble(c) / 600000;
        return d;
    }

    private double getLongitude(String longitude) {
        longitude = longitude.substring(1);
        String a = longitude.substring(0, 3);
        String b = longitude.substring(3, 5);
        String c = longitude.substring(5, longitude.length());

        double d = Tool.stringToInt(a) + Tool.stringToDouble(b) / 60 + Tool.stringToDouble(c) / 600000;
        return d;
    }


    int count = 0;
    GpsInfo lastInfo = null;
    int color = colors[0];
    float distance = 0;
    long time = 0;

    private void processCommand() {
        String data;
        while ((data = commands.poll()) != null) {
            Tool.logd("data= " + data);
            PackInfo packInfo = analysisPack(data);
            if (packInfo != null) {
                List<GpsInfo> packs = new ArrayList<>();
                for (int i = 0; i < packInfo.gpsInfolist.size(); i++) {
                    GpsInfo gpsInfo = packInfo.gpsInfolist.get(i);
                    try {
                        LatLng latLng = new LatLng(getLatitude(gpsInfo.latitude), getLongitude(gpsInfo.longitude));
                        converter.coord(latLng);
                        gpsInfo.color = color;
                        gpsInfo.latLng = converter.convert();

                        if (lastInfo != null) {
                            if (isWithinHour(lastInfo.date, gpsInfo.date)) {
                                distance += AMapUtils.calculateLineDistance(lastInfo.latLng, gpsInfo.latLng);
                                if (distance - lastInfo.distance > 10000) {
                                    Message.obtain(handler, 102).sendToTarget();
                                    continue;
                                }
                                time += calcTime(lastInfo.date, gpsInfo.date);
                                gpsInfo.distance = distance;
                                gpsInfo.time = time;
                                packs.add(gpsInfo);
                            } else {
                                if (packs.size() > 0) {
                                    List<GpsInfo> ls = new ArrayList<>();
                                    ls.addAll(packs);
                                    Message.obtain(handler, 98, ls).sendToTarget();
                                }

                                Tool.logd("分包: " + packs.size());
                                packs.clear();
                                colorType++;
                                color = colors[colorType % 3];
                                gpsInfo.color = color;
                                gpsInfo.isStart = true;
                                distance = 0;
                                time = 0;
                                packs.add(gpsInfo);
                                Message.obtain(handler, 99, gpsInfo).sendToTarget();
                            }
                        } else {
                            //第一个点
                            packs.add(gpsInfo);
                            Message.obtain(handler, 97, packInfo.pckTotal - 1, 0, gpsInfo).sendToTarget();
                        }
                    } catch (Exception e) {
                        Tool.logd("解析异常");
                    }
                    lastInfo = gpsInfo;
                }

                Message.obtain(handler, 98, packs).sendToTarget();

                if (packInfo.currentPck - count >= 10) {
                    count = packInfo.currentPck;
                    Message.obtain(handler, 100, packInfo.pckTotal - 1, packInfo.currentPck).sendToTarget();
                }
                if (packInfo.pckTotal - 1 == packInfo.currentPck) {
                    Message.obtain(handler, 101).sendToTarget();
                    stop = true;
                }
            }
        }
        processing = false;
        if (!stop) {
            pauseThread();
        }
    }

    private GpsInfo checkGps(String data) {
        String[] vs = data.split(",");
        if (vs.length == 6) {
            if (!vs[5].equals("Z")) {
                return null;
            }
            GpsInfo gpsInfo = new GpsInfo();
            String d = vs[0];
            gpsInfo.date = d;
           /* if (d.length() == 12) {
                gpsInfo.date = d.substring(0, 6);
                gpsInfo.stopTime = d.substring(6, 8) + ":" + d.substring(8, 10) + ":" + d.substring(10, 12);
            }*/
            gpsInfo.longitude = vs[1];
            gpsInfo.latitude = vs[2];
            gpsInfo.speed = String.valueOf(Tool.stringToInt(vs[3]));
            d = vs[4];
            if (d.length() == 5) {
                int t = Tool.stringToInt(d.substring(1, 5));
                d = d.substring(0, 1) + t;
            }
            gpsInfo.altitude = d;
            return gpsInfo;
        }
        return null;
    }

    private long calcTime(String date1, String date2) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMDDHHmmss", Locale.getDefault());
        try {
            Date d1 = simpleDateFormat.parse(date1);
            Date d2 = simpleDateFormat.parse(date2);

            return d2.getTime() - d1.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 是否在一个小时内
     *
     * @param s1
     * @param s2
     * @return
     */
    private boolean isWithinHour(String s1, String s2) {
//        Tool.logd(s1 + " = " + s2);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss", Locale.getDefault());
        try {
            Date d1 = simpleDateFormat.parse(s1);
            Date d2 = simpleDateFormat.parse(s2);
            return d2.getTime() - d1.getTime() < stopTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private synchronized PackInfo analysisPack(String data) {
        if (data.length() > 16) {
            String first = data.substring(0, 15);
            String end = data.substring(16, data.length());
            String[] vs = first.split(",");
            if (vs.length == 4) {
                PackInfo packInfo = new PackInfo();
                packInfo.countDays = Tool.stringToInt(vs[0]);
                packInfo.currentDay = Tool.stringToInt(vs[1]);
                packInfo.pckTotal = Tool.stringToInt(vs[2]);
                packInfo.currentPck = Tool.stringToInt(vs[3]);

                String[] gs = end.split(";");
                List<GpsInfo> gpsInfos = new ArrayList<>();
                for (String g : gs) {
                    GpsInfo gpsInfo = checkGps(g);
                    if (gpsInfo != null) {
                        gpsInfos.add(gpsInfo);
                    }
                }
                packInfo.gpsInfolist = gpsInfos;
                return packInfo;
            }
        } else {
            Tool.logd("analysisPack is null");
        }
        return null;
    }


}
