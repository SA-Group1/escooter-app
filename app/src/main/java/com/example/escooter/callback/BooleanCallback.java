package com.example.escooter.callback;

public interface BooleanCallback {
    void onSuccess(boolean isTrue);
    void onFailure(Exception e);
}
