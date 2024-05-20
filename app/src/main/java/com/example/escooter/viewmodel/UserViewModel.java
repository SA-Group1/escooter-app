package com.example.escooter.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.escooter.data.model.User;

public class UserViewModel extends ViewModel {
    private MutableLiveData<User> userData = new MutableLiveData<>();

    // 提供方法来设置用户数据
    public void setUserData(User user) {
        userData.setValue(user);
    }

    // 提供方法来获取用户数据
    public LiveData<User> getUserData() {
        return userData;
    }

}
