package com.xingge.carble.bean;

import android.graphics.Color;

import com.amap.api.maps.model.LatLng;

import java.util.List;


public class RecordInfo {

    public List<LatLng> latLngList;

    public int color = Color.RED;

    public RecordInfo(List<LatLng> latLngList) {
        this.latLngList = latLngList;
    }

    public RecordInfo(List<LatLng> latLngList, int color) {
        this.latLngList = latLngList;
        this.color = color;
    }
}
