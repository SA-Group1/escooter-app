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


    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRegistrationTime(String registrationTime) {
        this.registrationTime = registrationTime;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    public void setMemberCard(MemberCard memberCard) {
        this.memberCard = memberCard;
    }

}
