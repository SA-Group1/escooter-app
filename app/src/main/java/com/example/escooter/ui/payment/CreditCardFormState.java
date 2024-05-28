package com.example.escooter.ui.payment;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
public class CreditCardFormState {
    @Nullable
    private Integer cardNumberError;
    @Nullable
    private Integer vaildThruError;
    @Nullable
    private Integer cvvError;
    private boolean isDataValid;

    CreditCardFormState(@Nullable Integer cardNumberError, @Nullable Integer vaildThruError, @Nullable Integer cvvError) {
        this.cardNumberError = cardNumberError;
        this.vaildThruError = vaildThruError;
        this.cvvError = cvvError;
        this.isDataValid = false;
    }

    CreditCardFormState(boolean isDataValid) {
        this.cardNumberError = null;
        this.vaildThruError = null;
        this.cvvError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getCardNumberError() {
        return cardNumberError;
    }

    @Nullable
    public Integer getVaildThruErrorr() {
        return vaildThruError;
    }

    @Nullable
    public Integer getCvvError() {
        return cvvError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}