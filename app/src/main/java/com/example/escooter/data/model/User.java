package com.example.escooter.data.model;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    private int userId;
    private String account;
    private String userName;
    private String password;
    private String email;
    private String registrationTime;
    private String phoneNumber;
    private CreditCard creditCard;
    private MemberCard memberCard;

    // 構造函數
    public User(int userId, String account, String userName, String password, String email,
                        String registrationTime,String phoneNumber,CreditCard creditCard,MemberCard memberCard) {
        this.userId = userId;
        this.account = account;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.registrationTime = registrationTime;
        this.phoneNumber = phoneNumber;
        this.creditCard = creditCard;
        this.memberCard = memberCard;
    }

    // 從 JSON 創建 LoggedInUser 實例的靜態方法
    public static User fromJson(JSONObject jsonObject) throws JSONException {
        int userId = jsonObject.getInt("userId");
        String account = jsonObject.getString("account");
        String userName = jsonObject.getString("userName");
        String password = jsonObject.getString("password");
        String email = jsonObject.getString("email");
        String registrationTime = jsonObject.getString("registrationTime");
        String phoneNumber = jsonObject.getString("phoneNumber");
        CreditCard creditCard = null;
        if (jsonObject.has("creditCard")) {
            creditCard = CreditCard.fromJson(jsonObject.getJSONObject("creditCard"));
        }

        MemberCard memberCard = null;
        if (jsonObject.has("memberCard")) {
            memberCard = MemberCard.fromJson(jsonObject.getJSONObject("memberCard"));
        }

        return new User(userId, account, userName, password, email, registrationTime,phoneNumber,creditCard, memberCard);
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public MemberCard getMemberCard() {
        return memberCard;
    }

}
