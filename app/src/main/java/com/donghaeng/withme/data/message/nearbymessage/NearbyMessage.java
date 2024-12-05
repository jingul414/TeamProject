package com.donghaeng.withme.data.message.nearbymessage;

public class NearbyMessage {
    private final String type;
    private final Object payload;

    public NearbyMessage(String type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public Object getPayload() {
        return payload;
    }
}
