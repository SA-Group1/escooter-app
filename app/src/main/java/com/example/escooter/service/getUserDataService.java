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

public class getUserDataService {
    String baseUrl = "http://36.232.110.240:8080";
    String userDataUrl = baseUrl + "/api/getUserData";
    private Context context;


    public getUserDataService(Context context, String account, String password){
        this.context = context;

        JSONObject postData = new JSONObject();
        try {
            // 準備 POST 請求的資料
            postData.put("account", account);
            postData.put("password", password);
        } catch (
                JSONException e) {
            throw new RuntimeException(e);
        }

        HttpRequest getUserData = new HttpRequest(userDataUrl);

        getUserData.httpPost(postData, userResult -> {
            try {
                // 處理用戶資料回應的 JSON 資料
                JSONObject userData = userResult.getJSONObject("user");
                User data = User.fromJson(userData);
                System.out.println(userData);

                // 使用 runOnUiThread 切换到主线程
                ((Activity) context).runOnUiThread(() -> {
                    UserViewModel userViewModel = new ViewModelProvider((FragmentActivity) context).get(UserViewModel.class);
                    userViewModel.setUserData(data);
                });

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
