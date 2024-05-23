package com.example.escooter.service;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.escooter.data.model.User;
import com.example.escooter.network.HttpRequest;
import com.example.escooter.ui.viewmodel.UserViewModel;

import org.json.JSONException;
import org.json.JSONObject;

public class postUnbindCreditCardService {
    String baseUrl = "http://36.232.110.240:8080";
    String userDataUrl = baseUrl + "/api/unbindCreditCard";


    public postUnbindCreditCardService(String account, String password){

        JSONObject PostData = new JSONObject();
        try {
            PostData.put("account", account);
            PostData.put("password", password);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        HttpRequest PostunbindCreditCard= new HttpRequest(userDataUrl);
        // 發送 HTTP POST 請求
        PostunbindCreditCard.httpPost(PostData, result -> {
            try {
                if (result.getBoolean("status")){
                    System.out.println(result.getString("message"));
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
