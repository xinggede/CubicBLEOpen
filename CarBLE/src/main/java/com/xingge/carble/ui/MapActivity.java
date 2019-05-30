package com.xingge.carble.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.model.Polyline;
import com.amap.api.maps2d.model.PolylineOptions;
import com.xingge.carble.R;
import com.xingge.carble.base.mode.IBaseActivity;
import com.xingge.carble.bean.GpsInfo;
import com.xingge.carble.bean.RecordInfo;
import com.xingge.carble.bluetooth.States;
import com.xingge.carble.dialog.ChooseAdapter;
import com.xingge.carble.dialog.ChoosePopup;
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
public class MapActivity extends IBaseActivity<MainPresenter> implements MainContract.View {

    MapView mMapView = null;
    AMap aMap;
    MyLocationStyle myLocationStyle;
    LatLng latLng /*= new LatLng(22.539583333333333,113.95045333333334)*/;
    private Marker marker;
    private MarkerOptions markerOptions;
    private TextView tvDay;
    private ChoosePopup dayPopup;
    private ProcessGPSThread processGPSThread;
    private SeekBar seekBar;
    private int times[] = new int[]{10 * 60 * 1000, 30 * 60 * 1000, 60 * 60 * 1000, 2 * 60 * 60 * 1000, 6 * 60 * 60 * 1000, 12 * 60 * 60 * 1000};
    private String day;

    @Override
    protected MainPresenter onLoadPresenter() {
        return new MainPresenter(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_map;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mMapView = findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        if (aMap == null) {
            aMap = mMapView.getMap();
            markerOptions = new MarkerOptions();
            markerOptions.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            markerOptions.draggable(true);
        }

        tvDay = findViewById(R.id.tv_day);
        tvDay.setOnClickListener(this);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setEnabled(false);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                synchronized (listMap) {
                    if (fromUser && listMap.get(day) != null) {
                        GpsInfo gInfo = Objects.requireNonNull(listMap.get(day)).get(progress);
                        if (gInfo != null) {
                            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gInfo.latLng, aMap.getCameraPosition().zoom));
                            String str = Tool.dateToTime(gInfo.date);
                            str += "\n经度：" + gInfo.latLng.longitude + "\n纬度：" + gInfo.latLng.latitude;
                            str += "\n速度：" + gInfo.speed + "KM/H" + "\n海拔：" + gInfo.altitude + "米";
                            str += "\n距离：" + Tool.mToKM(gInfo.distance) + "KM";
                            Tool.logd(str + "  -- " + progress);
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
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);
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

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 97) { //开始
                seekBar.setEnabled(false);
                seekBar.setMax(msg.arg1);
                GpsInfo gpsInfo = (GpsInfo) msg.obj;
                showFirst(gpsInfo);
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
            } else if (msg.what == 100) { //进度
                seekBar.setProgress(msg.arg2);
            } else if (msg.what == 101) { //结束
                seekBar.setMax(listMap.get(day).size() - 1);
                seekBar.setProgress(seekBar.getMax());
                seekBar.setEnabled(true);
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_day) {
            if (dayPopup != null) {
                dayPopup.showAsDropDown(v);
            }
        } else if (v.getId() == R.id.bt_get_gps) {
            for (Polyline polyline : polylines) {
                polyline.remove();
            }
            if (marker != null) {
                marker.hideInfoWindow();
            }
            List<GpsInfo> infoList = listMap.get(day);
            if (infoList != null && infoList.size() > 0) {
                seekBar.setEnabled(true);
                seekBar.setProgress(0);
                seekBar.setMax(infoList.size());

                showFirst(infoList.get(0));
                List<GpsInfo> list = new ArrayList<>();
                for (GpsInfo gpsInfo : infoList) {
                    if (!gpsInfo.isStart) {
                        list.add(gpsInfo);
                    } else {
                        showMap(null, list);
                        list.clear();
                        list.add(gpsInfo);
                    }
                }
                showMap(null, list);
                return;
            }

            if (processGPSThread == null || processGPSThread.isStop()) {
                String t = getPresenter().getTLTime().split("-")[0];
                processGPSThread = new ProcessGPSThread(this, handler, times[Tool.stringToInt(t)]);
                processGPSThread.start();
            }

            if (getPresenter().getGTRkd(1, day, day)) {
                seekBar.setEnabled(false);
                seekBar.setProgress(0);
            }
        } else if (v.getId() == R.id.bt_change) {
            if (aMap.getMapType() == AMap.MAP_TYPE_NORMAL) {
                aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                ((Button) v).setText("普通地图");
            } else {
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);
                ((Button) v).setText("卫星地图");
            }
        } else if (v.getId() == R.id.ic_back) {
            finish();
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
            processGPSThread.addData(data);
//            setGTRkd(data);
        }
    }

    List<Polyline> polylines = new ArrayList<>();

    private void showFirst(GpsInfo gpsInfo) {
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gpsInfo.latLng, 15));
    }

    private synchronized void showMap(GpsInfo lastInfo, List<GpsInfo> infoList) {
        if (infoList.size() == 0) {
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
                findViewById(R.id.bt_get_gps).setEnabled(false);
                return;
            }
            if (countDay == vs.length - 1) {
                String[] list = new String[countDay];

                for (int i = 0; i < countDay; i++) {
                    list[i] = Tool.stringToDate(vs[i + 1]);
                }

                findViewById(R.id.bt_get_gps).setEnabled(true);
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
