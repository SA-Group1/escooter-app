package com.example.escooter.ui.rentrecord;

import androidx.annotation.Nullable;

import com.example.escooter.data.model.RentalRecord;

import java.util.ArrayList;
import java.util.List;

public class RentRecordResult {
    @Nullable
    private ArrayList<RentalRecord> rentalRecord;
    @Nullable
    private Exception error;

    public RentRecordResult(@Nullable Exception error) {
        this.error = error;
    }

    public RentRecordResult(@Nullable ArrayList<RentalRecord> rentalRecord) {
        this.rentalRecord = rentalRecord;
    }

    @Nullable
    public ArrayList<RentalRecord> getRentalRecord() {
        return rentalRecord;
    }

    @Nullable
    public Exception getError() {
        return error;
    }
}
