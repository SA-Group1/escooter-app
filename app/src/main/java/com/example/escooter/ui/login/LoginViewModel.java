package com.example.escooter.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.example.escooter.data.LoginRepository;
import com.example.escooter.data.Result;
import com.example.escooter.data.model.LoggedInUser;
import com.example.escooter.R;
import com.example.escooter.network.HttpRequest;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // 定義登入 API 的 URL
        String apiUrl = "http://36.232.88.50:8080/api/login";
        JSONObject postData = new JSONObject();
        try {
            // 準備 POST 請求的資料
            postData.put("account", username);
            postData.put("password", password);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        HttpRequest login = new HttpRequest(apiUrl);

        // 發送 HTTP POST 請求
        login.httpPost(postData, result -> {
            try {
                // 處理登入回應的 JSON 資料
                if (result.getBoolean("status")) {
                    // 成功登入，繼續發送獲取用戶資料的請求
                    String userDataUrl = "http://36.232.88.50:8080/api/getUserData";
                    HttpRequest getUserData = new HttpRequest(userDataUrl);

                    getUserData.httpPost(postData, userResult -> {
                        try {
                            // 處理用戶資料回應的 JSON 資料
                            JSONObject userData = userResult.getJSONObject("user");
                            LoggedInUser data = LoggedInUser.fromJson(userData);
                            loginResult.postValue(new LoginResult(new LoggedInUserView(data.getUserName())));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    // 登入失敗
                    loginResult.postValue(new LoginResult(R.string.login_failed));
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void loginDataChanged(String username, String password) {
        if (isUserNameValid(username) && isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(true));
        } else {
            loginFormState.setValue(new LoginFormState(false));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}