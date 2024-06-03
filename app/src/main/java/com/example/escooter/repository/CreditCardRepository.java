package com.example.escooter.repository;

import com.example.escooter.BuildConfig;
import com.example.escooter.callback.BooleanCallback;
import com.example.escooter.callback.HttpResultCallback;
import com.example.escooter.network.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class CreditCardRepository {
    public void bindCreditCard(String account, String password, String CardNumber, String VaildThru, String username, String Cvv, BooleanCallback callback) {

        JSONObject body = new JSONObject();
        try {
            body.put("account", account);
            body.put("password", password);
            body.put("cardNumber", CardNumber);
            body.put("expirationDate", VaildThru);
            body.put("cardHolderName", username);
            body.put("cvv", Cvv);
        } catch (JSONException e) {
            callback.onFailure(e);
            return;
        }

        HttpRequest.httpRequest(BuildConfig.BASE_URL + "/bindCreditCard", "POST", body, new HttpResultCallback<JSONObject>() {
            @Override
            public void onResult(JSONObject result) {
                try {
                    boolean isBindCreditCard = result.getBoolean("status");
                    callback.onSuccess(isBindCreditCard);
                } catch (JSONException e) {
                    callback.onFailure(e);
                }
            }
            @Override
            public void onError(Exception e) {
                System.out.println(e.toString());
                callback.onFailure(e);
            }
        });
    }

    public void unbindCreditCard(String account, String password, BooleanCallback callback) {

        JSONObject body = new JSONObject();
        try {
            body.put("account", account);
            body.put("password", password);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        System.out.println(body);
        HttpRequest.httpRequest(BuildConfig.BASE_URL + "/unbindCreditCard", "POST", body, new HttpResultCallback<JSONObject>() {
            @Override
            public void onResult(JSONObject result) {
                try {
                    boolean isUnbindCreditCard = result.getBoolean("status");
                    callback.onSuccess(isUnbindCreditCard);
                } catch (JSONException e) {
                    callback.onFailure(e);
                }
            }
            @Override
            public void onError(Exception e) {
                System.out.println(e.toString());
                callback.onFailure(e);
            }
        });
    }
}
