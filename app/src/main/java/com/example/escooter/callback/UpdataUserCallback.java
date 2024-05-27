package com.example.escooter.callback;

import com.example.escooter.data.model.User;

public interface UpdataUserCallback {
    void onSuccess(boolean isUpdataUserData);
    void onFailure(Exception e);
}
