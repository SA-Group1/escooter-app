package com.example.escooter.data.model;

public class Gps {
    private double longitude;
    private double latitude;

    public Gps(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

}

