package com.example.escooter.data;

import com.example.escooter.data.model.LoggedInUser;
import com.example.escooter.data.model.CreditCard;
import com.example.escooter.data.model.MemberCard;
import com.example.escooter.data.model.RentalRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication

            LoggedInUser fakeUser = new LoggedInUser(
                    username,
                    password
            );

            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
