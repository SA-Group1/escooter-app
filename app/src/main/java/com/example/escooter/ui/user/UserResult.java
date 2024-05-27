package com.example.escooter.ui.user;

import androidx.annotation.Nullable;

import com.example.escooter.data.model.User;

public class UserResult {
    @Nullable
    private User user;
    @Nullable
    private Exception error;

    public UserResult(@Nullable Exception error) {
        this.error = error;
    }

    public UserResult(@Nullable User user) {
        this.user = user;
    }

    @Nullable
    public User getUser() {
        return user;
    }

    @Nullable
    public Exception getError() {
        return error;
    }
}
