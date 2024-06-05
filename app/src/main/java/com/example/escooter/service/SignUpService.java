package com.example.escooter.service;

import com.example.escooter.callback.BooleanCallback;
import com.example.escooter.repository.LoginRepository;
import com.example.escooter.repository.SignUpRepository;

public class SignUpService {

    private final SignUpRepository signUpRepository = new SignUpRepository();


    public void SignUp(String username, String password, String userName, String email, String phoneNumber, BooleanCallback callback) {
        signUpRepository.SignUp(username, password, userName, email, phoneNumber, new BooleanCallback() {
            @Override
            public void onSuccess(boolean isSignUp) {
                callback.onSuccess(isSignUp);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }
}
