package com.donghaeng.withme.data.message.nearbymessage;

public class TextMessagePayload {
    private final String message;

    public TextMessagePayload(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
