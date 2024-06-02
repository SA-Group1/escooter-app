package com.example.escooter.callback;

public interface UploadUserPhotoCallback {
    void onSuccess(boolean isUploadUserData);
    void onFailure(Exception e);
}
