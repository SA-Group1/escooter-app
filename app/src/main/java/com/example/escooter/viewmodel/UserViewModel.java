package com.example.escooter.viewmodel;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.escooter.callback.BooleanCallback;
import com.example.escooter.callback.PhotoCallback;
import com.example.escooter.callback.UserCallback;
import com.example.escooter.data.model.User;
import com.example.escooter.service.UserService;
import com.example.escooter.ui.user.UserFormState;
import com.example.escooter.ui.user.UserResult;

import java.util.Objects;

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
    public LiveData<String> getUsername() {
        return username;
    }

    public LiveData<UserFormState> getUserFormState() {
        return userFormState;
    }

    public void getUserData() {
        if (account.getValue() == null || password.getValue() == null){
            System.out.println("Error");
            return;
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

        UserService.getUserPhoto(account.getValue(), password.getValue(), new PhotoCallback() {
            @Override
            public void onSuccess(String photo) {
                User user = Objects.requireNonNull(userResult.getValue()).getUser();
                assert user != null;
                user.setPhoto(photo);
                userResult.postValue(new UserResult(user));
            }

            @Override
            public void onFailure(Exception e) {
                userResult.postValue(new UserResult(e));
            }
        });
    }

    public void updateUserData() {
        if (account.getValue() == null || password.getValue() == null){
            System.out.println("Error");
        }
        UserService.updateUserData(account.getValue(), password.getValue(), username.getValue(), email.getValue(), phoneNumber.getValue(), new BooleanCallback() {
            @Override
            public void onSuccess(boolean isUpdateUserData) {
                if (userResult.getValue() != null && userResult.getValue().getUser() != null) {
                    User user = userResult.getValue().getUser();
                    user.setUserName(username.getValue());
                    user.setEmail(email.getValue());
                    user.setPhoneNumber(phoneNumber.getValue());
                    userResult.postValue(new UserResult(user));
                }
            }

            @Override
            public void onFailure(Exception e) {
                userResult.postValue(new UserResult(e));
            }
        });
    }

    public void uploadUserPhoto(String photo){
        UserService.uploadUserPhoto(account.getValue(), password.getValue(), photo, new BooleanCallback() {
            @Override
            public void onSuccess(boolean isUploadUserData) {
                User user = userResult.getValue().getUser();
                user.setPhoto(photo);
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

    public void clearData() {
        User user = new User();
        userResult.setValue(new UserResult(user));
        account.setValue(null);
        password.setValue(null);
        username.setValue(null);
        email.setValue(null);
        phoneNumber.setValue(null);
    }

}
