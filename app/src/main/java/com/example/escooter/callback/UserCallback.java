package com.example.escooter.callback;

import com.example.escooter.data.model.User;

public interface UserCallback {
    void onSuccess(User user);
    void onFailure(Exception e);
}
