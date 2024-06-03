package com.example.escooter.callback;

import com.example.escooter.data.model.ReturnAreas;

public interface ReturnAreasCallback {
    void onSuccess(ReturnAreas returnAreas);
    void onFailure(Exception e);
}
