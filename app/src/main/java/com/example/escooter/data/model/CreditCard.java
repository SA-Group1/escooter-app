package com.example.escooter.data.model;

import org.json.JSONException;
import org.json.JSONObject;

public class CreditCard {
    private String cardNumber;
    private String expirationDate;

    public CreditCard(String cardNumber, String expirationDate) {
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
    }

    public static CreditCard fromJson(JSONObject jsonObject) throws JSONException {
        String cardNumber = jsonObject.getString("cardNumber");
        String expirationDate = jsonObject.getString("expirationDate");
        return new CreditCard(cardNumber, expirationDate);
    }

    // Getter 和 Setter 方法
    public String getCreditCardNumber() {
        return cardNumber;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

}
