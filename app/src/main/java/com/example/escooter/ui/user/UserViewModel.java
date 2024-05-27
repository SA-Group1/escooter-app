package com.example.escooter.ui.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.escooter.callback.UserCallback;
import com.example.escooter.data.model.User;
import com.example.escooter.service.UserService;

public class UserViewModel extends ViewModel {
    private final UserService UserService = new UserService();
    private final MutableLiveData<UserResult> userResult = new MutableLiveData<>();
    private final MutableLiveData<String> account = new MutableLiveData<>();
    private final MutableLiveData<String> password = new MutableLiveData<>();

    public LiveData<UserResult> getUserResult() {
        return userResult;
    }
    public LiveData<String> getAccount() {
        return account;
    }
    public LiveData<String> getPassword() {
        return password;
    }

    public void getUserData() {
        System.out.println("帳號: " + account.getValue());
        System.out.println("密碼: " + password.getValue());
        if (account.getValue() == null || password.getValue() == null){
            System.out.println("Error");
        }
        UserService.getUserData(account.getValue(), password.getValue(), new UserCallback() {

            @Override
            public void onSuccess(User user) {
                userResult.postValue(new UserResult(user));
            }

            @Override
            public void onFailure(Exception e) {
                userResult.postValue(new UserResult(e));
            }
        });
    }

    public void setUserCredential(String account,String password) {

        this.account.setValue(account);
        this.password.setValue(password);
        System.out.println("設置的帳號: " + this.account.getValue());
        System.out.println("設置的密碼: " + this.password.getValue());
    }
}
