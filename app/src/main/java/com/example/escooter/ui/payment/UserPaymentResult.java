package com.example.escooter.ui.payment;

import androidx.annotation.Nullable;

import com.example.escooter.data.model.CreditCard;
import com.example.escooter.data.model.MemberCard;

/**
 * Authentication result : success (user details) or error message.
 */
public class UserPaymentResult {
    @Nullable
    private CreditCard creditCard;
    @Nullable
    private MemberCard memberCard;
    @Nullable
    private Exception error;

    public UserPaymentResult(@Nullable Exception error) {
        this.error = error;
    }

    public UserPaymentResult(@Nullable CreditCard creditCard, @Nullable MemberCard memberCard) {
        this.creditCard = creditCard;
        this.memberCard = memberCard;
    }

    @Nullable
    CreditCard getCreditCard() {
        return creditCard;
    }

    @Nullable
    public MemberCard getMemberCard() {
        return memberCard;
    }

    @Nullable
    Exception getError() {
        return error;
    }
}