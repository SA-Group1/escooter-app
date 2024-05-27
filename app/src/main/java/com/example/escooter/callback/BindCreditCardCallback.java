package com.example.escooter.callback;

import com.example.escooter.data.model.CreditCard;

public interface BindCreditCardCallback {
    void onSuccess(boolean isBindCreditCard);
    void onFailure(Exception e);
}
