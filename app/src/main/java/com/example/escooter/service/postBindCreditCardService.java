package com.example.escooter.service;

import android.app.AlertDialog;

import com.example.escooter.databinding.DialogPaymentAddCreditCardBinding;
import com.example.escooter.databinding.DialogPersonInfoEditProfileBinding;
import com.example.escooter.network.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class postBindCreditCardService {
    String baseUrl = "http://36.232.88.50:8080";
    String userDataUrl = baseUrl + "/api/bindCreditCard";
    public postBindCreditCardService(String account, String password, String username, DialogPaymentAddCreditCardBinding dialogBinding) {

        JSONObject PostData = new JSONObject();
        try {
            JSONObject userJson = new JSONObject();
            userJson.put("account", account);
            userJson.put("password", password);

            JSONObject creditCardJson = new JSONObject();
            creditCardJson.put("cardNumber", dialogBinding.userCardNumber.getText().toString());
            creditCardJson.put("expirationDate", dialogBinding.userVaildThru.getText().toString());
            creditCardJson.put("cardHolderName", username);
            creditCardJson.put("cvv", dialogBinding.userCvv.getText().toString());

            PostData.put("user", userJson);
            PostData.put("creditCard", creditCardJson);
        } catch (
                JSONException e) {
            throw new RuntimeException(e);
        }

        HttpRequest putupdateUserData = new HttpRequest(userDataUrl);
        // 發送 HTTP POST 請求
        putupdateUserData.httpPost(PostData, result -> {
            try {
                if (result.getBoolean("status")) {
                    System.out.println(result.getString("message"));
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
