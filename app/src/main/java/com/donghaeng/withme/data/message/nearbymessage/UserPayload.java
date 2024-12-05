package com.donghaeng.withme.data.message.nearbymessage;

import com.donghaeng.withme.data.user.User;

public class UserPayload {
    private final User user;

    public UserPayload(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
