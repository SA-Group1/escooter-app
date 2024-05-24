package com.example.escooter.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
public class LoggedInUserView {
    private String getUserName;
    //... other data fields that may be accessible to the UI

    public LoggedInUserView(String userName) {
        this.getUserName = userName;
    }

    String getUserName() {
        return getUserName;
    }
}