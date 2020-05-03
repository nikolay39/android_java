package com.example.android.sunshine.model.entity;

import android.support.annotation.Nullable;

public class Coord {
    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    private double lon;
    private double lat;

}
