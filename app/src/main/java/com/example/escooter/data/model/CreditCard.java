package com.example.escooter.data.model;

import org.json.JSONException;
import org.json.JSONObject;

public class CreditCard {
    private String cardNumber;
    private String expirationDate;
    private String cardHolderName;

    public CreditCard(String cardNumber, String expirationDate, String cardHolderName) {
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
        this.cardHolderName = cardHolderName;
    }

    public static CreditCard fromJson(JSONObject jsonObject) throws JSONException {
        String cardNumber = jsonObject.getString("cardNumber");
        String expirationDate = jsonObject.getString("expirationDate");
        String cardHolderName = jsonObject.getString("cardHolderName");
        return new CreditCard(cardNumber, expirationDate, cardHolderName);
    }

    // Getter 和 Setter 方法
}
