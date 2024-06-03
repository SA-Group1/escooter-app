package com.example.escooter.ui.menu;

import androidx.annotation.Nullable;

import com.example.escooter.data.model.Escooter;
import com.example.escooter.data.model.ReturnAreas;

import java.util.List;

public class MapResult {
    @Nullable
    private ReturnAreas returnArea;
    @Nullable
    private Exception error;

    public MapResult(@Nullable Exception error) {
        this.error = error;
    }

    public MapResult(@Nullable ReturnAreas returnArea) {
        this.returnArea = returnArea;
    }

    @Nullable
    public ReturnAreas getReturnAreas() {
        return returnArea;
    }

    @Nullable
    public Exception getError() {
        return error;
    }
}
