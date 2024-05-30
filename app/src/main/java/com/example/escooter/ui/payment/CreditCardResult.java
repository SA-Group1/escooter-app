package com.example.escooter.ui.payment;

import androidx.annotation.Nullable;

import com.example.escooter.data.model.CreditCard;

/**
 * Authentication result : success (user details) or error message.
 */
public class CreditCardResult {
    @Nullable
    private CreditCard creditCard;
    @Nullable
    private Exception error;

    public CreditCardResult(@Nullable Exception error) {
        this.error = error;
    }

    public CreditCardResult(@Nullable CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    @Nullable
    CreditCard getCreditCard() {
        return creditCard;
    }

    @Nullable
    Exception getError() {
        return error;
    }
}