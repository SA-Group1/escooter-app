package com.example.escooter.callback;

import com.example.escooter.data.model.Gps;

public interface EscooterGpsCallback {
    void onSuccess(Gps gps);
    void onFailure(Exception e);
}
