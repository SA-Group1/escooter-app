package com.example.escooter.repository;

import com.example.escooter.BuildConfig;
import com.example.escooter.callback.BooleanCallback;
import com.example.escooter.callback.HttpResultCallback;
import com.example.escooter.callback.PhotoCallback;
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

                    JSONObject userResult = result.getJSONObject("data");
                    System.out.println(userResult);
                    User user = new User();
                    user.setUserId(userResult.getInt("userId"));
                    user.setAccount(userResult.getString("account"));
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

    public void getUserPhoto(String account, String password, PhotoCallback callback) {

        JSONObject body = new JSONObject();
        try {
            body.put("account", account);
            body.put("password", password);
        } catch (JSONException e) {
            callback.onFailure(e);
            return;
        }

        HttpRequest.httpRequest(BuildConfig.BASE_URL + "/getUserPhoto", "POST", body, new HttpResultCallback<JSONObject>() {
            @Override
            public void onResult(JSONObject result) {
                try {
                    JSONObject jsonObject = result.getJSONObject("data");
                    String photo = jsonObject.getString("image");
                    callback.onSuccess(photo);
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


    public void updateUserData(String account, String password, String username, String email, String phoneNumber, BooleanCallback callback) {

        JSONObject body = new JSONObject();
        try {
            body.put("account", account);
            body.put("password", password);
            body.put("userName", username);
            body.put("email", email);
            body.put("phoneNumber", phoneNumber);
        } catch (
                JSONException e) {
            throw new RuntimeException(e);
        }

        HttpRequest.httpRequest(BuildConfig.BASE_URL + "/updateUserData", "PUT", body, new HttpResultCallback<JSONObject>() {
            @Override
            public void onResult(JSONObject result) {
                try {
                    boolean isUpdateUserData = result.getBoolean("status");
                    callback.onSuccess(isUpdateUserData);
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

    public void uploadUserPhoto(String account, String password,String photo, BooleanCallback callback) {

        JSONObject body = new JSONObject();
        try {
            body.put("account",account);
            body.put("password",password);
            body.put("image", photo);
        } catch (
                JSONException e) {
            throw new RuntimeException(e);
        }

        HttpRequest.httpRequest(BuildConfig.BASE_URL + "/uploadUserPhoto", "PUT", body, new HttpResultCallback<JSONObject>() {
            @Override
            public void onResult(JSONObject result) {
                try {
                    boolean isUploadPhoto = result.getBoolean("status");
                    callback.onSuccess(isUploadPhoto);
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
