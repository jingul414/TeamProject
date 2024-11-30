package com.donghaeng.withme.login.connect.message;

import com.donghaeng.withme.user.User;

public class UserPayload {
    private final User user;

    public UserPayload(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
