package com.example.escooter.ui.menu;

import androidx.annotation.Nullable;

import com.example.escooter.data.model.Escooter;

import java.util.List;

public class RentableListResult {
    @Nullable
    private List<Escooter> escooterList;
    @Nullable
    private Exception error;

    public RentableListResult(@Nullable Exception error) {
        this.error = error;
    }

    public RentableListResult(@Nullable List<Escooter> escooterList) {
        this.escooterList = escooterList;
    }

    @Nullable
    public List<Escooter> getEscooterList() {
        return escooterList;
    }

    @Nullable
    public Exception getError() {
        return error;
    }
}
