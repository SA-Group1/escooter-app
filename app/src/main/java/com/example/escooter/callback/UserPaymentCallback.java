package com.example.escooter.callback;

import com.example.escooter.data.model.CreditCard;
import com.example.escooter.data.model.MemberCard;

public interface UserPaymentCallback {
    void onSuccess(CreditCard creditCard, MemberCard memberCard);
    void onFailure(Exception e);
}
