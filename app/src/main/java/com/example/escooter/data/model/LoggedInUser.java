package com.example.escooter.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */

public class LoggedInUser {
    private String account;
    private String password;

    // 构造函数
    public LoggedInUser(String account, String password) {
        this.account = account;
        this.password = password;
    }

    // Getter 方法
    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }
}

