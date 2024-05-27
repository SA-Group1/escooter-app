package com.example.escooter.service;

import com.example.escooter.callback.UserCallback;
import com.example.escooter.data.model.User;
import com.example.escooter.repository.UserRepository;

public class UserService {
    private final UserRepository UserRepository = new UserRepository();


    public void getUserData(String username, String password, UserCallback callback) {
        UserRepository.getUserData(username, password, new UserCallback() {
            @Override
            public void onSuccess(User user) {
                callback.onSuccess(user);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }
}
