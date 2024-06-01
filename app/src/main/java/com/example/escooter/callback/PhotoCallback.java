package com.example.escooter.callback;

public interface PhotoCallback {
    void onSuccess(String photo);
    void onFailure(Exception e);
}
