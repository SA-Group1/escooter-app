package com.example.escooter.ui.menu;

import androidx.annotation.Nullable;

import com.example.escooter.data.model.Escooter;

import java.util.List;

public class ParkResult {
    @Nullable
    private boolean isPark;
    @Nullable
    private Exception error;

    public ParkResult(@Nullable Exception error) {
        this.error = error;
    }

    public ParkResult(@Nullable boolean isPark) {
        this.isPark = isPark;
    }

    @Nullable
    public boolean getEscooterList() {
        return isPark;
    }

    @Nullable
    public Exception getError() {
        return error;
    }
}
