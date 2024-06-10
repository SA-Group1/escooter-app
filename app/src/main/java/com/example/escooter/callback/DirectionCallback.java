package com.example.escooter.callback;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface DirectionCallback {
    void onSuccess(List<LatLng> points);
    void onFailure(Exception e);
}
