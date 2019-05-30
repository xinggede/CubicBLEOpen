package com.xingge.maplocation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

public class MainActivity extends AppCompatActivity implements GeocodeSearch.OnGeocodeSearchListener, AMap.OnMapClickListener {

    MapView mMapView = null;
    AMap aMap;
    MyLocationStyle myLocationStyle;
    GeocodeSearch geocodeSearch;
    LatLng latLng;
    private MarkerOptions markerOptions;
    EditText et1, et2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapView = findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        et1 = findViewById(R.id.et_input1);
        et2 = findViewById(R.id.et_input2);

        if (aMap == null) {
            aMap = mMapView.getMap();

            markerOptions = new MarkerOptions();
            markerOptions.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_RED));
            markerOptions.draggable(true);
        }
        geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(this);

        startLocation();
    }

    private void startLocation() {
        //显示室内地图
        aMap.showIndoorMap(true);
        aMap.setOnMapClickListener(this);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);


        myLocationStyle = new MyLocationStyle();
        myLocationStyle.interval(2000);
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationEnabled(true);
    }

    @Override
    protected void onDestroy() {
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

    Marker marker;
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                String addressName = result.getRegeocodeAddress().getFormatAddress();
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                if(marker == null || !marker.isInfoWindowShown()){
                    markerOptions.title(addressName);
                    markerOptions.position(latLng);
                    marker = aMap.addMarker(markerOptions);
                    marker.showInfoWindow();
                } else {
                    marker.setTitle(addressName);
                    marker.setPosition(latLng);
                }

            }
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        this.latLng = latLng;
        getAddress(latLng);
        et1.setText(latLng.longitude + "");
        et2.setText(latLng.latitude + "");
    }

    private void getAddress(LatLng latLng) {
        //逆地理编码查询条件：逆地理编码查询的地理坐标点、查询范围、坐标类型。
        LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200f, GeocodeSearch.AMAP);
        //异步查询
        geocodeSearch.getFromLocationAsyn(query);
    }

    public void skipMap(View view) {
        String latitude = et2.getText().toString().trim();
        String longitude = et1.getText().toString().trim();
        Intent intent = new Intent();
        if (Utils.checkMapAppsIsExist(MainActivity.this, Utils.GAODE_PKG)) {
            intent.setAction("android.intent.action.VIEW");
            intent.setPackage(Utils.GAODE_PKG);
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse(Utils.getGaodeURL(getText(R.string.app_name).toString(), "", latitude, longitude)));
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "高德地图未安装", Toast.LENGTH_SHORT).show();
        }
    }

    private String[] check(String text) {
        int index = text.indexOf("Lat");
        if (index == -1) {
            return null;
        }
        int i = text.indexOf(',');
        if (i == -1) {
            return null;
        }
        String latitude = text.substring(index, i);
        if (!latitude.contains(":")) {
            return null;
        }
        latitude = latitude.split(":")[1];
        latitude = latitude.substring(1, latitude.length());

        index = text.indexOf("Lon");
        if (index == -1) {
            return null;
        }
        i = text.indexOf(',', i + 1);
        String longitude = text.substring(index, i);
        if (!longitude.contains(":")) {
            return null;
        }
        longitude = longitude.split(":")[1];
        longitude = longitude.substring(1, longitude.length());

        return new String[]{longitude, latitude};
    }

    public void analyticalCode(View view) {
        String str[] = check(et1.getText().toString().trim());
        if (str == null) {
            return;
        }
        et1.setText(str[0]);
        et2.setText(str[1]);
    }
}
