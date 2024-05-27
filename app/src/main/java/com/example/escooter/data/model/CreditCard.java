package com.example.escooter.data.model;

public class CreditCard {
    private String cardNumber;
    private String expirationDate;

    // Getter 和 Setter 方法
    public String getCreditCardNumber() {
        return cardNumber;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
}
