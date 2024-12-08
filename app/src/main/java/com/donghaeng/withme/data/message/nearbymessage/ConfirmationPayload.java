package com.donghaeng.withme.data.message.nearbymessage;

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