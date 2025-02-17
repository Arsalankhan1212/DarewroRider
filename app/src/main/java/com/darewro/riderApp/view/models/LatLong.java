package com.darewro.riderApp.view.models;

import com.google.android.gms.maps.model.LatLng;

public class LatLong {
    LatLng latLng;
    String name;

    public LatLong(LatLng latLng, String name) {
        this.latLng = latLng;
        this.name = name;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
