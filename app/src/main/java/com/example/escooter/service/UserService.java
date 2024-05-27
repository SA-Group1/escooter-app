package com.example.escooter.service;

import com.example.escooter.callback.UpdataUserCallback;
import com.example.escooter.callback.UserCallback;
import com.example.escooter.data.model.User;
import com.example.escooter.repository.UserRepository;

public class UserService {
    private final UserRepository UserRepository = new UserRepository();


    public void getUserData(String account, String password, UserCallback callback) {
        UserRepository.getUserData(account, password, new UserCallback() {
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

    public void updataUserData(String account, String password, String username, String email, String phoneNumber, UpdataUserCallback callback) {
        UserRepository.updataUserData(account, password, username, email, phoneNumber, new UpdataUserCallback() {
            @Override
            public void onSuccess(boolean isUpdataUserData) {
                callback.onSuccess(isUpdataUserData);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }
}
