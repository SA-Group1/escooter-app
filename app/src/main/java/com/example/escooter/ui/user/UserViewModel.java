package com.example.escooter.ui.user;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.escooter.callback.UpdataUserCallback;
import com.example.escooter.callback.UserCallback;
import com.example.escooter.data.model.User;
import com.example.escooter.service.UserService;
import com.example.escooter.ui.login.LoginFormState;

public class UserViewModel extends ViewModel {
    private final UserService UserService = new UserService();
    private final MutableLiveData<UserFormState> userFormState = new MutableLiveData<>();
    private final MutableLiveData<UserResult> userResult = new MutableLiveData<>();
    private final MutableLiveData<String> account = new MutableLiveData<>();
    private final MutableLiveData<String> password = new MutableLiveData<>();
    private final MutableLiveData<String> username = new MutableLiveData<>();
    private final MutableLiveData<String> email = new MutableLiveData<>();
    private final MutableLiveData<String> phoneNumber = new MutableLiveData<>();

    public LiveData<UserResult> getUserResult() {
        return userResult;
    }
    public LiveData<String> getAccount() {
        return account;
    }
    public LiveData<String> getPassword() {
        return password;
    }
    public LiveData<UserFormState> getUserFormState() {
        return userFormState;
    }

    public void getUserData() {
        if (account.getValue() == null || password.getValue() == null){
            System.out.println("Error");
        }
        UserService.getUserData(account.getValue(), password.getValue(), new UserCallback() {

            @Override
            public void onSuccess(User user) {
                user.setPassword(password.getValue());
                userResult.postValue(new UserResult(user));
            }

            @Override
            public void onFailure(Exception e) {
                userResult.postValue(new UserResult(e));
            }
        });
    }

    public void updataUserData() {
        if (account.getValue() == null || password.getValue() == null){
            System.out.println("Error");
        }
        UserService.updataUserData(account.getValue(), password.getValue(), username.getValue(), email.getValue(), phoneNumber.getValue(), new UpdataUserCallback() {
            @Override
            public void onSuccess(boolean isUpdataUserData) {
                User user = userResult.getValue().getUser();
                user.setUserName(username.getValue());
                user.setEmail(email.getValue());
                user.setPhoneNumber(phoneNumber.getValue());
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
    }

    public void updataDataChanged(String username, String email, String phoneNumber) {
        if (isUserNameValid(username) && isEmailValid(email) && isPhoneNumberValid(phoneNumber)) {
            this.username.setValue(username);
            this.email.setValue(email);
            this.phoneNumber.setValue(phoneNumber);
            userFormState.setValue(new UserFormState(true));
        } else {
            userFormState.setValue(new UserFormState(false));
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
    private boolean isEmailValid(String email) {
        return email != null && email.trim().length() > 5;
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        return phoneNumber != null && phoneNumber.trim().length() > 5;
    }

}
