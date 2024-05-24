package com.example.escooter.callback;

public interface LoginCallback {
    void onSuccess(boolean isLogin);
    void onFailure(Exception e);
}