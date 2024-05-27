package com.example.escooter.repository;

import com.example.escooter.BuildConfig;
import com.example.escooter.callback.BindCreditCardCallback;
import com.example.escooter.callback.HttpResultCallback;
import com.example.escooter.callback.UnbindCreditCardCallback;
import com.example.escooter.callback.UserCallback;
import com.example.escooter.data.model.CreditCard;
import com.example.escooter.data.model.MemberCard;
import com.example.escooter.data.model.User;
import com.example.escooter.network.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class CreditCardRepository {
    public void bindCreditCard(String account, String password, String CardNumber, String VaildThru, String username, String Cvv, BindCreditCardCallback callback) {

        JSONObject body = new JSONObject();
        try {
            JSONObject userJson = new JSONObject();
            userJson.put("account", account);
            userJson.put("password", password);

            JSONObject creditCardJson = new JSONObject();
            creditCardJson.put("cardNumber", CardNumber);
            creditCardJson.put("expirationDate", VaildThru);
            creditCardJson.put("cardHolderName", username);
            creditCardJson.put("cvv", Cvv);
//            dialogBinding.userCardNumber.getText().toString()
//            dialogBinding.userVaildThru.getText().toString()
//            username
//            dialogBinding.userCvv.getText().toString()
            body.put("user", userJson);
            body.put("creditCard", creditCardJson);
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

    public void unbindCreditCard(String account, String password, UnbindCreditCardCallback callback) {

        JSONObject body = new JSONObject();
        try {
            body.put("account", account);
            body.put("password", password);
        } catch (
                JSONException e) {
            throw new RuntimeException(e);
        }

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
