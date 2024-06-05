package com.example.escooter.viewmodel;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.escooter.callback.BooleanCallback;
import com.example.escooter.service.SignUpService;
import com.example.escooter.ui.signup.SignUpFormState;
import com.example.escooter.ui.signup.SignUpResult;

public class SignUpViewModel extends ViewModel {

    private final SignUpService signUpService = new SignUpService();
    private final MutableLiveData<SignUpResult> signUpResult = new MutableLiveData<>();
    private final MutableLiveData<SignUpFormState> signUpFormState = new MutableLiveData<>();

    public LiveData<SignUpFormState> getSignUpFormState() {
        return signUpFormState;
    }
    public LiveData<SignUpResult> getSignUpResult() {
        return signUpResult;
    }
    public void SignUp(String account, String password, String userName, String email, String phoneNumber) {
        signUpService.SignUp(account, password, userName, email, phoneNumber, new BooleanCallback() {
            @Override
            public void onSuccess(boolean isSignUp) {
                signUpResult.postValue(new SignUpResult(isSignUp));
            }

            @Override
            public void onFailure(Exception e) {
                signUpResult.postValue(new SignUpResult(e));
            }
        });
    }
    public void signUpDataChanged(String account, String password, String userName, String email, String phoneNumber) {
        boolean isAccountValid = isAccountValid(account);
        boolean isPasswordValid = isPasswordValid(password);
        boolean isUserNameValid = isUserNameValid(userName);
        boolean isEmailValid = isEmailValid(email);
        boolean isPhoneNumberValid = isPhoneNumberValid(phoneNumber);

        if (isAccountValid && isPasswordValid && isUserNameValid && isEmailValid && isPhoneNumberValid) {
            signUpFormState.setValue(new SignUpFormState(true));
        } else {
            signUpFormState.setValue(new SignUpFormState(false));
        }
    }

    // A placeholder account validation check
    private boolean isAccountValid(String account) {
        return account != null && !account.trim().isEmpty();
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().isEmpty();
    }

    // A placeholder user name validation check
    private boolean isUserNameValid(String userName) {
        return userName != null && !userName.trim().isEmpty();
    }

    // A placeholder email validation check
    private boolean isEmailValid(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // A placeholder phone number validation check
    private boolean isPhoneNumberValid(String phoneNumber) {
        return phoneNumber != null && Patterns.PHONE.matcher(phoneNumber).matches();
    }
}