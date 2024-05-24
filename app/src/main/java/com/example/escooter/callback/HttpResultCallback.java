package com.example.escooter.callback;

public interface HttpResultCallback<T> {
    void onResult(T result);
    void onError(Exception e);
}
