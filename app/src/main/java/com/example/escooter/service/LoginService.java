package com.example.escooter.service;

import com.example.escooter.callback.BooleanCallback;
import com.example.escooter.repository.LoginRepository;

public class LoginService {

    private final LoginRepository loginRepository = new LoginRepository();


    public void login(String username, String password, BooleanCallback callback) {
        loginRepository.login(username, password, new BooleanCallback() {
            @Override
            public void onSuccess(boolean isLogin) {
                callback.onSuccess(isLogin);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }
}
