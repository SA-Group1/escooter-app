package com.example.escooter.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoggedInUser {
    private int userId;
    private String account;
    private String userName;
    private String password;
    private String email;
    private String registrationTime;
    private CreditCard creditCard;
    private MemberCard memberCard;
    private List<RentalRecord> rentalRecords;

    // 構造函數
    public LoggedInUser(int userId, String account, String userName, String password, String email,
                        String registrationTime, CreditCard creditCard, MemberCard memberCard, List<RentalRecord> rentalRecords) {
        this.userId = userId;
        this.account = account;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.registrationTime = registrationTime;
        this.creditCard = creditCard;
        this.memberCard = memberCard;
        this.rentalRecords = rentalRecords;
    }

    // 從 JSON 創建 LoggedInUser 實例的靜態方法
    public static LoggedInUser fromJson(JSONObject jsonObject) throws JSONException {
        int userId = jsonObject.getInt("userId");
        String account = jsonObject.getString("account");
        String userName = jsonObject.getString("userName");
        String password = jsonObject.getString("password");
        String email = jsonObject.getString("email");
        String registrationTime = jsonObject.getString("registrationTime");
        CreditCard creditCard = CreditCard.fromJson(jsonObject.getJSONObject("creditCard"));
        MemberCard memberCard = MemberCard.fromJson(jsonObject.getJSONObject("memberCard"));

        List<RentalRecord> rentalRecords = new ArrayList<>();
        JSONArray rentalRecordsArray = jsonObject.getJSONArray("rentalRecords");
        for (int i = 0; i < rentalRecordsArray.length(); i++) {
            rentalRecords.add(RentalRecord.fromJson(rentalRecordsArray.getJSONObject(i)));
        }

        return new LoggedInUser(userId, account, userName, password, email, registrationTime, creditCard, memberCard, rentalRecords);
    }

    // Getter 方法
    public int getUserId() {
        return userId;
    }

    public String getAccount() {
        return account;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getRegistrationTime() {
        return registrationTime;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public MemberCard getMemberCard() {
        return memberCard;
    }

    public List<RentalRecord> getRentalRecords() {
        return rentalRecords;
    }

    // 其他 getter 和 setter
}

