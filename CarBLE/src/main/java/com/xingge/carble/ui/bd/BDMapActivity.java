package com.xingge.carble.ui.bd;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.xingge.carble.R;
import com.xingge.carble.base.mode.IBaseActivity;
import com.xingge.carble.bean.GpsInfo;
import com.xingge.carble.bean.RecordInfo;
import com.xingge.carble.bluetooth.States;
import com.xingge.carble.dialog.ChooseAdapter;
import com.xingge.carble.dialog.ChoosePopup;
import com.xingge.carble.dialog.LineChartDialog;
import com.xingge.carble.ui.CusSeekBar;
import com.xingge.carble.ui.SearchActivity;
import com.xingge.carble.ui.mode.MainContract;
import com.xingge.carble.ui.mode.MainPresenter;
import com.xingge.carble.ui.mode.ProcessGPSThread;
import com.xingge.carble.util.CommandUtil;
import com.xingge.carble.util.Tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author 星哥的
 */
public class BDMapActivity extends IBaseActivity<MainPresenter> implements MainContract.View {

    MapView mMapView = null;
    BaiduMap aMap;
    LocationClient mLocationClient;
    private MapStatus mapStatus;
    private InfoWindow infoWindow;

    private TextView tvDay, tvTime1, tvTime2, tvTime3, tvTotalInfo;
    private ChoosePopup dayPopup;
    private ProcessGPSThread processGPSThread;
    private CusSeekBar seekBar;
    private int times[] = new int[]{10 * 60 * 1000, 30 * 60 * 1000, 60 * 60 * 1000, 2 * 60 * 60 * 1000, 6 * 60 * 60 * 1000, 12 * 60 * 60 * 1000};
    private String day;
    private int showType;
    private LineChartDialog lineChartDialog;
    private Button btGetGps;

