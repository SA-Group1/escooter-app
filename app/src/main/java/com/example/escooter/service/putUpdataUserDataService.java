package com.example.escooter.service;

import android.app.AlertDialog;
import android.content.Context;

import com.example.escooter.databinding.DialogPersonInfoEditProfileBinding;
import com.example.escooter.network.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class putUpdataUserDataService {
    String baseUrl = "http://36.232.110.240:8080";
    String userDataUrl = baseUrl + "/api/updateUserData";
    private Context context;
    public putUpdataUserDataService(String account, String password, AlertDialog dialog, DialogPersonInfoEditProfileBinding dialogBinding){
        JSONObject putData = new JSONObject();
        try {
            putData.put("account", account);
            putData.put("password", password);
            putData.put("userName", dialogBinding.userName.getText().toString());
            putData.put("email", dialogBinding.userEmail.getText().toString());
            putData.put("phoneNumber", dialogBinding.userPhoneNumber.getText().toString());
        } catch (
                JSONException e) {
            throw new RuntimeException(e);
        }

        HttpRequest putupdateUserData = new HttpRequest(userDataUrl);
        putupdateUserData.httpPut(putData, result -> {
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
