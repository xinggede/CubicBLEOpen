package com.xingge.carble.ui.gd;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
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
public class GDMapActivity extends IBaseActivity<MainPresenter> implements MainContract.View {

    MapView mMapView = null;
    AMap aMap;
    MyLocationStyle myLocationStyle;
    LatLng latLng /*= new LatLng(22.539583333333333,113.95045333333334)*/;
    private Marker marker;
    private MarkerOptions markerOptions;
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
        return R.layout.activity_map_gd;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
//        MapsInitializer.updatePrivacyShow(this,true,true);
//        MapsInitializer.updatePrivacyAgree(this,true);
        mMapView = findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        if (aMap == null) {
            aMap = mMapView.getMap();
            markerOptions = new MarkerOptions();
            markerOptions.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            markerOptions.draggable(true);
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
                List<GpsInfo> gpsInfos = listMap.get(day);
                if(gpsInfos == null || gpsInfos.isEmpty()){
                    return;
                }
                if(progress > gpsInfos.size() - 1){
                    return;
                }
                GpsInfo gInfo = gpsInfos.get(progress);
                if (gInfo != null) {
                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gInfo.latLng, aMap.getCameraPosition().zoom));
                    String str = Tool.dateToTime(gInfo.date);
                    str += "\n经度：" + GpsInfo.formatLongitude(gInfo.longitude, showType) + "\n纬度：" + GpsInfo.formatLatitude(gInfo.latitude, showType);
                    str += "\n速度：" + gInfo.speed + "KM/H" + "\n海拔：" + gInfo.altitude + "米";
                    str += "\n距离：" + Tool.mToKM(gInfo.distance) + "KM";
//                    Tool.logd(str + "  -- " + progress);
                    if (marker != null && marker.isInfoWindowShown()) {
                        marker.setTitle(str);
                        marker.setPosition(gInfo.latLng);
                    } else {
                        markerOptions.title(str);
                        markerOptions.position(gInfo.latLng);
                        marker = aMap.addMarker(markerOptions);
                    }
                    marker.showInfoWindow();
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
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setZoomControlsEnabled(false);

        myLocationStyle = new MyLocationStyle();
        myLocationStyle.interval(2000);
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        myLocationStyle.showMyLocation(true);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationEnabled(true);
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
            if (msg.what == 103) {
                btGetGps.setEnabled(true);
                seekBar.setEnabled(true);
                handler.sendEmptyMessage(101);
            } else {
                handler.removeMessages(103);
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
                        gpsInfoList = new ArrayList<>(infos);
                        listMap.put(day, gpsInfoList);
                    } else {
                        if (!infos.isEmpty() && infos.get(0).isStart) {
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
                    //最后一个GpsInfo的长度就是总长度，如果有分包则累加
                    for (int i = 0; i < gpsInfoList.size(); i++) {
                        if (gpsInfoList.get(i).isStart) {
                            GpsInfo gpsInfo;
                            if (i > 0) {
                                gpsInfo = gpsInfoList.get(i - 1);
                            } else {
                                gpsInfo = gpsInfoList.get(i);
                            }
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
                }
                if (msg.what != 101) {
                    handler.sendEmptyMessageDelayed(103, 10000);
                }
            }
            return true;
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
            if (aMap.getMapType() == AMap.MAP_TYPE_NORMAL) {
                aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                ((Button) v).setText("普通地图");
            } else {
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);
                ((Button) v).setText("卫星地图");
            }
        } else if (v.getId() == R.id.bt_chart) {
            List<GpsInfo> infoList = listMap.get(day);
            if (infoList != null && !infoList.isEmpty()) {
                showChartDialog(infoList);
            }
        } else if (v.getId() == R.id.ic_back) {
            finish();
        }
    }

    private void showGpsInfo() {
        for (Polyline polyline : polylines) {
            polyline.remove();
        }
        if (marker != null) {
            marker.hideInfoWindow();
        }
        tvTime1.setText("");
        tvTime2.setText("");
        tvTime3.setText("");
        tvTotalInfo.setText("");
        btGetGps.setEnabled(false);
        List<GpsInfo> infoList = listMap.get(day);
        if (infoList != null && !infoList.isEmpty()) {
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
                    GpsInfo g;
                    if (i > 0) {
                        g = infoList.get(i - 1);
                        seekBar.addProgress(first, i - 1, g.color);
                    } else {
                        g = infoList.get(i);
                        seekBar.addProgress(first, i, g.color);
                    }
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
            handler.sendEmptyMessageDelayed(103, 10000);
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

    List<Polyline> polylines = new ArrayList<>();

    private void showFirst(GpsInfo gpsInfo) {
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gpsInfo.latLng, 15));
    }

    private synchronized void showMap(GpsInfo lastInfo, List<GpsInfo> infoList) {
        if (infoList.isEmpty()) {
            return;
        }
        List<LatLng> latLngs = new ArrayList<>();
        if (lastInfo != null) {
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastInfo.latLng, 15));
            latLngs.add(lastInfo.latLng);
        }
        for (GpsInfo gpsInfo : infoList) {
            latLngs.add(gpsInfo.latLng);
        }
        PolylineOptions options = new PolylineOptions();
        options.addAll(latLngs);

        options.width(20).geodesic(true).color(infoList.get(0).color)
                .setDottedLine(false);//是否画虚线
        polylines.add(aMap.addPolyline(options));
    }

    private void showMap(RecordInfo recordInfo) {
        if (recordInfo.latLngList.size() == 0) {
            return;
        }
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(recordInfo.latLngList.get(0), 15));
        PolylineOptions options = new PolylineOptions();
        options.addAll(recordInfo.latLngList);

        options.width(20).geodesic(true).color(recordInfo.color)
                .setDottedLine(false);//是否画虚线
        polylines.add(aMap.addPolyline(options));
    }

    private void moveMap(List<LatLng> points) {
        /*aMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        LatLngBounds bounds = new LatLngBounds(points.get(0), points.get(points.size() - 2));
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));

        SmoothMoveMarker smoothMarker = new SmoothMoveMarker(aMap);
        // 设置滑动的图标
        smoothMarker.setDescriptor(BitmapDescriptorFactory.defaultMarker());

        LatLng drivePoint = points.get(0);
        Pair<Integer, LatLng> pair = SpatialRelationUtil.calShortestDistancePoint(points, drivePoint);
        points.set(pair.first, drivePoint);
        List<LatLng> subList = points.subList(pair.first, points.size());

        // 设置滑动的轨迹左边点
        smoothMarker.setPoints(subList);
        // 设置滑动的总时间
        smoothMarker.setTotalDuration(200);
        // 开始滑动
        smoothMarker.startSmoothMove();*/

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
