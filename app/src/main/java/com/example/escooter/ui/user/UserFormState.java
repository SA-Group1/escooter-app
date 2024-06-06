package com.example.escooter.ui.user;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
public class UserFormState {
    @Nullable
    private Integer usernameError;
    @Nullable
    private Integer emailError;
    @Nullable
    private Integer phoneNumberError;
    private boolean isDataValid;

    UserFormState(@Nullable Integer usernameError, @Nullable Integer emailError, @Nullable Integer phonenumberError) {
        this.usernameError = usernameError;
        this.emailError = emailError;
        this.phoneNumberError = phonenumberError;
        this.isDataValid = false;
    }

    public UserFormState(boolean isDataValid) {
        this.usernameError = null;
        this.emailError = null;
        this.phoneNumberError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    public Integer getEmailError() {
        return emailError;
    }

    @Nullable
    public Integer getPhoneNumberError() {
        return phoneNumberError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}