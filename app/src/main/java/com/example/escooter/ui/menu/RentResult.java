package com.example.escooter.ui.menu;

import androidx.annotation.Nullable;

import com.example.escooter.data.model.Escooter;
import com.example.escooter.data.model.User;

import java.util.List;

public class RentResult {
    @Nullable
    private Escooter escooter;
    @Nullable
    private Exception error;

    public RentResult(@Nullable Exception error) {
        this.error = error;
    }

    public RentResult(@Nullable Escooter escooter) {
        this.escooter = escooter;
    }

    @Nullable
    public Escooter getEscooter() {
        return escooter;
    }

    @Nullable
    public Exception getError() {
        return error;
    }
}
