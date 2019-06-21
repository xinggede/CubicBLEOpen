package com.xingge.carble.ui;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.xingge.carble.R;
import com.xingge.carble.base.mode.IBaseActivity;
import com.xingge.carble.bean.GpsInfo;
import com.xingge.carble.bluetooth.States;
import com.xingge.carble.dialog.InputLocationDialog;
import com.xingge.carble.dialog.SendLocationDialog;
import com.xingge.carble.ui.mode.MainContract;
import com.xingge.carble.ui.mode.MainPresenter;
import com.xingge.carble.ui.mode.ProcessGPSThread;
import com.xingge.carble.util.CommandUtil;
import com.xingge.carble.util.Tool;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 星哥的
 */
public class LocationActivity extends IBaseActivity<MainPresenter> implements MainContract.View, AMap.OnMyLocationChangeListener {

    MapView mMapView = null;
    AMap aMap;
    LatLng curLatLng;
    private Marker marker;
    private int showType;
    private InputLocationDialog inputLocationDialog;
    private SendLocationDialog sendLocationDialog;
    private CoordinateConverter converter;


    @Override
    protected MainPresenter onLoadPresenter() {
        return new MainPresenter(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_location;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        showType = getIntent().getIntExtra("showType", 0);

        converter = new CoordinateConverter(this);
        converter.from(CoordinateConverter.CoordType.GPS);

        mMapView = findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        if (aMap == null) {
            aMap = mMapView.getMap();
            aMap.setOnMyLocationChangeListener(this);
        }

        findViewById(R.id.bt_send).setEnabled(getIntent().getBooleanExtra("enable", true));

    }

    @Override
    protected void initEventAndData() {
        getPresenter().getGMDF();
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setZoomControlsEnabled(false);

        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.interval(2000);
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        myLocationStyle.showMyLocation(false);
        aMap.setMyLocationStyle(myLocationStyle);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (marker != null) {
            marker.destroy();
        }
        markerMap.clear();
        mMapView.onDestroy();
        if (inputLocationDialog != null) {
            inputLocationDialog.dismiss();
        }
        if (sendLocationDialog != null) {
            sendLocationDialog.dismiss();
        }
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_change) {
            if (aMap.getMapType() == AMap.MAP_TYPE_NORMAL) {
                aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                ((Button) v).setText("普通地图");
            } else {
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);
                ((Button) v).setText("卫星地图");
            }
        } else if (v.getId() == R.id.bt_input) {
            if (inputLocationDialog == null) {
                inputLocationDialog = new InputLocationDialog(this);
                inputLocationDialog.setBtClick(this);
            }
            inputLocationDialog.showType(showType);
        } else if (v.getId() == R.id.bt_send) {
            if (sendLocationDialog == null) {
                sendLocationDialog = new SendLocationDialog(this);
                sendLocationDialog.setBtClick(this);
            }
            sendLocationDialog.show();
        } else if (v.getId() == R.id.bt_confirm) {
            LatLng latLng = inputLocationDialog.getLatlng(showType);
            float distance = AMapUtils.calculateLineDistance(curLatLng, latLng);
            String title = inputLocationDialog.getTitle(showType);
            title += "距离:" + Tool.mToKM(distance) + "KM";
            addMark(latLng, title);
            inputLocationDialog.dismiss();
        } else if (v.getId() == R.id.bt_ok) {
            sendLocationDialog.dismiss();
            if (getPresenter().setRFRpt(1, sendLocationDialog.getValue())) {
                Tool.toastShow(this, "位置分享成功");
            }
        }
    }

    private void addMark(LatLng latLng, String title) {
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, aMap.getCameraPosition().zoom));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(latLng)
                .title(title);
        Marker marker = aMap.addMarker(markerOptions);
        marker.showInfoWindow();
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
        if (CommandUtil.GINFO.startsWith(command)) {
            setGInfo(data);
        } else if (CommandUtil.GMDF.startsWith(command)) {
            setGMdf(data);
        } else if (CommandUtil.RFRPT.startsWith(command)) {
            setRFRpt(data);
        }
    }

    private void setRFRpt(String data) {
        String[] vs = data.split(",");
        if (vs.length == 4) {
            String sn = vs[0];
            String id = vs[1];

            String lng = vs[2];
            String lat = vs[3];

            LatLng latLng = new LatLng(ProcessGPSThread.getLatitude(lat), ProcessGPSThread.getLongitude(lng));

            StringBuilder sb = new StringBuilder();
            sb.append("经度:").append(GpsInfo.formatLongitude(lng, showType)).append("\n");
            sb.append("纬度:").append(GpsInfo.formatLatitude(lat, showType)).append("\n");
            float distance = AMapUtils.calculateLineDistance(curLatLng, latLng);
            sb.append("距离:").append(Tool.mToKM(distance)).append("KM");
            addMarks(latLng, sb.toString(), sn + "-" + id);
        }
    }

    Map<String, Marker> markerMap = new HashMap<>();

    private void addMarks(LatLng latLng, String title, String key) {
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, aMap.getCameraPosition().zoom));
        Marker marker = markerMap.get(key);
        if (marker == null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                    .position(latLng)
                    .title(title);
            marker = aMap.addMarker(markerOptions);
            markerMap.put(key, marker);
        } else {
            marker.setTitle(title);
            marker.setPosition(latLng);
        }
        marker.showInfoWindow();
    }

    private void setGMdf(String data) {
        String[] vs = data.split(",");
        if (vs.length == 2) {
            String s = vs[1];
            showType = Tool.stringToInt(s);
        }
    }

    private void startLocation(boolean b) {
        aMap.setMyLocationEnabled(b);
    }

    private void setGInfo(String data) {
        GpsInfo gpsInfo = getPresenter().analysisGps(data);
        if (gpsInfo == null) {
            return;
        }
        if (gpsInfo.state == 0) {
            startLocation(true);
        } else {
            startLocation(false);
            LatLng latLng = new LatLng(ProcessGPSThread.getLatitude(gpsInfo.latitude), ProcessGPSThread.getLongitude(gpsInfo.longitude));
            converter.coord(latLng);
            showPoint(converter.convert());
        }
    }

    @Override
    public void onMyLocationChange(Location location) {
        Tool.logd("onMyLocationChange= " + location.toString());
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        showPoint(latLng);
    }

    private void showPoint(LatLng latLng) {
        curLatLng = latLng;
        if (marker != null) {
            marker.setPosition(latLng);
        } else {
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            markerOptions.draggable(false);
            marker = aMap.addMarker(markerOptions);
        }
    }
}
