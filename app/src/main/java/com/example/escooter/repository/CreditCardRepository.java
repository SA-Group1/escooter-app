package com.example.escooter.repository;

import com.example.escooter.BuildConfig;
import com.example.escooter.callback.BooleanCallback;
import com.example.escooter.callback.HttpResultCallback;
import com.example.escooter.callback.UserPaymentCallback;
import com.example.escooter.data.model.CreditCard;
import com.example.escooter.data.model.MemberCard;
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

    public void getUserPayment(String account, String password, UserPaymentCallback callback) {

        JSONObject body = new JSONObject();
        try {
            body.put("account", account);
            body.put("password", password);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        HttpRequest.httpRequest(BuildConfig.BASE_URL + "/getUserPayment", "POST", body, new HttpResultCallback<JSONObject>() {
            @Override
            public void onResult(JSONObject result) {
                try {
                    JSONObject data = result.getJSONObject("data");
                    JSONObject creditCardJson = data.getJSONObject("creditCard");
                    JSONObject memberCardJson = data.getJSONObject("memberCard");

                    // 解析 CreditCard 信息
                    CreditCard creditCard = new CreditCard();
                    creditCard.setCardNumber(creditCardJson.optString("cardNumber", null));
                    creditCard.setExpirationDate(creditCardJson.optString("expirationDate", null));

                    // 解析 MemberCard 信息
                    MemberCard memberCard = new MemberCard();
                    memberCard.setCardNumber(memberCardJson.getString("cardNumber"));
                    memberCard.setExpirationDate(memberCardJson.getString("expirationDate"));
                    memberCard.setValid(memberCardJson.getBoolean("valid"));

                    callback.onSuccess(creditCard,memberCard);
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
