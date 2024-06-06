package com.example.escooter.ui.signup;

import androidx.annotation.Nullable;

public class SignUpResult {
    private boolean isSignUp;
    @Nullable
    private Exception error;

    public SignUpResult(@Nullable Exception error) {
        this.error = error;
    }

    public SignUpResult(boolean isSignUp) {
        this.isSignUp = isSignUp;
    }

    public boolean getSignUp() {
        return isSignUp;
    }

    @Nullable
    public Exception getError() {
        return error;
    }
}
