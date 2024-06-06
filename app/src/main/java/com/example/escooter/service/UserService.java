package com.example.escooter.service;

import com.example.escooter.callback.BooleanCallback;
import com.example.escooter.callback.PhotoCallback;
import com.example.escooter.callback.UserCallback;
import com.example.escooter.data.model.User;
import com.example.escooter.repository.UserRepository;

public class UserService {
    private final UserRepository userRepository = new UserRepository();


    public void getUserData(String account, String password, UserCallback callback) {

        userRepository.getUserData(account, password, new UserCallback() {
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

    public void getUserPhoto(String account, String password, PhotoCallback callback){
        userRepository.getUserPhoto(account, password, new PhotoCallback() {
            @Override
            public void onSuccess(String photo) {
                callback.onSuccess(photo);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void updateUserData(String account, String password, String username, String email, String phoneNumber, BooleanCallback callback) {
        userRepository.updateUserData(account, password, username, email, phoneNumber, new BooleanCallback() {
            @Override
            public void onSuccess(boolean isUpdateUserData) {
                callback.onSuccess(isUpdateUserData);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void uploadUserPhoto(String account, String password,String photo, BooleanCallback callback){
        userRepository.uploadUserPhoto(account, password , photo , new BooleanCallback() {
            @Override
            public void onSuccess(boolean isUploadUserPhoto) {
                callback.onSuccess(isUploadUserPhoto);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }
}
