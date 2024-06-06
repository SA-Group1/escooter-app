package com.example.escooter.ui.menu;

import androidx.annotation.Nullable;

public class ParkResult {
    private boolean isPark;
    @Nullable
    private Exception error;

    public ParkResult(@Nullable Exception error) {
        this.error = error;
    }

    public ParkResult(boolean isPark) {
        this.isPark = isPark;
    }

    public boolean getEscooterList() {
        return isPark;
    }

    @Nullable
    public Exception getError() {
        return error;
    }
}
