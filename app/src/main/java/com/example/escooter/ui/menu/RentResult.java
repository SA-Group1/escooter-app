package com.example.escooter.ui.menu;

import androidx.annotation.Nullable;

import com.example.escooter.data.model.Escooter;
import com.example.escooter.data.model.User;

import java.util.List;

public class RentResult {
    @Nullable
    private List<Escooter> escooterList;
    @Nullable
    private Exception error;

    public RentResult(@Nullable Exception error) {
        this.error = error;
    }

    public RentResult(@Nullable List<Escooter> escooterList) {
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
