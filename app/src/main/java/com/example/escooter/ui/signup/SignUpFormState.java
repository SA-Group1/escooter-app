package com.example.escooter.ui.signup;

import androidx.annotation.Nullable;

/**
 * Data validation state of the sign-up form.
 */
public class SignUpFormState {
    @Nullable
    private Integer accountError;
    @Nullable
    private Integer passwordError;
    @Nullable
    private Integer userNameError;
    @Nullable
    private Integer emailError;
    @Nullable
    private Integer phoneNumberError;
    private boolean isDataValid;

    SignUpFormState(@Nullable Integer accountError, @Nullable Integer passwordError, @Nullable Integer userNameError, @Nullable Integer emailError, @Nullable Integer phoneNumberError) {
        this.accountError = accountError;
        this.passwordError = passwordError;
        this.userNameError = userNameError;
        this.emailError = emailError;
        this.phoneNumberError = phoneNumberError;
        this.isDataValid = false;
    }

    public SignUpFormState(boolean isDataValid) {
        this.accountError = null;
        this.passwordError = null;
        this.userNameError = null;
        this.emailError = null;
        this.phoneNumberError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getAccountError() {
        return accountError;
    }

    @Nullable
    public Integer getPasswordError() {
        return passwordError;
    }

    @Nullable
    public Integer getUserNameError() {
        return userNameError;
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
