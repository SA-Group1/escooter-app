package com.example.escooter.ui.menu;

import androidx.annotation.Nullable;

import com.example.escooter.data.model.Gps;

public class EscooterGpsResult {
    @Nullable
    private Gps gps;
    @Nullable
    private Exception error;

    public EscooterGpsResult(@Nullable Exception error) {
        this.error = error;
    }

    public EscooterGpsResult(@Nullable Gps gps) {
        this.gps = gps;
    }

    @Nullable
    public Gps getEscooterGps() {
        return gps;
    }

    @Nullable
    public Exception getError() {
        return error;
    }
}
