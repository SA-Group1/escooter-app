package com.example.escooter.repository;

import com.example.escooter.BuildConfig;
import com.example.escooter.callback.HttpResultCallback;
import com.example.escooter.callback.UserCallback;
import com.example.escooter.data.model.CreditCard;
import com.example.escooter.data.model.MemberCard;
import com.example.escooter.data.model.User;
import com.example.escooter.network.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class UserRepository {

    public void getUserData(String account, String password, UserCallback callback) {

        JSONObject body = new JSONObject();
        try {
            body.put("account", account);
            body.put("password", password);
        } catch (JSONException e) {
            callback.onFailure(e);
            return;
        }

        HttpRequest.httpRequest(BuildConfig.BASE_URL + "/getUserData", "POST", body, new HttpResultCallback<JSONObject>() {
            @Override
            public void onResult(JSONObject result) {
                try {
                    System.out.println(result);
                    JSONObject userResult = result.getJSONObject("user");
                    User user = new User();
                    user.setUserId(userResult.getInt("userId"));
                    System.out.println(userResult.getInt("userId"));
                    user.setAccount(userResult.getString("account"));
                    user.setPassword(userResult.getString("password"));
                    user.setUserName(userResult.getString("userName"));
                    user.setEmail(userResult.getString("email"));
                    user.setRegistrationTime(userResult.getString("registrationTime"));
                    user.setPhoneNumber(userResult.getString("phoneNumber"));
                    CreditCard creditCard = new CreditCard();
                    JSONObject creditCardJSONObject = userResult.getJSONObject("creditCard");
                    creditCard.setCardNumber(creditCardJSONObject.getString("cardNumber"));
                    creditCard.setExpirationDate(creditCardJSONObject.getString("expirationDate"));
                    user.setCreditCard(creditCard);
                    MemberCard memberCard = new MemberCard();
                    JSONObject memberCardJSONObject = userResult.getJSONObject("memberCard");
                    memberCard.setCardNumber(memberCardJSONObject.getString("cardNumber"));
                    memberCard.setExpirationDate(memberCardJSONObject.getString("expirationDate"));
                    memberCard.setValid(memberCardJSONObject.getBoolean("vaild"));
                    user.setMemberCard(memberCard);

                    callback.onSuccess(user);
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
