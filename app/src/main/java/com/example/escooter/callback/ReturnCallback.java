package com.example.escooter.callback;

import com.example.escooter.data.model.RentalRecord;

public interface ReturnCallback {
    void onSuccess(RentalRecord escooter);
    void onFailure(Exception e);
}
