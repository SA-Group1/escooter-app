package com.example.escooter.data.model;

import org.json.JSONException;
import org.json.JSONObject;

public class MemberCard {
    private String cardNumber;
    private String expirationDate;
    private boolean valid;

    public MemberCard(String cardNumber, String expirationDate, boolean valid) {
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
        this.valid = valid;
    }

    public static MemberCard fromJson(JSONObject jsonObject) throws JSONException {
        String cardNumber = jsonObject.optString("cardNumber", "");
        String expirationDate = jsonObject.optString("expirationDate", "");
        boolean valid = jsonObject.optBoolean("valid", true); // 默認為 true

        return new MemberCard(cardNumber, expirationDate, valid);
    }

    // Getter 和 Setter 方法
}
