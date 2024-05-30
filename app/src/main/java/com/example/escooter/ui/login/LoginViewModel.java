package com.example.escooter.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.example.escooter.callback.LoginCallback;
import com.example.escooter.service.LoginService;

public class LoginViewModel extends ViewModel {

    private final LoginService loginService = new LoginService();
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();

    public LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }
    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }
    public void login(String username, String password) {

        loginService.login(username, password, new LoginCallback() {

            @Override
            public void onSuccess(boolean isLogin) {
                if(isLogin){
                    loginResult.postValue(new LoginResult(new LoggedInUserView(username)));
                }
            }

            @Override
            public void onFailure(Exception e) {
                loginResult.postValue(new LoginResult(e));
            }
        });
    }
    public void loginDataChanged(String username, String password) {
        if (isUserNameValid(username) && isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(true));
        } else {
            loginFormState.setValue(new LoginFormState(false));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}