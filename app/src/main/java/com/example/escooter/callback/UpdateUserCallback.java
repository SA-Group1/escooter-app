package com.example.escooter.callback;

public interface UpdateUserCallback {
    void onSuccess(boolean isUpdateUserData);
    void onFailure(Exception e);
}
