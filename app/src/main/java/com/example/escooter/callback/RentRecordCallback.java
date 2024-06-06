package com.example.escooter.callback;

import com.example.escooter.data.model.RentalRecord;

import java.util.ArrayList;

public interface RentRecordCallback {
    void onSuccess(ArrayList<RentalRecord> rentalRecord);
    void onFailure(Exception e);
}
