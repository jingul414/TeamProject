package com.donghaeng.withme.login.connect.message;

public class ConfirmationPayload {
    private final String userId;
    private final boolean confirmation;

    public ConfirmationPayload(String userId, boolean confirmation) {
        this.userId = userId;
        this.confirmation = confirmation;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isConfirmation() {
        return confirmation;
    }
}