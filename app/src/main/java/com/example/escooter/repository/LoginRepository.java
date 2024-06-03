package com.example.escooter.repository;

import com.example.escooter.BuildConfig;
import com.example.escooter.callback.HttpResultCallback;
import com.example.escooter.callback.BooleanCallback;
import com.example.escooter.network.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    public void login(String account, String password, BooleanCallback callback) {

        JSONObject body = new JSONObject();
        try {
            body.put("account", account);
            body.put("password", password);
        } catch (JSONException e) {
            callback.onFailure(e);
            return;
        }

        HttpRequest.httpRequest(BuildConfig.BASE_URL + "/login", "POST", body, new HttpResultCallback<JSONObject>() {
            @Override
            public void onResult(JSONObject result) {
                try {
                    boolean isLogin = result.getBoolean("status");
                    callback.onSuccess(isLogin);
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