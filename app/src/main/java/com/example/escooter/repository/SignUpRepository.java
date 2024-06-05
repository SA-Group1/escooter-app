package com.example.escooter.repository;

import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.escooter.BuildConfig;
import com.example.escooter.R;
import com.example.escooter.callback.BooleanCallback;
import com.example.escooter.callback.HttpResultCallback;
import com.example.escooter.network.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class SignUpRepository {

    public void SignUp(String account, String password, String userName, String email, String phoneNumber, BooleanCallback callback) {

        JSONObject body = new JSONObject();
        try {
            body.put("account", account);
            body.put("password", password);
            body.put("userName", userName);
            body.put("email", email);
            body.put("phoneNumber", phoneNumber);

        } catch (JSONException e) {
            callback.onFailure(e);
            return;
        }

        HttpRequest.httpRequest(BuildConfig.BASE_URL + "/register", "POST", body, new HttpResultCallback<JSONObject>() {
            @Override
            public void onResult(JSONObject result) {
                try {
                    boolean isSignUp = result.getBoolean("status");
                    callback.onSuccess(isSignUp);
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