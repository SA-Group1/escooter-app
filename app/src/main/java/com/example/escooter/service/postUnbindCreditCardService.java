package com.example.escooter.service;

import com.example.escooter.network.HttpRequest;

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
