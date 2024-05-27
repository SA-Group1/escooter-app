package com.example.escooter.callback;

public interface UnbindCreditCardCallback {
    void onSuccess(boolean isUnbindCreditCard);
    void onFailure(Exception e);
}
