package com.example.escooter.data.model;

public class MemberCard {
    private String cardNumber;
    private String expirationDate;
    private boolean valid;

    public String getMemberCardNumber() {
        return cardNumber;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public boolean getValid() { return valid; }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
