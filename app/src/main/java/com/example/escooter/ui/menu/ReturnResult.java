package com.example.escooter.ui.menu;

import androidx.annotation.Nullable;

import com.example.escooter.data.model.RentalRecord;

public class ReturnResult {
    @Nullable
    private RentalRecord escooter;
    @Nullable
    private Exception error;

    public ReturnResult(@Nullable Exception error) {
        this.error = error;
    }

    public ReturnResult(@Nullable RentalRecord escooter) {
        this.escooter = escooter;
    }

    @Nullable
    public RentalRecord getRentalRecord() {
        return escooter;
    }

    @Nullable
    public Exception getError() {
        return error;
    }
}
