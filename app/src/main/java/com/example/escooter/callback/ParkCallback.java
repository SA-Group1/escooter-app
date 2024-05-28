package com.example.escooter.callback;

public interface ParkCallback {
    void onSuccess(boolean isPark);
    void onFailure(Exception e);
}