    @Override
    protected MainPresenter onLoadPresenter() {
        return new MainPresenter(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_map_bd;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(getApplicationContext());
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);

        mMapView = findViewById(R.id.map);
        mMapView.setLogoPosition(LogoPosition.logoPostionleftTop);
        mMapView.showZoomControls(false);
        if (aMap == null) {
            aMap = mMapView.getMap();
            aMap.setMyLocationEnabled(true);

            MapStatus.Builder builder = new MapStatus.Builder();
            builder.zoom(18.0f);
            mapStatus = builder.build();
            aMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
        }

        showType = getIntent().getIntExtra("showType", 0);

        btGetGps = findViewById(R.id.bt_get_gps);

        tvDay = findViewById(R.id.tv_day);
        tvDay.setOnClickListener(this);

        tvTime1 = findViewById(R.id.tv_time1);
        tvTime2 = findViewById(R.id.tv_time2);
        tvTime3 = findViewById(R.id.tv_time3);
        tvTotalInfo = findViewById(R.id.tv_total_info);

        seekBar = findViewById(R.id.seekBar);
        seekBar.setEnabled(false);
        seekBar.setChangedListener(new CusSeekBar.onChangedListener() {
            @Override
            public void onProgressChanged(int progress) {
                GpsInfo gInfo = Objects.requireNonNull(listMap.get(day)).get(progress);
                if (gInfo != null) {
                    LatLng latLng = new LatLng(gInfo.latLng.latitude, gInfo.latLng.longitude);
                    aMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(latLng, aMap.getMapStatus().zoom));

                    String str = Tool.dateToTime(gInfo.date);
                    str += "\n经度：" + GpsInfo.formatLongitude(gInfo.longitude, showType) + "\n纬度：" + GpsInfo.formatLatitude(gInfo.latitude, showType);
                    str += "\n速度：" + gInfo.speed + "KM/H" + "\n海拔：" + gInfo.altitude + "米";
                    str += "\n距离：" + Tool.mToKM(gInfo.distance) + "KM";

//
//                    if (marker != null && marker.isInfoWindowShown()) {
//                        marker.setTitle(str);
//                        marker.setPosition(gInfo.latLng);
//                    } else {
//                        markerOptions.title(str);
//                        markerOptions.position(gInfo.latLng);
//                        marker = aMap.addMarker(markerOptions);
//                    }
//                    marker.showInfoWindow();
                }
            }
        });
    }

    @Override
    protected void initEventAndData() {
        getPresenter().getGTRki();
        startLocation();
    }


    private void startLocation() {
        //定位初始化
        mLocationClient = new LocationClient(this);

        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(2000);

        //设置locationClientOption
        mLocationClient.setLocOption(option);

        //注册LocationListener监听器
        MyLocationListener myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);
        //开启地图定位图层
        mLocationClient.start();
    }

    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            aMap.setMyLocationData(locData);
        }
    }


    @Override
    protected void onDestroy() {
        listMap.clear();
        if (processGPSThread != null) {
            processGPSThread.closeProcessThread();
        }
        handler.removeCallbacksAndMessages(null);
        if (lineChartDialog != null) {
            lineChartDialog.dismiss();
        }
        super.onDestroy();
        mLocationClient.stop();
        aMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    private Map<String, List<GpsInfo>> listMap = new HashMap<>();

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 97) { //开始
                btGetGps.setEnabled(false);
                seekBar.setEnabled(false);
                seekBar.setMax(msg.arg1);
                GpsInfo gpsInfo = (GpsInfo) msg.obj;
                showFirst(gpsInfo);
                tvTime1.setText(Tool.dateToHour(gpsInfo.date));
            } else if (msg.what == 98) { //更新每个包
                List<GpsInfo> infos = (List<GpsInfo>) msg.obj;

                List<GpsInfo> gpsInfoList = listMap.get(day);
                if (gpsInfoList == null) {
                    showMap(null, infos);

                    gpsInfoList = new ArrayList<>();
                    gpsInfoList.addAll(infos);
                    listMap.put(day, gpsInfoList);
                } else {
                    if (infos.get(0).isStart) {
                        showMap(null, infos);
                    } else {
                        showMap(gpsInfoList.get(gpsInfoList.size() - 1), infos);
                    }
                    gpsInfoList.addAll(infos);
                }
            } else if (msg.what == 99) { //分包
                GpsInfo gpsInfo = (GpsInfo) msg.obj;
                showFirst(gpsInfo);
                seekBar.setColor(gpsInfo.color);
            } else if (msg.what == 100) { //进度
                seekBar.setProgress(msg.arg2);
            } else if (msg.what == 101) { //结束
                btGetGps.setEnabled(true);
                List<GpsInfo> gpsInfoList = listMap.get(day);

                seekBar.setMax(gpsInfoList.size() - 1);
                seekBar.setProgress(seekBar.getMax());
                seekBar.setEnabled(true);

//                tvTime2.setText(Tool.dateToHour(gpsInfoList.get(gpsInfoList.size() / 2).date));
                tvTime3.setText(Tool.dateToHour(gpsInfoList.get(seekBar.getMax()).date));

                float totalDistance = 0;
                long totalTime = 0;

                for (int i = 0; i < gpsInfoList.size(); i++) {
                    if (gpsInfoList.get(i).isStart) {
                        GpsInfo gpsInfo = gpsInfoList.get(i - 1);
                        totalDistance += gpsInfo.distance;
                        totalTime += gpsInfo.time;
                    }
                }
                totalDistance += gpsInfoList.get(seekBar.getMax()).distance;
                totalTime += gpsInfoList.get(seekBar.getMax()).time;

                StringBuilder sb = new StringBuilder();
                sb.append("总路程：").append(Tool.mToKM(totalDistance)).append("KM  ")
                        .append("用时：").append(Tool.sToM(totalTime)).append("  平均时速：")
                        .append(Tool.calcAverageSpeed(totalDistance, totalTime));
                tvTotalInfo.setText(sb.toString());
            } else if (msg.what == 102) {
                seekBar.setMax(seekBar.getMax() - 1);
            } else if (msg.what == 103) {
                btGetGps.setEnabled(true);
                seekBar.setEnabled(true);
            }
            return false;
        }
    });

    private void showChartDialog(List<GpsInfo> infoList) {
        if (lineChartDialog == null) {
            lineChartDialog = new LineChartDialog(this);
        }
        lineChartDialog.showData(infoList);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_day) {
            if (dayPopup != null) {
                dayPopup.showAsDropDown(v);
            }
        } else if (v.getId() == R.id.bt_get_gps) {
            showGpsInfo();
        } else if (v.getId() == R.id.bt_change) {
            if (aMap.getMapType() == BaiduMap.MAP_TYPE_NORMAL) {
                aMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                ((Button) v).setText("普通地图");
            } else {
                aMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                ((Button) v).setText("卫星地图");
            }
        } else if (v.getId() == R.id.bt_chart) {
            List<GpsInfo> infoList = listMap.get(day);
            if (infoList != null && infoList.size() > 0) {
                showChartDialog(infoList);
            }
        } else if (v.getId() == R.id.ic_back) {
            finish();
        }
    }

    private void showGpsInfo() {
        for (Overlay polyline : polylines) {
            polyline.remove();
        }
//        if (marker != null) {
//            marker.hideInfoWindow();
//        }
        tvTime1.setText("");
        tvTime2.setText("");
        tvTime3.setText("");
        tvTotalInfo.setText("");
        btGetGps.setEnabled(false);
        List<GpsInfo> infoList = listMap.get(day);
        if (infoList != null && infoList.size() > 0) {
            int size = infoList.size();
            seekBar.clear();
            seekBar.setEnabled(true);
            seekBar.setProgress(0);
            seekBar.setMax(size - 1);
            tvTime1.setText(Tool.dateToHour(infoList.get(0).date));
//            tvTime2.setText(Tool.dateToHour(infoList.get(size / 2).date));
            tvTime3.setText(Tool.dateToHour(infoList.get(size - 1).date));

            float totalDistance = 0;
            long totalTime = 0;

            showFirst(infoList.get(0));
            List<GpsInfo> list = new ArrayList<>();
            int first = 0;
            for (int i = 0; i < size; i++) {
                GpsInfo gpsInfo = infoList.get(i);
                if (!gpsInfo.isStart) {
                    list.add(gpsInfo);
                } else {

                    GpsInfo g = infoList.get(i - 1);
                    seekBar.addProgress(first, i - 1, g.color);
                    first = i;
                    totalDistance += g.distance;
                    totalTime += g.time;

                    showMap(null, list);
                    list.clear();
                    list.add(gpsInfo);
                }

            }

            GpsInfo g = infoList.get(seekBar.getMax());
            totalDistance += g.distance;
            totalTime += g.time;
            StringBuilder sb = new StringBuilder();
            sb.append("总路程：").append(Tool.mToKM(totalDistance)).append("KM     ")
                    .append("用时：").append(Tool.sToM(totalTime)).append("      平均时速：")
                    .append(Tool.calcAverageSpeed(totalDistance, totalTime));
            tvTotalInfo.setText(sb.toString());

            seekBar.addProgress(first, size - 1, g.color);
            seekBar.setProgress(seekBar.getMax());
            showMap(null, list);

            btGetGps.setEnabled(true);
            return;
        }

        if (processGPSThread == null || processGPSThread.isStop()) {
            String t = getPresenter().getTLTime().split("-")[0];
            processGPSThread = new ProcessGPSThread(this, handler, times[Tool.stringToInt(t)]);
            processGPSThread.start();
        }

        if (getPresenter().getGTRkd(1, day, day)) {
            seekBar.clear();
            seekBar.setEnabled(false);
            seekBar.setProgress(0);
            handler.sendEmptyMessageDelayed(103, 15000);
        }
    }

    @Override
    public void onConnectState(int state) {
        if (state != States.CONNECTED) {
            Tool.toastShow(this, "蓝牙连接已断开");
            Tool.startActivityClearTop(this, SearchActivity.class);
            finish();
        }
    }

    @Override
    public void onReceiveData(String command, String data) {
        if (CommandUtil.GTRKI.startsWith(command)) {
            setGTRki(data);
        } else if (CommandUtil.GTRKD.startsWith(command)) {
            if (processGPSThread != null) {
                processGPSThread.addData(data);
            }
//            setGTRkd(data);
        }
    }

    List<Overlay> polylines = new ArrayList<>();

    private void showFirst(GpsInfo gpsInfo) {
        LatLng latLng = new LatLng(gpsInfo.latLng.latitude, gpsInfo.latLng.longitude);
        aMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(latLng, 15));
    }

    private synchronized void showMap(GpsInfo lastInfo, List<GpsInfo> infoList) {
        if (infoList.size() == 0) {
            return;
        }
        List<LatLng> latLngs = new ArrayList<>();
        if (lastInfo != null) {
            LatLng latLng = new LatLng(lastInfo.latLng.latitude, lastInfo.latLng.longitude);
            aMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(latLng, 15));

            latLngs.add(latLng);
        }
        for (GpsInfo gpsInfo : infoList) {
            LatLng latLng = new LatLng(gpsInfo.latLng.latitude, gpsInfo.latLng.longitude);
            latLngs.add(latLng);
        }

        OverlayOptions mOverlayOptions = new PolylineOptions()
                .width(20)
                .color(infoList.get(0).color)
                .points(latLngs);

        polylines.add(aMap.addOverlay(mOverlayOptions));
    }

    private void showMap(RecordInfo recordInfo) {
        if (recordInfo.latLngList.size() == 0) {
            return;
        }
        LatLng latLng = new LatLng(recordInfo.latLngList.get(0).latitude, recordInfo.latLngList.get(0).longitude);
        aMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(latLng, 15));


        OverlayOptions mOverlayOptions = new PolylineOptions()
                .width(20)
                .color(recordInfo.color)
                /*.points(recordInfo.latLngList)*/;

        polylines.add(aMap.addOverlay(mOverlayOptions));
    }


    private void setGTRkd(String data) {
       /* PackInfo packInfo = getPresenter().analysisPack(data);
        if (packInfo != null) {
            packs.add(packInfo);
            progressDialog.setMax(packInfo.pckTotal);
            progressDialog.setProgress(packInfo.currentPck);
            if (packInfo.pckTotal - 1 == packInfo.currentPck) {
                progressDialog.dismiss();
            }
        }
        showMap();*/
    }

    private void setGTRki(String data) {
        String[] vs = data.split(",");
        if (vs.length > 0) {
            int countDay = Tool.stringToInt(vs[0]);
            Tool.logd("总天数：" + countDay + "; 数据长度: " + (vs.length - 1));
            if (countDay == 0) {
                tvDay.setEnabled(false);
                btGetGps.setEnabled(false);
                return;
            }
            if (countDay == vs.length - 1) {
                String[] list = new String[countDay];

                for (int i = 0; i < countDay; i++) {
                    list[i] = Tool.stringToDate(vs[i + 1]);
                }

                btGetGps.setEnabled(true);
                tvDay.setText(list[0]);
                day = Tool.dateToString(tvDay.getText().toString());

                dayPopup = new ChoosePopup(this, list, new ChooseAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {
                        tvDay.setText(dayPopup.getValue(position));
                        day = Tool.dateToString(dayPopup.getValue(position));
                        dayPopup.dismiss();
                    }
                });
            }
        }
    }
}
