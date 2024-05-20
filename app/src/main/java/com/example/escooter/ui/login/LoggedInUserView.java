package com.example.escooter.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private String getUserName;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String userName) {
        this.getUserName = userName;
    }

    String getDisplayName() {
        return getUserName;
    }
}