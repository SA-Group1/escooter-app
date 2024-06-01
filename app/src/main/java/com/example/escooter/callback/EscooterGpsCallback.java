package com.example.escooter.callback;

import com.example.escooter.data.model.Gps;
import com.example.escooter.data.model.RentalRecord;

import java.util.ArrayList;

public interface EscooterGpsCallback {
    void onSuccess(Gps gps);
    void onFailure(Exception e);
}
